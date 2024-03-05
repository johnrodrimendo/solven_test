package com.affirm.pagolink.model;

import java.math.BigInteger;

public class PagoLinkCreateSessionResponse {

    private String sessionKey;
    private BigInteger expirationTime;

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public BigInteger getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(BigInteger expirationTime) {
        this.expirationTime = expirationTime;
    }
}
