package com.affirm.common.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

public class Question35Form extends FormGeneric implements Serializable {

    private String ruc;
    private Boolean moreThan65;

    public Question35Form() {
        this.setValidator(new Question35Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator ruc;
        public BooleanFieldValidator moreThan65;

        public Validator() {
            addValidator(ruc = new StringFieldValidator(ValidatorUtil.RUC));
            addValidator(moreThan65 = new BooleanFieldValidator().setRequired(true));
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
            return Question35Form.this;
        }
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public Boolean getMoreThan65() {
        return moreThan65;
    }

    public void setMoreThan65(Boolean moreThan65) {
        this.moreThan65 = moreThan65;
    }
}