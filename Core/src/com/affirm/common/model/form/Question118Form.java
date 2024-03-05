package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

public class Question118Form extends FormGeneric implements Serializable {

    private Integer months;

    public Question118Form() {
        this.setValidator(new Question118Form.Validator());
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public class Validator extends FormValidator implements Serializable {
        public IntegerFieldValidator months;

        public Validator() {
            addValidator(months = new IntegerFieldValidator().setRequired(true).setMinValue(1).setMaxValue(999));
        }

        @Override
        protected void setDynamicValidations() { }

        @Override
        protected Object getSubclass() { return this; }

        @Override
        protected Object getFormClass() { return Question118Form.this;}
    }
}
