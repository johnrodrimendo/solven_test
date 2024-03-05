package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.form.Question88Form;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EmployeeService;
import com.affirm.common.service.PersonService;
import com.affirm.common.util.FormValidationException;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("question88Service")
public class Question88Service extends AbstractQuestionService<Question88Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonService personService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private EmployeeService employeeService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question88Form form = new Question88Form();
        switch (flowType) {
            case LOANAPPLICATION:

                if (fillSavedData) {
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                    PersonOcupationalInformation ocupationalInformation = personService.getOcupationalInformation(loanApplication.getPersonId(), (int) params.get(VIEW_PARAM_OCUPATION_NUMBER), locale);
                    if (ocupationalInformation != null && ocupationalInformation.getStartDate() != null) {
                        form.setStartDate(new SimpleDateFormat("MM/yyyy").format(ocupationalInformation.getStartDate()));
                    }
                }

                attributes.put("yearFrom", Calendar.getInstance().get(Calendar.YEAR) - 50);
                attributes.put("yearTo", Calendar.getInstance().get(Calendar.YEAR));
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question88Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question88Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                if (!form.getValidator().isHasErrors()) {
                    if (form.getStartDate() != null && !form.getStartDate().isEmpty()) {
                        Date startDate = new SimpleDateFormat("MM/yyyy").parse(form.getStartDate());
                        if (startDate.after(new Date())) {
                            throw new FormValidationException("La fecha es inválida");
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question88Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);
                personService.updateOcupationalStartDate(loanApplication.getPersonId(), ocupation.getNumber(), form.getStartDate() != null ? new SimpleDateFormat("MM/yyyy").parse(form.getStartDate()) : null);
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
                        Date startDate = agreementEmployees.stream().filter(e -> e.getEmploymentStartDateDate() != null).map(e -> e.getEmploymentStartDateDate()).findFirst().orElse(null);
                        if (startDate != null) {
                            if (saveData) {
                                PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);
                                personService.updateOcupationalStartDate(loanApplication.getPersonId(), ocupation.getNumber(), startDate);
                            }
                            return "DEFAULT";
                        }
                    }
                }

                Date essaludStartDate = personDao.getExternalEssaludStartDate(loanApplication.getPersonId());
                if (essaludStartDate != null) {
                    if (saveData) {
                        PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);
                        personService.updateOcupationalStartDate(loanApplication.getPersonId(), ocupation.getNumber(), essaludStartDate);
                    }
                    return "DEFAULT";
                }

                if (loanApplication.getCountryId().equals(CountryParam.COUNTRY_PERU)) {
                    StaticDBInfo staticDBInfo = personService.getIncome(loanApplication.getPersonId());
                    if (staticDBInfo != null) {
                        if (staticDBInfo.getIncome() >= Configuration.MIN_INCOME) {
                            //register start date
                            if (staticDBInfo.getInicio() != null) {
                                String[] startDate = staticDBInfo.getInicio().split("-");
                                String sd = startDate[1] + "/" + startDate[0];
                                if (saveData) {
                                    PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);
                                    personService.updateOcupationalStartDate(loanApplication.getPersonId(), ocupation.getNumber(), staticDBInfo.getInicio() != null ? new SimpleDateFormat("MM/yyyy").parse(sd) : null);
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

