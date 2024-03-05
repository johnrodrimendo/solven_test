package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Omar on 29/11/19.
 */
public class UniversidadPeruResult implements Serializable {

    private Integer queryId;
    private String inRuc;
    private String phoneNumber;
    private String phoneNumber2;
    private String phoneNumber3;
    private String phoneNumber4;
    private String address;
    private String district;
    private String department;

    public void fillFromDb(JSONObject json) {
        setQueryId(JsonUtil.getIntFromJson(json, "query_id", null));
        setInRuc(JsonUtil.getStringFromJson(json, "in_ruc", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        setPhoneNumber2(JsonUtil.getStringFromJson(json, "phone_number_2", null));
        setPhoneNumber3(JsonUtil.getStringFromJson(json, "phone_number_3", null));
        setPhoneNumber4(JsonUtil.getStringFromJson(json, "phone_number_4", null));
        setAddress(JsonUtil.getStringFromJson(json, "address", null));
        setDistrict(JsonUtil.getStringFromJson(json, "district", null));
        setDepartment(JsonUtil.getStringFromJson(json, "department", null));
    }

    public Integer getQueryId() {
        return queryId;
    }

    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }

    public String getInRuc() {
        return inRuc;
    }

    public void setInRuc(String inRuc) {
        this.inRuc = inRuc;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber2() {
        return phoneNumber2;
    }

    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }

    public String getPhoneNumber3() {
        return phoneNumber3;
    }

    public void setPhoneNumber3(String phoneNumber3) {
        this.phoneNumber3 = phoneNumber3;
    }

    public String getPhoneNumber4() {
        return phoneNumber4;
    }

    public void setPhoneNumber4(String phoneNumber4) {
        this.phoneNumber4 = phoneNumber4;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "UniversidadPeruResult{" +
                "queryId=" + queryId +
                ", inRuc='" + inRuc + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
