package com.affirm.client.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

/**
 * Created by jrodriguez on 31/05/16.
 */
public class LoanApplicationUpdateModalForm extends FormGeneric implements Serializable {

    private Integer updModalLoanAmmount;
    private Integer updModalLoanInstallemnts;
    private Integer updModalLoanShorTermDays;
    private Integer updModalLoanProductId;

    public LoanApplicationUpdateModalForm() {
        this.setValidator(new LoanApplicationUpdateModalFormValidator());
    }

    public LoanApplicationUpdateModalForm(Integer updModalLoanAmmount, Integer updModalLoanInstallemnts, Integer updModalLoanShorTermDays, Integer updModalLoanProductId) {
        this.updModalLoanAmmount = updModalLoanAmmount;
        this.updModalLoanInstallemnts = updModalLoanInstallemnts;
        this.updModalLoanShorTermDays = updModalLoanShorTermDays;
        this.updModalLoanProductId = updModalLoanProductId;
        this.setValidator(new LoanApplicationUpdateModalFormValidator());
    }

    public class LoanApplicationUpdateModalFormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator updModalLoanAmmount;
        public IntegerFieldValidator updModalLoanInstallemnts;
        public IntegerFieldValidator updModalLoanShorTermDays;

        public LoanApplicationUpdateModalFormValidator() {
            addValidator(updModalLoanAmmount = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_AMMOUNT_TRADITIONAL));
            addValidator(updModalLoanInstallemnts = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_INSTALLMENTS));
            addValidator(updModalLoanShorTermDays = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_DAYS).setRequired(false));
        }

        @Override
        protected void setDynamicValidations() {
//            if (LoanApplicationUpdateModalForm.this.updModalLoanShorTermDays != null) {
//                // Short Term Product
//                updModalLoanAmmount.update(ValidatorUtil.LOANAPPLICATION_AMMOUNT_SHORT_TERM);
//                updModalLoanShorTermDays.setRequired(true);
//                updModalLoanInstallemnts.setRequired(false);
//            } else {
//                // Traditional Product
//                updModalLoanAmmount.update(ValidatorUtil.LOANAPPLICATION_AMMOUNT_TRADITIONAL);
//                updModalLoanShorTermDays.setRequired(false);
//                updModalLoanInstallemnts.setRequired(true);
//            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return LoanApplicationUpdateModalForm.this;
        }
    }

    public Integer getUpdModalLoanAmmount() {
        return updModalLoanAmmount;
    }

    public void setUpdModalLoanAmmount(Integer updModalLoanAmmount) {
        this.updModalLoanAmmount = updModalLoanAmmount;
    }

    public Integer getUpdModalLoanInstallemnts() {
        return updModalLoanInstallemnts;
    }

    public void setUpdModalLoanInstallemnts(Integer updModalLoanInstallemnts) {
        this.updModalLoanInstallemnts = updModalLoanInstallemnts;
    }

    public Integer getUpdModalLoanShorTermDays() {
        return updModalLoanShorTermDays;
    }

    public void setUpdModalLoanShorTermDays(Integer updModalLoanShorTermDays) {
        this.updModalLoanShorTermDays = updModalLoanShorTermDays;
    }

    public Integer getUpdModalLoanProductId() {
        return updModalLoanProductId;
    }

    public void setUpdModalLoanProductId(Integer updModalLoanProductId) {
        this.updModalLoanProductId = updModalLoanProductId;
    }
}
