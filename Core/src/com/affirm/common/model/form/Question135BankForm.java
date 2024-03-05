package com.affirm.common.model.form;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.util.*;

import java.io.Serializable;

public class Question135BankForm extends FormGeneric implements Serializable {

    private Integer bankId;
    private Character bankAccountType;
    private String bankAccountNumber;
    private String bankAccountDepartment;
    private String bankAccountCci;

    public Question135BankForm() {
        this.setValidator(new Question135BankForm.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator bankId;
        public CharFieldValidator bankAccountType;
        public StringFieldValidator bankAccountNumber;
        public StringFieldValidator bankAccountDepartment;
        public StringFieldValidator bankAccountCci;

        public Validator() {
            addValidator(bankId = new IntegerFieldValidator(ValidatorUtil.BANK_ID));
            addValidator(bankAccountType = new CharFieldValidator(ValidatorUtil.BANK_ACCOUNT_TYPE));
            addValidator(bankAccountNumber = new StringFieldValidator(ValidatorUtil.BANK_ACCOUNT_NUMBER));
            addValidator(bankAccountDepartment = new StringFieldValidator(ValidatorUtil.DEPARTMENT));
            addValidator(bankAccountCci = new StringFieldValidator(ValidatorUtil.BANK_CCI_NUMBER).setRequired(false));
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
            return Question135BankForm.this;
        }

        public void setCountryId(Integer countryId) {
            if (countryId.equals(CountryParam.COUNTRY_ARGENTINA)) {
                bankAccountDepartment.setRequired(false);
                bankAccountNumber.setRequired(false);
                bankAccountCci = new StringFieldValidator(ValidatorUtil.BANK_CBU_NUMBER);
            } else {
                bankAccountDepartment.setRequired(true);
                bankAccountNumber.setRequired(true);
                bankAccountCci = new StringFieldValidator(ValidatorUtil.BANK_CCI_NUMBER).setRequired(false);
            }
        }
    }

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }

    public Character getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(Character bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankAccountDepartment() {
        return bankAccountDepartment;
    }

    public void setBankAccountDepartment(String bankAccountDepartment) {
        this.bankAccountDepartment = bankAccountDepartment;
    }

    public String getBankAccountCci() {
        return bankAccountCci;
    }

    public void setBankAccountCci(String bankAccountCci) {
        this.bankAccountCci = bankAccountCci;
    }
}