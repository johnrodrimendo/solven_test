package com.affirm.common.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

public class UniversidadPeru implements Serializable {

    private Integer personId;
    private String ruc;
    private String phoneNumber;


    public void fillFromDb(JSONObject json) {
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setRuc(JsonUtil.getStringFromJson(json, "ruc", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
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

    @Override
    public String toString() {
        return "UniversidadPeru{" +
                "personId=" + personId +
                ", ruc='" + ruc + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
