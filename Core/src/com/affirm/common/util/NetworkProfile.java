package com.affirm.common.util;

import org.json.JSONObject;

/**
 * Created by john on 23/12/16.
 */
public class NetworkProfile {

    private String id;
    private String email;
    private JSONObject response;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public JSONObject getResponse() {
        return response;
    }

    public void setResponse(JSONObject response) {
        this.response = response;
    }
}
