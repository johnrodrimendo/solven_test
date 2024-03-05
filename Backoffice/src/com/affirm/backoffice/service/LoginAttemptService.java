package com.affirm.backoffice.service;

import java.util.concurrent.ExecutionException;

public interface LoginAttemptService {
    void loginSucceeded(String key);

    void loginFailed(Integer sysUserId) throws ExecutionException;

    void blockSysUser(Integer sysUserId) ;
}
