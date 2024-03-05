/**
 *
 */
package com.affirm.client.dao;

import com.affirm.client.model.LoggedUserEntity;
import org.apache.shiro.authc.AuthenticationException;
import org.json.JSONArray;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public interface EntityCLDAO {
    String getHashedPassword(String email);

    LoggedUserEntity registerSessionEntity(String email, String ip, String metadata, Date signinDate, Locale locale, Integer entityId) throws AuthenticationException;

    void registerSessionLogout(int extranetSessionId, Date signoutDate) throws Exception;

    JSONArray getCreditFullInfo(int entityId) throws Exception;

    void activateTfaLogin(int entityId, boolean activate);

    void updateSharedSecret(int entityUserId, JSONArray tfaScratchCode, String tfaSharedSecret);

    String registerEntityUser(int entityId, String name, String firstSurname, String email);

     List<Integer> getRolesByEntityUser(int entityUserId);
}
