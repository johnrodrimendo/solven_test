package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.Question135Form;
import com.affirm.common.model.form.Question166Form;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.PersonService;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("question166Service")
public class Question166Service extends AbstractQuestionService<Question166Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDAO;
    @Autowired
    private PersonService personService;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private CatalogService catalogService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question166Form form = new Question166Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if (fillSavedData) {
                    PersonOcupationalInformation ocupationalInformation = personService.getOcupationalInformation(loanApplication.getPersonId(), (int) params.get(VIEW_PARAM_OCUPATION_NUMBER), locale);
                    if (ocupationalInformation != null) {
                        form.setOcupation(ocupationalInformation.getOcupation() != null ? ocupationalInformation.getOcupation().getId() : null);
                    }
                }

                List<Integer> occupationIds = evaluationService.getIdsCatalogFromConfigurationInProcessQuestion(
                        id, ProcessQuestion.Question.Constants.MERGE_OCUPATION_WORK_PLACE_DEPENDENT_PEP_PROFESSION_AND_OCUPATIONS, "occupationIds");

                List<Ocupation> occupations = null;

                if (occupationIds != null) {
                    occupations = catalogService.getOcupations(locale)
                            .stream()
                            .filter(e -> occupationIds.contains(e.getId()))
                            .collect(Collectors.toList());

                    occupations.sort(Comparator.comparing(Ocupation::getOrderId));
                }

                List<Integer> professionOccupationIds = evaluationService.getIdsCatalogFromConfigurationInProcessQuestion(
                        id, ProcessQuestion.Question.Constants.MERGE_OCUPATION_WORK_PLACE_DEPENDENT_PEP_PROFESSION_AND_OCUPATIONS, "professionOccupationIds");

                List<PersonProfessionOccupation> professionOccupations = null;

                if (professionOccupationIds != null) {
                    professionOccupations = catalogService.getProfessionOccupations()
                            .stream()
                            .filter(e -> professionOccupationIds.contains(e.getId()))
                            .collect(Collectors.toList());

                    professionOccupations.sort(Comparator.comparing(PersonProfessionOccupation::getOccupation));
                }

                List<CustomEntityActivity> activities = new ArrayList<>();
                List<CustomEntityProfession> customEntityProfessions = new ArrayList<>();
                boolean showActivities = false;
                boolean showCustomProfessions = false;

/*                if((loanApplication.getEntityId() != null && loanApplication.getEntityId().equals(Entity.AZTECA)) || (loanApplication.getSelectedEntityId() != null && loanApplication.getSelectedEntityId().equals(Entity.AZTECA))){
                    if(loanApplication.getProductCategoryId() == null || !loanApplication.getProductCategoryId().equals(ProductCategory.CUENTA_BANCARIA)){
                        activities = catalogService.getCustomEntityActivities(Entity.AZTECA);
                        showActivities = true;
                        ((Question166Form.Validator) form.getValidator()).activityId.setRequired(true);
                    }
                }*/

                if((loanApplication.getEntityId() != null && loanApplication.getEntityId().equals(Entity.AZTECA)) || (loanApplication.getSelectedEntityId() != null && loanApplication.getSelectedEntityId().equals(Entity.AZTECA))) {
                    customEntityProfessions = catalogService.getCustomEntityProfessions(Entity.AZTECA);
                    showCustomProfessions = true;
                    ((Question166Form.Validator) form.getValidator()).customProfessionId.setRequired(true);
                }

                attributes.put("activities", activities);
                attributes.put("showActivities", showActivities);
                attributes.put("customEntityProfessions", customEntityProfessions);
                attributes.put("showCustomProfessions", showCustomProfessions);
                attributes.put("form", form);
                attributes.put("professionOcupations", professionOccupations);
                attributes.put("occupations", occupations);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question166Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                PersonOcupationalInformation occupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), Configuration.getDefaultLocale());
                switch (occupation.getActivityType().getId()) {
                    case ActivityType.DEPENDENT:
                        return "DEPENDENT";
                    case ActivityType.INDEPENDENT:
                        return "INDEPENDENT";
                    case ActivityType.UNEMPLOYED:
                        return "NO_INCOME";
                    case ActivityType.PENSIONER:
                        return "PENSIONER";
                }
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question166Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
/*                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if((loanApplication.getEntityId() != null && loanApplication.getEntityId().equals(Entity.AZTECA)) || (loanApplication.getSelectedEntityId() != null && loanApplication.getSelectedEntityId().equals(Entity.AZTECA))){
                    if(loanApplication.getProductCategoryId() == null || !loanApplication.getProductCategoryId().equals(ProductCategory.CUENTA_BANCARIA)){
                        ((Question166Form.Validator) form.getValidator()).activityId.setRequired(true);
                    }
                }*/
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if((loanApplication.getEntityId() != null && loanApplication.getEntityId().equals(Entity.AZTECA)) || (loanApplication.getSelectedEntityId() != null && loanApplication.getSelectedEntityId().equals(Entity.AZTECA))) {
                    ((Question166Form.Validator) form.getValidator()).customProfessionId.setRequired(true);
                }

                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question166Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                personDAO.registerProfessionOccupation(loanApplication.getPersonId(), form.getProfessionOcupation());
//                PersonOcupationalInformation occupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);
//                personDAO.updateOcupatinalOcupation(loanApplication.getPersonId(), occupation.getNumber(), form.getOcupation());
                personDAO.updatePepInformation(loanApplication.getPersonId(), form.getIsPep(), form.getPepDetail());
                personDAO.updateOrInsertPersonDisqualifierByPersonIdOnly(loanApplication.getPersonId(), PersonDisqualifier.FACTA, form.getIsFatca(), null);
                personDAO.updateProfession(loanApplication.getPersonId(), form.getProfession());
                if(form.getActivityId() != null){
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.CUSTOM_ENTITY_ACTIVITY_ID.getKey(), form.getActivityId());
                    loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                }
                if(form.getCustomProfessionId() != null){
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.CUSTOM_ENTITY_PROFESSION_ID.getKey(), form.getCustomProfessionId());
                    loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                }
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
//                return "DEFAULT";
        }
        return null;
    }
}

