package com.affirm.tests.dao

import com.affirm.client.dao.SampleDAO
import com.affirm.tests.BaseAcquisitionConfig
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class SampleDAOTest extends BaseAcquisitionConfig {

    @Autowired
    SampleDAO sampleDAO

    static final Integer KEY = 10000
    static final String VALUE = "79a8034192055c1d8d0a0765b3856fff"
    static final long MILIS = new Date().getTime()


    static Map.Entry<Integer, String> getEntry() {
        Map<Integer, String> map = new HashMap<>();
        map.put(KEY, VALUE)

        return map.entrySet().first()
    }

    @Test
    void insertFromSampleDAO() {
        sampleDAO.insert(getEntry())
    }

    @Test
    void updateToUpperCaseFromSampleDAO() {
        sampleDAO.updateToUpperCase(getEntry())
    }

    @Test
    void deleteFromSampleDAO() {
        sampleDAO.delete(getEntry())
    }

    @Test
    void selectAllFromSampleDAO() {
        Map<Integer, String> result = sampleDAO.selectAll()
        Assert.assertNotNull(result)
    }

    @Test
    void logTransactionMilisFromSampleDAO() {
        sampleDAO.logTransactionMilis(MILIS)
    }
}
