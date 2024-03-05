package com.affirm.tests.system.configuration

import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseBoConfig
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class BackOfficeConfigurationTest extends BaseBoConfig {
    @Test
    void clientPropertyNameIsSetSuccess() {
        assertEquals Configuration.Application.BACKOFFICE.name(), System.getProperty("application")
    }
}
