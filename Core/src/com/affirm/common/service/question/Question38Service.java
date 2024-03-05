package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.form.Question38Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.service.PersonService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.ValidatorUtil;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question38Service")
public class Question38Service extends AbstractQuestionService<Question38Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UtilService utilService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private PersonService personService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question38Form form = new Question38Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                ((Question38Form.Validator) form.getValidator()).configValidator(loanApplication.getCountryId());
                if (loanApplication.getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
                    ((Question38Form.Validator) form.getValidator()).ruc.update(ValidatorUtil.CUIT_COMPANY);
                    ((Question38Form.Validator) form.getValidator()).ruc.setValidRegexErrorMsg("static.message.cuit");
                    ((Question38Form.Validator) form.getValidator()).ruc.setValidPatternErrorMsg("static.message.cuit");
                }

                if (fillSavedData) {
                    PersonOcupationalInformation ocupationalInformation = personService.getOcupationalInformation(loanApplication.getPersonId(), (int) params.get(VIEW_PARAM_OCUPATION_NUMBER), locale);
                    if (ocupationalInformation != null) {
                        form.setRuc(ocupationalInformation.getRuc());
                    }
                }

                attributes.put("form", form);

                if (loanApplication.getCountryId() == CountryParam.COUNTRY_PERU) {
                    attributes.put("regimes", PersonOcupationalInformation.Regime.values());
                } else {
                    attributes.put("regimes", Collections.emptyList());
                }
                attributes.put("loanApplication", loanApplication);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question38Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question38Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                ((Question38Form.Validator) form.getValidator()).configValidator(loanApplication.getCountryId());

                switch (loanApplication.getCountryId()) {
                    case CountryParam.COUNTRY_ARGENTINA: {
                        if (!form.isNotLegal() && !utilService.validarCuit(form.getRuc())) {
                            ((Question38Form.Validator) form.getValidator()).ruc.setError(messageSource.getMessage("static.message.cuit", null, locale));
                            return;
                        }

                        Question38Form.Validator val = (Question38Form.Validator) form.getValidator();
                        val.ruc.update(ValidatorUtil.CUIT_COMPANY);
                        val.ruc.setValidRegexErrorMsg("static.message.cuit");
                        val.ruc.setValidPatternErrorMsg("static.message.cuit");

                        break;
                    }
                    case CountryParam.COUNTRY_COLOMBIA: {
                        if (!form.isNotLegal() && !utilService.validarNit(form.getRuc())) {
                            ((Question38Form.Validator) form.getValidator()).ruc.setError(messageSource.getMessage("static.message.nit", null, locale));
                            return;
                        }

                        Question38Form.Validator val = (Question38Form.Validator) form.getValidator();
                        val.ruc.update(ValidatorUtil.NIT);
                        val.ruc.setValidRegexErrorMsg("static.message.nit");
                        val.ruc.setValidPatternErrorMsg("static.message.nit");

                        break;
                    }
                }

                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question38Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);

                if (!form.isNotLegal()) {
                    if (loanApplication.getCountryId() == CountryParam.COUNTRY_PERU) {
                        personService.updateOcupationalRuc(form.getRuc(), loanApplication.getPersonId(), true, form.getRegime(), loanApplication);
                    } else {
                        personService.updateOcupationalRuc(form.getRuc(), loanApplication.getPersonId(), false, loanApplication);
                    }
                }

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

