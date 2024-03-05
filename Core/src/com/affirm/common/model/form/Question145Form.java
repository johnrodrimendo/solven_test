package com.affirm.common.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

public class Question145Form extends FormGeneric implements Serializable {

    private Boolean serviceOrder;
    private String orderNumber;
    private Integer amount;
    private String activationDate;
    private Integer participationPercentage;
    private String incomeRange;
    private String incomeRangeCompany;

    public Question145Form() {
        this.setValidator(new Question145Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        BooleanFieldValidator serviceOrder;
        StringFieldValidator orderNumber;
        IntegerFieldValidator amount;
        StringFieldValidator activationDate;
        IntegerFieldValidator participationPercentage;
        StringFieldValidator incomeRange;
        StringFieldValidator incomeRangeCompany;

        public Validator() {
            addValidator(serviceOrder = new BooleanFieldValidator().setRequired(true));
            addValidator(orderNumber = new StringFieldValidator().setFieldName("Numero de orden").setRequired(true).setMaxCharacters(10).setValidRegex(StringFieldValidator.REGEXP_ALPHANUMERIC).setRestricted(true));
            addValidator(amount = new IntegerFieldValidator().setRequired(true).setRestricted(true).setMaxValue(9999999).setMinValue(100));
            addValidator(activationDate = new StringFieldValidator().setRequired(true).setValidRegex(StringFieldValidator.REGEXP_DDMMYYYY_SLASH_DATE));
            addValidator(participationPercentage = new IntegerFieldValidator().setRequired(true).setRestricted(true).setMaxValue(100).setMinValue(0));
            addValidator(incomeRange = new StringFieldValidator().setRequired(true));
            addValidator(incomeRangeCompany = new StringFieldValidator().setRequired(true));
        }

        @Override
        protected void setDynamicValidations() {
            if(Question145Form.this.serviceOrder){
                orderNumber.setRequired(true);
                amount.setRequired(true);
                activationDate.setRequired(true);
                participationPercentage.setRequired(true);
                incomeRange.setRequired(true);
                incomeRangeCompany.setRequired(true);
            }else{
                orderNumber.setRequired(false);
                amount.setRequired(false);
                activationDate.setRequired(false);
                participationPercentage.setRequired(false);
                incomeRange.setRequired(false);
                incomeRangeCompany.setRequired(false);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question145Form.this;
        }
    }

    public Boolean getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(Boolean serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(String activationDate) {
        this.activationDate = activationDate;
    }

    public Integer getParticipationPercentage() {
        return participationPercentage;
    }

    public void setParticipationPercentage(Integer participationPercentage) {
        this.participationPercentage = participationPercentage;
    }

    public String getIncomeRange() {
        return incomeRange;
    }

    public void setIncomeRange(String incomeRange) {
        this.incomeRange = incomeRange;
    }

    public String getIncomeRangeCompany() {
        return incomeRangeCompany;
    }

    public void setIncomeRangeCompany(String incomeRangeCompany) {
        this.incomeRangeCompany = incomeRangeCompany;
    }
}
