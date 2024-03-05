package com.affirm.common.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

public class Question44Form extends FormGeneric implements Serializable {

    private Integer salesPercentage;
    private String businessType;

    public Question44Form() {
        this.setValidator(new Question44Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator salesPercentage;
        public StringFieldValidator businessType;

        public Validator() {
            addValidator(salesPercentage = new IntegerFieldValidator(ValidatorUtil.OCUPATION_SALES_PERCENTAGE));
            addValidator(businessType = new StringFieldValidator(ValidatorUtil.OCUPATION_CLIENTS_NUMBER));
        }

        @Override
        protected void setDynamicValidations() {
            if (businessType.getValue().equals("minor")) salesPercentage.setRequired(false);
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question44Form.this;
        }
    }

    public Integer getSalesPercentage() {
        return salesPercentage;
    }

    public void setSalesPercentage(Integer salesPercentage) {
        this.salesPercentage = salesPercentage;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
}