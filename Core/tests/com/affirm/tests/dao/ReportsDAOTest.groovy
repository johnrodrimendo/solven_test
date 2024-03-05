package com.affirm.tests.dao

import com.affirm.common.dao.ReportsDAO
import com.affirm.common.model.DebtorsReport
import com.affirm.common.model.FunnelReportSection
import com.affirm.common.model.OperatorManagementReport
import com.affirm.common.model.ReportMonthsPeriod
import com.affirm.common.model.catalog.Employer
import com.affirm.common.model.transactional.*
import com.affirm.tests.BaseConfig
import org.apache.commons.lang3.tuple.Pair
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ReportsDAOTest extends BaseConfig {

    @Autowired
    ReportsDAO reportsDAO

    static final int REPORT_ID = 1
    static final int EMPLOYER_OR_GROUP_ID = 2
    static final boolean IS_GROUP = 3
    static final int REPORT_PROCESS_ID = 4
    static final Integer USER_ID = 5
    static final JSONObject PARAMS = null
    static final Character STATUS = 'A'
    static final String URL = "localhost:8080/reports"
    static final Date CURRENT_DATE = new Date()
    static final Date PROCESS_DATE = CURRENT_DATE
    static final Integer OFFSET = 10
    static final Integer LIMIT = 10
    static final int ENTITY_ID = 88888
    static final int ORIGIN = 12345
    static final Date START_DATE = CURRENT_DATE
    static final Date END_DATE = CURRENT_DATE
    static final Integer DATATYPE = 1
    static final JSONArray ENTITIES = new JSONArray()
    static final JSONArray PRODUCTS = null
    static final String SOURCE = ""
    static final String MEDIUM = ""
    static final String CAMPAIGN = ""
    static final JSONArray COUNTRIES = null
    static final String COUNTRY_ID = '["51"]'
    static final Date PERIOD1_FROM = CURRENT_DATE
    static final Date PERIOD1_TO = CURRENT_DATE
    static final Date PERIOD2_FROM = CURRENT_DATE
    static final Date PERIOD2_TO = CURRENT_DATE
    static final int SYS_USER_ID = 1002
    static final int PRODUCT_ID = 1002

    @Test
    void getRipleyReportFromReportsDAO() {
        List<RipleySefReport> result = reportsDAO.getRipleyReport(REPORT_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void getEmployerCreditCollectinDetailReportFromReportsDAO() {
        List<EmployerCreditsGatewayDetailReport> result = reportsDAO.getEmployerCreditCollectinDetailReport(
                EMPLOYER_OR_GROUP_ID, IS_GROUP)
        Assert.assertNull(result)
    }

    @Test
    void getEmployerCreditCollectinReportFromReportsDAO() {
        Pair<List<EmployerCreditsGatewayReport>, List<EmployerCreditsGatewayReport>> result =
                reportsDAO.getEmployerCreditCollectinReport(EMPLOYER_OR_GROUP_ID, IS_GROUP)
        Assert.assertNull(result)
    }

    @Test
    void getEmployersToSenEndOfMonthReportFromReportsDAO() {
        List<Employer> result = reportsDAO.getEmployersToSenEndOfMonthReport()
        Assert.assertNull(result)
    }

    @Test
    void getPendingDisbursementConsolidationReportReportsDAO() {
        List<PendingDisbursementConsolidationReportDetail> result = reportsDAO.getPendingDisbursementConsolidationReport()
        Assert.assertNull(result)
    }

    @Test
    void getReportProcesFromReportsDAO() {
        ReportProces result = reportsDAO.getReportProces(REPORT_PROCESS_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void registerReportProcesReportsDAO() {
        Integer result = reportsDAO.registerReportProces(REPORT_ID, USER_ID, PARAMS)
        Assert.assertNotNull(result)
    }

    @Test
    void updateReportProcesStatusFromReportsDAO() {
        reportsDAO.updateReportProcesStatus(REPORT_PROCESS_ID, STATUS)
    }

    @Test
    void updateReportProcesUrlFromReportsDAO() {
        reportsDAO.updateReportProcesUrl(REPORT_PROCESS_ID, URL)
    }

    @Test
    void updateReportProcesProcessDateFromReportsDAO() {
        reportsDAO.updateReportProcesProcessDate(REPORT_PROCESS_ID, PROCESS_DATE)
    }

    @Test
    void getReportProcesHistoricFromReportsDAO() {
        List<ReportProces> result = reportsDAO.getReportProcesHistoric(REPORT_ID, OFFSET, LIMIT)
        Assert.assertNotNull(result)
    }

    @Test
    void getFunnelReportProcesHistoricFromReportsDAO() {
        List<ReportProces> result = reportsDAO.getFunnelReportProcesHistoric(ORIGIN, REPORT_ID, ENTITY_ID, OFFSET, LIMIT)
        Assert.assertNotNull(result)
    }

    @Test
    void getFunnelReportFromReportsDAO() {
        List<FunnelReportSection> result = reportsDAO.getFunnelReport(START_DATE, END_DATE, DATATYPE, ENTITIES, PRODUCTS,
                SOURCE, MEDIUM, CAMPAIGN, COUNTRIES)
        Assert.assertNotNull(result)
    }

    @Test
    void getFunnelMarketplaceBrandedReportFromReportsDAO() {
        List<FunnelReportSection> result = reportsDAO.getFunnelMarketplaceBrandedReport(START_DATE, END_DATE, DATATYPE,
                ENTITIES, PRODUCTS, SOURCE, MEDIUM, CAMPAIGN, COUNTRIES)
        Assert.assertNotNull(result)
    }

    @Test
    void getReportPeriodsFromReportsDAO() {
        List<ReportMonthsPeriod> result = reportsDAO.getReportPeriods()
        Assert.assertNotNull(result)
    }

    @Test
    void getOperatorManagementReportReportsDAO() {
        OperatorManagementReport result = reportsDAO.getOperatorManagementReport(COUNTRY_ID, PERIOD1_FROM, PERIOD1_TO,
                PERIOD2_FROM, PERIOD2_TO, SYS_USER_ID, ENTITY_ID, PRODUCT_ID)
        Assert.assertNotNull(result)
    }

    @Test
    void getDebtorsReportFromReportsDAO() {
        List<DebtorsReport> result = reportsDAO.getDebtorsReport()
        Assert.assertNull(result)
    }

}
