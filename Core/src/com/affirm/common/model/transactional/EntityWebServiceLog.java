package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.Date;

public class EntityWebServiceLog<T> {

    public static final char STATUS_RUNNING = 'R';
    public static final char STATUS_SUCCESS = 'S';
    public static final char STATUS_FAILED = 'F';

    private Integer id;
    private Integer entityWebServiceId;
    private Integer loanApplicationId;
    private Date startDate;
    private Date finishDate;
    private Character status;
    private String request;
    private String response;
    private T soapResponse;
    private T restResponse;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "application_entity_ws", null));
        setEntityWebServiceId(JsonUtil.getIntFromJson(json, "entity_ws_id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setStartDate(JsonUtil.getPostgresDateFromJson(json, "start_date", null));
        setFinishDate(JsonUtil.getPostgresDateFromJson(json, "finish_date", null));
        setStatus(JsonUtil.getCharacterFromJson(json, "status", null));
        setRequest(JsonUtil.getStringFromJson(json, "request", null));
        setResponse(JsonUtil.getStringFromJson(json, "response", null));
    }

    public <T> T getParsedResponse(Class<T> returningObject){
        if(response == null)
            return null;
        return new Gson().fromJson(response, returningObject);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEntityWebServiceId() {
        return entityWebServiceId;
    }

    public void setEntityWebServiceId(Integer entityWebServiceId) {
        this.entityWebServiceId = entityWebServiceId;
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
