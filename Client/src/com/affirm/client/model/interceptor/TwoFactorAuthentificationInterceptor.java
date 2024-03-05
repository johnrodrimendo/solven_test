package com.affirm.client.model.interceptor;

import com.affirm.common.controller.TwoFactorAuthenticationController;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.service.CatalogService;
import com.affirm.security.model.ITwoFactorAuthLoggedUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by john on 18/11/16.
 */
@Component
public class TwoFactorAuthentificationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Subject subject = SecurityUtils.getSubject();
        Object principal = subject.getPrincipal();
        if (principal != null && subject.isAuthenticated()
                && principal instanceof ITwoFactorAuthLoggedUser
                && ((ITwoFactorAuthLoggedUser) principal).need2FA()
                && !((ITwoFactorAuthLoggedUser) principal).is2FALogged()) {

            subject.getSession().setAttribute("twoFactorSuccessRedirectUrl", request.getRequestURL() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
            throw new ModelAndViewDefiningException(new ModelAndView("redirect:/" + TwoFactorAuthenticationController.LOGIN_URL));
        }
        return true;
    }

}
