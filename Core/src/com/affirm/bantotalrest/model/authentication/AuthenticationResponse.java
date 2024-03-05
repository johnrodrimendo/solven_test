package com.affirm.bantotalrest.model.authentication;

import com.affirm.bantotalrest.model.common.BTResponseData;
import com.affirm.bantotalrest.model.common.BtOutRequest;

public class AuthenticationResponse extends BTResponseData {

    private String SessionToken;

    public String getSessionToken() {
        return SessionToken;
    }

    public void setSessionToken(String sessionToken) {
        SessionToken = sessionToken;
    }
}
