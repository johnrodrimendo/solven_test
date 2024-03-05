/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanApplicationBlock7Section9Form extends FormGeneric {

    private String shareholderRuc;
//    private String shareholderCompany;
    private Double shareholderShareholding;

    public LoanApplicationBlock7Section9Form() {
        this.setValidator(new LoanApplicationBlock7Section9FormValidator());
    }

    public class LoanApplicationBlock7Section9FormValidator extends FormValidator implements Serializable {

        public StringFieldValidator shareholderRuc;
//        public StringFieldValidator shareholderCompany;
        public DoubleFieldValidator shareholderShareholding;

        public LoanApplicationBlock7Section9FormValidator() {
            addValidator(shareholderRuc = new StringFieldValidator(ValidatorUtil.RUC).setRequiredErrorMsg("validation.activityJob.ruc"));
//            addValidator(shareholderCompany = new StringFieldValidator(ValidatorUtil.COMPANY_NAME).setRequiredErrorMsg("validation.activityJob.companyName"));
            addValidator(shareholderShareholding = new DoubleFieldValidator(ValidatorUtil.SHAREHOLDING).setRequiredErrorMsg("validation.activityJob.shareholderShareholding"));
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
            return LoanApplicationBlock7Section9Form.this;
        }
    }

    public String getShareholderRuc() {
        return shareholderRuc;
    }

    public void setShareholderRuc(String shareholderRuc) {
        this.shareholderRuc = shareholderRuc;
    }

    public Double getShareholderShareholding() {
        return shareholderShareholding;
    }

    public void setShareholderShareholding(Double shareholderShareholding) {
        this.shareholderShareholding = shareholderShareholding;
    }
}
