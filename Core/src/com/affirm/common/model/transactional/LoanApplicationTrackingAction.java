package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.TrackingAction;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

public class LoanApplicationTrackingAction implements Serializable {

    private TrackingAction trackingAction;
    private Integer id;
    private Integer userFileId;

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "application_tracking_action_id", null));
        Integer trackingActionId = JsonUtil.getIntFromJson(json, "tracking_action_id", null);
        setUserFileId(JsonUtil.getIntFromJson(json, "user_file_id", null));
        if( trackingActionId != null) {
            setTrackingAction(catalog.getTrackingActions().stream().filter(p -> p.getTrackingActionId().equals(trackingActionId)).findFirst().orElse(null));
        }
    }

    public TrackingAction getTrackingAction() {
        return trackingAction;
    }

    public void setTrackingAction(TrackingAction trackingAction) {
        this.trackingAction = trackingAction;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserFileId() {
        return userFileId;
    }

    public void setUserFileId(Integer userFileId) {
        this.userFileId = userFileId;
    }
}
