package com.affirm.warmi.model;

import java.util.List;

public class FinancialSystem {

    private String period;
    private String calification;
    private Double reportedDebtNC;
    private Double reportedDebtFC;
    private Integer arrearDays;
    private Double estimatedInstallmentNC;
    private Double estimatedInstallmentFC;
    private Long entitiesNumber;
    private List<PeriodDetail> periodDetails;

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

    public Double getReportedDebtNC() {
        return reportedDebtNC;
    }

    public void setReportedDebtNC(Double reportedDebtNC) {
        this.reportedDebtNC = reportedDebtNC;
    }

    public Double getReportedDebtFC() {
        return reportedDebtFC;
    }

    public void setReportedDebtFC(Double reportedDebtFC) {
        this.reportedDebtFC = reportedDebtFC;
    }

    public Integer getArrearDays() {
        return arrearDays;
    }

    public void setArrearDays(Integer arrearDays) {
        this.arrearDays = arrearDays;
    }

    public Double getEstimatedInstallmentNC() {
        return estimatedInstallmentNC;
    }

    public void setEstimatedInstallmentNC(Double estimatedInstallmentNC) {
        this.estimatedInstallmentNC = estimatedInstallmentNC;
    }

    public Double getEstimatedInstallmentFC() {
        return estimatedInstallmentFC;
    }

    public void setEstimatedInstallmentFC(Double estimatedInstallmentFC) {
        this.estimatedInstallmentFC = estimatedInstallmentFC;
    }

    public Long getEntitiesNumber() {
        return entitiesNumber;
    }

    public void setEntitiesNumber(Long entitiesNumber) {
        this.entitiesNumber = entitiesNumber;
    }

    public List<PeriodDetail> getPeriodDetails() {
        return periodDetails;
    }

    public void setPeriodDetails(List<PeriodDetail> periodDetails) {
        this.periodDetails = periodDetails;
    }
}
