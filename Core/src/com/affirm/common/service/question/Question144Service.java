package com.affirm.common.service.question;

import com.affirm.banbif.model.AccountOpeningForm;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.ApplicationRejectionReason;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.User;
import com.affirm.common.service.UserService;
import com.affirm.common.util.ProcessQuestionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question144Service")
public class Question144Service extends AbstractQuestionService<AccountOpeningForm> {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoanApplicationDAO loanApplicationDAO;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:

                LoanApplication loanApplication = loanApplicationDAO.getLoanApplicationLite(id, locale);
                User user = userService.getUser(userDAO.getUserIdByPersonId(loanApplication.getPersonId()));

                attributes.put("user", user);
                attributes.put("loan", loanApplication);
                attributes.put("form", new AccountOpeningForm());

                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, AccountOpeningForm form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, AccountOpeningForm form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, AccountOpeningForm form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        return null;
    }

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                switch (path) {
                    case "reject":
                        loanApplicationDAO.updateLoanApplicationStatus(id, LoanApplicationStatus.REJECTED, null);
                        loanApplicationDAO.registerRejectionWithComment(id, ApplicationRejectionReason.USER_CANCELATION, null);

                        return ProcessQuestionResponse.goToQuestion(ProcessQuestion.Question.Constants.BANBIF_ACCOUNT_OPENING);
                }
                break;
        }
        throw new Exception("No method configured");
    }
}
