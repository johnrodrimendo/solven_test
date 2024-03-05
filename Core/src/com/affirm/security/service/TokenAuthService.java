package com.affirm.security.service;

import com.affirm.security.model.SysUser;
import org.json.JSONArray;

import java.util.Map;

/**
 * Created by jarmando on 29/12/16.
 */
public interface TokenAuthService {

    Map.Entry<String, JSONArray> newTokenCredentials();

    boolean vinculate(SysUser sysUser, String secret, JSONArray scratchs, Integer tokenInt) throws Exception;

    boolean solvenAuth(String encryptedScharedSecret, String username, String solvenPassword);

    boolean checkAppSharedToken(Integer token);

    Integer getAppSharedToken();
}
