package com.affirm.entityExt.controller.banbif;

import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.client.service.impl.EntityExtranetServiceImpl;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.transactional.Credit;
import com.affirm.common.model.transactional.CreditEntityExtranetPainter;
import com.affirm.common.service.*;
import com.affirm.common.model.transactional.BanbifTcLeadLoan;
import com.affirm.common.util.CSVWriter;
import com.affirm.common.util.JsonFlattener;
import com.affirm.entityExt.models.PaginatorTableFilterForm;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller("entityExtranetLeadsBanbifController")
public class EntityExtranetLeadsBanbifController {

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CreditDAO creditDAO;
    @Autowired
    private EntityExtranetService entityExtranetService;
    @Autowired
    private ReportsService reportsService;

    @RequestMapping(value = "/leadsDelivered", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:leads:leadsDelivered:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showLeadsDelivered(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        int filterType = 2;
        List<BanbifTcLeadLoan> data = creditDAO.getBanbifLeadCreditCardLoan(null, null, null, filterType, locale, 0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR,null);
        Integer totalCount = creditDAO.getBanbifLeadCreditCardLoanCount(null, null, null, null, filterType, locale);
        Integer limitPaginator = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        model.addAttribute("limitPaginator", limitPaginator);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("data", data);
        model.addAttribute("filterType", filterType);
        model.addAttribute("page", "leadsDelivered");
        model.addAttribute("title", "Leads entregados");
        return new ModelAndView("/entityExtranet/extranetLeadsBanbif");
    }

    @RequestMapping(value = "/leadsToDeliver", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:leads:leadsToDeliver:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showLeadsToDeliver(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

//        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        int filterType = 1;
        List<BanbifTcLeadLoan> data = creditDAO.getBanbifLeadCreditCardLoan(null, null, null, filterType, locale, 0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR,null);
        Integer totalCount = creditDAO.getBanbifLeadCreditCardLoanCount(null, null, null, null, filterType, locale);
        Integer limitPaginator = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        model.addAttribute("limitPaginator", limitPaginator);
        model.addAttribute("data", data);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("filterType", filterType);
        model.addAttribute("page", "leadsToDeliver");
        model.addAttribute("title", "Leads");
        return new ModelAndView("/entityExtranet/extranetLeadsBanbif");
    }

    @RequestMapping(value = "/leadsToDeliver/download/{filterType}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "report:create", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public void extranetExportCreditExcel(@PathVariable Integer filterType, HttpServletResponse response, Locale locale) throws Exception {

        List<BanbifTcLeadLoan> data = creditDAO.getBanbifLeadCreditCardLoan(null, null, null, filterType, locale, null, null, null);
        String name = filterType == 1 ? "Leads_por_entregar" : "Leads_entregados";

        byte[] file = reportsService.createBanBifLeadsReport(data, name);
        if (file != null) {
            MediaType contentType = MediaType.valueOf("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment; filename=" + name + ".xls");
            response.setContentType(contentType.getType());
            response.getOutputStream().write(file);
        }
    }


}
