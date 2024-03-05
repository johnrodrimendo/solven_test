package com.affirm.entityExt.controller;

import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.common.dao.CatalogDAO;
import com.affirm.common.dao.ReportsDAO;
import com.affirm.common.model.ExtranetNote;
import com.affirm.common.model.FunnelReport;
import com.affirm.common.model.FunnelStep;
import com.affirm.common.model.UTMValue;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.BanbifPreApprovedBase;
import com.affirm.common.model.transactional.EntityExtranetUser;
import com.affirm.common.model.transactional.ExtranetMenuEntity;
import com.affirm.common.model.transactional.ReportProces;
import com.affirm.common.service.BrandingService;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.GoogleAnalyticsReportingService;
import com.affirm.common.service.ReportsService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.entityExt.models.*;
import com.affirm.system.configuration.Configuration;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.model.*;
import com.google.api.services.analyticsreporting.v4.model.Report;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller("entityExtranetFunnelFinController")
public class EntityExtranetFunnelFinController {

    public static final String URL = "funnelv3";
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public static final Gson gson = new Gson();
    public static final Integer EXTRANET_MENU = ExtranetMenu.FUNNEL_MENU;

    @Autowired
    private ReportsDAO reportsDao;
    @Autowired
    private GoogleAnalyticsReportingService googleAnalyticsReportingService;
    @Autowired
    private EntityExtranetService entityExtranetService;
    @Autowired
    private BrandingService brandingService;
    @Autowired
    private ReportsService reportsService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CatalogDAO catalogDAO;


    @RequestMapping(value = "/" + URL, method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:report:funnelv3:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showFunnel(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
         model.addAttribute("data", null);
        model.addAttribute("currentPage","funnelv3");
        model.addAttribute("page", "funnelv3");
        model.addAttribute("maxDays", FunnelReportFilterForm.MAX_DAYS_FILTER);
        EntityExtranetConfiguration extranetConfiguration = brandingService.getExtranetBrandingAsJson(loggedUserEntity.getPrincipalEntity().getId());
        Integer productCategoryId = null;
        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(),EXTRANET_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());
        if(!products.isEmpty()) productCategoryId = products.get(0).getProductCategoryId();
        Product productSelected = !products.isEmpty() ? products.get(0) : null;
        model.addAttribute("products", products);
        model.addAttribute("productSelected", productSelected != null ? productSelected.getId() : null);
        model.addAttribute("productCategorySelected", productCategoryId);
        if(productSelected != null){
            model.addAttribute("entityProductParams", catalogService.getEntityProductParams().stream().filter(e -> e.getEntity() != null && e.getEntity().getId().intValue() == loggedUserEntity.getPrincipalEntity().getId().intValue() && e.getProduct().getId().intValue() == productSelected.getId().intValue()).collect(Collectors.toList()));
        }
        if(extranetConfiguration != null && extranetConfiguration.getFunnelConfiguration() != null && !extranetConfiguration.getFunnelConfiguration().isEmpty() && productCategoryId != null) {
            Integer finalProductCategoryId = productCategoryId;
            EntityExtranetConfiguration.FunnelConfiguration funnelConfiguration = extranetConfiguration.getFunnelConfiguration().stream().filter(e -> e.getProductCategoryId().intValue() == finalProductCategoryId).findFirst().orElse(null);
            model.addAttribute("funnelConfiguration", funnelConfiguration);
        }

        List<ExtranetNote> notes = entityExtranetService.getNotesFromMenu(ExtranetMenu.FUNNEL_MENU,loggedUserEntity.getPrincipalEntity() != null ? loggedUserEntity.getPrincipalEntity().getId() : null, 0,PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR);
        Pair<Integer, Double> countAdSum = entityExtranetService.getNotesCount(ExtranetMenu.FUNNEL_MENU,locale);
        model.addAttribute("totalCount", countAdSum.getLeft());
        model.addAttribute("notes", notes);
        model.addAttribute("limitPaginator", PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR);
        return new ModelAndView("/entityExtranet/extranetFunnel");
    }

    @RequestMapping(value = "/" + URL + "/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:funnelv3:execute", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getFunnelData(
            ModelMap model, Locale locale, HttpServletRequest request, FunnelReportFilterForm filterForm) throws Exception {
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        filterForm.setEntity(loggedUserEntity.getPrincipalEntity().getId());
        model.addAttribute("data", getFunnelData(filterForm));
        EntityExtranetConfiguration extranetConfiguration = brandingService.getExtranetBrandingAsJson(loggedUserEntity.getPrincipalEntity().getId());
        if(extranetConfiguration == null || extranetConfiguration.getFunnelConfiguration() == null || extranetConfiguration.getFunnelConfiguration().isEmpty()) return new FunnelReportData();
        Integer productCategoryId = null;
        EntityExtranetConfiguration.FunnelConfiguration funnelConfiguration = null;
        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(),EXTRANET_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());
        if(products.size() == 0) return AjaxResponse.errorMessage("No tiene ningún producto configurado para esta bandeja");
        //OBTENER PRODUCTO
        if(filterForm.getProducto() != null) {
            FunnelReportFilterForm finalFilterForm = filterForm;
            products = products.stream().filter(e -> e.getId().equals(Integer.valueOf(finalFilterForm.getProducto()))).collect(Collectors.toList());
        }
        if(!products.isEmpty()) productCategoryId = products.get(0).getProductCategoryId();
        if(extranetConfiguration != null && extranetConfiguration.getFunnelConfiguration() != null && !extranetConfiguration.getFunnelConfiguration().isEmpty() && loggedUserEntity != null && productCategoryId != null) {
            Integer finalProductCategoryId = productCategoryId;
            funnelConfiguration = extranetConfiguration.getFunnelConfiguration().stream().filter(e -> e.getProductCategoryId().equals(finalProductCategoryId)).findFirst().orElse(null);
        }
        model.addAttribute("funnelConfiguration", funnelConfiguration);
        model.addAttribute("productCategorySelected", productCategoryId);
        return new ModelAndView("/entityExtranet/extranetFunnel :: reportData");
    }

