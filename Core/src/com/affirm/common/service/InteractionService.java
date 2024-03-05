package com.affirm.common.service;

import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.PersonInteraction;
import com.affirm.common.model.transactional.VerificationCallRequest;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author jrodriguez
 */
public interface InteractionService {

    void sendPersonInteraction(PersonInteraction interaction, JSONObject jsonReplace, Map<String, String> tags) throws Exception;

    void sendPersonInteraction(PersonInteraction interaction, JSONObject jsonVars, Map<String, String> tags, Map<String, String> templateVars) throws Exception;

    void resendPersonInteraction(PersonInteraction interaction) throws Exception;

    Integer registerCallRequestPersonInteraction(Integer loanApplicationId, String countryCode, String phoneNumber) throws Exception;

    List<VerificationCallRequest> getVerificationCallRequest(int loanApplicationId, String countryCode, String phoneNumber) throws Exception;

    void registerUnsubscription(String encryptedEmail) throws Exception;

    void modifyInteractionContent(PersonInteraction personInteraction, LoanApplication loanApplication);
}
