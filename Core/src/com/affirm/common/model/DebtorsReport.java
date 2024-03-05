package com.affirm.common.model;


import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.math.BigDecimal;

public class DebtorsReport {
    Integer id;
    BigDecimal debtAmount;
    String documentId,docNumber,name,firstSurname,lastSurname,street,phoneNumber,email,district,province,department,creditCode,dueDate;

    public void fillFromDb(JSONObject json) {
            setId(JsonUtil.getIntFromJson(json,"credit_id",null));
            setDocumentId(generateStringId(JsonUtil.getIntFromJson(json,"document_id",null)));
            setDocNumber(JsonUtil.getStringFromJson(json,"document_number",null));
            setName(JsonUtil.getStringFromJson(json,"person_name",null));
            setFirstSurname(JsonUtil.getStringFromJson(json,"first_surname",null));
            setLastSurname(JsonUtil.getStringFromJson(json,"last_surname",null));
            setStreet(JsonUtil.getStringFromJson(json,"street_name",null));
            setPhoneNumber(JsonUtil.getStringFromJson(json,"phone_number",null));
            setEmail(JsonUtil.getStringFromJson(json,"email",null));
            setDistrict(JsonUtil.getStringFromJson(json,"district",null));
            setProvince(JsonUtil.getStringFromJson(json,"province",null));
            setDepartment(JsonUtil.getStringFromJson(json,"department",null));
            setCreditCode(JsonUtil.getStringFromJson(json,"credit_code",null));
            setDueDate(JsonUtil.getStringFromJson(json,"due_date",null));
            setDebtAmount(BigDecimal.valueOf(JsonUtil.getDoubleFromJson(json,"debt_amount",0.00)));
    }


    private String generateStringId(Integer i) {
        String idType = null;
        if(i == IdentityDocumentType.DNI)
            idType = "DNI";
        else if(i == IdentityDocumentType.CE)
            idType = "C.EXTRANJ";
        else if(i == IdentityDocumentType.RUC)
            idType = "RUC";
        return idType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getDebtAmount() {
        return debtAmount;
    }

    public void setDebtAmount(BigDecimal debtAmount) {
        this.debtAmount = debtAmount;
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

    public String getLastSurname() {
        return lastSurname;
    }

    public void setLastSurname(String lastSurname) {
        this.lastSurname = lastSurname;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
