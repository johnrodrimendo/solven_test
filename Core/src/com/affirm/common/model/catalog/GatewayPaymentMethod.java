package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;


public class GatewayPaymentMethod implements Serializable {

    public final static int PAGO_EFECTIVO = 1;
    public final static int PAGO_LINK = 2;
    public final static int WESTERN_UNION = 3;

    private Integer id;
    private String name;
    private Boolean active;

    public void fillFromDb(JSONObject json, CatalogService catalog) {
        setId(JsonUtil.getIntFromJson(json, "collection_payment_method_id", null));
        setName(JsonUtil.getStringFromJson(json, "name", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

}
