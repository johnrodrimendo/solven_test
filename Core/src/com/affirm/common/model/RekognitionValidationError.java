package com.affirm.common.model;

import java.util.List;

public class RekognitionValidationError {

    private List<RekognitionValidationErrorData> data;

    public List<RekognitionValidationErrorData> getData() {
        return data;
    }

    public RekognitionValidationError(List<RekognitionValidationErrorData> data){
        this.data = data;
    }

    public void setData(List<RekognitionValidationErrorData> data) {
        this.data = data;
    }

    public static class RekognitionValidationErrorData{
        private Integer userFileId;
        private String errorMessage;

        public RekognitionValidationErrorData(Integer userFileId, String errorMessage){
            this.userFileId = userFileId;
            this.errorMessage = errorMessage;
        }

        public Integer getUserFileId() {
            return userFileId;
        }

        public void setUserFileId(Integer userFileId) {
            this.userFileId = userFileId;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
