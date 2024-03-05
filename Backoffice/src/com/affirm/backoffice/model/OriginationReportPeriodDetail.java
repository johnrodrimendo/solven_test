package com.affirm.backoffice.model;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.Product;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class OriginationReportPeriodDetail {

    public static final int ORIGINATION_CLOSED_PLATFORM = 1;
    public static final int ORIGINATION_MARKETPLACE = 2;
    public static final int ORIGINATION_WHITE_LABEL = 3;

    private Entity entity;
    private Product product;
    private Integer origination;
    private String period;
    private Integer quantity;
    private Double loanCapital;
    private Double entityCommission;

    public OriginationReportPeriodDetail() {
    }

    public OriginationReportPeriodDetail(OriginationReportPeriodDetail detail) {
        this.entity = detail.getEntity();
        this.product = detail.getProduct();
        this.origination = detail.getOrigination();
        this.period = detail.getPeriod();
        this.quantity = detail.getQuantity();
        this.loanCapital = detail.getLoanCapital();
        this.entityCommission = detail.getEntityCommission();
    }

    public OriginationReportPeriodDetail(Entity entity, Product product, Integer origination, String period, Integer quantity, Double loanCapital, Double entityCommission) {
        this.entity = entity;
        this.product = product;
        this.origination = origination;
        this.period = period;
        this.quantity = quantity;
        this.loanCapital = loanCapital;
        this.entityCommission = entityCommission;
    }

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        if (JsonUtil.getIntFromJson(json, "entity_id", null) != null)
            setEntity(catalog.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        if (JsonUtil.getIntFromJson(json, "product_id", null) != null)
            setProduct(catalog.getProduct(JsonUtil.getIntFromJson(json, "product_id", null)));
        setPeriod(JsonUtil.getStringFromJson(json, "period", null));
        setQuantity(JsonUtil.getIntFromJson(json, "quantity", null));
        setLoanCapital(JsonUtil.getDoubleFromJson(json, "loan_capital", null));
        setOrigination(JsonUtil.getIntFromJson(json, "origination", null));
        setEntityCommission(JsonUtil.getDoubleFromJson(json, "entity_commission", null));
    }

    public void sumDetailData(OriginationReportPeriodDetail detail) {
        setQuantity(getQuantity() + detail.getQuantity());
        setLoanCapital(getLoanCapital() + (detail.getLoanCapital() != null ? detail.getLoanCapital() : 0));
        setEntityCommission(getEntityCommission() + (detail.getEntityCommission() != null ? detail.getEntityCommission() : 0));
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getLoanCapital() {
        return loanCapital;
    }

    public void setLoanCapital(Double loanCapital) {
        this.loanCapital = loanCapital;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Integer getOrigination() {
        return origination;
    }

    public void setOrigination(Integer origination) {
        this.origination = origination;
    }

    public Double getEntityCommission() {
        return entityCommission;
    }

    public void setEntityCommission(Double entityCommission) {
        this.entityCommission = entityCommission;
    }
}
