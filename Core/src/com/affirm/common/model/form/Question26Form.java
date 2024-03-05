package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

public class Question26Form extends FormGeneric implements Serializable {

    private Integer activityType;

    public Question26Form() {
        this.setValidator(new Question26Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator activityType;

        public Validator() {
            addValidator(activityType = new IntegerFieldValidator().setRequired(true));
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
            return Question26Form.this;
        }
    }

    public Integer getActivityType() {
        return activityType;
    }

    public void setActivityType(Integer activityType) {
        this.activityType = activityType;
    }
}