package com.affirm.backoffice.service.impl;

import com.affirm.backoffice.service.LoginAttemptService;
import com.affirm.backoffice.util.InactiveSysuserException;
import com.affirm.backoffice.util.LoginFailedException;
import com.affirm.common.dao.SysUserDAO;
import com.affirm.common.util.AjaxResponse;
import com.affirm.system.configuration.Configuration;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {
    @Autowired
    private SysUserDAO sysUserDAO;

    private LoadingCache<String, Integer> attemptsCache;

    public LoginAttemptServiceImpl() {
        super();
        attemptsCache = CacheBuilder.newBuilder().
                expireAfterWrite(1, TimeUnit.DAYS).build(new CacheLoader<String, Integer>() {
            public Integer load(String key) {
                return 0;
            }
        });
    }

    public void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
    }

    public void loginFailed(Integer sysUserId) throws ExecutionException {
        int attempts = 0;
        if(sysUserDAO.sysUserIsActive(sysUserId)){
            try {
                attempts = attemptsCache.get(sysUserId.toString());
            } catch (ExecutionException e) {
                attempts = 0;
            }
            attempts++;
            attemptsCache.put(sysUserId.toString(), attempts);
            if(attemptsCache.get(sysUserId.toString()) >= Configuration.MAX_LOGIN_ATTEMPTS)
                blockSysUser(sysUserId);
            throw new LoginFailedException();
        } else {
            throw new InactiveSysuserException();
        }
    }

    public void blockSysUser(Integer sysUserId) {
        try {
            sysUserDAO.setActiveSysUser(sysUserId, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
