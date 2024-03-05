package com.affirm.entityExt.models;

import com.affirm.aws.RecognitionResultsPainter;
import com.affirm.bancoazteca.model.ReniecDataResponse;
import com.affirm.common.model.RekognitionProData;
import com.affirm.common.model.RekognitionReniecData;
import com.affirm.common.model.RekognitionValidationData;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.LoanApplicationApprovalValidation;
import com.affirm.common.model.transactional.LoanOffer;
import com.affirm.common.model.transactional.MatiResult;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SummaryIdentityValidationPainter {

    private MatiResult matiResult;
    private LoanApplicationApprovalValidation identityApprovalValidation;
    private Double rekognitionMinPercentage;
    private RecognitionResultsPainter lastRekognition;
    private RekognitionProData rekognitionProResult;
    private RekognitionReniecData rekognitionReniecData;
    private ReniecDataResponse.ReniecData reniecData;

    private DocumentationExtractedDataFromDatalabels documentationData;
    private Integer loanApplicationId;

    public String getIdentityApprovalValidationDisapprovalMessage(){
        if(identityApprovalValidation != null &&
                identityApprovalValidation.getApproved() != null &&
                !identityApprovalValidation.getApproved() &&
                identityApprovalValidation.getMessage() != null){
            return identityApprovalValidation.getMessage();
        }
        return null;
    }

    public boolean getShowValidationStatusChangeButton(){
        if(identityApprovalValidation == null  || (identityApprovalValidation.getApproved() != null && !identityApprovalValidation.getApproved()))
            return true;
        return false;
    }

    public String getMatiResultStatus(){
        if(matiResult != null && matiResult.getStatus() != null){
            switch (matiResult.getStatus()){
                case MatiResult.MATI_STATUS_VERIFIED:
                    return "Aprobado";
                case MatiResult.MATI_STATUS_REJECTED:
                    return "Rechazado";
                case MatiResult.MATI_STATUS_REVIEW:
                    return "Para revisión";
            }
        }
        return null;
    }

    public String getResultStatus(){
        if(rekognitionMinPercentage != null && lastRekognition != null){
            if(getLastRekognitionValue() >= rekognitionMinPercentage)
                return "Aprobado";
            else
                return "Rechazado";
        }
        return getMatiResultStatus();
    }

    public String getRekognitionStatus(){
        if(rekognitionMinPercentage != null && lastRekognition != null){
            if(getLastRekognitionValue() >= rekognitionMinPercentage)
                return "Aprobada <br/> (Superó el % de solicitud)";
            else
                return "Rechazada <br/> (No superó el % de solicitud)";
        }
        return null;
    }

    public Integer getLastRekognitionValue(){
        if(lastRekognition == null || lastRekognition.getResults() == null || lastRekognition.getResults().isEmpty()) return null;
        return lastRekognition.getResults().get(lastRekognition.getResults().size() -1).getHighSimilarity();
    }

    public MatiResult getMatiResult() {
        return matiResult;
    }

    public void setMatiResult(MatiResult matiResult) {
        this.matiResult = matiResult;
    }

    public LoanApplicationApprovalValidation getIdentityApprovalValidation() {
        return identityApprovalValidation;
    }

    public void setIdentityApprovalValidation(LoanApplicationApprovalValidation identityApprovalValidation) {
        this.identityApprovalValidation = identityApprovalValidation;
    }

    public Double getRekognitionMinPercentage() {
        return rekognitionMinPercentage;
    }

    public void setRekognitionMinPercentage(Double rekognitionMinPercentage) {
        this.rekognitionMinPercentage = rekognitionMinPercentage;
    }

    public RecognitionResultsPainter getLastRekognition() {
        return lastRekognition;
    }

    public void setLastRekognition(RecognitionResultsPainter lastRekognition) {
        this.lastRekognition = lastRekognition;
    }

    public RekognitionProData getRekognitionProResult() {
        return rekognitionProResult;
    }

    public void setRekognitionProResult(RekognitionProData rekognitionProResult) {
        this.rekognitionProResult = rekognitionProResult;
    }

    public RekognitionReniecData getRekognitionReniecData() {
        return rekognitionReniecData;
    }

    public void setRekognitionReniecData(RekognitionReniecData rekognitionReniecData) {
        this.rekognitionReniecData = rekognitionReniecData;
    }

    public ReniecDataResponse.ReniecData getReniecData() {
        return reniecData;
    }

    public void setReniecData(ReniecDataResponse.ReniecData reniecData) {
        this.reniecData = reniecData;
    }

    public boolean hasIdentityValidationResult(){
        return getMatiResult() != null || getRekognitionProResult() != null || getLastRekognition() != null || getRekognitionReniecData() != null;
    }

    public boolean hasReniecResultData(){
        return getReniecData() != null;
    }

    public String getIdentityValidationType(){
        if(getMatiResult() != null) return "MATI";
        if(getRekognitionProResult() != null) return "REKOGNITION_PRO";
        if(getLastRekognition() != null) return "REKOGNITION";
        if(getRekognitionReniecData() != null) return "REKOGNITION_RENIEC";
        return null;
    }

    public Date getProcesingDate(){
        if(getMatiResult() != null) return getMatiResult().getRegisterDate();
        if(getRekognitionProResult() != null) return getRekognitionProResult().getProcessDate();
        if(getLastRekognition() != null) return getLastRekognition().getLastRekognitionResult().getProcessDate();
        if(getRekognitionReniecData() != null) return getRekognitionReniecData().getProcessDate();
        return null;
    }

    public boolean hasValidationData(){
        return rekognitionReniecData != null ? true : false;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public DocumentationExtractedDataFromDatalabels getDocumentationData() {
        return documentationData;
    }

    public void setDocumentationData(DocumentationExtractedDataFromDatalabels documentationData) {
        this.documentationData = documentationData;
    }
}
