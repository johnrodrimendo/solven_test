package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.model.form.Question151Form;
import com.affirm.common.model.form.Question27Form;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.ValidatorUtil;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service("question151Service")
public class Question151Service extends AbstractQuestionService<Question151Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private UtilService utilService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private PersonService personService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private WebscrapperService webscrapperService;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private CountryContextService countryContextService;
    @Autowired
    private EvaluationService evaluationService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question151Form form = new Question151Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());

                if (fillSavedData) {
                    PersonOcupationalInformation ocupationalInformation = personService.getOcupationalInformation(loanApplication.getPersonId(), (int) params.get(VIEW_PARAM_OCUPATION_NUMBER), locale);
                    if (ocupationalInformation != null) {
                        form.setName(ocupationalInformation.getCompanyName());
                        form.setPhoneNumber(ocupationalInformation.getPhoneNumberWithoutCode());
                        form.setTypePhone(ocupationalInformation.getPhoneNumberType());
                    }
                }

                PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), Configuration.getDefaultLocale());
                attributes.put("phonePrefix", ocupation != null && ocupation.getAddressUbigeo() != null && ocupation.getAddressUbigeo().getDepartment() != null ? ocupation.getAddressUbigeo().getDepartment().getPhonePrefix() : null);
                attributes.put("loanApplication", loanApplication);
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question151Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question151Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question151Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                registerData(
                        loanApplication,
                        form.getTypePhone(),
                        form.getPhoneNumber(),
                        form.getName());
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

    private void registerData(LoanApplication loanApplication, String phoneType, String phoneNumber, String companyName) throws Exception {
        PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), Configuration.getDefaultLocale());
        personDao.updateOcupatinalPhoneNumber(loanApplication.getPersonId(), phoneType, ocupation.getNumber(), phoneNumber);
        personDao.updateOcupationalCompany(loanApplication.getPersonId(), ocupation.getNumber(), companyName);
    }

}

