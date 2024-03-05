package com.affirm.security.model;

import org.apache.shiro.authc.AuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created by john on 09/12/16.
 */
public class CompanyEmailToken implements AuthenticationToken {

    private String email;
    private String password;
    private HttpServletRequest request;
    private Locale locale;

    public CompanyEmailToken(String email, String password, Locale locale, HttpServletRequest request) {
        this.email = email;
        this.password = password;
        this.locale = locale;
        this.request = request;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
