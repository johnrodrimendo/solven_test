/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.common.model.catalog.MaritalStatus;
import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationStep1Form extends FormGeneric implements Serializable {

    private String name;
    private boolean nameCanChange = true;
    private String firstSurname;
    private boolean firstSurnameCanChange = true;
    private String lastSurname;
    private boolean lastSurnameCanChange = true;
    private Integer docType;
    private boolean docTypeCanChange = true;
    private String docNumber;
    private boolean docNumberCanChange = true;
    private Integer nationality = 1;
    private String birthday;
    private boolean birthdayCanChange = true;
    private Character gender;
    private boolean genderCanChange = true;
    private Integer maritalStatus;
    private boolean maritalStatusCanChange = true;
    private String companionName;
    private String companionFirstSurname;
    private String companionLastSurname;
    private Integer companionDocType;
    private String companionDocNumber;

    private String countryCode = "51";
    private String phoneNumber;
    private boolean phoneNumberCanChange = true;
    private String email;
    private Boolean pep;
    private String cityCode;
    private String landline;

    public LoanApplicationStep1Form() {
        this.setValidator(new LoanApplicationStep1FormValidator());
    }

    public class LoanApplicationStep1FormValidator extends FormValidator implements Serializable {

        public StringFieldValidator name;
        public StringFieldValidator firstSurname;
        public StringFieldValidator lastSurname;
        public IntegerFieldValidator nationality;
        public StringFieldValidator birthday;
        public CharFieldValidator gender;
        public IntegerFieldValidator maritalStatus;
        public StringFieldValidator companionName;
        public StringFieldValidator companionFirstSurname;
        public StringFieldValidator companionLastSurname;
        public IntegerFieldValidator companionDocType;
        public StringFieldValidator companionDocNumber;
        public StringFieldValidator phoneNumber;
        public StringFieldValidator email;
        public BooleanFieldValidator pep;
        public StringFieldValidator cityCode;
        public StringFieldValidator landline;

        public LoanApplicationStep1FormValidator() {
            addValidator(name = new StringFieldValidator(ValidatorUtil.NAME));
            addValidator(firstSurname = new StringFieldValidator(ValidatorUtil.FIRST_SURNAME));
            addValidator(lastSurname = new StringFieldValidator(ValidatorUtil.LAST_SURNAME));
            addValidator(nationality = new IntegerFieldValidator(ValidatorUtil.NATIONALITY));
            addValidator(birthday = new StringFieldValidator(ValidatorUtil.BIRTHDAY));
            addValidator(gender = new CharFieldValidator(ValidatorUtil.GENDER));
            addValidator(maritalStatus = new IntegerFieldValidator(ValidatorUtil.MARITAL_STATUS_ID));
            addValidator(companionName = new StringFieldValidator(ValidatorUtil.NAME));
            addValidator(companionFirstSurname = new StringFieldValidator(ValidatorUtil.FIRST_SURNAME));
            addValidator(companionLastSurname = new StringFieldValidator(ValidatorUtil.LAST_SURNAME));
            addValidator(companionDocType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID));
            addValidator(companionDocNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI));
            addValidator(phoneNumber = new StringFieldValidator(ValidatorUtil.CELLPHONE_NUMBER));
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL));
            addValidator(pep = new BooleanFieldValidator(ValidatorUtil.PEP));
            addValidator(cityCode = new StringFieldValidator(ValidatorUtil.CITY_CODE));
            addValidator(landline = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE));
        }

        @Override
        protected void setDynamicValidations() {
            boolean required = LoanApplicationStep1Form.this.maritalStatus != null
                    && (MaritalStatus.hasPartner(LoanApplicationStep1Form.this.maritalStatus));

            companionName.setRequired(required);
            companionFirstSurname.setRequired(required);
            companionLastSurname.setRequired(required);
            companionDocType.setRequired(required);
            companionDocNumber.setRequired(required);
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return LoanApplicationStep1Form.this;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (nameCanChange)
            this.name = name;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        if (firstSurnameCanChange)
            this.firstSurname = firstSurname;
    }

    public String getLastSurname() {
        return lastSurname;
    }

    public void setLastSurname(String lastSurname) {
        if (lastSurnameCanChange)
            this.lastSurname = lastSurname;
    }

    public Integer getDocType() {
        return docType;
    }

    public void setDocType(Integer docType) {
        if (docTypeCanChange)
            this.docType = docType;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        if (docNumberCanChange)
            this.docNumber = docNumber;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        if (birthdayCanChange) {
            this.birthday = birthday;
        }
    }

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        if (genderCanChange)
            this.gender = gender;
    }

    public Integer getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(Integer maritalStatus) {
        if (maritalStatusCanChange)
            this.maritalStatus = maritalStatus;
    }

    public String getCompanionName() {
        return companionName;
    }

    public void setCompanionName(String companionName) {
        this.companionName = companionName;
    }

    public String getCompanionFirstSurname() {
        return companionFirstSurname;
    }

    public void setCompanionFirstSurname(String companionFirstSurname) {
        this.companionFirstSurname = companionFirstSurname;
    }

    public String getCompanionLastSurname() {
        return companionLastSurname;
    }

    public void setCompanionLastSurname(String companionLastSurname) {
        this.companionLastSurname = companionLastSurname;
    }

    public Integer getCompanionDocType() {
        return companionDocType;
    }

    public void setCompanionDocType(Integer companionDocType) {
        this.companionDocType = companionDocType;
    }

    public String getCompanionDocNumber() {
        return companionDocNumber;
    }

    public void setCompanionDocNumber(String companionDocNumber) {
        this.companionDocNumber = companionDocNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumberCanChange)
            this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isGenderCanChange() {
        return genderCanChange;
    }

    public void setGenderCanChange(boolean genderCanChange) {
        this.genderCanChange = genderCanChange;
    }

    public boolean isMaritalStatusCanChange() {
        return maritalStatusCanChange;
    }

    public void setMaritalStatusCanChange(boolean maritalStatusCanChange) {
        this.maritalStatusCanChange = maritalStatusCanChange;
    }

    public boolean isDocTypeCanChange() {
        return docTypeCanChange;
    }

    public void setDocTypeCanChange(boolean docTypeCanChange) {
        this.docTypeCanChange = docTypeCanChange;
    }

    public boolean isDocNumberCanChange() {
        return docNumberCanChange;
    }

    public void setDocNumberCanChange(boolean docNumberCanChange) {
        this.docNumberCanChange = docNumberCanChange;
    }

    public boolean isPhoneNumberCanChange() {
        return phoneNumberCanChange;
    }

    public void setPhoneNumberCanChange(boolean phoneNumberCanChange) {
        this.phoneNumberCanChange = phoneNumberCanChange;
    }

    public boolean isNameCanChange() {
        return nameCanChange;
    }

    public void setNameCanChange(boolean nameCanChange) {
        this.nameCanChange = nameCanChange;
    }

    public boolean isFirstSurnameCanChange() {
        return firstSurnameCanChange;
    }

    public void setFirstSurnameCanChange(boolean firstSurnameCanChange) {
        this.firstSurnameCanChange = firstSurnameCanChange;
    }

    public boolean isLastSurnameCanChange() {
        return lastSurnameCanChange;
    }

    public void setLastSurnameCanChange(boolean lastSurnameCanChange) {
        this.lastSurnameCanChange = lastSurnameCanChange;
    }

    public Integer getNationality() {
        return nationality;
    }

    public void setNationality(Integer nationality) {
        this.nationality = nationality;
    }

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public Boolean getPep() {
        return pep;
    }

    public void setPep(Boolean pep) {
        this.pep = pep;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public boolean isBirthdayCanChange() {
        return birthdayCanChange;
    }

    public void setBirthdayCanChange(boolean birthdayCanChange) {
        this.birthdayCanChange = birthdayCanChange;
    }

    @Override
    public String toString() {
        return "LoanApplicationStep1Form{" +
                "name='" + name + '\'' +
                ", nameCanChange=" + nameCanChange +
                ", firstSurname='" + firstSurname + '\'' +
                ", firstSurnameCanChange=" + firstSurnameCanChange +
                ", lastSurname='" + lastSurname + '\'' +
                ", lastSurnameCanChange=" + lastSurnameCanChange +
                ", docType=" + docType +
                ", docTypeCanChange=" + docTypeCanChange +
                ", docNumber='" + docNumber + '\'' +
                ", docNumberCanChange=" + docNumberCanChange +
                ", nationality=" + nationality +
                ", birthday='" + birthday + '\'' +
                ", gender=" + gender +
                ", genderCanChange=" + genderCanChange +
                ", maritalStatus=" + maritalStatus +
                ", maritalStatusCanChange=" + maritalStatusCanChange +
                ", companionName='" + companionName + '\'' +
                ", companionFirstSurname='" + companionFirstSurname + '\'' +
                ", companionLastSurname='" + companionLastSurname + '\'' +
                ", companionDocType=" + companionDocType +
                ", companionDocNumber='" + companionDocNumber + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", phoneNumberCanChange=" + phoneNumberCanChange +
                ", email='" + email + '\'' +
                ", pep=" + pep +
                ", cityCode='" + cityCode + '\'' +
                ", landline='" + landline + '\'' +
                '}';
    }
}
