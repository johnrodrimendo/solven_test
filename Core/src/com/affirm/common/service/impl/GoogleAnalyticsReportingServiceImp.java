package com.affirm.common.service.impl;

import com.affirm.common.service.ErrorService;
import com.affirm.common.service.GoogleAnalyticsReportingService;
import com.affirm.system.configuration.Configuration;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.AnalyticsReportingScopes;
import com.google.api.services.analyticsreporting.v4.model.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("googleAnalticsReportingService")
public class GoogleAnalyticsReportingServiceImp implements GoogleAnalyticsReportingService {
    private static final String APPLICATION_NAME = "Analytics Reporting";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String SERVICE_ACCOUNT_EMAIL = "api-24@apisolven-184814.iam.gserviceaccount.com";
    private static Logger logger = Logger.getLogger(ReportsServiceImpl.class);

    @Autowired
    ErrorService errorService;

    public GoogleAnalyticsReportingServiceImp() {

    }

    public void getReport(String period1Start, String period1End, String period2Start, String period2End) {
        try {
            AnalyticsReporting service = initializeAnalyticsReporting();


        } catch (Exception e) {

        }
    }

    @Override
    public AnalyticsReporting initializeAnalyticsReporting() throws GeneralSecurityException, IOException {
        InputStream credentialInput = getClass().getResourceAsStream("/credentials.p12");
        File credentialFile = File.createTempFile("credential", "p12");
        OutputStream credentialOutput = new FileOutputStream(credentialFile);

        IOUtils.copy(credentialInput, credentialOutput);


        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
                .setServiceAccountPrivateKeyFromP12File(credentialFile)
                .setServiceAccountScopes(AnalyticsReportingScopes.all())
                .build();

        // Construct the Analytics Reporting service object.
        return new AnalyticsReporting.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }

    @Override
    public GetReportsResponse getReport(AnalyticsReporting service,  Calendar period1From, Calendar period1To, Calendar period2From, Calendar period2To) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        List<DateRange> dates = new ArrayList<>();

        Calendar date = Calendar.getInstance();

        date.set(Calendar.DAY_OF_MONTH, 1);


        String firstDayOfMonth = format.format(date.getTime());

        date.set(Calendar.MONTH, 0);

        String firstDayOfYear = format.format(date.getTime());

        date.add(Calendar.YEAR, - 1);
        String firstDayOfLastYear = format.format(date.getTime());

        String periodFrom1 = format.format(period1From.getTime());
        String periodTo1 = format.format(period1To.getTime());

        String periodFrom2 = format.format(period2From.getTime());
        String periodTo2 = format.format(period2To.getTime());

        dates.add(new DateRange().setStartDate(periodFrom1).setEndDate(periodTo1));
        dates.add(new DateRange().setStartDate(periodFrom2).setEndDate(periodTo2));
        dates.add(new DateRange().setStartDate(firstDayOfMonth).setEndDate("today"));
        dates.add(new DateRange().setStartDate(firstDayOfYear).setEndDate("today"));
        dates.add(new DateRange().setStartDate(firstDayOfLastYear).setEndDate("today"));

        List<Metric> metrics = new ArrayList<>();

        metrics.add(new Metric().setExpression("ga:users").setAlias("Usuarios (visitas)"));
        metrics.add(new Metric().setExpression("ga:newUsers").setAlias("Usuarios (nuevos)"));
        metrics.add(new Metric().setExpression("ga:sessions").setAlias("Sesiones"));
        metrics.add(new Metric().setExpression("ga:sessionsPerUser").setAlias("Sesiones / Usuario"));
        metrics.add(new Metric().setExpression("ga:pageviews").setAlias("Pageviews"));
        metrics.add(new Metric().setExpression("ga:pageviewsPerSession").setAlias("Pageviews / Session"));
        metrics.add(new Metric().setExpression("ga:avgSessionDuration").setAlias("Duración media de la sesión (segundos)"));
        metrics.add(new Metric().setExpression("ga:bounceRate").setAlias("% de Rebote"));
        GetReportsResponse response = new GetReportsResponse();

        response.setReports(new ArrayList<>());

        for (int i = 0 ; i < dates.size() ; i++) {
            response.getReports().addAll(getReport(service, dates.subList(i, i+ 1), metrics));
        }

