package com.affirm.common.model.form;

import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.*;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public class EfectivoAlToqueForm extends FormGeneric implements Serializable {

    private Integer docType;
    private String documentNumber;
    private String email;
    private Boolean acceptAgreement;
    private Integer productType;
    private Integer activityType;
    private Integer amount;
    private HttpServletRequest request;
    private String phone;
    private Integer agentId;
    private String categoryUrl;
    private String source;
    private String medium;
    private String campaign;
    private String term;
    private String content;
    private String gclid;
    private String gaClientID;

    public EfectivoAlToqueForm() {
        this.setValidator(new EfectivoAlToqueForm.Validator());
    }


    public class Validator extends FormValidator implements Serializable {

        public BooleanFieldValidator acceptAgreement;
        public IntegerFieldValidator activityType;
        public IntegerFieldValidator amount;
        public IntegerFieldValidator productType;
        public IntegerFieldValidator docType;
        public StringFieldValidator documentNumber;
        public StringFieldValidator email;
        public StringFieldValidator phone;

        public Validator() {
            addValidator(amount = new IntegerFieldValidator(ValidatorUtil.SELF_EVALUATION_AMOUNT).setMaxValueErrorMsg("validation.int.maxValueMoney").setMinValueErrorMsg("validation.int.minValueMoney").setFieldName("monto").setRequired(true).setRestricted(true));
            addValidator(productType = new IntegerFieldValidator().setRequired(true).setFieldName("producto"));
            addValidator(activityType = new IntegerFieldValidator().setRequired(true).setFieldName("ingresos"));
            addValidator(docType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID).setRequired(true));
            addValidator(documentNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI).setFieldName("Número de documento").setRequired(true));
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL).setFieldName("Correo electrónico").setRequired(true));
            addValidator(phone = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE).setFieldName("Número de celular").setRequired(true));
            addValidator(acceptAgreement = new BooleanFieldValidator().setRequired(true).setRequiredErrorMsg("validation.checkbix.termsConditions"));

        }

        @Override
        protected void setDynamicValidations() {
            if (EfectivoAlToqueForm.this.docType == IdentityDocumentType.DNI) {
                documentNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
            } else if (EfectivoAlToqueForm.this.docType == IdentityDocumentType.CE) {
                documentNumber.update(ValidatorUtil.DOC_NUMBER_CE);
            }
            phone.setMaxCharacters(10);
        }


        public void configValidator(Integer countryId) {
            documentNumber.update(ValidatorUtil.DOC_NUMBER_DNI);
            phone.update(ValidatorUtil.CELLPHONE_NUMBER);
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return EfectivoAlToqueForm.this;
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

    public Integer getActivityType() {
        return activityType;
    }

    public void setActivityType(Integer activityType) {
        this.activityType = activityType;
    }

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
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
}