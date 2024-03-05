package com.affirm.backoffice.controller;

import com.affirm.backoffice.dao.CreditBODAO;
import com.affirm.backoffice.model.CreditBoPainter;
import com.affirm.backoffice.model.CreditPendingDisbursementBoPainter;
import com.affirm.backoffice.model.LoanApplicationBoPainter;
import com.affirm.backoffice.model.PersonInteractionPainter;
import com.affirm.backoffice.model.form.RegisterDisbursementForm;
import com.affirm.backoffice.service.BackofficeService;
import com.affirm.backoffice.util.PaginationWrapper;
import com.affirm.common.dao.*;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.Currency;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.CryptoUtil;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.Marshall;
import com.affirm.compartamos.CompartamosServiceCall;
import com.affirm.compartamos.model.GenerarCreditoRequest;
import com.affirm.compartamos.model.GenerarCreditoResponse;
import com.affirm.nosis.NosisResult;
import com.affirm.security.model.SysUser;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
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

/**
 * @author jrodriguez
 */
@Controller
@Scope("request")
public class CreditController {

    private static Logger logger = Logger.getLogger(CreditController.class);
    private final Integer TAB_MANAGEMENT = 1;

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CreditBODAO creditBoDao;
    @Autowired
    private CreditService creditService;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private AwsSesEmailService awsSesEmailService;
    @Autowired
    private LoanNotifierService loanNotifierService;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private FileService fileService;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private BackofficeService backofficeService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private ReportsService reportsService;
    @Autowired
    private ReportsDAO reportsDao;
    @Autowired
    private InteractionDAO interactionDao;
    @Autowired
    private CreditDAO creditDAO;
    @Autowired
    private CompartamosServiceCall compartamosServiceCall;
    @Autowired
    private PersonService personService;
    @Autowired
    private TranslatorDAO translatorDAO;

    private SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd");


    @RequestMapping(value = "/disbursement/pending", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:disbursement:pending", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String getPendingDisbursement(
            ModelMap model, Locale locale,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", required = false, defaultValue = "250") Integer limit) throws Exception {

        PaginationWrapper<CreditPendingDisbursementBoPainter> credits = creditBoDao.getPendigDisbursement(backofficeService.getCountryActiveSysuser(), null, null, null, null, locale, 0, Configuration.BACKOFFICE_PAGINATION_LIMIT);
        model.addAttribute("credits", credits);
        return "creditPendingDisbursements";
    }