        // Return the response.
        return response;
    }

    @Override
    public GetReportsResponse getReport(AnalyticsReporting service, List<Pair<Date, Date>> periods) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        List<DateRange> dates = new ArrayList<>();
        List<Metric> metrics = new ArrayList<>();

        metrics.add(new Metric().setExpression("ga:users").setAlias("Usuarios (visitas)"));
        metrics.add(new Metric().setExpression("ga:newUsers").setAlias("Usuarios (nuevos)"));
        metrics.add(new Metric().setExpression("ga:sessions").setAlias("Sesiones"));
        metrics.add(new Metric().setExpression("ga:sessionsPerUser").setAlias("Sesiones / Usuario"));
        metrics.add(new Metric().setExpression("ga:pageviews").setAlias("Pageviews"));
        metrics.add(new Metric().setExpression("ga:pageviewsPerSession").setAlias("Pageviews / Session"));
        metrics.add(new Metric().setExpression("ga:avgSessionDuration").setAlias("Duración media de la sesión (segundos)"));
        metrics.add(new Metric().setExpression("ga:bounceRate").setAlias("% de Rebote"));

        for(Pair<Date, Date>period : periods) {
            String periodFrom = format.format(period.getKey());
            String periodTo = format.format(period.getValue());
            dates.add(new DateRange().setStartDate(periodFrom).setEndDate(periodTo));
        }

        GetReportsResponse response = new GetReportsResponse();

        response.setReports(new ArrayList<>());

        for (int i = 0 ; i < dates.size() ; i++) {
            response.getReports().addAll(getReport(service, dates.subList(i, i+ 1), metrics));
        }

        return response;
    }

    @Override
    public GetReportsResponse getReport(String viewId, AnalyticsReporting service, List<Pair<Date, Date>> periods) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        List<DateRange> dates = new ArrayList<>();
        List<Metric> metrics = new ArrayList<>();

        metrics.add(new Metric().setExpression("ga:users").setAlias("Usuarios (visitas)"));
        metrics.add(new Metric().setExpression("ga:newUsers").setAlias("Usuarios (nuevos)"));
        metrics.add(new Metric().setExpression("ga:sessions").setAlias("Sesiones"));
        metrics.add(new Metric().setExpression("ga:sessionsPerUser").setAlias("Sesiones / Usuario"));
        metrics.add(new Metric().setExpression("ga:pageviews").setAlias("Pageviews"));
        metrics.add(new Metric().setExpression("ga:pageviewsPerSession").setAlias("Pageviews / Session"));
        metrics.add(new Metric().setExpression("ga:avgSessionDuration").setAlias("Duración media de la sesión (segundos)"));
        metrics.add(new Metric().setExpression("ga:bounceRate").setAlias("% de Rebote"));

        for(Pair<Date, Date>period : periods) {
            String periodFrom = format.format(period.getKey());
            String periodTo = format.format(period.getValue());
            dates.add(new DateRange().setStartDate(periodFrom).setEndDate(periodTo));
        }

        GetReportsResponse response = new GetReportsResponse();

        response.setReports(new ArrayList<>());

        for (int i = 0 ; i < dates.size() ; i++) {
            response.getReports().addAll(getReport(viewId, service, dates.subList(i, i+ 1), metrics));
        }
        logger.debug(response.toPrettyString());

        return response;
    }

    @Override
    public GetReportsResponse getReport(List<String> viewIds, AnalyticsReporting service, List<Pair<Date, Date>> periods) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        List<DateRange> dates = new ArrayList<>();
        List<Metric> metrics = new ArrayList<>();

        metrics.add(new Metric().setExpression("ga:users").setAlias("Usuarios (visitas)"));
        metrics.add(new Metric().setExpression("ga:newUsers").setAlias("Usuarios (nuevos)"));
        metrics.add(new Metric().setExpression("ga:sessions").setAlias("Sesiones"));
        metrics.add(new Metric().setExpression("ga:sessionsPerUser").setAlias("Sesiones / Usuario"));
        metrics.add(new Metric().setExpression("ga:pageviews").setAlias("Pageviews"));
        metrics.add(new Metric().setExpression("ga:pageviewsPerSession").setAlias("Pageviews / Session"));
        metrics.add(new Metric().setExpression("ga:avgSessionDuration").setAlias("Duración media de la sesión (segundos)"));
        metrics.add(new Metric().setExpression("ga:bounceRate").setAlias("% de Rebote"));

        for(Pair<Date, Date>period : periods) {
            String periodFrom = format.format(period.getKey());
            String periodTo = format.format(period.getValue());
            dates.add(new DateRange().setStartDate(periodFrom).setEndDate(periodTo));
        }

        GetReportsResponse response = new GetReportsResponse();

        response.setReports(new ArrayList<>());

        for (String viewId: viewIds) {
            for (int i = 0 ; i < dates.size() ; i++) {
                response.getReports().addAll(getReport(viewId, service, dates.subList(i, i+ 1), metrics));
            }
        }
        logger.debug(response.toPrettyString());

        return response;
    }

    private List<Report> getReport(AnalyticsReporting service, List<DateRange> dates, List<Metric> metrics) throws IOException {
        ReportRequest request = new ReportRequest()
                .setViewId(Configuration.getViewID())
                .setDateRanges(dates)
                .setMetrics(metrics);

        ArrayList<ReportRequest> requests = new ArrayList<ReportRequest>();
        requests.add(request);

        GetReportsResponse response;
        try {
            GetReportsRequest getReport = new GetReportsRequest()
                    .setReportRequests(requests);
            response = service.reports().batchGet(getReport).execute();
        } catch (GoogleJsonResponseException e) {
            errorService.onError(e);
            return new ArrayList<>();
        }

        return response.getReports();
    }

    @Override
    public List<Report> getReport(String viewId, AnalyticsReporting service, DateRange dateRange, Metric metric) throws IOException {
        return getReport(viewId, service, Collections.singletonList(dateRange), Collections.singletonList(metric));
    }

    @Override
    public List<Report> getReport(String viewId, AnalyticsReporting service, List<DateRange> dates, List<Metric> metrics) throws IOException {
        ReportRequest request = new ReportRequest()
                .setViewId(viewId)
                .setDateRanges(dates)
                .setMetrics(metrics);

        ArrayList<ReportRequest> requests = new ArrayList<ReportRequest>();
        requests.add(request);

        GetReportsResponse response;
        try {
            GetReportsRequest getReport = new GetReportsRequest()
                    .setReportRequests(requests);
            response = service.reports().batchGet(getReport).execute();
        } catch (GoogleJsonResponseException e) {
            errorService.onError(e);
            return new ArrayList<>();
        }

        return response.getReports();
    }

    @Override
    public List<Report> getReport(String viewId, AnalyticsReporting service, List<DateRange> dates, List<Metric> metrics, String filterExpression) throws IOException {
        ReportRequest request = new ReportRequest()
                .setViewId(viewId)
                .setDateRanges(dates)
                .setMetrics(metrics)
                .setFiltersExpression(filterExpression);

        ArrayList<ReportRequest> requests = new ArrayList<ReportRequest>();
        requests.add(request);

        GetReportsResponse response;
        try {
            GetReportsRequest getReport = new GetReportsRequest()
                    .setReportRequests(requests);
            response = service.reports().batchGet(getReport).execute();
        } catch (GoogleJsonResponseException e) {
            errorService.onError(e);
            return new ArrayList<>();
        }

        return response.getReports();
    }

    @Override
    public GetReportsResponse getPageViews(AnalyticsReporting service, Calendar period1From, Calendar period1To, Calendar period2From, Calendar period2To, List<String> paths) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<Metric> metrics = new ArrayList<>();
        List<DateRange> dates = new ArrayList<>();
        List<DimensionFilterClause> filters = new ArrayList<>();
        List<DimensionFilter> filtersList = new ArrayList<>();

        metrics.add(new Metric().setExpression("ga:pageviews").setAlias("Pageviews"));

        filtersList.add(new DimensionFilter().setDimensionName("ga:pagePath").setOperator("EXACT").setExpressions(paths));
        filters.add(new DimensionFilterClause().setFilters(filtersList));

        Calendar date = Calendar.getInstance();

        date.set(Calendar.DAY_OF_MONTH, 1);


        String firstDayOfMonth = format.format(date.getTime());

        date.set(Calendar.MONTH, 0);

        String firstDayOfYear = format.format(date.getTime());

        date.add(Calendar.YEAR, - 1);
        String firstDayOfLastYear = format.format(date.getTime());

        String periodFrom1 = format.format(period1From.getTime());
        String periodTo1 = format.format(period1To.getTime());

        String periodFrom2 = format.format(period2From.getTime());
        String periodTo2 = format.format(period2To.getTime());

        dates.add(new DateRange().setStartDate(periodFrom1).setEndDate(periodTo1));
        dates.add(new DateRange().setStartDate(periodFrom2).setEndDate(periodTo2));
        dates.add(new DateRange().setStartDate(firstDayOfMonth).setEndDate("today"));
        dates.add(new DateRange().setStartDate(firstDayOfYear).setEndDate("today"));
        dates.add(new DateRange().setStartDate(firstDayOfLastYear).setEndDate("today"));

        GetReportsResponse response = new GetReportsResponse();

        response.setReports(new ArrayList<>());

        for (int i = 0 ; i < dates.size() ; i++) {
            response.getReports().addAll(getReport(service, dates.subList(i, i+1), metrics, filters));
        }

        return response;

    }

    @Override
    public GetReportsResponse getPageViews(AnalyticsReporting service, List<Pair<Date, Date>> periods, List<String> paths) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<Metric> metrics = new ArrayList<>();
        List<DateRange> dates = new ArrayList<>();
        List<DimensionFilterClause> filters = new ArrayList<>();
        List<DimensionFilter> filtersList = new ArrayList<>();

        metrics.add(new Metric().setExpression("ga:pageviews").setAlias("Pageviews"));

        filtersList.add(new DimensionFilter().setDimensionName("ga:pagePath").setOperator("EXACT").setExpressions(paths));
        filters.add(new DimensionFilterClause().setFilters(filtersList));

        for(Pair<Date, Date>period : periods) {
            String periodFrom = format.format(period.getKey());
            String periodTo = format.format(period.getValue());
            dates.add(new DateRange().setStartDate(periodFrom).setEndDate(periodTo));
        }

        GetReportsResponse response = new GetReportsResponse();

        response.setReports(new ArrayList<>());

        for (int i = 0 ; i < dates.size() ; i++) {
            response.getReports().addAll(getReport(service, dates.subList(i, i+1), metrics, filters));
        }

        return response;
    }

    @Override
    public GetReportsResponse getPageViews(String viewId, AnalyticsReporting service, List<Pair<Date, Date>> periods, List<String> paths) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<Metric> metrics = new ArrayList<>();
        List<DateRange> dates = new ArrayList<>();
        List<DimensionFilterClause> filters = new ArrayList<>();
        List<DimensionFilter> filtersList = new ArrayList<>();

        metrics.add(new Metric().setExpression("ga:pageviews").setAlias("Pageviews"));

        filtersList.add(new DimensionFilter().setDimensionName("ga:pagePath").setOperator("EXACT").setExpressions(paths));
        filters.add(new DimensionFilterClause().setFilters(filtersList));

        for(Pair<Date, Date>period : periods) {
            String periodFrom = format.format(period.getKey());
            String periodTo = format.format(period.getValue());
            dates.add(new DateRange().setStartDate(periodFrom).setEndDate(periodTo));
        }

        GetReportsResponse response = new GetReportsResponse();

        response.setReports(new ArrayList<>());

        for (int i = 0 ; i < dates.size() ; i++) {
            response.getReports().addAll(getReport(viewId, service, dates.subList(i, i+1), metrics, filters));
        }
        logger.debug(response.toPrettyString());

        return response;
    }

    @Override
    public GetReportsResponse getPageViews(List<String> viewIds, AnalyticsReporting service, List<Pair<Date, Date>> periods, List<String> paths) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<Metric> metrics = new ArrayList<>();
        List<DateRange> dates = new ArrayList<>();
        List<DimensionFilterClause> filters = new ArrayList<>();
        List<DimensionFilter> filtersList = new ArrayList<>();

        metrics.add(new Metric().setExpression("ga:pageviews").setAlias("Pageviews"));

        filtersList.add(new DimensionFilter().setDimensionName("ga:pagePath").setOperator("EXACT").setExpressions(paths));
        filters.add(new DimensionFilterClause().setFilters(filtersList));

        for(Pair<Date, Date>period : periods) {
            String periodFrom = format.format(period.getKey());
            String periodTo = format.format(period.getValue());
            dates.add(new DateRange().setStartDate(periodFrom).setEndDate(periodTo));
        }

        GetReportsResponse response = new GetReportsResponse();

        response.setReports(new ArrayList<>());

        for (String viewId: viewIds) {
            for (int i = 0 ; i < dates.size() ; i++) {
                response.getReports().addAll(getReport(viewId, service, dates.subList(i, i+1), metrics, filters));
            }
        }
        logger.debug(response.toPrettyString());

        return response;
    }

    private List<Report> getReport(AnalyticsReporting service, List<DateRange> dates, List<Metric> metrics, List<DimensionFilterClause> metricFilters) throws IOException {
        ReportRequest request = new ReportRequest()
                .setViewId(Configuration.getViewID())
                .setDateRanges(dates)
                .setMetrics(metrics)
                .setDimensionFilterClauses(metricFilters);

        ArrayList<ReportRequest> requests = new ArrayList<ReportRequest>();
        requests.add(request);

        GetReportsResponse response;
        try {
            GetReportsRequest getReport = new GetReportsRequest()
                    .setReportRequests(requests);
            response = service.reports().batchGet(getReport).execute();
        } catch (GoogleJsonResponseException e) {
            errorService.onError(e);
            return new ArrayList<>();
        }

        return response.getReports();
    }

    @Override
    public List<Report> getReport(String viewId, AnalyticsReporting service, List<DateRange> dates, List<Metric> metrics, List<DimensionFilterClause> metricFilters) throws IOException {
        ReportRequest request = new ReportRequest()
                .setViewId(viewId)
                .setDateRanges(dates)
                .setMetrics(metrics)
                .setDimensionFilterClauses(metricFilters);

        ArrayList<ReportRequest> requests = new ArrayList<ReportRequest>();
        requests.add(request);

        GetReportsResponse response;
        try {
            GetReportsRequest getReport = new GetReportsRequest()
                    .setReportRequests(requests);
            response = service.reports().batchGet(getReport).execute();
        } catch (GoogleJsonResponseException e) {
            errorService.onError(e);
            return new ArrayList<>();
        }

        return response.getReports();
    }
}
