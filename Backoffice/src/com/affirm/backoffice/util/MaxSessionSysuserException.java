package com.affirm.backoffice.util;

import org.apache.shiro.authc.AuthenticationException;

public class MaxSessionSysuserException extends AuthenticationException {
    private String message;

    public MaxSessionSysuserException() {
        this.message = "No se puede iniciar sesión. Ha superado el número máximo de sesiones activas";
    }

    public MaxSessionSysuserException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
