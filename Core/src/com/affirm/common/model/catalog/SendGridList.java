package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by dev5 on 13/10/17.
 */
public class SendGridList {

    private Integer id;
    private String name;
    private String description;
    private Boolean isActive;
    private Integer sendgridId;

    public void fillFromDB(JSONObject json){
        setId(JsonUtil.getIntFromJson(json, "contact_list_id", null));
        setName(JsonUtil.getStringFromJson(json, "contact_list", null));
        setDescription(JsonUtil.getStringFromJson(json, "description", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setSendgridId(JsonUtil.getIntFromJson(json, "sendgrid_id", null));
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }


    public Integer getSendgridId() {
        return sendgridId;
    }

    public void setSendgridId(Integer sendgridId) {
        this.sendgridId = sendgridId;
    }
}
