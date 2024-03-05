package com.affirm.common.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

public class Question137Form extends FormGeneric implements Serializable {

    private String name;
    private String surname;
    private String documentNumber;
    private String dateOfBirth;
    private String reason;
    private String email;
    private String landlineAreaCode;
    private String landline;
    private String mobile;
    private String mobileAreaCode;
    private Integer amount;
    private Boolean acceptAgreement;
    private Integer maritalStatus;
    private Integer dayOfBirth;
    private Integer monthOfBirth;
    private Integer yearOfBirth;
    private Integer afipActivity;
    private Boolean factaTrue;
    private Boolean factaFalse;
    private Boolean pepTrue;
    private Boolean pepFalse;
    private String  pepReason;

    public Question137Form() {
        this.setValidator(new Question137Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        private StringFieldValidator email;
        private StringFieldValidator landline;
        private StringFieldValidator mobile;
        private StringFieldValidator landlineAreaCode;
        private StringFieldValidator mobileAreaCode;
        private IntegerFieldValidator afipActivity;
        private IntegerFieldValidator maritalStatus;
        private BooleanFieldValidator factaTrue;
        private BooleanFieldValidator factaFalse;
        private BooleanFieldValidator pepTrue;
        private BooleanFieldValidator pepFalse;
        private StringFieldValidator  pepReason;


        public Validator() {
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL).setFieldName("Email"));
            addValidator(landline = new StringFieldValidator().setRequired(true).setMinCharacters(6).setMaxCharacters(8).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setFieldName("Teléfono"));
            addValidator(mobile = new StringFieldValidator().setRequired(true).setMinCharacters(6).setMaxCharacters(8).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setFieldName("Celular"));
            addValidator(mobileAreaCode = new StringFieldValidator().setRequired(true).setMinCharacters(2).setMaxCharacters(4).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setFieldName("Cod.Area"));
            addValidator(landlineAreaCode = new StringFieldValidator().setRequired(true).setMinCharacters(2).setMaxCharacters(4).setValidRegex(StringFieldValidator.REGEXP_INTEGER_NUMBERS).setFieldName("Cod.Area"));
            addValidator(afipActivity = new IntegerFieldValidator().setRequired(true).setFieldName("Actividades"));
            addValidator(maritalStatus = new IntegerFieldValidator().setRequired(true).setFieldName("Estado Civil"));
            addValidator( factaTrue = new BooleanFieldValidator().setFieldName("Si").setRequired(true));
            addValidator( factaFalse = new BooleanFieldValidator().setFieldName("No").setRequired(true));
            addValidator( pepFalse = new BooleanFieldValidator().setFieldName("Si").setRequired(true));
            addValidator( pepTrue = new BooleanFieldValidator().setFieldName("No").setRequired(true));
            addValidator( pepReason = new StringFieldValidator().setFieldName("Motivo PEP").setMinCharacters(12).setRequired(true));

        }
        @Override
        protected void setDynamicValidations() {
            mobile.setMaxCharacters(10 - Question137Form.this.mobileAreaCode.length());
            mobile.setMinCharacters(10 - Question137Form.this.mobileAreaCode.length());

            landline.setMaxCharacters(10 - Question137Form.this.landlineAreaCode.length());
            landline.setMinCharacters(10 - Question137Form.this.landlineAreaCode.length());

            if(pepTrue.getValue() != null){
                pepFalse.setRequired(false);
            }
            if(pepFalse.getValue() != null){
                pepTrue.setRequired(false);
                pepReason.setRequired(false);
            }
            if(factaTrue.getValue() != null){
                factaFalse.setRequired(false);
            }
            if(factaFalse.getValue() != null){
                factaTrue.setRequired(false);
        }

        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question137Form.this;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getDayOfBirth() {
        return dayOfBirth;
    }

    public void setDayOfBirth(Integer dayOfBirth) {
        this.dayOfBirth = dayOfBirth;
    }

    public Integer getMonthOfBirth() {
        return monthOfBirth;
    }

    public Boolean getAcceptAgreement() {
        return acceptAgreement;
    }

    public void setAcceptAgreement(Boolean acceptAgreement) {
        this.acceptAgreement = acceptAgreement;
    }

    public void setMonthOfBirth(Integer monthOfBirth) {
        this.monthOfBirth = monthOfBirth;
    }

    public Integer getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(Integer yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public String getLandlineAreaCode() {
        return landlineAreaCode;
    }

    public void setLandlineAreaCode(String landlineAreaCode) {
        this.landlineAreaCode = landlineAreaCode;
    }

    public String getMobileAreaCode() {
        return mobileAreaCode;
    }

    public void setMobileAreaCode(String mobileAreaCode) {
        this.mobileAreaCode = mobileAreaCode;
    }


    public Integer getAfipActivity() {
        return afipActivity;
    }

    public void setAfipActivity(Integer afipActivity) {
        this.afipActivity = afipActivity;
    }
    public Integer getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(Integer maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Boolean getFactaTrue() {
        return factaTrue;
    }

    public void setFactaTrue(Boolean factaTrue) {
        this.factaTrue = factaTrue;
    }

    public Boolean getFactaFalse() {
        return factaFalse;
    }

    public void setFactaFalse(Boolean factaFalse) {
        this.factaFalse = factaFalse;
    }

    public Boolean getPepTrue() {
        return pepTrue;
    }

    public void setPepTrue(Boolean pepTrue) {
        this.pepTrue = pepTrue;
    }

    public Boolean getPepFalse() {
        return pepFalse;
    }

    public void setPepFalse(Boolean pepFalse) {
        this.pepFalse = pepFalse;
    }

    public String getPepReason() {
        return pepReason;
    }

    public void setPepReason(String pepReason) {
        this.pepReason = pepReason;
    }
}