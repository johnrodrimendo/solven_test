package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;

import java.io.Serializable;

public class Question149Form extends FormGeneric implements Serializable {

    private String kind;

    public Question149Form() {
        this.setValidator(new Question149Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator kind;

        public Validator() {
            addValidator(kind = new StringFieldValidator().setRequired(true));
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
            return Question149Form.this;
        }
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}
