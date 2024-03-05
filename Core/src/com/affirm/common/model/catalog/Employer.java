package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by john on 04/01/17.
 */
public class Employer implements Serializable {

    private Integer id;
    private String name;
    private Boolean active;
    private String ruc;
    private String logoUrl;
    private Integer defaultPaymentDay;
    private Integer firstDueDateDays;
    private Integer daysAfterEndOfMonth;
    private Integer daysBeforeEndOfMonth;
    private String address;
    private String phoneNumber;
    private Integer employerGroupId;
    private Profession profession;
    private Integer cutoffDay;
    private Integer agreementPaymentDay;
    private Boolean entityActive;
    private Double entityTea;

    public void fillFromDb(CatalogService catalogService, Locale locale, JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "employer_id", null));
        setName(JsonUtil.getStringFromJson(json, "employer", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setRuc(JsonUtil.getStringFromJson(json, "ruc", null));
        setLogoUrl(JsonUtil.getStringFromJson(json, "employer_logo", null));
        setDefaultPaymentDay(JsonUtil.getIntFromJson(json, "default_payment_day", null));
        setFirstDueDateDays(JsonUtil.getIntFromJson(json, "first_due_date_days", null));
        setDaysAfterEndOfMonth(JsonUtil.getIntFromJson(json, "days_after_end_of_month", null));
        setDaysBeforeEndOfMonth(JsonUtil.getIntFromJson(json, "days_before_end_of_month", null));
        setAddress(JsonUtil.getStringFromJson(json, "address", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        setEmployerGroupId(JsonUtil.getIntFromJson(json, "employer_group_id", null));
        setCutoffDay(JsonUtil.getIntFromJson(json, "cutoff_day", null));
        setAgreementPaymentDay(JsonUtil.getIntFromJson(json, "agreement_payment_day", null));
        setEntityActive(JsonUtil.getBooleanFromJson(json, "is_entity_active", false));
        setEntityTea(JsonUtil.getDoubleFromJson(json, "effective_annual_rate", 0.0));
        if(JsonUtil.getIntFromJson(json, "profession_id", null) != null) {
            setProfession(catalogService.getProfession(locale, JsonUtil.getIntFromJson(json, "profession_id", null)));
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Integer getDefaultPaymentDay() {
        return defaultPaymentDay;
    }

    public void setDefaultPaymentDay(Integer defaultPaymentDay) {
        this.defaultPaymentDay = defaultPaymentDay;
    }

    public Integer getFirstDueDateDays() {
        return firstDueDateDays;
    }

    public void setFirstDueDateDays(Integer firstDueDateDays) {
        this.firstDueDateDays = firstDueDateDays;
    }

    public Integer getDaysAfterEndOfMonth() {
        return daysAfterEndOfMonth;
    }

    public void setDaysAfterEndOfMonth(Integer daysAfterEndOfMonth) {
        this.daysAfterEndOfMonth = daysAfterEndOfMonth;
    }

    public Integer getDaysBeforeEndOfMonth() {
        return daysBeforeEndOfMonth;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDaysBeforeEndOfMonth(Integer daysBeforeEndOfMonth) {
        this.daysBeforeEndOfMonth = daysBeforeEndOfMonth;
    }

    public Integer getEmployerGroupId() {
        return employerGroupId;
    }

    public void setEmployerGroupId(Integer employerGroupId) {
        this.employerGroupId = employerGroupId;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public Integer getAgreementPaymentDay() { return agreementPaymentDay; }

    public void setAgreementPaymentDay(Integer agreementPaymentDay) { this.agreementPaymentDay = agreementPaymentDay; }

    public Integer getCutoffDay() { return cutoffDay; }

    public void setCutoffDay(Integer cutoffDay) { this.cutoffDay = cutoffDay; }

    public Boolean getEntityActive() { return entityActive; }

    public void setEntityActive(Boolean entityActive) { this.entityActive = entityActive; }

    public Double getEntityTea() { return entityTea; }

    public void setEntityTea(Double entityTea) { this.entityTea = entityTea; }
}
