/**
 *
 */
package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class ProductMaxMinParameter implements Serializable{

    private Integer countryId;
    private Integer productId;
    private Integer minAmount;
    private Integer maxAmount;
    private Integer minInstallments;
    private Integer maxInstallments;
    private Double minTea;
    private Double maxTea;
    private Integer daysAfterEndOfMonth;
    private Integer daysBeforeEndOfMonth;
    private Integer minDaysFirstDueDate;
    private Integer maxDaysFirstDueDate;
    private Boolean active;
    private Integer entityId;

    public void fillFromDb(JSONObject json, CatalogService catalog) {
        setCountryId(JsonUtil.getIntFromJson(json, "country_id", null));
        setProductId(JsonUtil.getIntFromJson(json, "product_id", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setMinAmount(JsonUtil.getIntFromJson(json, "min_amount", null));
        setMaxAmount(JsonUtil.getIntFromJson(json, "max_amount", null));
        setMinInstallments(JsonUtil.getIntFromJson(json, "min_installments", null));
        setMaxInstallments(JsonUtil.getIntFromJson(json, "max_installments", null));
        setMinTea(JsonUtil.getDoubleFromJson(json, "min_effective_annual_rate", null));
        setMaxTea(JsonUtil.getDoubleFromJson(json, "max_effective_annual_rate", null));
        setDaysAfterEndOfMonth(JsonUtil.getIntFromJson(json, "days_after_end_of_month", null));
        setDaysBeforeEndOfMonth(JsonUtil.getIntFromJson(json, "days_before_end_of_month", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setMinDaysFirstDueDate(JsonUtil.getIntFromJson(json, "min_days_first_due_date", null));
        setMaxDaysFirstDueDate(JsonUtil.getIntFromJson(json, "max_days_first_due_date", null));
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getEntityId() { return entityId; }

    public void setEntityId(Integer entityId) { this.entityId = entityId; }

    public Integer getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Integer minAmount) {
        this.minAmount = minAmount;
    }

    public Integer getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Integer maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Integer getMinInstallments() {
        return minInstallments;
    }

    public void setMinInstallments(Integer minInstallments) {
        this.minInstallments = minInstallments;
    }

    public Integer getMaxInstallments() {
        return maxInstallments;
    }

    public void setMaxInstallments(Integer maxInstallments) {
        this.maxInstallments = maxInstallments;
    }

    public Double getMinTea() {
        return minTea;
    }

    public void setMinTea(Double minTea) {
        this.minTea = minTea;
    }

    public Double getMaxTea() {
        return maxTea;
    }

    public void setMaxTea(Double maxTea) {
        this.maxTea = maxTea;
    }

    public Integer getDaysAfterEndOfMonth() {
        return daysAfterEndOfMonth;
    }

    public void setDaysAfterEndOfMonth(Integer daysAfterEndOfMonth) {
        this.daysAfterEndOfMonth = daysAfterEndOfMonth;
    }

    public Integer getDaysBeforeEndOfMonth() {
        return daysBeforeEndOfMonth;
    }

    public void setDaysBeforeEndOfMonth(Integer daysBeforeEndOfMonth) {
        this.daysBeforeEndOfMonth = daysBeforeEndOfMonth;
    }

    public Integer getMinDaysFirstDueDate() {
        return minDaysFirstDueDate;
    }

    public void setMinDaysFirstDueDate(Integer minDaysFirstDueDate) {
        this.minDaysFirstDueDate = minDaysFirstDueDate;
    }

    public Integer getMaxDaysFirstDueDate() {
        return maxDaysFirstDueDate;
    }

    public void setMaxDaysFirstDueDate(Integer maxDaysFirstDueDate) {
        this.maxDaysFirstDueDate = maxDaysFirstDueDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
