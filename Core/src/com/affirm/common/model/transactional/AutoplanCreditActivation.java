package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class AutoplanCreditActivation {

    private Integer loanApplicationId;
    private Integer documentId;
    private String documentNumber;
    private String firstSurname;
    private String lastSurname;
    private String name;
    private Date birthday;
    private String phoneNumber;
    private String email;
    private Date registerDate;
    private String status;
    private Double amount;

    public void fillFromDb(JSONObject json) throws Exception {
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setDocumentId(JsonUtil.getIntFromJson(json, "document_id", null));
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setFirstSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        setName(JsonUtil.getStringFromJson(json, "person_name", null));
        setBirthday(JsonUtil.getPostgresDateFromJson(json, "birthday", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setStatus(JsonUtil.getStringFromJson(json, "status", null));
        setAmount(JsonUtil.getDoubleFromJson(json, "amount", null));
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public Integer getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
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

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "AutoplanCreditActivation{" +
                "loanApplicationId=" + loanApplicationId +
                ", documentId=" + documentId +
                ", documentNumber='" + documentNumber + '\'' +
                ", firstSurname='" + firstSurname + '\'' +
                ", lastSurname='" + lastSurname + '\'' +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", registerDate=" + registerDate +
                ", status='" + status + '\'' +
                ", amount=" + amount +
                '}';
    }
}
