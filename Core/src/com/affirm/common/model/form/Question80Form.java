package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

public class Question80Form extends FormGeneric implements Serializable {

    private Integer dependents;

    public Question80Form() {
        this.setValidator(new Question80Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator dependents;

        public Validator() {
            addValidator(dependents = new IntegerFieldValidator().setRequired(true).setMaxValue(20).setMinValue(0));
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
            return Question80Form.this;
        }
    }

    public Integer getDependents() {
        return dependents;
    }

    public void setDependents(Integer dependents) {
        this.dependents = dependents;
    }
}