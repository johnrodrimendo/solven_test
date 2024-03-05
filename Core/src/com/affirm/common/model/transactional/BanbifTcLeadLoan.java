package com.affirm.common.model.transactional;


import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

public class BanbifTcLeadLoan {

    private Integer loanApplicationId;
    private String documentNumber;
    private String name;
    private String lastName;
    private String lastSurname;
    private String email;
    private String cellphone;
    private String banbifBaseType;
    private String banbifChannel;
    private IdentityDocumentType documentType;
    private Integer personId;
    private Date registerDate;

    public String getLastSurname() {
        return lastSurname;
    }

    public void setLastSurname(String lastSurname) {
        this.lastSurname = lastSurname;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public IdentityDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(IdentityDocumentType documentType) {
        this.documentType = documentType;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getBanbifBaseType() {
        return banbifBaseType;
    }

    public void setBanbifBaseType(String banbifBaseType) {
        this.banbifBaseType = banbifBaseType;
    }

    public String getBanbifChannel() {
        return banbifChannel;
    }

    public void setBanbifChannel(String banbifChannel) {
        this.banbifChannel = banbifChannel;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception{

        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setName(JsonUtil.getStringFromJson(json, "person_name", null));
        setLastName(JsonUtil.getStringFromJson(json, "first_surname", null));
        setLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        setCellphone(JsonUtil.getStringFromJson(json, "phone_number", null));
        setBanbifBaseType(JsonUtil.getStringFromJson(json, "banbif_base_type", null));
        setBanbifChannel(JsonUtil.getStringFromJson(json, "banbif_channel", null));
        if (JsonUtil.getIntFromJson(json, "document_id", null) != null) {
            setDocumentType(catalog.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_id", null)));
        }
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
    }


}
