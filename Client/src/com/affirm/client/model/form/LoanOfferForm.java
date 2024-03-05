/**
 *
 */
package com.affirm.client.model.form;

import com.affirm.common.util.FormGeneric;
import com.affirm.common.util.FormValidator;
import com.affirm.common.util.IntegerFieldValidator;
import com.affirm.common.util.ValidatorUtil;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class LoanOfferForm extends FormGeneric {

    private Double ammount;
    private Integer installments;
    private Integer loanShorTermDays;
    private Double installmentAmmount;
    private Double commission;
    private Double effectiveAnnualRate;

    public LoanOfferForm() {
        this.setValidator(new LoanOfferFormValidator());
    }

    public class LoanOfferFormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator ammount;
        public IntegerFieldValidator installments;

        public LoanOfferFormValidator() {
            addValidator(ammount = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_AMMOUNT_TRADITIONAL));
            addValidator(installments = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_INSTALLMENTS));
        }

        @Override
        protected void setDynamicValidations() {
        }

//        @Override
//        protected void setValues() {
//            ammount.setValue(LoanOfferForm.this.ammount);
//            installments.setValue(LoanOfferForm.this.installments);
//        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return LoanOfferForm.this;
        }
    }

    public Double getAmmount() {
        return ammount;
    }

    public void setAmmount(Double ammount) {
        this.ammount = ammount;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Double getInstallmentAmmount() {
        return installmentAmmount;
    }

    public void setInstallmentAmmount(Double installmentAmmount) {
        this.installmentAmmount = installmentAmmount;
    }

    public Integer getLoanShorTermDays() {
        return loanShorTermDays;
    }

    public void setLoanShorTermDays(Integer loanShorTermDays) {
        this.loanShorTermDays = loanShorTermDays;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public Double getEffectiveAnnualRate() {
        return effectiveAnnualRate;
    }

    public void setEffectiveAnnualRate(Double effectiveAnnualRate) {
        this.effectiveAnnualRate = effectiveAnnualRate;
    }


}
