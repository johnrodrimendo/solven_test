package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class Affiliator {
    private Integer id;
    private String name;
    private String ruc;
    private String phoneNumber;
    private Date registerDate;
    private Date activationDate;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "affiliator_id", null));
        setName(JsonUtil.getStringFromJson(json, "name", null));
        setRuc(JsonUtil.getStringFromJson(json, "ruc", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setActivationDate(JsonUtil.getPostgresDateFromJson(json, "activation_date", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Date activationDate) {
        this.activationDate = activationDate;
    }
}
