package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev5 on 31/10/17.
 */
public class TrackingAction {

    public static final int INTERESTED = 1;
    public static final int SCHEDULE_PHONECALL = 2;
    public static final int NOT_INTERESTED = 3;
    public static final int STOP_FOLLOWUP = 4;
    public static final int BUSY = 5;
    public static final int WRONG_NUMBER = 6;
    public static final int MISSING_DOCUMENTATION = 7;
    public static final int VALID = 8;
    public static final int NO_RESPONSE = 9;
    public static final int DESACTIVAR_GESTION = 10;
    public static final int NO_SE_ENCUENTRA = 11;

    public static final int PHONE_VERIFICATION_CONTACTED = 12;
    public static final int PHONE_VERIFICATION_NO_RESPONSE = 13;
    public static final int PHONE_VERIFICATION_WRONG_NUMBER = 14;

    public static final int PHONE_VERIFICATION_CONTACTED_REFERRAL = 15;
    public static final int PHONE_VERIFICATION_NO_RESPONSE_REFERRAL = 16;
    public static final int PHONE_VERIFICATION_WRONG_NUMBER_REFERRAL = 17;

    public static final int TRACKING_PHONE_CALL = 18;

    public static final int ADDRESS_VERIFICATION_CONTACTED = 19;
    public static final int ADDRESS_VERIFICATION_NO_RESPONSE = 20;
    public static final int ADDRESS_VERIFICATION_WRONG_NUMBER = 21;

    public static final int WELCOME_CALL_CONTACTED = 22;
    public static final int WELCOME_CALL_NO_RESPONSE = 23;
    public static final int WELCOME_CALL_WRONG_NUMBER = 24;

    public static final String CATEGORY_CONTACT = "Contacto";
    public static final String CATEGORY_NO_CONTACT = "No Contacto";

    private Integer trackingActionId;
    private String trackingAction;
    private Boolean isActive;
    private String trackingActionCategory;
    private Boolean dateRequired;
    private List<String> arScreen;
    private List<TrackingDetail> trackingDetails;
    private String description;

    public void fillFromDb(JSONObject json) throws Exception {
        setTrackingActionId(JsonUtil.getIntFromJson(json, "tracking_action_id", null));
        setTrackingAction(JsonUtil.getStringFromJson(json, "tracking_action", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setTrackingActionCategory(JsonUtil.getStringFromJson(json, "tracking_action_category", null));
        setDateRequired(JsonUtil.getBooleanFromJson(json, "date_required", null));
        if(JsonUtil.getJsonArrayFromJson(json, "tracking_action_detail", null) != null && JsonUtil.getJsonArrayFromJson(json, "tracking_action_detail", null).length() > 0){
            List<TrackingDetail> trackingDetailsTemp = new ArrayList<>();
            for(Object rejectedReason : JsonUtil.getJsonArrayFromJson(json, "tracking_action_detail", null)){
                TrackingDetail trackingDetail = new TrackingDetail();
                trackingDetail.fillFromDb(((JSONObject)rejectedReason));
                trackingDetailsTemp.add(trackingDetail);
            }
            setTrackingDetails(trackingDetailsTemp);
        }
        if(JsonUtil.getJsonArrayFromJson(json, "ar_screen", null) != null && JsonUtil.getJsonArrayFromJson(json, "ar_screen", null).length() > 0){
            List<String> arScreenTemp = new ArrayList<>();
            for(Object screen : JsonUtil.getJsonArrayFromJson(json, "ar_screen", null)){
                arScreenTemp.add((String) screen);
            }
            setArScreen(arScreenTemp);
        }
        setDescription(JsonUtil.getStringFromJson(json, "description", null));
    }

    public Integer getTrackingActionId() {
        return trackingActionId;
    }

    public void setTrackingActionId(Integer trackingActionId) {
        this.trackingActionId = trackingActionId;
    }

    public String getTrackingAction() {
        return trackingAction;
    }

    public void setTrackingAction(String trackingAction) {
        this.trackingAction = trackingAction;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getTrackingActionCategory() {
        return trackingActionCategory;
    }

    public void setTrackingActionCategory(String trackingActionCategory) {
        this.trackingActionCategory = trackingActionCategory;
    }

    public Boolean getDateRequired() {
        return dateRequired;
    }

    public void setDateRequired(Boolean dateRequired) {
        this.dateRequired = dateRequired;
    }

    public List<String> getArScreen() {
        return arScreen;
    }

    public void setArScreen(List<String> arScreen) {
        this.arScreen = arScreen;
    }

    public List<TrackingDetail> getTrackingDetails() {
        return trackingDetails;
    }

    public void setTrackingDetails(List<TrackingDetail> trackingDetails) {
        this.trackingDetails = trackingDetails;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
