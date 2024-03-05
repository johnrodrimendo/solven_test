package com.affirm.backoffice.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class EntityError implements Serializable {
    private Integer entityErrorId;
    private String error;
    private Date registerDate;
    private Integer entityId;

    public void fillFromDb(JSONObject json) {
        setEntityErrorId(JsonUtil.getIntFromJson(json, "entity_error_id", null));
        setError(JsonUtil.getStringFromJson(json, "error", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
    }

    public Integer getEntityErrorId() {
        return entityErrorId;
    }

    public void setEntityErrorId(Integer entityErrorId) {
        this.entityErrorId = entityErrorId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }
}
