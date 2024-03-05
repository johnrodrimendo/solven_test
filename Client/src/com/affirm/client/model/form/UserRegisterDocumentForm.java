package com.affirm.client.model.form;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * Created by stbn on 07/10/16.
 */
public class UserRegisterDocumentForm extends FormGeneric implements Serializable {

    private Integer docType;
    private String docNumber;
    private String birthDate;

    public UserRegisterDocumentForm() {
        this.setValidator(new UserRegisterDocumentForm.UserRegisterDocumentFormValidator());
    }

    public class UserRegisterDocumentFormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator docType;
        public StringFieldValidator docNumber;
        public StringFieldValidator birthDate;


        public UserRegisterDocumentFormValidator() {
            addValidator(docType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID).setRequired(true));
            addValidator(docNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI).setRequired(true));
            addValidator(birthDate = new StringFieldValidator(ValidatorUtil.BIRTHDAY).setRequired(false));
        }

        @Override
        protected void setDynamicValidations() {
            if (UserRegisterDocumentForm.this.docType == IdentityDocumentType.DNI) {
                docNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
            } else if (UserRegisterDocumentForm.this.docType == IdentityDocumentType.CE) {
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
            return UserRegisterDocumentForm.this;
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
}