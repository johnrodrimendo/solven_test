package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.Ocupation;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.form.Question29Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.PersonService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("question29Service")
public class Question29Service extends AbstractQuestionService<Question29Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private PersonService personService;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private CatalogService catalogService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question29Form form = new Question29Form();
        switch (flowType) {
            case LOANAPPLICATION:

                if (fillSavedData) {
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                    PersonOcupationalInformation ocupationalInformation = personService.getOcupationalInformation(loanApplication.getPersonId(), (int) params.get(VIEW_PARAM_OCUPATION_NUMBER), locale);
                    if (ocupationalInformation != null) {
                        form.setOcupation(ocupationalInformation.getOcupation() != null ? ocupationalInformation.getOcupation().getId() : null);
                    }
                }

                List<Integer> occupationIds = evaluationService.getIdsCatalogFromConfigurationInProcessQuestion(
                        id, ProcessQuestion.Question.Constants.OCUPATION_WORK_PLACE_DEPENDENT, "occupationIds");

                List<Ocupation> occupations = null;

                if (occupationIds != null) {
                    occupations = catalogService.getOcupations(locale)
                            .stream()
                            .filter(e -> occupationIds.contains(e.getId()))
                            .collect(Collectors.toList());

                    occupations.sort(Comparator.comparing(Ocupation::getOrderId));
                }

                attributes.put("form", form);
                attributes.put("occupations", occupations);

                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question29Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question29Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question29Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);
                personDao.updateOcupatinalOcupation(loanApplication.getPersonId(), ocupation.getNumber(), form.getOcupation());
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

