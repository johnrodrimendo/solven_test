package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question21Form extends FormGeneric implements Serializable {

    private Integer maritalStatus;

    public Question21Form() {
        this.setValidator(new Question21Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator maritalStatus;

        public Validator() {
            addValidator(maritalStatus = new IntegerFieldValidator(ValidatorUtil.MARITAL_STATUS_ID));
        }

        @Override
        protected void setDynamicValidations() { }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question21Form.this;
        }
    }

    public Integer getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(Integer maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
}