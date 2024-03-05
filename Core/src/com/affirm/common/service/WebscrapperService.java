/**
 *
 */
package com.affirm.common.service;

import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.QueryBot;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * @author jrodriguez
 */
public interface WebscrapperService {
    QueryBot callSunatDniBot(String dni, int userId) throws Exception;

    QueryBot callSunatRucBot(String ruc, Integer userId) throws Exception;

    QueryBot callReniecBot(String docNumber, int userId) throws Exception;

    QueryBot callEssaludBot(int docType, String docNumber, int userId) throws Exception;

    //QueryBot callSbsBBot() throws Exception;

    QueryBot callRedamBot(int docType, String docNumber, int userId) throws Exception;

    QueryBot callClaroBot(int docType, String docNumber, Integer userId) throws Exception;

    QueryBot callMovistarBot(int docType, String docNumber, Integer userId) throws Exception;

    QueryBot callBitelBot(int docType, String docNumber, Integer userId) throws Exception;

    QueryBot callEntelBot(int docType, String docNumber, Integer userId) throws Exception;

    QueryBot callSatBot(int docType, String docNumber, Integer userId) throws Exception;

    QueryBot callSisBot(int docType, String docNumber, Integer userId) throws Exception;

    QueryBot callMigracionesBot(String docNumber, Date birthday, Integer userId);

    QueryBot callUserEmailDataBot(int userId, char emailProvider) throws Exception;

    QueryBot callVirginBot(int docType, String docNumber, int userId) throws Exception;

    int callCreateAccesoOffers(int loanApplicationId, double downPayment);

    QueryBot callBCRABot(int docType, String docNumber, Integer userId) throws Exception;

    QueryBot callAFIPBot(int docType, String docNumber, Integer userId) throws Exception;

    QueryBot callANSESBot(int docType, String docNumber, Integer userId) throws Exception;

    void sendToQueue(Integer queryBotId);

    CountryParam getCountry();

    void setCountry(CountryParam country);

    QueryBot callEvaluationBot(int loanApplicationId, boolean createdFromEntityExtranet, Date scheduledDate) throws Exception;

    QueryBot callEntityEvaluationBot(int loanApplicationId, int entityId, int productId, Date scheduledDate) throws Exception;

    QueryBot callReportBot(int reportProcesId) throws Exception;

    void runEquifax(int evaluation_id, int loanApplicationId, Integer documentTypeId, String documentNumber);

    QueryBot callUploadPreApprovedBase(int entityId, int productId, String csvUrl) throws Exception;

    QueryBot callSendSms(int countryId, String message, String csvUrl, Date scheduledDate, Integer sysUserId) throws Exception;

    QueryBot callRunSynthesized(String documentNumber, Integer loanApplicationId) throws Exception;

    QueryBot callONPEBot(String docNumber, Integer userId) throws Exception;

    QueryBot callSatPlateBot(int userId, String plate) throws Exception;

    QueryBot callSoatBot(int userId, String plate) throws Exception;

    QueryBot callBulkMailing(Integer userId, JSONObject json) throws Exception;

    QueryBot callRunFraudAlerts(LoanApplication loanApplication) throws Exception;

    QueryBot callApproveLoanApplication(LoanApplication loanApplication, Integer sysUserId, Locale locale, Integer auditTypeId, Integer userFileId);

    QueryBot callSendAccesoExpirationInteractions(JSONArray expirationsArray, Map<String, Object> mapParams, Integer userId) throws Exception;

    QueryBot callBoManagementFollowupInteraction(Integer interactionId, JSONArray loanApplicationArray, Map<String, Object> mapParams, Integer userId) throws Exception;

    QueryBot callUniversidadPeruBot(LoanApplication loanApplication, String ruc, int userId);

    QueryBot callUploadPreApprovedBaseCSV(Integer preApprovedProcessId) throws Exception;

    QueryBot callUploadNegativeBaseCSV(Integer negativeBaseProcessId) throws Exception;

    QueryBot callSendReportToFTPBot(Integer loanId) throws Exception;

    QueryBot callMatiProcess(Integer loanId) throws Exception;

    QueryBot callSendTConektaInformation(Integer loanApplicationId) throws Exception;
}
