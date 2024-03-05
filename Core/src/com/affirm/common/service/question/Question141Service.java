package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.form.Question141Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.service.CatalogService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question141Service")
public class Question141Service extends AbstractQuestionService<Question141Form> {

    private final CatalogService catalogService;

    private final LoanApplicationDAO loanApplicationDAO;
    private final PersonDAO personDAO;

    public Question141Service(CatalogService catalogService, LoanApplicationDAO loanApplicationDAO, PersonDAO personDAO) {
        this.catalogService = catalogService;
        this.loanApplicationDAO = loanApplicationDAO;
        this.personDAO = personDAO;
    }

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                attributes.put("banks", catalogService.getBanks(Entity.WENANCE, false, CountryParam.COUNTRY_ARGENTINA));
                attributes.put("form", new Question141Form());
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question141Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question141Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question141Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(id, locale);
                personDAO.updatePersonBankAccountInformation(loanApplication.getPersonId(), form.getBankId(), null, null, null, null);
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        return null;
    }
}
