package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question136Form extends FormGeneric implements Serializable {

    private Integer amount;

    public Question136Form() {
        this.setValidator(new Question136Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator amount;

        public Validator() {
            addValidator(amount = new IntegerFieldValidator(ValidatorUtil.SELF_EVALUATION_AMOUNT).setMaxValueErrorMsg("validation.int.maxValueMoney").setMinValueErrorMsg("validation.int.minValueMoney").setFieldName("monto"));
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
            return Question136Form.this;
        }
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}