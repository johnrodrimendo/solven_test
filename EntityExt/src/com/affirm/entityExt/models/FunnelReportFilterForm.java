package com.affirm.entityExt.models;

import com.affirm.common.model.catalog.Product;

import java.util.List;

public class FunnelReportFilterForm {

    public final static Integer MAX_DAYS_FILTER = 31;

    private String producto;
    private String analisis;
    private String edad;
    private String medio;
    private String tipoPlastico;
    private String fromDate1;
    private String toDate1;
    private String fromDate2;
    private String toDate2;
    private Integer entity;
    private Integer base;
    private List<Product> productsUserEntity;
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;
    private String utmContent;
    private String entityProductParam;

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getAnalisis() {
        return analisis;
    }

    public void setAnalisis(String analisis) {
        this.analisis = analisis;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getMedio() {
        return medio;
    }

    public void setMedio(String medio) {
        this.medio = medio;
    }

    public String getTipoPlastico() {
        return tipoPlastico;
    }

    public void setTipoPlastico(String tipoPlastico) {
        this.tipoPlastico = tipoPlastico;
    }

    public String getFromDate1() {
        return fromDate1;
    }

    public void setFromDate1(String fromDate1) {
        this.fromDate1 = fromDate1;
    }

    public String getToDate1() {
        return toDate1;
    }

    public void setToDate1(String toDate1) {
        this.toDate1 = toDate1;
    }

    public String getFromDate2() {
        return fromDate2;
    }

    public void setFromDate2(String fromDate2) {
        this.fromDate2 = fromDate2;
    }

    public String getToDate2() {
        return toDate2;
    }

    public void setToDate2(String toDate2) {
        this.toDate2 = toDate2;
    }

    public Integer getEntity() {
        return entity;
    }

    public void setEntity(Integer entity) {
        this.entity = entity;
    }

    public List<Product> getProductsUserEntity() {
        return productsUserEntity;
    }

    public void setProductsUserEntity(List<Product> productsUserEntity) {
        this.productsUserEntity = productsUserEntity;
    }

    public Integer getBase() {
        return base;
    }

    public void setBase(Integer base) {
        this.base = base;
    }

    public String getUtmSource() {
        return utmSource;
    }

    public void setUtmSource(String utmSource) {
        this.utmSource = utmSource;
    }

    public String getUtmMedium() {
        return utmMedium;
    }

    public void setUtmMedium(String utmMedium) {
        this.utmMedium = utmMedium;
    }

    public String getUtmCampaign() {
        return utmCampaign;
    }

    public void setUtmCampaign(String utmCampaign) {
        this.utmCampaign = utmCampaign;
    }

    public String getUtmContent() {
        return utmContent;
    }

    public void setUtmContent(String utmContent) {
        this.utmContent = utmContent;
    }

    public String getEntityProductParam() {
        return entityProductParam;
    }

    public void setEntityProductParam(String entityProductParam) {
        this.entityProductParam = entityProductParam;
    }
}
