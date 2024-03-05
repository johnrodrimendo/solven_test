package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

public class Question34Form extends FormGeneric implements Serializable {

    private Integer serviceType;

    public Question34Form() {
        this.setValidator(new Question34Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator serviceType;

        public Validator() {
            addValidator(serviceType = new IntegerFieldValidator().setRequired(true));
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
            return Question34Form.this;
        }
    }

    public Integer getServiceType() {
        return serviceType;
    }

    public void setServiceType(Integer serviceType) {
        this.serviceType = serviceType;
    }
}