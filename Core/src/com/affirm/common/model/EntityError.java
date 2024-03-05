package com.affirm.common.model;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class EntityError implements Serializable {

    private Integer entityErrorId;
    private String error;
    private Date registerDate;
    private Integer entityId;
    private Entity entity;
    private Integer loanApplicationId;
    private Integer entityWSId;
    private EntityWebService entityWebService;
    private Integer lgApplicationEntityWSId;

    public void fillFromDb(JSONObject jsonObject, CatalogService catalogService) {
        setEntityErrorId(JsonUtil.getIntFromJson(jsonObject, "entity_error_id", null));
        setError(JsonUtil.getStringFromJson(jsonObject, "error", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(jsonObject, "register_date", null));
        setEntityId(JsonUtil.getIntFromJson(jsonObject, "entity_id", null));
        if (JsonUtil.getIntFromJson(jsonObject, "entity_id", null) != null) {
            setEntity(catalogService.getEntity(JsonUtil.getIntFromJson(jsonObject, "entity_id", null)));
        }
        setLoanApplicationId(JsonUtil.getIntFromJson(jsonObject, "loan_application_id", null));
        setEntityWSId(JsonUtil.getIntFromJson(jsonObject, "entity_ws_id", null));
        if (JsonUtil.getIntFromJson(jsonObject, "entity_ws_id", null) != null) {
            setEntityWebService(catalogService.getEntityWebService(JsonUtil.getIntFromJson(jsonObject, "entity_ws_id", null)));
        }
        setLgApplicationEntityWSId(JsonUtil.getIntFromJson(jsonObject, "lg_application_entity_ws_id", null));

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

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public EntityWebService getEntityWebService() {
        return entityWebService;
    }

    public void setEntityWebService(EntityWebService entityWebService) {
        this.entityWebService = entityWebService;
    }

    public Integer getLgApplicationEntityWSId() {
        return lgApplicationEntityWSId;
    }

    public void setLgApplicationEntityWSId(Integer lgApplicationEntityWSId) {
        this.lgApplicationEntityWSId = lgApplicationEntityWSId;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Integer getEntityWSId() {
        return entityWSId;
    }

    public void setEntityWSId(Integer entityWSId) {
        this.entityWSId = entityWSId;
    }
}
