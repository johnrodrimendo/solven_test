package com.affirm.common.model;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class ExternalWSRecord {

    private Integer id;
    private Integer loanApplicationId;
    private Date startDate;
    private Date endDate;
    private String url;
    private String request;
    private String response;
    private Integer responseHttpCode;

    public ExternalWSRecord(){

    }

    public ExternalWSRecord(
        Integer loanApplicationId,
        Date startDate,
        Date endDate,
        String url,
        String request,
        String response,
        Integer responseHttpCode
    ){
        super();
        setLoanApplicationId(loanApplicationId);
        setStartDate(startDate);
        setUrl(url);
        setEndDate(endDate);
        setRequest(request);
        setResponse(response);
        setResponseHttpCode(responseHttpCode);
    }

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setId(JsonUtil.getIntFromJson(json, "id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setStartDate(JsonUtil.getPostgresDateFromJson(json, "start_date", null));
        setEndDate(JsonUtil.getPostgresDateFromJson(json, "end_date", null));
        setUrl(JsonUtil.getStringFromJson(json, "url", null));
        setRequest(JsonUtil.getStringFromJson(json, "request", null));
        setResponse(JsonUtil.getStringFromJson(json, "response", null));
        setResponseHttpCode(JsonUtil.getIntFromJson(json, "response_http_code", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Integer getResponseHttpCode() {
        return responseHttpCode;
    }

    public void setResponseHttpCode(Integer responseHttpCode) {
        this.responseHttpCode = responseHttpCode;
    }
}
