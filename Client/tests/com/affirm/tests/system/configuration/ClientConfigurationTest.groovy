package com.affirm.tests.system.configuration

import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseClientConfig
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class ClientConfigurationTest extends BaseClientConfig {
    @Test
    void clientPropertyNameIsSetSuccess() {
        assertEquals Configuration.Application.CLIENT.name(), System.getProperty("application")
    }
}
