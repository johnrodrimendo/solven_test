/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationBlock7Section5Form extends FormGeneric {

    private String independentRuc;
    private Integer independentVouchers;

    public LoanApplicationBlock7Section5Form() {
        this.setValidator(new LoanApplicationBlock7Section5FormValidator());
    }

    public class LoanApplicationBlock7Section5FormValidator extends FormValidator implements Serializable {

        public StringFieldValidator independentRuc;
        public IntegerFieldValidator independentVouchers;

        public LoanApplicationBlock7Section5FormValidator() {
            addValidator(independentRuc = new StringFieldValidator(ValidatorUtil.RUC).setRequiredErrorMsg("validation.activityJob.ruc"));
            addValidator(independentVouchers = new IntegerFieldValidator(ValidatorUtil.VOUCHER_TYPE_ID).setRequiredErrorMsg("validation.activityJob.independentVouchers"));
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
            return LoanApplicationBlock7Section5Form.this;
        }
    }

    public String getIndependentRuc() {
        return independentRuc;
    }

    public void setIndependentRuc(String independentRuc) {
        this.independentRuc = independentRuc;
    }

    public Integer getIndependentVouchers() {
        return independentVouchers;
    }

    public void setIndependentVouchers(Integer independentVouchers) {
        this.independentVouchers = independentVouchers;
    }
}
