package com.affirm.security.service;

import com.affirm.system.configuration.SecurityConfiguration;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by jrodriguez on 19/09/16.
 */
public interface SecurityService {
    String authenticate(String username, String password, boolean rememberMe, HttpServletRequest request);

    JSONObject registerSecurityAlert(SecurityConfiguration.Attack attack, String ip, String request) throws Exception;

    boolean checkAppSharedToken(Integer token);

    boolean isNotReadyAccount(String username)throws Exception;

    boolean isExpiredPassword(String username)throws Exception;
}
