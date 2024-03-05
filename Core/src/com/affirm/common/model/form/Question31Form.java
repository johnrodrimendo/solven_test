package com.affirm.common.model.form;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question31Form extends FormGeneric implements Serializable {

    private Integer variableIncome;

    public Question31Form() {
        this.setValidator(new Question31Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator variableIncome;

        public Validator() {
            addValidator(variableIncome = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME));
        }

        public void configValidator(Integer countryId) {
            if (CountryParam.COUNTRY_COLOMBIA == countryId) {
                variableIncome.update(new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME_COLOMBIA));
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
            return Question31Form.this;
        }
    }

    public Integer getVariableIncome() {
        return variableIncome;
    }

    public void setVariableIncome(Integer variableIncome) {
        this.variableIncome = variableIncome;
    }

}