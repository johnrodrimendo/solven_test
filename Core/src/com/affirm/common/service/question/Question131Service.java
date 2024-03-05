package com.affirm.common.service.question;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.ActivityType;
import com.affirm.common.model.catalog.CustomProfession;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.SubActivityType;
import com.affirm.common.model.form.Question131Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanApplicationPreliminaryEvaluation;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.PersonService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service("question131Service")
public class Question131Service extends AbstractQuestionService<Question131Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private PersonService personService;


    @Autowired
    EvaluationService evaluationService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question131Form form = new Question131Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());

                if (fillSavedData) {
                    Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
                    form.setCustomProfession(person.getCustomProfession() != null ? person.getCustomProfession().getId() : null);
                }
                attributes.put("customProfessions", getCustomProfessions(loanApplication.getId()));
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question131Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                PersonOcupationalInformation ocupationalInformation = personService.getOcupationalInformation(loanApplication.getPersonId(), PersonOcupationalInformation.PRINCIPAL, Configuration.getDefaultLocale());

                Integer activityType = null;
                Integer subActivityType = null;
                if(form.getActivityTypeId() != null){
                    activityType = form.getActivityTypeId();
                    subActivityType = form.getSubActivityTypeId();
                }else if(ocupationalInformation != null){
                    activityType = ocupationalInformation.getActivityType().getId();
                    subActivityType = ocupationalInformation.getSubActivityType() != null ? ocupationalInformation.getSubActivityType().getId() : null;
                }


                if (activityType == ActivityType.INDEPENDENT && subActivityType == SubActivityType.RENT)
                    return "RENT";
                if (activityType == ActivityType.INDEPENDENT && subActivityType == SubActivityType.PROFESSIONAL_SERVICE)
                    return "PROFESSIONAL_SERVICES";
                if (activityType == ActivityType.INDEPENDENT && subActivityType == SubActivityType.OWN_BUSINESS)
                    return "OWN";

                if (activityType == ActivityType.DEPENDENT)
                    return "DEPENDENT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question131Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question131Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                personDao.updateCustomProfession(loanApplication.getPersonId(), form.getCustomProfession());
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {

        switch (flowType) {
            case LOANAPPLICATION:

                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                List<LoanApplicationPreliminaryEvaluation> preliminaryEvaluations = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), Configuration.getDefaultLocale(), JsonResolverDAO.EVALUATION_FOLLOWER_DB);
                LoanApplicationPreliminaryEvaluation preliminaryEvaluation = preliminaryEvaluations.stream().filter(pr -> (pr.getApproved() == null || pr.getApproved()) && pr.getEntityId() == Entity.RIPLEY).findFirst().orElse(null);
                PersonOcupationalInformation ocupationalInformation = personService.getOcupationalInformation(loanApplication.getPersonId(), PersonOcupationalInformation.PRINCIPAL, locale);

                if(ocupationalInformation == null){
                    return null;
                }

                if (preliminaryEvaluation == null) {

                    if (ocupationalInformation.getActivityType().getId() == ActivityType.INDEPENDENT && ocupationalInformation.getSubActivityType().getId() == SubActivityType.OWN_BUSINESS)
                        return "OWN";
                    else if (ocupationalInformation.getActivityType().getId() == ActivityType.INDEPENDENT && ocupationalInformation.getSubActivityType().getId() == SubActivityType.RENT)
                        return "RENT";
                    else if (ocupationalInformation.getActivityType().getId() == ActivityType.INDEPENDENT && ocupationalInformation.getSubActivityType().getId() == SubActivityType.PROFESSIONAL_SERVICE)
                        return "PROFESSIONAL_SERVICES";
                    else if (ocupationalInformation.getActivityType().getId() == ActivityType.DEPENDENT)
                        return "DEPENDENT";
                } else {

                    if (ocupationalInformation.getActivityType().getId() == ActivityType.INDEPENDENT && ocupationalInformation.getSubActivityType().getId() == SubActivityType.OWN_BUSINESS) {
                        if(saveData){
                            personDao.updateCustomProfession(loanApplication.getPersonId(), CustomProfession.BUSINNES_OWNER);
                        }
                        return "OWN";
                    }

                }
        }
        return null;
    }

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                switch (path) {
                    case "getCustomProfessions":
                        List<CustomProfession> customProfessions = getCustomProfessionsByActivityType(Integer.parseInt(params.get("activityTypeId") + ""));
                        if (customProfessions != null)
                            return AjaxResponse.ok(new Gson().toJson(customProfessions));

                        return AjaxResponse.ok(null);
                }
                break;
        }
        throw new Exception("No method configured");
    }

    public List<CustomProfession> getCustomProfessions(Integer loanApplicationId) throws Exception {
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
        Person person = personDao.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
        List<PersonOcupationalInformation> ocupationalInformationList = personDao.getPersonOcupationalInformation(Configuration.getDefaultLocale(), person.getId());
        List<CustomProfession> customProfessionList = null;
        PersonOcupationalInformation ocupationalInformation = null;
        if (ocupationalInformationList != null) {
            ocupationalInformation = ocupationalInformationList.stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst().orElse(null);

            if(ocupationalInformation != null && ocupationalInformation.getActivityType() != null) {
                customProfessionList = getCustomProfessionsByActivityType(ocupationalInformation.getActivityType().getId());
            }

        }
        return customProfessionList;
    }

    private List<CustomProfession> getCustomProfessionsByActivityType(int activityTypeId) throws Exception {
        List<CustomProfession> customProfessionList = null;
        if (activityTypeId == ActivityType.DEPENDENT) {
            customProfessionList = catalogService.getCustomProfessions().stream()
                    .filter(customP -> customP.getActivityTypeIds().contains(ActivityType.DEPENDENT) && customP.getId() != CustomProfession.BUSINNES_OWNER)
                    .collect(Collectors.toList());
        }else if (activityTypeId == ActivityType.INDEPENDENT) {
            customProfessionList = catalogService.getCustomProfessions().stream()
                    .filter(customP -> customP.getActivityTypeIds().contains(ActivityType.INDEPENDENT) && customP.getId() != CustomProfession.BUSINNES_OWNER)
                    .collect(Collectors.toList());
        }
        return customProfessionList;
    }
}

