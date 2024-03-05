package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.form.Question36Form;
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

@Service("question36Service")
public class Question36Service extends AbstractQuestionService<Question36Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private PersonService personService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private MessageSource messageSource;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question36Form form = new Question36Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if (loanApplication.getCountryId() == CountryParam.COUNTRY_ARGENTINA){
                    ((Question36Form.Validator) form.getValidator()).ruc.update(ValidatorUtil.CUIT);
                    ((Question36Form.Validator) form.getValidator()).ruc.setValidRegexErrorMsg("static.message.cuit");
                    ((Question36Form.Validator) form.getValidator()).ruc.setValidPatternErrorMsg("static.message.cuit");
                }
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question36Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question36Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if (loanApplication.getCountryId() == CountryParam.COUNTRY_ARGENTINA){
                    if(!utilService.validarCuit(form.getRuc())){
                        ((Question36Form.Validator) form.getValidator()).ruc.setError(messageSource.getMessage("static.message.cuit",null,locale));
                        return;
                    }
                    Question36Form.Validator val=(Question36Form.Validator)form.getValidator();
                    val.ruc.update(ValidatorUtil.CUIT);
                    val.ruc.setValidRegexErrorMsg("static.message.cuit");
                    val.ruc.setValidPatternErrorMsg("static.message.cuit");
                }

                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question36Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);
                personDao.updateOcupatinalClient2(loanApplication.getPersonId(), ocupation.getNumber(), form.getRuc());
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

