package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Locale;

public class RetirementScheme {
    private Integer id;
    private String scheme;


    public void fillFromDb(JSONObject json, Locale locale) {
        setId(JsonUtil.getIntFromJson(json, "retirement_scheme_id", null));
        setScheme(JsonUtil.getStringFromJson(json, "retirement_scheme", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }
}
