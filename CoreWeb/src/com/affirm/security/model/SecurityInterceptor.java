package com.affirm.security.model;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EmailService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.Util;
import com.affirm.security.service.CSRFService;
import com.affirm.security.service.ReCaptchaService;
import com.affirm.security.service.SecurityService;
import com.affirm.system.configuration.Configuration;
import com.affirm.system.configuration.SecurityConfiguration;
import org.apache.shiro.SecurityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * Created by jarmando on 09/11/16.
 * Este Handler:
 * <p>
 * GET:  genera un CSRF Token si es que no tiene un Token y lo pone en HttpSession
 * POST: verifica que el CSRF Token sea válido y si no lo manda a un error page.
 */
@Component
public class SecurityInterceptor extends HandlerInterceptorAdapter {

    public static final String HEADER_SOLVEN_BOT = "x-solven-bot";

    @Autowired
    CSRFService csrfService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private ReCaptchaService reCaptchaService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private CatalogService catalogService;

    private static boolean debug = false;

    private void debugPrint(String s) {
        if (debug)
            System.out.println(getClass().getSimpleName() + " " + s);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //This is as empty as my soul, sorry.
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        debugPrint("SECURITY BEFORE");
        String firstParam = "";
        boolean isWebhook = utilService.isWebhookRequest(request);
        boolean isBlog = utilService.isBlogRequest(request);
        boolean isApiRest = utilService.isApiRestRequest(request);
        try {
            debugPrint("path info: " + request.getPathInfo());
            firstParam = request.getPathInfo().split("/")[1];
        } catch (Exception e) {
        }

        if (!isWebhook && !isBlog && !isApiRest) {

            //IF CAPTCHA IS NOT SOLVED GO TO CONFIRM HUMAN
            if (reCaptchaService.isCaptchaUnsolved() && !(firstParam.equals("confirmHuman") || firstParam.equals("checkSuccess"))) {
                reCaptchaService.redirectToConfirmHuman(request, response, reCaptchaService.destinyUrl(request));//system locked
                return false;
            }

            if (
                    request.getHeader("User-Agent") != null &&
                            ((request.getHeader(HEADER_SOLVEN_BOT) != null && request.getHeader(HEADER_SOLVEN_BOT).equals("true")) ||
                                    request.getHeader("User-Agent").contains("Googlebot") ||
                                    request.getHeader("User-Agent").contains("AdsBot-Google")||
                                    request.getHeader("User-Agent").contains("Google Page Speed Insights"))) {
                // LET IT PASS IF SPIDER FROM GOOGLE
                SecurityUtils.getSubject().getSession().setAttribute("countryUnlocked", true);
            } else {
                //COUNTRY CHECK
                boolean countryDetected = SecurityUtils.getSubject().getSession().getAttribute("countryUnlocked") != null;
                Object session_country_code = SecurityUtils.getSubject().getSession().getAttribute("ip_country_code");

                if (!countryDetected && !(firstParam.equals("confirmHuman") || firstParam.equals("checkSuccess"))) {//Country hasnt been setted in session
                    if (session_country_code == null) {//if there is no country in session, get it from ip
                        String country_code = Configuration.hostEnvIsLocal() ? "PE" : utilService.getCountryFromIp(Util.getClientIpAddres(request));
                        //country_code = "PE";

                        // Override the boolean in this envs
//                        if (Configuration.hostEnvIsLocal()) {
//                            country_code = "OT";
//                        }
//                        if (Configuration.hostEnvIsStage()) {
//                            country_code = "PE";
//                        }
//                        if (Configuration.hostEnvIsDev()) {
//                            country_code = "PE";
//                        }
                        session_country_code = country_code;
                    }

                    SecurityUtils.getSubject().getSession().setAttribute("ip_country_code", session_country_code);
                    CountryParam countryParam = catalogService.getCountryParamByCountryCode((String) session_country_code);
                    SecurityUtils.getSubject().getSession().setAttribute("countryUnlocked", true);
                    /*if (countryParam != null) {
                        // country of session is configured by us, omit the captcha
                        SecurityUtils.getSubject().getSession().setAttribute("countryUnlocked", true);
                    } else {
                        // country of session is not configured by us, go to the captcha
                        SecurityUtils.getSubject().getSession().setAttribute("countryUnlocked", false);
                        reCaptchaService.redirectToConfirmHuman(request, response, "/");
                        return false;
                    }*/
                }
            }
            SecurityUtils.getSubject().getSession().setAttribute("countryUnlocked", true);
        }
        return true;
    }
}