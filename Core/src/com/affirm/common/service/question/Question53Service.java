package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.SelfEvaluationDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.Question53Form;
import com.affirm.common.model.transactional.*;
import com.affirm.common.util.FormValidationException;
import com.affirm.common.util.GoToNextQuestionException;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("question53Service")
public class Question53Service extends AbstractQuestionService<Question53Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private SelfEvaluationDAO selfEvaluationDao;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question53Form form = new Question53Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                if(loanApplication.getProductCategoryId() != null && Arrays.asList(ProductCategory.GATEWAY).contains(loanApplication.getProductCategoryId()) && loanApplication.getAuxData() != null && loanApplication.getAuxData().getReferenceLoanApplicationId() != null && Arrays.asList(LoanApplication.ORIGIN_REFERENCED).contains(loanApplication.getOrigin())){
                    throw new GoToNextQuestionException("DEFAULT", ProcessQuestionSequence.TYPE_FORWARD);
                }
                ((Question53Form.Validator) form.getValidator()).configValidator(loanApplication.getCountryId(), flowType);

                if (fillSavedData) {
                    User user = userDao.getUser(loanApplication.getUserId());
                    form.setAreaCode(user.getPhoneCode());
                    form.setPhoneNumber(user.getPhoneNumberWithoutCode());
                }

                if(loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA){
                    if(loanApplication.getProductCategoryId() == ProductCategory.GATEWAY){
                        LoanApplicationPreliminaryEvaluation preEvaluationApproved = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), locale)
                                .stream().filter(p -> p.getApproved() != null && p.getApproved())
                                .findFirst().orElse(null);
                        if(preEvaluationApproved != null){
                            if(preEvaluationApproved.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY_VIGENTE){
                                attributes.put("customTitle", "Para ver tu estado de cuenta ingresa tu número celular.");
                            }
                        }
                    }
                }

                boolean isLoanPayment = false;
                if(loanApplication.getProductCategoryId() != null && loanApplication.getProductCategoryId().intValue() == ProductCategory.GATEWAY) isLoanPayment = true;
                attributes.put("isLoanPayment", isLoanPayment);
                attributes.put("form", form);
                attributes.put("countryId", loanApplication.getCountryId());
                attributes.put("isSelfEvaluation", false);
                attributes.put("isEvaluation", true);
                break;
            case SELFEVALUATION:
                SelfEvaluation selfEvaluation = selfEvaluationDao.getSelfEvaluation(id, locale);
                ((Question53Form.Validator) form.getValidator()).configValidator(selfEvaluation.getCountryParam().getId(), flowType);

                if (fillSavedData) {
                    User user = userDao.getUser(userDao.getUserIdByPersonId(selfEvaluation.getPersonId()));
                    form.setAreaCode(user.getPhoneCode());
                    form.setPhoneNumber(user.getPhoneNumberWithoutCode());
                }

                attributes.put("form", form);
                attributes.put("countryId", selfEvaluation.getCountryParam().getId());
                attributes.put("isSelfEvaluation", true);
                attributes.put("isEvaluation", false);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question53Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
            case SELFEVALUATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question53Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                ((Question53Form.Validator) form.getValidator()).configValidator(loanApplication.getCountryId(), flowType);
                form.getValidator().validate(locale);

                if (!form.getValidator().isHasErrors()) {
                    if (loanApplication.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
                        if ((form.getAreaCode() != null && form.getPhoneNumber() == null) ||
                                (form.getAreaCode() != null && form.getPhoneNumber() != null && form.getAreaCode().concat(form.getPhoneNumber()).length() != 11))
                            throw new FormValidationException("El número telefónico ingresado es incorrecto, no contiene los 10 dígitos requeridos.");
                    }
                }
                break;
            case SELFEVALUATION:
                SelfEvaluation selfEvaluation = selfEvaluationDao.getSelfEvaluation(id, locale);
                ((Question53Form.Validator) form.getValidator()).configValidator(selfEvaluation.getCountryParam().getId(), flowType);
                form.getValidator().validate(locale);

                if (!form.getValidator().isHasErrors()) {
                    if (selfEvaluation.getCountryParam().getId() == CountryParam.COUNTRY_ARGENTINA) {
                        if ((form.getAreaCode() != null && form.getPhoneNumber() == null) ||
                                (form.getAreaCode() != null && form.getPhoneNumber() != null && form.getAreaCode().concat(form.getPhoneNumber()).length() != 11))
                            throw new FormValidationException("El número telefónico ingresado es incorrecto, no contiene los 10 dígitos requeridos.");
                    }
                }
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question53Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                String phoneNUmber = form.getAreaCode() != null ? "(" + form.getAreaCode() + ") " + form.getPhoneNumber() : form.getPhoneNumber();
                if(loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA && loanApplication.getProductCategoryId() != null && loanApplication.getProductCategoryId() == ProductCategory.GATEWAY)
                    userDao.registerPhoneNumber(loanApplication.getUserId(), loanApplication.getCountry().getId() + "", phoneNUmber, true);
                else
                    userDao.registerPhoneNumber(loanApplication.getUserId(), loanApplication.getCountry().getId() + "", phoneNUmber);
                loanApplicationDao.updateSmsSent(loanApplication.getId(), false);

                // This is added to not send SMS for the moment
                /*ProcessQuestion currentQuestion = evaluationService.getQuestionFromEvaluationProcess(loanApplication, null, ProcessQuestion.Question.Constants.VERIFICATION_PHONE_NUMBER, null, null);
                boolean sendSms = Configuration.hostEnvIsProduction() ? JsonUtil.getBooleanFromJson(currentQuestion.getConfiguration(), "sendSms", false) : false;
                if (loanApplication.getEntityUserId() == null && sendSms) {
                    EntityBranding entityBranding = loanApplication.getEntityId() != null ? catalogService.getEntityBranding(loanApplication.getEntityId()) : null;
                    String entityShort = entityBranding != null && entityBranding.getBranded() ? entityBranding.getEntity().getShortName() : null;
                    userService.sendAuthTokenSms(loanApplication.getUserId(), loanApplication.getPersonId(), loanApplication.getCountry().getId() + "", phoneNUmber, null, loanApplication.getId(), entityShort, loanApplication.getCountryId());
                }*/
                break;
            case SELFEVALUATION:
                SelfEvaluation selfEvaluation = selfEvaluationDao.getSelfEvaluation(id, locale);
                if (form.getPhoneNumber() != null) {
                    Integer userId = userDao.getUserIdByPersonId(selfEvaluation.getPersonId());
                    phoneNUmber = form.getAreaCode() != null ? "(" + form.getAreaCode() + ") " + form.getPhoneNumber() : form.getPhoneNumber();
                    userDao.registerPhoneNumber(userId, selfEvaluation.getCountryParam().getId() + "", phoneNUmber);
                }
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
//        COMMENTED BECAUSE QUESTION 54 CAN GO BACKWARDS. WRONG_PHONE_NUMBER
//        switch (flowType) {
//            case LOANAPPLICATION:
//                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
//                if (evaluationService.isQuestionRepeatedInProces(loanApplication.getQuestionSequence(), 53)) {
//                    return "DEFAULT";
//                }
//                break;
//        }
        return null;
    }

}

