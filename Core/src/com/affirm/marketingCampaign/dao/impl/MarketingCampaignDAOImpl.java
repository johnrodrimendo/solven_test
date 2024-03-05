package com.affirm.marketingCampaign.dao.impl;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.model.catalog.EntityExtranetConfiguration;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.CreditEntityExtranetPainter;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.affirm.marketingCampaign.dao.MarketingCampaignDAO;
import com.affirm.marketingCampaign.model.MarketingCampaign;
import com.affirm.marketingCampaign.model.MarketingCampaignExtranetPainter;
import com.affirm.marketingCampaign.model.MarketingCampaignPersonInteraction;
import com.affirm.marketingCampaign.model.TemplateCampaign;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Repository("marketingCampaignDAO")
public class MarketingCampaignDAOImpl extends JsonResolverDAO implements MarketingCampaignDAO {

    @Autowired
    CatalogService catalogService;

    @Override
    public Pair<Integer, Double> getMarketingCampaignExtranetCount(Integer entityId, Date startDate, Date endDate, String query, Locale locale) throws Exception {
        JSONObject dbJson = queryForObjectInteraction("select * from interaction.get_marketing_campaign_count(?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.VARCHAR, query)
        );

        if (dbJson == null)
            return Pair.of(0, 0.0);

        return Pair.of(JsonUtil.getIntFromJson(dbJson, "count", 0), null);
    }

    @Override
    public List<MarketingCampaignExtranetPainter> getMarketingCampaignExtranet(Integer entityId, Date startDate, Date endDate, String query, Integer limit, Integer offset, Locale locale) throws Exception {
        if (offset != null && offset < 0) offset = 0;
        if (limit != null && limit < 0) limit = null;
        if (query != null && query.isEmpty()) query = null;
        JSONArray dbArray = queryForObjectInteraction("select * from interaction.get_marketing_campaign(?, ?, ?, ?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.VARCHAR, query),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.INTEGER, offset)
        );
        if (dbArray == null)
            return null;

        List<MarketingCampaignExtranetPainter> list = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {

            JSONObject json = dbArray.getJSONObject(i);

            MarketingCampaignExtranetPainter data = new MarketingCampaignExtranetPainter();
            data.fillFromDb(json, catalogService, locale);
            list.add(data);
        }
        return list;
    }

    @Override
    public List<TemplateCampaign> getCampaignTemplateExtranet(Integer entityId,Locale locale) throws Exception {
        JSONArray dbArray = queryForObjectInteraction("select * from interaction.get_campaign_templates_summary(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId)
        );
        if (dbArray == null)
            return null;

        List<TemplateCampaign> list = new ArrayList<>();

        for (int i = 0; i < dbArray.length(); i++) {
            JSONObject json = dbArray.getJSONObject(i);
            TemplateCampaign data = new TemplateCampaign();
            data.fillFromDb(json, catalogService, locale);
            list.add(data);
        }
        return list;
    }

    @Override
    public TemplateCampaign getCampaignTemplateExtranetById(Integer templateId,Locale locale) throws Exception {
        JSONObject dbJson = queryForObjectInteraction("select * from interaction.get_campaign_template_by_id(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, templateId)
        );

        if (dbJson == null)
            return null;

        TemplateCampaign data = new TemplateCampaign();
        data.fillFromDb(dbJson, catalogService, locale);

        return data;
    }

    @Override
    public MarketingCampaign getMarketingCampaign(Integer campaignId, Locale locale) throws Exception {
        JSONObject dbJson = queryForObjectInteraction("select * from interaction.get_campaign_by_id(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, campaignId)
        );

        if (dbJson == null)
            return null;

        MarketingCampaign data = new MarketingCampaign();
        data.fillFromDb(dbJson, catalogService, locale);

        return data;
    }

    @Override
    public Integer insertTestCampaign(int entity_id, int entity_user_id, char type) throws Exception {

        Integer dbJson = queryForObjectInteraction("INSERT INTO interaction.tb_marketing_campaign_test(type,entity_id,entity_user_id) values (? , ? ,?) returning marketing_campaign_test_id", Integer.class,
                new SqlParameterValue(Types.CHAR, type),
                new SqlParameterValue(Types.INTEGER, entity_id),
                new SqlParameterValue(Types.INTEGER, entity_user_id)
        );
        return dbJson;
    }

