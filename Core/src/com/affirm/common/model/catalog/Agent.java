package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jrodriguez on 20/06/16.
 */
public class Agent implements Serializable {

    public static final int ALE_EFECTIVO_AL_TOQUE = 104;

    private Integer id;
    private String name;
    private String avatarUrl;
    private String avatarUrl2;
    private Character gender;
    private String mailingCategory;
    private Boolean formAssistant;
    private Boolean isHiddenAssistant;
    private Entity entity;

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setId(JsonUtil.getIntFromJson(json, "agent_id", null));
        setName(JsonUtil.getStringFromJson(json, "agent_name", null));
        setAvatarUrl(JsonUtil.getStringFromJson(json, "agent_avatar", null));
        setAvatarUrl2(JsonUtil.getStringFromJson(json, "agent_avatar_2", null));
        setGender(JsonUtil.getCharacterFromJson(json, "gender", null));
        setMailingCategory(JsonUtil.getStringFromJson(json, "mailing_category", null));
        setFormAssistant(JsonUtil.getBooleanFromJson(json, "is_form_assistant", null));
        setHiddenAssistant(JsonUtil.getBooleanFromJson(json, "is_hidden_assistant", null));
        if(JsonUtil.getIntFromJson(json, "entity_id", null) != null)
            setEntity(catalogService.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
    }

    public String getProcessAvatar(){
        if(getAvatarUrl2() == null) return getAvatarUrl();
        return getAvatarUrl2();
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    public String getMailingCategory() {
        return mailingCategory;
    }

    public void setMailingCategory(String mailingCategory) {
        this.mailingCategory = mailingCategory;
    }

    public Boolean getFormAssistant() {
        return formAssistant;
    }

    public void setFormAssistant(Boolean formAssistant) {
        this.formAssistant = formAssistant;
    }

    public Boolean getHiddenAssistant() {
        return isHiddenAssistant;
    }

    public void setHiddenAssistant(Boolean hiddenAsistant) {
        isHiddenAssistant = hiddenAsistant;
    }

    public String getAvatarUrl2() {
        return avatarUrl2;
    }

    public void setAvatarUrl2(String avatarUrl2) {
        this.avatarUrl2 = avatarUrl2;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
