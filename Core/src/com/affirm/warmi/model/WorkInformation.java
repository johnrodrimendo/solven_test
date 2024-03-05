package com.affirm.warmi.model;

public class WorkInformation {

    private String taxPayerId;
    private String companyName;
    private String incomeRange;
    private String workingStartDate;
    private String lastReportedPeriod;
    private Integer employeesNumber;

    public String getTaxPayerId() {
        return taxPayerId;
    }

    public void setTaxPayerId(String taxPayerId) {
        this.taxPayerId = taxPayerId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIncomeRange() {
        return incomeRange;
    }

    public void setIncomeRange(String incomeRange) {
        this.incomeRange = incomeRange;
    }

    public String getWorkingStartDate() {
        return workingStartDate;
    }

    public void setWorkingStartDate(String workingStartDate) {
        this.workingStartDate = workingStartDate;
    }

    public String getLastReportedPeriod() {
        return lastReportedPeriod;
    }

    public void setLastReportedPeriod(String lastReportedPeriod) {
        this.lastReportedPeriod = lastReportedPeriod;
    }

    public Integer getEmployeesNumber() {
        return employeesNumber;
    }

    public void setEmployeesNumber(Integer employeesNumber) {
        this.employeesNumber = employeesNumber;
    }
}
