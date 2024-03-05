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
public class LoanApplicationDocumentsForm extends FormGeneric implements Serializable {

    private Integer relative1;
    private Integer relative2;
    private String contactCityCode1;
    private String contactCityCode2;
    private String contactPhone1;
    private String contactPhone2;

    public LoanApplicationDocumentsForm() {
        this.setValidator(new LoanApplicationDocumentsFormValidator());
    }

    public class LoanApplicationDocumentsFormValidator extends FormValidator implements Serializable {

        public StringFieldValidator contactPhone1;
        public StringFieldValidator contactPhone2;
        public StringFieldValidator contactCityCode1;
        public StringFieldValidator contactCityCode2;

        public LoanApplicationDocumentsFormValidator() {
            addValidator(contactPhone1 = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE).setRequired(true));
            addValidator(contactPhone2 = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE).setRequired(false));
            addValidator(contactCityCode1 = new StringFieldValidator(ValidatorUtil.CITY_CODE).setRequired(true));
            addValidator(contactCityCode2 = new StringFieldValidator(ValidatorUtil.CITY_CODE).setRequired(false));
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
            return LoanApplicationDocumentsForm.this;
        }
    }

    public Integer getRelative1() {
        return relative1;
    }

    public void setRelative1(Integer relative1) {
        this.relative1 = relative1;
    }

    public Integer getRelative2() {
        return relative2;
    }

    public void setRelative2(Integer relative2) {
        this.relative2 = relative2;
    }

    public String getContactPhone1() {
        return contactPhone1;
    }

    public void setContactPhone1(String contactPhone1) {
        this.contactPhone1 = contactPhone1;
    }

    public String getContactPhone2() {
        return contactPhone2;
    }

    public void setContactPhone2(String contactPhone2) {
        this.contactPhone2 = contactPhone2;
    }

    public String getContactCityCode1() {
        return contactCityCode1;
    }

    public void setContactCityCode1(String contactCityCode1) {
        this.contactCityCode1 = contactCityCode1;
    }

    public String getContactCityCode2() {
        return contactCityCode2;
    }

    public void setContactCityCode2(String contactCityCode2) {
        this.contactCityCode2 = contactCityCode2;
    }
}
