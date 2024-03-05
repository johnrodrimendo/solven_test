package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by renzodiaz on 4/26/17.
 */
public class EntityBranding {

    public static final String defaultColor = "#f9f9fb";

    private Entity entity;
    private String entityPrimaryColor;
    private String subdomain;
    private String domain;
    private String logoFooterUrl;
    private String entitySecundaryColor;
    private String entityLightColor;
    private String logoAgentPageUrl;
    private Boolean branded;
    private Boolean ninja;
    private String intercomKey;
    private String template;
    private String agentBackgroundColor;
    private String agentTextColor;
    private String colorLineOne;
    private String colorLineTwo;
    private String colorLineThree;
    private String favicon;
    private String tertiaryColor;
    private String processLogo;
    private String privacyPolicy;
    private String termsAndConditions;
    private List<String> minRequirements;
    private ProcessQuestionsConfiguration brandingQuestionConfiguration;
    private Map<List<Integer>, ProcessQuestionsConfiguration> entityProductQuestionConfigurations = new HashMap<>();
    private EntityBrandingLoanLandingConfiguration landingConfiguration;
    private EntityExtranetConfiguration entityExtranetConfiguration;
    private List<String> iframeDomainsAllowed;

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        if (JsonUtil.getIntFromJson(json, "entity_id", null) != null)
            setEntity(catalog.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        setEntityPrimaryColor(JsonUtil.getStringFromJson(json, "primary_color", null));
        setSubdomain(JsonUtil.getStringFromJson(json, "subdomain", null));
        setDomain(JsonUtil.getStringFromJson(json, "domain", null));
        setLogoFooterUrl(JsonUtil.getStringFromJson(json, "footer_logo", null));
        setEntitySecundaryColor(JsonUtil.getStringFromJson(json, "secondary_color", null));
        setEntityLightColor(JsonUtil.getStringFromJson(json, "light_color", null));
        setLogoAgentPageUrl(JsonUtil.getStringFromJson(json, "agent_page_logo", null));
        setBranded(JsonUtil.getBooleanFromJson(json, "is_branded", null));
        setIntercomKey(JsonUtil.getStringFromJson(json, "intercom_key", null));
        setNinja(JsonUtil.getBooleanFromJson(json, "is_ninja", null));
        setTemplate(JsonUtil.getStringFromJson(json, "template", null));
        setAgentBackgroundColor(JsonUtil.getStringFromJson(json, "agent_background_color", null));
        setAgentTextColor(JsonUtil.getStringFromJson(json, "agent_text_color", null));
        setColorLineOne(JsonUtil.getStringFromJson(json, "color_line_one", null));
        setColorLineTwo(JsonUtil.getStringFromJson(json, "color_line_two", null));
        setColorLineThree(JsonUtil.getStringFromJson(json, "color_line_three", null));
        setFavicon(JsonUtil.getStringFromJson(json, "favicon", null));
        setTertiaryColor(JsonUtil.getStringFromJson(json, "tertiary_color", null));
        setProcessLogo(JsonUtil.getStringFromJson(json, "process_logo", null));
        setPrivacyPolicy(JsonUtil.getStringFromJson(json, "privacy_policy", null));
        setTermsAndConditions(JsonUtil.getStringFromJson(json, "terms_conditions", null));

        if (JsonUtil.getJsonArrayFromJson(json, "ar_min_requirements", null) != null) {
            setMinRequirements(new ArrayList<>());
            JSONArray array = JsonUtil.getJsonArrayFromJson(json, "ar_min_requirements", null);
            for (int i = 0; i < array.length(); i++) {
                getMinRequirements().add(array.getString(i));
            }
        }
        if (JsonUtil.getJsonArrayFromJson(json, "js_question_config", null) != null) {
            JSONArray arrayQuestionsConfig = JsonUtil.getJsonArrayFromJson(json, "js_question_config", null);
            for (int i = 0; i < arrayQuestionsConfig.length(); i++) {
                JSONObject jsonQuestionConfig = arrayQuestionsConfig.getJSONObject(i);
                if (jsonQuestionConfig != null) {
                    if (JsonUtil.getBooleanFromJson(jsonQuestionConfig, "defaultForBranding", false)) {
                        setBrandingQuestionConfiguration(new ProcessQuestionsConfiguration());
                        getBrandingQuestionConfiguration().fillFromDb(JsonUtil.getJsonObjectFromJson(jsonQuestionConfig, "questions", null));
                    }

                    if (JsonUtil.getJsonArrayFromJson(jsonQuestionConfig, "entityProductParams", null) != null) {

                        List<Integer> listIds = new ArrayList<>();
                        JSONArray jsEntityProductParams = JsonUtil.getJsonArrayFromJson(jsonQuestionConfig, "entityProductParams", null);
                        for (int j = 0; j < jsEntityProductParams.length(); j++) {
                            listIds.add(jsEntityProductParams.getInt(j));
                        }

                        ProcessQuestionsConfiguration entityProductParamQuestions = new ProcessQuestionsConfiguration();
                        entityProductParamQuestions.fillFromDb(JsonUtil.getJsonObjectFromJson(jsonQuestionConfig, "questions", null));

                        getEntityProductQuestionConfigurations().put(listIds, entityProductParamQuestions);
                    }
                }
            }
        }
        setLandingConfiguration(new EntityBrandingLoanLandingConfiguration());
        getLandingConfiguration().fillFromDb(JsonUtil.getJsonObjectFromJson(json, "js_la_landing_configuration", null));
        if(JsonUtil.getJsonObjectFromJson(json, "js_extranet_configuration", null) != null)
            setEntityExtranetConfiguration(new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "js_extranet_configuration", null).toString(), EntityExtranetConfiguration.class));
        if(getLandingConfiguration().getLogoImageUrl() == null)
            getLandingConfiguration().setLogoImageUrl(entity.getLogoUrl());
        if (JsonUtil.getJsonArrayFromJson(json, "js_iframe_config", null) != null) {
            setIframeDomainsAllowed(new ArrayList<>());
            JSONArray array = JsonUtil.getJsonArrayFromJson(json, "js_iframe_config", null);
            for (int i = 0; i < array.length(); i++) {
                getIframeDomainsAllowed().add(array.getString(i));
            }
        }
    }

    public ProcessQuestionsConfiguration getEntityProductParamQuestionConfiguration(Integer... entityProductParamId) {
        return entityProductQuestionConfigurations.entrySet().stream().filter(e -> e.getKey().containsAll(Arrays.asList(entityProductParamId))).findFirst().map(e -> e.getValue()).orElse(null);
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public String getEntityPrimaryColor() {
        return entityPrimaryColor;
    }

    public void setEntityPrimaryColor(String entityPrimaryColor) {
        this.entityPrimaryColor = entityPrimaryColor;
    }

    public String getLogoFooterUrl() {
        return logoFooterUrl;
    }

    public void setLogoFooterUrl(String logoFooterUrl) {
        this.logoFooterUrl = logoFooterUrl;
    }

    public String getEntitySecundaryColor() {
        return entitySecundaryColor;
    }

    public void setEntitySecundaryColor(String entitySecundaryColor) {
        this.entitySecundaryColor = entitySecundaryColor;
    }

    public String getEntityLightColor() {
        return entityLightColor;
    }

    public void setEntityLightColor(String entityLightColor) {
        this.entityLightColor = entityLightColor;
    }

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getLogoAgentPageUrl() {
        return logoAgentPageUrl;
    }

    public void setLogoAgentPageUrl(String logoAgentPageUrl) {
        this.logoAgentPageUrl = logoAgentPageUrl;
    }

    public Boolean getBranded() {
        return branded;
    }

    public void setBranded(Boolean branded) {
        this.branded = branded;
    }

    public String getIntercomKey() {
        return intercomKey;
    }

    public void setIntercomKey(String intercomKey) {
        this.intercomKey = intercomKey;
    }

    public Boolean getNinja() {
        return ninja;
    }

    public void setNinja(Boolean ninja) {
        this.ninja = ninja;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getAgentBackgroundColor() {
        return agentBackgroundColor;
    }

    public void setAgentBackgroundColor(String agentBackgroundColor) {
        this.agentBackgroundColor = agentBackgroundColor;
    }

    public String getAgentTextColor() {
        return agentTextColor;
    }

    public void setAgentTextColor(String agentTextColor) {
        this.agentTextColor = agentTextColor;
    }

    public String getColorLineOne() {
        return colorLineOne;
    }

    public void setColorLineOne(String colorLineOne) {
        this.colorLineOne = colorLineOne;
    }

    public String getColorLineTwo() {
        return colorLineTwo;
    }

    public void setColorLineTwo(String colorLineTwo) {
        this.colorLineTwo = colorLineTwo;
    }

    public String getColorLineThree() {
        return colorLineThree;
    }

    public void setColorLineThree(String colorLineThree) {
        this.colorLineThree = colorLineThree;
    }

    public String getFavicon() {
        return favicon;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

    public String getTertiaryColor() {
        return tertiaryColor;
    }

    public void setTertiaryColor(String tertiaryColor) {
        this.tertiaryColor = tertiaryColor;
    }

    public String getProcessLogo() {
        return processLogo;
    }

    public void setProcessLogo(String processLogo) {
        this.processLogo = processLogo;
    }

    public List<String> getMinRequirements() {
        return minRequirements;
    }

    public void setMinRequirements(List<String> minRequirements) {
        this.minRequirements = minRequirements;
    }

    public String getBaseUrl() {
        return subdomain + "." + domain;
    }

    public ProcessQuestionsConfiguration getBrandingQuestionConfiguration() {
        return brandingQuestionConfiguration;
    }

    public void setBrandingQuestionConfiguration(ProcessQuestionsConfiguration brandingQuestionConfiguration) {
        this.brandingQuestionConfiguration = brandingQuestionConfiguration;
    }

    public Map<List<Integer>, ProcessQuestionsConfiguration> getEntityProductQuestionConfigurations() {
        return entityProductQuestionConfigurations;
    }

    public void setEntityProductQuestionConfigurations(Map<List<Integer>, ProcessQuestionsConfiguration> entityProductQuestionConfigurations) {
        this.entityProductQuestionConfigurations = entityProductQuestionConfigurations;
    }

    public String getPrivacyPolicy() {
        return privacyPolicy;
    }

    public void setPrivacyPolicy(String privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public EntityBrandingLoanLandingConfiguration getLandingConfiguration() {
        return landingConfiguration;
    }

    public void setLandingConfiguration(EntityBrandingLoanLandingConfiguration landingConfiguration) {
        this.landingConfiguration = landingConfiguration;
    }

    public EntityExtranetConfiguration getEntityExtranetConfiguration() {
        return entityExtranetConfiguration;
    }

    public void setEntityExtranetConfiguration(EntityExtranetConfiguration entityExtranetConfiguration) {
        this.entityExtranetConfiguration = entityExtranetConfiguration;
    }

    public List<String> getIframeDomainsAllowed() {
        return iframeDomainsAllowed;
    }

    public void setIframeDomainsAllowed(List<String> iframeDomainsAllowed) {
        this.iframeDomainsAllowed = iframeDomainsAllowed;
    }
}
