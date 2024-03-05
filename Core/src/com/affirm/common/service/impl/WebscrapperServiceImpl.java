/**
 *
 */
package com.affirm.common.service.impl;


import com.affirm.common.dao.BotDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.catalog.Bot;
import com.affirm.common.model.catalog.CountryParam;
import com.affirm.common.model.catalog.PhoneContractOperator;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.QueryBot;
import com.affirm.common.model.transactional.SunatResult;
import com.affirm.common.service.WebscrapperService;
import com.affirm.system.configuration.Configuration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * @author jrodriguez
 */

// TODO Rename this service to BotService
@Service("webscrapperService")
public class WebscrapperServiceImpl implements WebscrapperService {

    private static final Logger logger = Logger.getLogger(WebscrapperServiceImpl.class);

    @Autowired
    private BotDAO botDao;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;

    private CountryParam country;

    @Override
    public QueryBot callSunatDniBot(String dni, int userId) throws Exception {

        if (country == null || country.getId() != CountryParam.COUNTRY_PERU) {
            return null;
        }

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.SUNAT_BOT, QueryBot.STATUS_QUEUE, new Date(), null, userId);

        // Register params
        botDao.registerQueryResultSunat(queryBot.getId(), null, SunatResult.DNI_TYPE, dni);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callSunatRucBot(String ruc, Integer userId) throws Exception {

        if (country == null || country.getId() != CountryParam.COUNTRY_PERU) {
            return null;
        }

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.SUNAT_BOT, QueryBot.STATUS_QUEUE, new Date(), null, userId);

        // Register params
        botDao.registerQueryResultSunat(queryBot.getId(), null, SunatResult.RUC_TYPE, ruc);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callReniecBot(String docNumber, int userId) throws Exception {
        if (country == null || country.getId() != CountryParam.COUNTRY_PERU) {
            return null;
        }

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.RENIEC_BOT, QueryBot.STATUS_QUEUE, new Date(), null, userId);

        // Register params
        botDao.registerQueryResultReniec(queryBot.getId(), docNumber);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callEssaludBot(int docType, String docNumber, int userId) throws Exception {

        if (country == null || country.getId() != CountryParam.COUNTRY_PERU) {
            return null;
        }

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.ESSALUD_BOT, QueryBot.STATUS_QUEUE, new Date(), null, userId);

        // Register params
        botDao.registerQueryResultEssalud(queryBot.getId(), docType, docNumber);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    /*@Override
    public QueryBot callSbsBBot() throws Exception {

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.SBS_B_BOT, QueryBot.STATUS_QUEUE, new Date(), null, null);

        // Register params
        botDao.registerQueryResultSBS(queryBot.getId());

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }*/

    @Override
    public QueryBot callRedamBot(int docType, String docNumber, int userId) throws Exception {

        if (country == null || country.getId() != CountryParam.COUNTRY_PERU) {
            return null;
        }

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.REDAM_BOT, QueryBot.STATUS_QUEUE, new Date(), null, userId);

        // Register params
        botDao.registerQueryResultRedam(queryBot.getId(), docType, docNumber);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callClaroBot(int docType, String docNumber, Integer userId) throws Exception {

        if (country == null || country.getId() != CountryParam.COUNTRY_PERU) {
            return null;
        }

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.CLARO, QueryBot.STATUS_QUEUE, new Date(), null, userId);

        // Register params
        botDao.registerQueryResultPhoneContracts(queryBot.getId(), docType, docNumber, PhoneContractOperator.CLARO);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callMovistarBot(int docType, String docNumber, Integer userId) throws Exception {

        if (country == null || country.getId() != CountryParam.COUNTRY_PERU) {
            return null;
        }

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.MOVISTAR, QueryBot.STATUS_QUEUE, new Date(), null, userId);

        // Register params
        botDao.registerQueryResultPhoneContracts(queryBot.getId(), docType, docNumber, PhoneContractOperator.MOVISTAR);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callBitelBot(int docType, String docNumber, Integer userId) throws Exception {

        if (country == null || country.getId() != CountryParam.COUNTRY_PERU) {
            return null;
        }

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.BITEL, QueryBot.STATUS_QUEUE, new Date(), null, userId);

