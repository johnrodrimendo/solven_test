package com.affirm.common.util;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by jrodriguez on 07/07/16.
 */
public class AjaxResponse {

    public static ResponseEntity<String> errorFormValidation(String errorsJson) {
        JSONObject json = new JSONObject(errorsJson);
        json.put("type", "formValidation");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(json.toString());
    }

    public static ResponseEntity<String> errorMessage(String errorMessage) {
        JSONObject json = new JSONObject();
        json.put("type", "message");
        json.put("message", errorMessage);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(json.toString());
    }

    public static ResponseEntity<String> errorMessageWithLink(String errorMessage, String link) {
        JSONObject json = new JSONObject();
        json.put("type", "errorWithLink");
        json.put("message", errorMessage);
        json.put("redirectLink", link);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(json.toString());
    }

    public static ResponseEntity<String> message(String errorMessage) {
        JSONObject json = new JSONObject();
        json.put("type", "message");
        json.put("message", errorMessage);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(json.toString());
    }

    public static ResponseEntity<String> errorForbidden() {
        JSONObject json = new JSONObject();
        json.put("type", "message");
        json.put("message", "No estás autorizado para acceder a este recurso");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(json.toString());
    }

    public static ResponseEntity<String> errorShowRetryMessage(String errorMessage) {
        JSONObject json = new JSONObject();
        json.put("type", "message");
        json.put("message", errorMessage);
        return ResponseEntity.status(HttpStatus.NOT_EXTENDED).body(json.toString());
    }

    public static ResponseEntity<String> redirect(String url) {
        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).body(url);
    }

    public static ResponseEntity<String> ok(String body) {
        return ResponseEntity.ok(body == null ? "" : body); 
    }

    public static void writeErrorMessageToResponse(String errorMessage, HttpServletResponse response) throws Exception{
            writeErrorMessageToResponse(errorMessage,response,"message");
    }

    public static void writeErrorMessageToResponse(String errorMessage, HttpServletResponse response, String type) throws Exception{
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("message", errorMessage);
        response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("text/plain");
        response.getWriter().println(json.toString());
    }

    public static void sendReloadPage(HttpServletResponse response) throws Exception{
        JSONObject json = new JSONObject();
        json.put("type", "reloadPage");
        response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("text/plain");
        response.getWriter().println(json.toString());
    }

    public static void writeOkToResponse(String body, HttpServletResponse response) throws Exception{
        response.setStatus( HttpServletResponse.SC_OK);
        response.setContentType("text/plain");
        if(body != null)
            response.getWriter().println(body);
    }

    public static ResponseEntity<String> errorMessageWithCustomMessage(String errorMessage, String customType) {
        JSONObject json = new JSONObject();
        if(customType == null) customType = "message";
        json.put("type", customType);
        json.put("message", errorMessage);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(json.toString());
    }

}
