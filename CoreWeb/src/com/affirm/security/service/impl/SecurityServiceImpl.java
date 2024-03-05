package com.affirm.security.service.impl;

import com.affirm.common.dao.SysUserDAO;
import com.affirm.common.service.SessionService;
import com.affirm.common.service.UserService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.Util;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.security.model.SysUser;
import com.affirm.security.service.CSRFService;
import com.affirm.security.service.SecurityService;
import com.affirm.security.service.TokenAuthService;
import com.affirm.system.configuration.Configuration;
import com.affirm.system.configuration.SecurityConfiguration;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.util.SavedRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.time.LocalDate;

/**
 * Created by jrodriguez on 19/09/16.
 */
@Service("securityService")
public class SecurityServiceImpl implements SecurityService {

    private static Logger logger = Logger.getLogger(SecurityServiceImpl.class);

    @Autowired
    UserService userService;
    @Autowired
    SecurityDAO securityDao;
    @Autowired
    private TokenAuthService tokenAuthService;
    @Autowired
    private UtilService utilService;
    @Autowired
    SysUserDAO sysUserDAO;

    @Override
    public String authenticate(String username, String password, boolean rememberMe, HttpServletRequest request) {
        // Stop session fixation issues.
        // https://issues.apache.org/jira/browse/SHIRO-170
        boolean success = false;
        Subject loginAttempSubject = SecurityUtils.getSubject();
        //try {// Loguear con token
            handleSessionCleanUpIfAuthenticated(loginAttempSubject);
            AuthenticationToken token = new UsernamePasswordToken(username, password);
            loginAttempSubject.login(token);
            success = true;
        //} catch (Exception e) {
        //    logger.error("Error", e);
        //}
        /*if (success) {
            request.changeSessionId();//rotar id
        }*/
        //generate redirect and return
        SavedRequest savedRequest = ((SavedRequest) loginAttempSubject.getSession().getAttribute("shiroSavedRequest"));
        String redirect = "";
        if(!sysUserDAO.getSysUserByUsername(username).getActiveTfaLogin()){
            if (savedRequest != null)
                redirect = savedRequest.getRequestUrl().replaceFirst(request.getContextPath(), "");
            if (redirect.isEmpty())
                redirect = "/profile";
        } else {
            if (request.getPathInfo().equals("/login"))
                redirect = "/loginStep2";
            if (request.getPathInfo().equals("/loginStep2")){
                if (savedRequest != null)
                    redirect = savedRequest.getRequestUrl().replaceFirst(request.getContextPath(), "");
                if (redirect.isEmpty())
                    redirect = "/profile";  
            }
        }

        return success ? redirect : null;
    }

    /**
     * Removes all session but savedRequest and csrfToken
     */
    private void handleSessionCleanUpIfAuthenticated(Subject loginAttempSubject) {
        if (loginAttempSubject.isAuthenticated()) {
            Session session = loginAttempSubject.getSession();
            Iterator<Object> iterator = session.getAttributeKeys().iterator();
            ArrayList<String> list = new ArrayList<>();
            while (iterator.hasNext()) {
                Object key = iterator.next();
                if (
                        !key.equals(CSRFService.CSRF_TOKEN_FOR_SESSION_KEY) ||  //dont remove, this already has a regenerated value (validation)
                                !key.equals("shiroSavedRequest") ||                     //don't remove, it has info for redirect
                                !key.equals(SessionService.SESSION_MUTEX_KEY) ||          //dont remove, this already has a regenerated value (change id)
                                !key.equals("org.apache.shiro.web.session.HttpServletSession.HOST_SESSION_KEY") //don't remove host hasnt changed
                        )
                    //session.removeAttribute(key);//every other session attribute gets destroyed.
                    list.add(key.toString());
            }
            for(int i = 0; i < list.size(); i++){
                session.removeAttribute(list.indexOf(i));
            }
        }
    }

    @Override
    public JSONObject registerSecurityAlert(SecurityConfiguration.Attack attack, String ip, String request) throws Exception {
        JSONObject jsonObject = utilService.getIpGeolocation(ip, null);
        securityDao.registerExternalAttack(attack.name(),
                ip,
                JsonUtil.getDoubleFromJson(jsonObject, "latitude", null),
                JsonUtil.getDoubleFromJson(jsonObject, "longitude", null),
                request);
        return jsonObject;
    }

    @Override
    public boolean checkAppSharedToken(Integer token) {
        boolean b = tokenAuthService.checkAppSharedToken(token);
        if (!b) logger.debug("");
        return b;
    }

    @Override
    public boolean isNotReadyAccount(String username)throws Exception{
        SysUser sysUser = sysUserDAO.getSysUserByUsername(username);
        Date currentDate= new Date();
        return currentDate.before(sysUser.getRegiterDate());
    }

    @Override
    public boolean isExpiredPassword(String username)throws Exception{
        SysUser sysUser = sysUserDAO.getSysUserByUsername(username);
        Date lastPasswordDate=sysUser.getPasswordExpirationDate();
        Date currentDate= new Date();
        return currentDate.after(lastPasswordDate);
    }
}