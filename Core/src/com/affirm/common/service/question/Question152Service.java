package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.Ocupation;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.catalog.Profession;
import com.affirm.common.model.form.Question140Form;
import com.affirm.common.model.form.Question143Form;
import com.affirm.common.model.form.Question152Form;
import com.affirm.common.model.form.Question29Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.model.transactional.PersonProfessionOccupation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import com.affirm.system.configuration.Configuration;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("question152Service")
public class Question152Service extends AbstractQuestionService<Question152Form> {

    private final Question143Service question143Service;
    private final Question29Service question29Service;
    private final EvaluationService evaluationService;
    private final CatalogService catalogService;

    public Question152Service(Question143Service question143Service, Question29Service question29Service, EvaluationService evaluationService, CatalogService catalogService) {
        this.question143Service = question143Service;
        this.question29Service = question29Service;
        this.evaluationService = evaluationService;
        this.catalogService = catalogService;
    }

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {
        Map<String, Object> attributes = new HashMap<>();

        switch (flowType) {
            case LOANAPPLICATION:

                attributes.put("form", new Question152Form());
                attributes.put("professionOcupations", getProfessionOcupationsFromConfiguration(id));
                attributes.put("occupations", getOcupationsFromConfiguration(id));
                attributes.put("professions", getProfessionsFromConfiguration(id));
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question152Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question152Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                Question143Form form143 = new Question143Form();
                form143.setProfession(form.getProfession());
                form143.setProfessionOcupation(form.getProfessionOcupation());

                Question29Form form29 = new Question29Form();
                form29.setOcupation(form.getOcupation());

                this.question143Service.validateForm(flowType, id, form143, locale);
                this.question29Service.validateForm(flowType, id, form29, locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question152Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                Question143Form form143 = new Question143Form();
                form143.setProfession(form.getProfession());
                form143.setProfessionOcupation(form.getProfessionOcupation());

                Question29Form form29 = new Question29Form();
                form29.setOcupation(form.getOcupation());

                this.question143Service.saveData(flowType, id, form143, locale);
                this.question29Service.saveData(flowType, id, form29, locale);
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        return null;
    }

    public List<PersonProfessionOccupation> getProfessionOcupationsFromConfiguration(Integer loanApplicationId) throws Exception {
        List<Integer> professionOccupationIds = evaluationService.getIdsCatalogFromConfigurationInProcessQuestion(
                loanApplicationId, ProcessQuestion.Question.Constants.PROFESSION_OCCUPATION_AND_POSITION, "professionOccupationIds");
        List<PersonProfessionOccupation> professionOccupations = new ArrayList<>();
        if (professionOccupationIds != null) {
            professionOccupations = catalogService.getProfessionOccupations()
                    .stream()
                    .filter(e -> professionOccupationIds.contains(e.getId()))
                    .collect(Collectors.toList());

            professionOccupations.sort(Comparator.comparing(PersonProfessionOccupation::getOccupation));
        }
        return professionOccupations;
    }

    public List<Ocupation> getOcupationsFromConfiguration(Integer loanApplicationId) throws Exception {
        List<Integer> occupationIds = evaluationService.getIdsCatalogFromConfigurationInProcessQuestion(
                loanApplicationId, ProcessQuestion.Question.Constants.PROFESSION_OCCUPATION_AND_POSITION, "occupationIds");

        List<Ocupation> occupations = null;

        if (occupationIds != null) {
            occupations = catalogService.getOcupations(Configuration.getDefaultLocale())
                    .stream()
                    .filter(e -> occupationIds.contains(e.getId()))
                    .collect(Collectors.toList());

            occupations.sort(Comparator.comparing(Ocupation::getOrderId));
        }
        return occupations;
    }

    public List<Profession> getProfessionsFromConfiguration(Integer loanApplicationId) throws Exception {
        List<Integer> professionIds = evaluationService.getIdsCatalogFromConfigurationInProcessQuestion(
                loanApplicationId, ProcessQuestion.Question.Constants.PROFESSION_OCCUPATION_AND_POSITION, "profession");

        List<Profession> professions = null;

        if (professionIds != null) {
            professions = catalogService.getProfessions(Configuration.getDefaultLocale())
                    .stream()
                    .filter(e -> professionIds.contains(e.getId()))
                    .collect(Collectors.toList());
        }
        return professions;
    }
}

