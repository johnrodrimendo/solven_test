package com.affirm.tests.dao


import com.affirm.common.dao.WebServiceDAO
import com.affirm.common.model.catalog.EntityWebService
import com.affirm.tests.BaseConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired

class WebServiceDAOTest extends BaseConfig {

    @Autowired
    private WebServiceDAO webServiceDAO

    private static final Integer LOAN_APPLICATION_ID = 0
    private static final Integer ENTITY_WEBSERVICE_ID = EntityWebService.ABACO_ACTUALIZAR_SOCIO
    private static final Integer WEBSERVICE_LOG_ID = 1
    private static final char STATUS = 's'

    @Test
    void shouldRegisterEntityWebServiceResult() {
        Executable executable = { webServiceDAO.registerEntityWebServiceResult(LOAN_APPLICATION_ID, ENTITY_WEBSERVICE_ID, null) }

        Assertions.assertDoesNotThrow(executable)
    }

    @Test
    void shouldRegisterEntityWebServiceLog() {
        Integer result = webServiceDAO.registerEntityWebServiceLog(ENTITY_WEBSERVICE_ID, LOAN_APPLICATION_ID, null, null, STATUS, null, null)

        Assertions.assertNotNull(result)
    }

    @Test
    void shouldUpdateEntityWebServiceLogStatus() {
        Executable executable = { webServiceDAO.updateEntityWebServiceLogStatus(WEBSERVICE_LOG_ID, STATUS) }

        Assertions.assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdateEntityWebServiceLogResponse() {
        Executable executable = { webServiceDAO.updateEntityWebServiceLogResponse(WEBSERVICE_LOG_ID, null) }

        Assertions.assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdateEntityWebServiceLogRequest() {
        Executable executable = { webServiceDAO.updateEntityWebServiceLogRequest(WEBSERVICE_LOG_ID, null) }

        Assertions.assertDoesNotThrow(executable)
    }

    @Test
    void shouldUpdateEntityWebServiceLogFinishDate() {
        Executable executable = { webServiceDAO.updateEntityWebServiceLogFinishDate(WEBSERVICE_LOG_ID, null) }

        Assertions.assertDoesNotThrow(executable)
    }

    @Test
    void shouldGetExternalServiceResponse() {
        Executable executable = { webServiceDAO.getExternalServiceResponse(LOAN_APPLICATION_ID, ENTITY_WEBSERVICE_ID) }

        Assertions.assertDoesNotThrow(executable)
    }

    @Test
    void shouldGetEntityWebServiceLog() {
        Executable executable = { webServiceDAO.getEntityWebServiceLog(LOAN_APPLICATION_ID, ENTITY_WEBSERVICE_ID) }

        Assertions.assertDoesNotThrow(executable)
    }

}
