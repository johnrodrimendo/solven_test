package com.affirm.client.model.form;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.*;

import java.io.Serializable;

/**
 * Created by stbn on 07/10/16.
 */
public class SalaryAdvanceForm extends FormGeneric implements Serializable {

    private Integer docType;
    private String docNumber;
    private String cellphone;
    private String authToken;
    private String email;
    private String source;
    private String medium;
    private String campaign;
    private String term;
    private String content;
    private String gaClientID;

    public SalaryAdvanceForm() {
        this.setValidator(new SalaryAdvanceForm.SalaryAdvanceRegisterFormValidator());
    }

    public class SalaryAdvanceRegisterFormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator docType;
        public StringFieldValidator docNumber;
        public StringFieldValidator cellphone;
        public StringFieldValidator authToken;
        public StringFieldValidator email;


        public SalaryAdvanceRegisterFormValidator() {
            addValidator(docType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID));
            addValidator(docNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI));
            addValidator(cellphone = new StringFieldValidator(ValidatorUtil.CELLPHONE_NUMBER));
            addValidator(authToken = new StringFieldValidator(ValidatorUtil.SMS_TOKEN));
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL));
        }

        @Override
        protected void setDynamicValidations() {
            if (SalaryAdvanceForm.this.email == null) {
                docType.setRequired(true);
                docNumber.setRequired(true);
                cellphone.setRequired(true);
                email.setRequired(false);
                if (SalaryAdvanceForm.this.docType == IdentityDocumentType.DNI) {
                    docNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
                } else if (SalaryAdvanceForm.this.docType == IdentityDocumentType.CE) {
                    docNumber.update(ValidatorUtil.DOC_NUMBER_CE);
                }
            } else {
                docType.setRequired(false);
                docNumber.setRequired(false);
                cellphone.setRequired(false);
                email.setRequired(true);
            }

            if (SalaryAdvanceForm.this.authToken == null) {
                authToken.setRequired(false);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return SalaryAdvanceForm.this;
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

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGaClientID() {
        return gaClientID;
    }

    public void setGaClientID(String gaClientID) {
        this.gaClientID = gaClientID;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
