package com.affirm.security.dao.impl;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.model.RekognitionProData;
import com.affirm.common.model.RekognitionReniecData;
import com.affirm.common.model.catalog.ExtranetMenu;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.heroku.model.ServerStatus;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.security.model.EntityWsResult;
import com.affirm.security.model.SysUser;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
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
 * Created by jrodriguez on 19/09/16.
 */
@Repository("securityDao")
public class SecurityDAOImpl extends JsonResolverDAO implements SecurityDAO {


    @Autowired
    private CatalogService catalogService;

    @Override
    public SysUser getSysUser(String userName) {
        JSONObject dbJson = queryForObjectTrx("select * from security.get_sysuser(?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, userName));
        if (dbJson == null) {
            return null;
        }

        SysUser user = new SysUser();
        user.fillFromDb(catalogService, dbJson);
        return user;
    }

    @Override
    public void registerException(String stackTrace) throws Exception {
        queryForObjectTrx("select * from support.register_exception(?)", String.class, false,
                new SqlParameterValue(Types.VARCHAR, stackTrace));
    }

    @Override
    public void registerExternalAttack(String attackType, String attackerIP, Double ipLatitude, Double ipLongitude, String detail) throws Exception {
        queryForObjectTrx("select * from support.register_external_attack(?,?,?,?,?)", String.class,
                new SqlParameterValue(Types.VARCHAR, attackType),
                new SqlParameterValue(Types.VARCHAR, attackerIP),
                new SqlParameterValue(Types.NUMERIC, ipLatitude),
                new SqlParameterValue(Types.NUMERIC, ipLongitude),
                new SqlParameterValue(Types.VARCHAR, detail));
    }

    @Override
    public EntityWsResult getEntityResultWS(Integer loanApplicationId, Integer entityWS) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from security.get_tb_entity_ws_result(?, ?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityWS));
        if (dbJson == null) {
            return null;
        }

