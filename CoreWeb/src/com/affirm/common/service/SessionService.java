package com.affirm.common.service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by jarmando on 26/01/17.
 */
public interface SessionService {

    public static final String SESSION_MUTEX_KEY = "SESSION_MUTEX_KEY";

    Object getMutex();

    String getSessionCountryCode();
}
