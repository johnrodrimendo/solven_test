package com.affirm.tests.dao

import com.affirm.client.dao.ExtranetPartnerClientDAO
import com.affirm.client.model.ExtranetPartnerClient
import com.affirm.tests.BaseAcquisitionConfig
import org.json.JSONArray
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ExtranetPartnerClientDAOTest extends BaseAcquisitionConfig {

    @Autowired
    ExtranetPartnerClientDAO extranetPartnerClientDAO

    static final Integer ENTITY_ID = 12345
    static final JSONArray RECORDS = null
    static final String LIST_TYPE = ""

    @Test
    void registerPartnerClientFromExtranetPartnerClientDAO() {
        extranetPartnerClientDAO.registerPartnerClient(ENTITY_ID, RECORDS, LIST_TYPE)
    }

    @Test
    void getPartnerClientsFromEmployerDLDAO() {
        List<ExtranetPartnerClient> result = extranetPartnerClientDAO.getPartnerClients(ENTITY_ID)
        Assert.assertNotNull(result)
    }
}
