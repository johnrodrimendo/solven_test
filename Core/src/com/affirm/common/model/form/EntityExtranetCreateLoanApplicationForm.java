package com.affirm.common.model.form;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.*;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public class EntityExtranetCreateLoanApplicationForm extends FormGeneric implements Serializable {

        private Integer docType;
        private String documentNumber;
        private String email;
        private String name;
        private String surname;
        private String lastSurname;
        private String birthday;
        private Integer amount;
        private Integer installments;
        private Integer reason;
        private String phone;
        private String code;
        private Integer nationality;

        private Integer countryId;
        private HttpServletRequest request;
        private Integer agentId;
        private String source;
        private String medium;
        private String campaign;
        private String term;
        private String content;
        private String gclid;
        private String gaClientID;
        private Integer entityBrandingId;
        private Integer entityUserId;

        private boolean nonExistingPerson;

    public EntityExtranetCreateLoanApplicationForm() {
        this.setValidator(new EntityExtranetCreateLoanApplicationForm.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

            public IntegerFieldValidator docType;
            public StringFieldValidator documentNumber;
            public StringFieldValidator email;
            public StringFieldValidator name;
            public StringFieldValidator surname;
            public StringFieldValidator lastSurname;
            public StringFieldValidator birthday;
            public IntegerFieldValidator amount;
            public IntegerFieldValidator installments;
            public IntegerFieldValidator reason;
            public StringFieldValidator phone;
            public StringFieldValidator code;

            public Validator() {
                addValidator(docType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID));
                addValidator(documentNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI).setFieldName("Número de documento"));
                addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL).setFieldName("Correo electrónico"));
                addValidator(name = new StringFieldValidator(ValidatorUtil.NAME).setFieldName("Nombre").setRequired(false));
                addValidator(surname = new StringFieldValidator(ValidatorUtil.FIRST_SURNAME).setFieldName("Apellido").setRequired(false));
                addValidator(lastSurname = new StringFieldValidator(ValidatorUtil.LAST_SURNAME).setFieldName("Apellido Materno").setRequired(false));
                addValidator(birthday = new StringFieldValidator(ValidatorUtil.BIRTHDAY).setFieldName("Fecha Nacimiento").setRequired(false));
                addValidator(amount = new IntegerFieldValidator(ValidatorUtil.SELF_EVALUATION_AMOUNT).setMaxValueErrorMsg("validation.int.maxValueMoney").setMinValueErrorMsg("validation.int.minValueMoney").setFieldName("monto"));
                addValidator(installments = new IntegerFieldValidator(ValidatorUtil.LOANAPPLICATION_INSTALLMENTS).setMaxValueErrorMsg("validation.int.maxValueMoney").setMinValueErrorMsg("validation.int.minValueMoney").setFieldName("cuotas"));
                addValidator(reason=new IntegerFieldValidator().setFieldName("Destino").setRequired(true));
                addValidator(phone = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE).setFieldName("Número de celular").setRequired(true));
                addValidator(code = new StringFieldValidator(ValidatorUtil.COUNTRY_CODE).setFieldName("Código de Área").setRequired(false));
            }

            @Override
            protected void setDynamicValidations() {
                if (EntityExtranetCreateLoanApplicationForm.this.docType == IdentityDocumentType.DNI) {
                    documentNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
                } else if (EntityExtranetCreateLoanApplicationForm.this.docType == IdentityDocumentType.CE) {
                    documentNumber.update(ValidatorUtil.DOC_NUMBER_CE);
                } else if (EntityExtranetCreateLoanApplicationForm.this.docType == IdentityDocumentType.CDI) {
                    documentNumber.update(ValidatorUtil.DOC_NUMBER_CDI);
                } else if (EntityExtranetCreateLoanApplicationForm.this.docType == IdentityDocumentType.CUIL) {
                    documentNumber.update(ValidatorUtil.DOC_NUMBER_CUIL);
                } else if (EntityExtranetCreateLoanApplicationForm.this.docType == IdentityDocumentType.CUIT) {
                    documentNumber.update(ValidatorUtil.DOC_NUMBER_CUIT);
                }

                if (EntityExtranetCreateLoanApplicationForm.this.countryId == CountryParam.COUNTRY_PERU) {
                    code.setRequired(false);
                    phone.setMaxCharacters(10);
                } else if (EntityExtranetCreateLoanApplicationForm.this.countryId == CountryParam.COUNTRY_ARGENTINA) {
                    phone.setMaxCharacters(11 - EntityExtranetCreateLoanApplicationForm.this.code.length());
                    phone.setMinCharacters(11 - EntityExtranetCreateLoanApplicationForm.this.code.length());
                    code.setMaxCharacters(11 - EntityExtranetCreateLoanApplicationForm.this.phone.length());
                    code.setMinCharacters(11 - EntityExtranetCreateLoanApplicationForm.this.phone.length());
                }
            }


            public void configValidator(Integer countryId) {
                if (countryId == CountryParam.COUNTRY_ARGENTINA) {
                    docType.setRequired(false);
                    documentNumber.update(ValidatorUtil.DOC_NUMBER_CUIT);
                    name.setRequired(true);
                    surname.setRequired(true);
                    phone.update(ValidatorUtil.CELLPHONE_NUMBER_ARGENTINA.setMinCharacters(9).setMaxCharacters(11));
                    code.update(ValidatorUtil.ARGENTINA_PHONE_AREA_CODE);
                    birthday.setRequired(true);
                } else if (countryId == CountryParam.COUNTRY_PERU) {
                    documentNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
                    phone.update(ValidatorUtil.CELLPHONE_NUMBER);
                    code.setRequired(false);
                    name.setRequired(true);
                    surname.setRequired(true);
                    lastSurname.setRequired(true);
                    birthday.setRequired(true);
                }
            }

            @Override
            protected Object getSubclass() {
                return this;
            }

            @Override
            protected Object getFormClass() {
                return EntityExtranetCreateLoanApplicationForm.this;
            }
        }

    public Integer getDocType() {
        return docType;
    }

    public void setDocType(Integer docType) {
        this.docType = docType;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLastSurname() {
        return lastSurname;
    }

    public void setLastSurname(String lastSurname) {
        this.lastSurname = lastSurname;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
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

    public String getGclid() {
        return gclid;
    }

    public void setGclid(String gclid) {
        this.gclid = gclid;
    }

    public String getGaClientID() {
        return gaClientID;
    }

    public void setGaClientID(String gaClientID) {
        this.gaClientID = gaClientID;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getReason() {
        return reason;
    }

    public void setReason(Integer reason) {
        this.reason = reason;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getEntityBrandingId() {
        return entityBrandingId;
    }

    public void setEntityBrandingId(Integer entityBrandingId) {
        this.entityBrandingId = entityBrandingId;
    }

    public Integer getEntityUserId() {
        return entityUserId;
    }

    public void setEntityUserId(Integer entityUserId) {
        this.entityUserId = entityUserId;
    }

    public boolean isNonExistingPerson() {
        return nonExistingPerson;
    }

    public void setNonExistingPerson(boolean nonExistingPerson) {
        this.nonExistingPerson = nonExistingPerson;
    }

    public Integer getNationality() {
        return nationality;
    }

    public void setNationality(Integer nationality) {
        this.nationality = nationality;
    }
}
