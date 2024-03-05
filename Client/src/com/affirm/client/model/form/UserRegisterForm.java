package com.affirm.client.model.form;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.catalog.LoanApplicationReason;
import com.affirm.common.model.transactional.UserFacebook;
import com.affirm.common.util.*;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Date;

public class UserRegisterForm extends FormGeneric implements Serializable {

    private static final Logger logger = Logger.getLogger(UserRegisterForm.class);

    private String name;
    private String firstSurname;
    private String lastSurname;
    private Integer docType;
    private String docNumber;
    private String birthDate;

    // Cellphone data
    private String countryCode = "51";
    private String phoneNumber;

    // Facebook data
    private String facebookId;
    private String facebookEmail;
    private String facebookName;
    private String facebookFirstName;
    private String facebookLastName;
    private Integer facebookAgeMax;
    private Integer facebookAgeMin;
    private String facebookLink;
    private String facebookGender;
    private String facebookLocale;
    private String facebookPicture;
    private Integer facebookTimeZone;
    private String facebookUppdatedTime;
    private String facebookVerified;
    private String facebookBirthday;
    private String facebookLocation;

    // Loan application
    private Integer loanApplicationAmmount;
    private Integer loanApplicationInstallemnts;
    private Integer loanApplicationShorTermDays;
    private Integer loanApplicationReason;
    private Integer loanApplicationCluster;
    private Date firstDueDate;

    public UserRegisterForm() {
        this.setValidator(new UserRegisterFormValidator());
    }

    public class UserRegisterFormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator docType;
        public StringFieldValidator docNumber;
        public StringFieldValidator birthDate;
        public StringFieldValidator phoneNumber;
        public IntegerFieldValidator loanApplicationAmmount;
        public IntegerFieldValidator loanApplicationInstallemnts;
        public IntegerFieldValidator loanApplicationShorTermDays;

