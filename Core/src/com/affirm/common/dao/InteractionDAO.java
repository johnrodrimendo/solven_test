package com.affirm.common.dao;

import com.affirm.common.model.transactional.PersonInteraction;
import com.affirm.common.model.transactional.PersonInteractionStat;
import com.affirm.common.model.transactional.VerificationCallRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

/**
 * Created by jrodriguez on 08/06/16.
 */
public interface InteractionDAO {

    Integer insertPersonInteraction(PersonInteraction interaction);

    Integer insertPersonInteraction(PersonInteraction interaction, Integer queryBotId);

    Integer insertPersonInteractionResponse(Integer personInteractionId, Timestamp receivedAt, String message, JSONObject jsResponse);

    List<PersonInteraction> getPersonInteractions(int personId, Locale locale) throws Exception;

    List<PersonInteraction> getPersonInteractionsByLoanApplication(int personId, int loanApplicationId, Locale locale) throws Exception;

    PersonInteraction getPersonInteractionById(int personInteractionId, Locale locale) throws Exception;

    List<PersonInteraction> getPersonInteractionByQueryBotId(int queryBotId, Locale locale) throws Exception;

    List<PersonInteraction> getPersonInteractionByBotId(int botId, Locale locale) throws Exception;

    void updateSentPersonInteraction(int personInteractionId);

    void insertPersonInteractionStatus(int personInteractionId, String event, Timestamp timestamp) throws Exception;

    List<PersonInteractionStat> getPersonInteractionStat(int personInteractionId) throws Exception;

    void updateInteraction(PersonInteraction personInteraction);

    Integer insertCallRequestInteraction(int loanApplicationId, String countryCode, String phoneNumber) throws Exception;

    List<VerificationCallRequest> getVerificationCallRequestInteraction(int loanApplicationId, String countryCode, String phoneNumber) throws Exception;

    PersonInteraction getPersonInteractionWithResponses(int personInteractionId, Locale locale) throws Exception;

    JSONArray getPersonInteractionHourOfDayForFilter() throws Exception;

    JSONArray getPersonInteractionContentForFilter() throws Exception;

    List<PersonInteraction> getPersonInteractionsById(int[] personInteractionIds, Locale locale) throws Exception;

    void updateProviderDataPersonInteraction(int personInteractionId, JSONObject jsonObject);
}
