package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by john on 22/05/17.
 */
public class ProductCategoryCountry implements Serializable{

    private Integer categoryId;
    private Integer countryId;
    private String process;
    private Boolean active;
    private Boolean visible;

    public void fillFromDb(JSONObject json) {
        setCategoryId(JsonUtil.getIntFromJson(json, "category_id", null));
        setCountryId(JsonUtil.getIntFromJson(json, "country_id", null));
        if (JsonUtil.getJsonObjectFromJson(json, "js_evaluation_process", null) != null)
            setProcess(JsonUtil.getJsonObjectFromJson(json, "js_evaluation_process", null).toString());
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setVisible(JsonUtil.getBooleanFromJson(json, "is_visible", null));
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public JSONObject getJsonProcess() {
        return process != null ? new JSONObject(process) : null;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}
