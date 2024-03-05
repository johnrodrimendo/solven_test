package com.affirm.security.model;

import com.affirm.client.dao.UserCLDAO;
import com.affirm.client.model.LoggedUserClient;
import com.affirm.client.util.InvalidPasswordException;
import com.affirm.client.util.NoUserFoundException;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.transactional.User;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.OauthService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.NetworkProfile;
import com.affirm.system.configuration.Configuration;
import com.google.api.client.auth.oauth2.TokenResponse;
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
public class ClientExtranetRealmImpl extends AuthorizingRealm {

    private static Logger logger = Logger.getLogger(ClientExtranetRealmImpl.class);

    @Autowired
    private UserDAO userDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private OauthService oauthService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private UserCLDAO userClDao;
    @Autowired
    private CatalogService catalogService;

    @Override
    public boolean supports(AuthenticationToken token) {
        if (token instanceof ClientToken) {
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
            ClientToken clientToken = (ClientToken) token;
            User user = null;

            if (clientToken.getEmail() != null) {
                user = userDao.getUserByEmail(clientToken.getEmail().toLowerCase());
                if (user == null) {
                    throw new NoUserFoundException();
                }

                String hashedPassword = user.getPassword();
                if (hashedPassword == null)
                    throw new NoUserFoundException();//email not found
                boolean match = CryptoUtil.validatePassword(clientToken.getPassword(), hashedPassword);
                if (!match)
                    throw new InvalidPasswordException();//didnt match

            } else {

                Configuration.OauthNetwork network = null;
                String networkCode = null;
                if (clientToken.getFacebookCode() != null) {
                    network = Configuration.OauthNetwork.FACEBOOK;
                    networkCode = clientToken.getFacebookCode();
                } else if (clientToken.getLinkedinCode() != null) {
                    network = Configuration.OauthNetwork.LINKEDIN;
                    networkCode = clientToken.getLinkedinCode();
                } else if (clientToken.getGoogleCode() != null) {
                    network = Configuration.OauthNetwork.GOOGLE;
                    networkCode = clientToken.getGoogleCode();
                } else if (clientToken.getYahooCode() != null) {
                    network = Configuration.OauthNetwork.YAHOO;
                    networkCode = clientToken.getYahooCode();
                } else if (clientToken.getWindowsCode() != null) {
                    network = Configuration.OauthNetwork.WINDOWS;
                    networkCode = clientToken.getWindowsCode();
                }

                TokenResponse tokenResponse = oauthService.getAccessToken(network, networkCode);
                NetworkProfile networkProfile = oauthService.getNetworkProfile(network, tokenResponse.getAccessToken());

                // Get user by network
                if (network == Configuration.OauthNetwork.FACEBOOK) {
                    user = userDao.getUserByFacebookId(networkProfile.getId());
                } else if (network == Configuration.OauthNetwork.LINKEDIN) {
                    user = userDao.getUserByLinkedinId(networkProfile.getId());
                } else if (network == Configuration.OauthNetwork.GOOGLE) {
                    user = userDao.getUserByGoogle(networkProfile.getId());
                } else if (network == Configuration.OauthNetwork.WINDOWS) {
                    user = userDao.getUserByWindows(networkProfile.getId());
                } else if (network == Configuration.OauthNetwork.YAHOO) {
                    user = userDao.getUserByYahoo(networkProfile.getId());
                }

                if (user == null) {
                    throw new NoUserFoundException();
                }

                user.setPerson(personDao.getPerson(catalogService, clientToken.getLocale(), user.getPersonId(), false));
            }

            LoggedUserClient loggedUser = userClDao.registerSessionLogin(
                    user.getId(),
                    clientToken.getRequest().getRemoteAddr(),
                    utilService.parseHttpRequestAsJson(clientToken.getRequest()).toString(),
                    new Date(),
                    clientToken.getSysUserId());

            // Little hack because registerSessionLogin doesnt return the user json
            loggedUser.setId(user.getId());
            loggedUser.setFullName(user.getFullName());
            loggedUser.setEmail(user.getEmail());
            loggedUser.setPersonId(user.getPersonId());
            loggedUser.setPhoneNumber(user.getPhoneNumber());

            return new SimpleAuthenticationInfo(loggedUser, null, getName());

        } catch (Exception ex) {
            logger.error("Error autheticating user", ex);
            throw new AuthenticationException(ex);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

}