package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class MaintainedCarBrand implements Serializable {
    private Integer id;
    private String brand;
    private Boolean active;
    private Date registerDate;


    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "maintained_car_brand_id", null));
        setBrand(JsonUtil.getStringFromJson(json, "maintained_car_brand", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getRegisterDate() { return registerDate; }

    public void setRegisterDate(Date registerDate) { this.registerDate = registerDate; }

}
