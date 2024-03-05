/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationBlock7Section11Form extends FormGeneric {

    private Integer pensionerFrom;
    private Integer pensionerNetIncome;

    public LoanApplicationBlock7Section11Form() {
        this.setValidator(new LoanApplicationBlock7Section11FormValidator());
    }

    public class LoanApplicationBlock7Section11FormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator pensionerFrom;
        public IntegerFieldValidator pensionerNetIncome;

        public LoanApplicationBlock7Section11FormValidator() {
            addValidator(pensionerFrom = new IntegerFieldValidator(ValidatorUtil.PENSIONER_FROM_ID).setRequiredErrorMsg("validation.activityJob.pensionerFrom"));
            addValidator(pensionerNetIncome = new IntegerFieldValidator(ValidatorUtil.NET_INCOME).setRequiredErrorMsg("validation.activityJob.netIncome"));
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
            return LoanApplicationBlock7Section11Form.this;
        }
    }

    public Integer getPensionerFrom() {
        return pensionerFrom;
    }

    public void setPensionerFrom(Integer pensionerFrom) {
        this.pensionerFrom = pensionerFrom;
    }

    public Integer getPensionerNetIncome() {
        return pensionerNetIncome;
    }

    public void setPensionerNetIncome(Integer pensionerNetIncome) {
        this.pensionerNetIncome = pensionerNetIncome;
    }
}
