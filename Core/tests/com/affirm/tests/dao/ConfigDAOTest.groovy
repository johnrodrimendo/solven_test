package com.affirm.tests.dao

import com.affirm.common.dao.ConfigDAO
import com.affirm.tests.BaseConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired

class ConfigDAOTest extends BaseConfig {

    @Autowired
    private ConfigDAO configDAO

    @Test
    void shouldUpdateExchangeRateUSDPEN() {
        Executable executable = { configDAO.updateExchangeRateUSDPEN(3.25) }

        Assertions.assertDoesNotThrow(executable)
    }
}
