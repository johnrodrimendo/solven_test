package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

public class Question23Form extends FormGeneric implements Serializable {

    private Integer housingType;

    public Question23Form() {
        this.setValidator(new Question23Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator housingType;

        public Validator() {
            addValidator(housingType = new IntegerFieldValidator().setRequired(true));
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
            return Question23Form.this;
        }
    }

    public Integer getHousingType() {
        return housingType;
    }

    public void setHousingType(Integer housingType) {
        this.housingType = housingType;
    }
}
