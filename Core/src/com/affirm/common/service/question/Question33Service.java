package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.form.Question33Form;
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

@Service("question33Service")
public class Question33Service extends AbstractQuestionService<Question33Form> {

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
        Question33Form form = new Question33Form();
        switch (flowType) {
            case LOANAPPLICATION:

                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                ((Question33Form.Validator) form.getValidator()).configValidator(loanApplication.getCountryId());

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
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question33Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question33Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                ((Question33Form.Validator) form.getValidator()).configValidator(loanApplication.getCountryId());

                switch (loanApplication.getCountryId()) {
                    case CountryParam.COUNTRY_COLOMBIA: {
                        if (!form.getHaventRuc() && !utilService.validarNit(form.getRuc())) {
                            ((Question33Form.Validator) form.getValidator()).ruc.setError(messageSource.getMessage("static.message.nit", null, locale));
                            return;
                        }

                        Question33Form.Validator val = (Question33Form.Validator) form.getValidator();
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
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question33Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                personService.updateOcupationalRuc(form.getRuc(), loanApplication.getPersonId(), false, loanApplication);
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                // This question should not be skipped, but autocompleted!
                /*LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                if (loanApplication.getCountryId().equals(CountryParam.COUNTRY_PERU)) {
                    SunatResult sunatResult = personDao.getSunatResult(loanApplication.getPersonId());
                    if (sunatResult != null) {
                        if (sunatResult.getRuc() != null) {
                            if (saveData) {
                                PersonOcupationalInformation ocupation = personService.getCurrentOcupationalInformation(loanApplication.getPersonId(), locale);
                                personDao.updateOcupationalRuc(loanApplication.getPersonId(), ocupation.getNumber(), sunatResult.getRuc());
                            }
                            return "DEFAULT";
                        }
                    }
                }*/
                break;
        }
        return null;
    }

}

