package com.affirm.tests.system.configuration.dao

import com.affirm.backoffice.dao.BackofficeDAO
import com.affirm.backoffice.model.GeneralSearchResult
import com.affirm.backoffice.model.OriginationReportPeriod
import com.affirm.backoffice.model.ReportCreditGateway
import com.affirm.backoffice.model.ReportNoHolding
import com.affirm.backoffice.model.ScreenTrackReport
import com.affirm.backoffice.util.PaginationWrapper
import com.affirm.common.service.CatalogService
import com.affirm.tests.BaseBoConfig
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class BackofficeDAOTest extends BaseBoConfig {

    @Autowired
    BackofficeDAO backofficeDAO

    @Autowired
    CatalogService catalogService;

    static final Locale LOCALE = Locale.US
    static final String QUERY = null
    static final String USERNAME = "occoa"
    static final Integer OFFSET = 10
    static final Integer LIMIT = 10
    static final String COUNTRY = "51"
    static final Date CURRENT_DATE = new Date()
    static final Date START_DATE = CURRENT_DATE
    static final Date END_DATE = CURRENT_DATE
    static final Integer ENTITY_PRODUCT_PARAMETER_ID = 12345
    static final Integer SPEECH_ID = 99999
    static final String SPEECH = "Speech"
    static final Date PERIOD1_FROM = CURRENT_DATE
    static final Date PERIOD1_TO = CURRENT_DATE
    static final Date PERIOD2_FROM = CURRENT_DATE
    static final Date PERIOD2_TO = CURRENT_DATE
    static final Integer ENTITY_ID = 55555
    static final Integer[] ENTITIES = [ENTITY_ID]
    static final Integer DISBURSEMENT_TYPE = 1
    static final Integer[] DISBURSEMENT_TYPES = [DISBURSEMENT_TYPE]
    static final Integer DATA_TYPE = 10000
    static final String COUNTRY_JSON_ARRAY = '["12345"]'
    static final Integer LOAN_APP_STATUS = 88888
    static final Integer PRODUCT_CATEGORY = 66666
    static final Integer[] LOAN_APP_STATUS_ARRAY = [LOAN_APP_STATUS]
    static final Integer[] PRODUCT_CATEGORY_ARRAY = [PRODUCT_CATEGORY]

    @Test
    void generalSearchFromBackofficeDAO() {
        GeneralSearchResult generalSearchResult = backofficeDAO.generalSearch(QUERY, LOCALE)
        Assert.assertNotNull(generalSearchResult)
    }

    @Test
    void getSharedSecretFromBackofficeDAO() {
        String string = backofficeDAO.getSharedSecret(USERNAME)
        Assert.assertNull(string)
    }

    @Test
    void getNoHoldingReportFromBackofficeDAO() {
        PaginationWrapper<ReportNoHolding> paginationWrapper = backofficeDAO.getNoHoldingReport(COUNTRY, OFFSET, LIMIT)
        Assert.assertNotNull(paginationWrapper)
    }

    @Test
    void getCreditCollectionReportFromBackofficeDAO() {
        List<ReportCreditGateway> list = backofficeDAO.getCreditCollectionReport(COUNTRY_JSON_ARRAY, catalogService, LOCALE, START_DATE, END_DATE)
        Assert.assertNull(list)
    }

    @Test
    void getCreditCollectionReport2FromBackofficeDAO() {
        List<ReportCreditGateway> list = backofficeDAO.getCreditCollectionReport(COUNTRY_JSON_ARRAY, catalogService, LOCALE, START_DATE, END_DATE, OFFSET, LIMIT)
        Assert.assertNull(list)
    }

    @Test
    void updateSpeechFromBackofficeDAO() {
        backofficeDAO.updateSpeech(ENTITY_PRODUCT_PARAMETER_ID, SPEECH_ID, SPEECH)
    }

    @Test
    void getOriginationProductReportPeriodFromBackofficeDAO() {
        List<OriginationReportPeriod> list = backofficeDAO.getOriginationProductReportPeriod(
                COUNTRY_JSON_ARRAY, PERIOD1_FROM, PERIOD1_TO, PERIOD2_FROM, PERIOD2_TO, ENTITIES)
        Assert.assertNotNull(list)
    }

    @Test
    void getOriginationEntityProductReportPeriodFromBackofficeDAO() {
        List<OriginationReportPeriod> list = backofficeDAO.getOriginationEntityProductReportPeriod(COUNTRY_JSON_ARRAY, catalogService)
        Assert.assertNull(list)
    }

    @Test
    void getApplicationProcessPathOrderReportFromBackofficeDAO() {
        List<ScreenTrackReport> list = backofficeDAO.getApplicationProcessPathOrderReport(COUNTRY_JSON_ARRAY, START_DATE, END_DATE, LOCALE)
        Assert.assertNull(list)
    }

    @Test
    void getApplicationProcessPathTimeReportFromBackofficeDAO() {
        List<ScreenTrackReport> list = backofficeDAO.getApplicationProcessPathTimeReport(START_DATE, END_DATE, LOCALE)
        Assert.assertNotNull(list)
    }

    @Test
    void getApplicationProcessReportFromBackofficeDAO() {
        List<ScreenTrackReport> list = backofficeDAO.getApplicationProcessReport(COUNTRY_JSON_ARRAY, START_DATE, END_DATE,
                LOAN_APP_STATUS_ARRAY, PRODUCT_CATEGORY_ARRAY, LOCALE)
        Assert.assertNull(list)
    }
}
