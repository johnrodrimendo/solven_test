package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question140Form extends FormGeneric implements Serializable {

    private Integer activityType;

    public Question140Form() {
        this.setValidator(new Question140Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator activityType;

        public Validator() {
            addValidator(activityType = new IntegerFieldValidator(ValidatorUtil.ACTIVITY_TYPE_ID).setRequired(true));
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
            return Question140Form.this;
        }
    }

    public Integer getActivityType() {
        return activityType;
    }

    public void setActivityType(Integer activityType) {
        this.activityType = activityType;
    }
}
