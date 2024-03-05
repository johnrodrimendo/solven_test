package com.affirm.bancodelsol.model;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.util.DoubleFieldValidator;
import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;
import com.affirm.system.configuration.Configuration;

import java.io.Serializable;

public class CommissionClusterForm extends FormGeneric implements Serializable {

    private Integer minInstallments;
    private Integer maxInstallments;
    private Double minAmount;
    private Double maxAmount;
    private Double effectiveAnnualRate;

    public CommissionClusterForm() {
        this.setValidator(new CommissionClusterForm.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator minInstallments;
        public IntegerFieldValidator maxInstallments;
        public DoubleFieldValidator minAmount;
        public DoubleFieldValidator maxAmount;
        public DoubleFieldValidator effectiveAnnualRate;

        public Validator() {
            addValidator(minInstallments = new IntegerFieldValidator().setRequired(true).setRestricted(true).setMinValue(1).setMaxValue(Configuration.MAX_INSTALLMENTS));
            addValidator(maxInstallments = new IntegerFieldValidator().setRequired(true).setRestricted(true).setMinValue(1).setMaxValue(Configuration.MAX_INSTALLMENTS));
            addValidator(minAmount = new DoubleFieldValidator().setRequired(true).setRestricted(true).setMinValue(0.0).setMaxValue(1000.0));
            addValidator(maxAmount = new DoubleFieldValidator().setRequired(true).setRestricted(true).setMinValue(0.0).setMaxValue(1000.0));
            addValidator(effectiveAnnualRate = new DoubleFieldValidator().setRequired(true).setRestricted(true).setMinValue(0.0).setMaxValue(500.0));
        }

        public void configValidator(Integer countryId) {
            if (countryId == CountryParam.COUNTRY_PERU) {
                minAmount.setMinValue(100.0).setMaxValue(100 * 1000.0);
                maxAmount.setMinValue(100.0).setMaxValue(100 * 1000.0);
            } else if (countryId == CountryParam.COUNTRY_ARGENTINA) {
                minAmount.setMinValue(1000.0).setMaxValue(9 * 1111111.0);
                maxAmount.setMinValue(1000.0).setMaxValue(9 * 1111111.0);
            }
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
            return CommissionClusterForm.this;
        }
    }

    public Integer getMinInstallments() {
        return minInstallments;
    }

    public void setMinInstallments(Integer minInstallments) {
        this.minInstallments = minInstallments;
    }

    public Integer getMaxInstallments() {
        return maxInstallments;
    }

    public void setMaxInstallments(Integer maxInstallments) {
        this.maxInstallments = maxInstallments;
    }

    public Double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Double minAmount) {
        this.minAmount = minAmount;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Double maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Double getEffectiveAnnualRate() {
        return effectiveAnnualRate;
    }

    public void setEffectiveAnnualRate(Double effectiveAnnualRate) {
        this.effectiveAnnualRate = effectiveAnnualRate;
    }
}
