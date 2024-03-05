package com.affirm.common.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class Address {

    public static final String TYPE_HOME = "H";
    public static final String TYPE_WORK_PLACE = "W";

    private Integer addressId;
    private Integer personId;
    private String ubigeoId;
    private Integer housingTypeId;
    private Double addressLatitude;
    private Double addressLongitude;
//    TODO THERE ARE MORE

    public void fillFromDb(JSONObject json) {
        setAddressId(JsonUtil.getIntFromJson(json, "address_id", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setUbigeoId(JsonUtil.getStringFromJson(json, "ubigeo_id", null));
        setHousingTypeId(JsonUtil.getIntFromJson(json, "housing_type_id", null));
        setAddressLatitude(JsonUtil.getDoubleFromJson(json, "address_latitude", null));
        setAddressLongitude(JsonUtil.getDoubleFromJson(json, "address_longitude", null));
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getUbigeoId() {
        return ubigeoId;
    }

    public void setUbigeoId(String ubigeoId) {
        this.ubigeoId = ubigeoId;
    }

    public Integer getHousingTypeId() {
        return housingTypeId;
    }

    public void setHousingTypeId(Integer housingTypeId) {
        this.housingTypeId = housingTypeId;
    }

    public Double getAddressLatitude() {
        return addressLatitude;
    }

    public void setAddressLatitude(Double addressLatitude) {
        this.addressLatitude = addressLatitude;
    }

    public Double getAddressLongitude() {
        return addressLongitude;
    }

    public void setAddressLongitude(Double addressLongitude) {
        this.addressLongitude = addressLongitude;
    }
}
