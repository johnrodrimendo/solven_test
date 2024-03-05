package com.affirm.aws.lambda.model;

import java.util.List;

public class CBULambdaResponse {
    private List<CBUOwnerLambdaResponse> owners;
    private String type;
    private boolean is_active;
    private String currency;
    private String label;
    private CBUAccountRoutingLambdaResponse account_routing;
    private CBUBankRoutingLambdaResponse bank_routing;

    public CBULambdaResponse() {
    }

    public List<CBUOwnerLambdaResponse> getOwners() {
        return owners;
    }

    public void setOwners(List<CBUOwnerLambdaResponse> owners) {
        this.owners = owners;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public CBUAccountRoutingLambdaResponse getAccount_routing() {
        return account_routing;
    }

    public void setAccount_routing(CBUAccountRoutingLambdaResponse account_routing) {
        this.account_routing = account_routing;
    }

    public CBUBankRoutingLambdaResponse getBank_routing() {
        return bank_routing;
    }

    public void setBank_routing(CBUBankRoutingLambdaResponse bank_routing) {
        this.bank_routing = bank_routing;
    }

    @Override
    public String toString() {
        return "CBULambdaResponse{" +
                "owners=" + owners +
                ", type='" + type + '\'' +
                ", is_active=" + is_active +
                ", currency='" + currency + '\'' +
                ", label='" + label + '\'' +
                ", account_routing=" + account_routing +
                ", bank_routing=" + bank_routing +
                '}';
    }
}

