package com.affirm.tests.system.configuration

import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseLandingConfig
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class LandingConfigurationTest extends BaseLandingConfig {
    @Test
    void clientPropertyNameIsSetSuccess() {
        assertEquals Configuration.Application.CLIENT.name(), System.getProperty("application")
    }
}
