package com.affirm.common.service.question;

import com.affirm.acceso.model.Direccion;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.AddressBanBifForm;
import com.affirm.common.model.form.ShortProcessQuestion1Form;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.PersonService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.system.configuration.Configuration;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("question155Service")
public class Question155Service extends AbstractQuestionService<AddressBanBifForm> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private PersonService personService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        AddressBanBifForm form = new AddressBanBifForm();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);

                ((AddressBanBifForm.Validator) form.getValidator()).configValidatorByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(), 'J', showTermsAndConditions(loanApplication));

                List<Department> allDepartments = catalogService.getDepartments();
                List<Department> topDepartments = allDepartments.stream().filter(d -> Configuration.orderedTopDepartments.contains(d.getId())).collect(Collectors.toList());
                for (int i = 0; i < Configuration.orderedTopDepartments.size(); i++) {
                    Collections.swap(topDepartments, Configuration.orderedTopDepartments.indexOf(topDepartments.get(i).getId()), i);
                }
                allDepartments.removeAll(topDepartments);

                attributes.put("showTermsAndConditions", showTermsAndConditions(loanApplication));

                if ((loanApplication.getEntityId() != null && loanApplication.getEntityId().equals(Entity.BANBIF)) || (loanApplication.getSelectedEntityId() != null && loanApplication.getSelectedEntityId().equals(Entity.BANBIF))) {
                    attributes.put("tittle", "Domicilio laboral");
                } else attributes.put("tittle", "¿Cuál es tu dirección laboral?");

                Boolean showFieldRucEsSalud = false;
                Boolean showFieldComapnyNameEsSalud = false;
                if (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.BANBIF) {
                    StaticDBInfo staticDBInfo = personService.getIncome(loanApplication.getPersonId());

                        if (staticDBInfo != null && staticDBInfo.getRuc() != null) {
                            ((AddressBanBifForm.Validator) form.getValidator()).ruc.setRequired(false);
                        } else {
                            ((AddressBanBifForm.Validator) form.getValidator()).ruc.setRequired(true);
                            showFieldRucEsSalud = true;
                        }

                        if (staticDBInfo != null && staticDBInfo.getRazonSocial() != null) {
                            ((AddressBanBifForm.Validator) form.getValidator()).companyName.setRequired(false);
                        } else {
                            ((AddressBanBifForm.Validator) form.getValidator()).companyName.setRequired(true);
                            showFieldComapnyNameEsSalud = true;
                        }

                }
                attributes.put("showFieldRucEsSalud", showFieldRucEsSalud);
                attributes.put("showFieldComapnyNameEsSalud", showFieldComapnyNameEsSalud);
                attributes.put("loanApplication", loanApplication);
                attributes.put("form", form);
                attributes.put("topDepartments", topDepartments);
                attributes.put("allDepartments", allDepartments);
                attributes.put("person", personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false));
                attributes.put("questionId", ProcessQuestion.Question.Constants.WORKPLACE_ADDRESS_DISGREGATED);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, AddressBanBifForm form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                List<PersonOcupationalInformation> occupations = personDao.getPersonOcupationalInformation(Configuration.getDefaultLocale(), loanApplication.getPersonId());
                if (occupations != null && (loanApplication.getEntityId() == null || !Arrays.asList(Entity.BANBIF).contains(loanApplication.getEntityId()))) {
                    PersonOcupationalInformation principalOccupation = occupations.stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst().orElse(null);
                    if (principalOccupation != null && principalOccupation.getActivityType() != null && principalOccupation.getActivityType().getId().equals(ActivityType.DEPENDENT))
                        return "DEPENDENT";
                }
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
                ((AddressBanBifForm.Validator) form.getValidator()).configValidatorByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(), 'J', showTermsAndConditions(loanApplication));
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

                personDao.deleteDisaggregatedAddress(loanApplication.getPersonId(), 'J');

                Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);

                String ubigeo = form.getDepartamento() + form.getProvincia() + form.getDistrito();

                Direccion address = new Direccion(catalogService,
                        "J",
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

                personDao.registerDisgregatedAddress(person.getId(), address);

                if(showTermsAndConditions(loanApplication)){
                    if ((loanApplication.getSelectedEntityId() != null && loanApplication.getSelectedEntityId().equals(Entity.BANBIF)) || (loanApplication.getEntityId() != null && loanApplication.getEntityId().equals(Entity.BANBIF))) {
                        loanApplicationDao.updateEntityCustomData(loanApplication.getId(),
                                loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANBIF_DIRECCION_ENTREGA.getKey(), form.getAddressToSend()));
                    }

                    if (loanApplication.getAuxData() == null) loanApplication.setAuxData(new LoanApplicationAuxData());
                    loanApplication.getAuxData().setAcceptedTyC(form.getAcceptedTyC());
                    loanApplicationDao.updateAuxData(loanApplication.getId(), loanApplication.getAuxData());
                }

                if (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.BANBIF) {

                    StaticDBInfo staticDBInfo = personService.getIncome(loanApplication.getPersonId());

                    if (staticDBInfo != null && staticDBInfo.getRuc() != null) {
                        registerRuc(loanApplication, staticDBInfo.getRuc());
                    } else {
                        registerRuc(loanApplication, form.getRuc());
                    }

                    if (staticDBInfo != null && staticDBInfo.getRazonSocial() != null) {
                        registerCompanyName(loanApplication, staticDBInfo.getRazonSocial());
                    } else {
                        registerCompanyName(loanApplication, form.getCompanyName());
                    }
                }

                personDao.updateOcupationalAddress(
                        person.getId(),
                        PersonOcupationalInformation.PRINCIPAL,
                        String.format("%s %s %s", roadType, form.getRoadName(), form.getHouseNumber()),
                        form.getHomeAddress(),
                        form.getDepartamento(),
                        form.getProvincia(),
                        form.getDistrito());


                break;
        }
    }

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                switch (path) {
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

    private void registerRuc(LoanApplication loanApplication, String ruc) throws Exception {
        if(loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.BANBIF){
            PersonOcupationalInformation occupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), Configuration.getDefaultLocale());
            if(occupation == null){
                personDao.cleanOcupationalInformation(loanApplication.getPersonId(), PersonOcupationalInformation.PRINCIPAL);
                personDao.updateOcupationalActivityType(loanApplication.getPersonId(), PersonOcupationalInformation.PRINCIPAL, ActivityType.DEPENDENT);
            }
        }
        personService.updateOcupationalRuc(ruc, loanApplication.getPersonId(), true, loanApplication);
    }

    private void registerCompanyName(LoanApplication loanApplication, String conpanyName) throws Exception {
        PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), Configuration.getDefaultLocale());
        personDao.updateOcupationalCompany(loanApplication.getPersonId(), ocupation.getNumber(), conpanyName);
    }

    private boolean showTermsAndConditions(LoanApplication loanApplication){
        if ((loanApplication.getSelectedEntityId() != null && loanApplication.getSelectedEntityId().equals(Entity.BANBIF)) || (loanApplication.getEntityId() != null && loanApplication.getEntityId().equals(Entity.BANBIF))) {
            return loanApplication.getSelectedEntityProductParameterId() == null || loanApplication.getSelectedEntityProductParameterId() != EntityProductParams.ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO;
        } else return false;
    }
}

