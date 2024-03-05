package com.affirm.backoffice.model;

import com.affirm.backoffice.service.BackofficeService;
import com.affirm.common.dao.SysUserDAO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TfaInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    SysUserDAO sysUserDAO;

    @Autowired
    BackofficeService backofficeService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Session session = SecurityUtils.getSubject().getSession();

        if (backofficeService.getLoggedSysuser().getActiveTfaLogin() != null && backofficeService.getLoggedSysuser().getActiveTfaLogin() == true) {
            if (!"success".equals(session.getAttribute("step2Success"))) {
                ModelAndView model = new ModelAndView("redirect:/loginStep2");
                throw new ModelAndViewDefiningException(model);
            }
        }
        return true;
    }
}
