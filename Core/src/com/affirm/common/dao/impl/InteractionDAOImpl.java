package com.affirm.common.dao.impl;

import com.affirm.common.dao.InteractionDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.model.transactional.PersonInteraction;
import com.affirm.common.model.transactional.PersonInteractionAttachment;
import com.affirm.common.model.transactional.PersonInteractionStat;
import com.affirm.common.model.transactional.VerificationCallRequest;
import com.affirm.common.service.CatalogService;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */

@Repository
public class InteractionDAOImpl extends JsonResolverDAO implements InteractionDAO {


    @Autowired
    private CatalogService catalogService;

    @Override
    public Integer insertPersonInteraction(PersonInteraction interaction) {
        Integer personInteractionId = queryForObjectInteraction("select * from interaction.ins_person_interaction(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                Integer.class,
                new SqlParameterValue(Types.INTEGER, interaction.getCreditId()),
                new SqlParameterValue(Types.INTEGER, interaction.getLoanApplicationId()),
                new SqlParameterValue(Types.INTEGER, interaction.getPersonId()),
                new SqlParameterValue(Types.INTEGER, interaction.getInteractionType().getId()),
                new SqlParameterValue(Types.INTEGER, interaction.getInteractionContent().getId()),
                new SqlParameterValue(Types.VARCHAR, interaction.getDestination()),
                new SqlParameterValue(Types.VARCHAR, interaction.getSubject()),
                new SqlParameterValue(Types.VARCHAR, interaction.getBody()),
                new SqlParameterValue(Types.VARCHAR, interaction.getDetail()),
                new SqlParameterValue(Types.BOOLEAN, false),
                new SqlParameterValue(Types.VARCHAR, interaction.getCcMails() != null ? String.join(",", interaction.getCcMails()) : null));
        if (interaction.getAttachments() != null) {
            for (PersonInteractionAttachment attachment : interaction.getAttachments()) {
                queryForObjectInteraction("select * from interaction.register_attachment(?, ?)",
                        String.class,
                        new SqlParameterValue(Types.INTEGER, personInteractionId),
                        new SqlParameterValue(Types.INTEGER, attachment.getUserFileId()));
            }
        }
        return personInteractionId;
    }

    @Override
    public Integer insertPersonInteraction(PersonInteraction interaction, Integer queryBotId) {
        Integer personInteractionId = queryForObjectInteraction("select * from interaction.ins_person_interaction(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                Integer.class,
                new SqlParameterValue(Types.INTEGER, interaction.getCreditId()),
                new SqlParameterValue(Types.INTEGER, interaction.getLoanApplicationId()),
                new SqlParameterValue(Types.INTEGER, interaction.getSelfEvaluationId()),
                new SqlParameterValue(Types.INTEGER, interaction.getPersonId()),
                new SqlParameterValue(Types.INTEGER, interaction.getInteractionType().getId()),
                new SqlParameterValue(Types.INTEGER, interaction.getInteractionContent().getId()),
                new SqlParameterValue(Types.VARCHAR, interaction.getDestination()),
                new SqlParameterValue(Types.VARCHAR, interaction.getSubject()),
                new SqlParameterValue(Types.VARCHAR, interaction.getBody()),
                new SqlParameterValue(Types.VARCHAR, interaction.getDetail()),
                new SqlParameterValue(Types.BOOLEAN, false),
                new SqlParameterValue(Types.VARCHAR, interaction.getCcMails() != null ? String.join(",", interaction.getCcMails()) : null),
                new SqlParameterValue(Types.INTEGER, queryBotId));
        if (interaction.getAttachments() != null) {
            for (PersonInteractionAttachment attachment : interaction.getAttachments()) {
                queryForObjectInteraction("select * from interaction.register_attachment(?, ?)",
                        String.class,
                        new SqlParameterValue(Types.INTEGER, personInteractionId),
                        new SqlParameterValue(Types.INTEGER, attachment.getUserFileId()));
            }
        }
        return personInteractionId;
    }

