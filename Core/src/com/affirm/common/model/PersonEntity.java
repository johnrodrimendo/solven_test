package com.affirm.common.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class PersonEntity {

    private Integer personId;
    private Integer entityId;
    private String associatedId;
    private String passbookNumber;
    private Boolean isValidated;
    private Date validatedDate;
    private Date registerDate;

    public void fillFromDb(JSONObject json) {
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setAssociatedId(JsonUtil.getStringFromJson(json, "associated_id", null));
        setPassbookNumber(JsonUtil.getStringFromJson(json, "passbook_number", null));
        setValidated(JsonUtil.getBooleanFromJson(json, "is_validated", null));
        setValidatedDate(JsonUtil.getPostgresDateFromJson(json, "validated_date", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getAssociatedId() {
        return associatedId;
    }

    public void setAssociatedId(String associatedId) {
        this.associatedId = associatedId;
    }

    public String getPassbookNumber() {
        return passbookNumber;
    }

    public void setPassbookNumber(String passbookNumber) {
        this.passbookNumber = passbookNumber;
    }

    public Boolean getValidated() {
        return isValidated;
    }

    public void setValidated(Boolean validated) {
        isValidated = validated;
    }

    public Date getValidatedDate() {
        return validatedDate;
    }

    public void setValidatedDate(Date validatedDate) {
        this.validatedDate = validatedDate;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }
}
