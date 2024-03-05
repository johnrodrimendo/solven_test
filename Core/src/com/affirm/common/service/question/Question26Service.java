package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.ActivityType;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.catalog.SubActivityType;
import com.affirm.common.model.form.Question26Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EvaluationService;
import com.affirm.common.service.PersonService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service("question26Service")
public class Question26Service extends AbstractQuestionService<Question26Form> {

    public static final int ACTIVITY_TYPE_CAS = -1;
    public static final int ACTIVITY_IND_FORMAL = -2;
    public static final int ACTIVITY_IND_INFORMAL = -3;

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private PersonService personService;
    @Autowired
    private EvaluationService evaluationService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question26Form form = new Question26Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());

                if (fillSavedData) {
                    PersonOcupationalInformation ocupationalInformation = personService.getOcupationalInformation(loanApplication.getPersonId(), PersonOcupationalInformation.PRINCIPAL, locale);
                    if (ocupationalInformation != null) {
                        form.setActivityType(ocupationalInformation.getActivityType().getId());
                    }
                }

                attributes.put("loanApplication", loanApplication);
//                attributes.put("yearFrom", Calendar.getInstance().get(Calendar.YEAR) - 50);
//                attributes.put("yearTo", Calendar.getInstance().get(Calendar.YEAR));
                attributes.put("form", form);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question26Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                switch (form.getActivityType()) {
                    case ActivityType.DEPENDENT:
                        return "DEPENDENT";
                    case ACTIVITY_IND_FORMAL:
                    case ACTIVITY_IND_INFORMAL:
                    case ActivityType.INDEPENDENT:
                        return "INDEPENDENT";
                    case ActivityType.UNEMPLOYED:
                        return "NO_INCOME";
                    case ActivityType.MONOTRIBUTISTA:
                        return "MONOTRIBUTISTA";
                    case ActivityType.PENSIONER:
                        return "PENSIONER";
                    case ACTIVITY_TYPE_CAS:
                        return "CAS";
                    default:
                        return "DEFAULT";
                }
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question26Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question26Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                personDao.cleanOcupationalInformation(loanApplication.getPersonId(), PersonOcupationalInformation.PRINCIPAL);

                if(form.getActivityType() == ACTIVITY_TYPE_CAS){
                    personDao.updateOcupationalActivityType(loanApplication.getPersonId(), PersonOcupationalInformation.PRINCIPAL, ActivityType.INDEPENDENT);
                    personDao.updateOcupatinalSubActivityType(loanApplication.getPersonId(), PersonOcupationalInformation.PRINCIPAL, SubActivityType.PROFESSIONAL_SERVICE);
                }else if(form.getActivityType() == ACTIVITY_IND_FORMAL || form.getActivityType() == ACTIVITY_IND_INFORMAL){
                    personDao.updateOcupationalActivityType(loanApplication.getPersonId(), PersonOcupationalInformation.PRINCIPAL, ActivityType.INDEPENDENT);
                    personDao.updateOcupationalFormal(loanApplication.getPersonId(), PersonOcupationalInformation.PRINCIPAL, form.getActivityType() == ACTIVITY_IND_FORMAL);
                }else{
                    personDao.updateOcupationalActivityType(loanApplication.getPersonId(), PersonOcupationalInformation.PRINCIPAL, form.getActivityType());
                    if (form.getActivityType() == ActivityType.PENSIONER || form.getActivityType() == ActivityType.STUDENT || form.getActivityType() == ActivityType.HOUSEKEEPER) {
                        JSONArray arr = catalogService.getActivityType(locale, form.getActivityType()).getJsDefaults();
                        Integer months = utilService.getDefaultValueField(arr, "employment_time", loanApplication.getCountryId());
                        if (months != null) {
                            Date startDate = utilService.addMonths(new Date(), -1 * months);
                            personService.updateOcupationalStartDate(loanApplication.getPersonId(), PersonOcupationalInformation.PRINCIPAL, startDate);
                        }

                        Integer income = utilService.getDefaultValueField(arr, "income", loanApplication.getCountryId());
                        if (income != null)
                            personDao.updateFixedGrossIncome(loanApplication.getPersonId(), PersonOcupationalInformation.PRINCIPAL, income);
                    }
                }

                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                ProcessQuestion currentQuestion = evaluationService.getQuestionFromEvaluationProcess(loanApplication, null, ProcessQuestion.Question.Constants.PRINCIPAL_INCOME_TYPE, null, null);
                if (currentQuestion.getSkip() != null && currentQuestion.getSkip() && currentQuestion.getConfiguration() != null) {
                    Integer defaultActivityType = JsonUtil.getIntFromJson(currentQuestion.getConfiguration(), "defaultActivityType", null);
                    if (defaultActivityType != null) {
                        if (saveData) {
                            personDao.cleanOcupationalInformation(loanApplication.getPersonId(), PersonOcupationalInformation.PRINCIPAL);
                            personDao.updateOcupationalActivityType(loanApplication.getPersonId(), PersonOcupationalInformation.PRINCIPAL, defaultActivityType);
                        }
                        return "DEFAULT";
                    }
                }
                break;
        }
        return null;
    }

}

