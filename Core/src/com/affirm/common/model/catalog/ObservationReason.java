package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

public class ObservationReason implements Serializable {

    private Integer id;
    private String reason;

    public void fillFromDb(JSONObject json, Locale locale) {
        setId(JsonUtil.getIntFromJson(json, "observation_reason_id", null));
        setReason(JsonUtil.getStringFromJson(json, "observation_reason", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
