package com.affirm.common.service.impl;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.ConsolidationAccountType;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.catalog.InteractionContent;
import com.affirm.common.model.catalog.InteractionType;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jrodriguez
 */

@Service("debtConsolidationService")
public class DebtConsolidationServiceImpl implements DebtConsolidationService {

    private static final Logger logger = Logger.getLogger(DebtConsolidationServiceImpl.class);

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private UserService userService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private InteractionService interactionService;

    @Override
    public List<ConsolidableDebt> getPersonConsolidableDebts(int personId, Locale locale) throws Exception {

        List<ConsolidableDebt> creditCardDebts = new ArrayList<>();
        List<ConsolidableDebt> consumeDebts = new ArrayList<>();

        Person person = personDao.getPerson(catalogService, locale, personId, false);
        List<EntityConsolidableDebt> entitiesDebts = personDao.getConsolidableDebts(person.getDocumentType().getId(), person.getDocumentNumber(), true);
        if (entitiesDebts != null && !entitiesDebts.isEmpty()) {
            for (EntityConsolidableDebt entityDebt : entitiesDebts) {
                if (entityDebt.getAccounts() != null) {

                    // First, the easy one. Consume accounts.
                    ConsolidableDebt consumeDebt = new ConsolidableDebt(entityDebt.getEntity());
                    consumeDebt.setConsolidationAccounttype(ConsolidationAccountType.CREDITO_CONSUMO);
                    consumeDebt.setSelected(false);
                    entityDebt.getAccounts().stream()
                            .filter(a -> ArrayUtils.contains(AccountConsolidableDebt.CONSUME_ACCOUNTS, a.getAccount().getId()))
                            .forEach(a -> {
                                consumeDebt.setBalance((int) Math.ceil(consumeDebt.getBalance() + a.getBalance()));
                                // This means it will only be reflected the las rate
                                consumeDebt.setRate(a.getRate());
                            });
                    if (consumeDebt.getBalance() > 0) {
                        // Add to list
                        consumeDebts.add(consumeDebt);
                    }

                    // Second, the dificult one. CreditCard accounts.
                    ConsolidableDebt creditCardDebt = new ConsolidableDebt(entityDebt.getEntity());
                    creditCardDebt.setConsolidationAccounttype(ConsolidationAccountType.TARJETA_CREDITO);
                    creditCardDebt.setSelected(false);
                    entityDebt.getAccounts().stream()
                            .filter(a -> ArrayUtils.contains(AccountConsolidableDebt.CREDITCARD_ACCOUNTS, a.getAccount().getId()))
                            .forEach(a -> {
                                creditCardDebt.setBalance((int) Math.ceil(creditCardDebt.getBalance() + a.getBalance()));
                                // This means it will only be reflected the las rate
                                creditCardDebt.setRate(a.getRate());
                            });
                    if (creditCardDebt.getBalance() > 0) {
                        // Search the resolvente debt
                        MutableDouble revolventeDebt = new MutableDouble();
                        entityDebt.getAccounts().stream()
                                .filter(a -> a.getAccount().getId() == AccountConsolidableDebt.CREDITCARD_REVOLVENTE)
                                .forEach(a -> revolventeDebt.add(a.getBalance()));
                        // Search the sobregiro debt
                        MutableDouble sobregiroDebt = new MutableDouble();
                        entityDebt.getAccounts().stream()
                                .filter(a -> a.getAccount().getId() == AccountConsolidableDebt.CREDITCARD_SOBREGIRO)
                                .forEach(a -> sobregiroDebt.add(a.getBalance()));

                        if (revolventeDebt.doubleValue() > 0) {
                            creditCardDebt.setBalanceLP((int) Math.ceil(revolventeDebt.doubleValue()));
                            creditCardDebt.setBalanceDE((int) Math.ceil(sobregiroDebt.doubleValue()));
                        } else if (sobregiroDebt.doubleValue() > 0) {
                            // Search the linea_tc
                            MutableDouble lineaTarjeta = new MutableDouble();
                            entityDebt.getAccounts().stream()
                                    .filter(a -> a.getAccount().getId() == AccountConsolidableDebt.CREDITCARD_LINEA)
                                    .forEach(a -> lineaTarjeta.add(a.getBalance()));

                            if (sobregiroDebt.doubleValue() <= lineaTarjeta.doubleValue() * 0.1) {
                                creditCardDebt.setBalanceDE((int) Math.ceil(sobregiroDebt.doubleValue()));
                            } else {
                                creditCardDebt.setBalanceLP((int) Math.ceil(sobregiroDebt.doubleValue()));
                            }
                        }

                        // Add to list
                        creditCardDebts.add(creditCardDebt);
                    }
                }
            }

        }

        return Stream.concat(consumeDebts.stream(), creditCardDebts.stream()).collect(Collectors.toList());
    }

