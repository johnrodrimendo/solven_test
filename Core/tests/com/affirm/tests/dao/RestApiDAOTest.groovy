package com.affirm.tests.dao

import com.affirm.common.dao.RestApiDAO
import com.affirm.common.model.transactional.WsClient
import com.affirm.tests.BaseConfig
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class RestApiDAOTest extends BaseConfig {

    @Autowired
    RestApiDAO restApiDAO

    static final String API_KEY = "C6DFA0B215B2CF24EF04794F718A3FC8"
    static final int WS_CLIENT_ID = 5

    @Test
    void getWsClientByApiKeyFromRestApiDAO() {
        WsClient result = restApiDAO.getWsClientByApiKey(API_KEY)
        Assert.assertNull(result)
    }

    @Test
    void getWsClientByWsClientFromRestApiDAO() {
        WsClient result = restApiDAO.getWsClientByWsClient(WS_CLIENT_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void getEntityByClientIdFromRestApiDAO() {
        Integer result = restApiDAO.getEntityByClientId(WS_CLIENT_ID)
        Assert.assertNotNull(result)
    }
}
