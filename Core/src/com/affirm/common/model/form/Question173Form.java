package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

public class Question173Form extends FormGeneric implements Serializable {

    private Integer offerId;

    public Question173Form() {
        this.setValidator(new Question173Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator offerId;

        public Validator() {
            addValidator(offerId = new IntegerFieldValidator().setRequired(true));
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
            return Question173Form.this;
        }
    }

    public Integer getOfferId() {
        return offerId;
    }

    public void setOfferId(Integer offerId) {
        this.offerId = offerId;
    }
}