package com.affirm.common.dao.impl;

import com.affirm.common.dao.BotDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.model.catalog.Bot;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jrodriguez
 */

@Repository
public class BotDAOImpl extends JsonResolverDAO implements BotDAO {

    @Autowired
    private CatalogService catalogService;

    @Override
    public QueryBot registerQuery(int botId, int statusId, Integer userId) {
        return registerQuery(botId, statusId, new Date(), null, userId, null);
    }

    @Override
    public QueryBot registerQuery(int botId, int statusId, Date startTime, JSONObject parameters, Integer userId) {
        return registerQuery(botId, statusId, startTime, parameters, userId, null);
    }

    @Override
    public QueryBot registerQuery(int botId, int statusId, Date startTime, JSONObject parameters, Integer userId, Date scheduledDate) {
        JSONObject dbJson = queryForObjectExternal("select * from external.ins_query(?, ?, ?, ?, ?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, botId),
                new SqlParameterValue(Types.INTEGER, statusId),
                new SqlParameterValue(Types.TIMESTAMP, startTime),
                new SqlParameterValue(Types.OTHER, parameters),
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.TIMESTAMP, scheduledDate));

        QueryBot queryBot = new QueryBot();
        queryBot.setId(dbJson.getInt("query_id"));
        queryBot.setStatusId(statusId);
        queryBot.setStartTime(startTime);
        return queryBot;
    }

    @Override
    public void updateQuery(QueryBot queryBot) throws Exception {
        queryForObjectExternal("select * from external.upd_query(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, queryBot.getId()),
                new SqlParameterValue(Types.INTEGER, queryBot.getStatusId()),
                new SqlParameterValue(Types.TIMESTAMP, queryBot.getFinishTime()));
    }

    @Override
    public void registerQueryResultSunat(int id, String ruc, int docType, String docNumber) throws Exception {
        queryForObjectExternal("select * from external.ins_query_result_sunat(?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, id),
                new SqlParameterValue(Types.VARCHAR, ruc),
                new SqlParameterValue(Types.VARCHAR, docType + ""),
                new SqlParameterValue(Types.VARCHAR, docNumber));
    }

    @Override
    public void updateQueryResultSunat(int queryId, SunatResult sunatResult) throws Exception {
        queryForObjectExternal("select * from external.upd_query_result_sunat_warmi(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, queryId),
                new SqlParameterValue(Types.VARCHAR, sunatResult.getRuc()),
                new SqlParameterValue(Types.VARCHAR, sunatResult.getTradeName()),
                new SqlParameterValue(Types.VARCHAR, sunatResult.getLocated()),
                new SqlParameterValue(Types.VARCHAR, sunatResult.getStatus()),
                new SqlParameterValue(Types.VARCHAR, sunatResult.getTaxpayerType()),
                new SqlParameterValue(Types.TIMESTAMP, sunatResult.getRegisterDate()),
                new SqlParameterValue(Types.TIMESTAMP, sunatResult.getStartupDate()),
                new SqlParameterValue(Types.VARCHAR, sunatResult.getTaxpayerCondition()),
                new SqlParameterValue(Types.VARCHAR, sunatResult.getFiscalAddress()),
                new SqlParameterValue(Types.VARCHAR, sunatResult.getVoucherEmittingSystem()),
                new SqlParameterValue(Types.VARCHAR, sunatResult.getExternalCommerceActivity()),
                new SqlParameterValue(Types.VARCHAR, sunatResult.getAccountingSystem()),
                new SqlParameterValue(Types.VARCHAR, sunatResult.getOcupation()),
                new SqlParameterValue(Types.OTHER, sunatResult.getEconomicActivities()),
                new SqlParameterValue(Types.OTHER, sunatResult.getVoucher()),
                new SqlParameterValue(Types.VARCHAR, sunatResult.getDigitalEmissionSystem()),
                new SqlParameterValue(Types.DATE, sunatResult.getDigitalEmitterSince()),
                new SqlParameterValue(Types.VARCHAR, sunatResult.getDigitalVouchers()),
                new SqlParameterValue(Types.VARCHAR, sunatResult.getPleJoinedSince()),
                new SqlParameterValue(Types.OTHER, sunatResult.getPadron()));
    }

    @Override
    public void registerQueryResultReniec(int id, String docNumber) throws Exception {
        queryForObjectExternal("select * from external.ins_query_result_reniec(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, id),
                new SqlParameterValue(Types.VARCHAR, docNumber));
    }

    @Override
    public void updateQueryResultReniec(int queryId, ReniecResult reniecResult) throws Exception {
        queryForObjectExternal("select * from external.upd_query_result_reniec(?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, queryId),
                new SqlParameterValue(Types.VARCHAR, reniecResult.getDocument_number()),
                new SqlParameterValue(Types.VARCHAR, reniecResult.getFull_name()),
                new SqlParameterValue(Types.VARCHAR, reniecResult.getNineth_digit()),
                new SqlParameterValue(Types.VARCHAR, reniecResult.getPrevious_character_verification()));
    }

    @Override
    public void registerQueryResultEssalud(int id, int docType, String docNumber) throws Exception {
        queryForObjectExternal("select * from external.ins_query_result_essalud(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, id),
                new SqlParameterValue(Types.INTEGER, docType),
                new SqlParameterValue(Types.VARCHAR, docNumber));
    }

    @Override
    public void updateQueryResultEssalud(int queryId, EssaludResult essaludResult) throws Exception {
        queryForObjectExternal("select * from external.upd_query_result_essalud(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, queryId),
                new SqlParameterValue(Types.VARCHAR, essaludResult.getFullName()),
                new SqlParameterValue(Types.VARCHAR, essaludResult.getDocumentNumber()),
                new SqlParameterValue(Types.VARCHAR, essaludResult.getInssuredType()),
                new SqlParameterValue(Types.VARCHAR, essaludResult.getAutogeneratedID()),
                new SqlParameterValue(Types.VARCHAR, essaludResult.getInssuranceType()),
                new SqlParameterValue(Types.VARCHAR, essaludResult.getHealthCenter()),
                new SqlParameterValue(Types.VARCHAR, essaludResult.getHealthCenterAddress()),
                new SqlParameterValue(Types.DATE, essaludResult.getSince()),
                new SqlParameterValue(Types.DATE, essaludResult.getUntil()),
                new SqlParameterValue(Types.VARCHAR, essaludResult.getAfiliatedTo()));
    }

    @Override
    public void registerQueryResultSBS(int id) throws Exception {
        queryForObjectExternal("select * from external.ins_query_result_sbs_b(?)", String.class,
                new SqlParameterValue(Types.INTEGER, id));
    }

    @Override
    public void updateQueryResultSBS(int queryId, SbsResult sbsResult) throws Exception {
        queryForObjectExternal("select * from external.upd_query_result_sbs_b(?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, queryId),
                new SqlParameterValue(Types.VARCHAR, sbsResult.getProducto()),
                new SqlParameterValue(Types.VARCHAR, sbsResult.getEntidad()),
                new SqlParameterValue(Types.NUMERIC, sbsResult.getTasa()));
    }

    @Override
    public void updateQueryResultSBSrate(double sbsTopRate) throws Exception {
        queryForObjectExternal("select * from external.upd_sbs_max_rate(?)", String.class,
                new SqlParameterValue(Types.INTEGER, sbsTopRate));
    }

    @Override
    public void registerQueryResultRedam(int id, int docType, String docNumber) throws Exception {
        queryForObjectExternal("select * from external.ins_query_result_redam(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, id),
                new SqlParameterValue(Types.INTEGER, docType),
                new SqlParameterValue(Types.VARCHAR, docNumber));
    }

    @Override
    public void updateQueryResultRedam(int queryId, RedamResult redamResult) throws Exception {
        queryForObjectExternal("select * from external.upd_query_result_redam(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, queryId),
                new SqlParameterValue(Types.VARCHAR, redamResult.getDocumentNumber()),
                new SqlParameterValue(Types.VARCHAR, redamResult.getJudicialDistrict()),
                new SqlParameterValue(Types.VARCHAR, redamResult.getCourt()),
                new SqlParameterValue(Types.VARCHAR, redamResult.getSecretary()),
                new SqlParameterValue(Types.VARCHAR, redamResult.getRegistry()),
                new SqlParameterValue(Types.NUMERIC, redamResult.getMonthlyPension()),
                new SqlParameterValue(Types.INTEGER, redamResult.getInstallments()),
                new SqlParameterValue(Types.NUMERIC, redamResult.getAmountDue()),
                new SqlParameterValue(Types.NUMERIC, redamResult.getInterest()));
    }

    @Override
    public void registerQueryResultPhoneContracts(int id, int docType, String docNumber, String operator) throws Exception {
        queryForObjectExternal("select * from external.ins_query_result_phones(?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, id),
                new SqlParameterValue(Types.INTEGER, docType),
                new SqlParameterValue(Types.VARCHAR, docNumber),
                new SqlParameterValue(Types.VARCHAR, operator));
    }

    @Override
    public void updateQueryResultPhoneContracts(int queryId, LineaResult lineaResult) throws Exception {
        queryForObjectTrx("select * from external.upd_query_result_phones(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, queryId),
                new SqlParameterValue(Types.INTEGER, lineaResult.getCantidad()),
                new SqlParameterValue(Types.VARCHAR, lineaResult.getLineas()));
    }

    @Override
    public void registerQueryResultSat(int id, int docType, String docNumber) throws Exception {
        queryForObjectExternal("select * from external.ins_query_result_sat(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, id),
                new SqlParameterValue(Types.INTEGER, docType),
                new SqlParameterValue(Types.VARCHAR, docNumber));
    }

    @Override
    public void updateQueryResultSat(int queryId, SatResult satResult) throws Exception {
        queryForObjectExternal("select * from external.upd_query_result_sat(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, queryId),
//                new SqlParameterValue(Types.OTHER, satResult.getSatIdReport() != null && !satResult.getSatIdReport().isEmpty() ? new Gson().toJson(satResult.getSatIdReport()) : "[]")
                new SqlParameterValue(Types.OTHER, satResult.getSatIdReportJSONArray() != null ? satResult.getSatIdReportJSONArray().toString() : "[]")
        );
    }

    @Override
    public void registerQueryResultSis(int id, int docType, String docNumber) throws Exception {
        queryForObjectExternal("select * from external.ins_query_result_sis(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, id),
                new SqlParameterValue(Types.INTEGER, docType),
                new SqlParameterValue(Types.VARCHAR, docNumber));
    }

    @Override
    public void updateQueryResultSis(int queryId, SisResult sisResult) throws Exception {
        queryForObjectExternal("select * from external.upd_query_result_sis(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, queryId),
                new SqlParameterValue(Types.VARCHAR, sisResult.getFullName()),
                new SqlParameterValue(Types.VARCHAR, sisResult.getDocumentNumber()),
                new SqlParameterValue(Types.VARCHAR, sisResult.getAffiliationNumber()),
                new SqlParameterValue(Types.VARCHAR, sisResult.getInsuranceType()),
                new SqlParameterValue(Types.VARCHAR, sisResult.getInsuredType()),
                new SqlParameterValue(Types.VARCHAR, sisResult.getFormatType()),
                new SqlParameterValue(Types.DATE, sisResult.getEnrollmentDate()),
                new SqlParameterValue(Types.VARCHAR, sisResult.getBenefitPlan()),
                new SqlParameterValue(Types.VARCHAR, sisResult.getHealthCenter()),
                new SqlParameterValue(Types.VARCHAR, sisResult.getHealthCenterAddress()),
                new SqlParameterValue(Types.VARCHAR, sisResult.getStatus()),
                new SqlParameterValue(Types.DATE, sisResult.getValidUntil())
        );
    }

    @Override
    public void registerQueryResultMigrations(int id, String docNumber, Date birthday) {
        queryForObjectTrx("select * from external.ins_query_result_migrations(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, id),
                new SqlParameterValue(Types.VARCHAR, docNumber),
                new SqlParameterValue(Types.DATE, birthday));
    }

    @Override
    public void updateQueryResultMigrations(int queryId, MigracionesResult migracionesResult) throws Exception {
        queryForObjectTrx("select * from external.upd_query_result_migrations(?, ?, ?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, queryId),
                new SqlParameterValue(Types.VARCHAR, migracionesResult.getFullName()),
                new SqlParameterValue(Types.VARCHAR, migracionesResult.getNationality()),
                new SqlParameterValue(Types.VARCHAR, migracionesResult.getResidence()),
                new SqlParameterValue(Types.VARCHAR, migracionesResult.getTae()),
                new SqlParameterValue(Types.DATE, migracionesResult.getDocumentExpeditionDate()),
                new SqlParameterValue(Types.DATE, migracionesResult.getDocumentDueDate())
        );
    }

    @Override
    public List<BufferTransaction> runBufferTransactions() throws Exception {
        JSONArray dbArray = queryForObjectTrx("select * from security.run_buffer_transactions()", JSONArray.class);
        if (dbArray == null)
            return null;

        List<BufferTransaction> precessedBuffers = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            BufferTransaction buffer = new BufferTransaction();
            buffer.fillFromDb(dbArray.getJSONObject(i), catalogService);
            precessedBuffers.add(buffer);
        }
        return precessedBuffers;
    }

    @Override
    public void proccessManagementScheduleAll() throws Exception {
        queryForObjectTrx("select * from credit.proccess_management_schedule_all()", String.class);
    }

    @Override
    public List<PersonInteraction> sendDailyInteractions() throws Exception {
        System.out.println("sendDailyInteractions");

        List<PersonInteraction> personInteractions = new ArrayList<>();

        JSONArray dbJsonCollection = queryForObject("select * from interaction.send_collection_interactions_daily()", JSONArray.class, new JSONArray(), INTERACTION_DB);

        for (int i = 0; i < dbJsonCollection.length(); i++) {
            PersonInteraction personInteraction = new PersonInteraction();
            personInteraction.fillFromDb(dbJsonCollection.getJSONObject(i), catalogService, null);
            personInteractions.add(personInteraction);
        }

        JSONArray dbJsonCommercial = queryForObjectTrx("select * from interaction.send_commercial_interactions_daily()", JSONArray.class);
        for (int i = 0; i < dbJsonCommercial.length(); i++) {
            PersonInteraction personInteraction = new PersonInteraction();
            personInteraction.fillFromDb(dbJsonCommercial.getJSONObject(i), catalogService, null);
            personInteractions.add(personInteraction);
        }
        //returns a list of commercial and collection interactions
        return personInteractions;
    }

    @Override
    public QueryBot getQueryBot(int queryId) {
        JSONObject dbJson = queryForObjectExternal("select * from external.get_query_bot_result(?)",
                JSONObject.class, new SqlParameterValue(Types.INTEGER, queryId));
        if (dbJson == null) {
            return null;
        }
        QueryBot querybot = new QueryBot();
        querybot.fillFromDb(dbJson);
        return querybot;
    }

    @Override
    public boolean botsRunning(int userId) throws Exception {

        Boolean dbBoolean = queryForObjectExternal("select * from external.bots_running(?)", Boolean.class, new SqlParameterValue(Types.INTEGER, userId));

        return dbBoolean;
    }

    @Override
    public void dailyProccess() throws Exception {
        queryForObjectTrx("select * from credit.run_daily_process(?)", String.class, new SqlParameterValue(Types.INTEGER, Configuration.MAX_INACTIVE_DAYS));
    }

    @Override
    public Object getQueryResult(int queryId, int botId) {
        JSONObject dbJson = queryForObjectExternal("select * from external.get_query_result(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, queryId));
        if (dbJson == null) {
            return null;
        }

        switch (botId) {
            case Bot.SUNAT_BOT:
                SunatResult sunatResult = new SunatResult();
                sunatResult.fillFromDb(dbJson);
                return sunatResult;
//            case Bot.RENIEC_BOT:
//                ReniecResult reniecResult = new ReniecResult();
//                reniecResult.fillFromDb(dbJson);
//                return reniecResult;
//            case Bot.ESSALUD_BOT:
//                EssaludResult essaludResult = new EssaludResult();
//                essaludResult.fillFromDb(dbJson);
//                return essaludResult;
//            case Bot.REDAM_BOT:
//                RedamResult redamResult = new RedamResult();
//                redamResult.fillFromDb(dbJson);
//                return redamResult;
            case Bot.CLARO:
            case Bot.MOVISTAR:
            case Bot.BITEL:
            case Bot.ENTEL:
                LineaResult lineaResult = new LineaResult();
                lineaResult.fillFromDb(dbJson);
                return lineaResult;
//            case Bot.SIS:
//                SisResult sisResult = new SisResult();
//                sisResult.fillFromDb(dbJson);
//                return sisResult;
//            case Bot.SAT:
//                SatResult satResult = new SatResult();
//                satResult.fillFromDb(dbJson);
//                return satResult;
            case Bot.SAT_PLATE:
                SatPlateResult satPlateResult = new SatPlateResult();
                satPlateResult.fillFromDb(dbJson);
                return satPlateResult;
            case Bot.MIGRACIONES:
                MigracionesResult migracionesResult = new MigracionesResult();
                migracionesResult.fillFromDb(dbJson);
                return migracionesResult;
            case Bot.AFIP:
                AfipResult afipResult = new AfipResult();
                afipResult.fillFromDb(dbJson);
                return afipResult;
            case Bot.ANSES:
                AnsesResult ansesResult = new AnsesResult();
                ansesResult.fillFromDb(dbJson);
                return ansesResult;
            case Bot.BCRA:
                BcraResult bcraResult = new BcraResult();
                bcraResult.fillFromDb(dbJson);
                return bcraResult;
            case Bot.PADRON:
                PadronResult padronResult = new PadronResult();
                padronResult.fillFromDb(dbJson);
                return padronResult;
            case Bot.ONPE:
                OnpeResult onpeResult = new OnpeResult();
                onpeResult.fillFromDb(dbJson);
                return onpeResult;
            case Bot.SOAT_RECORDS:
                SoatRecordsResult soatResult = new SoatRecordsResult();
                soatResult.fillFromDb(dbJson);
                return soatResult;
            case Bot.UNIVERSIDAD_PERU:
                UniversidadPeruResult universidadPeruResult = new UniversidadPeruResult();
                universidadPeruResult.fillFromDb(dbJson);
                return universidadPeruResult;
            default:
                System.out.println("NOT IN BOTDAOIMPL - getQueryResult");
        }
        return null;
    }

    @Override
    public void registerACSession(Integer entityId, String sessionId) throws Exception {
        queryForObjectTrx("select * from security.register_entity_session(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.VARCHAR, sessionId));
    }

    @Override
    public String getACSession(Integer entityId) throws Exception {
        return queryForObjectTrx("select * from security.get_last_entity_session(?)", String.class,
                new SqlParameterValue(Types.INTEGER, entityId));
    }

    @Override
    public void registerQueryResultAFIP(int id, int documentType, String documentNumber) throws Exception {
        queryForObjectExternal("select * from external.ins_query_result_afip(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, id),
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber));
    }

    @Override
    public void updateQueryResultAFIP(int queryId, AfipResult afipResult) throws Exception {
        queryForObjectTrx("select * from external.upd_query_result_afip(?, ?, ?::JSON, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, queryId),
                new SqlParameterValue(Types.VARCHAR, afipResult.getFullName()),
                new SqlParameterValue(Types.OTHER, afipResult.getIrgJSONArray() != null ? afipResult.getIrgJSONArray().toString() : "[]"),
                new SqlParameterValue(Types.VARCHAR, afipResult.getDf1()),
                new SqlParameterValue(Types.VARCHAR, afipResult.getDf2()),
                new SqlParameterValue(Types.VARCHAR, afipResult.getDi1()),
                new SqlParameterValue(Types.VARCHAR, afipResult.getDi2()),
                new SqlParameterValue(Types.VARCHAR, afipResult.getDi3()),
                new SqlParameterValue(Types.VARCHAR, afipResult.getTipo()),
                new SqlParameterValue(Types.VARCHAR, afipResult.getDf3()),
                new SqlParameterValue(Types.VARCHAR, afipResult.getControl1()),
                new SqlParameterValue(Types.VARCHAR, afipResult.getControl2()),
                new SqlParameterValue(Types.VARCHAR, afipResult.getControl3()),
                new SqlParameterValue(Types.VARCHAR, afipResult.getControl4()),
                new SqlParameterValue(Types.VARCHAR, afipResult.getControlCategoria()),
                new SqlParameterValue(Types.VARCHAR, afipResult.getControlActividad()));
    }

    @Override
    public void registerQueryResultANSES(int id, int documentType, String documentNumber) throws Exception {
        queryForObjectExternal("select * from external.ins_query_result_anses(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, id),
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber));
    }

    @Override
    public void updateQueryResultANSES(int queryId, AnsesResult ansesResult) throws Exception {
        queryForObjectExternal("select * from external.upd_query_result_anses(?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, queryId),
                new SqlParameterValue(Types.VARCHAR, ansesResult.getFullName()),
                new SqlParameterValue(Types.VARCHAR, ansesResult.getCuilCuit()),
                new SqlParameterValue(Types.VARCHAR, ansesResult.getCodigoTransaccion()),
                new SqlParameterValue(Types.OTHER, ansesResult.getDetails() != null && !ansesResult.getDetails().isEmpty() ? new Gson().toJson(ansesResult.getDetails()) : null));
    }

    @Override
    public void registerQueryResultBCRA(int id, int documentType, String documentNumber) throws Exception {
        queryForObjectExternal("select * from external.ins_query_result_bcra(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, id),
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber));
    }

    @Override
    public void updateQueryResultBCRA(int queryId, BcraResult bcraResult) throws Exception {
        queryForObjectExternal("select * from external.upd_query_result_bcra(?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, queryId),
                new SqlParameterValue(Types.OTHER, bcraResult.getDeudores() != null ? new Gson().toJson(bcraResult.getDeudores()) : null),
                new SqlParameterValue(Types.OTHER, bcraResult.getCheques() != null ? new Gson().toJson(bcraResult.getCheques()) : null),
                new SqlParameterValue(Types.VARCHAR, bcraResult.getOriginDate()),
                new SqlParameterValue(Types.OTHER, bcraResult.getHistorialDeudas() != null ? new Gson().toJson(bcraResult.getHistorialDeudas()) : null));
    }

    @Override
    public void updateQueryResultPadron(int queryId, PadronResult padronResult) throws Exception {
        queryForObjectExternal("select * from external.upd_query_result_padron(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, queryId),
                new SqlParameterValue(Types.VARCHAR, padronResult.getFullName()),
                new SqlParameterValue(Types.VARCHAR, padronResult.getMatricula()),
                new SqlParameterValue(Types.VARCHAR, padronResult.getDistritoName()),
                new SqlParameterValue(Types.VARCHAR, padronResult.getCircuito()),
                new SqlParameterValue(Types.VARCHAR, padronResult.getSeccion()),
                new SqlParameterValue(Types.VARCHAR, padronResult.getEstablecimiento()),
                new SqlParameterValue(Types.VARCHAR, padronResult.getDireccion()),
                new SqlParameterValue(Types.VARCHAR, padronResult.getMesa()),
                new SqlParameterValue(Types.VARCHAR, padronResult.getOrden()));
    }

    @Override
    public void registerACVehicles(String vehiclesJson) throws Exception {
        queryForObjectTrx("select * from vehicle.register_acceso_vehicles(?)", String.class,
                new SqlParameterValue(Types.OTHER, vehiclesJson));
    }

    @Override
    public void registerProxyRequestLog(int proxyId, int queryBotId, boolean success) throws Exception {
        queryForObjectExternal("select * FROM external.register_proxy_request_log(?, ?, ?);", String.class,
                new SqlParameterValue(Types.INTEGER, proxyId),
                new SqlParameterValue(Types.INTEGER, queryBotId),
                new SqlParameterValue(Types.BOOLEAN, success));
    }

    @Override
    public List<QueryBot> getQueryBotQueue() {
        JSONArray jsonArray = queryForObjectExternal("select * from external.get_query_bot_queue();", JSONArray.class);

        List<QueryBot> queryBots = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); ++i) {
            QueryBot queryBot = new QueryBot();
            queryBot.fillFromDb(jsonArray.getJSONObject(i));
            queryBots.add(queryBot);
        }

        return queryBots;
    }

    @Override
    public void registerHolidays(HolidaysResult holidaysResult) throws Exception {
        queryForObjectTrx("select * from support.register_holidays(?)", String.class,
                new SqlParameterValue(Types.OTHER, new Gson().toJson(holidaysResult.getHolidays())));
    }

    @Override
    public void registerHolidays(HolidaysResult holidaysResult, Integer countryId) throws Exception {
        queryForObjectTrx("select * from support.register_holidays(?,?)", String.class,
                new SqlParameterValue(Types.OTHER, new Gson().toJson(holidaysResult.getHolidays())),
                new SqlParameterValue(Types.INTEGER, countryId));
    }

    @Override
    public List<QueryBot> getLoanApplicationEvaluationQueryBot(int loanApplicationId, int status) {
        JSONArray jsonArray = queryForObjectExternal("select * from external.get_application_evaluation_queue(?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, status));
        if (jsonArray == null)
            return null;

        List<QueryBot> queryBots = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            QueryBot queryBot = new QueryBot();
            queryBot.fillFromDb(jsonArray.getJSONObject(i));
            queryBots.add(queryBot);
        }

        return queryBots;
    }

    @Override
    public List<QueryBot> getScheduledQueryBots() {
        JSONArray jsonArray = queryForObjectExternal("select * from external.get_pending_lg_query()", JSONArray.class);
        if (jsonArray == null)
            return null;

        List<QueryBot> queryBots = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            QueryBot queryBot = new QueryBot();
            queryBot.fillFromDb(jsonArray.getJSONObject(i));
            queryBots.add(queryBot);
        }

        return queryBots;
    }

    @Override
    public List<QueryBot> getQueryBotsByBotId(int botId, int limit, int offset) {
        JSONArray jsonArray = queryForObjectExternal("select * from external.get_lg_query(?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, botId),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.INTEGER, offset));
        if (jsonArray == null)
            return new ArrayList<>();

        List<QueryBot> queryBots = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            QueryBot queryBot = new QueryBot();
            queryBot.fillFromDb(jsonArray.getJSONObject(i));
            queryBots.add(queryBot);
        }

        return queryBots;
    }

    @Override
    public void registerQueryResultONPE(int id, String documentNumber) throws Exception {
        queryForObjectExternal("select * from external.ins_query_result_onpe(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, id),
                new SqlParameterValue(Types.VARCHAR, documentNumber));
    }

    @Override
    public void updateQueryResultONPE(int queryId, OnpeResult onpeResult) throws Exception {
        queryForObjectExternal("select * from external.upd_query_result_onpe(?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, queryId),
                new SqlParameterValue(Types.VARCHAR, onpeResult.getFullName()),
                new SqlParameterValue(Types.OTHER, onpeResult.getDetails() != null && !onpeResult.getDetails().isEmpty() ? new Gson().toJson(onpeResult.getDetails()) : null));
    }

    @Override
    public List<PersonInteraction> getAutomaticInteractionsToSend() throws Exception {
        List<PersonInteraction> personInteractions = new ArrayList<>();
        JSONArray dbJsonCollection = queryForObject("select * from interaction.send_interactions_minutely()", JSONArray.class, new JSONArray(), INTERACTION_DB);
        for (int i = 0; i < dbJsonCollection.length(); i++) {
            PersonInteraction personInteraction = new PersonInteraction();
            personInteraction.fillFromDb(dbJsonCollection.getJSONObject(i), catalogService, null);
            personInteractions.add(personInteraction);
        }
        return personInteractions;
    }

    @Override
    public List<PersonInteraction> getApprovedDataMailsToSend(Integer interactionContentId) throws Exception {
        List<PersonInteraction> personInteractions = new ArrayList<>();
        JSONArray dbJsonCollection = queryForObjectInteraction("select * from interaction.send_approved_data_mailing(?)", JSONArray.class, new SqlParameterValue(Types.INTEGER, interactionContentId));
        if (dbJsonCollection != null) {
            for (int i = 0; i < dbJsonCollection.length(); i++) {
                PersonInteraction personInteraction = new PersonInteraction();
                personInteraction.fillFromDb(dbJsonCollection.getJSONObject(i), catalogService, null);
                personInteractions.add(personInteraction);
            }
        }
        return personInteractions;
    }

    @Override
    public void registerQueryResultSATPlate(int id, String plate) throws Exception {
        queryForObjectExternal("select * from external.ins_query_result_sat_plate( ? ,?)", String.class,
                new SqlParameterValue(Types.INTEGER, id),
                new SqlParameterValue(Types.VARCHAR, plate));
    }

    @Override
    public void updateQueryResultSATPlate(int queryId, SatPlateResult satPlateResult) throws Exception {
        queryForObjectExternal("select * from external.upd_query_result_sat_plate(?,?,?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, queryId),
                new SqlParameterValue(Types.NUMERIC, satPlateResult.getTotal()),
                new SqlParameterValue(Types.VARCHAR, satPlateResult.getMessage()),
                new SqlParameterValue(Types.OTHER, satPlateResult.getReports() != null ? new Gson().toJson(satPlateResult.getReports()) : null));
    }

    @Override
    public void registerQueryResultSoatRecords(int queryId, String plate) throws Exception {
        queryForObjectExternal("select * from external.ins_query_result_soat(?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, queryId),
                new SqlParameterValue(Types.VARCHAR, plate));
    }

    @Override
    public void updateQueryResultSoatRecords(int queryId, SoatRecordsResult soatRecordsResult) throws Exception {
        queryForObjectExternal("select * from external.upd_query_result_soat(?,?,?,?,?,?,?,?,?,?,?,?)", String.class,
                new SqlParameterValue(Types.INTEGER, queryId),
                new SqlParameterValue(Types.VARCHAR, soatRecordsResult.getCompany()),
                new SqlParameterValue(Types.DATE, soatRecordsResult.getStartDate()),
                new SqlParameterValue(Types.DATE, soatRecordsResult.getEndingDate()),
                new SqlParameterValue(Types.VARCHAR, soatRecordsResult.getRegPlate()),
                new SqlParameterValue(Types.VARCHAR, soatRecordsResult.getCertificate()),
                new SqlParameterValue(Types.VARCHAR, soatRecordsResult.getUsage()),
                new SqlParameterValue(Types.VARCHAR, soatRecordsResult.getVehicleCategory()),
                new SqlParameterValue(Types.VARCHAR, soatRecordsResult.getState()),
                new SqlParameterValue(Types.VARCHAR, soatRecordsResult.getTypeOfDocument()),
                new SqlParameterValue(Types.TIMESTAMP, soatRecordsResult.getCreationDate()),
                new SqlParameterValue(Types.OTHER, soatRecordsResult.getReports() != null ? new Gson().toJson(soatRecordsResult.getReports()) : null));
    }

    @Override
    public List<PersonInteraction> getHourlyInteractions(int hour) throws Exception {
        List<PersonInteraction> personInteractions = new ArrayList<>();

        JSONArray dbJsonCollection = queryForObject("select * from interaction.send_interactions_hourly(?)", JSONArray.class, new JSONArray(), INTERACTION_DB,
                new SqlParameterValue(Types.INTEGER, hour));

        for (int i = 0; i < dbJsonCollection.length(); i++) {
            PersonInteraction personInteraction = new PersonInteraction();
            personInteraction.fillFromDb(dbJsonCollection.getJSONObject(i), catalogService, null);
            personInteractions.add(personInteraction);
        }

        return personInteractions;
    }

    @Override
    public Boolean isBotAbleToRun(Integer botId) throws Exception {
        return queryForObjectExternal("select * from external.bot_is_runnable_on_demand(?)", Boolean.class, new SqlParameterValue(Types.INTEGER, botId));
    }


    @Override
    public List<QueryBot> getScheduledQueryBots(Integer botId, String yearmonth) {
        JSONArray jsonArray = queryForObjectExternal("select * from external.get_scheduled_bots(?,?);", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, botId),
                new SqlParameterValue(Types.VARCHAR, yearmonth));
        if (jsonArray == null)
            return new ArrayList<>();

        List<QueryBot> queryBots = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); ++i) {
            QueryBot queryBot = new QueryBot();
            queryBot.fillFromDb(jsonArray.getJSONObject(i));
            queryBots.add(queryBot);
        }
        return queryBots;
    }

    @Override
    public void updateParameters(int queryId, JSONObject params) {
        update("UPDATE external.lg_query set parameters = ? where query_id = ?",
                EXTERNAL_DB,
                new SqlParameterValue(Types.OTHER, params),
                new SqlParameterValue(Types.INTEGER, queryId));
    }

    @Override
    public void registerQueryResultUniversidadPeru(int id, String ruc) {
        queryForObjectExternal("select * from external.ins_query_result_universidadperu(?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, id),
                new SqlParameterValue(Types.VARCHAR, ruc));
    }

    @Override
    public void updateQueryResultUniversidadPeru(int id, String phoneNumber, String phoneNumber2, String phoneNumber3, String phoneNumber4, String address, String district, String department) {
        queryForObjectExternal("select * from external.upd_query_result_universidadperu(?, ?, ?, ?, ?, ?, ?, ?)", String.class,
                new SqlParameterValue(Types.INTEGER, id),
                new SqlParameterValue(Types.VARCHAR, phoneNumber),
                new SqlParameterValue(Types.VARCHAR, phoneNumber2),
                new SqlParameterValue(Types.VARCHAR, phoneNumber3),
                new SqlParameterValue(Types.VARCHAR, phoneNumber4),
                new SqlParameterValue(Types.VARCHAR, address),
                new SqlParameterValue(Types.VARCHAR, district),
                new SqlParameterValue(Types.VARCHAR, department));
    }
}