        // Register params
        botDao.registerQueryResultPhoneContracts(queryBot.getId(), docType, docNumber, PhoneContractOperator.BITEL);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callEntelBot(int docType, String docNumber, Integer userId) throws Exception {

        if (country == null || country.getId() != CountryParam.COUNTRY_PERU) {
            return null;
        }

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.ENTEL, QueryBot.STATUS_QUEUE, new Date(), null, userId);

        // Register params
        botDao.registerQueryResultPhoneContracts(queryBot.getId(), docType, docNumber, PhoneContractOperator.ENTEL);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callSatBot(int docType, String docNumber, Integer userId) throws Exception {

        if (country == null || country.getId() != CountryParam.COUNTRY_PERU) {
            return null;
        }

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.SAT, QueryBot.STATUS_QUEUE, new Date(), null, userId);

        // Register params
        botDao.registerQueryResultSat(queryBot.getId(), docType, docNumber);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callSatPlateBot(int userId, String plate) throws Exception {

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.SAT_PLATE, QueryBot.STATUS_QUEUE, new Date(), null, userId);
        // Register params
        botDao.registerQueryResultSATPlate(queryBot.getId(), plate);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callSisBot(int docType, String docNumber, Integer userId) throws Exception {

        if (country == null || country.getId() != CountryParam.COUNTRY_PERU) {
            return null;
        }

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.SIS, QueryBot.STATUS_QUEUE, new Date(), null, userId);

        // Register params
        botDao.registerQueryResultSis(queryBot.getId(), docType, docNumber);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callMigracionesBot(String docNumber, Date birthday, Integer userId) {

        if (country == null || country.getId() != CountryParam.COUNTRY_PERU) {
            return null;
        }

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.MIGRACIONES, QueryBot.STATUS_QUEUE, new Date(), null, userId);

        // Register params
        botDao.registerQueryResultMigrations(queryBot.getId(), docNumber, birthday);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callUserEmailDataBot(int userId, char emailProvider) throws Exception {
        // Register query
        JSONObject params = new JSONObject();
        params.put("userId", userId);
        QueryBot queryBot = botDao.registerQuery(
                emailProvider == 'G' ? Bot.GOOGLE_USER_DATA : emailProvider == 'W' ? Bot.WINDOWS_USER_DATA : emailProvider == 'Y' ? Bot.YAHOO_USER_DATA : null,
                QueryBot.STATUS_QUEUE, new Date(), params, userId);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.DEFAULT), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callVirginBot(int docType, String docNumber, int userId) throws Exception {
        if (country == null || country.getId() != CountryParam.COUNTRY_PERU) {
            return null;
        }

        logger.debug("callVirginBot");

        JSONObject params = new JSONObject();
        params.put("docType", docType);
        params.put("docNumber", docNumber);
        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.VIRGIN, QueryBot.STATUS_QUEUE, new Date(), params, userId);

