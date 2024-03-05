package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.form.Question140Form;
import com.affirm.common.model.form.Question143Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.PersonProfessionOccupation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("question143Service")
public class Question143Service extends AbstractQuestionService<Question143Form> {

    private final LoanApplicationDAO loanApplicationDao;
    private final PersonDAO personDAO;
    private final CatalogService catalogService;
    private final EvaluationService evaluationService;

    public Question143Service(LoanApplicationDAO loanApplicationDao, PersonDAO personDAO, CatalogService catalogService, EvaluationService evaluationService) {
        this.loanApplicationDao = loanApplicationDao;
        this.personDAO = personDAO;
        this.catalogService = catalogService;
        this.evaluationService = evaluationService;
    }

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:

                /*LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                ProcessQuestion currentQuestion = evaluationService.getQuestionFromEvaluationProcess(loanApplication, null, ProcessQuestion.Question.Constants.PROFESSION_AND_OCUPATIONS, null, null);*/

                List<Integer> professionOccupationIds = evaluationService.getIdsCatalogFromConfigurationInProcessQuestion(
                        id, ProcessQuestion.Question.Constants.PROFESSION_AND_OCUPATIONS, "professionOccupationIds");

                /*if (currentQuestion.getConfiguration() != null) {
                    JSONArray jsonArray = JsonUtil.getJsonArrayFromJson(currentQuestion.getConfiguration(), "professionOccupationIds", null);
                    if (jsonArray != null) {
                        professionOccupationIds = JsonUtil.getListFromJsonArray(jsonArray, (arr, i) -> arr.getInt(i));
                    } else {
                        professionOccupationIds = null;
                    }
                } else {
                    professionOccupationIds = null;
                }*/

                List<PersonProfessionOccupation> professionOccupations = null;

                if (professionOccupationIds != null) {
                    professionOccupations = catalogService.getProfessionOccupations()
                            .stream()
                            .filter(e -> professionOccupationIds.contains(e.getId()))
                            .collect(Collectors.toList());

                    professionOccupations.sort(Comparator.comparing(PersonProfessionOccupation::getOccupation));
                }

                attributes.put("form", new Question140Form());
                attributes.put("professionOcupations", professionOccupations);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question143Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question143Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question143Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                personDAO.registerProfessionOccupation(loanApplication.getPersonId(), form.getProfessionOcupation());
                personDAO.updateProfession(loanApplication.getPersonId(), form.getProfession());
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);

                if (loanApplication != null && loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.FUNDACION_DE_LA_MUJER) {
                    return "DEFAULT";
                }
        }

        return null;
    }
}

