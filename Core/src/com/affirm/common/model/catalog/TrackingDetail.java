package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by dev5 on 24/01/18.
 */
public class TrackingDetail {

    private Integer trackingReasonId;
    private String trackingReason;
    private Boolean isActive;

    public void fillFromDb(JSONObject json) throws Exception {
        setTrackingReasonId(JsonUtil.getIntFromJson(json, "tracking_action_detail_id", null));
        setTrackingReason(JsonUtil.getStringFromJson(json, "tracking_action_detail", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
    }

    public Integer getTrackingReasonId() {
        return trackingReasonId;
    }

    public void setTrackingReasonId(Integer trackingReasonId) {
        this.trackingReasonId = trackingReasonId;
    }

    public String getTrackingReason() {
        return trackingReason;
    }

    public void setTrackingReason(String trackingReason) {
        this.trackingReason = trackingReason;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
