package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jrodriguez on 08/09/16.
 */
public class Profession implements Serializable {

    public static final int OTHER = 27;

    private Integer id;
    private String profession;
    private String textInt;
    private Boolean active;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "profession_id", null));
        setProfession(JsonUtil.getStringFromJson(json, "proffession", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setTextInt(JsonUtil.getStringFromJson(json, "text_int", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
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
}
