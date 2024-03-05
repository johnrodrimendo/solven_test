package com.affirm.entityExt.controller;

import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.model.form.EntityExtranetRegisterDisbursementForm;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.client.service.impl.EntityExtranetServiceImpl;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.AppointmentSchedule;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.BrandingService;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.ReportsService;
import com.affirm.common.service.UtilService;
import com.affirm.common.service.document.DocumentService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.entityExt.models.BanbifFunnelReportFilterForm;
import com.affirm.entityExt.models.PaginatorTableFilterForm;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller("entityExtranetResultPaginatorController")
public class EntityExtranetResultPaginatorController {
    public static final String URL = "extranet-paginator";

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CreditDAO creditDAO;
    @Autowired
    private EntityExtranetService entityExtranetService;
    @Autowired
    private ReportsService reportsService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private UtilService utilService;
    @Autowired
    private BrandingService brandingService;

    @RequestMapping(value = URL+"/leadsDelivered", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:leads:leadsDelivered:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showLeadsDelivered(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        int filterType = 2;
        Integer offset = 0;
        Integer limit = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        if(filter != null) limit = filter.getLimit();
        if(filter != null && filter.getPage() != null && limit != null) {
            offset = filter.getPage() * limit;
        }
        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }

        List<BanbifTcLeadLoan> data = creditDAO.getBanbifLeadCreditCardLoan(null,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                filterType, locale, offset, limit,searchValue);
        if(offset < 0) offset = 0;
        model.addAttribute("offset", offset);
        model.addAttribute("data", data);
        model.addAttribute("filterType", filterType);
        model.addAttribute("page", "leadsDelivered");
        model.addAttribute("title", "Leads entregados");
        return  new ModelAndView("/entityExtranet/extranetLeadsBanbif :: list");
    }

    @RequestMapping(value = URL+"/leadsToDeliver", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:leads:leadsToDeliver:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showLeadsToDeliver(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        int filterType = 1;
        Integer offset = 0;
        Integer limit = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        if(filter != null) limit = filter.getLimit();
        if(filter != null && filter.getPage() != null && limit != null) {
            offset = filter.getPage() * limit;
        }
        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }

        List<BanbifTcLeadLoan> data = creditDAO.getBanbifLeadCreditCardLoan(null,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null
                , filterType, locale, offset, limit,searchValue);

        if(offset < 0) offset = 0;
        model.addAttribute("offset", offset);
        model.addAttribute("data", data);
        model.addAttribute("filterType", filterType);
        model.addAttribute("page", "leadsToDeliver");
        model.addAttribute("title", "Leads");
        return  new ModelAndView("/entityExtranet/extranetLeadsBanbif :: list");
    }

    @RequestMapping(value = URL+"/pending", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:credit:pending:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showPending(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        Integer offset = 0;
        Integer limit = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        if(filter != null) limit = filter.getLimit();
        if(filter != null && filter.getPage() != null && limit != null) {
            offset = filter.getPage() * limit;
        }
        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.TO_VERIFY_CREDITS_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());


        List<CreditEntityExtranetPainter> credits = entityExtranetService.getCreditsToShow(ExtranetMenu.TO_VERIFY_CREDITS_MENU,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null
                , locale,offset, limit, searchValue, false,null,products.stream().map(Product::getId).collect(Collectors.toList()));

        Pair<List<Date>, Map<String, List<AppointmentSchedule>>> availableDates = scheduleAvailableDates(1);
        if(offset < 0) offset = 0;
        model.addAttribute("offset", offset);
        model.addAttribute("credits", credits);
        model.addAttribute("page", "pending");
        model.addAttribute("title", "Solicitudes a verificar");
        model.addAttribute("showAccesoScheduleAppointmentModal", credits.stream().anyMatch(c -> c.isShowAccesoScheduleAppointmentButton() || c.isShowAccesoReScheduleAppointmentButton()));
        model.addAttribute("availableDates", availableDates.getLeft());
        model.addAttribute("jsonSchedulesMap", new Gson().toJson(availableDates.getRight()));
        model.addAttribute("showButton", true);

        if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) return new ModelAndView("/entityExtranet/extranetCreditsBDS :: list");

