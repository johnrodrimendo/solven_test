package com.affirm.common.model.form;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.*;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public class Question1Form extends FormGeneric implements Serializable {

    private Integer docType;
    private String docNumber;
    private String email;
    private Boolean acceptAgreement;
    private String name;
    private String surName;
    private Boolean pep;

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

    public Question1Form() {
        this.setValidator(new Question1Form.Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator docType;
        public StringFieldValidator docNumber;
        public StringFieldValidator email;
        public BooleanFieldValidator acceptAgreement;
        public StringFieldValidator name;
        public StringFieldValidator surName;
        public BooleanFieldValidator pep;

        public Validator() {
            addValidator(docType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID));
            addValidator(docNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI).setFieldName("Numero de documento"));
            addValidator(pep = new BooleanFieldValidator(ValidatorUtil.PEP).setRequired(false));
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL).setFieldName("Correo electronico"));
            addValidator(acceptAgreement = new BooleanFieldValidator().setRequired(false).setRequiredErrorMsg("validation.checkbix.termsConditions"));
            addValidator(name = new StringFieldValidator(ValidatorUtil.NAME).setRequired(false).setFieldName("Nombre"));
            addValidator(surName = new StringFieldValidator(ValidatorUtil.FIRST_SURNAME).setRequired(false).setFieldName("Apellido"));
        }

        @Override
        protected void setDynamicValidations() {
            if (Question1Form.this.docType == IdentityDocumentType.DNI) {
                docNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
            } else if (Question1Form.this.docType == IdentityDocumentType.CE) {
                docNumber.update(ValidatorUtil.DOC_NUMBER_CE);
            } else if (Question1Form.this.docType == IdentityDocumentType.CDI) {
                docNumber.update(ValidatorUtil.DOC_NUMBER_CDI_PERSONAL);
            } else if (Question1Form.this.docType == IdentityDocumentType.CUIL) {
                docNumber.update(ValidatorUtil.DOC_NUMBER_CUIL_PERSONAL);
            } else if (Question1Form.this.docType == IdentityDocumentType.CUIT) {
                docNumber.update(ValidatorUtil.DOC_NUMBER_CUIT_PERSONAL);
            }
        }


        public void configValidator(Integer countryId) {
            if (countryId == CountryParam.COUNTRY_ARGENTINA) {
                docType.setRequired(false);
                docNumber.update(ValidatorUtil.DOC_NUMBER_CUIT_PERSONAL);
                acceptAgreement.setRequired(true);
                name.setRequired(true);
                surName.setRequired(true);
            } else if (countryId == CountryParam.COUNTRY_PERU) {
                docNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
                acceptAgreement.setRequired(true);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return Question1Form.this;
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
        this.email = email.trim();
    }

    public Boolean getAcceptAgreement() {
        return acceptAgreement;
    }

    public void setAcceptAgreement(Boolean acceptAgreement) {
        this.acceptAgreement = acceptAgreement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
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
}