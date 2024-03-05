package com.affirm.apirest.model;

public class ApiRestErrorResponse {

    private ErrorData error;

    ApiRestErrorResponse(String code, String message){
        error = new ErrorData();
        error.code = code;
        error.message = message;
    }

    public static class ErrorData{
        private String code;
        private String message;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }


    public ErrorData getError() {
        return error;
    }

    public void setError(ErrorData error) {
        this.error = error;
    }
}
