/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationBlock7Section8Form extends FormGeneric {

    private String rentierTime;
    private Integer rentierGrossIncome;
    private Integer rentierNetIncome;

    public LoanApplicationBlock7Section8Form() {
        this.setValidator(new LoanApplicationBlock7Section8FormValidator());
    }

    public class LoanApplicationBlock7Section8FormValidator extends FormValidator implements Serializable {

        public StringFieldValidator rentierTime;
        public IntegerFieldValidator rentierGrossIncome;
        public IntegerFieldValidator rentierNetIncome;

        public LoanApplicationBlock7Section8FormValidator() {
            addValidator(rentierTime = new StringFieldValidator(ValidatorUtil.ACTIVITY_TIME).setRequiredErrorMsg("validation.activityJob.activityTime"));
            addValidator(rentierGrossIncome = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME).setRequiredErrorMsg("validation.activityJob.grossIncome"));
            addValidator(rentierNetIncome = new IntegerFieldValidator(ValidatorUtil.NET_INCOME).setRequiredErrorMsg("validation.activityJob.netIncome"));
        }

        @Override
        protected void setDynamicValidations() {
            rentierNetIncome.setMaxValue(LoanApplicationBlock7Section8Form.this.rentierGrossIncome);
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return LoanApplicationBlock7Section8Form.this;
        }
    }

    public String getRentierTime() {
        return rentierTime;
    }

    public void setRentierTime(String rentierTime) {
        this.rentierTime = rentierTime;
    }

    public Integer getRentierGrossIncome() {
        return rentierGrossIncome;
    }

    public void setRentierGrossIncome(Integer rentierGrossIncome) {
        this.rentierGrossIncome = rentierGrossIncome;
    }

    public Integer getRentierNetIncome() {
        return rentierNetIncome;
    }

    public void setRentierNetIncome(Integer rentierNetIncome) {
        this.rentierNetIncome = rentierNetIncome;
    }
}
