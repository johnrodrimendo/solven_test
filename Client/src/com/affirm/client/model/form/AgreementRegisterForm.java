package com.affirm.client.model.form;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * Created by stbn on 07/10/16.
 */
public class AgreementRegisterForm extends FormGeneric implements Serializable {

    private Integer loanReasonId;
    private Integer docType;
    private String docNumber;
    private String birthday;
    private String email;

    public AgreementRegisterForm() {
        this.setValidator(new AgreementRegisterForm.AgreementRegisterFormValidator());
    }

    public class AgreementRegisterFormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator loanReasonId;
        public IntegerFieldValidator docType;
        public StringFieldValidator docNumber;
        public StringFieldValidator birthday;
        public StringFieldValidator email;


        public AgreementRegisterFormValidator() {
            addValidator(loanReasonId = new IntegerFieldValidator().setRequired(true));
            addValidator(docType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID).setRequired(false));
            addValidator(docNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI).setRequired(false));
            addValidator(birthday = new StringFieldValidator(ValidatorUtil.BIRTHDAY).setRequired(false));
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL).setRequired(false));
        }

        @Override
        protected void setDynamicValidations() {
            if (AgreementRegisterForm.this.email == null) {
                docType.setRequired(true);
                docNumber.setRequired(true);
                email.setRequired(false);
                if (AgreementRegisterForm.this.docType == IdentityDocumentType.DNI) {
                    docNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
                } else if (AgreementRegisterForm.this.docType == IdentityDocumentType.CE) {
                    docNumber.update(ValidatorUtil.DOC_NUMBER_CE);
                }
            } else {
                docType.setRequired(false);
                docNumber.setRequired(false);
                email.setRequired(true);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return AgreementRegisterForm.this;
        }
    }

    public Integer getLoanReasonId() {
        return loanReasonId;
    }

    public void setLoanReasonId(Integer loanReasonId) {
        this.loanReasonId = loanReasonId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
