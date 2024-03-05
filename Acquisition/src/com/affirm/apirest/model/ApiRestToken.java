package com.affirm.apirest.model;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class ApiRestToken {

    private Long id;
    private String token;
    private Integer apiRestUserId;
    private Date registerDate;
    private Date validityDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getApiRestUserId() {
        return apiRestUserId;
    }

    public void setApiRestUserId(Integer apiRestUserId) {
        this.apiRestUserId = apiRestUserId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(Date validityDate) {
        this.validityDate = validityDate;
    }

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getLongFromJson(json, "id", null));
        setToken(JsonUtil.getStringFromJson(json, "token", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setValidityDate(JsonUtil.getPostgresDateFromJson(json, "validity_date", null));
        setApiRestUserId(JsonUtil.getIntFromJson(json, "api_rest_user_id", null));
    }


}
