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
public class ValidateCellphoneForm extends FormGeneric {

    private String authToken;

    public ValidateCellphoneForm() {
        this.setValidator(new ValidateCellphoneFormValidator());
    }

    public class ValidateCellphoneFormValidator extends FormValidator implements Serializable {

        public StringFieldValidator authToken;

        public ValidateCellphoneFormValidator() {
            addValidator(authToken = new StringFieldValidator(ValidatorUtil.SMS_TOKEN));
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
            return ValidateCellphoneForm.this;
        }
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
