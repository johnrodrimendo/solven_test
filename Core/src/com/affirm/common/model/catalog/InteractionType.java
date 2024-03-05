package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jrodriguez on 31/08/16.
 */
public class InteractionType implements Serializable {

    public static final int MAIL = 1;
    public static final int SMS = 2;
    public static final int CALL = 3;
//    public static final int CALL_AUTOMATIC = 4;
    public static final int CHAT = 5;

    private Integer id;
    private String type;
    private Boolean active;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "interaction_type_id", null));
        setType(JsonUtil.getStringFromJson(json, "interaction_type", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
