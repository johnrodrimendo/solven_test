package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class InteractionProvider {

    public static final int SENGRID = 1;
    public static final int TWILIO = 2;
    public static final int AWS = 3;
    public static final int WHATSAPP = 4;
    public static final int INTICO = 5;
    public static final int INFOBIP = 6;

    private Integer id;
    private String type;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "interaction_provider_id", null));
        setType(JsonUtil.getStringFromJson(json, "interaction_provider", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
