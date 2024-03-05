package com.affirm.common.model.form;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.*;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public class ShortProcessQuestion1Form extends FormGeneric implements Serializable {

    private Integer docType;
    private String documentNumber;
    private String email;
    private Boolean acceptAgreement;
    private Boolean conditionsPolicy;
    private String name;
    private String surname;
    private Boolean pep;
    private Integer reason;
    private Integer amount;
    private Integer timeLimit;

    private Integer countryId;
    private HttpServletRequest request;
    private Integer agentId;
    private String categoryUrl;
    private String externalParams;
    private String source;
    private String medium;
    private String campaign;
    private String term;
    private String content;
    private String gclid;
    private String gaClientID;
    private String phone;
    private String code;
    private String birthday;
    private String referrerPersonId;
    private String ABTesting;
    private Boolean acceptAgreement2;

    public ShortProcessQuestion1Form() {
        this.setValidator(new ShortProcessQuestion1Form.Validator());
    }

    public Integer getReason() {
        return reason;
    }

    public void setReason(Integer reason) {
        this.reason = reason;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public class Validator extends FormValidator implements Serializable {

        public BooleanFieldValidator acceptAgreement;
        public BooleanFieldValidator conditionsPolicy;
        public StringFieldValidator name;
        public StringFieldValidator surname;
        //public BooleanFieldValidator pep;

        public IntegerFieldValidator amount;
        public IntegerFieldValidator reason;
        public IntegerFieldValidator docType;
        public StringFieldValidator documentNumber;
        public StringFieldValidator email;
        public StringFieldValidator phone;
        public StringFieldValidator code;
        public StringFieldValidator birthday;
        public IntegerFieldValidator timeLimit;
        public BooleanFieldValidator acceptAgreement2;

        public Validator() {
            addValidator(amount = new IntegerFieldValidator(ValidatorUtil.SELF_EVALUATION_AMOUNT).setMaxValueErrorMsg("validation.int.maxValueMoney").setMinValueErrorMsg("validation.int.minValueMoney").setFieldName("monto").setRequired(true).setRestricted(true));
            addValidator(reason = new IntegerFieldValidator().setRequired(true).setFieldName("Destino"));
            addValidator(docType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID).setRequired(true));
            addValidator(documentNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI).setFieldName("Número de documento").setRequired(true));
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL).setFieldName("Correo electrónico").setRequired(true));
            addValidator(phone = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE).setFieldName("Número de celular").setRequired(true));
            addValidator(name = new StringFieldValidator(ValidatorUtil.NAME).setFieldName("Nombre").setRequired(false));
            addValidator(surname = new StringFieldValidator(ValidatorUtil.FIRST_SURNAME).setFieldName("Apellido").setRequired(false));
            addValidator(acceptAgreement = new BooleanFieldValidator().setRequired(true).setRequiredErrorMsg("validation.checkbix.termsConditions"));
            addValidator(conditionsPolicy = new BooleanFieldValidator().setRequired(false).setRequiredErrorMsg("validation.checkbix.privacyPolicy"));
            addValidator(code = new StringFieldValidator(ValidatorUtil.COUNTRY_CODE).setFieldName("Código de Área").setRequired(false));
            addValidator(birthday = new StringFieldValidator(ValidatorUtil.BIRTHDAY).setRequired(false));
            addValidator(timeLimit = new IntegerFieldValidator().setRequired(false).setFieldName("Plazo"));
            addValidator(acceptAgreement2 = new BooleanFieldValidator().setRequired(false).setRequiredErrorMsg("validation.checkbix.termsConditions"));
        }

        @Override
        protected void setDynamicValidations() {
            if (ShortProcessQuestion1Form.this.docType == IdentityDocumentType.DNI) {
                documentNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
            } else if (ShortProcessQuestion1Form.this.docType == IdentityDocumentType.CE) {
                documentNumber.update(ValidatorUtil.DOC_NUMBER_CE);
            } else if (ShortProcessQuestion1Form.this.docType == IdentityDocumentType.CDI) {
                documentNumber.update(ValidatorUtil.DOC_NUMBER_CDI);
            } else if (ShortProcessQuestion1Form.this.docType == IdentityDocumentType.CUIL) {
                documentNumber.update(ValidatorUtil.DOC_NUMBER_CUIL);
            } else if (ShortProcessQuestion1Form.this.docType == IdentityDocumentType.CUIT) {
                documentNumber.update(ValidatorUtil.DOC_NUMBER_CUIT);
            } else if (ShortProcessQuestion1Form.this.docType == IdentityDocumentType.COL_CEDULA_CIUDADANIA) {
                documentNumber.update(ValidatorUtil.DOC_NUMBER_COL_CEDULA);
            } else if (ShortProcessQuestion1Form.this.docType == IdentityDocumentType.COL_CEDULA_EXTRANJERIA) {
                documentNumber.update(ValidatorUtil.DOC_NUMBER_COL_CEDULA_EXT);
            }

            if (ShortProcessQuestion1Form.this.countryId == CountryParam.COUNTRY_PERU) {
                code.setRequired(false);
                phone.setMaxCharacters(10);
            } else if (ShortProcessQuestion1Form.this.countryId == CountryParam.COUNTRY_ARGENTINA) {
                phone.setMaxCharacters(11 - ShortProcessQuestion1Form.this.code.length());
                phone.setMinCharacters(11 - ShortProcessQuestion1Form.this.code.length());
            } else if (ShortProcessQuestion1Form.this.countryId == CountryParam.COUNTRY_COLOMBIA) {
                code.setRequired(false);
                phone.setMaxCharacters(10);
                phone.setMinCharacters(10);
            }
        }


        public void configValidator(Integer countryId) {
            if (countryId == CountryParam.COUNTRY_ARGENTINA) {
                docType.setRequired(false);
                documentNumber.update(ValidatorUtil.DOC_NUMBER_CUIT);
                name.setRequired(false);
                surname.setRequired(false);
                phone.update(ValidatorUtil.CELLPHONE_NUMBER_ARGENTINA.setMinCharacters(9).setMaxCharacters(11));
                code.update(ValidatorUtil.ARGENTINA_PHONE_AREA_CODE);
                birthday.setRequired(true);
            } else if (countryId == CountryParam.COUNTRY_PERU) {
                documentNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
                phone.update(ValidatorUtil.CELLPHONE_NUMBER);
                code.setRequired(false);
                birthday.setRequired(false);
            } else if (countryId == CountryParam.COUNTRY_COLOMBIA) {
                amount.setMaxValue(50 * 1000 * 1000).setMinValue(5 * 100 * 1000);
                documentNumber.update(ValidatorUtil.DOC_NUMBER_COL_CEDULA);
                phone.update(ValidatorUtil.CELLPHONE_NUMBER_COLOMBIA);
                code.setRequired(false);
                birthday.setRequired(false);
                conditionsPolicy.setRequired(true);
                timeLimit.setRequired(true).setMinValue(1).setMaxValue(24);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return ShortProcessQuestion1Form.this;
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
        this.email = email.trim();
    }

    public Boolean getAcceptAgreement() {
        return acceptAgreement;
    }

    public void setAcceptAgreement(Boolean acceptAgreement) {
        this.acceptAgreement = acceptAgreement;
    }

    public Boolean getConditionsPolicy() {
        return conditionsPolicy;
    }

    public void setConditionsPolicy(Boolean conditionsPolicy) {
        this.conditionsPolicy = conditionsPolicy;
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

    public Boolean getPep() {
        return pep;
    }

    public void setPep(Boolean pep) {
        this.pep = pep;
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

    public String getCategoryUrl() {
        return categoryUrl;
    }

    public void setCategoryUrl(String categoryUrl) {
        this.categoryUrl = categoryUrl;
    }

    public String getExternalParams() {
        return externalParams;
    }

    public void setExternalParams(String externalParams) {
        this.externalParams = externalParams;
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

    public String getReferrerPersonId() {
        return referrerPersonId;
    }

    public void setReferrerPersonId(String referrerPersonId) {
        this.referrerPersonId = referrerPersonId;
    }

    public String getABTesting() {
        return ABTesting;
    }

    public void setABTesting(String ABTesting) {
        this.ABTesting = ABTesting;
    }

    public Boolean getAcceptAgreement2() {
        return acceptAgreement2;
    }

    public void setAcceptAgreement2(Boolean acceptAgreement2) {
        this.acceptAgreement2 = acceptAgreement2;
    }
}
