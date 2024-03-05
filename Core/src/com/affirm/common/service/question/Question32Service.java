package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.ActivityType;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.SubActivityType;
import com.affirm.common.model.form.Question32Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.PersonService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("question32Service")
public class Question32Service extends AbstractQuestionService<Question32Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private PersonService personService;
    @Autowired
    private PersonDAO personDao;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question32Form form = new Question32Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                List<SubActivityType> subActivities = catalogService.getCatalog(SubActivityType.class, locale, true, (a -> a.getActivityType().getId() == ActivityType.INDEPENDENT));
                if(loanApplication.getCountryId() == CountryParam.COUNTRY_PERU || loanApplication.getCountryId() == CountryParam.COUNTRY_COLOMBIA)
                    subActivities.removeIf(s -> s.getId() == SubActivityType.SHAREHOLDER);

                if (fillSavedData) {
                    PersonOcupationalInformation ocupationalInformation = personService.getOcupationalInformation(loanApplication.getPersonId(), (int) params.get(VIEW_PARAM_OCUPATION_NUMBER), locale);
                    if (ocupationalInformation != null) {
                        form.setSubActivity(ocupationalInformation.getSubActivityType() != null ? ocupationalInformation.getSubActivityType().getId() : null);
                    }
                }

                attributes.put("subActivities", subActivities);
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question32Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                switch (form.getSubActivity()) {
                    case SubActivityType.PROFESSIONAL_SERVICE:
                        return "PROFESSIONAL_SERVICES";
                    case SubActivityType.OWN_BUSINESS:
                        return "OWN";
                    case SubActivityType.RENT:
                        return "RENT";
                    case SubActivityType.SHAREHOLDER:
                        return "SHAREHOLDER";
                }
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question32Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question32Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);

                // Clean the ocupational activity
                Date startDate = ocupation.getStartDate();
                String employmentTime = ocupation.getEmploymentTime();
                personDao.updateOcupationalActivityType(loanApplication.getPersonId(), ocupation.getNumber(), ocupation.getActivityType().getId());
                personDao.updateOcupatinalSubActivityType(loanApplication.getPersonId(), ocupation.getNumber(), form.getSubActivity());
                if (startDate != null)
                    personDao.updateOcupatinalStartDate(loanApplication.getPersonId(), ocupation.getNumber(), startDate);
                if (employmentTime != null)
                    personDao.updateEmploymentTime(loanApplication.getPersonId(), ocupation.getNumber(), employmentTime);
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

