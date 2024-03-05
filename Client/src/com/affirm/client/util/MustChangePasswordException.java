package com.affirm.client.util;

import org.apache.shiro.authc.AuthenticationException;

public class MustChangePasswordException extends AuthenticationException {

    private String message;

    public MustChangePasswordException() {
        this.message = "Debe de actualizar su contraseña.";
    }

    public MustChangePasswordException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
