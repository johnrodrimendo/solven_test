package com.affirm.marketingCampaign.dao;

import com.affirm.common.model.UserOfHierarchy;
import com.affirm.common.model.catalog.EntityExtranetConfiguration;
import com.affirm.common.model.catalog.ExtranetMenu;
import com.affirm.common.model.security.OldPassword;
import com.affirm.common.model.transactional.*;
import com.affirm.common.util.AjaxResponse;
import com.affirm.marketingCampaign.model.MarketingCampaign;
import com.affirm.marketingCampaign.model.MarketingCampaignExtranetPainter;
import com.affirm.marketingCampaign.model.MarketingCampaignPersonInteraction;
import com.affirm.marketingCampaign.model.TemplateCampaign;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * @author jrodriguez
 */
public interface MarketingCampaignDAO {

    Pair<Integer, Double> getMarketingCampaignExtranetCount(Integer entityId, Date startDate, Date endDate, String query, Locale locale) throws Exception;

    List<MarketingCampaignExtranetPainter> getMarketingCampaignExtranet(Integer entityId, Date startDate, Date endDate, String query, Integer limit, Integer offset, Locale locale) throws Exception;

    List<TemplateCampaign> getCampaignTemplateExtranet(Integer entityId,Locale locale) throws Exception;

    TemplateCampaign getCampaignTemplateExtranetById(Integer templateId,Locale locale) throws Exception;

    MarketingCampaign getMarketingCampaign(Integer campaignId, Locale locale) throws Exception;

    Integer insertTestCampaign(int entity_id, int entity_user_id, char type) throws Exception;

    Integer insertCampaignTemplate(String name, Character type, Integer parent_campaign_template_id, Integer entity_id, Integer entity_user_id, String subject, String body, String header_img, Boolean is_active, Boolean is_saved) throws Exception;

    Integer createMarketingCampaign(String name, Character type, Character status, Integer campaignTemplateId, Integer entityId, Integer entityUserId, Integer queryBotId, Integer collectionBaseId, Character receiverType, TemplateCampaign templateCampaign,  EntityExtranetConfiguration.MarketingCampaignConfiguration configuration) throws Exception;

    void updateMarketingCampaignQueryBotId(Integer marketingCampaignTemplateId, Integer queryBotId) throws Exception;
    
    void updateMarketingCampaignPersonInteractionIds(int marketingCampaignId, List<MarketingCampaignPersonInteraction> personInteractionIds) throws Exception;

    void updateMarketingCampaignTrackingEvents(int marketingCampaignId, MarketingCampaign.MarketingTotalTrackingEvents trackingEvents) throws Exception;

    List<MarketingCampaign> getMarketingCampaignToTrack() throws Exception;

    void updateMarketingCampaignStatus(int marketingCampaignId, Character status) throws Exception;
}