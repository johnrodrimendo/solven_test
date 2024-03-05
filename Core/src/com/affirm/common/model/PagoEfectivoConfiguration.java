package com.affirm.common.model;

public class PagoEfectivoConfiguration {

    private String idService;
    private String accessKey;
    private String secretKey;
    private String urlGenerateAuthToken;
    private String urlGenerateCIP;
    private String urlStatusCIP;
    private String currency;

    public String getIdService() {
        return idService;
    }

    public void setIdService(String idService) {
        this.idService = idService;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getUrlGenerateAuthToken() {
        return urlGenerateAuthToken;
    }

    public void setUrlGenerateAuthToken(String urlGenerateAuthToken) {
        this.urlGenerateAuthToken = urlGenerateAuthToken;
    }

    public String getUrlGenerateCIP() {
        return urlGenerateCIP;
    }

    public void setUrlGenerateCIP(String urlGenerateCIP) {
        this.urlGenerateCIP = urlGenerateCIP;
    }

    public String getUrlStatusCIP() {
        return urlStatusCIP;
    }

    public void setUrlStatusCIP(String urlStatusCIP) {
        this.urlStatusCIP = urlStatusCIP;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
