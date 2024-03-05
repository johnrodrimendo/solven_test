package com.affirm.entityExt.controller;

import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.common.dao.*;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.AjaxResponse;
import com.affirm.entityExt.models.TrayReportForm;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller("entityExtranetTrayReportsController")
public class EntityExtranetTrayReportsController {

    public static final String URL = "extranet_reports";

    @Autowired
    private EntityExtranetService entityExtranetService;
    @Autowired
    private ReportsDAO reportDAO;
    @Autowired
    private ReportsService reportsService;
    @Autowired
    private BrandingService brandingService;
    @Autowired
    private CreditDAO creditDAO;
    @Autowired
    private CatalogService catalogService;

    @RequestMapping(value = "/" + URL, method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "report:tray:detail", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getDetail(
            ModelMap model, Locale locale, HttpServletRequest request, @RequestParam(value = "reportId") Integer reportId) throws Exception {
        int userId = entityExtranetService.getLoggedUserEntity().getId();
        ReportProces report = reportDAO.getReportProces(reportId);
        if(report.getUserId() != null && report.getUserId() != userId) return AjaxResponse.errorMessage("No tiene acceso a este reporte");
        if(report.getUrl() != null) report.setUrl(reportsService.createReportDownloadUrl(report.getId()));
        return AjaxResponse.ok(new Gson().toJson(report));
    }

    @RequestMapping(value = "/" + URL, method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "report:tray:execute", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object generateReport(
            ModelMap model, Locale locale, HttpServletRequest request, TrayReportForm form) throws Exception {
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date from = form.getCreationFrom() != null && !form.getCreationFrom().isEmpty() ? sdf.parse(form.getCreationFrom()) : null;
        Date to = form.getCreationTo() != null && !form.getCreationTo().isEmpty() ? sdf.parse(form.getCreationTo()) : null;
        Integer[] steps = {};
        Integer[] product = null;
        Integer[] status = null;
        Integer[] entityProductsParam = null;
        List<Integer> loanStatus = new ArrayList<>();
        List<Integer> creditStatus = new ArrayList<>();
        List<String> rejectedReason = null;

        if(form != null){
            if(form.getEntityProductParam() != null) {
                entityProductsParam = new Gson().fromJson(form.getEntityProductParam(), Integer[].class);
            }
            if(form.getDisbursementType() != null) {
                Integer[] disbursementTypes = new Gson().fromJson(form.getDisbursementType(), Integer[].class);
                if(disbursementTypes.length > 0){
                    List<Integer> entityProductParamsId = catalogService.getEntityProductParams().stream().filter(e -> e.getEntity().getId().equals(loggedUserEntity.getPrincipalEntity().getId()) && e.getExtranetCreditGeneration() != null && e.getExtranetCreditGeneration() && Arrays.stream(disbursementTypes).anyMatch(d->d.equals(e.getDisbursementType()))).collect(Collectors.toList()).stream().map(e->e.getId()).collect(Collectors.toList());
                    entityProductsParam = new Integer[entityProductParamsId.size()];
                    entityProductsParam = entityProductParamsId.toArray(entityProductsParam);
                }
            }
            if(form.getProduct() != null) {
                product = new Gson().fromJson(form.getProduct(), Integer[].class);
            }
            if(form.getRejectedReason() != null) {
                rejectedReason = new Gson().fromJson(form.getRejectedReason(), new TypeToken<ArrayList<String>>(){}.getType());
            }

        }

        Integer productCategoryId = null;

        if(form != null && form.getStatus() != null) {
            status = new Gson().fromJson(form.getStatus(), Integer[].class);
            if(form.getTray() != null){
                switch (form.getTray()){
                    case ExtranetMenu.PAYMENT_COMMITMENT_MENU:
                        for (Integer statusValue : status) {
                            switch (statusValue){
                                case 1:
                                    loanStatus.add(LoanApplicationStatus.PRE_EVAL_APPROVED);
                                    loanStatus.add(LoanApplicationStatus.EVAL_APPROVED);
                                    break;
                                case 2:
                                    creditStatus.add(CreditStatus.ACCEPTED_OFFER);
                                    break;
                                case 3:
                                    creditStatus.add(CreditStatus.PENDING_PAYMENT);
                                    break;
                                case 4:
                                    creditStatus.add(CreditStatus.PAYED);
                                    break;
                                case 5:
                                    creditStatus.add(CreditStatus.PENDING_CONFIRMATION_BT);
                                    break;
                                case 6:
                                    creditStatus.add(CreditStatus.PAYED_INFORMED);
                                    break;
                            }
                        }
                        break;
                }
            }
        }

        EntityExtranetConfiguration.FunnelConfiguration funnelConfiguration = null;
        EntityExtranetConfiguration extranetConfiguration = brandingService.getExtranetBrandingAsJson(loggedUserEntity.getPrincipalEntity().getId());
        if(extranetConfiguration != null && extranetConfiguration.getFunnelConfiguration() != null && !extranetConfiguration.getFunnelConfiguration().isEmpty() && loggedUserEntity != null && loggedUserEntity.getProductCategoryId() != null) funnelConfiguration = extranetConfiguration.getFunnelConfiguration().stream().filter(e -> e.getProductCategoryId() == loggedUserEntity.getProductCategoryId()).findFirst().orElse(null);
        if(funnelConfiguration != null && funnelConfiguration.getSteps() != null && !funnelConfiguration.getSteps().isEmpty()){
            steps =  funnelConfiguration.getSteps().stream().map(EntityExtranetConfiguration.FunnelStep::getStepId).collect(Collectors.toList()).toArray(new Integer[funnelConfiguration.getSteps().size()]);
        }
        List<Integer> loanIds = new ArrayList<>();

        EntityExtranetUser.MenuEntityProductCategory menuEntityProductCategory = entityExtranetService.getMenuEntityProductCategoryByTray(loggedUserEntity.getMenuEntityProductCategories(),form.getTray());
        List<Product> products = entityExtranetService.getProductsByCategory(menuEntityProductCategory == null ? null : menuEntityProductCategory.getProductCategories(), loggedUserEntity.getPrincipalEntity().getId());

        if(form.getTrayReport() != null){
            switch (form.getTray()){
                case ExtranetMenu.TO_DISBURSE_CREDITS_MENU:
                    List<CreditEntityExtranetPainter> creditsG = creditDAO.getEntityCredits(loggedUserEntity.getPrincipalEntity().getId(), form.getTray(), from, to, loggedUserEntity.getId(), locale, null, null, form.getSearch(), product,true,null);
                    loanIds = creditsG != null && !creditsG.isEmpty() ? creditsG.stream().map(CreditEntityExtranetPainter::getLoanApplicationId).collect(Collectors.toList()) : new ArrayList<>();
                    break;
                case ExtranetMenu.DISBURSEMENT_CREDITS_MENU:
                case ExtranetMenu.CALL_CENTER_MENU:
                case ExtranetMenu.TO_VERIFY_CREDITS_MENU:
                case ExtranetMenu.TO_UPLOAD_CREDITS_MENU:
                    List<CreditEntityExtranetPainter> credits = creditDAO.getEntityCredits(loggedUserEntity.getPrincipalEntity().getId(), form.getTray(), from, to, loggedUserEntity.getId(), locale, null, null, form.getSearch(), entityProductsParam,true,products.stream().map(Product::getId).collect(Collectors.toList()));
                    loanIds = credits != null && !credits.isEmpty() ? credits.stream().map(CreditEntityExtranetPainter::getLoanApplicationId).collect(Collectors.toList()) : new ArrayList<>();
                    break;
                case ExtranetMenu.BEING_PROCESSED_MENU:
                    if((form.getMaxProgress() != null && form.getMinProgress() == null) || (form.getMinProgress() != null && form.getMaxProgress() == null)){
                        return AjaxResponse.errorMessage("Debe indicar el valor máximo y mínimo");
                    }
                    List<CreditBancoDelSolExtranetPainter> creditsInProcess = creditDAO.getEntityCreditsByLoggedUserId(loggedUserEntity.getId(), from, to, null,null, null, locale, true,product != null ? Arrays.asList(product) : products.stream().map(Product::getId).collect(Collectors.toList()), form.getMinProgress(),form.getMaxProgress());
                    if(creditsInProcess != null) loanIds = creditsInProcess != null && !creditsInProcess.isEmpty() ?  creditsInProcess.stream().map(CreditBancoDelSolExtranetPainter::getLoanApplicationId).collect(Collectors.toList())  : new ArrayList<>();
                    break;
                case ExtranetMenu.REJECTED_MENU:
                    List<CreditBancoDelSolExtranetPainter> rejected = creditDAO.getEntityRejectedLoanApplications(loggedUserEntity.getPrincipalEntity().getId(), from, to, null,null, null, locale, true,product != null ? Arrays.asList(product) : products.stream().map(Product::getId).collect(Collectors.toList()), rejectedReason);
                    if(rejected != null) loanIds = rejected != null && !rejected.isEmpty() ?  rejected.stream().map(CreditBancoDelSolExtranetPainter::getLoanApplicationId).collect(Collectors.toList())  : new ArrayList<>();
                    break;
                case ExtranetMenu.PAYMENT_COMMITMENT_MENU:
                    List<GatewayLoanApplicationExtranetPainter> paymentCommitments = creditDAO.getCollectionLoanApplication(
                            loggedUserEntity.getPrincipalEntity().getId(), from, to,
                            loanStatus != null && !loanStatus.isEmpty() ? loanStatus.stream().toArray(Integer[] ::new) : null,
                            creditStatus != null && !creditStatus.isEmpty() ? creditStatus.stream().toArray(Integer[] ::new) : null,
                            entityProductsParam,
                            null,null, null, true,locale);
                    if(paymentCommitments != null) loanIds = paymentCommitments != null && !paymentCommitments.isEmpty() ?  paymentCommitments.stream().map(GatewayLoanApplicationExtranetPainter::getLoanApplicationId).collect(Collectors.toList())  : new ArrayList<>();
                    break;
            }
            ReportProces reportProces = reportsService.createReport(loanIds.toArray(new Integer[loanIds.size()]),loggedUserEntity.getPrincipalEntity().getId(), loggedUserEntity.getId(),form.getTrayReport(),form.getTray(),from,to,form.getSearch(),loggedUserEntity.getProducts().stream().map(Product::getId).collect(Collectors.toList()).toArray(new Integer[loggedUserEntity.getProducts().size()]),steps, form.getTray() != null && form.getTray().equals(ExtranetMenu.PAYMENT_COMMITMENT_MENU) ? true : false, form.getTray() != null && form.getTray().equals(ExtranetMenu.REJECTED_MENU) ? true : false);
            return AjaxResponse.ok(new Gson().toJson(reportProces));
        }
        return AjaxResponse.ok(new Gson().toJson(null));
    }


}
