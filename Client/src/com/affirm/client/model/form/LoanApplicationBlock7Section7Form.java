/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationBlock7Section7Form extends FormGeneric {

    private String rentierRuc;
    private Integer[] rentierBelonging;

    public LoanApplicationBlock7Section7Form() {
        this.setValidator(new LoanApplicationBlock7Section7FormValidator());
    }

    public class LoanApplicationBlock7Section7FormValidator extends FormValidator implements Serializable {

        public StringFieldValidator rentierRuc;
        public IntegerFieldValidator rentierBelonging;

        public LoanApplicationBlock7Section7FormValidator() {
            addValidator(rentierRuc = new StringFieldValidator(ValidatorUtil.RUC).setRequiredErrorMsg("validation.activityJob.ruc"));
            addValidator(rentierBelonging = new IntegerFieldValidator(ValidatorUtil.RENTIER_BELONGING).setRequiredErrorMsg("validation.activityJob.rentierBelonging"));
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
            return LoanApplicationBlock7Section7Form.this;
        }
    }

    public String getRentierRuc() {
        return rentierRuc;
    }

    public void setRentierRuc(String rentierRuc) {
        this.rentierRuc = rentierRuc;
    }

    public Integer[] getRentierBelonging() {
        return rentierBelonging;
    }

    public void setRentierBelonging(Integer[] rentierBelonging) {
        this.rentierBelonging = rentierBelonging;
    }
}