    @Override
    public Integer insertPersonInteractionResponse(Integer personInteractionId, Timestamp receivedAt, String message, JSONObject jsResponse) {
        return queryForObjectInteraction("select * from interaction.ins_person_interaction_response(?, ?, ?, ?)",
                Integer.class,
                new SqlParameterValue(Types.INTEGER, personInteractionId),
                new SqlParameterValue(Types.TIMESTAMP, receivedAt),
                new SqlParameterValue(Types.VARCHAR, message),
                new SqlParameterValue(Types.OTHER, jsResponse));
    }

    @Override
    public List<PersonInteraction> getPersonInteractions(int personId, Locale locale) throws Exception {
        JSONArray dbArray = queryForObjectInteraction("select * from interaction.bo_get_person_interactions(?)",
                JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbArray == null) {
            return null;
        }

        List<PersonInteraction> interactions = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            PersonInteraction interaction = new PersonInteraction();
            interaction.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            interactions.add(interaction);
        }
        return interactions;
    }

    @Override
    public List<PersonInteraction> getPersonInteractionsByLoanApplication(int personId, int loanApplicationId, Locale locale) throws Exception {
        JSONArray dbArray = queryForObjectInteraction("select * from interaction.bo_get_person_interactions(?, ?)",
                JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId));
        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<PersonInteraction> interactions = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            PersonInteraction interaction = new PersonInteraction();
            interaction.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            interactions.add(interaction);
        }
        return interactions;
    }

    @Override
    public PersonInteraction getPersonInteractionById(int personInteractionId, Locale locale) throws Exception {
        JSONObject dbJson = queryForObjectInteraction("select * from interaction.bo_get_person_interaction(?)",
                JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personInteractionId));
        if (dbJson == null) {
            return null;
        }

        PersonInteraction interaction = new PersonInteraction();
        interaction.fillFromDb(dbJson, catalogService, locale);
        return interaction;
    }

    @Override
    public List<PersonInteraction> getPersonInteractionByQueryBotId(int queryBotId, Locale locale) throws Exception {
        JSONArray dbArray = queryForObjectInteraction("select * from interaction.get_person_interaction_by_query_bot_id(?)",
                JSONArray.class,
                new SqlParameterValue(Types.INTEGER, queryBotId));
        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<PersonInteraction> interactions = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            PersonInteraction interaction = new PersonInteraction();
            interaction.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            interactions.add(interaction);
        }
        return interactions;
    }

    @Override
    public List<PersonInteraction> getPersonInteractionByBotId(int botId, Locale locale) throws Exception {
        JSONArray dbArray = queryForObjectInteraction("select * from interaction.get_person_interaction_by_bot_id(?)",
                JSONArray.class,
                new SqlParameterValue(Types.INTEGER, botId));
        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<PersonInteraction> interactions = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            PersonInteraction interaction = new PersonInteraction();
            interaction.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            interactions.add(interaction);
        }
        return interactions;
    }

    @Override
    public void updateSentPersonInteraction(int personInteractionId) {
        queryForObjectInteraction("select * from interaction.person_interaction_sent(?)", String.class,
                new SqlParameterValue(Types.INTEGER, personInteractionId));
    }

    @Override
    public void updateProviderDataPersonInteraction(int personInteractionId, JSONObject jsonObject) {
        update("update interaction.tb_person_interaction set provider_data = ? where person_interaction_id = ?;",INTERACTION_DB,
                new SqlParameterValue(Types.OTHER, jsonObject.toString()),
                new SqlParameterValue(Types.INTEGER, personInteractionId));
    }

    @Override
    public void insertPersonInteractionStatus(int personInteractionId, String event, Timestamp timestamp) throws Exception {
        queryForObjectInteraction("select * from interaction.ins_person_interaction_stats(?, ?, ?)",
                String.class,
                new SqlParameterValue(Types.INTEGER, personInteractionId),
                new SqlParameterValue(Types.VARCHAR, event),
                new SqlParameterValue(Types.TIMESTAMP, timestamp));
    }

    @Override
    public List<PersonInteractionStat> getPersonInteractionStat(int personInteractionId) throws Exception {
        JSONArray dbArray = queryForObjectInteraction("select * from interaction.bo_get_person_interaction_stats(?)",
                JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personInteractionId));
        if (dbArray == null) {
            return null;
        }


        List<PersonInteractionStat> stats = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            PersonInteractionStat stat = new PersonInteractionStat();
            stat.fillFromDb(dbArray.getJSONObject(i));
            stats.add(stat);
        }
        return stats;
    }

    @Override
    public void updateInteraction(PersonInteraction personInteraction) {
        int personInteractionId = personInteraction.getId();
        String subject = personInteraction.getSubject();
        String body = personInteraction.getBody();
        Integer providerId = null != personInteraction.getInteractionProvider() ? personInteraction.getInteractionProvider().getId() : null;
        queryForObjectInteraction("select * from interaction.update_interaction_content(?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, personInteractionId),
                new SqlParameterValue(Types.VARCHAR, subject),
                new SqlParameterValue(Types.VARCHAR, body),
                new SqlParameterValue(Types.INTEGER, providerId));
    }

    @Override
    public Integer insertCallRequestInteraction(int loanApplicationId, String countryCode, String phoneNumber) throws Exception {
        return queryForObjectInteraction("select * from interaction.ins_verification_call_request(?, ?, ?)",
                Integer.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.VARCHAR, countryCode),
                new SqlParameterValue(Types.VARCHAR, phoneNumber));
    }

    @Override
    public List<VerificationCallRequest> getVerificationCallRequestInteraction(int loanApplicationId, String countryCode, String phoneNumber) throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_verification_call_request(?, ?, ?);",
                JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.VARCHAR, countryCode),
                new SqlParameterValue(Types.VARCHAR, phoneNumber));
        if (dbArray == null) {
            return null;
        }

        List<VerificationCallRequest> verifications = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            VerificationCallRequest verification = new VerificationCallRequest();
            verification.fillFromDb(dbArray.getJSONObject(i));
            verifications.add(verification);
        }
        return verifications;
    }

    @Override
    public PersonInteraction getPersonInteractionWithResponses(int personInteractionId, Locale locale) throws Exception {
        JSONObject dbJson = queryForObjectInteraction("select * from interaction.get_person_interaction_response(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personInteractionId));
        if (dbJson == null) {
            return null;
        }

        PersonInteraction interaction = new PersonInteraction();
        interaction.fillFromDb(dbJson, catalogService, locale);
        return interaction;
    }

    @Override
    public JSONArray getPersonInteractionHourOfDayForFilter() throws Exception {
        JSONArray array = queryForObjectInteraction("select * from interaction.get_interaciont_hour_of_day_to_filter()", JSONArray.class);
        return array;
    }

    @Override
    public JSONArray getPersonInteractionContentForFilter() throws Exception {
        JSONArray array = queryForObjectInteraction("select * from interaction.get_interaction_content_id_to_filter()", JSONArray.class);
        return array;
    }

    @Override
    public List<PersonInteraction> getPersonInteractionsById(int[] personInteractionIds, Locale locale) throws Exception {
        JSONArray dbArray = queryForObjectInteraction("select * from interaction.get_persons_interactions_by_id(?)",
                JSONArray.class,
                new SqlParameterValue(Types.OTHER, new Gson().toJson(personInteractionIds)));
        if (dbArray == null) {
            return null;
        }

        List<PersonInteraction> interactions = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            PersonInteraction interaction = new PersonInteraction();
            interaction.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            interactions.add(interaction);
        }
        return interactions;
    }
}
