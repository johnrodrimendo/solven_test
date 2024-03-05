package com.affirm.common.model.form;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.util.*;

import java.io.Serializable;

public class Question30Form extends FormGeneric implements Serializable {

    private Integer income;
    private Boolean variableIncome;

    public Question30Form() {
        this.setValidator(new Question30Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator income;
        public BooleanFieldValidator variableIncome;

        public Validator() {
            addValidator(income = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME));
            addValidator(variableIncome = new BooleanFieldValidator().setRequired(true));
        }

        public void configValidator(Integer countryId) {
            if (CountryParam.COUNTRY_COLOMBIA == countryId) {
                income.update(new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME_COLOMBIA));
            }
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
            return Question30Form.this;
        }
    }

    public Integer getIncome() {
        return income;
    }

    public void setIncome(Integer income) {
        this.income = income;
    }

    public Boolean getVariableIncome() {
        return variableIncome;
    }

    public void setVariableIncome(Boolean variableIncome) {
        this.variableIncome = variableIncome;
    }
}