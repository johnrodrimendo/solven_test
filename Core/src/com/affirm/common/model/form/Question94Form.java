package com.affirm.common.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

public class Question94Form extends FormGeneric implements Serializable {

    private Integer balance;
    private Double rate;
    private Integer installments;
    private Boolean consolidable;
    private String creditCardNumber;
    private Integer creditCardBrand;
    private String creditCardDepartment;
    private String loanNumber;

    public Question94Form() {
        this.setValidator(new Question94Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator balance;
        public DoubleFieldValidator rate;
        public IntegerFieldValidator installments;
        public BooleanFieldValidator consolidable;
        public StringFieldValidator creditCardNumber;
        public IntegerFieldValidator creditCardBrand;
        public StringFieldValidator creditCardDepartment;
        public StringFieldValidator loanNumber;

        public Validator() {
            addValidator(balance = new IntegerFieldValidator().setRequired(false).setRestricted(true).setMaxValue(999999).setMinValue(100));
            addValidator(rate = new DoubleFieldValidator().setRequired(false).setRestricted(true).setMaxValue(999.99).setMinValue(0.0));
            addValidator(installments = new IntegerFieldValidator().setRequired(false));
            addValidator(consolidable = new BooleanFieldValidator().setRequired(false));
            addValidator(creditCardNumber = new StringFieldValidator(ValidatorUtil.CREDIT_CARD_NUMBER).setRequired(false));
            addValidator(creditCardBrand = new IntegerFieldValidator().setRequired(false));
            addValidator(creditCardDepartment = new StringFieldValidator(ValidatorUtil.DEPARTMENT).setRequired(false));
            addValidator(loanNumber = new StringFieldValidator(ValidatorUtil.LOAN_NUMBER).setRequired(false));
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
            return Question94Form.this;
        }
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Boolean getConsolidable() {
        return consolidable;
    }

    public void setConsolidable(Boolean consolidable) {
        this.consolidable = consolidable;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public Integer getCreditCardBrand() {
        return creditCardBrand;
    }

    public void setCreditCardBrand(Integer creditCardBrand) {
        this.creditCardBrand = creditCardBrand;
    }

    public String getCreditCardDepartment() {
        return creditCardDepartment;
    }

    public void setCreditCardDepartment(String creditCardDepartment) {
        this.creditCardDepartment = creditCardDepartment;
    }

    public String getLoanNumber() {
        return loanNumber;
    }

    public void setLoanNumber(String loanNumber) {
        this.loanNumber = loanNumber;
    }
}