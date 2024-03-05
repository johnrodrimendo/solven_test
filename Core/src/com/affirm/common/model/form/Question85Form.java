package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;

import java.io.Serializable;

public class Question85Form extends FormGeneric implements Serializable {
    private Boolean response;

    public Question85Form() {
        this.setValidator(new Question85Form.Validator());
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
            return Question85Form.this;
        }
    }

    public Boolean getResponse() {
        return response;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }
}