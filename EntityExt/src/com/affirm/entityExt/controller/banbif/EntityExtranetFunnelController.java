package com.affirm.entityExt.controller.banbif;

import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.common.dao.ReportsDAO;
import com.affirm.common.model.ExtranetNote;
import com.affirm.common.model.FunnelReport;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.ExtranetMenu;
import com.affirm.common.model.catalog.WebApplication;
import com.affirm.common.model.transactional.BanbifPreApprovedBase;
import com.affirm.common.model.transactional.ReportProces;
import com.affirm.common.service.GoogleAnalyticsReportingService;
import com.affirm.common.service.ReportsService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.entityExt.models.BanbifFunnelReportData;
import com.affirm.entityExt.models.BanbifFunnelReportFilterForm;
import com.affirm.entityExt.models.ExtranetNoteForm;
import com.affirm.entityExt.models.PaginatorTableFilterForm;
import com.affirm.entityExt.models.FunnelReportFilterForm;
import com.affirm.system.configuration.Configuration;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller("entityExtranetFunnelController")
public class EntityExtranetFunnelController {

    public static final String URL = "funnel";
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public static final Gson gson = new Gson();

    @Autowired
    private ReportsDAO reportsDao;

    @Autowired
    private GoogleAnalyticsReportingService googleAnalyticsReportingService;

    @Autowired
    private EntityExtranetService entityExtranetService;

    @Autowired
    private ReportsService reportsService;


