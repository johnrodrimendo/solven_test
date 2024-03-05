package com.affirm.common.service;

/**
 * Created by john on 21/10/16.
 */
public interface SmsService {
    void sendSms(String to, String message, Integer interactionId) throws Exception;
}
