package com.affirm.common.model;

import com.affirm.common.service.UtilService;
import com.google.api.services.analyticsreporting.v4.model.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FunnelReportDashboardPainter {

    private List<String> periods;
    private List<String> rows;
    private List<FunnelDashboardSection> sections;

    private UtilService utilService;

    private GetReportsResponse responseGoogleAnalytics;
    private GetReportsResponse pageViewsSalaryAdvance;
    private GetReportsResponse pageViewsConsumer;

    private HashMap<String, Integer> mapPageSalaryAdvance;
    private HashMap<String, Integer> mapPageSalaryConsumer;

    private List<RowGoogleAnalytics> gaRows;
    private HashMap<Integer, RowGoogleAnalytics> mapGA;

    private List<RowBot> botRows;
    private HashMap<Integer, RowBot> mapBots;

    private List<RowGoogleAnalytics> googleAnalyticsRow;

    public void processReport() throws Exception {
        this.processGoogleAnalyticsReport();
    }

    public void processViews() {
        int index = 0;
        mapPageSalaryAdvance = new HashMap<>();
        mapPageSalaryConsumer = new HashMap<>();

        for (Report report : pageViewsSalaryAdvance.getReports()) {
            ColumnHeader header = report.getColumnHeader();
            List<MetricHeaderEntry> metricHeaders = header.getMetricHeader().getMetricHeaderEntries();
            List<ReportRow> rows = report.getData().getRows();
            int value = 0;
            if(rows != null) {
                for (ReportRow rowReport : rows) {
                    List<DateRangeValues> dates = rowReport.getMetrics();
                    for (int dateIndex = 0; dateIndex < dates.size(); dateIndex++) {
                        DateRangeValues values = dates.get(dateIndex);

                        for (int valueIndex = 0; valueIndex < values.getValues().size() && valueIndex < metricHeaders.size(); valueIndex++) {
                            value = Integer.valueOf(values.getValues().get(valueIndex));
                        }
                    }
                }
            }
            mapPageSalaryAdvance.put(periods.get(index), value);
            index++;
        }

        index = 0;
        for (Report report : pageViewsConsumer.getReports()) {
            ColumnHeader header = report.getColumnHeader();
            List<MetricHeaderEntry> metricHeaders = header.getMetricHeader().getMetricHeaderEntries();
            List<ReportRow> rows = report.getData().getRows();
            int value = 0;
            if(rows != null) {
                for (ReportRow rowReport : rows) {
                    List<DateRangeValues> dates = rowReport.getMetrics();
                    for (int dateIndex = 0; dateIndex < dates.size(); dateIndex++) {
                        DateRangeValues values = dates.get(dateIndex);

                        for (int valueIndex = 0; valueIndex < values.getValues().size() && valueIndex < metricHeaders.size(); valueIndex++) {
                            value = Integer.valueOf(values.getValues().get(valueIndex));
                        }
                    }
                }
            }
            mapPageSalaryConsumer.put(periods.get(index), value);
            index++;
        }
    }

    private void processGoogleAnalyticsReport() throws Exception {
        List<String> metrics = new ArrayList<>();
        metrics.add("Usuarios (visitas)");
        metrics.add("Usuarios (nuevos)");
        metrics.add("Sesiones");
        metrics.add("Sesiones / Usuario");
        metrics.add("Pageviews");
        metrics.add("Pageviews / Session");
        metrics.add("Duración media de la sesión");
        metrics.add("% de Rebote");
        mapGA = new HashMap<>();
        gaRows = new ArrayList<>();
        int index = 0;

        for (Report report : responseGoogleAnalytics.getReports()) {
            ColumnHeader header = report.getColumnHeader();
            List<MetricHeaderEntry> metricHeaders = header.getMetricHeader().getMetricHeaderEntries();
            List<ReportRow> rows = report.getData().getRows();

            if(rows != null) {
                for (ReportRow rowReport : rows) {
                    List<DateRangeValues> dates = rowReport.getMetrics();
                    for (int dateIndex = 0; dateIndex < dates.size(); dateIndex++) {
                        DateRangeValues values = dates.get(dateIndex);
                        for (int valueIndex = 0; valueIndex < values.getValues().size() && valueIndex < metricHeaders.size(); valueIndex++) {
                            RowGoogleAnalytics row = mapGA.get(valueIndex);

                            if (row == null) {
                                row = new RowGoogleAnalytics();
                                row.setName(metricHeaders.get(valueIndex).getName());
                                mapGA.put(valueIndex, row);
                                gaRows.add(row);
                            }

                            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
                            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

                            symbols.setGroupingSeparator(' ');
                            symbols.setDecimalSeparator('.');

                            if(valueIndex == 3 || valueIndex == 5 || valueIndex == 7) {
                                formatter.applyPattern("###,###,##0.00");
                            }
                            else {
                                formatter.applyPattern("###,###,##0");
                            }

                            formatter.setDecimalFormatSymbols(symbols);

                            RowGoogleAnalyticsDetail rowGoogleAnalyticsDetail = new RowGoogleAnalyticsDetail();

                            Double realValue = Double.valueOf(values.getValues().get(valueIndex) != null ? values.getValues().get(valueIndex) : "0.0");
                            String value = formatter.format(realValue);

                            rowGoogleAnalyticsDetail.setQuantity(value);

                            if (row.getValues() == null) {
                                row.setValues(new HashMap<>());
                            }
                            row.getValues().put(periods.get(index), rowGoogleAnalyticsDetail);
                        }
                        index++;
                    }
                }
            }
            else {
                for(int indexVal = 0 ; indexVal < 8 ; ++indexVal) {
                    RowGoogleAnalytics row = mapGA.get(indexVal);

                    if(row == null) {
                        row = new RowGoogleAnalytics();
                        row.setName(metrics.get(indexVal));
                        gaRows.add(row);
                        mapGA.put(indexVal, row);
                    }

                    RowGoogleAnalyticsDetail rowGoogleAnalyticsDetail = new RowGoogleAnalyticsDetail();

                    rowGoogleAnalyticsDetail.setQuantity("0");

                    if (row.getValues() == null) {
                        row.setValues(new HashMap<>());
                    }
                    row.getValues().put(periods.get(index), rowGoogleAnalyticsDetail);
                }
                index ++;
            }
        }
    }

    public FunnelReportDashboardPainter(GetReportsResponse responseGoogleAnalytics, List<FunnelDashboardSection> sections, UtilService utilService) throws Exception {
        setUtilService(utilService);
        this.responseGoogleAnalytics = responseGoogleAnalytics;
        this.sections = sections;

        periods = new ArrayList<>();
        periods.add("Periodo 1");
        periods.add("Periodo 2");
        periods.add("MTD");
        periods.add("YTD");
        periods.add("Histórico");

        rows = new ArrayList<>();
        rows.add("Nueva");
        rows.add("Pre-evaluación aprobada");
        rows.add("Evaluación aprobada");
        rows.add("A la espera de aprobación");
        rows.add("Aprobada");
        rows.add("Aprobada y firmada");
        rows.add("Desembolsados");

        processReport();

        HashMap<String, Integer> visits = new HashMap<>();

        for (RowGoogleAnalytics row : gaRows) {
            if (row.getName().equals("Usuarios (visitas)")) {
                for (String period : periods) {
                    String strVisits = row.getValues().get(period).getQuantity();

                    strVisits = strVisits.replaceAll(",", "");
                    strVisits = strVisits.replaceAll(" ", "");
                    strVisits = strVisits.replaceAll("%", "");

                    Integer totalVisits = Integer.valueOf(strVisits);
                    visits.put(period, totalVisits);
                }
                break;
            }
        }

        for (FunnelDashboardSection section : sections) {
            HashMap<String, HashMap<String, List<String>>> hashMap = section.getMapDetail();

            for (String period : periods) {
                HashMap<String, List<String>> rowMap = hashMap.get(period);

                for (String row : section.getRows()) {
                    List<String> values = rowMap.get(row);

                    if (values == null) {
                        values = new ArrayList<>();
                    }

                    while (values.size() < section.getValues()) {
                        values.add("0");
                    }

                    String perVisit = values.get(1);

                    rowMap.put(row, values);


                    if (!section.getName().equals("Consumos variables")) {
                        if (section.getMapDetail() != null && section.getMapDetail().get(period) != null && section.getMapDetail().get(period).get(rows.get(0)) != null && Integer.valueOf(section.getMapDetail().get(period).get(rows.get(0)).get(0)) > 0) {
                            values.set(1, utilService.percentFormat(100.0 * Integer.valueOf(section.getMapDetail().get(period).get(row).get(0)) / Integer.valueOf(section.getMapDetail().get(period).get(rows.get(0)).get(0)), "PE"));
                        }
                    }
                }
            }
        }
    }

    public List<String> getPeriods() {
        return periods;
    }

    public void setPeriods(List<String> periods) {
        this.periods = periods;
    }

    public List<RowBot> getBotRows() {
        return botRows;
    }

    public void setBotRows(List<RowBot> botRows) {
        this.botRows = botRows;
    }

    public HashMap<Integer, RowBot> getMapBots() {
        return mapBots;
    }

    public void setMapBots(HashMap<Integer, RowBot> mapBots) {
        this.mapBots = mapBots;
    }

    public GetReportsResponse getResponseGoogleAnalytics() {
        return responseGoogleAnalytics;
    }

    public void setResponseGoogleAnalytics(GetReportsResponse responseGoogleAnalytics) {
        this.responseGoogleAnalytics = responseGoogleAnalytics;
    }

    public List<RowGoogleAnalytics> getGaRows() {
        return gaRows;
    }

    public void setGaRows(List<RowGoogleAnalytics> gaRows) {
        this.gaRows = gaRows;
    }

    public HashMap<Integer, RowGoogleAnalytics> getMapGA() {
        return mapGA;
    }

    public void setMapGA(HashMap<Integer, RowGoogleAnalytics> mapGA) {
        this.mapGA = mapGA;
    }

    public List<RowGoogleAnalytics> getGoogleAnalyticsRow() {
        return googleAnalyticsRow;
    }

    public List<FunnelDashboardSection> getSections() {
        return sections;
    }

    public void setSections(List<FunnelDashboardSection> sections) {
        this.sections = sections;
    }

    public UtilService getUtilService() {
        return utilService;
    }

    public void setUtilService(UtilService utilService) {
        this.utilService = utilService;
    }

    public List<String> getRows() {
        return rows;
    }

    public void setRows(List<String> rows) {
        this.rows = rows;
    }

    public HashMap<String, Integer> getMapPageSalaryAdvance() {
        return mapPageSalaryAdvance;
    }

    public void setMapPageSalaryAdvance(HashMap<String, Integer> mapPageSalaryAdvance) {
        this.mapPageSalaryAdvance = mapPageSalaryAdvance;
    }

    public HashMap<String, Integer> getMapPageSalaryConsumer() {
        return mapPageSalaryConsumer;
    }

    public void setMapPageSalaryConsumer(HashMap<String, Integer> mapPageSalaryConsumer) {
        this.mapPageSalaryConsumer = mapPageSalaryConsumer;
    }

    public GetReportsResponse getPageViewsSalaryAdvance() {
        return pageViewsSalaryAdvance;
    }

    public void setPageViewsSalaryAdvance(GetReportsResponse pageViewsSalaryAdvance) {
        this.pageViewsSalaryAdvance = pageViewsSalaryAdvance;
    }

    public GetReportsResponse getPageViewsConsumer() {
        return pageViewsConsumer;
    }

    public void setPageViewsConsumer(GetReportsResponse pageViewsConsumer) {
        this.pageViewsConsumer = pageViewsConsumer;
    }

    public void setGoogleAnalyticsRow(List<RowGoogleAnalytics> googleAnalyticsRow) {
        this.googleAnalyticsRow = googleAnalyticsRow;
    }

    class RowBot {
        private String name;
        private Integer botId;
        private HashMap<String, RowBotDetail> values;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public HashMap<String, RowBotDetail> getValues() {
            return values;
        }

        public void setValues(HashMap<String, RowBotDetail> values) {
            this.values = values;
        }

        public Integer getBotId() {
            return botId;
        }

        public void setBotId(Integer botId) {
            this.botId = botId;
        }
    }

    class RowBotDetail {
        private Double cost;
        private Integer quantity;

        public Double getCost() {
            return cost;
        }

        public void setCost(Double cost) {
            this.cost = cost;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
