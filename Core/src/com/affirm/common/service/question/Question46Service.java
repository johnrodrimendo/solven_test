package com.affirm.common.service.question;

import com.affirm.common.model.form.Question37Form;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

@Service("question46Service")
public class Question46Service extends AbstractQuestionService<Question37Form>  {

    @Autowired
    private Question37Service question37Service;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        return question37Service.getViewAttributes(flowType, id, locale, fillSavedData, params);
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question37Form form) throws Exception {
        return question37Service.getQuestionResultToGo(flowType, id, form);
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question37Form form, Locale locale) throws Exception {
        question37Service.validateForm(flowType, id, form, locale);
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question37Form form, Locale locale) throws Exception {
        question37Service.saveData(flowType, id, form, locale);
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        return question37Service.getSkippedQuestionResultToGo(flowType, id, locale, saveData);
    }

}

