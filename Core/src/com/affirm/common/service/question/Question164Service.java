package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.form.Question164Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.User;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.SqlErrorMessageException;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question164Service")
public class Question164Service extends AbstractQuestionService<Question164Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private EvaluationService evaluationService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question164Form form = new Question164Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if (fillSavedData) {
                    User user = userDAO.getUser(loanApplication.getUserId());
                    form.setEmail(user.getEmail());
                    form.setEmail(user.getPhoneNumber());
                }

                attributes.put("loanApplication", loanApplication);
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question164Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question164Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question164Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                // Phone number
                String phoneNUmber = form.getPhoneNumber();
                userDAO.registerPhoneNumber(loanApplication.getUserId(), loanApplication.getCountry().getId() + "", phoneNUmber);
                loanApplicationDao.updateSmsSent(loanApplication.getId(), false);
                // Email
                createEmailPassword(form.getEmail(), loanApplication.getUserId());
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                ProcessQuestion currentQuestion = evaluationService.getQuestionFromEvaluationProcess(loanApplication, null, ProcessQuestion.Question.Constants.EMAIL_AND_CELLPHONE, null, null);
                Boolean dontSkip = null;
                if (currentQuestion.getConfiguration() != null) {
                    dontSkip = JsonUtil.getBooleanFromJson(currentQuestion.getConfiguration(), "donntSkip", false);
                }
                if(dontSkip == null || !dontSkip){
                    User user = userDAO.getUser(loanApplication.getUserId());
                    if(user != null && user.getEmail() != null && user.getPhoneNumber() != null)
                        return "DEFAULT";
                }

                break;
        }
        return null;
    }

    public void createEmailPassword(String email, Integer userId) throws Exception {
        User user = userDAO.getUser(userId);
        if (user.getEmail() != null && !user.getEmail().equalsIgnoreCase(email)) {
            throw new SqlErrorMessageException(null, "El  email no coincide con el registrado");
        } else if (user.getEmail() == null) {
            int emailId = userDAO.registerEmailChange(userId, email.toLowerCase());
            userDAO.validateEmailChange(userId, emailId);
        }
    }
}

