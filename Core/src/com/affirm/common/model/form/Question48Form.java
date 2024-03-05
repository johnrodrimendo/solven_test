package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

public class Question48Form extends FormGeneric implements Serializable {

    private Integer activity;

    public Question48Form() {
        this.setValidator(new Question48Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator activity;

        public Validator() {
            addValidator(activity = new IntegerFieldValidator().setRequired(true));
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
            return Question48Form.this;
        }
    }

    public Integer getActivity() {
        return activity;
    }

    public void setActivity(Integer activity) {
        this.activity = activity;
    }
}