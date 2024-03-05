package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

public class Question32Form extends FormGeneric implements Serializable {

    private Integer subActivity;

    public Question32Form() {
        this.setValidator(new Question32Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator subActivity;

        public Validator() {
            addValidator(subActivity = new IntegerFieldValidator().setRequired(true));
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
            return Question32Form.this;
        }
    }

    public Integer getSubActivity() {
        return subActivity;
    }

    public void setSubActivity(Integer subActivity) {
        this.subActivity = subActivity;
    }
}