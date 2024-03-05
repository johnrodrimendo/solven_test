package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question109Form extends FormGeneric implements Serializable {
    private Integer income;

    public Question109Form() {
        this.setValidator(new Question109Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator income;

        public Validator() { addValidator(income = new IntegerFieldValidator(ValidatorUtil.OTHER_INCOME)); }

        @Override
        protected void setDynamicValidations() { }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question109Form.this;
        }
    }

    public Integer getIncome() {
        return income;
    }

    public void setIncome(Integer income) {
        this.income = income;
    }
}
