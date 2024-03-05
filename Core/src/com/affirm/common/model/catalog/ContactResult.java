package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jrodriguez on 07/09/16.
 */
public class ContactResult implements Serializable {

    private Integer id;
    private String type;
    private String result;
    private Boolean dateRequired;
    private Boolean amountRequired;
    private Boolean active;

    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "contact_result_id", null));
        setType(JsonUtil.getStringFromJson(json, "contact_type", null));
        setResult(JsonUtil.getStringFromJson(json, "contact_result", null));
        setDateRequired(JsonUtil.getBooleanFromJson(json, "date_required", null));
        setAmountRequired(JsonUtil.getBooleanFromJson(json, "amount_required", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Boolean getDateRequired() {
        return dateRequired;
    }

    public void setDateRequired(Boolean dateRequired) {
        this.dateRequired = dateRequired;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getAmountRequired() {
        return amountRequired;
    }

    public void setAmountRequired(Boolean amountRequired) {
        this.amountRequired = amountRequired;
    }
}
