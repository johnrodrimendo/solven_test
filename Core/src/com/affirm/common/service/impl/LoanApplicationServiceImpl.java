package com.affirm.common.service.impl;

import com.affirm.acceso.AccesoServiceCall;
import com.affirm.acceso.model.Direccion;
import com.affirm.bancoazteca.model.AztecaAgency;
import com.affirm.bantotalrest.exception.BT40147Exception;
import com.affirm.bantotalrest.service.BTApiRestService;
import com.affirm.bpeoplerest.service.BPeopleApiRestService;
import com.affirm.cajasullana.CajaSullanaServiceCall;
import com.affirm.cajasullana.model.GenerarSolicitudRequest;
import com.affirm.cajasullana.model.ListaModalidad;
import com.affirm.common.dao.*;
import com.affirm.common.model.BantotalApiData;
import com.affirm.common.model.ExternalWSRecord;
import com.affirm.common.model.CreateLoanApplicationRequest;
import com.affirm.common.model.catalog.Currency;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.EntityExtranetCreateLoanApplicationForm;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.service.document.DocumentService;
import com.affirm.common.service.entities.AeluService;
import com.affirm.common.service.entities.CredigobService;
import com.affirm.common.service.external.SalesDoublerService;
import com.affirm.common.service.question.Question95Service;
import com.affirm.common.util.*;
import com.affirm.compartamos.CompartamosServiceCall;
import com.affirm.compartamos.model.GenerarCreditoRequest;
import com.affirm.compartamos.model.GenerarCreditoResponse;
import com.affirm.fdlm.FdlmServiceCall;
import com.affirm.fdlm.creditoconsumo.response.Sucursal;
import com.affirm.marketingCampaign.dao.MarketingCampaignDAO;
import com.affirm.marketingCampaign.model.MarketingCampaign;
import com.affirm.security.service.ExternalWebServiceService;
import com.affirm.system.configuration.Configuration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.Irr;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */

@Service("loanApplicationService")
public class LoanApplicationServiceImpl implements LoanApplicationService {

    private static final Logger logger = Logger.getLogger(LoanApplicationServiceImpl.class);

    public static final String SHOULD_VALIDATE_EMAIL_KEY = "shouldValidateEmail";

    public final OkHttpClient client = new OkHttpClient.Builder()
            .build();

    ExecutorService botsExecutor = Executors.newFixedThreadPool(30);

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private BotDAO botDao;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private UserService userService;
    @Autowired
    private WebscrapperService webscrapperService;
    @Autowired
    private AwsSesEmailService awsSesEmailService;
    @Autowired
    private AgreementService agreementService;
    @Autowired
    private CreditService creditService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private TranslatorDAO translatorDAO;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private ErrorService errorService;
    @Autowired
    private CajaSullanaServiceCall cajaSullanaServiceCall;
    @Autowired
    private WebServiceDAO webServiceDAO;
    @Autowired
    private EntityProductEvaluationProcessDAO entityProductEvaluationProcessDao;
    @Autowired
    private FileService fileService;
    @Autowired
    private ProductService productService;
    @Autowired
    private PersonService personService;
    @Autowired
    private Question95Service question95Service;
    @Autowired
    private DebtConsolidationService debtConsolidationService;
    @Autowired
    private AeluService aeluService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private SalesDoublerService salesDoublerService;
    @Autowired
    private CompartamosServiceCall compartamosServiceCall;
    @Autowired
    private AccesoServiceCall accesoServiceCall;
    @Autowired
    private CredigobService credigobService;
    @Autowired
    private FdlmServiceCall fdlmServiceCall;
    @Autowired
    private ExternalWebServiceService externalWebService;
    @Autowired
    private FunnelStepService funnelStepService;
    @Autowired
    private LoanApplicationApprovalValidationService loanApplicationApprovalValidationService;
    @Autowired
    private MarketingCampaignDAO marketingCampaignDAO;
    @Autowired
    private BTApiRestService btApiRestService;
    @Autowired
    private ExternalWSRecordDAO externalWSRecordDAO;
    @Autowired
    private BPeopleApiRestService bPeopleApiRestService;
    @Autowired
    private InteractionDAO interactionDAO;


    @Override
    public LoanApplicationPreliminaryEvaluation getLastPreliminaryEvaluation(int loanApplicationId, Locale locale, EntityBranding entityBranding)
            throws Exception {
        return getLastPreliminaryEvaluation(loanApplicationId, locale, entityBranding, false);
    }

    @Override
    public LoanApplicationPreliminaryEvaluation getLastPreliminaryEvaluation(int loanApplicationId, Locale locale, EntityBranding entityBranding, boolean followerDatabase)
            throws Exception {

        LoanApplicationPreliminaryEvaluation loanPreEvaluation;
        if (followerDatabase) {
            loanPreEvaluation = loanApplicationDao.getLastPreliminaryEvaluation(loanApplicationId, locale, JsonResolverDAO.EVALUATION_FOLLOWER_DB);
        } else {
            loanPreEvaluation = loanApplicationDao.getLastPreliminaryEvaluation(loanApplicationId, locale);
        }


        LoanApplication loanApplication = loanApplicationDao.getLoanApplicationLite(loanApplicationId, locale);
        String evaluatorName = catalogService.getEntity(Entity.AFFIRM).getShortName();
        if (loanApplication.getEntityId() != null) {
            evaluatorName = catalogService.getEntity(loanApplication.getEntityId()).getShortName();
        } else if (entityBranding != null) {
            evaluatorName = entityBranding.getEntity().getShortName();
        }


        if (loanPreEvaluation != null && loanPreEvaluation.getHardFilterMessageKey() != null) {
            ProductAgeRange ageRange = null;
            if (loanPreEvaluation.getProduct() != null && loanPreEvaluation.getEntityId() != null) {
                ageRange = catalogService.getProductAgeRange(loanPreEvaluation.getProduct().getId(), loanPreEvaluation.getEntityId());
            }
            Currency currency = loanApplication.getCurrency();
            Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);

            String appName = Configuration.APP_NAME;
            if (loanApplication.getProductCategoryId() != null && loanApplication.getProductCategoryId() == ProductCategory.LEADS) {
                appName = "Efectivo al Toque";
                if (evaluatorName.equals(catalogService.getEntity(Entity.AFFIRM).getShortName())) {
                    evaluatorName = appName;
                }
            }

            loanPreEvaluation.setHardFilterMessage(messageSource.getMessage(loanPreEvaluation.getHardFilterMessageKey(),
                    new Object[]{appName,
                            ageRange != null ? ageRange.getMinAge() : null,
                            ageRange != null ? ageRange.getMaxAge() : null,
                            currency != null ? currency.getSymbol() : Configuration.SOLES_CURRENCY,
                            loanPreEvaluation.getParameter(),
                            evaluatorName,
                            person != null ? person.getFirstName() : null,
                    }, locale));

        }
        return loanPreEvaluation;
    }

    @Override
    public LoanApplicationEvaluation getLastEvaluation(int loanApplicationId, int personId, Locale locale) throws Exception {
        return getLastEvaluation(loanApplicationId, personId, locale, false);
    }

    @Override
    public LoanApplicationEvaluation getLastEvaluation(int loanApplicationId, int personId, Locale locale, boolean followerDatabase) throws Exception {

        LoanApplicationEvaluation loanEvaluation;
        if(followerDatabase)
            loanEvaluation = loanApplicationDao.getLastEvaluation(loanApplicationId, locale, JsonResolverDAO.EVALUATION_FOLLOWER_DB);
        else
            loanEvaluation = loanApplicationDao.getLastEvaluation(loanApplicationId, locale);

        LoanApplication loanApplication = loanApplicationDao.getLoanApplicationLite(loanApplicationId, locale);
        String evaluatorName = catalogService.getEntity(Entity.AFFIRM).getShortName();
        if (loanApplication.getEntityId() != null) {
            evaluatorName = catalogService.getEntity(loanApplication.getEntityId()).getShortName();
        }

        if (loanEvaluation != null && loanEvaluation.getPolicyMessageKey() != null) {
            ProductAgeRange ageRange = catalogService.getProductAgeRanges().get(0);
            Currency currency = loanApplication.getCurrency();

            loanEvaluation.setPolicyMessage(messageSource.getMessage(loanEvaluation.getPolicyMessageKey(),
                    new Object[]{Configuration.APP_NAME,
                            ageRange.getMinAge(),
                            ageRange.getMaxAge(),
                            currency != null ? currency.getSymbol() : Configuration.SOLES_CURRENCY,
                            loanEvaluation.getParameter(),
                            evaluatorName
                    }, locale));
        }
        return loanEvaluation;
    }
