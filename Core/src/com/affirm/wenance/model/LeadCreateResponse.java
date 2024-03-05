package com.affirm.wenance.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LeadCreateResponse {

    private String id;
    private String operationId;
    private Date creationDate;
    private String redirectUrl;

    public void fillFromJson(JSONObject json) throws Exception {
        setId(JsonUtil.getStringFromJson(json, "lead_id", null));
        setOperationId(JsonUtil.getStringFromJson(json, "operation_id", null));
        if (json.has("creation_date")) {
            setCreationDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(JsonUtil.getStringFromJson(json, "creation_date", null)));
        }
        setRedirectUrl(JsonUtil.getStringFromJson(json, "redirect_url", null));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
