package com.affirm.backoffice.model;

import com.affirm.backoffice.util.IPaginationWrapperElement;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * Created by dev5 on 15/09/17.
 */
public class ReportNoHolding implements IPaginationWrapperElement {

    private String creditCode;
    private Integer creditId;
    private Integer installmentId;
    private Double liborValue;
    private Double liborMonthlyPlusSeven;
    private Double liborDailyPlusSeven;
    private Integer loanDays;
    private Double interestLiborPlusSeven;
    private Double interestTax;
    private Double taxWithHolding;
    private Double entitySubTotal;
    private Double entityCumulativeTotal;
    private Double solvenDistribution;
    private Double netToEntity;
    private CountryParam country;

    public void fillFromDb(CatalogService catalogService, JSONObject json) throws Exception {
        setCreditCode(JsonUtil.getStringFromJson(json, "credit_code", null));
        setCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
        setInstallmentId(JsonUtil.getIntFromJson(json, "installment_id", null));
        setLiborValue(JsonUtil.getDoubleFromJson(json, "libor_value", null));
        setLiborMonthlyPlusSeven(JsonUtil.getDoubleFromJson(json, "libor_monthly_plus_seven", null));
        setLiborDailyPlusSeven(JsonUtil.getDoubleFromJson(json, "libor_daily_plus_seven", null));
        setLoanDays(JsonUtil.getIntFromJson(json, "loan_days", null));
        setInterestLiborPlusSeven(JsonUtil.getDoubleFromJson(json, "interest_on_libor_plus_seven", null));
        setInterestTax(JsonUtil.getDoubleFromJson(json, "interest_tax", null));
        setTaxWithHolding(JsonUtil.getDoubleFromJson(json, "tax_withholding", null));
        setEntitySubTotal(JsonUtil.getDoubleFromJson(json, "entity_sub_total", null));
        setEntityCumulativeTotal(JsonUtil.getDoubleFromJson(json, "entity_cumulative_total", null));
        setSolvenDistribution(JsonUtil.getDoubleFromJson(json, "solven_distribution", null));
        setNetToEntity(JsonUtil.getDoubleFromJson(json, "net_to_entity", null));
        setCountry(catalogService.getCountryParam(JsonUtil.getIntFromJson(json, "country_id", null)));
    }

    public void fillFromDb(JSONObject json, CatalogService catalog, MessageSource messageSource, Locale locale) throws Exception {
        fillFromDb(catalog, json);
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public Integer getCreditId() {
        return creditId;
    }

    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
    }

    public Integer getInstallmentId() {
        return installmentId;
    }

    public void setInstallmentId(Integer installmentId) {
        this.installmentId = installmentId;
    }

    public Double getLiborValue() {
        return liborValue;
    }

    public void setLiborValue(Double liborValue) {
        this.liborValue = liborValue;
    }

    public Double getLiborMonthlyPlusSeven() {
        return liborMonthlyPlusSeven;
    }

    public void setLiborMonthlyPlusSeven(Double liborMonthlyPlusSeven) {
        this.liborMonthlyPlusSeven = liborMonthlyPlusSeven;
    }

    public Double getLiborDailyPlusSeven() {
        return liborDailyPlusSeven;
    }

    public void setLiborDailyPlusSeven(Double liborDailyPlusSeven) {
        this.liborDailyPlusSeven = liborDailyPlusSeven;
    }

    public Integer getLoanDays() {
        return loanDays;
    }

    public void setLoanDays(Integer loanDays) {
        this.loanDays = loanDays;
    }

    public Double getInterestLiborPlusSeven() {
        return interestLiborPlusSeven;
    }

    public void setInterestLiborPlusSeven(Double interestLiborPlusSeven) {
        this.interestLiborPlusSeven = interestLiborPlusSeven;
    }

    public Double getInterestTax() {
        return interestTax;
    }

    public void setInterestTax(Double interestTax) {
        this.interestTax = interestTax;
    }

    public Double getTaxWithHolding() {
        return taxWithHolding;
    }

    public void setTaxWithHolding(Double taxWithHolding) {
        this.taxWithHolding = taxWithHolding;
    }

    public Double getEntitySubTotal() {
        return entitySubTotal;
    }

    public void setEntitySubTotal(Double entitySubTotal) {
        this.entitySubTotal = entitySubTotal;
    }

    public Double getEntityCumulativeTotal() {
        return entityCumulativeTotal;
    }

    public void setEntityCumulativeTotal(Double entityCumulativeTotal) {
        this.entityCumulativeTotal = entityCumulativeTotal;
    }

    public Double getSolvenDistribution() {
        return solvenDistribution;
    }

    public void setSolvenDistribution(Double solvenDistribution) {
        this.solvenDistribution = solvenDistribution;
    }

    public Double getNetToEntity() {
        return netToEntity;
    }

    public void setNetToEntity(Double netToEntity) {
        this.netToEntity = netToEntity;
    }

    public CountryParam getCountry() {
        return country;
    }

    public void setCountry(CountryParam country) {
        this.country = country;
    }
}
