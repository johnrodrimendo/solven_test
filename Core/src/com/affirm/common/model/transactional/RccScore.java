package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RccScore {

    private String docType;
    private String docNumber;
    private Integer experience;
    private List<RccMonthlyScore> rccMonthlyScores;

    public void fillFromDb(JSONObject json) throws Exception {
        JSONObject scoreJson = JsonUtil.getJsonObjectFromJson(json, "sc", null);
        if(scoreJson != null){
            setDocType(JsonUtil.getStringFromJson(scoreJson, "tip_ide", null));
            setDocNumber(JsonUtil.getStringFromJson(scoreJson, "num_ide", null));
            setExperience(JsonUtil.getIntFromJson(scoreJson, "exp", null));
        }
        JSONArray monthlyScoresJson = JsonUtil.getJsonArrayFromJson(json, "sm", null);
        if(monthlyScoresJson != null){
            rccMonthlyScores = new ArrayList<>();
            for(int i=0; i<monthlyScoresJson.length(); i++){
                RccMonthlyScore score = new RccMonthlyScore();
                score.fillFromDb(monthlyScoresJson.getJSONObject(i));
                rccMonthlyScores.add(score);
            }
        }
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public List<RccMonthlyScore> getRccMonthlyScores() {
        return rccMonthlyScores;
    }

    public void setRccMonthlyScores(List<RccMonthlyScore> rccMonthlyScores) {
        this.rccMonthlyScores = rccMonthlyScores;
    }
}
