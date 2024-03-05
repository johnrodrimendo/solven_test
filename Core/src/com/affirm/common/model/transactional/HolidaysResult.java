package com.affirm.common.model.transactional;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HolidaysResult implements Serializable {

    private List<Date> holidays;
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public HolidaysResult() {
        holidays = new ArrayList<>();
    }

    public List<Date> getHolidays() {
        return holidays;
    }

    public void setHolidays(List<Date> holidays) {
        this.holidays = holidays;
    }

    public void addHoliday(Date holiday) {
        holidays.add(holiday);
    }
}

