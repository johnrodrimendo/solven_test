package com.affirm.common.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class ApprovedDataLoanApplication {

    private Integer loanApplicationId;
    private Integer entityId;
    private Double maxAmount;
    private Boolean allowedProcess;
//    TODO THERE R MORE

    public void fillFromDb(JSONObject json) {
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setMaxAmount(JsonUtil.getDoubleFromJson(json, "max_amount", null));
        setAllowedProcess(JsonUtil.getBooleanFromJson(json, "allowed_process", null));
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

    public Double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Double maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Boolean getAllowedProcess() {
        return allowedProcess;
    }

    public void setAllowedProcess(Boolean allowedProcess) {
        this.allowedProcess = allowedProcess;
    }
}
