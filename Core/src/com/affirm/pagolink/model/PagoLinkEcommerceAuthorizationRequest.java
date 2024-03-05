package com.affirm.pagolink.model;

public class PagoLinkEcommerceAuthorizationRequest {

    private String channel = "Web";
    private Boolean countable = true;
    private String captureType = "manual";
    private PagoLinkEcommerceAuthorizationOrder order = new PagoLinkEcommerceAuthorizationOrder();
    private PagoLinkEcommerceAuthorizationCustomer cardHolder = new PagoLinkEcommerceAuthorizationCustomer();

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Boolean getCountable() {
        return countable;
    }

    public void setCountable(Boolean countable) {
        this.countable = countable;
    }

    public String getCaptureType() {
        return captureType;
    }

    public void setCaptureType(String captureType) {
        this.captureType = captureType;
    }

    public PagoLinkEcommerceAuthorizationOrder getOrder() {
        return order;
    }

    public void setOrder(PagoLinkEcommerceAuthorizationOrder order) {
        this.order = order;
    }

    public PagoLinkEcommerceAuthorizationCustomer getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(PagoLinkEcommerceAuthorizationCustomer cardHolder) {
        this.cardHolder = cardHolder;
    }

    public static class PagoLinkEcommerceAuthorizationCustomer{
        private String documentNumber;
        private String documentType;

        public String getDocumentNumber() {
            return documentNumber;
        }

        public void setDocumentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
        }

        public String getDocumentType() {
            return documentType;
        }

        public void setDocumentType(String documentType) {
            this.documentType = documentType;
        }

    }

    public static class PagoLinkEcommerceAuthorizationOrder{
        private Double amount;
        private String currency;
        private String purchaseNumber;
        private String tokenId;

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getPurchaseNumber() {
            return purchaseNumber;
        }

        public void setPurchaseNumber(String purchaseNumber) {
            this.purchaseNumber = purchaseNumber;
        }

        public String getTokenId() {
            return tokenId;
        }

        public void setTokenId(String tokenId) {
            this.tokenId = tokenId;
        }
    }


}
