package com.affirm.common.model.rcc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class FinancialSystem {

    private String period;
    private String calification;
    private double reportedDebtSoles;
    private double reportedDebtSolesWOmortgage;
    private int arrears;
    private double estimatedFeeSoles;
    private double estimatedFeeSolesWOmortgage;
    private long entitiesNumber;
    private Date periodDate;
    private List<FinancialSystemDetail> financialSystemDetails;
    private List<KeyRcc> keysRcc;

    public void addKeyRcc(KeyRcc keyRcc) {
        if (keysRcc == null) {
            keysRcc = new ArrayList<>();
        }
        keysRcc.add(keyRcc);
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getCalification() {
        return calification;
    }

    public void setCalification(String calification) {
        this.calification = calification;
    }

    public double getReportedDebtSoles() {
        return reportedDebtSoles;
    }

    public void setReportedDebtSoles(double reportedDebtSoles) {
        this.reportedDebtSoles = reportedDebtSoles;
    }

    public double getReportedDebtSolesWOmortgage() {
        return reportedDebtSolesWOmortgage;
    }

    public void setReportedDebtSolesWOmortgage(double reportedDebtSolesWOmortgage) {
        this.reportedDebtSolesWOmortgage = reportedDebtSolesWOmortgage;
    }

    public int getArrears() {
        return arrears;
    }

    public void setArrears(int arrears) {
        this.arrears = arrears;
    }

    public double getEstimatedFeeSoles() {
        return estimatedFeeSoles;
    }

    public void setEstimatedFeeSoles(double estimatedFeeSoles) {
        this.estimatedFeeSoles = estimatedFeeSoles;
    }

    public double getEstimatedFeeSolesWOmortgage() {
        return estimatedFeeSolesWOmortgage;
    }

    public void setEstimatedFeeSolesWOmortgage(double estimatedFeeSolesWOmortgage) {
        this.estimatedFeeSolesWOmortgage = estimatedFeeSolesWOmortgage;
    }

    public long getEntitiesNumber() {
        return entitiesNumber;
    }

    public void setEntitiesNumber(long entitiesNumber) {
        this.entitiesNumber = entitiesNumber;
    }

    public Date getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(Date periodDate) {
        this.periodDate = periodDate;
    }

    public List<FinancialSystemDetail> getFinancialSystemDetails() {
        return financialSystemDetails;
    }

    public void setFinancialSystemDetails(List<FinancialSystemDetail> financialSystemDetails) {
        this.financialSystemDetails = financialSystemDetails;
    }

    public List<KeyRcc> getKeysRcc() {
        return keysRcc;
    }

    public void setKeysRcc(List<KeyRcc> keysRcc) {
        this.keysRcc = keysRcc;
    }
}