//
//    @Override
//    public void executeEvaluation(int loanApplicationId, boolean isRetry) throws Exception {
//        QueryBot queryBot = webscrapperService.callEvaluationBot(loanApplicationId, isRetry ? 30 : null);
//        LoanApplicationEvaluationsProcess evaluationProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplicationId);
//        evaluationProcess.addEvaluationQueryBot(queryBot.getId());
//        loanApplicationDao.updateLoanApplicationEvaluationProcessEvaluationBot(loanApplicationId, evaluationProcess.getEvaluationBots());
//    }

    @Override
    public boolean runEvaluationBot(int loanApplicationId, boolean createdFromEntityExtranet) throws Exception {

        LoanApplicationEvaluationsProcess evaluationProcess = loanApplicationDao.getLoanApplicationEvaluationsProcess(loanApplicationId);

        // First validate that theres no evaluation bot alredy running
        List<QueryBot> runningBots = botDao.getLoanApplicationEvaluationQueryBot(loanApplicationId, QueryBot.STATUS_RUNNING);
        if (runningBots != null && !runningBots.isEmpty()) {
            return false;
        }

        // Cancel the possible queryBots that are in queue
        List<QueryBot> queueEvaluationBots = botDao.getLoanApplicationEvaluationQueryBot(loanApplicationId, QueryBot.STATUS_QUEUE);
        if (queueEvaluationBots != null) {
            for (QueryBot queryBot : queueEvaluationBots) {
                queryBot.setStatusId(QueryBot.STATUS_CANCELLED);
                botDao.updateQuery(queryBot);
            }
        }

        QueryBot queryBot = webscrapperService.callEvaluationBot(loanApplicationId, createdFromEntityExtranet, null);
        evaluationProcess.addEvaluationQueryBot(queryBot.getId());
        loanApplicationDao.updateLoanApplicationEvaluationProcessEvaluationBot(loanApplicationId, evaluationProcess.getEvaluationBots());
        return true;
    }

    @Override
    public boolean runEntityEvaluationBot(int loanApplicationId, int entityId, int productId, boolean isPreliminaryEvaluation, boolean isRetry) throws Exception {

        if (isPreliminaryEvaluation) {
            EntityProductEvaluationsProcess entityProductProcess = getEntityProductEvaluationProcess(loanApplicationId, entityId, productId);
            Integer retrayDelay = null;

            if (isRetry) {

                Integer maxRetries = catalogService.getEntityProductParam(entityId, productId).getMaxRetries();
                if (entityProductProcess.getPreEvaluationRetries() >= maxRetries) {
                    return false;
                }

                if (entityProductProcess.getPreEvaluationRetries() == 0)
                    retrayDelay = Configuration.hostEnvIsProduction() ? 1 * 60 : 30;
                else if (entityProductProcess.getPreEvaluationRetries() == 1)
                    retrayDelay = Configuration.hostEnvIsProduction() ? 2 * 60 : 1 * 30;
                else if (entityProductProcess.getPreEvaluationRetries() == 2)
                    retrayDelay = Configuration.hostEnvIsProduction() ? 3 * 60 : 1 * 30;
                else if (entityProductProcess.getPreEvaluationRetries() == 3)
                    retrayDelay = Configuration.hostEnvIsProduction() ? 5 * 60 : 1 * 30;
                else if (entityProductProcess.getPreEvaluationRetries() == 4)
                    retrayDelay = Configuration.hostEnvIsProduction() ? 10 * 60 : 1 * 30;
                else if (entityProductProcess.getPreEvaluationRetries() == 5)
                    retrayDelay = Configuration.hostEnvIsProduction() ? 15 * 60 : 1 * 30;
                else if (entityProductProcess.getPreEvaluationRetries() == 6)
                    retrayDelay = Configuration.hostEnvIsProduction() ? 20 * 60 : 1 * 30;
                else if (entityProductProcess.getPreEvaluationRetries() >= 7)
                    return false;

                entityProductEvaluationProcessDao.updatePreliminaryEvaluationRetries(entityProductProcess.getPreEvaluationRetries() + 1, loanApplicationId, entityId, productId);
            }

            QueryBot queryBot;
            if (retrayDelay != null) {
                Calendar scheduledCalendar = Calendar.getInstance();
                scheduledCalendar.add(Calendar.SECOND, retrayDelay);
                queryBot = webscrapperService.callEntityEvaluationBot(loanApplicationId, entityId, productId, scheduledCalendar.getTime());
            } else {
                queryBot = webscrapperService.callEntityEvaluationBot(loanApplicationId, entityId, productId, null);
            }

            entityProductProcess.addPreliminaryEvaluationQueryBot(queryBot.getId());
            entityProductEvaluationProcessDao.updatePreliminaryEvaluationQueryBots(entityProductProcess.getPreEvaluationBots(), loanApplicationId, entityId, productId);
        } else {
            EntityProductEvaluationsProcess entityProductProcess = getEntityProductEvaluationProcess(loanApplicationId, entityId, productId);
            Integer retrayDelay = null;

            if (isRetry) {

                Integer maxRetries = catalogService.getEntityProductParam(entityId, productId).getMaxRetries();
                if (entityProductProcess.getEvaluationRetries() >= maxRetries) {
                    return false;
                }

                if (entityProductProcess.getEvaluationRetries() == 0)
                    retrayDelay = Configuration.hostEnvIsProduction() ? 1 * 60 : 30;
                else if (entityProductProcess.getEvaluationRetries() == 1)
                    retrayDelay = Configuration.hostEnvIsProduction() ? 2 * 60 : 1 * 30;
                else if (entityProductProcess.getEvaluationRetries() == 2)
                    retrayDelay = Configuration.hostEnvIsProduction() ? 3 * 60 : 1 * 30;
                else if (entityProductProcess.getEvaluationRetries() == 3)
                    retrayDelay = Configuration.hostEnvIsProduction() ? 5 * 60 * 60 : 1 * 30;
                else if (entityProductProcess.getEvaluationRetries() == 4)
                    retrayDelay = Configuration.hostEnvIsProduction() ? 10 * 60 : 1 * 30;
                else if (entityProductProcess.getEvaluationRetries() == 5)
                    retrayDelay = Configuration.hostEnvIsProduction() ? 15 * 60 : 1 * 30;
                else if (entityProductProcess.getEvaluationRetries() == 6)
                    retrayDelay = Configuration.hostEnvIsProduction() ? 20 * 60 : 1 * 30;
                else if (entityProductProcess.getEvaluationRetries() >= 7)
                    return false;

                entityProductEvaluationProcessDao.updateEvaluationRetries(entityProductProcess.getEvaluationRetries() + 1, loanApplicationId, entityId, productId);
            }

            QueryBot queryBot;
            if (retrayDelay != null) {
                Calendar scheduledCalendar = Calendar.getInstance();
                scheduledCalendar.add(Calendar.SECOND, retrayDelay);
                queryBot = webscrapperService.callEntityEvaluationBot(loanApplicationId, entityId, productId, scheduledCalendar.getTime());
            } else {
                queryBot = webscrapperService.callEntityEvaluationBot(loanApplicationId, entityId, productId, null);
            }

            entityProductProcess.addEvaluationQueryBot(queryBot.getId());
            entityProductEvaluationProcessDao.updateEvaluationQueryBots(entityProductProcess.getEvaluationBots(), loanApplicationId, entityId, productId);
        }
        return true;
    }

    @Override
    public Integer getLoanApplicationIdFromLoanAplicationToken(String loanAplicationToken) throws Exception {
        JSONObject jsonDecrypted = new JSONObject(CryptoUtil.decrypt(loanAplicationToken));
        return jsonDecrypted.getInt("loan");
    }

    @Override
    public void sendPostSignatureInteractions(int userId, int loanApplicationId, HttpServletRequest request, HttpServletResponse response, SpringTemplateEngine templateEngine, Locale locale) throws Exception {
        //Send mail to CLient
        User user = userDao.getUser(userId);
        Person person = personDao.getPerson(catalogService, locale, user.getPersonId(), false);
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), locale, false, Credit.class);

        List<Integer> interactionIds = catalogService.getEntityProductParamById(credit.getEntityProductParameterId()).getPostSignatureInteractionIds();
        if (interactionIds != null) {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("CLIENT_NAME", person.getFirstName());
            jsonParams.put("ENTITY", credit.getEntity().getFullName());
            jsonParams.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
            jsonParams.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);
            if (credit.getEmployer() != null) {
                jsonParams.put("EMPLOYER", credit.getEmployer().getName());
            }

            for (Integer interactionId : interactionIds) {
                InteractionContent interactionContent = catalogService.getInteractionContent(interactionId, loanApplication.getCountryId());

                PersonInteraction interaction = new PersonInteraction();
                interaction.setPersonId(user.getPersonId());
                interaction.setCreditId(loanApplication.getCreditId());
                interaction.setCreditCode(credit.getCode());
                interaction.setLoanApplicationId(loanApplication.getId());
                if (interactionContent.getType().getId() == InteractionType.MAIL)
                    interaction.setDestination(user.getEmail());
                else if (interactionContent.getType().getId() == InteractionType.SMS)
                    interaction.setDestination(user.getPhoneNumber());
                interaction.setInteractionType(interactionContent.getType());
                interaction.setInteractionContent(interactionContent);
                if(credit.getEntity().getId() == Entity.AZTECA){
                    interaction.setSenderName(String.format("%s de %s",
                            loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION,
                            credit.getEntity().getShortName()
                    ));
                }

                List<PersonInteractionAttachment> attachments = new ArrayList<>();
                if (credit.getProduct().getId() == Product.DEBT_CONSOLIDATION) {
                    PersonInteractionAttachment atachment = new PersonInteractionAttachment();
                    atachment.setBytes(creditService.renderAssociatedFileFromPdf(credit, person, user, locale));
                    atachment.setFilename("Pagare.pdf");
                    attachments.add(atachment);
                } else if (credit.getProduct().getId() == Product.AGREEMENT) {
                    PersonInteractionAttachment atachment = new PersonInteractionAttachment();
                    atachment.setBytes(creditService.renderAssociatedFileFromPdf(credit, person, user, locale));
                    atachment.setFilename("Ficha de socio.pdf");
                    attachments.add(atachment);
                } else if (credit.getProduct().getId() == Product.AUTOS) {
                    if (credit.getEntity().getId() == Entity.ACCESO) {
                        if (credit.getVehicle().getVehicleDealership().getContactInformationImageUrls() != null) {
                            String dealershipContactImages = "";
                            for (String url : credit.getVehicle().getVehicleDealership().getContactInformationImageUrls()) {
                                dealershipContactImages = dealershipContactImages + "<img src=\"" + url + "\"><br/>";
                            }
                            jsonParams.put("CONTACT_INFORMATION", dealershipContactImages);
                        }
                    }
                }

                if(interactionContent.getId() == InteractionContent.FINANSOL_APROBADO){
                    jsonParams.put("@GENDER", person.getGender() != null ? (person.getGender() == 'M' ? "o" : "a") : "@");
                    List<String> ccMails = Arrays.asList("gtarazona@finansol.pe",
                            "gmeza@finansol.pe",
                            "cnolazco@finansol.pe",
                            "gcueva@finansol.pe",
                            "ycajas@finansol.pe",
                            "rrodriguez.finansol@gmail.com",
                            "nrojas.finansol@gmail.com",
                            "malcedo@finansol.pe",
                            "esajami.finansol@gmail.com",
                            "dvilca.finansol@gmail.com",
                            "gc@finansol.pe");
                    interaction.setCcMails(ccMails.toArray(new String[ccMails.size()]));
                }
                else if(interactionContent.getId() == InteractionContent.PRISMA_APROBADO){
                    jsonParams.put("@GENDER", person.getGender() != null ? (person.getGender() == 'M' ? "o" : "a") : "@");
                }
                else{
                    if (loanApplication.getStatus().getId() == LoanApplicationStatus.APPROVED_SIGNED) {
                        PersonInteractionAttachment atachment = new PersonInteractionAttachment();
                        atachment.setBytes(creditService.createContract(credit.getId(), request, response, locale, templateEngine, "Contrato.pdf", true));
                        atachment.setFilename("Contrato.pdf");
                        attachments.add(atachment);
                    }
                }


                interaction.setAttachments(attachments);

                //interactionService.modifyInteractionContent(interaction,loanApplication);

                interactionService.sendPersonInteraction(interaction, jsonParams, null);
            }
        }

        // Send disbursement mail only if is Solven entity or the disbursement is on us
        EntityProductParams entityProductParam = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());
        if (!entityProductParam.getSelfDisbursement() || credit.getEntity().getId() == Entity.AFFIRM)
            //Send mail to bo
            awsSesEmailService.sendEmail(
                    Configuration.getDisbursmentMailTo()//from
                    , Configuration.getDisbursmentMailTo()//to
                    , null//cc
                    , "¡Hora de hacer un desembolso! =D"//title
                    , "Corre, Rosita, CORRE!"//body
                    , null
                    , null);//others

        LoanApplicationAuxData loanApplicationAuxData = loanApplication.getAuxData();
        if(loanApplicationAuxData == null) loanApplicationAuxData = new LoanApplicationAuxData();
        loanApplicationAuxData.setSentPostSignatureInteraction(true);
        loanApplicationDao.updateAuxData(loanApplicationId, loanApplicationAuxData);
    }

    @Override
    public void sendWaitingForDownPaymentInteraction(int userId, int loanApplicationId, Locale locale) throws Exception {
        //Send mail to CLient
        User user = userDao.getUser(userId);
        Person person = personDao.getPerson(catalogService, locale, user.getPersonId(), false);
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), locale, false, Credit.class);

        PersonInteraction interaction = new PersonInteraction();
        interaction.setPersonId(user.getPersonId());
        interaction.setCreditId(loanApplication.getCreditId());
        interaction.setCreditCode(credit.getCode());
        interaction.setLoanApplicationId(loanApplication.getId());
        interaction.setDestination(user.getEmail());
        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.REGISTRO_LOANAPPLICATION_SIGNATURE_MAIL_VEHICLE_V2, loanApplication.getCountryId()));

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("CLIENT_NAME", person.getFirstName());
        jsonParams.put("ENTITY", credit.getEntity().getFullName());
        jsonParams.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        jsonParams.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);
        jsonParams.put("CAR_DEALERSHIP", credit.getVehicle().getVehicleDealership().getName());
        jsonParams.put("CAR_DEALERSHIP_RUC", credit.getVehicle().getVehicleDealership().getRuc());
        jsonParams.put("DOWN_PAYMENT", utilService.doubleMoneyFormat(credit.getDownPayment(), Currency.USD_CURRENCY));
        if (credit.getVehicle().getVehicleDealership().getBankAccountList() != null) {
            String dealershipAccounts = "";
            for (BankAccount account : credit.getVehicle().getVehicleDealership().getBankAccountList()) {
                dealershipAccounts = dealershipAccounts + "<b>" + account.getBank().getName() + "</b><br/>";
                if (account.getPaymentType() != null)
                    dealershipAccounts = dealershipAccounts + (account.getPaymentType() == BankAccount.PAYMENT_TYPE_INTERNET ? "<u>Pagos por internet</u>" : "<u>Pagos por ventanilla</u>") + "<br/>";
                dealershipAccounts = dealershipAccounts + (account.getAccountType() == BankAccount.ACCOUNT_TYPE_CORRIENTE ? "N° de Cta. Corriente " : "N° de convenio de recaudo ") +
                        (account.getCurrencyId() == Currency.USD ? "Dólares" : "Soles") + " " + account.getAccountNumber() + "<br/>";
                if (account.getCciCode() != null)
                    dealershipAccounts = dealershipAccounts + "CCI " + account.getCciCode() + "<br/><br/>";
            }
            jsonParams.put("CAR_DEALERSHIP_ACCOUNTS", dealershipAccounts);
        }

        interactionService.sendPersonInteraction(interaction, jsonParams, null);

        // Send SMS
        interaction = new PersonInteraction();
        interaction.setPersonId(user.getPersonId());
        interaction.setCreditId(loanApplication.getCreditId());
        interaction.setCreditCode(credit.getCode());
        interaction.setLoanApplicationId(loanApplication.getId());
        interaction.setDestination(user.getPhoneNumber());
        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.SMS));
        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.REGISTRO_LOANAPPLICATION_SIGNATURE_SMS_VEHICLE, loanApplication.getCountryId()));
        interactionService.sendPersonInteraction(interaction, jsonParams, null);
    }

    @Override
    public String generateLoanApplicationToken(int userId, int personId, int loanApplicationId) {
        return generateLoanApplicationToken(userId, personId, loanApplicationId, null);
    }

    @Override
    public String generateLoanApplicationToken(int userId, int personId, int loanApplicationId, boolean signLink) {
        if (signLink) {
            Map<String, Object> param = new HashMap<>();
            param.put("signlink", true);
            return generateLoanApplicationToken(userId, personId, loanApplicationId, param);
        } else {
            return generateLoanApplicationToken(userId, personId, loanApplicationId, null);
        }
    }

    @Override
    public String generateLoanApplicationToken(int userId, int personId, int loanApplicationId, Map<String, Object> extraParams) {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("loan", loanApplicationId);
        jsonParam.put("user", userId);
        jsonParam.put("person", personId);
        if (extraParams != null) {
            extraParams.forEach((k, v) -> jsonParam.put(k, v));
        }
        return CryptoUtil.encrypt(jsonParam.toString());
    }

    // TODO Move to WebscrapperService -> Rename webscrapperService to BotService
    @Override
    public void executeEmailage(Integer userId, String email, String ip) throws Exception {
        // Register query
        JSONObject params = new JSONObject();

        params.put("userId", userId);
        params.put("email", email);
        params.put("ip", ip);
        QueryBot queryBot = botDao.registerQuery(Bot.EMAILAGE, QueryBot.STATUS_QUEUE, new Date(), params, userId);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.DEFAULT), queryBot.getId() + ""));
    }

    /*@Override
    public void callUserBotsIfLoanOk(int loanApplicationId, Person person, String ipAddress, Locale locale) throws Exception {
        boolean mustCallBots = loanApplicationDao.shouldCallBots(loanApplicationId);
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        if (mustCallBots) {
            webscrapperService.setCountry(person.getCountry());
            botsExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Run user bots
                        userService.callUserExternalDataBots(person.getUserId(), person.getDocumentType().getId(), person.getDocumentNumber(), person.getBirthday(), loanApplication.getGuaranteedVehiclePlate());
                        // Run scrappers of the ruc ocupation
                        List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(locale, person.getId());
                        if (ocupations != null) {
                            for (PersonOcupationalInformation ocupation : ocupations) {
                                if (ocupation.getRuc() != null) {
                                    webscrapperService.callClaroBot(IdentityDocumentType.RUC, ocupation.getRuc(), null);
                                    webscrapperService.callMovistarBot(IdentityDocumentType.RUC, ocupation.getRuc(), null);
                                    webscrapperService.callBitelBot(IdentityDocumentType.RUC, ocupation.getRuc(), null);
                                    webscrapperService.callSunatRucBot(ocupation.getRuc(), null);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ErrorServiceImpl.onErrorStatic(ex);
                    }
                }
            });
        }
        return;
    }*/

    @Override
    public Pair<Boolean, String> validateLoanApplicationApproval(Integer loanApplicationId) throws Exception {
        JSONObject jsonValidation = loanApplicationDao.preApprovalValidation(loanApplicationId);
        String messageSeparator = "<br/>";
        String message = null;
        boolean success = true;

        boolean v1 = jsonValidation.getBoolean("v1");
        success = success && v1;
        boolean v2 = jsonValidation.getBoolean("v2");
        success = success && v2;
        boolean v3 = jsonValidation.getBoolean("v3");
        success = success && v3;
            /*boolean v4 = jsonValidation.getBoolean("v4");
            success = success && phoneVerified;*/
        boolean v5 = jsonValidation.getBoolean("v5");
        success = success && v5;

        boolean v6 = jsonValidation.getBoolean("v6");
        success = success && v6;
        boolean v7 = jsonValidation.getBoolean("v7");
        success = success && v7;
        boolean v8 = jsonValidation.getBoolean("v8");
        success = success && v8;
            /*boolean v9 = jsonValidation.getBoolean("v9");
            success = success && v9;
            boolean v10 = jsonValidation.getBoolean("v10");
            success = success && phoneVerified;*/

        boolean v11 = jsonValidation.getBoolean("v11");
        success = success && v11;
            /*boolean v12 = jsonValidation.getBoolean("v12");
            success = success && phoneVerified;*/
        boolean v13 = jsonValidation.getBoolean("v13");
        success = success && v13;
            /*boolean v14 = jsonValidation.getBoolean("v14");
            success = success && phoneVerified;
            boolean v15 = jsonValidation.getBoolean("v15");
            success = success && phoneVerified;

            boolean v16 = jsonValidation.getBoolean("v16");
            success = success && phoneVerified;*/
        boolean v17 = jsonValidation.getBoolean("v17");
        success = success && v17;
        boolean v18 = jsonValidation.getBoolean("v18");
        success = success && v18;
        boolean v19 = jsonValidation.getBoolean("v19");
        success = success && v19;
           /* boolean v20 = jsonValidation.getBoolean("v20");
            success = success && v20;*/
        boolean v21 = jsonValidation.getBoolean("v21");
        success = success && v21;
        boolean v22 = jsonValidation.getBoolean("v22");
        success = success && v22;
        boolean v23 = jsonValidation.getBoolean("v23");
        success = success && v23;

        if (!success) {
            message = "";
            if (!v1) {
                message += "La fecha de inicio de la solicitud es mayor a 30 días." + messageSeparator;
            }
            if (!v2) {
                message += "El monto a desembolsar está fuera de los parámetros del producto." + messageSeparator;
            }
            if (!v3) {
                message += "La tasa está fuera de los parámetros permitidos en la política del producto." + messageSeparator;
            }
            if (!v5) {
                message += "Falta verificar la documentación obligatoria." + messageSeparator;
            }

            if (!v6) {
                message += "Falta verificar los ingresos." + messageSeparator;
            }
            if (!v7) {
                message += "Falta que todos los robots ejecuten correctamente." + messageSeparator;
            }
            if (!v8) {
                message += "Falta que Equifax se ejecute correctamente." + messageSeparator;
            }
                /*if(!v9) {
                    msg += "Falta ejecutar Iovation.\n";
                }*/

            if (!v11) {
                message += "El email ya se utilizó para otra solicitud de crédito con otro DNI." + messageSeparator;
            }
            if (!v13) {
                message += "El teléfono celular verificado se utilizó para otra solicitud de crédito con otro DNI." + messageSeparator;
            }

            if (!v17) {
                message += "El solicitante tiene un crédito en mora registrado en Solven." + messageSeparator;
            }
            if (!v18) {
                message += "El solicitante se encuentra en lista negra." + messageSeparator;
            }
            if (!v19) {
                message += "El solicitante tiene otra solicitud en proceso en ese momento." + messageSeparator;
            }
               /* if(!v20) {
                    msg += "El dispositivo utilizado por el solicitante tiene otra solicitud de crédito a nombre de un tercero.<br/>";
                }*/
            if (!v21) {
                message += "La fecha de nacimiento declarada no es correcta." + messageSeparator;
            }
            if (!v22) {
                message += "Los teléfonos de referencia no han sido validados." + messageSeparator;
            }
            if (!v23) {
                message += "Existen deudas a consolidar de Crédito de Consumo que no cuentan con Nro de Préstamo." + messageSeparator;
            }
        }

        return Pair.of(success, message);
    }

    @Override
    public void approveLoanApplicationAsync(int loanApplicationId, Integer sysUserId, HttpServletRequest request, HttpServletResponse response, SpringTemplateEngine templateEngine, Locale locale) throws Exception{
        approveLoanApplication(loanApplicationId, sysUserId, request, response, templateEngine, locale);
    }

    @Override
    public void approveLoanApplication(int loanApplicationId, Integer sysUserId, HttpServletRequest request, HttpServletResponse response, SpringTemplateEngine templateEngine, Locale locale) throws Exception {
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        List<LoanOffer> loanOffers = new ArrayList<>();
        if(loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.BANBIF) loanOffers = loanApplicationDao.getAllLoanOffers(loanApplicationId);
        else loanOffers = loanApplicationDao.getLoanOffers(loanApplicationId);
        LoanOffer offerSelected =  loanOffers.stream().filter(o -> o.getSelected()).findFirst().orElse(null);
        EntityProductParams entityParams = catalogService.getEntityProductParamById(offerSelected.getEntityProductParameterId());

        // If need to be audit, and it's not audited yet, just change the status of the loan app and dont create the credit yet.
        // Once it is approved in the audit, this function will be called again but
        if (entityParams.getLoanApplicationAuditTypes() != null && !entityParams.getLoanApplicationAuditTypes().isEmpty()
                && (loanApplication.getApprovedAudit() == null || !loanApplication.getApprovedAudit())) { // LoanAudit
            loanApplicationDao.updateLoanApplicationStatus(loanApplicationId, LoanApplicationStatus.APPROVED, sysUserId);
            return;
        }

        approveLoanApplicationWithoutAuditValidation(loanApplication, offerSelected, sysUserId, request, response, templateEngine, locale);
    }

    @Override
    public void approveLoanApplicationWithoutAuditValidation(LoanApplication loanApplication, LoanOffer offerSelected, Integer sysUserId, HttpServletRequest request, HttpServletResponse response, SpringTemplateEngine templateEngine, Locale locale) throws Exception {
        EntityProductParams entityParams = catalogService.getEntityProductParamById(offerSelected.getEntityProductParameterId());
        if(loanApplication.getSelectedEntityProductParameterId() == null) loanApplication.setSelectedEntityProductParameterId(entityParams.getId());

        loanApplicationApprovalValidationService.validateAndUpdate(loanApplication.getId(), ApprovalValidation.VERIF_CORREO_ELECTRONICO);

        // Validate that the loan has the approval validations done
        if (!loanApplicationApprovalValidationService.loanHasAllValidations(loanApplication, null)) {
            // It it has manual revision, go to evaluation extranet tray
            if (loanApplicationApprovalValidationService.loanHasAnyValidationsForManualApproval(loanApplication, null)) {
                throw new Exception("The loan has validations for manual approva");
            }
            // It it's from BAZ CUenta and has any failed validation, reject it
            if (offerSelected.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO &&
                    loanApplicationApprovalValidationService.loanHasAnyFailedValidations(loanApplication, null)) {
                registerEvaluationRejection(loanApplication, Entity.AZTECA, locale);
            }
            throw new Exception("The loan doesnt has all the validations");
        }

        if(Arrays.asList(
                EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE,
                EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO
        ).contains(entityParams.getId())){
           if(offerSelected.getSignatureFullName() == null || offerSelected.getSignatureFullName().isEmpty()) throw new Exception("The loan has not been signed");
        }

        // If is an Agreement from abaco, call the webservice to create associated
        if (offerSelected.getProduct().getId() == Product.AGREEMENT && offerSelected.getEntity().getId() == Entity.ABACO) {
            agreementService.sendAssociatedToEntity(loanApplication.getPersonId(), offerSelected.getEntity().getId(), locale, loanApplication.getId());
        }

        if (offerSelected.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_FDLM) {
            fdlmServiceCall.callCrearCredito(loanApplication, offerSelected);
        }

        Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), true);

        Credit credit = loanApplicationDao.generateCredit(loanApplication.getId(), locale);

        if (offerSelected.getProduct().getId() == ProductCategory.CONSUMO && offerSelected.getEntity().getId() == Entity.COMPARTAMOS) {
            /*if (result != null)
                creditDao.registerSchedule(credit.getId(), gson.toJson(result));*/
        } else {
            if (!Arrays.asList(EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO,EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_CERO_MEMBRESIA, EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO, EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY, EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY_VIGENTE,EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO,EntityProductParams.ENT_PROD_PARAM_AZTECA_IDENTIDAD).contains(offerSelected.getEntityProductParameterId())) {
                creditDao.generateOriginalSchedule(credit);
            }
        }

        try{
            //If the loan is a lead from sales doubler, call its postback
            if (offerSelected.getEntity().getId() != Entity.TARJETAS_PERUANAS) {
                if (loanApplication.getSource() != null && loanApplication.getSource().equalsIgnoreCase(LoanApplication.LEAD_SALESDOUBLER)) {
                    salesDoublerService.callCPAPostback(
                            loanApplication.getJsLeadParam() != null ? JsonUtil.getStringFromJson(loanApplication.getJsLeadParam(), "aff_sub", null) : null,
                            loanApplication.getId() + "");
                }
            }

            credit = creditDao.getCreditByID(credit.getId(), locale, true, Credit.class);

            if (offerSelected.getEntity().getId() == Entity.CAJASULLANA) {
                PersonAssociated personAssociated = personDao.getAssociated(person.getId(), Entity.CAJASULLANA);
                PersonContactInformation personContactInformation = personDao.getPersonContactInformation(locale, person.getId());
                JSONObject serviceAdmisibilidad = webServiceDAO.getExternalServiceResponse(loanApplication.getId(), EntityWebService.CAJASULLANA_ADMISIBILIDAD);
                JSONObject serviceResponse = webServiceDAO.getExternalServiceResponse(loanApplication.getId(), EntityWebService.CAJASULLANA_EXPERIAN);
                PersonOcupationalInformation personOcupationalInformation = personDao.getPersonOcupationalInformation(locale, person.getId()).stream().filter(e -> e.getNumber().equals(1)).findFirst().orElse(null);

                boolean newClient = false;
                List<ListaModalidad> listaModalidadList = new ArrayList<>();
                JSONArray arr = JsonUtil.getJsonArrayFromJson(serviceAdmisibilidad.getJSONObject("result"), "listaModalidad", null);
                if (arr != null) {
                    for (int i = 0; i < arr.length(); i++) {
                        ListaModalidad listaModalidad = new ListaModalidad(arr.getJSONObject(i));
                        if (listaModalidad.getCodigoClase() == 0) {
                            newClient = true;
                            break;
                        }
                    }
                }

                GenerarSolicitudRequest generarCreditoRequest = new GenerarSolicitudRequest(
                        translatorDAO,
                        catalogService,
                        person,
                        personAssociated != null ? personAssociated.getAssociatedId() : "",
                        JsonUtil.getStringFromJson(serviceResponse.getJSONObject("result"), "idEvaluacionExperian", null),
                        credit, personContactInformation, personOcupationalInformation,
                        null, newClient,
                        JsonUtil.getStringFromJson(serviceResponse.getJSONObject("result"), "idEmail", null),
                        JsonUtil.getStringFromJson(serviceResponse.getJSONObject("result"), "idCelular", null)

                );
                cajaSullanaServiceCall.callGenerarSolcitud(generarCreditoRequest, loanApplication.getId());
            } else if (credit.getProduct().getId() == ProductCategory.CONSUMO && credit.getEntity().getId() == Entity.COMPARTAMOS) {
                GenerarCreditoResponse result = null;
                User user = userDao.getUser(userDao.getUserIdByPersonId(loanApplication.getPersonId()));
                List<PersonOcupationalInformation> personOcupationalInformations = personDao.getPersonOcupationalInformation(locale, loanApplication.getPersonId());
                PersonOcupationalInformation personOcupationalInformation = null;

                PersonContactInformation personContactInformation = personDao.getPersonContactInformation(locale, loanApplication.getPersonId());
                List<DisggregatedAddress> disggregatedAddresses = personDao.getDisggregatedAddress(loanApplication.getPersonId());
                PersonBankAccountInformation personBankAccountInformation = personDao.getPersonBankAccountInformation(locale, loanApplication.getPersonId());

                if (personOcupationalInformations != null) {
                    personOcupationalInformation = personOcupationalInformations.stream().filter(p -> p.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst().orElse(null);
                }

                SunatResult sunatResult = personDao.getSunatResult(loanApplication.getPersonId());
                SunatResult sunatResultRuc = null;
                if (personOcupationalInformation != null && personOcupationalInformation.getRuc() != null) {
                    sunatResultRuc = personDao.getSunatResultByRuc(personOcupationalInformation.getRuc());
                }
                ExperianResult experianResult = loanApplicationDao.getExperianResultList(loanApplication.getId()).get(0);
                RucInfo rucInfo = personDao.getRucInfo(personOcupationalInformation.getRuc());
                LoanOffer loanOffer = loanApplicationDao.getLoanOffers(loanApplication.getId()).stream().filter(e -> e.getSelected()).findFirst().orElse(null);

                GenerarCreditoRequest generarCreditoRequest = new GenerarCreditoRequest("01", //Código 01 - Generación de un crédito
                        person,
                        user,
                        personOcupationalInformation,
                        disggregatedAddresses,
                        loanApplication.getRegisterDate(),
                        loanApplication, loanOffer,
                        personBankAccountInformation,
                        personContactInformation,
                        sunatResult,
                        experianResult,
                        rucInfo,
                        translatorDAO,
                        null,
                        sunatResultRuc,
                        credit);

                result = compartamosServiceCall.callGeneracionCredito(generarCreditoRequest, loanApplication.getId(), credit.getId());
                Gson gson = new Gson();
                creditDao.registerSchedule(credit.getId(), gson.toJson(result));
                loanApplicationDao.updateEntityApplicationCode(loanApplication.getId(), result.getCredito().getNumeroCuenta());
            }

            // Call abaco if the person is assciated, else wait till RRHH check the associated check
            if (credit.getProduct().getId() == Product.AGREEMENT && credit.getEntity().getId() == Entity.ABACO) {
                PersonAssociated associated = personDao.getAssociated(loanApplication.getPersonId(), credit.getEntity().getId());
                if (associated.getValidated() && credit.getEntityCreditCode() == null) {
                    agreementService.createAssociatedCredit(person, credit);
                }
            } else if (credit.getProduct().getId() == Product.AGREEMENT && credit.getEntity().getId() == Entity.EFL) {
                PersonAssociated associated = personDao.getAssociated(loanApplication.getPersonId(), credit.getEntity().getId());
                if (associated == null)
                    personDao.registerAssociated(person.getId(), credit.getEntity().getId(), null, null);
            } else if (credit.getProduct().getId() == Product.DEBT_CONSOLIDATION) {
                PersonAssociated associated = personDao.getAssociated(loanApplication.getPersonId(), credit.getEntity().getId());
                if (associated == null)
                    personDao.registerAssociated(person.getId(), credit.getEntity().getId(), null, null);
            }

            // Update the status of the loan to Approved and Signed if it does have a signature, or to Approved if it doesnt
            if (offerSelected.getSignatureFullName() != null && !offerSelected.getSignatureFullName().isEmpty()) {
                loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.APPROVED, sysUserId);
                loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.APPROVED_SIGNED, sysUserId);
                loanApplication.setStatus(catalogService.getLoanApplicationStatus(locale, LoanApplicationStatus.APPROVED_SIGNED));
            } else {
                loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.APPROVED, sysUserId);
                loanApplication.setStatus(catalogService.getLoanApplicationStatus(locale, LoanApplicationStatus.APPROVED));
            }

            if (EntityProductParams.ENT_PROD_PARAM_RIPLEY_PRESTAMOYA.contains(offerSelected.getEntityProductParameterId())) {
                byte[] pdfAsBytes = generateLoanRequestSheet(credit);
                String filename = "Solicitud_Credito_Ripley.xls";
                fileService.writeUserFile(pdfAsBytes, loanApplication.getUserId(), filename);
                userDao.registerUserFile(loanApplication.getUserId(), loanApplication.getId(), UserFileType.SOLICITUD_CREDITO_RIPLEY, filename);
            }

            // If is acceso LD, create the expediente, create the cnotract and call the documentation WS
            if (EntityProductParams.ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD.contains(offerSelected.getEntityProductParameterId())) {
                loanApplication = loanApplicationDao.getLoanApplication(loanApplication.getId(), locale);
                if(loanApplication.getEntityApplicationCode() == null){
                    String codExpediente = accesoServiceCall.callGenerarExpediente(loanApplication, credit);
                    if(codExpediente != null){
                        loanApplication.setEntityApplicationCode(codExpediente);
                        loanApplicationDao.updateEntityApplicationCode(loanApplication.getId(), codExpediente);
                    }
                }

                if(credit.getEntityCreditCode() == null){
                    String codCredito = accesoServiceCall.callGenerarExpediente(loanApplication, credit);
                    if(codCredito != null){
                        credit.setEntityCreditCode(codCredito);
                        creditDao.updateCrediCodeByCreditId(credit.getId(), codCredito);
                    }
                }

                creditService.createContract(credit.getId(), request, response, locale, templateEngine, "Contrato.pdf", false);
                accesoServiceCall.callGenerarDocumentacion(loanApplication);

                // Change status to originated
                creditDao.updateCreditStatus(credit.getId(), CreditStatus.ORIGINATED, credit.getEntity().getId());
                creditDao.updateCreditSubStatus(credit.getId(), CreditSubStatus.PENDING_ENTIY_DISBURSEMENT);
            }

            // Send email to the user
            // Banco del Sol should not send any interaction
            if(!Arrays.asList(EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL, EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL_AGENCIAS, EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL_CAMPANA,
                    EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL_SINIESTROS, EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO, EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_CERO_MEMBRESIA, EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO, EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY, EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY_VIGENTE,
                    EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO, EntityProductParams.ENT_PROD_PARAM_AZTECA_IDENTIDAD, EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE
            ).contains(entityParams.getId())){
                if (loanApplication.getStatus().getId() == LoanApplicationStatus.APPROVED_SIGNED) {
//                    IF SHOULD NOT CREATE CONTRACT AGAIN FOR ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD
                    if (!EntityProductParams.ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD.contains(offerSelected.getEntityProductParameterId())) {
                        //Create and save the contract
                        creditService.createContract(credit.getId(), request, response, locale, templateEngine, "Contrato.pdf", false);
                    }
                    // Send the signature mail to the client
                    if (credit.getProduct().getId() == Product.AUTOS)
                        sendWaitingForDownPaymentInteraction(loanApplication.getUserId(), loanApplication.getId(), locale);
                    else
                        sendPostSignatureInteractions(loanApplication.getUserId(), loanApplication.getId(), request, response, templateEngine, locale);
                }else{
                    if (offerSelected.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AELU_CONVENIO) {
                        byte[] zipAsBytes = aeluService.generatePreliminaryDocumentation(loanApplication.getId());
                        String filename = FileServiceImpl.AELU_DOCUMENTACION_PRELIMINAR;
                        fileService.writeUserFile(zipAsBytes, loanApplication.getUserId(), filename);
                        userDao.registerUserFile(loanApplication.getUserId(), loanApplication.getId(), UserFileType.PRELIMINARY_DOCUMENTATION, filename);
                        aeluService.sendPreliminaryDocumentationEmail(loanApplication.getId(), zipAsBytes);
                    }else if (offerSelected.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_CREDIGOB_PROOVEDOR_ESTADO) {
                        credigobService.sendApproveLoanApplicationEmail(loanApplication, credit, person);
                    } else {
                        if (entityParams.getSignatureType() == EntityProductParams.CONTRACT_TYPE_DIGITAL || entityParams.getSignatureType() == EntityProductParams.CONTRACT_TYPE_MIXTO) {
                            if(!Arrays.asList(
                                    EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO,
                                    EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE,
                                    EntityProductParams.ENT_PROD_PARAM_PRISMA_DEPENDIENTES,
                                    EntityProductParams.ENT_PROD_PARAM_PRISMA_MICROEMPRENDEDORES,
                                    EntityProductParams.ENT_PROD_PARAM_PRISMA_SOCIIOS_ACTUALES
                                    ).contains(offerSelected.getEntityProductParameterId())) sendLoanApplicationApprovalMail(loanApplication.getId(), loanApplication.getPersonId(), locale);
                        }
                        else if (entityParams.getExtranetCreditGeneration() != null &&
                                entityParams.getExtranetCreditGeneration() &&
                                offerSelected.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_PRISMA_DEPENDIENTES &&
                                offerSelected.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_PRISMA_MICROEMPRENDEDORES &&
                                offerSelected.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_PRISMA_SOCIIOS_ACTUALES &&
                                offerSelected.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_FDLM &&
                                offerSelected.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_AZTECA)
                            sendLoanApplicationExtranetApprovalMail(loanApplication.getId(), loanApplication.getPersonId(), entityParams.getDisbursementType(), locale);
                    }
                }
            }

            if (offerSelected.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_FDLM) {
                creditDao.updateCreditStatus(credit.getId(), CreditStatus.ORIGINATED, null);
                creditDao.updateCreditSubStatus(credit.getId(), CreditSubStatus.PENDING_ENTIY_DISBURSEMENT);

                User user = userDao.getUser(userDao.getUserIdByPersonId(loanApplication.getPersonId()));

                PersonInteraction personInteraction = new PersonInteraction();
                personInteraction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                personInteraction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.FDLM_CREDIT_DISBURSEMENT, loanApplication.getCountry().getId()));
                personInteraction.setDestination(user.getEmail());
                personInteraction.setPersonId(loanApplication.getPersonId());
                personInteraction.setLoanApplicationId(loanApplication.getId());
                personInteraction.setCreditId(credit.getId());

                Sucursal sucursal = externalWebService.getSucursalFdlm(loanApplication.getId());

                byte[] contract = fileService.getAssociatedFile("20200203_CONTRATO_UNIFICADO_DE_CREDITO_DIGITAL_(FDM_V.1).pdf");
                PersonInteractionAttachment atachment = new PersonInteractionAttachment();
                atachment.setBytes(contract);
                atachment.setFilename("CONTRATO_UNIFICADO_DE_CREDITO_DIGITAL.pdf");
                personInteraction.setAttachments(Arrays.asList(atachment));

                JSONObject jsonVars = new JSONObject();
                jsonVars.put("CLIENT_NAME", person.getFirstName());
                jsonVars.put("CLIENT_LASTNAME", person.getFirstSurname());
                jsonVars.put("AMOUNT", "\\" + utilService.doubleMoneyFormat(offerSelected.getAmmount(), loanApplication.getCurrency()));
                jsonVars.put("ADDRESS_NAME", sucursal.getNombre());
                jsonVars.put("ADDRESS_NUMBER", sucursal.getDireccion());

                interactionService.sendPersonInteraction(personInteraction, jsonVars, null);
            }
            if (Arrays.asList(
                    EntityProductParams.ENT_PROD_PARAM_FINANSOL_CONSUMO,
                    EntityProductParams.ENT_PROD_PARAM_FINANSOL_CONSUMO_BASE,
                    EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO,
                    EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_CERO_MEMBRESIA,
                    EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO,
                    EntityProductParams.ENT_PROD_PARAM_AZTECA,
                    EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE,
                    EntityProductParams.ENT_PROD_PARAM_PRISMA_MICROEMPRENDEDORES,
                    EntityProductParams.ENT_PROD_PARAM_PRISMA_DEPENDIENTES,
                    EntityProductParams.ENT_PROD_PARAM_PRISMA_SOCIIOS_ACTUALES
            ).contains(offerSelected.getEntityProductParameterId())) {
                if(offerSelected.getEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE){
                    creditDao.updateCreditStatus(credit.getId(), CreditStatus.ORIGINATED, null);
                    creditDao.updateCreditSubStatus(credit.getId(), CreditSubStatus.PENDING_ENTIY_DISBURSEMENT);
                }

                sendInteractionTcEnCaminoIfBanBif(offerSelected, loanApplication, credit, person);
                sendInteractionEmail(offerSelected,loanApplication,credit,person);
            }

            if (Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO).contains(offerSelected.getEntityProductParameterId())) {
                //sendLoanApplicationApprovalMail(loanApplication.getId(), loanApplication.getPersonId(), locale);
            //    creditDao.updateCreditStatus(credit.getId(), CreditStatus.ORIGINATED, null);
            //    creditDao.updateCreditSubStatus(credit.getId(), CreditSubStatus.PENDING_ENTIY_DISBURSEMENT);
            //    creditService.createContract(credit.getId(), request, response, locale, templateEngine, "Contrato.pdf", false);
            }

            if (Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE).contains(offerSelected.getEntityProductParameterId())) {
                //sendLoanApplicationApprovalMail(loanApplication.getId(), loanApplication.getPersonId(), locale);
            }

            if (Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_IDENTIDAD).contains(offerSelected.getEntityProductParameterId())) {
                creditDao.updateCreditStatus(credit.getId(), CreditStatus.ORIGINATED_DISBURSED, null);
                creditDao.updateCreditSubStatus(credit.getId(), CreditSubStatus.PENDING_ENTIY_LOAD);
            }

            if (Arrays.asList(
                    EntityProductParams.ENT_PROD_PARAM_PRISMA_DEPENDIENTES,
                    EntityProductParams.ENT_PROD_PARAM_PRISMA_MICROEMPRENDEDORES,
                    EntityProductParams.ENT_PROD_PARAM_PRISMA_SOCIIOS_ACTUALES
            ).contains(offerSelected.getEntityProductParameterId())) {
                creditService.createContract(credit.getId(), request, response, locale, templateEngine, "Contrato.pdf", false);
            }

            if(offerSelected.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY || offerSelected.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY_VIGENTE){
                creditDao.updateCreditStatus(credit.getId(), CreditStatus.ACCEPTED_OFFER, credit.getEntity().getId());
            }

            // Send the email to the entity informing of the new credit
            if (entityParams.getCreditGenerationEntityAlertInteraction() != null) {
                String emailTo = null;
                List<String> emailCC = new ArrayList<>();
                JSONArray arrayRecivers = entityParams.getCreditGenerationEntityAlertInteraction().getJSONArray("receiver");
                for (int i = 0; i < arrayRecivers.length(); i++) {
                    if (i == 0)
                        emailTo = arrayRecivers.getString(i);
                    else
                        emailCC.add(arrayRecivers.getString(i));
                }
                InteractionContent emailContent = catalogService.getInteractionContent(entityParams.getCreditGenerationEntityAlertInteraction().getInt("interaction_content_id"), loanApplication.getCountryId());

                awsSesEmailService.sendRawEmail(
                        null,
                        emailContent.getFromMail(),
                        null,
                        emailTo,
                        emailCC.toArray(new String[emailCC.size()]),
                        emailContent.getSubject().replaceAll("%PRODUCT_NAME%", entityParams.getEntityProduct() != null ? entityParams.getEntityProduct() : ""),
                        null,
                        emailContent.getBody(),
                        emailContent.getTemplate(),
                        null, null, emailContent.getHourOfDay(), null);
            }
            // Call webhook if it exists informing the approved event
            if(loanApplication.getAuxData() != null && loanApplication.getAuxData().getApiRestWebhookUrl() != null){
                sendLoanWebhookEvent("approved", loanApplication);
            }

            if(offerSelected.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO || offerSelected.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_CERO_MEMBRESIA || offerSelected.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO){
                this.sendBanBifTCLead("NEW_TC_REQUEST", loanApplication.getId(),locale);
            }


            // update the loan funnel steps
            loanApplication = loanApplicationDao.getLoanApplication(loanApplication.getId(), locale);
            funnelStepService.registerStep(loanApplication);


            if (Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_IDENTIDAD).contains(offerSelected.getEntityProductParameterId())) {
                // Update the user ohne number and email
                userService.activateUserEmailPhoneNumber(loanApplication.getAuxData().getRegisteredPhoneNumberId(), loanApplication.getAuxData().getRegisteredEmailId(), loanApplication.getUserId());

                //LLAMADA BPEOPLE
                User user = userDao.getUser(loanApplication.getUserId());
                bPeopleApiRestService.bPeopleCrearUsuario(loanApplication,person,user);
            }

        }catch(Exception ex){
            // If there is an error in the approve and is Acceso LD, return the credit to loan application for the retry to works
            if(Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE, EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO).contains(entityParams.getId()) || EntityProductParams.ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD.contains(entityParams.getId())){
                creditDao.returnCreditToLoanApplication(credit.getId());
            }
            throw ex;
        }
    }

    @Override
    public void sendInteractionTcEnCaminoIfBanBif(LoanOffer offerSelected, LoanApplication loanApplication, Credit credit, Person person) throws Exception {
        if (offerSelected.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO || offerSelected.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_CERO_MEMBRESIA || offerSelected.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO) {
            User user = userDao.getUser(userDao.getUserIdByPersonId(loanApplication.getPersonId()));

            PersonInteraction personInteraction = new PersonInteraction();
            personInteraction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
            personInteraction.setInteractionContent(catalogService.getInteractionContent(
                    offerSelected.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO ? InteractionContent.BANBIF_TC_EN_CAMINO_MAS_EFECTIVO : InteractionContent.BANBIF_TC_EN_CAMINO, loanApplication.getCountry().getId()));
            personInteraction.setDestination(user.getEmail());
            personInteraction.setPersonId(loanApplication.getPersonId());
            personInteraction.setLoanApplicationId(loanApplication.getId());
            personInteraction.setCreditId(credit.getId());

            JSONObject jsonVars = new JSONObject();
            jsonVars.put("CLIENT_NAME", person.getFirstName());
            jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);
            jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);

            interactionService.sendPersonInteraction(personInteraction, jsonVars, null);
        }
    }

    @Override
    public void sendInteractionEmail(LoanOffer offerSelected, LoanApplication loanApplication, Credit credit, Person person) throws Exception {
        PersonInteraction personInteraction = null;
        JSONObject jsonVars = new JSONObject();
        User user = userDao.getUser(userDao.getUserIdByPersonId(loanApplication.getPersonId()));
        String loanApplicationToken = generateLoanApplicationToken(person.getUserId(), person.getId(), loanApplication.getId(), true);
        String link =  generateLoanApplicationLinkEntity(loanApplication, loanApplicationToken);



        jsonVars.put("LINK", link);
        jsonVars.put("CLIENT_NAME", person.getFirstName());
        jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

        if(personInteraction != null) interactionService.sendPersonInteraction(personInteraction, jsonVars, null);
    }

    public void sendBanBifTCLead(String type, Integer loanApplicationId, Locale locale) throws Exception {
        if(type == null || loanApplicationId == null) return;
        if(locale == null) locale = Configuration.getDefaultLocale();
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), true);
        User user = userDao.getUser(userDao.getUserIdByPersonId(loanApplication.getPersonId()));
        String bodyMessage = "";
        String subject = "";
        String[] ccMails = null;
        switch (type){
            case "NEW_TC_REQUEST":

                List<LoanOffer> loanOffers =  loanApplicationDao.getAllLoanOffers(loanApplicationId);
                LoanOffer offerSelected =  loanOffers != null ? loanOffers.stream().filter(o -> o.getSelected()).findFirst().orElse(null) : null;

                JSONObject data = JsonUtil.getJsonObjectFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey(), null);
                String getEntityCustomDataValue = data != null ? data.toString() : null;
                BanbifPreApprovedBase banbifPreApprovedBase =  new Gson().fromJson(getEntityCustomDataValue, BanbifPreApprovedBase.class);
                String personFullName = String.format("%s%s, %s", person.getFirstSurname() != null ? person.getFirstSurname() : "",person.getLastSurname() != null ? " "+person.getLastSurname() : "", person.getName() != null ? person.getName() : null);
                if(banbifPreApprovedBase.getNombres() != null && !banbifPreApprovedBase.getNombres().trim().isEmpty()){
                    personFullName = String.format("%s, %s", banbifPreApprovedBase.getApellidos() != null ? banbifPreApprovedBase.getApellidos().trim() : "", banbifPreApprovedBase.getNombres() != null ? banbifPreApprovedBase.getNombres().trim() : "");
                }
                subject = String.format("Nueva solicitud Solven - Base: %s", banbifPreApprovedBase.getTipoBase() != null ? banbifPreApprovedBase.getTipoBase() : "");
                bodyMessage = "_@@idSubProducto|_@@nombreCompleto|_@@documento|_@@telefono|_@@email|Dólares|_@@monto||_@@puntos|_@@tipoTarjeta|_@@tipoSolicitud|_@@oficinaDepartamento|_@@oficinaDireccion|_@@fechaParaRecoger|_@@nroCuenta|_@@nroCuentaCCI|_@@ubigeoDomicilio|_@@zonaDomicilio|_@@tipoViaDomicilio|_@@nombreDomicilio|_@@nroDomicilio|_@@interiorDomicilio|_@@referenciaDomicilio|_@@nombreEmpresa|_@@ubigeoTrabajo|_@@zonaTrabajo|_@@tipoViaTrabajo|_@@nombreTrabajo|_@@nroTrabajo|_@@interiorTrabajo|_@@referenciaTrabajo|_@@envioA|_@@envioDescripcion|_@@aceptoAcuerdo";

                if(Arrays.asList(EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO).contains(offerSelected != null ? offerSelected.getEntityProductParameterId() : null)){
                    bodyMessage = "_@@idSubProducto|_@@nombreCompleto|_@@documento|_@@telefono|_@@email|_@@tipoSolicitud|_@@departamentoDomicilio|_@@provinciaDomicilio|_@@distritoDomicilio|_@@tipoZonaDomicilio|_@@nombreZonaDomicilio|_@@tipoViaDomicilio|_@@nombreViaDomicilio|_@@nroDomicilio|_@@interiorDomicilio|_@@referenciaDomicilio|_@@trabajaDondeVive|_@@departamentoTrabajo|_@@provinciaTrabajo|_@@distritoTrabajo|_@@tipoZonaTrabajo|_@@nombreZonaTrabajo|_@@tipoViaTrabajo|_@@nombreViaTrabajo|_@@nroTrabajo|_@@interiorTrabajo|_@@referenciaTrabajo|_@@envioDescripcion|_@@montoDesembolso|_@@plazo|_@@cuota|_@@cuentaTransferencia|_@@bancoDestino|_@@fechaFacturacion|_@@aceptacionUsoMisDatosPersonales|_@@rucEmpleador|_@@nombreEmpresa|_@@aceptoAcuerdo";
                }
                //Montos

                DecimalFormatSymbols unusualSymbols =
                        new DecimalFormatSymbols();
                unusualSymbols.setDecimalSeparator('.');
                unusualSymbols.setGroupingSeparator(',');
                DecimalFormat df = new DecimalFormat("#,###.00",unusualSymbols);

                if(!Arrays.asList(EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO).contains(offerSelected != null ? offerSelected.getEntityProductParameterId() : null)){
                    bodyMessage = bodyMessage.replace("_@@monto", df.format(banbifPreApprovedBase.getLinea()))
                            .replace("_@@puntos",banbifPreApprovedBase.getBonoBienvenida().toString());
                }
                if(Arrays.asList(EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO).contains(offerSelected != null ? offerSelected.getEntityProductParameterId() : null)){
                    PersonBankAccountInformation personBankAccountInformation = personDao.getPersonBankAccountInformation(locale, loanApplication.getPersonId());
                    Integer paymentDay = JsonUtil.getIntFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANBIF_TC_PAYMENT_DAY.getKey(), null);
                    bodyMessage = bodyMessage
                            .replace("_@@idSubProducto", "Mas efectivo")
                            .replace("_@@montoDesembolso", df.format(offerSelected.getAmmount()))
                            .replace("_@@plazo", offerSelected.getInstallments().toString())
                            .replace("_@@cuota", df.format(offerSelected.getInstallmentAmmount()))
                            .replace("_@@cuentaTransferencia", personBankAccountInformation != null && personBankAccountInformation.getCciCode() != null? personBankAccountInformation.getCciCode() : "_@@cuentaTransferencia")
                            .replace("_@@bancoDestino", personBankAccountInformation != null  && personBankAccountInformation.getBank() != null ? personBankAccountInformation.getBank().getName() : "_@@bancoDestino")
                            .replace("_@@fechaFacturacion", paymentDay != null ? paymentDay.toString() : "_@@fechaFacturacion")
                            .replace("_@@aceptacionUsoMisDatosPersonales", loanApplication.getAuxData() != null && loanApplication.getAuxData().getAcceptAgreement() != null && loanApplication.getAuxData().getAcceptAgreement() ? "Si" : "No");
                }
                //Direccion hogar

                Direccion disagregatedAddress = personDao.getDisggregatedAddress(loanApplication.getPersonId(), "H");
                if (disagregatedAddress != null) {
                    Ubigeo ubigeo = catalogService.getUbigeo(disagregatedAddress.getUbigeo());
                    if(ubigeo != null){
                        bodyMessage = bodyMessage.replace("_@@ubigeoDomicilio", String.format("%s, %s, %s",
                                ubigeo.getDepartment() != null ? ubigeo.getDepartment().getName() : "Departamento",
                                ubigeo.getProvince() != null ? ubigeo.getProvince().getName() : "Provincia",
                                ubigeo.getDistrict() != null ? ubigeo.getDistrict().getName() : "Distrito"
                                ));
                        if( ubigeo.getDepartment() != null) bodyMessage = bodyMessage.replace("_@@departamentoDomicilio", ubigeo.getDepartment().getName());
                        if( ubigeo.getProvince() != null) bodyMessage = bodyMessage.replace("_@@provinciaDomicilio", ubigeo.getProvince().getName());
                        if( ubigeo.getDistrict() != null) bodyMessage = bodyMessage.replace("_@@distritoDomicilio", ubigeo.getDistrict().getName());
                    }
                    bodyMessage = bodyMessage
                            .replace("_@@zonaDomicilio", disagregatedAddress.getTipoZona() != null ? getZoneType(disagregatedAddress.getTipoZona()) : "_@@zonaDomicilio")
                            .replace("_@@tipoZonaDomicilio", disagregatedAddress.getTipoZona() != null ? getZoneType(disagregatedAddress.getTipoZona()) : "_@@tipoZonaDomicilio")
                            .replace("_@@nombreZonaDomicilio",disagregatedAddress.getNombreZona() != null ? disagregatedAddress.getNombreZona() : "_@@nombreZonaDomicilio")
                            .replace("_@@tipoViaDomicilio",getStreetType(disagregatedAddress.getTipoVia()))
                            .replace("_@@nombreDomicilio",disagregatedAddress.getNombreVia() != null ? disagregatedAddress.getNombreVia() : "_@@nombreDomicilio")
                            .replace("_@@nombreViaDomicilio",disagregatedAddress.getNombreVia() != null ? disagregatedAddress.getNombreVia() : "_@@nombreViaDomicilio")
                            .replace("_@@nroDomicilio",disagregatedAddress.getNumeroVia() != null ? disagregatedAddress.getNumeroVia() : "_@@nroDomicilio")
                            .replace("_@@interiorDomicilio",disagregatedAddress.getNumeroInterior() != null ? disagregatedAddress.getNumeroInterior() : "_@@interiorDomicilio")
                            .replace("_@@referenciaDomicilio",disagregatedAddress.getReferencia() != null ? disagregatedAddress.getReferencia() : "_@@referenciaDomicilio");
                }
                //Direccion trabajo
                Direccion disagregatedLabAddress = personDao.getDisggregatedAddress(loanApplication.getPersonId(), "J");
                if (disagregatedLabAddress != null) {
                    Ubigeo ubigeoLab = catalogService.getUbigeo(disagregatedLabAddress.getUbigeo());
                    if(ubigeoLab != null){
                        bodyMessage = bodyMessage.replace("_@@ubigeoTrabajo", String.format("%s, %s, %s",
                                ubigeoLab.getDepartment() != null ? ubigeoLab.getDepartment().getName() : "Departamento",
                                ubigeoLab.getProvince() != null ? ubigeoLab.getProvince().getName() : "Provincia",
                                ubigeoLab.getDistrict() != null ? ubigeoLab.getDistrict().getName() : "Distrito"
                        ));
                        if( ubigeoLab.getDepartment() != null) bodyMessage = bodyMessage.replace("_@@departamentoTrabajo", ubigeoLab.getDepartment().getName());
                        if( ubigeoLab.getProvince() != null) bodyMessage = bodyMessage.replace("_@@provinciaTrabajo", ubigeoLab.getProvince().getName());
                        if( ubigeoLab.getDistrict() != null) bodyMessage = bodyMessage.replace("_@@distritoTrabajo", ubigeoLab.getDistrict().getName());
                    }
                    bodyMessage = bodyMessage
                            .replace("_@@zonaTrabajo", disagregatedLabAddress.getTipoZona() != null ? getZoneType(disagregatedLabAddress.getTipoZona()) : "_@@zonaTrabajo")
                            .replace("_@@tipoZonaTrabajo", disagregatedLabAddress.getTipoZona() != null ? getZoneType(disagregatedLabAddress.getTipoZona()) : "_@@tipoZonaTrabajo")
                            .replace("_@@nombreZonaTrabajo",disagregatedLabAddress.getNombreZona() != null ? disagregatedLabAddress.getNombreZona() : "_@@nombreZonaTrabajo")
                            .replace("_@@tipoViaTrabajo",getStreetType(disagregatedLabAddress.getTipoVia()))
                            .replace("_@@nombreTrabajo",disagregatedLabAddress.getNombreVia() != null ? disagregatedLabAddress.getNombreVia() : "_@@nombreTrabajo")
                            .replace("_@@nombreViaTrabajo",disagregatedLabAddress.getNombreVia() != null ? disagregatedLabAddress.getNombreVia() : "_@@nombreViaTrabajo")
                            .replace("_@@nroTrabajo",disagregatedLabAddress.getNumeroVia() != null ? disagregatedLabAddress.getNumeroVia() : "_@@nroTrabajo")
                            .replace("_@@interiorTrabajo",disagregatedLabAddress.getNumeroInterior() != null ? disagregatedLabAddress.getNumeroInterior() : "_@@interiorTrabajo")
                            .replace("_@@referenciaTrabajo",disagregatedLabAddress.getReferencia() != null ? disagregatedLabAddress.getReferencia() : "_@@referenciaTrabajo")
                            .replace("_@@oficinaDireccion", disagregatedLabAddress.getDireccionCompleta() != null ? disagregatedLabAddress.getDireccionCompleta() : "_@@oficinaDireccion")
                            .replace("_@@oficinaDepartamento", disagregatedLabAddress.getNumeroInterior() != null ? disagregatedLabAddress.getNumeroInterior() : "_@@oficinaDepartamento");
                    }
                    String envioA = "";
                    String envioADescripcion = "";
                    switch (JsonUtil.getStringFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANBIF_DIRECCION_ENTREGA.getKey(), "")){
                        case "H":
                            envioA = "1";
                            envioADescripcion = Arrays.asList(EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO).contains(offerSelected != null ? offerSelected.getEntityProductParameterId() : null) ? "Vivienda" : "Domicilio";
                            break;
                        case "J":
                            envioA = "2";
                            envioADescripcion = Arrays.asList(EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO).contains(offerSelected != null ? offerSelected.getEntityProductParameterId() : null) ? "Laboral" : "Oficina";
                            break;
                    }
                    String tipoDeSolicitud = "";
                    switch (JsonUtil.getStringFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANBIF_TIPO_SOLICITUD.getKey(), "")){
                        case "O":
                            tipoDeSolicitud = "SOLICITUD EN LINEA";
                            break;
                        case "T":
                            tipoDeSolicitud = "BANCA TELEFÓNICA";
                            break;
                    }
                    if(Arrays.asList(EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO).contains(offerSelected != null ? offerSelected.getEntityProductParameterId() : null)) tipoDeSolicitud = "SOLICITUD EN LINEA";
                    //Tipo de tarjeta
                    if(offerSelected.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_CERO_MEMBRESIA){
                        bodyMessage = bodyMessage.replace("_@@tipoTarjeta", String.format("/Portals/0/cuenta100digital/img/tc-%s.png", BanbifPreApprovedBase.BANBIF_ZERO_MEMBERSHIP_CARD.toLowerCase().replaceAll("visa","").trim().replaceAll(" ","-")));
                    }else if(banbifPreApprovedBase.getPlastico() != null && Arrays.asList(BanbifPreApprovedBase.BANBIF_GOLD_CARD,BanbifPreApprovedBase.BANBIF_CLASSIC_CARD,BanbifPreApprovedBase.BANBIF_INFINITE_CARD,BanbifPreApprovedBase.BANBIF_PLATINUM_CARD,BanbifPreApprovedBase.BANBIF_SIGNATURE_CARD,BanbifPreApprovedBase.BANBIF_MASTERCARD_CARD).contains(banbifPreApprovedBase.getPlastico())){
                        bodyMessage = bodyMessage.replace("_@@tipoTarjeta", String.format("/Portals/0/cuenta100digital/img/tc-%s.png", banbifPreApprovedBase.getPlastico().toLowerCase().replaceAll("visa","").trim().replaceAll(" ","-")));
                    }

                PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), Configuration.getDefaultLocale());
                    if(ocupation != null){
                        bodyMessage = bodyMessage.replace("_@@trabajaDondeVive", ocupation.getHomeAddress() != null && ocupation.getHomeAddress() ? "Si" : "No");
                    }
                    if(ocupation != null && ocupation.getCompanyName() != null && !ocupation.getCompanyName().isEmpty()){
                        bodyMessage = bodyMessage
                                .replace("_@@nombreEmpresa", ocupation.getCompanyName())
                                .replace("_@@rucEmpleador", ocupation.getRuc() != null ? ocupation.getRuc() : "_@@rucEmpleador");
                    }else{
                        StaticDBInfo staticDBInfo = personDao.getInfoFromStaticDB(loanApplication.getPersonId());
                        if(staticDBInfo != null && staticDBInfo.getRazonSocial() != null && !staticDBInfo.getRazonSocial().isEmpty()){
                            bodyMessage = bodyMessage
                                    .replace("_@@nombreEmpresa", staticDBInfo.getRazonSocial())
                                    .replace("_@@rucEmpleador", staticDBInfo.getRuc());
                        }
                    }

                //Datos personales
                bodyMessage = bodyMessage
                        .replace("_@@nombreCompleto", personFullName)
                        .replace("_@@documento",person.getDocumentNumber())
                        .replace("_@@telefono", user.getPhoneNumber())
                        .replace("_@@email", user.getEmail())
                        //TIPO DE SOLICITUD
                        .replace("_@@tipoSolicitud",!tipoDeSolicitud.isEmpty() ? tipoDeSolicitud : "_@@tipoDeSolicitud")
                        //ENVIO
                        .replace("_@@envioA",!envioA.isEmpty() ? envioA : "_@@envioA")
                        .replace("_@@envioDescripcion", !envioADescripcion.isEmpty() ? envioADescripcion : "_@@envioDescripcion")
                        .replace("_@@aceptoAcuerdo", loanApplication.getAuxData().getAcceptAgreement() != null && loanApplication.getAuxData().getAcceptAgreement()? "Si" : "No");

                if(Arrays.asList(EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO).contains(offerSelected != null ? offerSelected.getEntityProductParameterId() : null))
                    ccMails = new String[]{"NSANTANDER@banbif.com.pe","GRODRIGUEZ@banbif.com.pe","RJAUREGUI@banbif.com.pe","CRAZZA@banbif.com.pe","kleiva@banbif.com.pe","liliana.melo@konecta-group.com","fiorella.velasco@konecta-group.com","bancatelefonica_banbif@konecta-group.com","olenka.picon@konecta-group.com","marcello.arana@konecta-group.com","silvia.talledo@konecta-group.com"};

                    break;
            case "GOOD_SCORE":

                JSONObject entityCData = JsonUtil.getJsonObjectFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey(), null);
                String entityCustomDataValueS = entityCData != null ? entityCData.toString() : null;
                BanbifPreApprovedBase banbifPreApprovedBaseR =  new Gson().fromJson(entityCustomDataValueS, BanbifPreApprovedBase.class);
                String personName = person != null && person.getName() != null ? person.getName() : "";
                String personLastname = person != null && person.getFirstSurname() != null ? person.getFirstSurname() : "";
                if(banbifPreApprovedBaseR != null){
                    if(banbifPreApprovedBaseR.getNombres() != null && !banbifPreApprovedBaseR.getNombres().trim().isEmpty()) personName = banbifPreApprovedBaseR.getNombres().trim();
                    if(banbifPreApprovedBaseR.getApellidos() != null && !banbifPreApprovedBaseR.getApellidos().trim().isEmpty()) personLastname = banbifPreApprovedBaseR.getApellidos().trim().split(" ")[0];
                }
                subject = String.format("Interesado solicitud Solven - Base: %s", banbifPreApprovedBaseR.getTipoBase() != null ? banbifPreApprovedBaseR.getTipoBase() : "");

                bodyMessage = "Esta persona esta interesada y tiene buen score: <br>"+
                        "Nombre: {{name}} <br> Apellido: {{lastname}} <br> Tipo Doc.: {{docType}} <br> Número Doc.: {{docNumber}} <br> Email: {{email}} <br> Teléfono: {{phone}} <br> ";
                bodyMessage = bodyMessage
                        .replace("{{name}}", personName)
                        .replace("{{lastname}}", personLastname)
                        .replace("{{phone}}", user.getPhoneNumber())
                        .replace("{{email}}", user.getEmail())
                        .replace("{{docType}}", person.getDocumentType().getName())
                        .replace("{{docNumber}}", person.getDocumentNumber());
                break;

        }

        awsSesEmailService.sendRawEmail(
                null,
                "notificaciones@solven.pe",
                null,
                "rprincipe@banbif.com.pe",
                ccMails != null ? ccMails : new String[]{"hguerreros@banbif.com.pe","nlachosc@grupogss.com","CPITA@banbif.com.pe", "NSANTANDER@banbif.com.pe", "liliana.melo@konecta-group.com","fiorella.velasco@konecta-group.com","bancatelefonica_banbif@konecta-group.com","olenka.picon@konecta-group.com","marcello.arana@konecta-group.com","silvia.talledo@konecta-group.com"},
                subject,
                bodyMessage,
                bodyMessage,
                null,
                null, null, null, null);

    }

    @Override
    public String getStreetType(Integer streetTypeId) throws Exception {
        if(streetTypeId == null) return "-";
        List<StreetType> streetTypes = catalogService.getStreetTypesBanBif();
        StreetType streetType = streetTypes.stream().filter(e -> e.getId().equals(streetTypeId)).findFirst().orElse(null);
        if(streetType == null) return "-";
        return streetType.getType();
    }

    @Override
    public String getZoneType(Integer zoneType) throws Exception {
        if(zoneType == null) return "-";
        List<AreaType> areaTypes = catalogService.getAreaTypesBanBif();
        AreaType areaType = areaTypes.stream().filter(e -> e.getId().equals(zoneType)).findFirst().orElse(null);
        if(areaType == null) return "-";
        return areaType.getName();
    }

    @Override
    public void sendLoanApplicationConfirmScheduleMail(int loanApplicationId, int personId, Date scheduledDate, Integer appointmentScheduleId, Locale locale) throws Exception {
        PersonContactInformation personContact = personDao.getPersonContactInformation(locale, personId);
        Person person = personDao.getPerson(catalogService, locale, personId, false);
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        LoanOffer loanOffers = loanApplicationDao.getLoanOffers(loanApplicationId).stream().filter(e -> e.getSelected()).findFirst().orElse(null);

        // TODO Put in a threadpool
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonVars = new JSONObject();
                    String baseUrl = null;

                    jsonVars.put("CLIENT_NAME", person.getName().split(" ")[0]);
                    jsonVars.put("ENTITY", loanOffers.getEntity().getFullName());
                    jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
                    jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

                    jsonVars.put("APPOINTMENT_DATE", new SimpleDateFormat("dd/MM/yyyy").format(scheduledDate));
                    jsonVars.put("APPOINTMENT_HOUR", catalogService.getAppoimentSchedulesById(appointmentScheduleId).getStartTime());
                    jsonVars.put("APPOINTMENT_PLACE", loanApplication.getGuaranteedVehicleAppointmentPlace());

                    PersonInteraction interaction = new PersonInteraction();
                    interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                    interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.CONFIRM_SCHEDULE_GUARANTEED, loanApplication.getCountryId()));
                    interaction.setDestination(personContact.getEmail());
                    interaction.setLoanApplicationId(loanApplicationId);
                    interaction.setPersonId(person.getId());

                    interactionService.sendPersonInteraction(interaction, jsonVars, null);
                } catch (Exception ex) {
                    logger.error("Error sending signature email", ex);
                    ErrorServiceImpl.onErrorStatic(ex);
                }
            }
        }).start();
    }

    @Override
    public void sendLoanApplicationScheduleMail(int loanApplicationId, int personId, Locale locale) throws Exception {
        PersonContactInformation personContact = personDao.getPersonContactInformation(locale, personId);
        Person person = personDao.getPerson(catalogService, locale, personId, false);
        String loanApplicationToken = generateLoanApplicationToken(person.getUserId(), person.getId(), loanApplicationId, true);
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        LoanOffer loanOffers = loanApplicationDao.getLoanOffers(loanApplicationId).stream().filter(e -> e.getSelected()).findFirst().orElse(null);

        // TODO Put in a threadpool
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonVars = new JSONObject();
                    String baseUrl = null;

                    jsonVars.put("CLIENT_NAME", person.getName().split(" ")[0]);
                    jsonVars.put("ENTITY", loanOffers.getEntity().getFullName());

                    if (loanApplication.getEntityId() != null) {
                        EntityBranding entityBranding = catalogService.getEntityBranding(loanApplication.getEntityId());

                        if (entityBranding != null) {
                            baseUrl = Configuration.hostEnvIsLocal() ? "http://" : "https://";

                            baseUrl += (entityBranding.getSubdomain() + "." + entityBranding.getDomain());

                            baseUrl += Configuration.hostEnvIsLocal() ? ":8080" : "";
                        }
                    }


                    jsonVars.put("LINK", (baseUrl != null ? baseUrl : Configuration.getClientDomain()) + "/loanapplication/" + loanApplicationToken);

                    PersonInteraction interaction = new PersonInteraction();
                    interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                    interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.SCHEDULE_GUARANTEED, loanApplication.getCountryId()));

                    jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
                    jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

                    interaction.setDestination(personContact.getEmail());
                    interaction.setLoanApplicationId(loanApplicationId);
                    interaction.setPersonId(person.getId());

                    interactionService.sendPersonInteraction(interaction, jsonVars, null);
                } catch (Exception ex) {
                    logger.error("Error sending signature email", ex);
                    ErrorServiceImpl.onErrorStatic(ex);
                }
            }
        }).start();
    }

    @Override
    public void sendLoanApplicationApprovalMail(int loanApplicationId, int personId, Locale locale) throws Exception {
        PersonContactInformation personContact = personDao.getPersonContactInformation(locale, personId);
        Person person = personDao.getPerson(catalogService, locale, personId, false);
        List<PersonInteraction> personInteractions = interactionDAO.getPersonInteractionsByLoanApplication(personId, loanApplicationId, locale);

        String loanApplicationToken = generateLoanApplicationToken(person.getUserId(), person.getId(), loanApplicationId, true);
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        LoanOffer loanOffers = loanApplicationDao.getLoanOffers(loanApplicationId).stream().filter(e -> e.getSelected()).findFirst().orElse(null);

        // TODO Put in a threadpool
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonVars = new JSONObject();

                    String baseUrl = null;

                    if (loanApplication.getEntityId() != null) {
                        EntityBranding entityBranding = catalogService.getEntityBranding(loanApplication.getEntityId());

                        if (entityBranding != null) {
                            baseUrl = Configuration.hostEnvIsLocal() ? "http://" : "https://";

                            baseUrl += (entityBranding.getSubdomain() + "." + entityBranding.getDomain());

                            baseUrl += Configuration.hostEnvIsLocal() ? ":8080" : "";
                        }
                    }

                    String linkToRedirect = null;

                    if(loanApplication.getCountry().getId().equals(CountryParam.COUNTRY_PERU)){
                        Map<String,Object> extraParams = new HashMap<>();
                        extraParams.put(LoanApplicationServiceImpl.SHOULD_VALIDATE_EMAIL_KEY, true);
                        linkToRedirect = generateLoanApplicationLinkEntity(loanApplication,extraParams);
                    }

                    if(linkToRedirect == null) linkToRedirect = generateLoanApplicationLinkEntity(loanApplication, loanApplicationToken);

                    jsonVars.put("LINK", linkToRedirect);
                    jsonVars.put("CLIENT_NAME", person.getName().split(" ")[0]);
                    jsonVars.put("CLIENT_NAME", person.getName().split(" ")[0]);
                    jsonVars.put("ENTITY", loanOffers.getEntity().getFullName());
                    jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
                    jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

                    int interactionContentId = InteractionContent.REGISTRO_LOANAPPLICATION_APPROVAL_MAIL;
                    // TODO Esto deberia estar en un campo en el entity product param
                    if (loanOffers.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_TARJETA_PERUANA_PREPAGO) {
                        interactionContentId = InteractionContent.TARJETAs_PERUANAS_PREPAGO_APROBACION_BO;
                    }

                    if (loanOffers.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO) {
                        interactionContentId = InteractionContent.REGISTRO_LOANAPPLICATION_APPROVAL_MAIL_BANK_ACCOUNT;
                    }

                    if (loanOffers.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE) {
                        interactionContentId = InteractionContent.REGISTRO_LOANAPPLICATION_APPROVAL_MAIL_AZTECA_ONLINE;
                    }

                    PersonInteraction interaction = new PersonInteraction();
                    interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                    interaction.setInteractionContent(catalogService.getInteractionContent(interactionContentId, loanApplication.getCountryId()));
                    interaction.setDestination(personContact.getEmail());
                    interaction.setLoanApplicationId(loanApplicationId);
                    interaction.setPersonId(person.getId());

                    if(loanOffers.getEntity().getId() == Entity.AZTECA){
                        interaction.setSenderName(String.format("%s de %s",
                                loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION,
                                loanOffers.getEntity().getShortName()
                        ));
                    }

                    int finalInteractionContentId = interactionContentId;
                    if(!personInteractions.stream().anyMatch(e -> e.getInteractionContent() != null && e.getInteractionContent().getId().intValue() == finalInteractionContentId)) interactionService.sendPersonInteraction(interaction, jsonVars, null);
                } catch (Exception ex) {
                    logger.error("Error sending signature email", ex);
                    ErrorServiceImpl.onErrorStatic(ex);
                }
            }
        }).start();
    }

    @Override
    public void sendLoanApplicationExtranetApprovalMail(int loanApplicationId, int personId, int disbursementType, Locale locale) throws Exception {
        PersonContactInformation personContact = personDao.getPersonContactInformation(locale, personId);
        Person person = personDao.getPerson(catalogService, locale, personId, false);

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        LoanOffer loanOffers = loanApplicationDao.getLoanOffers(loanApplicationId).stream().filter(e -> e.getSelected()).findFirst().orElse(null);

        // TODO Put in a threadpool
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonVars = new JSONObject();
                    jsonVars.put("CLIENT_NAME", person.getName().split(" ")[0]);
                    jsonVars.put("ENTITY", loanOffers.getEntity().getFullName());

                    PersonInteraction interaction = new PersonInteraction();
                    interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                    if (disbursementType == EntityProductParams.DISBURSEMENT_TYPE_RETIREMNT)
                        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.REGISTRO_LOANAPPLICATION_EXT_APPROVAL_MAIL_RETIREMENT, loanApplication.getCountryId()));
                    else if (disbursementType == EntityProductParams.DISBURSEMENT_TYPE_DEPOSIT)
                        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.REGISTRO_LOANAPPLICATION_EXT_APPROVAL_MAIL_DEPOSIT, loanApplication.getCountryId()));

                    jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
                    jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

                    interaction.setDestination(personContact.getEmail());
                    interaction.setLoanApplicationId(loanApplicationId);
                    interaction.setPersonId(person.getId());

                    interactionService.sendPersonInteraction(interaction, jsonVars, null);
                } catch (Exception ex) {
                    logger.error("Error sending signature email", ex);
                    ErrorServiceImpl.onErrorStatic(ex);
                }
            }
        }).start();
    }

    @Override
    public JSONObject getIovationByLoanApplication(Integer id) throws Exception {
        return loanApplicationDao.getIovationByLoanApplication(id);
    }

    @Override
    public void generateOfferTCEA(LoanOffer offer, int loanApplicationId) throws Exception {

        EntityProductParams entityProductParam = catalogService.getEntityProductParamById(offer.getEntityProductParameterId());
        if (entityProductParam.getSolvenGeneratesSchedule()) {

            List<Double> paymentsList = new ArrayList<>();
            List<Date> dateList = new ArrayList<>();

//            if(EntityProductParams.ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD.contains(offer.getEntityProductParameterId())){
//                loanApplicationDao.generateOfferScheduleAccesoPromo(offer.getId());
//                LoanOffer newOffer = loanApplicationDao.getLoanOffersAll(loanApplicationId).stream().filter(o -> o.getId().equals(offer.getId())).findFirst().orElse(null);
//
//                paymentsList.add(((offer.getAmmount()) * -1));
//                dateList.add(DateUtils.truncate(new Date(), Calendar.DATE));
//                double hidenCommission = (offer.getHiddenCommission() != null ? offer.getHiddenCommission() : 0);
//
//                List<OriginalSchedule> orinigalScheduleForTcea = offer.getOfferSchedule().stream().sequential().filter(o -> o.getInstallmentId() != 0).collect(Collectors.toList());
//                List<OriginalSchedule> newScheduleForTcea = newOffer.getOfferSchedule().stream().sequential().filter(o -> o.getInstallmentId() != 0).collect(Collectors.toList());
//
//                double firstPayment = orinigalScheduleForTcea.get(0).getInstallmentAmount() + hidenCommission + (offer.getCost() != null ? offer.getCost() : 0);
//                for (int i = 0; i < newScheduleForTcea.size(); i++) {
//                    if (i <= 2) {
//                        paymentsList.add(firstPayment / 2.0);
//                    } else {
//                        paymentsList.add(orinigalScheduleForTcea.get(i - 2).getInstallmentAmount() + hidenCommission);
//                    }
//                    dateList.add(newScheduleForTcea.get(i).getBillingDate());
//                }
//            }else{
                paymentsList.add(((offer.getAmmount()) * -1));
                dateList.add(DateUtils.truncate(new Date(), Calendar.DATE));

                double hidenCommission = (offer.getHiddenCommission() != null ? offer.getHiddenCommission() : 0);
                List<OriginalSchedule> scheduleForTcea = offer.getOfferSchedule().stream().sequential().filter(o -> o.getInstallmentId() != 0).collect(Collectors.toList());
                for (int i = 0; i < scheduleForTcea.size(); i++) {
                    double installmentAmount = scheduleForTcea.get(i).getInstallmentAmount();
                    if(offer.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_FINANSOL_CONSUMO || offer.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_FINANSOL_CONSUMO_BASE){
                        installmentAmount = scheduleForTcea.get(i).getInstallmentAmount() - scheduleForTcea.get(i).getCollectionCommission();
                    } else if(offer.getEntityId() == Entity.PRISMA){
                        installmentAmount = scheduleForTcea.get(i).getInstallmentAmount() - scheduleForTcea.get(i).getCollectionCommission() - hidenCommission;
                    }
                    if (i == 0) {
                        paymentsList.add(installmentAmount + hidenCommission + (offer.getCost() != null ? offer.getCost() : 0));
                        dateList.add(scheduleForTcea.get(i).getBillingDate());
                    } else {
                        paymentsList.add(installmentAmount + hidenCommission);
                        dateList.add(scheduleForTcea.get(i).getBillingDate());
                    }
                }

                // When Azteca, the first date is the first payment date - 1 month, also the days beetween dates should be 30
                if(offer.getEntity().getId() == Entity.PRISMA/* || offer.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA || offer.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE*/){
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateList.get(1));
                    cal.add(Calendar.MONTH, -1);
                    dateList.set(0, cal.getTime());

                    for (int i = 1; i <= offer.getInstallments(); i++) {
                        cal.setTime(dateList.get(i - 1));
                        cal.add(Calendar.DATE, 30);
                        dateList.set(i, cal.getTime());
                    }
                }
//            }


            // Exclusive validation for salary advance
            // If the first date is the same as the last, make the first date with time in the las second of the day
            if (offer.getProduct().getId() == Product.SALARY_ADVANCE) {
                Date firstInArray = dateList.get(0);
                Date lastInArray = dateList.get(dateList.size() - 1);
                if (!firstInArray.before(lastInArray) && !firstInArray.after(lastInArray)) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(firstInArray);
                    c.add(Calendar.DATE, 1);
                    c.add(Calendar.SECOND, -1);
                    dateList.set(0, c.getTime());
                }
            }

            Double[] payments = paymentsList.toArray(new Double[paymentsList.size()]);
            Date[] dates = dateList.toArray(new Date[dateList.size()]);

            // If the entity needs a custom tcea, calculate it
            double tcea = XirrDate.Newtons_method(0.1, payments, dates, offer.getEntity().getAnnualizaXirr());
            double customEntityTcea = tcea;
            if (offer.getEntity().getCustomEffectiveAnnualCostRate() != null && offer.getEntity().getCustomEffectiveAnnualCostRate()) {
                switch (offer.getEntity().getId()) {
                    case Entity.RIPLEY:
                        if (!offer.getEntityProductParameterId().equals(EntityProductParams.ENT_PROD_PARAM_RIPLEY))
                            break;
                        customEntityTcea = (Math.pow(1 + Irr.irr(ArrayUtils.toPrimitive(payments)), 12) - 1) * 100.0;
                        break;
                    case Entity.COMPARTAMOS:
                        customEntityTcea = (Math.pow(1 + Irr.irr(ArrayUtils.toPrimitive(payments)), 12) - 1) * 100.0;
                        break;
                }
            }

            // If its azteca consumo, the tcea should be equal to the tea
