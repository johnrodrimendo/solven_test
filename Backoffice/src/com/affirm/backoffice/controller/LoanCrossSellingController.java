package com.affirm.backoffice.controller;

import com.affirm.backoffice.dao.LoanApplicationBODAO;
import com.affirm.backoffice.service.BackofficeService;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.system.configuration.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Controller
@RequestMapping("/loanApplication/crossselling")
public class LoanCrossSellingController {

    private final BackofficeService backofficeService;

    private final LoanApplicationBODAO loanApplicationBODAO;

    public LoanCrossSellingController(BackofficeService backofficeService, LoanApplicationBODAO loanApplicationBODAO) {
        this.backofficeService = backofficeService;
        this.loanApplicationBODAO = loanApplicationBODAO;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:crossselling", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public String showLoanApplicationsCrossSelling(ModelMap model, Locale locale) throws Exception {

        model.addAttribute("wrapper", loanApplicationBODAO.getLoanApplicationsListCrossSelling(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                backofficeService.getCountryActiveSysuser(),
                null,
                locale,
                0,
                Configuration.BACKOFFICE_PAGINATION_LIMIT));

        return "loanApplicationsCrossSelling";
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:loanapplication:crossselling", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public String showLoanApplicationsCrossSellingList(
            ModelMap model, Locale locale,
            @RequestParam(value = "amountFrom[]", required = false) String amountFromFilter,
            @RequestParam(value = "amountTo[]", required = false) String amountToFilter,
            @RequestParam(value = "creationFrom[]", required = false) String creationFromFilter,
            @RequestParam(value = "creationTo[]", required = false) String creationToFilter,
            @RequestParam(value = "document_number[]", required = false) String documentNumberFilter,
            @RequestParam(value = "reason[]", required = false) Integer[] reason,
            @RequestParam(value = "entity[]", required = false) Integer[] entity,
            @RequestParam(value = "employer[]", required = false) Integer[] employer,
            @RequestParam(value = "analyst[]", required = false) String analyst,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "limit", required = false, defaultValue = "200") int limit) throws Exception {

        Date creationFrom = creationFromFilter != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFromFilter) : null;
        Date creationTo = creationToFilter != null ? new SimpleDateFormat("dd/MM/yyyy").parse(creationToFilter) : null;
        Integer entities = entity != null ? (entity.length > 0 ? entity[0] : null) : null;
        Integer reasons = reason != null ? (reason.length > 0 ? reason[0] : null) : null;

        model.addAttribute("wrapper", loanApplicationBODAO.getLoanApplicationsListCrossSelling(
                creationFrom,
                creationTo,
                amountFromFilter,
                amountToFilter,
                documentNumberFilter,
                entities,
                null,
                reasons,
                backofficeService.getCountryActiveSysuser(),
                analyst,
                locale,
                offset,
                limit));

        return "loanApplicationsCrossSelling :: list";
    }
}