package com.affirm.warmi.model;

public class DriverFine {

    private String infringementNumber;
    private String rule;
    private String infringementName;
    private String infringementDate;
    private String plateNumber;
    private Double amount;
    private String licence;
    private String status;

    public String getInfringementNumber() {
        return infringementNumber;
    }

    public void setInfringementNumber(String infringementNumber) {
        this.infringementNumber = infringementNumber;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getInfringementName() {
        return infringementName;
    }

    public void setInfringementName(String infringementName) {
        this.infringementName = infringementName;
    }

    public String getInfringementDate() {
        return infringementDate;
    }

    public void setInfringementDate(String infringementDate) {
        this.infringementDate = infringementDate;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
