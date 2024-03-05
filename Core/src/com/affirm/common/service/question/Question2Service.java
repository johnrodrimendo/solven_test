package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.SelfEvaluationDAO;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.form.Question2Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.SelfEvaluation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.common.service.WebscrapperService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question2Service")
public class Question2Service extends AbstractQuestionService<Question2Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private SelfEvaluationDAO selfEvaluationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private UtilService utilService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private WebscrapperService webscrapperService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question2Form form = new Question2Form();
        switch (flowType) {
            case LOANAPPLICATION:
            case SELFEVALUATION:

                if (fillSavedData) {
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                    Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
                    form.setBirthday(person.getBirthday() != null ? new SimpleDateFormat("dd/MM/yyyy").format(person.getBirthday()) : null);
                }

                attributes.put("yearFrom", 1900);
                attributes.put("yearTo", Calendar.getInstance().get(Calendar.YEAR));
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question2Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
            case SELFEVALUATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question2Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
            case SELFEVALUATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question2Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                personDao.updateBirthday(loanApplication.getPersonId(), utilService.parseDate(form.getBirthday(), "dd/MM/yyyy", locale));
                Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
                /*if (person.getDocumentType().getId() == IdentityDocumentType.CE) {
                    webscrapperService.setCountry(catalogService.getCountryParam(CountryParam.COUNTRY_PERU));
                    webscrapperService.callMigracionesBot(person.getDocumentNumber(), person.getBirthday(), null);
                }*/
                break;
            case SELFEVALUATION:
                SelfEvaluation selfEvaluation = selfEvaluationDao.getSelfEvaluation(id, locale);
                personDao.updateBirthday(selfEvaluation.getPersonId(), utilService.parseDate(form.getBirthday(), "dd/MM/yyyy", locale));
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
            case SELFEVALUATION:
                break;
        }
        return null;
    }

}

