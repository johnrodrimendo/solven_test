package com.affirm.common.model.form;

import com.affirm.common.model.catalog.PhoneType;
import com.affirm.common.util.*;

import java.io.Serializable;

public class Question153Form extends FormGeneric implements Serializable {

    private Boolean acept;

    public Question153Form() {
        this.setValidator(new Question153Form.Validator());
    }

    public Boolean getAcept() {
        return acept;
    }

    public void setAcept(Boolean acept) {
        this.acept = acept;
    }

    public class Validator extends FormValidator implements Serializable {

        public BooleanFieldValidator acept;

        public Validator() {
            addValidator(acept = new BooleanFieldValidator().setRequired(true));
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
            return Question153Form.this;
        }
    }
}