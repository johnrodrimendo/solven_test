package com.affirm.api.endpoint.soap.errors;

public class CreditoFault {

    private String faultCode;
    private String faultString;

    public String getFaultCode() {
        return faultCode;
    }
    public void setFaultCode(String faultCode) {
        this.faultCode = faultCode;
    }
    public String getFaultString() {
        return faultString;
    }
    public void setFaultString(String faultString) {
        this.faultString = faultString;
    }

}