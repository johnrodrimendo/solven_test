package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.form.Question153Form;
import com.affirm.common.model.form.Question153Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.service.*;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question153Service")
public class Question153Service extends AbstractQuestionService<Question153Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question153Form form = new Question153Form();
        switch (flowType) {
            case LOANAPPLICATION:
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question153Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                if(form.getAcept()){
                    return "DEFAULT";
                }else{
                    return "DONT_ACEPT";
                }
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question153Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question153Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.FDLM_ACEPTA_ASEGURABILIDAD.getKey(), form.getAcept());
                loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());

                // If dont acept, reject the loan
                if(!form.getAcept()){
                    loanApplicationDao.updateLoanApplicationStatus(id, LoanApplicationStatus.REJECTED, null);
                }
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
        return null;
    }

}

