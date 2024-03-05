package com.affirm.common.dao.impl;

import com.affirm.common.dao.ErrorEntityDao;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.model.EntityError;
import com.affirm.common.model.EntityErrorExtranetPainter;
import com.affirm.common.model.ExtranetNote;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.apache.commons.lang3.tuple.Pair;
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
public class ErrorEntityDaoImpl extends JsonResolverDAO implements ErrorEntityDao {

    @Autowired
    CatalogService catalogService;

    @Override
    public List<EntityErrorExtranetPainter> getEntityErrors(int entityId, Date startDate, Date endDate, String search, Integer offset, Integer limit) {
        if(search != null && search.isEmpty()) search = null;
        JSONArray dbJson = queryForObjectTrx("select * from support.get_entity_errors(?,?,?,?,?,?);", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.VARCHAR, search),
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit));
        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<EntityErrorExtranetPainter> entityErrorsList = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            EntityErrorExtranetPainter entityError = new EntityErrorExtranetPainter();
            entityError.fillFromDb(dbJson.getJSONObject(i),catalogService);
            entityErrorsList.add(entityError);
        }

        return entityErrorsList;
    }

    @Override
    public Pair<Integer, Double> getEntityErrorsCount(Integer entityId, Date startDate, Date endDate,  String search) {
        if(search != null && search.isEmpty()) search = null;
        JSONObject dbJson = queryForObjectTrx("select * from support.get_entity_errors_count(?,?,?,?);", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.VARCHAR, search),
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate)
                );

        if (dbJson == null)
            return Pair.of(0, 0.0);

        return Pair.of(JsonUtil.getIntFromJson(dbJson, "count", 0), null);
    }

    @Override
    public EntityErrorExtranetPainter getEntityErrorsById(int entityExtranetErrorId, int entityId) {
        JSONObject dbJson = queryForObjectTrx("select * from support.get_entity_error_by_id(?, ?);", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, entityExtranetErrorId),
                new SqlParameterValue(Types.INTEGER, entityId)
        );

        if (dbJson == null)
            return null;

        EntityErrorExtranetPainter entityError = new EntityErrorExtranetPainter();
        entityError.fillFromDb(dbJson,catalogService);
        return entityError;
    }

    @Override
    public void addEntityError(int loanApplicationId, int entityId, int entityWsId, String error, Integer lgEntityWsId) {
        updateTrx("INSERT INTO support.tb_entity_error(error, entity_id, entity_ws_id, lg_application_entity_ws_id, loan_application_id) VALUES (?,?,?,?,?)",
                new SqlParameterValue(Types.VARCHAR, error),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, entityWsId),
                new SqlParameterValue(Types.INTEGER, lgEntityWsId),
                new SqlParameterValue(Types.INTEGER, loanApplicationId)
        );
    }
}