    @Override
    public void registerconsolidation(List<ConsolidableDebt> consolidation, int loanApplicationId) {
        for (ConsolidableDebt debt : consolidation) {
            loanApplicationDao.registerConsolidationAccount(loanApplicationId, debt.getConsolidationAccounttype(), debt.getEntity().getCode(), (double) debt.getBalance(), debt.getInstallments(), debt.getRate(), debt.isSelected(), debt.getAccountCardNumber(), debt.getBrandId(), debt.getDepartmentUbigeo());
            if (debt.getBalanceDE() > 0) {
                loanApplicationDao.registerConsolidationAccount(loanApplicationId, ConsolidationAccountType.DISPONIBILIDAD_EFECTIVO, debt.getEntity().getCode(), (double) debt.getBalanceDE(), null, debt.getRateDE(), true, null, null, null);
            }
            if (debt.getBalanceLP() > 0) {
                loanApplicationDao.registerConsolidationAccount(loanApplicationId, ConsolidationAccountType.LINEA_PARALELA, debt.getEntity().getCode(), (double) debt.getBalanceLP(), null, debt.getRateLP(), true, null, null, null);
            }
        }
    }

    @Override
    public void sendConsolidableAccountsEmail(int creditId, Locale locale, int interactionContentId, byte[] contractBytes) throws Exception {

        Credit credit = creditDao.getCreditByID(creditId, locale, false, Credit.class);
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);
        List<ConsolidableDebt> consolidableDebts = loanApplicationDao.getConsolidationAccounts(credit.getLoanApplicationId()).stream().filter(ConsolidableDebt::isSelected).collect(Collectors.toList());
        Person person = personDao.getPerson(catalogService, locale, credit.getPersonId(), false);
        User user = userDao.getUser(person.getUserId());

        String emailBody = catalogService.getInteractionContent(interactionContentId, loanApplication.getCountryId()).getBody();

        String tcBlockStart = "<tc-accounts-loop>";
        String tcBlockEnd = "</tc-accounts-loop>";
        String tcDebtBody = emailBody.substring(emailBody.indexOf(tcBlockStart) + tcBlockStart.length(), emailBody.indexOf(tcBlockEnd));
        emailBody = emailBody.replace(emailBody.substring(emailBody.indexOf(tcBlockStart), emailBody.indexOf(tcBlockEnd) + tcBlockEnd.length()), "");

        String consumoBlockStart = "<consumo-accounts-loop>";
        String consumoBlockEnd = "</consumo-accounts-loop>";
        String consumoDebtBody = emailBody.substring(emailBody.indexOf(consumoBlockStart) + consumoBlockStart.length(), emailBody.indexOf(consumoBlockEnd));
        emailBody = emailBody.replace(emailBody.substring(emailBody.indexOf(consumoBlockStart), emailBody.indexOf(consumoBlockEnd) + consumoBlockEnd.length()), "");

