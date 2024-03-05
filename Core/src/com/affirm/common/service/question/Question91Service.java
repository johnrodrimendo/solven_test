package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.form.Question91Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.model.transactional.SunatResult;
import com.affirm.common.service.PersonService;
import com.affirm.common.util.FormValidationException;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("question91Service")
public class Question91Service extends AbstractQuestionService<Question91Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private PersonService personService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question91Form form = new Question91Form();
        switch (flowType) {
            case LOANAPPLICATION:

                if (fillSavedData) {
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                    PersonOcupationalInformation ocupationalInformation = personService.getOcupationalInformation(loanApplication.getPersonId(), (int) params.get(VIEW_PARAM_OCUPATION_NUMBER), locale);
                    if (ocupationalInformation != null) {
                        form.setStartDate(ocupationalInformation.getStartDate() != null ? new SimpleDateFormat("MM/yyyy").format(ocupationalInformation.getStartDate()) : null);
                    }
                }

                attributes.put("form", form);
                attributes.put("yearFrom", Calendar.getInstance().get(Calendar.YEAR) - 50);
                attributes.put("yearTo", Calendar.getInstance().get(Calendar.YEAR));
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question91Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question91Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                if (!form.getValidator().isHasErrors()) {
                    if (form.getStartDate() != null && !form.getStartDate().isEmpty()) {
                        Date startDate = new SimpleDateFormat("MM/yyyy").parse(form.getStartDate());
                        if (startDate.after(new Date())) {
                            throw new FormValidationException("La fecha es inválida");
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question91Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);
                personService.updateOcupationalStartDate(loanApplication.getPersonId(), ocupation.getNumber(), form.getStartDate() != null ? new SimpleDateFormat("MM/yyyy").parse(form.getStartDate()) : null);
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                SunatResult sunatResult = personDao.getSunatResult(loanApplication.getPersonId());
                if (sunatResult != null && sunatResult.getRegisterDate() != null) {
                    if (saveData) {
                        PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);
                        personService.updateOcupationalStartDate(loanApplication.getPersonId(), ocupation.getNumber(), sunatResult.getRegisterDate());
                    }
                    return "DEFAULT";
                }
                break;
        }
        return null;
    }

}

