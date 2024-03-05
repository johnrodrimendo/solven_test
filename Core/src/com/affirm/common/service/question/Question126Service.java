package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.form.Question94Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;

@Service("question126Service")
public class Question126Service extends Question94Service {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = super.getViewAttributes(flowType, id, locale, fillSavedData, params);
        attributes.put("questionId", 126);
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question94Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if (loanApplication.getQuestionSequence().stream().anyMatch(s -> s.getId().equals(ProcessQuestion.Question.Constants.RUNNING_EVALUATION))) {
                    return "EVALUATION";
                } else if (loanApplication.getQuestionSequence().stream().anyMatch(s -> s.getId().equals(ProcessQuestion.Question.Constants.RUNNING_PREEVALUATION))) {
                    return "PRELIMINARY_EVALUATION";
                } else {
                    return "DEFAULT";
                }
        }
        return null;
    }

}

