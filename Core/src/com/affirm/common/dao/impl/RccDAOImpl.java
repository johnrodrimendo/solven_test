package com.affirm.common.dao.impl;

import com.affirm.bancodelsol.model.CampaniaBds;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.RccDAO;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.system.configuration.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RccDAOImpl extends JsonResolverDAO implements RccDAO {

    @Autowired
    private CatalogService catalogService;

    @Override
    public List<RccSynthesized> getRccSynthesized(String documentNumber) throws Exception {
        JSONArray dbJson = queryForObject("select * from sysrcc.get_rcc_synthesized(?)", JSONArray.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentNumber));
        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<RccSynthesized> results = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            RccSynthesized result = new RccSynthesized();
            result.fillFromDb(dbJson.getJSONObject(i));
            results.add(result);
        }
        return results;
    }

    @Override
    public List<RccDate> getRccDates() throws Exception {
        JSONArray dbJson = queryForObject("select * from sysrcc.get_rcc_dates()", JSONArray.class, REPOSITORY_DB);
        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<RccDate> results = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            RccDate result = new RccDate();
            result.fillFromDb(dbJson.getJSONObject(i));
            results.add(result);
        }
        return results;
    }

    @Override
    public RccScore getRccScore(String documentNumber) throws Exception {
        JSONObject dbJson = queryForObject("select * from sysrcc.get_rcc_score(?)", JSONObject.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentNumber));
        if (dbJson == null) {
            return null;
        }

        RccScore result = new RccScore();
        result.fillFromDb(dbJson);
        return result;
    }

    @Override
    public List<RccIdeGrouped> getRccIdeGrouped(String documentNumber) throws Exception {
        JSONArray dbJson = queryForObject("select * from sbsrcc.get_rcc_ide(?)", JSONArray.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentNumber));
        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<RccIdeGrouped> results = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            RccIdeGrouped result = new RccIdeGrouped();
            result.fillFromDb(dbJson.getJSONObject(i),catalogService);
            results.add(result);
        }
        // Filter to only responds the records from the person 1
        return results.stream().filter(r -> r.getRccIde().getTipPer() != null && r.getRccIde().getTipPer().equalsIgnoreCase("1")).collect(Collectors.toList());
    }

    @Override
    public List<CendeuDate> getCendeuDates() throws Exception {
        JSONArray dbJson = queryForObject("select * from cendeu.get_cendeu_dates()", JSONArray.class, REPOSITORY_DB);
        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<CendeuDate> results = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            CendeuDate result = new CendeuDate();
            result.fillFromDb(dbJson.getJSONObject(i));
            results.add(result);
        }
        return results;
    }

    @Override
    public Boolean getCendeuRejectedChecks(String documentNumber) throws Exception {
        return queryForObject("select * from cendeu.rejected_checks(?)", Boolean.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentNumber));
    }

    @Override
    public List<BDSBase> getBancoDelSolBase(String documentNumber) throws Exception {
        JSONArray dbArray = queryForObject("select * from bases.get_bds(?)", JSONArray.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentNumber));
        if (dbArray == null)
            return new ArrayList<>();

        List<BDSBase> results = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            BDSBase result = new BDSBase();
            result.fillFromDb(dbArray.getJSONObject(i));
            results.add(result);
        }
        return results;
    }

    @Override
    public Integer getMaxScoreMonthly(String ruc, Integer start, Integer end) {
        return queryForObject("select * from sysrcc.get_max_score_monthly(?, ?, ?)", Integer.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, ruc),
                new SqlParameterValue(Types.INTEGER, start),
                new SqlParameterValue(Types.INTEGER, end));
    }

    @Override
    public List<RccSynthesizedExtraFields> getRccSynthesizedExtraFields(String documentNumber) throws Exception {
        JSONArray dbJson = queryForObject("select * from sysrcc.get_rcc_synthesized(?)", JSONArray.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentNumber));
        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<RccSynthesizedExtraFields> results = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            RccSynthesizedExtraFields result = new RccSynthesizedExtraFields();
            result.fillFromDb(dbJson.getJSONObject(i));
            results.add(result);
        }
        return results;
    }

    @Override
    public BDSBaseProcess getCurrentBancoDelSolBaseProcess() {
        JSONObject dbJson = queryForObject("select row_to_json(tx) from (select * from bases.tb_bds_base_process where type = 'base' order by process_date desc limit 1) as tx",
                JSONObject.class, REPOSITORY_DB);
        if (dbJson == null)
            return null;

        BDSBaseProcess process = new BDSBaseProcess();
        process.fillFromDb(dbJson);
        return process;
    }

    @Override
    public Boolean existsInFinansolPreApprovedBase(String documentNumber) throws Exception {
        return queryForObject("select * from bases.exists_in_finansol_pre_approved(?);", Boolean.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentNumber));
    }

    @Override
    public FinansolPreApprovedBase getFinansolPreApprovedBase(String documentType, String documentNumber) {
        JSONObject result = queryForObject("select * from bases.get_finansol_pre_approved(?, ?)", JSONObject.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber));
        if(result == null)
            return null;
        FinansolPreApprovedBase base = new FinansolPreApprovedBase();
        base.fillFromDb(result);
        return base;
    }

    @Override
    public CampaniaBds getLastCampaniaBds(String documentNumber) {
        String query = null;
        if(Configuration.hostEnvIsProduction()){
            query = "select * from bases.get_bds_campania_by_cuit(?)";
        }else{
            query = "select * from bases.get_bds_campania_by_cuit_temp(?)";
        }
        JSONObject result = queryForObject(query, JSONObject.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentNumber));
        if(result == null)
            return null;
        CampaniaBds base = new CampaniaBds();
        base.fillFromDb(result);
        return base;
    }

    @Override
    public long copyCSVToPrismaTable(File file, String table) throws IOException, SQLException {
        return copyFromCommandToTempTable(file, REPOSITORY_DB, table, true);
    }

    @Override
    public long copyCSVToTable(File file, String table, Boolean truncate, String additionalCommand) throws IOException, SQLException {
        if(truncate == null) truncate = false;
        return copyFromCommandToTempTable(file, REPOSITORY_DB, table, truncate,additionalCommand);
    }

    @Override
    public long copyCSVToTable(File file, String table, Boolean truncate) throws IOException, SQLException {
        if(truncate == null) truncate = false;
        return copyFromCommandToTempTable(file, REPOSITORY_DB, table, truncate);
    }

    @Override
    public void copyTableToAnother(String tableFrom, String tableTo){
        update(String.format("INSERT INTO %s (SELECT * FROM %s)", tableTo, tableFrom), REPOSITORY_DB);
    }

    @Override
    public void truncateTable(String table){
        update("TRUNCATE "+table, REPOSITORY_DB);
    }

