package com.affirm.security.model;

import com.affirm.common.service.AwsSesEmailService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.Util;
import com.affirm.security.service.CSRFService;
import com.affirm.security.service.ReCaptchaService;
import com.affirm.security.service.SecurityService;
import com.affirm.system.configuration.Configuration;
import com.affirm.system.configuration.SecurityConfiguration;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jarmando on 09/11/16.
 * Este Handler:
 * <p>
 * GET:  genera un CSRF Token si es que no tiene un Token y lo pone en HttpSession
 * POST: verifica que el CSRF Token sea válido y si no lo manda a un error page.
 */
@Component
public class CSRFInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    CSRFService csrfService;
    @Autowired
    private AwsSesEmailService awsSesEmailService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private ReCaptchaService reCaptchaService;
    @Autowired
    private UtilService utilService;

    public static final String IFRAME_HEADER_VALIDATOR = "EQmsmpULSd";
    public static final String IFRAME_HEADER_ENABLE_VALUE = "11RDSCiEUPCPJFebUY914bgbcc8qw8drlhyXxPhc";
    public static final List<String> USERS_AGENTS_TO_SKIP_VALIDATION = Arrays.asList("Safari","iPhone");
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //This is as empty as my soul, sorry.
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean isWebhook = utilService.isWebhookRequest(request);

        if(Configuration.hostEnvIsLocal()){
            return true;
        }

        // If it's not a post, save in session a new CSR token if doesnt exists yet and create the CSRF Cookie
        if (!request.getMethod().equalsIgnoreCase("POST")) {

            // Ignore if its a webhook request
            if (utilService.isWebhookRequest(request))
                return true;

            // Generate new token
            csrfService.generateNewTokenInSessionIfNull();

            // Send it to the client
            csrfService.setTokenToClientFromSession(request, response);
            return true;
        } else {
            // Ignore if its a webhook request
            if (utilService.isWebhookRequest(request))
                return true;

            if (utilService.isApiRestRequest(request))
                return true;

            if (csrfService.validateToken(request)) {
                return true;
            } else {
                String userAgent = request.getHeader("User-Agent");
                String iFrameHeaderValue =  request.getHeader(IFRAME_HEADER_VALIDATOR);

                //SKIP VALIDATION IF THE REQUEST WAS MADE THOUGH IFRAME AND THE DEVICE ITS A IPHONE
                if(userAgent != null && !userAgent.isEmpty() && iFrameHeaderValue != null && !iFrameHeaderValue.isEmpty()){
                    if(iFrameHeaderValue.equalsIgnoreCase(IFRAME_HEADER_ENABLE_VALUE)){
                        userAgent = userAgent.toLowerCase();
                        String finalUserAgent = userAgent;
                        if(USERS_AGENTS_TO_SKIP_VALIDATION.stream().anyMatch(e -> finalUserAgent.contains(e.toLowerCase()))) return true;
                    }
                }

                String ip = Util.getClientIpAddres(request);
                JSONObject locationJson = securityService.registerSecurityAlert(SecurityConfiguration.Attack.CSRF, ip, utilService.parseHttpRequestAsJson(request).toString());
                if (Configuration.hostEnvIsProduction()) {
                    String from = "alertas@solven.pe";
                    String to = Configuration.EMAIL_ERROR_TO();
                    String subject = "Security Alert in " + Configuration.hostEnvName();

                    awsSesEmailService.sendEmail(
                            from,
                            to,
                            null,
                            subject,
                            "CSRF Tokens didn't match. \n" +
                            "From IP: " + ip + ". \n" +
                            "IP Location: " + (locationJson == null ? "Not found." : locationJson.toString()) + "\n" +
                            "URL path: " + request.getRequestURL() +
                            "Server token: " + request.getSession().getAttribute(csrfService.CSRF_LAST_MISMATCH_SERVER) + "\n" +
                            "Client token: " + request.getSession().getAttribute(csrfService.CSRF_LAST_MISMATCH_CLIENT),
                            null,
                            null);
                }

                response.sendRedirect(Configuration.isClient() ? Configuration.getClientDomain() : Configuration.getBackofficeDomain());
            }
            return false;
        }
    }
}