package com.affirm.common.service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public interface CookieManagementService {

    String createSetCookieHeader(Cookie cookie, String sameSite);

    void createSetCookieHeader(HttpServletResponse httpServletResponse, Cookie cookie, String sameSite);

}
