package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EntityProductEvaluationsProcess implements Serializable {

    public static final char STATUS_RUNNING = 'R';
    public static final char STATUS_RUNNING_DELAYED = 'D';
    public static final char STATUS_FAILED = 'F';
    public static final char STATUS_SUCCESS = 'S';

    private Integer id;
    private Integer loanApplicationId;
    private Integer entityId;
    private Integer productId;
    private Character preEvaluationStatus;
    private List<Integer> preEvaluationBots;
    private Character evaluationStatus;
    private List<Integer> evaluationBots;
    private Integer preEvaluationRetries;
    private Integer evaluationRetries;
    private Boolean readyForProcess;
    private Boolean preliminaryEvaluationApproved;
    private Boolean evaluationApproved;
    private Boolean selectable;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "entity_product_evaluation_id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setProductId(JsonUtil.getIntFromJson(json, "product_id", null));
        setPreEvaluationStatus(JsonUtil.getCharacterFromJson(json, "preliminary_evaluation_status", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_preliminary_evaluation_bots", null) != null) {
            setPreEvaluationBots(new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "js_preliminary_evaluation_bots", null).toString(), new TypeToken<ArrayList<Integer>>() {
            }.getType()));
        }
        setEvaluationStatus(JsonUtil.getCharacterFromJson(json, "evaluation_status", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_evaluation_bots", null) != null) {
            setEvaluationBots(new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "js_evaluation_bots", null).toString(), new TypeToken<ArrayList<Integer>>() {
            }.getType()));
        }
        setPreEvaluationRetries(JsonUtil.getIntFromJson(json, "preliminary_evaluation_retries", null));
        setEvaluationRetries(JsonUtil.getIntFromJson(json, "evaluation_retries", null));
        setReadyForProcess(JsonUtil.getBooleanFromJson(json, "is_ready", null));
        setPreliminaryEvaluationApproved(JsonUtil.getBooleanFromJson(json, "preliminary_evaluation_approved", null));
        setEvaluationApproved(JsonUtil.getBooleanFromJson(json, "evaluation_approved", null));
        setSelectable(JsonUtil.getBooleanFromJson(json, "is_selectable", null));
    }

    public void addPreliminaryEvaluationQueryBot(int queryBotId) {
        if (preEvaluationBots == null)
            preEvaluationBots = new ArrayList<>();
        preEvaluationBots.add(queryBotId);
    }

    public void addEvaluationQueryBot(int queryBotId) {
        if (evaluationBots == null)
            evaluationBots = new ArrayList<>();
        evaluationBots.add(queryBotId);
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

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Character getPreEvaluationStatus() {
        return preEvaluationStatus;
    }

    public void setPreEvaluationStatus(Character preEvaluationStatus) {
        this.preEvaluationStatus = preEvaluationStatus;
    }

    public List<Integer> getPreEvaluationBots() {
        return preEvaluationBots;
    }

    public void setPreEvaluationBots(List<Integer> preEvaluationBots) {
        this.preEvaluationBots = preEvaluationBots;
    }

    public Character getEvaluationStatus() {
        return evaluationStatus;
    }

    public void setEvaluationStatus(Character evaluationStatus) {
        this.evaluationStatus = evaluationStatus;
    }

    public List<Integer> getEvaluationBots() {
        return evaluationBots;
    }

    public void setEvaluationBots(List<Integer> evaluationBots) {
        this.evaluationBots = evaluationBots;
    }

    public Integer getPreEvaluationRetries() {
        return preEvaluationRetries;
    }

    public void setPreEvaluationRetries(Integer preEvaluationRetries) {
        this.preEvaluationRetries = preEvaluationRetries;
    }

    public Integer getEvaluationRetries() {
        return evaluationRetries;
    }

    public void setEvaluationRetries(Integer evaluationRetries) {
        this.evaluationRetries = evaluationRetries;
    }

    public Boolean getReadyForProcess() {
        return readyForProcess;
    }

    public void setReadyForProcess(Boolean readyForProcess) {
        this.readyForProcess = readyForProcess;
    }

    public Boolean getEvaluationApproved() {
        return evaluationApproved;
    }

    public void setEvaluationApproved(Boolean evaluationApproved) {
        this.evaluationApproved = evaluationApproved;
    }

    public Boolean getPreliminaryEvaluationApproved() {
        return preliminaryEvaluationApproved;
    }

    public void setPreliminaryEvaluationApproved(Boolean preliminaryEvaluationApproved) {
        this.preliminaryEvaluationApproved = preliminaryEvaluationApproved;
    }

    public Boolean getSelectable() {
        return selectable;
    }

    public void setSelectable(Boolean selectable) {
        this.selectable = selectable;
    }
}
