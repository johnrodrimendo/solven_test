/**
 *
 */
package com.affirm.common.dao.impl;

import com.affirm.bancodelsol.model.DisburseCreditReport;
import com.affirm.bancodelsol.model.DisbursedCreditReport;
import com.affirm.bancodelsol.model.LoanApplicationInProcessReport;
import com.affirm.common.dao.JsonResolverDAO;
import com.affirm.common.dao.ReportsDAO;
import com.affirm.common.model.*;
import com.affirm.common.model.catalog.Employer;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author jrodriguez
 */

@Repository
public class ReportsDAOImpl extends JsonResolverDAO implements ReportsDAO {


    @Autowired
    private CatalogService catalogService;

    @Override
    public List<RipleySefReport> getRipleyReport(int reportId) throws Exception {
        JSONArray jarray = queryForObject("select * from credit.get_ripley_sef_report(?)", JSONArray.class, false, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, reportId));
        if (jarray == null) {
            return null;
        }

        List<RipleySefReport> reports = new ArrayList<>();
        for (int i = 0; i < jarray.length(); i++) {
            RipleySefReport report = new RipleySefReport();
            report.fillFromDb(jarray.getJSONObject(i), catalogService);
            reports.add(report);
        }
        return reports;
    }

    @Override
    public List<EmployerCreditsGatewayDetailReport> getEmployerCreditCollectinDetailReport(int employerOrGroupId, boolean isGroup) throws Exception {
        JSONArray jarray = queryForObject("select * from credit.employer_credits_collection_detail(?, ?)", JSONArray.class, false, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, employerOrGroupId),
                new SqlParameterValue(Types.BOOLEAN, isGroup));
        if (jarray == null) {
            return null;
        }

        List<EmployerCreditsGatewayDetailReport> reports = new ArrayList<>();
        for (int i = 0; i < jarray.length(); i++) {
            EmployerCreditsGatewayDetailReport report = new EmployerCreditsGatewayDetailReport();
            report.fillFromDb(jarray.getJSONObject(i), catalogService);
            reports.add(report);
        }
        return reports;
    }

    @Override
    public Pair<List<EmployerCreditsGatewayReport>, List<EmployerCreditsGatewayReport>> getEmployerCreditCollectinReport(int employerOrGroupId, boolean isGroup) throws Exception {
        JSONObject dbJson = queryForObject("select * from credit.employer_credits_collection(?, ?)", JSONObject.class, false, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, employerOrGroupId),
                new SqlParameterValue(Types.BOOLEAN, isGroup));
        if (dbJson == null) {
            return null;
        }

        if (JsonUtil.getJsonArrayFromJson(dbJson, "js_adelantos", null) != null &&
                JsonUtil.getJsonArrayFromJson(dbJson, "js_otros", null) != null) {

            List<EmployerCreditsGatewayReport> adelantos = new ArrayList<>();
            JSONArray array = JsonUtil.getJsonArrayFromJson(dbJson, "js_adelantos", null);
            for (int i = 0; i < array.length(); i++) {
                EmployerCreditsGatewayReport adelanto = new EmployerCreditsGatewayReport();
                adelanto.fillFromDb(array.getJSONObject(i), catalogService);
                adelantos.add(adelanto);
            }

            List<EmployerCreditsGatewayReport> otros = new ArrayList<>();
            array = JsonUtil.getJsonArrayFromJson(dbJson, "js_otros", null);
            for (int i = 0; i < array.length(); i++) {
                EmployerCreditsGatewayReport otro = new EmployerCreditsGatewayReport();
                otro.fillFromDb(array.getJSONObject(i), catalogService);
                otros.add(otro);
            }

            return Pair.of(adelantos, otros);
        }
        return null;
    }

    @Override
    public List<Employer> getEmployersToSenEndOfMonthReport() throws Exception {
        JSONArray dbJson = queryForObjectTrx("select * from support.must_send_employer_end_of_month()", JSONArray.class, false);
        if (dbJson == null) {
            return null;
        }

        List<Employer> employers = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            employers.add(catalogService.getEmployer(dbJson.getInt(i)));
        }
        return employers;
    }

    @Override
    public List<PendingDisbursementConsolidationReportDetail> getPendingDisbursementConsolidationReport() throws Exception {
        JSONArray jarray = queryForObject("select * from credit.bo_get_pending_disbursement_consolidation()", JSONArray.class, false, READABLE_DB);
        if (jarray == null) {
            return null;
        }

        List<PendingDisbursementConsolidationReportDetail> reports = new ArrayList<>();
        for (int i = 0; i < jarray.length(); i++) {
            PendingDisbursementConsolidationReportDetail report = new PendingDisbursementConsolidationReportDetail();
            report.fillFromDb(jarray.getJSONObject(i), catalogService);
            reports.add(report);
        }
        return reports;
    }

    @Override
    public ReportProces getReportProces(int reportProcesId) {
        JSONObject dbJson = queryForObjectTrx("select * from support.get_generated_report_by_id(?)", JSONObject.class,
                new SqlParameterValue(Types.INTEGER, reportProcesId));
        if (dbJson == null) {
            return null;
        }

        ReportProces reportProces = new ReportProces();
        reportProces.fillFromDb(dbJson);
        return reportProces;
    }

    @Override
    public Integer registerReportProces(int reportId, Integer userId, JSONObject params) {
        return queryForObjectTrx("select * from support.register_report(?, ?, ?);", Integer.class,
                new SqlParameterValue(Types.INTEGER, reportId),
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.OTHER, params != null ? params.toString() : null));
    }

    @Override
    public void updateReportProcesStatus(int reportProcesId, Character status) {
        updateTrx("UPDATE support.tb_generated_reports set status = ? where generated_report_id = ?",
                new SqlParameterValue(Types.CHAR, status),
                new SqlParameterValue(Types.INTEGER, reportProcesId));
    }

    @Override
    public void updateReportProcesUrl(int reportProcesId, String url) {
        updateTrx("UPDATE support.tb_generated_reports set url = ? where generated_report_id = ?",
                new SqlParameterValue(Types.VARCHAR, url),
                new SqlParameterValue(Types.INTEGER, reportProcesId));
    }

    @Override
    public void updateReportProcesProcessDate(int reportProcesId, Date processDate) {
        updateTrx("UPDATE support.tb_generated_reports set process_date = ? where generated_report_id = ?",
                new SqlParameterValue(Types.TIMESTAMP, processDate),
                new SqlParameterValue(Types.INTEGER, reportProcesId));
    }

    @Override
    public List<ReportProces> getReportProcesHistoric(int reportId, int offset, int limit) {
        JSONArray dbJson = queryForObjectTrx("select * from support.get_historical_reports_by_report_id(?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, reportId),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit));
        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<ReportProces> reports = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            ReportProces reportProces = new ReportProces();
            reportProces.fillFromDb(dbJson.getJSONObject(i));
            reports.add(reportProces);
        }
        return reports;
    }

    @Override
    public List<ReportProces> getFunnelReportProcesHistoric(int origin, int reportId, Integer entityId, int offset, int limit) {
        JSONArray dbJson = queryForObjectTrx("select * from support.get_historical_funnel_report(?, ?, ?, ?, ?)", JSONArray.class, false,
                new SqlParameterValue(Types.INTEGER, origin),
                new SqlParameterValue(Types.INTEGER, reportId),
                new SqlParameterValue(Types.INTEGER, entityId),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit));
        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<ReportProces> reports = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            ReportProces reportProces = new ReportProces();
            reportProces.fillFromDb(dbJson.getJSONObject(i));
            reports.add(reportProces);
        }
        return reports;
    }

    @Override
    public List<FunnelReportSection> getFunnelReport(Date startDate, Date endDate, Integer dateType, JSONArray entities, JSONArray products, String source, String medium, String campaign, JSONArray countries) throws Exception {
        JSONArray jsonArray = queryForObjectTrx("select * from credit.rp_funnel_dashboard_v2(?, ?, ?, ?::JSON, ?::JSON, ?, ?, ?, ?);", JSONArray.class, false,
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.INTEGER, dateType),
                new SqlParameterValue(Types.VARCHAR, (entities != null && entities.length() > 0) ? entities.toString() : null),
                new SqlParameterValue(Types.VARCHAR, (products != null && products.length() > 0) ? products.toString() : null),
                new SqlParameterValue(Types.VARCHAR, source),
                new SqlParameterValue(Types.VARCHAR, medium),
                new SqlParameterValue(Types.VARCHAR, campaign),
                new SqlParameterValue(Types.OTHER, (countries != null && countries.length() > 0) ? countries.toString() : null));

        if (jsonArray == null) {
            return null;
        }

        List<FunnelReportSection> sections = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); ++i) {
            FunnelReportSection section = new FunnelReportSection();
            section.fillFromDb(jsonArray.getJSONObject(i));
            sections.add(section);
        }

        return sections;
    }

    @Override
    public List<FunnelReportSection> getFunnelMarketplaceBrandedReport(Date startDate, Date endDate, Integer dateType, JSONArray entities, JSONArray products, String source, String medium, String campaign, JSONArray countries) throws Exception {
        JSONArray jsonArray = queryForObject("select * from credit.rp_funnel_dashboard_v2_mkp_branded(?, ?, ?, ?::JSON, ?::JSON, ?, ?, ?, ?);", JSONArray.class, false, READABLE_DB,
                new SqlParameterValue(Types.DATE, startDate),
                new SqlParameterValue(Types.DATE, endDate),
                new SqlParameterValue(Types.INTEGER, dateType),
                new SqlParameterValue(Types.VARCHAR, entities.toString()),
                new SqlParameterValue(Types.VARCHAR, (products != null && products.length() > 0) ? products.toString() : null),
                new SqlParameterValue(Types.VARCHAR, source),
                new SqlParameterValue(Types.VARCHAR, medium),
                new SqlParameterValue(Types.VARCHAR, campaign),
                new SqlParameterValue(Types.OTHER, (countries != null && countries.length() > 0) ? countries.toString() : null));

        if (jsonArray == null) {
            return null;
        }

        List<FunnelReportSection> sections = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); ++i) {
            FunnelReportSection section = new FunnelReportSection();
            if (!"null".equals(jsonArray.get(i).toString())) {
                section.fillFromDb(jsonArray.getJSONObject(i));
            }
            sections.add(section);
        }

        return sections;
    }

    @Override
    public List<ReportMonthsPeriod> getReportPeriods() throws Exception {
        JSONArray jsonArray = queryForObjectTrx("select * from credit.rp_get_funnel_report_periods()", JSONArray.class, false);

        if (jsonArray == null) {
            return null;
        }

        List<ReportMonthsPeriod> periods = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); ++i) {
            ReportMonthsPeriod period = new ReportMonthsPeriod();
            period.fillFromDb(jsonArray.getJSONObject(i));
            periods.add(period);
        }

        return periods;
    }

    @Override
    public OperatorManagementReport getOperatorManagementReport(String countryId, Date period1From, Date period1To, Date period2From, Date period2To, Integer sysUserId, Integer entityId, Integer productId) throws Exception {
        JSONObject dbJson = queryForObject("SELECT * from credit.rp_tracking_dashboard(?, ?, ?, ?, ?, ?, ?, ?)", JSONObject.class, false, READABLE_DB,
                new SqlParameterValue(Types.OTHER, countryId),
                new SqlParameterValue(Types.INTEGER, sysUserId),
                new SqlParameterValue(Types.DATE, period1From),
                new SqlParameterValue(Types.DATE, period1To),
                new SqlParameterValue(Types.DATE, period2From),
                new SqlParameterValue(Types.DATE, period2To),
                new SqlParameterValue(Types.INTEGER, productId),
                new SqlParameterValue(Types.INTEGER, entityId));
        if (dbJson == null) {
            return null;
        }

        OperatorManagementReport report = new OperatorManagementReport();
        report.fillFromDb(dbJson);
        return report;
    }

    @Override
    public List<DebtorsReport> getDebtorsReport() throws Exception {
        JSONArray jsonArray = queryForObjectTrx("select * from credit.get_rcd_report()", JSONArray.class, false);

        if (jsonArray == null) {
            return null;
        }

        List<DebtorsReport> reports = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); ++i) {
            DebtorsReport debtorsReport = new DebtorsReport();
            debtorsReport.fillFromDb(jsonArray.getJSONObject(i));
            reports.add(debtorsReport);
        }
        return reports;
    }

    @Override
    public List<FunnelDashboardSection> getFunnelDashboard(Date period1From, Date period1To, Date period2From, Date period2To, Integer entity, String disbursementTypes, Integer dateType, String countryId) throws Exception {
        JSONArray dbArray = queryForObject("select * from credit.rp_funnel_dashboard(?,?,?,?,?,?::JSON, null, null, null, ?::JSON, ?)", JSONArray.class, false, READABLE_DB,
                new SqlParameterValue(Types.DATE, period1From),
                new SqlParameterValue(Types.DATE, period1To),
                new SqlParameterValue(Types.DATE, period2From),
                new SqlParameterValue(Types.DATE, period2To),
                new SqlParameterValue(Types.INTEGER, entity),
                new SqlParameterValue(Types.OTHER, disbursementTypes),
                new SqlParameterValue(Types.OTHER, countryId),
                new SqlParameterValue(Types.INTEGER, dateType));

        if (dbArray == null) {
            return null;
        }

        List<FunnelDashboardSection> sections = new ArrayList<>();

        for (int i = 0; i < dbArray.length(); ++i) {
            FunnelDashboardSection section = new FunnelDashboardSection();
            section.fillFromDb(dbArray.getJSONObject(i), catalogService);
            sections.add(section);
        }

        return sections;
    }

    @Override
    public List<ScreenTrackReport> getApplicationProcessPathOrderReport(String country, Date from, Date until, Locale locale) throws Exception {
        JSONArray dbArray = queryForObject("SELECT * from credit.rp_application_proccess_path(?, ?, ?::JSON)", JSONArray.class, false, READABLE_DB,
                new SqlParameterValue(Types.DATE, from),
                new SqlParameterValue(Types.DATE, until),
                new SqlParameterValue(Types.OTHER, country));
        if (dbArray == null) {
            return null;
        }

        List<ScreenTrackReport> reports = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ScreenTrackReport report = new ScreenTrackReport();
            report.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            reports.add(report);
        }
        return reports;
    }

    @Override
    public List<ScreenTrackReport> getApplicationProcessPathTimeReport(Date from, Date until, Locale locale) throws Exception {
        JSONArray dbArray = queryForObject("SELECT * from credit.rp_application_proccess_path_time(?, ?)", JSONArray.class, false, READABLE_DB,
                new SqlParameterValue(Types.DATE, from),
                new SqlParameterValue(Types.DATE, until));
        if (dbArray == null) {
            return null;
        }

        List<ScreenTrackReport> reports = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ScreenTrackReport report = new ScreenTrackReport();
            report.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            reports.add(report);
        }
        return reports;
    }

    @Override
    public List<ScreenReport> getApplicationProcessReport(String country, Date from, Date until, Integer[] loanAppStatus, Integer[] productCategories, Locale locale) throws Exception {
        JSONArray dbArray = queryForObject("SELECT * from credit.rp_proccess_questions(?, ?, ?::JSON, ?::JSON, ?::JSON)", JSONArray.class, false, READABLE_DB,
                new SqlParameterValue(Types.DATE, from),
                new SqlParameterValue(Types.DATE, until),
                new SqlParameterValue(Types.OTHER, loanAppStatus != null ? new Gson().toJson(loanAppStatus) : null),
                new SqlParameterValue(Types.OTHER, productCategories != null ? new Gson().toJson(productCategories) : null),
                new SqlParameterValue(Types.OTHER, country));
        if (dbArray == null) {
            return null;
        }

        List<ScreenReport> reports = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            ScreenReport report = new ScreenReport();
            report.fillFromDb(dbArray.getJSONObject(i), catalogService, locale);
            reports.add(report);
        }
        return reports;
    }

    @Override
    public List<LoanApplicationInProcessReport> getLoanApplicationInProcessReportBDS(Integer documentType, String documentNumber, Integer[] producers, Date from, Date until, Integer[] loanApplicationStatuses) throws Exception {
        JSONArray dbArray = queryForObject("SELECT * FROM credit.rp_application_proccess_bds(?, ?, ?::JSON, ?, ?, ?::JSON)", JSONArray.class, false, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.OTHER, producers != null ? new Gson().toJson(producers) : null),
                new SqlParameterValue(Types.DATE, from),
                new SqlParameterValue(Types.DATE, until),
                new SqlParameterValue(Types.OTHER, loanApplicationStatuses != null ? new Gson().toJson(loanApplicationStatuses) : null));
        if (dbArray == null) {
            return null;
        }

        List<LoanApplicationInProcessReport> reports = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            LoanApplicationInProcessReport report = new LoanApplicationInProcessReport();
            report.fillFromDb(dbArray.getJSONObject(i), catalogService, Configuration.getDefaultLocale());
            reports.add(report);
        }
        return reports;
    }

    @Override
    public List<DisburseCreditReport> getDisburseCreditReportBDS(Integer documentType, String documentNumber, Integer[] producers, Date from, Date until) throws Exception {
        JSONArray dbArray = queryForObject("SELECT * from credit.rp_pending_disbursement_bds(?, ?, ?::JSON, ?, ?)", JSONArray.class, false, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.OTHER, producers != null ? new Gson().toJson(producers) : null),
                new SqlParameterValue(Types.DATE, from),
                new SqlParameterValue(Types.DATE, until));
        if (dbArray == null) {
            return null;
        }

        List<DisburseCreditReport> reports = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            DisburseCreditReport report = new DisburseCreditReport();
            report.fillFromDb(dbArray.getJSONObject(i), catalogService, Configuration.getDefaultLocale());
            reports.add(report);
        }
        return reports;
    }

    @Override
    public List<DisbursedCreditReport> getDisbursedCreditReportBDS(Integer documentType, String documentNumber, Integer[] producers, Date from, Date until, JSONArray internalStatuses, Date disbursementStartDate, Date disbursementEndDate) throws Exception {
        JSONArray dbArray = queryForObject("SELECT * FROM credit.rp_disbursed_bds(?, ?, ?::JSON, ?, ?, ?::JSON, ?, ?)", JSONArray.class, false, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.OTHER, producers != null ? new Gson().toJson(producers) : null),
                new SqlParameterValue(Types.DATE, from),
                new SqlParameterValue(Types.DATE, until),
                new SqlParameterValue(Types.OTHER, internalStatuses),
                new SqlParameterValue(Types.DATE, disbursementStartDate),
                new SqlParameterValue(Types.DATE, disbursementEndDate));
        if (dbArray == null) {
            return null;
        }

        List<DisbursedCreditReport> reports = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            DisbursedCreditReport report = new DisbursedCreditReport();
            report.fillFromDb(dbArray.getJSONObject(i), catalogService, Configuration.getDefaultLocale());
            reports.add(report);
        }
        return reports;
    }

    @Override
    public JSONArray getRiskReportBDS(Integer documentType, String documentNumber, Integer[] producers, Date from, Date until, Integer[] creditSubStatuses,
                                      JSONArray internalStatuses, Integer[] applicationStatuses, Date lastExecutionStartDate, Date lastExecutionEndDate) throws Exception {

        String query = null;

        if(Configuration.hostEnvIsProduction()){
            query = "SELECT * FROM credit.get_validate_date_bds(?, ?, ?::JSON, ?, ?, ?::JSON, ?::JSON, ?::JSON, ?, ?)";
        }else{
            query = "SELECT * FROM credit.get_validate_date_bds_temp(?, ?, ?::JSON, ?, ?, ?::JSON, ?::JSON, ?::JSON, ?, ?)";
        }

        return queryForObject(query, JSONArray.class, false, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.OTHER, producers != null ? new Gson().toJson(producers) : null),
                new SqlParameterValue(Types.DATE, from),
                new SqlParameterValue(Types.DATE, until),
                new SqlParameterValue(Types.OTHER, creditSubStatuses != null ? new Gson().toJson(creditSubStatuses) : null),
                new SqlParameterValue(Types.OTHER, internalStatuses),
                new SqlParameterValue(Types.OTHER, applicationStatuses != null ? new Gson().toJson(applicationStatuses) : null),
                new SqlParameterValue(Types.DATE, lastExecutionStartDate),
                new SqlParameterValue(Types.DATE, lastExecutionEndDate));
    }

    @Override
    public List<ReportProces> getReportProcesHistoric(int userId, int reportId, int offset, int limit) {
        JSONArray dbJson = queryForObjectTrx("select * from support.get_historical_reports_by_report_id(?, ?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, reportId),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.INTEGER, userId));
        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<ReportProces> reports = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            ReportProces reportProces = new ReportProces();
            reportProces.fillFromDb(dbJson.getJSONObject(i));
            reports.add(reportProces);
        }
        return reports;
    }

    @Override
    public List<ReportProces> getReportProcesHistoric(int userId, int reportId, int offset, int limit, int origin) {
        JSONArray dbJson = queryForObjectTrx("select * from support.get_historical_reports_by_report_id(?, ?, ?, ?, ?)", JSONArray.class,
                new SqlParameterValue(Types.INTEGER, reportId),
                new SqlParameterValue(Types.INTEGER, offset),
                new SqlParameterValue(Types.INTEGER, limit),
                new SqlParameterValue(Types.INTEGER, userId),
                new SqlParameterValue(Types.INTEGER, origin));
        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<ReportProces> reports = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            ReportProces reportProces = new ReportProces();
            reportProces.fillFromDb(dbJson.getJSONObject(i));
            reports.add(reportProces);
        }
        return reports;
    }

    @Override
    public List<PersonInteractionFollowUpReport> getPersoninteractionFollowUpReports(String jsonParam) {
        JSONArray dbJson = queryForObjectInteraction("select * from interaction.get_downloadable_person_interaction_id(?::json);", JSONArray.class,
                new SqlParameterValue(Types.OTHER, jsonParam));
        if (dbJson == null) {
            return new ArrayList<>();
        }

        List<PersonInteractionFollowUpReport> reports = new ArrayList<>();
        for (int i = 0; i < dbJson.length(); i++) {
            PersonInteractionFollowUpReport reportProces = new PersonInteractionFollowUpReport();
            reportProces.fillFromDb(dbJson.getJSONObject(i));
            reports.add(reportProces);
        }
        return reports;
    }

    @Override
    public List<com.affirm.fdlm.report.LoanApplicationInProcessReport> getLoanApplicationInProcessReportFDLM(Integer documentType, String documentNumber, String lastname, Date from, Date until, Date updatedFrom, Date updatedUntil, Integer[] loanApplicationStatuses) throws Exception {
        JSONArray dbArray = queryForObject("SELECT * FROM credit.rp_application_proccess_fdlm(?, ?, ?, ?, ?, ?, ?, ?::JSON)", JSONArray.class, false, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, documentType),
                new SqlParameterValue(Types.VARCHAR, documentNumber),
                new SqlParameterValue(Types.VARCHAR, lastname),
                new SqlParameterValue(Types.DATE, from),
                new SqlParameterValue(Types.DATE, until),
                new SqlParameterValue(Types.DATE, updatedFrom),
                new SqlParameterValue(Types.DATE, updatedUntil),
                new SqlParameterValue(Types.OTHER, loanApplicationStatuses != null ? new Gson().toJson(loanApplicationStatuses) : null));
        if (dbArray == null) {
            return null;
        }

        List<com.affirm.fdlm.report.LoanApplicationInProcessReport> reports = new ArrayList<>();
        for (int i = 0; i < dbArray.length(); i++) {
            com.affirm.fdlm.report.LoanApplicationInProcessReport report = new com.affirm.fdlm.report.LoanApplicationInProcessReport();
            report.fillFromDb(dbArray.getJSONObject(i), catalogService, Configuration.getDefaultLocale());
            reports.add(report);
        }
        return reports;
    }

    @Override
    public FunnelReport getBanbifFunnelReport(Integer minAge, Integer maxAge, String requestType, String cardType, Date fromDate, Date toDate, String abTesting, List<Integer> products) throws Exception {
        JSONObject dbJson = queryForObject("select credit.rp_funnel_banbif(?, ?, ?, ?, ?, ?, ?, ?);", JSONObject.class, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, minAge),
                new SqlParameterValue(Types.INTEGER, maxAge),
                new SqlParameterValue(Types.VARCHAR, requestType),
                new SqlParameterValue(Types.VARCHAR, cardType),
                new SqlParameterValue(Types.DATE, fromDate),
                new SqlParameterValue(Types.DATE, toDate),
                new SqlParameterValue(Types.VARCHAR, abTesting),
                new SqlParameterValue(Types.OTHER, new Gson().toJson(products)));
        if (dbJson == null) {
            return null;
        }

        FunnelReport report = new FunnelReport();
        report.fillFromDb(dbJson);
        return report;
    }

    @Override
    public FunnelReport getFunnelV3ReportData(Integer entity,Integer minAge, Integer maxAge, String requestType, String cardType, Date fromDate, Date toDate, String abTesting, List<Integer> products, Integer base, List<Integer> steps) throws Exception{
        return getFunnelV3ReportData(entity,minAge,maxAge,requestType,cardType,fromDate,toDate,abTesting,products,base,steps,null,null,null,null, null);
    }

    @Override
    public FunnelReport getFunnelV3ReportData(Integer entity,Integer minAge, Integer maxAge, String requestType, String cardType, Date fromDate, Date toDate, String abTesting, List<Integer> products, Integer base, List<Integer> steps,List<String> utmSources,List<String> utmMedium,List<String> utmCampaign,List<String> utmContent,List<Integer> entityProductParams) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(utmSources != null && utmSources.isEmpty()) utmSources = null;
        if(utmMedium != null && utmMedium.isEmpty()) utmMedium = null;
        if(utmCampaign != null && utmCampaign.isEmpty()) utmCampaign = null;
        if(utmContent != null && utmContent.isEmpty()) utmContent = null;
        if(entityProductParams != null && entityProductParams.isEmpty()) entityProductParams = null;
        JSONObject dbJson = queryForObject("select credit.rp_funnel_entity(?, ?, ?, ?, ?, ?::date, ?::date, ?, ?, ?, ?, ?, ?, ?, ?, ?);", JSONObject.class, true, READABLE_DB,
                new SqlParameterValue(Types.INTEGER, entity),
                new SqlParameterValue(Types.INTEGER, minAge),
                new SqlParameterValue(Types.INTEGER, maxAge),
                new SqlParameterValue(Types.VARCHAR, requestType),
                new SqlParameterValue(Types.VARCHAR, cardType),
                new SqlParameterValue(Types.VARCHAR, fromDate != null ? sdf.format(fromDate) : null),
                new SqlParameterValue(Types.VARCHAR, toDate != null ? sdf.format(toDate) : null),
                new SqlParameterValue(Types.VARCHAR, abTesting),
                new SqlParameterValue(Types.OTHER, new Gson().toJson(products)),
                new SqlParameterValue(Types.VARCHAR, base),
                new SqlParameterValue(Types.OTHER, new Gson().toJson(steps)),
                new SqlParameterValue(utmSources != null ? Types.OTHER : Types.NULL, utmSources != null ? new Gson().toJson(utmSources) : null),
                new SqlParameterValue(utmMedium != null ? Types.OTHER : Types.NULL, utmMedium != null ? new Gson().toJson(utmMedium) : null),
                new SqlParameterValue(utmCampaign != null ? Types.OTHER : Types.NULL, utmCampaign != null ? new Gson().toJson(utmCampaign) : null),
                new SqlParameterValue(utmContent != null ? Types.OTHER : Types.NULL, utmContent != null ? new Gson().toJson(utmContent) : null),
                new SqlParameterValue(entityProductParams != null ? Types.OTHER : Types.NULL, entityProductParams != null ? new Gson().toJson(entityProductParams) : null)
                );
        if (dbJson == null) {
            return null;
        }

        FunnelReport report = new FunnelReport();
        report.fillFromDb(dbJson);
        return report;
    }
}
