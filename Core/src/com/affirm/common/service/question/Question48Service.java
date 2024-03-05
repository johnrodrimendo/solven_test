package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.ActivityType;
import com.affirm.common.model.form.Question48Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.service.PersonService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question48Service")
public class Question48Service extends AbstractQuestionService<Question48Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private PersonService personService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question48Form form = new Question48Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);

                if (fillSavedData) {
                    PersonOcupationalInformation ocupationalInformation = personService.getOcupationalInformation(loanApplication.getPersonId(), (int) params.get(VIEW_PARAM_OCUPATION_NUMBER), locale);
                    if (ocupationalInformation != null) {
                        form.setActivity(ocupationalInformation.getActivityType() != null ? ocupationalInformation.getActivityType().getId() : null);
                    }
                }

                attributes.put("loanApplication", loanApplication);
                attributes.put("actualYear", Calendar.getInstance().get(Calendar.YEAR));
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question48Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                switch (form.getActivity()) {
                    case ActivityType.DEPENDENT:
                        return "DEPENDENT";
                    case ActivityType.INDEPENDENT:
                        return "INDEPENDENT";
                    case ActivityType.MONOTRIBUTISTA:
                        return "MONOTRIBUTISTA";
                    case ActivityType.PENSIONER:
                        return "PENSIONER";
                }
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question48Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question48Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                PersonOcupationalInformation currentOcupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);
                personDao.cleanOcupationalInformation(loanApplication.getPersonId(), currentOcupation.getNumber() + 1);
                personDao.updateOcupationalActivityType(loanApplication.getPersonId(), currentOcupation.getNumber() + 1, form.getActivity());
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

