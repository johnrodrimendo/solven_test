package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by john on 04/01/17.
 */
public class StudyLevel implements Serializable {

    public static final int MIDDLE_SCHOOL = 1;
    public static final int TECHNICIAN = 2;
    public static final int COLLEGE = 3;
    public static final int PHD = 4;
    public static final int DOCTORATE = 5;
    public static final int CONCLUDED_COLLEGE = 6;
    public static final int ELEMENTARY_SCHOOL = 7;

    private Integer id;
    private String level;
    private Integer order;
    private Boolean active;
    private String text;
    private Boolean showMarketplace;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "study_level_id", null));
        setLevel(JsonUtil.getStringFromJson(json, "study_level", null));
        setOrder(JsonUtil.getIntFromJson(json, "order_id", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setText(JsonUtil.getStringFromJson(json, "text_int", null));
        setShowMarketplace(JsonUtil.getBooleanFromJson(json, "is_show_marketplace", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getShowMarketplace() {
        return showMarketplace;
    }

    public void setShowMarketplace(Boolean showMarketplace) {
        this.showMarketplace = showMarketplace;
    }
}
