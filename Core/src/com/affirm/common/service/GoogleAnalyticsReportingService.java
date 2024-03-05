package com.affirm.common.service;

import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.model.*;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public interface GoogleAnalyticsReportingService {
    AnalyticsReporting initializeAnalyticsReporting() throws GeneralSecurityException, IOException;
    GetReportsResponse getReport(AnalyticsReporting service, Calendar period1From, Calendar period1To, Calendar period2From, Calendar period2To) throws IOException;
    GetReportsResponse getReport(AnalyticsReporting service, List<Pair<Date, Date>> periods) throws IOException;
    GetReportsResponse getReport(String viewId, AnalyticsReporting service, List<Pair<Date, Date>> periods) throws IOException;
    GetReportsResponse getReport(List<String> viewIds, AnalyticsReporting service, List<Pair<Date, Date>> periods) throws IOException;
    List<Report> getReport(String viewId, AnalyticsReporting service, DateRange dateRange, Metric metric) throws IOException;

    List<Report> getReport(String viewId, AnalyticsReporting service, List<DateRange> dates, List<Metric> metrics) throws IOException;

    List<Report> getReport(String viewId, AnalyticsReporting service, List<DateRange> dates, List<Metric> metrics, String filterExpression) throws IOException;

    GetReportsResponse getPageViews(AnalyticsReporting service, Calendar period1From, Calendar period1To, Calendar period2From, Calendar period2To, List<String> paths) throws IOException;
    GetReportsResponse getPageViews(AnalyticsReporting service, List<Pair<Date, Date>> periods, List<String> paths) throws IOException;
    GetReportsResponse getPageViews(String viewId, AnalyticsReporting service, List<Pair<Date, Date>> periods, List<String> paths) throws IOException;
    GetReportsResponse getPageViews(List<String> viewIds, AnalyticsReporting service, List<Pair<Date, Date>> periods, List<String> paths) throws IOException;

    List<Report> getReport(String viewId, AnalyticsReporting service, List<DateRange> dates, List<Metric> metrics, List<DimensionFilterClause> metricFilters) throws IOException;
}
