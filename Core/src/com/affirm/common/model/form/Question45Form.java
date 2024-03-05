package com.affirm.common.model.form;

import com.affirm.common.util.DoubleFieldValidator;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question45Form extends FormGeneric implements Serializable {

    private Double income;

    public Question45Form() {
        this.setValidator(new Question45Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public DoubleFieldValidator income;

        public Validator() {
            addValidator(income = new DoubleFieldValidator(ValidatorUtil.OCUPATION_DAILY_INCOME));
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
            return Question45Form.this;
        }
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }
}