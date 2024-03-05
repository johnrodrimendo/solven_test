package com.affirm.common.model.form;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.*;

import java.io.Serializable;

public class CreateRemarketingLoanApplicationForm extends FormGeneric implements Serializable {

    private Integer documentType;
    private String documentNumber;
    private String email;
    private Integer countryId;

    public CreateRemarketingLoanApplicationForm() {
        this.setValidator(new CreateRemarketingLoanApplicationForm.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator documentType;
        public StringFieldValidator documentNumber;
        public StringFieldValidator email;

        public Validator() {
            addValidator(documentType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID));
            addValidator(documentNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI).setFieldName("número de documento"));
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL).setFieldName("correo electrónico"));
        }

        @Override
        protected void setDynamicValidations() {

            if (CreateRemarketingLoanApplicationForm.this.documentType == IdentityDocumentType.DNI) {
                documentNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
            } else if (CreateRemarketingLoanApplicationForm.this.documentType == IdentityDocumentType.CE) {
                documentNumber.update(ValidatorUtil.DOC_NUMBER_CE);
            } else if (CreateRemarketingLoanApplicationForm.this.documentType == IdentityDocumentType.CDI) {
                documentNumber.update(ValidatorUtil.DOC_NUMBER_CDI_PERSONAL);
            } else if (CreateRemarketingLoanApplicationForm.this.documentType == IdentityDocumentType.CUIL) {
                documentNumber.update(ValidatorUtil.DOC_NUMBER_CUIL_PERSONAL);
            } else if (CreateRemarketingLoanApplicationForm.this.documentType == IdentityDocumentType.CUIT) {
                documentNumber.update(ValidatorUtil.DOC_NUMBER_CUIT_PERSONAL);
            }
        }

        public void configValidator(Integer countryId) {
            if (countryId == CountryParam.COUNTRY_ARGENTINA) {
                documentType.setRequired(false);
                documentNumber.update(ValidatorUtil.DOC_NUMBER_CUIT_PERSONAL);
            } else if (countryId == CountryParam.COUNTRY_PERU) {
                documentNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return CreateRemarketingLoanApplicationForm.this;
        }
    }

    public Integer getDocumentType() {
        return documentType;
    }

    public void setDocumentType(Integer documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }
}
