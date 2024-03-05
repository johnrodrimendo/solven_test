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
public class LoanApplicationBlock7Section3Form extends FormGeneric {

    private String dependentRuc;
    //    private String dependentCompany;
    private String dependentPhoneNumber;
    private String dependentPhoneNumberCode;

    public LoanApplicationBlock7Section3Form() {
        this.setValidator(new LoanApplicationBlock7Section3FormValidator());
    }

    public class LoanApplicationBlock7Section3FormValidator extends FormValidator implements Serializable {

        public StringFieldValidator dependentRuc;
        //        public StringFieldValidator dependentCompany;
        public StringFieldValidator dependentPhoneNumber;
        public StringFieldValidator dependentPhoneNumberCode;

        public LoanApplicationBlock7Section3FormValidator() {
            addValidator(dependentRuc = new StringFieldValidator(ValidatorUtil.RUC).setRequiredErrorMsg("validation.activityJob.ruc"));
//            addValidator(dependentCompany = new StringFieldValidator(ValidatorUtil.COMPANY_NAME).setRequiredErrorMsg("validation.activityJob.companyName"));
            addValidator(dependentPhoneNumber = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE).setRequiredErrorMsg("validation.process.phoneNumber").setMinCharacters(6));
            addValidator(dependentPhoneNumberCode = new StringFieldValidator(ValidatorUtil.CITY_CODE));
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
            return LoanApplicationBlock7Section3Form.this;
        }
    }

    public String getDependentRuc() {
        return dependentRuc;
    }

    public void setDependentRuc(String dependentRuc) {
        this.dependentRuc = dependentRuc;
    }

    public String getDependentPhoneNumber() {
        return dependentPhoneNumber;
    }

    public void setDependentPhoneNumber(String dependentPhoneNumber) {
        this.dependentPhoneNumber = dependentPhoneNumber;
    }

    public String getDependentPhoneNumberCode() {
        return dependentPhoneNumberCode;
    }

    public void setDependentPhoneNumberCode(String dependentPhoneNumberCode) {
        this.dependentPhoneNumberCode = dependentPhoneNumberCode;
    }
}
