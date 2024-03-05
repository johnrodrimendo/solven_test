package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Bank;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

/**
 * Created by jrodriguez on 24/08/16.
 */
public class MultiCreditPayment {

    private Integer id;
    private Integer employerId;
    private Boolean disaggregated;
    private Bank bank;
    private String accountNumber;
    private String depositorName;
    private String returningInfo;
    private Date paymentDate;
    private String subsidiary;
    private String depositorId;
    private String currency;
    private String paymentDocNumber;
    private String identificationDocNumber;
    private Date expirationDate;
    private Double amount;
    private Double arrearsAmount;
    private Double minAmount;
    private Character registerType;
    private Integer updateSysUserId;
    private Double cashSurplus;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "credit_payment_multi_id", null));
        setDisaggregated(JsonUtil.getBooleanFromJson(json, "is_disaggregated", null));
        if (JsonUtil.getIntFromJson(json, "bank_id", null) != null)
            setBank(catalog.getBank(JsonUtil.getIntFromJson(json, "bank_id", null)));
        setAccountNumber(JsonUtil.getStringFromJson(json, "account_number", null));
        setDepositorName(JsonUtil.getStringFromJson(json, "depositor_name", null));
        setReturningInfo(JsonUtil.getStringFromJson(json, "returning_info", null));
        setPaymentDate(JsonUtil.getPostgresDateFromJson(json, "payment_date", null));
        setSubsidiary(JsonUtil.getStringFromJson(json, "subsidiary", null));
        setDepositorId(JsonUtil.getStringFromJson(json, "depositor_id", null));
        setCurrency(JsonUtil.getStringFromJson(json, "currency", null));
        setPaymentDocNumber(JsonUtil.getStringFromJson(json, "payment_document_number", null));
        setIdentificationDocNumber(JsonUtil.getStringFromJson(json, "identification_document_number", null));
        setExpirationDate(JsonUtil.getPostgresDateFromJson(json, "expiration_date", null));
        setAmount(JsonUtil.getDoubleFromJson(json, "amount", null));
        setArrearsAmount(JsonUtil.getDoubleFromJson(json, "arrears_amount", null));
        setMinAmount(JsonUtil.getDoubleFromJson(json, "min_amount", null));
        setRegisterType(JsonUtil.getCharacterFromJson(json, "register_type", null));
        setUpdateSysUserId(JsonUtil.getIntFromJson(json, "update_sys_user_id", null));
        setEmployerId(JsonUtil.getIntFromJson(json, "employer_id", null));
        setCashSurplus(JsonUtil.getDoubleFromJson(json, "cash_surplus", null));

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEmployerId() {
        return employerId;
    }

    public void setEmployerId(Integer employerId) {
        this.employerId = employerId;
    }

    public Boolean getDisaggregated() {
        return disaggregated;
    }

    public void setDisaggregated(Boolean disaggregated) {
        this.disaggregated = disaggregated;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getDepositorName() {
        return depositorName;
    }

    public void setDepositorName(String depositorName) {
        this.depositorName = depositorName;
    }

    public String getReturningInfo() {
        return returningInfo;
    }

    public void setReturningInfo(String returningInfo) {
        this.returningInfo = returningInfo;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getSubsidiary() {
        return subsidiary;
    }

    public void setSubsidiary(String subsidiary) {
        this.subsidiary = subsidiary;
    }

    public String getDepositorId() {
        return depositorId;
    }

    public void setDepositorId(String depositorId) {
        this.depositorId = depositorId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentDocNumber() {
        return paymentDocNumber;
    }

    public void setPaymentDocNumber(String paymentDocNumber) {
        this.paymentDocNumber = paymentDocNumber;
    }

    public String getIdentificationDocNumber() {
        return identificationDocNumber;
    }

    public void setIdentificationDocNumber(String identificationDocNumber) {
        this.identificationDocNumber = identificationDocNumber;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getArrearsAmount() {
        return arrearsAmount;
    }

    public void setArrearsAmount(Double arrearsAmount) {
        this.arrearsAmount = arrearsAmount;
    }

    public Double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Double minAmount) {
        this.minAmount = minAmount;
    }

    public Character getRegisterType() {
        return registerType;
    }

    public void setRegisterType(Character registerType) {
        this.registerType = registerType;
    }

    public Integer getUpdateSysUserId() {
        return updateSysUserId;
    }

    public void setUpdateSysUserId(Integer updateSysUserId) {
        this.updateSysUserId = updateSysUserId;
    }

    public Double getCashSurplus() {
        return cashSurplus;
    }

    public void setCashSurplus(Double cashSurplus) {
        this.cashSurplus = cashSurplus;
    }
}
