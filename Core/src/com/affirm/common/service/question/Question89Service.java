package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.form.Question89Form;
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

@Service("question89Service")
public class Question89Service extends AbstractQuestionService<Question89Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonService personService;
    @Autowired
    private PersonDAO personDao;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question89Form form = new Question89Form();
        switch (flowType) {
            case LOANAPPLICATION:

                if (fillSavedData) {
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                    PersonOcupationalInformation ocupationalInformation = personService.getOcupationalInformation(loanApplication.getPersonId(), (int) params.get(VIEW_PARAM_OCUPATION_NUMBER), locale);
                    if (ocupationalInformation != null) {
                        form.setStartDate(ocupationalInformation.getStartDate() != null ? new SimpleDateFormat("MM/yyyy").format(ocupationalInformation.getStartDate()) : null);
                    }
                }

                attributes.put("yearFrom", Calendar.getInstance().get(Calendar.YEAR) - 50);
                attributes.put("yearTo", Calendar.getInstance().get(Calendar.YEAR));
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question89Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question89Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                if (!form.getValidator().isHasErrors() && form.getStartDate() != null && !form.getStartDate().isEmpty()) {
                    Date startDate = new SimpleDateFormat("MM/yyyy").parse(form.getStartDate());
                    if (startDate.after(new Date())) {
                        throw new FormValidationException("La fecha es inválida");
                    }
                }
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question89Form form, Locale locale) throws Exception {
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

                // ESTO SE COMENTO PORQUE PARECE QUE LO DE ABAJO ES UNA VERSION MEJORADA
//                SunatResult sunatResult = personDao.getSunatResult(loanApplication.getPersonId());
//                if (sunatResult != null && sunatResult.getRegisterDate() != null) {
//                    if (saveData) {
//                        PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);
//                        personService.updateOcupationalStartDate(loanApplication.getPersonId(), ocupation.getNumber(), sunatResult.getRegisterDate());
//                    }
//                    return "DEFAULT";
//                }

                SunatResult sunatResult = personDao.getSunatResult(loanApplication.getPersonId());
                if (loanApplication.getCountryId().equals(CountryParam.COUNTRY_PERU)) {
                    if (sunatResult != null) {
                        if (sunatResult.getStartupDate() != null) {
                            String sd = sunatResult.getStartupDate().getMonth() + "/" + sunatResult.getStartupDate().getYear();
                            if (saveData) {
                                PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);
                                personService.updateOcupationalStartDate(loanApplication.getPersonId(), ocupation.getNumber(), sunatResult.getStartupDate() != null ? new SimpleDateFormat("MM/yyyy").parse(sd) : null);
                            }
                            return "DEFAULT";
                        }
                    }
                }

                break;
        }
        return null;
    }

}

