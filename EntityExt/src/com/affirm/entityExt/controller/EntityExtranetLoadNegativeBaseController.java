package com.affirm.entityExt.controller;

import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.NegativeBaseProcessDAO;
import com.affirm.common.dao.PreApprovedDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.util.AjaxResponse;
import com.affirm.negativebase.model.NegativeBaseProcessed;
import com.affirm.negativebase.service.NegativeBaseService;
import com.affirm.preapprovedbase.model.PreApprovedBaseProcessed;
import com.affirm.preapprovedbase.service.PreApprovedBaseService;
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
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Controller("entityExtranetLoadNegativeBaseController")
public class EntityExtranetLoadNegativeBaseController {

    public static final String URL = "loadNegativeBase";

    private LoanApplicationDAO loanApplicationDAO;
    private EntityExtranetService entityExtranetService;
    private NegativeBaseService negativeBaseService;
    private NegativeBaseProcessDAO negativeBaseProcessDAO;

    @Autowired
    public EntityExtranetLoadNegativeBaseController(LoanApplicationDAO loanApplicationDAO, EntityExtranetService entityExtranetService, NegativeBaseService negativeBaseService, NegativeBaseProcessDAO negativeBaseProcessDAO) {
        this.loanApplicationDAO = loanApplicationDAO;
        this.entityExtranetService = entityExtranetService;
        this.negativeBaseService = negativeBaseService;
        this.negativeBaseProcessDAO = negativeBaseProcessDAO;
    }

    @RequestMapping(value = "/" + URL, method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:loadNegativeBase:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showNegativeBaseView(ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        model.addAttribute("title", "Carga de Base Negativa");
        model.addAttribute("page", "loadNegativeBase");
        List<NegativeBaseProcessed> negativeBaseProcesseds = negativeBaseProcessDAO.getHistoricListNegativeBase(loggedUserEntity.getPrincipalEntity().getId(),10,0);
        model.addAttribute("historicData", negativeBaseProcesseds);
        switch (loggedUserEntity.getPrincipalEntity().getId()){
            case Entity.PRISMA:
                model.addAttribute("templateUrl", "https://solven-public.s3.amazonaws.com/img/prisma/prisma_negative.csv");
                break;
        }
        return new ModelAndView("/entityExtranet/extranetLoadNegativeBase");
    }

    @RequestMapping(value = "/" + URL+ "/upload", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "execute:loadNegativeBase:upload", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public ResponseEntity<String> uploadBasePreApproved(
            HttpServletRequest request, HttpServletResponse response, @RequestParam("file") MultipartFile file,
            @RequestParam("action") Character action
    ) throws Exception {
        try {
            LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
            if(action != null){
                if(!action.equals(NegativeBaseProcessed.PROCESS_TYPE_ADD) && !action.equals(NegativeBaseProcessed.PROCESS_TYPE_OVERWRITE)) action = NegativeBaseProcessed.PROCESS_TYPE_ADD;
            }
            else action = NegativeBaseProcessed.PROCESS_TYPE_ADD;
            this.negativeBaseService.uploadFileToBucket(file, loggedUserEntity.getPrincipalEntity().getId(),loggedUserEntity.getId(), action);
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
    @RequiresPermissionOr403(permissions = "menu:loadNegativeBase:view", type = RequiresPermissionOr403.Type.AJAX)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object showPreApprovedReportList(ModelMap model) throws Exception {
        int userId = entityExtranetService.getLoggedUserEntity().getId();
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        List<NegativeBaseProcessed> negativeBaseProcesseds = negativeBaseProcessDAO.getHistoricListNegativeBase(loggedUserEntity.getPrincipalEntity().getId(),10,0);
        model.addAttribute("historicData", negativeBaseProcesseds);
        return new ModelAndView("/entityExtranet/extranetLoadNegativeBase :: historicDataResults");
    }

}
