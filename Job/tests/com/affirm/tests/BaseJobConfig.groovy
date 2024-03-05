package com.affirm.tests

import com.affirm.common.util.EnviromentSetupHelper
import com.affirm.system.configuration.SpringJobConfiguration
import com.affirm.system.configuration.SpringRootConfiguration
import groovy.transform.CompileStatic
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@CompileStatic
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = [SpringRootConfiguration.class, SpringJobConfiguration.class])
abstract class BaseJobConfig {

    @BeforeAll
    static void setUpAll() {
        println 'initial setup for all test set'

    }



    @BeforeEach
    void setUp() {
        println 'initial setup for individual test'

    }

    @AfterEach
    void tearDown() {
        println 'cleanup after individual test'

    }



    @AfterAll
    static void teatrDowAlln() {
        println 'cleanup after tests in class'

    }

    static {
        EnviromentSetupHelper.loadEnvVariables()
    }

}
