package com.affirm.common.service.question;

import com.affirm.acceso.model.Direccion;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.AddressBanBifForm;
import com.affirm.common.model.form.Question108Form;
import com.affirm.common.model.form.Question142Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.StaticDBInfo;
import com.affirm.common.service.*;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.JsonUtil;
import com.affirm.geocoding.model.GeocodingResult;
import com.affirm.geocoding.service.GeocodingService;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("question154Service")
public class Question154Service extends AbstractQuestionService<AddressBanBifForm> {

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
    private Question82Service question82Service;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private GeocodingService geocodingService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        AddressBanBifForm form = new AddressBanBifForm();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);

                ((AddressBanBifForm.Validator) form.getValidator()).configValidatorByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),'H', null);

                List<Department> allDepartments = catalogService.getDepartments();
                List<Department> topDepartments = allDepartments.stream().filter(d -> Configuration.orderedTopDepartments.contains(d.getId())).collect(Collectors.toList());
                for (int i = 0; i < Configuration.orderedTopDepartments.size(); i++) {
                    Collections.swap(topDepartments, Configuration.orderedTopDepartments.indexOf(topDepartments.get(i).getId()), i);
                }
                allDepartments.removeAll(topDepartments);

                Direccion disagregatedAddress = personDao.getDisggregatedAddress(loanApplication.getPersonId(), "H");

                if (disagregatedAddress != null) {
                    Ubigeo ubigeo = catalogService.getUbigeo(disagregatedAddress.getUbigeo());
                    String department = ubigeo.getDepartment().getId();
                    String province = ubigeo.getProvince().getId();
                    String district = ubigeo.getDistrict().getId();

                    form.setDepartamento(department);
                    form.setProvincia(province);
                    form.setDistrito(district);
                    form.setRoadType(disagregatedAddress.getTipoVia() + "");
                    form.setRoadName(disagregatedAddress.getNombreVia());
                    form.setHouseNumber(disagregatedAddress.getNumeroVia());
                    form.setInterior(disagregatedAddress.getNumeroInterior());
                    form.setReference(disagregatedAddress.getReferencia());
                    form.setZoneType(disagregatedAddress.getTipoZona() + "");
                    form.setZoneName(disagregatedAddress.getNombreZona());
                }

                ProcessQuestion currentQuestion = evaluationService.getQuestionFromEvaluationProcess(loanApplication, null, ProcessQuestion.Question.Constants.HOME_ADDRESS_DISGREGATED, null, null);
                if (currentQuestion.getConfiguration() != null) {
                    Boolean showReference = JsonUtil.getBooleanFromJson(currentQuestion.getConfiguration(), "showReference", false);
                    attributes.put("enableNavigatorLocation", JsonUtil.getBooleanFromJson(currentQuestion.getConfiguration(), "enableNavigatorLocation", false));
                    attributes.put("browserLocation", false);
                }

                attributes.put("showTermsAndConditions", false);
                if((loanApplication.getEntityId() != null && loanApplication.getEntityId().equals(Entity.BANBIF)) || (loanApplication.getSelectedEntityId() != null && loanApplication.getSelectedEntityId().equals(Entity.BANBIF))){
                    attributes.put("tittle", "Domicilio vivienda");
                }
                else attributes.put("tittle", "¿Cuál es tu dirección de vivienda?");


                JSONObject jsonAddress = getJsonHomeAddress(loanApplication, locale);
                attributes.put("loanApplication", loanApplication);
                attributes.put("jsonLocationDetails",jsonAddress != null && !jsonAddress.keySet().isEmpty() ? jsonAddress.toString() : null);
                attributes.put("form", form);
                attributes.put("topDepartments", topDepartments);
                attributes.put("allDepartments", allDepartments);
                attributes.put("person", personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false));
                attributes.put("questionId", ProcessQuestion.Question.Constants.HOME_ADDRESS_DISGREGATED);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, AddressBanBifForm form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, AddressBanBifForm form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                if (form.getZoneType() != null && form.getZoneType().equals("-1"))
                    form.setZoneType(null);

                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                ((AddressBanBifForm.Validator) form.getValidator()).configValidatorByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),'H', null);
                form.getValidator().validate(locale);
                // Vadliate the zoneType and RoadType
                if (form.getZoneType() != null && form.getZoneType().equals("13") && form.getRoadType() != null && form.getRoadType().equals("14")) {
                    ((AddressBanBifForm.Validator) form.getValidator()).roadType.setError("Requerido");
                }
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, AddressBanBifForm form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                String roadType = catalogService.getStreetTypes()
                        .stream()
                        .filter(e -> e.getId().equals(Integer.valueOf(form.getRoadType())))
                        .findFirst()
                        .orElseGet(StreetType::new)
                        .getType();

                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());

                personDao.deleteDisaggregatedAddress(loanApplication.getPersonId(), 'H');

                Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);

                personDao.updateAddressInformation(
                        person.getId(),
                        form.getDepartamento(),
                        form.getProvincia(),
                        form.getDistrito(),
                        Integer.valueOf(form.getRoadType()),
                        String.format("%s %s %s", roadType, form.getRoadName(), form.getHouseNumber()),
                        form.getHouseNumber(),
                        form.getInterior(),
                        form.getReference(),
                        countryContextService.getCountryDefaultMapLatitude(CountryParam.COUNTRY_PERU),
                        countryContextService.getCountryDefaultMapLongitude(CountryParam.COUNTRY_PERU));

                String ubigeo = null;
                if (person.getDocumentType().getCountryId().equals(CountryParam.COUNTRY_PERU)) {
                    ubigeo = form.getDepartamento() + form.getProvincia() + form.getDistrito();
                }
                Direccion address = new Direccion(catalogService,
                        "H",
                        form.getRoadType(),
                        form.getRoadName(),
                        form.getHouseNumber(),
                        form.getInterior(),
                        null,
                        null,
                        ubigeo,
                        form.getReference(),
                        form.getZoneType(),
                        form.getZoneName(),
                        Long.valueOf(form.getDistrito()),
                        null,
                        null,
                        null);

                GeocodingResult geocodingResult = geocodingService.getGeoLocationResult(loanApplication.getId(), address);
                if(geocodingResult != null && geocodingResult.getGeometry() != null && geocodingResult.getGeometry().getLocation() != null){
                    address.setLatitude(geocodingResult.getGeometry().getLocation().getLat());
                    address.setLongitude(geocodingResult.getGeometry().getLocation().getLng());
                }
                personDao.registerDisgregatedAddress(person.getId(), address);

                if(address.getLatitude() != null && address.getLongitude() != null) personDao.registerAddressCoordinates(person.getId(), address);

                break;
        }
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
                    case "province":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getProvinces((String) params.get("departmentId"))));
                    case "generalDistrict":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getGeneralDistrictByProvincePostalCode((Integer) params.get("provinceId"), (String) params.get("postalCode"))));
                    case "district":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getDistricts((String) params.get("departmentId"), (String) params.get("provinceId"))));
                    case "generalProvince":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getGeneralProvinces(Integer.parseInt(params.get("departmentId").toString()))));
                    case "districtsByProvienceId":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getDistrictsByProvinceId(Integer.parseInt(params.get("provinceId").toString()))));
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

    private JSONObject getJsonHomeAddress(LoanApplication loanApplication, Locale locale) throws Exception {
        Direccion disagregatedAddress = personDao.getDisggregatedAddress(loanApplication.getPersonId(), "H");
        if (disagregatedAddress != null) {
            Ubigeo ubigeo = catalogService.getUbigeo(disagregatedAddress.getUbigeo());

            JSONObject json = new JSONObject();
            json.put("roadName", disagregatedAddress.getNombreVia());
            json.put("departamento", ubigeo.getDepartment().getId());
            json.put("provincia", ubigeo.getProvince().getId());
            json.put("provinciaLabel", ubigeo.getProvince().getName());
            json.put("distrito", ubigeo.getDistrict().getId());
            json.put("distritoLabel", ubigeo.getDistrict().getName());
            json.put("roadType", disagregatedAddress.getTipoVia());
            json.put("interior", disagregatedAddress.getNumeroInterior());
            json.put("houseNumber", disagregatedAddress.getNumeroVia());
            json.put("zoneType", disagregatedAddress.getTipoZona());
            json.put("zoneName", disagregatedAddress.getNombreZona());
            json.put("reference", disagregatedAddress.getReferencia());

            return json;
        }

        return null;
    }
}