//            if(Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_ONLINE, EntityProductParams.ENT_PROD_PARAM_AZTECA).contains(offer.getEntityProductParameterId())){
//                tcea = offer.getEffectiveAnualRate();
//                customEntityTcea = offer.getEffectiveAnualRate();
//            }

            if(!EntityProductParams.ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD.contains(offer.getEntityProductParameterId())){
                if (offer.getEffectiveAnualRate() != null && customEntityTcea < offer.getEffectiveAnualRate()) {
                    sendBadTceaAlertEmail(null, offer.getId(), customEntityTcea, offer.getEffectiveAnualRate());
                    customEntityTcea = offer.getEffectiveAnualRate();
                    tcea = offer.getEffectiveAnualRate();
                }
            }

            offer.setEffectiveAnnualCostRate(customEntityTcea);
            loanApplicationDao.updateTCEA(offer.getId(), customEntityTcea);
            loanApplicationDao.updateSolvenTCEA(offer.getId(), tcea);

            // If AccesoLD, update the TEA also
//            if(EntityProductParams.ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD.contains(offer.getEntityProductParameterId())){
//                loanApplicationDao.updateTEA(offer.getId(), tcea);
//                loanApplicationDao.generateOfferScheduleAccesoPromo(offer.getId());
//                loanApplicationDao.updateOfferInstallments(offer.getId(), offer.getInstallments() + 2);
//            }
        }
    }

    @Override
    public Double generateOfferTCEAWithoutIva(LoanOffer offer) throws Exception {
        EntityProductParams entityProductParam = catalogService.getEntityProductParamById(offer.getEntityProductParameterId());

        if (entityProductParam.getSolvenGeneratesSchedule()) {
            List<Double> paymentsList = new ArrayList<>();
            List<Date> dateList = new ArrayList<>();

            if(offer.getStampTax() != null)
                paymentsList.add(((offer.getAmmount() - offer.getStampTax()) * -1));
            else
                paymentsList.add(((offer.getAmmount()) * -1));

            dateList.add(DateUtils.truncate(new Date(), Calendar.DATE));

            double hidenCommission = (offer.getHiddenCommission() != null ? offer.getHiddenCommission() : 0);
            List<OriginalSchedule> scheduleForTcea = offer.getOfferSchedule().stream().sequential().filter(o -> o.getInstallmentId() != 0).collect(Collectors.toList());
            for (int i = 0; i < scheduleForTcea.size(); i++) {
                if (i == 0) {
                    paymentsList.add(scheduleForTcea.get(i).getInstallmentAmount() - scheduleForTcea.get(i).getInterestTax() + hidenCommission + (offer.getCost() != null ? offer.getCost() : 0));
                    dateList.add(scheduleForTcea.get(i).getBillingDate());
                } else {
                    paymentsList.add(scheduleForTcea.get(i).getInstallmentAmount() - scheduleForTcea.get(i).getInterestTax() + hidenCommission);
                    dateList.add(scheduleForTcea.get(i).getBillingDate());
                }
            }

            // Exclusive validation for salary advance
            // If the first date is the same as the last, make the first date with time in the las second of the day
            if (offer.getProduct().getId() == Product.SALARY_ADVANCE) {
                Date firstInArray = dateList.get(0);
                Date lastInArray = dateList.get(dateList.size() - 1);
                if (!firstInArray.before(lastInArray) && !firstInArray.after(lastInArray)) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(firstInArray);
                    c.add(Calendar.DATE, 1);
                    c.add(Calendar.SECOND, -1);
                    dateList.set(0, c.getTime());
                }
            }

            Double[] payments = paymentsList.toArray(new Double[paymentsList.size()]);
            Date[] dates = dateList.toArray(new Date[dateList.size()]);

            // If the entity needs a custom tcea, calculate it
            double tcea = XirrDate.Newtons_method(0.1, payments, dates, offer.getEntity().getAnnualizaXirr());
            double customEntityTcea = tcea;
            if (offer.getEntity().getCustomEffectiveAnnualCostRate() != null && offer.getEntity().getCustomEffectiveAnnualCostRate()) {
                switch (offer.getEntity().getId()) {
                    case Entity.RIPLEY:
                        if (!offer.getEntityProductParameterId().equals(EntityProductParams.ENT_PROD_PARAM_RIPLEY))
                            break;
                        customEntityTcea = (Math.pow(1 + Irr.irr(ArrayUtils.toPrimitive(payments)), 12) - 1) * 100.0;
                        break;
                    case Entity.COMPARTAMOS:
                        customEntityTcea = (Math.pow(1 + Irr.irr(ArrayUtils.toPrimitive(payments)), 12) - 1) * 100.0;
                        break;
                }
            }

            return customEntityTcea;
        }

        return null;
    }

    @Override
    public void updateVehicle(int loanApplicationId, int veihcleId) {
        loanApplicationDao.updateVehicleId(loanApplicationId, veihcleId);
        loanApplicationDao.updateAmount(loanApplicationId, catalogService.getVehicle(veihcleId, Configuration.getDefaultLocale()).getPrice());
    }

    @Override
    public void registerEvaluationRejection(LoanApplication loanApplication, int entiyToRejectId, Locale locale) throws Exception {
        List<LoanApplicationEvaluation> evaluations = loanApplicationDao.getEvaluations(loanApplication.getId(), locale);
        LoanApplicationEvaluation entityEvaluation = evaluations.stream()
                .filter(e -> e.getEntityId() == entiyToRejectId)
                .findFirst().orElse(null);

        if (entityEvaluation == null)
            throw new Exception("There is no entity evaluation to reject");
        Calendar expirationDate = Calendar.getInstance();
        expirationDate.add(Calendar.DATE, 15);
        loanApplicationDao.updateEvaluationStep(entityEvaluation.getId(), 54, expirationDate.getTime());
        loanApplicationDao.updateCurrentQuestion(loanApplication.getId(), ProcessQuestion.Question.DISAPPROVE_EVALUATION.getId());
    }

    @Override
    public LoanApplication getLoanApplicationById(int loanApplicationId) throws Exception {
        return loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
    }

    @Override
    public boolean isConsolidationLoanApplication(int loanApplicationId) throws Exception {
        List<ConsolidableDebt> consolidableDebts = loanApplicationDao.getConsolidationAccounts(loanApplicationId);
        return consolidableDebts != null && consolidableDebts.stream().anyMatch(c -> c.isSelected());
    }

    @Override
    public void sendSchedulePhysicalSignatureInteraction(int userId, int loanApplicationId, Locale locale) throws Exception {
        //Send mail to CLient
        User user = userDao.getUser(userId);
        Person person = personDao.getPerson(catalogService, locale, user.getPersonId(), false);
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), locale, false, Credit.class);

        PersonInteraction interaction = new PersonInteraction();
        interaction.setPersonId(user.getPersonId());
        interaction.setCreditId(loanApplication.getCreditId());
        interaction.setCreditCode(credit.getCode());
        interaction.setLoanApplicationId(loanApplication.getId());
        interaction.setDestination(user.getEmail());
        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.SCHEDULE_PHYSICAL_SIGNATURE_MAIL, loanApplication.getCountryId()));

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("CLIENT_NAME", person.getFirstName());
        jsonParams.put("ENTITY", credit.getEntity().getShortName());
        jsonParams.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        jsonParams.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);
        jsonParams.put("LINK", generateLoanApplicationLinkEntity(loanApplication));
        interactionService.sendPersonInteraction(interaction, jsonParams, null);

        // Send SMS
        interaction = new PersonInteraction();
        interaction.setPersonId(user.getPersonId());
        interaction.setCreditId(loanApplication.getCreditId());
        interaction.setCreditCode(credit.getCode());
        interaction.setLoanApplicationId(loanApplication.getId());
        interaction.setDestination(user.getPhoneNumber());
        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.SMS));
        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.SCHEDULE_PHYSICAL_SIGNATURE_SMS, loanApplication.getCountryId()));
        interactionService.sendPersonInteraction(interaction, jsonParams, null);
    }

    @Override
    public void sendMissingDocumentation(int userId, int loanApplicationId, Locale locale) throws Exception {
        try {
            User user = userDao.getUser(userId);
            Person person = personDao.getPerson(catalogService, locale, user.getPersonId(), false);
            LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);

            List<UserFile> userFiles = loanApplicationDao.getLoanApplicationUserFiles(loanApplication.getId());

            HashSet<Integer> userFilesId = new HashSet<>();
            if (userFiles != null) {
                for (UserFile userFile : userFiles) {
                    userFilesId.add(userFile.getFileType().getId());
                }
            }

            StringBuilder documents = new StringBuilder("");
            int index = 1;

            List<Pair<Integer, Boolean>> pendingDocuments =
                    getRequiredDocumentsByLoanApplication(loanApplication).stream()
                            .filter(p -> !userFilesId.contains(p.getLeft())).collect(Collectors.toList());
            for (Pair<Integer, Boolean> pair : pendingDocuments) {
                UserFileType userFileType = catalogService.getUserFileType(pair.getLeft());
                documents.append("<li><b>");
                documents.append(String.valueOf(index));
                documents.append(".</b> ");
                documents.append(userFileType.getType());
                documents.append("</li>");
                index++;
            }

            PersonInteraction interaction = new PersonInteraction();
            interaction.setPersonId(user.getPersonId());
            interaction.setLoanApplicationId(loanApplication.getId());
            interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
            interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.MISSING_DOCUMENTATION, loanApplication.getCountryId()));
            interaction.setDestination(user.getEmail());

            JSONObject jsonParams = new JSONObject();

            jsonParams.put("CLIENT_SUBJECT_NAME", (person.getFirstName() == null || person.getFirstName().equals("")) ? "Hola" : person.getFirstName());
            jsonParams.put("CLIENT_NAME", (person.getFirstName() == null || person.getFirstName().equals("")) ? "" : person.getFirstName());
            jsonParams.put("LINK", generateLoanApplicationLinkEntity(loanApplication));
            jsonParams.put("DOCUMENTS", documents.toString());
            jsonParams.put("AGENT_IMAGE_URL", loanApplication.getAgent().getAvatarUrl2() != null ? loanApplication.getAgent().getAvatarUrl2() : loanApplication.getAgent().getAvatarUrl());
            jsonParams.put("AGENT_FULLNAME", loanApplication.getAgent().getName());

            interactionService.sendPersonInteraction(interaction, jsonParams, null);
        } catch (Exception ex) {
            errorService.onError(ex);
        }
    }

    @Override
    public String generateLoanApplicationLinkEntity(LoanApplication loanApplication) throws Exception {
        return generateLoanApplicationLinkEntity(loanApplication, generateLoanApplicationToken(loanApplication.getUserId(), loanApplication.getPersonId(), loanApplication.getId()));
    }

    @Override
    public String generateLoanApplicationLinkEntity(LoanApplication loanApplication,Map<String, Object> extraParams) throws Exception {
        return generateLoanApplicationLinkEntity(loanApplication, generateLoanApplicationToken(loanApplication.getUserId(), loanApplication.getPersonId(), loanApplication.getId(),extraParams));
    }

    @Override
    public String generateLoanApplicationLinkEntity(LoanApplication loanApplication, String loanApplicationToken) throws Exception {
        String baseUrl = null;
        if (loanApplication.getEntityId() != null) {
            String entitySubDomain = catalogService.getEntityBranding(loanApplication.getEntityId()).getSubdomain();

            if (Configuration.hostEnvIsLocal()) {
                String countryDomain = loanApplication.getCountry().getDomains().get(0);
                baseUrl = "http://" + entitySubDomain + "." + countryDomain + ":8080/";
            } else if (Configuration.hostEnvIsProduction()) {
                String countryDomain = loanApplication.getCountry().getDomains().get(0);
                baseUrl = "https://" + entitySubDomain + "." + countryDomain + "/";
            } else {
                String countryDomain = loanApplication.getCountry().getDomains().get(0);
                baseUrl = "https://" + entitySubDomain + "." + countryDomain + "/";
            }
        } else {
            if (loanApplication.getProduct() != null && loanApplication.getProduct().getId() == Product.LEADS) {
                if (loanApplication.getProduct().getCountryDomains() != null && !loanApplication.getProduct().getCountryDomains().isEmpty()) {
                    ProductCountryDomain productCountryDomain = loanApplication.getProduct().getCountryDomains().stream().filter(c -> c.getCountryId().equals(loanApplication.getCountryId())).findFirst().orElse(null);
                    if (productCountryDomain != null && !productCountryDomain.getDomains().isEmpty()) {
                        if (Configuration.hostEnvIsLocal()) {
                            baseUrl = "http://" + productCountryDomain.getDomains().get(0) + ":8080/";
                        } else {
                            baseUrl = "https://" + productCountryDomain.getDomains().get(0) + "/";
                        }
                    }
                }
            }

            if (baseUrl == null) {
                String countryDomain = loanApplication.getCountry().getDomains().get(0);
                if (Configuration.hostEnvIsLocal()) {
                    baseUrl = "http://" + countryDomain + ":8080/";
                } else if(!Configuration.hostEnvIsProduction()) {
                    baseUrl = "https://" + Configuration.getEnvironmmentName() + "." + countryDomain + "/";
                } else {
                    baseUrl = "https://" + countryDomain + "/";
                }
            }
        }

        return baseUrl +
                ProductCategory.GET_URL_BY_ID(loanApplication.getProductCategoryId()) + "/" +
                Configuration.EVALUATION_CONTROLLER_URL + "/" + loanApplicationToken;
    }


    @Override
    public String generateLoanApplicationLinkEntityMailing(LoanApplication loanApplication, String additionalData) throws Exception {
        String baseUrl = null;
        if (loanApplication.getEntityId() != null) {
            String entitySubDomain = catalogService.getEntityBranding(loanApplication.getEntityId()).getSubdomain();

            if (Configuration.hostEnvIsLocal()) {
                String countryDomain = loanApplication.getCountry().getDomains().get(0);
                baseUrl = "http://" + entitySubDomain + "." + countryDomain + ":8080/";
            } else if (Configuration.hostEnvIsProduction()) {
                String countryDomain = loanApplication.getCountry().getDomains().get(0);
                baseUrl = "http://" + entitySubDomain + "." + countryDomain + "/";
            } else {
                String countryDomain = loanApplication.getCountry().getDomains().get(0);
                baseUrl = "https://" + entitySubDomain + "." + countryDomain + "/";
            }
        } else {
            if (loanApplication.getProduct() != null && loanApplication.getProduct().getId() == Product.LEADS) {
                if (loanApplication.getProduct().getCountryDomains() != null && !loanApplication.getProduct().getCountryDomains().isEmpty()) {
                    ProductCountryDomain productCountryDomain = loanApplication.getProduct().getCountryDomains().stream().filter(c -> c.getCountryId().equals(loanApplication.getCountryId())).findFirst().orElse(null);
                    if (productCountryDomain != null && !productCountryDomain.getDomains().isEmpty()) {
                        if (Configuration.hostEnvIsLocal()) {
                            baseUrl = "http://" + productCountryDomain.getDomains().get(0) + ":8080/";
                        } else {
                            baseUrl = "https://" + productCountryDomain.getDomains().get(0) + "/";
                        }
                    }
                }
            }

            if (baseUrl == null) {
                String countryDomain = loanApplication.getCountry().getDomains().get(0);
                if (Configuration.hostEnvIsLocal()) {
                    baseUrl = "http://" + countryDomain + ":8080/";
                } else if(!Configuration.hostEnvIsProduction()) {
                    baseUrl = "https://" + Configuration.getEnvironmmentName() + "." + countryDomain + "/";
                } else {
                    baseUrl = "https://" + countryDomain + "/";
                }
            }
        }

        return baseUrl +
                ProductCategory.GET_URL_BY_ID(loanApplication.getProductCategoryId()) + additionalData;
    }

    @Override
    public void sendVehicleDisbursedInteraction(int userId, int loanApplicationId, Locale locale) throws Exception {
        //Send mail to CLient
        User user = userDao.getUser(userId);
        Person person = personDao.getPerson(catalogService, locale, user.getPersonId(), false);
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), locale, false, Credit.class);

        PersonInteraction interaction = new PersonInteraction();
        interaction.setPersonId(user.getPersonId());
        interaction.setCreditId(loanApplication.getCreditId());
        interaction.setCreditCode(credit.getCode());
        interaction.setLoanApplicationId(loanApplication.getId());
        interaction.setDestination(user.getEmail());
        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.VEHICLE_DISBURSED_MAIL, loanApplication.getCountryId()));

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("CLIENT_NAME", person.getFirstName());
        jsonParams.put("ENTITY", credit.getEntity().getFullName());
        jsonParams.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        jsonParams.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);
        if (credit.getEntity().getId() == Entity.ACCESO) {
            if (credit.getVehicle().getVehicleDealership().getContactInformationImageUrls() != null) {
                String dealershipContactImages = "";
                for (String url : credit.getVehicle().getVehicleDealership().getContactInformationImageUrls()) {
                    dealershipContactImages = dealershipContactImages + "<img style=\"width: 48%;\" src=\"" + url + "\">";
                }
                jsonParams.put("CONTACT_INFORMATION", dealershipContactImages);
            }
        }
        interactionService.sendPersonInteraction(interaction, jsonParams, null);

        // Send SMS
        interaction = new PersonInteraction();
        interaction.setPersonId(user.getPersonId());
        interaction.setCreditId(loanApplication.getCreditId());
        interaction.setCreditCode(credit.getCode());
        interaction.setLoanApplicationId(loanApplication.getId());
        interaction.setDestination(user.getPhoneNumber());
        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.SMS));
        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.VEHICLE_DISBURSED_SMS, loanApplication.getCountryId()));
        interactionService.sendPersonInteraction(interaction, jsonParams, null);
    }

