package com.affirm.common.model.transactional;

public class OnpeDetail {
    private String code;
    private String electoralProcess;
    private String omissionType;
    private Double amount;
    private String collectionStage;
    private String message;

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    public String getElectoralProcess() { return electoralProcess; }

    public void setElectoralProcess(String electoralProcess) { this.electoralProcess = electoralProcess; }

    public String getOmissionType() { return omissionType; }

    public void setOmissionType(String omissionType) { this.omissionType = omissionType; }

    public Double getAmount() { return amount; }

    public void setAmount(Double amount) { this.amount = amount; }

    public String getCollectionStage() { return collectionStage; }

    public void setCollectionStage(String collectionStage) { this.collectionStage = collectionStage; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }
}
