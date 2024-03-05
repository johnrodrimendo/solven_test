package com.affirm.common.dao;

import com.affirm.common.model.transactional.WsClient;

/**
 * Created by jrodriguez on 26/09/16.
 */
public interface RestApiDAO {

    WsClient getWsClientByApiKey(String apiKey);
    WsClient getWsClientByWsClient(int ws_client_id);
    int getEntityByClientId(int wsClientId);
}