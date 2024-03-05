package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class FraudFlagStatus {
    private Integer id;
    private String status;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "fraud_flag_status_id", null));
        setStatus(JsonUtil.getStringFromJson(json, "fraud_flag_status", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