        // Register params
        botDao.registerQueryResultPhoneContracts(queryBot.getId(), docType, docNumber, PhoneContractOperator.VIRGIN);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public int callCreateAccesoOffers(int loanApplicationId, double downPayment) {
        // Register query
        JSONObject params = new JSONObject();
        params.put("loanApplicationId", loanApplicationId);
        params.put("downPayment", downPayment);
        QueryBot queryBot = botDao.registerQuery(Bot.ACCESO_OFFERS, QueryBot.STATUS_QUEUE, new Date(), params, null);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.DEFAULT), queryBot.getId() + ""));

        return queryBot.getId();
    }

    @Override
    public QueryBot callBCRABot(int docType, String docNumber, Integer userId) throws Exception {

        if (country == null || country.getId() != CountryParam.COUNTRY_ARGENTINA) {
            return null;
        }

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.BCRA, QueryBot.STATUS_QUEUE, new Date(), null, userId);

        // Register params
        botDao.registerQueryResultBCRA(queryBot.getId(), docType, docNumber);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callAFIPBot(int docType, String docNumber, Integer userId) throws Exception {

        if (country == null || country.getId() != CountryParam.COUNTRY_ARGENTINA) {
            return null;
        }

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.AFIP, QueryBot.STATUS_QUEUE, new Date(), null, userId);

        // Register params
        botDao.registerQueryResultAFIP(queryBot.getId(), docType, docNumber);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callANSESBot(int docType, String docNumber, Integer userId) throws Exception {

        if (country == null || country.getId() != CountryParam.COUNTRY_ARGENTINA) {
            return null;
        }

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.ANSES, QueryBot.STATUS_QUEUE, new Date(), null, userId);

        // Register params
        botDao.registerQueryResultANSES(queryBot.getId(), docType, docNumber);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public void sendToQueue(Integer queryBotId) {
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.DEFAULT), queryBotId + ""));// TODO CUAL ES SU COLA?
    }

    @Override
    public CountryParam getCountry() {
        return country;
    }

    @Override
    public void setCountry(CountryParam country) {
        this.country = country;
    }

    @Override
    public QueryBot callEvaluationBot(int loanApplicationId, boolean createdFromEntityExtranet, Date scheduledDate) throws Exception {

        // Register query
        JSONObject params = new JSONObject();
        params.put("loanApplicationId", loanApplicationId);
        params.put("createdFromEntityExtranet", createdFromEntityExtranet);

        // Call AWS SQS
        QueryBot queryBot;
        if (scheduledDate == null) {
            queryBot = botDao.registerQuery(Bot.EVALUATION_PROCESS, QueryBot.STATUS_QUEUE, new Date(), params, null);

            AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
            SendMessageRequest request = new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.EVALUATION_INITIALIZER), queryBot.getId() + "");
            sqs.sendMessage(request);
        } else {
            queryBot = botDao.registerQuery(Bot.EVALUATION_PROCESS, QueryBot.STATUS_SCHEDULED, new Date(), params, null, scheduledDate);
        }

        return queryBot;
    }

    @Override
    public QueryBot callEntityEvaluationBot(int loanApplicationId, int entityId, int productId, Date scheduledDate) throws Exception {

        // Register query
        JSONObject params = new JSONObject();
        params.put("loanApplicationId", loanApplicationId);
        params.put("entityId", entityId);
        params.put("productId", productId);

        // Call AWS SQS
        QueryBot queryBot;
        if (scheduledDate == null) {
            queryBot = botDao.registerQuery(Bot.ENTITY_EVALUATION_PROCESS, QueryBot.STATUS_QUEUE, new Date(), params, null);

            AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
            SendMessageRequest request = new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.EVALUATION_PROCESSOR), queryBot.getId() + "");
            sqs.sendMessage(request);
        } else {
            queryBot = botDao.registerQuery(Bot.ENTITY_EVALUATION_PROCESS, QueryBot.STATUS_SCHEDULED, new Date(), params, null, scheduledDate);
        }

        return queryBot;
    }

    @Override
    public QueryBot callReportBot(int reportProcesId) throws Exception {

        // Register query
        JSONObject params = new JSONObject();
        params.put("reportProcesId", reportProcesId);
        QueryBot queryBot = botDao.registerQuery(Bot.REPORT_PROCESOR, QueryBot.STATUS_QUEUE, new Date(), params, null);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.REPORT), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public void runEquifax(int evaluation_id, int loanApplicationId, Integer documentTypeId, String documentNumber) {
        // Register query
        JSONObject params = new JSONObject();
        params.put("evaluationId", evaluation_id);
        params.put("loanApplicationId", loanApplicationId);
        params.put("docType", documentTypeId);
        params.put("docNumber", documentNumber);
        QueryBot queryBot = botDao.registerQuery(Bot.EQUIFAX_BUREAU, QueryBot.STATUS_QUEUE, new Date(), params, null);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.DEFAULT), queryBot.getId() + ""));
    }

    @Override
    public QueryBot callUploadPreApprovedBase(int entityId, int productId, String csvUrl) throws Exception {

        // Register query
        JSONObject params = new JSONObject();
        params.put("entityId", entityId);
        params.put("productId", productId);
        params.put("fileName", csvUrl);
        QueryBot queryBot = botDao.registerQuery(Bot.UPLOAD_PRE_APPROVED_BASE, QueryBot.STATUS_QUEUE, new Date(), params, null);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.DEFAULT), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callRunSynthesized(String documentNumber, Integer loanApplicationId) throws Exception {

        // Register query
        JSONObject params = new JSONObject();
        params.put("documentNumber", documentNumber);
        params.put("loanApplicationId", loanApplicationId);
        QueryBot queryBot = botDao.registerQuery(Bot.RUN_SYNTHESIZED, QueryBot.STATUS_QUEUE, new Date(), params, null);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callSendSms(int countryId, String message, String csvUrl, Date scheduledDate, Integer sysUserId) throws Exception {

        // Register query
        JSONObject params = new JSONObject();
        params.put("countryId", countryId);
        params.put("message", message);
        params.put("fileName", csvUrl);
        params.put("sysUserId", sysUserId);

        QueryBot queryBot;
        if (scheduledDate == null) {
            queryBot = botDao.registerQuery(Bot.SEND_SMS, QueryBot.STATUS_QUEUE, new Date(), params, null);

            AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
            sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.DEFAULT), queryBot.getId() + ""));
        } else {
            queryBot = botDao.registerQuery(Bot.SEND_SMS, QueryBot.STATUS_SCHEDULED, new Date(), params, null, scheduledDate);
        }
        return queryBot;
    }

    @Override
    public QueryBot callONPEBot(String docNumber, Integer userId) throws Exception {

        if (country == null || country.getId() != CountryParam.COUNTRY_PERU) {
            return null;
        }

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.ONPE, QueryBot.STATUS_QUEUE, new Date(), null, userId);

        // Register params
        botDao.registerQueryResultONPE(queryBot.getId(), docNumber);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callSoatBot(int userId, String plate) throws Exception {

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.SOAT_RECORDS, QueryBot.STATUS_QUEUE, new Date(), null, userId);
        // Register params
        botDao.registerQueryResultSoatRecords(queryBot.getId(), plate);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callBulkMailing(Integer userId, JSONObject json) throws Exception {

        // Register query
        QueryBot queryBot = botDao.registerQuery(Bot.RE_EVALUATION_EMAIL_SENDER, QueryBot.STATUS_QUEUE, new Date(), json, userId);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.DEFAULT), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callRunFraudAlerts(LoanApplication loanApplication) throws Exception {

        // Register query
        JSONObject params = new JSONObject();
        params.put("loanApplicationId", loanApplication.getId());
        QueryBot queryBot = botDao.registerQuery(Bot.FRAUD_ALERTS, QueryBot.STATUS_QUEUE, new Date(), params, null);

        loanApplication.getFraudAlertQueryIds().add(queryBot.getId());
        loanApplicationDao.updateFraudAlertQueryBots(loanApplication.getId(), new Gson().toJson(loanApplication.getFraudAlertQueryIds()));

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.FRAUD_ALERTS), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callSendAccesoExpirationInteractions(JSONArray expirationsArray, Map<String, Object> mapParams, Integer userId) throws Exception {

        // Register query
        JSONObject params = new JSONObject();
        params.put("expirations", expirationsArray);

        for (Map.Entry<String, Object> entry: mapParams.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }

        QueryBot queryBot = botDao.registerQuery(Bot.SEND_ACCESO_EXPIRATIO_INTERACTIONS, QueryBot.STATUS_QUEUE, new Date(), params, userId);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.DEFAULT), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callBoManagementFollowupInteraction(Integer interactionId, JSONArray loanApplicationArray, Map<String, Object> mapParams, Integer userId) throws Exception {

        // Register query
        JSONObject params = new JSONObject();
        params.put("interactionId", interactionId);
        params.put("loanApplicationIds", loanApplicationArray);

        for (Map.Entry<String, Object> entry : mapParams.entrySet()) {
            params.put(entry.getKey(), entry.getValue());
        }

        QueryBot queryBot = botDao.registerQuery(Bot.BO_MANAGEMENT_FOLLOWUP_INTERACTION, QueryBot.STATUS_QUEUE, new Date(), params, userId);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.DEFAULT), queryBot.getId() + ""));// TODO SQS

        return queryBot;
    }

    @Override
    public QueryBot callApproveLoanApplication(LoanApplication loanApplication, Integer sysUserId, Locale locale, Integer auditTypeId, Integer userFileId) {

        Gson gson = new Gson();

        // Register query
        JSONObject params = new JSONObject();
        params.put("loanApplicationId", loanApplication.getId());
        params.put("sysUserId", sysUserId);
        params.put("locale", gson.toJson(locale));
        params.put("auditTypeId", auditTypeId);
        params.put("userFileId", userFileId);

        QueryBot queryBot = botDao.registerQuery(Bot.APPROVE_LOAN_APPLICATION, QueryBot.STATUS_QUEUE, new Date(), params, null);

        if (loanApplication.getApprovalQueryBotIds() == null) {
            loanApplication.setApprovalQueryBotIds(new ArrayList<>());
        }

        loanApplication.getApprovalQueryBotIds().add(queryBot.getId());
        loanApplicationDao.updateApprovalQueryBotIds(loanApplication.getId(), new Gson().toJson(loanApplication.getApprovalQueryBotIds()));

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.APPROVE_LOAN_APPLICATION), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callUniversidadPeruBot(LoanApplication loanApplication, String ruc, int userId) {

        // Register query
        JSONObject params = new JSONObject();
        params.put("loanApplicationId", loanApplication.getId());
        QueryBot queryBot = botDao.registerQuery(Bot.UNIVERSIDAD_PERU, QueryBot.STATUS_QUEUE, new Date(), params, userId);

        // Register params
        botDao.registerQueryResultUniversidadPeru(queryBot.getId(), ruc);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.SCRAPPER), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callUploadPreApprovedBaseCSV(Integer preApprovedProcessId) throws Exception {

        // Register query
        JSONObject params = new JSONObject();
        params.put("preApprovedProcessId", preApprovedProcessId);
        QueryBot queryBot = botDao.registerQuery(Bot.LOAD_PRE_APPROVED_BASE, QueryBot.STATUS_QUEUE, new Date(), params, null);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.DEFAULT), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callUploadNegativeBaseCSV(Integer negativeBaseProcessId) throws Exception {

        // Register query
        JSONObject params = new JSONObject();
        params.put("negativeBaseProcessId", negativeBaseProcessId);
        QueryBot queryBot = botDao.registerQuery(Bot.LOAD_NEGATIVE_BASE, QueryBot.STATUS_QUEUE, new Date(), params, null);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.DEFAULT), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callSendReportToFTPBot(Integer loanId) throws Exception {

        // Register query
        JSONObject params = new JSONObject();
        params.put("loanId", loanId);
        QueryBot queryBot = botDao.registerQuery(Bot.SEND_REPORT_TO_FTP, QueryBot.STATUS_QUEUE, new Date(), params, null);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.DEFAULT), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callMatiProcess(Integer loanId) throws Exception {

        // Register query
        JSONObject params = new JSONObject();
        params.put("loanId", loanId);
        QueryBot queryBot = botDao.registerQuery(Bot.MATI_PROCESS, QueryBot.STATUS_QUEUE, new Date(), params, null);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        sqs.sendMessage(new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.DEFAULT), queryBot.getId() + ""));

        return queryBot;
    }

    @Override
    public QueryBot callSendTConektaInformation(Integer loanApplicationId) throws Exception {

        // Register query
        JSONObject params = new JSONObject();
        params.put("loanApplicationId", loanApplicationId);
        QueryBot queryBot = botDao.registerQuery(Bot.SEND_TCONEKTA_INFORMATION, QueryBot.STATUS_QUEUE, new Date(), params, null);

        // Call AWS SQS
        AmazonSQS sqs = new AmazonSQSClient(new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_ACCESS_KEY")));
        SendMessageRequest request = new SendMessageRequest(Configuration.queueUrl(Configuration.SqsQueue.DEFAULT), queryBot.getId() + "");
        request.setDelaySeconds(4*60);
        sqs.sendMessage(request);

        return queryBot;
    }

}
