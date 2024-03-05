package com.affirm.backoffice.model.form;

import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * Created by jrodriguez on 21/07/16.
 */
public class RegisterDisbursementForm extends FormGeneric implements Serializable {

    private String disbursementDate;
    private Character paymentType;
    private String paymentCheckNumber;
    private Integer signatureSysuser;

    public RegisterDisbursementForm() {
        this.setValidator(new RegisterDisbursementFormValidator());
    }

    public class RegisterDisbursementFormValidator extends FormValidator implements Serializable {

        public StringFieldValidator disbursementDate;
        public CharFieldValidator paymentType;
        public StringFieldValidator paymentCheckNumber;
        public IntegerFieldValidator signatureSysuser;

        public RegisterDisbursementFormValidator() {
            addValidator(disbursementDate = new StringFieldValidator(ValidatorUtil.DISBURSEMENT_DATE));
            addValidator(paymentType = new CharFieldValidator(ValidatorUtil.PAYMENT_TYPE));
            addValidator(paymentCheckNumber = new StringFieldValidator(ValidatorUtil.PAYMENT_CHECK_NUMBER));
            addValidator(signatureSysuser = new IntegerFieldValidator(ValidatorUtil.PAYMENT_SIGNATURE_SYSUSER_ID));
        }

        @Override
        protected void setDynamicValidations() {
            if (paymentType.getValue() == 'C') {
                paymentCheckNumber.setRequired(true);
            } else if (paymentType.getValue() == 'T') {
                paymentCheckNumber.setRequired(false);
            }
        }

//        @Override
//        protected void setValues() {
//            disbursementDate.setValue(RegisterDisbursementForm.this.disbursementDate);
//            paymentType.setValue(RegisterDisbursementForm.this.paymentType);
//            paymentCheckNumber.setValue(RegisterDisbursementForm.this.paymentCheckNumber);
//            signatureSysuser.setValue(RegisterDisbursementForm.this.signatureSysuser);
//        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return RegisterDisbursementForm.this;
        }

    }

    public String getDisbursementDate() {
        return disbursementDate;
    }

    public void setDisbursementDate(String disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public Character getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Character paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentCheckNumber() {
        return paymentCheckNumber;
    }

    public void setPaymentCheckNumber(String paymentCheckNumber) {
        this.paymentCheckNumber = paymentCheckNumber;
    }

    public Integer getSignatureSysuser() {
        return signatureSysuser;
    }

    public void setSignatureSysuser(Integer signatureSysuser) {
        this.signatureSysuser = signatureSysuser;
    }
}
