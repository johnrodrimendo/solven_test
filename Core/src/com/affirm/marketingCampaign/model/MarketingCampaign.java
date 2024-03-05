package com.affirm.marketingCampaign.model;

import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityExtranetConfiguration;
import com.affirm.common.model.transactional.HardFilter;
import com.affirm.common.model.transactional.LoanApplicationApprovalValidation;
import com.affirm.common.model.transactional.Policy;
import com.affirm.common.model.transactional.ReturningReason;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MarketingCampaign {


    public static final Character EMAIL = 'E';
    public static final Character SMS = 'S';

    public final static Character RECEIVER_TYPE_ALL = 'A';
    public final static Character RECEIVER_TYPE_ALL_EXCLUDE_ACTIVE = 'E';
    public final static Character RECEIVER_TYPE_ONLY_ACTIVE = 'O';

    public static final Character PENDING_STATUS = 'P';
    public static final Character STATUS_RUNNING =  'R';
    public static final Character STATUS_SUCCESS = 'S';
    public static final Character STATUS_FAIL = 'F';


    private Integer marketingCampaignId;
    private String name;
    private Character type;
    private Character status;
    private Integer parentMarketingCampaignId;
    private Integer campaignTemplateId;
    private Date registerDate;
    private Entity entity;
    private Integer entityUserId;
    private List<MarketingCampaignPersonInteraction> jsPersonInteractions;
    private MarketingTotalTrackingEvents jsTotalTrackingEvents;
    private Boolean isTracking;
    private Integer queryBotId;
    private Integer collectionBaseId;
    private Character receiverType;
    private EntityExtranetConfiguration.MarketingCampaignConfiguration jsConfiguration;
    private TemplateCampaign jsTemplate;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {

        setMarketingCampaignId(JsonUtil.getIntFromJson(json, "marketing_campaign_id", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setName(JsonUtil.getStringFromJson(json, "name", null));
        setType(JsonUtil.getCharacterFromJson(json, "type", null));
        setStatus(JsonUtil.getCharacterFromJson(json, "status", null));
        setParentMarketingCampaignId(JsonUtil.getIntFromJson(json, "parent_marketing_campaign_id", null));
        setCampaignTemplateId(JsonUtil.getIntFromJson(json, "campaign_template_id", null));
        if (JsonUtil.getIntFromJson(json, "entity_id", null) != null)
            setEntity(catalog.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        setEntityUserId(JsonUtil.getIntFromJson(json, "entity_user_id", null));
        setQueryBotId(JsonUtil.getIntFromJson(json, "query_bot_id", null));
        setCollectionBaseId(JsonUtil.getIntFromJson(json, "collection_base_id", null));
        setReceiverType(JsonUtil.getCharacterFromJson(json, "receiver_type", null));
        setTracking(JsonUtil.getBooleanFromJson(json, "is_tracking", null));
        if(JsonUtil.getJsonArrayFromJson(json, "js_person_interactions", null) != null) setJsPersonInteractions(new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "js_person_interactions", new JSONArray()).toString(), new TypeToken<ArrayList<MarketingCampaignPersonInteraction>>() {
}.getType()));
        if (JsonUtil.getJsonObjectFromJson(json, "js_template", null) != null)
            setJsTemplate(new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "js_template", null).toString(), TemplateCampaign.class));
        if(JsonUtil.getJsonObjectFromJson(json, "js_configuration", null) != null) setJsConfiguration( new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "js_configuration", null).toString(), EntityExtranetConfiguration.MarketingCampaignConfiguration.class));
        if(JsonUtil.getJsonObjectFromJson(json, "js_total_tracking_events", null) != null) setJsTotalTrackingEvents(new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "js_total_tracking_events", null).toString(),MarketingTotalTrackingEvents.class));
    }

    public void addPersonInteraction(Integer personInteractionId) {
        if (jsPersonInteractions == null)
            jsPersonInteractions = new ArrayList<>();
        MarketingCampaignPersonInteraction json = new MarketingCampaignPersonInteraction();
        json.setPersonInteractionId(personInteractionId);
        jsPersonInteractions.add(json);
    }

    public void addPersonInteractionSmsCode(String code) {
        if (jsPersonInteractions == null)
            jsPersonInteractions = new ArrayList<>();
        MarketingCampaignPersonInteraction json = new MarketingCampaignPersonInteraction();
        json.setInticoSmsCode(code);
        jsPersonInteractions.add(json);
    }

    public Integer getMarketingCampaignId() {
        return marketingCampaignId;
    }

    public void setMarketingCampaignId(Integer marketingCampaignId) {
        this.marketingCampaignId = marketingCampaignId;
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

    public Integer getParentMarketingCampaignId() {
        return parentMarketingCampaignId;
    }

    public void setParentMarketingCampaignId(Integer parentMarketingCampaignId) {
        this.parentMarketingCampaignId = parentMarketingCampaignId;
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

    public List<MarketingCampaignPersonInteraction> getJsPersonInteractions() {
        return jsPersonInteractions;
    }

    public void setJsPersonInteractions(List<MarketingCampaignPersonInteraction> jsPersonInteractions) {
        this.jsPersonInteractions = jsPersonInteractions;
    }

    public MarketingTotalTrackingEvents getJsTotalTrackingEvents() {
        return jsTotalTrackingEvents;
    }

    public void setJsTotalTrackingEvents(MarketingTotalTrackingEvents jsTotalTrackingEvents) {
        this.jsTotalTrackingEvents = jsTotalTrackingEvents;
    }

    public Boolean getTracking() {
        return isTracking;
    }

    public void setTracking(Boolean tracking) {
        isTracking = tracking;
    }

    public Integer getQueryBotId() {
        return queryBotId;
    }

    public void setQueryBotId(Integer queryBotId) {
        this.queryBotId = queryBotId;
    }

    public Integer getCollectionBaseId() {
        return collectionBaseId;
    }

    public void setCollectionBaseId(Integer collectionBaseId) {
        this.collectionBaseId = collectionBaseId;
    }

    public Character getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(Character receiverType) {
        this.receiverType = receiverType;
    }

    public EntityExtranetConfiguration.MarketingCampaignConfiguration getJsConfiguration() {
        return jsConfiguration;
    }

    public void setJsConfiguration(EntityExtranetConfiguration.MarketingCampaignConfiguration jsConfiguration) {
        this.jsConfiguration = jsConfiguration;
    }

    public TemplateCampaign getJsTemplate() {
        return jsTemplate;
    }

    public void setJsTemplate(TemplateCampaign jsTemplate) {
        this.jsTemplate = jsTemplate;
    }

    public Integer getCampaignTemplateId() {
        return campaignTemplateId;
    }

    public void setCampaignTemplateId(Integer campaignTemplateId) {
        this.campaignTemplateId = campaignTemplateId;
    }

    public MarketingTotalTrackingEvents calculateTotalEvents(){
        if(jsTotalTrackingEvents == null) jsTotalTrackingEvents = new MarketingTotalTrackingEvents();
        if(jsPersonInteractions != null && !jsPersonInteractions.isEmpty() && type != null){
            Integer clicked = 0;
            Integer opened = 0;
            Integer sent = 0;

            for (MarketingCampaignPersonInteraction jsPersonInteraction : jsPersonInteractions) {
                if(type == EMAIL){
                    if((jsPersonInteraction.getEmailSend() != null && jsPersonInteraction.getEmailSend()) || (jsPersonInteraction.getEmailSend() != null && jsPersonInteraction.getEmailDelivery())) sent += 1;
                    if(jsPersonInteraction.getEmailOpen() != null && jsPersonInteraction.getEmailOpen()) opened += 1;
                    if(jsPersonInteraction.getEmailClick() != null && jsPersonInteraction.getEmailClick()) clicked += 1;
                }
                else if(type == SMS){
                    if(jsPersonInteraction.getSmsDelivered() != null && jsPersonInteraction.getSmsDelivered()) sent += 1;
                }
            }
            jsTotalTrackingEvents.setInteractionSent(sent);
            jsTotalTrackingEvents.setInteractionClicked(clicked);
            jsTotalTrackingEvents.setInteractionOpened(opened);
        }
        return jsTotalTrackingEvents;
    }

    public static class MarketingTotalTrackingEvents{

        private Integer interactionSent;
        private Integer interactionOpened;
        private Integer interactionClicked;
        private Integer registered;
        private Integer paid;

        public Integer getInteractionSent() {
            return interactionSent;
        }

        public void setInteractionSent(Integer interactionSent) {
            this.interactionSent = interactionSent;
        }

        public Integer getInteractionOpened() {
            return interactionOpened;
        }

        public void setInteractionOpened(Integer interactionOpened) {
            this.interactionOpened = interactionOpened;
        }

        public Integer getInteractionClicked() {
            return interactionClicked;
        }

        public void setInteractionClicked(Integer interactionClicked) {
            this.interactionClicked = interactionClicked;
        }

        public Integer getRegistered() {
            return registered;
        }

        public void setRegistered(Integer registered) {
            this.registered = registered;
        }

        public Integer getPaid() {
            return paid;
        }

        public void setPaid(Integer paid) {
            this.paid = paid;
        }
    }
}
