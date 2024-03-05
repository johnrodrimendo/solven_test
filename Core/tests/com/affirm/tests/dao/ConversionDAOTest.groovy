package com.affirm.tests.dao

import com.affirm.common.dao.ConversionDAO
import com.affirm.common.model.transactional.Conversion
import com.affirm.tests.BaseConfig
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ConversionDAOTest extends BaseConfig {

    @Autowired
    ConversionDAO conversionDAO

    static final Integer LOAN_APPLICATION_ID = 2483
    static final String PIXEL_ENTITY = "organic"
    static final String INSTANCE = "preApr"

    @Test
    void registerPixelConversionFromConversionDAO() {
        conversionDAO.registerPixelConversion(LOAN_APPLICATION_ID, PIXEL_ENTITY, INSTANCE)
    }

    @Test
    void getConversionsFromConversionDAO() {
        List<Conversion> result = conversionDAO.getConversions(LOAN_APPLICATION_ID)
        Assert.assertNotNull(result)
    }
}
