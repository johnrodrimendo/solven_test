package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

public class Question141Form extends FormGeneric {

    private Integer bankId;

    public Question141Form() {
        this.setValidator(new Question141Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator bankId;

        public Validator() {
            addValidator(bankId = new IntegerFieldValidator(ValidatorUtil.BANK_ID).setRequired(true));
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
            return Question141Form.this;
        }
    }

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }
}
