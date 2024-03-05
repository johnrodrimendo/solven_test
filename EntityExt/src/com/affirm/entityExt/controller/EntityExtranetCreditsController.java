package com.affirm.entityExt.controller;

import com.affirm.bancodelsol.service.BancoDelSolService;
import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.model.form.EntityExtranetRegisterDisbursementForm;
import com.affirm.client.service.EmailCLService;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.client.service.impl.EntityExtranetServiceImpl;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.AppointmentSchedule;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.service.entities.AeluService;
import com.affirm.common.service.external.ToroLeadService;
import com.affirm.common.service.impl.FunnelStepService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.JsonUtil;
import com.affirm.entityExt.models.PaginatorTableFilterForm;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.shiro.SecurityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class EntityExtranetCreditsController {

    @Autowired
    private CreditDAO creditDAO;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private EntityExtranetService entityExtranetService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private LoanNotifierService loanNotifierService;
    @Autowired
    private InteractionService interactionService;
    @Autowired
    private CreditService creditService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private UserService userService;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private EmailCLService emailCLService;
    @Autowired
    private ReportsService reportsService;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private AeluService aeluService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private BrandingService brandingService;

    @Autowired
    private AwsSesEmailService awsSesEmailService;

    @Autowired
    private BancoDelSolService bancoDelSolService;
    @Autowired
    private OfflineConversionService offlineConversionService;
    @Autowired
    private ToroLeadService toroLeadService;
    @Autowired
    private FunnelStepService funnelStepService;

    @RequestMapping(value = "/pending", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:credit:pending:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showPending(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.TO_VERIFY_CREDITS_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());
        model.addAttribute("products", products);
        List<CreditEntityExtranetPainter> credits = entityExtranetService.getCreditsToShow(ExtranetMenu.TO_VERIFY_CREDITS_MENU, null, null, locale,0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, null, false,null, products.stream().map(Product::getId).collect(Collectors.toList()));
        Pair<Integer, Double> countAdSum = entityExtranetService.getCreditsToShowCount(ExtranetMenu.TO_VERIFY_CREDITS_MENU, null, null, null, locale,null, products.stream().map(Product::getId).collect(Collectors.toList()));
        model.addAttribute("totalCount", countAdSum.getLeft());

        Pair<List<Date>, Map<String, List<AppointmentSchedule>>> availableDates = scheduleAvailableDates(1);
        Integer limitPaginator = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        model.addAttribute("limitPaginator", limitPaginator);
        model.addAttribute("credits", credits);
        model.addAttribute("page", "pending");
        model.addAttribute("title", "Solicitudes a verificar");
        model.addAttribute("showAccesoScheduleAppointmentModal", credits.stream().anyMatch(c -> c.isShowAccesoScheduleAppointmentButton() || c.isShowAccesoReScheduleAppointmentButton()));
        model.addAttribute("availableDates", availableDates.getLeft());
        model.addAttribute("jsonSchedulesMap", new Gson().toJson(availableDates.getRight()));
        model.addAttribute("showButton", true);
        model.addAttribute("tray", ExtranetMenu.TO_VERIFY_CREDITS_MENU);

        if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) return new ModelAndView("/entityExtranet/extranetCreditsBDS");

        return new ModelAndView("/entityExtranet/extranetCredits");
    }

    @RequestMapping(value = "/toUpload", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:credit:toUpload:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showToUpload(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.TO_UPLOAD_CREDITS_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());
        model.addAttribute("products", products);

        List<CreditEntityExtranetPainter> credits = entityExtranetService.getCreditsToShow(ExtranetMenu.TO_UPLOAD_CREDITS_MENU, null, null, locale,0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, null, false,null, products.stream().map(Product::getId).collect(Collectors.toList()));
        Pair<Integer, Double> countAdSum = entityExtranetService.getCreditsToShowCount(ExtranetMenu.TO_UPLOAD_CREDITS_MENU, null, null, null, locale,null, products.stream().map(Product::getId).collect(Collectors.toList()));

        model.addAttribute("totalCount", countAdSum.getLeft());
        Integer limitPaginator = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        model.addAttribute("limitPaginator", limitPaginator);
        model.addAttribute("credits", credits);
        model.addAttribute("page", "toUpload");
        model.addAttribute("title", "Creación de cliente/crédito");
        model.addAttribute("showAccesoCreateButton", credits.stream().anyMatch(c -> c.isShowAccesoCreateButton()));
        model.addAttribute("showButton", true);
        model.addAttribute("tray", ExtranetMenu.TO_UPLOAD_CREDITS_MENU);

        if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) return new ModelAndView("/entityExtranet/extranetCreditsBDS");

        return new ModelAndView("/entityExtranet/extranetCredits");
    }

    @RequestMapping(value = "/generated", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:credit:generated:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showGenerated(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.TO_DISBURSE_CREDITS_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());
        model.addAttribute("products", products);

        List<CreditEntityExtranetPainter> credits = entityExtranetService.getCreditsToShow(ExtranetMenu.TO_DISBURSE_CREDITS_MENU, null, null, locale,0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR,null, false, null, products.stream().map(Product::getId).collect(Collectors.toList()));
        Pair<Integer, Double> countAdSum = entityExtranetService.getCreditsToShowCount(ExtranetMenu.TO_DISBURSE_CREDITS_MENU, null, null, null, locale,null,products.stream().map(Product::getId).collect(Collectors.toList()));

        Integer limitPaginator = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        model.addAttribute("limitPaginator", limitPaginator);
        model.addAttribute("totalCount", countAdSum.getLeft());
        model.addAttribute("credits", credits);
        model.addAttribute("showFinalDocumentationModal", credits.stream().anyMatch(c -> c.isShowAeluUploadFinalDocumentationButton() || c.isShowAccesoUploadFinalDocumentationButton()));
        model.addAttribute("showAeluInternalDisbursementModal", credits.stream().anyMatch(c -> c.isShowAeluInternalDisbursementButton()));
        model.addAttribute("page", "generated");
        model.addAttribute("registerDisbursementForm", new EntityExtranetRegisterDisbursementForm());
        if (loggedUserEntity.getEntities().get(0).getId() != null && loggedUserEntity.getEntities().get(0).getId() == Entity.BANCO_DEL_SOL) {
            JSONObject jsonHierarchy = personDao.getBancoDelSolEmployeeHierarchy(loggedUserEntity.getId());
            model.addAttribute("userHierarchy", jsonHierarchy.isNull("level1") && jsonHierarchy.isNull("level2") && jsonHierarchy.isNull("level3") ? null : jsonHierarchy);
            model.addAttribute("title", "Créditos a desembolsar");
        }
        else model.addAttribute("title", "Originados (Inactivos)");

        Integer loggedUserEntityId = entityExtranetService.getLoggedUserEntity().getPrincipalEntity().getId();
        List<EntityProductParams> entityProductParams = catalogService.getEntityProductParams().stream().filter(e -> e.getEntity().getId().equals(loggedUserEntityId)).collect(Collectors.toList());


        model.addAttribute("products", products);

        model.addAttribute("entityProductParams", entityProductParams);
        model.addAttribute("disbursementTypes", entityProductParams.stream().map(e -> e.getDisbursementTypeObject()).distinct().collect(Collectors.toList()));

        model.addAttribute("showButton", true);
        model.addAttribute("tray", ExtranetMenu.TO_DISBURSE_CREDITS_MENU);
        model.addAttribute("sidebarClosed", true);


        EntityExtranetConfiguration extranetConfiguration = brandingService.getExtranetBrandingAsJson(loggedUserEntity.getPrincipalEntity().getId());
        if(extranetConfiguration != null && extranetConfiguration.getDisburseCreditPageConfiguration() != null && extranetConfiguration.getDisburseCreditPageConfiguration().getDetailConfiguration() != null)  return new ModelAndView("/entityExtranet/extranetPageWithDetail");

        if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) return new ModelAndView("/entityExtranet/extranetCreditsBDS");

        return new ModelAndView("/entityExtranet/extranetCredits");
    }

    @RequestMapping(value = "/generatedtc", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:credit:generatedtc:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showGeneratedTc(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.TO_DELIVER_TC_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());
        model.addAttribute("products", products);
        List<CreditEntityExtranetPainter> credits = entityExtranetService.getCreditsToShow(ExtranetMenu.TO_DELIVER_TC_MENU, null, null, locale,0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, null, false,null, products.stream().map(Product::getId).collect(Collectors.toList()));
        Pair<Integer, Double> countAdSum = entityExtranetService.getCreditsToShowCount(ExtranetMenu.TO_DELIVER_TC_MENU, null, null, null, locale,null, products.stream().map(Product::getId).collect(Collectors.toList()));

        model.addAttribute("totalCount", countAdSum.getLeft());
        Integer limitPaginator = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        model.addAttribute("limitPaginator", limitPaginator);
        model.addAttribute("credits", credits);
        model.addAttribute("showFinalDocumentationModal", credits.stream().anyMatch(c -> c.isShowAeluUploadFinalDocumentationButton() || c.isShowAccesoUploadFinalDocumentationButton()));
        model.addAttribute("showAeluInternalDisbursementModal", credits.stream().anyMatch(c -> c.isShowAeluInternalDisbursementButton()));
        model.addAttribute("page", "generatedtc");
        model.addAttribute("showButton", false);
        model.addAttribute("title", "Tarjetas a entregar");
        model.addAttribute("bandeja", ExtranetMenu.TO_DELIVER_TC_MENU);
        model.addAttribute("registerDisbursementForm", new EntityExtranetRegisterDisbursementForm());
        if (loggedUserEntity.getEntities().get(0).getId() != null && loggedUserEntity.getEntities().get(0).getId() == Entity.BANCO_DEL_SOL) {
            JSONObject jsonHierarchy = personDao.getBancoDelSolEmployeeHierarchy(loggedUserEntity.getId());
            model.addAttribute("userHierarchy", jsonHierarchy.isNull("level1") && jsonHierarchy.isNull("level2") && jsonHierarchy.isNull("level3") ? null : jsonHierarchy);
        }
        model.addAttribute("tray", ExtranetMenu.TO_DELIVER_TC_MENU);

        if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) return new ModelAndView("/entityExtranet/extranetCreditsBDS");

        return new ModelAndView("/entityExtranet/extranetCredits");
    }

    @RequestMapping(value = "/terminated", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:credit:terminated:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showTerminated(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.DISBURSEMENT_CREDITS_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());
        model.addAttribute("products", products);

        List<CreditEntityExtranetPainter> credits = entityExtranetService.getCreditsToShow(ExtranetMenu.DISBURSEMENT_CREDITS_MENU, null, null, locale,0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, null,false,null,products.stream().map(Product::getId).collect(Collectors.toList()));
        Pair<Integer, Double> countAdSum = entityExtranetService.getCreditsToShowCount(ExtranetMenu.DISBURSEMENT_CREDITS_MENU, null, null, null, locale, null,products.stream().map(Product::getId).collect(Collectors.toList()));

        model.addAttribute("totalCount", countAdSum.getLeft());
        Integer limitPaginator = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        model.addAttribute("limitPaginator", limitPaginator);
        model.addAttribute("totalCredits", countAdSum.getLeft());
        model.addAttribute("sumCredits", countAdSum.getRight());
        model.addAttribute("currency", credits.size() > 0 ? credits.get(0).getCurrency() : catalogService.getCountryParam(entityExtranetService.getLoggedUserEntity().getEntities().get(0).getCountryId()).getCurrency());
        model.addAttribute("credits", credits);
        model.addAttribute("page", "terminated");
        model.addAttribute("showButton", true);
        if (loggedUserEntity.getEntities().get(0).getId() != null && loggedUserEntity.getEntities().get(0).getId() == Entity.BANCO_DEL_SOL) {
            JSONObject jsonHierarchy = personDao.getBancoDelSolEmployeeHierarchy(loggedUserEntity.getId());
            model.addAttribute("userHierarchy", jsonHierarchy.isNull("level1") && jsonHierarchy.isNull("level2") && jsonHierarchy.isNull("level3") ? null : jsonHierarchy);
            model.addAttribute("title", "Créditos desembolsados");
        }
        else{
            model.addAttribute("title", "Originados (Activos)");
        }
        model.addAttribute("tray", ExtranetMenu.DISBURSEMENT_CREDITS_MENU);
        model.addAttribute("sidebarClosed", true);

        EntityExtranetConfiguration extranetConfiguration = brandingService.getExtranetBrandingAsJson(loggedUserEntity.getPrincipalEntity().getId());
        if(extranetConfiguration != null && extranetConfiguration.getDisbursedCreditPageConfiguration() != null && extranetConfiguration.getDisbursedCreditPageConfiguration().getDetailConfiguration() != null)  return new ModelAndView("/entityExtranet/extranetPageWithDetail");

        if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) return new ModelAndView("/entityExtranet/extranetCreditsBDS");

        return new ModelAndView("/entityExtranet/extranetCredits");
    }

    @RequestMapping(value = "/terminatedtc", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:credit:terminatedtc:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showTerminatedTc(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.DELIVERED_TC_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());
        model.addAttribute("products", products);

        List<CreditEntityExtranetPainter> credits = entityExtranetService.getCreditsToShow(ExtranetMenu.DELIVERED_TC_MENU, null, null, locale,0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, null, false,null, products.stream().map(Product::getId).collect(Collectors.toList()));
        Pair<Integer, Double> countAdSum = entityExtranetService.getCreditsToShowCount(ExtranetMenu.DELIVERED_TC_MENU, null, null, null, locale,null, products.stream().map(Product::getId).collect(Collectors.toList()));

        model.addAttribute("totalCount", countAdSum.getLeft());
        Integer limitPaginator = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        model.addAttribute("limitPaginator", limitPaginator);
        model.addAttribute("totalCredits", countAdSum.getLeft());
        model.addAttribute("sumCredits", countAdSum.getRight());
        model.addAttribute("currency", credits.size() > 0 ? credits.get(0).getCurrency() : catalogService.getCountryParam(entityExtranetService.getLoggedUserEntity().getEntities().get(0).getCountryId()).getCurrency());
        model.addAttribute("credits", credits);
        model.addAttribute("page", "terminatedtc");
        model.addAttribute("showButton", false);
        model.addAttribute("title", "Tarjetas entregadas");
        model.addAttribute("bandeja", ExtranetMenu.DELIVERED_TC_MENU);
        if (loggedUserEntity.getEntities().get(0).getId() != null && loggedUserEntity.getEntities().get(0).getId() == Entity.BANCO_DEL_SOL) {
            JSONObject jsonHierarchy = personDao.getBancoDelSolEmployeeHierarchy(loggedUserEntity.getId());
            model.addAttribute("userHierarchy", jsonHierarchy.isNull("level1") && jsonHierarchy.isNull("level2") && jsonHierarchy.isNull("level3") ? null : jsonHierarchy);
        }
        model.addAttribute("tray", ExtranetMenu.DELIVERED_TC_MENU);

        if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) return new ModelAndView("/entityExtranet/extranetCreditsBDS");

        return new ModelAndView("/entityExtranet/extranetCredits");
    }

    @RequestMapping(value = "/terminated/list", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showTerminatedList(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam(value = "creationFrom[]", required = false) String creationFrom,
            @RequestParam(value = "creationTo[]", required = false) String creationTo) throws Exception {

        List<CreditEntityExtranetPainter> credits = entityExtranetService.getCreditsToShow(
                ExtranetMenu.DISBURSEMENT_CREDITS_MENU,
                creationFrom != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                locale,0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, null);
        Integer limitPaginator = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
//        Integer totalCount = entityExtranetService.getCreditsToShowCount(
//                ExtranetMenu.DISBURSEMENT_CREDITS_MENU,
//                creationFrom != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
//                creationTo != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
//                locale);
//        model.addAttribute("totalCountData", totalCount);
        model.addAttribute("limitPaginator", limitPaginator);
//        model.addAttribute("totalCredits", credits.size());
//        model.addAttribute("sumCredits", credits.stream().mapToDouble(Credit::getAmount).sum());
        model.addAttribute("currency", credits.size() > 0 ? credits.get(0).getCurrency() : catalogService.getCountryParam(entityExtranetService.getLoggedUserEntity().getEntities().get(0).getCountryId()).getCurrency());
        model.addAttribute("credits", credits);
        model.addAttribute("page", "terminated");
        model.addAttribute("title", "Créditos desembolsados");
        model.addAttribute("showButton", true);

        if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) return new ModelAndView("/entityExtranet/extranetCreditsBDS :: list");

        return new ModelAndView("/entityExtranet/extranetCredits :: list");
    }

    @RequestMapping(value = "/updateStatus/pending", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:pending:validate", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object updateStatusPending(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("creditId") Integer creditId,
            @RequestParam(value = "creditDisbursementReferenceNumber", required = false) String referenceNumber) throws Exception {

        return commonUpdateStatus(model, locale, request, response, "pending", creditId, referenceNumber, null, null);
    }

    @RequestMapping(value = "/updateStatus/appointment", method = RequestMethod.POST)
    @RequiresPermissionOr403(type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
//permissions = "credit:toUpload:create",
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object updateStatusToAppointmentRegistered(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("creditId") Integer creditId,
            @RequestParam("appointmentScheduleId") Integer appointmentScheduleId,
            @RequestParam("appointmentDate") String appointmentDate,
            @RequestParam("appointmentPlace") String appointmentPlace) throws Exception {

        // Validate the date
        Date selectedDate = new SimpleDateFormat("dd/MM/yyyy").parse(appointmentDate);

        Pair<List<Date>, Map<String, List<AppointmentSchedule>>> availableDates = scheduleAvailableDates(1);
        Date minAvailableDate = availableDates.getLeft().get(0);
        Date maxAvailableDate = availableDates.getLeft().get(availableDates.getLeft().size() - 1);
        if (selectedDate.compareTo(minAvailableDate) < 0 || selectedDate.compareTo(maxAvailableDate) > 0) {
            return AjaxResponse.errorMessage("La fecha seleccionada no se encuentra en el rango válido");
        }
        if (appointmentPlace == null || "".equals(appointmentPlace.trim())) {
            return AjaxResponse.errorMessage("El campo Lugar es requerido");
        }

        CreditEntityExtranetPainter credit = entityExtranetService.getCreditToShowById(creditId, ExtranetMenu.TO_VERIFY_CREDITS_MENU, locale);
        if (credit != null && (credit.isShowAccesoScheduleAppointmentButton() || credit.isShowAccesoReScheduleAppointmentButton())) {
            loanApplicationDao.registerAppointmentSchedule(credit.getLoanApplicationId(), selectedDate, appointmentScheduleId, appointmentPlace);
            creditDAO.updateCreditSubStatus(credit.getId(), CreditSubStatus.ACCESO_APPOINTMENT_REGISTERED);
            loanApplicationService.sendLoanApplicationConfirmScheduleMail(credit.getLoanApplicationId(), credit.getPersonId(), selectedDate, appointmentScheduleId, locale);
            return AjaxResponse.ok(null);
        } else {
            return AjaxResponse.errorMessage("No es posible agendar la cita");
        }
    }

    @RequestMapping(value = "/updateStatus/toUpload", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:toUpload:upload", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object updateStatusToUpload(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("creditId") Integer creditId,
            @RequestParam(value = "creditDisbursementReferenceNumber", required = false) String creditReferenceNumber,
            @RequestParam(value = "loanDisbursementReferenceNumber", required = false) String loanReferenceNumber) throws Exception {

        return commonUpdateStatus(model, locale, request, response, "toUpload", creditId, creditReferenceNumber, loanReferenceNumber, null);
    }

    @RequestMapping(value = "/updateStatus/generated", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:generated:disbursement", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object updateStatusGenerated(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            EntityExtranetRegisterDisbursementForm form,
            @RequestParam("creditId") Integer creditId
            /*@RequestParam(value = "creditDisbursementReferenceNumber", required = false) String referenceNumber,
            @RequestParam(value = "creditDisbursementDate", required = false) String disbursementDate*/) throws Exception {

        return commonUpdateStatus(model, locale, request, response, "generated", creditId, null, null, form);
    }

    @RequestMapping(value = "/updateStatus/bank_account_saving", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:generated:disbursement", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object updateStatusBankAccountSaving(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("creditId") Integer creditId
            /*@RequestParam(value = "creditDisbursementReferenceNumber", required = false) String referenceNumber,
            @RequestParam(value = "creditDisbursementDate", required = false) String disbursementDate*/) throws Exception {

        return commonUpdateStatus(model, locale, request, response, "bank_account_saving", creditId, null, null, null);
    }

    @RequestMapping(value = "/updateStatus/generatedtc", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:generated:disbursement", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object updateStatusGeneratedtc(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            EntityExtranetRegisterDisbursementForm form,
            @RequestParam("creditId") Integer creditId
            /*@RequestParam(value = "creditDisbursementReferenceNumber", required = false) String referenceNumber,
            @RequestParam(value = "creditDisbursementDate", required = false) String disbursementDate*/) throws Exception {

        return commonUpdateStatus(model, locale, request, response, "generatedtc", creditId, null, null, form);
    }

    @RequestMapping(value = "/rejectCredit/pending", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:pending:reject", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object rejectCreditPending(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("creditId") Integer creditId,
            @RequestParam("creditRejectionReasonId") Integer creditRejectionReasonId,
            @RequestParam("creditRejectionReasonComment") String creditRejectionReasonComment) throws Exception {

        return commonRejectCredit(model, locale, request, response, "pending", creditId, creditRejectionReasonId, creditRejectionReasonComment);
    }

    @RequestMapping(value = "/rejectCredit/generated", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:generated:reject", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object rejectCreditGenerated(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("creditId") Integer creditId,
            @RequestParam("creditRejectionReasonId") Integer creditRejectionReasonId,
            @RequestParam("creditRejectionReasonComment") String creditRejectionReasonComment) throws Exception {

        return commonRejectCredit(model, locale, request, response, "generated", creditId, creditRejectionReasonId, creditRejectionReasonComment);
    }

    @RequestMapping(value = "/rejectCredit/toUpload", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:toUpload:reject", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object rejectCreditToUpload(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("creditId") Integer creditId,
            @RequestParam("creditRejectionReasonId") Integer creditRejectionReasonId,
            @RequestParam("creditRejectionReasonComment") String creditRejectionReasonComment) throws Exception {

        return commonRejectCredit(model, locale, request, response, "toUpload", creditId, creditRejectionReasonId, creditRejectionReasonComment);
    }

    @RequestMapping(value = "/rejectionReasons", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getRejectionReasons(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("creditId") Integer creditId,
            @RequestParam("rejectionReasonlistTypeId") String rejectionReasonListTypeId) throws Exception {

        Credit credit = creditDAO.getCreditByID(creditId, locale, false, Credit.class);

        // First try to load the reasons to show only for the entity/product.
        List<CreditRejectionReason> reasonsList = catalogService.getCreditRejectionReasonsEntityProductParamExclusive(credit.getEntityProductParameterId(), false);
        if (reasonsList != null && !reasonsList.isEmpty()) {
            return AjaxResponse.ok(new Gson().toJson(reasonsList));
        }

        // If there's no reason specific for the entity/product, load the reasons accordingly
        switch (rejectionReasonListTypeId) {
            case CreditRejectionReason.ONLY_VERIFICATION_LIST_TYPE:
                reasonsList = catalogService.getCreditRejectionReasonExtranetOnlyVerification();
                break;
            case CreditRejectionReason.REGULAR_LIST_TYPE:
                reasonsList = catalogService.getCreditRejectionReasonExtranet();
                break;
        }

        List<CreditRejectionReason> reasons = new ArrayList<>();
        if (reasonsList != null) {
            for (CreditRejectionReason creditRejectionReason : reasonsList) {
                if (creditRejectionReason.getJsonArrayExclusiveRejectionReason() != null) {
                    if (creditRejectionReason.exclusiveForEntityProduct(credit.getEntityProductParameterId(), true)) {
                        reasons.add(creditRejectionReason);
                    }
                } else {
                    reasons.add(creditRejectionReason);
                }
            }
        }
        Gson gson = new Gson();
        return AjaxResponse.ok(gson.toJson(reasons));
    }

    @RequestMapping(value = "/getSignatureDate", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getSignatureDate(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("creditId") Integer creditId) throws Exception {
        Credit credit = creditDAO.getCreditByID(creditId, locale, false, Credit.class);

        Calendar cal = Calendar.getInstance();
        cal.setTime(credit.getSignatureDate() != null ? credit.getSignatureDate() : credit.getRegisterDate());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int[] date = new int[3];
        date[0] = year;
        date[1] = month - 1;
        date[2] = day;
        Gson gson = new Gson();
        return AjaxResponse.ok(gson.toJson(date));
    }

    @RequestMapping(value = "/reports/ripleyConsolidadoAprobacionCreditos", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public void ripleyConsolidadoAprobacionCreditos(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (!entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.RIPLEY)) {
            return;
        }

        LoggedUserEntity user = entityExtranetService.getLoggedUserEntity();

        List<Credit> credits = new ArrayList<>();
        List<CreditEntityExtranetPainter> partialCredits = entityExtranetService.getCreditsToShow(ExtranetMenu.TO_VERIFY_CREDITS_MENU, null, null, locale,0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, null);
        if (partialCredits != null && partialCredits.size() > 0)
            credits.addAll(partialCredits);

        byte[] file = reportsService.createApproveCreditsReport(credits);
        if (file != null) {
            MediaType contentType = MediaType.valueOf("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment; filename=Consolidado_para_Aprobacion_de_creditos.xls");
            response.setContentType(contentType.getType());
            response.getOutputStream().write(file);
        }
    }

    @RequestMapping(value = "/uploadRipleyFinalSchedule", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:generated:uploadRipleyFinalSchedule", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object upploadFinalSchedule(
            ModelMap modelMap, Locale locale,
            @RequestParam("file") MultipartFile[] file,
            @RequestParam("creditId") Integer creditId) throws Exception {

        Credit credit = creditDAO.getCreditByID(creditId, locale, false, Credit.class);
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);
        List<UserFile> loanFiles = loanApplicationDao.getLoanApplicationUserFiles(credit.getLoanApplicationId());

        if (loanFiles != null && loanFiles.stream().anyMatch(l -> l.getFileType().getId().equals(UserFileType.RIPLEY_FINAL_SCHEDULE))) {
            return AjaxResponse.errorMessage("Ya existe un cronograma final");
        }

        userService.registerUserFiles(file, credit.getLoanApplicationId(), loanApplication.getUserId(), UserFileType.RIPLEY_FINAL_SCHEDULE);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/removeRipleyFinalSchedule", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:generated:deleteRipleyFinalSchedule", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object removeFinalSchedule(
            ModelMap modelMap, Locale locale,
            @RequestParam("creditId") Integer creditId) throws Exception {

        Credit credit = creditDAO.getCreditByID(creditId, locale, false, Credit.class);
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);
        List<UserFile> loanFiles = loanApplicationDao.getLoanApplicationUserFiles(credit.getLoanApplicationId());

        if (loanFiles == null || loanFiles.stream().noneMatch(l -> l.getFileType().getId().equals(UserFileType.RIPLEY_FINAL_SCHEDULE))) {
            return AjaxResponse.errorMessage("No existe cronograma final a eliminar");
        }

        for (UserFile file : loanFiles.stream().filter(f -> f.getFileType().getId().equals(UserFileType.RIPLEY_FINAL_SCHEDULE)).collect(Collectors.toList())) {
            userDao.updateUserFileType(file.getId(), UserFileType.ELIMINADOS);
        }

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/uploadFinalDocumentation", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object upploadFinalDocumentation(
            ModelMap modelMap, Locale locale,
            @RequestParam("file") MultipartFile[] file,
            @RequestParam("creditId") Integer creditId) throws Exception {

        // Validate the user can updateTrx the credit
        CreditEntityExtranetPainter credit = entityExtranetService.getCreditToShowById(creditId, ExtranetMenu.TO_DISBURSE_CREDITS_MENU, locale);
        if (credit == null || (!credit.isShowAeluUploadFinalDocumentationButton() && !credit.isShowAccesoUploadFinalDocumentationButton())) {
            return AjaxResponse.errorMessage("El crédito no es válido");
        }

        // validate the user file doesnt exists yet
        List<UserFile> loanFiles = loanApplicationDao.getLoanApplicationUserFiles(credit.getLoanApplicationId());
        if (loanFiles != null && loanFiles.stream().anyMatch(l -> l.getFileType().getId().equals(UserFileType.FINAL_DOCUMENTATION))) {
            return AjaxResponse.errorMessage("Ya existe la documentación final");
        }

        // Save the user file
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);
        userService.registerUserFiles(file, credit.getLoanApplicationId(), loanApplication.getUserId(), UserFileType.FINAL_DOCUMENTATION);

        // Change sub status
        if (credit.getEntityProductParameterId().equals(EntityProductParams.ENT_PROD_PARAM_AELU_CONVENIO)) {
            creditDAO.updateCreditSubStatus(creditId, CreditSubStatus.AELU_PENDING_PROMISORY_NOTE);
            aeluService.sendPendingPromisoryNotEmail(loanApplication.getId());
        } else if (EntityProductParams.ENT_PROD_PARAM_ACCESO_GARANTIZADO.contains(credit.getEntityProductParameterId())) {
            creditDAO.updateCreditSubStatus(creditId, CreditSubStatus.PENDING_ENTIY_DISBURSEMENT);
        }

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/registerCreditInternalDisbursement", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:generated:aeluRegisterInternalDisbursemet", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerCreditInternalDisbursement(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("creditId") Integer creditId) throws Exception {

        // Validate the user can updateTrx the credit
        List<CreditEntityExtranetPainter> validCredits = entityExtranetService.getCreditsToShow(ExtranetMenu.TO_DISBURSE_CREDITS_MENU, null, null, locale,0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, null);
        if (validCredits.stream().noneMatch(c -> c.getId().equals(creditId) && c.isShowAeluInternalDisbursementButton())) {
            return AjaxResponse.errorMessage("El crédito no es válido");
        }

        creditDAO.updateCreditSubStatus(creditId, CreditSubStatus.PENDING_FINAL_DOCUMENTATION);
        return AjaxResponse.ok(null);
    }

    private Object commonUpdateStatus(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            String page, Integer creditId, String creditReferenceNumber, String loanReferenceNumber, EntityExtranetRegisterDisbursementForm registerDisbursementForm) throws Exception {

        int flag = 0;
        LoggedUserEntity loggedUser = entityExtranetService.getLoggedUserEntity();
        switch (page) {
            case "pending": {
                // Validate the permission
                if (!SecurityUtils.getSubject().isPermitted("credit:pending:validate")) {
                    return AjaxResponse.errorForbidden();
                }
                creditDAO.updateCreditSubStatus(creditId, CreditSubStatus.PENDING_ENTIY_LOAD);
                flag = ExtranetMenu.TO_VERIFY_CREDITS_MENU;
                model.addAttribute("page", "pending");
                break;
            }
            case "toUpload": {

                // Validate the permission
                if (!SecurityUtils.getSubject().isPermitted("credit:toUpload:upload")) {
                    return AjaxResponse.errorForbidden();
                }

                Credit credit = creditDAO.getCreditByID(creditId, locale, false, Credit.class);
                EntityProductParams entityProductParams = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());
                if (entityProductParams.getId() != EntityProductParams.ENT_PROD_PARAM_BANBIF_LIBRE_DISPONIBILIDAD &&
                        entityProductParams.getDisbursementType().equals(EntityProductParams.DISBURSEMENT_TYPE_RETIREMNT)) {
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);
                    Person person = personDao.getPerson(catalogService, locale, credit.getPersonId(), false);
                    PersonContactInformation personContact = personDao.getPersonContactInformation(locale, person.getId());

                    PersonInteraction interaction = new PersonInteraction();
                    interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                    interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.EXTRANET_ENTITY_ORIGINATED_RETIREMENT_DISBURSEMENT_MAIL, loanApplication.getCountryId()));
                    interaction.setDestination(personContact.getEmail());
                    interaction.setCreditId(creditId);
                    interaction.setPersonId(loanApplication.getPersonId());

                    JSONObject jsonVars = new JSONObject();
                    jsonVars.put("CLIENT_NAME", person.getName().split(" ")[0]);
                    jsonVars.put("ENTITY", entityExtranetService.getLoggedUserEntity().getEntities().get(0).getFullName());
                    jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
                    jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

                    interactionService.sendPersonInteraction(interaction, jsonVars, null);
                }

                if (credit.getEntityProductParameterId().equals(EntityProductParams.ENT_PROD_PARAM_AELU_CONVENIO)) {
                    creditDAO.updateCreditSubStatus(creditId, CreditSubStatus.AELU_PENDING_INTERNAL_DISBURSEMENT);
                } else if (EntityProductParams.ENT_PROD_PARAM_ACCESO_GARANTIZADO.contains(credit.getEntityProductParameterId())) {
                    creditDAO.updateCreditSubStatus(creditId, CreditSubStatus.PENDING_FINAL_DOCUMENTATION);
                } else {
                    creditDAO.updateCreditSubStatus(creditId, CreditSubStatus.PENDING_ENTIY_DISBURSEMENT);
                }

                creditDAO.updateCreditStatusExtranet(creditId, CreditStatus.ORIGINATED, loggedUser.getId());
                if (creditReferenceNumber != null && !creditReferenceNumber.isEmpty())
                    creditDAO.updateCrediCodeByCreditId(creditId, creditReferenceNumber);
                if (loanReferenceNumber != null && !loanReferenceNumber.isEmpty())
                    loanApplicationDao.updateEntityApplicationCode(credit.getLoanApplicationId(), loanReferenceNumber);
                creditDAO.updateGeneratedInEntity(creditId, loggedUser.getId());
                if (credit.getSignatureDate() == null) creditDAO.updateSignatureDate(creditId, new Date());

                if(EntityProductParams.ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD.contains(credit.getEntityProductParameterId())){
                    creditService.createContract(creditId, request, response, locale, templateEngine, "Contrato-".concat(entityExtranetService.getLoggedUserEntity().getEntities().get(0).getFullName()).concat(".pdf"), false);
                }

                flag = ExtranetMenu.TO_UPLOAD_CREDITS_MENU;
                model.addAttribute("page", "toUpload");
                break;
            }
            case "generated": {

                // Validate the permission
                if (!SecurityUtils.getSubject().isPermitted("credit:generated:disbursement")) {
                    return AjaxResponse.errorForbidden();
                }

                registerDisbursementForm.getValidator().validate(locale);
                if (registerDisbursementForm.getValidator().isHasErrors())
                    return AjaxResponse.errorFormValidation(registerDisbursementForm.getValidator().getErrorsJson());

                creditService.updateCreditConditionsAmountInstallmentsAndTea(creditId, registerDisbursementForm.getAmount(), registerDisbursementForm.getInstallments(), registerDisbursementForm.getTea(), loggedUser.getId());

                Credit credit = creditDAO.getCreditByID(creditId, Configuration.getDefaultLocale(), true, Credit.class);
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);
                Person person = personDao.getPerson(catalogService, locale, credit.getPersonId(), false);
                PersonContactInformation personContact = personDao.getPersonContactInformation(locale, person.getId());

                SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");

                EntityProductParams entityProductParams = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());

                if(entityProductParams.getId() != EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL && entityProductParams.getId() != EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL_AGENCIAS && entityProductParams.getId() != EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL_CAMPANA
                        && entityProductParams.getId() != EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL_SINIESTROS
                        && credit.getEntity().getId() != Entity.FINANSOL && credit.getEntity().getId() != Entity.PRISMA && credit.getEntity().getId() != Entity.AZTECA){
                    if (entityProductParams.getDisbursementType().equals(EntityProductParams.DISBURSEMENT_TYPE_DEPOSIT)) {


                        PersonInteraction interaction = new PersonInteraction();
                        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                        if (credit.getEntity().getId() == Entity.MULTIFINANZAS) {
                            interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.DISBURMENT_EMAIL_MULTIFINANZAS, loanApplication.getCountryId()));
                        } else {
                            if (credit.getEntity().getId() == Entity.ACCESO && credit.getProduct().getId() == Product.GUARANTEED) {
                                interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.ACCESO_GUARANTEED_EMAIL, loanApplication.getCountryId()));
                            } else if (EntityProductParams.ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD.contains(credit.getEntityProductParameterId())) {
                                interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.ACCESO_LIBRE_DISPONIBILIDAD_DISBURSEMENT, loanApplication.getCountryId()));
                            } else {
                                interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.DISBURSEMENT_MAIL_NO_DOCUMENT, loanApplication.getCountryId()));
                            }
                        }

                        interaction.setDestination(personContact.getEmail());
                        interaction.setCreditId(creditId);
                        interaction.setPersonId(person.getId());

                        JSONObject jsonVars = new JSONObject();
                        jsonVars.put("CLIENT_NAME", person.getName().split(" ")[0]);
                        jsonVars.put("ENTITY", entityExtranetService.getLoggedUserEntity().getEntities().get(0).getFullName());
                        jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);
                        jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);

                        if (credit.getEntity().getId() == Entity.MULTIFINANZAS) {
                            PersonBankAccountInformation personBankAccountInformation = personDao.getPersonBankAccountInformation(locale, loanApplication.getPersonId());
                            String cbu = "................";
                            cbu = cbu + (personBankAccountInformation != null ? personBankAccountInformation.getBankAccount().substring(personBankAccountInformation.getBankAccount().length() - 4) : "....");
                            jsonVars.put("BANK_ACCOUNT_NUMBER", cbu);
                            jsonVars.put("INSTALLMENTS", credit.getInstallments().toString());
                            jsonVars.put("INSTALLMENT_AMOUNT", utilService.customDoubleFormat(credit.getInstallmentAmountAvg(), 2));
                        }else if (EntityProductParams.ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD.contains(credit.getEntityProductParameterId())) {
                            jsonVars.put("FIRST_DISBURSEMENT_DATE", new SimpleDateFormat("dd/MM/yyyy").format(credit.getOriginalSchedule().get(0).getDueDate()));
                        }

                        ArrayList<PersonInteractionAttachment> atachementList = new ArrayList<>();

                        if (entityProductParams.getSignatureType() != EntityProductParams.CONTRACT_TYPE_MANUAL && entityProductParams.getContract() != null) {
                            byte[] contract = creditService.createContract(creditId, request, response, locale, templateEngine, "Contrato-".concat(entityExtranetService.getLoggedUserEntity().getEntities().get(0).getFullName()).concat(".pdf"), false);
                            PersonInteractionAttachment atachment = new PersonInteractionAttachment();
                            atachment.setBytes(contract);
                            atachment.setFilename("Contrato-".concat(entityExtranetService.getLoggedUserEntity().getEntities().get(0).getFullName()).concat(".pdf"));
                            atachementList.add(atachment);
                        }

                        if (credit.getEntity().getId() == Entity.MULTIFINANZAS) {
                            LoanOffer loanOffer = loanApplicationDao.getLoanOffers(loanApplication.getId()).stream().filter(o -> o.getSelected()).findFirst().orElse(null);
                            if (loanOffer != null) {
                                String extension = ".pdf";

                                EntityBranding entityBranding = catalogService.getEntityBranding(loanOffer.getEntity().getId());
                                String signature = "";
                                byte[] summarySheet = creditService.createSummarySheet(request, response, locale, templateEngine, credit,
                                        loanOffer, person, loanApplication, entityProductParams, entityBranding, signature);
                                PersonInteractionAttachment summarySheetAttachment = new PersonInteractionAttachment();
                                summarySheetAttachment.setBytes(summarySheet);
                                summarySheetAttachment.setFilename("Hoja resumen-".concat(entityExtranetService.getLoggedUserEntity().getEntities().get(0).getFullName()).concat(".").concat(extension));
                                atachementList.add(summarySheetAttachment);

                                userService.registerUserFileByte(summarySheet, loanApplication.getId(), person.getUserId(), UserFileType.HOJA_RESUMEN, extension);
                            }
                        }
                        interaction.setAttachments(atachementList);
                        interactionService.sendPersonInteraction(interaction, jsonVars, null);
                    }
                }

                creditDAO.updateDisbursementDate(creditId, sf.parse(registerDisbursementForm.getDisbursementDate()));
                creditDAO.updateDisbursmentInInEntity(creditId, loggedUser.getId());
                creditDAO.updateCreditStatusExtranet(creditId, CreditStatus.ORIGINATED_DISBURSED, loggedUser.getId());
                loanNotifierService.notifyDisbursement(credit.getLoanApplicationId(), Configuration.getDefaultLocale());
                offlineConversionService.sendOfflineConversion(credit);

                if (loanApplication.getSource() != null && loanApplication.getSource().equalsIgnoreCase(LoanApplication.LEAD_TORO)) {
                    toroLeadService.callCPAPostback(
                            loanApplication.getJsLeadParam() != null ? JsonUtil.getStringFromJson(loanApplication.getJsLeadParam(), "toro_sid", null) : null);
                }

                flag = ExtranetMenu.TO_DISBURSE_CREDITS_MENU;
                model.addAttribute("page", "generated");
                break;
            }
            case "generatedtc": {

                // Validate the permission
                if (!SecurityUtils.getSubject().isPermitted("credit:generated:disbursement")) {
                    return AjaxResponse.errorForbidden();
                }

                registerDisbursementForm.getValidator().validate(locale);
                if (registerDisbursementForm.getValidator().isHasErrors())
                    return AjaxResponse.errorFormValidation(registerDisbursementForm.getValidator().getErrorsJson());

                Credit credit = creditDAO.getCreditByID(creditId, Configuration.getDefaultLocale(), true, Credit.class);
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);
                Person person = personDao.getPerson(catalogService, locale, credit.getPersonId(), false);
                PersonContactInformation personContact = personDao.getPersonContactInformation(locale, person.getId());

                SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");

                EntityProductParams entityProductParams = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());

                creditDAO.updateDisbursementDate(creditId, sf.parse(registerDisbursementForm.getDisbursementDate()));
                creditDAO.updateDisbursmentInInEntity(creditId, loggedUser.getId());
                creditDAO.updateCreditStatusExtranet(creditId, CreditStatus.ORIGINATED_DISBURSED, loggedUser.getId());
                loanNotifierService.notifyDisbursement(credit.getLoanApplicationId(), Configuration.getDefaultLocale());
                offlineConversionService.sendOfflineConversion(credit);

                flag = ExtranetMenu.TO_DISBURSE_CREDITS_MENU;
                model.addAttribute("page", "generatedtc");
                break;
            }
            case "bank_account_saving": {

                // Validate the permission
                if (!SecurityUtils.getSubject().isPermitted("credit:generated:disbursement")) {
                    return AjaxResponse.errorForbidden();
                }


                Credit credit = creditDAO.getCreditByID(creditId, Configuration.getDefaultLocale(), false, Credit.class);

                if(credit.getEntityProductParameterId() != null && !credit.getEntityProductParameterId().equals(EntityProductParams.ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO)) return AjaxResponse.errorMessage("No puede activar esta solicitud");

                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);
                Person person = personDao.getPerson(catalogService, locale, credit.getPersonId(), false);
                PersonContactInformation personContact = personDao.getPersonContactInformation(locale, person.getId());

                SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");

                EntityProductParams entityProductParams = catalogService.getEntityProductParamById(credit.getEntityProductParameterId());

                creditDAO.updateDisbursementDate(creditId, new Date());
                creditDAO.updateDisbursmentInInEntity(creditId, loggedUser.getId());
                creditDAO.updateCreditStatusExtranet(creditId, CreditStatus.ORIGINATED_DISBURSED, loggedUser.getId());
                loanNotifierService.notifyDisbursement(credit.getLoanApplicationId(), Configuration.getDefaultLocale());

                flag = ExtranetMenu.TO_DISBURSE_CREDITS_MENU;
                model.addAttribute("page", "generated");
                break;
            }
        }

        // update the loan funnel steps
        Credit credit = creditDAO.getCreditByID(creditId, locale, false, Credit.class);
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);
        funnelStepService.registerStep(loanApplication);

        model.addAttribute("showButton", true);
        model.addAttribute("credits", entityExtranetService.getCreditsToShow(flag, null, null, locale,0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, null));
        Integer limitPaginator = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        model.addAttribute("limitPaginator", limitPaginator);

        if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) return new ModelAndView("/entityExtranet/extranetCreditsBDS :: list");

        return new ModelAndView("/entityExtranet/extranetCredits :: list");
    }

    private Object commonRejectCredit(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            String page, Integer creditId, Integer creditRejectionReasonId, String creditRejectionReasonComment) throws Exception {

        if(creditRejectionReasonComment != null && !creditRejectionReasonComment.trim().isEmpty() && creditRejectionReasonComment.trim().length() > 200) {
            return AjaxResponse.errorMessage("El comentario no puede tener más de 200 caracteres");
        }

//        LoggedUserEntity user = entityExtranetService.getLoggedUserEntity();
        Credit credit = creditDAO.getCreditByID(creditId, locale, false, Credit.class);
        CreditRejectionReason creditRejectionReason = catalogService.getCreditRejectionReason(creditRejectionReasonId);
        int flag = 0;

        credit.setRejectionReason(creditRejectionReason);
        creditDAO.registerRejectionWithComment(creditId, creditRejectionReasonId, creditRejectionReasonComment);
        loanNotifierService.notifyRejection(loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale), credit);

        switch (page) {
            case "pending": {
                if(credit.getEntity() != null && credit.getEntity().getId() != Entity.BANCO_DEL_SOL){
                    emailCLService.sendRejectionMail(credit, locale);
                }
                flag = ExtranetMenu.TO_VERIFY_CREDITS_MENU;
                break;
            }
            case "generated": {
                if(credit.getEntity() != null && credit.getEntity().getId() != Entity.BANCO_DEL_SOL) {
                    emailCLService.sendRejectionMail(credit, locale);
                    if(credit.getEntity().getId() == Entity.AZTECA){
                        loanApplicationDao.updateCurrentQuestion(credit.getLoanApplicationId() ,ProcessQuestion.Question.DISAPPROVE_EVALUATION.getId());
                    }
                }

                flag = ExtranetMenu.TO_DISBURSE_CREDITS_MENU;
                break;
            }
        }

        model.addAttribute("showButton", true);
        model.addAttribute("credits", entityExtranetService.getCreditsToShow(flag, null, null, locale,0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, null));
        Integer limitPaginator = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        model.addAttribute("limitPaginator", limitPaginator);

        if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) return new ModelAndView("/entityExtranet/extranetCreditsBDS :: list");

        return new ModelAndView("/entityExtranet/extranetCredits :: list");
    }

    //    TODO MOVER A SERVICE O UTIL???
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

    @RequestMapping(value = "/observationReasons", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getObservationReasons(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("creditId") Integer creditId) throws Exception {

        List<ObservationReason> reasonsList = catalogService.getCreditObservationReasons();
        return AjaxResponse.ok(new Gson().toJson(reasonsList));
    }

    @RequestMapping(value = "/observationReason", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object observeCredit(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("creditId") Integer creditId,
            @RequestParam("observationReasonId") Integer observationReasonId) throws Exception {

        creditDAO.updateObservation(creditId, observationReasonId);
        Credit credit = creditDAO.getCreditByID(creditId, locale, false, Credit.class);
        String message = "Credito :" + credit.getCode() + " ha sido observado por " + credit.getEntity().getShortName();
        LoanApplication application = loanApplicationService.getLoanApplicationById(credit.getLoanApplicationId());
        String link = "<a target=\"_blank\" href=\"" + utilService.createLoanApplicationClientUrl(application.getUserId(), credit.getLoanApplicationId(), credit.getPersonId()) + "\">Ir a la URL de la evaluación</a>";

        awsSesEmailService.sendEmail(
                Configuration.EMAIL_CONTACT_FROM()//from
                , "cs@solven.pe"
                , null//cc
                , "¡" + message + "!"
                , null
                , message + "<br/> Tipo de observacion : " + credit.getObservationReason().getReason() + "<br>" + link
                , null);//others

        List<CreditEntityExtranetPainter> credits = entityExtranetService.getCreditsToShow(ExtranetMenu.TO_VERIFY_CREDITS_MENU, null, null, locale,0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, null);
        model.addAttribute("credits", credits);
        model.addAttribute("page", "pending");
        model.addAttribute("showButton", true);

        if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) return new ModelAndView("/entityExtranet/extranetCreditsBDS :: list");

        return new ModelAndView("/entityExtranet/extranetCredits :: list");
    }

    @RequestMapping(value = "/beingProcessed", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:credit:beingProcessed:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showBeingProcessed(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        int userId = loggedUserEntity.getId();

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.BEING_PROCESSED_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());

        List<CreditBancoDelSolExtranetPainter> credits = entityExtranetService.getCreditsBeingProcessedByLoggedUserId(userId, null, null, null, 0, (entityExtranetService.getLoggedUserEntity().getPrincipalEntity().getId() == Entity.BANCO_DEL_SOL) ? null : PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, locale, false, products.stream().map(Product::getId).collect(Collectors.toList()),                null, null);

        Pair<Integer, Double> countAdSum = creditDAO.getEntityCreditsByLoggedUserIdCount(
                userId,
                 null,
                 null,
                null,
                products.stream().map(Product::getId).collect(Collectors.toList()),
                null,
                null,
                locale);

        model.addAttribute("credits", credits);
        model.addAttribute("totalCount", countAdSum.getLeft());
        model.addAttribute("page", "beingProcessed");
        model.addAttribute("title", "Solicitudes en Proceso");
        model.addAttribute("showButton", true);
        model.addAttribute("tray", ExtranetMenu.BEING_PROCESSED_MENU);
        model.addAttribute("showProgressSelector", false);
        model.addAttribute("products", products);

        Integer limitPaginator = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        if (entityExtranetService.getLoggedUserEntity().getPrincipalEntity().getId() != Entity.BANCO_DEL_SOL) model.addAttribute("sidebarClosed", true);
        model.addAttribute("limitPaginator", limitPaginator);
        if(entityExtranetService.getLoggedUserEntity().getPrincipalEntity().getId() == Entity.BANCO_DEL_SOL)
            return new ModelAndView("/entityExtranet/extranetBancoDelSolCredits");

        EntityExtranetConfiguration extranetConfiguration = brandingService.getExtranetBrandingAsJson(entityExtranetService.getLoggedUserEntity().getPrincipalEntity().getId());
        if(extranetConfiguration != null && extranetConfiguration.getInProcessCreditPageConfiguration() != null && extranetConfiguration.getInProcessCreditPageConfiguration().getDetailConfiguration() != null)  return new ModelAndView("/entityExtranet/extranetPageWithDetail");

        return new ModelAndView("/entityExtranet/extranetBeingProcessed");

    }

    @RequestMapping(value = "/beingProcessed/list", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showBeingProcessedList(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam(value = "creationFrom[]", required = false) String creationFrom,
            @RequestParam(value = "creationTo[]", required = false) String creationTo) throws Exception {

        int userId = entityExtranetService.getLoggedUserEntity().getId();
        List<CreditBancoDelSolExtranetPainter> credits = entityExtranetService.getCreditsBeingProcessedByLoggedUserId(userId, creationFrom != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null, creationTo != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null, null, 0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, locale);

        model.addAttribute("credits", credits);
        model.addAttribute("page", "beingProcessed");
        model.addAttribute("title", "Solicitudes en Proceso");
        model.addAttribute("showButton", true);

        return new ModelAndView("/entityExtranet/extranetBeingProcessed :: list");
    }

    @RequestMapping(value = "/updateInternalStatus", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object updateInternalStatus(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("creditId") Integer creditId,
            @RequestParam("internalStatus") String internalStatus) throws Exception {

        Credit credit = creditDAO.getCreditByID(creditId, locale, false, Credit.class);
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);

        loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_INTERNAL_CREDIT_STATUS.getKey(), internalStatus);
        loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/redirectToClientProcess", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "loanApplication:viewAsClient", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object redirectToLoanProces(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam(value = "creditId", required = false) Integer creditId,
            @RequestParam(value = "loanApplicationId", required = false) Integer loanApplicationId) throws Exception {

        if(creditId != null){
            Credit credit = creditDAO.getCreditByID(creditId, locale, false, Credit.class);
            if (credit == null || !credit.getEntity().getId().equals(entityExtranetService.getLoggedUserEntity().getPrincipalEntity().getId()))
                throw new Exception("The credit doesnt exists");
            LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);
            return "redirect:"+loanApplicationService.generateLoanApplicationLinkEntity(loanApplication);
        }else if(loanApplicationId != null){
            LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId, locale);
            if (loanApplication == null || loanApplication.getEntityId() == null || !loanApplication.getEntityId().equals(entityExtranetService.getLoggedUserEntity().getPrincipalEntity().getId()))
                throw new Exception("The credit doesnt exists");
            return "redirect:"+loanApplicationService.generateLoanApplicationLinkEntity(loanApplication);
        }
        return null;
    }

    @RequestMapping(value = "/loanApplication/{page}/expire", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "loan:expire", type = RequiresPermissionOr403.Type.AJAX, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object expireLoanApplication(ModelMap model, Locale locale, @PathVariable("page") String page, @RequestParam("loanId") Integer loanId) throws Exception {
        if ("terminated".equalsIgnoreCase(page)) {
            return AjaxResponse.errorMessage("Esta bandeja no tiene permitido expirar solicitudes");
        }

        int flag;
        String fragmentToUpdate;
        switch (page) {
            case "pending": {
                flag = ExtranetMenu.TO_VERIFY_CREDITS_MENU;
                fragmentToUpdate = "/entityExtranet/extranetCredits :: list";
                if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) fragmentToUpdate ="/entityExtranet/extranetCreditsBDS :: list";
                break;
            }
            case "generated": {
                flag = ExtranetMenu.TO_DISBURSE_CREDITS_MENU;
                fragmentToUpdate = "/entityExtranet/extranetCredits :: list";
                if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) fragmentToUpdate ="/entityExtranet/extranetCreditsBDS :: list";
                break;
            }
            case "toUpload": {
                flag = ExtranetMenu.TO_UPLOAD_CREDITS_MENU;
                fragmentToUpdate = "/entityExtranet/extranetCredits :: list";
                if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) fragmentToUpdate ="/entityExtranet/extranetCreditsBDS :: list";
                break;
            }
            case "beingProcessed": {
                flag = ExtranetMenu.BEING_PROCESSED_MENU;
                if(entityExtranetService.getLoggedUserEntity().getPrincipalEntity().getId() == Entity.BANCO_DEL_SOL)
                    fragmentToUpdate = "/entityExtranet/extranetBancoDelSolCredits :: list";
                else
                    fragmentToUpdate = "/entityExtranet/extranetBeingProcessed :: list";
                break;
            }
            default: {
                return AjaxResponse.errorMessage("Esta bandeja no tiene permitido expirar solicitudes");
            }
        }

        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanId, locale);

        if (loanApplication.getStatus().getId() == LoanApplicationStatus.EXPIRED) {
            return AjaxResponse.errorMessage("La solicitud ya se encuentra expirada");
        } else if (loanApplication.getStatus().getId() == LoanApplicationStatus.REJECTED || loanApplication.getStatus().getId() == LoanApplicationStatus.REJECTED_AUTOMATIC || loanApplication.getStatus().getId() == LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION) {
            loanApplicationDao.expireLoanApplication(loanId);
            return AjaxResponse.errorMessage("La solicitud ya se encuentra rechazada");
        } else if (loanApplication.getStatus().getId() != LoanApplicationStatus.EXPIRED) {
            loanApplicationDao.expireLoanApplication(loanId);
        } else {
            return AjaxResponse.errorMessage("No es posible expirar esta solicitud");
        }

        if (flag == ExtranetMenu.BEING_PROCESSED_MENU) {
            int userId = entityExtranetService.getLoggedUserEntity().getId();
            model.addAttribute("credits", entityExtranetService.getCreditsBeingProcessedByLoggedUserId(userId, null, null, null, 0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, locale));
        } else {
            model.addAttribute("credits", entityExtranetService.getCreditsToShow(flag, null, null, locale,0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, null));
        }
        model.addAttribute("showButton", true);

        return new ModelAndView(fragmentToUpdate);
    }

    @RequestMapping(value = "/callcenter", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:credit:callcenter:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showCallCenter(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(), ExtranetMenu.CALL_CENTER_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());

        List<CreditEntityExtranetPainter> credits = entityExtranetService.getCreditsToShow(ExtranetMenu.CALL_CENTER_MENU, null, null, locale,0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, null);
        Pair<Integer, Double> countAdSum = entityExtranetService.getCreditsToShowCount(ExtranetMenu.CALL_CENTER_MENU, null, null, null, locale);


        Integer limitPaginator = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        model.addAttribute("limitPaginator", limitPaginator);
        model.addAttribute("totalCount", countAdSum.getLeft());
        model.addAttribute("credits", credits);
        model.addAttribute("showFinalDocumentationModal", credits.stream().anyMatch(c -> c.isShowAeluUploadFinalDocumentationButton() || c.isShowAccesoUploadFinalDocumentationButton()));
        model.addAttribute("showAeluInternalDisbursementModal", credits.stream().anyMatch(c -> c.isShowAeluInternalDisbursementButton()));
        model.addAttribute("page", "callcenter");
        model.addAttribute("title", "Call Centers");
        model.addAttribute("registerDisbursementForm", new EntityExtranetRegisterDisbursementForm());
        if (loggedUserEntity.getEntities().get(0).getId() != null && loggedUserEntity.getEntities().get(0).getId() == Entity.BANCO_DEL_SOL) {
            JSONObject jsonHierarchy = personDao.getBancoDelSolEmployeeHierarchy(loggedUserEntity.getId());
            model.addAttribute("userHierarchy", jsonHierarchy.isNull("level1") && jsonHierarchy.isNull("level2") && jsonHierarchy.isNull("level3") ? null : jsonHierarchy);
        }
        model.addAttribute("showButton", true);
        model.addAttribute("tray", ExtranetMenu.CALL_CENTER_MENU);

        if(entityExtranetService.getLoggedUserEntity().containsEntityId(Entity.BANCO_DEL_SOL)) return new ModelAndView("/entityExtranet/extranetCreditsBDS");

        return new ModelAndView("/entityExtranet/extranetCredits");
    }

    @RequestMapping(value = "/bandeja/download/{bandeja}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "report:create", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public void extranetExportCreditExcel(@PathVariable Integer bandeja, HttpServletResponse response, Locale locale) throws Exception {

        List<CreditEntityExtranetPainter> credits = entityExtranetService.getCreditsToShow(bandeja, null, null, locale, null, null, null);
        String name;
        switch (bandeja) {
            case ExtranetMenu.TO_DELIVER_TC_MENU:
                name = "Tarjetas_a_entregar";
                break;
            case ExtranetMenu.DELIVERED_TC_MENU:
                name = "Tarjetas_entregadas";
                break;
            default:
                name = "Archivo";
        }

        byte[] file = reportsService.createCreditsReport(credits, name);
        if (file != null) {
            MediaType contentType = MediaType.valueOf("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment; filename=" + name + ".xls");
            response.setContentType(contentType.getType());
            response.getOutputStream().write(file);
        }
    }

    @RequestMapping(value = "/rejected", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:credit:rejected:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showRejected(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();


        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(),ExtranetMenu.REJECTED_MENU);
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());
        List<CreditBancoDelSolExtranetPainter> credits = entityExtranetService.getRejectedLoanApplications(loggedUserEntity.getPrincipalEntity().getId(),null,null,locale,null,0,PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, false, products.stream().map(Product::getId).collect(Collectors.toList()),null);
        Pair<Integer, Double> countAdSum = creditDAO.getEntityRejectedLoanApplicationsCount(loggedUserEntity.getPrincipalEntity().getId(),null,null, null, products.stream().map(Product::getId).collect(Collectors.toList()),null, locale);

        Integer limitPaginator = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;

        EntityAllRejectionReasons entityAllRejectionReasons = creditDAO.getEntityAllRejectionReason(loggedUserEntity.getPrincipalEntity().getId(),locale);

        model.addAttribute("limitPaginator", limitPaginator);
        model.addAttribute("totalCount", countAdSum.getLeft());
        model.addAttribute("credits", credits);
        model.addAttribute("page", "rejected");
        model.addAttribute("title", "Solicitudes rechazadas");
        model.addAttribute("showButton", true);
        model.addAttribute("tray", ExtranetMenu.REJECTED_MENU);
        model.addAttribute("products", products);
        model.addAttribute("entityAllRejectionReasons", entityAllRejectionReasons);

        EntityExtranetConfiguration extranetConfiguration = brandingService.getExtranetBrandingAsJson(loggedUserEntity.getPrincipalEntity().getId());
        if(extranetConfiguration != null && extranetConfiguration.getRejectedPageConfiguration() != null && extranetConfiguration.getRejectedPageConfiguration().getDetailConfiguration() != null)  return new ModelAndView("/entityExtranet/extranetPageWithDetail");

        return new ModelAndView("/entityExtranet/extranetRejected");
    }

}
