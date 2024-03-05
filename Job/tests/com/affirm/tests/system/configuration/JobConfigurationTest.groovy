package com.affirm.tests.system.configuration

import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseJobConfig
import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class JobConfigurationTest extends BaseJobConfig {

    @Test
    void jobPropertyNameIsSetSuccess() {
        assertEquals Configuration.Application.JOB.name(), System.getProperty("application")
    }
}
