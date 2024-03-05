package com.affirm.common.model.pagoefectivo;

public class PagoEfectivoCreateAuthorizationRequest {

    private String accessKey;
    private String idService;
    private String dateRequest;
    private String hashString;

    public PagoEfectivoCreateAuthorizationRequest() {
    }

    public PagoEfectivoCreateAuthorizationRequest(String accessKey, String idService, String dateRequest, String hashString) {
        super();
        this.accessKey = accessKey;
        this.idService = idService;
        this.dateRequest = dateRequest;
        this.hashString = hashString;
    }

    public String getAccessKey() {
        return accessKey;
    }
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }
    public String getIdService() {
        return idService;
    }
    public void setIdService(String idService) {
        this.idService = idService;
    }
    public String getDateRequest() {
        return dateRequest;
    }
    public void setDateRequest(String dateRequest) {
        this.dateRequest = dateRequest;
    }
    public String getHashString() {
        return hashString;
    }
    public void setHashString(String hashString) {
        this.hashString = hashString;
    }

}
