package com.affirm.bancoazteca.model;

import java.util.List;

public class ReniecLoginResponse extends ReniecComonResponse{

    private ReniecAuthData Data;

    public ReniecAuthData getData() {
        return Data;
    }

    public void setData(ReniecAuthData data) {
        Data = data;
    }

    public static class ReniecAuthData{
        private String Token;
        private String RefreshToken;
        private Boolean Success;
        private List<String> Errors;

        public String getToken() {
            return Token;
        }

        public void setToken(String token) {
            Token = token;
        }

        public String getRefreshToken() {
            return RefreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            RefreshToken = refreshToken;
        }

        public Boolean getSuccess() {
            return Success;
        }

        public void setSuccess(Boolean success) {
            Success = success;
        }

        public List<String> getErrors() {
            return Errors;
        }

        public void setErrors(List<String> errors) {
            Errors = errors;
        }
    }

}
