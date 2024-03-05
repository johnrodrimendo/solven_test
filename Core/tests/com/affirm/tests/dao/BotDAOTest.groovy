package com.affirm.tests.dao

import com.affirm.common.dao.BotDAO
import com.affirm.common.model.transactional.*
import com.affirm.tests.BaseConfig
import org.json.JSONObject
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class BotDAOTest extends BaseConfig {

    @Autowired
    private BotDAO botDAO

    static final int BOT_ID = 1
    static final int STATUS_ID = 1
    static final Date CURRENT_DATE = new Date()
    static final Date START_TIME = CURRENT_DATE
    static final Date BIRTHDAY = CURRENT_DATE
    static final JSONObject PARAMETERS = new JSONObject()
    static final Integer USER_ID = 1633
    static final Date SCHEDULE_DATE = CURRENT_DATE
    static final QUERY_BOT = getQueryBot()
    static final QUERY_ID = 123312
    static final String RUC = "12345678977"
    static final Integer DOC_TYPE = 1
    static final String DOC_NUMBER = "45454545"
    static final SunatResult SUNAT_RESULT = getSunatResult()
    static final ReniecResult RENIEC_RESULT = getReniecResult()
    static final EssaludResult ESSALUD_RESULT = getEssaludResult()
    static final SbsResult SBS_RESULT = getSbsResult()
    static final double SBS_TOP_RATE = 20
    static final RedamResult REDAM_RESULT = getRedamResult()
    static final String OPERATOR = "operator"
    static final LineaResult LINEA_RESULT = getLineaResult()
    static final SatResult SAT_RESULT = getSatResult()
    static final SisResult SIS_RESULT = getSisResult()
    static final MigracionesResult MIGRACIONES_RESULT = getMigracionesResult()
    static final int ENTITY_ID = 12
    static final String SESSION_ID = "11"
    static final AfipResult AFIP_RESULT = getAfipResult()
    static final AnsesResult ANSES_RESULT = getAnsesResult()
    static final BcraResult BCRA_RESULT = getBcraResult()
    static final PadronResult PADRON_RESULT = getPadronResult()
    static final String VEHICLES_JSON = '["12345"]'
    static final int PROXY_ID = 2
    static final boolean SUCCESS = true
    static final HolidaysResult HOLIDAYS_RESULT = getHolidaysResult()
    static final Integer COUNTRY_ID = 51
    static final Integer LOAN_APPLICATION_ID = 1697
    static final Integer OFFSET = 10
    static final Integer LIMIT = 10
    static final OnpeResult ONPE_RESULT = getOnpeResult()
    static final Integer INTERACTION_CONTENT_ID  = 9
    static final String PLATE = ""
    static final SatPlateResult SAT_PLATE_RESULT = getSatPlateResult()
    static final SoatRecordsResult SOAT_RECORDS_RESULT = getSoatRecordsResult()
    static final int HOUR = 9
    static final String YEAR_MONTH = "5-01"


    static final QueryBot getQueryBot() {
        QueryBot queryBot = new QueryBot()

        return queryBot
    }

    static final SunatResult getSunatResult() {
        SunatResult sunatResult = new SunatResult()

        return sunatResult
    }

    static final ReniecResult getReniecResult() {
        ReniecResult reniecResult = new ReniecResult()

        return reniecResult
    }

    static final EssaludResult getEssaludResult() {
        EssaludResult essaludResult = new EssaludResult()

        return essaludResult
    }

    static final SbsResult getSbsResult() {
        SbsResult sbsResult = new SbsResult()
        sbsResult.setProducto("Producto")
        sbsResult.setEntidad("Entidad")
        sbsResult.setTasa(0.0)

        return sbsResult
    }

    static final RedamResult getRedamResult() {
        RedamResult redamResult = new RedamResult()

        return redamResult
    }

    static final LineaResult getLineaResult() {
        LineaResult lineaResult = new LineaResult()

        return lineaResult
    }

    static final SisResult getSisResult() {
        SisResult sisResult = new SisResult()

        return sisResult
    }

    static final SatResult getSatResult() {
        SatResult satResult = new SatResult()

        return satResult
    }

    static final MigracionesResult getMigracionesResult() {
        MigracionesResult migracionesResult = new MigracionesResult()

        return migracionesResult
    }

    static final AfipResult getAfipResult() {
        AfipResult afipResult = new AfipResult()

        return afipResult
    }

    static final AnsesResult getAnsesResult() {
        AnsesResult ansesResult = new AnsesResult()

        return ansesResult
    }

    static final BcraResult getBcraResult() {
        BcraResult bcraResult = new BcraResult()

        return bcraResult
    }

    static final PadronResult getPadronResult() {
        PadronResult padronResult = new PadronResult()

        return padronResult;
    }

    static final HolidaysResult getHolidaysResult() {
        HolidaysResult holidaysResult = new HolidaysResult()

        return holidaysResult
    }

    static final OnpeResult getOnpeResult() {
        OnpeResult onpeResult = new OnpeResult()

        return onpeResult
    }

    static final SatPlateResult getSatPlateResult() {
        SatPlateResult satPlateResult = new SatPlateResult()

        return satPlateResult
    }

    static final SoatRecordsResult getSoatRecordsResult() {
        SoatRecordsResult soatRecordsResult = new SoatRecordsResult()

        return soatRecordsResult
    }

    @Test
    void registerQueryFromBotDAO() {
        QueryBot result = botDAO.registerQuery(BOT_ID, STATUS_ID, START_TIME, PARAMETERS, USER_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void registerQuery2FromBotDAO() {
        QueryBot result = botDAO.registerQuery(BOT_ID, STATUS_ID, START_TIME, PARAMETERS, USER_ID, SCHEDULE_DATE)
        Assert.assertNotNull(result)
    }

    @Test
    void updateQueryFromBotDAO() {
        botDAO.updateQuery(QUERY_BOT)
    }

    @Test
    void registerQueryResultSunatFromBotDAO() {
        botDAO.registerQueryResultSunat(QUERY_ID, RUC, DOC_TYPE, DOC_NUMBER)
    }

    @Test
    void updateQueryResultSunatFromBotDAO() {
        botDAO.updateQueryResultSunat(QUERY_ID, SUNAT_RESULT)
    }

    @Test
    void registerQueryResultReniecFromBotDAO() {
        botDAO.registerQueryResultReniec(QUERY_ID, DOC_NUMBER)
    }

    @Test
    void updateQueryResultReniecFromBotDAO() {
        botDAO.updateQueryResultReniec(QUERY_ID, RENIEC_RESULT)
    }

    @Test
    void registerQueryResultEssaludFromBotDAO() {
        botDAO.registerQueryResultEssalud(QUERY_ID, DOC_TYPE, DOC_NUMBER)
    }

    @Test
    void updateQueryResultEssaludFromBotDAO() {
        botDAO.updateQueryResultEssalud(QUERY_ID, ESSALUD_RESULT)
    }

    @Test
    void registerQueryResultSBSFromBotDAO() {
        botDAO.registerQueryResultSBS(QUERY_ID)
    }

    @Test
    void updateQueryResultSBSFromBotDAO() {
        botDAO.updateQueryResultSBS(QUERY_ID, SBS_RESULT)
    }

    @Test
    void updateQueryResultSBSrateFromBotDAO() {
        botDAO.updateQueryResultSBSrate(SBS_TOP_RATE)
    }

    @Test
    void registerQueryResultRedamFromBotDAO() {
        botDAO.registerQueryResultRedam(QUERY_ID, DOC_TYPE, DOC_NUMBER)
    }

    @Test
    void updateQueryResultRedamFromBotDAO() {
        botDAO.updateQueryResultRedam(QUERY_ID, REDAM_RESULT)
    }

    @Test
    void registerQueryResultPhoneContractsFromBotDAO() {
        botDAO.registerQueryResultPhoneContracts(QUERY_ID, DOC_TYPE, DOC_NUMBER, OPERATOR)
    }

    @Test
    void updateQueryResultPhoneContractsFromBotDAO() {
        botDAO.updateQueryResultPhoneContracts(QUERY_ID, LINEA_RESULT)
    }

    @Test
    void registerQueryResultSatFromBotDAO() {
        botDAO.registerQueryResultSat(QUERY_ID, DOC_TYPE, DOC_NUMBER)
    }

    @Test
    void updateQueryResultSatFromBotDAO() {
        botDAO.updateQueryResultSat(QUERY_ID, SAT_RESULT)
    }

    @Test
    void registerQueryResultSisFromBotDAO() {
        botDAO.registerQueryResultSis(QUERY_ID, DOC_TYPE, DOC_NUMBER)
    }

    @Test
    void updateQueryResultSisFromBotDAO() {
        botDAO.updateQueryResultSis(QUERY_ID, SIS_RESULT)
    }

    @Test
    void registerQueryResultMigrationsFromBotDAO() {
        botDAO.registerQueryResultMigrations(QUERY_ID, DOC_NUMBER, BIRTHDAY)
    }

    @Test
    void updateQueryResultMigrationsFromBotDAO() {
        botDAO.updateQueryResultMigrations(QUERY_ID, MIGRACIONES_RESULT)
    }

    @Test
    void runBufferTransactionsFromBotDAO() {
        List<BufferTransaction> result = botDAO.runBufferTransactions()
        Assert.assertNull(result)
    }

    @Test
    void proccessManagementScheduleAllFromBotDAO() {
        botDAO.proccessManagementScheduleAll()
    }

    @Test
    void sendDailyInteractionsFromBotDAO() {
        List<PersonInteraction> result = botDAO.sendDailyInteractions()
        Assert.assertNotNull(result)
    }

    @Test
    void getQueryBotFromBotDAO() {
        QueryBot result = botDAO.getQueryBot(QUERY_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void botsRunningFromBotDAO() {
        boolean result = botDAO.botsRunning(USER_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void dailyProccessFromBotDAO() {
        botDAO.dailyProccess()
    }

    @Test
    void getQueryResultFromBotDAO() {
        Object result = botDAO.getQueryResult(QUERY_ID, BOT_ID)
        Assert.assertNull(result)
    }

    @Test
    void registerACSessionFromBotDAO() {
        botDAO.registerACSession(ENTITY_ID, SESSION_ID)
    }

    @Test
    void getACSessionFromBotDAO() {
        String result = botDAO.getACSession(ENTITY_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void registerQueryResultAFIPFromBotDAO() {
        botDAO.registerQueryResultAFIP(QUERY_ID, DOC_TYPE, DOC_NUMBER)
    }

    @Test
    void updateQueryResultAFIPFromBotDAO() {
        botDAO.updateQueryResultAFIP(QUERY_ID, AFIP_RESULT)
    }

    @Test
    void registerQueryResultANSESFromBotDAO() {
        botDAO.registerQueryResultANSES(QUERY_ID, DOC_TYPE, DOC_NUMBER)
    }

    @Test
    void updateQueryResultANSESFromBotDAO() {
        botDAO.updateQueryResultANSES(QUERY_ID, ANSES_RESULT)
    }

    @Test
    void registerQueryResultBCRAFromBotDAO() {
        botDAO.registerQueryResultBCRA(QUERY_ID, DOC_TYPE, DOC_NUMBER)
    }

    @Test
    void updateQueryResultBCRAFromBotDAO() {
        botDAO.updateQueryResultBCRA(QUERY_ID, BCRA_RESULT)
    }

    @Test
    void updateQueryResultPadronFromBotDAO() {
        botDAO.updateQueryResultPadron(QUERY_ID, PADRON_RESULT)
    }

    @Test
    void registerACVehiclesFromBotDAO() {
        botDAO.registerACVehicles(VEHICLES_JSON)
    }

    @Test
    void registerProxyRequestLogFromBotDAO() {
        botDAO.registerProxyRequestLog(PROXY_ID, QUERY_ID, SUCCESS)
    }

    @Test
    void getQueryBotQueueFromBotDAO() {
        List<QueryBot> result = botDAO.getQueryBotQueue()
        Assert.assertNotNull(result)
    }

    @Test
    void registerHolidaysFromBotDAO() {
        botDAO.registerHolidays(HOLIDAYS_RESULT)
    }

    @Test
    void registerHolidays2FromBotDAO() {
        botDAO.registerHolidays(HOLIDAYS_RESULT, COUNTRY_ID)
    }

    @Test
    void getLoanApplicationEvaluationQueryBotFromBotDAO() {
        List<QueryBot> result = botDAO.getLoanApplicationEvaluationQueryBot(LOAN_APPLICATION_ID, STATUS_ID)
        Assert.assertNull(result)
    }

    @Test
    void getScheduledQueryBotsFromBotDAO() {
        List<QueryBot> result = botDAO.getScheduledQueryBots()
        Assert.assertNull(result)
    }

    @Test
    void getQueryBotsByBotIdFromBotDAO() {
        List<QueryBot> result = botDAO.getQueryBotsByBotId(BOT_ID, LIMIT, OFFSET)
        Assert.assertNotNull(result)
    }

    @Test
    void registerQueryResultONPEFromBotDAO() {
        botDAO.registerQueryResultONPE(QUERY_ID, DOC_NUMBER)
    }

    @Test
    void updateQueryResultONPEFromBotDAO() {
        QueryBot result = botDAO.updateQueryResultONPE(QUERY_ID, ONPE_RESULT)
        Assert.assertNull(result)
    }

    @Test
    void getAutomaticInteractionsToSendFromBotDAO() {
        List<PersonInteraction> result = botDAO.getAutomaticInteractionsToSend()
        Assert.assertNotNull(result)
    }

    @Test
    void getApprovedDataMailsToSendFromBotDAO() {
        List<PersonInteraction> result = botDAO.getApprovedDataMailsToSend(INTERACTION_CONTENT_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void registerQueryResultSATPlateFromBotDAO() {
        botDAO.registerQueryResultSATPlate(QUERY_ID, PLATE)
    }

    @Test
    void updateQueryResultSATPlateFromBotDAO() {
        botDAO.updateQueryResultSATPlate(QUERY_ID, SAT_PLATE_RESULT)
    }

    @Test
    void registerQueryResultSoatRecordsFromBotDAO() {
        botDAO.registerQueryResultSoatRecords(QUERY_ID, PLATE)
    }

    @Test
    void updateQueryResultSoatRecordsFromBotDAO() {
        botDAO.updateQueryResultSoatRecords(QUERY_ID, SOAT_RECORDS_RESULT)
    }

    @Test
    void getHourlyInteractionsFromBotDAO() {
        List<PersonInteraction> result = botDAO.getHourlyInteractions(HOUR)
        Assert.assertNotNull(result)
    }

    @Test
    void isBotAbleToRunFromBotDAO() {
        Boolean result = botDAO.isBotAbleToRun(BOT_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void getScheduled2QueryBotsFromBotDAO() {
        List<QueryBot> result = botDAO.getScheduledQueryBots(BOT_ID, YEAR_MONTH)
        Assert.assertNotNull(result)
    }

}
