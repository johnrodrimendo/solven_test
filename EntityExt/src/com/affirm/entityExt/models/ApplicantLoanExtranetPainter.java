package com.affirm.entityExt.models;

import com.affirm.acceso.model.Direccion;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.PersonContactInformation;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.model.transactional.PersonProfessionOccupation;

import java.util.Date;

public class ApplicantLoanExtranetPainter {

    private Integer loanApplicationId;
    private Integer userId;
    private Integer personId;
    private String name;
    private String firstSurname;
    private String lastSurname;
    private IdentityDocumentType documentType;
    private String documentNumber;
    private MaritalStatus maritalStatus;
    private Date birthday;
    private Nationality nationality;
    private Integer dependents;
    private PersonProfessionOccupation professionOccupation;
    private Boolean pep;
    private StudyLevel studyLevel;
    private Profession profession;
    private String phoneNumber;
    private Boolean phoneVerified;
    private String email;
    private Boolean emailVerified;
    private Direccion disagregatedAddress;
    private PersonContactInformation personContactInformation;
    private String address;
    private PersonOcupationalInformation personOcupationalInformation;
    private Person partner;
    private CountryParam countryParam;
    private Double navLatitude;
    private Double navLongitude;
    private Boolean fatca;

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
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

    public IdentityDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(IdentityDocumentType documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Nationality getNationality() {
        return nationality;
    }

    public void setNationality(Nationality nationality) {
        this.nationality = nationality;
    }

    public Integer getDependents() {
        return dependents;
    }

    public void setDependents(Integer dependents) {
        this.dependents = dependents;
    }

    public PersonProfessionOccupation getProfessionOccupation() {
        return professionOccupation;
    }

    public void setProfessionOccupation(PersonProfessionOccupation professionOccupation) {
        this.professionOccupation = professionOccupation;
    }

    public Boolean getPep() {
        return pep;
    }

    public void setPep(Boolean pep) {
        this.pep = pep;
    }

    public StudyLevel getStudyLevel() {
        return studyLevel;
    }

    public void setStudyLevel(StudyLevel studyLevel) {
        this.studyLevel = studyLevel;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Direccion getDisagregatedAddress() {
        return disagregatedAddress;
    }

    public void setDisagregatedAddress(Direccion disagregatedAddress) {
        this.disagregatedAddress = disagregatedAddress;
    }

    public PersonContactInformation getPersonContactInformation() {
        return personContactInformation;
    }

    public void setPersonContactInformation(PersonContactInformation personContactInformation) {
        this.personContactInformation = personContactInformation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public PersonOcupationalInformation getPersonOcupationalInformation() {
        return personOcupationalInformation;
    }

    public void setPersonOcupationalInformation(PersonOcupationalInformation personOcupationalInformation) {
        this.personOcupationalInformation = personOcupationalInformation;
    }

    public Person getPartner() {
        return partner;
    }

    public void setPartner(Person partner) {
        this.partner = partner;
    }

    public CountryParam getCountryParam() {
        return countryParam;
    }

    public void setCountryParam(CountryParam countryParam) {
        this.countryParam = countryParam;
    }

    public Double getNavLatitude() {
        return navLatitude;
    }

    public void setNavLatitude(Double navLatitude) {
        this.navLatitude = navLatitude;
    }

    public Double getNavLongitude() {
        return navLongitude;
    }

    public void setNavLongitude(Double navLongitude) {
        this.navLongitude = navLongitude;
    }

    public Boolean getFatca() {
        return fatca;
    }

    public void setFatca(Boolean fatca) {
        this.fatca = fatca;
    }
}
