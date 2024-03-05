package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.form.Question35Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.service.PersonService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.ValidatorUtil;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question35Service")
public class Question35Service extends AbstractQuestionService<Question35Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private UtilService utilService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private PersonService personService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question35Form form = new Question35Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if (loanApplication.getCountryId() == CountryParam.COUNTRY_ARGENTINA){
                    ((Question35Form.Validator) form.getValidator()).ruc.update(ValidatorUtil.CUIT);
                    ((Question35Form.Validator) form.getValidator()).ruc.setValidRegexErrorMsg("static.message.cuit");
                    ((Question35Form.Validator) form.getValidator()).ruc.setValidPatternErrorMsg("static.message.cuit");
                }

                if (fillSavedData) {
                    PersonOcupationalInformation ocupationalInformation = personService.getOcupationalInformation(loanApplication.getPersonId(), (int) params.get(VIEW_PARAM_OCUPATION_NUMBER), locale);
                    if (ocupationalInformation != null) {
                        form.setRuc(ocupationalInformation.getClient1Ruc());
                        form.setMoreThan65(ocupationalInformation.getClient1Ruc65());
                    }
                }

                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question35Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return form.getMoreThan65() ? "MORE_65_PERCENT" : "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question35Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if (loanApplication.getCountryId() == CountryParam.COUNTRY_ARGENTINA){
                    if(!utilService.validarCuit(form.getRuc())){
                        ((Question35Form.Validator) form.getValidator()).ruc.setError(messageSource.getMessage("static.message.cuit",null,locale));
                        return;
                    }
                    Question35Form.Validator val=(Question35Form.Validator)form.getValidator();
                    val.ruc.update(ValidatorUtil.CUIT);
                    val.ruc.setValidRegexErrorMsg("static.message.cuit");
                    val.ruc.setValidPatternErrorMsg("static.message.cuit");
                }

                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question35Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);
                personDao.updateOcupatinalClient1(loanApplication.getPersonId(), ocupation.getNumber(), form.getRuc());
                personDao.updateClient1Ruc65(loanApplication.getPersonId(), ocupation.getNumber(), form.getMoreThan65());
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

