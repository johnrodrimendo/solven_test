package com.affirm.client.model.form;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.*;
import org.apache.log4j.Logger;

import java.io.Serializable;

public class ShortTermRegisterForm extends FormGeneric implements Serializable {

    private static final Logger logger = Logger.getLogger(ShortTermRegisterForm.class);

    private Integer docType;
    private String docNumber;
    private String birthDate;

    // Cellphone data
    private String countryCode = "51";
    private String phoneNumber;

    // Loan application
    private Integer loanApplicationAmmount;
    private Integer loanApplicationInstallments;
    private Integer loanApplicationCluster;

    public ShortTermRegisterForm() {
        this.setValidator(new UserRegisterFormValidator());
    }

    public class UserRegisterFormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator docType;
        public StringFieldValidator docNumber;
        public StringFieldValidator birthDate;
        public StringFieldValidator phoneNumber;
        public IntegerFieldValidator loanApplicationAmmount;
        public IntegerFieldValidator loanApplicationInstallments;
        public IntegerFieldValidator loanApplicationCluster;

        public UserRegisterFormValidator() {
            addValidator(docType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID));
            addValidator(docNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI));
            addValidator(birthDate = new StringFieldValidator(ValidatorUtil.BIRTHDAY).setRequired(false));
            addValidator(phoneNumber = new StringFieldValidator(ValidatorUtil.CELLPHONE_NUMBER).setRequired(false));
            addValidator(loanApplicationAmmount = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_AMMOUNT_SHORT_TERM));
            addValidator(loanApplicationInstallments = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_INSTALLMENTS_SHORT_TERM));
            addValidator(loanApplicationCluster = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_CLUSTER));
        }

        @Override
        protected void setDynamicValidations() {
            if (ShortTermRegisterForm.this.docType == IdentityDocumentType.DNI) {
                docNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
                birthDate.setRequired(false);
            } else if (ShortTermRegisterForm.this.docType == IdentityDocumentType.CE) {
                docNumber.update(ValidatorUtil.DOC_NUMBER_CE);
                birthDate.setRequired(true);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return ShortTermRegisterForm.this;
        }
    }

    public Integer getDocType() {
        return docType;
    }

    public void setDocType(Integer docType) {
        this.docType = docType;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getLoanApplicationAmmount() {
        return loanApplicationAmmount;
    }

    public void setLoanApplicationAmmount(Integer loanApplicationAmmount) {
        this.loanApplicationAmmount = loanApplicationAmmount;
    }

    public Integer getLoanApplicationInstallments() {
        return loanApplicationInstallments;
    }

    public void setLoanApplicationInstallments(Integer loanApplicationInstallments) {
        this.loanApplicationInstallments = loanApplicationInstallments;
    }

    public Integer getLoanApplicationCluster() {
        return loanApplicationCluster;
    }

    public void setLoanApplicationCluster(Integer loanApplicationCluster) {
        this.loanApplicationCluster = loanApplicationCluster;
    }
}
