package com.affirm.security.model;

import org.apache.shiro.authc.AuthenticationToken;

import java.util.Locale;

/**
 * Created by john on 09/12/16.
 */
public class PhantomToken implements AuthenticationToken {

    private Integer userId;
    private Integer sysUserId;
    private Locale locale;

    public PhantomToken(int userId, int sysUserId, Locale locale) {
        this.userId = userId;
        this.sysUserId = sysUserId;
        this.locale = locale;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(Integer sysUserId) {
        this.sysUserId = sysUserId;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
