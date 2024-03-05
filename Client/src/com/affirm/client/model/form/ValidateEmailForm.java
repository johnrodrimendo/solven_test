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
public class ValidateEmailForm extends FormGeneric {

    private String email;

    public ValidateEmailForm() {
        this.setValidator(new RegisterCellphoneFormValidator());
    }

    public static ValidateEmailForm newInstance(){
        return new ValidateEmailForm();
    }

    public class RegisterCellphoneFormValidator extends FormValidator implements Serializable {

        public StringFieldValidator email;

        public RegisterCellphoneFormValidator() {
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL));
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
            return ValidateEmailForm.this;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
