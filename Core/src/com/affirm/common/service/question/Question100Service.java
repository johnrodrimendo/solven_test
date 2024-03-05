package com.affirm.common.service.question;

import com.affirm.acceso.model.Direccion;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.Department;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.Province;
import com.affirm.common.model.form.AddressForm;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.CountryContextService;
import com.affirm.common.service.PersonService;
import com.affirm.common.service.UserService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("question100Service")
public class Question100Service extends AbstractQuestionService<AddressForm> {

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
    private PersonService personService;
    @Autowired
    private Question82Service question82Service;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        AddressForm form = new AddressForm();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                ((AddressForm.Validator) form.getValidator()).configValidator(loanApplication.getCountryId(), null);

                if (fillSavedData) {
                    if (loanApplication.getCountryId().equals(CountryParam.COUNTRY_PERU)) {
                        form.fillData(personService.getOcupationalInformation(loanApplication.getPersonId(), (int) params.get(VIEW_PARAM_OCUPATION_NUMBER), locale));
                    } else if (loanApplication.getCountryId().equals(CountryParam.COUNTRY_ARGENTINA)) {
                        form.fillData(personDao.getDisggregatedAddress(loanApplication.getPersonId(), "J"));
                    }
                }

                List<Department> allDepartments = catalogService.getDepartments();
                List<Department> topDepartments = allDepartments.stream().filter(d -> Configuration.orderedTopDepartments.contains(d.getId())).collect(Collectors.toList());
                for (int i = 0; i < Configuration.orderedTopDepartments.size(); i++) {
                    Collections.swap(topDepartments, Configuration.orderedTopDepartments.indexOf(topDepartments.get(i).getId()), i);
                }
                allDepartments.removeAll(topDepartments);

                attributes.put("loanApplication", loanApplication);
                attributes.put("questionId", 61);
                attributes.put("form", form);
                attributes.put("topDepartments", topDepartments);
                attributes.put("allDepartments", allDepartments);
                if(loanApplication.getEntityId() == null || !Arrays.asList(Entity.CREDIGOB, Entity.FUNDACION_DE_LA_MUJER).contains(loanApplication.getEntityId())){
                    attributes.put("showWorkOnHouse", true);
                }
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, AddressForm form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
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
                PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);

                if(loanApplication.getCountryId().equals(CountryParam.COUNTRY_PERU)){
                    personDao.updateOcupationalAddress(loanApplication.getPersonId(), ocupation.getNumber(), form.getRoadName(), form.getHomeAddress(), form.getDepartamento(), form.getProvincia(), form.getDistrito());
                } else if(loanApplication.getCountryId().equals(CountryParam.COUNTRY_ARGENTINA)){
                    Direccion direccion = new Direccion(catalogService, "J", form.getRoadType(), form.getRoadName(), form.getHouseNumber(), form.getInterior(), form.getManzana(), form.getLote(), null, form.getReference(), form.getZoneType(), form.getZoneName(), Long.valueOf(form.getDistrito()), form.getFloor(), form.getWithoutNumber() != null ? Boolean.valueOf(form.getWithoutNumber()) : null, form.getPostalCode());
                    personDao.registerDisgregatedAddress(loanApplication.getPersonId(), direccion);
                    personDao.updateOcupationalAddress(loanApplication.getPersonId(), ocupation.getNumber(), direccion.getDireccionCompleta(), form.getHomeAddress(), null, null, null);
                }
                break;
        }
    }

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                switch (path) {
                    case "province":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getProvinces((String)params.get("departmentId"))));
                    case "provinceByPostalCode":
                        List<Province> provinceList = catalogService.getGeneralProvinceByPostalCode(CountryParam.COUNTRY_ARGENTINA, (String)params.get("postalCode"));
                        if (provinceList.size() > 0)
                            return AjaxResponse.ok(new Gson().toJson(provinceList));
                        return AjaxResponse.errorMessage("El código postal es incorrecto");
                    case "generalDistrict":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getGeneralDistrictByProvincePostalCode((Integer)params.get("provinceId"), (String)params.get("postalCode"))));
                    case "district":
                        return AjaxResponse.ok(new Gson().toJson(catalogService.getDistricts((String)params.get("departmentId"), (String)params.get("provinceId"))));
                    case "homeAddress":
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                        JSONObject jsonAddress = question82Service.getJsonHomeAddress(loanApplication, locale);
                        if(jsonAddress != null)
                            return AjaxResponse.ok(jsonAddress.toString());

                        return AjaxResponse.errorMessage("No tienes dirección registrada");
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

