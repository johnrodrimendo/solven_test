package com.affirm.client.service.impl;

import com.affirm.client.dao.UserCLDAO;
import com.affirm.client.model.LoggedUserClient;
import com.affirm.client.service.ExtranetService;
import com.affirm.common.dao.InteractionDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.transactional.PersonInteraction;
import com.affirm.common.model.transactional.User;
import com.affirm.common.service.UtilService;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by jrodriguez on 27/09/16.
 */

@Service("extranetService")
public class ExtranetServiceImpl implements ExtranetService {

    private static Logger logger = Logger.getLogger(ExtranetServiceImpl.class);

    @Autowired
    private UserDAO userDao;
    @Autowired
    private UserCLDAO userClDao;
    @Autowired
    private InteractionDAO interactionDao;
    @Autowired
    private UtilService utilService;

    @Override
    public void login(AuthenticationToken token, HttpServletRequest request, Integer sysUserId) throws Exception {

        // Log in and sets the session timeout to 5 min
        SecurityUtils.getSubject().login(token);
        SecurityUtils.getSubject().getSession().setTimeout(Configuration.getExtranetTimeoutMinutes() * 60 * 1000);
        SecurityUtils.getSubject().getSession().setAttribute("extranetLoginId", ((LoggedUserClient) SecurityUtils.getSubject().getPrincipal()).getSessionId());
    }

    @Override
    public void onLogout(int sessionId, Date logoutDate) throws Exception {
        userDao.registerSessionLogout(sessionId, logoutDate);
    }

    @Override
    public LoggedUserClient getLoggedUserClient() throws Exception {
        return ((LoggedUserClient) SecurityUtils.getSubject().getPrincipal());
    }

    @Override
    public List<PersonInteraction> getLastInteractions(Locale locale) throws Exception {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        List<PersonInteraction> personInteractions = interactionDao.getPersonInteractions(user.getPersonId(), locale);
        if (personInteractions != null)
            return personInteractions.stream().limit(5).collect(Collectors.toList());
        return new ArrayList<>();
    }

    @Override
    public String customIconSVG(String icon){
        if(icon == null) return null;
        switch (icon){
            case "check":
                return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"9.394\" height=\"6.811\" viewBox=\"0 0 9.394 6.811\">\n" +
                        "  <path id=\"Icon_feather-check\" data-name=\"Icon feather-check\" d=\"M13.273,9l-5,5L6,11.727\" transform=\"translate(-4.939 -7.939)\" fill=\"none\" stroke=\"#fff\" stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"1.5\"/>\n" +
                        "</svg>";
            case "check-rounded":
                return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"31\" height=\"31\" viewBox=\"0 0 31 31\">\n" +
                        "  <g id=\"Grupo_10583\" data-name=\"Grupo 10583\" transform=\"translate(-10569.246 -1319)\">\n" +
                        "    <circle id=\"Elipse_87\" data-name=\"Elipse 87\" cx=\"15.5\" cy=\"15.5\" r=\"15.5\" transform=\"translate(10569.246 1319)\" fill=\"#fff\"/>\n" +
                        "    <path id=\"Icon_feather-check\" data-name=\"Icon feather-check\" d=\"M18.426,9,9.883,17.543,6,13.66\" transform=\"translate(10572.533 1321.229)\" fill=\"none\" stroke=\"#a2aaad\" stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"2\"/>\n" +
                        "  </g>\n" +
                        "</svg>";
            case "notification":
                return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"9.997\" height=\"10.995\" viewBox=\"0 0 9.997 10.995\">\n" +
                        "  <g id=\"Icon_feather-bell\" data-name=\"Icon feather-bell\" transform=\"translate(-4 -2.5)\">\n" +
                        "    <path id=\"Trazado_9775\" data-name=\"Trazado 9775\" d=\"M12,6A3,3,0,1,0,6,6c0,3.5-1.5,4.5-1.5,4.5h9S12,9.5,12,6\" fill=\"none\" stroke=\"#6f737a\" stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"1\"/>\n" +
                        "    <path id=\"Trazado_9776\" data-name=\"Trazado 9776\" d=\"M17.134,31.5a1,1,0,0,1-1.729,0\" transform=\"translate(-7.271 -19.003)\" fill=\"none\" stroke=\"#6f737a\" stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"1\"/>\n" +
                        "  </g>\n" +
                        "</svg>";
            case "star":
                return "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"9.682\" height=\"9.256\" viewBox=\"0 0 9.682 9.256\">\n" +
                        "  <path id=\"Icon_feather-star\" data-name=\"Icon feather-star\" d=\"M7.341,3,8.682,5.717l3,.438L9.511,8.27l.512,2.986L7.341,9.845,4.658,11.256,5.17,8.27,3,6.156l3-.438Z\" transform=\"translate(-2.5 -2.5)\" fill=\"none\" stroke=\"#fff\" stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"1\"/>\n" +
                        "</svg>";
        }
        return null;
    }

}
