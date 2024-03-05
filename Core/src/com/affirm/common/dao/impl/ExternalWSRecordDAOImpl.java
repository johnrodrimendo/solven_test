package com.affirm.common.dao.impl;

import com.affirm.common.dao.ExternalWSRecordDAO;
import com.affirm.common.dao.ExtranetNoteDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.model.ExternalWSRecord;
import com.affirm.common.model.ExtranetNote;
import com.affirm.common.service.CatalogService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Repository
public class ExternalWSRecordDAOImpl extends JsonResolverDAO implements ExternalWSRecordDAO {


    @Autowired
    private CatalogService catalogService;

    @Override
    public void insertExternalWSRecord(Integer loanApplicationId, Date startDate, String url, String request, String response, Integer responseHttpCode) {
        queryForObject("SELECT * FROM external.ins_lg_external_ws_record(?,?,?,?,?,?,?);", String.class, true, EXTERNAL_DB,
                new SqlParameterValue(Types.INTEGER, loanApplicationId),
                new SqlParameterValue(Types.TIMESTAMP, startDate),
                new SqlParameterValue(Types.TIMESTAMP, new Date()),
                new SqlParameterValue(Types.VARCHAR, url),
                new SqlParameterValue(Types.VARCHAR, request),
                new SqlParameterValue(Types.VARCHAR, response),
                new SqlParameterValue(Types.INTEGER, responseHttpCode)
        );
    }

    @Override
    public void insertExternalWSRecord(ExternalWSRecord externalWSRecord) {
        insertExternalWSRecord(externalWSRecord.getLoanApplicationId(), externalWSRecord.getStartDate(), externalWSRecord.getUrl(), externalWSRecord.getRequest(), externalWSRecord.getResponse(), externalWSRecord.getResponseHttpCode());
    }

    @Override
    public List<ExternalWSRecord> getLoanApplicationExternalWSRecords(int loanApplicationId) {
        JSONArray dbArray = queryForObject("SELECT * FROM external.get_loan_application_lg_external_ws_record(?);", JSONArray.class, true, EXTERNAL_DB,
                new SqlParameterValue(Types.INTEGER, loanApplicationId)
        );
        if (dbArray == null) {
            return new ArrayList<>();
        }

        List<ExternalWSRecord> data = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ExternalWSRecord externalWSRecord = new ExternalWSRecord();
            externalWSRecord.fillFromDb(dbArray.getJSONObject(i), catalogService);
            data.add(externalWSRecord);
        }
        return data;
    }

    @Override
    public ExternalWSRecord getExternalWSRecord(int externalWSRecordId) {
        JSONObject dbJson = queryForObject("SELECT * FROM external.lg_external_ws_record(?);", JSONObject.class, true, EXTERNAL_DB,
                new SqlParameterValue(Types.INTEGER, externalWSRecordId)
        );
        if (dbJson == null) return null;

        ExternalWSRecord data = new ExternalWSRecord();
        data.fillFromDb(dbJson, catalogService);
        return data;
    }

    @Override
    public Integer insertWebhookRequest(String url, String request) throws Exception {

        Integer dbJson = queryForObjectExternal("INSERT INTO external.lg_webhook_request(url,request) values (? ,?) returning id", Integer.class,
                new SqlParameterValue(Types.VARCHAR, url),
                new SqlParameterValue(Types.VARCHAR, request)
        );
        return dbJson;
    }

}
