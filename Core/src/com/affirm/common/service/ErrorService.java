/**
 *
 */
package com.affirm.common.service;

/**
 * @author jrodriguez
 */
public interface ErrorService {
    void onError(Throwable throwable);

    void sendErrorCriticSlack(String message);

    void sendErrorSlack(String message);

    void sendGeneralErrorSlack(String message);
}
