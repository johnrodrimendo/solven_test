package com.affirm.security.model;

import com.affirm.client.dao.EntityCLDAO;
import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.util.InvalidPasswordException;
import com.affirm.client.util.MustChangePasswordException;
import com.affirm.client.util.NoUserFoundException;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.EntityBranding;
import com.affirm.common.model.transactional.UserEntity;
import com.affirm.common.service.BrandingService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.CryptoUtil;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.JdbcUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Set;

/**
 * Created by john on 08/12/16.
 */
public class EntityExtranetRealmImpl extends JdbcRealmImpl {
    private static Logger logger = Logger.getLogger(EntityExtranetRealmImpl.class);

    @Autowired
    private EntityCLDAO entityClDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private UtilService utilService;
    @Autowired
    private BrandingService brandingService;

    @Override
    public boolean supports(AuthenticationToken token) {
        if (token instanceof EntityEmailToken) {
            return true;
        }
        return false;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //null usernames are invalid
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }

        String username = ((UserEntity) getAvailablePrincipal(principals)).getEmail();

        Connection conn = null;
        Set<String> roleNames = null;
        Set<String> permissions = null;
        try {
            conn = dataSource.getConnection();

            // Retrieve roles and permissions from database
            roleNames = getRoleNamesForUser(conn, username);
            logger.info("Obteniendo roles de "+username + " - "+roleNames);
            if (permissionsLookupEnabled) {
                permissions = getPermissions(conn, username, roleNames);
                logger.info("Obteniendo permisos de "+username + " - "+permissions);
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

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        EntityEmailToken entityToken = (EntityEmailToken) token;
        EntityBranding entityBranding = null;
        try {
            entityBranding = brandingService.getEntityBranding(entityToken.getRequest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String hashedPassword = entityClDAO.getHashedPassword(entityToken.getEmail().toLowerCase());
        UserEntity userEntity = userDAO.getUserEntityByEmail(entityToken.getEmail(), entityToken.getLocale(), entityBranding != null ? entityBranding.getEntity().getId() : null);
        if (hashedPassword == null)
            throw new NoUserFoundException();//email not found
        if (userEntity.getMustChangePassword() == true || hashedPassword == "")
            throw new MustChangePasswordException();
        boolean match = CryptoUtil.validatePassword(entityToken.getPassword(), hashedPassword);
        if (!match)
            throw new InvalidPasswordException();//didnt match

        EntityBranding finalEntityBranding = entityBranding;
        if(userEntity.getEntities() != null  && entityBranding != null && entityBranding.getEntity() != null && userEntity.getEntities().stream().filter(e -> e.getId() == finalEntityBranding.getEntity().getId()).findFirst().orElse(null) == null){
            throw new NoUserFoundException();
        }

        //register session
        LoggedUserEntity loggedEntity = null;

        loggedEntity = entityClDAO.registerSessionEntity(
                entityToken.getEmail(),
                entityToken.getRequest().getRemoteAddr(),
                utilService.parseHttpRequestAsJson(entityToken.getRequest()).toString(),
                new Date(), entityToken.getLocale(),
                entityBranding != null ? entityBranding.getEntity().getId() : null);

        return new SimpleAuthenticationInfo(loggedEntity, null, getName());
    }

}