    private FunnelReportData getFunnelData(FunnelReportFilterForm filterForm)  throws Exception{
        if (filterForm == null)
            filterForm = new FunnelReportFilterForm();
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        List<FunnelStep> funnelSteps = catalogDAO.getFunnelSteps();
        EntityExtranetConfiguration extranetConfiguration = brandingService.getExtranetBrandingAsJson(filterForm.getEntity());
        if(extranetConfiguration == null || extranetConfiguration.getFunnelConfiguration() == null || extranetConfiguration.getFunnelConfiguration().isEmpty()) return new FunnelReportData();
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
//        List<Integer> productIds = gson.fromJson(filterForm.getProducto(), new TypeToken<ArrayList<Integer>>() {
//        }.getType());

        List<String> utmSource = filterForm.getUtmSource() != null && !filterForm.getUtmSource().isEmpty() ? gson.fromJson(filterForm.getUtmSource(), new TypeToken<ArrayList<String>>() {}.getType()) : null;
        List<String> utmCampaign = filterForm.getUtmCampaign() != null && !filterForm.getUtmCampaign().isEmpty() ? gson.fromJson(filterForm.getUtmCampaign(), new TypeToken<ArrayList<String>>() {}.getType()) : null;
        List<String> utmContent = filterForm.getUtmContent() != null && !filterForm.getUtmContent().isEmpty() ? gson.fromJson(filterForm.getUtmContent(), new TypeToken<ArrayList<String>>() {}.getType()) : null;
        List<String> utmMedium = filterForm.getUtmMedium() != null && !filterForm.getUtmMedium().isEmpty() ? gson.fromJson(filterForm.getUtmMedium(), new TypeToken<ArrayList<String>>() {}.getType()) : null;
        List<Integer> entityProductParams = filterForm.getEntityProductParam() != null && !filterForm.getEntityProductParam().isEmpty() ? gson.fromJson(filterForm.getEntityProductParam(), new TypeToken<ArrayList<Integer>>() {}.getType()) : null;

        Integer base = filterForm.getBase();
//        if(productIds.stream().filter(e -> e == -1).findFirst().orElse(null) != null) productIds = filterForm.getProductsUserEntity().stream().map(Product::getId).collect(Collectors.toList());
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

        FunnelReportData data = new FunnelReportData();
        FunnelReport report1;
        FunnelReport report2;
        EntityExtranetConfiguration.FunnelConfiguration funnelConfiguration = null;

        Integer productCategoryId = null;
        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(),EXTRANET_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());
        //OBTENER PRODUCTO
        if(filterForm.getProducto() != null) {
            FunnelReportFilterForm finalFilterForm = filterForm;
            products = products.stream().filter(e -> e.getId().equals(Integer.valueOf(finalFilterForm.getProducto()))).collect(Collectors.toList());
        }
        if(!products.isEmpty()) productCategoryId = products.get(0).getProductCategoryId();
        if(extranetConfiguration != null && extranetConfiguration.getFunnelConfiguration() != null && !extranetConfiguration.getFunnelConfiguration().isEmpty() && loggedUserEntity != null && productCategoryId != null) {
            Integer finalProductCategoryId = productCategoryId;
            funnelConfiguration = extranetConfiguration.getFunnelConfiguration().stream().filter(e -> e.getProductCategoryId().equals(finalProductCategoryId)).findFirst().orElse(null);
        }
        if(entityProductParams != null && !entityProductParams.isEmpty()){
            if(entityProductParams.stream().anyMatch(e -> e.intValue() == -1)) entityProductParams = new ArrayList<>();
            entityProductParams.removeIf(e -> e == null || e == -1);
        }
        int totalUsuariosVisitas1 = 0;
        int totalUsuariosVisitas2;
        AnalyticsReporting service = googleAnalyticsReportingService.initializeAnalyticsReporting();
        if(fromDate1 == null){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -FunnelReportFilterForm.MAX_DAYS_FILTER);
            fromDate1 = cal.getTime();
            toDate1 = new Date();
        }
        if(analisis.equalsIgnoreCase("champion")){
            report1 = reportsDao.getFunnelV3ReportData(filterForm.getEntity(),minAge, maxAge, requestType, cardType, fromDate1, toDate1, "A", products.stream().map(Product::getId).collect(Collectors.toList()), base, funnelConfiguration != null && funnelConfiguration.getSteps() != null && !funnelConfiguration.getSteps().isEmpty() ? funnelConfiguration.getSteps().stream().map(EntityExtranetConfiguration.FunnelStep::getStepId).collect(Collectors.toList()) : new ArrayList<>(),utmSource, utmMedium, utmCampaign, utmContent, entityProductParams);
            report2 = reportsDao.getFunnelV3ReportData(filterForm.getEntity(),minAge, maxAge, requestType, cardType, fromDate1, toDate1, "B", products.stream().map(Product::getId).collect(Collectors.toList()),base, funnelConfiguration != null && funnelConfiguration.getSteps() != null && !funnelConfiguration.getSteps().isEmpty() ? funnelConfiguration.getSteps().stream().map(EntityExtranetConfiguration.FunnelStep::getStepId).collect(Collectors.toList()) : new ArrayList<>(),utmSource, utmMedium, utmCampaign, utmContent, entityProductParams);

            data.setLeftReport(new FunnelReportData.SideReport(fromDate1, toDate1, "Champion", new ArrayList<>()));
            data.setRightReport(new FunnelReportData.SideReport(fromDate1, toDate1, "Challenger", new ArrayList<>()));

            totalUsuariosVisitas1 = getUsuariosVisitas(fromDate1, toDate1, service, filterForm.getEntity(), true, productCategoryId);
            totalUsuariosVisitas2 = getUsuariosVisitas(fromDate1, toDate1, service, filterForm.getEntity(), false, productCategoryId);
        }else{
            if(fromDate2 == null || toDate2 == null){
                toDate2 = fromDate1;
                Calendar cal2 = Calendar.getInstance();
                cal2.setTime(fromDate1);
                cal2.add(Calendar.DATE, -FunnelReportFilterForm.MAX_DAYS_FILTER);
                fromDate2 = cal2.getTime();
            }
            report1 = reportsDao.getFunnelV3ReportData(filterForm.getEntity(),minAge, maxAge, requestType, cardType, fromDate1, toDate1, null, products.stream().map(Product::getId).collect(Collectors.toList()),base,funnelConfiguration != null && funnelConfiguration.getSteps() != null && !funnelConfiguration.getSteps().isEmpty() ? funnelConfiguration.getSteps().stream().map(EntityExtranetConfiguration.FunnelStep::getStepId).collect(Collectors.toList()) : new ArrayList<>(),utmSource, utmMedium, utmCampaign, utmContent, entityProductParams);
            report2 = reportsDao.getFunnelV3ReportData(filterForm.getEntity(),minAge, maxAge, requestType, cardType, fromDate2, toDate2, null, products.stream().map(Product::getId).collect(Collectors.toList()),base, funnelConfiguration != null && funnelConfiguration.getSteps() != null && !funnelConfiguration.getSteps().isEmpty() ? funnelConfiguration.getSteps().stream().map(EntityExtranetConfiguration.FunnelStep::getStepId).collect(Collectors.toList()) : new ArrayList<>(),utmSource, utmMedium, utmCampaign, utmContent, entityProductParams);
            data.setLeftReport(new FunnelReportData.SideReport(fromDate1, toDate1, "Periodo 1", new ArrayList<>()));
            data.setRightReport(new FunnelReportData.SideReport(fromDate2, toDate2, "Periodo 2", new ArrayList<>()));

            totalUsuariosVisitas1 = getUsuariosVisitas(fromDate1, toDate1, service, filterForm.getEntity(), null, productCategoryId);
            if (fromDate2 != null) {
                totalUsuariosVisitas2 = getUsuariosVisitas(fromDate2, toDate2, service, filterForm.getEntity(), null, productCategoryId);
            } else {
                totalUsuariosVisitas2 = totalUsuariosVisitas1;
            }
        }

        Double count = -1.0;
