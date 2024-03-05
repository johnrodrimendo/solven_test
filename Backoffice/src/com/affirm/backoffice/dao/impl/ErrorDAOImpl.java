package com.affirm.backoffice.dao.impl;

import com.affirm.backoffice.model.*;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.backoffice.dao.ErrorDAO;
import org.json.JSONArray;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository("errorDAO")
public class ErrorDAOImpl extends JsonResolverDAO implements ErrorDAO {


    @Override
    public List<ExceptionApp> getExceptions(int limit, int offset) {
        JSONArray dbJson = queryForObjectTrx("select * from support.get_recent_exceptions(?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.INTEGER, offset));

        if (dbJson == null) {
            return null;
        }

        List<ExceptionApp> exceptions = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            ExceptionApp exception = new ExceptionApp();
            exception.fillFromDb(dbJson.getJSONObject(i));
            exceptions.add(exception);
        }

        return exceptions;
    }

    @Override
    public List<RecurrentException> getRecurrentExceptions(int limit, int offset, Date startDate, Date endDate) {
        JSONArray dbJson = queryForObjectTrx("select * from support.get_recurrent_exceptions(?, ?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate));
        if (dbJson == null) {
            return null;
        }

        List<RecurrentException> exceptions = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            RecurrentException exception = new RecurrentException();
            exception.fillFromDb(dbJson.getJSONObject(i));
            exceptions.add(exception);
        }

        return exceptions;
    }

    @Override
    public List<ReportEntityWsStatus> getReportEntityWsStatus(int limit, int offset, Date startDate, Date endDate) {
        JSONArray dbJson = queryForObjectTrx("select * from security.rp_entity_ws_status(?, ?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate));
        if (dbJson == null) {
            return null;
        }

        List<ReportEntityWsStatus> reportEntityWsStatuses = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            ReportEntityWsStatus reportEntityWsStatus = new ReportEntityWsStatus();
            reportEntityWsStatus.fillFromDb(dbJson.getJSONObject(i));
            reportEntityWsStatuses.add(reportEntityWsStatus);
        }

        return reportEntityWsStatuses;
    }

    @Override
    public List<ReportProcessByHour> getReportProcessByHour(Date startDate, Date endDate) {
        JSONArray dbJson = queryForObjectTrx("select * from support.rp_process_by_hour(?, ?)", JSONArray.class,
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate));

        if (dbJson == null) {
            return null;
        }

        List<ReportProcessByHour> processesByHour = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            ReportProcessByHour processByHour = new ReportProcessByHour();
            processByHour.fillFromDb(dbJson.getJSONObject(i));
            processesByHour.add(processByHour);
        }

        return processesByHour;
    }


    @Override
    public List<EntityError> getEntityErrorsByEntityId(int entityId) {
        JSONArray dbJson = queryForObjectTrx("select * from support.tb_entity_error where entity_id = ?", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId));
        if (dbJson == null) {
            return null;
        }

        List<EntityError> EntityErrors = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            EntityError entityError = new EntityError();
            entityError.fillFromDb(dbJson.getJSONObject(i));
            EntityErrors.add(entityError);
        }

        return EntityErrors;
    }
}
