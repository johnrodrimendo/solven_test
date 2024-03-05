package com.affirm.common.service.question;

import com.affirm.acceso.model.Direccion;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.AddressForm;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanApplicationPreliminaryEvaluation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.CountryContextService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.UserService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.ResponseEntityException;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("question22Service")
public class Question22Service extends AbstractQuestionService<AddressForm> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CountryContextService countryContextService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UserService userService;
    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    Question82Service question82Service;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        AddressForm form = new AddressForm();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);

                ((AddressForm.Validator) form.getValidator()).configValidator(loanApplication.getCountryId(), null);

                boolean hasBrowserLocation = !(loanApplication.getNavLatitude() == null || loanApplication.getNavLongitude() == null);
                attributes.put("browserLocation", hasBrowserLocation);
                if (!hasBrowserLocation) {
                    loanApplicationDao.updateConsentNavGeolocation(loanApplication.getId(), false);
                }

                boolean enableProvinceValidation = false;

                if((loanApplication.getEntityId() != null && loanApplication.getEntityId().equals(Entity.BANCO_DEL_SOL)) || (loanApplication.getSelectedEntityId() != null && loanApplication.getSelectedEntityId().equals(Entity.BANCO_DEL_SOL))){
                    enableProvinceValidation = true;
                }

                if (fillSavedData || (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.BANCO_DEL_SOL)) {
                    if (loanApplication.getCountryId().equals(CountryParam.COUNTRY_PERU)) {
                        form.fillData(personDao.getPersonContactInformation(locale, loanApplication.getPersonId()));
                    } else if (loanApplication.getCountryId().equals(CountryParam.COUNTRY_ARGENTINA)) {
                        form.fillData(personDao.getDisggregatedAddress(loanApplication.getPersonId(), "H"));
                    }
                }

                List<Department> allDepartments = new ArrayList<>();
                List<Department> topDepartments = new ArrayList<>();

                if (CountryParam.COUNTRY_PERU == loanApplication.getCountryId()) {

                    allDepartments = catalogService.getDepartments();
                    topDepartments = allDepartments.stream().filter(d -> Configuration.orderedTopDepartments.contains(d.getId())).collect(Collectors.toList());
                    for (int i = 0; i < Configuration.orderedTopDepartments.size(); i++) {
                        Collections.swap(topDepartments, Configuration.orderedTopDepartments.indexOf(topDepartments.get(i).getId()), i);
                    }
                    allDepartments.removeAll(topDepartments);

                } else if (CountryParam.COUNTRY_COLOMBIA == loanApplication.getCountryId()) {
                    allDepartments = catalogService.getGeneralDepartment(loanApplication.getCountryId());
                }

                ProcessQuestion currentQuestion = evaluationService.getQuestionFromEvaluationProcess(loanApplication, null, ProcessQuestion.Question.Constants.HOME_ADDRESS, null, null);
                if (currentQuestion.getConfiguration() != null) {
                    attributes.put("enableNavigatorLocation", JsonUtil.getBooleanFromJson(currentQuestion.getConfiguration(), "enableNavigatorLocation", false));
                    attributes.put("showReference", JsonUtil.getBooleanFromJson(currentQuestion.getConfiguration(), "showReference", false));
                    attributes.put("browserLocation", false);
                    attributes.put("tittle", JsonUtil.getStringFromJson(currentQuestion.getConfiguration(), "tittle", null));
                }

                JSONObject jsonAddress = question82Service.getJsonHomeAddress(loanApplication, locale);

                attributes.put("loanApplication", loanApplication);
                attributes.put("questionId", 22);
                attributes.put("jsonLocationDetails",jsonAddress != null && !jsonAddress.keySet().isEmpty() ? jsonAddress.toString() : null);
                attributes.put("form", form);
                attributes.put("topDepartments", topDepartments);
                attributes.put("allDepartments", allDepartments);
                attributes.put("enableProvinceValidation", enableProvinceValidation);
                attributes.put("person", personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false));
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, AddressForm form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                List<LoanApplicationPreliminaryEvaluation> preEvaluations = loanApplicationDao.getPreliminaryEvaluations(id, Configuration.getDefaultLocale(), JsonResolverDAO.EVALUATION_FOLLOWER_DB);
                if (preEvaluations.stream().anyMatch(preEval -> preEval.getEntityId() == Entity.CAJA_LOS_ANDES)) {
                    return "RESIDENCE_TIME";
                }

                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, AddressForm form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                if (form.getZoneType() != null && form.getZoneType().equals("-1"))
                    form.setZoneType(null);

                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                ((AddressForm.Validator) form.getValidator()).configValidator(loanApplication.getCountryId(), form.getWithoutNumber());
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, AddressForm form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if (loanApplication.getCountryId().equals(CountryParam.COUNTRY_PERU)) {
                    personDao.updateAddressInformation(
                            loanApplication.getPersonId(),
                            form.getDepartamento(), form.getProvincia(), form.getDistrito(),
                            form.getRoadType() != null ? Integer.valueOf(form.getRoadType()) : null,
                            form.getRoadName(),
                            form.getHouseNumber(), form.getInterior(), form.getReference(),
                            countryContextService.getCountryDefaultMapLatitude(CountryParam.COUNTRY_PERU),
                            countryContextService.getCountryDefaultMapLongitude(CountryParam.COUNTRY_PERU));
                    personDao.deleteDisaggregatedAddress(loanApplication.getPersonId());
                } else if (loanApplication.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {

                    String currentProvince = JsonUtil.getStringFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_PROVINCIA_RETIRO.getKey(), null);
                    if(currentProvince != null && form.getProvincia() != null && !currentProvince.equalsIgnoreCase(form.getProvincia())) throw new ResponseEntityException(AjaxResponse.errorMessage("La Provincia ingresada es distinta a la evaluación, se debe recalcular la oferta"));

                    personDao.updateAddressInformation(
                            loanApplication.getPersonId(),
                            form.getDepartamento(), form.getProvincia(), form.getDistrito(), form.getRoadType() != null ? Integer.valueOf(form.getRoadType()) : null,
                            (form.getRoadType() != null ? catalogService.getAvenuesById(Integer.valueOf(form.getRoadType())).getName() : "") + " " + form.getRoadName() + " " + (form.getHouseNumber() != null ? form.getHouseNumber() : "") + " " + (form.getWithoutNumber() != null && form.getWithoutNumber() ? "S/N" : "") + " " + " " + (form.getInterior() != null ? "Dep. " + form.getInterior() : "") + " " +
                                    (form.getFloor() != null ? " Piso: " + form.getFloor() : null) + " " + (form.getZoneType() != null ? catalogService.getAreaTypeById(Integer.valueOf(form.getZoneType())).getName() : "") + " " + (form.getZoneName() != null ? form.getZoneName() : "") + " " + (form.getReference() != null ? "Ref.: " + form.getReference() : null) + " " + (form.getPostalCode() != null ? "Cod. Postal: " + form.getPostalCode() : null),
                            form.getHouseNumber(), form.getInterior(), form.getReference(),
                            countryContextService.getCountryDefaultMapLatitude(CountryParam.COUNTRY_ARGENTINA),
                            countryContextService.getCountryDefaultMapLongitude(CountryParam.COUNTRY_ARGENTINA));

                    personDao.registerDisgregatedAddress(loanApplication.getPersonId(),
                            new Direccion(catalogService, "H", form.getRoadType(), form.getRoadName(), form.getHouseNumber(), form.getInterior(), form.getManzana(), form.getLote(), null, form.getReference(), form.getZoneType(), form.getZoneName(), Long.valueOf(form.getDistrito()), form.getFloor(), form.getWithoutNumber() != null ? Boolean.valueOf(form.getWithoutNumber()) : null, form.getPostalCode()));
                } else if (CountryParam.COUNTRY_COLOMBIA == loanApplication.getCountryId()) {
                    personDao.updateAddressInformation(
                            loanApplication.getPersonId(),
                            form.getDepartamento(), form.getProvincia(), form.getDistrito(), form.getRoadType() != null ? Integer.valueOf(form.getRoadType()) : null,
                            form.getRoadName(),
                            form.getHouseNumber(), form.getInterior(), form.getReference(),
                            countryContextService.getCountryDefaultMapLatitude(CountryParam.COUNTRY_COLOMBIA),
                            countryContextService.getCountryDefaultMapLongitude(CountryParam.COUNTRY_COLOMBIA));
                }
                break;
        }
    }

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = null;
                AddressForm form = null;
                switch (path) {
                    case "navlocation":
                        loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        userService.registerBrowserUbication(loanApplication.getId(), (Double) params.get("latitude"), (Double) params.get("longitude"));
                        return AjaxResponse.ok(null);
                    case "province":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getProvinces((String) params.get("departmentId"))));
                    case "provinceByPostalCode":
                        List<Province> provinceList = catalogService.getGeneralProvinceByPostalCode(CountryParam.COUNTRY_ARGENTINA, (String) params.get("postalCode"));
                        if (provinceList.size() > 0)
                            return AjaxResponse.ok(new Gson().toJson(provinceList));
                        return AjaxResponse.errorMessage("El código postal es incorrecto");
                    case "generalDistrict":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getGeneralDistrictByProvincePostalCode((Integer) params.get("provinceId"), (String) params.get("postalCode"))));
                    case "district":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getDistricts((String) params.get("departmentId"), (String) params.get("provinceId"))));
                    case "generalProvince":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getGeneralProvinces(Integer.parseInt(params.get("departmentId").toString()))));
                    case "districtsByProvienceId":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getDistrictsByProvinceId(Integer.parseInt(params.get("provinceId").toString()))));
                    case "provinceValidation":
                        loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        boolean isValid = true;
                        if((loanApplication.getEntityId() != null && loanApplication.getEntityId().equals(Entity.BANCO_DEL_SOL)) || (loanApplication.getSelectedEntityId() != null && loanApplication.getSelectedEntityId().equals(Entity.BANCO_DEL_SOL))){
                            form = (AddressForm) params.get("form");
                            String currentProvince = JsonUtil.getStringFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_PROVINCIA_RETIRO.getKey(), null);
                            if(currentProvince != null && form.getProvincia() != null && !currentProvince.equalsIgnoreCase(form.getProvincia())) isValid = false;
                        }
                        JSONObject provinceValidationJSON = new JSONObject();
                        provinceValidationJSON.put("isValid", isValid);
                        return AjaxResponse.ok(provinceValidationJSON.toString());
                    case "returnToOffers":
                        loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        if((loanApplication.getEntityId() != null && loanApplication.getEntityId().equals(Entity.BANCO_DEL_SOL)) || (loanApplication.getSelectedEntityId() != null && loanApplication.getSelectedEntityId().equals(Entity.BANCO_DEL_SOL))){
                            form = (AddressForm) params.get("form");
                            personDao.updateAddressInformation(
                                    loanApplication.getPersonId(),
                                    form.getDepartamento(), form.getProvincia(), form.getDistrito(), form.getRoadType() != null ? Integer.valueOf(form.getRoadType()) : null,
                                    (form.getRoadType() != null ? catalogService.getAvenuesById(Integer.valueOf(form.getRoadType())).getName() : "") + " " + form.getRoadName() + " " + (form.getHouseNumber() != null ? form.getHouseNumber() : "") + " " + (form.getWithoutNumber() != null && form.getWithoutNumber() ? "S/N" : "") + " " + " " + (form.getInterior() != null ? "Dep. " + form.getInterior() : "") + " " +
                                            (form.getFloor() != null ? " Piso: " + form.getFloor() : null) + " " + (form.getZoneType() != null ? catalogService.getAreaTypeById(Integer.valueOf(form.getZoneType())).getName() : "") + " " + (form.getZoneName() != null ? form.getZoneName() : "") + " " + (form.getReference() != null ? "Ref.: " + form.getReference() : null) + " " + (form.getPostalCode() != null ? "Cod. Postal: " + form.getPostalCode() : null),
                                    form.getHouseNumber(), form.getInterior(), form.getReference(),
                                    countryContextService.getCountryDefaultMapLatitude(CountryParam.COUNTRY_ARGENTINA),
                                    countryContextService.getCountryDefaultMapLongitude(CountryParam.COUNTRY_ARGENTINA));

                            personDao.registerDisgregatedAddress(loanApplication.getPersonId(),
                                    new Direccion(catalogService, "H", form.getRoadType(), form.getRoadName(), form.getHouseNumber(), form.getInterior(), form.getManzana(), form.getLote(), null, form.getReference(), form.getZoneType(), form.getZoneName(), Long.valueOf(form.getDistrito()), form.getFloor(), form.getWithoutNumber() != null ? Boolean.valueOf(form.getWithoutNumber()) : null, form.getPostalCode()));
                            loanApplicationDao.updateEntityCustomData(loanApplication.getId(),
                                    loanApplication.getEntityCustomData()
                                            .put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_PROVINCIA_RETIRO.getKey(), form.getProvincia())
                            );
                            loanApplicationDao.removeLoanOffers(loanApplication.getId());
                            loanApplicationDao.updateCurrentQuestion(loanApplication.getId(),ProcessQuestion.Question.Constants.OFFER);
                            return AjaxResponse.ok(null);
                        }
                        else return AjaxResponse.errorMessage("No puede realizar esta acción");
                }
                break;
        }
        throw new Exception("No method configured");
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

