package com.affirm.security.model;

import org.apache.shiro.authc.AuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created by john on 09/12/16.
 */
public class ClientToken implements AuthenticationToken {

    private String email;
    private String password;
    private Integer sysUserId;
    private HttpServletRequest request;
    private Locale locale;
    private String linkedinCode;
    private String facebookCode;
    private String yahooCode;
    private String windowsCode;
    private String googleCode;

    public ClientToken(Locale locale, HttpServletRequest request) {
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

    public String getLinkedinCode() {
        return linkedinCode;
    }

    public void setLinkedinCode(String linkedinCode) {
        this.linkedinCode = linkedinCode;
    }

    public String getFacebookCode() {
        return facebookCode;
    }

    public void setFacebookCode(String facebookCode) {
        this.facebookCode = facebookCode;
    }

    public String getYahooCode() {
        return yahooCode;
    }

    public void setYahooCode(String yahooCode) {
        this.yahooCode = yahooCode;
    }

    public String getWindowsCode() {
        return windowsCode;
    }

    public void setWindowsCode(String windowsCode) {
        this.windowsCode = windowsCode;
    }

    public String getGoogleCode() {
        return googleCode;
    }

    public void setGoogleCode(String googleCode) {
        this.googleCode = googleCode;
    }

    public Integer getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(Integer sysUserId) {
        this.sysUserId = sysUserId;
    }
}
