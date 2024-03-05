/**
 *
 */
package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Cluster;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public class EntityRate implements Serializable {

    private Integer productId;
    private String entity;
    private Integer installments;
    private Double effectiveAnualRate;
    private Double commission;
    private String commissionType;
    private Integer maxAmountCommission;
    private Cluster cluster;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setProductId(JsonUtil.getIntFromJson(json, "product_id", null));
        setEntity(JsonUtil.getStringFromJson(json, "entity", null));
        setInstallments(JsonUtil.getIntFromJson(json, "installments", null));
        setEffectiveAnualRate(JsonUtil.getDoubleFromJson(json, "effective_annual_rate", null));
        setCommission(JsonUtil.getDoubleFromJson(json, "commission", null));
        setCommissionType(JsonUtil.getStringFromJson(json, "commission_type", null));
        setMaxAmountCommission(JsonUtil.getIntFromJson(json, "max_ammount_comm", null));
        setCluster(catalog.getCluster(JsonUtil.getIntFromJson(json, "cluster_id", null), locale));
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
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

    public String getCommissionType() {
        return commissionType;
    }

    public void setCommissionType(String commissionType) {
        this.commissionType = commissionType;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public Integer getMaxAmountCommission() {
        return maxAmountCommission;
    }

    public void setMaxAmountCommission(Integer maxAmountCommission) {
        this.maxAmountCommission = maxAmountCommission;
    }
}

