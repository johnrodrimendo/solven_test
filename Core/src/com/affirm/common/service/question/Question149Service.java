package com.affirm.common.service.question;

import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.form.Question149Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.service.PersonService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question149Service")
public class Question149Service extends AbstractQuestionService<Question149Form> {

    private final PersonDAO personDAO;

    private final LoanApplicationService loanApplicationService;
    private final PersonService personService;

    private Map<String, String> kinds;

    public Question149Service(PersonDAO personDAO, LoanApplicationService loanApplicationService, PersonService personService) {
        this.personDAO = personDAO;

        this.loanApplicationService = loanApplicationService;
        this.personService = personService;

        kinds = new HashMap<>();
        kinds.put("F", "Fijo");
        kinds.put("I", "Indefinido");
    }

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        Question149Form form = new Question149Form();

        switch (flowType) {
            case LOANAPPLICATION: {
                attributes.put("kinds", kinds);
                attributes.put("form", form);

                break;
            }
        }

        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question149Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }

        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question149Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question149Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = this.loanApplicationService.getLoanApplicationById(id);
                PersonOcupationalInformation occupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);

                personDAO.updateContractType(loanApplication.getPersonId(), occupation.getNumber(), form.getKind().charAt(0));

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

    public String getContractKindByKey(String key) {
        return kinds.get(key);
    }
}
