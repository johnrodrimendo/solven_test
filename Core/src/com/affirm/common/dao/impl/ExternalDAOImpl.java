package com.affirm.common.dao.impl;

import com.affirm.bantotalrest.model.BantotalToken;
import com.affirm.common.dao.ExternalDAO;
import com.affirm.common.dao.ExternalWSRecordDAO;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.model.ExternalWSRecord;
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
public class ExternalDAOImpl extends JsonResolverDAO implements ExternalDAO {

    @Autowired
    private CatalogService catalogService;

    @Override
    public void insertBantotalToken(String token) {
        queryForObject("SELECT * FROM external.ins_bantotal_token(?);", String.class, true, EXTERNAL_DB,
                new SqlParameterValue(Types.VARCHAR, token)
        );
    }


    @Override
    public BantotalToken getBantotalToken() {
        JSONObject dbJson = queryForObject("SELECT * FROM external.get_bantotal_token();", JSONObject.class, true, EXTERNAL_DB);
        if (dbJson == null) return null;

        BantotalToken data = new BantotalToken();
        data.fillFromDb(dbJson, catalogService);
        return data;
    }


}
