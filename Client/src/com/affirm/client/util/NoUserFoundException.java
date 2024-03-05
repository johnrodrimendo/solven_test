package com.affirm.client.util;

import org.apache.shiro.authc.AuthenticationException;

/**
 * Created by john on 19/12/16.
 */
public class NoUserFoundException extends AuthenticationException {
    private String message;

    public NoUserFoundException() {
        this.message = "No existe usuario asociado a esta cuenta.";
    }

    public NoUserFoundException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