        public UserRegisterFormValidator() {
            addValidator(docType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID));
            addValidator(docNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI));
            addValidator(birthDate = new StringFieldValidator(ValidatorUtil.BIRTHDAY).setRequired(false));
            addValidator(phoneNumber = new StringFieldValidator(ValidatorUtil.CELLPHONE_NUMBER).setRequired(false));
            addValidator(loanApplicationAmmount = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_AMMOUNT_TRADITIONAL));
            addValidator(loanApplicationInstallemnts = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_INSTALLMENTS));
            addValidator(loanApplicationShorTermDays = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_DAYS));
        }

        @Override
        protected void setDynamicValidations() {
            if (UserRegisterForm.this.loanApplicationReason == LoanApplicationReason.ADELANTO) {
                // Short Term Product
                loanApplicationAmmount.update(ValidatorUtil.LOANAPPLICATION_AMMOUNT_SHORT_TERM);
                loanApplicationShorTermDays.setRequired(true);
                loanApplicationInstallemnts.setRequired(false);
            } else {
                // Traditional Product
                loanApplicationAmmount.update(ValidatorUtil.LOANAPPLICATION_AMMOUNT_TRADITIONAL);
                loanApplicationShorTermDays.setRequired(false);
                loanApplicationInstallemnts.setRequired(true);
            }

            birthDate.setRequired(UserRegisterForm.this.docType == IdentityDocumentType.CE);

            if(UserRegisterForm.this.docType == IdentityDocumentType.DNI){
                docNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
            }else if(UserRegisterForm.this.docType == IdentityDocumentType.CE){
                docNumber.update(ValidatorUtil.DOC_NUMBER_CE);
            }

            if (UserRegisterForm.this.facebookId == null) {
                phoneNumber.setRequired(true);
            } else {
                phoneNumber.setRequired(false);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return UserRegisterForm.this;
        }
    }

    public UserFacebook getUserFacebook() {
        if (facebookId != null) {
            UserFacebook facebook = new UserFacebook();
            facebook.setFacebookId(facebookId);
            facebook.setFacebookEmail(facebookEmail);
            facebook.setFacebookName(facebookName);
            facebook.setFacebookFirstName(facebookFirstName);
            facebook.setFacebookLastName(facebookLastName);
            facebook.setFacebookAgeMax(facebookAgeMax);
            facebook.setFacebookAgeMin(facebookAgeMin);
            facebook.setFacebookLink(facebookLink);
            facebook.setFacebookGender(facebookGender);
            facebook.setFacebookLocale(facebookLocale);
            facebook.setFacebookPicture(facebookPicture);
            facebook.setFacebookTimeZone(facebookTimeZone);
            if (facebookUppdatedTime != null) {
                try {
                    facebook.setFacebookUpdatedTime(Util.FACEBOOK_DATE_FORMATTER.parse(facebookUppdatedTime));
                } catch (Exception ex) {
                }
            }
            facebook.setFacebookVerified(facebookVerified);
            facebook.setFacebookBirthday(facebookBirthday);
            facebook.setFacebookLocation(facebookLocation);
            return facebook;
        }
        return null;
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

    public Integer getDocType() {
        return docType;
    }

    public void setDocType(Integer docType) {
        this.docType = docType;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
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
        this.phoneNumber = phoneNumber;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getFacebookEmail() {
        return facebookEmail;
    }

    public void setFacebookEmail(String facebookEmail) {
        this.facebookEmail = facebookEmail;
    }

    public String getFacebookName() {
        return facebookName;
    }

    public void setFacebookName(String facebookName) {
        this.facebookName = facebookName;
    }

    public String getFacebookFirstName() {
        return facebookFirstName;
    }

    public void setFacebookFirstName(String facebookFirstName) {
        this.facebookFirstName = facebookFirstName;
    }

    public String getFacebookLastName() {
        return facebookLastName;
    }

    public void setFacebookLastName(String facebookLastName) {
        this.facebookLastName = facebookLastName;
    }

    public Integer getFacebookAgeMax() {
        return facebookAgeMax;
    }

    public void setFacebookAgeMax(Integer facebookAgeMax) {
        this.facebookAgeMax = facebookAgeMax;
    }

    public Integer getFacebookAgeMin() {
        return facebookAgeMin;
    }

    public void setFacebookAgeMin(Integer facebookAgeMin) {
        this.facebookAgeMin = facebookAgeMin;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getFacebookGender() {
        return facebookGender;
    }

    public void setFacebookGender(String facebookGender) {
        this.facebookGender = facebookGender;
    }

    public String getFacebookLocale() {
        return facebookLocale;
    }

    public void setFacebookLocale(String facebookLocale) {
        this.facebookLocale = facebookLocale;
    }

    public String getFacebookPicture() {
        return facebookPicture;
    }

    public void setFacebookPicture(String facebookPicture) {
        this.facebookPicture = facebookPicture;
    }

    public Integer getFacebookTimeZone() {
        return facebookTimeZone;
    }

    public void setFacebookTimeZone(Integer facebookTimeZone) {
        this.facebookTimeZone = facebookTimeZone;
    }

    public String getFacebookUppdatedTime() {
        return facebookUppdatedTime;
    }

    public void setFacebookUppdatedTime(String facebookUppdatedTime) {
        this.facebookUppdatedTime = facebookUppdatedTime;
    }

    public String getFacebookVerified() {
        return facebookVerified;
    }

    public void setFacebookVerified(String facebookVerified) {
        this.facebookVerified = facebookVerified;
    }

    public String getFacebookBirthday() {
        return facebookBirthday;
    }

    public void setFacebookBirthday(String facebookBirthday) {
        this.facebookBirthday = facebookBirthday;
    }

    public String getFacebookLocation() {
        return facebookLocation;
    }

    public void setFacebookLocation(String facebookLocation) {
        this.facebookLocation = facebookLocation;
    }

    public Integer getLoanApplicationCluster() {
        return loanApplicationCluster;
    }

    public void setLoanApplicationCluster(Integer loanApplicationCluster) {
        this.loanApplicationCluster = loanApplicationCluster;
    }

    public Integer getLoanApplicationReason() {
        return loanApplicationReason;
    }

    public void setLoanApplicationReason(Integer loanApplicationReason) {
        this.loanApplicationReason = loanApplicationReason;
    }

    public Integer getLoanApplicationShorTermDays() {
        return loanApplicationShorTermDays;
    }

    public void setLoanApplicationShorTermDays(Integer loanApplicationShorTermDays) {
        this.loanApplicationShorTermDays = loanApplicationShorTermDays;
    }

    public Integer getLoanApplicationInstallemnts() {
        return loanApplicationInstallemnts;
    }

    public void setLoanApplicationInstallemnts(Integer loanApplicationInstallemnts) {
        this.loanApplicationInstallemnts = loanApplicationInstallemnts;
    }

    public Integer getLoanApplicationAmmount() {
        return loanApplicationAmmount;
    }

    public void setLoanApplicationAmmount(Integer loanApplicationAmmount) {
        this.loanApplicationAmmount = loanApplicationAmmount;
    }

    public Date getFirstDueDate() {
        return firstDueDate;
    }

    public void setFirstDueDate(Date firstDueDate) {
        this.firstDueDate = firstDueDate;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "UserRegisterForm{" +
                "name='" + name + '\'' +
                ", firstSurname='" + firstSurname + '\'' +
                ", lastSurname='" + lastSurname + '\'' +
                ", docType=" + docType +
                ", docNumber='" + docNumber + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", facebookId='" + facebookId + '\'' +
                ", facebookEmail='" + facebookEmail + '\'' +
                ", facebookName='" + facebookName + '\'' +
                ", facebookFirstName='" + facebookFirstName + '\'' +
                ", facebookLastName='" + facebookLastName + '\'' +
                ", facebookAgeMax=" + facebookAgeMax +
                ", facebookAgeMin=" + facebookAgeMin +
                ", facebookLink='" + facebookLink + '\'' +
                ", facebookGender='" + facebookGender + '\'' +
                ", facebookLocale='" + facebookLocale + '\'' +
                ", facebookPicture='" + facebookPicture + '\'' +
                ", facebookTimeZone=" + facebookTimeZone +
                ", facebookUppdatedTime='" + facebookUppdatedTime + '\'' +
                ", facebookVerified='" + facebookVerified + '\'' +
                ", facebookBirthday='" + facebookBirthday + '\'' +
                ", facebookLocation='" + facebookLocation + '\'' +
                ", loanApplicationAmmount=" + loanApplicationAmmount +
                ", loanApplicationInstallemnts=" + loanApplicationInstallemnts +
                ", loanApplicationShorTermDays=" + loanApplicationShorTermDays +
                ", loanApplicationReason=" + loanApplicationReason +
                ", loanApplicationCluster=" + loanApplicationCluster +
                '}';
    }
}
