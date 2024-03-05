package com.affirm.client.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by jrodriguez on 08/06/16.
 */
public class RebateTransaction {

    private String errorMessage;

    public void fillFromDb(JSONObject json) {
        setErrorMessage(JsonUtil.getStringFromJson(json, "mensaje", null));
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
