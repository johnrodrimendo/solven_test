/**
 *
 */
package com.affirm.common.service;

import com.affirm.common.util.NetworkProfile;
import com.affirm.system.configuration.Configuration;
import com.google.api.client.auth.oauth2.TokenResponse;
import org.json.JSONObject;

/**
 * @author jrodriguez
 */
public interface OauthService {

    TokenResponse getAccessToken(Configuration.OauthNetwork network, String code) throws Exception;

//    String getUserPrincipalEmail(Configuration.OauthNetwork network, String accessToken) throws Exception;

    NetworkProfile getNetworkProfile(Configuration.OauthNetwork network, String accessToken) throws Exception;

    JSONObject requestUserData(Configuration.OauthNetwork network, String accessToken, String url) throws Exception;

    Integer requestFriendsCount(Configuration.OauthNetwork network, String accessToken) throws Exception;
}
