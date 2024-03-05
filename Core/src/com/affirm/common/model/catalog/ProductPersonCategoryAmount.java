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
public class ProductPersonCategoryAmount implements Serializable {

    private Integer id;
    private Integer minAmount;
    private Integer maxAmount;
    private Integer maxInstallments;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "person_category_id", null));
        setMinAmount(JsonUtil.getIntFromJson(json, "min_ammount", null));
        setMaxAmount(JsonUtil.getIntFromJson(json, "max_ammount", null));
        setMaxInstallments(JsonUtil.getIntFromJson(json, "max_installments", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public Integer getMaxInstallments() {
        return maxInstallments;
    }

    public void setMaxInstallments(Integer maxInstallments) {
        this.maxInstallments = maxInstallments;
    }
}
