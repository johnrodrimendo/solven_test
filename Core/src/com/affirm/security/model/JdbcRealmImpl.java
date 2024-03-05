package com.affirm.security.model;

import com.affirm.security.dao.SecurityDAO;
import org.apache.log4j.Logger;
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
import java.util.Set;

/**
 * Created by jrodriguez on 19/09/16.
 */

@Component
public class JdbcRealmImpl extends JdbcRealm {
    private static Logger logger = Logger.getLogger(JdbcRealmImpl.class);

    @Autowired
    private SecurityDAO securityDao;

    public JdbcRealmImpl() {
        super();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            final AuthenticationToken token) throws AuthenticationException {

        final AuthenticationInfo info = super.doGetAuthenticationInfo(token);
        SysUser user;
        try {
            user = securityDao.getSysUser(((UsernamePasswordToken) token).getUsername());
        } catch (Exception ex) {
            logger.error("Error autentificando usuario", ex);
            throw new AuthenticationException();
        }
        return new SimpleAuthenticationInfo(user, info.getCredentials(), getName());
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
