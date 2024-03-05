package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

public class Question29Form extends FormGeneric implements Serializable {

    private Integer ocupation;

    public Question29Form() {
        this.setValidator(new Question29Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator ocupation;

        public Validator() {
            addValidator(ocupation = new IntegerFieldValidator().setRequired(true));
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
            return Question29Form.this;
        }
    }

    public Integer getOcupation() {
        return ocupation;
    }

    public void setOcupation(Integer ocupation) {
        this.ocupation = ocupation;
    }
}