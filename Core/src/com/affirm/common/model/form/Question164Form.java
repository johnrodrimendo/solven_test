package com.affirm.common.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

public class Question164Form extends FormGeneric implements Serializable {

    private String phoneNumber;
    private String email;

    public Question164Form() {
        this.setValidator(new Question164Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator phoneNumber;
        public StringFieldValidator email;

        public Validator() {
            addValidator(phoneNumber = new StringFieldValidator(ValidatorUtil.CELLPHONE_NUMBER).setFieldName("Nro. Celular"));
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL).setFieldName("Email"));
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
            return Question164Form.this;
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}