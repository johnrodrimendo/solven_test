package com.affirm.tests.dao


import com.affirm.latam.dao.LatamDAO
import com.affirm.tests.BaseAcquisitionConfig
import org.json.JSONObject
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class LatamDAOTest extends BaseAcquisitionConfig {

    @Autowired
    LatamDAO latamDAO

    @Test
    void avgCreditRateFromLatamDAO() {
        JSONObject result = latamDAO.avgCreditRate()
        Assert.assertNotNull(result)
    }
}
