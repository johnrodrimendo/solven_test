package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.form.Question133Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.service.PersonService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question133Service")
public class Question133Service extends AbstractQuestionService<Question133Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonService personService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                attributes.put("form", new Question133Form());
                attributes.put("homeAddress", personService.getContactInformation(loanApplication.getPersonId()).getFullAddress());
                PersonOcupationalInformation personOcupationalInformation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), Configuration.getDefaultLocale());
                attributes.put("jobAddress", personOcupationalInformation != null ? personOcupationalInformation.getFullAddress() : null);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question133Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question133Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question133Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.TARJETA_PERUANA_DELIVERY_PLACE.getKey(), form.getPlace());
                loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());

        switch (flowType) {
            case LOANAPPLICATION:
//                TOMAR POR DEFECTO HOMEADDRESS SI NO TIENE UN JOBADDRESS
                PersonOcupationalInformation personOcupationalInformation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), Configuration.getDefaultLocale());
                if(personOcupationalInformation == null || personOcupationalInformation.getAddressUbigeo() == null) {
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.TARJETA_PERUANA_DELIVERY_PLACE.getKey(), "H");
                    loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                    return "DEFAULT";
                }

                break;
        }
        return null;
    }
}

