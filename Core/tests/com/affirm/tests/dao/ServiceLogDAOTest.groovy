package com.affirm.tests.dao

import com.affirm.common.dao.ServiceLogDAO
import com.affirm.common.model.transactional.LogSmsBulkSend
import com.affirm.tests.BaseConfig
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ServiceLogDAOTest extends BaseConfig {

    @Autowired
    ServiceLogDAO serviceLogDAO

    static final int ENTITY_ID = 12
    static final Integer LOAN_APPLICATION_ID = 1697
    static final String REQUEST = ""
    static final String RESPONSE = ""
    static final Integer PROCESS_STATUS = 1
    static final Integer SERVICE_ID = 1
    static final Integer OPERATION_ID = 2
    static final Integer LOG_ID = 4
    static final long FAILED = 5
    static final long SUCCESS = 6
    static final Integer PRODUCT_ID = 11
    static final int SYS_USER_ID = 14
    static final int QUERY_BOT_ID = 17

    @Test
    void registerServiceCallFromServiceLogDAO() {
        Integer result = serviceLogDAO.registerServiceCall(ENTITY_ID, LOAN_APPLICATION_ID, REQUEST, RESPONSE,
                PROCESS_STATUS, SERVICE_ID, OPERATION_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void updateServiceCallFromServiceLogDAO() {
        serviceLogDAO.updateServiceCall(LOG_ID, ENTITY_ID, LOAN_APPLICATION_ID, RESPONSE, PROCESS_STATUS, SERVICE_ID,
                OPERATION_ID)
    }

    @Test
    void registerSMSSenderServiceLogFromServiceLogDAO() {
        serviceLogDAO.registerSMSSenderServiceLog(FAILED, SUCCESS, ENTITY_ID, PRODUCT_ID, SYS_USER_ID, QUERY_BOT_ID)
    }

    @Test
    void getLogSmsBulkSendByQueryBotFromServiceLogDAO() {
        LogSmsBulkSend result = serviceLogDAO.getLogSmsBulkSendByQueryBot(QUERY_BOT_ID)
        Assert.assertNotNull(result)
    }
}
