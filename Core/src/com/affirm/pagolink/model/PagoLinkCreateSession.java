package com.affirm.pagolink.model;

import org.json.JSONObject;

public class PagoLinkCreateSession {

    private Double amount;
    private String channel;
    private Double recurrenceMaxAmount;
    private PagoLinkAntifraud antifraud;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Double getRecurrenceMaxAmount() {
        return recurrenceMaxAmount;
    }

    public void setRecurrenceMaxAmount(Double recurrenceMaxAmount) {
        this.recurrenceMaxAmount = recurrenceMaxAmount;
    }

    public PagoLinkAntifraud getAntifraud() {
        return antifraud;
    }

    public void setAntifraud(PagoLinkAntifraud antifraud) {
        this.antifraud = antifraud;
    }

    public static class PagoLinkAntifraud{

        private String clientIp;
        private PagoLinkMerchantDefineData merchantDefineData;

        public String getClientIp() {
            return clientIp;
        }

        public void setClientIp(String clientIp) {
            this.clientIp = clientIp;
        }

        public PagoLinkMerchantDefineData getMerchantDefineData() {
            return merchantDefineData;
        }

        public void setMerchantDefineData(PagoLinkMerchantDefineData merchantDefineData) {
            this.merchantDefineData = merchantDefineData;
        }
    }

    public static class PagoLinkMerchantDefineData{
        private String MDD4;
        private String MDD21;
        private String MDD32;
        private String MDD75;
        private String MDD77;

        public String getMDD4() {
            return MDD4;
        }

        public void setMDD4(String MDD4) {
            this.MDD4 = MDD4;
        }

        public String getMDD21() {
            return MDD21;
        }

        public void setMDD21(String MDD21) {
            this.MDD21 = MDD21;
        }

        public String getMDD32() {
            return MDD32;
        }

        public void setMDD32(String MDD32) {
            this.MDD32 = MDD32;
        }

        public String getMDD75() {
            return MDD75;
        }

        public void setMDD75(String MDD75) {
            this.MDD75 = MDD75;
        }

        public String getMDD77() {
            return MDD77;
        }

        public void setMDD77(String MDD77) {
            this.MDD77 = MDD77;
        }
    }


}

