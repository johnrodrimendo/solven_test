package com.affirm.security.model;

import com.affirm.backoffice.dao.BackofficeDAO;
import com.affirm.backoffice.service.BackofficeService;
import com.affirm.backoffice.service.LoginAttemptService;
import com.affirm.backoffice.service.SysUserService;
import com.affirm.backoffice.util.InactiveSysuserException;
import com.affirm.backoffice.util.LaboralScheduleException;
import com.affirm.backoffice.util.MaxSessionSysuserException;
import com.affirm.common.util.CryptoUtil;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.security.service.TokenAuthService;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.JdbcUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by jrodriguez on 19/09/16.
 */

@Component
public class GoogleAuthenticatorRealm extends JdbcRealm {

    private static Logger logger = Logger.getLogger(GoogleAuthenticatorRealm.class);

    @Autowired
    private SecurityDAO securityDao;
    @Autowired
    private BackofficeDAO backofficeDao;
    @Autowired
    private TokenAuthService tokenAuthService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private LoginAttemptService loginAttemptService;
    @Autowired
    private BackofficeService backofficeService;

    public GoogleAuthenticatorRealm() {
        super();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            final AuthenticationToken token) throws AuthenticationException {
        SysUser sysUser = null;
        boolean correctToken = false;
        Boolean countryUnlocked = backofficeService.isCountryUnlocked();
        if (countryUnlocked) {
            // Get user and decrypt secret
            sysUser = securityDao.getSysUser(((UsernamePasswordToken) token).getUsername());
            if(sysUserService.isActive(sysUser.getId())) {
                if (sysUserService.validLoginDate(sysUser.getScheduleId(), "PE")) {
                    String encryptedSecret = backofficeDao.getSharedSecret(((UsernamePasswordToken) token).getUsername());
                    // Validate the token is correct
                    String inputSolvenPassword = new String(((UsernamePasswordToken) token).getPassword());
                    if (!Configuration.hostEnvIsProduction()) { //demo login
                        if (sysUser.getUserName().substring(sysUser.getUserName().length() - 4).equals("Demo")) {
                            System.out.println(sysUser.getUserName());
                            System.out.println(inputSolvenPassword);
                            if (sysUser.getPassword().equals(inputSolvenPassword))
                                return new SimpleAuthenticationInfo(sysUser, null, getName());
                        }
                    }
                    correctToken = tokenAuthService.solvenAuth(encryptedSecret, sysUser.getUserName(), inputSolvenPassword);

                    if (correctToken) {
                        SimpleAuthenticationInfo sai = new SimpleAuthenticationInfo(sysUser, null, getName());
                        String sessionId = sysUserService.onLogin(sysUser.getId(), new Date());
                        if(Integer.parseInt(sessionId) > 0){
                            SecurityUtils.getSubject().getSession().setAttribute("boLoginId", sessionId);
                            loginAttemptService.loginSucceeded(sysUser.getId().toString());
                            return sai;
                        } else if("-1".equals(sessionId)){
                            throw new MaxSessionSysuserException();
                        }

                    } else {
                        try {
                            loginAttemptService.loginFailed(sysUser.getId());
                        } catch (ExecutionException e) {
                        }
                    }
                } else {
                    throw new LaboralScheduleException();
                }
            } else {
                throw new InactiveSysuserException();
            }
        } else {
            logger.warn("Warning this client won't be able to enter the system ever. Country Locked");
        }
        throw new IncorrectCredentialsException();
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //null usernames are invalid
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        String username = ((SysUser) getAvailablePrincipal(principals)).getUserName();

        Connection conn = null;
        Set<String> roleNames = null;
        Set<String> permissions = null;
        try {
            conn = dataSource.getConnection();

            // Retrieve roles and permissions from database
            roleNames = getRoleNamesForUser(conn, username);
            if (permissionsLookupEnabled) {
                permissions = getPermissions(conn, username, roleNames);
            }

        } catch (SQLException e) {
            final String message = "There was a SQL error while authorizing user [" + username + "]";
            logger.error(message, e);

            // Rethrow any SQL errors as an authorization exception
            throw new AuthorizationException(message, e);
        } finally {
            JdbcUtils.closeConnection(conn);
        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
        info.setStringPermissions(permissions);
        return info;
    }

}
