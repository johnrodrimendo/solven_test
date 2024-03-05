package com.affirm.common.model;

import com.affirm.common.EntityProductParamIdentityValidationParamsConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class RekognitionReniecData implements Serializable {

    public static final char REJECTED_STATUS = 'R';
    public static final char APPROVED_STATUS = 'S';
    public static final char REVIEW_NEEDED_STATUS = 'M';

    public static final char SELFIE_WITH_DOCUMENT_PROCESS_TYPE = 'S';
    public static final char SELFIE_WITH_RENIEC_PROCESS_TYPE = 'R';
    public static final char DOCUMENT_WITH_RENIEC_PROCESS_TYPE = 'D';

    public static final double MINIMUM_FACE_MATCHING_VALUE = 80.0;
    public static final double LIMIT_FACE_MATCHING_FOR_REJECT_VALUE = 60.0;
    public static final double MINIMUM_VALUE_FOR_SELFIE_RENIEC = 90.0;
    private Integer selfieDocumentId;
    private Integer frontDocumentId;
    private Integer backDocumentId;

    private Double faceMatching;
    private Double selfieReniecFaceMatching;
    private Double docReniecFaceMatching;
    private Character status;
    private Character selfieDocStatus;
    private Character selfieReniecStatus;
    private Character docReniecStatus;
    private List<DataLabels> data = new ArrayList<>();
    private List<DataLabels> selfieReniecData = new ArrayList<>();
    private List<DataLabels> docReniecData = new ArrayList<>();
    private Date processDate = new Date();
    private Integer userFilesIdDniMerged;
    private List<String> additionalErrors = new ArrayList<>();

    private List<RekognitionValidationData> selfieDocDataValidations = new ArrayList<>();
    private List<RekognitionValidationData> selfieReniecDataValidations = new ArrayList<>();
    private List<RekognitionValidationData> docReniecDataValidations = new ArrayList<>();

    public Integer getSelfieDocumentId() {
        return selfieDocumentId;
    }

    public void setSelfieDocumentId(Integer selfieDocumentId) {
        this.selfieDocumentId = selfieDocumentId;
    }

    public Integer getFrontDocumentId() {
        return frontDocumentId;
    }

    public void setFrontDocumentId(Integer frontDocumentId) {
        this.frontDocumentId = frontDocumentId;
    }

    public Integer getBackDocumentId() {
        return backDocumentId;
    }

    public void setBackDocumentId(Integer backDocumentId) {
        this.backDocumentId = backDocumentId;
    }



    public Double getSelfieReniecFaceMatching() {
        return selfieReniecFaceMatching;
    }

    public void setSelfieReniecFaceMatching(Double selfieReniecFaceMatching) {
        this.selfieReniecFaceMatching = selfieReniecFaceMatching;
    }

    public Double getDocReniecFaceMatching() {
        return docReniecFaceMatching;
    }

    public void setDocReniecFaceMatching(Double docReniecFaceMatching) {
        this.docReniecFaceMatching = docReniecFaceMatching;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public Double getFaceMatching() {
        return faceMatching;
    }

    public void setFaceMatching(Double faceMatching) {
        this.faceMatching = faceMatching;
    }

    public List<DataLabels> getData() {
        return data;
    }

    public void setData(List<DataLabels> data) {
        this.data = data;
    }

    public List<DataLabels> getSelfieReniecData() {
        return selfieReniecData;
    }

    public void setSelfieReniecData(List<DataLabels> selfieReniecData) {
        this.selfieReniecData = selfieReniecData;
    }

    public List<DataLabels> getDocReniecData() {
        return docReniecData;
    }

    public void setDocReniecData(List<DataLabels> docReniecData) {
        this.docReniecData = docReniecData;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public Integer getUserFilesIdDniMerged() {
        return userFilesIdDniMerged;
    }

    public void setUserFilesIdDniMerged(Integer userFilesIdDniMerged) {
        this.userFilesIdDniMerged = userFilesIdDniMerged;
    }

    public List<String> getAdditionalErrors() {
        return additionalErrors;
    }

    public void setAdditionalErrors(List<String> additionalErrors) {
        this.additionalErrors = additionalErrors;
    }

    public Character getSelfieDocStatus() {
        return selfieDocStatus;
    }

    public void setSelfieDocStatus(Character selfieDocStatus) {
        this.selfieDocStatus = selfieDocStatus;
    }

    public Character getSelfieReniecStatus() {
        return selfieReniecStatus;
    }

    public void setSelfieReniecStatus(Character selfieReniecStatus) {
        this.selfieReniecStatus = selfieReniecStatus;
    }

    public Character getDocReniecStatus() {
        return docReniecStatus;
    }

    public void setDocReniecStatus(Character docReniecStatus) {
        this.docReniecStatus = docReniecStatus;
    }

    /*    public List<String> getErrors(){
        List<String> errors = new ArrayList<>();
        if(data != null && !data.isEmpty()){
            List<DataLabels> errorsData = data.stream().filter( e-> e.getError() != null && e.getError().getReason() != null).collect(Collectors.toList());
            for (DataLabels errorsDatum : errorsData) {
               errors.add(errorsDatum.getError().getReason());
            }
        }
        if(additionalErrors != null) errors.addAll(additionalErrors);
        return errors;
    }*/

    public List<RekognitionValidationData> getSelfieDocDataValidations() {
        return selfieDocDataValidations;
    }

    public void setSelfieDocDataValidations(List<RekognitionValidationData> selfieDocDataValidations) {
        this.selfieDocDataValidations = selfieDocDataValidations;
    }

    public List<RekognitionValidationData> getSelfieReniecDataValidations() {
        return selfieReniecDataValidations;
    }

    public void setSelfieReniecDataValidations(List<RekognitionValidationData> selfieReniecDataValidations) {
        this.selfieReniecDataValidations = selfieReniecDataValidations;
    }

    public List<RekognitionValidationData> getDocReniecDataValidations() {
        return docReniecDataValidations;
    }

    public void setDocReniecDataValidations(List<RekognitionValidationData> docReniecDataValidations) {
        this.docReniecDataValidations = docReniecDataValidations;
    }

    public Double getMinimunFaceMatchingValue(){
        if(faceMatching != null && selfieReniecFaceMatching != null && docReniecFaceMatching != null){
            return Math.min(Math.min(faceMatching, selfieReniecFaceMatching), docReniecFaceMatching);
        }
        else if(faceMatching != null && selfieReniecFaceMatching != null){
            return Math.min(faceMatching, selfieReniecFaceMatching);
        }
        else if(faceMatching != null && docReniecFaceMatching != null){
            return Math.min(faceMatching, docReniecFaceMatching);
        }
        return faceMatching;
    }

    public List<RekognitionValidationData> getSelfieDocDataValidationsWhithoutFaceMatching() {
        return selfieDocDataValidations != null ? selfieDocDataValidations.stream().filter(e -> !e.getType().equalsIgnoreCase(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FACE_MATCHING)).collect(Collectors.toList()) : null;
    }
    public List<RekognitionValidationData> getSelfieReniecDataValidationsWhithoutFaceMatching() {
        return selfieReniecDataValidations != null ? selfieReniecDataValidations.stream().filter(e -> !e.getType().equalsIgnoreCase(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FACE_MATCHING)).collect(Collectors.toList()) : null;
    }
    public List<RekognitionValidationData> getDocReniecDataValidationsWhithoutFaceMatching() {
        return docReniecDataValidations != null ? docReniecDataValidations.stream().filter(e -> !e.getType().equalsIgnoreCase(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FACE_MATCHING)).collect(Collectors.toList()) : null;
    }

    public RekognitionValidationData getSelfieDocDataValidationFaceMatching() {
        return selfieDocDataValidations != null ? selfieDocDataValidations.stream().filter(e -> e.getType().equalsIgnoreCase(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FACE_MATCHING)).findFirst().orElse(null) : null;
    }
    public RekognitionValidationData getSelfieReniecDataValidationFaceMatching() {
        return selfieReniecDataValidations != null ? selfieReniecDataValidations.stream().filter(e -> e.getType().equalsIgnoreCase(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FACE_MATCHING)).findFirst().orElse(null) : null;
    }
    public RekognitionValidationData getDocReniecDataValidationFaceMatching() {
        return docReniecDataValidations != null ? docReniecDataValidations.stream().filter(e -> e.getType().equalsIgnoreCase(EntityProductParamIdentityValidationParamsConfig.LABEL_KEY_FACE_MATCHING)).findFirst().orElse(null) : null;
    }
}
