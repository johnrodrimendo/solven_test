package com.affirm.client.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

/**
 * Created by renzodiaz on 11/24/16.
 */
public class LoanApplicationSignatureForm extends FormGeneric {

    private String loanApplicationSign;

    public LoanApplicationSignatureForm() {
        this.setValidator(new LoanApplicationSignatureValidator());
    }

    public class LoanApplicationSignatureValidator extends FormValidator implements Serializable {

        public StringFieldValidator loanApplicationSign;

        public LoanApplicationSignatureValidator() {
            addValidator(loanApplicationSign = new StringFieldValidator(ValidatorUtil.LOANAPPLICATION_SIGNATURE));
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
            return LoanApplicationSignatureForm.this;
        }

    }

    public String getLoanApplicationSign() {
        return loanApplicationSign;
    }

    public void setLoanApplicationSign(String loanApplicationSign) {
        this.loanApplicationSign = loanApplicationSign;
    }
}
