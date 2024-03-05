package com.affirm.common.model.pagoefectivo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PagoEfectivoWebhookBody {
    private String eventType;
    private String operationNumber;
    private PagoEfectivoWebhookData data;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getOperationNumber() {
        return operationNumber;
    }

    public void setOperationNumber(String operationNumber) {
        this.operationNumber = operationNumber;
    }

    public PagoEfectivoWebhookData getData() {
        return data;
    }

    public void setData(PagoEfectivoWebhookData data) {
        this.data = data;
    }

    public static class PagoEfectivoWebhookData {
        private String cip;
        private String currency;
        private Double amount;
        private String transactionCode;
        private String paymentDate;


        public Date getDateExpiryAsDate() throws Exception {
            if (paymentDate == null)
                return null;
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(paymentDate);
        }

        public PagoEfectivoWebhookData() {
            super();
        }

        public String getCip() {
            return cip;
        }

        public void setCip(String cip) {
            this.cip = cip;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public String getTransactionCode() {
            return transactionCode;
        }

        public void setTransactionCode(String transactionCode) {
            this.transactionCode = transactionCode;
        }

        public String getPaymentDate() {
            return paymentDate;
        }

        public void setPaymentDate(String paymentDate) {
            this.paymentDate = paymentDate;
        }
    }
}
