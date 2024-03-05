package com.affirm.backoffice.util;

import org.apache.shiro.authc.AuthenticationException;

public class InactiveSysuserException extends AuthenticationException {
    private String message;

    public InactiveSysuserException() {
        this.message = "Su cuenta se encuentra bloqueada. Por favor contacte a su administrador.";
    }

    public InactiveSysuserException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}