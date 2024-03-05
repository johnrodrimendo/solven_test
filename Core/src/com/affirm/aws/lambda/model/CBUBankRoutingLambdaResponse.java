package com.affirm.aws.lambda.model;

public class CBUBankRoutingLambdaResponse {
    private String scheme;
    private String address;
    private String code;

    public CBUBankRoutingLambdaResponse() {
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
