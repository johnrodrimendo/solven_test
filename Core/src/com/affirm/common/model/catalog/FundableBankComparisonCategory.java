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
public class FundableBankComparisonCategory implements Serializable {

    private Category comparisonCategory;
    private Bank bank;
    private Boolean active;

    public void fillFromDb(JSONObject json, CatalogService catalog) {
        if (JsonUtil.getIntFromJson(json, "comparison_category_id", null) != null)
            setComparisonCategory(catalog.getComparisonCategoryById(JsonUtil.getIntFromJson(json, "comparison_category_id", null)));
        if (JsonUtil.getIntFromJson(json, "bank_id", null) != null)
            setBank(catalog.getBank(JsonUtil.getIntFromJson(json, "bank_id", null)));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
    }

    public Category getComparisonCategory() {
        return comparisonCategory;
    }

    public void setComparisonCategory(Category comparisonCategory) {
        this.comparisonCategory = comparisonCategory;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
