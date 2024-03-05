package com.affirm.entityExt.controller;

import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PreApprovedDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.WebApplication;
import com.affirm.common.model.transactional.LeadLoanApplication;
import com.affirm.common.model.transactional.ReportProces;
import com.affirm.common.service.document.DocumentService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.preapprovedbase.model.PreApprovedBaseProcessed;
import com.affirm.preapprovedbase.service.PreApprovedBaseService;
import com.affirm.system.configuration.Configuration;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Controller("entityExtranetLoadPreApprovedBaseController")
public class EntityExtranetLoadPreApprovedBaseController {

    public static final String URL = "loadPreApprovedBase";

    private LoanApplicationDAO loanApplicationDAO;
    private EntityExtranetService entityExtranetService;
    private PreApprovedBaseService preApprovedBaseService;
    private PreApprovedDAO preApprovedDAO;

    @Autowired
    public EntityExtranetLoadPreApprovedBaseController(LoanApplicationDAO loanApplicationDAO, EntityExtranetService entityExtranetService,PreApprovedBaseService preApprovedBaseService,PreApprovedDAO preApprovedDAO) {
        this.loanApplicationDAO = loanApplicationDAO;
        this.entityExtranetService = entityExtranetService;
        this.preApprovedBaseService = preApprovedBaseService;
        this.preApprovedDAO = preApprovedDAO;
    }

    @RequestMapping(value = "/" + URL, method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loadPreApprovedBase:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showLeads(ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        model.addAttribute("title", "Carga de Base Preaprobada");
        model.addAttribute("page", "loadPreApprovedBase");
        List<PreApprovedBaseProcessed> preApprovedBaseProcesseds = preApprovedDAO.getHistoricListPreAppovedBase(loggedUserEntity.getPrincipalEntity().getId(),10,0);
        model.addAttribute("historicData", preApprovedBaseProcesseds);
        switch (loggedUserEntity.getPrincipalEntity().getId()){
            case Entity.PRISMA:
                model.addAttribute("templateUrl", "https://solven-public.s3.amazonaws.com/img/prisma/plantilla.csv");
                break;
            case Entity.AZTECA:
                model.addAttribute("templateUrl", "https://solven-public.s3.amazonaws.com/img/alfin/plantilla.csv");
                break;
        }
        return new ModelAndView("/entityExtranet/extranetLoadPreApprovedBase");
    }

    @RequestMapping(value = "/" + URL+ "/upload", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "execute:loadPreApprovedBase:upload", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> uploadBasePreApproved(
            HttpServletRequest request, HttpServletResponse response, @RequestParam("file") MultipartFile file
    ) throws Exception {
        try {
            LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
            this.preApprovedBaseService.uploadFileToBucket(file, loggedUserEntity.getPrincipalEntity().getId(),loggedUserEntity.getId());
            return AjaxResponse.ok(null);
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResponse.errorMessage(e.getMessage() != null ? e.getMessage() : "Error al procesar el archivo");
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResponse.errorMessage("Ocurrió un error");
        }
    }

    @RequestMapping(value = "/" + URL + "/upload/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:loadPreApprovedBase:view", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object showPreApprovedReportList(ModelMap model) throws Exception {
        int userId = entityExtranetService.getLoggedUserEntity().getId();
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        List<PreApprovedBaseProcessed> preApprovedBaseProcesseds = preApprovedDAO.getHistoricListPreAppovedBase(loggedUserEntity.getPrincipalEntity().getId(),10,0);
        model.addAttribute("historicData", preApprovedBaseProcesseds);
        return new ModelAndView("/entityExtranet/extranetLoadPreApprovedBase :: historicDataResults");
    }

}
