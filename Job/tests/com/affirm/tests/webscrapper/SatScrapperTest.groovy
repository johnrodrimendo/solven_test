package com.affirm.tests.webscrapper


import com.affirm.common.model.catalog.IdentityDocumentType
import com.affirm.common.model.transactional.SatResult
import com.affirm.jobs.webscrapper.SatScrapper
import groovy.transform.CompileStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull

@CompileStatic
class SatScrapperTest {


    SatScrapper scrapper


    @BeforeEach
    void setUp() {
        scrapper = new SatScrapper("fturconi", "ostk2004", 15, null)
    }

    @Test
    @Disabled
    void produceReportWithTwoFieldsOneIsBlank() {
        /*SatResult satResult = scrapper.getData(IdentityDocumentType.DNI, "45173239");

        assertEquals 'ABANTO TERRONES JOSE MICHAEL', satResult.satIdReport[0].taxpayer
        assertNotNull satResult.satIdReport[0].annualReports*/
    }

    @Test
    void getTicketsSuccessForLimaDistrict() {
        scrapper.getTicketsByRegInLima(25, 'D3U281')
    }

    @Test
    @Disabled
    void getTicketsNoRecordsForLimaDistrict() {
        scrapper.getTicketsByRegInLima(25, 'XXXXXX')
    }

    @AfterEach
    void tearDown() {
        scrapper.close()
    }
}

/*  M1K737  (9 multas)
Test setup - override :
Scrapper class :
    protected boolean localChromeDriver = true;
method setupDriver -> override
    System.setProperty("webdriver.chrome.driver", "/opt/chromedriver");

Configuration class :
    public static boolean hostEnvIsLocal() {return true;}
    public static boolean hostEnvIsNotLocal() {return false;}

*/
