package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class UserNetworkToken implements Serializable {

    private Integer id;
    private Integer userId;
    private Character network;
    private String accessToken;
    private String email;
    private Date registerDate;
    private String refreshToken;
    private JSONObject contacts;
    private JSONObject profile;

    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "network_token_id", null));
        setUserId(JsonUtil.getIntFromJson(json, "user_id", null));
        setNetwork(JsonUtil.getCharacterFromJson(json, "network_provider", null));
        setAccessToken(JsonUtil.getStringFromJson(json, "network_token", null));
        setEmail(JsonUtil.getStringFromJson(json, "email", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setRefreshToken(JsonUtil.getStringFromJson(json, "refresh_token", null));
        setContacts(JsonUtil.getJsonObjectFromJson(json, "contacts", null));
        setProfile(JsonUtil.getJsonObjectFromJson(json, "profile", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Character getNetwork() {
        return network;
    }

    public void setNetwork(Character network) {
        this.network = network;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public JSONObject getContacts() {
        return contacts;
    }

    public void setContacts(JSONObject contacts) {
        this.contacts = contacts;
    }

    public JSONObject getProfile() {
        return profile;
    }

    public void setProfile(JSONObject profile) {
        this.profile = profile;
    }
}
