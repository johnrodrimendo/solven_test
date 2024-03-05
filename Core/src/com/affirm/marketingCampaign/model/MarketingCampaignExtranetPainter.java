package com.affirm.marketingCampaign.model;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

public class MarketingCampaignExtranetPainter extends MarketingCampaign{

    private String campaignTemplateName;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        super.fillFromDb(json, catalog, locale);
        setCampaignTemplateName(JsonUtil.getStringFromJson(json, "campaign_template_name", null));
    }

    public String getCampaignTemplateName() {
        return campaignTemplateName;
    }

    public void setCampaignTemplateName(String campaignTemplateName) {
        this.campaignTemplateName = campaignTemplateName;
    }
}
