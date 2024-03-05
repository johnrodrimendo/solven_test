package com.affirm.common.dao.impl;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.PreApprovedDAO;
import com.affirm.common.model.transactional.ReportProces;
import com.affirm.negativebase.model.NegativeBaseProcessed;
import com.affirm.preapprovedbase.model.PreApprovedBaseProcessed;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class PreApprovedDaoImpl extends JsonResolverDAO implements PreApprovedDAO {

    @Override
    public Integer registerUploadPreApprovedProcess(Integer entityId, String url, Integer entityUserId){
        return queryForObjectTrx("select * from support.ins_base_pre_approved_processed(?, ?, ?);", Integer.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.VARCHAR, url),
                new SqlParameterValue(Types.INTEGER, entityUserId));
    }

    @Override
    public PreApprovedBaseProcessed getPreApprovedBaseProcessed(Integer processId) {

        JSONArray dbJson = queryForObjectTrx("select * from support.get_base_pre_approved_processed_by_id(?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, processId));
        if (dbJson == null) {
            return null;
        }

        for (int i = 0; i < dbJson.length(); i++) {
            PreApprovedBaseProcessed preApprovedBaseProcessed = new PreApprovedBaseProcessed();
            preApprovedBaseProcessed.fillFromDb(dbJson.getJSONObject(i));
            return preApprovedBaseProcessed;
        }
        return null;
    }

    @Override
    public List<PreApprovedBaseProcessed> getHistoricListPreAppovedBase(Integer entityId, Integer limit, Integer offset){
        JSONArray dbJson = queryForObjectTrx("select * from support.get_base_pre_approved_processed(?, ?, ?);", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.INTEGER, offset));
        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<PreApprovedBaseProcessed> preApprovedBaseProcesseds = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            PreApprovedBaseProcessed preApprovedBaseProcessed = new PreApprovedBaseProcessed();
            preApprovedBaseProcessed.fillFromDb(dbJson.getJSONObject(i));
            preApprovedBaseProcesseds.add(preApprovedBaseProcessed);
        }
        return preApprovedBaseProcesseds;
    }

    @Override
    public void updateProcessDate(int processId, Date processDate) {
        updateTrx("UPDATE support.tb_base_pre_approved_processed set process_date = ? where base_pre_approved_processed_id = ?",
                new SqlParameterValue(Types.TIMESTAMP, processDate),
                new SqlParameterValue(Types.INTEGER, processId));
    }

    @Override
    public void updateProcessStatus(int processId, Character status) {
        updateTrx("UPDATE support.tb_base_pre_approved_processed set status = ? where base_pre_approved_processed_id = ?",
                new SqlParameterValue(Types.CHAR, status),
                new SqlParameterValue(Types.INTEGER, processId));
    }

    @Override
    public void updateProcessMessage(int processId, PreApprovedBaseProcessed.ErrorDetail error) {
        updateTrx("UPDATE support.tb_base_pre_approved_processed set error_detail = ? where base_pre_approved_processed_id = ?",
                new SqlParameterValue(Types.OTHER, new Gson().toJson(error)),
                new SqlParameterValue(Types.INTEGER, processId));
    }

}
