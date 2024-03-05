package com.affirm.security.service.impl;

import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.Util;
import com.affirm.security.service.ReCaptchaService;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jrodriguez on 27/09/16.
 */
@Service
public class ReCaptchaServiceImpl implements ReCaptchaService {
    private static Logger logger = Logger.getLogger(ReCaptchaServiceImpl.class);
    public String REDIRECT_URL_KEY = "REDIRECT_URL_KEY";

    @Override
    public void redirectToConfirmHuman(HttpServletRequest request, HttpServletResponse response, String urlOnSuccess) throws Exception {
        SecurityUtils.getSubject().getSession().setAttribute(REDIRECT_URL_KEY, urlOnSuccess);
        response.sendRedirect(request.getContextPath() + CONFIRM_URL);
    }

    @Override
    public boolean checkSuccess(HttpServletRequest request, String responseToken) throws Exception {
        String publicParams = "&response=" + responseToken + "&remoteip=" + Util.getClientIpAddres(request);
        String checkSucccessUrl = "https://www.google.com/recaptcha/api/siteverify?" +
                "secret=" + System.getenv("RECAPTCHA_SECRET") + publicParams;
        JSONObject json = JsonUtil.getJSONObjectFromUrl(checkSucccessUrl);
        return JsonUtil.getBooleanFromJson(json, "success", false);
    }

    @Override
    public boolean checkInvisibleSuccess(HttpServletRequest request, String responseToken) throws Exception {
        String publicParams = "&response=" + responseToken + "&remoteip=" + Util.getClientIpAddres(request);
        String checkSucccessUrl = "https://www.google.com/recaptcha/api/siteverify?" +
                "secret=" + System.getenv("INVISIBLE_CAPTCHA_SECRET") + publicParams;
        JSONObject json = JsonUtil.getJSONObjectFromUrl(checkSucccessUrl);
        return JsonUtil.getBooleanFromJson(json, "success", false);
    }

    @Override
    public String destinyUrl(HttpServletRequest request) {
        String destinyUrl = (String) SecurityUtils.getSubject().getSession().getAttribute(REDIRECT_URL_KEY);
        if (destinyUrl == null) {
            String contextPath = request.getContextPath();
            contextPath = contextPath == null ? "/" : contextPath;
            contextPath = contextPath.equals("") ? "/" : contextPath;
            return contextPath;
        } else {
            return destinyUrl;
        }
    }

    @Override
    public void cleanDestinyUrl() {
        SecurityUtils.getSubject().getSession().setAttribute(REDIRECT_URL_KEY, null);
    }

    @Override
    public boolean isCaptchaUnsolved() {
        return SecurityUtils.getSubject().getSession().getAttribute(REDIRECT_URL_KEY) != null;
    }

    @Override
    public boolean contactCheckSuccess(HttpServletRequest request) {
        String responseToken = request.getParameter("g-recaptcha-response");

        String publicParams = "&response=" + responseToken + "&remoteip=" + Util.getClientIpAddres(request);

        String checkSucccessUrl = "https://www.google.com/recaptcha/api/siteverify?" +
                "secret=" + System.getenv("HIDDENCAPTCHA_SECRET") + publicParams;

        JSONObject json = JsonUtil.getJSONObjectFromUrl(checkSucccessUrl);

        return JsonUtil.getBooleanFromJson(json, "success", false);
    }
}