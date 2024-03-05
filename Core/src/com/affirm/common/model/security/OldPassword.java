package com.affirm.common.model.security;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by dev5 on 18/08/17.
 */
public class OldPassword {

    private int passwordId;
    private int entityUserId;
    private String password;
    private Date registerDate;

    public void fillFromDb(JSONObject json){
        setPasswordId(JsonUtil.getIntFromJson(json, "entity_users_password_id", null));
        setEntityUserId(JsonUtil.getIntFromJson(json, "entity_user_id", null));
        setPassword(JsonUtil.getStringFromJson(json, "password", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
    }

    public int getPasswordId() {
        return passwordId;
    }

    public void setPasswordId(int passwordId) {
        this.passwordId = passwordId;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public void setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }
}
