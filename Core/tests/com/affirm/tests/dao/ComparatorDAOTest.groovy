package com.affirm.tests.dao

import com.affirm.common.dao.ComparatorDAO
import com.affirm.tests.BaseConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ComparatorDAOTest extends BaseConfig {

    @Autowired
    ComparatorDAO comparatorDAO

    static final int BANK_ID = 1000
    static final String RATES = '["2"]'

    @Test
    void registerBankProductRatesFromComparatorDAO() {
        comparatorDAO.registerBankProductRates(BANK_ID, RATES)
    }

    @Test
    void applyProductRatesFromComparatorDAO() {
        comparatorDAO.applyProductRates()
    }
}
