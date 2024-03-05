package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class FraudFlag {
    private Integer id;
    private String flag;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "fraud_flag_id", null));
        setFlag(JsonUtil.getStringFromJson(json, "fraud_flag", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
