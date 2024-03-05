package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;

import java.io.Serializable;

public class Question117Form extends FormGeneric implements Serializable {

    private Integer offerRejectionReason;

    public Question117Form() {
        this.setValidator(new Question117Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {
        public IntegerFieldValidator offerRejectionReason;

        public Validator() {
            addValidator(offerRejectionReason = new IntegerFieldValidator().setRequired(true));
        }

        @Override
        protected void setDynamicValidations() { }

        @Override
        protected Object getSubclass() { return this; }

        @Override
        protected Object getFormClass() { return Question117Form.this;}
    }

    public Integer getOfferRejectionReason() { return offerRejectionReason; }

    public void setOfferRejectionReason(Integer offerRejectionReason) { this.offerRejectionReason = offerRejectionReason; }
}
