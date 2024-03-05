package com.affirm.common.model.security;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class OldPasswordBackoffice {
    private int passwordId;
    private int sysUserId;
    private String password;
    private Date registerDate;

    public void fillFromDb(JSONObject json){
        setPasswordId(JsonUtil.getIntFromJson(json, "sysuser_password_id", null));
        setSysUserId(JsonUtil.getIntFromJson(json, "sysuser_id", null));
        setPassword(JsonUtil.getStringFromJson(json, "password", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
    }

    public int getPasswordId() {
        return passwordId;
    }

    public void setPasswordId(int passwordId) {
        this.passwordId = passwordId;
    }

    public int getSysUserId() { return sysUserId; }

    public void setSysUserId(int sysUserId) { this.sysUserId = sysUserId; }

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