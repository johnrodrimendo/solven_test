package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class ApplicationBureauLog<T> {
    public static final char STATUS_RUNNING = 'R';
    public static final char STATUS_SUCCESS = 'S';
    public static final char STATUS_FAILED = 'F';

    private Integer id;
    private Integer bureauId;
    private Integer loanApplicationId;
    private Date startDate;
    private Date finishDate;
    private Character status;
    private String request;
    private String response;
    private T soapResponse;
    private T restResponse;

    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "application_bureau_id", null));
        setBureauId(JsonUtil.getIntFromJson(json, "bureau_id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setStartDate(JsonUtil.getPostgresDateFromJson(json, "start_date", null));
        setFinishDate(JsonUtil.getPostgresDateFromJson(json, "finish_date", null));
        setStatus(JsonUtil.getCharacterFromJson(json, "status", null));
        setRequest(JsonUtil.getStringFromJson(json, "request", null));
        setResponse(JsonUtil.getStringFromJson(json, "response", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBureauId() {
        return bureauId;
    }

    public void setBureauId(Integer bureauId) {
        this.bureauId = bureauId;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public T getSoapResponse() {
        return soapResponse;
    }

    public void setSoapResponse(T soapResponse) {
        this.soapResponse = soapResponse;
    }

    public T getRestResponse() {
        return restResponse;
    }

    public void setRestResponse(T restResponse) {
        this.restResponse = restResponse;
    }
}
