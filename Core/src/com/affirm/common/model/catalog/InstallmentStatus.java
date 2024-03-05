package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by dev5 on 23/03/18.
 */
public class InstallmentStatus {

    private int id;
    private String status;
    private String statusKey;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "installment_status_id", null));
        setStatus(JsonUtil.getStringFromJson(json, "installment_status", null));
        setStatusKey(JsonUtil.getStringFromJson(json, "text_int", null));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusKey() {
        return statusKey;
    }

    public void setStatusKey(String statusKey) {
        this.statusKey = statusKey;
    }
}
