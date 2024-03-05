package com.affirm.backoffice.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

public class RecurrentException implements Serializable {
    ExceptionApp exception;
    int times;

    public void fillFromDb(JSONObject json) {
        ExceptionApp e = new ExceptionApp();
        e.fillFromDb(json);
        setException(e);
        setTimes(JsonUtil.getIntFromJson(json, "count", null));
    }

    public ExceptionApp getException() { return exception; }

    public void setException(ExceptionApp exception) { this.exception = exception; }

    public int getTimes() { return times; }

    public void setTimes(int times) { this.times = times; }
}