    @RequestMapping(value = "/disbursement/pending/reportSeguripago", method = RequestMethod.GET)
//    @RequiresPermissionOr403(permissions = "credit:disbursement:seguripagoReport", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public void getReporteSeguripago(
            ModelMap model, Locale locale, HttpServletResponse response) throws Exception {


        byte[] file = reportsService.createProsegurPendingDisbursementConsolidationReport(locale);
        if (file != null) {
            List<PendingDisbursementConsolidationReportDetail> reports = reportsDao.getPendingDisbursementConsolidationReport();
            if (reports != null) {
                for (PendingDisbursementConsolidationReportDetail report : reports) {
                    creditBoDao.registerDisbursementBuffer(report.getCreditId());
                }
            }

            MediaType contentType = MediaType.valueOf("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment; filename=prosegur.xlsx");
            response.setContentType(contentType.getType());
            response.getOutputStream().write(file);
        }
    }

    @RequestMapping(value = "/disbursement/pending2/list", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:disbursement:pending", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getPendingDisbursementList(
            ModelMap model, Locale locale,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", required = false, defaultValue = "250") Integer limit) throws Exception {
        model.addAttribute("credits", creditBoDao.getPendigDisbursement(backofficeService.getCountryActiveSysuser(), null, null, null, null, locale, 0, Configuration.BACKOFFICE_PAGINATION_LIMIT));
        return new ModelAndView("creditPendingDisbursements :: #portletCreditsPendingDisbursement");
    }

    @RequestMapping(value = "/disbursement/pending/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:disbursement:pending", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getPendingDisbursementList(ModelMap model, Locale locale,
                                             @RequestParam(value = "document_number[]", required = false) String documentNumber,
                                             @RequestParam(value = "product[]", required = false) Integer[] producttId,
                                             @RequestParam(value = "entity[]", required = false) Integer[] entityId,
                                             @RequestParam(value = "employer[]", required = false) Integer[] employerId,
                                             @RequestParam(value = "offset", required = false) Integer offset) throws Exception {
        PaginationWrapper<CreditPendingDisbursementBoPainter> credits = creditBoDao.getPendigDisbursement(
                backofficeService.getCountryActiveSysuser(),
                documentNumber,
                producttId != null ? (producttId.length > 0 ? producttId[0] : null) : null,
                entityId != null ? (entityId.length > 0 ? entityId[0] : null) : null,
                employerId != null ? (employerId.length > 0 ? employerId[0] : null) : null,
                locale,
                offset,
                Configuration.BACKOFFICE_PAGINATION_LIMIT);
        model.addAttribute("credits", credits);
        return new ModelAndView("creditPendingDisbursements :: list");
    }

    @RequestMapping(value = "/disbursement/confirmation", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:disbursement:confirmation", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String getPendingDisbursementConfirmation(ModelMap model, Locale locale,
                                                     @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset) throws Exception {
        PaginationWrapper<CreditBoPainter> credits = creditBoDao.getPendigDisbursementConfirmation(backofficeService.getCountryActiveSysuser(), locale, null, null, null,
                null, null, null, Configuration.BACKOFFICE_PAGINATION_LIMIT, offset);
        model.addAttribute("offset", offset);
        model.addAttribute("credits", credits);
        return "creditPendingDisbursementsConfirmation";
    }

    @RequestMapping(value = "/disbursement/confirmation2/list", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:disbursement:confirmation", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getPendingDisbursementConfirmationList2(ModelMap model, Locale locale,
                                                          @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset) throws Exception {

        PaginationWrapper<CreditBoPainter> credits = creditBoDao.getPendigDisbursementConfirmation(backofficeService.getCountryActiveSysuser(), locale, null, null, null,
                null, null, null, Configuration.BACKOFFICE_PAGINATION_LIMIT, offset);
        model.addAttribute("offset", offset);
        model.addAttribute("credits", credits);
        return new ModelAndView("creditPendingDisbursementsConfirmation :: #portletCreditsPendingConfirmationDisbursement");
    }

    @RequestMapping(value = "/disbursement/confirmation/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:disbursement:confirmation", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getPendingDisbursementConfirmationList(ModelMap model, Locale locale,
                                                         @RequestParam(value = "document_number[]", required = false) String documentNumber,
                                                         @RequestParam(value = "product[]", required = false) Integer[] producttId,
                                                         @RequestParam(value = "entity[]", required = false) Integer[] entityId,
                                                         @RequestParam(value = "employer[]", required = false) Integer[] employerId,
                                                         @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset) throws Exception {

        PaginationWrapper<CreditBoPainter> credits = creditBoDao.getPendigDisbursementConfirmation(backofficeService.getCountryActiveSysuser(), locale, null, null, documentNumber,
                producttId != null ? (producttId.length > 0 ? producttId[0] : null) : null,
                entityId != null ? (entityId.length > 0 ? entityId[0] : null) : null,
                employerId != null ? (employerId.length > 0 ? employerId[0] : null) : null,
                Configuration.BACKOFFICE_PAGINATION_LIMIT,
                offset);
        model.addAttribute("offset", offset);
        model.addAttribute("credits", credits);
        return new ModelAndView("creditPendingDisbursementsConfirmation :: list");
    }

    @RequestMapping(value = "/credit/registerDisbursement/modal", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "credit:disbursement:register", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getRegisterDisbursementModal(ModelMap model, Locale locale,
                                               @RequestParam("creditsId") Integer[] creditsId) throws Exception {
        RegisterDisbursementForm form = new RegisterDisbursementForm();
        form.setDisbursementDate(new SimpleDateFormat(Configuration.BACKOFFICE_FRONT_ONLY_DATE_FORMAT).format(new Date()));
        model.addAttribute("registerDisbursementForm", form);
        model.addAttribute("creditsId", Arrays.stream(creditsId).map(c -> c + "").collect(Collectors.joining(",")));
        return new ModelAndView("fragments/pendingDisbursementsFragments :: registerDisbursementModal");
    }

    @RequestMapping(value = "/credit/{creditId}/registerDisbursement", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:disbursement:register", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerDisbursement(
            ModelMap model, Locale locale,
            RegisterDisbursementForm registerDisbursementForm,
            @PathVariable("creditId") Integer creditId) throws Exception {

        /*registerDisbursementForm.getValidator().validate(locale);
        if (registerDisbursementForm.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(registerDisbursementForm.getValidator().getErrorsJson());
        }*/

        JSONObject jsonValidation = creditDAO.preDisbursementValidation(creditId);

        boolean success = true;

        boolean v1 = JsonUtil.getBooleanFromJson(jsonValidation, "v1", true);
        success = success && v1;
        boolean v2 = JsonUtil.getBooleanFromJson(jsonValidation, "v2", true);
        success = success && v2;
        boolean v3 = JsonUtil.getBooleanFromJson(jsonValidation, "v3", true);
        success = success && v3;
        boolean v4 = JsonUtil.getBooleanFromJson(jsonValidation, "v4", true);
        success = success && v4;
        boolean v5 = JsonUtil.getBooleanFromJson(jsonValidation, "v5", true);
        success = success && v5;
        boolean v6 = JsonUtil.getBooleanFromJson(jsonValidation, "v6", true);
        success = success && v6;
        boolean v7 = JsonUtil.getBooleanFromJson(jsonValidation, "v7", true);
        success = success && v7;
        boolean v8 = JsonUtil.getBooleanFromJson(jsonValidation, "v8", true);
        success = success && v8;
        boolean v9 = JsonUtil.getBooleanFromJson(jsonValidation, "v9", true);
        success = success && v9;
        boolean v10 = JsonUtil.getBooleanFromJson(jsonValidation, "v10", true);
        success = success && v10;
        boolean v11 = JsonUtil.getBooleanFromJson(jsonValidation, "v11", true);
        success = success && v11;

        if (!success) {
            String msg = "\n";
            if (!v1) {
                msg += "La fecha de aprobación de la solicitud ha caducado.\n";
            }
            if (!v2) {
                msg += "El email no se encuentra verificado.\n";
            }
            if (!v3) {
                msg += "El contrato no está firmado de acuerdo a los parámetros de “firma ok”.\n";
            }
            if (!v4) {
                msg += "Los datos bancarios necesarios para la transferencia no están presentes o no son válidos.\n";
            }
            if (!v5) {
                msg += "Falta el check del analista al contrato firmado en el backoffice.\n";
            }
            if (!v6) {
                msg += "El email ya se utilizó para otra solicitud de crédito con otro DNI.\n";
            }
            if (!v7) {
                msg += "El teléfono celular verificado se utilizó para otra solicitud de crédito con otro DNI.\n";
            }
            if (!v8) {
                msg += "Caida por RCC.\n";
            }
            if (!v9) {
                msg += "El solicitante tiene un crédito en mora con Solven.\n";
            }
            if (!v10) {
                msg += "Solicitante se encuentra en lista negra.\n";
            }
            if (!v11) {
                msg += "El dispositivo utilizado por el solicitante tiene una solicitud de crédito " +
                        "(en cualquier estado) o un crédito, vinculado a un DNI distinto.\n";
            }
            return AjaxResponse.errorMessage(msg);
        }

        creditBoDao.registerDisbursementBuffer(creditId);

        awsSesEmailService.sendEmail(
                Configuration.getDisbursmentMailTo()//from
                , Configuration.getDisbursmentConfirmatorMailTo()//to
                , null//cc
                , "¡Hora de FIRMAR un desembolso! =D"//title
                , "Corre, Facundo, CORRE!"
                , null//body
                , null);//others

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/credit/{creditId}/registerDisbursementRejection", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:disbursement:confirm", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerDisbursementRejection(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @PathVariable("creditId") Integer creditId,
            @RequestParam(value = "creditRejectionReasonId", required = false) Integer creditRejectionReasonId,
            @RequestParam(value = "creditRejectionComment", required = false) String creditRejectionComment) throws Exception {

        if(creditRejectionComment != null && !creditRejectionComment.trim().isEmpty() && creditRejectionComment.trim().length() > 200) {
            return AjaxResponse.errorMessage("El comentario no puede tener más de 200 caracteres");
        }

        creditBoDao.registerDisbursementConfirmationWithComment(creditId, creditRejectionReasonId, ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId(), null, null, null, null, creditRejectionComment);
        if (creditRejectionReasonId == CreditRejectionReason.POSSIBLE_FRAUD)
            creditBoDao.insertNegativBase(creditId);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/credit/{creditId}/registerDisbursementConfirmation", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:disbursement:confirm", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerDisbursementConfirmation(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            RegisterDisbursementForm registerDisbursementForm,
            @PathVariable("creditId") Integer creditId,
            @RequestParam(value = "creditRejectionReasonId", required = false) Integer creditRejectionReasonId) throws Exception {

        registerDisbursementForm.getValidator().validate(locale);
        if (registerDisbursementForm.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(registerDisbursementForm.getValidator().getErrorsJson());
        }

        creditBoDao.registerDisbursementConfirmation(
                creditId,
                creditRejectionReasonId,
                ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId(),
                new SimpleDateFormat("dd/MM/yyyy").parse(registerDisbursementForm.getDisbursementDate()),
                registerDisbursementForm.getPaymentType(),
                registerDisbursementForm.getSignatureSysuser(),
                registerDisbursementForm.getPaymentCheckNumber());

        if (creditRejectionReasonId == null)
            creditService.createContract(creditId, request, response, locale, templateEngine, null, false);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/credit/registerDisbursementConfirmation", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:disbursement:confirm", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerMultipleDisbursementConfirmation(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            RegisterDisbursementForm registerDisbursementForm,
            @RequestParam("credits[]") Integer[] credits,
            @RequestParam(value = "creditRejectionReasonId", required = false) Integer creditRejectionReasonId) throws Exception {

        registerDisbursementForm.getValidator().validate(locale);
        if (registerDisbursementForm.getValidator().isHasErrors()) {
            return AjaxResponse.errorFormValidation(registerDisbursementForm.getValidator().getErrorsJson());
        }

        if (credits != null) {
            for (int i = 0; i < credits.length; i++) {
                creditBoDao.registerDisbursementConfirmation(
                        credits[i],
                        creditRejectionReasonId,
                        ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId(),
                        new SimpleDateFormat("dd/MM/yyyy").parse(registerDisbursementForm.getDisbursementDate()),
                        registerDisbursementForm.getPaymentType(),
                        registerDisbursementForm.getSignatureSysuser(),
                        registerDisbursementForm.getPaymentCheckNumber());

                if (creditRejectionReasonId == null)
                    creditService.createContract(credits[i], request, response, locale, templateEngine, null, false);
            }
        }
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/credit/{creditId}/reject", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:disbursement:refuse", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object rejectCredit(
            ModelMap model, Locale locale,
            @PathVariable("creditId") Integer creditId,
            @RequestParam(value = "creditRejectionReasonId", required = false) Integer creditRejectionReasonId,
            @RequestParam(value = "creditRejectionComment", required = false) String creditRejectionComment) throws Exception {

        if(creditRejectionComment != null && !creditRejectionComment.trim().isEmpty() && creditRejectionComment.trim().length() > 200) {
            return AjaxResponse.errorMessage("El comentario no puede tener más de 200 caracteres");
        }

        creditDAO.registerRejectionWithComment(creditId, creditRejectionReasonId, creditRejectionComment);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/credits", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:credits", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showCredits(
            ModelMap model, Locale locale) throws Exception {
        Integer limit = Configuration.BACKOFFICE_PAGINATION_LIMIT;
        PaginationWrapper<CreditBoPainter> creditos = creditBoDao.getCreditsFilter(
                backofficeService.getCountryActiveSysuser(),
                locale,
                null,
                null,
                null,
                null,
                null,
                TAB_MANAGEMENT,
                null,
                null,
                null,
                null,
                0,
                limit);

        model.addAttribute("offset", 0);
        model.addAttribute("credits", creditos);
        model.addAttribute("tabId", 1);
        return "credits";
    }


    @RequestMapping(value = "/credits/filter", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:credits", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showCreditsFilter(
            ModelMap model, Locale locale,
            @RequestParam(value = "tabId", defaultValue = "1") Integer tabId,
            @RequestParam(value = "creationFrom[]", required = false) String creationFrom,
            @RequestParam(value = "creationTo[]", required = false) String creationTo,
            @RequestParam(value = "document_number[]", required = false) String documentNumber,
            @RequestParam(value = "employer[]", required = false) Integer[] employer,
            @RequestParam(value = "product[]", required = false) Integer[] productId,
            @RequestParam(value = "analyst[]", required = false) String[] analystId,
            @RequestParam(value = "entity[]", required = false) Integer[] entityId,
            @RequestParam(value = "branding[]", required = false) Boolean[] isBranded,
            @RequestParam(value = "welcomeCall[]", required = false) Boolean[] welcomeCall,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset) throws Exception {
        // TabId -> 1: con gestion, 2: sin gestion, 3: adelanto de sueldo
        PaginationWrapper<CreditBoPainter> creditos = creditBoDao.getCreditsFilter(backofficeService.getCountryActiveSysuser(), locale,
                creationFrom != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                productId,
                analystId,
                entityId,
                tabId,
                documentNumber,
                employer != null ? (employer.length > 0 ? employer[0] : null) : null,
                isBranded != null ? (isBranded.length > 0 ? isBranded[0] : null) : null,
                welcomeCall != null ? (welcomeCall.length > 0 ? welcomeCall[0] : null) : null,
                offset,
                Configuration.BACKOFFICE_PAGINATION_LIMIT);

        model.addAttribute("credits", creditos);
        model.addAttribute("offset", offset);
        model.addAttribute("tabId", tabId);
        return new ModelAndView("credits :: list");
    }

    @RequestMapping(value = "/credit/{creditId}/registerCollectionContactResult", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:collection:contactResult:register", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerContact(
            ModelMap model, Locale locale,
            @PathVariable("creditId") Integer creditId,
            @RequestParam("date") String date,
            @RequestParam("contactResultId") Integer contactResultId,
            @RequestParam("amount") Integer amount,
            @RequestParam("comment") String comment) throws Exception {

        creditBoDao.registerCollectionContactResult(creditId,
                ((SysUser) SecurityUtils.getSubject().getPrincipal()).getId(), contactResultId,
                date != null && !date.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(date) : null,
                amount, comment);
        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/credit/{creditId}/registerFraud", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "credit:collection:contactResult:register", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerContact(
            ModelMap model, Locale locale,
            @PathVariable("creditId") Integer creditId) {

        creditBoDao.insertNegativBase(creditId);

        return AjaxResponse.ok(null);
    }


    @RequestMapping(value = "/collections", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:collection", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showCollections(
            ModelMap model, Locale locale) throws Exception {
        model.addAttribute("creditCollection", creditBoDao.getCreditCollectionFilter(
                backofficeService.getCountryActiveSysuser(),
                locale, null, null, null, null, null, null,
                0, Configuration.BACKOFFICE_PAGINATION_LIMIT, null,
                null,
                null,
                null));
        return "collections";
    }

    @RequestMapping(value = "/collections/filter", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:collection", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object showCollectionsFiltered(
            ModelMap model, Locale locale,
            @RequestParam(value = "product[]", required = false) Integer[] productId,
            @RequestParam(value = "cluster[]", required = false) Integer[] clusterId,
            @RequestParam(value = "collector[]", required = false) String collector,
            @RequestParam(value = "entity[]", required = false) Integer[] entityId,
            @RequestParam(value = "tranche[]", required = false) Integer[] trancheId,
            @RequestParam(value = "paused[]", required = false) Boolean paused,
            @RequestParam(value = "documentNumber[]", required = false) String documentNumber,
            @RequestParam(value = "employer[]", required = false) Integer[] employerId,
            @RequestParam(value = "dueDateFrom[]", required = false) String dueDateFrom,
            @RequestParam(value = "dueDateTo[]", required = false) String dueDateTo,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit) throws Exception {

        //Validate if there is any filter
        if (productId != null || clusterId != null || collector != null || entityId
                != null || trancheId != null || paused != null || documentNumber != null || employerId != null ||
                employerId != null || dueDateFrom != null || dueDateTo != null) {
            model.addAttribute("creditCollection", creditBoDao.getCreditCollectionFilter(
                    backofficeService.getCountryActiveSysuser(),
                    locale, productId, clusterId, collector, entityId, trancheId, paused,
                    offset, limit, documentNumber,
                    employerId != null ? (employerId.length > 0 ? employerId[0] : null) : null,
                    dueDateFrom != null ? new SimpleDateFormat("dd/MM/yyyy").parse(dueDateFrom) : null,
                    dueDateTo != null ? new SimpleDateFormat("dd/MM/yyyy").parse(dueDateTo) : null));
        } else {
            model.addAttribute("creditCollection", creditBoDao.getCreditCollection(backofficeService.getCountryActiveSysuser(), locale, 0, Configuration.BACKOFFICE_PAGINATION_LIMIT));
        }
        return new ModelAndView("collections :: list");
    }

    @RequestMapping(value = "/externalView/credit/{token}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "credit:view:externalView", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getCreditViewScreen(
            ModelMap model, Locale locale,
            @PathVariable("token") String token) throws Exception {
        JSONObject jsonToken = new JSONObject(CryptoUtil.decrypt(token));
        Integer creditId = JsonUtil.getIntFromJson(jsonToken, "creditId", null);

        CreditBoPainter credit = creditDAO.getCreditBO(creditId, locale, CreditBoPainter.class);
        credit.setShowExpanded(true);
        credit.setManagementSchedule(creditDAO.getManagementSchedule(credit.getId()));
        credit.setConsolidatedDebts(creditDAO.getConsolidatedDebts(credit.getId()));
        credit.setDownPaymentDetail(creditDAO.getDownPayments(credit.getId()));
        credit.setLoanApplication(loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale, LoanApplicationBoPainter.class));
        credit.setEntityProductParams(catalogService.getEntityProductParamById(credit.getEntityProductParameterId()));
        Person person = personDao.getPerson(catalogService, locale, credit.getPersonId(), true);

        List<Object> interactions = new ArrayList<>();
        List<PersonInteraction> personInteractions = interactionDao.getPersonInteractions(credit.getPersonId(), locale);
        List<GatewayContacts> gatewayContacts = personDao.getCollectionContacts(credit.getPersonId());

        List<Object> allInteractions = new ArrayList<>();
        if (personInteractions != null)
            allInteractions.addAll(personInteractions);
        if (gatewayContacts != null)
            allInteractions.addAll(gatewayContacts);

        for (Object obj : allInteractions) {
            Integer loanApplicationId = obj instanceof PersonInteraction ? ((PersonInteraction) obj).getLoanApplicationId() : null;

            if (creditId != null && credit.getId().equals(creditId)) {
                interactions.add(obj);
            } else if (loanApplicationId != null && credit.getLoanApplicationId().equals(loanApplicationId)) {
                interactions.add(obj);
            }
        }

        PersonInteractionPainter painter = new PersonInteractionPainter();
        painter.setCreditId(credit.getId());
        painter.setCreditCode(credit.getCode());
        painter.setLoanApplicationId(credit.getLoanApplicationId());
        painter.setRegisterDate(credit.getRegisterDate());

        if (credit.getLoanApplicationId() != null) {
            LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);
            painter.setLoanApplicationCode(loanApplication != null ? loanApplication.getCode() : null);
        }
        painter.setInteractions(interactions);

        credit.setInteractions(painter);

        if (person.getDocumentType().getCountryId() == CountryParam.COUNTRY_ARGENTINA) {
            Integer personId = person.getId();
            String nse = personService.getNosisNSE(personId);
            SocioeconomicLevel socioeconomicLevel = null;
            if (nse != null) {
                socioeconomicLevel = catalogService.getSocioEconomicLevelByLevel(nse);
            }
            BcraResult bcraResult = personDao.getBcraResult(personId);
            Double bcraIndebtedness = null;
            if (bcraResult != null && bcraResult.getHistorialDeudas().size() > 0) {
                List<String> periods = bcraResult.getHistorialDeudas().get(0).getHistorial().stream().map(e -> e.getPeriodo()).collect(Collectors.toList());
                for (int i = 0; i < periods.size(); i++) {
                    bcraIndebtedness = bcraResult.getDeudaWorstSituacionByPeriod(i);
                    if (bcraIndebtedness != null && bcraIndebtedness != 0) {
                        break;
                    }
                }
            }

            Double estimateIncome = null;
            Double nseAverageValue = Double.MAX_VALUE;
            Double personalIncome = Double.MAX_VALUE;
            if (socioeconomicLevel != null) {
                nseAverageValue = 0.8 * (new Double(socioeconomicLevel.getAvgIncome()));
            }

            PersonOcupationalInformation principalOcupationInformation = null;
            if(personDao.getPersonOcupationalInformation(locale, person.getId()) != null){
                principalOcupationInformation = personDao.getPersonOcupationalInformation(locale, person.getId())
                        .stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL)
                        .findFirst().orElse(null);
            }

            if (principalOcupationInformation != null) {
                personalIncome = principalOcupationInformation.getFixedGrossIncome();
            }
            if (principalOcupationInformation != null || socioeconomicLevel != null) {
                estimateIncome = nseAverageValue < personalIncome ? nseAverageValue : personalIncome;
            }

            Double dti = null;
            Double nosisComitment = null;
            if (personService.getNosisCommitment(personId) != null) {
                nosisComitment = new Double(personService.getNosisCommitment(personId));
            }

            if (estimateIncome != null && estimateIncome != 0 && nosisComitment != null) {
                dti = (credit.getInstallmentAmountAvg() + nosisComitment) / estimateIncome;
            }
            credit.setNosisCommitment(nosisComitment);
            credit.setBcraIndebtedness(bcraIndebtedness);
            credit.setEstimateIncome(estimateIncome);
            credit.setDti(dti);
        }

        if (EntityProductParams.ENT_PROD_PARAM_BANBIF_LIBRE_DISPONIBILIDAD == credit.getEntityProductParameterId()) {
            List<Referral> referrals = personDao.getReferrals(person.getId(), locale);
            model.addAttribute("referrals", referrals);
        }

        model.addAttribute("person", person);
        model.addAttribute("credit", credit);
        model.addAttribute("onlyView", true);
        model.addAttribute("showLoanApplicationTab", false);
        model.addAttribute("showDocumentationTab", false);
        model.addAttribute("showRekognitionTab", false);
        model.addAttribute("showInteractionsTab", true);
        model.addAttribute("showNotes", true);
        model.addAttribute("showPepOfacTab", false);
        return "fragments/personFragmentsCredit :: credit";
    }

    @RequestMapping(value = "/externalView/bankAccount/{token}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "credit:view:externalView", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getCreditBankAccount(
            ModelMap model, Locale locale,
            @PathVariable("token") String token) throws Exception {
        JSONObject jsonToken = new JSONObject(CryptoUtil.decrypt(token));
        int personId = JsonUtil.getIntFromJson(jsonToken, "personId", null);

        Person person = personDao.getPerson(catalogService, locale, personId, true);

        Integer[] creditIds = creditDAO.getActiveCreditIdsByPerson(locale, personId, null);
        if (creditIds != null) {
            for (int i = 0; i < creditIds.length; i++) {
                Credit credit = creditDAO.getCreditByID(creditIds[i], locale, false, Credit.class);
                if (credit.getProduct().isAdvance()) {
                    model.addAttribute("advanceCredit", true);
                    break;
                }
            }
        }
        model.addAttribute("person", person);
        model.addAttribute("bankAccount", personDao.getPersonBankAccountInformation(locale, personId));
        model.addAttribute("employeeBankAccounts", personDao.getEmployeesByDocument(person.getDocumentType().getId(), person.getDocumentNumber(), locale));
        return "fragments/personFragments :: bank_account";
    }

    @RequestMapping(value = "/externalView/bcraResult/{token}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "credit:view:externalView", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getBcraResult(
            ModelMap model, Locale locale,
            @PathVariable("token") String token) throws Exception {
        JSONObject jsonToken = new JSONObject(CryptoUtil.decrypt(token));
        int personId = JsonUtil.getIntFromJson(jsonToken, "personId", null);
        Person person = personDao.getPerson(catalogService, locale, personId, true);

        model.addAttribute("personCountry", person.getCountry().getId());

        switch (person.getCountry().getId()) {
            case CountryParam.COUNTRY_ARGENTINA:
                BcraResult bcraResult = personDao.getBcraResult(personId);
                if (bcraResult != null && bcraResult.getHistorialDeudas() != null && bcraResult.getHistorialDeudas().size() > 0) {
                    List<String> banks = bcraResult.getHistorialDeudas().stream().map(h -> h.getNombre()).collect(Collectors.toList());
                    List<String> periods = bcraResult.getHistorialDeudas().get(0).getHistorial().stream().map(e -> e.getPeriodo()).collect(Collectors.toList());
                    model.addAttribute("banks", banks);
                    model.addAttribute("periods", periods);
                }
                model.addAttribute("bcraResult", bcraResult);

                break;
            case CountryParam.COUNTRY_PERU:
                model.addAttribute("personRccCal", personDao.getPersonRccCalification(personId));
                model.addAttribute("personRcc", personDao.getPersonRcc(personId));
                break;
        }
        
        Person partner = person.getPartner();

        if (partner != null) {
            model.addAttribute("partnerRccCal", personDao.getPersonRccCalification(partner.getId()));
            model.addAttribute("partnerRcc", personDao.getPersonRcc(partner.getId()));
        }

        List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(locale, personId);
        if(ocupations != null){
            PersonOcupationalInformation principalOcupation = ocupations.stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst().orElse(null);
            if (principalOcupation != null) {
                model.addAttribute("rucRccCal", personDao.getRucRccCalification(principalOcupation.getRuc()));
                model.addAttribute("rucRcc", personDao.getRucRcc(principalOcupation.getRuc()));
            }
        }

        return "fragments/personFragments :: tab-rcc";
    }

    @RequestMapping(value = "/externalView/nosisResult/{token}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "credit:view:externalView", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getNosisResult(
            ModelMap model, Locale locale,
            @PathVariable("token") String token) throws Exception {
        JSONObject jsonToken = new JSONObject(CryptoUtil.decrypt(token));

        int personId = JsonUtil.getIntFromJson(jsonToken, "personId", null);
        NosisResult nosisResult = personDao.getBureauResult(personId, NosisResult.class);
        Marshall marshall = new Marshall();
        if (nosisResult != null) {
            if (nosisResult.getParteHTML() != null && nosisResult.getParteHTML().getHtml() != null) {
                model.addAttribute("parteHtml", nosisResult.getParteHTML().getHtml());
                nosisResult.setParteHTML(null);
                System.out.println("datos = " + nosisResult.getParteXML().getDatos().get(0).getNombre());
            }

            model.addAttribute("nosisXml", marshall.toXml(nosisResult));
        }

        return "fragments/person/bureaus :: tab-nosis";
    }

    @RequestMapping(value = "/credit/downPayment", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:credits", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerDownPayment(
            ModelMap model, Locale locale,
            @RequestParam("downPaymentBank") Integer downPaymentBank,
            @RequestParam("downPayment") Double downPayment,
            @RequestParam("downPaymentOps") String downPaymentOps,
            @RequestParam("creditId") Integer creditId) throws Exception {

        String response = creditDAO.registerDownPayment(creditId, downPaymentBank, Currency.USD, downPayment, downPaymentOps);
        JSONObject dbObject = new JSONObject(response);
        Credit credit = creditDAO.getCreditByID(creditId, Configuration.getDefaultLocale(), false, Credit.class);
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);

        if (JsonUtil.getBooleanFromJson(dbObject, "down_payment_paid", false)) {
            // pago total
            loanNotifierService.notifyDisbursement(credit.getLoanApplicationId(), Configuration.getDefaultLocale());
            loanApplicationService.sendVehicleTotalDownPayment(loanApplication.getUserId(), credit.getLoanApplicationId(), locale);
        } else {
            // pago parcial
            loanApplicationService.sendVehiclePartialDownPayment(loanApplication.getUserId(), credit.getLoanApplicationId(), locale);
        }

        return AjaxResponse.ok(response);
    }

    @RequestMapping(value = "/credit/downPaymentDetails", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:credits", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object registerDownPaymentDetails(
            ModelMap model, Locale locale,
            @RequestParam("creditId") Integer creditId) throws Exception {
        Credit credit = creditDAO.getCreditByID(creditId, locale, false, Credit.class);
        credit.setDownPaymentDetail(creditDAO.getDownPayments(creditId));

        model.addAttribute("credit", credit);
        model.addAttribute("downPaymentCurrencySymbol", credit.getDownPaymentCurrency().getSymbol());
        return new ModelAndView("fragments/personFragments :: downPayments");
    }

    @RequestMapping(value = "/credit/uploadWelcomeCall", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object registerWelcomeCall(
            ModelMap model, Locale locale,
            @RequestParam("welcomeCall") MultipartFile file,
            @RequestParam("creditId") Integer creditId) throws Exception {

        if (file.getContentType().toString().equals("audio/mpeg") || file.getContentType().toString().equals("audio/mp3")) {
            Credit credit = creditDAO.getCreditByID(creditId, locale, false, Credit.class);
            LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale, LoanApplication.class);
            String fileName = fileService.writeUserFile(file.getBytes(), loanApplication.getUserId(), credit.getCode().concat(".wav"));
            userDAO.registerUserFile(loanApplication.getUserId(), credit.getLoanApplicationId(), UserFileType.WELCOME_CALL, fileName);
            return AjaxResponse.ok(null);
        } else {
            return AjaxResponse.errorMessage("El formato del welcome call es incorrecto.");
        }
    }

    @RequestMapping(value = "/credit/deleteWelcomeCall", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object deleteWelcomeCall(
            ModelMap model, Locale locale,
            @RequestParam("creditId") Integer creditId) throws Exception {

        Credit credit = creditDAO.getCreditByID(creditId, locale, false, Credit.class);
        Integer personId = credit.getPersonId();

        LoanApplicationUserFiles files = new LoanApplicationUserFiles();
        List<LoanApplicationUserFiles> userFiles = personDao.getUserFiles(personId, locale);
        for (LoanApplicationUserFiles file : userFiles) {
            if (file.getLoanApplicationId().equals(credit.getLoanApplicationId())) {
                files = file;
                break;
            }
        }

        UserFile userFile = files.getUserFileList().stream().filter(e -> e.getFileType().getId() == UserFileType.WELCOME_CALL).findFirst().orElse(null);
        userDAO.updateUserFileType(userFile.getId(), UserFileType.ELIMINADOS);

        return AjaxResponse.ok(null);
    }

    @RequestMapping(value = "/credit/regenerateOnEntity", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object regenerateCredit(
            ModelMap model, Locale locale,
            @RequestParam("creditId") Integer creditId) throws Exception {

        Credit credit = creditDAO.getCreditByID(creditId, locale, false, Credit.class);
        if (credit.getEntity() != null) {
            switch (credit.getEntity().getId()) {
                case Entity.COMPARTAMOS:
                    LoanApplication loanApplication = loanApplicationDao.getLoanApplication(credit.getLoanApplicationId(), locale);
                    Person person = personDao.getPerson(catalogService, locale, credit.getPersonId(), true);
                    GenerarCreditoResponse result = null;

                    User user = userDAO.getUser(userDAO.getUserIdByPersonId(person.getId()));
                    List<PersonOcupationalInformation> personOcupationalInformations = personDao.getPersonOcupationalInformation(locale, person.getId());
                    PersonOcupationalInformation personOcupationalInformation = null;

                    PersonContactInformation personContactInformation = personDao.getPersonContactInformation(locale, person.getId());
                    List<DisggregatedAddress> disggregatedAddresses = personDao.getDisggregatedAddress(person.getId());
                    PersonBankAccountInformation personBankAccountInformation = personDao.getPersonBankAccountInformation(locale, person.getId());

                    if (personOcupationalInformations != null) {
                        personOcupationalInformation = personOcupationalInformations.stream().filter(p -> p.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst().orElse(null);
                    }

                    SunatResult sunatResult = personDao.getSunatResult(person.getId());
                    SunatResult sunatResultRuc = null;
                    if (personOcupationalInformation != null && personOcupationalInformation.getRuc() != null) {
                        sunatResultRuc = personDao.getSunatResultByRuc(personOcupationalInformation.getRuc());
                    }

                    ExperianResult experianResult = loanApplicationDao.getExperianResultList(loanApplication.getId()).get(0);
                    RucInfo rucInfo = personDao.getRucInfo(personOcupationalInformation.getRuc());
                    LoanOffer loanOffer = loanApplicationDao.getLoanOffers(loanApplication.getId()).stream().filter(e -> e.getSelected()).findFirst().orElse(null);

                    GenerarCreditoRequest generarCreditoRequest = new GenerarCreditoRequest("02", //Código 02 - Actualización de un crédito
                            person,
                            user,
                            personOcupationalInformation,
                            disggregatedAddresses,
                            loanApplication.getRegisterDate(),
                            loanApplication, loanOffer,
                            personBankAccountInformation,
                            personContactInformation,
                            sunatResult,
                            experianResult,
                            rucInfo,
                            translatorDAO,
                            credit.getEntityCreditCode(),
                            sunatResultRuc,
                            credit);

                    result = compartamosServiceCall.callGeneracionCredito(generarCreditoRequest, loanApplication.getId(), credit.getId());
                    Gson gson = new Gson();
                    creditDAO.registerSchedule(credit.getId(), gson.toJson(result));
                    creditDAO.updateReturningReasons(creditId, null);
                    loanApplicationDao.updateEntityApplicationCode(loanApplication.getId(), result.getCredito().getCuenta());
                    return AjaxResponse.ok(null);
                default:
                    return AjaxResponse.errorMessage("La entidad no permite regenerar créditos");
            }

        }

        return AjaxResponse.errorMessage("El crédito no está asociado a ninguna entidad");
    }

    @RequestMapping(value = "/credit/getTagManagerKeyByCreditId", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getCountryId(
            ModelMap model, Locale locale,
            @RequestParam(value = "creditsId") Integer[] creditsId) throws Exception {
        String tagManagerKey = creditService.getGoogleTagManagerKey(creditsId[0]);
        JSONObject json = new JSONObject();
        json.put("tagManagerKey", tagManagerKey);
        return AjaxResponse.ok(json.toString());
    }
}
