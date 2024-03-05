package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jrodriguez on 07/09/16.
 */
public class ComparisonReason implements Serializable {

    public static final int REASON_GROUP_CREDIT_HISTORY = 1;
    public static final int REASON_GROUP_BETTER_CARD = 2;
    public static final int REASON_GROUP_LOAN = 3;
    public static final int REASON_GROUP_URGENCY = 4;

    public static final int REASON_CAR = 11;

    private Integer id;
    private Integer reasonGroupId;
    private String reasonKey;
    private String reason;
    private String subreasonKey;
    private String subreason;
    private Integer categorId;
    private Boolean active;
    private String icon;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "comparison_reason_id", null));
        setReasonGroupId(JsonUtil.getIntFromJson(json, "comparison_reason_1_id", null));
        setReasonKey(JsonUtil.getStringFromJson(json, "comparison_reason_1", null));
        setSubreasonKey(JsonUtil.getStringFromJson(json, "comparison_reason_2", null));
        setCategorId(JsonUtil.getIntFromJson(json, "comparison_category_id", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setIcon(JsonUtil.getStringFromJson(json, "icon", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReasonGroupId() {
        return reasonGroupId;
    }

    public void setReasonGroupId(Integer reasonGroupId) {
        this.reasonGroupId = reasonGroupId;
    }

    public String getReasonKey() {
        return reasonKey;
    }

    public void setReasonKey(String reasonKey) {
        this.reasonKey = reasonKey;
    }

    public String getSubreasonKey() {
        return subreasonKey;
    }

    public void setSubreasonKey(String subreasonKey) {
        this.subreasonKey = subreasonKey;
    }

    public Integer getCategorId() {
        return categorId;
    }

    public void setCategorId(Integer categorId) {
        this.categorId = categorId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSubreason() {
        return subreason;
    }

    public void setSubreason(String subreason) {
        this.subreason = subreason;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
