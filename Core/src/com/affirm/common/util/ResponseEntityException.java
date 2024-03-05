/**
 *
 */
package com.affirm.common.util;

import org.springframework.http.ResponseEntity;

/**
 * @author jrodriguez
 * <p>
 * Exception that contains the message key in the message.properties to
 * show to the client
 */
public class ResponseEntityException extends RuntimeException {

    private ResponseEntity responseEntity;

    public ResponseEntityException(ResponseEntity responseEntity) {
        super();
        this.responseEntity = responseEntity;
    }

    public ResponseEntity getResponseEntity() {
        return responseEntity;
    }

    public void setResponseEntity(ResponseEntity responseEntity) {
        this.responseEntity = responseEntity;
    }
}
