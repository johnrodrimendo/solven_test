package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.form.Question47Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.service.PersonService;
import com.affirm.common.util.FormValidationException;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question47Service")
public class Question47Service extends AbstractQuestionService<Question47Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private PersonService personService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question47Form form = new Question47Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);
                if (ocupation != null) {
                    attributes.put("showOtherIncome", ocupation.getNumber() == PersonOcupationalInformation.PRINCIPAL);
                }

                if (fillSavedData) {
                    PersonOcupationalInformation ocupationalInformation = personService.getOcupationalInformation(loanApplication.getPersonId(), (int) params.get(VIEW_PARAM_OCUPATION_NUMBER), locale);
                    if (ocupationalInformation != null) {
                        form.setRent(ocupationalInformation.getFixedGrossIncome() != null ? ocupationalInformation.getFixedGrossIncome().intValue() : null);
                        form.setBelongings(ocupationalInformation.getBelonging() != null ? ocupationalInformation.getBelonging().stream().map(b -> b.getId()).toArray(Integer[]::new) : null);
                    }
                }

                ((Question47Form.Validator) form.getValidator()).configValidator(loanApplication.getCountryId());
                attributes.put("form", form);
                attributes.put("loanApplication", loanApplication);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question47Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question47Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                if (form.getBelongings() == null || form.getBelongings().length < 1) {
                    throw new FormValidationException("Es necesario elegir al menos un rubro.");
                }

                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                ((Question47Form.Validator) form.getValidator()).configValidator(loanApplication.getCountryId());
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question47Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);
                personDao.updateFixedGrossIncome(loanApplication.getPersonId(), ocupation.getNumber(), form.getRent());
                // TODO alter table to ssupport array belonging
                String arrBelongings = Arrays.toString(form.getBelongings());
                personDao.updateBelonging(loanApplication.getPersonId(), ocupation.getNumber(), arrBelongings.substring(1, arrBelongings.length() - 1));
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

