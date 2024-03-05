package com.affirm.client.model.form;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.*;
import org.apache.log4j.Logger;

import java.io.Serializable;

public class DebtConsolidationRegisterForm extends FormGeneric implements Serializable {

    private static final Logger logger = Logger.getLogger(DebtConsolidationRegisterForm.class);

    private Integer docType;
    private String docNumber;
    private String countryCode = "51";
    private String cellphone;
    private String birthday;

    public DebtConsolidationRegisterForm() {
        this.setValidator(new DebtConsolidationRegisterFormValidator());
    }

    public class DebtConsolidationRegisterFormValidator extends FormValidator implements Serializable {

        public IntegerFieldValidator docType;
        public StringFieldValidator docNumber;
        public StringFieldValidator countryCode;
        public StringFieldValidator cellphone;
        public StringFieldValidator birthday;

        public DebtConsolidationRegisterFormValidator() {
            addValidator(docType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID));
            addValidator(docNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI));
            addValidator(countryCode = new StringFieldValidator(ValidatorUtil.COUNTRY_CODE));
            addValidator(cellphone = new StringFieldValidator(ValidatorUtil.CELLPHONE_NUMBER));
            addValidator(birthday = new StringFieldValidator(ValidatorUtil.BIRTHDAY));
        }

        @Override
        protected void setDynamicValidations() {
            if (DebtConsolidationRegisterForm.this.docType == IdentityDocumentType.DNI) {
                docNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
                birthday.setRequired(false);
            } else if (DebtConsolidationRegisterForm.this.docType == IdentityDocumentType.CE) {
                docNumber.update(ValidatorUtil.DOC_NUMBER_CE);
                birthday.setRequired(true);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return DebtConsolidationRegisterForm.this;
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

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