//    @Override
//    public Boolean existsInBanBifPreApprovedBase(String documentType, String documentNumber) throws Exception {
//        if(Configuration.hostEnvIsProduction()){
//            return queryForObject("select * from bases.exists_in_banbif_pre_approved(?, ?);", Boolean.class, REPOSITORY_DB,
//                    new SqlParameterValue(Types.VARCHAR, documentType),
//                    new SqlParameterValue(Types.VARCHAR, documentNumber));
//        }else{
//            return queryForObject("select * from bases.exists_in_banbif_pre_approved_stg(?, ?);", Boolean.class, REPOSITORY_DB,
//                    new SqlParameterValue(Types.VARCHAR, documentType),
//                    new SqlParameterValue(Types.VARCHAR, documentNumber));
//        }
//    }

    @Override
    public List<BanbifPreApprovedBase> getBanbifPreApprovedBase(String documentType, String documentNumber) {
        JSONArray dbJson = null;
        if(Configuration.hostEnvIsProduction()){
            dbJson = queryForObject("select * from bases.get_banbif_pre_approved(?, ?)", JSONArray.class, REPOSITORY_DB,
                    new SqlParameterValue(Types.VARCHAR, documentType),
                    new SqlParameterValue(Types.VARCHAR, documentNumber));
        }else{
            dbJson = queryForObject("select * from bases.get_banbif_pre_approved_stg(?, ?)", JSONArray.class, REPOSITORY_DB,
                    new SqlParameterValue(Types.VARCHAR, documentType),
                    new SqlParameterValue(Types.VARCHAR, documentNumber));
        }
        if(dbJson == null)
            return new ArrayList<>();

        List<BanbifPreApprovedBase> results = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            BanbifPreApprovedBase result = new BanbifPreApprovedBase();
            result.fillFromDb(dbJson.getJSONObject(i));
            results.add(result);
        }
        return results;
    }

    @Override
    public Boolean existsInPrismaPreApprovedBase(String documentNumber) throws Exception {
        return queryForObject("select * from bases.exists_in_prisma_pre_approved(?);", Boolean.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentNumber));
    }

    @Override
    public Boolean existsInAztecaPreApprovedBase(String documentNumber) throws Exception {
        return queryForObject("select * from bases.exists_in_azteca_pre_approved(?);", Boolean.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentNumber));
    }

    @Override
    public AztecaPreApprovedBase getAztecaPreApprovedBase(String documentNumber) {
        JSONObject result;
        if(Configuration.hostEnvIsProduction()){
            result = queryForObject("select * from bases.get_aztca_pre_approved(?)", JSONObject.class, REPOSITORY_DB,
                    new SqlParameterValue(Types.VARCHAR, documentNumber));
        }else{
            result = queryForObject("select * from bases.get_aztca_pre_approved_temp(?)", JSONObject.class, REPOSITORY_DB,
                    new SqlParameterValue(Types.VARCHAR, documentNumber));
        }
        if(result == null)
            return null;
        AztecaPreApprovedBase base = new AztecaPreApprovedBase();
        base.fillFromDb(result);
        return base;
    }

    @Override
    public CampaniaBds getCampaniaBdsById(Integer id) {
        String query = null;
        if(Configuration.hostEnvIsProduction()){
            query = "select * from bases.get_bds_campania_by_id(?)";
        }else{
            query = "select * from bases.get_bds_campania_by_id_temp(?)";
        }
        JSONObject result = queryForObject(query, JSONObject.class, REPOSITORY_DB,
                new SqlParameterValue(Types.INTEGER, id));
        if(result == null)
            return null;
        CampaniaBds base = new CampaniaBds();
        base.fillFromDb(result);
        return base;
    }

    @Override
    public PrismaPreApprovedBase getPrismaPreApprovedBase(String documentType, String documentNumber) {
        JSONObject result = queryForObject("select * from bases.get_prisma_pre_approved(?, ?)", JSONObject.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber));
        if(result == null)
            return null;
        PrismaPreApprovedBase base = new PrismaPreApprovedBase();
        base.fillFromDb(result);
        return base;
    }

    @Override
    public Boolean existsInBDSCNEBase(String documentNumber) throws Exception {
        return queryForObject("select * from bases.exists_in_bds_cne_base(?);", Boolean.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentNumber));
    }

    @Override
    public List<CendeuRejectedCheck> getCendeuRejectedChecksByCuit(String documentNumber) throws Exception {
        JSONArray dbJson = queryForObject("select * from cendeu.get_rejected_checks_by_cuit(?)", JSONArray.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentNumber));
        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<CendeuRejectedCheck> results = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            CendeuRejectedCheck result = new CendeuRejectedCheck();
            result.fillFromDb(dbJson.getJSONObject(i));
            results.add(result);
        }
        return results;
    }

    @Override
    public AztecaGetawayBase getAztecaCobranzaBase(String documentType, String documentNumber) {
        JSONObject result;
        if(Configuration.hostEnvIsProduction()){
            result = queryForObject("select * from bases.get_azteca_cobranza(?, ?)", JSONObject.class, REPOSITORY_DB,
                    new SqlParameterValue(Types.VARCHAR, documentType),
                    new SqlParameterValue(Types.VARCHAR, documentNumber));
        }else{
            result = queryForObject("select * from bases.get_azteca_cobranza_temp(?, ?)", JSONObject.class, REPOSITORY_DB,
                    new SqlParameterValue(Types.VARCHAR, documentType),
                    new SqlParameterValue(Types.VARCHAR, documentNumber));
        }
        if(result == null)
            return null;
        AztecaGetawayBase base = new AztecaGetawayBase();
        base.fillFromDb(result);
        return base;
    }

    @Override
    public List<AztecaGetawayBase> getMaretingCampaignDestinations(Integer entityId, Character filterType) {

        String query = "";

        if(Configuration.hostEnvIsProduction()) query = "select support.get_cobranza_base_destinations(?, ?)";
        else query = "select support.get_cobranza_base_destinations_temp(?, ?)";

        JSONArray dbArray = queryForObjectTrx(query, JSONArray.class,
                    new SqlParameterValue(Types.INTEGER, entityId),
                    new SqlParameterValue(Types.CHAR, filterType));
        if(dbArray == null)
            return new ArrayList<>();

        List<AztecaGetawayBase> results = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            AztecaGetawayBase result = new AztecaGetawayBase();
            result.fillFromDb(dbArray.getJSONObject(i));
            results.add(result);
        }
        return results;
    }


    @Override
    public List<AztecaGetawayBase> getAztecaCobranzaBases(String documentType, String documentNumber) {
        JSONArray result;

        String query = "";

        if(Configuration.hostEnvIsProduction()) query = "select * from bases.get_azteca_cobranza_bases(?, ?)";
        else query = "select * from bases.get_azteca_cobranza_bases_temp(?, ?)";

        result = queryForObject(query, JSONArray.class, REPOSITORY_DB,
                new SqlParameterValue(Types.VARCHAR, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber));

        if(result == null)
            return new ArrayList<>();

        List<AztecaGetawayBase> results = new ArrayList<>();
        for (int i = 0; i < result.length(); i++) {
            AztecaGetawayBase base = new AztecaGetawayBase();
            base.fillFromDb(result.getJSONObject(i));
            results.add(base);
        }

        return results;
    }

    @Override
    public Date getBanbifBaseValidUntil() {
        String query = "";
        if(Configuration.hostEnvIsProduction()) query = "select valid_until from bases.tb_banbif_pre_approved where valid_until is not null limit 1";
        else query = "select valid_until from bases.tb_banbif_pre_approved_stg where valid_until is not null limit 1";

        return queryForObject(query, Date.class, REPOSITORY_DB);
    }
}