    public Integer insertCampaignTemplate(String name, Character type, Integer parent_campaign_template_id, Integer entity_id, Integer entity_user_id, String subject, String body, String header_img, Boolean is_active, Boolean is_saved) throws Exception {

        //Integer dbJson = queryForObjectInteraction("INSERT INTO interaction.tb_marketing_campaign_test(type,entity_id,entity_user_id) values (? , ? ,?) returning marketing_campaign_test_id", Integer.class,
        Integer dbJson = queryForObjectInteraction("INSERT INTO interaction.tb_campaign_template(name, type, parent_campaign_template_id, entity_id,\n" +
                        "                                  entity_user_id, subject, body, header_img, is_active, is_saved) values (? , ? , ? , ? , ? , ? , ? , ? , ? , ?) returning campaign_template_id", Integer.class,
                new SqlParameterValue(Types.VARCHAR, name),
                new SqlParameterValue(Types.CHAR, type),
                new SqlParameterValue(Types.INTEGER, parent_campaign_template_id),
                new SqlParameterValue(Types.INTEGER, entity_id),
                new SqlParameterValue(Types.INTEGER, entity_user_id),
                new SqlParameterValue(Types.VARCHAR, subject),
                new SqlParameterValue(Types.VARCHAR, body),
                new SqlParameterValue(Types.VARCHAR, header_img),
                new SqlParameterValue(Types.BOOLEAN, is_active),
                new SqlParameterValue(Types.BOOLEAN, is_saved)
        );
        return dbJson;
    }

    @Override
    public Integer createMarketingCampaign(String name, Character type, Character status, Integer campaignTemplateId, Integer entityId, Integer entityUserId, Integer queryBotId, Integer collectionBaseId, Character receiverType, TemplateCampaign templateCampaign, EntityExtranetConfiguration.MarketingCampaignConfiguration configuration) throws Exception {

        Integer marketingCampaignId = queryForObjectInteraction("INSERT INTO interaction.tb_marketing_campaign(name,type,status,campaign_template_id,entity_id,entity_user_id,query_bot_id,collection_base_id, receiver_type, js_template, js_configuration,is_tracking) values (?,?,?,?,?,?,?,?,?,?,?,?) returning marketing_campaign_id", Integer.class,
                new SqlParameterValue(Types.VARCHAR, name),
                new SqlParameterValue(Types.CHAR, type),
                new SqlParameterValue(Types.CHAR, status),
                new SqlParameterValue(Types.INTEGER, campaignTemplateId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, entityUserId),
                new SqlParameterValue(Types.INTEGER, queryBotId),
                new SqlParameterValue(Types.INTEGER, collectionBaseId),
                new SqlParameterValue(Types.CHAR, receiverType),
                new SqlParameterValue(templateCampaign != null ? Types.OTHER : Types.NULL, templateCampaign != null ? new Gson().toJson(templateCampaign) : null),
                new SqlParameterValue(configuration != null ? Types.OTHER : Types.NULL, configuration != null ? new Gson().toJson(configuration) : null),
                new SqlParameterValue(Types.BOOLEAN, true)
        );

        return marketingCampaignId;
    }

    @Override
    public void updateMarketingCampaignQueryBotId(Integer marketingCampaignTemplateId, Integer queryBotId) throws Exception {
        update("UPDATE interaction.tb_marketing_campaign SET query_bot_id = ? WHERE marketing_campaign_id = ?", INTERACTION_DB,
                new SqlParameterValue(Types.INTEGER, queryBotId),
                new SqlParameterValue(Types.INTEGER, marketingCampaignTemplateId)
        );
    }

    @Override
    public void updateMarketingCampaignPersonInteractionIds(int marketingCampaignId, List<MarketingCampaignPersonInteraction> personInteractionIds) throws Exception {
        update("update interaction.tb_marketing_campaign set js_person_interactions = ? where marketing_campaign_id = ?;", INTERACTION_DB,
                new SqlParameterValue(Types.OTHER, personInteractionIds != null ? new Gson().toJson(personInteractionIds) : null),
                new SqlParameterValue(Types.INTEGER, marketingCampaignId));
    }

    @Override
    public void updateMarketingCampaignTrackingEvents(int marketingCampaignId, MarketingCampaign.MarketingTotalTrackingEvents trackingEvents) throws Exception {
        update("update interaction.tb_marketing_campaign set js_total_tracking_events = ? where marketing_campaign_id = ?;",INTERACTION_DB,
                new SqlParameterValue(Types.OTHER, new Gson().toJson(trackingEvents)),
                new SqlParameterValue(Types.INTEGER, marketingCampaignId));
    }


    @Override
    public List<MarketingCampaign> getMarketingCampaignToTrack() throws Exception {
        JSONArray dbJson = queryForObjectInteraction("select * from interaction.get_marketing_campaign_to_get_events()", JSONArray.class);
        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<MarketingCampaign> credits = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            MarketingCampaign credit = new MarketingCampaign();
            credit.fillFromDb(dbJson.getJSONObject(i), catalogService, Configuration.getDefaultLocale());
            credits.add(credit);
        }
        return credits;
    }

    @Override
    public void updateMarketingCampaignStatus(int marketingCampaignId, Character status) throws Exception {
        update("update interaction.tb_marketing_campaign set status = ? where marketing_campaign_id = ?;",INTERACTION_DB,
                new SqlParameterValue(Types.CHAR, status),
                new SqlParameterValue(Types.INTEGER, marketingCampaignId));
    }



}
