package com.affirm.client.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * Created by stbn on 07/10/16.
 */
public class EntityExtranetRegisterDisbursementForm extends FormGeneric implements Serializable {

    private String disbursementDate;
    private Double amount;
    private Integer installments;
    private Double tea;

    public EntityExtranetRegisterDisbursementForm() {
        this.setValidator(new EntityExtranetRegisterDisbursementForm.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator disbursementDate;
        public DoubleFieldValidator amount;
        public IntegerFieldValidator installments;
        public DoubleFieldValidator tea;

        public Validator() {
            addValidator(disbursementDate = new StringFieldValidator(ValidatorUtil.DISBURSEMENT_DATE).setFieldName("Fecha de desembolso"));
            addValidator(amount = new DoubleFieldValidator().setRequired(false).setRestricted(true).setMaxValue(99999999.99).setMinValue(100.00));
            addValidator(installments = new IntegerFieldValidator().setRequired(false).setRestricted(true).setMaxValue(99).setMinValue(1));
            addValidator(tea = new DoubleFieldValidator().setRequired(false).setRestricted(true).setMaxValue(999.99).setMinValue(0.0));
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
            return EntityExtranetRegisterDisbursementForm.this;
        }
    }

    public String getDisbursementDate() {
        return disbursementDate;
    }

    public void setDisbursementDate(String disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Double getTea() {
        return tea;
    }

    public void setTea(Double tea) {
        this.tea = tea;
    }
}
