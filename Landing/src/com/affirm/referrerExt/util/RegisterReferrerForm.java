package com.affirm.referrerExt.util;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.*;

import java.io.Serializable;

public class RegisterReferrerForm extends FormGeneric implements Serializable {

    private String email;
    private String phoneNumber;
    private Integer docType;
    private String docNumber;

    public RegisterReferrerForm() {
        this.setValidator(new RegisterReferrerForm.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public StringFieldValidator email;
        public StringFieldValidator phoneNumber;
        public IntegerFieldValidator docType;
        public StringFieldValidator docNumber;

        public Validator() {
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL));
            addValidator(phoneNumber = new StringFieldValidator(ValidatorUtil.CELLPHONE_NUMBER));
            addValidator(docType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID));
            addValidator(docNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI));
        }

        @Override
        protected void setDynamicValidations() {
            if (RegisterReferrerForm.this.docType == IdentityDocumentType.DNI) {
                docNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
            } else if (RegisterReferrerForm.this.docType == IdentityDocumentType.CE) {
                docNumber.update(ValidatorUtil.DOC_NUMBER_CE);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return RegisterReferrerForm.this;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
}
