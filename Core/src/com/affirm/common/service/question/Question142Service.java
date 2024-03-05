package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.Department;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.catalog.Province;
import com.affirm.common.model.form.Question142Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.PersonContactInformation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.CountryContextService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.UserService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("question142Service")
public class Question142Service extends AbstractQuestionService<Question142Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CountryContextService countryContextService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private Question82Service question82Service;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private UserService userService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question142Form form = new Question142Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                JSONObject jsonAddress = question82Service.getJsonHomeAddress(loanApplication, locale);
                boolean hasBrowserLocation = !(loanApplication.getNavLatitude() == null || loanApplication.getNavLongitude() == null);

                List<Department> allDepartments = new ArrayList<>();
                List<Department> topDepartments = new ArrayList<>();

                if (!hasBrowserLocation) {
                    loanApplicationDao.updateConsentNavGeolocation(loanApplication.getId(), false);
                }

                if (fillSavedData) {
                    PersonContactInformation contactInformation = personDao.getPersonContactInformation(locale, loanApplication.getPersonId());
                    if (contactInformation != null) {
                        form.setRoadName(contactInformation.getAddressStreetName());
                        form.setDepartamento(contactInformation.getAddressUbigeo() != null ? contactInformation.getAddressUbigeo().getDepartment().getId() : null);
                        form.setProvincia(contactInformation.getAddressUbigeo() != null ? contactInformation.getAddressUbigeo().getProvince().getId() : null);
                        form.setDistrito(contactInformation.getAddressUbigeo() != null ? contactInformation.getAddressUbigeo().getDistrict().getId() : null);
                        form.setHousingType(contactInformation.getHousingType() != null ? contactInformation.getHousingType().getId() : null);
                    }
                }

                switch (loanApplication.getCountryId()) {
                    case CountryParam.COUNTRY_PERU: {
                        allDepartments = catalogService.getDepartments();
                        topDepartments = allDepartments.stream().filter(d -> Configuration.orderedTopDepartments.contains(d.getId())).collect(Collectors.toList());

                        for (int i = 0; i < Configuration.orderedTopDepartments.size(); i++) {
                            Collections.swap(topDepartments, Configuration.orderedTopDepartments.indexOf(topDepartments.get(i).getId()), i);
                        }

                        allDepartments.removeAll(topDepartments);

                        break;
                    }
                    case CountryParam.COUNTRY_COLOMBIA: {
                        allDepartments = catalogService.getGeneralDepartment(loanApplication.getCountryId());
                    }
                    default:
                        break;
                }

                ProcessQuestion currentQuestion = evaluationService.getQuestionFromEvaluationProcess(loanApplication, null, ProcessQuestion.Question.Constants.HOME_ADDRESS_AND_TYPE, null, null);
                if (currentQuestion.getConfiguration() != null) {
                    Boolean showReference = JsonUtil.getBooleanFromJson(currentQuestion.getConfiguration(), "showReference", false);
                    attributes.put("showReference", showReference);
                    if(showReference)  ((Question142Form.Validator) form.getValidator()).reference.setRequired(true);
                    attributes.put("enableNavigatorLocation", JsonUtil.getBooleanFromJson(currentQuestion.getConfiguration(), "enableNavigatorLocation", false));
                    attributes.put("browserLocation", false);
                }

                List<Integer> houseTypeIds = evaluationService.getIdsCatalogFromConfigurationInProcessQuestion(id, ProcessQuestion.Question.Constants.HOME_ADDRESS_AND_TYPE, "houseTypeIds");

                ((Question142Form.Validator) form.getValidator()).configValidator(loanApplication.getCountryId());

                attributes.put("houseTypes", catalogService.getHousingTypes(locale).stream().filter(h -> houseTypeIds.contains(h.getId())).collect(Collectors.toList()));
                attributes.put("loanApplication", loanApplication);
                attributes.put("browserLocation", hasBrowserLocation);
                attributes.put("jsonLocationDetails", jsonAddress != null && !jsonAddress.keySet().isEmpty() ? jsonAddress.toString() : null);
                attributes.put("form", form);
                attributes.put("topDepartments", topDepartments);
                attributes.put("allDepartments", allDepartments);
                attributes.put("person", personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false));

                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question142Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question142Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                ((Question142Form.Validator) form.getValidator()).configValidator(loanApplication.getCountryId());
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question142Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());

                if (CountryParam.COUNTRY_PERU == loanApplication.getCountryId()) {
                    personDao.updateAddressInformation(
                            loanApplication.getPersonId(),
                            form.getDepartamento(), form.getProvincia(), form.getDistrito(),
                            null,
                            form.getRoadName(),
                            null, null, form.getReference(),
                            countryContextService.getCountryDefaultMapLatitude(CountryParam.COUNTRY_PERU),
                            countryContextService.getCountryDefaultMapLongitude(CountryParam.COUNTRY_PERU));
                    personDao.updateHousingType(loanApplication.getPersonId(), form.getHousingType());

                } else if (CountryParam.COUNTRY_COLOMBIA == loanApplication.getCountryId()) {
                    personDao.updateAddressInformation(
                            loanApplication.getPersonId(),
                            form.getDistrito(),
                            null,
                            form.getRoadName(),
                            null, null, null,
                            countryContextService.getCountryDefaultMapLatitude(CountryParam.COUNTRY_COLOMBIA),
                            countryContextService.getCountryDefaultMapLongitude(CountryParam.COUNTRY_COLOMBIA));
                    personDao.updateHousingType(loanApplication.getPersonId(), form.getHousingType());
                }
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

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                switch (path) {
                    case "navlocation":
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        userService.registerBrowserUbication(loanApplication.getId(), (Double) params.get("latitude"), (Double) params.get("longitude"));
                        return AjaxResponse.ok(null);
                }
                break;
        }
        throw new Exception("No method configured");
    }

}

