package com.affirm.dialogflow;

import com.affirm.common.model.catalog.CountryParam;

public class DialogflowRequest {

    private Integer country;
    private String name;
    private String lastname;
    private String email;
    private Integer identityDocument;
    private String birthdate;
    private String identityDocumentNumber;
    private Integer loanApplicationReason;
    private String platform;
    private String platformUserId;
    private String phoneNumber;
    private String areaCode;

    public DialogflowRequest() {
    }

    public DialogflowRequest(Integer country, String name, String lastname, Integer identityDocument, String birthdate, String identityDocumentNumber, Integer loanApplicationReason, String email) {
        this.country = country;
        this.name = name;
        this.lastname = lastname;
        this.identityDocument = identityDocument;
        this.birthdate = birthdate;
        this.identityDocumentNumber = identityDocumentNumber;
        this.loanApplicationReason = loanApplicationReason;
        this.email = email;
    }

    public DialogflowRequest(Integer country, Integer identityDocument, String birthdate, String identityDocumentNumber, Integer loanApplicationReason, String email) {
        this.country = country;
        this.identityDocument = identityDocument;
        this.birthdate = birthdate;
        this.identityDocumentNumber = identityDocumentNumber;
        this.loanApplicationReason = loanApplicationReason;
        this.email = email;
    }

    public DialogflowRequest(Integer country, Integer identityDocument, String identityDocumentNumber, Integer loanApplicationReason, String email) {
        this.country = country;
        this.identityDocument = identityDocument;
        this.identityDocumentNumber = identityDocumentNumber;
        this.loanApplicationReason = loanApplicationReason;
        this.email = email;
    }

    public static Integer defaulAmount(Integer country) {
        int defaultAmount = 0;

        if(country == CountryParam.COUNTRY_PERU) {
            defaultAmount = 7500;
        } else if (country == CountryParam.COUNTRY_ARGENTINA) {
            defaultAmount = 30000;
        }

        return defaultAmount;
    }

    public static Integer defaultInstallments(Integer country) {
        int defaultInstallments = 0;

        if(country == CountryParam.COUNTRY_PERU) {
            defaultInstallments = 24;
        } else if (country == CountryParam.COUNTRY_ARGENTINA) {
            defaultInstallments = 18;
        }

        return defaultInstallments;
    }

    public Integer getCountry() {
        return country;
    }

    public void setCountry(Integer country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getIdentityDocument() {
        return identityDocument;
    }

    public void setIdentityDocument(Integer identityDocument) {
        this.identityDocument = identityDocument;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getIdentityDocumentNumber() {
        return identityDocumentNumber;
    }

    public void setIdentityDocumentNumber(String identityDocumentNumber) {
        this.identityDocumentNumber = identityDocumentNumber;
    }

    public Integer getLoanApplicationReason() {
        return loanApplicationReason;
    }

    public void setLoanApplicationReason(Integer loanApplicationReason) {
        this.loanApplicationReason = loanApplicationReason;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getPlatformUserId() {
        return platformUserId;
    }

    public void setPlatformUserId(String platformUserId) {
        this.platformUserId = platformUserId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @Override
    public String toString() {
        return "DialogflowRequest{" +
                "country=" + country +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", identityDocument=" + identityDocument +
                ", birthdate='" + birthdate + '\'' +
                ", identityDocumentNumber='" + identityDocumentNumber + '\'' +
                ", loanApplicationReason=" + loanApplicationReason +
                ", email='" + email + '\'' +
                ", platform=" + platform +
                ", platformUserId='" + platformUserId + '\'' +
                '}';
    }
}
