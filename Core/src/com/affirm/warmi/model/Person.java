package com.affirm.warmi.model;

public class Person {

    private String documentType;
    private String documentNumber;
    private String dateOfBirth;
    private String surnames;
    private String names;
    private String foreignNationality;
    private String foreignResidenceType;
    private String foreignResidenceDueDate;
    private String foreignDocumentDueDate;
    private String foreignDocumentExpeditionDate;
    private Boolean perFindMinor;
    private Boolean perFindDeceased;

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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSurnames() {
        return surnames;
    }

    public void setSurnames(String surnames) {
        this.surnames = surnames;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getForeignNationality() {
        return foreignNationality;
    }

    public void setForeignNationality(String foreignNationality) {
        this.foreignNationality = foreignNationality;
    }

    public String getForeignResidenceType() {
        return foreignResidenceType;
    }

    public void setForeignResidenceType(String foreignResidenceType) {
        this.foreignResidenceType = foreignResidenceType;
    }

    public String getForeignResidenceDueDate() {
        return foreignResidenceDueDate;
    }

    public void setForeignResidenceDueDate(String foreignResidenceDueDate) {
        this.foreignResidenceDueDate = foreignResidenceDueDate;
    }

    public String getForeignDocumentDueDate() {
        return foreignDocumentDueDate;
    }

    public void setForeignDocumentDueDate(String foreignDocumentDueDate) {
        this.foreignDocumentDueDate = foreignDocumentDueDate;
    }

    public String getForeignDocumentExpeditionDate() {
        return foreignDocumentExpeditionDate;
    }

    public void setForeignDocumentExpeditionDate(String foreignDocumentExpeditionDate) {
        this.foreignDocumentExpeditionDate = foreignDocumentExpeditionDate;
    }

    public Boolean getPerFindMinor() {
        return perFindMinor;
    }

    public void setPerFindMinor(Boolean perFindMinor) {
        this.perFindMinor = perFindMinor;
    }

    public Boolean getPerFindDeceased() {
        return perFindDeceased;
    }

    public void setPerFindDeceased(Boolean perFindDeceased) {
        this.perFindDeceased = perFindDeceased;
    }
}
