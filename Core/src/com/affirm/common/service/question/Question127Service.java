package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.form.Question127Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question127Service")
public class Question127Service extends AbstractQuestionService<Question127Form> {

    private LoanApplicationDAO loanApplicationDao;

    @Autowired
    public Question127Service(LoanApplicationDAO loanApplicationDao) {
        this.loanApplicationDao = loanApplicationDao;
    }

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {
        HashMap<String, Object> attributes = new HashMap<>();
        Question127Form form = new Question127Form();
        attributes.put("form", form);
        return attributes;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question127Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question127Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question127Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANK_ACCOUNT_STATEMENT.getKey(), form.getBankAccountStatement());
                loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                Boolean bankAccountStatement = JsonUtil.getBooleanFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANK_ACCOUNT_STATEMENT.getKey(), null);
                if (bankAccountStatement != null) {
                    return "DEFAULT";
                }
                break;
        }
        return null;
    }
}
