package com.affirm.tests.webscrapper

import com.affirm.common.model.transactional.OnpeResult
import com.affirm.jobs.webscrapper.OnpeScrapper
import groovy.transform.CompileStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

@CompileStatic
class OnpeScrapperTest {
    OnpeScrapper scrapper

    @BeforeEach
    void setUp() {
        scrapper = new OnpeScrapper("fturconi", "ostk2004", 15, null)
    }

    @Test
    @Disabled
    void unpaidFinesFoundSuccess() {
        OnpeResult result = scrapper.getData("45065448")
        assertEquals 'ALLISON ELIZABETH VILLAR FLORES', result.fullName
        assertEquals 'SUFRAGIO', result.details[0].omissionType
    }

    @Test
    void NoUnpaidFinesFoundSuccess() {
        OnpeResult result = scrapper.getData("47437121")
        assertEquals null, result.fullName
        assertTrue result.details[0].message.contains('no cuenta con multas pendientes')
    }

    @AfterEach
    void tearDown() {
        scrapper.close()
    }
}

/*
Test setup - override :
Scrapper class :
    protected boolean localChromeDriver = true;
method setupDriver -> override
    System.setProperty("webdriver.chrome.driver", "/opt/chromedriver");

Configuration class :
    public static boolean hostEnvIsLocal() {return true;}
    public static boolean hostEnvIsNotLocal() {return false;}

*/
