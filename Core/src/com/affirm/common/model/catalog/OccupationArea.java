package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

public class OccupationArea implements Serializable {

    private Integer id;
    private String area;
    private String textInt;
    private Integer orderId;
    private Boolean active;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "ocupation_area_id", null));
        setArea(JsonUtil.getStringFromJson(json, "area", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setTextInt(JsonUtil.getStringFromJson(json, "text_int", null));
        setOrderId(JsonUtil.getIntFromJson(json, "order_id", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTextInt() {
        return textInt;
    }

    public void setTextInt(String textInt) {
        this.textInt = textInt;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
