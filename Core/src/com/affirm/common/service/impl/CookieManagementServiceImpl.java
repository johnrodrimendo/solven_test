package com.affirm.common.service.impl;

import com.affirm.common.service.ContractScheduleService;
import com.affirm.common.service.CookieManagementService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service("cookieManagementService")
public class CookieManagementServiceImpl implements CookieManagementService {

    public static final String DOMAIN = "Domain";
    public static final String PATH = "Path";
    public static final String SAME_SITE = "SameSite";
    public static final String SECURE = "Secure";
    public static final String HTTP_ONLY = "HttpOnly";
    public static final String MAX_AGE = "Max-Age";
    public static final String EXPIRES = "Expires";
    public static final String NONE = "none";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String SEPERATOR = "; ";
    public static final String ASSIGN = "=";

    @Override
    public String createSetCookieHeader(Cookie cookie, String sameSite) {
        return createSetCookieHeader(cookie.getName(), cookie.getValue(), cookie.getDomain(), cookie.getPath(),
                sameSite, cookie.getSecure(), cookie.isHttpOnly(), cookie.getMaxAge());
    }

    @Override
    public void createSetCookieHeader(HttpServletResponse httpServletResponse, Cookie cookie, String sameSite) {
        String cookieHeader = createSetCookieHeader(cookie.getName(), cookie.getValue(), cookie.getDomain(),
                cookie.getPath(), sameSite, cookie.getSecure(), cookie.isHttpOnly(), cookie.getMaxAge());

        httpServletResponse.addHeader(SET_COOKIE, cookieHeader);
    }

    public static String createSetCookieHeader(String cookieName, String cookieValue, String domain, String path,
                                               String sameSite, boolean isSecure, boolean isHttpOnly, Integer maxAge) {
        String sameSiteValue = StringUtils.isBlank(sameSite) ? NONE : sameSite;
        StringBuilder cookieString = new StringBuilder()
                .append(cookieName).append(ASSIGN).append(cookieValue).append(SEPERATOR)
                .append(PATH).append(ASSIGN).append(path).append(SEPERATOR)
                .append(SAME_SITE).append(ASSIGN).append(sameSiteValue).append(SEPERATOR);
        if (isSecure) {
            cookieString.append(SECURE + SEPERATOR);
        }
        if (isHttpOnly) {
            cookieString.append(HTTP_ONLY + SEPERATOR);
        }
        cookieString.append(maxAge != null && maxAge >0 ? MAX_AGE : EXPIRES)
                .append(ASSIGN)
                .append(maxAge != null && maxAge >0 ? maxAge : "Session")
                .append(SEPERATOR);
        return cookieString.toString();
    }


}

