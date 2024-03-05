package com.affirm.common.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class ApplicationEFLAssessment {

    private Integer loanApplicationId;
    private String eflSessionUid;
    private Double score;
    private JSONObject jsResult;
    private Date registerDate;
    private String scoreConfidence;

    public void fillFromDb(JSONObject json) {
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setEflSessionUid(JsonUtil.getStringFromJson(json, "efl_session_uid", null));
        setScore(JsonUtil.getDoubleFromJson(json, "score", null));
        setJsResult(JsonUtil.getJsonObjectFromJson(json, "js_result", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setScoreConfidence(JsonUtil.getStringFromJson(json, "score_confidence", null));
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public String getEflSessionUid() {
        return eflSessionUid;
    }

    public void setEflSessionUid(String eflSessionUid) {
        this.eflSessionUid = eflSessionUid;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public JSONObject getJsResult() {
        return jsResult;
    }

    public void setJsResult(JSONObject jsResult) {
        this.jsResult = jsResult;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getScoreConfidence() {
        return scoreConfidence;
    }

    public void setScoreConfidence(String scoreConfidence) {
        this.scoreConfidence = scoreConfidence;
    }
}
