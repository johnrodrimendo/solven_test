package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.PhoneType;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.form.Question101Form;
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

@Service("question101Service")
public class Question101Service extends AbstractQuestionService<Question101Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private PersonService personService;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private CatalogService catalogService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question101Form form = new Question101Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                ((Question101Form.Validator) form.getValidator()).configValidator(loanApplication.getCountryId());

                if (fillSavedData) {
                    PersonOcupationalInformation ocupationalInformation = personService.getOcupationalInformation(loanApplication.getPersonId(), (int) params.get(VIEW_PARAM_OCUPATION_NUMBER), locale);
                    if (ocupationalInformation != null) {
                        form.setPhoneCode(ocupationalInformation.getPhoneCode());
                        form.setPhoneNumber(ocupationalInformation.getPhoneNumberWithoutCode());
                        form.setTypePhone(ocupationalInformation.getPhoneNumberType());
                    }
                }

                PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), Configuration.getDefaultLocale());
                attributes.put("phonePrefix", ocupation != null && ocupation.getAddressUbigeo() != null && ocupation.getAddressUbigeo().getDepartment() != null ? ocupation.getAddressUbigeo().getDepartment().getPhonePrefix() : null);
                attributes.put("loanApplication", loanApplication);
                attributes.put("questionId", 101);
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question101Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question101Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                ((Question101Form.Validator) form.getValidator()).configValidator(loanApplication.getCountryId());
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question101Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                registerPhoneNumber(
                        loanApplication,
                        form.getTypePhone(),
                        (form.getPhoneCode() != null && !form.getPhoneCode().isEmpty() ? "(" + form.getPhoneCode() + ") " : "") + form.getPhoneNumber());
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
                        String phoneNumber = agreementEmployees.stream().filter(e -> e.getEmployer() != null && e.getEmployer().getPhoneNumber() != null).map(e -> e.getEmployer().getPhoneNumber()).findFirst().orElse(null);
                        if (phoneNumber != null) {
                            if (saveData) {
                                registerPhoneNumber(loanApplication, PhoneType.LANDLINE, phoneNumber);
                            }
                            return "DEFAULT";
                        }
                    }
                }

                // If it comes from the entity extranet, set the phone number of the registered employer
                if (loanApplication.getEntityUserId() != null) {
                    Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
                    UserEntity userEntity = userDao.getUserEntityById(loanApplication.getEntityUserId(), locale);
                    List<Employee> employees = employeeService.getEmployeesByEmailOrDocumentByProduct(null,
                            person.getDocumentType().getId(),
                            person.getDocumentNumber(),
                            userEntity.getEntities().get(0).getId(), locale);

                    if (!employees.isEmpty()) {
                        Employee employee = employees.get(0);
                        if (saveData)
                            registerPhoneNumber(loanApplication, PhoneType.LANDLINE, employee.getEmployer().getPhoneNumber());
                        return "DEFAULT";
                    }
                }
                break;
        }
        return null;
    }

    private void registerPhoneNumber(LoanApplication loanApplication, String phoneType, String phoneNumber) throws Exception {
        PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), Configuration.getDefaultLocale());
        personDao.updateOcupatinalPhoneNumber(loanApplication.getPersonId(), phoneType, ocupation.getNumber(), phoneNumber);
    }

}

