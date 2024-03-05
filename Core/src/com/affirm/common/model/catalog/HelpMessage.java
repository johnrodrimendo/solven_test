package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by john on 04/01/17.
 */
public class HelpMessage implements Serializable {

    private Integer id;
    private String tittleKey;
    private String bodyKey;
    private String tittle;
    private String body;

    public void fillFromDb(JSONObject json) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "help_message_id", null));
        setTittleKey(JsonUtil.getStringFromJson(json, "help_message_tittle", null));
        setBodyKey(JsonUtil.getStringFromJson(json, "help_message_body", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTittleKey() {
        return tittleKey;
    }

    public void setTittleKey(String tittleKey) {
        this.tittleKey = tittleKey;
    }

    public String getBodyKey() {
        return bodyKey;
    }

    public void setBodyKey(String bodyKey) {
        this.bodyKey = bodyKey;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
