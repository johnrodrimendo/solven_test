package com.affirm.client.util;

import org.apache.shiro.authc.AuthenticationException;

public class MaxUserSessionReachedException extends AuthenticationException {

    private String message;

    public MaxUserSessionReachedException() {
        this.message = "Se alcanzó la cantidad máxima de sesiones del usuario.";
    }

    public MaxUserSessionReachedException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
