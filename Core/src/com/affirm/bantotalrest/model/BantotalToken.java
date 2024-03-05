package com.affirm.bantotalrest.model;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class BantotalToken {

    private Integer id;
    private Date registerDate;
    private String token;
    private Boolean isActive;

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setId(JsonUtil.getIntFromJson(json, "id", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setToken(JsonUtil.getStringFromJson(json, "token", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
