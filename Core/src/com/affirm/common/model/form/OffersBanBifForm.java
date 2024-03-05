package com.affirm.common.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

public class OffersBanBifForm extends FormGeneric implements Serializable {

    private Character loanApplicationType;
    private String abTesting;
    private Integer offerId;

    public OffersBanBifForm() {
        this.setValidator(new OffersBanBifForm.Validator());
    }

    public Character getLoanApplicationType() {
        return loanApplicationType;
    }

    public void setLoanApplicationType(Character loanApplicationType) {
        this.loanApplicationType = loanApplicationType;
    }

    public String getAbTesting() {
        return abTesting;
    }

    public void setAbTesting(String abTesting) {
        this.abTesting = abTesting;
    }

    public Integer getOfferId() {
        return offerId;
    }

    public void setOfferId(Integer offerId) {
        this.offerId = offerId;
    }

    public class Validator extends FormValidator implements Serializable {

        public CharFieldValidator loanApplicationType;
        public IntegerFieldValidator offerId;

        public Validator() {
            addValidator(loanApplicationType = new CharFieldValidator(ValidatorUtil.BANBIF_LOAN_APPLICATION_TYPE).setFieldName("Tipo de solicitud"));
            addValidator(offerId = new IntegerFieldValidator().setRequired(true));
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
            return OffersBanBifForm.this;
        }

        public void configValidator() {
        }
    }
}
