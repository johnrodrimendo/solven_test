package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoanApplicationEvaluationsProcess implements Serializable {

    public static final char STATUS_RUNNING = 'R';
    public static final char STATUS_RUNNING_DELAYED = 'D';
    public static final char STATUS_FINISHED = 'F';
    public static final char STATUS_STOPPED = 'S';

    private Integer loanApplicationId;
    private List<Integer> preEvaluationBots;
    private Character preEvaluationStatus;
    private List<Integer> evaluationBots;
    private Character evaluationStatus;
    private Boolean readyForPreEvaluation;
    private Boolean readyForEvaluation;
    private Integer retries;
    private Boolean sendDelayedEmail;
    private Character synthesizedStatus;
    private Date evaluationStartDate;

    public void fillFromDb(JSONObject json) {
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_pre_evaluation_bots", null) != null) {
            setPreEvaluationBots(new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "js_pre_evaluation_bots", null).toString(), new TypeToken<ArrayList<Integer>>() {
            }.getType()));
        }
        setPreEvaluationStatus(JsonUtil.getCharacterFromJson(json, "pre_evaluation_status", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_evaluation_bots", null) != null) {
            setEvaluationBots(new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "js_evaluation_bots", null).toString(), new TypeToken<ArrayList<Integer>>() {
            }.getType()));
        }
        setEvaluationStatus(JsonUtil.getCharacterFromJson(json, "evaluation_status", null));
        setReadyForEvaluation(JsonUtil.getBooleanFromJson(json, "ready_for_evaluation", null));
        setReadyForPreEvaluation(JsonUtil.getBooleanFromJson(json, "ready_for_pre_evaluation", null));
        setRetries(JsonUtil.getIntFromJson(json, "retries", null));
        setSendDelayedEmail(JsonUtil.getBooleanFromJson(json, "send_delayed_email", null));
        setSynthesizedStatus(JsonUtil.getCharacterFromJson(json, "synthesized_status", null));
        setEvaluationStartDate(JsonUtil.getPostgresDateFromJson(json, "evaluation_start_date", null));
    }

    public Integer getCurrentEvaluationQueryBot(){
        if (evaluationBots == null)
            return null;
        return evaluationBots.get(evaluationBots.size() - 1);
    }

    public void addEvaluationQueryBot(int queryBotId) {
        if (evaluationBots == null)
            evaluationBots = new ArrayList<>();
        evaluationBots.add(queryBotId);
    }

    public void addPreEvaluationQueryBot(int queryBotId) {
        if (preEvaluationBots == null)
            preEvaluationBots = new ArrayList<>();
        preEvaluationBots.add(queryBotId);
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public List<Integer> getPreEvaluationBots() {
        return preEvaluationBots;
    }

    public void setPreEvaluationBots(List<Integer> preEvaluationBots) {
        this.preEvaluationBots = preEvaluationBots;
    }

    public Character getPreEvaluationStatus() {
        return preEvaluationStatus;
    }

    public void setPreEvaluationStatus(Character preEvaluationStatus) {
        this.preEvaluationStatus = preEvaluationStatus;
    }

    public List<Integer> getEvaluationBots() {
        return evaluationBots;
    }

    public void setEvaluationBots(List<Integer> evaluationBots) {
        this.evaluationBots = evaluationBots;
    }

    public Character getEvaluationStatus() {
        return evaluationStatus;
    }

    public void setEvaluationStatus(Character evaluationStatus) {
        this.evaluationStatus = evaluationStatus;
    }

    public Boolean getReadyForEvaluation() {
        return readyForEvaluation;
    }

    public void setReadyForEvaluation(Boolean readyForEvaluation) {
        this.readyForEvaluation = readyForEvaluation;
    }

    public Boolean getReadyForPreEvaluation() {
        return readyForPreEvaluation;
    }

    public void setReadyForPreEvaluation(Boolean readyForPreEvaluation) {
        this.readyForPreEvaluation = readyForPreEvaluation;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Boolean getSendDelayedEmail() {
        return sendDelayedEmail;
    }

    public void setSendDelayedEmail(Boolean sendDelayedEmail) {
        this.sendDelayedEmail = sendDelayedEmail;
    }

    public Character getSynthesizedStatus() {
        return synthesizedStatus;
    }

    public void setSynthesizedStatus(Character synthesizedStatus) {
        this.synthesizedStatus = synthesizedStatus;
    }

    public Date getEvaluationStartDate() {
        return evaluationStartDate;
    }

    public void setEvaluationStartDate(Date evaluationStartDate) {
        this.evaluationStartDate = evaluationStartDate;
    }
}
