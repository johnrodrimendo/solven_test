package com.affirm.tests.dao

import com.affirm.common.dao.CurrencyDAO
import com.affirm.common.model.catalog.CountryParam
import com.affirm.tests.BaseConfig
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow

class CurrencyDAOTest extends BaseConfig {

    @Autowired
    private CurrencyDAO currencyDAO

    @Test
    void shouldRegisterExchangeRate() {
        Executable peruExecutable = { currencyDAO.registerExchangeRate(CountryParam.COUNTRY_PERU, 0.0) }
        Executable argentinaExecutable = { currencyDAO.registerExchangeRate(CountryParam.COUNTRY_ARGENTINA, 0.0) }

        assertDoesNotThrow(peruExecutable)
        assertDoesNotThrow(argentinaExecutable)
    }
}
