package com.affirm.client.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by jrodriguez on 08/06/16.
 */
public class DepositorRegisteredPayment {

    private Integer paymentId;
    private String depositorCode;
    private Double paidAmount;
    private Double inArrearsAmount;
    private String errorMessage;

    public void fillFromDb(JSONObject json) {
        setPaymentId(JsonUtil.getIntFromJson(json, "payment_id", null));
        setDepositorCode(JsonUtil.getStringFromJson(json, "depositor_code", null));
        setPaidAmount(JsonUtil.getDoubleFromJson(json, "paid_amount", null));
        setInArrearsAmount(JsonUtil.getDoubleFromJson(json, "in_arrears_amount", null));
        setErrorMessage(JsonUtil.getStringFromJson(json, "mensaje", null));
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public String getDepositorCode() {
        return depositorCode;
    }

    public void setDepositorCode(String depositorCode) {
        this.depositorCode = depositorCode;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Double getInArrearsAmount() {
        return inArrearsAmount;
    }

    public void setInArrearsAmount(Double inArrearsAmount) {
        this.inArrearsAmount = inArrearsAmount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
