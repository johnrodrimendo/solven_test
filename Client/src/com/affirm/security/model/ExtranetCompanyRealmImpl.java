package com.affirm.security.model;

import com.affirm.client.dao.EmployerCLDAO;
import com.affirm.client.model.LoggedUserEmployer;
import com.affirm.client.util.InvalidPasswordException;
import com.affirm.client.util.NoUserFoundException;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.CryptoUtil;
import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by john on 08/12/16.
 */
public class ExtranetCompanyRealmImpl extends AuthorizingRealm {
    private static Logger logger = Logger.getLogger(ExtranetCompanyRealmImpl.class);

    @Autowired
    private EmployerCLDAO employerDao;
    @Autowired
    private UtilService utilService;

    @Override
    public boolean supports(AuthenticationToken token) {
        if (token instanceof CompanyEmailToken) {
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
        try {
            CompanyEmailToken companyToken = (CompanyEmailToken) token;
            String hashedPassword = employerDao.getHashedPassword(companyToken.getEmail().toLowerCase());
            if(hashedPassword == null)
                throw new NoUserFoundException();//email not found
            boolean match = CryptoUtil.validatePassword(companyToken.getPassword(), hashedPassword);
            if(!match)
                throw new InvalidPasswordException();//didnt match
            //register session
            LoggedUserEmployer loggedEmployer = employerDao.registerSessionEmployer(
                    companyToken.getEmail(),
                    companyToken.getRequest().getRemoteAddr(),
                    utilService.parseHttpRequestAsJson(companyToken.getRequest()).toString(),
                    new Date(), companyToken.getLocale());
            if(loggedEmployer == null)
                throw new NoUserFoundException();//db procedure unexpected return
            return new SimpleAuthenticationInfo(loggedEmployer, null, getName());
        } catch (Exception ex) {
            logger.error("Error autheticating user", ex);
            throw new AuthenticationException(ex);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

}