package com.affirm.common.service;

import com.affirm.common.model.EntityErrorExtranetPainter;
import com.affirm.common.model.transactional.*;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by john on 21/10/16.
 */
public interface ReportsService {
    byte[] createRipleyReportExcel() throws Exception;

    byte[] endMonthCompanyResume(int employerOrGroupId, boolean isGroup) throws Exception;

    byte[] createProsegurPendingDisbursementConsolidationReport(Locale locale) throws Exception;

    byte[] createLoanReport(Date startDate, Date endDate, String countries) throws Exception;

    byte[] createLoanLightReport(Date startDate, Date endDate, Integer[] countryIds, Integer[] entityIds) throws Exception;

    byte[] createLoanLightReport(Integer minAge, Integer maxAge, String requestType, String cardType, Date startDate, Date endDate, Date startDate2, Date endDate2, Integer[] countryIds, Integer[] entityIds, Integer[] products) throws Exception;

    byte[] createOriginationReport(Date startDate, Date endDate, String country, String symbol, boolean internationalCurrency) throws Exception;

    Pair<byte[], String> processReport(ReportProces reportProces) throws Exception;

    ReportProces createReporteSolicitudesBo(Integer userId, Date startDate, Date endDate, String country) throws Exception;

    ReportProces createReporteSolicitudesLight(Integer userId, Date startDate, Date endDate, Integer[] countries, Integer[] entities, int origin) throws Exception;

    ReportProces createReporteSolicitudesLight(Integer userId, Integer minAge, Integer maxAge, String requestType, String cardType, Date startDate, Date endDate, Date startDate2, Date endDate2, Integer[] countries, Integer[] entities, Integer[] products, int origin) throws Exception;

    ReportProces createReporteOriginacionesBo(Integer userId, Date startDate, Date endDate, String countries, String symbol, boolean internationalCurrency) throws Exception;

    String createReportDownloadUrl(int reportProcesId) throws Exception;

    byte[] createLoanDetailReport() throws Exception;

    byte[] createApproveCreditsReport(List<Credit> credits) throws Exception;

    byte[] createFunnelReport(Date startDate, Date endDate, Integer dateType, String entities, String products, String source, String medium, String campaign, Integer origin, String countries) throws Exception;

    ReportProces createReporteFunnelBo(Integer userId, Date startDate, Date endDate, Integer dateType, Integer[] entities, Integer[] products, String source, String medium, String campaign, int origin, Integer[] countries) throws Exception;

    ReportProces createReporteGestionOperadoresBO(Integer userId, String countryId, Date period1From, Date period1To, Date period2From, Date period2To, Integer sysUserId, Integer entityId, Integer productId, String symbol) throws Exception;

    ReportProces createReportDebtorConsolidation(Integer userId) throws Exception;

    ReportProces createReportPantallas(Integer userId, String countryId, Date startDate, Date endDate, Integer[] products, Integer[] statuses) throws Exception;

    ReportProces createReportPantallasRecorridas(Integer userId, String countryId, Date startDate, Date endDate) throws Exception;

    ReportProces createReportSolicitudesEnProceso(Integer userId, String countryId, Integer documentType, String documentNumber, Integer[] producers, Date startDate, Date endDate, Integer[] loanStatuses) throws Exception;

    ReportProces createReportCreditosADesembolsar(Integer userId, String countryId, Integer documentType, String documentNumber, Integer[] producers, Date startDate, Date endDate) throws Exception;

    ReportProces createReportCreditosDesembolsados(Integer userId, String countryId, Integer documentType, String documentNumber, Integer[] producers, Date startDate, Date endDate, String[] internalStatuses, Date disbursementStartDate, Date disbursementEndDate) throws Exception;

    ReportProces createReportRiesgos(Integer userId, String countryId, Integer documentType, String documentNumber, Integer[] producers, Date startDate, Date endDate, Integer[] loanStatuses, String[] internalStatuses, Integer[] creditStatuses, Date lastExecutionStartDate, Date lastExecutionEndDate) throws Exception;

    byte[] createOperatorsManagementsReport(String countryId, Date period1From, Date period1To, Date period2From, Date period2To, Integer sysUserId, Integer entityId, Integer productId, String symbol) throws Exception;

    byte[] createDebtConsolidationReport() throws Exception;

    ReportProces createReporteFunnelDashboardBo(Integer userId, Integer dateType, Date period1From, Date period1To, Date period2From, Date period2To, Integer[] countryId, Integer entityId, Integer[] disbursementType) throws Exception;

