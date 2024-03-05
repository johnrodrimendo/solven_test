package com.affirm.security.model;

import com.affirm.client.util.NoUserFoundException;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.transactional.User;
import com.affirm.common.service.CatalogService;
import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by john on 08/12/16.
 */
public class PhantomRealmImpl extends AuthorizingRealm {

    private static Logger logger = Logger.getLogger(PhantomRealmImpl.class);

    @Autowired
    private UserDAO userDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;

    @Override
    public boolean supports(AuthenticationToken token) {
        if (token instanceof PhantomToken) {
            return true;
        }
        return false;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        PhantomToken phantomToken = (PhantomToken) token;
        try {
            User user = userDao.getUser(phantomToken.getUserId());
            if (user != null) {
                user.setPerson(personDao.getPerson(catalogService, phantomToken.getLocale(), user.getPersonId(), false));
                return new SimpleAuthenticationInfo(user, null, getName());
            }
            throw new NoUserFoundException();
        } catch (Exception ex) {
            logger.error("Error autheticating user", ex);
            throw new AuthenticationException(ex);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

}