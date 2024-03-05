/**
 *
 */
package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class RateCommission implements Serializable {

    private Integer id;
    private Integer productId;
    private Integer entityId;
    private Integer priceId;
    private Integer clusterId;
    private Integer installments;
    private Double effectiveAnualRate;
    private Integer maxAmountCommission;
    private Integer minAmount;
    private Integer minInstallments;

    public void fillFromDb(JSONObject json){
        setId(JsonUtil.getIntFromJson(json, "rate_commission_id", null));
        setProductId(JsonUtil.getIntFromJson(json, "product_id", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setPriceId(JsonUtil.getIntFromJson(json, "price_id", null));
        setClusterId(JsonUtil.getIntFromJson(json, "cluster_id", null));
        setInstallments(JsonUtil.getIntFromJson(json, "installments", null));
        setEffectiveAnualRate(JsonUtil.getDoubleFromJson(json, "effective_annual_rate", null));
        setMaxAmountCommission(JsonUtil.getIntFromJson(json, "max_ammount_comm", null));
        setMinAmount(JsonUtil.getIntFromJson(json, "min_ammount", null));
        setMinInstallments(JsonUtil.getIntFromJson(json, "min_installments", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getPriceId() {
        return priceId;
    }

    public void setPriceId(Integer priceId) {
        this.priceId = priceId;
    }

    public Integer getClusterId() {
        return clusterId;
    }

    public void setClusterId(Integer clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Double getEffectiveAnualRate() {
        return effectiveAnualRate;
    }

    public void setEffectiveAnualRate(Double effectiveAnualRate) {
        this.effectiveAnualRate = effectiveAnualRate;
    }

    public Integer getMaxAmountCommission() {
        return maxAmountCommission;
    }

    public void setMaxAmountCommission(Integer maxAmountCommission) {
        this.maxAmountCommission = maxAmountCommission;
    }

    public Integer getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Integer minAmount) {
        this.minAmount = minAmount;
    }

    public Integer getMinInstallments() {
        return minInstallments;
    }

    public void setMinInstallments(Integer minInstallments) {
        this.minInstallments = minInstallments;
    }
}

