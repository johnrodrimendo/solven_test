package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.catalog.StudyLevel;
import com.affirm.common.model.form.Question24Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("question24Service")
public class Question24Service extends AbstractQuestionService<Question24Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private EvaluationService evaluationService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question24Form form = new Question24Form();
        switch (flowType) {
            case LOANAPPLICATION:

                List<StudyLevel> studyLevels = new ArrayList<>();
                List<Integer> studyLevelIds = evaluationService.getIdsCatalogFromConfigurationInProcessQuestion(
                        id, ProcessQuestion.Question.Constants.STUDIES_LEVEL, "studyLevelIds");
                if(studyLevelIds != null){
                    for(Integer  studyId : studyLevelIds){
                        studyLevels.add(catalogService.getStudyLevel(locale, studyId));
                    }
                    studyLevels = studyLevels.stream().sorted(Comparator.comparingInt(s -> s.getOrder())).collect(Collectors.toList());
                }else{
                    studyLevels = catalogService.getStudyLevels(locale).stream().filter(s -> s.getShowMarketplace()).sorted(Comparator.comparingInt(s -> s.getOrder())).collect(Collectors.toList());
                }

                if (fillSavedData) {
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                    Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
                    form.setStudyLevel(person.getStudyLevel() != null ? person.getStudyLevel().getId() : null);
                }

                attributes.put("form", form);
                attributes.put("studyLevels", studyLevels);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question24Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "INSTITUTO_O_MAS";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question24Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question24Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                personDao.updateStudyLevel(loanApplication.getPersonId(), form.getStudyLevel());
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

