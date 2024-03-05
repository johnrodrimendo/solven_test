package com.affirm.common.dao.impl;

import com.affirm.common.dao.BasesDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.model.transactional.AztecaGatewayBasePhone;
import com.affirm.common.model.transactional.GatewayBaseEvent;
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
public class BasesDaoImpl extends JsonResolverDAO implements BasesDAO {

    @Autowired
    CatalogService catalogService;

    @Override
    public List<AztecaGatewayBasePhone> getAztecaCobranzaPhones(int offset, int limit) {
        JSONArray dbJson = queryForObject("select * from bases.get_azteca_cobranza_phones(?, ?)", JSONArray.class, REPOSITORY_DB,
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit));
        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<AztecaGatewayBasePhone> basePhones = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            AztecaGatewayBasePhone basePhone = new AztecaGatewayBasePhone();
            basePhone.fillFromDb(dbJson.getJSONObject(i));
            basePhones.add(basePhone);
        }
        return basePhones;
    }

    @Override
    public Integer getAztecaCobranzaPhonesCount() {
        return queryForObject("select * from bases.get_azteca_cobranza_phones_count()", Integer.class, REPOSITORY_DB);
    }

    @Override
    public GatewayBaseEvent registerCollectionBaseEvent(Integer entityId, Date registerDate, Character type, Character status) {
        Integer id = queryForObjectTrx("insert into support.tb_collection_base_event(entity_id, register_date, type, status) values (?, ?, ?, ?) returning collection_base_id", Integer.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.TIMESTAMP, registerDate),
                new SqlParameterValue(Types.CHAR, type),
                new SqlParameterValue(Types.CHAR, status));

        GatewayBaseEvent baseEvent = new GatewayBaseEvent();
        baseEvent.setId(id);
        return baseEvent;
    }

    @Override
    public void updateCollectionBaseEventStatus(int id, Character status) throws Exception {
        updateTrx("UPDATE support.tb_collection_base_event set status = ? WHERE collection_base_id = ?;",
                new SqlParameterValue(Types.CHAR, status),
                new SqlParameterValue(Types.INTEGER, id));
    }

    @Override
    public void updateCollectionBaseEventFinishDate(int id, Date finishDate) throws Exception {
        updateTrx("UPDATE support.tb_collection_base_event set finish_date = ? WHERE collection_base_id = ?;",
                new SqlParameterValue(Types.TIMESTAMP, finishDate),
                new SqlParameterValue(Types.INTEGER, id));
    }

    @Override
    public void updateCollectionBaseEventCounts(int id, Integer success, Integer failed) throws Exception {
        updateTrx("UPDATE support.tb_collection_base_event set success_count = ?, failed_count = ? WHERE collection_base_id = ?;",
                new SqlParameterValue(Types.INTEGER, success),
                new SqlParameterValue(Types.INTEGER, failed),
                new SqlParameterValue(Types.INTEGER, id));
    }


    @Override
    public GatewayBaseEvent getLastCollectionBaseEventByType(Integer entityId, Character type) {
        JSONObject data = queryForObjectTrx("select * FROM support.get_last_collection_base(?,?,?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.CHAR, type),
                new SqlParameterValue(Types.CHAR, GatewayBaseEvent.STATUS_SUCCESS));

        GatewayBaseEvent baseEvent = new GatewayBaseEvent();
        baseEvent.fillFromDb(data,catalogService);

        return baseEvent;
    }

}
