package com.affirm.common.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

/**
 * Created by jrodriguez on 31/05/16.
 */
public class UpdateLoanApplicationForm extends FormGeneric implements Serializable {

    private Integer amount;
    private Integer installments;

    public UpdateLoanApplicationForm() {
        this.setValidator(new Validator());
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator amount;
        public IntegerFieldValidator installments;

        public Validator() {
            addValidator(amount = new IntegerFieldValidator(ValidatorUtil.SELF_EVALUATION_AMOUNT));
            addValidator(installments = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_INSTALLMENTS));
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
            return UpdateLoanApplicationForm.this;
        }

        public void redefineMaxMin(Integer maxAmount, Integer minAmount, Integer maxInstallments, Integer minInstallments){
            amount.setMaxValue(maxAmount).setMinValue(minAmount);
            installments.setMaxValue(maxInstallments).setMinValue(minInstallments);
        }
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

}
