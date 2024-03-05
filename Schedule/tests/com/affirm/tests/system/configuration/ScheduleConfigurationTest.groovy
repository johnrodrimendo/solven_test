package com.affirm.tests.system.configuration

import com.affirm.system.configuration.Configuration
import com.affirm.tests.BaseScheduleConfig
import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class ScheduleConfigurationTest extends BaseScheduleConfig {

    @Test
    void schedulePropertyNameIsSetSuccess() {
        assertEquals Configuration.Application.SCHEDULE.name(), System.getProperty("application")
    }
}
