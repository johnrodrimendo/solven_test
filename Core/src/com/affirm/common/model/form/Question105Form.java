package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;

public class Question105Form extends FormGeneric {
    private String email;

    public Question105Form() {
        this.setValidator(new Question105Form.Validator());
    }

    public class Validator extends FormValidator {

        public StringFieldValidator email;

        public Validator() {
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL).setRequired(true));
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
            return Question105Form.this;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}