package com.affirm.pagolink.model;

import org.json.JSONObject;

public class PagoLinkCreate {

    public final static String SINGLEPAY_ORDERTYPE = "SINGLEPAY";

    private String externalId;
    private String orderType;
    private String description;
    private Double amount;
    private JSONObject customData;
    private String currencyId = "PEN";
    private PagoLinkCustomer customer;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public JSONObject getCustomData() {
        return customData;
    }

    public void setCustomData(JSONObject customData) {
        this.customData = customData;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public PagoLinkCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(PagoLinkCustomer customer) {
        this.customer = customer;
    }

}
