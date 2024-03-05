package com.affirm.backoffice.controller;

import com.affirm.common.service.impl.GoogleAnalyticsReportingServiceImp;
import com.affirm.system.configuration.Configuration;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import java.io.*;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.*;


import com.google.api.services.analyticsreporting.v4.AnalyticsReportingScopes;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.model.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

public class Test {
    private static final String APPLICATION_NAME = "Hello Analytics Reporting";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
//    private static final String KEY_FILE_LOCATION = "/home/x-keyboard/Descargas/credentials.p12";
    private static final String SERVICE_ACCOUNT_EMAIL = "api-24@apisolven-184814.iam.gserviceaccount.com";
    private static final String VIEW_ID = Configuration.GA_MARKETPLACE;

    private static final List<String> paths = Collections.singletonList("/credito-de-consumo/evaluacion");

    public static void main(String[] args) {
        try {
            AnalyticsReporting service = initializeAnalyticsReporting();
            GetReportsResponse response = getReport(service);
            printResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes an authorized Analytics Reporting service object.
     *
     * @return The analytics reporting service object.
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private static AnalyticsReporting initializeAnalyticsReporting() throws GeneralSecurityException, IOException {
        InputStream credentialInput = GoogleAnalyticsReportingServiceImp.class.getClass().getResourceAsStream("/credentials.p12");
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

    /**
     * Query the Analytics Reporting API V4.
     * Constructs a request for the sessions for the past seven days.
     * Returns the API response.
     *
     * @param service
     * @return GetReportResponse
     * @throws IOException
     */
    private static GetReportsResponse getReport(AnalyticsReporting service) throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        List<DateRange> dates = new ArrayList<>();

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));

        if(startDate.compareTo(endDate) < 0) {
            System.out.println("Mes actual no concluido. Tomando mes anterior");
        }

        startDate.set(Calendar.DAY_OF_MONTH, 1);
        startDate.add(Calendar.MONTH, -1);

        endDate.add(Calendar.MONTH, -1);
        endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));

        String firstDayOfMonth = format.format(startDate.getTime());
        String lastDayOfMonth = format.format(endDate.getTime());

        System.out.println(String.format("Rango de fechas: %s - %s", firstDayOfMonth, lastDayOfMonth));
        dates.add(new DateRange().setStartDate(firstDayOfMonth).setEndDate(lastDayOfMonth));

        List<Metric> metrics = new ArrayList<>();

        metrics.add(new Metric().setExpression("ga:users").setAlias("Usuarios (visitas)"));
        metrics.add(new Metric().setExpression("ga:newUsers").setAlias("Usuarios (nuevos)"));
        metrics.add(new Metric().setExpression("ga:sessions").setAlias("Sesiones"));
        metrics.add(new Metric().setExpression("ga:sessionsPerUser").setAlias("Sesiones / Usuario"));
        metrics.add(new Metric().setExpression("ga:pageviews").setAlias("Pageviews"));
        metrics.add(new Metric().setExpression("ga:pageviewsPerSession").setAlias("Pageviews / Session"));
        metrics.add(new Metric().setExpression("ga:sessionDuration").setAlias("Duración media de la sesión"));
        metrics.add(new Metric().setExpression("ga:bounceRate").setAlias("% de Rebote"));

        ReportRequest request = new ReportRequest()
                .setViewId(VIEW_ID)
                .setDateRanges(dates)
                .setMetrics(metrics)
                ;

//        FILTRO POR PAGE
        System.out.println("Filtro por page:" + paths.toString());
        List<DimensionFilterClause> filters = new ArrayList<>();
        List<DimensionFilter> filtersList = new ArrayList<>();
        filtersList.add(new DimensionFilter().setDimensionName("ga:pagePath").setOperator("EXACT").setExpressions(paths));
        filters.add(new DimensionFilterClause().setFilters(filtersList));
        request.setDimensionFilterClauses(filters);

        ArrayList<ReportRequest> requests = new ArrayList<ReportRequest>();
        requests.add(request);

        // Create the GetReportsRequest object.
        GetReportsRequest getReport = new GetReportsRequest()
                .setReportRequests(requests);

        // Call the batchGet method.
        return service.reports().batchGet(getReport).execute();
    }

    /**
     * Parses and prints the Analytics Reporting API V4 response.
     *
     * @param response the Analytics Reporting API V4 response.
     */
    private static void printResponse(GetReportsResponse response) {
        System.out.println(response.toString());
        for (Report report: response.getReports()) {
            ColumnHeader header = report.getColumnHeader();
            List<String> dimensionHeaders = header.getDimensions();
            List<MetricHeaderEntry> metricHeaders = header.getMetricHeader().getMetricHeaderEntries();
            List<ReportRow> rows = report.getData().getRows();

            if (rows == null) {
                System.out.println("No data found for " + VIEW_ID);
                return;
            }

            for (ReportRow row: rows) {
                List<String> dimensions = row.getDimensions();
                List<DateRangeValues> metrics = row.getMetrics();
                /*for (int i = 0; i < dimensionHeaders.size() && i < dimensions.size(); i++) {
                    System.out.println(dimensionHeaders.get(i) + ": " + dimensions.get(i));
                }*/

                for (int j = 0; j < metrics.size(); j++) {
                    System.out.println("Result Row (" + (j + 1) + "): ");
                    DateRangeValues values = metrics.get(j);
                    for (int k = 0; k < values.getValues().size() && k < metricHeaders.size(); k++) {
                        System.out.println(metricHeaders.get(k).getName() + ": " + values.getValues().get(k));
                    }
                    System.out.println();
                }
            }
        }
    }
}
