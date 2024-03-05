package com.affirm.common.model.form;

import com.affirm.common.util.DoubleFieldValidator;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;

import java.io.Serializable;
import java.util.Date;

public class RextieForm extends FormGeneric implements Serializable {

    private Double sourceCurrencyAmount;
    private Double targetCurrencyAmount;
    private String sourceCurrency;
    private String targetCurrency;
    private Date quoteExpiringDate;


    public RextieForm() {
        this.setValidator(new RextieForm.Validator());
    }

    public class Validator extends FormValidator implements Serializable {
        public DoubleFieldValidator sourceCurrencyAmount;
        public DoubleFieldValidator targetCurrencyAmount;

        public Validator() {
            addValidator(sourceCurrencyAmount = new DoubleFieldValidator().setRequired(true).setMinValue(0.0).setMaxValue(9 * 1111111.11));
            addValidator(targetCurrencyAmount = new DoubleFieldValidator().setRequired(true).setMinValue(0.0).setMaxValue(9 * 1111111.11));
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
            return RextieForm.this;
        }
    }

    public Double getSourceCurrencyAmount() {
        return sourceCurrencyAmount;
    }

    public void setSourceCurrencyAmount(Double sourceCurrencyAmount) {
        this.sourceCurrencyAmount = sourceCurrencyAmount;
    }

    public Double getTargetCurrencyAmount() {
        return targetCurrencyAmount;
    }

    public void setTargetCurrencyAmount(Double targetCurrencyAmount) {
        this.targetCurrencyAmount = targetCurrencyAmount;
    }

    public String getSourceCurrency() {
        return sourceCurrency;
    }

    public void setSourceCurrency(String sourceCurrency) {
        this.sourceCurrency = sourceCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }
}