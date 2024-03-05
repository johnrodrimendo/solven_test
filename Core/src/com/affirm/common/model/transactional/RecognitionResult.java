package com.affirm.common.model.transactional;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * Created by john on 04/01/17.
 */
public class RecognitionResult implements Serializable {

    private Integer id;
    private Integer personId;
    private Integer loanApplicationId;
    private Integer highSimilarity;
    private Date processDate;
    private Integer userFilesIdDniA;
    private Integer userFilesIdDniB;
    private Integer userFilesIdDniMerged;
    private Integer selfieUserFileId;
    private JSONObject similarities;
    private JSONObject faceAnalysis;
    private JSONObject sceneAnalysis;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "rekognition_result_id", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setHighSimilarity(JsonUtil.getIntFromJson(json, "high_similarity", null));
        setProcessDate(JsonUtil.getPostgresDateFromJson(json, "process_date", null));
        userFilesIdDniA = JsonUtil.getIntFromJson(json, "user_files_id_dni_a", null);
        userFilesIdDniB = JsonUtil.getIntFromJson(json, "user_files_id_dni_b", null);
        userFilesIdDniMerged = JsonUtil.getIntFromJson(json, "user_files_id_dni_merged", null);
        setSelfieUserFileId(JsonUtil.getIntFromJson(json, "user_files_id_selfie", null));
        if (JsonUtil.getJsonObjectFromJson(json, "json_similarities", null) != null) {
            setSimilarities(JsonUtil.getJsonObjectFromJson(json, "json_similarities", null));
        }
        if (JsonUtil.getJsonObjectFromJson(json, "detect_labels", null) != null) {
            setSceneAnalysis(JsonUtil.getJsonObjectFromJson(json, "detect_labels", null));
        }
        if (JsonUtil.getJsonObjectFromJson(json, "detect_faces", null) != null) {
            setFaceAnalysis(JsonUtil.getJsonObjectFromJson(json, "detect_faces", null));
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public Integer getHighSimilarity() {
        return highSimilarity;
    }

    public void setHighSimilarity(Integer highSimilarity) {
        this.highSimilarity = highSimilarity;
    }

    public JSONObject getSimilarities() {
        return similarities;
    }

    public void setSimilarities(JSONObject similarities) {
        this.similarities = similarities;
    }

    public void setFaceAnalysis(JSONObject faceAnalysis) { this.faceAnalysis = faceAnalysis; }


    public void setSceneAnalysis(JSONObject sceneAnalysis) { this.sceneAnalysis = sceneAnalysis; }


    public void setFaces(JSONObject faceAnalysis) {
        this.setFaceAnalysis(faceAnalysis);
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public Integer getUserFilesIdDniA() {
        return userFilesIdDniA;
    }

    public void setUserFilesIdDniA(Integer userFilesIdDniA) {
        this.userFilesIdDniA = userFilesIdDniA;
    }

    public Integer getUserFilesIdDniB() {
        return userFilesIdDniB;
    }

    public void setUserFilesIdDniB(Integer userFilesIdDniB) {
        this.userFilesIdDniB = userFilesIdDniB;
    }

    public Integer getUserFilesIdDniMerged() {
        return userFilesIdDniMerged;
    }

    public void setUserFilesIdDniMerged(Integer userFilesIdDniMerged) {
        this.userFilesIdDniMerged = userFilesIdDniMerged;
    }

    public Integer getSelfieUserFileId() {
        return selfieUserFileId;
    }

    public void setSelfieUserFileId(Integer selfieUserFileId) {
        this.selfieUserFileId = selfieUserFileId;
    }

    public JSONObject getFaceAnalysis() {
        return faceAnalysis;
    }

    public JSONObject getSceneAnalysis() {
        return sceneAnalysis;
    }
}
