package com.affirm.backoffice.model;

import com.affirm.common.model.catalog.Bank;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

/**
 * Created by jrodriguez on 24/08/16.
 */
public class CreditPayment {

    private Integer id;
    private Integer creditId;
    private Boolean acredited;
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
    private Double ammount;
    private Double arrearsAmmount;
    private Double minAmmount;
    private String registerType;
    private String creditCode;
    private Double pendingAmount;;
    private Boolean overAlert;
    private Boolean underAlert;
    private Boolean statusAlert;
    private Credit credit;
    private CountryParam country;

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "credit_payment_id", null));
        setCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
        setAcredited(JsonUtil.getBooleanFromJson(json, "is_accredited", null));
        if (JsonUtil.getIntFromJson(json, "bank_id", null) != null) {
            setBank(catalog.getBank(JsonUtil.getIntFromJson(json, "bank_id", null)));
        }
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
        setAmmount(JsonUtil.getDoubleFromJson(json, "amount", null));
        setArrearsAmmount(JsonUtil.getDoubleFromJson(json, "arrears_amount", null));
        setMinAmmount(JsonUtil.getDoubleFromJson(json, "min_amount", null));
        setRegisterType(JsonUtil.getStringFromJson(json, "register_type", null));
        setCreditCode(JsonUtil.getStringFromJson(json, "credit_code", null));
        setPendingAmount(JsonUtil.getDoubleFromJson(json, "pending_installment_amount", null));
        setOverAlert(JsonUtil.getBooleanFromJson(json, "over_alert", null));
        setUnderAlert(JsonUtil.getBooleanFromJson(json, "under_alert", null));
        setStatusAlert(JsonUtil.getBooleanFromJson(json, "status_alert", null));
        setCountry(catalog.getCountryParam(JsonUtil.getIntFromJson(json, "country_id", null)));
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCreditId() {
        return creditId;
    }

    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
    }

    public Boolean getAcredited() {
        return acredited;
    }

    public void setAcredited(Boolean acredited) {
        this.acredited = acredited;
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

    public Double getAmmount() {
        return ammount;
    }

    public void setAmmount(Double ammount) {
        this.ammount = ammount;
    }

    public Double getArrearsAmmount() {
        return arrearsAmmount;
    }

    public void setArrearsAmmount(Double arrearsAmmount) {
        this.arrearsAmmount = arrearsAmmount;
    }

    public Double getMinAmmount() {
        return minAmmount;
    }

    public void setMinAmmount(Double minAmmount) {
        this.minAmmount = minAmmount;
    }

    public String getRegisterType() {
        return registerType;
    }

    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }

    public Credit getCredit() {
        return credit;
    }

    public void setCredit(Credit credit) {
        this.credit = credit;
    }

    public Double getPendingAmount() {
        return pendingAmount;
    }

    public void setPendingAmount(Double pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public Boolean getOverAlert() {
        return overAlert;
    }

    public void setOverAlert(Boolean overAlert) {
        this.overAlert = overAlert;
    }

    public Boolean getUnderAlert() {
        return underAlert;
    }

    public void setUnderAlert(Boolean underAlert) {
        this.underAlert = underAlert;
    }

    public Boolean getStatusAlert() {
        return statusAlert;
    }

    public void setStatusAlert(Boolean statusAlert) {
        this.statusAlert = statusAlert;
    }

    public CountryParam getCountry() {
        return country;
    }

    public void setCountry(CountryParam country) {
        this.country = country;
    }
}
