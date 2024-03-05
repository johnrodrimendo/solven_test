package com.affirm.warmi.model;

public class PeriodDetail {

    private String entity;
    private String debtType;
    private Double reportedDebtNC;
    private Double reportedDebtFC;
    private Integer arrearDays;
    private String calification;

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getDebtType() {
        return debtType;
    }

    public void setDebtType(String debtType) {
        this.debtType = debtType;
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

    public String getCalification() {
        return calification;
    }

    public void setCalification(String calification) {
        this.calification = calification;
    }
}
