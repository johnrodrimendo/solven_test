package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.Holiday;
import com.affirm.common.model.form.Question115Form;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.LoanApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service("question115Service")
public class Question115Service extends AbstractQuestionService<Question115Form> {

    public final int START_HOUR = 9;
    public final int START_MINUTE = 0;
    public final int END_HOUR = 18;
    public final int END_MINUTE = 30;
    public final int SATURDAY = 6;
    public final int END_HOUR_SATURDAY = 13;

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private LoanApplicationService loanApplicationService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        Question115Form form = new Question115Form();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);

                List<Holiday> holidays = catalogService.getHolidaysByCountry(loanApplication.getCountryId());
                List<Date> availableDates = getAvailableDates(holidays, 3);

                if (fillSavedData) {
                    form.setAssistedProcess(loanApplication.getAssistedProcess());
                }else{
                    // This is the only way to know if its client
                    loanApplicationDao.registerAssistedProcessSchedule(loanApplication.getId(), null);
                }

                attributes.put("officeStartHour", START_HOUR);
                attributes.put("officeStartMinute", START_MINUTE);
                attributes.put("officeEndHour", END_HOUR);
                attributes.put("officeEndMinute", END_MINUTE);
                attributes.put("availableDates", availableDates);
                attributes.put("personFirstName", person.getFirstName());
                attributes.put("form", form);
                attributes.put("officeEndHourSaturday", END_HOUR_SATURDAY);
                attributes.put("saturday", SATURDAY);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question115Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                if (form.getAssistedProcess()) {
                    return "ASSISTED";
                }
                return "WEB";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question115Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                form.getValidator().validate(locale);
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question115Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                if (form.getAssistedProcess() && form.getDate() != null && form.getTime() != null) {
                    Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(form.getDate() + " " + form.getTime());
                    loanApplicationDao.registerAssistedProcessSchedule(id, date);
                }
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                if(loanApplication.getEntityId() != null)
                    return "WEB";

                boolean hasAnyApprovedPreEvaluation = loanApplicationService.hasAnyApprovedPreEvaluation(loanApplication.getId(), Arrays.asList(Entity.COMPARTAMOS, Entity.AELU, Entity.ABACO, Entity.ACCESO));
                if(!hasAnyApprovedPreEvaluation)
                    return "WEB";

                break;
        }
        return null;
    }

    private boolean isWeekend(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    private boolean isHoliday(List<Holiday> holidays, Calendar calendar) {
        for(Holiday holiday : holidays){
            Calendar holCal = Calendar.getInstance();
            holCal.setTime(holiday.getDate());
            if(holCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                    holCal.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR))
                return true;
        }
        return false;
    }

    private boolean atLeast(Calendar now, int hours) {
        return now.get(Calendar.HOUR_OF_DAY) + hours <= END_HOUR;
    }

    public List<Date> getAvailableDates(List<Holiday> holidays, int days) {
        List<Date> availableDates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        if (!isWeekend(calendar) && !isHoliday(holidays, calendar) && atLeast(calendar, 2)) {
            availableDates.add(calendar.getTime());
            days--;
        }
        calendar.add(Calendar.DATE, 1);

        while (days > 0) {
            if (!isWeekend(calendar) && !isHoliday(holidays, calendar)) {
                availableDates.add(calendar.getTime());
                days--;
            }
            calendar.add(Calendar.DATE, 1);
        }

        return availableDates;
    }

}

