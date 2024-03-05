package com.affirm.backoffice.model;

import com.affirm.common.model.catalog.Entity;

import java.util.ArrayList;
import java.util.List;

public class OriginationReportEntityGroup {

    private Entity entity;
    private List<OriginationReportOriginationTypeGroup> originationTypes;
    private boolean totalReport = false;

    public OriginationReportEntityGroup(Entity entity) {
        this.entity = entity;
        originationTypes = new ArrayList<>();
    }

    public String getEntityName(){
        if(totalReport)
            return "TOTAL ORIGINADO";
        else
            return entity.getFullName();
    }

    public Integer getTotalDetailQuantityByPeriod(String period){
        return originationTypes.stream().map(o -> o.getProducts()).flatMap(List::stream).map(p -> p.getDetailByPeriod(period)).mapToInt(p -> p.getQuantity()).sum();
    }

    public Double getTotalDetailLoanCapitalByPeriod(String period){
        return originationTypes.stream().map(o -> o.getProducts()).flatMap(List::stream).map(p -> p.getDetailByPeriod(period)).mapToDouble(p -> p.getLoanCapital()).sum();
    }

    public Double getTotalDetailEntityCommissionByPeriod(String period){
        return originationTypes.stream().map(o -> o.getProducts()).flatMap(List::stream).map(p -> p.getDetailByPeriod(period)).mapToDouble(p -> p.getEntityCommission()).sum();
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public List<OriginationReportOriginationTypeGroup> getOriginationTypes() {
        return originationTypes;
    }

    public void setOriginationTypes(List<OriginationReportOriginationTypeGroup> originationTypes) {
        this.originationTypes = originationTypes;
    }

    public boolean isTotalReport() {
        return totalReport;
    }

    public void setTotalReport(boolean totalReport) {
        this.totalReport = totalReport;
    }
}
