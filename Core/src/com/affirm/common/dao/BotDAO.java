/**
 *
 */
package com.affirm.common.dao;

import com.affirm.common.model.transactional.*;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * @author jrodriguez
 */
public interface BotDAO {

//    public List<Bot> getBots() throws Exception;

    /**
     * Register the query created in the system.
     *
     * @param botId
     * @param statusId
     * @param startTime
     * @return QueryBot created with its id
     * @throws Exception
     */
    QueryBot registerQuery(int botId, int statusId, Date startTime, JSONObject parameters, Integer userId);

    QueryBot registerQuery(int botId, int statusId, Date startTime, JSONObject parameters, Integer userId, Date scheduledDate);

    QueryBot registerQuery(int botId, int statusId, Integer userId);

    public void updateQuery(QueryBot query) throws Exception;

    //SUNAT
    public void registerQueryResultSunat(int id, String ruc, int docType, String docNumber) throws Exception;

    void registerQueryResultAFIP(int id, int documentType, String documentNumber) throws Exception;

    void updateQueryResultSunat(int queryId, SunatResult sunatResult) throws Exception;

    //RENIEC
    public void registerQueryResultReniec(int id, String docNumber) throws Exception;

    public void updateQueryResultReniec(int queryId, ReniecResult reniecResult) throws Exception;

    //ESSALUD
    public void registerQueryResultEssalud(int id, int docType, String docNumber) throws Exception;

    public void updateQueryResultEssalud(int queryId, EssaludResult essaludResult) throws Exception;

    //SBS
    public void registerQueryResultSBS(int id) throws Exception;

    public void updateQueryResultSBS(int queryId, SbsResult sbsResult) throws Exception;

    public void updateQueryResultSBSrate(double sbsTopRate) throws Exception;

    //REDAM
    public void registerQueryResultRedam(int id, int docType, String docNumber) throws Exception;

    public void updateQueryResultRedam(int queryId, RedamResult redamResult) throws Exception;

    //SAT
    public void registerQueryResultSat(int id, int docType, String docNumber) throws Exception;

    public void updateQueryResultSat(int queryId, SatResult satResult) throws Exception;

    //SIS
    public void registerQueryResultSis(int id, int docType, String docNumber) throws Exception;

    public void updateQueryResultSis(int queryId, SisResult sisResult) throws Exception;

    //MIGRACIONES
    public void registerQueryResultMigrations(int id, String docNumber, Date birthday);

    public void updateQueryResultMigrations(int queryId, MigracionesResult migracionesResult) throws Exception;

    //PHONE CONTRACT
    public void registerQueryResultPhoneContracts(int id, int docType, String docNumber, String operator) throws Exception;

    public void updateQueryResultPhoneContracts(int queryId, LineaResult lineaResult) throws Exception;

    //BUFFER
    List<BufferTransaction> runBufferTransactions() throws Exception;

    //MANAGEMENT SCHEDULE
    void proccessManagementScheduleAll() throws Exception;

    //DAILY INTERACTIONS
    List<PersonInteraction> sendDailyInteractions() throws Exception;

    //ARE USER BOTS (Scrappers) RUNNING?
    boolean botsRunning(int userId) throws Exception;

    //DAILY PROCESS
    void dailyProccess() throws Exception;

    QueryBot getQueryBot(int queryId);

    Object getQueryResult(int queryId, int botId);

    void registerACSession(Integer entityId, String sessionId) throws Exception;

    String getACSession(Integer entityId) throws Exception;

    void updateQueryResultAFIP(int queryId, AfipResult afipResult) throws Exception;

    void registerQueryResultANSES(int id, int documentType, String documentNumber) throws Exception;

    void updateQueryResultANSES(int queryId, AnsesResult ansesResult) throws Exception;

    void registerQueryResultBCRA(int id, int documentType, String documentNumber) throws Exception;

    void updateQueryResultBCRA(int queryId, BcraResult bcraResult) throws Exception;


    void updateQueryResultPadron(int queryId, PadronResult padronResult) throws Exception;

    void registerACVehicles(String vehiclesJson) throws Exception;

    void registerProxyRequestLog(int proxyId, int queryBotId, boolean success) throws Exception;

    List<QueryBot> getQueryBotQueue();

    // Holidays
    void registerHolidays(HolidaysResult holidaysResult) throws Exception;

    public void registerHolidays(HolidaysResult holidaysResult, Integer countryId) throws Exception;

    List<QueryBot> getLoanApplicationEvaluationQueryBot(int loanApplicationId, int status);

    List<QueryBot> getScheduledQueryBots();

    List<QueryBot> getQueryBotsByBotId(int botId, int limit, int offset);

    //ONPE
    void registerQueryResultONPE(int id, String documentNumber) throws Exception;

    void updateQueryResultONPE(int queryId, OnpeResult onpeResult) throws Exception;

    List<PersonInteraction> getAutomaticInteractionsToSend() throws Exception;

    List<PersonInteraction> getApprovedDataMailsToSend(Integer interactionContentId) throws Exception;

    void registerQueryResultSATPlate(int id, String plate) throws Exception;

    void updateQueryResultSATPlate(int queryId, SatPlateResult satPlateResult) throws Exception;

    void registerQueryResultSoatRecords(int queryId, String plate) throws Exception;

    void updateQueryResultSoatRecords(int queryId, SoatRecordsResult satPlateResult) throws Exception;

    List<PersonInteraction> getHourlyInteractions(int hour) throws Exception;

    Boolean isBotAbleToRun(Integer botId) throws Exception;

    List<QueryBot> getScheduledQueryBots(Integer botId, String yearmonth) throws Exception;

    void updateParameters(int queryId, JSONObject params);

    void registerQueryResultUniversidadPeru(int id, String ruc);

    void updateQueryResultUniversidadPeru(int id, String phoneNumber, String phoneNumber2, String phoneNumber3, String phoneNumber4, String address, String district, String department);
}