package com.affirm.common;

import java.util.List;

public class EntityProductParamUserFileUploadValidationConfig {

    public static final String UPLOAD_VALIDATION_DOCUMENT_OCR_TYPE = "DOCUMENT_OCR";
    public static final String UPLOAD_VALIDATION_SELFIE_FACIAL_ANALYSIS_TYPE = "SELFIE_FACIAL_ANALYSIS";
    public static final String UPLOAD_VALIDATION_SELFIE_LABEL_DETECTION_TYPE = "SELFIE_LABEL_DETECTION";

    private Integer maxRetries;
    private List<UploadValidations> uploadValidations;

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public List<UploadValidations> getUploadValidations() {
        return uploadValidations;
    }

    public void setUploadValidations(List<UploadValidations> uploadValidations) {
        this.uploadValidations = uploadValidations;
    }

    public static class UploadValidations{
        private String type;
        private UploadValidationsConfig config;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public UploadValidationsConfig getConfig() {
            return config;
        }

        public void setConfig(UploadValidationsConfig config) {
            this.config = config;
        }
    }

    public static class UploadValidationsConfig{
        private Double qualityMinPercentage;
        private Double confidenceMinPercentage;
        private Boolean searchDocumentNumber;
        private Integer minCoincidences;
        private List<UploadValidationsConfigLabel> includeLabels;
        private List<UploadValidationsConfigLabel> excludeLabels;

        public Double getQualityMinPercentage() {
            return qualityMinPercentage;
        }

        public void setQualityMinPercentage(Double qualityMinPercentage) {
            this.qualityMinPercentage = qualityMinPercentage;
        }

        public Double getConfidenceMinPercentage() {
            return confidenceMinPercentage;
        }

        public void setConfidenceMinPercentage(Double confidenceMinPercentage) {
            this.confidenceMinPercentage = confidenceMinPercentage;
        }

        public Boolean getSearchDocumentNumber() {
            return searchDocumentNumber;
        }

        public void setSearchDocumentNumber(Boolean searchDocumentNumber) {
            this.searchDocumentNumber = searchDocumentNumber;
        }

        public Integer getMinCoincidences() {
            return minCoincidences;
        }

        public void setMinCoincidences(Integer minCoincidences) {
            this.minCoincidences = minCoincidences;
        }

        public List<UploadValidationsConfigLabel> getIncludeLabels() {
            return includeLabels;
        }

        public void setIncludeLabels(List<UploadValidationsConfigLabel> includeLabels) {
            this.includeLabels = includeLabels;
        }

        public List<UploadValidationsConfigLabel> getExcludeLabels() {
            return excludeLabels;
        }

        public void setExcludeLabels(List<UploadValidationsConfigLabel> excludeLabels) {
            this.excludeLabels = excludeLabels;
        }
    }

    public static class UploadValidationsConfigLabel{
        private String label;
        private Double minPercentage;
        private Double maxPercentage;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public Double getMinPercentage() {
            return minPercentage;
        }

        public void setMinPercentage(Double minPercentage) {
            this.minPercentage = minPercentage;
        }

        public Double getMaxPercentage() {
            return maxPercentage;
        }

        public void setMaxPercentage(Double maxPercentage) {
            this.maxPercentage = maxPercentage;
        }
    }
}
