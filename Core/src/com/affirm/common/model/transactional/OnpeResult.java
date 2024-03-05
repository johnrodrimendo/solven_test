package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OnpeResult implements Serializable {
    private String fullName;
    private String inDocnumber;
    private List<OnpeDetail> details = new ArrayList<>();

    public void fillFromDb(JSONObject json) {
        setFullName(JsonUtil.getStringFromJson(json, "full_name", null));
        setInDocnumber(JsonUtil.getStringFromJson(json, "in_document_number", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_result", null) != null) {
            setDetails(new Gson().fromJson(
                    JsonUtil.getJsonArrayFromJson(json, "js_result", null).toString(),
                    new TypeToken<List<OnpeDetail>>() {
                    }.getType()));
        }
    }

    public String getFullName() { return fullName; }

    public void setFullName(String fullName) { this.fullName = fullName; }

    public List<OnpeDetail> getDetails() { return details; }

    public void setDetails(List<OnpeDetail> details) { this.details = details; }

    public String getInDocnumber() { return inDocnumber; }

    public void setInDocnumber(String inDocnumber) { this.inDocnumber = inDocnumber; }
}
