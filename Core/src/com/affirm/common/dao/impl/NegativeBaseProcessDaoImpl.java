package com.affirm.common.dao.impl;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.NegativeBaseProcessDAO;
import com.affirm.negativebase.model.NegativeBaseProcessed;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class NegativeBaseProcessDaoImpl extends JsonResolverDAO implements NegativeBaseProcessDAO {

    @Override
    public Integer registerUploadNegativeBaseProcess(Integer entityId, String url, Integer entityUserId, Character type){
        return queryForObjectTrx("select * from support.ins_negative_base_processed(?, ?, ?, ?);", Integer.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.VARCHAR, url),
                new SqlParameterValue(Types.INTEGER, entityUserId),
                new SqlParameterValue(Types.CHAR, type)
                );
    }

    @Override
    public NegativeBaseProcessed getNegativeBaseProcessed(Integer processId) {

        JSONArray dbJson = queryForObjectTrx("select * from support.get_negative_base_processed_by_id(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, processId));
        if (dbJson == null) {
            return null;
        }

        for (int i = 0; i < dbJson.length(); i++) {
            NegativeBaseProcessed negativeBaseProcessed = new NegativeBaseProcessed();
            negativeBaseProcessed.fillFromDb(dbJson.getJSONObject(i));
            return negativeBaseProcessed;
        }
        return null;
    }

    @Override
    public List<NegativeBaseProcessed> getHistoricListNegativeBase(Integer entityId, Integer limit, Integer offset){
        JSONArray dbJson = queryForObjectTrx("select * from support.get_negative_base_processed(?, ?, ?);", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.INTEGER, offset));
        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<NegativeBaseProcessed> negativeBaseProcesseds = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            NegativeBaseProcessed negativeBaseProcessed = new NegativeBaseProcessed();
            negativeBaseProcessed.fillFromDb(dbJson.getJSONObject(i));
            negativeBaseProcesseds.add(negativeBaseProcessed);
        }
        return negativeBaseProcesseds;
    }

    @Override
    public void updateProcessDate(int processId, Date processDate) {
        updateTrx("UPDATE support.tb_negative_base_processed set process_date = ? where negative_base_processed_id = ?",
                new SqlParameterValue(Types.TIMESTAMP, processDate),
                new SqlParameterValue(Types.INTEGER, processId));
    }

    @Override
    public void updateProcessStatus(int processId, Character status) {
        updateTrx("UPDATE support.tb_negative_base_processed set status = ? where negative_base_processed_id = ?",
                new SqlParameterValue(Types.CHAR, status),
                new SqlParameterValue(Types.INTEGER, processId));
    }

    @Override
    public void updateProcessMessage(int processId, NegativeBaseProcessed.ErrorDetail error) {
        updateTrx("UPDATE support.tb_negative_base_processed set error_detail = ? where negative_base_processed_id = ?",
                new SqlParameterValue(Types.OTHER, new Gson().toJson(error)),
                new SqlParameterValue(Types.INTEGER, processId));
    }

}