        return new ModelAndView("/entityExtranet/extranetCredits :: list");
    }

    @RequestMapping(value = URL+"/toUpload", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:credit:toUpload:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showToUpload(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        Integer offset = 0;
        Integer limit = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        if(filter != null) limit = filter.getLimit();
        if(filter != null && filter.getPage() != null && limit != null) {
            offset = filter.getPage() * limit;
        }
        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.TO_UPLOAD_CREDITS_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());

        List<CreditEntityExtranetPainter> credits = entityExtranetService.getCreditsToShow(ExtranetMenu.TO_UPLOAD_CREDITS_MENU,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null
                , locale,offset,limit,                searchValue,
                false,
                null,
                products.stream().map(Product::getId).collect(Collectors.toList()));
        if(offset < 0) offset = 0;
        model.addAttribute("offset", offset);
        model.addAttribute("credits", credits);
        model.addAttribute("page", "toUpload");
        model.addAttribute("title", "Creación de cliente/crédito");
        model.addAttribute("showAccesoCreateButton", credits.stream().anyMatch(c -> c.isShowAccesoCreateButton()));
        model.addAttribute("showButton", true);

        if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) return new ModelAndView("/entityExtranet/extranetCreditsBDS :: list");

        return new ModelAndView("/entityExtranet/extranetCredits :: list");
    }

    @RequestMapping(value = URL+"/generated", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:credit:generated:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showGenerated(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        Integer offset = 0;
        Integer limit = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        Integer[] entityProductsParam = null;
        if(filter != null) limit = filter.getLimit();
        if(filter != null && filter.getPage() != null && limit != null) {
            offset = filter.getPage() * limit;
        }
        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
            if(filter.getEntityProductParam() != null) {
                entityProductsParam = new Gson().fromJson(filter.getEntityProductParam(), Integer[].class);
            }
            if(filter.getDisbursementType() != null) {
                Integer[] disbursementTypes = new Gson().fromJson(filter.getDisbursementType(), Integer[].class);
                if(disbursementTypes.length > 0){
                    List<Integer> entityProductParamsId = catalogService.getEntityProductParams().stream().filter(e -> e.getEntity().getId().equals(loggedUserEntity.getPrincipalEntity().getId()) && e.getExtranetCreditGeneration() != null && e.getExtranetCreditGeneration() && Arrays.stream(disbursementTypes).anyMatch(d->d.equals(e.getDisbursementType()))).collect(Collectors.toList()).stream().map(e->e.getId()).collect(Collectors.toList());
                    entityProductsParam = new Integer[entityProductParamsId.size()];
                    entityProductsParam = entityProductParamsId.toArray(entityProductsParam);
                }
            }
        }

        List<Integer> product = null;
        if(filter.getProduct() != null) {
            product = new Gson().fromJson(filter.getProduct(), new TypeToken<ArrayList<Integer>>(){}.getType());
        }

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.TO_DISBURSE_CREDITS_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());

        List<Integer> finalProduct = product;
        List<CreditEntityExtranetPainter> credits = entityExtranetService.getCreditsToShow(ExtranetMenu.TO_DISBURSE_CREDITS_MENU,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                locale,offset,limit,                searchValue,false,entityProductsParam,
                product != null ? products.stream().filter(e -> finalProduct.contains(e.getId())).map(Product::getId).collect(Collectors.toList()) : products.stream().map(Product::getId).collect(Collectors.toList())
                );


        if(offset < 0) offset = 0;
        model.addAttribute("offset", offset);
        model.addAttribute("credits", credits);
        model.addAttribute("showFinalDocumentationModal", credits.stream().anyMatch(c -> c.isShowAeluUploadFinalDocumentationButton() || c.isShowAccesoUploadFinalDocumentationButton()));
        model.addAttribute("showAeluInternalDisbursementModal", credits.stream().anyMatch(c -> c.isShowAeluInternalDisbursementButton()));
        model.addAttribute("page", "generated");
        model.addAttribute("title", "Créditos a desembolsar");
        model.addAttribute("registerDisbursementForm", new EntityExtranetRegisterDisbursementForm());
        if (loggedUserEntity.getEntities().get(0).getId() != null && loggedUserEntity.getEntities().get(0).getId() == Entity.BANCO_DEL_SOL) {
            JSONObject jsonHierarchy = personDao.getBancoDelSolEmployeeHierarchy(loggedUserEntity.getId());
            model.addAttribute("userHierarchy", jsonHierarchy.isNull("level1") && jsonHierarchy.isNull("level2") && jsonHierarchy.isNull("level3") ? null : jsonHierarchy);
        }
        model.addAttribute("showButton", true);

        model.addAttribute("tray", ExtranetMenu.TO_DISBURSE_CREDITS_MENU);
        EntityExtranetConfiguration extranetConfiguration = brandingService.getExtranetBrandingAsJson(loggedUserEntity.getPrincipalEntity().getId());
        if(extranetConfiguration != null && extranetConfiguration.getDisburseCreditPageConfiguration() != null && extranetConfiguration.getDisburseCreditPageConfiguration().getDetailConfiguration() != null)  return new ModelAndView("/entityExtranet/extranetPageWithDetail :: list");

        if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) return new ModelAndView("/entityExtranet/extranetCreditsBDS :: list");

        return new ModelAndView("/entityExtranet/extranetCredits :: list");
    }

    @RequestMapping(value = URL+"/generatedtc", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:credit:generatedtc:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showGeneratedTc(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        Integer offset = 0;
        Integer limit = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        if(filter != null) limit = filter.getLimit();
        if(filter != null && filter.getPage() != null && limit != null) {
            offset = filter.getPage() * limit;
        }
        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.TO_DELIVER_TC_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());

        List<CreditEntityExtranetPainter> credits = entityExtranetService.getCreditsToShow(ExtranetMenu.TO_DELIVER_TC_MENU,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                locale, offset, limit,
                searchValue,
                false,
                null,
                products.stream().map(Product::getId).collect(Collectors.toList()));
        if(offset < 0) offset = 0;
        model.addAttribute("offset", offset);
        model.addAttribute("credits", credits);
        model.addAttribute("showFinalDocumentationModal", credits.stream().anyMatch(c -> c.isShowAeluUploadFinalDocumentationButton() || c.isShowAccesoUploadFinalDocumentationButton()));
        model.addAttribute("showAeluInternalDisbursementModal", credits.stream().anyMatch(c -> c.isShowAeluInternalDisbursementButton()));
        model.addAttribute("page", "generatedtc");
        model.addAttribute("showButton", false);
        model.addAttribute("title", "Tarjetas a entregar");
        model.addAttribute("registerDisbursementForm", new EntityExtranetRegisterDisbursementForm());
        if (loggedUserEntity.getEntities().get(0).getId() != null && loggedUserEntity.getEntities().get(0).getId() == Entity.BANCO_DEL_SOL) {
            JSONObject jsonHierarchy = personDao.getBancoDelSolEmployeeHierarchy(loggedUserEntity.getId());
            model.addAttribute("userHierarchy", jsonHierarchy.isNull("level1") && jsonHierarchy.isNull("level2") && jsonHierarchy.isNull("level3") ? null : jsonHierarchy);
        }

        if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) return new ModelAndView("/entityExtranet/extranetCreditsBDS :: list");

        return new ModelAndView("/entityExtranet/extranetCredits :: list");
    }

    @RequestMapping(value = URL+"/terminated", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:credit:terminated:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showTerminated(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        Integer offset = 0;
        Integer limit = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        if(filter != null) limit = filter.getLimit();
        if(filter != null && filter.getPage() != null && limit != null) {
            offset = filter.getPage() * limit;
        }
        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        List<Integer> product = null;

        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }
        if(filter.getProduct() != null) {
            product = new Gson().fromJson(filter.getProduct(), new TypeToken<ArrayList<Integer>>(){}.getType());
        }

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.DISBURSEMENT_CREDITS_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());


        List<Integer> finalProduct = product;
        List<CreditEntityExtranetPainter> credits = entityExtranetService.getCreditsToShow(ExtranetMenu.DISBURSEMENT_CREDITS_MENU,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null
                , locale,offset,
                limit,
                searchValue,
                false,
                null,
                product != null ? products.stream().filter(e -> finalProduct.contains(e.getId())).map(Product::getId).collect(Collectors.toList()) : null
        );
        if(offset < 0) offset = 0;
        model.addAttribute("offset", offset);
        model.addAttribute("totalCredits", null);
        model.addAttribute("excludeSetTotals", true);
        model.addAttribute("sumCredits", credits.stream().filter(e-> e.getAmount() != null).mapToDouble(Credit::getAmount).sum());
        model.addAttribute("currency", credits.size() > 0 ? credits.get(0).getCurrency() : catalogService.getCountryParam(entityExtranetService.getLoggedUserEntity().getEntities().get(0).getCountryId()).getCurrency());
        model.addAttribute("credits", credits);
        model.addAttribute("page", "terminated");
        model.addAttribute("title", "Créditos desembolsados");
        model.addAttribute("showButton", true);
        if (loggedUserEntity.getEntities().get(0).getId() != null && loggedUserEntity.getEntities().get(0).getId() == Entity.BANCO_DEL_SOL) {
            JSONObject jsonHierarchy = personDao.getBancoDelSolEmployeeHierarchy(loggedUserEntity.getId());
            model.addAttribute("userHierarchy", jsonHierarchy.isNull("level1") && jsonHierarchy.isNull("level2") && jsonHierarchy.isNull("level3") ? null : jsonHierarchy);
        }

        model.addAttribute("tray", ExtranetMenu.DISBURSEMENT_CREDITS_MENU);
        EntityExtranetConfiguration extranetConfiguration = brandingService.getExtranetBrandingAsJson(loggedUserEntity.getPrincipalEntity().getId());
        if(extranetConfiguration != null && extranetConfiguration.getDisbursedCreditPageConfiguration() != null && extranetConfiguration.getDisbursedCreditPageConfiguration().getDetailConfiguration() != null)  return new ModelAndView("/entityExtranet/extranetPageWithDetail :: list");

        if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) return new ModelAndView("/entityExtranet/extranetCreditsBDS :: list");

        return new ModelAndView("/entityExtranet/extranetCredits :: list");
    }

    @RequestMapping(value = URL+"/callcenter", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:credit:callcenter:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showCallCenter(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        Integer offset = 0;
        Integer limit = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        if(filter != null) limit = filter.getLimit();
        if(filter != null && filter.getPage() != null && limit != null) {
            offset = filter.getPage() * limit;
        }
        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        List<CreditEntityExtranetPainter> credits = entityExtranetService.getCreditsToShow(ExtranetMenu.CALL_CENTER_MENU,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null
                , locale,offset,limit,                searchValue);
        if(offset < 0) offset = 0;
        model.addAttribute("offset", offset);
        model.addAttribute("credits", credits);
        model.addAttribute("showFinalDocumentationModal", credits.stream().anyMatch(c -> c.isShowAeluUploadFinalDocumentationButton() || c.isShowAccesoUploadFinalDocumentationButton()));
        model.addAttribute("showAeluInternalDisbursementModal", credits.stream().anyMatch(c -> c.isShowAeluInternalDisbursementButton()));
        model.addAttribute("page", "callcenter");
        model.addAttribute("title", "Call Centers");
        model.addAttribute("registerDisbursementForm", new EntityExtranetRegisterDisbursementForm());
        model.addAttribute("showButton", true);

        if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) return new ModelAndView("/entityExtranet/extranetCreditsBDS :: list");

        return new ModelAndView("/entityExtranet/extranetCredits :: list");
    }


    @RequestMapping(value = URL+"/terminatedtc", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:credit:terminatedtc:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showTerminatedTc(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        Integer offset = 0;
        Integer limit = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        if(filter != null) limit = filter.getLimit();
        if(filter != null && filter.getPage() != null && limit != null) {
            offset = filter.getPage() * limit;
        }
        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }
        if(offset < 0) offset = 0;
        model.addAttribute("offset", offset);
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.TO_UPLOAD_CREDITS_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());

        List<CreditEntityExtranetPainter> credits = entityExtranetService.getCreditsToShow(ExtranetMenu.DELIVERED_TC_MENU,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null
                , locale,offset,limit,
                searchValue,
                false,
                null,
                products.stream().map(Product::getId).collect(Collectors.toList()));

        model.addAttribute("totalCredits", credits.size());
        model.addAttribute("sumCredits", credits.stream().mapToDouble(c -> c.getAmount() != null ? c.getAmount() : 0.0).sum());
        model.addAttribute("currency", credits.size() > 0 ? credits.get(0).getCurrency() : catalogService.getCountryParam(entityExtranetService.getLoggedUserEntity().getEntities().get(0).getCountryId()).getCurrency());
        model.addAttribute("credits", credits);
        model.addAttribute("page", "terminatedtc");
        model.addAttribute("showButton", false);
        model.addAttribute("title", "Tarjetas entregadas");
        if (loggedUserEntity.getEntities().get(0).getId() != null && loggedUserEntity.getEntities().get(0).getId() == Entity.BANCO_DEL_SOL) {
            JSONObject jsonHierarchy = personDao.getBancoDelSolEmployeeHierarchy(loggedUserEntity.getId());
            model.addAttribute("userHierarchy", jsonHierarchy.isNull("level1") && jsonHierarchy.isNull("level2") && jsonHierarchy.isNull("level3") ? null : jsonHierarchy);
        }

        if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) return new ModelAndView("/entityExtranet/extranetCreditsBDS :: list");

        return new ModelAndView("/entityExtranet/extranetCredits :: list");
    }

    private Pair<List<Date>, Map<String, List<AppointmentSchedule>>> scheduleAvailableDates(int hoursAfter) throws Exception {

        List<Date> availableDatesFinal = new ArrayList<>();
        Map<String, List<AppointmentSchedule>> schedulesMap = new LinkedHashMap<>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        List<AppointmentSchedule> appointmentScheduleList = loanApplicationDao.getAvailableDates();
        List<Date> availableDates = appointmentScheduleList.stream().map(AppointmentSchedule::getDate).distinct().collect(Collectors.toList());

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, hoursAfter);
        Date afterHours = cal.getTime();

        for (Date availableDate1 : availableDates) {
            String availableDate = sdf.format(availableDate1);
            List<AppointmentSchedule> hoursDateList2 = appointmentScheduleList.stream().filter(a -> sdf.format(a.getDate()).equals(availableDate)).collect(Collectors.toList());
            List<AppointmentSchedule> hoursDates = new ArrayList<>();

            for (AppointmentSchedule appointmentSchedule : hoursDateList2) {
                Calendar time = Calendar.getInstance();
                time.setTime(appointmentSchedule.getDate());
                time.set(Calendar.HOUR, Integer.parseInt(appointmentSchedule.getStartTime().substring(0, 2)));
                time.set(Calendar.MINUTE, Integer.parseInt(appointmentSchedule.getStartTime().substring(3, 5)));

                if (sdf.format(appointmentSchedule.getDate()).equals(availableDate) && time.getTime().after(afterHours)) {
                    hoursDates.add(appointmentSchedule);
                }
            }

            if (hoursDates.size() > 0) {
                availableDatesFinal.add(availableDate1);
                schedulesMap.put(availableDate, hoursDates);
            }
        }

        return Pair.of(availableDatesFinal, schedulesMap);
    }

    @RequestMapping(value = URL+"/rejected", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:credit:rejected:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showRejected(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        Integer offset = 0;
        Integer limit = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        if(filter != null) limit = filter.getLimit();
        if(filter != null && filter.getPage() != null && limit != null) {
            offset = filter.getPage() * limit;
        }
        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        List<Integer> product = null;
        List<String> rejectedReason = null;

        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
            if(filter.getProduct() != null) {
                product = new Gson().fromJson(filter.getProduct(), new TypeToken<ArrayList<Integer>>(){}.getType());
            }
            if(filter.getRejectedReason() != null) {
                rejectedReason = new Gson().fromJson(filter.getRejectedReason(), new TypeToken<ArrayList<String>>(){}.getType());
            }
        }
        if(offset < 0) offset = 0;


        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.REJECTED_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());

        List<Integer> finalProduct = product;
        List<CreditBancoDelSolExtranetPainter> credits = entityExtranetService.getRejectedLoanApplications(
                loggedUserEntity.getPrincipalEntity().getId(),
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                locale,
                searchValue,
                offset,
                limit,
                false,
                product != null ? products.stream().filter(e -> finalProduct.contains(e.getId())).map(Product::getId).collect(Collectors.toList()) : products.stream().map(Product::getId).collect(Collectors.toList()),
                rejectedReason
                );

        model.addAttribute("offset", offset);
        model.addAttribute("credits", credits);
        model.addAttribute("page", "rejected");
        model.addAttribute("title", "Solicitudes rechazadas");
        model.addAttribute("showButton", true);

        EntityExtranetConfiguration extranetConfiguration = brandingService.getExtranetBrandingAsJson(loggedUserEntity.getPrincipalEntity().getId());
        if(extranetConfiguration != null && extranetConfiguration.getRejectedPageConfiguration() != null && extranetConfiguration.getRejectedPageConfiguration().getDetailConfiguration() != null)  return new ModelAndView("/entityExtranet/extranetPageWithDetail :: list");

        return new ModelAndView("/entityExtranet/extranetRejected :: list");
    }

    @RequestMapping(value = URL+"/rejected/count", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:credit:rejected:view", type = RequiresPermissionOr403.Type.AJAX)
    public Object showRejectedListCount(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        List<Integer> product = null;
        List<String> rejectedReason = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
            if(filter.getProduct() != null) {
                product = new Gson().fromJson(filter.getProduct(), new TypeToken<ArrayList<Integer>>(){}.getType());
            }
            if(filter.getRejectedReason() != null) {
                rejectedReason = new Gson().fromJson(filter.getRejectedReason(), new TypeToken<ArrayList<String>>(){}.getType());
            }
        }

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.REJECTED_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());

        List<Integer> finalProduct = product;
        Pair<Integer, Double> countAdSum = creditDAO.getEntityRejectedLoanApplicationsCount(
                loggedUserEntity.getPrincipalEntity().getId(),
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                searchValue,
                product != null ? products.stream().filter(e -> finalProduct.contains(e.getId())).map(Product::getId).collect(Collectors.toList()) : products.stream().map(Product::getId).collect(Collectors.toList()),
                rejectedReason,
                locale);



        Map<String, Object> data = new HashMap<String, Object>();
        data.put("count",countAdSum.getLeft());

        return AjaxResponse.ok(new Gson().toJson(data));
    }

    @RequestMapping(value = URL+"/callcenter/count", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:credit:callcenter:view", type = RequiresPermissionOr403.Type.AJAX)
    public Object showCallCenterListCount(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }

        Pair<Integer, Double> countAdSum = entityExtranetService.getCreditsToShowCount(
                ExtranetMenu.CALL_CENTER_MENU,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                searchValue,
                locale);

        //PARA OBTENER LA MONEDA DEL CREDITO
        List<CreditEntityExtranetPainter> credits = entityExtranetService.getCreditsToShow(
                ExtranetMenu.CALL_CENTER_MENU,
                null,
                null,
                locale,0, 1, null);

        Map<String, Object> data = new HashMap<String, Object>();
        if(creationFrom == null && creationTo == null){
            data.put("count",null);
            data.put("sumCredits", null);
        }
        else{
            data.put("count",countAdSum.getLeft());
            data.put("sumCredits", utilService.doubleMoneyFormat(countAdSum.getRight(),credits.size() > 0 ? credits.get(0).getCurrency() : catalogService.getCountryParam(entityExtranetService.getLoggedUserEntity().getEntities().get(0).getCountryId()).getCurrency()));
        }

        return AjaxResponse.ok(new Gson().toJson(data));
    }

    @RequestMapping(value = URL+"/terminated/count", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:credit:terminated:view", type = RequiresPermissionOr403.Type.AJAX)
    public Object showTerminatedListCount(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        List<Integer> product = null;

        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
            if(filter.getProduct() != null) {
                product = new Gson().fromJson(filter.getProduct(), new TypeToken<ArrayList<Integer>>(){}.getType());
            }
        }

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.DISBURSEMENT_CREDITS_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());

        List<Integer> finalProduct = product;
        Pair<Integer, Double> countAdSum = entityExtranetService.getCreditsToShowCount(
                ExtranetMenu.DISBURSEMENT_CREDITS_MENU,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                searchValue,
                locale,
                null,
                product != null ? products.stream().filter(e -> finalProduct.contains(e.getId())).map(Product::getId).collect(Collectors.toList()) : products.stream().map(Product::getId).collect(Collectors.toList()));

        //PARA OBTENER LA MONEDA DEL CREDITO
        List<CreditEntityExtranetPainter> credits = entityExtranetService.getCreditsToShow(
                ExtranetMenu.DISBURSEMENT_CREDITS_MENU,
                null,
                null,
                locale,0, 1,
                null,
                false,
                null,
                products.stream().map(Product::getId).collect(Collectors.toList()));

        Map<String, Object> data = new HashMap<String, Object>();
        if(creationFrom == null && creationTo == null){
            data.put("count",null);
            data.put("sumCredits", null);
        }
        else{
            data.put("count",countAdSum.getLeft());
            data.put("sumCredits", utilService.doubleMoneyFormat(countAdSum.getRight(),credits.size() > 0 ? credits.get(0).getCurrency() : catalogService.getCountryParam(entityExtranetService.getLoggedUserEntity().getEntities().get(0).getCountryId()).getCurrency()));
        }

        return AjaxResponse.ok(new Gson().toJson(data));
    }

    @RequestMapping(value = URL+"/leadsDelivered/count", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:leads:leadsDelivered:view", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showLeadsDeliveredCount(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        int filterType = 2;
        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }

        Integer count = creditDAO.getBanbifLeadCreditCardLoanCount(null,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                searchValue,
                filterType, locale);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("count",count);
        return AjaxResponse.ok(new Gson().toJson(data));
    }

    @RequestMapping(value = URL+"/leadsToDeliver/count", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:leads:leadsToDeliver:view", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showLeadsToDeliverCount(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        int filterType = 1;
        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }

        Integer count = creditDAO.getBanbifLeadCreditCardLoanCount(null,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                searchValue,
                filterType, locale);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("count",count);
        return AjaxResponse.ok(new Gson().toJson(data));
    }

    @RequestMapping(value = URL+"/pending/count", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:credit:pending:view", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showPendingCount(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.TO_VERIFY_CREDITS_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());


        Pair<Integer, Double> countAdSum = entityExtranetService.getCreditsToShowCount(
                ExtranetMenu.TO_VERIFY_CREDITS_MENU,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                searchValue,
                locale,
                null,products.stream().map(Product::getId).collect(Collectors.toList())
                );

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("count",countAdSum.getLeft());
        data.put("sumCredits", utilService.doubleMoneyFormat(countAdSum.getRight()));
        return AjaxResponse.ok(new Gson().toJson(data));

    }

    @RequestMapping(value = URL+"/toUpload/count", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:credit:toUpload:view", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showToUploadCount(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.TO_UPLOAD_CREDITS_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());

        Pair<Integer, Double> countAdSum = entityExtranetService.getCreditsToShowCount(
                ExtranetMenu.TO_UPLOAD_CREDITS_MENU,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                searchValue,
                locale,
                null,products.stream().map(Product::getId).collect(Collectors.toList())
                );

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("count",countAdSum.getLeft());
        data.put("sumCredits", utilService.doubleMoneyFormat(countAdSum.getRight()));
        return AjaxResponse.ok(new Gson().toJson(data));

    }

    @RequestMapping(value = URL+"/generated/count", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:credit:generated:view", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showGeneratedCount(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        Integer[] entityProductsParam = null;
        List<Integer> product = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
            if(filter.getEntityProductParam() != null) {
                entityProductsParam = new Gson().fromJson(filter.getEntityProductParam(), Integer[].class);
            }
            if(filter.getDisbursementType() != null) {
                Integer[] disbursementTypes = new Gson().fromJson(filter.getDisbursementType(), Integer[].class);
                if(disbursementTypes.length > 0){
                    List<Integer> entityProductParamsId = catalogService.getEntityProductParams().stream().filter(e -> e.getEntity().getId().equals(loggedUserEntity.getPrincipalEntity().getId()) && e.getExtranetCreditGeneration() != null && e.getExtranetCreditGeneration() && Arrays.stream(disbursementTypes).anyMatch(d->d.equals(e.getDisbursementType()))).collect(Collectors.toList()).stream().map(e->e.getId()).collect(Collectors.toList());
                    entityProductsParam = new Integer[entityProductParamsId.size()];
                    entityProductsParam = entityProductParamsId.toArray(entityProductsParam);
                }
            }
            if(filter.getProduct() != null) {
                product = new Gson().fromJson(filter.getProduct(), new TypeToken<ArrayList<Integer>>(){}.getType());
            }
        }

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.TO_DISBURSE_CREDITS_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());


        List<Integer> finalProduct = product;
        Pair<Integer, Double> countAdSum = entityExtranetService.getCreditsToShowCount(
                ExtranetMenu.TO_DISBURSE_CREDITS_MENU,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                searchValue,
                locale,entityProductsParam,
                product != null ? products.stream().filter(e -> finalProduct.contains(e.getId())).map(Product::getId).collect(Collectors.toList()) : products.stream().map(Product::getId).collect(Collectors.toList())
                );

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("count",countAdSum.getLeft());
        data.put("sumCredits", utilService.doubleMoneyFormat(countAdSum.getRight()));
        return AjaxResponse.ok(new Gson().toJson(data));

    }

    @RequestMapping(value = URL+"/generatedtc/count", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:credit:generatedtc:view", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showGeneratedTcCount(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.TO_DELIVER_TC_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());


        Pair<Integer, Double> countAdSum = entityExtranetService.getCreditsToShowCount(
                ExtranetMenu.TO_DELIVER_TC_MENU,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                searchValue,
                locale,
                null,products.stream().map(Product::getId).collect(Collectors.toList())
                );

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("count",countAdSum.getLeft());
        data.put("sumCredits", utilService.doubleMoneyFormat(countAdSum.getRight()));
        return AjaxResponse.ok(new Gson().toJson(data));

    }


    @RequestMapping(value = URL+"/terminatedtc/count", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:credit:terminatedtc:view", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showTerminatedTcCount(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.DELIVERED_TC_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());

        Pair<Integer, Double> countAdSum = entityExtranetService.getCreditsToShowCount(
                ExtranetMenu.DELIVERED_TC_MENU,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                searchValue,
                locale,null,products.stream().map(Product::getId).collect(Collectors.toList()));

        Map<String, Object> data = new HashMap<String, Object>();
        if(creationFrom == null && creationTo == null){
            data.put("count",null);
            data.put("sumCredits", null);
        }
        else{
            data.put("count",countAdSum.getLeft());
            data.put("sumCredits", utilService.doubleMoneyFormat(countAdSum.getRight(),"$"));
        }
        return AjaxResponse.ok(new Gson().toJson(data));
    }

    @RequestMapping(value = URL+"/beingProcessed", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:credit:beingProcessed:view", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showBeingProcessed(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        Integer offset = 0;
        Integer limit = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        if(filter != null) limit = filter.getLimit();
        if(filter != null && filter.getPage() != null && limit != null) {
            offset = filter.getPage() * limit;
        }
        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        List<Integer> product = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
            if(filter.getProduct() != null) {
                product = new Gson().fromJson(filter.getProduct(), new TypeToken<ArrayList<Integer>>(){}.getType());
            }
        }
        if(offset < 0) offset = 0;

        if((filter.getMaxProgress() != null && filter.getMinProgress() == null) || (filter.getMinProgress() != null && filter.getMaxProgress() == null)){
            return AjaxResponse.errorMessage("Debe indicar el valor máximo y mínimo");
        }

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.BEING_PROCESSED_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());

        List<Integer> finalProduct = product;
        List<CreditBancoDelSolExtranetPainter> credits = entityExtranetService.getCreditsBeingProcessedByLoggedUserId(
                loggedUserEntity.getId(),
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                searchValue,
                offset,
                limit,
                locale,
                false,
                product != null ? products.stream().filter(e -> finalProduct.contains(e.getId())).map(Product::getId).collect(Collectors.toList()) : products.stream().map(Product::getId).collect(Collectors.toList()),
                filter.getMinProgress(),
                filter.getMaxProgress()
                );

        model.addAttribute("offset", offset);
        model.addAttribute("credits", credits);
        model.addAttribute("page", "beingProcessed");
        model.addAttribute("title", "Solicitudes en Proceso");
        model.addAttribute("showButton", true);

        if(entityExtranetService.getLoggedUserEntity().getPrincipalEntity().getId() == Entity.BANCO_DEL_SOL)
            return new ModelAndView("/entityExtranet/extranetBancoDelSolCredits :: list");

        EntityExtranetConfiguration extranetConfiguration = brandingService.getExtranetBrandingAsJson(entityExtranetService.getLoggedUserEntity().getPrincipalEntity().getId());
        if(extranetConfiguration != null && extranetConfiguration.getInProcessCreditPageConfiguration() != null && extranetConfiguration.getInProcessCreditPageConfiguration().getDetailConfiguration() != null)  return new ModelAndView("/entityExtranet/extranetPageWithDetail :: list");

        return new ModelAndView("/entityExtranet/extranetBeingProcessed :: list");

    }

    @RequestMapping(value = URL+"/beingProcessed/count", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:credit:beingProcessed:view", type = RequiresPermissionOr403.Type.AJAX)
    public Object showBeingProcessedCount(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        List<Integer> product = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
            if(filter.getProduct() != null) {
                product = new Gson().fromJson(filter.getProduct(), new TypeToken<ArrayList<Integer>>(){}.getType());
            }
            if((filter.getMaxProgress() != null && filter.getMinProgress() == null) || (filter.getMinProgress() != null && filter.getMaxProgress() == null)){
                return AjaxResponse.errorMessage("Debe indicar el valor máximo y mínimo");
            }
        }

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.BEING_PROCESSED_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());


        List<Integer> finalProduct = product;
        Pair<Integer, Double> countAdSum = creditDAO.getEntityCreditsByLoggedUserIdCount(
                loggedUserEntity.getId(),
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                searchValue,
                product != null ? products.stream().filter(e -> finalProduct.contains(e.getId())).map(Product::getId).collect(Collectors.toList()) : products.stream().map(Product::getId).collect(Collectors.toList()),
                filter.getMinProgress(),
                filter.getMaxProgress(),
                locale);


        Map<String, Object> data = new HashMap<String, Object>();
        data.put("count",countAdSum.getLeft());

        return AjaxResponse.ok(new Gson().toJson(data));
    }



}
