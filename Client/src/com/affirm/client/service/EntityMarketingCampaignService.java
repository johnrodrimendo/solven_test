package com.affirm.client.service;

import com.affirm.common.model.catalog.EntityExtranetConfiguration;
import com.affirm.common.model.transactional.*;
import com.affirm.marketingCampaign.model.MarketingCampaign;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by jrodriguez on 26/09/16.
 */
public interface EntityMarketingCampaignService {

    Object sendTestEmail(Integer personInteractionId, String from, String fromName, String to, String[] cc, String subject, String plainMessage, String htmlMessage, String templateId, List<Attachment> attached, JSONObject jsonVariables, String hourOfDay, Map<String, String> mapTags) throws Exception;

    String getSenderEmailToUse(EntityExtranetConfiguration.MarketingCampaignConfiguration marketingCampaignConfiguration) throws Exception;

    Integer insertTestCampaign(Integer idEntity,Integer idEntityUser, Character type) throws Exception;

    Integer insertCampaignTemplate(String name, Character type, Integer parent_campaign_template_id, Integer entity_id, Integer entity_user_id, String subject, String body, String header_img, Boolean is_active, Boolean is_saved) throws Exception;

    List<String> getBaseFields(Integer productCategory, Integer entityId);

    Boolean canCreateByAvailableType(Character type, List<EntityExtranetConfiguration.MarketingCampaignConfigurationAvailableType> availableTypes);

    Map<String, String>  fillJsonWithAztecaCobranzaBase(AztecaGetawayBase aztecaGetawayBase);

    Object sendTestSMS(String destination, String body) throws Exception;

    Object sendTestSMS(String destination, String body, JSONObject jsonVars) throws Exception;

    String additionalParamsLinkCampaign(MarketingCampaign marketingCampaign);

}