    byte[] createReporteFunnelDashBoardBo(Date period1From, Date period1To, Date period2From, Date period2To, Integer entityId, String disbursementTypes, Integer dateType, String countryId) throws Exception;

    byte[] createScreenReport(String countryId, Date startDate, Date endDate, Integer[] products, Integer[] statuses, Locale locale) throws Exception;

    byte[] createTrackScreenReport(String countryId, Date startDate, Date endDate, Locale locale) throws Exception;

    byte[] createLoanInProcessReport(Date registerDateReport, Date processDateReport, String countryId, Integer documentType, String documentNumber, Integer[] producersIds, String producersNameConcat, Date startDate, Date endDate, Integer[] statusId) throws Exception;

    byte[] createCreditToDisburseReport(Date registerDateReport, Date processDateReport, String countryId, Integer documentType, String documentNumber, Integer[] producersIds, String producersNameConcat, Date startDate, Date endDate) throws Exception;

    byte[] createDisbursedCreditReport(Date registerDateReport, Date processDateReport, String countryId, Integer documentType, String documentNumber, Integer[] producersIds, String producersNameConcat, Date startDate, Date endDate, JSONArray internalStatusId, Date disbursementStartDate, Date disbursementEndDate) throws Exception;

    byte[] createRiskReport(Integer documentType, String documentNumber, Integer[] producers, Date startDate, Date endDate, Integer[] loanStatuses, JSONArray internalStatuses, Integer[] creditStatuses, Date lastExecutionStartDate, Date lastExecutionEndDate) throws Exception;

    byte[] createPersoninteractionFollowUpReports(String event, JSONArray personInteractionIds) throws Exception;

    ReportProces createReportSolicitudesEnProcesoFDLM(Integer userId, String countryId, Integer documentType, String documentNumber, String lastname, Date startDate, Date endDate, Date updatedStartDate, Date updatedEndDate, Integer[] loanStatuses) throws Exception;

    byte[] createLoanInProcessReportFDLM(Date registerDateReport, Date processDateReport, String countryId, Integer documentType, String documentNumber, String lastname, Date startDate, Date endDate, Date updatedStartDate, Date updatedEndDate, Integer[] statusId) throws Exception;

    byte[] createBanBifLeadsReport(List<BanbifTcLeadLoan> data, String sheetname);

    byte[] createEntityErrorNotificationDetail(EntityErrorExtranetPainter entityError, EntityWebServiceLog entityWebServiceLog) throws Exception;

    byte[] createCreditsReport(List<CreditEntityExtranetPainter> credits, String sheetname) throws Exception;

    ReportProces createReporteFunnelV3(Integer userId, Integer minAge, Integer maxAge, String requestType, String cardType, Date startDate, Date endDate, Date startDate2, Date endDate2, Integer[] countries, Integer[] entities, Integer[] products,Integer[] steps, Integer base, int origin) throws Exception;

    ReportProces createReport(Integer[] loanIds, Integer entityId, int userId, Integer report, Integer tray, Date startDate, Date endDate, String search,Integer[] products,Integer[] steps, Boolean isPaymentCommitment, Boolean isRejectedTrayReport) throws Exception;

    byte[] createTrayExtranetReport(Integer entity,Integer[] loanIds, Integer[] products, Integer[] steps) throws Exception;

    byte[] createRejectedTrayReport(Integer entity,Integer[] loanIds, Integer[] products, Integer[] steps) throws Exception;

    byte[] createTrayExtranetReportPaymentCommitment(Integer entity,Integer[] loanIds, Integer[] products, Integer[] steps) throws Exception;

    ReportProces createReporteFunnelV3(Integer userId, Integer minAge, Integer maxAge, String requestType, String cardType, Date startDate, Date endDate, Date startDate2, Date endDate2, Integer[] countries, Integer[] entities, Integer[] products,Integer[] steps, Integer base, int origin, Integer customReportId,List<String> utmSources,List<String> utmMedium,List<String> utmCampaign,List<String> utmContent,List<Integer> entityProductParams) throws Exception;

    byte[] createFunnelV3Report(Integer minAge, Integer maxAge, String requestType, String cardType, Date startDate, Date endDate, Date startDate2, Date endDate2, Integer[] countryIds, Integer[] entityIds, Integer[] products,Integer[] steps, List<String> utmSources,List<String> utmMedium,List<String> utmCampaign,List<String> utmContent,List<Integer> entityProductParams) throws Exception;
}
