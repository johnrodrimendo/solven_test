package com.affirm.common.model.pagoefectivo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PagoEfectivoCreateCIPResponse {
    private int code;
    private String message;
    private PagoEfectivoCIPData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PagoEfectivoCIPData getData() {
        return data;
    }

    public void setData(PagoEfectivoCIPData data) {
        this.data = data;
    }

    public static class PagoEfectivoCIPData {
        private int cip;
        private String currency;
        private Double amount;
        private String transactionCode;
        private String dateExpiry;
        private String cipUrl;

        public Date getDateExpiryAsDate() throws Exception {
            if (dateExpiry == null)
                return null;
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(dateExpiry);
        }

        public PagoEfectivoCIPData() {
            super();
        }

        public int getCip() {
            return cip;
        }

        public void setCip(int cip) {
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

        public String getDateExpiry() {
            return dateExpiry;
        }

        public void setDateExpiry(String dateExpiry) {
            this.dateExpiry = dateExpiry;
        }

        public String getCipUrl() {
            return cipUrl;
        }

        public void setCipUrl(String cipUrl) {
            this.cipUrl = cipUrl;
        }

    }
}
