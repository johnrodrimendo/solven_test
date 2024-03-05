/**
 *
 */
package com.affirm.common.util;

/**
 * @author jrodriguez
 * <p>
 * Exception that contains the message key in the message.properties to
 * show to the client
 */
public class FormValidationException extends RuntimeException {

    public FormValidationException(String message) {
        super(message);
    }
}
