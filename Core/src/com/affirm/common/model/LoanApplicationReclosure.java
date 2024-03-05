package com.affirm.common.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class LoanApplicationReclosure {

    private Integer loanApplicationId;
    private Integer entityId;
    private Boolean isReclosureable;
    private Date registerDate;
    private Double pendingCreditAmount;

    public void fillFromDb(JSONObject json) {
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setReclosureable(JsonUtil.getBooleanFromJson(json, "is_reclosurable", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setPendingCreditAmount(JsonUtil.getDoubleFromJson(json, "pending_credit_amount", null));
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

    public Boolean getReclosureable() {
        return isReclosureable;
    }

    public void setReclosureable(Boolean reclosureable) {
        isReclosureable = reclosureable;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Double getPendingCreditAmount() {
        return pendingCreditAmount;
    }

    public void setPendingCreditAmount(Double pendingCreditAmount) {
        this.pendingCreditAmount = pendingCreditAmount;
    }
}
