package com.affirm.entityExt.controller;

import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.GatewayLoanApplicationExtranetPainter;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.entityExt.models.PaginatorTableFilterForm;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller("entityExtranetPaymentCommitmentController")
public class EntityExtranetPaymentCommitmentController {

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CreditDAO creditDAO;
    @Autowired
    private EntityExtranetService entityExtranetService;

    public static final String URL = "paymentCommitment";

    @RequestMapping(value = "/"+URL, method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:paymentCommitment:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showPaymentCommitment(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        List<GatewayLoanApplicationExtranetPainter> data = creditDAO.getCollectionLoanApplication(loggedUserEntity.getPrincipalEntity().getId(), null, null, null, null, null,0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, null, false, locale);

        List<EntityProductParams> entityProductParams = catalogService.getEntityProductParams().stream().filter(e -> e.getEntity() != null && e.getEntity().getId().intValue() == loggedUserEntity.getPrincipalEntity().getId().intValue() && e.getProduct().getId().intValue() == Product.GATEWAY_PAGO).collect(Collectors.toList());

        Pair<Integer, Double> countAdSum = creditDAO.getCollectionLoanApplicationCount(loggedUserEntity.getPrincipalEntity().getId(), null, null, null,null, null, null, locale);

        Integer limitPaginator = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        model.addAttribute("limitPaginator", limitPaginator);
        model.addAttribute("totalCount", countAdSum.getLeft());
        model.addAttribute("data", data);
        model.addAttribute("page", "paymentCommitment");
        model.addAttribute("title", "Compromisos de Pagos");
        model.addAttribute("tray", ExtranetMenu.PAYMENT_COMMITMENT_MENU);

        model.addAttribute("products", entityProductParams);
        model.addAttribute("productSelected", null);

        return new ModelAndView("/entityExtranet/extranetPaymentCommitment");
    }

    @RequestMapping(value = "/"+URL, method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:paymentCommitment:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object showPaymentCommitmentList(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        Integer offset = 0;
        Integer limit = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        Integer[] status = null;

        List<Integer> loanStatus = new ArrayList<>();
        List<Integer> creditStatus = new ArrayList<>();
        List<Integer> entityProducParams = null;

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
                entityProducParams = new Gson().fromJson(filter.getEntityProductParam(), new TypeToken<ArrayList<Integer>>(){}.getType());
            }
            else{
                entityProducParams = catalogService.getEntityProductParams().stream().filter(e -> e.getEntity() != null && e.getEntity().getId() == loggedUserEntity.getPrincipalEntity().getId() && e.getProduct().getId() == Product.GATEWAY_PAGO).map(EntityProductParams::getId).collect(Collectors.toList());
            }
            if(filter.getStatus() != null) {
                status = new Gson().fromJson(filter.getStatus(), Integer[].class);
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
            }
        }
        if(offset < 0) offset = 0;

        List<GatewayLoanApplicationExtranetPainter> data = creditDAO.getCollectionLoanApplication(
                loggedUserEntity.getPrincipalEntity().getId(),
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                loanStatus != null && !loanStatus.isEmpty() ? loanStatus.stream().toArray(Integer[] ::new) : null,
                creditStatus != null && !creditStatus.isEmpty() ? creditStatus.stream().toArray(Integer[] ::new) : null,
                entityProducParams != null && !entityProducParams.isEmpty() ? entityProducParams.stream().toArray(Integer[] ::new) : null,
                offset,
                limit,
                searchValue,
                false,
                locale);

        model.addAttribute("offset", offset);
        model.addAttribute("data", data);
        model.addAttribute("page", "paymentCommitment");
        model.addAttribute("title", "Compromisos de Pagos");

        return new ModelAndView("/entityExtranet/extranetPaymentCommitment :: list");
    }

    @RequestMapping(value = "/"+URL+"/count", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:paymentCommitment:view", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    public Object showPaymentCommitmentCount(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        Integer[] status = null;
        List<Integer> loanStatus = new ArrayList<>();
        List<Integer> creditStatus = new ArrayList<>();
        List<Integer> entityProducParams = null;

        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
            if(filter.getEntityProductParam() != null) {
                entityProducParams = new Gson().fromJson(filter.getEntityProductParam(), new TypeToken<ArrayList<Integer>>(){}.getType());
            }
            else{
                entityProducParams = catalogService.getEntityProductParams().stream().filter(e -> e.getEntity() != null && e.getEntity().getId() == loggedUserEntity.getPrincipalEntity().getId() && e.getProduct().getId() == Product.GATEWAY_PAGO).map(EntityProductParams::getId).collect(Collectors.toList());
            }
            if(filter.getStatus() != null) {
                status = new Gson().fromJson(filter.getStatus(), Integer[].class);
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
            }
        }

        Pair<Integer, Double> countAdSum = creditDAO.getCollectionLoanApplicationCount(
                loggedUserEntity.getPrincipalEntity().getId(),
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                loanStatus != null && !loanStatus.isEmpty() ? loanStatus.stream().toArray(Integer[] ::new) : null,
                creditStatus != null && !creditStatus.isEmpty() ? creditStatus.stream().toArray(Integer[] ::new) : null,
                searchValue,
                entityProducParams != null && !entityProducParams.isEmpty() ? entityProducParams.stream().toArray(Integer[] ::new) : null,
                locale);


        Map<String, Object> data = new HashMap<String, Object>();
        data.put("count",countAdSum.getLeft());

        return AjaxResponse.ok(new Gson().toJson(data));
    }


}
