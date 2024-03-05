package com.affirm.common.model.transactional;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by jrodriguez on 09/06/16.
 */

public class LoanApplicationUpdateParams implements Serializable {

    private Integer loanApplicationId;
    private Integer minAmount;
    private Integer maxAmount;
    private Integer minInstallments;
    private Integer maxInstallments;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setMinAmount(JsonUtil.getIntFromJson(json, "min_amount", null));
        setMaxAmount(JsonUtil.getIntFromJson(json, "max_amount", null));
        setMinInstallments(JsonUtil.getIntFromJson(json, "min_installments", null));
        setMaxInstallments(JsonUtil.getIntFromJson(json, "max_installments", null));
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
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
}
