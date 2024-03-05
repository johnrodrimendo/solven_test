package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.form.Question156Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.service.PrestamypeService;
import com.affirm.common.util.FormGeneric;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question157Service")
public class Question157Service extends AbstractQuestionService<FormGeneric> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    PrestamypeService prestamypeService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {
        Map<String, Object> attributes = new HashMap<>();

        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                boolean isApproved = prestamypeService.isApproved(loanApplication);
                if(isApproved){
                    if(loanApplication.getStatus().getId() != LoanApplicationStatus.LEAD_REFERRED){
                        loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.LEAD_REFERRED, null);
                    }
                    if(loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.PRESTAMYPE_LEAD_EMAIL_SENT.getKey()) == null){
                        prestamypeService.sendLeadEmail(loanApplication);
                        JSONObject data = new JSONObject(loanApplication.getEntityCustomData());
                        data.put(LoanApplication.EntityCustomDataKeys.PRESTAMYPE_LEAD_EMAIL_SENT.getKey(), "true");
                        loanApplicationDao.updateEntityCustomData(loanApplication.getId(), data);
                    }
                }
                if(!isApproved) {
                    if (loanApplication.getStatus().getId() != LoanApplicationStatus.REJECTED_AUTOMATIC) {
                        loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.REJECTED_AUTOMATIC, null);
                    }
                }
                attributes.put("isApproved", prestamypeService.isApproved(loanApplication));
                attributes.put("showGoBack", false);
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
                form.getValidator().validate(locale);
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
                break;
        }
        return null;
    }
}