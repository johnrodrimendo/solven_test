package com.affirm.security.model;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.apache.shiro.web.filter.authc.PassThruAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Created by john on 22/08/17.
 */
public class MultiSessionAuthenticationFilter extends PassThruAuthenticationFilter {

    private Class permitedUserClass;

    public Class getPermitedUserClass() {
        return permitedUserClass;
    }

    public void setPermitedUserClass(Class permitedUserClass) {
        this.permitedUserClass = permitedUserClass;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        return super.onAccessDenied(servletRequest, servletResponse);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = this.getSubject(request, response);
        return subject.isAuthenticated() && permitedUserClass.isInstance(subject.getPrincipal());
    }
}
