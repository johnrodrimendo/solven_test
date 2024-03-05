package com.affirm.common.model.aspect;

import com.affirm.common.model.annotation.OneTimeTokenAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.WebApplication;
import com.affirm.common.model.transactional.UserEntity;
import com.affirm.common.service.CookieManagementService;
import com.affirm.common.service.CountryContextService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.security.model.CSRFInterceptor;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by john on 18/08/17.
 * <p>
 * This interceptor has 2 functinos:
 * - Set the request param COUNTRY_PARAM by domain
 * - Set the default locale by country
 */
@Aspect
@Component
public class OneTimeTokenAspect extends HandlerInterceptorAdapter {

    private static Logger logger = Logger.getLogger(OneTimeTokenAspect.class);

    private final String HEADER_NAME = "7_7";
    private final String SESSION_KEY = "oneTimeTokens";

    @Autowired
    private CookieManagementService cookieManagementService;

    @Pointcut("execution(* com.affirm.landing.controller.*.*(..))")
    public void pointCutClient() {
    }

    @Around("(pointCutClient()) && @annotation(annotation)")
    public Object permissionAction(ProceedingJoinPoint jp, OneTimeTokenAnnotation annotation) throws Throwable {

        // get the parameters
        HttpServletRequest request = null;
        for (Object arg : jp.getArgs()) {
            if (arg instanceof HttpServletRequest) {
                request = (HttpServletRequest) arg;
            }
        }

        // If its a POST and in the whitelist, then validate that the token is in the list in the session
        if (request.getMethod().equalsIgnoreCase("POST")) {

            // get the parameters
            for (Object arg : jp.getArgs()) {
                if (arg instanceof HttpServletRequest) {
                    request = (HttpServletRequest) arg;
                }
            }

            Boolean forceValidation = false;

            //SKIP VALIDATION IF THE REQUEST WAS MADE THOUGH IFRAME AND THE DEVICE ITS A IPHONE
            String userAgent = request.getHeader("User-Agent");
            String iFrameHeaderValue =  request.getHeader(CSRFInterceptor.IFRAME_HEADER_VALIDATOR);
            if(userAgent != null && !userAgent.isEmpty() && iFrameHeaderValue != null && !iFrameHeaderValue.isEmpty()){
                if(iFrameHeaderValue.equalsIgnoreCase(CSRFInterceptor.IFRAME_HEADER_ENABLE_VALUE)){
                    userAgent = userAgent.toLowerCase();
                    String finalUserAgent = userAgent;
                    if(CSRFInterceptor.USERS_AGENTS_TO_SKIP_VALIDATION.stream().anyMatch(e -> finalUserAgent.contains(e.toLowerCase()))) forceValidation = true;
                }
            }

            // validate the token
            String oneTimeToken = null;
            if (request.getCookies() != null) {
                for (int i = 0; i < request.getCookies().length; i++) {
                    Cookie cookie = request.getCookies()[i];
                    if (cookie.getName().equalsIgnoreCase(HEADER_NAME))
                        oneTimeToken = cookie.getValue();
                }
            }


            // Validate that hte token is valid
            if (forceValidation || isValidToken(request, oneTimeToken)) {
                // Go to the method
                Object methodRepsonse =  jp.proceed();
                // If the request is with httpcode ok, then delete the token used
                if(methodRepsonse instanceof ResponseEntity){
                    ResponseEntity methodResponseEntity = (ResponseEntity) methodRepsonse;
                    if(methodResponseEntity.getStatusCode().is2xxSuccessful() ||methodResponseEntity.getStatusCode().is3xxRedirection()) {
                        removeToken(request, oneTimeToken);
                    }
                }
                return methodRepsonse;
            } else {
                return AjaxResponse.errorForbidden();
            }
        }

        // If its a GET and in the whitelist, then after calling the method, set the cookie
        if (request.getMethod().equalsIgnoreCase("GET")) {

            // got to the method
            Object methodResponse = jp.proceed();

            // get the parameters
            HttpServletResponse response = null;
            ModelMap modelMap = null;
            for (Object arg : jp.getArgs()) {
                if (arg instanceof HttpServletRequest) {
                    request = (HttpServletRequest) arg;
                }
                if (arg instanceof HttpServletResponse) {
                    response = (HttpServletResponse) arg;
                }
                if (arg instanceof ModelMap) {
                    modelMap = (ModelMap) arg;
                }
            }

            // Send the token as a cookie
            String token = RandomStringUtils.random(24, true, true);
            // Add the token to the request
            modelMap.addAttribute("oneTimeToken", token);
            // Add the token as a cookie
            Cookie oneTimeTokenCookie = new Cookie(HEADER_NAME, token);
            oneTimeTokenCookie.setVersion(1);
            oneTimeTokenCookie.setHttpOnly(false);
            oneTimeTokenCookie.setPath(request.getContextPath().equals("") ? "/" : request.getContextPath());
//            if (!com.affirm.system.configuration.Configuration.hostEnvIsLocal()) {
                oneTimeTokenCookie.setSecure(true);
//            }
            cookieManagementService.createSetCookieHeader(response, oneTimeTokenCookie, "");
            // Add the token to the session
            addToken(request, token);

            return methodResponse;
        }

        return jp.proceed();
    }

    private boolean isValidToken(HttpServletRequest request, String token){
        List<String> oneTimeTokens = new ArrayList<>();
        if (request.getSession().getAttribute(SESSION_KEY) != null) {
            oneTimeTokens = (List<String>) request.getSession().getAttribute(SESSION_KEY);
        }
        return oneTimeTokens.stream().anyMatch(s -> s.equalsIgnoreCase(token));
    }

    private void removeToken(HttpServletRequest request, String token){
        List<String> oneTimeTokens = new ArrayList<>();
        if (request.getSession().getAttribute(SESSION_KEY) != null) {
            oneTimeTokens = (List<String>) request.getSession().getAttribute(SESSION_KEY);
        }
        oneTimeTokens.removeIf(s -> s.equals(token));
        request.getSession().setAttribute(SESSION_KEY, oneTimeTokens);
    }

    private void addToken(HttpServletRequest request, String token) {
        List<String> oneTimeTokens = new ArrayList<>();
        if (request.getSession().getAttribute(SESSION_KEY) != null) {
            oneTimeTokens = (List<String>) request.getSession().getAttribute(SESSION_KEY);
        }
        oneTimeTokens.add(token);
        request.getSession().setAttribute(SESSION_KEY, oneTimeTokens);
    }
}
