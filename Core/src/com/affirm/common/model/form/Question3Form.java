package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

public class Question3Form extends FormGeneric implements Serializable {

    private Integer loanReasonId;

    public Question3Form() {
        this.setValidator(new Question3Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator loanReasonId;

        public Validator() {
            addValidator(loanReasonId = new IntegerFieldValidator().setRequired(true));
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
            return Question3Form.this;
        }
    }

    public Integer getLoanReasonId() {
        return loanReasonId;
    }

    public void setLoanReasonId(Integer loanReasonId) {
        this.loanReasonId = loanReasonId;
    }
}