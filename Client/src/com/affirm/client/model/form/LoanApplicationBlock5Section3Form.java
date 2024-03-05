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
public class LoanApplicationBlock5Section3Form extends FormGeneric {

    private String smsToken;

    public LoanApplicationBlock5Section3Form() {
        this.setValidator(new LoanApplicationBlock5Section3FormValidator());
    }

    public class LoanApplicationBlock5Section3FormValidator extends FormValidator implements Serializable {

        public StringFieldValidator smsToken;

        public LoanApplicationBlock5Section3FormValidator() {
            addValidator(smsToken = new StringFieldValidator(ValidatorUtil.SMS_TOKEN));
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
            return LoanApplicationBlock5Section3Form.this;
        }
    }

    public String getSmsToken() {
        return smsToken;
    }

    public void setSmsToken(String smsToken) {
        this.smsToken = smsToken;
    }
}
