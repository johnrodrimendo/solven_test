package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.form.Question130Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.ResponseEntityException;
import com.affirm.system.configuration.Configuration;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question130Service")
public class Question130Service extends AbstractQuestionService<Question130Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private Question21Service question21Service;
    @Autowired
    private Question80Service question80Service;
    @Autowired
    private Question108Service question108Service;
    @Autowired
    private Question22Service question22Service;
    @Autowired
    private Question23Service question23Service;
    @Autowired
    private Question24Service question24Service;
    @Autowired
    private Question118Service question118Service;
    @Autowired
    private Question25Service question25Service;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:

                Map<Integer, Map<String, Object>> questionAttrs = new HashMap<>();
                questionAttrs.put(21, question21Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(80, question80Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(108, question108Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(22, question22Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(23, question23Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                questionAttrs.put(24, question24Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                if(question118Service.getSkippedQuestionResultToGo(flowType, id, locale, false) == null){
                    questionAttrs.put(118, question118Service.getViewAttributes(flowType, id, locale, fillSavedData, params));
                }
                questionAttrs.put(25, question25Service.getViewAttributes(flowType, id, locale, fillSavedData, params));

                attributes.put("questionAttrs", questionAttrs);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question130Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    public Map<Integer, FormGeneric> getQuestionFormsToProcess(Question130Form form, QuestionFlowService.Type flowType, Integer id) throws Exception {
        Map<Integer, FormGeneric> forms = new HashMap<>();

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
        forms.put(21, form.getQuestion21Form());
        if (question21Service.getQuestionResultToGo(flowType, id, form.getQuestion21Form()).equals("WITH_PARTNER"))
            forms.put(108, form.getQuestion108Form());
        forms.put(80, form.getQuestion80Form());
        forms.put(22, form.getQuestion22Form());
        forms.put(23, form.getQuestion23Form());
        forms.put(24, form.getQuestion24Form());
        if(question118Service.getSkippedQuestionResultToGo(flowType, id, Configuration.getDefaultLocale(), false) == null)
            forms.put(118, form.getQuestion118Form());
        forms.put(25, form.getQuestion25Form());
        return forms;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question130Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                Map<Integer, FormGeneric> forms = getQuestionFormsToProcess(form, flowType, id);
                for (Map.Entry<Integer, FormGeneric> entry : forms.entrySet()) {
                    Object questionService = applicationContext.getBean("question" + entry.getKey() + "Service");
                    ((AbstractQuestionService) questionService).validateForm(flowType, id, entry.getValue(), locale);
                    if (entry.getValue().getValidator().isHasErrors()){
                        JSONObject jsonError = new JSONObject(entry.getValue().getValidator().getErrorsJson());
                        jsonError.put("questionId", entry.getKey());
                        throw new ResponseEntityException(AjaxResponse.errorFormValidation(jsonError.toString()));
                    }
                }
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question130Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                Map<Integer, FormGeneric> forms = getQuestionFormsToProcess(form, flowType, id);
                for (Map.Entry<Integer, FormGeneric> entry : forms.entrySet()) {
                    Object questionService = applicationContext.getBean("question" + entry.getKey() + "Service");
                    ((AbstractQuestionService) questionService).saveData(flowType, id, entry.getValue(), locale);
                }
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                if (loanApplication.getAssistedProcess())
                    return "NORMAL_FLOW";

                if (loanApplication.getQuestionFlow() == null)
                    setLoanApplicationQuestioFlow(loanApplication);
                if (loanApplication.getQuestionFlow() == LoanApplication.QUESTION_FLOW_ONE_BY_ONE)
                    return "NORMAL_FLOW";

                break;
        }
        return null;
    }

    public void setLoanApplicationQuestioFlow(LoanApplication loanApplication) {
//        char questionFlow = loanApplication.getId() % 20 == 0 ? LoanApplication.QUESTION_FLOW_GROUPED : LoanApplication.QUESTION_FLOW_ONE_BY_ONE;
        char questionFlow = LoanApplication.QUESTION_FLOW_ONE_BY_ONE;
        loanApplicationDao.updateQuestionFlow(loanApplication.getId(), questionFlow);
        loanApplication.setQuestionFlow(questionFlow);
    }
}

