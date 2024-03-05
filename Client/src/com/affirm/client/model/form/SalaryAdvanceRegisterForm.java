package com.affirm.client.model.form;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * Created by stbn on 07/10/16.
 */
public class SalaryAdvanceRegisterForm extends FormGeneric implements Serializable {

    private Integer docType;
    private String docNumber;
    private String birthday;
    private String email;

    public SalaryAdvanceRegisterForm() {
        this.setValidator(new SalaryAdvanceRegisterForm.SalaryAdvanceRegisterFormValidator());
    }

    public class SalaryAdvanceRegisterFormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator docType;
        public StringFieldValidator docNumber;
        public StringFieldValidator birthday;
        public StringFieldValidator email;


        public SalaryAdvanceRegisterFormValidator() {
            addValidator(docType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID).setRequired(false));
            addValidator(docNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI).setRequired(false));
            addValidator(birthday = new StringFieldValidator(ValidatorUtil.BIRTHDAY).setRequired(false));
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL).setRequired(false));
        }

        @Override
        protected void setDynamicValidations() {
            if (SalaryAdvanceRegisterForm.this.email == null) {
                docType.setRequired(true);
                docNumber.setRequired(true);
                email.setRequired(false);
                if (SalaryAdvanceRegisterForm.this.docType == IdentityDocumentType.DNI) {
                    docNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
                } else if (SalaryAdvanceRegisterForm.this.docType == IdentityDocumentType.CE) {
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
            return SalaryAdvanceRegisterForm.this;
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
