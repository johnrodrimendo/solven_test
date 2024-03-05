package com.affirm.client.service;

import com.affirm.client.model.LoggedUserClient;
import com.affirm.common.model.transactional.PersonInteraction;
import org.apache.shiro.authc.AuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 26/09/16.
 */
public interface ExtranetService {

    void login(AuthenticationToken token, HttpServletRequest request, Integer sysUserId) throws Exception;

    void onLogout(int sessionId, Date logoutDate) throws Exception;

    LoggedUserClient getLoggedUserClient() throws Exception;

    List<PersonInteraction> getLastInteractions(Locale locale) throws Exception;

    String customIconSVG(String icon);
}
