package com.affirm.common.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;
public class AfipActivitiy {

    private int id;
    private String description;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "actividad_id", null));
        setDescription(JsonUtil.getStringFromJson(json, "descripcion", null));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
