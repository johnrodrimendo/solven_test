package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class ApiRestUser {

    public static final int BPEOPLE_USER = 1;

    private Integer id;
    private String username;
    private String password;
    private Integer entityId;
    private Entity entity;
    private Date registerDate;
    private Boolean valid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setId(JsonUtil.getIntFromJson(json, "id", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        if(entityId != null) setEntity(catalogService.getEntity(entityId));
        setUsername(JsonUtil.getStringFromJson(json, "username", null));
        setPassword(JsonUtil.getStringFromJson(json, "password", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setValid(JsonUtil.getBooleanFromJson(json, "valid", null));
    }


}
