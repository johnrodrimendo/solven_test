package com.affirm.pagolink.model;

import java.math.BigInteger;

public class PagoLinkEcommerceAuthorizationResponse {

    private PagoLinkEcommerceHeaderResponse header;
    private PagoLinkEcommerceOrderResponse order;
    private PagoLinkEcommerceDataMapResponse dataMap;
    private PagoLinkEcommerceDataMapResponse data;
    private Integer errorCode;
    private String errorMessage;

    public PagoLinkEcommerceHeaderResponse getHeader() {
        return header;
    }

    public void setHeader(PagoLinkEcommerceHeaderResponse header) {
        this.header = header;
    }

    public PagoLinkEcommerceOrderResponse getOrder() {
        return order;
    }

    public void setOrder(PagoLinkEcommerceOrderResponse order) {
        this.order = order;
    }

    public PagoLinkEcommerceDataMapResponse getDataMap() {
        return dataMap;
    }

    public void setDataMap(PagoLinkEcommerceDataMapResponse dataMap) {
        this.dataMap = dataMap;
    }

    public PagoLinkEcommerceDataMapResponse getData() {
        return data;
    }

    public void setData(PagoLinkEcommerceDataMapResponse data) {
        this.data = data;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static class PagoLinkEcommerceHeaderResponse{
        private String ecoreTransactionUUID;
        private Long ecoreTransactionDate;
        private Integer millis;

        public String getEcoreTransactionUUID() {
            return ecoreTransactionUUID;
        }

        public void setEcoreTransactionUUID(String ecoreTransactionUUID) {
            this.ecoreTransactionUUID = ecoreTransactionUUID;
        }

        public Long getEcoreTransactionDate() {
            return ecoreTransactionDate;
        }

        public void setEcoreTransactionDate(Long ecoreTransactionDate) {
            this.ecoreTransactionDate = ecoreTransactionDate;
        }

        public Integer getMillis() {
            return millis;
        }

        public void setMillis(Integer millis) {
            this.millis = millis;
        }
    }

    public static class PagoLinkEcommerceOrderResponse{
        private String tokenId;
        private String purchaseNumber;
        private String productId;
        private String amount;
        private String currency;
        private String authorize;
        private String authorizationCode;
        private String traceNumber;
        private String transactionDate;
        private String transactionId;

        public String getTokenId() {
            return tokenId;
        }

        public void setTokenId(String tokenId) {
            this.tokenId = tokenId;
        }

        public String getPurchaseNumber() {
            return purchaseNumber;
        }

        public void setPurchaseNumber(String purchaseNumber) {
            this.purchaseNumber = purchaseNumber;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getAuthorize() {
            return authorize;
        }

        public void setAuthorize(String authorize) {
            this.authorize = authorize;
        }

        public String getAuthorizationCode() {
            return authorizationCode;
        }

        public void setAuthorizationCode(String authorizationCode) {
            this.authorizationCode = authorizationCode;
        }

        public String getTraceNumber() {
            return traceNumber;
        }

        public void setTraceNumber(String traceNumber) {
            this.traceNumber = traceNumber;
        }

        public String getTransactionDate() {
            return transactionDate;
        }

        public void setTransactionDate(String transactionDate) {
            this.transactionDate = transactionDate;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }
    }

    public static class PagoLinkEcommerceDataMapResponse{

        private String CURRENCY;
        private String TRANSACTION_DATE;
        private String TERMINAL;
        private String ACTION_CODE;
        private String TRACE_NUMBER;
        private String ECI_DESCRIPTION;
        private String CARD_TYPE;
        private String ECI;
        private String SIGNATURE;
        private String CARD;
        private String BRAND;
        private String MERCHANT;
        private String STATUS;
        private String ADQUIRENTE;
        private String ACTION_DESCRIPTION;
        private String ID_UNICO;
        private String AMOUNT;
        private String PROCESS_CODE;
        private String TRANSACTION_ID;
        private String BRAND_ACTION_CODE;
        private String BRAND_HOST_DATE_TIME;
        private String BRAND_HOST_ID;
        private String AUTHORIZATION_CODE;
        private String ID_RESOLUTOR;
        private String BRAND_NAME;
        private String RECURRENCE_STATUS;

        public String getCURRENCY() {
            return CURRENCY;
        }

        public void setCURRENCY(String CURRENCY) {
            this.CURRENCY = CURRENCY;
        }

        public String getTRANSACTION_DATE() {
            return TRANSACTION_DATE;
        }

        public void setTRANSACTION_DATE(String TRANSACTION_DATE) {
            this.TRANSACTION_DATE = TRANSACTION_DATE;
        }

        public String getTERMINAL() {
            return TERMINAL;
        }

        public void setTERMINAL(String TERMINAL) {
            this.TERMINAL = TERMINAL;
        }

        public String getACTION_CODE() {
            return ACTION_CODE;
        }

        public void setACTION_CODE(String ACTION_CODE) {
            this.ACTION_CODE = ACTION_CODE;
        }

        public String getTRACE_NUMBER() {
            return TRACE_NUMBER;
        }

        public void setTRACE_NUMBER(String TRACE_NUMBER) {
            this.TRACE_NUMBER = TRACE_NUMBER;
        }

        public String getECI_DESCRIPTION() {
            return ECI_DESCRIPTION;
        }

        public void setECI_DESCRIPTION(String ECI_DESCRIPTION) {
            this.ECI_DESCRIPTION = ECI_DESCRIPTION;
        }

        public String getECI() {
            return ECI;
        }

        public void setECI(String ECI) {
            this.ECI = ECI;
        }

        public String getBRAND() {
            return BRAND;
        }

        public void setBRAND(String BRAND) {
            this.BRAND = BRAND;
        }

        public String getCARD() {
            return CARD;
        }

        public void setCARD(String CARD) {
            this.CARD = CARD;
        }

        public String getMERCHANT() {
            return MERCHANT;
        }

        public void setMERCHANT(String MERCHANT) {
            this.MERCHANT = MERCHANT;
        }

        public String getSTATUS() {
            return STATUS;
        }

        public void setSTATUS(String STATUS) {
            this.STATUS = STATUS;
        }

        public String getADQUIRENTE() {
            return ADQUIRENTE;
        }

        public void setADQUIRENTE(String ADQUIRENTE) {
            this.ADQUIRENTE = ADQUIRENTE;
        }

        public String getACTION_DESCRIPTION() {
            return ACTION_DESCRIPTION;
        }

        public void setACTION_DESCRIPTION(String ACTION_DESCRIPTION) {
            this.ACTION_DESCRIPTION = ACTION_DESCRIPTION;
        }

        public String getID_UNICO() {
            return ID_UNICO;
        }

        public void setID_UNICO(String ID_UNICO) {
            this.ID_UNICO = ID_UNICO;
        }

        public String getAMOUNT() {
            return AMOUNT;
        }

        public void setAMOUNT(String AMOUNT) {
            this.AMOUNT = AMOUNT;
        }

        public String getPROCESS_CODE() {
            return PROCESS_CODE;
        }

        public void setPROCESS_CODE(String PROCESS_CODE) {
            this.PROCESS_CODE = PROCESS_CODE;
        }

        public String getRECURRENCE_STATUS() {
            return RECURRENCE_STATUS;
        }

        public void setRECURRENCE_STATUS(String RECURRENCE_STATUS) {
            this.RECURRENCE_STATUS = RECURRENCE_STATUS;
        }

        public String getTRANSACTION_ID() {
            return TRANSACTION_ID;
        }

        public void setTRANSACTION_ID(String TRANSACTION_ID) {
            this.TRANSACTION_ID = TRANSACTION_ID;
        }

        public String getAUTHORIZATION_CODE() {
            return AUTHORIZATION_CODE;
        }

        public void setAUTHORIZATION_CODE(String AUTHORIZATION_CODE) {
            this.AUTHORIZATION_CODE = AUTHORIZATION_CODE;
        }

        public String getCARD_TYPE() {
            return CARD_TYPE;
        }

        public void setCARD_TYPE(String CARD_TYPE) {
            this.CARD_TYPE = CARD_TYPE;
        }

        public String getSIGNATURE() {
            return SIGNATURE;
        }

        public void setSIGNATURE(String SIGNATURE) {
            this.SIGNATURE = SIGNATURE;
        }

        public String getBRAND_ACTION_CODE() {
            return BRAND_ACTION_CODE;
        }

        public void setBRAND_ACTION_CODE(String BRAND_ACTION_CODE) {
            this.BRAND_ACTION_CODE = BRAND_ACTION_CODE;
        }

        public String getBRAND_HOST_DATE_TIME() {
            return BRAND_HOST_DATE_TIME;
        }

        public void setBRAND_HOST_DATE_TIME(String BRAND_HOST_DATE_TIME) {
            this.BRAND_HOST_DATE_TIME = BRAND_HOST_DATE_TIME;
        }

        public String getBRAND_HOST_ID() {
            return BRAND_HOST_ID;
        }

        public void setBRAND_HOST_ID(String BRAND_HOST_ID) {
            this.BRAND_HOST_ID = BRAND_HOST_ID;
        }

        public String getID_RESOLUTOR() {
            return ID_RESOLUTOR;
        }

        public void setID_RESOLUTOR(String ID_RESOLUTOR) {
            this.ID_RESOLUTOR = ID_RESOLUTOR;
        }

        public String getBRAND_NAME() {
            return BRAND_NAME;
        }

        public void setBRAND_NAME(String BRAND_NAME) {
            this.BRAND_NAME = BRAND_NAME;
        }

        public String getCustomCurrency(){
            if(getCURRENCY() != null){
                switch (getCURRENCY()){
                    case "0604":
                        return "Soles";
                }
            }
            return null;
        }
    }


}
