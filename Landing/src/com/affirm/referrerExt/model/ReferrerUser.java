package com.affirm.referrerExt.model;

import com.affirm.common.model.catalog.Bank;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class ReferrerUser implements Serializable {

    private Integer id;
    private IdentityDocumentType documentType;
    private String documentNumber;
    private String name;
    private String firstSurname;
    private String email;
    private String phoneNumber;
    private Bank bank;
    private String bankAccountNumber;
    private String cci;
    private Integer personId;
    private Date registerDate;


    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setId(JsonUtil.getIntFromJson(json, "referrer_user_id", null));
        if (JsonUtil.getIntFromJson(json, "document_type_id", null) != null)
            setDocumentType(catalogService.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_type_id", null)));
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setName(JsonUtil.getStringFromJson(json, "name", null));
        setFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        if (JsonUtil.getIntFromJson(json, "bank_id", null) != null)
            setBank(catalogService.getBank(JsonUtil.getIntFromJson(json, "bank_id", null)));
        setBankAccountNumber(JsonUtil.getStringFromJson(json, "bank_account_number", null));
        setCci(JsonUtil.getStringFromJson(json, "cci", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public IdentityDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(IdentityDocumentType documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getCci() {
        return cci;
    }

    public void setCci(String cci) {
        this.cci = cci;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }
}
