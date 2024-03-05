package com.affirm.common.model;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class RekognitionProData implements Serializable {

    public static final char REJECTED_STATUS = 'R';
    public static final char APPROVED_STATUS = 'S';
    public static final char REVIEW_NEEDED_STATUS = 'M';

    public static final double MINIMUM_FACE_MATCHING_VALUE = 80.0;

    private Integer selfieDocumentId;
    private Integer frontDocumentId;
    private Integer backDocumentId;
    private Double faceMatching;
    private Character status;
    private List<DataLabels> data = new ArrayList<>();
    private Date processDate = new Date();
    private Integer userFilesIdDniMerged;
    private List<String> additionalErrors = new ArrayList<>();

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

    public Double getFaceMatching() {
        return faceMatching;
    }

    public void setFaceMatching(Double faceMatching) {
        this.faceMatching = faceMatching;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public List<DataLabels> getData() {
        return data;
    }

    public void setData(List<DataLabels> data) {
        this.data = data;
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

    public List<String> getErrors(){
        List<String> errors = new ArrayList<>();
        if(data != null && !data.isEmpty()){
            List<DataLabels> errorsData = data.stream().filter( e-> e.getError() != null && e.getError().getReason() != null).collect(Collectors.toList());
            for (DataLabels errorsDatum : errorsData) {
               errors.add(errorsDatum.getError().getReason());
            }
        }
        if(additionalErrors != null) errors.addAll(additionalErrors);
        return errors;
    }

    public List<String> getAdditionalErrors() {
        return additionalErrors;
    }

    public void setAdditionalErrors(List<String> additionalErrors) {
        this.additionalErrors = additionalErrors;
    }
}
