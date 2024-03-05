package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

public class Mileage implements Serializable {
    private Integer mileageId;
    private String mileage;
    private Boolean isActive;
    private Integer id;
    private Boolean active;

    public void fillFromDb(JSONObject json){
        setMileageId(JsonUtil.getIntFromJson(json, "mileage_id", null));
        setId(JsonUtil.getIntFromJson(json, "mileage_id", null));
        setMileage(JsonUtil.getStringFromJson(json, "mileage", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
    }

    public Integer getMileageId() {
        return mileageId;
    }

    public void setMileageId(Integer mileageId) {
        this.mileageId = mileageId;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public Boolean getActive() {
        return isActive;
    }


    public void setActive(Boolean active) {
        this.active = active;
    }
}
