/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationBlock7Section4Form extends FormGeneric {

    private String dependentTime;
    private Integer dependentGrossIncome;
    private Integer dependentNetIncome;

    public LoanApplicationBlock7Section4Form() {
        this.setValidator(new LoanApplicationBlock7Section4FormValidator());
    }

    public class LoanApplicationBlock7Section4FormValidator extends FormValidator implements Serializable {

        public StringFieldValidator dependentTime;
        public IntegerFieldValidator dependentGrossIncome;
        public IntegerFieldValidator dependentNetIncome;

        public LoanApplicationBlock7Section4FormValidator() {
            addValidator(dependentTime = new StringFieldValidator(ValidatorUtil.ACTIVITY_TIME).setRequiredErrorMsg("validation.activityJob.activityTime"));
            addValidator(dependentGrossIncome = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME).setRequiredErrorMsg("validation.activityJob.grossIncome"));
            addValidator(dependentNetIncome = new IntegerFieldValidator(ValidatorUtil.NET_INCOME).setRequiredErrorMsg("validation.activityJob.netIncome"));
        }

        @Override
        protected void setDynamicValidations() {
            dependentNetIncome.setMaxValue(LoanApplicationBlock7Section4Form.this.dependentGrossIncome);
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return LoanApplicationBlock7Section4Form.this;
        }
    }

    public String getDependentTime() {
        return dependentTime;
    }

    public void setDependentTime(String dependentTime) {
        this.dependentTime = dependentTime;
    }

    public Integer getDependentGrossIncome() {
        return dependentGrossIncome;
    }

    public void setDependentGrossIncome(Integer dependentGrossIncome) {
        this.dependentGrossIncome = dependentGrossIncome;
    }

    public Integer getDependentNetIncome() {
        return dependentNetIncome;
    }

    public void setDependentNetIncome(Integer dependentNetIncome) {
        this.dependentNetIncome = dependentNetIncome;
    }
}
