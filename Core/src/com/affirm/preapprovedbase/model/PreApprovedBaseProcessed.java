package com.affirm.preapprovedbase.model;


import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.Date;

public class PreApprovedBaseProcessed {

    public static final char STATUS_QUEUE = 'Q';
    public static final char STATUS_PENDING = 'P';
    public static final char STATUS_SUCCESS = 'S';
    public static final char STATUS_FAILED = 'F';

    private Integer id;
    private Integer entityId;
    private String url;
    private Date registerDate;
    private Date processDate;
    private Character status;
    private Integer entityUserId;
    private ErrorDetail errorDetail;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public Integer getEntityUserId() {
        return entityUserId;
    }

    public void setEntityUserId(Integer entityUserId) {
        this.entityUserId = entityUserId;
    }

    public ErrorDetail getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(ErrorDetail errorDetail) {
        this.errorDetail = errorDetail;
    }

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "base_pre_approved_processed_id", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setUrl(JsonUtil.getStringFromJson(json, "url", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setProcessDate(JsonUtil.getPostgresDateFromJson(json, "process_date", null));
        setStatus(JsonUtil.getCharacterFromJson(json, "status", null));
        setEntityUserId(JsonUtil.getIntFromJson(json, "entity_user_id", null));
        if(JsonUtil.getJsonObjectFromJson(json, "error_detail", null) != null) setErrorDetail(new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "error_detail", null).toString(),ErrorDetail.class));

    }

    public static class ErrorDetail{
        private String detailMessage;
        private String code;
        private String customMessage;

        public String getDetailMessage() {
            return detailMessage;
        }

        public void setDetailMessage(String detailMessage) {
            this.detailMessage = detailMessage;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCustomMessage() {
            return customMessage;
        }

        public void setCustomMessage(String customMessage) {
            this.customMessage = customMessage;
        }
    }


}
