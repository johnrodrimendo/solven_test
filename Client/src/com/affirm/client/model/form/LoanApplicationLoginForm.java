package com.affirm.client.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

/**
 * Created by renzodiaz on 11/23/16.
 */
public class LoanApplicationLoginForm extends FormGeneric {

    private String cellPhoneNumber;

    public LoanApplicationLoginForm() {
        this.setValidator(new LoanApplicationLoginForm.LoginLoanApplicationFormValidator());
    }

    public class LoginLoanApplicationFormValidator extends FormValidator implements Serializable {

        public StringFieldValidator cellPhoneNumber;

        public LoginLoanApplicationFormValidator() {
            addValidator(cellPhoneNumber = new StringFieldValidator(ValidatorUtil.CELLPHONE_NUMBER));
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
            return LoanApplicationLoginForm.this;
        }
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
    }
}
