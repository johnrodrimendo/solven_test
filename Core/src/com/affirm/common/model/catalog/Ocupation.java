/**
 *
 */
package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class Ocupation implements Serializable {

    public static final int OTHER = 19;

    private Integer id;
    private String ocupation;
    private String textInt;
    private Integer orderId;
    private Boolean active;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "ocupation_id", null));
        setOcupation(JsonUtil.getStringFromJson(json, "ocupation", null));
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

    public String getOcupation() {
        return ocupation;
    }

    public void setOcupation(String ocupation) {
        this.ocupation = ocupation;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
}
