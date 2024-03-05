package com.affirm.apirest.model;

import com.affirm.common.model.catalog.AreaType;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.util.*;

import java.io.Serializable;

public class GenerateLoanApiRequest extends FormGeneric {

    public static final int AZTECA_CREDITO_CONSUMO = 1;
    public static final int AZTECA_CUENTA_AHORRO_META = 2;
    public static final int AZTECA_CUENTA_AHORRO_DIA = 3;

    private Integer documentType;
    private String documentNumber;
    private String cellphone;
    private String email;
    private Integer entityProductId;
    private String token;
    private Integer apiRestUserId;
    private String trackingId;
    private String webhookUrl;
    private Integer productCategoryId;

    public GenerateLoanApiRequest() {
        this.setValidator(new Validator());
    }

    public class Validator extends FormValidator implements Serializable {

        public IntegerFieldValidator documentType;
        public StringFieldValidator documentNumber;
        public StringFieldValidator cellphone;
        public StringFieldValidator email;
        public IntegerFieldValidator entityProductId;
        public StringFieldValidator token;
        public IntegerFieldValidator apiRestUserId;
        public StringFieldValidator trackingId;
        public StringFieldValidator webhookUrl;
        public IntegerFieldValidator productCategoryId;

        public Validator() {
            addValidator(documentType = new IntegerFieldValidator(ValidatorUtil.DOC_TYPE_ID).setRequired(true));
            addValidator(documentNumber = new StringFieldValidator(ValidatorUtil.DOC_NUMBER_DNI).setRequired(true));
            addValidator(email = new StringFieldValidator(ValidatorUtil.EMAIL).setRequired(true));
            addValidator(cellphone = new StringFieldValidator(ValidatorUtil.PHONE_OR_CELLPHONE).setRequired(true));
            addValidator(entityProductId = new IntegerFieldValidator().setRequired(true));
            addValidator(productCategoryId = new IntegerFieldValidator().setRequired(false));
            addValidator(token = new StringFieldValidator().setValidRegex(null).setRequired(true));
            addValidator(apiRestUserId = new IntegerFieldValidator().setRequired(false));
            addValidator(trackingId = new StringFieldValidator().setRequired(false).setValidRegex(null));
            addValidator(webhookUrl = new StringFieldValidator().setRequired(false).setValidRegex(null));
        }

        @Override
        protected void setDynamicValidations() {
            if(documentType != null && documentType.equals(IdentityDocumentType.CE)){
                documentNumber.setMaxCharacters(9).setValidRegex(StringFieldValidator.REGEXP_ALPHANUMERIC);
            }
        }

        @Override
        protected Object getSubclass() {
            return this;
        }

        @Override
        protected Object getFormClass() {
            return GenerateLoanApiRequest.this;
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

    public Integer getEntityProductId() {
        return entityProductId;
    }

    public void setEntityProductId(Integer entityProductId) {
        this.entityProductId = entityProductId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getApiRestUserId() {
        return apiRestUserId;
    }

    public void setApiRestUserId(Integer apiRestUserId) {
        this.apiRestUserId = apiRestUserId;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public Integer getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(Integer productCategoryId) {
        this.productCategoryId = productCategoryId;
    }
}
