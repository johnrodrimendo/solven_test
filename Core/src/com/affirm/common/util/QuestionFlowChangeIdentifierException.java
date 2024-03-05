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
public class QuestionFlowChangeIdentifierException extends RuntimeException {

    private Integer newIdentifier;

    public QuestionFlowChangeIdentifierException(Integer newIdentifier) {
        super();
        this.newIdentifier = newIdentifier;
    }

    public Integer getNewIdentifier() {
        return newIdentifier;
    }

    public void setNewIdentifier(Integer newIdentifier) {
        this.newIdentifier = newIdentifier;
    }
}
