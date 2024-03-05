package com.affirm.common.service.question;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.form.Question108Form;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.*;
import com.affirm.system.configuration.Configuration;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service("question108Service")
public class Question108Service extends AbstractQuestionService<Question108Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CountryContextService countryContextService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private WebscrapperService webscrapperService;
    @Autowired
    private PersonService personService;
    @Autowired
    private EvaluationService evaluationService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question108Form form = new Question108Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), true);

                if (loanApplication.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
                    ((Question108Form.Validator) form.getValidator()).partnerDocType.setRequired(false);
                    ((Question108Form.Validator) form.getValidator()).partnerDocNumber.update(ValidatorUtil.DOC_NUMBER_CUIT);
                } else if (loanApplication.getCountryId() == CountryParam.COUNTRY_PERU) {
                    ((Question108Form.Validator) form.getValidator()).partnerDocNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
                }

                if (fillSavedData) {
                    if (person.getPartner() != null) {
                        form.setPartnerDocType(person.getPartner().getDocumentType().getId());
                        form.setPartnerDocNumber(person.getPartner().getDocumentNumber());
                    }
                }

                attributes.put("loanApplication", loanApplication);
                attributes.put("personName", person.getFirstName());
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question108Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question108Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);

                if (loanApplication.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
                    form.setPartnerDocType(IdentityDocumentType.CDI);
                    ((Question108Form.Validator) form.getValidator()).partnerDocType.setRequired(false);
                }
//                else if (loanApplication.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
//                    ((Question108Form.Validator) form.getValidator()).partnerDocType.setRequired(true);
//                }

                if (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA) {
                    ((Question108Form.Validator) form.getValidator()).partnerDay.setRequired(false);
                    ((Question108Form.Validator) form.getValidator()).partnerMonth.setRequired(false);
                    form.setEntityBrandingId(Entity.AZTECA);
                }

                if (form.getPartnerDocNumber() == null && form.getPartnerDocType() == null) {
                    form = null;
                }

                if (form != null) {
                    form.getValidator().validate(locale);

                    if (loanApplication.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
                        if (form.getPartnerDocNumber() != null) {
                            if (!utilService.validarCuit(form.getPartnerDocNumber())) {
                                ((Question108Form.Validator) form.getValidator()).partnerDocNumber.setError(messageSource.getMessage("static.message.cuit", null, locale));
                            }
                        }

                    }
                }

                if (person.getDocumentType().getId().equals(form.getPartnerDocType()) && person.getDocumentNumber().equals(form.getPartnerDocNumber()))
                    throw new FormValidationException("El documento de identidad de tu esposo(a) o conviviente debe ser diferente al tuyo");

                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question108Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                Integer partnerPersonId = null;
                if (form.getPartnerDocType() != null && form.getPartnerDocNumber() != null) {
                    partnerPersonId = personDao.getPersonIdByDocument(form.getPartnerDocType(), form.getPartnerDocNumber());
                    if (partnerPersonId == null)
                        partnerPersonId = personDao.createPerson(form.getPartnerDocType(), form.getPartnerDocNumber(), locale).getId();
                    personDao.updatePartner(loanApplication.getPersonId(), partnerPersonId);
                    webscrapperService.callRunSynthesized(form.getPartnerDocNumber(), null);
                    Person partnerPerson = personDao.getPerson(partnerPersonId, false, locale);
                    if(form.getPartnerFirstSurname() != null && partnerPerson.getFirstSurname() == null){
                        personDao.updateName(partnerPersonId, form.getPartnerName());
                        personDao.updateFirstSurname(partnerPersonId, form.getPartnerFirstSurname());
                        personDao.updateLastSurname(partnerPersonId, form.getPartnerLastSurname());
                        personDao.updateGender(partnerPersonId, form.getGender());
                    }

                } else {
                    form.setLoanApplicationId(loanApplication.getId());
                    personService.asyncPersonPartnerUpdate(form, loanApplication.getPersonId()); // long executing time method
                }

                boolean askForNames = false;
                // If argentina, run the AFIP, ANSES and BCRA bots fro the partner
                if (loanApplication.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
                    webscrapperService.callAFIPBot(form.getPartnerDocType(), form.getPartnerDocNumber(), null);
                    webscrapperService.callANSESBot(form.getPartnerDocType(), form.getPartnerDocNumber(), null);
                    webscrapperService.callBCRABot(form.getPartnerDocType(), form.getPartnerDocNumber(), null);
                }else if(loanApplication.getCountryId() == CountryParam.COUNTRY_PERU) {
                    if(partnerPersonId != null){
                        Person person = personDao.getPerson(partnerPersonId, false, locale);
                        if(person.getName() == null){
                            askForNames = true;
                        }
                    }
                }

                if(askForNames){
                    throw new ResponseEntityException(AjaxResponse.ok("askNames"));
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
                    case "askFormNames":{
                        Boolean askForNames = false;
                        Question108Form form = (Question108Form) params.get("form");
                        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                        Integer partnerPersonId = personDao.getPersonIdByDocument(form.getPartnerDocType(), form.getPartnerDocNumber());
                        if(partnerPersonId == null){
                            if(form.getPartnerDocType() == IdentityDocumentType.DNI && personDao.getReniecDBData(form.getPartnerDocNumber()) != null){
                                askForNames = false;
                            }else{
                                askForNames = true;
                            }
                        }
                        return AjaxResponse.ok(askForNames.toString());
                    }
                }
                break;
        }
        throw new Exception("No method configured");
    }
}