        EntityWsResult wsResult = new EntityWsResult();
        wsResult.fillFromDb(dbJson);
        return wsResult;
    }

    @Override
    public Integer getPermissionId(String permission) {
        if (permission == null)
            return null;

        String[] levels = permission.split(":");
        List<String> wheres = new ArrayList<>();
        if (levels.length > 0) wheres.add("entity = \'" + levels[0] + "\'");
        if (levels.length > 1) wheres.add("field = \'" + levels[1] + "\'");
        if (levels.length > 2) wheres.add("process = \'" + levels[2] + "\'");
        if (levels.length > 3) wheres.add("level4 = \'" + levels[3] + "\'");

        return queryForObjectTrx("select permission_id from security.tb_permission where " + StringUtils.join(wheres.toArray(new String[wheres.size()]), " and "), Integer.class);
    }

    @Override
    public List<ExtranetMenuEntity> getExtranetMenuEntities(int entityId) {
        JSONArray dbArray = queryForObjectTrx("select * from security.get_extranet_menu_entity(?)", JSONArray.class, false,
                new SqlParameterValue(Types.INTEGER, entityId));

        if(dbArray == null) return new ArrayList<>();

        List<ExtranetMenuEntity> extranetMenuEntities = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ExtranetMenuEntity extranetMenuEntity = new ExtranetMenuEntity();
            extranetMenuEntity.fillFromDb(dbArray.getJSONObject(i), catalogService);
            extranetMenuEntities.add(extranetMenuEntity);
        }
        return extranetMenuEntities;
    }

    @Override
    public void registerServerStatus(ServerStatus serverStatus) {
        queryForObjectTrx("select * from security.ins_server_status(?, ?, ?)", String.class,
                new SqlParameterValue(Types.VARCHAR, serverStatus.getApp()),
                new SqlParameterValue(Types.VARCHAR, serverStatus.getState()),
                new SqlParameterValue(Types.TIMESTAMP, serverStatus.getRegisterDate()));
    }

    @Override
    public List<ServerStatus> getServerStatus(Date from) {
        JSONArray dbArray = queryForObjectTrx("select * from security.get_server_status(?)", JSONArray.class, false,
                new SqlParameterValue(Types.TIMESTAMP, from));

        List<ServerStatus> serverStatuses = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ServerStatus serverStatus = new ServerStatus();
            serverStatus.fillFromDb(dbArray.getJSONObject(i));
            serverStatuses.add(serverStatus);
        }
        return serverStatuses;
    }

    @Override
    public MatiResult registerMatiResult(Integer loanApplicationId, Integer queryId) {
        Integer id = queryForObjectTrx("insert into credit.tb_mati_result (loan_application_id, query_id) values (?, ?) returning mati_result_id", Integer.class, false,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, queryId));

        MatiResult matiResult = new MatiResult();
        matiResult.setId(id);
        matiResult.setLoanApplicationId(loanApplicationId);
        return matiResult;
    }

    @Override
    public List<MatiResult> getMatiResultsByLoanApplication(int loanApplicationId) {
        JSONArray dbArray = queryForObjectTrx("select * from credit.get_loan_application_mati_results(?)", JSONArray.class, false,
                new SqlParameterValue(Types.INTEGER, loanApplicationId));

        if(dbArray == null)
            return new ArrayList<>();

        List<MatiResult> matiResults = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            MatiResult matiResult = new MatiResult();
            matiResult.fillFromDb(dbArray.getJSONObject(i));
            matiResults.add(matiResult);
        }
        return matiResults;
    }

    @Override
    public void updateMatiResult(int matiResultId, Date finishDate, String response, Integer status) throws Exception {
        updateTrx("UPDATE credit.tb_mati_result SET finish_date = ?, status = ?, response = ?::json WHERE mati_result_id = ?;",
                new SqlParameterValue(Types.TIMESTAMP, finishDate),
                new SqlParameterValue(Types.INTEGER, status),
                new SqlParameterValue(Types.VARCHAR, response),
                new SqlParameterValue(Types.INTEGER, matiResultId));
    }

    @Override
    public void updateMatiStatus(int matiResultId, Integer status) throws Exception {
        updateTrx("UPDATE credit.tb_mati_result SET status = ? WHERE mati_result_id = ?;",
                new SqlParameterValue(Types.INTEGER, status),
                new SqlParameterValue(Types.INTEGER, matiResultId));
    }

    @Override
    public void updateMatiResultLoanApplicationId(int matiResultId, Integer loanApplicationId) throws Exception {
        updateTrx("UPDATE credit.tb_mati_result SET loan_application_id = ? WHERE mati_result_id = ?;",
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, matiResultId));
    }

    @Override
    public void updateMatiResultVerificationId(int matiResultId, String verificationId) throws Exception {
        updateTrx("UPDATE credit.tb_mati_result SET verification_id = ? WHERE mati_result_id = ?;",
                new SqlParameterValue(Types.VARCHAR, verificationId),
                new SqlParameterValue(Types.INTEGER, matiResultId));
    }

    @Override
    public RekognitionProProcess getRekognitionProProcess(int loanApplicationId, char type, Integer userFileTypeId) {
        JSONObject jsonResult = queryForObjectTrx("select * from credit.get_rekognition_pro_process_result(?,?,?)", JSONObject.class, false,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.CHAR, type),
                new SqlParameterValue(Types.INTEGER, userFileTypeId)
                );
        if(jsonResult == null)
            return null;
        RekognitionProProcess result = new RekognitionProProcess();
        result.fillFromDb(jsonResult, catalogService);
        return result;
    }

    @Override
    public RekognitionProProcess getRekognitionProProcessById(int processId) {
        JSONObject jsonResult = queryForObjectTrx("select * from credit.get_rekognition_pro_process_result_by_id(?)", JSONObject.class, false,
                new SqlParameterValue(Types.INTEGER, processId)
        );
        if(jsonResult == null)
            return null;
        RekognitionProProcess result = new RekognitionProProcess();
        result.fillFromDb(jsonResult, catalogService);
        return result;
    }

    @Override
    public RekognitionProProcess getRekognitionProProcessByLoanIdUserFileIdAndTypeAndStatus(int loanApplicationId, char type, int userFileId, Character status) {
        JSONObject jsonResult = queryForObjectTrx("select * from credit.get_rekognition_pro_process_result_by_loan_type_user_status(?,?,?,?)", JSONObject.class, false,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.CHAR, type),
                new SqlParameterValue(Types.INTEGER, userFileId),
                new SqlParameterValue(Types.CHAR, status)
        );
        if(jsonResult == null)
            return null;
        RekognitionProProcess result = new RekognitionProProcess();
        result.fillFromDb(jsonResult, catalogService);
        return result;
    }

    @Override
    public RekognitionProProcess saveRekognitionProcess(int loanApplicationId, char type, int userFileTypeId, int userFileId, Character status, JSONObject response) {
        JSONObject jsonResult = queryForObjectTrx("select * from credit.save_rekognition_pro_process(?,?,?,?,?,?)", JSONObject.class, false,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.CHAR, type),
                new SqlParameterValue(Types.OTHER, response != null ? response.toString() : null),
                new SqlParameterValue(Types.CHAR, status),
                new SqlParameterValue(Types.INTEGER, userFileTypeId),
                new SqlParameterValue(Types.INTEGER, userFileId)
        );
        if(jsonResult == null)
            return null;
        RekognitionProProcess result = new RekognitionProProcess();
        result.fillFromDb(jsonResult, catalogService);
        return result;
    }

    @Override
    public RekognitionProProcess updateRekognitionProcess(int processId, JSONObject response, char status, String errorDetail) {
        JSONObject jsonResult = queryForObjectTrx("select * from credit.update_rekognition_pro_process(?,?,?,?)", JSONObject.class, false,
                new SqlParameterValue(Types.INTEGER, processId),
                new SqlParameterValue(Types.OTHER, response != null ? response.toString() : null),
                new SqlParameterValue(Types.CHAR, status),
                new SqlParameterValue(Types.VARCHAR, errorDetail)
        );
        if(jsonResult == null)
            return null;
        RekognitionProProcess result = new RekognitionProProcess();
        result.fillFromDb(jsonResult, catalogService);
        return result;
    }

    @Override
    public RekognitionProResult saveRekognitionProResult(int loanApplicationId, Character status, RekognitionProData response) {
        JSONObject jsonResult = queryForObjectTrx("select * from credit.save_rekognition_pro_result(?,?,?)", JSONObject.class, false,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.OTHER, response != null ? new Gson().toJson(response) : null),
                new SqlParameterValue(Types.CHAR, status)
        );
        if(jsonResult == null)
            return null;
        RekognitionProResult result = new RekognitionProResult();
        result.fillFromDb(jsonResult, catalogService);
        return result;
    }


    @Override
    public RekognitionProResult getRekognitionProResult(int loanApplicationId) {
        JSONObject jsonResult = queryForObjectTrx("select * from credit.get_rekognition_pro_result_by_loan_application_id(?)", JSONObject.class, false,
                new SqlParameterValue(Types.INTEGER, loanApplicationId)
        );
        if(jsonResult == null)
            return null;
        RekognitionProResult result = new RekognitionProResult();
        result.fillFromDb(jsonResult, catalogService);
        return result;
    }

    @Override
    public RekognitionReniecResult saveRekognitionReniecResult(int loanApplicationId, Character status, RekognitionReniecData response) {
        JSONObject jsonResult = queryForObjectTrx("select * from credit.save_rekognition_reniec_result(?,?,?)", JSONObject.class, false,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.OTHER, response != null ? new Gson().toJson(response) : null),
                new SqlParameterValue(Types.CHAR, status)
        );
        if(jsonResult == null)
            return null;
        RekognitionReniecResult result = new RekognitionReniecResult();
        result.fillFromDb(jsonResult, catalogService);
        return result;
    }

    @Override
    public RekognitionReniecResult getRekognitionReniecResult(int loanApplicationId) {
        JSONObject jsonResult = queryForObjectTrx("select * from credit.get_rekognition_reniec_result_by_loan_application_id(?)", JSONObject.class, false,
                new SqlParameterValue(Types.INTEGER, loanApplicationId)
        );
        if(jsonResult == null)
            return null;
        RekognitionReniecResult result = new RekognitionReniecResult();
        result.fillFromDb(jsonResult, catalogService);
        return result;
    }
}
