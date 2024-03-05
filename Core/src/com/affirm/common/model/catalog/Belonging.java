package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sTbn on 4/08/16.
 */
public class Belonging implements Serializable {

    private Integer id;
    private String name;
    private String messageKey;
    private List<Integer> requiredDocuments;

    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "belonging_id", null));
        setName(JsonUtil.getStringFromJson(json, "belonging", null));
        setMessageKey(JsonUtil.getStringFromJson(json, "text_int", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_required_filetype", null) != null) {
            JSONArray requireds = JsonUtil.getJsonArrayFromJson(json, "js_required_filetype", null);
            List<Integer> requiedFiles = new ArrayList<>();
            for (int j = 0; j < requireds.length(); j++) {
                requiedFiles.add(requireds.getInt(j));
            }
            setRequiredDocuments(requiedFiles);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public List<Integer> getRequiredDocuments() {
        return requiredDocuments;
    }

    public void setRequiredDocuments(List<Integer> requiredDocuments) {
        this.requiredDocuments = requiredDocuments;
    }
}
