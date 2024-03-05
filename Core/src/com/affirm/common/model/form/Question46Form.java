package com.affirm.common.model.form;

import com.affirm.common.util.DoubleFieldValidator;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question46Form extends FormGeneric implements Serializable {

    private Double income;

    public Question46Form() {
        this.setValidator(new Question46Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public DoubleFieldValidator income;

        public Validator() {
            addValidator(income = new DoubleFieldValidator(ValidatorUtil.OCUPATION_U12M_INCOME));
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
            return Question46Form.this;
        }
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }
}