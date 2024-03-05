/**
 *
 */
package com.affirm.common.util;

/**
 * @author jrodriguez
 *         <p>
 *         Exception that contains the message key in the message.properties to
 *         show to the client
 */
public class SqlErrorMessageException extends RuntimeException {

    private String messageKey;
    private String messageBody;

    public SqlErrorMessageException(String messageKey, String messageBody) {
        this.messageKey = messageKey;
        this.messageBody = messageBody;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getMessageBody() {
        return messageBody;
    }
}
