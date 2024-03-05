package com.affirm.common.dao.impl;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.WebServiceDAO;
import com.affirm.common.model.transactional.EntityWebServiceLog;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jrodriguez on 08/06/16.
 */

@Repository("webServiceDao")
public class WebServiceDAOImpl extends JsonResolverDAO implements WebServiceDAO {



    @Override
    public void registerEntityWebServiceResult(int loanApplicationId, int entityWebServiceId, String jsonResult) {
        queryForObjectTrx("select * from credit.register_entity_ws_result(?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, entityWebServiceId),
                new SqlParameterValue(Types.OTHER, jsonResult));
    }

    @Override
    public Integer registerEntityWebServiceLog(int entityWebServiceId, Integer loanApplicationId, Date startDate, Date finishDate, char status, String request, String response) {
        return queryForObjectTrx("select * from security.register_application_entity_ws(?, ?, ?, ?, ?, ?, ?)", Integer.class,
                new SqlParameterValue(Types.INTEGER, entityWebServiceId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.TIMESTAMP, startDate),
                new SqlParameterValue(Types.TIMESTAMP, finishDate),
                new SqlParameterValue(Types.CHAR, status),
                new SqlParameterValue(Types.VARCHAR, request),
                new SqlParameterValue(Types.VARCHAR, response));
    }

    @Override
    public void updateEntityWebServiceLogStatus(int entityWebServiceLogId, char status) {
        updateTrx("UPDATE security.lg_application_entity_ws SET status = ? WHERE application_entity_ws = ?;",
                new SqlParameterValue(Types.CHAR, status),
                new SqlParameterValue(Types.INTEGER, entityWebServiceLogId));
    }

    @Override
    public void updateEntityWebServiceLogResponse(int entityWebServiceLogId, String response) {
        updateTrx("UPDATE security.lg_application_entity_ws SET response = ? WHERE application_entity_ws = ?;",
                new SqlParameterValue(Types.VARCHAR, response),
                new SqlParameterValue(Types.INTEGER, entityWebServiceLogId));
    }

    @Override
    public void updateEntityWebServiceLogRequest(int entityWebServiceLogId, String request) {
        updateTrx("UPDATE security.lg_application_entity_ws SET request = ? WHERE application_entity_ws = ?;",
                new SqlParameterValue(Types.VARCHAR, request),
                new SqlParameterValue(Types.INTEGER, entityWebServiceLogId));
    }

    @Override
    public void updateEntityWebServiceLogFinishDate(int entityWebServiceLogId, Date finishDate) {
        updateTrx("UPDATE security.lg_application_entity_ws SET finish_date = ? WHERE application_entity_ws = ?;",
                new SqlParameterValue(Types.TIMESTAMP, finishDate),
                new SqlParameterValue(Types.INTEGER, entityWebServiceLogId));
    }

    @Override
    public JSONObject getExternalServiceResponse(Integer loanApplicationId, Integer wsServiceId) {
        return queryForObjectTrx("select * from security.get_tb_entity_ws_result(?,?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, wsServiceId));
    }

    @Override
    public List<EntityWebServiceLog> getEntityWebServiceLog(Integer loanApplicationId, Integer wsServiceId) {
        JSONArray dbArray = queryForObjectTrx("select * from security.get_application_entity_ws(?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.INTEGER, wsServiceId));

        if (dbArray == null)
            return null;

        List<EntityWebServiceLog> logs = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            EntityWebServiceLog log = new EntityWebServiceLog();
            log.fillFromDb(dbArray.getJSONObject(i));
            logs.add(log);
        }

        return logs;
    }



    @Override
    public List<EntityWebServiceLog> getEntityWebServiceLogByWsServiceId(Integer wsServiceId, Integer offset, Integer limit) {
        if(offset == null) offset = 0;
        if(limit == null) limit = 10;
        JSONArray dbArray = queryForObjectTrx("select * from security.get_application_entity_ws_lg(?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, wsServiceId),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit)
        );

        if (dbArray == null)
            return null;

        List<EntityWebServiceLog> logs = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            EntityWebServiceLog log = new EntityWebServiceLog();
            log.fillFromDb(dbArray.getJSONObject(i));
            logs.add(log);
        }

        return logs;
    }

    @Override
    public EntityWebServiceLog getEntityWebServiceLogById(Integer entityWebServiceLogId) {
        JSONObject dbJson = queryForObjectTrx("select * from security.get_lg_application_entity_ws_by_id(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, entityWebServiceLogId));

        if (dbJson == null)
            return null;

        EntityWebServiceLog log = new EntityWebServiceLog();
        log.fillFromDb(dbJson);

        return log;

    }

}
