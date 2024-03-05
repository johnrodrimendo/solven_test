package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question39Form extends FormGeneric implements Serializable {

    private Integer shareholding;

    public Question39Form() {
        this.setValidator(new Question39Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator shareholding;

        public Validator() {
            addValidator(shareholding = new IntegerFieldValidator(ValidatorUtil.SHAREHOLDING_INT));
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
            return Question39Form.this;
        }
    }

    public Integer getShareholding() {
        return shareholding;
    }

    public void setShareholding(Integer shareholding) {
        this.shareholding = shareholding;
    }
}