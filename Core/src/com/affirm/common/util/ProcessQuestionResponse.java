package com.affirm.common.util;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by jrodriguez on 07/07/16.
 */
public class ProcessQuestionResponse {

    public static ResponseEntity<String> goToQuestion(int questionId, String token) {
        JSONObject json = new JSONObject();
        json.put("type", "processQuestion");
        json.put("goto", questionId);
        json.put("token", token);
        return AjaxResponse.ok(json.toString());
    }

    public static ResponseEntity<String> goToQuestion(int questionId) {
        JSONObject json = new JSONObject();
        json.put("type", "processQuestion");
        json.put("goto", questionId);
        return AjaxResponse.ok(json.toString());
    }

    public static void writeGoToQuestionToResponse(int questionId, HttpServletResponse response) throws Exception{
        JSONObject json = new JSONObject();
        json.put("type", "processQuestion");
        json.put("goto", questionId);
        AjaxResponse.writeOkToResponse(json.toString(), response);
    }

}
