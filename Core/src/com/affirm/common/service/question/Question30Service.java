package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.form.Question30Form;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EmployeeService;
import com.affirm.common.service.PersonService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service("question30Service")
public class Question30Service extends AbstractQuestionService<Question30Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private PersonService personService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private EmployeeService employeeService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question30Form form = new Question30Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());

                if (fillSavedData) {
                    PersonOcupationalInformation ocupationalInformation = personService.getOcupationalInformation(loanApplication.getPersonId(), (int) params.get(VIEW_PARAM_OCUPATION_NUMBER), locale);
                    if (ocupationalInformation != null) {
                        form.setIncome(ocupationalInformation.getFixedGrossIncome() != null ? ocupationalInformation.getFixedGrossIncome().intValue() : null);
                    }
                }

                ((Question30Form.Validator) form.getValidator()).configValidator(loanApplication.getCountryId());

                attributes.put("loanApplication", loanApplication);
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question30Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                if (form.getVariableIncome()) {
                    return "VARIABLE_INCOME";
                } else {
                    return "DEFAULT";
                }
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question30Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                ((Question30Form.Validator) form.getValidator()).configValidator(loanApplication.getCountryId());
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question30Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);
                personDao.updateFixedGrossIncome(loanApplication.getPersonId(), ocupation.getNumber(), form.getIncome());
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());

                // If is branding ABACO and the person is a Agreement Employee, then get the date of the paysheet and skip
                if (loanApplication.getEntityId() != null && loanApplication.getEntityId().equals(Entity.ABACO)) {
                    Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
                    List<Employee> agreementEmployees = employeeService.getEmployeesByEmailOrDocumentByEntityProduct(null, person.getDocumentType().getId(), person.getDocumentNumber(), Entity.ABACO, Product.AGREEMENT, Configuration.getDefaultLocale());
                    if (agreementEmployees != null) {
                        Double fixedGrossIncome = agreementEmployees.stream().filter(e -> e.getFixedGrossIncome() != null).map(e -> e.getFixedGrossIncome()).findFirst().orElse(null);
                        if (fixedGrossIncome != null) {
                            if (saveData) {
                                PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);
                                personDao.updateFixedGrossIncome(loanApplication.getPersonId(), ocupation.getNumber(), fixedGrossIncome.intValue());
                            }
                            return "DEFAULT";
                        }
                    }
                }

                if (loanApplication.getCountryId().equals(CountryParam.COUNTRY_PERU)) {
                    StaticDBInfo staticDBInfo = personService.getIncome(loanApplication.getPersonId());
                    if (staticDBInfo != null) {
                        if (staticDBInfo.getIncome() >= Configuration.MIN_INCOME) {
                            //register income
                            if (staticDBInfo.getIncome() != null) {
                                if(saveData){
                                    PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);
                                    personDao.updateFixedGrossIncome(loanApplication.getPersonId(), ocupation.getNumber(), staticDBInfo.getIncome().intValue());
                                    personDao.updateOcupationalCompany(loanApplication.getPersonId(), ocupation.getNumber(), staticDBInfo.getRazonSocial());
                                }
                                return "DEFAULT";
                            }
                        }
                    }
                }
                break;
        }
        return null;
    }

}

