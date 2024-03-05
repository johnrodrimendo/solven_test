package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

public class Question131Form extends FormGeneric implements Serializable {

    private Integer customProfession;
    private Integer activityTypeId;
    private Integer subActivityTypeId;

    public Question131Form() {
        this.setValidator(new Question131Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator customProfession;

        public Validator() {
            addValidator(customProfession = new IntegerFieldValidator().setRequired(true));
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
            return Question131Form.this;
        }
    }

    public Integer getCustomProfession() {
        return customProfession;
    }

    public void setCustomProfession(Integer customProfession) {
        this.customProfession = customProfession;
    }

    public Integer getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(Integer activityTypeId) {
        this.activityTypeId = activityTypeId;
    }

    public Integer getSubActivityTypeId() {
        return subActivityTypeId;
    }

    public void setSubActivityTypeId(Integer subActivityTypeId) {
        this.subActivityTypeId = subActivityTypeId;
    }
}