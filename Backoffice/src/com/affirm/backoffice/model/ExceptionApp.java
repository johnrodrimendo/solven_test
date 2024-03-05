package com.affirm.backoffice.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class ExceptionApp implements Serializable {
    private Integer exceptionId;
    private String stacktrace;
    private Date registerDate;

    public void fillFromDb(JSONObject json) {
        setExceptionId(JsonUtil.getIntFromJson(json, "exception_id", null));
        setStacktrace(JsonUtil.getStringFromJson(json, "stacktrace", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
    }

    public Integer getExceptionId() { return exceptionId; }

    public void setExceptionId(Integer exceptionId) { this.exceptionId = exceptionId; }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) { this.stacktrace = stacktrace; }

    public Date getRegisterDate() { return registerDate; }

    public void setRegisterDate(Date registerDate) { this.registerDate = registerDate; }
}
