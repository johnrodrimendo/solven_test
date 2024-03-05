package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.ComparisonCreditCost;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by jrodriguez on 09/06/16.
 */

public class ComparisonResultCost implements Serializable {

    private ComparisonCreditCost comparisonCreditCost;
    private Double value;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) {
        if (JsonUtil.getIntFromJson(json, "credit_cost_id", null) != null)
            setComparisonCreditCost(catalog.getComparisonCreditCost(JsonUtil.getIntFromJson(json, "credit_cost_id", null), locale));
        setValue(JsonUtil.getDoubleFromJson(json, "cost", null));
    }

    public ComparisonCreditCost getComparisonCreditCost() {
        return comparisonCreditCost;
    }

    public void setComparisonCreditCost(ComparisonCreditCost comparisonCreditCost) {
        this.comparisonCreditCost = comparisonCreditCost;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}