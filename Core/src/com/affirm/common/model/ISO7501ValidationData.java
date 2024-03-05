package com.affirm.common.model;

import com.affirm.common.EntityProductParamIdentityValidationParamsConfig;
import com.affirm.common.model.catalog.IdentityDocumentType;

import java.io.Serializable;
import java.util.Date;

public class ISO7501ValidationData implements Serializable {

    private Boolean isValid;
    private String gender;
    private String documentNumber;
    private Date birthdate;
    private Date expirationDate;

    private String lastName;
    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }
}
