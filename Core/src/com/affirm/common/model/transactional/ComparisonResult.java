package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Bank;
import com.affirm.common.model.catalog.Category;
import com.affirm.common.model.catalog.FundableBankComparisonCategory;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 09/06/16.
 */

public class ComparisonResult implements Serializable {

    private Integer bankProductRateId;
    private Bank bank;
    private Category comparisonCategoy;
    private FundableBankComparisonCategory fundableBankComparisonCategory;
    private String product;
    private String productUrl;
    private String resume;
    private Double minEffectiveAnualRate;
    private Double maxEffectiveAnualRate;
    private Date registerDate;
    private Integer brandId;
    private Integer minIncomes;
    private Boolean active;
    private Double effectiveAnualCostrate;
    private Double effectiveAnualRate;
    private Double monthlyPaymentAvg;
    private Double insurancePaymentAvg;
    private Double totalPayment;
    private Double monthlyCostsAvg;
    private List<ComparisonResultCost> costs = new ArrayList<>();

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) {
        setBankProductRateId(JsonUtil.getIntFromJson(json, "bank_product_rate_id", null));
        if (JsonUtil.getIntFromJson(json, "bank_id", null) != null)
            setBank(catalog.getBank(JsonUtil.getIntFromJson(json, "bank_id", null)));
        if (JsonUtil.getIntFromJson(json, "comparison_category_id", null) != null)
            setComparisonCategoy(catalog.getComparisonCategoryById(JsonUtil.getIntFromJson(json, "comparison_category_id", null)));
        if (getBank() != null && getComparisonCategoy() != null)
            setFundableBankComparisonCategory(catalog.getFundableBankComparisonCategory(getBank().getId(), getComparisonCategoy().getId()));
        setProduct(JsonUtil.getStringFromJson(json, "product", null));
        setProductUrl(JsonUtil.getStringFromJson(json, "product_url", null));
        setResume(JsonUtil.getStringFromJson(json, "resume", null));
        setMinEffectiveAnualRate(JsonUtil.getDoubleFromJson(json, "effective_annual_rate_min", null));
        setMaxEffectiveAnualRate(JsonUtil.getDoubleFromJson(json, "effective_annual_rate_max", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setBrandId(JsonUtil.getIntFromJson(json, "brand_id", null));
        setMinIncomes(JsonUtil.getIntFromJson(json, "minimun_incomes", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        if (JsonUtil.getJsonArrayFromJson(json, "costs", null) != null) {
            setCosts(new ArrayList<>());
            JSONArray arrayCosts = JsonUtil.getJsonArrayFromJson(json, "costs", null);
            for (int i = 0; i < arrayCosts.length(); i++) {
                ComparisonResultCost cost = new ComparisonResultCost();
                cost.fillFromDb(arrayCosts.getJSONObject(i), catalog, locale);
                getCosts().add(cost);
            }
        }
    }

    public boolean canApplyOnlineSolven() {
        return fundableBankComparisonCategory != null
                && fundableBankComparisonCategory.getBank() != null
                && fundableBankComparisonCategory.getBank().getEntity() != null
                && fundableBankComparisonCategory.getComparisonCategory() != null
                && fundableBankComparisonCategory.getComparisonCategory().getProduct() != null;
    }

    public Integer getBankProductRateId() {
        return bankProductRateId;
    }

    public void setBankProductRateId(Integer bankProductRateId) {
        this.bankProductRateId = bankProductRateId;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public Double getMinEffectiveAnualRate() {
        return minEffectiveAnualRate;
    }

    public void setMinEffectiveAnualRate(Double minEffectiveAnualRate) {
        this.minEffectiveAnualRate = minEffectiveAnualRate;
    }

    public Double getMaxEffectiveAnualRate() {
        return maxEffectiveAnualRate;
    }

    public void setMaxEffectiveAnualRate(Double maxEffectiveAnualRate) {
        this.maxEffectiveAnualRate = maxEffectiveAnualRate;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getMinIncomes() {
        return minIncomes;
    }

    public void setMinIncomes(Integer minIncomes) {
        this.minIncomes = minIncomes;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<ComparisonResultCost> getCosts() {
        return costs;
    }

    public void setCosts(List<ComparisonResultCost> costs) {
        this.costs = costs;
    }

    public Double getEffectiveAnualCostrate() {
        return effectiveAnualCostrate;
    }

    public void setEffectiveAnualCostrate(Double effectiveAnualCostrate) {
        this.effectiveAnualCostrate = effectiveAnualCostrate;
    }

    public Double getEffectiveAnualRate() {
        return effectiveAnualRate;
    }

    public void setEffectiveAnualRate(Double effectiveAnualRate) {
        this.effectiveAnualRate = effectiveAnualRate;
    }

    public Category getComparisonCategoy() {
        return comparisonCategoy;
    }

    public void setComparisonCategoy(Category comparisonCategoy) {
        this.comparisonCategoy = comparisonCategoy;
    }

    public FundableBankComparisonCategory getFundableBankComparisonCategory() {
        return fundableBankComparisonCategory;
    }

    public void setFundableBankComparisonCategory(FundableBankComparisonCategory fundableBankComparisonCategory) {
        this.fundableBankComparisonCategory = fundableBankComparisonCategory;
    }

    public Double getMonthlyPaymentAvg() {
        return monthlyPaymentAvg;
    }

    public void setMonthlyPaymentAvg(Double monthlyPaymentAvg) {
        this.monthlyPaymentAvg = monthlyPaymentAvg;
    }

    public Double getInsurancePaymentAvg() {
        return insurancePaymentAvg;
    }

    public void setInsurancePaymentAvg(Double insurancePaymentAvg) {
        this.insurancePaymentAvg = insurancePaymentAvg;
    }

    public Double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(Double totalPayment) {
        this.totalPayment = totalPayment;
    }

    public Double getMonthlyCostsAvg() {
        return monthlyCostsAvg;
    }

    public void setMonthlyCostsAvg(Double monthlyCostsAvg) {
        this.monthlyCostsAvg = monthlyCostsAvg;
    }
}