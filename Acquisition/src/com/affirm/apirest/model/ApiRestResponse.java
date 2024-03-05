package com.affirm.apirest.model;

import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by jrodriguez on 07/07/16.
 */
public class ApiRestResponse {

    public static final String INTERNAL_SERVER_ERROR = "000";
    public static final String ERROR_FORBIDDEN = "001";
    public static final String ERROR_INVALID_USER = "002";
    public static final String ERROR_INVALID_TOKEN = "003";
    public static final String ERROR_INVALID_FIELDS = "004";

    private Object data;

    public static ResponseEntity<ApiRestErrorResponse> errorMessage(String errorCode) {
        String errorMessage = null;
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if(errorCode != null){
            switch (errorCode){
                case INTERNAL_SERVER_ERROR: errorMessage = "Ha ocurrido un error insesperado"; status = HttpStatus.INTERNAL_SERVER_ERROR; break;
                case ERROR_FORBIDDEN: errorMessage = "Acción no permitida"; status = HttpStatus.FORBIDDEN; break;
                case ERROR_INVALID_USER: errorMessage = "El usuario es incorrecto"; status = HttpStatus.UNAUTHORIZED; break;
                case ERROR_INVALID_TOKEN: errorMessage = "El token no es válido"; status = HttpStatus.UNAUTHORIZED; break;
                case ERROR_INVALID_FIELDS: errorMessage = "Parámetros inválidos"; status = HttpStatus.UNPROCESSABLE_ENTITY; break;
            }
        }

        ApiRestErrorResponse errorResponse = new ApiRestErrorResponse(errorCode, errorMessage);
        return ResponseEntity.status(status).body(errorResponse);
    }

    public static ResponseEntity<ApiRestOkResponse> ok(Object body) {
        ApiRestOkResponse apiRestOkResponse = new ApiRestOkResponse();
        apiRestOkResponse.setData(body);
        return ResponseEntity.ok(apiRestOkResponse);
    }


}
