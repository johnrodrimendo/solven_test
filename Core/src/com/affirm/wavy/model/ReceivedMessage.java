package com.affirm.wavy.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class ReceivedMessage {

    private String id;
    private String source;
    private String origin;
    private ReceivedMessageProfile userProfile;
    private String correlationId;
    private String campaignId;
    private String campaignAlias;
    private ReceivedMessageChat message;
    private long receivedAt;
    private Date receivedDate;
    private String extraInfo;

    public void fillFromJson(JSONObject json) {
        setId(JsonUtil.getStringFromJson(json, "id", null));
        setSource(JsonUtil.getStringFromJson(json, "source", null));
        setOrigin(JsonUtil.getStringFromJson(json, "origin", null));

        JSONObject userProfileJson = JsonUtil.getJsonObjectFromJson(json, "userProfile", null);
        if (userProfileJson != null) {
            userProfile = new ReceivedMessageProfile();
            userProfile.setName(userProfileJson.getString("name"));
        }

        setCorrelationId(JsonUtil.getStringFromJson(json, "correlationId", null));
        setCampaignId(JsonUtil.getStringFromJson(json, "campaignId", null));
        setCampaignAlias(JsonUtil.getStringFromJson(json, "campaignAlias", null));

        JSONObject messageJson = JsonUtil.getJsonObjectFromJson(json, "message", null);
        if (messageJson != null) {
            message = new ReceivedMessageChat();
            message.setMessageText(JsonUtil.getStringFromJson(messageJson, "messageText", null));
            message.setType(JsonUtil.getStringFromJson(messageJson, "type", null));
            message.setMimeType(JsonUtil.getStringFromJson(messageJson, "mimeType", null));
            message.setMediaUrl(JsonUtil.getStringFromJson(messageJson, "mediaUrl", null));
        }

        setReceivedAt(JsonUtil.getLongFromJson(json, "receivedAt", null));
        setReceivedDate(JsonUtil.getPostgresDateFromJson(json, "receivedDate", null));
        setExtraInfo(JsonUtil.getStringFromJson(json, "extraInfo", null));
    }

    public ReceivedMessage() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public ReceivedMessageProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(ReceivedMessageProfile userProfile) {
        this.userProfile = userProfile;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignAlias() {
        return campaignAlias;
    }

    public void setCampaignAlias(String campaignAlias) {
        this.campaignAlias = campaignAlias;
    }

    public ReceivedMessageChat getMessage() {
        return message;
    }

    public void setMessage(ReceivedMessageChat message) {
        this.message = message;
    }

    public long getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(long receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }
}
