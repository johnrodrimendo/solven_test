/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationBlock5Form extends FormGeneric {

    private String phoneNumber;

    public LoanApplicationBlock5Form() {
        this.setValidator(new LoanApplicationBlock5FormValidator());
    }

    public class LoanApplicationBlock5FormValidator extends FormValidator implements Serializable {

        public StringFieldValidator phoneNumber;

        public LoanApplicationBlock5FormValidator() {
            addValidator(phoneNumber = new StringFieldValidator(ValidatorUtil.CELLPHONE_NUMBER).setRequiredErrorMsg("validation.process.phoneNumber"));
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
            return LoanApplicationBlock5Form.this;
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
