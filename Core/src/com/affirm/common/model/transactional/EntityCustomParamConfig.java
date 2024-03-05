package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jarmando on 15/02/17.
 */
public class EntityCustomParamConfig implements Serializable {
    private Integer referrals;
    private List<Integer> fileTypes;
    private List<Integer> extrafileTypes;
    private List<Integer> requiredRelationship;

    public void fillFromDb(JSONObject json) {
        setReferrals(JsonUtil.getIntFromJson(json, "referrals", null));
        if(JsonUtil.getJsonArrayFromJson(json, "filetype", null) != null){
            setFileTypes(new ArrayList<>());
            JSONArray array = JsonUtil.getJsonArrayFromJson(json, "filetype", null);
            for(int i=0; i<array.length(); i++){
                getFileTypes().add(array.getInt(i));
            }
        }

        if(JsonUtil.getJsonArrayFromJson(json, "extra_filetype", null) != null){
            setExtrafileTypes(new ArrayList<>());
            JSONArray array = JsonUtil.getJsonArrayFromJson(json, "extra_filetype", null);
            for(int i=0; i<array.length(); i++){
                getExtrafileTypes().add(array.getInt(i));
            }
        }

        if(JsonUtil.getJsonArrayFromJson(json, "required_relationships", null) != null){
            setRequiredRelationship(new ArrayList<>());
            JSONArray array = JsonUtil.getJsonArrayFromJson(json, "required_relationships", null);
            for(int i=0; i<array.length(); i++){
                getRequiredRelationship().add(array.getInt(i));
            }
        }

    }

    public List<Integer> getRequiredRelationship() {
        return requiredRelationship;
    }

    public void setRequiredRelationship(List<Integer> requiredRelationship) {
        this.requiredRelationship = requiredRelationship;
    }

    public Integer getReferrals() {
        return referrals;
    }

    public void setReferrals(Integer referrals) {
        this.referrals = referrals;
    }

    public List<Integer> getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(List<Integer> fileTypes) {
        this.fileTypes = fileTypes;
    }

    public List<Integer> getExtrafileTypes() {
        return extrafileTypes;
    }

    public void setExtrafileTypes(List<Integer> extrafileTypes) {
        this.extrafileTypes = extrafileTypes;
    }
}