        String variableAmountStart = "<variable-amount>";
        String variableAmountEnd = "</variable-amount>";
        String variableAmountbody = emailBody.substring(emailBody.indexOf(variableAmountStart) + variableAmountStart.length(), emailBody.indexOf(variableAmountEnd));
        emailBody = emailBody.replace(emailBody.substring(emailBody.indexOf(variableAmountStart), emailBody.indexOf(variableAmountEnd) + variableAmountEnd.length()), "");
        Double variableAmount = credit.getAmount() - consolidableDebts.stream().mapToInt(ConsolidableDebt::getBalance).sum();
        emailBody = emailBody.replace("%VARIABLE_AMOUNT_BLOCK%", variableAmount.intValue() > 0 ? variableAmountbody.replace("%VARIABLE_AMOUNT%", utilService.integerMoneyFormat(variableAmount)) : "");

        String accountsBody = "";
        for (ConsolidableDebt debt : consolidableDebts) {
            String debtBody = "";
            if (debt.getConsolidationAccounttype() == ConsolidationAccountType.TARJETA_CREDITO) {
                debtBody = tcDebtBody;
                debtBody = debtBody.replace("%BANK%", debt.getEntity().getShortName())
                        .replace("%AMOUNT%", utilService.integerMoneyFormat(debt.getBalance()))
                        .replace("%NUMBER%", debt.getAccountCardNumberEncrypted())
                        .replace("%BRAND%", catalogService.getBrandById(debt.getBrandId()).getName())
                        .replace("%CITY%", catalogService.getDepartmentById(debt.getDepartmentUbigeo()).getName());
            } else if (debt.getConsolidationAccounttype() == ConsolidationAccountType.CREDITO_CONSUMO) {
                debtBody = consumoDebtBody;
                debtBody = debtBody.replace("%BANK%", debt.getEntity().getShortName())
                        .replace("%AMOUNT%", utilService.integerMoneyFormat(debt.getBalance()));
            }
            accountsBody = accountsBody + debtBody + "</br></br>";
        }
        emailBody = emailBody.replace("%ACCOUNTS%", accountsBody);

        JSONObject jsonVars = new JSONObject();
        jsonVars.put("CLIENT_NAME", person.getFirstName());
        jsonVars.put("AMOUNT", utilService.integerMoneyFormat(consolidableDebts.stream().mapToInt(ConsolidableDebt::getBalance).sum()));
        jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

        PersonInteraction interaction = new PersonInteraction();
        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
        interaction.setInteractionContent(catalogService.getInteractionContent(interactionContentId, loanApplication.getCountryId()));
        interaction.setBody(emailBody);
        interaction.setDestination(user.getEmail());
        interaction.setLoanApplicationId(credit.getLoanApplicationId());
        interaction.setCreditId(credit.getId());
        interaction.setPersonId(user.getPersonId());

        EntityProductParams entityParam = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());
        if (interactionContentId == InteractionContent.DISBURSEMENT_CONSOLIDATION_OPEN_MAIL && entityParam.getSendContract()) {

            // Validate the contract exists
            if (credit.getContractUserFileId() == null) {
                throw new Exception("There is no contract created yet, and its not posible create it in worker");
            }

            interaction.setAttachments(new ArrayList<>());
            if (contractBytes != null) {
                PersonInteractionAttachment attachment = new PersonInteractionAttachment();
                attachment.setBytes(contractBytes);
                // TODO Instead of putting the first of the list, send the userFileId as parameter with th byte[]
                attachment.setUserFileId(credit.getContractUserFileId().get(0));
                interaction.getAttachments().add(attachment);
            } else {
                for (int i = 0; i < credit.getContractUserFileId().size(); i++) {
                    PersonInteractionAttachment attachment = new PersonInteractionAttachment();
                    attachment.setUserFileId(credit.getContractUserFileId().get(i));
                    interaction.getAttachments().add(attachment);
                }
            }
        }
        interactionService.sendPersonInteraction(interaction, jsonVars, null);
    }
}