//    @Override
//    public boolean runPreEvaluationBotsArgentinaIfNotYet(LoanApplication loanApplication, LoanApplicationEvaluationsProcess evaluationsProcess) throws Exception {
//
//        // If the bots are alredy running, just return and continue
//        if (evaluationsProcess.getPreEvaluationBots() != null && !evaluationsProcess.getPreEvaluationBots().isEmpty()) {
//            for (Integer queryBotId : evaluationsProcess.getPreEvaluationBots()) {
//                QueryBot queryBot = botDao.getQueryBot(queryBotId);
//                if (queryBot != null && Arrays.asList(Bot.BCRA, Bot.ANSES, Bot.AFIP).contains(queryBot.getBotId()))
//                    return false;
//            }
//        }
//
//        // Run bots
//        webscrapperService.setCountry(loanApplication.getCountry());
//        Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
//        QueryBot BCRAQueryBot = webscrapperService.callBCRABot(person.getDocumentType().getId(), person.getDocumentNumber(), person.getUserId());
//        QueryBot AFIPQueryBot = webscrapperService.callAFIPBot(person.getDocumentType().getId(), person.getDocumentNumber(), person.getUserId());
//        QueryBot ANSESQueryBot = webscrapperService.callANSESBot(person.getDocumentType().getId(), person.getDocumentNumber(), person.getUserId());
//
//        evaluationsProcess.addPreEvaluationQueryBot(BCRAQueryBot.getId());
//        evaluationsProcess.addPreEvaluationQueryBot(AFIPQueryBot.getId());
//        evaluationsProcess.addPreEvaluationQueryBot(ANSESQueryBot.getId());
//
//        loanApplicationDao.updatePreEvaluationQueryBots(loanApplication.getId(), evaluationsProcess.getPreEvaluationBots());
//
//        return true;
//    }

    @Override
    public void sendVehiclePartialDownPayment(int userId, int loanApplicationId, Locale locale) throws Exception {
        User user = userDao.getUser(userId);
        Person person = personDao.getPerson(catalogService, locale, user.getPersonId(), false);
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), locale, false, Credit.class);

        PersonInteraction interaction = new PersonInteraction();
        interaction.setPersonId(user.getPersonId());
        interaction.setCreditId(loanApplication.getCreditId());
        interaction.setCreditCode(credit.getCode());
        interaction.setLoanApplicationId(loanApplication.getId());
        interaction.setDestination(user.getEmail());
        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.VEHICLE_PARTIAL_DOWNPAYMENT, loanApplication.getCountryId()));

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("CLIENT_NAME", person.getFirstName());
        jsonParams.put("ENTITY", credit.getEntity().getFullName());
        jsonParams.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        jsonParams.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);
        jsonParams.put("DOCUMENT_NUMBER", credit.getEntityCreditCode() != null ? credit.getEntityCreditCode() : "");
        jsonParams.put("DOWN_PAYMENT", credit.getDownPayment().toString());
        jsonParams.put("CAR_DEALERSHIP", credit.getVehicle().getVehicleDealership().getName());
        jsonParams.put("CAR_DEALERSHIP_RUC", credit.getVehicle().getVehicleDealership().getRuc());
        if (credit.getVehicle().getVehicleDealership().getBankAccountList() != null) {
            String dealershipAccounts = "";
            for (BankAccount account : credit.getVehicle().getVehicleDealership().getBankAccountList()) {
                dealershipAccounts = dealershipAccounts + "<b>" + account.getBank().getName() + "</b><br/>";
                if (account.getPaymentType() != null)
                    dealershipAccounts = dealershipAccounts + (account.getPaymentType() == BankAccount.PAYMENT_TYPE_INTERNET ? "<u>Pagos por internet</u>" : "<u>Pagos por ventanilla</u>") + "<br/>";
                dealershipAccounts = dealershipAccounts + (account.getAccountType() == BankAccount.ACCOUNT_TYPE_CORRIENTE ? "N° de Cta. Corriente " : "N° de convenio de recaudo ") +
                        (account.getCurrencyId() == Currency.USD ? "Dólares" : "Soles") + " " + account.getAccountNumber() + "<br/>";
                if (account.getCciCode() != null)
                    dealershipAccounts = dealershipAccounts + "CCI " + account.getCciCode() + "<br/><br/>";
            }
            jsonParams.put("CAR_DEALERSHIP_ACCOUNTS", dealershipAccounts);
        }
        interactionService.sendPersonInteraction(interaction, jsonParams, null);

        // Send SMS
        interaction = new PersonInteraction();
        interaction.setPersonId(user.getPersonId());
        interaction.setCreditId(loanApplication.getCreditId());
        interaction.setCreditCode(credit.getCode());
        interaction.setLoanApplicationId(loanApplication.getId());
        interaction.setDestination(user.getPhoneNumber());
        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.SMS));
        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.REGISTRO_LOANAPPLICATION_SIGNATURE_SMS_VEHICLE, loanApplication.getCountryId()));
        interactionService.sendPersonInteraction(interaction, jsonParams, null);
    }

    public void sendVehicleTotalDownPayment(int userId, int loanApplicationId, Locale locale) throws Exception {
        User user = userDao.getUser(userId);
        Person person = personDao.getPerson(catalogService, locale, user.getPersonId(), false);
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        Credit credit = creditDao.getCreditByID(loanApplication.getCreditId(), locale, false, Credit.class);
        String loanApplicationToken = generateLoanApplicationToken(person.getUserId(), person.getId(), loanApplicationId, true);

        PersonInteraction interaction = new PersonInteraction();
        interaction.setPersonId(user.getPersonId());
        interaction.setCreditId(loanApplication.getCreditId());
        interaction.setCreditCode(credit.getCode());
        interaction.setLoanApplicationId(loanApplication.getId());
        interaction.setDestination(user.getEmail());
        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.VEHICLE_TOTAL_DOWNPAYMENT, loanApplication.getCountryId()));

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("CLIENT_NAME", person.getFirstName());
        jsonParams.put("ENTITY", credit.getEntity().getFullName());
        jsonParams.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
        jsonParams.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);
        jsonParams.put("DOCUMENT_NUMBER", credit.getEntityCreditCode() != null ? credit.getEntityCreditCode() : "");
        jsonParams.put("DOWN_PAYMENT", credit.getDownPayment().toString());
        jsonParams.put("CONFIRMATION_LINK", generateLoanApplicationLinkEntity(loanApplication, loanApplicationToken));
        interactionService.sendPersonInteraction(interaction, jsonParams, null);

        // Send SMS
        interaction = new PersonInteraction();
        interaction.setPersonId(user.getPersonId());
        interaction.setCreditId(loanApplication.getCreditId());
        interaction.setCreditCode(credit.getCode());
        interaction.setLoanApplicationId(loanApplication.getId());
        interaction.setDestination(user.getPhoneNumber());
        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.SMS));
        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.REGISTRO_LOANAPPLICATION_SIGNATURE_SMS_VEHICLE, loanApplication.getCountryId()));
        interactionService.sendPersonInteraction(interaction, jsonParams, null);
    }

    @Override
    public void sendBackendGeneratedPreEvaluationEmail(LoanApplication loanApplication) throws Exception {

        LoanApplicationPreliminaryEvaluation loanPreEvaluation = getLastPreliminaryEvaluation(loanApplication.getId(), Configuration.getDefaultLocale(), null);

        Integer interactionContentId;
        String link;

        if (loanPreEvaluation != null && loanPreEvaluation.getApproved()) {
            interactionContentId = InteractionContent.AFFILIATOR_APPROVED;
            link = generateLoanApplicationLinkEntity(loanApplication) + "?showWelcome=true";
        } else {
            interactionContentId = InteractionContent.AFFILIATOR_FAILED;
            link = "";
        }

        Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
        User user = userDao.getUser(person.getUserId());

        try {
            JSONObject jsonVars = new JSONObject();
            jsonVars.put("CLIENT_SUBJECT_NAME", (person.getFirstName() == null || person.getFirstName().equals("")) ? "Hola" : person.getFirstName());
            jsonVars.put("CLIENT_NAME", (person.getFirstName() == null || person.getFirstName().equals("")) ? "" : person.getFirstName());
            jsonVars.put("LINK", link);
            jsonVars.put("AGENT_IMAGE_URL", Configuration.AGENT_IMAGE_URL_COLLECTION);
            jsonVars.put("AGENT_FULLNAME", Configuration.AGENT_FULLNAME_COLLECTION);
            if (loanPreEvaluation != null) {
                jsonVars.put("REJECTION_REASON", loanPreEvaluation.getHardFilterMessage());
            }
            PersonInteraction interaction = new PersonInteraction();
            interaction.setPersonId(person.getId());
            interaction.setLoanApplicationId(loanApplication.getId());
            interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
            interaction.setInteractionContent(catalogService.getInteractionContent(interactionContentId, loanApplication.getCountryId()));
            interaction.setDestination(user.getEmail());
            interactionService.sendPersonInteraction(interaction, jsonVars, null);
        } catch (Exception ex) {
            errorService.onError(ex);
        }
    }

    @Override
    public List<Integer> getBureausPendingToRun(LoanApplication loanApplication, List<LoanApplicationEvaluation> evaluations) throws Exception {
        List<Integer> bureausToRun = new ArrayList<>();
        if (evaluations != null) {
            List<LoanApplicationEvaluation> evaluationList = evaluations.stream().filter(e -> e.getApproved()).collect(Collectors.toList());
            if (evaluationList != null) {
                List<ApplicationBureau> bureausThatHasRun = loanApplicationDao.getBureauResults(loanApplication.getId());
                for (LoanApplicationEvaluation evaluation : evaluationList) {
                    EntityProductParams params = catalogService.getEntityProductParamById(evaluation.getEntityProductParameterId());
                    if (params.getEntity().getBureaus() != null && !params.getEntity().getBureaus().isEmpty() && params.mustRunBureau()) {
                        for(Bureau bureau : params.getEntity().getBureaus()){
                            if(!bureausToRun.contains(bureau.getId()) && bureausThatHasRun.stream().noneMatch(b -> b.getBureauId() != null && b.getBureauId().equals(bureau.getId()))){
                                bureausToRun.add(bureau.getId());
                            }
                        }
                    }
                }
            }

        }
        return bureausToRun;
    }

