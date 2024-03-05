package com.affirm.common.model.rcc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CalificationHeaderRcc {

    private Date rccDate;
    private String period;
    private String calificationName;
    private Integer entitiesNumber;
    private String reportedDebtSoles;
    private String reportedDebtSolesWOmortgage;
    private int arrears;
    private String estimatedFeeSoles;
    private String estimatedFeeSolesWOmortgage;
    private List<KeyRcc> keysRcc;

    public void addKeyRcc(KeyRcc keyRcc) {
        if (keysRcc == null) {
            keysRcc = new ArrayList<>();
        }
        keysRcc.add(keyRcc);
    }


    public Date getRccDate() {
        return rccDate;
    }

    public void setRccDate(Date rccDate) {
        this.rccDate = rccDate;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getCalificationName() {
        return calificationName;
    }

    public void setCalificationName(String calificationName) {
        this.calificationName = calificationName;
    }

    public Integer getEntitiesNumber() {
        return entitiesNumber;
    }

    public void setEntitiesNumber(Integer entitiesNumber) {
        this.entitiesNumber = entitiesNumber;
    }

    public String getReportedDebtSoles() {
        return reportedDebtSoles;
    }

    public void setReportedDebtSoles(String reportedDebtSoles) {
        this.reportedDebtSoles = reportedDebtSoles;
    }

    public String getReportedDebtSolesWOmortgage() {
        return reportedDebtSolesWOmortgage;
    }

    public void setReportedDebtSolesWOmortgage(String reportedDebtSolesWOmortgage) {
        this.reportedDebtSolesWOmortgage = reportedDebtSolesWOmortgage;
    }

    public int getArrears() {
        return arrears;
    }

    public void setArrears(int arrears) {
        this.arrears = arrears;
    }

    public String getEstimatedFeeSoles() {
        return estimatedFeeSoles;
    }

    public void setEstimatedFeeSoles(String estimatedFeeSoles) {
        this.estimatedFeeSoles = estimatedFeeSoles;
    }

    public String getEstimatedFeeSolesWOmortgage() {
        return estimatedFeeSolesWOmortgage;
    }

    public void setEstimatedFeeSolesWOmortgage(String estimatedFeeSolesWOmortgage) {
        this.estimatedFeeSolesWOmortgage = estimatedFeeSolesWOmortgage;
    }

    public List<KeyRcc> getKeysRcc() {
        return keysRcc;
    }

    public void setKeysRcc(List<KeyRcc> keysRcc) {
        this.keysRcc = keysRcc;
    }
}
