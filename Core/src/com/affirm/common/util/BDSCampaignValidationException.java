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
public class BDSCampaignValidationException extends RuntimeException {

    public BDSCampaignValidationException(String message) {
        super(message);
    }
}
