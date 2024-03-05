package com.affirm.common.model.pagoefectivo;

public class PagoEfectivoCreateAuthorizationResponse {

    private int code;
    private String message;
    private AuthorizationData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AuthorizationData getData() {
        return data;
    }

    public void setData(AuthorizationData data) {
        this.data = data;
    }

    public static class AuthorizationData {

        private String token;
        private String codeService;
        private String tokenStart;
        private String tokenExpires;

        public AuthorizationData() {
            super();
        }
        public String getToken() {
            return token;
        }
        public void setToken(String token) {
            this.token = token;
        }
        public String getCodeService() {
            return codeService;
        }
        public void setCodeService(String codeService) {
            this.codeService = codeService;
        }
        public String getTokenStart() {
            return tokenStart;
        }
        public void setTokenStart(String tokenStart) {
            this.tokenStart = tokenStart;
        }
        public String getTokenExpires() {
            return tokenExpires;
        }
        public void setTokenExpires(String tokenExpires) {
            this.tokenExpires = tokenExpires;
        }

    }



}
