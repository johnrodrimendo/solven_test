package com.affirm.common.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

public class Question181Form extends FormGeneric implements Serializable {


    private Integer offerId;
    private Integer firstDueDay;

    public Question181Form() {
        this.setValidator(new Question181Form.Validator());
    }

    public Integer getOfferId() {
        return offerId;
    }

    public void setOfferId(Integer offerId) {
        this.offerId = offerId;
    }

    public Integer getFirstDueDay() {
        return firstDueDay;
    }

    public void setFirstDueDay(Integer firstDueDay) {
        this.firstDueDay = firstDueDay;
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator offerId;
        public IntegerFieldValidator firstDueDay;

        public Validator() {
            addValidator(offerId = new IntegerFieldValidator().setRequired(true));
            addValidator(firstDueDay = new IntegerFieldValidator().setRequired(true).setRequiredErrorMsg("Debes elegir un día"));
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
            return Question181Form.this;
        }

        public void configValidator() {
        }
    }
}