    @RequestMapping(value = "/" + URL, method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:report:funnelv2:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showFunnel(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

         model.addAttribute("data", null);
        model.addAttribute("maxDays", BanbifFunnelReportFilterForm.MAX_DAYS_FILTER);
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        List<ExtranetNote> notes = entityExtranetService.getNotesFromMenu(ExtranetMenu.FUNNEL_BANBIF_MENU,loggedUserEntity.getPrincipalEntity() != null ? loggedUserEntity.getPrincipalEntity().getId() : null,0,PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR);
        Pair<Integer, Double> countAdSum = entityExtranetService.getNotesCount(ExtranetMenu.FUNNEL_BANBIF_MENU,locale);
        model.addAttribute("totalCount", countAdSum.getLeft());
        model.addAttribute("limitPaginator", PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR);
        model.addAttribute("notes", notes);
        return new ModelAndView("/entityExtranet/extranetFunnelBanBif");
    }

    @RequestMapping(value = "/" + URL + "/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:funnelv2:execute", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getFunnelData(
            ModelMap model, Locale locale, HttpServletRequest request, BanbifFunnelReportFilterForm filterForm) throws Exception {
        model.addAttribute("data", getFunnelData(filterForm));
        return new ModelAndView("/entityExtranet/extranetFunnelBanBif :: reportData");
    }

    private BanbifFunnelReportData getFunnelData(BanbifFunnelReportFilterForm filterForm)  throws Exception{
        if (filterForm == null)
            filterForm = new BanbifFunnelReportFilterForm();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String analisis = filterForm.getAnalisis() != null ? filterForm.getAnalisis() : "champion";
        String requestType = null;
        String cardType = null;
        Integer minAge = null;
        Integer maxAge = null;
        Date fromDate1 = filterForm.getFromDate1() != null ? sdf.parse(filterForm.getFromDate1()) : null;
        Date toDate1 = filterForm.getToDate1() != null ? sdf.parse(filterForm.getToDate1()) : null;
        Date fromDate2 = filterForm.getFromDate2() != null ? sdf.parse(filterForm.getFromDate2()) : null;
        Date toDate2 = filterForm.getToDate2() != null ? sdf.parse(filterForm.getToDate2()) : null;
        List<Integer> productIds = gson.fromJson(filterForm.getProducto(), new TypeToken<ArrayList<Integer>>() {
        }.getType());
        if (filterForm.getEdad() != null) {
            switch (filterForm.getEdad()) {
                case "1":
                    maxAge = 34;
                    break;
                case "2":
                    minAge = 35;
                    maxAge = 44;
                    break;
                case "3":
                    minAge = 45;
                    maxAge = 54;
                    break;
                case "4":
                    minAge = 45;
            }
        }
        if(filterForm.getMedio() != null) {
            switch (filterForm.getMedio()){
                case "1": requestType = "O";break;
                case "2": requestType = "T";
            }
        }
        if(filterForm.getTipoPlastico() != null) {
            switch (filterForm.getTipoPlastico()){
                case "0":
                    cardType = null;
                    break;
                case "1":
                    cardType = BanbifPreApprovedBase.BANBIF_CLASSIC_CARD;
                    break;
                case "2":
                    cardType = BanbifPreApprovedBase.BANBIF_GOLD_CARD;
                    break;
                case "3":
                    cardType = BanbifPreApprovedBase.BANBIF_PLATINUM_CARD;
                    break;
                case "4":
                    cardType = BanbifPreApprovedBase.BANBIF_INFINITE_CARD;
                    break;
                case "5":
                    cardType = BanbifPreApprovedBase.BANBIF_MASTERCARD_CARD;
                    break;
                case "6":
                    cardType = BanbifPreApprovedBase.BANBIF_SIGNATURE_CARD;
                    break;
            }
        }

        BanbifFunnelReportData data = new BanbifFunnelReportData();
        FunnelReport report1;
        FunnelReport report2;
        if(fromDate1 == null){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -BanbifFunnelReportFilterForm.MAX_DAYS_FILTER);
            fromDate1 = cal.getTime();
            toDate1 = new Date();
        }
        if(analisis.equalsIgnoreCase("champion")){
            report1 = reportsDao.getBanbifFunnelReport(minAge, maxAge, requestType, cardType, fromDate1, toDate1, "A", productIds);
            report2 = reportsDao.getBanbifFunnelReport(minAge, maxAge, requestType, cardType, fromDate1, toDate1, "B", productIds);

            data.setLeftReport(new BanbifFunnelReportData.SideReport(fromDate1, toDate1, "Champion", new ArrayList<>()));
            data.setRightReport(new BanbifFunnelReportData.SideReport(fromDate1, toDate1, "Challenger", new ArrayList<>()));
        }else{
            if(fromDate2 == null || toDate2 == null){
                toDate2 = fromDate1;
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(fromDate1);
                cal2.add(Calendar.DATE, -FunnelReportFilterForm.MAX_DAYS_FILTER);
                fromDate2 = cal2.getTime();
            }
            report1 = reportsDao.getBanbifFunnelReport(minAge, maxAge, requestType, cardType, fromDate1, toDate1, null, productIds);
            report2 = reportsDao.getBanbifFunnelReport(minAge, maxAge, requestType, cardType, fromDate2, toDate2, null, productIds);
            data.setLeftReport(new BanbifFunnelReportData.SideReport(fromDate1, toDate1, "Periodo 1", new ArrayList<>()));
            data.setRightReport(new BanbifFunnelReportData.SideReport(fromDate2, toDate2, "Periodo 2", new ArrayList<>()));
        }

        AnalyticsReporting service = googleAnalyticsReportingService.initializeAnalyticsReporting();

        int totalUsuariosVisitas1 = 0;
        int totalUsuariosVisitas2;

        if (fromDate1 != null) {
            if(analisis.equalsIgnoreCase("champion")) totalUsuariosVisitas1 = getUsuariosVisitas(fromDate1, toDate1, service, true);
            else totalUsuariosVisitas1 = getUsuariosVisitas(fromDate1, toDate1, service, true) + getUsuariosVisitas(fromDate1, toDate1, service, false);
        }

        if (fromDate2 != null) {
            if(analisis.equalsIgnoreCase("champion")) totalUsuariosVisitas2 = getUsuariosVisitas(fromDate2, toDate2, service,false);
            else totalUsuariosVisitas2 = getUsuariosVisitas(fromDate2, toDate2, service, true) + getUsuariosVisitas(fromDate2, toDate2, service, false);
        }
        else {
            if(analisis.equalsIgnoreCase("champion")) totalUsuariosVisitas2 = getUsuariosVisitas(fromDate1, toDate1, service, false);
            else totalUsuariosVisitas2 = totalUsuariosVisitas1;
        }

        data.getLeftReport().getSteps().add(new BanbifFunnelReportData.Step(0.0, "Visitas", totalUsuariosVisitas1, 100.0, 100.0, true));
        data.getLeftReport().getSteps().add(new BanbifFunnelReportData.Step(1.0, "Registro", report1.getStep1(), this.getPercentageOfTotal(report1.getStep1(), totalUsuariosVisitas1), this.getPercentageOfTotal(report1.getStep1(), totalUsuariosVisitas1), report1.getStep1() != null));
        data.getLeftReport().getSteps().add(new BanbifFunnelReportData.Step(2.0, "Con oferta", report1.getStep2(), this.getPercentageOfTotal(report1.getStep2(), report1.getStep1()), this.getPercentageOfTotal(report1.getStep2(), totalUsuariosVisitas1), report1.getStep2() != null));
//        data.getLeftReport().getSteps().add(new BanbifFunnelReportData.Step(3.0, "PIN validado", report1.getStep3(), this.getPercentageOfTotal(report1.getStep3(), report1.getStep2()), this.getPercentageOfTotal(report1.getStep3(), totalUsuariosVisitas1), report1.getStep3() != null));
        data.getLeftReport().getSteps().add(new BanbifFunnelReportData.Step(3.0, "Validación aprobada", report1.getStep4(), this.getPercentageOfTotal(report1.getStep4(), report1.getStep2()), this.getPercentageOfTotal(report1.getStep4(), totalUsuariosVisitas1), report1.getStep4() != null));
        data.getLeftReport().getSteps().add(new BanbifFunnelReportData.Step(4.0, "Oferta aceptada", report1.getStep5(), this.getPercentageOfTotal(report1.getStep5(), report1.getStep4()), this.getPercentageOfTotal(report1.getStep5(), totalUsuariosVisitas1), report1.getStep5() != null));
        data.getLeftReport().getSteps().add(new BanbifFunnelReportData.Step(5.0, "Domicilio vivienda", report1.getStep6(), this.getPercentageOfTotal(report1.getStep6(), report1.getStep5()), this.getPercentageOfTotal(report1.getStep6(), totalUsuariosVisitas1), report1.getStep6() != null));
        data.getLeftReport().getSteps().add(new BanbifFunnelReportData.Step(6.0, "Solicitud completa", report1.getStep7(), this.getPercentageOfTotal(report1.getStep7(), report1.getStep6()), this.getPercentageOfTotal(report1.getStep7(), totalUsuariosVisitas1), report1.getStep7() != null));
        data.getLeftReport().getSteps().add(new BanbifFunnelReportData.Step(7.0, "Entregada", report1.getStep8(), this.getPercentageOfTotal(report1.getStep8(), report1.getStep7()), this.getPercentageOfTotal(report1.getStep8(), totalUsuariosVisitas1), report1.getStep8() != null));

        data.getRightReport().getSteps().add(new BanbifFunnelReportData.Step(0.0, "Visitas", totalUsuariosVisitas2, 100.0, 100.0, true));
        data.getRightReport().getSteps().add(new BanbifFunnelReportData.Step(1.0, "Registro", report2.getStep1(), this.getPercentageOfTotal(report2.getStep1(), totalUsuariosVisitas2), this.getPercentageOfTotal(report2.getStep1(), totalUsuariosVisitas2), report2.getStep1() != null));
        data.getRightReport().getSteps().add(new BanbifFunnelReportData.Step(2.0, "Con oferta", report2.getStep2(), this.getPercentageOfTotal(report2.getStep2(), report2.getStep1()), this.getPercentageOfTotal(report2.getStep2(), totalUsuariosVisitas2), report2.getStep2() != null));
//        data.getRightReport().getSteps().add(new BanbifFunnelReportData.Step(3.0, "PIN validado", report2.getStep3(), this.getPercentageOfTotal(report2.getStep3(), report2.getStep2()), this.getPercentageOfTotal(report2.getStep3(), totalUsuariosVisitas2), report2.getStep3() != null));
        data.getRightReport().getSteps().add(new BanbifFunnelReportData.Step(3.0, "Validación aprobada", report2.getStep4(), this.getPercentageOfTotal(report2.getStep4(), report2.getStep2()), this.getPercentageOfTotal(report2.getStep4(), totalUsuariosVisitas2), report2.getStep4() != null));
        data.getRightReport().getSteps().add(new BanbifFunnelReportData.Step(4.0, "Oferta aceptada", report2.getStep5(), this.getPercentageOfTotal(report2.getStep5(), report2.getStep4()), this.getPercentageOfTotal(report2.getStep5(), totalUsuariosVisitas2), report2.getStep5() != null));
        data.getRightReport().getSteps().add(new BanbifFunnelReportData.Step(5.0, "Domicilio vivienda", report2.getStep6(), this.getPercentageOfTotal(report2.getStep6(), report2.getStep5()), this.getPercentageOfTotal(report2.getStep6(), totalUsuariosVisitas2), report2.getStep6() != null));
        data.getRightReport().getSteps().add(new BanbifFunnelReportData.Step(6.0, "Solicitud completa", report2.getStep7(), this.getPercentageOfTotal(report2.getStep7(), report2.getStep6()), this.getPercentageOfTotal(report2.getStep7(), totalUsuariosVisitas2), report2.getStep7() != null));
        data.getRightReport().getSteps().add(new BanbifFunnelReportData.Step(7.0, "Entregada", report2.getStep8(), this.getPercentageOfTotal(report2.getStep8(), report2.getStep7()), this.getPercentageOfTotal(report2.getStep8(), totalUsuariosVisitas2), report2.getStep8() != null));

        return data;
    }

    private double getPercentageOfTotal(Integer part, Integer total) {
        if(part == null || total == null)
            return 0.0;
        if (total == 0)
            return 0.0;

        return part * 100.0 / total;
    }

//    private int getUsuariosVisitas(Date from, Date to, AnalyticsReporting service) throws IOException {
//        String periodFrom = format.format(from);
//        if(to == null) {
//            Calendar calDateTo = Calendar.getInstance();
//            calDateTo.setTime(from);
//            calDateTo.add(Calendar.DATE, BanbifFunnelReportFilterForm.MAX_DAYS_FILTER);
//            to = calDateTo.getTime();
//        }
//        String periodTo = format.format(to);
//        DateRange dateRange = new DateRange().setStartDate(periodFrom).setEndDate(periodTo);
//
//        List<Report> reports = googleAnalyticsReportingService
//                .getReport(Configuration.GA_BANBIF, service, dateRange, new Metric().setExpression("ga:users").setAlias("Usuarios (visitas)"));
//
//        HashMap<String, Double> totalGAResults = new HashMap<>();
//
//        for (com.google.api.services.analyticsreporting.v4.model.Report report : reports) {
//            ColumnHeader header = report.getColumnHeader();
//            List<MetricHeaderEntry> metricHeaders = header.getMetricHeader().getMetricHeaderEntries();
//            List<ReportRow> rows = report.getData().getRows();
//
//            if (rows != null) {
//                for (ReportRow row : rows) {
//                    for (DateRangeValues dateRangeValues : row.getMetrics()) {
//                        for (int i = 0; i < dateRangeValues.getValues().size(); i++) {
//                            Double newValue = Double.parseDouble(dateRangeValues.getValues().get(i));
//                            totalGAResults.computeIfPresent(metricHeaders.get(i).getName(), (k, v) -> (v + newValue));
//                            totalGAResults.putIfAbsent(metricHeaders.get(i).getName(), newValue);
//                        }
//                    }
//                }
//            } else {
//                for (MetricHeaderEntry metricHeaderEntry : metricHeaders) {
//                    totalGAResults.put(metricHeaderEntry.getName(), 0.0);
//                }
//            }
//        }
//
//        return Configuration.hostEnvIsProduction() ?
//                totalGAResults.get("Usuarios (visitas)").intValue() :
//                100;
//    }

    public int getUsuariosVisitas(Date from, Date to, AnalyticsReporting service, Boolean champion) throws IOException {
        String periodFrom = format.format(from);
        if(to == null) {
            Calendar calDateTo = Calendar.getInstance();
            calDateTo.setTime(from);
            calDateTo.add(Calendar.DATE, BanbifFunnelReportFilterForm.MAX_DAYS_FILTER);
            to = calDateTo.getTime();
        }
        String periodTo = format.format(to);
        DateRange dateRange = new DateRange().setStartDate(periodFrom).setEndDate(periodTo);

        String analyticViewId = Configuration.GA_BANBIF;

        List<DimensionFilterClause> filters = new ArrayList<>();
        List<DimensionFilter> filtersList = new ArrayList<>();
        if(champion != null){
            filtersList.add(new DimensionFilter().setDimensionName("ga:eventAction").setOperator("EXACT").setExpressions(Arrays.asList("viewLanding" + (champion ? "A" : "B"))));
        }else{
            filtersList.add(new DimensionFilter().setDimensionName("ga:eventAction").setOperator("EXACT").setExpressions(Arrays.asList("viewLanding")));
        }
        filters.add(new DimensionFilterClause().setFilters(filtersList));

        List<Report> reports = googleAnalyticsReportingService.getReport(analyticViewId, service, Arrays.asList(dateRange), Arrays.asList(new Metric().setExpression("ga:totalEvents")), filters);

        HashMap<String, Double> totalGAResults = new HashMap<>();

        for (Report report : reports) {
            ColumnHeader header = report.getColumnHeader();
            List<MetricHeaderEntry> metricHeaders = header.getMetricHeader().getMetricHeaderEntries();
            List<ReportRow> rows = report.getData().getRows();

            if (rows != null) {
                for (ReportRow row : rows) {
                    for (DateRangeValues dateRangeValues : row.getMetrics()) {
                        for (int i = 0; i < dateRangeValues.getValues().size(); i++) {
                            Double newValue = Double.parseDouble(dateRangeValues.getValues().get(i));
                            totalGAResults.computeIfPresent(metricHeaders.get(i).getName(), (k, v) -> (v + newValue));
                            totalGAResults.putIfAbsent(metricHeaders.get(i).getName(), newValue);
                        }
                    }
                }
            } else {
                for (MetricHeaderEntry metricHeaderEntry : metricHeaders) {
                    totalGAResults.put(metricHeaderEntry.getName(), 0.0);
                }
            }
        }

        return totalGAResults.get("ga:totalEvents").intValue();

    }

    @RequestMapping(value = "/" + URL + "/loansLight/export", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:loansLight:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showReportingLoansLight(BanbifFunnelReportFilterForm filterForm) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String requestType = null;
        String cardType = null;
        Integer minAge = null;
        Integer maxAge = null;
        Date from = filterForm.getFromDate1() != null ? sdf.parse(filterForm.getFromDate1()) : null;
        Date to = filterForm.getToDate1() != null ? sdf.parse(filterForm.getToDate1()) : null;
        Date from2 = filterForm.getFromDate2() != null ? sdf.parse(filterForm.getFromDate2()) : null;
        Date to2 = filterForm.getToDate2() != null ? sdf.parse(filterForm.getToDate2()) : null;

        if (filterForm.getEdad() != null) {
            switch (filterForm.getEdad()) {
                case "1":
                    maxAge = 34;
                    break;
                case "2":
                    minAge = 35;
                    maxAge = 44;
                    break;
                case "3":
                    minAge = 45;
                    maxAge = 54;
                    break;
                case "4":
                    minAge = 45;
            }
        }
        if(filterForm.getMedio() != null) {
            switch (filterForm.getMedio()){
                case "1": requestType = "O";break;
                case "2": requestType = "T";break;
            }
        }
        if(filterForm.getTipoPlastico() != null) {
            switch (filterForm.getTipoPlastico()){
                case "0":
                    cardType = null;
                    break;
                case "1":
                    cardType = BanbifPreApprovedBase.BANBIF_CLASSIC_CARD;
                    break;
                case "2":
                    cardType = BanbifPreApprovedBase.BANBIF_GOLD_CARD;
                    break;
                case "3":
                    cardType = BanbifPreApprovedBase.BANBIF_PLATINUM_CARD;
                    break;
                case "4":
                    cardType = BanbifPreApprovedBase.BANBIF_INFINITE_CARD;
                    break;
                case "5":
                    cardType = BanbifPreApprovedBase.BANBIF_MASTERCARD_CARD;
                    break;
                case "6":
                    cardType = BanbifPreApprovedBase.BANBIF_SIGNATURE_CARD;
                    break;
            }
        }

        Integer[] entities = { entityExtranetService.getPrincipalEntity().getId() };
        Integer[] countries = { entityExtranetService.getPrincipalEntity().getCountryId() };
        Integer[] products = gson.fromJson(filterForm.getProducto(), Integer[].class);

        reportsService.createReporteSolicitudesLight(
                entityExtranetService.getLoggedUserEntity().getId(),
                minAge,
                maxAge,
                requestType,
                cardType,
                from,
                to,
                from2,
                to2,
                countries,
                entities,
                products,
                WebApplication.ENTITY_EXTRANET);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/" + URL + "/loansLight/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:loansLight:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object showLoansLightReportList(ModelMap model) throws Exception {
        int userId = entityExtranetService.getLoggedUserEntity().getId();
        List<ReportProces> historicReports = reportsDao.getReportProcesHistoric(userId, com.affirm.common.model.catalog.Report.REPORTE_SOLICITUDES_LIGHT, 0, 5, WebApplication.ENTITY_EXTRANET);
        model.addAttribute("loansLightHistoricReports", historicReports);
        return new ModelAndView("/entityExtranet/extranetReports :: loansLightResults");
    }

    @RequestMapping(value = "/" + URL + "/notes/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:report:funnelv2:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object getNotes(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        Integer offset = 0;
        Integer limit = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        if(filter != null) limit = filter.getLimit();
        if(filter != null && filter.getPage() != null && limit != null) {
            offset = filter.getPage() * limit;
        }
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        List<ExtranetNote> notes = entityExtranetService.getNotesFromMenu(ExtranetMenu.FUNNEL_BANBIF_MENU,loggedUserEntity.getPrincipalEntity() != null ? loggedUserEntity.getPrincipalEntity().getId() : null, offset, limit);
        model.addAttribute("notes", notes);
        return new ModelAndView("/entityExtranet/fragments/extranetNoteFragment :: list");
    }

    @RequestMapping(value = "/" + URL + "/notes/count", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:report:funnelv2:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object countNotes(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {
        Pair<Integer, Double> countAdSum = entityExtranetService.getNotesCount(ExtranetMenu.FUNNEL_BANBIF_MENU,locale);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("count", countAdSum.getLeft());

        return AjaxResponse.ok(new Gson().toJson(data));
    }

    @RequestMapping(value = "/" + URL + "/notes", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:report:funnelv2:view", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object createNote(ModelMap model, ExtranetNoteForm form) throws Exception {
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        if(form.getId() == null) entityExtranetService.insExtranetNote(ExtranetMenu.FUNNEL_BANBIF_MENU,form.getType(),form.getNote());
        else {
            ExtranetNote note = entityExtranetService.editExtranetNote(ExtranetMenu.FUNNEL_BANBIF_MENU, form.getId(),form.getType(),form.getNote());
            if(note == null) AjaxResponse.errorMessage("No existe el registro o no tiene permiso para modificarlo");
        }
        List<ExtranetNote> notes = entityExtranetService.getNotesFromMenu(ExtranetMenu.FUNNEL_BANBIF_MENU,loggedUserEntity.getPrincipalEntity() != null ? loggedUserEntity.getPrincipalEntity().getId() : null, 0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR);
        model.addAttribute("notes", notes);
        return new ModelAndView("/entityExtranet/fragments/extranetNoteFragment :: list");
    }


}
