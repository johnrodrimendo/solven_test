package com.affirm.common.service.question;

import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.form.Question21Form;
import com.affirm.common.model.form.Question86Form;
import com.affirm.common.model.transactional.LoanApplication;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question106Service")
public class Question106Service extends AbstractQuestionService<Question86Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private Question86Service question86Service;
    @Autowired
    private CreditDAO creditDao;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question21Form form = new Question21Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                if (loanApplicationDao.mustCallIovation(loanApplication.getId())) {
                    question86Service.addAttributesToExecuteIO(attributes, loanApplication.getUserId(), loanApplication.getId());
                }
                attributes.put("showMessageOffer", true);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question86Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question86Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question86Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                question86Service.saveData(flowType, id, form, locale);
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                JSONObject result = loanApplicationDao.getIovationByLoanApplication(loanApplication.getId());

                if(result != null)
                    return "DEFAULT";

                if (loanApplication.getEntityUserId() != null)
                    return "DEFAULT";

                if (!loanApplicationDao.mustCallIovation(loanApplication.getId()))
                    return "DEFAULT";
                break;
        }
        return null;
    }

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        return question86Service.customMethod(path, flowType, id, locale, params);
    }

}

