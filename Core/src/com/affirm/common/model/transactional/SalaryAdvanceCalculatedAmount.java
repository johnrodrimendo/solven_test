package com.affirm.common.model.transactional;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by john on 10/01/17.
 */
public class SalaryAdvanceCalculatedAmount implements Serializable {

    private Double maxAmount;
    private String rejectionReasonKey;
    private List<EntityRate> entityRates;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setMaxAmount(JsonUtil.getDoubleFromJson(json, "max_amount", null));
        setRejectionReasonKey(JsonUtil.getStringFromJson(json, "rejection_reason", null));
        if (JsonUtil.getJsonArrayFromJson(json, "rate_commission", null) != null) {
            entityRates = new ArrayList<>();
            JSONArray entityArr = JsonUtil.getJsonArrayFromJson(json, "rate_commission", null);
            for (int i = 0; i < entityArr.length(); i++) {
                EntityRate entity = new EntityRate();
                entity.fillFromDb(entityArr.getJSONObject(i), catalog, locale);
                entityRates.add(entity);
            }
        }
    }

    public EntityRate getEntityRateByAmount(int amount) {
        EntityRate rate = null;
        for (EntityRate e : entityRates) {
            if (amount <= e.getMaxAmountCommission() && (rate == null || rate.getMaxAmountCommission() > e.getMaxAmountCommission())) {
                rate = e;
            }
        }
        return rate;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Double maxAmount) {
        this.maxAmount = maxAmount;
    }

    public String getRejectionReasonKey() {
        return rejectionReasonKey;
    }

    public void setRejectionReasonKey(String rejectionReasonKey) {
        this.rejectionReasonKey = rejectionReasonKey;
    }

    public List<EntityRate> getEntityRates() {
        return entityRates;
    }

    public void setEntityRates(List<EntityRate> entityRates) {
        this.entityRates = entityRates;
    }
}
