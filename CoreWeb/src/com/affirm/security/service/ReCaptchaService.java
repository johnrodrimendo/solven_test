package com.affirm.security.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jarmando on 26/09/16.
 */
public interface ReCaptchaService {
    String CONFIRM_URL = "/confirmHuman";

    /** Blocks the system and will redirect the client to solve the captcha until the destinyUrl is cleaned. */
    void redirectToConfirmHuman(HttpServletRequest request, HttpServletResponse response, String urlOnSuccess) throws Exception;

    /** Asks google if client solved captcha successfully. */
    boolean checkSuccess(HttpServletRequest request, String responseToken) throws Exception;

    boolean checkInvisibleSuccess(HttpServletRequest request, String responseToken) throws Exception;

    /** Defines the destiny when captcha is successfully solved. */
    String destinyUrl(HttpServletRequest request);

    /** Unblocks the mechanism allowing normal navigation. */
    void cleanDestinyUrl();

    /** Unblocks the mechanism allowing normal navigation. */
    boolean isCaptchaUnsolved();

    boolean contactCheckSuccess(HttpServletRequest request);

}
