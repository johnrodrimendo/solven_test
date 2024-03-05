package com.affirm.common;

import java.util.List;

public class EntityProductParamIdentityValidationConfig {

    private Double rekognitionMinPercentage;
    private Boolean runMati;
    private Boolean runRekognition;
    private Boolean runRekognitionPro;
    private Boolean runRekognitionReniec;
    private Boolean runLivenessProcess;
    private Boolean runIPCountryValidation;
    private List<String> allowedCountries;
    private List<String> allowedOrganizationsForRestOrigin;
    private EntityProductParamUserFileUploadValidationConfig userFileUploadValidation;

    private List<EntityProductParamIdentityValidationParamsConfig> validationParamsConfig;
    private Boolean runISOValidation;

    public Double getRekognitionMinPercentage() {
        return rekognitionMinPercentage;
    }

    public void setRekognitionMinPercentage(Double rekognitionMinPercentage) {
        this.rekognitionMinPercentage = rekognitionMinPercentage;
    }

    public Boolean getRunMati() {
        return runMati;
    }

    public void setRunMati(Boolean runMati) {
        this.runMati = runMati;
    }

    public Boolean getRunRekognition() {
        return runRekognition;
    }

    public void setRunRekognition(Boolean runRekognition) {
        this.runRekognition = runRekognition;
    }

    public Boolean getRunRekognitionPro() {
        return runRekognitionPro;
    }

    public void setRunRekognitionPro(Boolean runRekognitionPro) {
        this.runRekognitionPro = runRekognitionPro;
    }

    public Boolean getRunRekognitionReniec() {
        return runRekognitionReniec;
    }

    public void setRunRekognitionReniec(Boolean runRekognitionReniec) {
        this.runRekognitionReniec = runRekognitionReniec;
    }

    public EntityProductParamUserFileUploadValidationConfig getUserFileUploadValidation() {
        return userFileUploadValidation;
    }

    public void setUserFileUploadValidation(EntityProductParamUserFileUploadValidationConfig userFileUploadValidation) {
        this.userFileUploadValidation = userFileUploadValidation;
    }

    public List<EntityProductParamIdentityValidationParamsConfig> getValidationParamsConfig() {
        return validationParamsConfig;
    }

    public void setValidationParamsConfig(List<EntityProductParamIdentityValidationParamsConfig> validationParamsConfig) {
        this.validationParamsConfig = validationParamsConfig;
    }

    public Boolean getRunLivenessProcess() {
        return runLivenessProcess;
    }

    public void setRunLivenessProcess(Boolean runLivenessProcess) {
        this.runLivenessProcess = runLivenessProcess;
    }

    public Boolean getRunIPCountryValidation() {
        return runIPCountryValidation;
    }

    public void setRunIPCountryValidation(Boolean runIPCountryValidation) {
        this.runIPCountryValidation = runIPCountryValidation;
    }

    public List<String> getAllowedCountries() {
        return allowedCountries;
    }

    public void setAllowedCountries(List<String> allowedCountries) {
        this.allowedCountries = allowedCountries;
    }

    public List<String> getAllowedOrganizationsForRestOrigin() {
        return allowedOrganizationsForRestOrigin;
    }

    public void setAllowedOrganizationsForRestOrigin(List<String> allowedOrganizationsForRestOrigin) {
        this.allowedOrganizationsForRestOrigin = allowedOrganizationsForRestOrigin;
    }

    public Boolean getRunISOValidation() { return runISOValidation; }

    public void setRunISOValidation(Boolean runISOValidation) { this.runISOValidation = runISOValidation; }
}
