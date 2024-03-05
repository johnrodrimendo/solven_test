package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dev5 on 19/06/17.
 */
public class ComparisonCategory {

    private Integer rateId;
    private Integer categoryId;
    private Integer bankId;
    private String productName;
    private String productURL;
    private String resume;
    private Double maxTEA;
    private Double minTEA;
    private Double minimunIncomes;
    private Integer brandId;
    private Date registerDate;
    private Boolean active;
    private List<ComparisonCost> costs;


    public void fillFromDb(JSONObject json){
        setCategoryId(JsonUtil.getIntFromJson(json, "comparison_category_id", null));
        setRateId(JsonUtil.getIntFromJson(json, "bank_product_rate_id", null));
        setBankId(JsonUtil.getIntFromJson(json, "bank_id", null));
        setProductName(JsonUtil.getStringFromJson(json, "product", null));
        setProductURL(JsonUtil.getStringFromJson(json, "product_url", null));
        setResume(JsonUtil.getStringFromJson(json, "resume", null));
        setMinTEA(JsonUtil.getDoubleFromJson(json, "effective_annual_rate_min", null));
        setMaxTEA(JsonUtil.getDoubleFromJson(json, "effective_annual_rate_max", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setMinimunIncomes(JsonUtil.getDoubleFromJson(json, "minimun_incomes", null));
        setBrandId(JsonUtil.getIntFromJson(json, "brand_id", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        JSONArray comparisonProductsArray = JsonUtil.getJsonArrayFromJson(json, "costs", null);
        setCosts(new ArrayList<>());
        if(comparisonProductsArray!=null){
            for (int i=0; i < comparisonProductsArray.length(); i++) {
                ComparisonCost comparisonCost = new ComparisonCost();
                comparisonCost.fillFromDb(comparisonProductsArray.getJSONObject(i));
                getCosts().add(comparisonCost);
            }
        }
    }


    public Integer getRateId() {
        return rateId;
    }

    public void setRateId(Integer rateId) {
        this.rateId = rateId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductURL() {
        return productURL;
    }

    public void setProductURL(String productURL) {
        this.productURL = productURL;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public Double getMaxTEA() {
        return maxTEA;
    }

    public void setMaxTEA(Double maxTEA) {
        this.maxTEA = maxTEA;
    }

    public Double getMinTEA() {
        return minTEA;
    }

    public void setMinTEA(Double minTEA) {
        this.minTEA = minTEA;
    }

    public Double getMinimunIncomes() {
        return minimunIncomes;
    }

    public void setMinimunIncomes(Double minimunIncomes) {
        this.minimunIncomes = minimunIncomes;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<ComparisonCost> getCosts() {
        return costs;
    }

    public void setCosts(List<ComparisonCost> costs) {
        this.costs = costs;
    }
}
