package com.affirm.common.model;

public class BankAccountOfferData {

    public  static final char TRADITIONAL_TYPE = 'T';
    public  static final char HIGH_PROFITABILITY_TYPE = 'H';

    public  static final String TRADITIONAL_NAME = "CUENTA DÍA A DÍA";
    public  static final String HIGH_PROFITABILITY_NAME = "CUENTA AHORRO META";

    private Character type;
    private Double annualInterest;
    private Boolean physicalAccountType;
    private Boolean virtualAccountType;
    private Integer monthlyWithdrawalATM;
    private Integer monthlyWithdrawalAgency;
    private Double monthlyMaintenanceAmount;
    private Integer allowedOperations;
    private String currency;
    private Integer currencyId;
    private Double trea;

    public Character getType() {
        return type;
    }

    public void setType(Character type) {
        this.type = type;
    }

    public Double getAnnualInterest() {
        return annualInterest;
    }

    public void setAnnualInterest(Double annualInterest) {
        this.annualInterest = annualInterest;
    }

    public Boolean getPhysicalAccountType() {
        return physicalAccountType;
    }

    public void setPhysicalAccountType(Boolean physicalAccountType) {
        this.physicalAccountType = physicalAccountType;
    }

    public Boolean getVirtualAccountType() {
        return virtualAccountType;
    }

    public void setVirtualAccountType(Boolean virtualAccountType) {
        this.virtualAccountType = virtualAccountType;
    }

    public Integer getMonthlyWithdrawalATM() {
        return monthlyWithdrawalATM;
    }

    public void setMonthlyWithdrawalATM(Integer monthlyWithdrawalATM) {
        this.monthlyWithdrawalATM = monthlyWithdrawalATM;
    }

    public Integer getMonthlyWithdrawalAgency() {
        return monthlyWithdrawalAgency;
    }

    public void setMonthlyWithdrawalAgency(Integer monthlyWithdrawalAgency) {
        this.monthlyWithdrawalAgency = monthlyWithdrawalAgency;
    }

    public Double getMonthlyMaintenanceAmount() {
        return monthlyMaintenanceAmount;
    }

    public void setMonthlyMaintenanceAmount(Double monthlyMaintenanceAmount) {
        this.monthlyMaintenanceAmount = monthlyMaintenanceAmount;
    }

    public Integer getAllowedOperations() {
        return allowedOperations;
    }

    public void setAllowedOperations(Integer allowedOperations) {
        this.allowedOperations = allowedOperations;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public Double getTrea() {
        return trea;
    }

    public void setTrea(Double trea) {
        this.trea = trea;
    }
}
