package com.affirm.entityExt.controller;

import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.transactional.LeadLoanApplication;
import com.affirm.common.service.document.DocumentService;
import com.affirm.system.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller("entityExtranetLeadsController")
public class EntityExtranetLeadsController {

    public static final String URL = "leads";
    private static final String LEAD_EXCEL_FILE_NAME = "EfectivoAlToque.xlsx";

    private LoanApplicationDAO loanApplicationDAO;

    private DocumentService documentService;
    private EntityExtranetService entityExtranetService;

    @Autowired
    public EntityExtranetLeadsController(LoanApplicationDAO loanApplicationDAO, DocumentService documentService, EntityExtranetService entityExtranetService) {
        this.loanApplicationDAO = loanApplicationDAO;
        this.documentService = documentService;
        this.entityExtranetService = entityExtranetService;
    }

    @RequestMapping(value = "/" + URL, method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:lead:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showLeads(ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        List<LeadLoanApplication> currentMonthLeads = loanApplicationDAO.getLeadLoanApplicationsByEntityAndDate(loggedUserEntity.getPrincipalEntity().getId(), new Date());

        model.addAttribute("leadsTitle", "Leads Mes " + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Configuration.getDefaultLocale()));
        model.addAttribute("currentMonthLead", currentMonthLeads != null ? currentMonthLeads.get(0) : null);
        model.addAttribute("previousLeads", loanApplicationDAO.getApprovedLeadLoanApplications(loggedUserEntity.getPrincipalEntity().getId()));

        return new ModelAndView("/entityExtranet/extranetLeads");
    }

    @RequestMapping(value = "/" + URL + "/download", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "lead:report:download", type = RequiresPermissionOr403.Type.WEB)
    @ResponseBody
    public void downloadTemplate(HttpServletResponse response, @RequestParam(value = "month", required = false) Integer month, @RequestParam(value = "year", required = false) Integer year) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        MediaType contentType = MediaType.valueOf("application/vnd.ms-excel");
        Integer monthToFilter = month;
        Integer yearToFilter = year;

        if (monthToFilter == null && yearToFilter == null) {
            Calendar cal = Calendar.getInstance();
            monthToFilter = cal.get(Calendar.MONTH) + 1;
            yearToFilter = cal.get(Calendar.YEAR);
        }

        byte[] excel = documentService.generateLeadReportSpreadSheet(loggedUserEntity.getPrincipalEntity().getId(), monthToFilter, yearToFilter);

        response.setHeader("Content-disposition", "attachment; filename=" + LEAD_EXCEL_FILE_NAME);
        response.setContentType(contentType.getType());
        response.getOutputStream().write(excel);
    }
}
