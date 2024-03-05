package com.affirm.common.model.form;

import com.affirm.common.util.BooleanFieldValidator;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;

import java.io.Serializable;

public class Question127Form extends FormGeneric implements Serializable {

    private Boolean bankAccountStatement;

    public Question127Form() {
        this.setValidator(new Question127Form.Validator());
    }

    public Boolean getBankAccountStatement() {
        return bankAccountStatement;
    }

    public void setBankAccountStatement(Boolean bankAccountStatement) {
        this.bankAccountStatement = bankAccountStatement;
    }

    public class Validator extends FormValidator implements Serializable {

        public BooleanFieldValidator bankAccountStatement;

        public Validator() {
            addValidator(bankAccountStatement = new BooleanFieldValidator().setRequired(true));
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
            return Question127Form.this;
        }
    }

}
