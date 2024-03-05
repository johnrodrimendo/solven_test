package com.affirm.aws.lambda.model;

public class CBUAccountRoutingLambdaResponse {
    private String scheme;
    private String address;

    public CBUAccountRoutingLambdaResponse() {
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
}
