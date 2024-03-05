package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.InteractionContent;
import com.affirm.common.model.catalog.InteractionProvider;
import com.affirm.common.model.catalog.InteractionType;
import com.affirm.common.model.catalog.Relationship;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.StringFieldValidator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 31/08/16.
 */
public class PersonInteraction implements Serializable {

    private Integer id;
    private Integer personId;
    private Integer creditId;
    private Integer loanApplicationId;
    private Integer selfEvaluationId;
    private String creditCode;
    private String loanApplicationCode;
    private InteractionType interactionType;
    private InteractionContent interactionContent;
    private InteractionProvider interactionProvider;
    private String destination;
    private String subject;
    private String body;
    private String detail;
    private Date registerDate;
    private Boolean sent;
    private Boolean automatic;
    private String senderName;
    private String[] ccMails;
    private String template;
    private Integer countryId;
    private List<PersonInteractionStat> stats;
    private List<PersonInteractionAttachment> attachments;
    private Boolean holder;
    private Relationship relationship;
    private Integer queryBotId;
    private List<PersonInteractionResponse> interactionResponses;
    private JSONObject providerData;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "person_interaction_id", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setSelfEvaluationId(JsonUtil.getIntFromJson(json, "self_evaluation_id", null));
        setCreditCode(JsonUtil.getStringFromJson(json, "credit_code", null));
        setLoanApplicationCode(JsonUtil.getStringFromJson(json, "loan_application_code", null));
        setCountryId(JsonUtil.getIntFromJson(json, "country_id", null));
        if (JsonUtil.getIntFromJson(json, "interaction_type_id", null) != null) {
            setInteractionType(catalog.getInteractionType(JsonUtil.getIntFromJson(json, "interaction_type_id", null)));
        }
        if (JsonUtil.getIntFromJson(json, "interaction_provider_id", null) != null) {
            setInteractionProvider(catalog.getInteractionProvider(JsonUtil.getIntFromJson(json, "interaction_provider_id", null)));
        }
        if (JsonUtil.getIntFromJson(json, "interaction_content_id", null) != null) {
            setInteractionContent(catalog.getInteractionContent(JsonUtil.getIntFromJson(json, "interaction_content_id", null), getCountryId()));
        }
        setDestination(JsonUtil.getStringFromJson(json, "destination", null));
        setSubject(JsonUtil.getStringFromJson(json, "subject", null));
        setBody(JsonUtil.getStringFromJson(json, "body", null));
        setDetail(JsonUtil.getStringFromJson(json, "detail", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setSent(JsonUtil.getBooleanFromJson(json, "sent", null));
        setAutomatic(JsonUtil.getBooleanFromJson(json, "is_automatic", null));
        if (JsonUtil.getJsonArrayFromJson(json, "bo_get_person_interaction_stats", null) != null) {
            JSONArray jsonStats = JsonUtil.getJsonArrayFromJson(json, "bo_get_person_interaction_stats", null);
            stats = new ArrayList<>();
            for (int i = 0; i < jsonStats.length(); i++) {
                PersonInteractionStat stat = new PersonInteractionStat();
                stat.fillFromDb(jsonStats.getJSONObject(i));
                stats.add(stat);
            }
        }
        setSenderName(JsonUtil.getStringFromJson(json, "sender_name", null));
        if (JsonUtil.getStringFromJson(json, "cc_mails", null) != null) {
            setCcMails(JsonUtil.getStringFromJson(json, "cc_mails", null).split(","));
        }
        setTemplate(JsonUtil.getStringFromJson(json, "template", null));
        setHolder(JsonUtil.getBooleanFromJson(json, "titular", null));
        setRelationship(catalog.getRelationship(JsonUtil.getIntFromJson(json, "relationship_id", null), locale));
        setQueryBotId(JsonUtil.getIntFromJson(json, "query_bot_id", null));

        JSONArray jsonArray = JsonUtil.getJsonArrayFromJson(json, "js_responses", null);
        if (jsonArray != null) {
            interactionResponses = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                PersonInteractionResponse personInteractionResponse = new PersonInteractionResponse();
                personInteractionResponse.fillFromDb(jsonArray.getJSONObject(i));
                interactionResponses.add(personInteractionResponse);
            }
        }

        if(JsonUtil.getJsonObjectFromJson(json, "provider_data", null) != null) setProviderData(JsonUtil.getJsonObjectFromJson(json, "provider_data", null));
    }

