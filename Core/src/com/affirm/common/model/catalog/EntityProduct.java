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
public class EntityProduct implements Serializable {

    private Integer entityId;
    private Product product;
    private Employer employer;
    private Boolean customMaxAmount;

    public void fillFromDb(JSONObject json, CatalogService catalog) {
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        if (JsonUtil.getIntFromJson(json, "product_id", null) != null) {
            setProduct(catalog.getProduct(JsonUtil.getIntFromJson(json, "product_id", null)));
        }
        if (JsonUtil.getIntFromJson(json, "employer_id", null) != null) {
            setEmployer(catalog.getEmployer(JsonUtil.getIntFromJson(json, "employer_id", null)));
        }
        setCustomMaxAmount(JsonUtil.getBooleanFromJson(json, "custom_max_amount", null));
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    public Boolean getCustomMaxAmount() {
        return customMaxAmount;
    }

    public void setCustomMaxAmount(Boolean customMaxAmount) {
        this.customMaxAmount = customMaxAmount;
    }
}
