package com.affirm.common.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

public class Question182Form extends FormGeneric implements Serializable {


    private Integer bankId;
    private String bankAccountCci;
    private Boolean acceptedTyC;
    private Character addressToSend;
    private Boolean isDeposit;

    public Question182Form() {
        this.setValidator(new Question182Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator bankId;
        public StringFieldValidator bankAccountCci;
        public BooleanFieldValidator acceptedTyC;
        public CharFieldValidator addressToSend;

        public Validator() {
            addValidator(bankId = new IntegerFieldValidator(ValidatorUtil.BANK_ID).setRequired(false));
            addValidator(bankAccountCci = new StringFieldValidator(ValidatorUtil.BANK_CCI_NUMBER).setRequired(false));
            addValidator(acceptedTyC = new BooleanFieldValidator(ValidatorUtil.BANBIF_TYCS).setRequired(true).setFieldName("Términos y condiciones").setRequiredErrorMsg("Necesitamos que aceptes las condiciones."));
            addValidator(addressToSend = new CharFieldValidator(ValidatorUtil.ADDRESS_TO_SEND).setFieldName("Dirección de entrega"));
        }

        @Override
        protected void setDynamicValidations() {
            if (Question182Form.this.isDeposit) {
                bankId.setRequired(true);
                bankAccountCci.setRequired(true);
            } else {
                bankId.setRequired(false);
                bankAccountCci.setRequired(false);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question182Form.this;
        }

        public void configValidator() {
        }
    }

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }

    public String getBankAccountCci() {
        return bankAccountCci;
    }

    public void setBankAccountCci(String bankAccountCci) {
        this.bankAccountCci = bankAccountCci;
    }

    public Boolean getAcceptedTyC() {
        return acceptedTyC;
    }

    public void setAcceptedTyC(Boolean acceptedTyC) {
        this.acceptedTyC = acceptedTyC;
    }

    public Character getAddressToSend() {
        return addressToSend;
    }

    public void setAddressToSend(Character addressToSend) {
        this.addressToSend = addressToSend;
    }

    public Boolean getDeposit() {
        return isDeposit;
    }

    public void setDeposit(Boolean deposit) {
        isDeposit = deposit;
    }
}
