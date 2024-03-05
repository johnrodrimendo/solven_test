package com.affirm.common.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

public class Question135Form extends FormGeneric implements Serializable {

    private String loanApplicationSign;
    private boolean terms1;
    private boolean terms2;
    private boolean terms3;

    public Question135Form() {
        this.setValidator(new Question135Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator loanApplicationSign;
        public BooleanFieldValidator terms1;
        public BooleanFieldValidator terms2;
        public BooleanFieldValidator terms3;

        public Validator() {
            addValidator(loanApplicationSign = new StringFieldValidator(ValidatorUtil.LOANAPPLICATION_SIGNATURE));
            addValidator(terms1 = new BooleanFieldValidator(new BooleanFieldValidator().setRequired(false).setRequiredErrorMsg("Debes seleccionar la opción")));
            addValidator(terms2 = new BooleanFieldValidator(new BooleanFieldValidator().setRequired(false).setRequiredErrorMsg("Debes seleccionar la opción")));
            addValidator(terms3 = new BooleanFieldValidator(new BooleanFieldValidator().setRequired(false).setRequiredErrorMsg("Debes seleccionar la opción")));
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
            return Question135Form.this;
        }
    }

    public String getLoanApplicationSign() {
        return loanApplicationSign;
    }

    public void setLoanApplicationSign(String loanApplicationSign) {
        this.loanApplicationSign = loanApplicationSign;
    }

    public boolean isTerms1() {
        return terms1;
    }

    public void setTerms1(boolean terms1) {
        this.terms1 = terms1;
    }

    public boolean isTerms2() {
        return terms2;
    }

    public void setTerms2(boolean terms2) {
        this.terms2 = terms2;
    }

    public boolean isTerms3() {
        return terms3;
    }

    public void setTerms3(boolean terms3) {
        this.terms3 = terms3;
    }
}