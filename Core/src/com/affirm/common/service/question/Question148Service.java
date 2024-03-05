package com.affirm.common.service.question;

import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.OccupationArea;
import com.affirm.common.model.catalog.Ocupation;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.form.Question148Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.LoanApplicationService;
import com.affirm.common.service.PersonService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service("question148Service")
public class Question148Service extends AbstractQuestionService<Question148Form> {

    private final PersonDAO personDAO;

    private final LoanApplicationService loanApplicationService;
    private final PersonService personService;
    private final EvaluationService evaluationService;
    private final CatalogService catalogService;

    public Question148Service(PersonDAO personDAO, LoanApplicationService loanApplicationService, PersonService personService, EvaluationService evaluationService, CatalogService catalogService) {
        this.personDAO = personDAO;

        this.loanApplicationService = loanApplicationService;
        this.personService = personService;
        this.evaluationService = evaluationService;
        this.catalogService = catalogService;
    }

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        Question148Form form = new Question148Form();

        switch (flowType) {
            case LOANAPPLICATION: {
                List<Integer> areaIds = evaluationService.getIdsCatalogFromConfigurationInProcessQuestion(id, ProcessQuestion.Question.Constants.FDM_AREA_OCCUPATION, "areaIds");
                List<Integer> occupationIds = evaluationService.getIdsCatalogFromConfigurationInProcessQuestion(id, ProcessQuestion.Question.Constants.FDM_AREA_OCCUPATION, "occupationIds");

                List<OccupationArea> areas = catalogService.getOccupationAreas(locale).stream().filter(a -> areaIds.contains(a.getId())).collect(Collectors.toList());
                List<Ocupation> occupations = catalogService.getOcupations(locale).stream().filter(o -> occupationIds.contains(o.getId())).collect(Collectors.toList());

                attributes.put("areas", areas);
                attributes.put("occupations", occupations);
                attributes.put("form", form);

                break;
            }
        }

        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question148Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }

        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question148Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question148Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = this.loanApplicationService.getLoanApplicationById(id);
                PersonOcupationalInformation occupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);

                personDAO.updateOcupationAreaId(loanApplication.getPersonId(), occupation.getNumber(), form.getArea());
                personDAO.updateOcupatinalOcupation(loanApplication.getPersonId(), occupation.getNumber(), form.getOccupation());

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
}
