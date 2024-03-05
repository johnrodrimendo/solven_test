package com.affirm.backoffice.util;

import org.apache.shiro.authc.AuthenticationException;

public class LoginFailedException extends AuthenticationException{
    private String message;

    public LoginFailedException() {
        this.message = "Usuario o contraseña incorrectos. Intente nuevamente.";
    }

    public LoginFailedException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}