//    public List<UserFileType> getRequiredUserFileTypes(LoanApplication loanApplication, Locale locale) throws Exception{
//
//        List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(locale, loanApplication.getPersonId());
//        PersonOcupationalInformation principalOcupation = null;
//        if(ocupations != null)
//            principalOcupation = ocupations.stream()
//                    .filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL)
//                    .findFirst().orElse(null);
//
//        List<LoanOffer> loanOffers = loanApplicationDao.getLoanOffers(loanApplication.getId());
//        EntityProductParams entityProductParams = catalogService.getEntityProductParam(loanOffers.get(0).getEntityId(), loanApplication.getProduct().getId());
//        List<UserFile> userFiles = loanApplicationDao.getLoanApplicationUserFiles(loanApplication.getId());
//        PersonContactInformation contactInformation = personDao.getPersonContactInformation(locale, loanApplication.getPersonId());
//
//        HashSet<Integer> userFilesId = new HashSet<>();
//        if(userFiles != null) {
//            for (UserFile userFile : userFiles) {
//                userFilesId.add(userFile.getFileType().getId());
//            }
//        }
//
//        List<UserFileType> requiredFiles = new ArrayList<>();
//        for(Integer i : entityProductParams.getRequiredDocuments()) {
//            if(userFilesId.contains(i))
//                continue;
//            UserFileType userFileType = catalogService.getUserFileType(i);
//            list.add(i);
//            if (loanApplication.getOrigin() != null && loanApplication.getOrigin().equals(LoanApplication.ORIGIN_EXTRANET_ENTITY) && i == UserFileType.SELFIE)
//                continue;
//            JSONObject jsonConfigFile = new JSONObject();
//            if(userFileType == null)
//                continue;
//            jsonConfigFile.put("id", i);
//            jsonConfigFile.put("name", userFileType.getType());
//            jsonConfigFile.put("order", userFileType.getOrder());
//            jsonConfigFile.put("maxFiles", i == UserFileType.DNI_FRONTAL || i == UserFileType.DNI_ANVERSO ? 1 : 5);
//            jsonConfigFile.put("showTooltip", i == UserFileType.BOLETA_PAGO || i == UserFileType.COMPROBANTE_DIRECCION);
//            jsonConfigFile.put("tooltip", messageSource.getMessage(userFileType.getTooltip(), null, locale));
//            jsonConfigFile.put("required", true);
//            String thumbnail = userFileType.getThumbnail();
//            jsonConfigFile.put("thumbnail", thumbnail);
//            jsonConfigFile.put("showImage", thumbnail != null && !thumbnail.equals(""));
//            jsonConfigFiles.put(jsonConfigFile);
//        }
//
//        if (ocupations != null) {
//
//
//            for (Integer i : principalOcupation.getRequiredDocuments()) {
//                if(userFilesId.contains(i))
//                    continue;
//                if(list.contains(i))
//                    continue;
//                if (loanApplication.getOrigin() != null && loanApplication.getOrigin().equals(LoanApplication.ORIGIN_EXTRANET_ENTITY) && i == UserFileType.SELFIE)
//                    continue;
//                JSONObject jsonConfigFile = new JSONObject();
//                UserFileType userFileType = catalogService.getUserFileType(i);
//                if(userFileType == null)
//                    continue;
//                jsonConfigFile.put("id", i);
//                jsonConfigFile.put("name", userFileType.getType());
//                jsonConfigFile.put("order", userFileType.getOrder());
//                jsonConfigFile.put("maxFiles", i == UserFileType.DNI_FRONTAL || i == UserFileType.DNI_ANVERSO ? 1 : 5);
//                jsonConfigFile.put("showTooltip", i == UserFileType.BOLETA_PAGO || i == UserFileType.COMPROBANTE_DIRECCION);
//                jsonConfigFile.put("tooltip", messageSource.getMessage(userFileType.getTooltip(), null, locale));
//                jsonConfigFile.put("required", UserFileType.SELFIE == i);
//                String thumbnail = userFileType.getThumbnail();
//                jsonConfigFile.put("thumbnail", thumbnail);
//                jsonConfigFile.put("showImage", thumbnail != null && !thumbnail.equals(""));
//                jsonConfigFiles.put(jsonConfigFile);
//            }
//
//            if(contactInformation != null && contactInformation.getHousingType() != null){
//                for(Integer i : contactInformation.getHousingType().getRequiredDocumentsByEntity(loanOffers.get(0).getEntityId())){
//                    if(userFilesId.contains(i))
//                        continue;
//                    if(list.contains(i))
//                        continue;
//                    if (loanApplication.getOrigin() != null && loanApplication.getOrigin().equals(LoanApplication.ORIGIN_EXTRANET_ENTITY) && i == UserFileType.SELFIE)
//                        continue;
//                    JSONObject jsonConfigFile = new JSONObject();
//                    UserFileType userFileType = catalogService.getUserFileType(i);
//                    if(userFileType == null)
//                        continue;
//                    jsonConfigFile.put("id", i);
//                    jsonConfigFile.put("name", userFileType.getType());
//                    jsonConfigFile.put("order", userFileType.getOrder());
//                    jsonConfigFile.put("maxFiles", i == UserFileType.DNI_FRONTAL || i == UserFileType.DNI_ANVERSO ? 1 : 5);
//                    jsonConfigFile.put("showTooltip", i == UserFileType.BOLETA_PAGO || i == UserFileType.COMPROBANTE_DIRECCION);
//                    jsonConfigFile.put("tooltip", messageSource.getMessage(userFileType.getTooltip(), null, locale));
//                    jsonConfigFile.put("required", UserFileType.SELFIE == i);
//                    String thumbnail = userFileType.getThumbnail();
//                    jsonConfigFile.put("thumbnail", thumbnail);
//                    jsonConfigFile.put("showImage", thumbnail != null && !thumbnail.equals(""));
//                    jsonConfigFiles.put(jsonConfigFile);
//                }
//            }
//
//            JSONObject jsonMessages = new JSONObject();
//            MessageSource messageSource = SpringUtil.getMEssageSource();
//            jsonMessages.put("selfie", messageSource.getMessage("questions.block.takeSelfie", null, locale));
//            jsonMessages.put("uploadPhoto", messageSource.getMessage("questions.block.uploadPhoto", null, locale));
//            jsonMessages.put("takePhoto", messageSource.getMessage("questions.block.takePhoto", null, locale));
//            jsonMessages.put("uploadDoc", messageSource.getMessage("questions.block.uploadDoc", null, locale));
//
//            model.addAttribute("jsonTakePictureConfig", jsonConfigFiles.toString());
//            model.addAttribute("jsonMessages", jsonMessages.toString());
//        }
//    }

    @Override
    public List<Pair<Integer, Boolean>> getRequiredDocumentsByLoanApplication(LoanApplication loanApplication) throws Exception {
        List<Pair<Integer, Boolean>> result = new ArrayList<>();
        EntityCustomParamConfig entityConfig = loanApplicationDao.getEntityCustomParamsConfig(loanApplication.getId());
        if (entityConfig != null && entityConfig.getFileTypes() != null && !entityConfig.getFileTypes().isEmpty()) {
            for (Integer i : entityConfig.getFileTypes()) {
                if (result.stream().noneMatch(r -> r.getLeft().equals(i)))
                    result.add(Pair.of(i, true));
            }
        } else {
            EntityProductParams entityProductParams = null;
            PersonOcupationalInformation principalOcupation = null;
            PersonContactInformation contactInformation = personDao.getPersonContactInformation(Configuration.getDefaultLocale(), loanApplication.getPersonId());

            List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(Configuration.getDefaultLocale(), loanApplication.getPersonId());
            if (ocupations != null) {
                principalOcupation = ocupations.stream()
                        .filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL)
                        .findFirst().orElse(null);
            }
            List<LoanOffer> loanOffers = loanApplicationDao.getLoanOffers(loanApplication.getId());
            if (loanOffers != null) {
                LoanOffer selectedOffer = loanOffers.stream().filter(o -> o.getSelected() != null && o.getSelected()).findFirst().orElse(null);
                if (selectedOffer != null) {
                    entityProductParams = catalogService.getEntityProductParamById(selectedOffer.getEntityProductParameterId());
                    if (entityProductParams == null) {
                        entityProductParams = catalogService.getEntityProductParam(selectedOffer.getEntityId(), loanApplication.getProduct().getId());
                    }
                }
            }

            // If it's identity validation, get the entityprouductparam from the pre evaluations
            if(loanApplication.getProductCategoryId() == ProductCategory.VALIDACION_IDENTIDAD){
                List<LoanApplicationPreliminaryEvaluation> preevals = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), Configuration.getDefaultLocale());
                Integer entityProductParamId = preevals.stream().filter(p -> p.getStatus() == 'S' && p.getApproved() != null && p.getApproved()).map(p -> p.getEntityProductParameterId()).findFirst().orElse(null);
                if(entityProductParamId != null)
                    entityProductParams = catalogService.getEntityProductParamById(entityProductParamId);
            }

            if (entityConfig != null && entityConfig.getExtrafileTypes() != null && entityConfig.getExtrafileTypes().size() > 0) {
                for (Integer fileType : entityConfig.getExtrafileTypes()) {
                    if (result.stream().noneMatch(r -> r.getLeft().equals(fileType)))
                        result.add(Pair.of(fileType, true));
                }
            }

            if (entityProductParams != null) {
                for (Integer i : entityProductParams.getRequiredDocuments()) {
                    if (result.stream().noneMatch(r -> r.getLeft().equals(i)))
                        result.add(Pair.of(i, true));
                }
            }
            if (principalOcupation != null && principalOcupation.getRequiredDocuments() != null && entityProductParams != null) {
                if (entityProductParams.getEntity().getId().equals(Entity.AFFIRM) || entityProductParams.getEntity().getId().equals(Entity.EFL)) {
                    for (Integer i : principalOcupation.getRequiredDocuments()) {
                        if (result.stream().noneMatch(r -> r.getLeft().equals(i)))
                            result.add(Pair.of(i, UserFileType.SELFIE == i));
                    }
                }
            }
            if (contactInformation != null && contactInformation.getHousingType() != null && entityProductParams != null) {
                for (Integer i : contactInformation.getHousingType().getRequiredDocumentsByEntityProduct(entityProductParams.getEntity().getId(), entityProductParams.getProduct().getId())) {
                    if (result.stream().noneMatch(r -> r.getLeft().equals(i)))
                        result.add(Pair.of(i, UserFileType.SELFIE == i));
                }
            }
        }

        // f the loan app is from extranet entity, the selfie is not obligatory
        boolean isFromExtranetEntity = loanApplication.getOrigin() != null && loanApplication.getOrigin().equals(LoanApplication.ORIGIN_EXTRANET_ENTITY);
        if (isFromExtranetEntity) {
            for (int i = 0; i < result.size(); i++) {
                Pair<Integer, Boolean> pair = result.get(i);
                if (pair.getLeft().equals(UserFileType.SELFIE)) {
                    result.set(i, Pair.of(pair.getLeft(), false));
                }
            }
        }

        return result;
    }

    @Override
    public List<Integer> getRequiredDocumentsIdsByLoanApplication(LoanApplication loanApplication) throws Exception {
        return getRequiredDocumentsByLoanApplication(loanApplication).stream().mapToInt(r -> r.getLeft()).boxed().collect(Collectors.toList());
    }

    @Override
    public List<ProcessQuestionSequence> getQuestionSequenceWithoutBackwards(List<ProcessQuestionSequence> questionSequence) {
        List<ProcessQuestionSequence> simplifiedSequence = new ArrayList<>();
        List<ProcessQuestionSequence> realSequence = questionSequence;

        for (ProcessQuestionSequence sequence : realSequence) {
            if (sequence.getType() == ProcessQuestionSequence.TYPE_FORWARD || sequence.getType() == ProcessQuestionSequence.TYPE_SKIPPED) {
                simplifiedSequence.add(sequence);
            } else if (sequence.getType() == ProcessQuestionSequence.TYPE_BACKWARD) {
                simplifiedSequence.remove(simplifiedSequence.size() - 1);
            }
        }

        return simplifiedSequence;
    }

    @Override
    public EntityProductEvaluationsProcess getEntityProductEvaluationProcess(int loanApplicationId, int entityId, int productId) throws Exception {
        return loanApplicationDao.getEntityProductEvaluationProceses(loanApplicationId)
                .stream()
                .filter(e -> e.getEntityId().equals(entityId) && e.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    private Cell createCell(Row row, int column, CellStyle style) {
        Cell cell = row.createCell(column);
        if (style != null)
            cell.setCellStyle(style);
        return cell;
    }

    @Override
    public byte[] generateLoanRequestSheet(Credit credit) throws Exception {
        byte[] spreadsheet = null;
        int loanProviderId = credit.getEntity().getId();

        if (loanProviderId == Entity.RIPLEY) {
            spreadsheet = documentService.generateRipleyLoanSpreadSheet(credit);
            logger.info("Generating Ripley spreadsheeet");
        }
        return spreadsheet;
    }

    private void createEmailPassword(String email, Integer userId) throws Exception {
        User user = userDao.getUser(userId);
        if (user.getEmail() != null && !user.getEmail().equalsIgnoreCase(email)) {
            throw new SqlErrorMessageException(null, "El  email no coincide con el registrado");
        } else if (user.getEmail() == null) {
            int emailId = userDao.registerEmailChange(userId, email.toLowerCase());
            userDao.validateEmailChange(userId, emailId);
        }
    }

    private void applyExternalParams(String externalParams, int loanApplicationId) throws Exception {
        if (externalParams != null && !externalParams.trim().isEmpty()) {
            JSONObject jsonExternalParams = new JSONObject(CryptoUtil.decrypt(externalParams));
            if (JsonUtil.getIntFromJson(jsonExternalParams, "forcedEntity", null) != null &&
                    JsonUtil.getIntFromJson(jsonExternalParams, "forcedProduct", null) != null) {
                loanApplicationDao.updateEntityId(loanApplicationId, JsonUtil.getIntFromJson(jsonExternalParams, "forcedEntity", null));
                loanApplicationDao.updateProductId(loanApplicationId, JsonUtil.getIntFromJson(jsonExternalParams, "forcedProduct", null));
            }
            if (JsonUtil.getIntFromJson(jsonExternalParams, "vehicleId", null) != null) {
                updateVehicle(loanApplicationId, JsonUtil.getIntFromJson(jsonExternalParams, "vehicleId", null));
            }
        }
    }

    @Override
    public List<String> getUtmSources() {
        HashMap<String, HashMap<String, List>> utmParameters = loanApplicationDao.getUtmParameters();
        if (utmParameters == null)
            return null;

        List<String> sources = new ArrayList<>();
        for (String key : utmParameters.keySet()) {
            sources.add(key);
        }

        return sources;
    }

    @Override
    public List<String> getUtmMediumsBySource(String source) {
        HashMap<String, HashMap<String, List>> utmParameters = loanApplicationDao.getUtmParameters();
        if (utmParameters == null)
            return null;

        List<String> mediums = new ArrayList<>();
        utmParameters.entrySet().stream().filter(u -> u.getKey().equals(source)).map(u -> u.getValue().keySet()).forEach(u -> mediums.addAll(u));
        return mediums;
    }

    @Override
    public List<String> getUtmCampaignsByMedium(String source, String medium) {
        HashMap<String, HashMap<String, List>> utmParameters = loanApplicationDao.getUtmParameters();
        if (utmParameters == null)
            return null;

        List<String> campaigns = new ArrayList<>();
        utmParameters.entrySet().stream().filter(u -> u.getKey().equals(source)).forEach(u -> u.getValue().entrySet().stream()
                .filter(e -> e.getKey().equals(medium)).map(e -> e.getValue()).forEach(e -> campaigns.addAll(e)));
        return campaigns;
    }

    public EntityExtranetCreateLoanApplicationForm getConfiguredCreateLoanApplicationForm(int countryId, EntityExtranetCreateLoanApplicationForm form) throws Exception {
        if (form == null) {
            form = new EntityExtranetCreateLoanApplicationForm();
        }

        form.setCountryId(countryId);
        ((EntityExtranetCreateLoanApplicationForm.Validator) form.getValidator()).configValidator(countryId);
        ((EntityExtranetCreateLoanApplicationForm.Validator) form.getValidator()).amount.setMaxValue(productService.getMaxAmount(ProductCategory.CONSUMO, form.getCountryId()));
        ((EntityExtranetCreateLoanApplicationForm.Validator) form.getValidator()).amount.setMinValue(productService.getMinAmount(ProductCategory.CONSUMO, form.getCountryId()));

        ((EntityExtranetCreateLoanApplicationForm.Validator) form.getValidator()).amount.setMinValue(productService.getMinAmount(ProductCategory.CONSUMO, form.getCountryId()));

        return form;
    }

    @Override
    public ResponseEntity validateLoanApplicationCreateForm(EntityExtranetCreateLoanApplicationForm form, Locale locale) throws Exception {
        ((EntityExtranetCreateLoanApplicationForm.Validator) form.getValidator()).configValidator(form.getCountryId());
        if (form.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
            form.setDocType(IdentityDocumentType.CDI);
        }

//        ((EntityExtranetCreateLoanApplicationForm.Validator) form.getValidator()).amount.setMaxValue(productService.getMaxAmount(ProductCategory.CONSUMO, form.getCountryId()));
//        ((EntityExtranetCreateLoanApplicationForm.Validator) form.getValidator()).amount.setMinValue(productService.getMinAmount(ProductCategory.CONSUMO, form.getCountryId()));
        form.getValidator().validate(locale);

        if (form.getValidator().isHasErrors()) {
            if (ArrayUtils.contains(new int[]{IdentityDocumentType.CDI, IdentityDocumentType.CUIL, IdentityDocumentType.CUIT}, form.getDocType())
                    && form.getDocumentNumber() != null
                    && (form.getDocumentNumber().startsWith("30") || form.getDocumentNumber().startsWith("33") || form.getDocumentNumber().startsWith("34"))) {
                ((EntityExtranetCreateLoanApplicationForm.Validator) form.getValidator()).documentNumber.setError("El CUIT ingresado corresponde a una empresa. Por el momento solo operamos con personas físicas. Gracias por visitarnos.");
                return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
            }

            if (ArrayUtils.contains(new int[]{IdentityDocumentType.CDI, IdentityDocumentType.CUIL, IdentityDocumentType.CUIT}, form.getDocType())) {
                if (form.getDocumentNumber() != null) {
                    if (!utilService.validarCuit(form.getDocumentNumber())) {
                        ((EntityExtranetCreateLoanApplicationForm.Validator) form.getValidator()).documentNumber.setError(messageSource.getMessage("static.message.cuit", null, locale));
                        return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
                    }
                }
            }
        }

        // Validate that the document type is valid
        if (personService.getValidIdentityDocumentTypes(form.getCountryId(), null).stream().noneMatch(d -> d.getId().equals(form.getDocType()))) {
            return AjaxResponse.errorMessage("El tipo de documento es inválido");
        }

        // Validae the email is the same as registered before
        User user = userDao.getUserByDocument(form.getDocType(), form.getDocumentNumber());
        if (user != null && user.getEmail() != null && !user.getEmail().equalsIgnoreCase(form.getEmail()))
            return AjaxResponse.errorMessage("El  email no coincide con el registrado");

        return null;
    }

    @Override
    public LoanApplication getActiveLoanApplication(int docType, String docNumber, int productCategoryId) throws Exception {
        User user = userDao.getUserByDocument(docType, docNumber);
        if (user != null) {
            return loanApplicationDao.getActiveLoanApplicationByPerson(Configuration.getDefaultLocale(), user.getPersonId(), productCategoryId);
        }
        return null;
    }

    @Override
    public ResponseEntity createLoanApplication(EntityExtranetCreateLoanApplicationForm form) throws Exception {

        //register new Loan Application
        User user = userDao.registerUser(form.getName(), form.getSurname(), form.getLastSurname(), form.getDocType(), form.getDocumentNumber(), (form.getBirthday() != null ? new SimpleDateFormat("dd/MM/yyyy").parse(form.getBirthday()) : null));
        if (form.getDocType() == IdentityDocumentType.CE)
            personDao.updateNationality(user.getPersonId(), form.getNationality());
        Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), user.getPersonId(), false);
        if (form.getCountryId().equals(CountryParam.COUNTRY_ARGENTINA)) {
            personDao.updateBirthday(person.getId(), utilService.parseDate(form.getBirthday(), "dd/MM/yyyy", Configuration.getDefaultLocale()));
        }
        String phoneNUmber = form.getCode() != null ? "(" + form.getCode() + ") " + form.getPhone() : form.getPhone();
        if (phoneNUmber != null)
            userDao.registerPhoneNumber(person.getUserId(), form.getCountryId() + "", phoneNUmber);

        // Insert the email or respond that the email is invalid
        createEmailPassword(form.getEmail(), user.getId());

        LoanApplication loanApplication = loanApplicationDao.getActiveLoanApplicationByPerson(Configuration.getDefaultLocale(), user.getPersonId(), ProductCategory.CONSUMO);
        if (loanApplication != null) {
            return AjaxResponse.redirect(generateLoanApplicationLinkEntity(loanApplication));
        }
        //Save values in loanApplication Database
        loanApplication = loanApplicationDao.registerLoanApplication(
                user.getId(),
                form.getAmount(),
                Configuration.DEFAULT_INSTALLMENTS,
                form.getReason(),
                null,
                null,
                null,
                LoanApplication.ORIGIN_EXTRANET_ENTITY,
                null,
                null,
                null,
                form.getCountryId());

        if (form.getRequest() != null) {
            userService.registerIpUbication(Util.getClientIpAddres(form.getRequest()), loanApplication.getId());
            loanApplicationDao.updateUserAgent(loanApplication.getId(), form.getRequest().getHeader("User-Agent"));
        }
        loanApplicationDao.updateProductCategory(loanApplication.getId(), ProductCategory.CONSUMO);
        loanApplicationDao.updateFormAssistant(loanApplication.getId(), form.getAgentId());
        loanApplicationDao.updateSourceMediumCampaign(loanApplication.getId(), form.getSource(), form.getMedium(), form.getCampaign());
        loanApplicationDao.updateTermContent(loanApplication.getId(), form.getTerm(), form.getContent());
        loanApplicationDao.updateGoogleClickId(loanApplication.getId(), form.getGclid());
        loanApplicationDao.updateGAClientID(loanApplication.getId(), form.getGaClientID());

        loanApplication = loanApplicationDao.getLoanApplication(loanApplication.getId(), Configuration.getDefaultLocale());

        evaluationService.forwardByResult(loanApplication, null, form.getRequest());

        loanApplicationDao.updateCurrentQuestion(loanApplication.getId(), ProcessQuestion.Question.Constants.RUNNING_PREEVALUATION);

        loanApplicationDao.updatePerson(loanApplication.getId(), user.getPersonId(), user.getId());
        if (form.getEntityUserId() != null)
            loanApplicationDao.updateEntityUser(loanApplication.getId(), form.getEntityUserId());

        // Only if Argentina, start running the evaluation, so the bots doesnt take too long
        if (form.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
            runEvaluationBot(loanApplication.getId(), false);
        }

        if (form.getEntityBrandingId() != null)
            loanApplicationDao.updateEntityId(loanApplication.getId(), form.getEntityBrandingId());

        question95Service.runPreEvaluationBotIfNoRunYet(loanApplication.getId(),false);

        loanApplication = loanApplicationDao.getLoanApplication(loanApplication.getId(), Configuration.getDefaultLocale());
        return AjaxResponse.redirect(generateLoanApplicationLinkEntity(loanApplication));
    }

    @Override
    public boolean hasAnyApprovedPreEvaluation(int loanApplicationId, List<Integer> notThisEntity) throws Exception {
        List<LoanApplicationPreliminaryEvaluation> lapes = loanApplicationDao.getPreliminaryEvaluations(loanApplicationId, Configuration.getDefaultLocale(), JsonResolverDAO.EVALUATION_FOLLOWER_DB);
        List<LoanApplicationPreliminaryEvaluation> approvedLapes = new ArrayList<>();
        if (notThisEntity == null)
            approvedLapes = lapes.stream().filter(l -> l.getApproved() != null && l.getApproved()).collect(Collectors.toList());
        else
            approvedLapes = lapes.stream().filter(l -> l.getApproved() != null && l.getApproved() && !notThisEntity.contains(l.getEntityId())).collect(Collectors.toList());

        for (LoanApplicationPreliminaryEvaluation approvedLape : approvedLapes) {
            EntityProductEvaluationsProcess epep = getEntityProductEvaluationProcess(loanApplicationId, approvedLape.getEntityId(), approvedLape.getProduct().getId());
            if (epep != null && epep.getReadyForProcess() != null && epep.getReadyForProcess() && epep.getSelectable() != null && epep.getSelectable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public RescueScreenParams getRescueSreenParams(int loanApplicationId, int personId) throws Exception {
        RescueScreenParams rescueParams = loanApplicationDao.getRescueScreenParams(loanApplicationId);
        if (rescueParams == null) {
            rescueParams = new RescueScreenParams();
            rescueParams.setShowScreen(false);
        }

        // If Consolidation is in the rescue offers, validate that the person have debts to consolidate
        if (rescueParams.getShowScreen() && rescueParams.getProducts() != null && rescueParams.getProducts().stream().anyMatch(p -> p == Product.DEBT_CONSOLIDATION_OPEN || p == Product.DEBT_CONSOLIDATION)) {
            List<ConsolidableDebt> consolidableDebts = loanApplicationDao.getConsolidationAccounts(loanApplicationId);
            if (consolidableDebts == null) {

                // Get the person consolidable debts
                consolidableDebts = debtConsolidationService.getPersonConsolidableDebts(personId, Configuration.getDefaultLocale());
                if (consolidableDebts == null || consolidableDebts.isEmpty()) {
                    rescueParams.getProducts().removeIf(p -> p == Product.DEBT_CONSOLIDATION_OPEN || p == Product.DEBT_CONSOLIDATION);
                }
            }
        }

        if (rescueParams.getEntityProductParams() == null || rescueParams.getEntityProductParams().isEmpty())
            rescueParams.setShowScreen(false);

        if (rescueParams.getEntityProductParams() != null && (rescueParams.getEntityProductParams().size() == 1 && rescueParams.getEntityProductParams().stream().anyMatch(p -> p == EntityProductParams.ENT_PROD_PARAM_WENANCE_LEAD))) {
            LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
            rescueParams.setShowScreen(loanApplication.getCurrentQuestionId() != ProcessQuestion.Question.Constants.DISAPPROVE_EVALUATION);
        }

        return rescueParams;
    }

    @Override
    public void sendBadTceaAlertEmail(Integer creditId, Integer offerId, Double tcea, Double tea) {
        String body;
        if (creditId != null)
            body = "La TCEA (" + tcea + ") del credito " + creditId + " es menor a su TEA (" + tea + ")";
        else
            body = "La TCEA (" + tcea + ") de la oferta " + offerId + " es menor a su TEA (" + tea + ")";

        awsSesEmailService.sendEmail(
                "alertas@solven.pe"
                , "dev@solven.pe"
                , null
                , "TCEA mal generada!"
                , body
                , null
                , null);
    }

    @Override
    public HelpMessage loanApplicationtHelpMessage(LoanApplication loanApplication) throws Exception {
        HelpMessage helpMessage = null;
        LoanApplicationPreliminaryEvaluation loanPreliminaryEvaluation = getLastPreliminaryEvaluation(loanApplication.getId(), Configuration.getDefaultLocale(), null);
        if (loanPreliminaryEvaluation != null && loanPreliminaryEvaluation.getApproved() != null && !loanPreliminaryEvaluation.getApproved()) {
            helpMessage = loanPreliminaryEvaluation.getHelpMessage();
        } else {
            LoanApplicationEvaluation loanEvaluation = getLastEvaluation(loanApplication.getId(), loanApplication.getPersonId(), Configuration.getDefaultLocale());
            if (loanEvaluation != null) {
                helpMessage = loanEvaluation.getHelpMessage();
            }
        }
        return helpMessage;
    }

    @Override
    public boolean runFraudAlertsAndSendEmail(LoanApplication loanApplication) throws Exception {
        boolean loanIsRejected = !creditDao.runFraudAlerts(loanApplication.getId());

        List<LoanApplicationFraudAlert> loanApplicationFraudAlerts = creditDao.getLoanApplicationFraudAlerts(loanApplication.getId(), FraudAlertStatus.NUEVO);

        long needToReview = 0, notNeedToReview = 0;

        if (loanApplicationFraudAlerts != null) {
            needToReview = loanApplicationFraudAlerts.stream().filter(p -> p.getFraudAlert().getSupervisionRequired()).count();
            notNeedToReview = loanApplicationFraudAlerts.stream().filter(p -> !p.getFraudAlert().getSupervisionRequired()).count();
        }

        InteractionContent interactionContent = catalogService.getInteractionContent(InteractionContent.FRAUD_ALERTS_TO_REVIEW, loanApplication.getCountryId());

        String to = Configuration.PERU_MANAGER_EMAIL;
        String[] cc = new String[]{Configuration.GENERAL_MANAGER_EMAIL, Configuration.ARGENTINA_MANAGER_EMAIL};

        if (!Configuration.hostEnvIsProduction()) {
            cc = new String[]{Configuration.GENERAL_MANAGER_EMAIL, Configuration.ARGENTINA_MANAGER_EMAIL};
        }

        String subject = interactionContent.getSubject();
        String message = interactionContent.getBody();
        subject = subject
                .replace("%TOTAL_FRAUD_ALERTS%", String.valueOf(needToReview + notNeedToReview));
        message = message
                .replace("%TOTAL_FRAUD_ALERTS%", String.valueOf(needToReview + notNeedToReview))
                .replace("%TOTAL_NEED_TO_REVIEW_FRAUD_ALERTS%", String.valueOf(needToReview))
                .replace("%LOAN_APPLICATION_CODE%", loanApplication.getCode())
                .replace("%LINK%", Configuration.getBackofficeDomain() +
                        String.format("/person?personId=%s&tab=applications&applicationId=%s",
                                loanApplication.getPersonId(), loanApplication.getId()));

        if (loanApplication.getCountry().getId() == CountryParam.COUNTRY_ARGENTINA) {
            to = Configuration.ARGENTINA_MANAGER_EMAIL;
            cc = new String[]{Configuration.GENERAL_MANAGER_EMAIL, Configuration.PERU_MANAGER_EMAIL};

            if (!Configuration.hostEnvIsProduction()) {
                cc = new String[]{Configuration.GENERAL_MANAGER_EMAIL, Configuration.PERU_MANAGER_EMAIL};
            }
        }

        awsSesEmailService.sendEmail("alertas@solven.pe", to, cc, subject, null, message, null);

        return loanIsRejected;
    }

    @Override
    public void registerReferrerIfExists(Integer loanApplicationId, String referrerPersonId) {
        if (referrerPersonId != null) {
            try {
                Integer personId = Integer.parseInt(referrerPersonId);
                loanApplicationDao.updateReferrerPersonId(loanApplicationId, personId);
            } catch (Exception ex) {
                logger.error("Error registering referrer", ex);
                ErrorServiceImpl.onErrorStatic(ex);
            }
        }
    }

    @Override
    public void reevaluateLoanApplications(Integer... loanApplicationIds) {
        String concatIds = concatIdsToReevaluate(loanApplicationIds);
        if (!concatIds.isEmpty()) {
            loanApplicationDao.reevaluate(concatIds);
        }
    }

    @Override
    public LoanOffer getSelectedOffer(Integer loanApplicationId) throws Exception{
        return loanApplicationDao.getLoanOffers(loanApplicationId)
                .stream()
                .filter(o -> o.getSelected())
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<LoanOffer> getLonaOffers(Integer loanApplicationId) throws Exception {
        return loanApplicationDao.getLoanOffers(loanApplicationId);
    }

    @Override
    public void reevaluateLoanApplicationsButKeepBureaus(Integer... loanApplicationIds) {
        String concatIds = concatIdsToReevaluate(loanApplicationIds);
        if (!concatIds.isEmpty()) {
            loanApplicationDao.reevaluateButKeepBureaus(concatIds);
        }
    }

    private String concatIdsToReevaluate(Integer... loanApplicationIds) {
        String concatIds = "";

        for (Integer id : loanApplicationIds) {
            concatIds += id + ",";
        }

        if (!concatIds.isEmpty()) {
            concatIds = "{" + concatIds.substring(0, concatIds.length() - 1) + "}";
        }
        return concatIds;
    }

    @Override
    public void deleteDocumentsAndAskAgain(LoanApplication loanApplication, List<Integer> userFileTypeIds) throws Exception{
        // First delete the user files with the type in the list
        List<UserFile> userFiles = loanApplicationDao.getLoanApplicationUserFiles(loanApplication.getId());
        List<UserFile> userFilesToDelete = userFiles.stream().filter(u -> userFileTypeIds.contains(u.getFileType())).collect(Collectors.toList());
        for(UserFile userFile : userFilesToDelete){
            userDao.updateUserFileType(userFile.getId(), UserFileType.ELIMINADOS);
        }
        // Send the loan to the documentation question
        loanApplicationDao.updateCurrentQuestion(loanApplication.getId(),ProcessQuestion.Question.Constants.MISSING_DOCUMENTATION);
        // Send Interactio to the client
        sendMissingDocumentation(loanApplication.getUserId(), loanApplication.getId(), Configuration.getDefaultLocale());
    }

    @Override
    public String generateLandingLinkEntity(Integer entityId, Integer productCategoryId) throws Exception {
        String baseUrl = null;
        CountryParam countryParam = catalogService.getCountryParam(CountryParam.COUNTRY_PERU);
        if (entityId != null) {
            EntityBranding entityBranding = catalogService.getEntityBranding(entityId);
            Entity entity = catalogService.getEntity(entityId);
            countryParam = catalogService.getCountryParam(entity.getCountryId());

            String entitySubDomain =  entityBranding.getSubdomain();

            if (Configuration.hostEnvIsLocal()) {
                String countryDomain = countryParam.getDomains().get(0);
                baseUrl = "http://" + entitySubDomain + "." + countryDomain + ":8080/";
            } else if (Configuration.hostEnvIsProduction()) {
                String countryDomain = countryParam.getDomains().get(0);
                baseUrl = "http://" + entitySubDomain + "." + countryDomain + "/";
            } else {
                String countryDomain = countryParam.getDomains().get(0);
                baseUrl = "https://" + entitySubDomain + "." + countryDomain + "/";
            }
        } else {

            if (baseUrl == null) {
                String countryDomain = countryParam.getDomains().get(0);
                if (Configuration.hostEnvIsLocal()) {
                    baseUrl = "http://" + countryDomain + ":8080/";
                } else if(!Configuration.hostEnvIsProduction()) {
                    baseUrl = "https://" + Configuration.getEnvironmmentName() + "." + countryDomain + "/";
                } else {
                    baseUrl = "https://" + countryDomain + "/";
                }
            }
        }

        return baseUrl +
                ProductCategory.GET_URL_BY_ID(productCategoryId);
    }

    @Override
    public void updateMarketingCampaignByLoanApplication(LoanApplication loanApplication, Integer marketingCampaignId, String action) throws Exception {
        if (action == null || loanApplication == null || marketingCampaignId == null) return;
        MarketingCampaign marketingCampaign = marketingCampaignDAO.getMarketingCampaign(marketingCampaignId, Configuration.getDefaultLocale());
        if (marketingCampaign != null && loanApplication.getEntityId() != null) {
            if (marketingCampaign.getEntity().getId().equals(loanApplication.getEntityId())) {
                MarketingCampaign.MarketingTotalTrackingEvents marketingTotalTrackingEvents = marketingCampaign.getJsTotalTrackingEvents();
                if (marketingTotalTrackingEvents == null)
                    marketingTotalTrackingEvents = new MarketingCampaign.MarketingTotalTrackingEvents();
                switch (action) {
                    case "registered":
                        if (marketingTotalTrackingEvents.getRegistered() == null)
                            marketingTotalTrackingEvents.setRegistered(1);
                        else
                            marketingTotalTrackingEvents.setRegistered(marketingTotalTrackingEvents.getRegistered() + 1);
                        break;
                    case "paid":
                        if (marketingTotalTrackingEvents.getPaid() == null) marketingTotalTrackingEvents.setPaid(1);
                        else marketingTotalTrackingEvents.setPaid(marketingTotalTrackingEvents.getPaid() + 1);
                        break;
                }
                marketingCampaignDAO.updateMarketingCampaignTrackingEvents(marketingCampaignId, marketingTotalTrackingEvents);
            }
        }
    }

    @Override
    public void sendLoanWebhookEvent(String event, LoanApplication loanApplication){
        try{
            JSONObject bodyJson = new JSONObject();
            bodyJson.put("event", event);
            bodyJson.put("trackingId", loanApplication.getAuxData().getApiRestTrackingId());
            Date iniDate = new Date();
            String webhookUrl = convertWebhookUrlByEntity(loanApplication,loanApplication.getAuxData().getApiRestWebhookUrl());
            OkHttpClient clientCall = client.newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .build();
            okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json");
            okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, bodyJson.toString());
            Request serviceRequest = createWebhookRequest(loanApplication, convertWebhookUrlByEntity(loanApplication,loanApplication.getAuxData().getApiRestWebhookUrl()), body);

            Response response = clientCall.newCall(serviceRequest).execute();
            String responseReturn = response.body().string();

            ExternalWSRecord externalWSRecord = new ExternalWSRecord(loanApplication.getId() ,iniDate,new Date(),webhookUrl,bodyJson.toString(),responseReturn,null);
            externalWSRecord.setResponseHttpCode(response != null && response.networkResponse() != null ? response.networkResponse().code() : null);
            externalWSRecordDAO.insertExternalWSRecord(externalWSRecord);

            System.out.println("[sendLoanWebhookEvent [" + loanApplication.getAuxData().getApiRestWebhookUrl() + "][" + (new Date().getTime() - iniDate.getTime()) + "]] : " + responseReturn);
        }catch (Exception ex){
            errorService.onError(ex);
        }
    }

    public String convertWebhookUrlByEntity(LoanApplication loanApplication,String webHookUrl){
        if(loanApplication.getEntityId() == null && loanApplication.getSelectedEntityId() == null) return webHookUrl;
        String domain = null;
        switch (loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId()){
            case Entity.AZTECA:
                domain = null;
                break;
        }
        if(domain != null){
            String lastCharacter = domain.substring(domain.length() - 1);
            if(lastCharacter.equals("/")) domain = domain.substring(0,domain.length() - 2);
            String webHookUrlWithoutHost = webHookUrl.substring(webHookUrl.indexOf("/",9));
            return  domain + webHookUrlWithoutHost;
        }
        return webHookUrl;
    }

    private Request createWebhookRequest(LoanApplication loanApplication, String url, okhttp3.RequestBody body){
        if(loanApplication.getEntityId() != null || loanApplication.getSelectedEntityId() != null) {
            switch (loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId()){
                case Entity.AZTECA:
                    return new Request.Builder()
                            .url(url)
                            .addHeader("apikey", System.getenv("BANCO_AZTECA_WEBHOOK_KEY"))
                            .post(body)
                            .build();
            }
        }
        return new Request.Builder()
                .url(convertWebhookUrlByEntity(loanApplication,loanApplication.getAuxData().getApiRestWebhookUrl()))
                .post(body)
                .build();
    }

    @Override
    public LoanApplication createLoanApplication(CreateLoanApplicationRequest request, HttpServletRequest httpServletRequest) throws Exception {
        EntityBranding brandingEntity = request.getEntityId() != null ? catalogService.getEntityBranding(request.getEntityId()) : null;
        List<Agent> agentsList =  catalogService.getFormAssistantsAgents(request.getEntityId());
        int randomIndexWithinRange = (int) Math.floor(Math.random() * agentsList.size());
        int agentId = agentsList.get(randomIndexWithinRange).getId();
        Character abTesting = LoanApplication.AB_TESTING_A.charAt(0);
        if(request.getAbTesting() != null) abTesting = request.getAbTesting();

        User user = userService.getOrRegisterUser(request.getDocumentType(), request.getDocumentNumber(), null, null, null);
        Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), user.getPersonId(), false);

        // Insert the email or respond that the email is invalid
        if(request.getEmail() != null) userService.createEmailPassword(request.getEmail(), user.getId());

        LoanApplication loanApplication = null;
        if (brandingEntity != null) {
            loanApplication = loanApplicationDao.getActiveLoanApplicationByPerson(Configuration.getDefaultLocale(), user.getPersonId(), request.getProductCategoryId(), brandingEntity.getEntity().getId());
        } else {
            loanApplication = loanApplicationDao.getActiveLoanApplicationByPerson(Configuration.getDefaultLocale(), user.getPersonId(), request.getProductCategoryId());
        }

        // If there is an active loan, return it
        if (loanApplication != null) {
            if(request.getForceCreation() != null && request.getForceCreation()){
                loanApplicationDao.expireLoanApplication(loanApplication.getId());
                loanApplication = null;
            }
            else{
                if(request.getPhoneNumber() != null) userDao.registerPhoneNumber(loanApplication.getUserId(), loanApplication.getCountry().getId() + "", request.getPhoneNumber());
                LoanApplicationAuxData loanAuxData = loanApplication.getAuxData();
                if(loanAuxData == null) loanAuxData = new LoanApplicationAuxData();
                loanAuxData.setSkipPinQuestion(request.getSkipSMSPinValidationQuestion());
                loanApplicationDao.updateAuxData(loanApplication.getId(),loanAuxData);
                return loanApplication;
            }
        }

        // REGISTER PHONENUMBER OR RESPOND THAT PHONENUMBER IS INVALID
        if(request.getPhoneNumber() != null) userDao.registerPhoneNumber(user.getId(), brandingEntity.getEntity().getCountryId() + "", request.getPhoneNumber());

        Integer installemnts = Configuration.DEFAULT_INSTALLMENTS;
        if(brandingEntity != null && Arrays.asList(Entity.FINANSOL, Entity.PRISMA).contains(brandingEntity.getEntity().getId()))
            installemnts = 12;
        //Save values in loanApplication Database
        loanApplication = loanApplicationDao.registerLoanApplication(
                user.getId(),
                null,
                installemnts,
                null,
                null,
                null,
                null,
                request.getOrigin(),
                null,
                null,
                request.getEntityId(),
                brandingEntity.getEntity().getCountryId());

        userService.registerIpUbication(Util.getClientIpAddres(httpServletRequest), loanApplication.getId());

        loanApplicationDao.updateProductCategory(loanApplication.getId(), request.getProductCategoryId());
        loanApplicationDao.updateFormAssistant(loanApplication.getId(), agentId);

        LoanApplicationAuxData loanApplicationAuxData = loanApplication.getAuxData();
        if(loanApplicationAuxData == null) loanApplicationAuxData = new LoanApplicationAuxData();
        loanApplicationAuxData.setSkipPinQuestion(request.getSkipSMSPinValidationQuestion());
        loanApplicationAuxData.setReferenceLoanApplicationId(request.getReferenceLoanApplicationId());
        loanApplicationAuxData.setAbTestingValue(abTesting);
        loanApplicationDao.updateAuxData(loanApplication.getId(),loanApplicationAuxData);

        if (brandingEntity != null && Entity.BANBIF == brandingEntity.getEntity().getId()) {
            if (loanApplication.getEntityCustomData() == null) {
                loanApplication.setEntityCustomData(new JSONObject());
            }

            loanApplicationDao.updateEntityCustomData(loanApplication.getId(),
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANBIF_LANDING_AB_TESTING.getKey(), abTesting));
        }else if (brandingEntity != null && Entity.AZTECA == brandingEntity.getEntity().getId()) {
            if (loanApplication.getEntityCustomData() == null) {
                loanApplication.setEntityCustomData(new JSONObject());
            }

            loanApplicationDao.updateEntityCustomData(loanApplication.getId(),
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_LANDING_AB_TESTING.getKey(), abTesting));
        }

        if(brandingEntity != null && Entity.AZTECA == brandingEntity.getEntity().getId() && request.getProductCategoryId() != null){
            Date newExpirationDate = generateExpirationDateByLoan(brandingEntity.getEntity().getId(), request.getProductCategoryId());
            if(newExpirationDate != null) loanApplicationDao.updateExpirationDate(loanApplication.getId(), newExpirationDate);
        }

        loanApplication = loanApplicationDao.getLoanApplication(loanApplication.getId(), Configuration.getDefaultLocale());

        if (brandingEntity != null && Entity.AZTECA == brandingEntity.getEntity().getId() && request.getProductCategoryId() == ProductCategory.GATEWAY) {
            loanApplicationDao.updateLoanApplicationCode(loanApplication.getId(),  loanApplication.getCode().replace("LA","CA"));
        }

        if (brandingEntity != null && brandingEntity.getLandingConfiguration() != null && brandingEntity.getLandingConfiguration().getQuestionToGo() != null)
            loanApplicationDao.updateCurrentQuestion(loanApplication.getId(), brandingEntity.getLandingConfiguration().getQuestionToGo());
        else
        {
            if (person != null && person.getFirstName() != null && !person.getFirstName().isEmpty() && person.getBirthday() != null)
                loanApplicationDao.updateCurrentQuestion(loanApplication.getId(), ProcessQuestion.Question.Constants.RUNNING_PREEVALUATION);
            else
                loanApplicationDao.updateCurrentQuestion(loanApplication.getId(), ProcessQuestion.Question.Constants.NO_RENIEC_BEFORE_PRE_EVALUATION);
        }

        if (brandingEntity != null)
            loanApplicationDao.updateEntityId(loanApplication.getId(), brandingEntity.getEntity().getId());

        loanApplication = loanApplicationDao.getLoanApplication(loanApplication.getId(), Configuration.getDefaultLocale());
        loanApplicationDao.updatePerson(loanApplication.getId(), user.getPersonId(), user.getId());
        if(loanApplication.getCurrentProcessQuestion() != null && loanApplication.getCurrentProcessQuestion().getId() == ProcessQuestion.Question.Constants.RUNNING_PREEVALUATION){
            Boolean runEvaluation = false;
            if(loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA && loanApplication.getProductCategoryId() == ProductCategory.GATEWAY) runEvaluation = true;
            question95Service.runPreEvaluationBotIfNoRunYet(loanApplication.getId(),runEvaluation);
        }

        funnelStepService.registerStep(loanApplication);

        return loanApplication;
    }


    public Date generateExpirationDateByLoan(Integer entityId, Integer productCategory) {
        if(entityId != null && productCategory != null){
            switch (entityId){
                case Entity.AZTECA:
                    if (productCategory == ProductCategory.VALIDACION_IDENTIDAD) {
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.MINUTE, 15);
                        return cal.getTime();
                    }
                    else if (productCategory == ProductCategory.GATEWAY) {
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, 1);
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        return cal.getTime();
                    }
                    else if (productCategory == ProductCategory.CONSUMO) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
                        cal.set(Calendar.HOUR_OF_DAY, 23);
                        cal.set(Calendar.MINUTE, 59);
                        cal.set(Calendar.SECOND, 59);
                        return cal.getTime();
                    }
                    break;
            }
        }
        return null;
    }

    @Override
    public void sendLoanApplicationNewOfferEmail(int loanApplicationId, int personId, Locale locale) throws Exception {
        PersonContactInformation personContact = personDao.getPersonContactInformation(locale, personId);
        Person person = personDao.getPerson(catalogService, locale, personId, false);
        List<PersonInteraction> personInteractions = interactionDAO.getPersonInteractionsByLoanApplication(personId, loanApplicationId, locale);

        String loanApplicationToken = generateLoanApplicationToken(person.getUserId(), person.getId(), loanApplicationId, true);
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        if(!Arrays.asList(Entity.AZTECA).contains(loanApplication.getEntityId())) return;
        Entity entity = catalogService.getEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        // TODO Put in a threadpool
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonVars = new JSONObject();

                    String baseUrl = null;

                    if (loanApplication.getEntityId() != null) {
                        EntityBranding entityBranding = catalogService.getEntityBranding(loanApplication.getEntityId());

                        if (entityBranding != null) {
                            baseUrl = Configuration.hostEnvIsLocal() ? "http://" : "https://";

                            baseUrl += (entityBranding.getSubdomain() + "." + entityBranding.getDomain());

                            baseUrl += Configuration.hostEnvIsLocal() ? ":8080" : "";
                        }
                    }

                    String linkToRedirect = null;

                    if(loanApplication.getCountry().getId().equals(CountryParam.COUNTRY_PERU)){
                        Map<String,Object> extraParams = new HashMap<>();
                        extraParams.put(LoanApplicationServiceImpl.SHOULD_VALIDATE_EMAIL_KEY, true);
                        linkToRedirect = generateLoanApplicationLinkEntity(loanApplication,extraParams);
                    }

                    if(linkToRedirect == null) linkToRedirect = generateLoanApplicationLinkEntity(loanApplication, loanApplicationToken);

                    jsonVars.put("LINK", linkToRedirect);
                    jsonVars.put("CLIENT_NAME", person.getName().split(" ")[0]);
                    jsonVars.put("CLIENT_NAME", person.getName().split(" ")[0]);
                    jsonVars.put("ENTITY", entity.getFullName());
                    jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
                    jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

                    int interactionContentId = InteractionContent.NUEVA_OFERTA_CONSUMO_AZTECA;

                    PersonInteraction interaction = new PersonInteraction();
                    interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                    interaction.setInteractionContent(catalogService.getInteractionContent(interactionContentId, loanApplication.getCountryId()));
                    interaction.setDestination(personContact.getEmail());
                    interaction.setLoanApplicationId(loanApplicationId);
                    interaction.setPersonId(person.getId());

                    interaction.setSenderName(String.format("%s de %s",
                            loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION,
                            entity.getShortName()
                    ));

                    int finalInteractionContentId = interactionContentId;
                    if(!personInteractions.stream().anyMatch(e -> e.getInteractionContent() != null && e.getInteractionContent().getId().intValue() == finalInteractionContentId)) interactionService.sendPersonInteraction(interaction, jsonVars, null);
                } catch (Exception ex) {
                    logger.error("Error sending signature email", ex);
                    ErrorServiceImpl.onErrorStatic(ex);
                }
            }
        }).start();
    }

    @Override
    public EntityProductParams getEntityProductParamFromLoanApplication( LoanApplication loanApplication) throws Exception {
        EntityProductParams entityProductParams = null;
        if (loanApplication.getSelectedEntityProductParameterId() == null) {
            if (loanApplication.getProductCategoryId() == ProductCategory.VALIDACION_IDENTIDAD) {
                List<LoanApplicationPreliminaryEvaluation> preevals = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), Configuration.getDefaultLocale());
                Integer entityProductParamId = preevals.stream().filter(p -> p.getStatus() == 'S' && p.getApproved() != null && p.getApproved()).map(p -> p.getEntityProductParameterId()).findFirst().orElse(null);
                if (entityProductParamId != null)
                    entityProductParams = catalogService.getEntityProductParamById(entityProductParamId);
            }
        } else
            entityProductParams = catalogService.getEntityProductParamById(loanApplication.getSelectedEntityProductParameterId());
        return entityProductParams;
    }

    @Override
    public Boolean validateLoanApplicationByIPGeolocation( LoanApplication loanApplication, EntityProductParams entityProductParams, Locale locale) throws Exception {
        if(entityProductParams == null) entityProductParams = this.getEntityProductParamFromLoanApplication(loanApplication);
        boolean validateIpLocation = false;
        boolean isValid = true;
        List<String> allowedCountries = new ArrayList<>();
        List<String> allowedOrganizationsForRestOrigin = new ArrayList<>();
        if(entityProductParams != null && entityProductParams.getEntityProductParamIdentityValidationConfig() != null && entityProductParams.getEntityProductParamIdentityValidationConfig().getRunIPCountryValidation() != null){
            validateIpLocation = entityProductParams.getEntityProductParamIdentityValidationConfig().getRunIPCountryValidation();
            allowedCountries = entityProductParams.getEntityProductParamIdentityValidationConfig().getAllowedCountries();
            allowedOrganizationsForRestOrigin = entityProductParams.getEntityProductParamIdentityValidationConfig().getAllowedOrganizationsForRestOrigin();
            if(allowedCountries == null) allowedCountries = new ArrayList<>();
            if(allowedOrganizationsForRestOrigin == null) allowedOrganizationsForRestOrigin = new ArrayList<>();
        }
        if(validateIpLocation){
            isValid = allowedCountries.contains(loanApplication.getIpCountryCode());
            if(Arrays.asList(LoanApplication.ORIGIN_API_REST).contains(loanApplication.getOrigin())){
                isValid = false;
                String organization = JsonUtil.getStringFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.GEOLOCATION_IP_ORGANIZATION.getKey(), null);
                if(organization != null && allowedOrganizationsForRestOrigin != null && !allowedOrganizationsForRestOrigin.isEmpty()){
                    isValid = allowedOrganizationsForRestOrigin.contains(organization);
                }
            }
        }
        return isValid;
    }

    @Override
    public Integer getDifferenceFromLoanAndDueDate(LoanApplication loanApplication){
        if(loanApplication.getFirstDueDate() == null) return null;
        Integer daysBetween = Math.toIntExact(TimeUnit.DAYS.convert(Math.abs(new Date().getTime() - loanApplication.getFirstDueDate().getTime()), TimeUnit.MILLISECONDS));
        return daysBetween;
    }

    @Override
    public boolean isValidDueDate(LoanApplication loanApplication, int minDays){
        Integer daysBetween = this.getDifferenceFromLoanAndDueDate(loanApplication);
        if(daysBetween == null) return false;
        return daysBetween >= minDays;
    }

    @Override
    public Date updateAlfinDueDate(LoanApplication loanApplication, List<Date> enableDates, EntityProductParams entityProductParams) throws Exception {
        enableDates.sort((d1,d2) -> d1.compareTo(d2));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(loanApplication.getFirstDueDate());
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        Date firstValidDate = enableDates.stream().filter(e -> e.compareTo(calendar.getTime()) >= 0).findFirst().orElse(null);
        if(firstValidDate == null){
            errorService.sendErrorCriticSlack(String.format("Loan %s with first due date to change = null", loanApplication.getId().toString()));
            throw new RuntimeException("UpdateAlfinDueDate error, firstValidDate = null");
        }
        loanApplication.setFirstDueDate(firstValidDate);
        loanApplicationDao.updateFirstDueDate(loanApplication.getId(), firstValidDate);
        loanApplicationDao.updateEntityCustomData(loanApplication.getId(),
                loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.AZTECA_DUE_DATE_MODIFIED.getKey(), true));
        BantotalApiData bantotalApiData = loanApplication.getBanTotalApiData();
        if(bantotalApiData != null && bantotalApiData.getOperacionUIdSimulacionConCliente() != null){
            bantotalApiData.setOperacionUIdSimulacionConCliente(null);
            loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANTOTAL_API_DATA.getKey(), new JSONObject(new Gson().toJson(bantotalApiData)));
            loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
        }
        return firstValidDate;
    }

    @Override
    public void sendLoanApplicationFirsDueDateEmail(int loanApplicationId, int personId, Locale locale) throws Exception {
        PersonContactInformation personContact = personDao.getPersonContactInformation(locale, personId);
        Person person = personDao.getPerson(catalogService, locale, personId, false);
        List<PersonInteraction> personInteractions = interactionDAO.getPersonInteractionsByLoanApplication(personId, loanApplicationId, locale);

        String loanApplicationToken = generateLoanApplicationToken(person.getUserId(), person.getId(), loanApplicationId, true);
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
        if(!Arrays.asList(Entity.AZTECA).contains(loanApplication.getEntityId())) return;
        Entity entity = catalogService.getEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonVars = new JSONObject();

                    String baseUrl = null;

                    if (loanApplication.getEntityId() != null) {
                        EntityBranding entityBranding = catalogService.getEntityBranding(loanApplication.getEntityId());

                        if (entityBranding != null) {
                            baseUrl = Configuration.hostEnvIsLocal() ? "http://" : "https://";

                            baseUrl += (entityBranding.getSubdomain() + "." + entityBranding.getDomain());

                            baseUrl += Configuration.hostEnvIsLocal() ? ":8080" : "";
                        }
                    }

                    String linkToRedirect = null;

                    if(loanApplication.getCountry().getId().equals(CountryParam.COUNTRY_PERU)){
                        Map<String,Object> extraParams = new HashMap<>();
                        extraParams.put(LoanApplicationServiceImpl.SHOULD_VALIDATE_EMAIL_KEY, true);
                        linkToRedirect = generateLoanApplicationLinkEntity(loanApplication,extraParams);
                    }

                    if(linkToRedirect == null) linkToRedirect = generateLoanApplicationLinkEntity(loanApplication, loanApplicationToken);

                    jsonVars.put("LINK", linkToRedirect);
                    jsonVars.put("CLIENT_NAME", person.getName().split(" ")[0]);
                    jsonVars.put("CLIENT_NAME", person.getName().split(" ")[0]);
                    jsonVars.put("ENTITY", entity.getFullName());
                    jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
                    jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

                    int interactionContentId = InteractionContent.ALFIN_CAMBIO_DE_FECHA_PRIMERA_CUOTA;

                    PersonInteraction interaction = new PersonInteraction();
                    interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                    interaction.setInteractionContent(catalogService.getInteractionContent(interactionContentId, loanApplication.getCountryId()));
                    interaction.setDestination(personContact.getEmail());
                    interaction.setLoanApplicationId(loanApplicationId);
                    interaction.setPersonId(person.getId());

                    interaction.setSenderName(String.format("%s de %s",
                            loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION,
                            entity.getShortName()
                    ));

                    int finalInteractionContentId = interactionContentId;
                    //if(!personInteractions.stream().anyMatch(e -> e.getInteractionContent() != null && e.getInteractionContent().getId().intValue() == finalInteractionContentId))
                    interactionService.sendPersonInteraction(interaction, jsonVars, null);
                } catch (Exception ex) {
                    logger.error("Error sending signature email", ex);
                    ErrorServiceImpl.onErrorStatic(ex);
                }
            }
        }).start();
    }



}