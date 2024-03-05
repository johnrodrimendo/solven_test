package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class FraudFlagRejectionReason {

    private Integer id;
    private String reason;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "fraud_flag_rejection_reason_id", null));
        setReason(JsonUtil.getStringFromJson(json, "fraud_flag_rejection_reason", null));
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
