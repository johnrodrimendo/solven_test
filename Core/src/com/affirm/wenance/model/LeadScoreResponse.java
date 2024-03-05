package com.affirm.wenance.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LeadScoreResponse {

    private String id;
    private String status;
    private String identificationType;
    private String identificationValue;
    private String brand;
    private String country;
    private String firstName;
    private String lastName;
    private Date createdDate;

    public void fillFromJson(JSONObject json) throws Exception {
        setId(JsonUtil.getStringFromJson(json, "id", null));
        setStatus(JsonUtil.getStringFromJson(json, "status", null));

        JSONObject identificationJson = JsonUtil.getJsonObjectFromJson(json, "identification", null);
        if (identificationJson != null) {
            setIdentificationType(JsonUtil.getStringFromJson(identificationJson, "type", null));
            setIdentificationValue(JsonUtil.getStringFromJson(identificationJson, "value", null));
        }

        setBrand(JsonUtil.getStringFromJson(json, "brand", null));
        setCountry(JsonUtil.getStringFromJson(json, "country", null));
        setFirstName(JsonUtil.getStringFromJson(json, "first_name", null));
        setLastName(JsonUtil.getStringFromJson(json, "last_name", null));
        if (json.has("created_date")) {
            setCreatedDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(JsonUtil.getStringFromJson(json, "created_date", null)));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(String identificationType) {
        this.identificationType = identificationType;
    }

    public String getIdentificationValue() {
        return identificationValue;
    }

    public void setIdentificationValue(String identificationValue) {
        this.identificationValue = identificationValue;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
