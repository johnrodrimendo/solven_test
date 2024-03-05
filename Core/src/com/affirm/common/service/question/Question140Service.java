package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.form.Question140Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.service.CatalogService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question140Service")
public class Question140Service extends AbstractQuestionService<Question140Form> {

    private final LoanApplicationDAO loanApplicationDao;
    private final PersonDAO personDAO;
    private final CatalogService catalogService;

    public Question140Service(LoanApplicationDAO loanApplicationDao, PersonDAO personDAO, CatalogService catalogService) {
        this.loanApplicationDao = loanApplicationDao;
        this.personDAO = personDAO;
        this.catalogService = catalogService;
    }

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                attributes.put("form", new Question140Form());
                attributes.put("professions", catalogService.getProfessionOccupations());

                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question140Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question140Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question140Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);

                personDAO.registerProfessionOccupation(loanApplication.getPersonId(), form.getActivityType());
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        return null;
    }
}

