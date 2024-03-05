package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.catalog.ConsolidationAccountType;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.form.Question94Form;
import com.affirm.common.model.transactional.ConsolidableDebt;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.DebtConsolidationService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.ProcessQuestionResponse;
import com.affirm.common.util.ResponseEntityException;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Service("question94Service")
public class Question94Service extends AbstractQuestionService<Question94Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private DebtConsolidationService debtConsolidationService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private EvaluationService evaluationService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question94Form form = new Question94Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                List<ConsolidableDebt> consolidableDebts = loanApplicationDao.getConsolidationAccounts(loanApplication.getId());
                if (consolidableDebts == null) {

                    // Get the person consolidable debts
                    consolidableDebts = debtConsolidationService.getPersonConsolidableDebts(loanApplication.getPersonId(), locale);

                    // Register the cnosolidations debts
                    debtConsolidationService.registerconsolidation(consolidableDebts, loanApplication.getId());
                    consolidableDebts = loanApplicationDao.getConsolidationAccounts(loanApplication.getId());
                    if (consolidableDebts == null)
                        consolidableDebts = new ArrayList<>();

//                    loanApplicationDao.updateAmount(loanApplication.getId(), consolidableDebts.stream().mapToInt(c -> c.getTotalBalance()).sum());
                    loanApplicationDao.updateInstallments(
                            loanApplication.getId(),
                            catalogService.getCatalogById(Product.class, Product.DEBT_CONSOLIDATION, locale).getProductParams(loanApplication.getCountryId()).getMaxInstallments());
                }

                attributes.put("loanApplication", loanApplication);
                attributes.put("debts", consolidableDebts);
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question94Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question94Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question94Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                List<ConsolidableDebt> debts = loanApplicationDao.getConsolidationAccounts(id);

                if (debts == null || debts.stream().noneMatch(d -> d.isSelected())) {
                    throw new ResponseEntityException(AjaxResponse.errorMessage("Tienes que seleccionar al menos una deuda a consolidar."));
                }
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                List<ConsolidableDebt> consolidableDebts = loanApplicationDao.getConsolidationAccounts(loanApplication.getId());
                if (consolidableDebts == null) {

                    // Get the person consolidable debts
                    consolidableDebts = debtConsolidationService.getPersonConsolidableDebts(loanApplication.getPersonId(), locale);

                    if (consolidableDebts == null || consolidableDebts.isEmpty())
                        return "NO_DEBTS";
                }
                break;
        }
        return null;
    }

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                switch (path) {
                    case "updateConsolidableDebt":{
                        Integer consolidationAccountType = (Integer)params.get("consolidationAccountType");
                        String entityCode = (String)params.get("entityCode");
                        Question94Form form = (Question94Form) params.get("form");

                        form.getValidator().validate(locale);
                        if (form.getValidator().isHasErrors()) {
                            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
                        }

                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        double maxAmount = catalogService.getCatalog(Product.class, locale, true, p -> p.getProductCategoryId() == loanApplication.getProductCategoryId().intValue())
                                .stream()
                                .map(p -> p.getProductParams(loanApplication.getCountryId()))
                                .filter(p -> p != null)
                                .mapToDouble(p -> p.getMaxAmount()).max().orElse(0);

                        List<ConsolidableDebt> debts = loanApplicationDao.getConsolidationAccounts(loanApplication.getId());
                        ConsolidableDebt debtToUpdate = debts.stream()
                                .filter(d -> d.getEntity().getCode().equals(entityCode) && d.getConsolidationAccounttype() == consolidationAccountType).findFirst().orElse(null);

                        if (debtToUpdate == null)
                            return AjaxResponse.errorMessage("No existe la deuda");

                        // Update the consolidable debt
                        if (form.getBalance() != null) {
                            debtToUpdate.setBalance(form.getBalance());

                            // If the debt is active, validate that the total amount is not greater than the max amount
                            if (debtToUpdate.isSelected()) {
                                double totalDebtAmount = debts.stream().filter(ConsolidableDebt::isSelected).mapToDouble(ConsolidableDebt::getTotalBalance).sum();
                                if (totalDebtAmount > maxAmount) {
                                    return AjaxResponse.errorMessage("El monto es mayor al permitido");
                                }
                            }
                            debtConsolidationService.registerconsolidation(Collections.singletonList(debtToUpdate), loanApplication.getId());
                        } else if (form.getRate() != null) {
                            debtToUpdate.setRate(form.getRate());
                            debtConsolidationService.registerconsolidation(Collections.singletonList(debtToUpdate), loanApplication.getId());
                        } else if (form.getInstallments() != null) {
                            debtToUpdate.setInstallments(form.getInstallments());
                            debtConsolidationService.registerconsolidation(Collections.singletonList(debtToUpdate), loanApplication.getId());
                        } else if (form.getConsolidable() != null) {
                            debtToUpdate.setSelected(form.getConsolidable());

                            // If the debt is changing to active and is credit card, verify the  credit card variables
                            if (debtToUpdate.getConsolidationAccounttype() == ConsolidationAccountType.TARJETA_CREDITO && debtToUpdate.isSelected()) {
                                ((Question94Form.Validator) form.getValidator()).creditCardNumber.setRequired(true);
                                ((Question94Form.Validator) form.getValidator()).creditCardBrand.setRequired(true);
                                ((Question94Form.Validator) form.getValidator()).creditCardDepartment.setRequired(true);
                                form.getValidator().validate(locale);
                                if (form.getValidator().isHasErrors())
                                    return AjaxResponse.errorMessage("Falta información de la tarjeta de crédito");

                                debtToUpdate.setAccountCardNumber(form.getCreditCardNumber());
                                debtToUpdate.setBrandId(form.getCreditCardBrand());
                                debtToUpdate.setDepartmentUbigeo(form.getCreditCardDepartment());
                            }

                            if (debtToUpdate.getConsolidationAccounttype() == ConsolidationAccountType.CREDITO_CONSUMO && debtToUpdate.isSelected()){
                                ((Question94Form.Validator) form.getValidator()).loanNumber.setRequired(false);
                                form.getValidator().validate(locale);
                                debtToUpdate.setAccountCardNumber(form.getLoanNumber());
                            }

                            // If the debt is changing to active, validate that the total amount is not greater than the max amount
                            if (debtToUpdate.isSelected()) {
                                double totalDebtAmount = debts.stream().filter(ConsolidableDebt::isSelected).mapToDouble(ConsolidableDebt::getTotalBalance).sum();
                                if (totalDebtAmount > maxAmount) {
                                    return AjaxResponse.errorMessage("El monto es mayor al permitido");
                                }
                            }
                            debtConsolidationService.registerconsolidation(Collections.singletonList(debtToUpdate), loanApplication.getId());
                        }

                        int totalDebtAmount = debts.stream().filter(ConsolidableDebt::isSelected).mapToInt(ConsolidableDebt::getTotalBalance).sum();
                        loanApplicationDao.updateAmount(loanApplication.getId(), totalDebtAmount);

                        return AjaxResponse.ok(null);
                    }
                    case "nodebts": {
                        HttpServletRequest request = (HttpServletRequest)params.get("request");

                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        List<ConsolidableDebt> debts = loanApplicationDao.getConsolidationAccounts(loanApplication.getId());
                        if (debts != null) {
                            for (ConsolidableDebt debt : debts.stream().filter(d -> d.isSelected()).collect(Collectors.toList())) {
                                debt.setSelected(false);
                                debtConsolidationService.registerconsolidation(Collections.singletonList(debt), loanApplication.getId());
                            }
                        }
                        return ProcessQuestionResponse.goToQuestion(evaluationService.forwardByResult(loanApplication, "NO_DEBTS", request));
                    }
                }
                break;
        }
        throw new Exception("No method configured");
    }

}

