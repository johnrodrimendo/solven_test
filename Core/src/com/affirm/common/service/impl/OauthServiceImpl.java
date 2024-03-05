/**
 *
 */
package com.affirm.common.service.impl;

import com.affirm.common.service.OauthService;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.NetworkProfile;
import com.affirm.system.configuration.Configuration;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * @author jrodriguez
 */

@Service("oauthService")
public class OauthServiceImpl implements OauthService {

    private static Logger logger = Logger.getLogger(OauthServiceImpl.class);

    @Override
    public TokenResponse getAccessToken(Configuration.OauthNetwork network, String code) throws Exception {
        return new AuthorizationCodeTokenRequest(new NetHttpTransport(), new JacksonFactory(),
                new GenericUrl(network.getTokenUrl()), code)
                .setRedirectUri(network.getRedirectUri())
                .set("client_id", network.getClientId())
                .set("client_secret", network.getClientSecret())
                .execute();
    }

//    @Override
//    public String getUserPrincipalEmail(Configuration.OauthNetwork network, String accessToken) throws Exception {
//        JSONObject responseJson = requestUserData(network, accessToken, network.getProfileUrl());
//        if (responseJson != null) {
//            switch (network) {
//                case GOOGLE:
//                    return responseJson.getJSONArray("emails").getJSONObject(0).getString("value");
//                case WINDOWS:
//                    return responseJson.getJSONObject("emails").getString("account");
//                case YAHOO:
//                    JSONArray jsonCorreos = responseJson.getJSONObject("query").getJSONObject("results").getJSONObject("profile").getJSONArray("emails");
//                    for (int i = 0; i < jsonCorreos.length(); i++) {
//                        if (jsonCorreos.getJSONObject(i).getBoolean("primary")) {
//                            return jsonCorreos.getJSONObject(i).getString("handle");
//                        }
//                    }
//                    break;
//            }
//        }
//        return null;
//    }

    @Override
    public NetworkProfile getNetworkProfile(Configuration.OauthNetwork network, String accessToken) throws Exception {
        JSONObject responseJson = requestUserData(network, accessToken, network.getProfileUrl());
        if (responseJson != null) {
            NetworkProfile networkProfile = new NetworkProfile();
            networkProfile.setResponse(responseJson);
            logger.debug("Return from network: " + responseJson.toString());
            switch (network) {
                case GOOGLE:
                    JSONArray jsonEmails = responseJson.getJSONArray("emailAddresses");
                    for (int i = 0; i < jsonEmails.length(); i++) {
                        if (jsonEmails.getJSONObject(i).getJSONObject("metadata").getBoolean("primary")) {
                            networkProfile.setEmail(jsonEmails.getJSONObject(i).getString("value"));
                            break;
                        }
                    }
                    networkProfile.setId(responseJson.getJSONObject("metadata").getJSONArray("sources").getJSONObject(0).getString("id"));
                    break;
                case WINDOWS:
                    networkProfile.setEmail(responseJson.getJSONObject("emails").getString("account"));
                    networkProfile.setId(JsonUtil.getStringFromJson(responseJson, "id", null));
                    break;
                case YAHOO:
                    JSONObject jsonProfile = responseJson.getJSONObject("query").getJSONObject("results").getJSONObject("profile");
                    if (JsonUtil.getJsonArrayFromJson(jsonProfile, "emails", null) != null) {
                        JSONArray jsonCorreos = JsonUtil.getJsonArrayFromJson(jsonProfile, "emails", null);
                        for (int i = 0; i < jsonCorreos.length(); i++) {
                            if (jsonCorreos.getJSONObject(i).getBoolean("primary")) {
                                networkProfile.setEmail(jsonCorreos.getJSONObject(i).getString("handle"));
                                break;
                            }
                        }
                    } else if (JsonUtil.getJsonObjectFromJson(jsonProfile, "emails", null) != null) {
                        networkProfile.setEmail(jsonProfile.getJSONObject("emails").getString("handle"));
                    }

                    networkProfile.setId(responseJson.getJSONObject("query").getJSONObject("results").getJSONObject("profile").getString("guid"));
                    break;
                case FACEBOOK:
                    networkProfile.setEmail(JsonUtil.getStringFromJson(responseJson, "email", null));
                    networkProfile.setId(JsonUtil.getStringFromJson(responseJson, "id", null));
                    break;
                case LINKEDIN:
                    networkProfile.setEmail(JsonUtil.getStringFromJson(responseJson, "emailAddress", null));
                    networkProfile.setId(JsonUtil.getStringFromJson(responseJson, "id", null));
                    break;
                case MERCADOLIBRE:
                    networkProfile.setEmail(JsonUtil.getStringFromJson(responseJson, "email", null));
                    networkProfile.setId(JsonUtil.getStringFromJson(responseJson, "id", null));
                    break;
            }
            return networkProfile;
        }
        return null;
    }

    @Override
    public JSONObject requestUserData(Configuration.OauthNetwork network, String accessToken, String url) throws Exception {
        if (network == Configuration.OauthNetwork.MERCADOLIBRE) {
            url = url + "?access_token=" + accessToken;
        }
        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) {
                System.out.println("Seting authorization: "+"Bearer " + accessToken);
                request.getHeaders().setAuthorization("Bearer " + accessToken);
                request.getHeaders().setAccept("application/json");
            }
        });
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(url));
        HttpResponse response = request.execute();
        try {
            if (response.isSuccessStatusCode()) {
                String responseString = response.parseAsString();
                return new JSONObject(responseString);
            }
        } finally {
            response.disconnect();
        }
        return null;
    }

    @Override
    public Integer requestFriendsCount(Configuration.OauthNetwork network, String accessToken) throws Exception {
        String url;
        switch (network) {
            case FACEBOOK:
                url = "https://graph.facebook.com/v2.8/me/friends?format=json";
                break;
            default:
                return null;
        }

        HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) {
                request.getHeaders().setAuthorization("Bearer " + accessToken);
                request.getHeaders().setAccept("application/json");
            }
        });
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(url));
        HttpResponse response = request.execute();
        try {
            if (response.isSuccessStatusCode()) {
                String responseString = response.parseAsString();
                JSONObject responseJson = new JSONObject(responseString);
                JSONObject jsonSummary = JsonUtil.getJsonObjectFromJson(responseJson, "summary", null);
                return jsonSummary != null ? JsonUtil.getIntFromJson(jsonSummary, "total_count", null) : null;
            }
        } finally {
            response.disconnect();
        }
        return null;
    }
}