//

        Integer lastStepLeft = 0;
        Integer lastStepRight = 0;


        data.getLeftReport().getSteps(). add(new FunnelReportData.Step(count, "Visitas", totalUsuariosVisitas1, 100.0, 100.0, true));
        data.getRightReport().getSteps().add( new FunnelReportData.Step(count, "Visitas", totalUsuariosVisitas2, 100.0, 100.0, true));
        count++;

        for (EntityExtranetConfiguration.FunnelStep step : funnelConfiguration.getSteps()) {
            if(step.getStepId() == null) continue;
            FunnelStep stepConfiguration = funnelSteps.stream().filter(e -> e.getId().intValue() == step.getStepId()).findFirst().orElse(null);
            if(stepConfiguration == null) continue;
            if(count == null) count = 0.0;
            else count++;

            switch (step.getStepId()){
                case FunnelStep.REGISTERED:
                    data.getLeftReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report1.getStep1(), this.getPercentageOfTotal(report1.getStep1(), totalUsuariosVisitas1), this.getPercentageOfTotal(report1.getStep1(), totalUsuariosVisitas1), report1.getStep1() != null));
                    data.getRightReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report2.getStep1(), this.getPercentageOfTotal(report2.getStep1(), totalUsuariosVisitas2), this.getPercentageOfTotal(report2.getStep1(), totalUsuariosVisitas2), report2.getStep1() != null));
                    lastStepLeft = report1.getStep1();
                    lastStepRight = report2.getStep1();
                    break;
                case FunnelStep.PRE_EVALUATION_APPROVED:
                    data.getLeftReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(),report1.getStep2(), this.getPercentageOfTotal(report1.getStep2(), lastStepLeft), this.getPercentageOfTotal(report1.getStep2(), totalUsuariosVisitas1), report1.getStep2() != null));
                    data.getRightReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(),report2.getStep2(), this.getPercentageOfTotal(report2.getStep2(), lastStepRight), this.getPercentageOfTotal(report2.getStep2(), totalUsuariosVisitas2), report2.getStep2() != null));
                    lastStepLeft = report1.getStep2();
                    lastStepRight = report2.getStep2();
                    break;
                case FunnelStep.PIN_VALIDATED:
                    data.getLeftReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report1.getStep3(), this.getPercentageOfTotal(report1.getStep3(), lastStepLeft), this.getPercentageOfTotal(report1.getStep3(), totalUsuariosVisitas1), report1.getStep3() != null));
                    data.getRightReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report2.getStep3(), this.getPercentageOfTotal(report2.getStep3(), lastStepRight), this.getPercentageOfTotal(report2.getStep3(), totalUsuariosVisitas2), report2.getStep3() != null));
                    lastStepLeft = report1.getStep3();
                    lastStepRight = report2.getStep3();
                    break;
                case FunnelStep.APPROVED_VALIDATION:
                    data.getLeftReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report1.getStep4(), this.getPercentageOfTotal(report1.getStep4(), lastStepLeft), this.getPercentageOfTotal(report1.getStep4(), totalUsuariosVisitas1), report1.getStep4() != null));
                    data.getRightReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report2.getStep4(), this.getPercentageOfTotal(report2.getStep4(),lastStepRight), this.getPercentageOfTotal(report2.getStep4(), totalUsuariosVisitas2), report2.getStep4() != null));
                    lastStepLeft = report1.getStep4();
                    lastStepRight = report2.getStep4();
                    break;
                case FunnelStep.REQUEST_COMPLETE:
                    data.getLeftReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report1.getStep5(), this.getPercentageOfTotal(report1.getStep5(), lastStepLeft), this.getPercentageOfTotal(report1.getStep5(), totalUsuariosVisitas1), report1.getStep5() != null));
                    data.getRightReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report2.getStep5(), this.getPercentageOfTotal(report2.getStep5(), lastStepRight), this.getPercentageOfTotal(report2.getStep5(), totalUsuariosVisitas2), report2.getStep5() != null));
                    lastStepLeft = report1.getStep5();
                    lastStepRight = report2.getStep5();
                    break;
                case FunnelStep.REQUEST_WITH_OFFER:
                    data.getLeftReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report1.getStep6(), this.getPercentageOfTotal(report1.getStep6(), lastStepLeft), this.getPercentageOfTotal(report1.getStep6(), totalUsuariosVisitas1), report1.getStep6() != null));
                    data.getRightReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report2.getStep6(), this.getPercentageOfTotal(report2.getStep6(), lastStepRight), this.getPercentageOfTotal(report2.getStep6(), totalUsuariosVisitas2), report2.getStep6() != null));
                    lastStepLeft = report1.getStep6();
                    lastStepRight = report2.getStep6();
                    break;
                case FunnelStep.ACCEPTED_OFFER:
                    data.getLeftReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report1.getStep7(), this.getPercentageOfTotal(report1.getStep7(), lastStepLeft), this.getPercentageOfTotal(report1.getStep7(), totalUsuariosVisitas1), report1.getStep7() != null));
                    data.getRightReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report2.getStep7(), this.getPercentageOfTotal(report2.getStep7(), lastStepRight), this.getPercentageOfTotal(report2.getStep7(), totalUsuariosVisitas2), report2.getStep7() != null));
                    lastStepLeft = report1.getStep7();
                    lastStepRight = report2.getStep7();
                    break;
                case FunnelStep.VALIDATION:
                    data.getLeftReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report1.getStep8(), this.getPercentageOfTotal(report1.getStep8(), lastStepLeft), this.getPercentageOfTotal(report1.getStep8(), totalUsuariosVisitas1), report1.getStep8() != null));
                    data.getRightReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report2.getStep8(), this.getPercentageOfTotal(report2.getStep8(), lastStepRight), this.getPercentageOfTotal(report2.getStep8(), totalUsuariosVisitas2), report2.getStep8() != null));
                    lastStepLeft = report1.getStep8();
                    lastStepRight = report2.getStep8();
                    break;
                case FunnelStep.SIGNATURE:
                    data.getLeftReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report1.getStep9(), this.getPercentageOfTotal(report1.getStep9(), lastStepLeft), this.getPercentageOfTotal(report1.getStep9(), totalUsuariosVisitas1), report1.getStep9() != null));
                    data.getRightReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report2.getStep9(), this.getPercentageOfTotal(report2.getStep9(), lastStepRight), this.getPercentageOfTotal(report2.getStep9(), totalUsuariosVisitas2), report2.getStep9() != null));
                    lastStepLeft = report1.getStep9();
                    lastStepRight = report2.getStep9();
                    break;
                case FunnelStep.VERIFICATION:
                    data.getLeftReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report1.getStep10(), this.getPercentageOfTotal(report1.getStep10(), lastStepLeft), this.getPercentageOfTotal(report1.getStep10(), totalUsuariosVisitas1), report1.getStep10() != null));
                    data.getRightReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report2.getStep10(), this.getPercentageOfTotal(report2.getStep10(), lastStepRight), this.getPercentageOfTotal(report2.getStep10(), totalUsuariosVisitas2), report2.getStep10() != null));
                    lastStepLeft = report1.getStep10();
                    lastStepRight = report2.getStep10();
                    break;
                case FunnelStep.APPROBATION:
                    data.getLeftReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report1.getStep11(), this.getPercentageOfTotal(report1.getStep11(), lastStepLeft), this.getPercentageOfTotal(report1.getStep11(), totalUsuariosVisitas1), report1.getStep11() != null));
                    data.getRightReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report2.getStep11(), this.getPercentageOfTotal(report2.getStep11(), lastStepRight), this.getPercentageOfTotal(report2.getStep11(), totalUsuariosVisitas2), report2.getStep11() != null));
                    lastStepLeft = report1.getStep11();
                    lastStepRight = report2.getStep11();
                    break;
                case FunnelStep.DISBURSEMENT:
                    data.getLeftReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report1.getStep12(), this.getPercentageOfTotal(report1.getStep12(), lastStepLeft), this.getPercentageOfTotal(report1.getStep12(), totalUsuariosVisitas1), report1.getStep12() != null));
                    data.getRightReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report2.getStep12(), this.getPercentageOfTotal(report2.getStep12(), lastStepRight), this.getPercentageOfTotal(report2.getStep12(), totalUsuariosVisitas2), report2.getStep12() != null));
                    lastStepLeft = report1.getStep12();
                    lastStepRight = report2.getStep12();
                    break;
                case FunnelStep.DISBURSED:
                    data.getLeftReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report1.getStep13(), this.getPercentageOfTotal(report1.getStep13(), lastStepLeft), this.getPercentageOfTotal(report1.getStep13(), totalUsuariosVisitas1), report1.getStep13() != null));
                    data.getRightReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report2.getStep13(), this.getPercentageOfTotal(report2.getStep13(), lastStepRight), this.getPercentageOfTotal(report2.getStep13(), totalUsuariosVisitas2), report2.getStep13() != null));
                    lastStepLeft = report1.getStep13();
                    lastStepRight = report2.getStep13();
                    break;
                case FunnelStep.HOUSING_ADDRESS:
                    data.getLeftReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report1.getStep14(), this.getPercentageOfTotal(report1.getStep14(), lastStepLeft), this.getPercentageOfTotal(report1.getStep14(), totalUsuariosVisitas1), report1.getStep14() != null));
                    data.getRightReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report2.getStep14(), this.getPercentageOfTotal(report2.getStep14(), lastStepRight), this.getPercentageOfTotal(report2.getStep14(), totalUsuariosVisitas2), report2.getStep14() != null));
                    lastStepLeft = report1.getStep14();
                    lastStepRight = report2.getStep14();
                    break;
                case FunnelStep.REQUEST_FINALIZED:
                    data.getLeftReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report1.getStep15(), this.getPercentageOfTotal(report1.getStep15(), lastStepLeft), this.getPercentageOfTotal(report1.getStep15(), totalUsuariosVisitas1), report1.getStep7() != null));
                    data.getRightReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report2.getStep15(), this.getPercentageOfTotal(report2.getStep15(), lastStepRight), this.getPercentageOfTotal(report2.getStep15(), totalUsuariosVisitas2), report2.getStep7() != null));
                    lastStepLeft = report1.getStep15();
                    lastStepRight = report2.getStep15();
                    break;
                case FunnelStep.COMMITMENT_GENERATED:
                    data.getLeftReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report1.getStep17(), this.getPercentageOfTotal(report1.getStep17(), lastStepLeft), this.getPercentageOfTotal(report1.getStep17(), totalUsuariosVisitas1), report1.getStep17() != null));
                    data.getRightReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report2.getStep17(), this.getPercentageOfTotal(report2.getStep17(), lastStepRight), this.getPercentageOfTotal(report2.getStep17(), totalUsuariosVisitas2), report2.getStep17() != null));
                    lastStepLeft = report1.getStep17();
                    lastStepRight = report2.getStep17();
                    break;
                case FunnelStep.COMMITMENT_PAID:
                    data.getLeftReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report1.getStep18(), this.getPercentageOfTotal(report1.getStep18(), lastStepLeft), this.getPercentageOfTotal(report1.getStep18(), totalUsuariosVisitas1), report1.getStep18() != null));
                    data.getRightReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report2.getStep18(), this.getPercentageOfTotal(report2.getStep18(), lastStepRight), this.getPercentageOfTotal(report2.getStep18(), totalUsuariosVisitas2), report2.getStep18() != null));
                    lastStepLeft = report1.getStep18();
                    lastStepRight = report2.getStep18();
                    break;
                case FunnelStep.PRE_LOAN_APPLICATION_REGISTER:
                    data.getLeftReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report1.getStep21(), this.getPercentageOfTotal(report1.getStep21(), lastStepLeft), this.getPercentageOfTotal(report1.getStep21(), totalUsuariosVisitas1), report1.getStep21() != null));
                    data.getRightReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report2.getStep21(), this.getPercentageOfTotal(report2.getStep21(), lastStepRight), this.getPercentageOfTotal(report2.getStep21(), totalUsuariosVisitas2), report2.getStep21() != null));
                    lastStepLeft = report1.getStep21();
                    lastStepRight = report2.getStep21();
                    break;
                case FunnelStep.REGISTERED_ALFIN:
                    data.getLeftReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report1.getStep22(), this.getPercentageOfTotal(report1.getStep22(), lastStepLeft), this.getPercentageOfTotal(report1.getStep22(), totalUsuariosVisitas1), report1.getStep22() != null));
                    data.getRightReport().getSteps().add(new FunnelReportData.Step(count, step.getName() != null ? step.getName() : stepConfiguration.getName(), report2.getStep22(), this.getPercentageOfTotal(report2.getStep22(), lastStepRight), this.getPercentageOfTotal(report2.getStep22(), totalUsuariosVisitas2), report2.getStep22() != null));
                    lastStepLeft = report1.getStep22();
                    lastStepRight = report2.getStep22();
                    break;
            }
        }


        return data;
    }

    private double getPercentageOfTotal(Integer part, Integer total) {
        if(part == null || total == null)
            return 0.0;
        if (total == 0)
            return 0.0;

        return part * 100.0 / total;
    }

    public int getUsuariosVisitas(Date from, Date to, AnalyticsReporting service, int entityId, Boolean champion, Integer productCategory) throws IOException, ParseException {
        // Fix form BAZ. If not champion/challenger, the total vistis is the sum
        if(entityId == Entity.AZTECA && champion == null){
            int championVisit = getUsuariosVisitas(from, to, service, entityId, true, productCategory);
            int challengerVisit = getUsuariosVisitas(from, to, service, entityId, false, productCategory);
            return championVisit + challengerVisit;
        }
        String periodFrom = format.format(from);
        if(to == null) {
            Calendar calDateTo = Calendar.getInstance();
            calDateTo.setTime(from);
            calDateTo.add(Calendar.DATE, BanbifFunnelReportFilterForm.MAX_DAYS_FILTER);
            to = calDateTo.getTime();
        }
        String periodTo = format.format(to);
        DateRange dateRange = new DateRange().setStartDate(periodFrom).setEndDate(periodTo);

        String analyticViewId;
        switch (entityId){
            case Entity.BANBIF:
                analyticViewId = Configuration.GA_BANBIF;
                break;
            case Entity.AZTECA:
                if(productCategory == ProductCategory.GATEWAY){
                    analyticViewId = Configuration.GA_AZTECA_COBRANZA;
                }else if(productCategory == ProductCategory.CUENTA_BANCARIA){
                    analyticViewId = Configuration.GA_AZTECA_CUENTA;
                }else{
                    analyticViewId = Configuration.GA_AZTECA;
                }
            break;
            case Entity.PRISMA:
                analyticViewId = Configuration.GA_PRISMA;
                break;
            default:
                analyticViewId = Configuration.GA_MARKETPLACE;
                break;
        }

        // If it need to read events
        if(Arrays.asList(Entity.AZTECA, Entity.BANBIF, Entity.PRISMA).contains(entityId)){

            // Fix for Azteca to send the missed data between 10/07 to 15/07
            int missedViewsToAdd = 0;
            if(entityId == Entity.AZTECA && productCategory == ProductCategory.CONSUMO ){
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Map<Date, Integer> missedViewsAzteca = new HashMap<>();
                if(champion){
                    missedViewsAzteca.put(sdf.parse("17/06/2021"), 1371);
                    missedViewsAzteca.put(sdf.parse("18/06/2021"), 1198);
                    missedViewsAzteca.put(sdf.parse("19/06/2021"), 1020);
                    missedViewsAzteca.put(sdf.parse("20/06/2021"), 755);
                    missedViewsAzteca.put(sdf.parse("21/06/2021"), 1301);
                    missedViewsAzteca.put(sdf.parse("22/06/2021"), 1290);
                    missedViewsAzteca.put(sdf.parse("23/06/2021"), 1134);
                    missedViewsAzteca.put(sdf.parse("24/06/2021"), 1098);
                    missedViewsAzteca.put(sdf.parse("25/06/2021"), 3341);
                    missedViewsAzteca.put(sdf.parse("26/06/2021"), 1667);
                    missedViewsAzteca.put(sdf.parse("27/06/2021"), 1076);
                    missedViewsAzteca.put(sdf.parse("28/06/2021"), 1474);
                    missedViewsAzteca.put(sdf.parse("29/06/2021"), 1231);
                    missedViewsAzteca.put(sdf.parse("30/06/2021"), 1276);
                    missedViewsAzteca.put(sdf.parse("30/06/2021"), 1276);
                    missedViewsAzteca.put(sdf.parse("01/07/2021"), 2110);
                    missedViewsAzteca.put(sdf.parse("02/07/2021"), 11584);
                    missedViewsAzteca.put(sdf.parse("03/07/2021"), 3337);
                    missedViewsAzteca.put(sdf.parse("04/07/2021"), 2137);
                    missedViewsAzteca.put(sdf.parse("05/07/2021"), 2684);
                    missedViewsAzteca.put(sdf.parse("06/07/2021"), 2572);
                    missedViewsAzteca.put(sdf.parse("07/07/2021"), 2551);
                    missedViewsAzteca.put(sdf.parse("08/07/2021"), 2819);
                    missedViewsAzteca.put(sdf.parse("09/07/2021"), 2799);
                    missedViewsAzteca.put(sdf.parse("10/07/2021"), 8350);
                    missedViewsAzteca.put(sdf.parse("11/07/2021"), 2674);
                    missedViewsAzteca.put(sdf.parse("12/07/2021"), 4213);
                    missedViewsAzteca.put(sdf.parse("13/07/2021"), 3228);
                    missedViewsAzteca.put(sdf.parse("14/07/2021"), 2742);
                    missedViewsAzteca.put(sdf.parse("15/07/2021"), 3778);
                }else{
                    missedViewsAzteca.put(sdf.parse("17/06/2021"), -1371);
                    missedViewsAzteca.put(sdf.parse("18/06/2021"), -1198);
                    missedViewsAzteca.put(sdf.parse("19/06/2021"), -1020);
                    missedViewsAzteca.put(sdf.parse("20/06/2021"), -755);
                    missedViewsAzteca.put(sdf.parse("21/06/2021"), -1301);
                    missedViewsAzteca.put(sdf.parse("22/06/2021"), -1290);
                    missedViewsAzteca.put(sdf.parse("23/06/2021"), -1134);
                    missedViewsAzteca.put(sdf.parse("24/06/2021"), -1098);
                    missedViewsAzteca.put(sdf.parse("25/06/2021"), -3341);
                    missedViewsAzteca.put(sdf.parse("26/06/2021"), -1667);
                    missedViewsAzteca.put(sdf.parse("27/06/2021"), -1076);
                    missedViewsAzteca.put(sdf.parse("28/06/2021"), -1474);
                    missedViewsAzteca.put(sdf.parse("29/06/2021"), -1231);
                    missedViewsAzteca.put(sdf.parse("30/06/2021"), -1276);
                    missedViewsAzteca.put(sdf.parse("30/06/2021"), -1276);
                    missedViewsAzteca.put(sdf.parse("01/07/2021"), -2110);
                    missedViewsAzteca.put(sdf.parse("02/07/2021"), -11584);
                    missedViewsAzteca.put(sdf.parse("03/07/2021"), -3337);
                    missedViewsAzteca.put(sdf.parse("04/07/2021"), -2137);
                    missedViewsAzteca.put(sdf.parse("05/07/2021"), -2684);
                    missedViewsAzteca.put(sdf.parse("06/07/2021"), -2572);
                    missedViewsAzteca.put(sdf.parse("07/07/2021"), -2551);
                    missedViewsAzteca.put(sdf.parse("08/07/2021"), -2819);
                    missedViewsAzteca.put(sdf.parse("09/07/2021"), -550);
                }
                
                for (Map.Entry<Date, Integer> entry : missedViewsAzteca.entrySet()) {
                    if((entry.getKey().after(from) && entry.getKey().before(to)) ||
                            sdf.format(entry.getKey()).equalsIgnoreCase(sdf.format(from)) ||
                            sdf.format(entry.getKey()).equalsIgnoreCase(sdf.format(to))){
                        missedViewsToAdd += entry.getValue();
                    }
                }
            }else if(entityId == Entity.AZTECA && productCategory == ProductCategory.CUENTA_BANCARIA ){
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Map<Date, Integer> missedViewsAzteca = new HashMap<>();
                if(champion) {
                    missedViewsAzteca.put(sdf.parse("25/12/2022"), 250);
                    missedViewsAzteca.put(sdf.parse("24/12/2022"), 271);
                    missedViewsAzteca.put(sdf.parse("23/12/2022"), 283);
                    missedViewsAzteca.put(sdf.parse("22/12/2022"), 304);
                    missedViewsAzteca.put(sdf.parse("21/12/2022"), 311);
                    missedViewsAzteca.put(sdf.parse("20/12/2022"), 326);
                    missedViewsAzteca.put(sdf.parse("19/12/2022"), 319);
                    missedViewsAzteca.put(sdf.parse("18/12/2022"), 257);
                    missedViewsAzteca.put(sdf.parse("17/12/2022"), 282);
                    missedViewsAzteca.put(sdf.parse("16/12/2022"), 399);
                    missedViewsAzteca.put(sdf.parse("15/12/2022"), 552);
                    missedViewsAzteca.put(sdf.parse("14/12/2022"), 287);
                    missedViewsAzteca.put(sdf.parse("13/12/2022"), 130);
                    missedViewsAzteca.put(sdf.parse("12/12/2022"), 120);
                    missedViewsAzteca.put(sdf.parse("11/12/2022"), 65);
                    missedViewsAzteca.put(sdf.parse("10/12/2022"), 111);
                    missedViewsAzteca.put(sdf.parse("09/12/2022"), 94);
                    missedViewsAzteca.put(sdf.parse("08/12/2022"), 89);
                    missedViewsAzteca.put(sdf.parse("07/12/2022"), 121);
                    missedViewsAzteca.put(sdf.parse("06/12/2022"), 158);
                    missedViewsAzteca.put(sdf.parse("05/12/2022"), 157);
                    missedViewsAzteca.put(sdf.parse("04/12/2022"), 93);
                    missedViewsAzteca.put(sdf.parse("03/12/2022"), 121);
                    missedViewsAzteca.put(sdf.parse("02/12/2022"), 139);
                    missedViewsAzteca.put(sdf.parse("01/12/2022"), 158);
                    missedViewsAzteca.put(sdf.parse("30/11/2022"), 170);
                    missedViewsAzteca.put(sdf.parse("29/11/2022"), 156);
                    missedViewsAzteca.put(sdf.parse("28/11/2022"), 166);
                    missedViewsAzteca.put(sdf.parse("27/11/2022"), 115);
                    missedViewsAzteca.put(sdf.parse("26/11/2022"), 164);
                    missedViewsAzteca.put(sdf.parse("25/11/2022"), 214);
                    missedViewsAzteca.put(sdf.parse("24/11/2022"), 247);
                    missedViewsAzteca.put(sdf.parse("23/11/2022"), 468);
                    missedViewsAzteca.put(sdf.parse("22/11/2022"), 559);
                    missedViewsAzteca.put(sdf.parse("21/11/2022"), 690);
                    missedViewsAzteca.put(sdf.parse("20/11/2022"), 1645);
                    missedViewsAzteca.put(sdf.parse("19/11/2022"), 1758);
                    missedViewsAzteca.put(sdf.parse("18/11/2022"), 1890);
                    missedViewsAzteca.put(sdf.parse("17/11/2022"), 1947);
                    missedViewsAzteca.put(sdf.parse("16/11/2022"), 2049);
                    missedViewsAzteca.put(sdf.parse("15/11/2022"), 1546);
                    missedViewsAzteca.put(sdf.parse("14/11/2022"), 1399);
                    missedViewsAzteca.put(sdf.parse("13/11/2022"), 2828);
                    missedViewsAzteca.put(sdf.parse("12/11/2022"), 3237);
                    missedViewsAzteca.put(sdf.parse("11/11/2022"), 3221);
                    missedViewsAzteca.put(sdf.parse("10/11/2022"), 3495);
                }

                for (Map.Entry<Date, Integer> entry : missedViewsAzteca.entrySet()) {
                    if((entry.getKey().after(from) && entry.getKey().before(to)) ||
                            sdf.format(entry.getKey()).equalsIgnoreCase(sdf.format(from)) ||
                            sdf.format(entry.getKey()).equalsIgnoreCase(sdf.format(to))){
                        missedViewsToAdd += entry.getValue();
                    }
                }
            }

            String eventName = "viewLanding";

            if(entityId == Entity.AZTECA && productCategory == ProductCategory.GATEWAY){
                eventName = "viewLandingCobranza";
            }

            if(entityId == Entity.AZTECA && productCategory == ProductCategory.CUENTA_BANCARIA){
                eventName = "viewLandingCuenta";
            }

            if(entityId == Entity.AZTECA && productCategory == ProductCategory.CONSEJ0) {
                eventName = "viewLandingConsejo";
            }

            if(entityId == Entity.AZTECA && productCategory == ProductCategory.VALIDACION_IDENTIDAD){
                eventName = "brandingLandingIdentidadView";
            }

            List<DimensionFilterClause> filters = new ArrayList<>();
            List<DimensionFilter> filtersList = new ArrayList<>();
            if(champion != null){
                if(entityId == Entity.BANBIF)
                    filtersList.add(new DimensionFilter().setDimensionName("ga:eventAction").setOperator("EXACT").setExpressions(Arrays.asList(eventName + (champion ? "A" : "B"))));
                else
                    filtersList.add(new DimensionFilter().setDimensionName("ga:eventAction").setOperator("EXACT").setExpressions(Arrays.asList(eventName + (champion ? "" : "B"))));
            }else{
                filtersList.add(new DimensionFilter().setDimensionName("ga:eventAction").setOperator("EXACT").setExpressions(Arrays.asList(eventName)));
            }
            filters.add(new DimensionFilterClause().setFilters(filtersList));

            List<Report> reports = googleAnalyticsReportingService.getReport(analyticViewId, service, Arrays.asList(dateRange), Arrays.asList(new Metric().setExpression("ga:uniqueEvents")), filters);

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

            return totalGAResults.get("ga:uniqueEvents").intValue() + missedViewsToAdd;
        }else{
            List<Report> reports = googleAnalyticsReportingService
                    .getReport(analyticViewId, service, dateRange, new Metric().setExpression("ga:users").setAlias("Usuarios (visitas)"));

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

            return Configuration.hostEnvIsProduction() ?
                    (totalGAResults.get("Usuarios (visitas)") != null? totalGAResults.get("Usuarios (visitas)").intValue() : 0) :
                    100;
        }

    }

    @RequestMapping(value = "/" + URL + "/report/export", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:funnelv3:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showReportingLoansFunnelV3(FunnelReportFilterForm filterForm) throws Exception {
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String requestType = null;
        String cardType = null;
        Integer minAge = null;
        Integer maxAge = null;
        Date from = filterForm.getFromDate1() != null ? sdf.parse(filterForm.getFromDate1()) : null;
        Date to = filterForm.getToDate1() != null ? sdf.parse(filterForm.getToDate1()) : null;
        Date from2 = filterForm.getFromDate2() != null ? sdf.parse(filterForm.getFromDate2()) : null;
        Date to2 = filterForm.getToDate2() != null ? sdf.parse(filterForm.getToDate2()) : null;

        List<String> utmSource = filterForm.getUtmSource() != null && !filterForm.getUtmSource().isEmpty() ? gson.fromJson(filterForm.getUtmSource(), new TypeToken<ArrayList<String>>() {}.getType()) : null;
        List<String> utmCampaign = filterForm.getUtmCampaign() != null && !filterForm.getUtmCampaign().isEmpty() ? gson.fromJson(filterForm.getUtmCampaign(), new TypeToken<ArrayList<String>>() {}.getType()) : null;
        List<String> utmContent = filterForm.getUtmContent() != null && !filterForm.getUtmContent().isEmpty() ? gson.fromJson(filterForm.getUtmContent(), new TypeToken<ArrayList<String>>() {}.getType()) : null;
        List<String> utmMedium = filterForm.getUtmMedium() != null && !filterForm.getUtmMedium().isEmpty() ? gson.fromJson(filterForm.getUtmMedium(), new TypeToken<ArrayList<String>>() {}.getType()) : null;
        List<Integer> entityProductParams = filterForm.getEntityProductParam() != null && !filterForm.getEntityProductParam().isEmpty() ? gson.fromJson(filterForm.getEntityProductParam(), new TypeToken<ArrayList<Integer>>() {}.getType()) : null;

        if(entityProductParams != null && !entityProductParams.isEmpty()){
            if(entityProductParams.stream().anyMatch(e -> e.intValue() == -1)) entityProductParams = new ArrayList<>();
            entityProductParams.removeIf(e -> e == null || e == -1);
        }
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


        if(filterForm.getAnalisis() != null && filterForm.getAnalisis().equalsIgnoreCase("champion")){
            filterForm.setFromDate2(null);
            filterForm.setToDate2(null);
            from2 = null;
            to2 = null;
        }

        Integer[] entities = { entityExtranetService.getPrincipalEntity().getId() };
        Integer[] countries = { entityExtranetService.getPrincipalEntity().getCountryId() };

//        Integer[] products = gson.fromJson(filterForm.getProducto(), Integer[].class);
//        if(Arrays.stream(products).anyMatch(e -> e == -1)){
//            products = entityExtranetService.getLoggedUserEntity().getProducts().stream().map(Product::getId).collect(Collectors.toList()).toArray(new Integer[0]);
//        }

        Integer base = filterForm.getBase();
        Integer[] steps = {};

        EntityExtranetConfiguration.FunnelConfiguration funnelConfiguration = null;
        EntityExtranetConfiguration extranetConfiguration = brandingService.getExtranetBrandingAsJson(loggedUserEntity.getPrincipalEntity().getId());

        Integer productCategoryId = null;
        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(),EXTRANET_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());
        //OBTENER PRODUCTO
        if(filterForm.getProducto() != null) {
            FunnelReportFilterForm finalFilterForm = filterForm;
            products = products.stream().filter(e -> e.getId().equals(Integer.valueOf(finalFilterForm.getProducto()))).collect(Collectors.toList());
        }
        if(!products.isEmpty()) productCategoryId = products.get(0).getProductCategoryId();
        if(extranetConfiguration != null && extranetConfiguration.getFunnelConfiguration() != null && !extranetConfiguration.getFunnelConfiguration().isEmpty() && loggedUserEntity != null && productCategoryId!= null) {
            Integer finalProductCategoryId = productCategoryId;
            funnelConfiguration = extranetConfiguration.getFunnelConfiguration().stream().filter(e -> e.getProductCategoryId().equals(finalProductCategoryId)).findFirst().orElse(null);
        }
        if(extranetConfiguration != null && extranetConfiguration.getFunnelConfiguration() != null && !extranetConfiguration.getFunnelConfiguration().isEmpty() && loggedUserEntity != null && productCategoryId != null) {
            Integer finalProductCategoryId1 = productCategoryId;
            funnelConfiguration = extranetConfiguration.getFunnelConfiguration().stream().filter(e -> e.getProductCategoryId().equals(finalProductCategoryId1)).findFirst().orElse(null);
        }

        if(funnelConfiguration != null && funnelConfiguration.getSteps() != null && !funnelConfiguration.getSteps().isEmpty()){
            steps =  funnelConfiguration.getSteps().stream().map(EntityExtranetConfiguration.FunnelStep::getStepId).collect(Collectors.toList()).toArray(new Integer[funnelConfiguration.getSteps().size()]);
        }
        Integer[] productsArray = products.stream().map(Product::getId).toArray(Integer[]::new);
        reportsService.createReporteFunnelV3(
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
                productsArray,
                steps,
                base,
                WebApplication.ENTITY_EXTRANET,
                null,
                utmSource,
                utmCampaign,
                utmContent,
                utmMedium,
                entityProductParams
                );

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/" + URL + "/report/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:funnelv3:execute", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object showLoansLightReportList(ModelMap model) throws Exception {
        int userId = entityExtranetService.getLoggedUserEntity().getId();
            List<ReportProces> historicReports = reportsDao.getReportProcesHistoric(userId, com.affirm.common.model.catalog.Report.REPORTE_FUNNELV3, 0, 5, WebApplication.ENTITY_EXTRANET);
        model.addAttribute("loansLightHistoricReports", historicReports);
        return new ModelAndView("/entityExtranet/extranetReports :: loansLightFunnelResults");
    }

    @RequestMapping(value = "/" + URL + "/notes/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:report:funnelv3:view", type = RequiresPermissionOr403.Type.WEB)
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
        List<ExtranetNote> notes = entityExtranetService.getNotesFromMenu(ExtranetMenu.FUNNEL_MENU,loggedUserEntity.getPrincipalEntity() != null ? loggedUserEntity.getPrincipalEntity().getId() : null, offset, limit);
        model.addAttribute("notes", notes);
        return new ModelAndView("/entityExtranet/fragments/extranetNoteFragment :: list");
    }

    @RequestMapping(value = "/" + URL + "/notes/count", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:report:funnelv3:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object countNotes(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        Pair<Integer, Double> countAdSum = entityExtranetService.getNotesCount(ExtranetMenu.FUNNEL_MENU,locale);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("count", countAdSum.getLeft());

        return AjaxResponse.ok(new Gson().toJson(data));
    }

    @RequestMapping(value = "/" + URL + "/notes", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:report:funnelv3:view", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object createNote(ModelMap model, ExtranetNoteForm form) throws Exception {
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        if(form.getId() == null) entityExtranetService.insExtranetNote(ExtranetMenu.FUNNEL_MENU,form.getType(),form.getNote());
        else {
            ExtranetNote note = entityExtranetService.editExtranetNote(ExtranetMenu.FUNNEL_MENU, form.getId(),form.getType(),form.getNote());
            if(note == null) AjaxResponse.errorMessage("No existe el registro o no tiene permiso para modificarlo");
        }
        List<ExtranetNote> notes = entityExtranetService.getNotesFromMenu(ExtranetMenu.FUNNEL_MENU,loggedUserEntity.getPrincipalEntity() != null ? loggedUserEntity.getPrincipalEntity().getId() : null, 0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR);
        model.addAttribute("notes", notes);
        return new ModelAndView("/entityExtranet/fragments/extranetNoteFragment :: list");
    }


    @RequestMapping(value = "/" + URL + "/utm_values", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:report:funnelv3:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object getUTMValues(
            ModelMap model, Locale locale) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        UTMValue utmValue = entityExtranetService.getUTMValuesFromEntity(loggedUserEntity.getPrincipalEntity().getId());

        return AjaxResponse.ok(new Gson().toJson(utmValue));
    }

    @RequestMapping(value = "/" + URL + "/entity_products", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:report:funnelv3:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object getEntityProductValues(
            ModelMap model, Locale locale, @RequestParam Integer product) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        List<EntityProductParams> entityProductParams = catalogService.getEntityProductParams().stream().filter(e -> e.getEntity() != null && e.getEntity().getId().intValue() == loggedUserEntity.getPrincipalEntity().getId().intValue() && e.getProduct().getId().intValue() == product.intValue()).collect(Collectors.toList());

        List<Map<String, Object>> data = new ArrayList<>();

        for (EntityProductParams entityProductParam : entityProductParams) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("value", entityProductParam.getId());
            map.put("text", entityProductParam.getEntityProduct());
            data.add(map);
        }

        return AjaxResponse.ok(new Gson().toJson(data));
    }
}
