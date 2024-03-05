package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.Bank;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by dev5 on 28/11/17.
 */
public class BankAccount {

    public static final char ACCOUNT_TYPE_AHORRO = 'S';
    public static final char ACCOUNT_TYPE_CORRIENTE = 'C';
    public static final char ACCOUNT_TYPE_RECAUDO = 'R';
    public static final char PAYMENT_TYPE_INTERNET = 'I';
    public static final char PAYMENT_TYPE_VENTANILLA = 'V';

    private Bank bank;
    private Character accountType;
    private Character paymentType;
    private String accountNumber;
    private String cciCode;
    private Integer currencyId;
    private Boolean isActive;

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setBank(catalogService.getBank(JsonUtil.getIntFromJson(json, "bank_id", null)));
        setAccountType(JsonUtil.getCharacterFromJson(json, "bank_account_type", null));
        setPaymentType(JsonUtil.getCharacterFromJson(json, "payment_type", null));
        setAccountNumber(JsonUtil.getStringFromJson(json, "bank_account_number", null));
        setCciCode(JsonUtil.getStringFromJson(json, "cci_code", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setCurrencyId(JsonUtil.getIntFromJson(json, "currency_id", null));
    }

    public Character getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Character paymentType) {
        this.paymentType = paymentType;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public Character getAccountType() {
        return accountType;
    }

    public void setAccountType(Character accountType) {
        this.accountType = accountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCciCode() {
        return cciCode;
    }

    public void setCciCode(String cciCode) {
        this.cciCode = cciCode;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }
}
