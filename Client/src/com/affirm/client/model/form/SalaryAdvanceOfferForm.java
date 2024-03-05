/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class SalaryAdvanceOfferForm extends FormGeneric {

    private Integer amount;
    private Double commission;

    public SalaryAdvanceOfferForm() {
        this.setValidator(new SalaryAdvanceOfferFormValidator());
    }

    public class SalaryAdvanceOfferFormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator amount;
        public DoubleFieldValidator commission;

        public SalaryAdvanceOfferFormValidator() {
            addValidator(amount = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_AMMOUNT_SALARY_ADVANCE));
            addValidator(commission = new DoubleFieldValidator(ValidatorUtil.SALARYADVANCE_COMMISSION));
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
            return SalaryAdvanceOfferForm.this;
        }
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }
}
