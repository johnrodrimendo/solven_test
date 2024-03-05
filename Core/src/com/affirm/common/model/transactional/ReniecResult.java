package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by john on 29/09/16.
 */
public class ReniecResult implements Serializable {

    private Integer queryId;
    private String inDocnumber;
    private String document_number;
    private String full_name;
    private String nineth_digit;
    private String previous_character_verification;

    public void fillFromDb(JSONObject json) {
        setQueryId(JsonUtil.getIntFromJson(json, "query_id", null));
        setInDocnumber(JsonUtil.getStringFromJson(json, "in_document_number", null));
        setDocument_number(JsonUtil.getStringFromJson(json, "document_number", null));
        setFull_name(JsonUtil.getStringFromJson(json, "full_name", null));
        setNineth_digit(JsonUtil.getStringFromJson(json, "nineth_digit", null));
        setPrevious_character_verification(JsonUtil.getStringFromJson(json, "previous_character_verification", null));
    }

    public Integer getQueryId() {
        return queryId;
    }

    public void setQueryId(Integer queryId) {
        this.queryId = queryId;
    }

    public String getInDocnumber() {
        return inDocnumber;
    }

    public void setInDocnumber(String inDocnumber) {
        this.inDocnumber = inDocnumber;
    }

    public String getDocument_number() {
        return document_number;
    }

    public void setDocument_number(String document_number) {
        this.document_number = document_number;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getNineth_digit() {
        return nineth_digit;
    }

    public void setNineth_digit(String nineth_digit) {
        this.nineth_digit = nineth_digit;
    }

    public String getPrevious_character_verification() {
        return previous_character_verification;
    }

    public void setPrevious_character_verification(String previous_character_verification) {
        this.previous_character_verification = previous_character_verification;
    }

    @Override
    public String toString() {
        return "Reniec Result " + full_name + "/" + document_number + "/" + nineth_digit + "/" + previous_character_verification;
    }
}
