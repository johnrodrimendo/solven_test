package com.affirm.common.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class PersonInteractionFollowUpReport {

    private String initcap;
    private String email;
    private String name;
    private String firstSurname;
    private String lastSurname;
    private String documentNumber;
    private Integer loanApplicationId;
    private Integer creditId;
    private String event;

    public void fillFromDb(JSONObject json) {
        setInitcap(JsonUtil.getStringFromJson(json, "initcap", null));
        setEmail(JsonUtil.getStringFromJson(json, "destination", null));
        setName(JsonUtil.getStringFromJson(json, "person_name", null));
        setFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
        setEvent(JsonUtil.getStringFromJson(json, "v_event", null));
    }

    public String getInitcap() {
        return initcap;
    }

    public void setInitcap(String initcap) {
        this.initcap = initcap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public Integer getCreditId() {
        return creditId;
    }

    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
