package com.affirm.entityExt.models.form;

import com.affirm.common.util.*;
import com.affirm.marketingCampaign.model.MarketingCampaign;

import java.io.Serializable;

public class RejectLoanApplicationForm extends FormGeneric implements Serializable {

    private String applicationRejectionComment;
    private Integer applicationRejectionReasonId;


    public RejectLoanApplicationForm() {
        this.setValidator(new Validator());
    }

    public String getApplicationRejectionComment() {
        return applicationRejectionComment;
    }

    public void setApplicationRejectionComment(String applicationRejectionComment) {
        this.applicationRejectionComment = applicationRejectionComment;
    }

    public Integer getApplicationRejectionReasonId() {
        return applicationRejectionReasonId;
    }

    public void setApplicationRejectionReasonId(Integer applicationRejectionReasonId) {
        this.applicationRejectionReasonId = applicationRejectionReasonId;
    }

    public class Validator extends FormValidator implements Serializable{

        public StringFieldValidator applicationRejectionComment;
        public IntegerFieldValidator applicationRejectionReasonId;


        public Validator() {
            addValidator(applicationRejectionComment = new StringFieldValidator().setValidRegex(null).setRequired(true));
            addValidator(applicationRejectionReasonId = new IntegerFieldValidator().setRequired(true));
        }

        @Override
        protected void setDynamicValidations() { }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return RejectLoanApplicationForm.this;
        }

    }
}
