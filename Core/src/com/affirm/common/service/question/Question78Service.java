package com.affirm.common.service.question;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.transactional.LoanApplicationEvaluation;
import com.affirm.common.util.FormGeneric;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service("question78Service")
public class Question78Service extends AbstractQuestionService<FormGeneric> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, FormGeneric form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, FormGeneric form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, FormGeneric form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                List<LoanApplicationEvaluation> evaluations = loanApplicationDao.getEvaluations(id, locale, JsonResolverDAO.EVALUATION_FOLLOWER_DB);

                // Only show the question if there is an approved EFL evaluation
                if (evaluations != null && evaluations.stream().noneMatch(e -> e.getEntityId() == Entity.EFL && e.getApproved())) {
                    return "DEFAULT";
                }

                // Verify if the EFL question should be shown and if should be skipped
                Pair<Boolean, Boolean> eflConfig = loanApplicationDao.getEflQuestionConfiguration(id);
                if (eflConfig == null || !eflConfig.getKey()) {
                    return "DEFAULT";
                }

                break;
        }
        return null;
    }

}

