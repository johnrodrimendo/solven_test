package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Agent;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 09/06/16.
 */

public class Comparison implements Serializable {

    private Integer id;
    private Integer selfEvaluationId;
    private Date registerDate;
    private Integer comparisonReasonId;
    private Double amount;
    private Integer installments;
    private Double fixedGrossIncome;
    private Agent agent;
    private Integer currentQuestionId;
    private List<ProcessQuestionSequence> questionSequence = new ArrayList<>();
    private Boolean executed;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) {
        setId(JsonUtil.getIntFromJson(json, "comparison_id", null));
        setSelfEvaluationId(JsonUtil.getIntFromJson(json, "self_evaluation_id", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setComparisonReasonId(JsonUtil.getIntFromJson(json, "comparison_reason_id", null));
        setAmount(JsonUtil.getDoubleFromJson(json, "amount", null));
        setInstallments(JsonUtil.getIntFromJson(json, "installments", null));
        setFixedGrossIncome(JsonUtil.getDoubleFromJson(json, "fixed_gross_income", null));
        setCurrentQuestionId(JsonUtil.getIntFromJson(json, "current_procces_question_id", null));
        if (JsonUtil.getJsonArrayFromJson(json, "proccess_question_id_sequence", null) != null)
            setQuestionSequence(
                    new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "proccess_question_id_sequence", null).toString(), new TypeToken<ArrayList<ProcessQuestionSequence>>() {
                    }.getType()));
        if (JsonUtil.getIntFromJson(json, "form_assistant_id", null) != null)
            setAgent(catalog.getAgent(JsonUtil.getIntFromJson(json, "form_assistant_id", null)));
        setExecuted(JsonUtil.getBooleanFromJson(json, "is_executed", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSelfEvaluationId() {
        return selfEvaluationId;
    }

    public void setSelfEvaluationId(Integer selfEvaluationId) {
        this.selfEvaluationId = selfEvaluationId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Integer getComparisonReasonId() {
        return comparisonReasonId;
    }

    public void setComparisonReasonId(Integer comparisonReasonId) {
        this.comparisonReasonId = comparisonReasonId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Integer getCurrentQuestionId() {
        return currentQuestionId;
    }

    public void setCurrentQuestionId(Integer currentQuestionId) {
        this.currentQuestionId = currentQuestionId;
    }

    public List<ProcessQuestionSequence> getQuestionSequence() {
        return questionSequence;
    }

    public void setQuestionSequence(List<ProcessQuestionSequence> questionSequence) {
        this.questionSequence = questionSequence;
    }

    public Double getFixedGrossIncome() {
        return fixedGrossIncome;
    }

    public void setFixedGrossIncome(Double fixedGrossIncome) {
        this.fixedGrossIncome = fixedGrossIncome;
    }

    public Boolean getExecuted() {
        return executed;
    }

    public void setExecuted(Boolean executed) {
        this.executed = executed;
    }
}