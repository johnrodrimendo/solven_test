package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;

import java.io.Serializable;

public class Question49Form extends FormGeneric implements Serializable {
    private Boolean response;

    public Question49Form() {
        this.setValidator(new Question49Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {


        public Validator() {

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
            return Question49Form.this;
        }
    }

    public Boolean getResponse() {
        return response;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }
}