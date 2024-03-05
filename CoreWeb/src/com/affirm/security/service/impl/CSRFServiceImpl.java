package com.affirm.security.service.impl;

import com.affirm.common.service.CookieManagementService;
import com.affirm.common.service.SessionService;
import com.affirm.security.service.CSRFService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Created by jarmando on 14/11/16.
 */
@Service
public class CSRFServiceImpl implements CSRFService {


    @Autowired
    private CookieManagementService cookieManagementService;

    @Autowired
    private SessionService sessionService;

    @Override
    public String generateNewTokenInSessionIfNull() {
        synchronized (sessionService.getMutex()) {
            String token = getTokenFromSession();
            if (token == null) {
                token = generateNewTokenInSession();
            }
            return token;
        }
    }

    @Override
    public String generateNewTokenInSession() {
        synchronized (sessionService.getMutex()) {
            String token = generateUUID().toString();
            setTokenToSession(token);
            return token;
        }
    }


    @Override
    public String setTokenToClientFromSession(HttpServletRequest request, HttpServletResponse response) {
        String token = generateNewTokenInSessionIfNull();
        Cookie cookie = newCSRFCookie(token, request);
//        response.addCookie(cookie);
        cookieManagementService.createSetCookieHeader(response,cookie,"None");
        return token;
    }

    @Override
    public boolean validateToken(HttpServletRequest request) {
        synchronized (sessionService.getMutex()) {
            String sessionToken = getTokenFromSession();
            String headerToken = request.getHeader(CSRF_TOKEN_HEADER_KEY);

            if ((sessionToken != null && headerToken != null && sessionToken.equals(headerToken))) {
                return true;
            } else {
                // Save the CSRF mismatch for future log
                request.getSession().setAttribute(CSRF_LAST_MISMATCH_SERVER, sessionToken);
                request.getSession().setAttribute(CSRF_LAST_MISMATCH_CLIENT, headerToken);
                return false;
            }
        }
    }

    private String getTokenFromSession() {
        return (String) SecurityUtils.getSubject().getSession().getAttribute(CSRF_TOKEN_FOR_SESSION_KEY);
    }

    private void setTokenToSession(String token) {
        SecurityUtils.getSubject().getSession().setAttribute(CSRF_TOKEN_FOR_SESSION_KEY, token);
    }

    private Cookie newCSRFCookie(String token, HttpServletRequest request) {
        Cookie cookie = new Cookie(CSRF_TOKEN_FOR_CLIENT_KEY, token);
        cookie.setVersion(1);
        cookie.setHttpOnly(false);
        cookie.setPath(request.getContextPath().equals("") ? "/" : request.getContextPath());
        cookie.setComment(";SameSite=NONE");
//        cookie.setMaxAge(600);
        //WHEN WE HAVE HTTPS AND WE ARE NOT IN LOCAL, SET SECURE TRUE
        if (com.affirm.system.configuration.Configuration.HTTPS_SSL_IMPLEMENTED) {
            if (com.affirm.system.configuration.Configuration.hostEnvIsProduction() ||
                    (com.affirm.system.configuration.Configuration.hostEnvIsStage() && !com.affirm.system.configuration.Configuration.REDUCE_SECURITY_NOT_PROD)) {
                cookie.setSecure(true);
            }
        }
        return cookie;
    }

    private synchronized String generateUUID() {
        synchronized (sessionService.getMutex()) {
            String token;
            token = UUID.randomUUID().toString();
            return token;
        }
    }

}
