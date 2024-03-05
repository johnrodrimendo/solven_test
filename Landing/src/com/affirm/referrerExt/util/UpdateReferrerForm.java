package com.affirm.referrerExt.util;

import com.affirm.common.model.catalog.Bank;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.*;

import java.io.Serializable;

public class UpdateReferrerForm extends FormGeneric implements Serializable {

    private Integer documentType;
    private String documentNumber;
    private String name;
    private String firstSurname;
    private String email;
    private String phoneNumber;
    private Integer bank;
    private String bankAccountNumber;
    private String cci;

    public UpdateReferrerForm() {
        this.setValidator(new UpdateReferrerForm.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator name;
        public StringFieldValidator firstSurname;
        public StringFieldValidator email;
        public StringFieldValidator phoneNumber;
        public IntegerFieldValidator bank;
        public StringFieldValidator bankAccountNumber;
        public StringFieldValidator cci;

        public Validator() {
            addValidator(name = new StringFieldValidator(ValidatorUtil.NAME).setRequired(false));
            addValidator(firstSurname = new StringFieldValidator(ValidatorUtil.FIRST_SURNAME).setRequired(false));
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL));
            addValidator(phoneNumber = new StringFieldValidator(ValidatorUtil.CELLPHONE_NUMBER));
            addValidator(bank = new IntegerFieldValidator().setRequired(false));
            addValidator(bankAccountNumber = new StringFieldValidator(ValidatorUtil.BANK_ACCOUNT_NUMBER).setRequired(false));
            addValidator(cci = new StringFieldValidator(ValidatorUtil.BANK_CCI_NUMBER).setRequired(false));
        }

        @Override
        protected void setDynamicValidations() {
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return UpdateReferrerForm.this;
        }
    }

    public Integer getDocumentType() {
        return documentType;
    }

    public void setDocumentType(Integer documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getBank() {
        return bank;
    }

    public void setBank(Integer bank) {
        this.bank = bank;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getCci() {
        return cci;
    }

    public void setCci(String cci) {
        this.cci = cci;
    }
}
