package com.affirm.client.util;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

/**
 * Created by john on 27/02/17.
 */
public class ApiWsResponse {

    public static ResponseEntity<String> ok(Object obj) {
        JSONObject response = new JSONObject();
        response.put("status", "OK");
        if (obj != null) {
            if (obj instanceof JSONObject || obj instanceof JSONArray) {
                response.put("results", obj);
            } else {
                response.put("results", new JSONObject(new Gson().toJson(obj)));
            }
        }
        return ResponseEntity.ok(response.toString());
    }

    public static ResponseEntity<String> error(String errorCode, String errorMessage) {
        JSONObject response = new JSONObject();
        response.put("status", "ERROR");
        if (errorCode != null) {
            response.put("errorCode", errorCode);
        }
        if (errorMessage != null) {
            response.put("message", errorMessage);
        }
        return ResponseEntity.ok(response.toString());
    }

}
