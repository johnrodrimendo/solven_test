package com.affirm.backoffice.dao.impl;

import com.affirm.backoffice.dao.PersonBODAO;
import com.affirm.backoffice.model.LineaResultBoPainter;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jrodriguez on 08/06/16.
 */
@Repository("personBoDao")
public class PersonBODAOImpl extends JsonResolverDAO implements PersonBODAO {

    @Autowired
    private CatalogService catalogService;

    @Override
    public ReniecResult getReniecResult(int personId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.bo_get_reniec(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return null;
        }

        ReniecResult reniec = new ReniecResult();
        reniec.fillFromDb(dbJson);
        return reniec;
    }

    @Override
    public EssaludResult getEssaludResult(int personId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.bo_get_essalud(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return null;
        }

        EssaludResult essalud = new EssaludResult();
        essalud.fillFromDb(dbJson);
        return essalud;
    }

    @Override
    public List<LineaResultBoPainter> getPhoneContractLineaResult(int personId) throws Exception {
        String query = "select * from person.bo_get_phone_contract(?)";

        JSONArray dbArray = queryForObjectTrx(query, JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbArray == null) {
            return null;
        }

        List<LineaResultBoPainter> lineas = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            LineaResultBoPainter linea = new LineaResultBoPainter();
            linea.fillFromDb(dbArray.getJSONObject(i));
            lineas.add(linea);
        }
        return lineas;
    }

    @Override
    public SatResult getSatResult(int personId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.bo_get_sat(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return null;
        }

        SatResult sat = new SatResult();
        sat.fillFromDb(dbJson);
        return sat;
    }

    @Override
    public SisResult getSisResult(int personId) throws Exception {
        JSONObject dbJson = queryForObjectTrx("select * from person.bo_get_sis(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJson == null) {
            return null;
        }

        SisResult sis = new SisResult();
        sis.fillFromDb(dbJson);
        return sis;
    }

    @Override
    public List<SoatRecordsResult> getSoatRecordsResults(int personId) throws Exception{
        JSONArray dbJsonArray = queryForObjectTrx("select * from person.bo_get_soat(?)"
                , JSONArray.class,
                new SqlParameterValue(Types.INTEGER, personId));
        if (dbJsonArray == null) {
            return null;
        }

        List<SoatRecordsResult> soatRecordsResults = new ArrayList<>();

        for (int i = 0; i < dbJsonArray.length(); i++) {
            SoatRecordsResult spr = new SoatRecordsResult();
            spr.fillFromDb(new JSONObject(dbJsonArray.get(i).toString()));
            soatRecordsResults.add(spr);
        }
        return soatRecordsResults;
    }
}
