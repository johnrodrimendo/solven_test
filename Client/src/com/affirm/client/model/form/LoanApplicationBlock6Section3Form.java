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
public class LoanApplicationBlock6Section3Form extends FormGeneric {

    private String fullAddress;

    public LoanApplicationBlock6Section3Form() {
        this.setValidator(new LoanApplicationBlock6Section3FormValidator());
    }

    public class LoanApplicationBlock6Section3FormValidator extends FormValidator implements Serializable {

        public StringFieldValidator fullAddress;

        public LoanApplicationBlock6Section3FormValidator() {
            addValidator(fullAddress = new StringFieldValidator(ValidatorUtil.FULL_ADDRESS).setRequiredErrorMsg("validation.address.fullAddress"));
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
            return LoanApplicationBlock6Section3Form.this;
        }
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }
}
