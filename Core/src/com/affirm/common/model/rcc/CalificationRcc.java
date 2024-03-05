package com.affirm.common.model.rcc;

import java.util.Date;

public class CalificationRcc {

    private Date rccDate;
    private Integer entities;
    private Double normal;
    private Double cpp;
    private Double defficient;
    private Double doubtful;
    private Double loss;
    private String calification;
    private String personType;
    private String debtCode;

    public Date getRccDate() {
        return rccDate;
    }

    public void setRccDate(Date rccDate) {
        this.rccDate = rccDate;
    }

    public Integer getEntities() {
        return entities;
    }

    public void setEntities(Integer entities) {
        this.entities = entities;
    }

    public Double getNormal() {
        return normal;
    }

    public void setNormal(Double normal) {
        this.normal = normal;
    }

    public Double getCpp() {
        return cpp;
    }

    public void setCpp(Double cpp) {
        this.cpp = cpp;
    }

    public Double getDefficient() {
        return defficient;
    }

    public void setDefficient(Double defficient) {
        this.defficient = defficient;
    }

    public Double getDoubtful() {
        return doubtful;
    }

    public void setDoubtful(Double doubtful) {
        this.doubtful = doubtful;
    }

    public Double getLoss() {
        return loss;
    }

    public void setLoss(Double loss) {
        this.loss = loss;
    }

    public String getCalification() {
        return calification;
    }

    public void setCalification(String calification) {
        this.calification = calification;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getDebtCode() {
        return debtCode;
    }

    public void setDebtCode(String debtCode) {
        this.debtCode = debtCode;
    }
}
