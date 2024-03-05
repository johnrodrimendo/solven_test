package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.SelfEvaluationDAO;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.ProcessQuestionSequence;
import com.affirm.common.model.transactional.SelfEvaluation;
import com.affirm.common.service.CountryContextService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.SelfEvaluationService;
import com.affirm.common.util.*;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.ParameterizedType;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractQuestionService<T extends FormGeneric> {

    public static final String VIEW_PARAM_OCUPATION_NUMBER = "ocupationNumber";

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private SelfEvaluationDAO selfEvaluationDao;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private SelfEvaluationService selfEvaluationService;
    @Autowired
    private CountryContextService countryContextService;

    public Integer getQuestionIdToGo(QuestionFlowService.Type flowType, Integer id, T form, HttpServletRequest request, Integer questionId) throws Exception {
        String result = getQuestionResultToGo(flowType, id, form);

        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                return evaluationService.getQuestionIdByResult(loanApplication, questionId, result, request);
            case SELFEVALUATION:
                SelfEvaluation selfEvaluation = selfEvaluationDao.getSelfEvaluation(id, Configuration.getDefaultLocale());
                return selfEvaluationService.getQuestionIdByResult(selfEvaluation, selfEvaluation.getCurrentQuestionId(), result, request);
        }
        return null;
    }

    public ResponseEntity goToNextQuestionClient(QuestionFlowService.Type flowType, Integer id, T form, HttpServletRequest request, Integer categoryId, Integer questionId) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION: {
                LoanApplication loanApplication = null;
                if (id != null)
                    loanApplication = loanApplicationDao.getLoanApplicationLite(id, Configuration.getDefaultLocale());

                String resultToGo;
                try {
                    resultToGo = getQuestionResultToGo(flowType, id, form);
                } catch (ResponseEntityException rex) {
                    return rex.getResponseEntity();
                }

                if (loanApplication != null) {
                    return ProcessQuestionResponse.goToQuestion(
                            evaluationService.forwardByResult(
                                    loanApplication,
                                    resultToGo,
                                    request), evaluationService.generateEvaluationToken(loanApplication.getUserId(), loanApplication.getPersonId(), loanApplication.getId()));
                } else {
                    return ProcessQuestionResponse.goToQuestion(
                            evaluationService.getQuestionIdByResult(null,
                                    categoryId,
                                    questionId,
                                    resultToGo,
                                    countryContextService.getCountryParamsByRequest(request).getId(),
                                    request));
                }
            }
            case SELFEVALUATION: {
                SelfEvaluation selfEvaluation = null;
                if (id != null)
                    selfEvaluation = selfEvaluationDao.getSelfEvaluation(id, Configuration.getDefaultLocale());

                String resultToGo;
                try {
                    resultToGo = getQuestionResultToGo(flowType, id, form);
                } catch (ResponseEntityException rex) {
                    return rex.getResponseEntity();
                }

                if (selfEvaluation != null) {
                    return ProcessQuestionResponse.goToQuestion(
                            selfEvaluationService.forwardByResult(
                                    selfEvaluation,
                                    resultToGo,
                                    request), selfEvaluationService.generateSelfEvaluationToken(selfEvaluation.getId()));
                } else {
                    return ProcessQuestionResponse.goToQuestion(
                            selfEvaluationService.forwardByResult(
                                    selfEvaluation,
                                    resultToGo,
                                    request));
                }
            }
        }
        return null;
    }

    public ResponseEntity goToNextQuestionBackoffice(QuestionFlowService.Type flowType, Integer id, T form, HttpServletRequest request, Integer questionId) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION: {
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());

                String resultToGo;
                try {
                    resultToGo = getQuestionResultToGo(flowType, id, form);
                } catch (ResponseEntityException rex) {
                    return rex.getResponseEntity();
                }

                if (loanApplication.getCurrentQuestionId() != questionId.intValue()) {
                    // Return the question sequence until find the question to update
                    Integer backwardQuestionId = loanApplication.getCurrentQuestionId();
                    int maxLoops = 30;
                    while (backwardQuestionId != questionId.intValue() && maxLoops > 0) {
                        try {
                            backwardQuestionId = evaluationService.backward(loanApplication, null);
                        }catch (Exception ex){
                            break;
                        }
                        loanApplication = loanApplicationDao.getLoanApplication(loanApplication.getId(), Configuration.getDefaultLocale());
                        maxLoops--;
                    }

                    if (loanApplication.getCurrentQuestionId() != questionId.intValue()) {
                        // The backwards loop failed, what to do now? just update the current question id
                        loanApplicationDao.updateCurrentQuestion(loanApplication.getId(), questionId);
                        loanApplication.setCurrentQuestionId(questionId);
                    }
                }

                evaluationService.forwardByResult(
                        loanApplication,
                        resultToGo,
                        request);
                return AjaxResponse.ok(null);
            }
            case SELFEVALUATION: {
                SelfEvaluation selfEvaluation = selfEvaluationDao.getSelfEvaluation(id, Configuration.getDefaultLocale());
                String resultToGo;
                try {
                    resultToGo = getQuestionResultToGo(flowType, id, form);
                } catch (ResponseEntityException rex) {
                    return rex.getResponseEntity();
                }

                selfEvaluationService.forwardByResult(
                        selfEvaluation,
                        resultToGo,
                        request);

                return AjaxResponse.ok(null);
            }
        }
        return null;
    }

    public ResponseEntity validateFormClient(QuestionFlowService.Type flowType, Integer id, T form, Locale locale) throws Exception {
        try {
            validateForm(flowType, id, form, locale);
        } catch (FormValidationException formEx) {
            return AjaxResponse.errorMessage(formEx.getMessage());
        } catch (ResponseEntityException rex) {
            return rex.getResponseEntity();
        }

        if (form != null && form.getValidator() != null && form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        return null;
    }

    public ResponseEntity validateFormBackoffice(QuestionFlowService.Type flowType, Integer id, T form, Locale locale) throws Exception {
        try {
            validateForm(flowType, id, form, locale);
        } catch (FormValidationException formEx) {
            return AjaxResponse.errorMessage(formEx.getMessage());
        }

        if (form != null && form.getValidator() != null && form.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(form.getValidator().getErrorsJson());
        }

        return null;
    }

    public ResponseEntity skipQuestionClient(QuestionFlowService.Type flowType, Integer id, Locale locale, HttpServletRequest request) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                return ProcessQuestionResponse.goToQuestion(
                        evaluationService.forwardByResult(
                                loanApplication,
                                getSkippedQuestionResultToGo(flowType, id, locale, true),
                                ProcessQuestionSequence.TYPE_SKIPPED,
                                request));
            case SELFEVALUATION:
//                throw new Exception("There is not skip to selfevaluation");
        }
        return null;
    }

    public ResponseEntity skipQuestionBackoffice(QuestionFlowService.Type flowType, Integer id, Locale locale, HttpServletRequest request, Integer questionId) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());

                if (loanApplication.getCurrentQuestionId() != questionId.intValue()) {
                    try{
                        // Return the question sequence until find the question to update
                        Integer backwardQuestionId = loanApplication.getCurrentQuestionId();
                        int maxLoops = 30;
                        while (backwardQuestionId != questionId.intValue() && maxLoops > 0) {
                            backwardQuestionId = evaluationService.backward(loanApplication, null);
                            loanApplication = loanApplicationDao.getLoanApplication(loanApplication.getId(), Configuration.getDefaultLocale());
                            maxLoops--;
                        }
                    }catch (Exception ignored){
                    }

                    if (loanApplication.getCurrentQuestionId() != questionId.intValue()) {
                        // The backwards loop failed, what to do now? just update the current question id
                        loanApplicationDao.updateCurrentQuestion(loanApplication.getId(), questionId);
                        loanApplication.setCurrentQuestionId(questionId);
                    }
                }

                evaluationService.forwardByResult(
                        loanApplication,
                        getSkippedQuestionResultToGo(flowType, id, locale, true),
                        ProcessQuestionSequence.TYPE_SKIPPED,
                        request);

                return AjaxResponse.ok(null);
            case SELFEVALUATION:
                throw new Exception("There is not skip to selfevaluation");
        }
        return null;
    }

    public T fromJsonToForm(String json) throws Exception {
        if (json == null)
            return null;

        return new Gson().fromJson(json, ((Class<T>) ((ParameterizedType) this.getClass().
                getGenericSuperclass()).getActualTypeArguments()[0]));
    }

    public Integer forwardByResult(QuestionFlowService.Type flowType, Integer id, HttpServletRequest request, String result, int sequenceType) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                return evaluationService.forwardByResult(
                                loanApplication,
                                result,
                                sequenceType,
                                request);
            case SELFEVALUATION:
                throw new Exception("There is not skip to selfevaluation");
        }
        return null;
    }

    public abstract Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception;

    public abstract String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, T form) throws Exception;

    protected abstract void validateForm(QuestionFlowService.Type flowType, Integer id, T form, Locale locale) throws Exception;

    public abstract void saveData(QuestionFlowService.Type flowType, Integer id, T form, Locale locale) throws Exception;

    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        throw new Exception("This method was not configured");
    }

    public abstract String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception;


}
