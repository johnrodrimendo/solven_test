package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by dev5 on 16/06/17.
 */
public class PersonValidator {

    private Integer docType;
    private String docNumber;
    private Boolean isValidPhoneNumber;
    private Boolean isValidEmail;
    private Boolean isValidBankAccount;
    private Boolean isValidCCI;

    public void fillFromDb(JSONObject json, Locale locale) {
        setDocType(JsonUtil.getIntFromJson(json, "doctype", null));
        setDocNumber(JsonUtil.getStringFromJson(json, "docnumber", null));
        setValidPhoneNumber(!JsonUtil.getBooleanFromJson(json, "phonenumber", null));
        setValidEmail(!JsonUtil.getBooleanFromJson(json, "email", null));
        setValidBankAccount(!JsonUtil.getBooleanFromJson(json, "accountnumber", null));
        setValidCCI(!JsonUtil.getBooleanFromJson(json, "accountnumbercci", null));
    }

    public Boolean isValidPhoneNumber() {
        return isValidPhoneNumber;
    }

    public void setValidPhoneNumber(Boolean validPhoneNumber) {
        isValidPhoneNumber = validPhoneNumber;
    }

    public Boolean isValidEmail() {
        return isValidEmail;
    }

    public void setValidEmail(Boolean validEmail) {
        isValidEmail = validEmail;
    }

    public Boolean isValidBankAccount() {
        return isValidBankAccount;
    }

    public void setValidBankAccount(Boolean validBankAccount) {
        isValidBankAccount = validBankAccount;
    }

    public Boolean isValidCCI() {
        return isValidCCI;
    }

    public void setValidCCI(Boolean validCCI) {
        isValidCCI = validCCI;
    }

    public Integer getDocType() {
        return docType;
    }

    public void setDocType(Integer docType) {
        this.docType = docType;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }
}
