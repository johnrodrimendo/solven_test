package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by john on 22/11/16.
 */
public class IcarValidationField implements Serializable {

    public Integer id;
    public Integer icarValidationId;
    public String code;
    public String value;

    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "icar_validation_field_id", null));
        setIcarValidationId(JsonUtil.getIntFromJson(json, "icar_validation_id", null));
        setCode(JsonUtil.getStringFromJson(json, "code", null));
        setValue(JsonUtil.getStringFromJson(json, "value", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIcarValidationId() {
        return icarValidationId;
    }

    public void setIcarValidationId(Integer icarValidationId) {
        this.icarValidationId = icarValidationId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
