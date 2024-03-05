package com.affirm.backoffice.model;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.LoanApplicationStatus;
import com.affirm.common.model.catalog.ProcessQuestion;
import com.affirm.common.model.catalog.ProductCategory;
import com.affirm.common.model.transactional.ProcessQuestionSequence;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScreenReport {

    private ProcessQuestion processQuestion;
    private Integer loanApplicationsQuantity;
    private Integer totalTime;
    private Integer proceeded;
    private Integer abandoned;
    private Integer rejected;
    private Date lastVisit;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        if (JsonUtil.getIntFromJson(json, "process_question_id", null) != null)
            setProcessQuestion(catalog.getProcessQuestion(JsonUtil.getIntFromJson(json, "process_question_id", null)));
        setLoanApplicationsQuantity(JsonUtil.getIntFromJson(json, "count", null));
        setTotalTime(JsonUtil.getIntFromJson(json, "question_time", null));
        setProceeded(JsonUtil.getIntFromJson(json, "proceeded", null));
        setAbandoned(JsonUtil.getIntFromJson(json, "abandoned", null));
        setRejected(JsonUtil.getIntFromJson(json, "rejected", null));
        setLastVisit(JsonUtil.getPostgresDateFromJson(json, "last_visit", null));
    }

    public ProcessQuestion getProcessQuestion() {
        return processQuestion;
    }

    public void setProcessQuestion(ProcessQuestion processQuestion) {
        this.processQuestion = processQuestion;
    }

    public Integer getLoanApplicationsQuantity() {
        return loanApplicationsQuantity;
    }

    public void setLoanApplicationsQuantity(Integer loanApplicationsQuantity) {
        this.loanApplicationsQuantity = loanApplicationsQuantity;
    }

    public Integer getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Integer totalTime) {
        this.totalTime = totalTime;
    }

    public Integer getProceeded() {
        return proceeded;
    }

    public void setProceeded(Integer proceeded) {
        this.proceeded = proceeded;
    }

    public Integer getAbandoned() {
        return abandoned;
    }

    public void setAbandoned(Integer abandoned) {
        this.abandoned = abandoned;
    }

    public Integer getRejected() {
        return rejected;
    }

    public void setRejected(Integer rejected) {
        this.rejected = rejected;
    }

    public Date getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(Date lastVisit) {
        this.lastVisit = lastVisit;
    }
}
