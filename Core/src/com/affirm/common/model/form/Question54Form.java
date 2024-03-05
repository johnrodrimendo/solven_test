package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question54Form extends FormGeneric implements Serializable {

    private String pin;

    public Question54Form() {
        this.setValidator(new Question54Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator pin;

        public Validator() {
            addValidator(pin = new StringFieldValidator(ValidatorUtil.SMS_TOKEN));
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
            return Question54Form.this;
        }
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}