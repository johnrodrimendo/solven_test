package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by john on 13/01/17.
 */
public class ReportProces implements Serializable{

    public static final char STATUS_QUEUE = 'Q';
    public static final char STATUS_PENDING = 'P';
    public static final char STATUS_SUCCESS = 'S';
    public static final char STATUS_FAILED = 'F';

    private Integer id;
    Integer reportId;
    private JSONObject params;
    private String url;
    private Date registerDate;
    private Date processDate;
    private Character status;
    private Integer userId;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "generated_report_id", null));
        setReportId(JsonUtil.getIntFromJson(json, "report_id", null));
        setParams(JsonUtil.getJsonObjectFromJson(json, "params", null));
        setUrl(JsonUtil.getStringFromJson(json, "url", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setProcessDate(JsonUtil.getPostgresDateFromJson(json, "process_date", null));
        setStatus(JsonUtil.getCharacterFromJson(json, "status", null));
        setUserId(JsonUtil.getIntFromJson(json, "user_id", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public JSONObject getParams() {
        return params;
    }

    public void setParams(JSONObject params) {
        this.params = params;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
