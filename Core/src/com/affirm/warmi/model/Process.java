package com.affirm.warmi.model;

public class Process {

    private String processId;
    private Character status;
    private String account;
    private String profileCode;
    private Integer countryId;
    private String documentType;
    private String documentNumber;
    private String registerDate;
    private String finishDate;
    private Integer userId;
    private String evaluatedPersonFullname;
    private Boolean approvePolicies;

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getProfileCode() {
        return profileCode;
    }

    public void setProfileCode(String profileCode) {
        this.profileCode = profileCode;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEvaluatedPersonFullname() {
        return evaluatedPersonFullname;
    }

    public void setEvaluatedPersonFullname(String evaluatedPersonFullname) {
        this.evaluatedPersonFullname = evaluatedPersonFullname;
    }

    public Boolean getApprovePolicies() {
        return approvePolicies;
    }

    public void setApprovePolicies(Boolean approvePolicies) {
        this.approvePolicies = approvePolicies;
    }
}
