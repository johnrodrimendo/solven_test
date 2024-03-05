package com.affirm.security.service.impl;

import com.affirm.common.dao.CatalogDAO;
import com.affirm.common.dao.SysUserDAO;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.Util;
import com.affirm.security.model.SysUser;
import com.affirm.security.service.TokenAuthService;
import com.affirm.system.configuration.Configuration;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Map;

/**
 * Created by jarmando on 29/12/16.
 */
@Service("tokenAuthService")
public class TokenAuthServiceImpl implements TokenAuthService {

    GoogleAuthenticator gAuth = new GoogleAuthenticator();

    @Autowired
    CatalogDAO catalogDAO;
    @Autowired
    SysUserDAO sysUserDAO;

    @Override
    public Map.Entry<String, JSONArray> newTokenCredentials(){
        final GoogleAuthenticatorKey gaMasterKey = gAuth.createCredentials();
        return new Map.Entry<String, JSONArray>(){
            @Override
            public String getKey() {
                return gaMasterKey.getKey();
            }

            @Override
            public JSONArray getValue() {
                return new JSONArray(gaMasterKey.getScratchCodes());
            }

            @Override
            public JSONArray setValue(JSONArray value) {
                return null;
            }
        };
    }

    @Override
    public boolean vinculate(SysUser sysUser, String secret, JSONArray scratchs, Integer tokenInt) throws Exception {
        if(tokenInt == null){
            return false;
        }
        boolean challenge = gAuth.authorize(secret, tokenInt);
        if (challenge) {
            //encrypt
            String hashSecret = CryptoUtil.encryptAuthSecret(sysUser.getUserName(), secret);
            //persist
            sysUserDAO.updateSharedSecret(sysUser.getId(), hashSecret, scratchs);
        }
        return challenge;
    }

    @Override
    public boolean solvenAuth(String encryptedSharedSecret, String username, String solvenPassword) {
        //Try numeric TOTP token auth (production)
        Integer numberToken = Util.intOrNull(solvenPassword);
        boolean correctToken = false;
        if (numberToken != null && encryptedSharedSecret != null && !sysUserDAO.getSysUserByUsername(username).getActiveTfaLogin()){
            String decryptedSecret = CryptoUtil.decryptAuthSecret(username, encryptedSharedSecret);
            correctToken = gAuth.authorize(decryptedSecret, numberToken);
        }
        //Try fallback parssword for stg and loc
        if(correctToken == false && (!Configuration.hostEnvIsProduction())) {
            System.out.println("production key failed. try loc stg key");
            int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            correctToken = solvenPassword.trim().equals(username.trim() + hours);
        } else if(correctToken == false && (Configuration.hostEnvIsProduction())){
            try{
                correctToken = CryptoUtil.validatePassword(solvenPassword, sysUserDAO.getPasswordByUsername(username));
            } catch (NullPointerException e){
                return false;
            }
        }
        //Try master TOTP if encryptedSharedSecret is null
        if(correctToken == false && encryptedSharedSecret == null) {
            String key = System.getenv("SECRET_ENCRYPT_KEY").substring(8, 16) + System.getenv("SECRET_ENCRYPT_KEY").substring(0, 8);
            String decriptedSecret = CryptoUtil.decryptAES(System.getenv("MASTER_TFA_SECRET"), key);
            correctToken = gAuth.authorize(decriptedSecret, numberToken);
        }
        return correctToken;
    }

    @Override
    public boolean checkAppSharedToken(Integer token) {
        String appSharedSecret = catalogDAO.getConfigParams().get("APP_SHARED_SECRET");
        return gAuth.authorize(appSharedSecret, token);
    }

    @Override
    public Integer getAppSharedToken() {
        String appSharedSecret = catalogDAO.getConfigParams().get("APP_SHARED_SECRET");
        return gAuth.getTotpPassword(appSharedSecret);
    }
}
