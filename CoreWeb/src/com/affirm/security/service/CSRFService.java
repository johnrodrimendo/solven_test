package com.affirm.security.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jarmando on 14/11/16.
 */
public interface CSRFService {
    String CSRF_TOKEN_FOR_SESSION_KEY = "$JustAKeyForMyCSRFToken$";
    String CSRF_TOKEN_FOR_CLIENT_KEY = "fibonacci13";
    String CSRF_TOKEN_HEADER_KEY = "x-csrf-token";
    String CSRF_LAST_MISMATCH_SERVER = "CSRF_LAST_MISMATCH_SERVER";
    String CSRF_LAST_MISMATCH_CLIENT = "CSRF_LAST_MISMATCH_CLIENT";

    String generateNewTokenInSessionIfNull();

    String generateNewTokenInSession();

    String setTokenToClientFromSession(HttpServletRequest request, HttpServletResponse response);

    boolean validateToken(HttpServletRequest request);

}