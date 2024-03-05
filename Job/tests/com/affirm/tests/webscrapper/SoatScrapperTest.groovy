package com.affirm.tests.webscrapper


import com.affirm.common.model.transactional.SoatRecordsResult
import com.affirm.jobs.webscrapper.SoatRecordsScrapper
import com.affirm.tests.BaseJobConfig
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SoatScrapperTest extends BaseJobConfig{

    SoatRecordsScrapper scrapper

    @BeforeEach
    void setUp() {
        scrapper = new SoatRecordsScrapper("fturconi", "ostk2004", 15, null)
    }

    @Test
    void getSoatRecordsSuccess() {
        SoatRecordsResult soatRecordsResult = scrapper.getRecordsByReg(25, 'D3U281')
        println soatRecordsResult
        Assert.assertTrue soatRecordsResult != null;
    }

    @Test
    void getSoatRecordsNullOrFailedCaptchas() {
        SoatRecordsResult soatRecordsResult = scrapper.getRecordsByReg(25, '%%%%%%')
        Assert.assertTrue soatRecordsResult == null
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