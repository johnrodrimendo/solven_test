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
public class LoanApplicationBlock7Section12Form extends FormGeneric {

    private String housekeeperEmployer;
    private String housekeeperEmployerPhone;
    private String housekeeperEmployerPhoneCode;

    public LoanApplicationBlock7Section12Form() {
        this.setValidator(new LoanApplicationBlock7Section12FormValidator());
    }

    public class LoanApplicationBlock7Section12FormValidator extends FormValidator implements Serializable {

        public StringFieldValidator housekeeperEmployer;
        public StringFieldValidator housekeeperEmployerPhone;
        public StringFieldValidator housekeeperEmployerPhoneCode;

        public LoanApplicationBlock7Section12FormValidator() {
            addValidator(housekeeperEmployer = new StringFieldValidator(ValidatorUtil.HOUSEKEEPER_EMPLOYER).setRequiredErrorMsg("validation.activityJob.housekeeperEmployer"));
            addValidator(housekeeperEmployerPhone = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE).setRequiredErrorMsg("validation.process.phoneNumber").setMinCharacters(6));
            addValidator(housekeeperEmployerPhoneCode = new StringFieldValidator(ValidatorUtil.CITY_CODE));
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
            return LoanApplicationBlock7Section12Form.this;
        }
    }

    public String getHousekeeperEmployer() {
        return housekeeperEmployer;
    }

    public void setHousekeeperEmployer(String housekeeperEmployer) {
        this.housekeeperEmployer = housekeeperEmployer;
    }

    public String getHousekeeperEmployerPhone() {
        return housekeeperEmployerPhone;
    }

    public void setHousekeeperEmployerPhone(String housekeeperEmployerPhone) {
        this.housekeeperEmployerPhone = housekeeperEmployerPhone;
    }

    public String getHousekeeperEmployerPhoneCode() {
        return housekeeperEmployerPhoneCode;
    }

    public void setHousekeeperEmployerPhoneCode(String housekeeperEmployerPhoneCode) {
        this.housekeeperEmployerPhoneCode = housekeeperEmployerPhoneCode;
    }
}
