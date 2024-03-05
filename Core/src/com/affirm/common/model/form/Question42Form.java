package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question42Form extends FormGeneric implements Serializable {

    private Integer salesPercentage;

    public Question42Form() {
        this.setValidator(new Question42Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator salesPercentage;

        public Validator() {
            addValidator(salesPercentage = new IntegerFieldValidator(ValidatorUtil.OCUPATION_SALES_PERCENTAGE));
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
            return Question42Form.this;
        }
    }

    public Integer getSalesPercentage() {
        return salesPercentage;
    }

    public void
    setSalesPercentage(Integer salesPercentage) {
        this.salesPercentage = salesPercentage;
    }
}