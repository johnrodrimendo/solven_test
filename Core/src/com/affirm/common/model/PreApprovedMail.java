package com.affirm.common.model;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.InteractionContent;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class PreApprovedMail {
    int id;
    boolean isActive;
    String name;
    InteractionContent interactionContent;
    CountryParam country;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public InteractionContent getInteractionContent() {
        return interactionContent;
    }

    public void setInteractionContent(InteractionContent interactionContent) {
        this.interactionContent = interactionContent;
    }

    public CountryParam getCountry() {
        return country;
    }

    public void setCountry(CountryParam country) {
        this.country = country;
    }


    public void fillFromDb(JSONObject json, CatalogService catalog) {
        setId(JsonUtil.getIntFromJson(json, "pre_approved_mailing_id", null));
        setActive(JsonUtil.getBooleanFromJson(json, "is_active", null));
        setName(JsonUtil.getStringFromJson(json, "pre_approved_mailing", null));
        if (JsonUtil.getIntFromJson(json, "country_id", null) != null) {
            country = new CountryParam();
            country.setId(JsonUtil.getIntFromJson(json, "country_id", null));
        }
        if (JsonUtil.getIntFromJson(json, "interaction_content_id", null) != null) {
            interactionContent = new InteractionContent();
            interactionContent = catalog.getInteractionContent(JsonUtil.getIntFromJson(json, "interaction_content_id", null), JsonUtil.getIntFromJson(json, "country_id", null));
        }
    }

}
