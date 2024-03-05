package com.affirm.client.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by jrodriguez on 08/06/16.
 */
public class DepositorAmount {

    private Integer transactionId;
    private String clientName;
    private Double currentAmount;
    private String dueDate;
    private Integer inArrearsInstallments;
    private Double inArrearsAmount;
    private Double total;
    private String fullcargaReference;
    private String errorMessage;

    public void fillFromDb(JSONObject json) {
        setTransactionId(JsonUtil.getIntFromJson(json, "ws_transaction_id", null));
        setClientName(JsonUtil.getStringFromJson(json, "client_name", null));
        setCurrentAmount(JsonUtil.getDoubleFromJson(json, "current_amount", null));
        setDueDate(JsonUtil.getStringFromJson(json, "due_date", null));
        setInArrearsInstallments(JsonUtil.getIntFromJson(json, "in_arrears_installments", null));
        setInArrearsAmount(JsonUtil.getDoubleFromJson(json, "in_arrears_amount", null));
        setTotal(JsonUtil.getDoubleFromJson(json, "total", null));
        setErrorMessage(JsonUtil.getStringFromJson(json, "mensaje", null));
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(Double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getInArrearsInstallments() {
        return inArrearsInstallments;
    }

    public void setInArrearsInstallments(Integer inArrearsInstallments) {
        this.inArrearsInstallments = inArrearsInstallments;
    }

    public Double getInArrearsAmount() {
        return inArrearsAmount;
    }

    public void setInArrearsAmount(Double inArrearsAmount) {
        this.inArrearsAmount = inArrearsAmount;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getFullcargaReference() {
        return fullcargaReference;
    }

    public void setFullcargaReference(String fullcargaReference) {
        this.fullcargaReference = fullcargaReference;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
