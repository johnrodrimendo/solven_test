package com.affirm.client.util;

import org.apache.shiro.authc.AuthenticationException;

public class InvalidPasswordException extends AuthenticationException {
    private String message;

    public InvalidPasswordException() {
        this.message = "Contraseña inválida, intente nuevamente.";
    }

    public InvalidPasswordException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

