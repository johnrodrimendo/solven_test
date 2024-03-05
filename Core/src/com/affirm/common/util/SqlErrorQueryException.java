package com.affirm.common.util;

public class SqlErrorQueryException extends RuntimeException {

    public SqlErrorQueryException(String message, Throwable cause) {
        super(message, cause);
    }

}
