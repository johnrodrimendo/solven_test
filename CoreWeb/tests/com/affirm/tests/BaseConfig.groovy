package com.affirm.tests

import com.affirm.common.util.EnviromentSetupHelper
import com.affirm.system.configuration.SpringRootConfiguration
import com.affirm.system.configuration.SpringWebConfiguration
import groovy.transform.CompileStatic
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration

@CompileStatic
@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = [SpringRootConfiguration.class, SpringWebConfiguration.class])
abstract class BaseConfig {


    @BeforeAll
    static void setUpAll() {
        println 'initial setup for all test set'

    }

    @BeforeClass
    static void setUpClass() {
        println 'initial setup for tests in class'

    }

    @Before
    void setUp() {
        println 'initial setup for individual test'

    }

    @After
    void tearDown() {
        println 'cleanup after individual test'

    }

    @AfterClass
    static void tearDownClass() {
        println 'cleanup after set sets in class'

    }

    @AfterAll
    static void teatrDowAlln() {
        println 'cleanup after tests in class'

    }


    static {
        EnviromentSetupHelper.loadEnvVariables()
    }

}
