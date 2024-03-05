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
public class LoanApplicationBlock7Section10Form extends FormGeneric {

    private Integer shareholderResultU12M;

    public LoanApplicationBlock7Section10Form() {
        this.setValidator(new LoanApplicationBlock7Section10FormValidator());
    }

    public class LoanApplicationBlock7Section10FormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator shareholderResultU12M;

        public LoanApplicationBlock7Section10FormValidator() {
            addValidator(shareholderResultU12M = new IntegerFieldValidator(ValidatorUtil.RESULT_U12M).setRequiredErrorMsg("validation.activityJob.shareholderResultU12M"));
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
            return LoanApplicationBlock7Section10Form.this;
        }
    }

    public Integer getShareholderResultU12M() {
        return shareholderResultU12M;
    }

    public void setShareholderResultU12M(Integer shareholderResultU12M) {
        this.shareholderResultU12M = shareholderResultU12M;
    }
}