    public String getCcMailsReadable() {
        if (ccMails == null)
            return null;
        return String.join(",", ccMails);
    }

    public void addCcMail(String ccMail) {
        if (ccMails != null)
            ccMails[ccMails.length] = ccMail;
        else
            ccMails = new String[]{ccMail};
    }

//    TODO TO TEST LATER
    public void setDefaultProviderByDestination(String destination, CatalogService catalog) {
        this.setDestination(destination);

        if(destination.startsWith("+") || destination.matches("(^\\+[0-9]+$)|\\d")) {
            this.setInteractionType(catalog.getInteractionType(InteractionType.SMS));
            this.setInteractionProvider(catalog.getInteractionProvider(InteractionProvider.AWS));
        } else if(destination.matches(StringFieldValidator.PATTER_REGEX_EMAIL)) {
            this.setInteractionType(catalog.getInteractionType(InteractionType.SMS));
            this.setInteractionProvider(catalog.getInteractionProvider(InteractionProvider.SENGRID));
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Integer getCreditId() {
        return creditId;
    }

    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public Integer getSelfEvaluationId() {
        return selfEvaluationId;
    }

    public void setSelfEvaluationId(Integer selfEvaluationId) {
        this.selfEvaluationId = selfEvaluationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getLoanApplicationCode() {
        return loanApplicationCode;
    }

    public void setLoanApplicationCode(String loanApplicationCode) {
        this.loanApplicationCode = loanApplicationCode;
    }

    public InteractionType getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(InteractionType interactionType) {
        this.interactionType = interactionType;
    }

    public InteractionContent getInteractionContent() {
        return interactionContent;
    }

    public void setInteractionContent(InteractionContent interactionContent) {
        this.interactionContent = interactionContent;
    }

    public InteractionProvider getInteractionProvider() {
        return interactionProvider;
    }

    public void setInteractionProvider(InteractionProvider interactionProvider) {
        this.interactionProvider = interactionProvider;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * This is the template. To acccess The interaction body with personalized texts call getInteractionContent().getBody()
     */
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Boolean getSent() {
        return sent;
    }

    public void setSent(Boolean sent) {
        this.sent = sent;
    }

    public List<PersonInteractionStat> getStats() {
        return stats;
    }

    public void setStats(List<PersonInteractionStat> stats) {
        this.stats = stats;
    }

    public List<PersonInteractionAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<PersonInteractionAttachment> attachments) {
        this.attachments = attachments;
    }

    public Boolean getAutomatic() {
        return automatic;
    }

    public void setAutomatic(Boolean automatic) {
        this.automatic = automatic;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String[] getCcMails() {
        return ccMails;
    }

    public void setCcMails(String[] ccMails) {
        this.ccMails = ccMails;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Boolean getHolder() {
        return holder;
    }

    public void setHolder(Boolean holder) {
        this.holder = holder;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public Integer getQueryBotId() {
        return queryBotId;
    }

    public void setQueryBotId(Integer queryBotId) {
        this.queryBotId = queryBotId;
    }

    public List<PersonInteractionResponse> getInteractionResponses() {
        return interactionResponses;
    }

    public void setInteractionResponses(List<PersonInteractionResponse> interactionResponses) {
        this.interactionResponses = interactionResponses;
    }

    public JSONObject getProviderData() {
        return providerData;
    }

    public void setProviderData(JSONObject providerData) {
        this.providerData = providerData;
    }
}
