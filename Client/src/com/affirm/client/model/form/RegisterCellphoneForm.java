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
public class RegisterCellphoneForm extends FormGeneric {

    // TODO Send the real country code
    private String countryCode = "51";
    private String cellphone;

    public RegisterCellphoneForm() {
        this.setValidator(new RegisterCellphoneFormValidator());
    }

    public class RegisterCellphoneFormValidator extends FormValidator implements Serializable {

        public StringFieldValidator countryCode;
        public StringFieldValidator cellphone;

        public RegisterCellphoneFormValidator() {
            addValidator(countryCode = new StringFieldValidator(ValidatorUtil.COUNTRY_CODE));
            addValidator(cellphone = new StringFieldValidator(ValidatorUtil.CELLPHONE_NUMBER));
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
            return RegisterCellphoneForm.this;
        }
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }
}
