package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.ActivityType;
import com.affirm.common.model.form.Question120Form;
import com.affirm.common.model.transactional.FrequencySalary;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("question120Service")
public class Question120Service extends AbstractQuestionService<Question120Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        Question120Form form = new Question120Form();

        switch (flowType) {
            case LOANAPPLICATION:
                attributes.put("form", form);
                break;
        }

        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question120Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question120Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question120Form form, Locale locale) throws Exception {
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
        PersonOcupationalInformation ocupationalInformation = personDao.getPersonOcupationalInformation(locale, loanApplication.getPersonId())
                .stream().filter(o -> o.getActivityType() != null && o.getActivityType().getId().equals(ActivityType.DEPENDENT)).findFirst().orElse(null);
        FrequencySalary frequencySalary = new FrequencySalary();
        frequencySalary.setFrequencyType(form.getFrequencyType());
        List<Integer> days = new ArrayList<>();

        switch (form.getFrequencyType()) {
            case 'M':
                days.add(form.getMonthlyPaymentDay());
                break;
            case 'Q':
                days.add(form.getFirstBiweeklyPaymentDay());
                days.add(form.getSecondBiweeklyPaymentDay());
                break;
            case 'S':
                days.add(form.getWeeklyPaymentDay());
                break;
        }
        frequencySalary.setDays(days);

        personDao.updateFrequencySalary(loanApplication.getPersonId(), ocupationalInformation.getNumber(), frequencySalary);
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        return null;
    }
}
