package com.affirm.backoffice.model;

import java.util.ArrayList;
import java.util.List;

public class OriginationReportOriginationTypeGroup {

    private Integer originationType;
    private List<OriginationReportProductGroup> products;
    private boolean totalReport = false;

    public OriginationReportOriginationTypeGroup(Integer originationType) {
        this.originationType = originationType;
        products = new ArrayList<>();
    }

    public String getOriginationName(){
        if(totalReport)
            return "TOTAL ORIGINADO";
        else if(originationType == OriginationReportPeriodDetail.ORIGINATION_CLOSED_PLATFORM)
            return "Plataforma Cerrada";
        else if(originationType == OriginationReportPeriodDetail.ORIGINATION_MARKETPLACE)
            return "Marketplace";
        else if(originationType == OriginationReportPeriodDetail.ORIGINATION_WHITE_LABEL)
            return "White Label";
        else
            return "Unknow";
    }

    public Integer getTotalDetailQuantityByPeriod(String period){
        return products.stream().map(p -> p.getDetailByPeriod(period)).mapToInt(p -> p.getQuantity()).sum();
    }

    public Double getTotalDetailLoanCapitalByPeriod(String period){
        return products.stream().map(p -> p.getDetailByPeriod(period)).mapToDouble(p -> p.getLoanCapital()).sum();
    }

    public Double getTotalDetailEntityCommissionByPeriod(String period){
        return products.stream().map(p -> p.getDetailByPeriod(period)).mapToDouble(p -> p.getEntityCommission()).sum();
    }

    public Integer getOriginationType() {
        return originationType;
    }

    public void setOriginationType(Integer originationType) {
        this.originationType = originationType;
    }

    public List<OriginationReportProductGroup> getProducts() {
        return products;
    }

    public void setProducts(List<OriginationReportProductGroup> products) {
        this.products = products;
    }

    public boolean isTotalReport() {
        return totalReport;
    }

    public void setTotalReport(boolean totalReport) {
        this.totalReport = totalReport;
    }
}
