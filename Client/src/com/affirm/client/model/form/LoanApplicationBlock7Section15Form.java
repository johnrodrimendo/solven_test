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
public class LoanApplicationBlock7Section15Form extends FormGeneric {

    private Integer otherIncome;

    public LoanApplicationBlock7Section15Form() {
        this.setValidator(new LoanApplicationBlock7Section15FormValidator());
    }

    public class LoanApplicationBlock7Section15FormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator otherIncome;

        public LoanApplicationBlock7Section15FormValidator() {
            addValidator(otherIncome = new IntegerFieldValidator(ValidatorUtil.OTHER_INCOME).setRequiredErrorMsg("validation.activityJob.anotherIncomes"));
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
            return LoanApplicationBlock7Section15Form.this;
        }
    }

    public Integer getOtherIncome() {
        return otherIncome;
    }

    public void setOtherIncome(Integer otherIncome) {
        this.otherIncome = otherIncome;
    }
}
