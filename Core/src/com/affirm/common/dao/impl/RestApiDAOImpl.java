package com.affirm.common.dao.impl;

import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.RestApiDAO;
import com.affirm.common.model.transactional.WsClient;
import org.json.JSONObject;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;

/**
 * Created by jrodriguez on 26/09/16.
 */

@Repository("restApiDAO")
public class RestApiDAOImpl extends JsonResolverDAO implements RestApiDAO {



    @Override
    public WsClient getWsClientByApiKey(String apiKey) {
        JSONObject dbJson = queryForObjectTrx("select * from security.get_apikey(?)", JSONObject.class,
                new SqlParameterValue(Types.VARCHAR, apiKey));
        if (dbJson == null)
            return null;

        WsClient wsClient = new WsClient();
        wsClient.fillFromDb(dbJson);
        return wsClient;
    }

    @Override
    public WsClient getWsClientByWsClient(int ws_client_id) {
        JSONObject dbJson = queryForObjectTrx("select * from security.get_apikey(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, ws_client_id));
        if (dbJson == null)
            return null;

        WsClient wsClient = new WsClient();
        wsClient.fillFromDb(dbJson);
        return wsClient;
    }

    public int getEntityByClientId(int wsClientId) {
        return queryForObjectTrx("select * from security.get_entity_id_by_ws_client_id(?)", Integer.class,
                new SqlParameterValue(Types.INTEGER, wsClientId));

    }



}