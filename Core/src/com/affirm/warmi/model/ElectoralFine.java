package com.affirm.warmi.model;

public class ElectoralFine {

    private String code;
    private String description;
    private Double amount;
    private String comissionType;
    private String collectionStage;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getComissionType() {
        return comissionType;
    }

    public void setComissionType(String comissionType) {
        this.comissionType = comissionType;
    }

    public String getCollectionStage() {
        return collectionStage;
    }

    public void setCollectionStage(String collectionStage) {
        this.collectionStage = collectionStage;
    }
}
