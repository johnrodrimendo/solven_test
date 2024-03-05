package com.affirm.common.model;


import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class ReportMonthsPeriod {
    private Date from;
    private Date to;
    private String period;

    public void fillFromDb(JSONObject json) {
        setPeriod(JsonUtil.getStringFromJson(json, "period", null));
        setFrom(JsonUtil.getPostgresDateFromJson(json, "date_from", null));
        setTo(JsonUtil.getPostgresDateFromJson(json, "date_to", null));
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
