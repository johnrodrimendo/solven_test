package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.catalog.Product;
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

@Service("question27Service")
public class Question27Service extends AbstractQuestionService<Question27Form> {

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
        Question27Form form = new Question27Form();
        switch (flowType) {
            case LOANAPPLICATION:

                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());

                if (fillSavedData) {

                    PersonOcupationalInformation ocupationalInformation = personService.getOcupationalInformation(loanApplication.getPersonId(), (int) params.get(VIEW_PARAM_OCUPATION_NUMBER), locale);
                    if (ocupationalInformation != null) {
                        form.setRuc(ocupationalInformation.getRuc());
                    }
                }

                HttpServletRequest request = loanApplication != null ? null : (HttpServletRequest) params.get("request");
                Integer countryId = loanApplication != null ? loanApplication.getCountryId() : countryContextService.getCountryParamsByRequest(request).getId();
                ((Question27Form.Validator) form.getValidator()).configValidator(countryId);

                ProcessQuestion currentQuestion = evaluationService.getQuestionFromEvaluationProcess(loanApplication, null, ProcessQuestion.Question.Constants.RUC_WORK_PLACE_DEPENDENT, null, null);
                if (currentQuestion.getConfiguration() != null) {
                    attributes.put("tittle", JsonUtil.getStringFromJson(currentQuestion.getConfiguration(), "tittle", null));
                }
                attributes.put("loanApplication", loanApplication);
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question27Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question27Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());

                switch (loanApplication.getCountryId()) {
                    case CountryParam.COUNTRY_ARGENTINA: {
                        if (!utilService.validarCuit(form.getRuc())) {
                            ((Question27Form.Validator) form.getValidator()).ruc.setError(messageSource.getMessage("static.message.cuit", null, locale));
                            return;
                        }
                        Question27Form.Validator val = (Question27Form.Validator) form.getValidator();
                        val.ruc.update(ValidatorUtil.CUIT);
                        val.ruc.setValidRegexErrorMsg("static.message.cuit");
                        val.ruc.setValidPatternErrorMsg("static.message.cuit");

                        break;
                    }
                    case CountryParam.COUNTRY_COLOMBIA: {
                        if (!utilService.validarNit(form.getRuc())) {
                            ((Question27Form.Validator) form.getValidator()).ruc.setError(messageSource.getMessage("static.message.nit", null, locale));
                            return;
                        }

                        Question27Form.Validator val = (Question27Form.Validator) form.getValidator();
                        val.ruc.update(ValidatorUtil.NIT);
                        val.ruc.setValidRegexErrorMsg("static.message.nit");
                        val.ruc.setValidPatternErrorMsg("static.message.nit");

                        break;
                    }
                    default:
                        break;
                }


                form.getValidator().validate(locale);

                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question27Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                registerRuc(loanApplication, form.getRuc());
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
                        String ruc = agreementEmployees.stream().filter(e -> e.getEmployer() != null && e.getEmployer().getRuc() != null).map(e -> e.getEmployer().getRuc()).findFirst().orElse(null);
                        if (ruc != null) {
                            if (saveData) {
                                registerRuc(loanApplication, ruc);
                            }
                            return "DEFAULT";
                        }
                    }
                }

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
                            registerRuc(loanApplication, employee.getEmployer().getRuc());
                        return "DEFAULT";
                    }
                }

                if (loanApplication.getCountryId().equals(CountryParam.COUNTRY_PERU)) {
                    // If its not branding from credigob
                    if (loanApplication.getEntityId() == null || loanApplication.getEntityId() != Entity.CREDIGOB) {
                        StaticDBInfo staticDBInfo = personService.getIncome(loanApplication.getPersonId());
                        if (staticDBInfo != null) {
                            if (staticDBInfo.getIncome() >= Configuration.MIN_INCOME) {
                                //register ruc
                                if (staticDBInfo.getRuc() != null) {
                                    if (saveData)
                                        registerRuc(loanApplication, staticDBInfo.getRuc());
                                    return "DEFAULT";
                                }
                            }
                        }
                    }
                }

                if ((loanApplication.getCountryId() != null && loanApplication.getCountryId() == CountryParam.COUNTRY_COLOMBIA) && (loanApplication.getEntityId() != null && loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.FUNDACION_DE_LA_MUJER)) {
                    return "DEFAULT";
                }

                break;
        }
        return null;
    }

    private void registerRuc(LoanApplication loanApplication, String ruc) throws Exception {
        personService.updateOcupationalRuc(ruc, loanApplication.getPersonId(), false, loanApplication);
    }

}

