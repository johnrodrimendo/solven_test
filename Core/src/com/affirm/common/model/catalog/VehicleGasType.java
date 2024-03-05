package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by dev5 on 20/06/17.
 */
public class VehicleGasType implements Serializable {

    private int id;
    private String type;
    private String i18nKey;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "gas_type_id", null));
        setI18nKey(JsonUtil.getStringFromJson(json, "text_int", null));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getI18nKey() {
        return i18nKey;
    }

    public void setI18nKey(String i18nKey) {
        this.i18nKey = i18nKey;
    }
}
