package com.affirm.entityExt.models;

public class DocumentationExtractedDataFromDatalabels {

    private String documentNumber;
    private String personName;
    private String firstSurname;
    private String otherSurnames;
    private String locality;
    private String documentExpirationDate;
    private String dateOfBirth;

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public String getOtherSurnames() {
        return otherSurnames;
    }

    public void setOtherSurnames(String otherSurnames) {
        this.otherSurnames = otherSurnames;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getDocumentExpirationDate() {
        return documentExpirationDate;
    }

    public void setDocumentExpirationDate(String documentExpirationDate) {
        this.documentExpirationDate = documentExpirationDate;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
