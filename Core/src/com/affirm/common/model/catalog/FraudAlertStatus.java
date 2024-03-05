package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class FraudAlertStatus {
    public static Integer NUEVO = 1;
    public static Integer POR_REVISAR = 2;
    public static Integer REVISADO = 3;

    public Integer id;
    public String status;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "fraud_alert_status_id", null));
        setStatus(JsonUtil.getStringFromJson(json, "fraud_alert_status", null));
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
