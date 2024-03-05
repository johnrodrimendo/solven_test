package com.affirm.common.model.rcc;

import java.util.Date;

public class RccDetail {

    private String accountType;
    private String accountCode;
    private String accountName;
    private double solesAmount;
    private double dollarAmount;
    private int arrears;
    private Date rccDate;
    private String originalDebtCode;
    private String calificationName;

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public double getSolesAmount() {
        return solesAmount;
    }

    public void setSolesAmount(double solesAmount) {
        this.solesAmount = solesAmount;
    }

    public double getDollarAmount() {
        return dollarAmount;
    }

    public void setDollarAmount(double dollarAmount) {
        this.dollarAmount = dollarAmount;
    }

    public int getArrears() {
        return arrears;
    }

    public void setArrears(int arrears) {
        this.arrears = arrears;
    }

    public Date getRccDate() {
        return rccDate;
    }

    public void setRccDate(Date rccDate) {
        this.rccDate = rccDate;
    }

    public String getOriginalDebtCode() {
        return originalDebtCode;
    }

    public void setOriginalDebtCode(String originalDebtCode) {
        this.originalDebtCode = originalDebtCode;
    }

    public String getCalificationName() {
        return calificationName;
    }

    public void setCalificationName(String calificationName) {
        this.calificationName = calificationName;
    }
}





