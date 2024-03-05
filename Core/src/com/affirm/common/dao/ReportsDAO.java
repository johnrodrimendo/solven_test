/**
 *
 */
package com.affirm.common.dao;

import com.affirm.bancodelsol.model.DisburseCreditReport;
import com.affirm.bancodelsol.model.DisbursedCreditReport;
import com.affirm.bancodelsol.model.LoanApplicationInProcessReport;
import com.affirm.common.model.*;
import com.affirm.common.model.catalog.Employer;
import com.affirm.common.model.transactional.*;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */
public interface ReportsDAO {
    List<RipleySefReport> getRipleyReport(int reportId) throws Exception;

    List<EmployerCreditsGatewayDetailReport> getEmployerCreditCollectinDetailReport(int employerOrGroupId, boolean isGroup) throws Exception;

    Pair<List<EmployerCreditsGatewayReport>, List<EmployerCreditsGatewayReport>> getEmployerCreditCollectinReport(int employerOrGroupId, boolean isGroup) throws Exception;

    List<Employer> getEmployersToSenEndOfMonthReport() throws Exception;

    List<PendingDisbursementConsolidationReportDetail> getPendingDisbursementConsolidationReport() throws Exception;

    ReportProces getReportProces(int reportProcesId);

    Integer registerReportProces(int reportId, Integer userId, JSONObject params);

    void updateReportProcesStatus(int reportProcesId, Character status);

    void updateReportProcesUrl(int reportProcesId, String url);

    void updateReportProcesProcessDate(int reportProcesId, Date processDate);

    List<ReportProces> getReportProcesHistoric(int reportId, int offset, int limit);

    List<ReportProces> getFunnelReportProcesHistoric(int origin, int reportId, Integer entityId, int offset, int limit);

    List<FunnelReportSection> getFunnelReport(Date startDate, Date endDate, Integer dateType, JSONArray entities, JSONArray products, String source, String medium, String campaign, JSONArray countries) throws Exception;

    List<FunnelReportSection> getFunnelMarketplaceBrandedReport(Date startDate, Date endDate, Integer dateType, JSONArray entities, JSONArray products, String source, String medium, String campaign, JSONArray countries) throws Exception;

    List<ReportMonthsPeriod> getReportPeriods() throws Exception;

    OperatorManagementReport getOperatorManagementReport(String countryId, Date period1From, Date period1To, Date period2From, Date period2To, Integer sysUserId, Integer entityId, Integer productId) throws Exception;

    List<DebtorsReport> getDebtorsReport() throws Exception;

    List<FunnelDashboardSection> getFunnelDashboard(Date period1From, Date period1To, Date period2From, Date period2To, Integer entity, String disbursementTypes, Integer dateType, String countryId) throws Exception;

    List<ScreenTrackReport> getApplicationProcessPathOrderReport(String country, Date from, Date until, Locale locale) throws Exception;

    List<ScreenTrackReport> getApplicationProcessPathTimeReport(Date from, Date until, Locale locale) throws Exception;

    List<ScreenReport> getApplicationProcessReport(String country, Date from, Date until, Integer[] loanAppStatus, Integer[] productCategories, Locale locale) throws Exception;

    List<LoanApplicationInProcessReport> getLoanApplicationInProcessReportBDS(Integer documentType, String documentNumber, Integer[] producers, Date from, Date until, Integer[] loanApplicationStatuses) throws Exception;

    List<DisburseCreditReport> getDisburseCreditReportBDS(Integer documentType, String documentNumber, Integer[] producers, Date from, Date until) throws Exception;

    List<DisbursedCreditReport> getDisbursedCreditReportBDS(Integer documentType, String documentNumber, Integer[] producers, Date from, Date until, JSONArray internalStatuses, Date disbursementStartDate, Date disbursementEndDate) throws Exception;

    JSONArray getRiskReportBDS(Integer documentType, String documentNumber, Integer[] producers, Date from, Date until, Integer[] creditSubStatuses, JSONArray internalStatuses, Integer[] applicationStatuses, Date lastExecutionStartDate, Date lastExecutionEndDate) throws Exception;

    List<ReportProces> getReportProcesHistoric(int userId, int reportId, int offset, int limit);

    List<ReportProces> getReportProcesHistoric(int userId, int reportId, int offset, int limit, int origin);

    List<PersonInteractionFollowUpReport> getPersoninteractionFollowUpReports(String jsonParam);

    List<com.affirm.fdlm.report.LoanApplicationInProcessReport> getLoanApplicationInProcessReportFDLM(Integer documentType, String documentNumber, String lastname, Date from, Date until, Date updatedFrom, Date updatedUntil, Integer[] loanApplicationStatuses) throws Exception;

    FunnelReport getBanbifFunnelReport(Integer minAge, Integer maxAge, String requestType, String cardType, Date fromDate, Date toDate, String abTesting, List<Integer> products) throws Exception;

    FunnelReport getFunnelV3ReportData(Integer entity, Integer minAge, Integer maxAge, String requestType, String cardType, Date fromDate, Date toDate, String abTesting, List<Integer> products, Integer base, List<Integer> steps) throws Exception;

    FunnelReport getFunnelV3ReportData(Integer entity,Integer minAge, Integer maxAge, String requestType, String cardType, Date fromDate, Date toDate, String abTesting, List<Integer> products, Integer base, List<Integer> steps,List<String> utmSources,List<String> utmMedium,List<String> utmCampaign,List<String> utmContent,List<Integer> entityProductParams) throws Exception;
}
