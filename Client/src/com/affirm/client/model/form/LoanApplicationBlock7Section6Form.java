/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationBlock7Section6Form extends FormGeneric {

    private String independentTime;
    private Integer independentGrossIncome;
    private Integer independentNetIncome;

    public LoanApplicationBlock7Section6Form() {
        this.setValidator(new LoanApplicationBlock7Section6FormValidator());
    }

    public class LoanApplicationBlock7Section6FormValidator extends FormValidator implements Serializable {

        public StringFieldValidator independentTime;
        public IntegerFieldValidator independentGrossIncome;
        public IntegerFieldValidator independentNetIncome;

        public LoanApplicationBlock7Section6FormValidator() {
            addValidator(independentTime = new StringFieldValidator(ValidatorUtil.ACTIVITY_TIME).setRequiredErrorMsg("validation.activityJob.activityTime"));
            addValidator(independentGrossIncome = new IntegerFieldValidator(ValidatorUtil.GROSS_INCOME).setRequiredErrorMsg("validation.activityJob.grossIncome"));
            addValidator(independentNetIncome = new IntegerFieldValidator(ValidatorUtil.NET_INCOME).setRequiredErrorMsg("validation.activityJob.netIncome"));
        }

        @Override
        protected void setDynamicValidations() {
            independentNetIncome.setMaxValue(LoanApplicationBlock7Section6Form.this.independentGrossIncome);
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return LoanApplicationBlock7Section6Form.this;
        }
    }

    public String getIndependentTime() {
        return independentTime;
    }

    public void setIndependentTime(String independentTime) {
        this.independentTime = independentTime;
    }

    public Integer getIndependentGrossIncome() {
        return independentGrossIncome;
    }

    public void setIndependentGrossIncome(Integer independentGrossIncome) {
        this.independentGrossIncome = independentGrossIncome;
    }

    public Integer getIndependentNetIncome() {
        return independentNetIncome;
    }

    public void setIndependentNetIncome(Integer independentNetIncome) {
        this.independentNetIncome = independentNetIncome;
    }
}
