package com.affirm.common.service.question;

import com.affirm.banbif.model.ParentsNameForm;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.Relationship;
import com.affirm.common.model.transactional.LoanApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question146Service")
public class Question146Service extends AbstractQuestionService<ParentsNameForm> {

    @Autowired
    private LoanApplicationDAO loanApplicationDAO;

    @Autowired
    private PersonDAO personDAO;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:

                attributes.put("form", new ParentsNameForm());

                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, ParentsNameForm form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, ParentsNameForm form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, ParentsNameForm form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(id, locale);

                personDAO.deletePreviousReferrals(loanApplication.getPersonId());

                personDAO.createReferral(
                        loanApplication.getPersonId(),
                        null,
                        form.getFatherNames() + " " + form.getFatherSurnames(),
                        Relationship.FATHER,
                        String.valueOf(CountryParam.COUNTRY_PERU),
                        "",
                        null, locale);

                personDAO.createReferral(
                        loanApplication.getPersonId(),
                        null,
                        form.getMotherNames() + " " + form.getMotherSurnames(),
                        Relationship.MOTHER,
                        String.valueOf(CountryParam.COUNTRY_PERU),
                        "",
                        null, locale);
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        return null;
    }
}
