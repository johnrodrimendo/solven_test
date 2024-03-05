package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question98Form extends FormGeneric implements Serializable {

    private Integer income;
    private Integer scheme;
    private Integer retirementMonth;
    private Integer retirementYear;


    public Question98Form() {
        this.setValidator(new Question98Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator income;
        public IntegerFieldValidator scheme;
        public IntegerFieldValidator retirementMonth;
        public IntegerFieldValidator retirementYear;

        public Validator() {
            addValidator(income = new IntegerFieldValidator(ValidatorUtil.PENSION_BENEFIT));
            addValidator(scheme = new IntegerFieldValidator().setRequired(true).setRestricted(true).setMinValue(1));
            addValidator(retirementMonth = new IntegerFieldValidator(ValidatorUtil.BIRTHDATE_MONTH));
            addValidator(retirementYear = new IntegerFieldValidator(ValidatorUtil.RETIREMENT_YEAR));
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
            return Question98Form.this;
        }
    }

    public Integer getIncome() {
        return income;
    }

    public void setIncome(Integer income) {
        this.income = income;
    }

    public Integer getScheme() {
        return scheme;
    }

    public void setScheme(Integer scheme) {
        this.scheme = scheme;
    }

    public Integer getRetirementMonth() {
        return retirementMonth;
    }

    public void setRetirementMonth(Integer retirementMonth) {
        this.retirementMonth = retirementMonth;
    }

    public Integer getRetirementYear() {
        return retirementYear;
    }

    public void setRetirementYear(Integer retirementYear) {
        this.retirementYear = retirementYear;
    }
}