package com.affirm.common.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

public class Question37Form extends FormGeneric implements Serializable {

    private Integer monthlyIncome;
    private String hopeGrow;

    public Question37Form() {
        this.setValidator(new Question37Form.Validator());
    }

    public String getHopeGrow() {
        return hopeGrow;
    }

    public void setHopeGrow(String hopeGrow) {
        this.hopeGrow = hopeGrow;
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator monthlyIncome;
        public BooleanFieldValidator hopeGrow;

        public Validator() {
            addValidator(monthlyIncome = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME));
            addValidator(hopeGrow = new BooleanFieldValidator().setRequired(true));
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
            return Question37Form.this;
        }
    }

    public Integer getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(Integer monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }
}