package com.affirm.efl.model;

/**
 * Created by dev5 on 06/07/17.
 */
public class EFLTokens {

    private String authToken;
    private String reqToken;

    @Override
    public String toString() {
        return "EFLTokens{" +
                "authToken='" + authToken + '\'' +
                ", reqToken='" + reqToken + '\'' +
                '}';
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getReqToken() {
        return reqToken;
    }

    public void setReqToken(String reqToken) {
        this.reqToken = reqToken;
    }
}
