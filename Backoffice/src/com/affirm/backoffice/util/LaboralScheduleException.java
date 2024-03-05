package com.affirm.backoffice.util;

import org.apache.shiro.authc.AuthenticationException;

public class LaboralScheduleException extends AuthenticationException {
    private String message;

    public LaboralScheduleException() {
        this.message = "No se puede iniciar sesión, usted se encuentra fuera de su horario laboral.";
    }

    public LaboralScheduleException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
