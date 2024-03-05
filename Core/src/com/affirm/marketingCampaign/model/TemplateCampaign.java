package com.affirm.marketingCampaign.model;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

public class TemplateCampaign {


    private Long campaignTemplateId;
    private String name;
    private Character type;
    private Character status;
    private Long parentCampaignTemplateId;
    private Date registerDate;
    private Entity entity;
    private Integer entityUserId;
    private String subject;
    private String body;
    private String headerImg;
    private Boolean isActive;
    private Boolean isSaved;


    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {

        setCampaignTemplateId(JsonUtil.getLongFromJson(json, "campaign_template_id", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setName(JsonUtil.getStringFromJson(json, "name", null));
        setType(JsonUtil.getCharacterFromJson(json, "type", null));
        setStatus(JsonUtil.getCharacterFromJson(json, "status", null));
        setParentCampaignTemplateId(JsonUtil.getLongFromJson(json, "parent_campaign_template_id", null));
        if(JsonUtil.getIntFromJson(json, "entity_id", null) != null) setEntity(catalog.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        setEntityUserId(JsonUtil.getIntFromJson(json, "entity_user_id", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setSaved(JsonUtil.getBooleanFromJson(json, "isSaved", null));
        setHeaderImg(JsonUtil.getStringFromJson(json, "header_img", null));
        setBody(JsonUtil.getStringFromJson(json, "body", null));
        setSubject(JsonUtil.getStringFromJson(json, "subject", null));

    }

    public Long getCampaignTemplateId() {
        return campaignTemplateId;
    }

    public void setCampaignTemplateId(Long campaignTemplateId) {
        this.campaignTemplateId = campaignTemplateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Character getType() {
        return type;
    }

    public void setType(Character type) {
        this.type = type;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public Long getParentCampaignTemplateId() {
        return parentCampaignTemplateId;
    }

    public void setParentCampaignTemplateId(Long parentCampaignTemplateId) {
        this.parentCampaignTemplateId = parentCampaignTemplateId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Integer getEntityUserId() {
        return entityUserId;
    }

    public void setEntityUserId(Integer entityUserId) {
        this.entityUserId = entityUserId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getHeaderImg() {
        return headerImg;
    }

    public void setHeaderImg(String headerImg) {
        this.headerImg = headerImg;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getSaved() {
        return isSaved;
    }

    public void setSaved(Boolean saved) {
        isSaved = saved;
    }
}
