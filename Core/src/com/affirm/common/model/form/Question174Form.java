package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

public class Question174Form extends FormGeneric implements Serializable {

    private Integer disbursementOption;

    public Question174Form() {
        this.setValidator(new Question174Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator disbursementOption;

        public Validator() {
            addValidator(disbursementOption = new IntegerFieldValidator().setRequired(true).setFieldName("Cuenta destino"));
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
            return Question174Form.this;
        }
    }

    public Integer getDisbursementOption() {
        return disbursementOption;
    }

    public void setDisbursementOption(Integer disbursementOption) {
        this.disbursementOption = disbursementOption;
    }
}