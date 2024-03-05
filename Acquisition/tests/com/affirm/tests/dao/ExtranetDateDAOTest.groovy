package com.affirm.tests.dao

import com.affirm.client.dao.ExtranetDateDAO
import com.affirm.client.model.ExtranetDate
import com.affirm.tests.BaseAcquisitionConfig
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ExtranetDateDAOTest extends BaseAcquisitionConfig {

    @Autowired
    ExtranetDateDAO extranetDateDAO

    static final Date CURRENT_DATE = new Date()
    static final Date START_DATE = CURRENT_DATE
    static final Date END_DATE = CURRENT_DATE

    @Test
    void getExtranetDatesFromExtranetDateDAO() {
        List<ExtranetDate> result = extranetDateDAO.getExtranetDates(START_DATE, END_DATE)
        Assert.assertNotNull(result)
    }
}
