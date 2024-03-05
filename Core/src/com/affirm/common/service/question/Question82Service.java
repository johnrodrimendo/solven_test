package com.affirm.common.service.question;

import com.affirm.acceso.model.Direccion;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.AddressForm;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EmployeeService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.PersonService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("question82Service")
public class Question82Service extends AbstractQuestionService<AddressForm> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private PersonService personService;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EvaluationService evaluationService;

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

                ProcessQuestion currentQuestion = evaluationService.getQuestionFromEvaluationProcess(loanApplication, null, ProcessQuestion.Question.Constants.ADRESS_WORK_PLACE_DEPENDENT, null, null);
                if (currentQuestion.getConfiguration() != null) {
                    attributes.put("tittle", JsonUtil.getStringFromJson(currentQuestion.getConfiguration(), "tittle", null));
                }

                attributes.put("loanApplication", loanApplication);
                attributes.put("questionId", 82);
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

                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
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
                    registerAddress(loanApplication, form.getRoadName(), form.getHomeAddress(), form.getDepartamento(), form.getProvincia(), form.getDistrito());
                } else if (loanApplication.getCountryId().equals(CountryParam.COUNTRY_ARGENTINA)) {
                    Direccion direccion = new Direccion(catalogService, "J", form.getRoadType(), form.getRoadName(), form.getHouseNumber(), form.getInterior(), form.getManzana(), form.getLote(), null, form.getReference(), form.getZoneType(), form.getZoneName(), Long.valueOf(form.getDistrito()), form.getFloor(), form.getWithoutNumber() != null ? Boolean.valueOf(form.getWithoutNumber()) : null, form.getPostalCode());
                    personDao.registerDisgregatedAddress(loanApplication.getPersonId(), direccion);
                    registerAddress(loanApplication, direccion.getDireccionCompleta(), form.getHomeAddress(), null, null, null);
                } else if (CountryParam.COUNTRY_COLOMBIA == loanApplication.getCountryId()) {
                    registerAddress(loanApplication, form.getRoadName(), form.getHomeAddress(), form.getDepartamento(), form.getProvincia(), form.getDistrito());
                }
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);

                // If is branding ABACO and the person is a Agreement Employee, then get the date of the paysheet and skip
                if (loanApplication.getEntityId() != null && loanApplication.getEntityId().equals(Entity.ABACO)) {
                    Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
                    List<Employee> agreementEmployees = employeeService.getEmployeesByEmailOrDocumentByEntityProduct(null, person.getDocumentType().getId(), person.getDocumentNumber(), Entity.ABACO, Product.AGREEMENT, Configuration.getDefaultLocale());
                    if (agreementEmployees != null) {
                        String address = agreementEmployees.stream().filter(e -> e.getEmployer() != null && e.getEmployer().getAddress() != null).map(e -> e.getEmployer().getAddress()).findFirst().orElse(null);
                        if (address != null) {
                            if (saveData) {
                                registerAddress(loanApplication, address, false, null, null, null);
                            }
                            return "DEFAULT";
                        }
                    }
                }

                // If it comes from the entity extranet, set the address of the registered employer
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
                            registerAddress(loanApplication, employee.getEmployer().getAddress(), false, null, null, null);
                        return "DEFAULT";
                    }
                }

                if (loanApplication.getCountryId().equals(CountryParam.COUNTRY_PERU)) {
                    // If its not branding from credigob
                    if(loanApplication.getEntityId() == null || loanApplication.getEntityId() != Entity.CREDIGOB){
                        StaticDBInfo staticDBInfo=personService.getIncome(loanApplication.getPersonId());
                        if(staticDBInfo!=null){
                            if(staticDBInfo.getIncome()>=Configuration.MIN_INCOME){
                                if(staticDBInfo.getRuc()!=null){
                                    RucInfo rucInfo=personDao.getRucInfo(staticDBInfo.getRuc());
                                    if(rucInfo!=null){
                                        if(saveData){
                                            registerAddress(loanApplication, rucInfo.getFiscalAddress(), false, null, null, null);
                                        }
                                        return "DEFAULT";
                                    }
                                }
                            }
                        }
                    }
                }
                break;
        }
        return null;
    }

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                switch (path) {
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
                    case "homeAddress":
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                        JSONObject jsonAddress = getJsonHomeAddress(loanApplication, locale);
                        if (jsonAddress != null)
                            return AjaxResponse.ok(jsonAddress.toString());

                        return AjaxResponse.errorMessage("No tienes dirección registrada");
                }
                break;
        }
        throw new Exception("No method configured");
    }

    public JSONObject getJsonHomeAddress(LoanApplication loanApplication, Locale locale) throws Exception {
        if (loanApplication.getCountryId() == CountryParam.COUNTRY_PERU) {
            PersonContactInformation personContactInfo = personDao.getPersonContactInformation(locale, loanApplication.getPersonId());
            if (personContactInfo != null) {
                JSONObject json = new JSONObject();
                json.put("roadName", personContactInfo.getAddressStreetName());
                json.put("departamento", personContactInfo.getAddressUbigeo() != null ? personContactInfo.getAddressUbigeo().getDepartment().getId() : null);
                json.put("provincia", personContactInfo.getAddressUbigeo() != null ? personContactInfo.getAddressUbigeo().getProvince().getId() : null);
                json.put("provinciaLabel", personContactInfo.getAddressUbigeo() != null ? personContactInfo.getAddressUbigeo().getProvince().getName() : null);
                json.put("distrito", personContactInfo.getAddressUbigeo() != null ? personContactInfo.getAddressUbigeo().getDistrict().getId() : null);
                json.put("distritoLabel", personContactInfo.getAddressUbigeo() != null ? personContactInfo.getAddressUbigeo().getDistrict().getName() : null);
                json.put("houseType", personContactInfo.getHousingType() != null ? personContactInfo.getHousingType().getId() : null);
                return json;
            }
        } else if (loanApplication.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
            Direccion disagregatedAddress = personDao.getDisggregatedAddress(loanApplication.getPersonId(), "H");
            if (disagregatedAddress != null) {

                District generalDistrict = catalogService.getGeneralDistrictById(disagregatedAddress.getLocalityId());
                String province = generalDistrict.getProvince().getProvinceId() + "";
                String provinceLabel = generalDistrict.getProvince().getName();
                String district = generalDistrict.getDistrictId() + "";
                String districtLabel = generalDistrict.getName();
                String postalCode = generalDistrict.getPostalCode();

                JSONObject json = new JSONObject();
                json.put("roadType", disagregatedAddress.getTipoVia());
                json.put("roadName", disagregatedAddress.getNombreVia());
                json.put("withoutNumber", disagregatedAddress.getWithoutNumber());
                json.put("floor", disagregatedAddress.getFloor());
                json.put("interior", disagregatedAddress.getNumeroInterior());
                json.put("houseNumber", disagregatedAddress.getNumeroVia());
                json.put("manzana", disagregatedAddress.getManzana());
                json.put("lote", disagregatedAddress.getLote());
                json.put("zoneType", disagregatedAddress.getTipoZona());
                json.put("zoneName", disagregatedAddress.getNombreZona());
                json.put("reference", disagregatedAddress.getReferencia());
                json.put("provincia", province);
                json.put("provinciaLabel", provinceLabel);
                json.put("distrito", district);
                json.put("distritoLabel", districtLabel);
                json.put("postalCode", postalCode);

                return json;
            }
        } else if (loanApplication.getCountryId() == CountryParam.COUNTRY_COLOMBIA) {
            PersonContactInformation personContactInfo = personDao.getPersonContactInformation(locale, loanApplication.getPersonId());
            if (personContactInfo != null) {
                JSONObject json = new JSONObject();
                json.put("roadName", personContactInfo.getAddressStreetName());
                json.put("departamento", personContactInfo.getDepartment() != null ? personContactInfo.getDepartment().getDepartmentId() : null);
                json.put("provincia", personContactInfo.getProvince() != null ? personContactInfo.getProvince().getProvinceId() : null);
                json.put("provinciaLabel", personContactInfo.getProvince() != null ? personContactInfo.getProvince().getName() : null);
                json.put("distrito", personContactInfo.getDistrict() != null ? personContactInfo.getDistrict().getDistrictId() : null);
                json.put("distritoLabel", personContactInfo.getDistrict() != null ? personContactInfo.getDistrict().getName() : null);

                return json;
            }
        }

        return null;
    }

    private void registerAddress(LoanApplication loanApplication, String address, Boolean isHomeAddress, String department, String province, String district) throws Exception {
        PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), Configuration.getDefaultLocale());

        if (CountryParam.COUNTRY_COLOMBIA == loanApplication.getCountryId()) {
            personDao.updateOcupationalAddress(loanApplication.getPersonId(), ocupation.getNumber(), address, isHomeAddress, district);
        } else {
            personDao.updateOcupationalAddress(loanApplication.getPersonId(), ocupation.getNumber(), address, isHomeAddress, department, province, district);
        }
    }

}

