package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question2Form extends FormGeneric implements Serializable {

    private String birthday;

    public Question2Form() {
        this.setValidator(new Question2Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator birthday;

        public Validator() {
            addValidator(birthday = new StringFieldValidator(ValidatorUtil.BIRTHDAY));
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
            return Question2Form.this;
        }
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}