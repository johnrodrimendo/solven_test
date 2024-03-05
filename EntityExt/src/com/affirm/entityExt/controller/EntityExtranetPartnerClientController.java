package com.affirm.entityExt.controller;

import com.affirm.client.model.ExtranetPartnerClient;
import com.affirm.client.service.EntityExtranetPartnerClientService;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.WebApplication;
import com.affirm.common.service.FileService;
import com.affirm.common.util.AjaxResponse;
import org.apache.log4j.Logger;
import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Controller("entityExtranetPartnerClientController")
public class EntityExtranetPartnerClientController {

    private static Logger logger = Logger.getLogger(EntityExtranetPartnerClientController.class);

    public static final String URL = "currentPartnerClient";

    private static final String EXCEL_TEMPLATE = "Plantilla_Socios_Clientes_Actuales.xlsx";

    private FileService fileService;
    private EntityExtranetService entityExtranetService;
    private EntityExtranetPartnerClientService extranetPartnerClientService;

    @Autowired
    public EntityExtranetPartnerClientController(
            FileService fileService,
            EntityExtranetService entityExtranetService,
            EntityExtranetPartnerClientService extranetPartnerClientService) {
        this.fileService = fileService;
        this.entityExtranetService = entityExtranetService;
        this.extranetPartnerClientService = extranetPartnerClientService;
    }

    @RequestMapping(value = "/" + URL, method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:associated:load:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showCurrentPartnerClient(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        ExtranetPartnerClient.ListType[] listType = ExtranetPartnerClient.ListType.values();
        Integer loggedUserEntityId = entityExtranetService.getLoggedUserEntity().getEntities().get(0).getId();
        List<List<ExtranetPartnerClient>> partnerClients = this.extranetPartnerClientService.getPartnerClients(loggedUserEntityId);

        model.addAttribute("listType", listType);
        model.addAttribute("partnerClientResults", partnerClients.get(0));
        model.addAttribute("blacklistResults", partnerClients.get(1));
        return new ModelAndView("/entityExtranet/currentPartnerClient");

    }

    @RequestMapping(value = "/" + URL + "/upload", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "associated:load:save", type = RequiresPermissionOr403.Type.AJAX, saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object uploadPartnerClient(
            ModelMap model, Locale locale, HttpServletRequest request,
            @RequestParam("actionType") String listType,
            @RequestParam("file") MultipartFile file) throws Exception {

        Integer loggedUserEntityId = entityExtranetService.getLoggedUserEntity().getEntities().get(0).getId();

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            JSONArray jsonArray = extranetPartnerClientService.parsePartnerClientFile(workbook);
            extranetPartnerClientService.saveRecords(loggedUserEntityId, jsonArray, listType);
            return AjaxResponse.ok(jsonArray.toString());
        } catch (EmptyFileException e) {
            e.printStackTrace();
            return AjaxResponse.errorMessage("El archivo no tiene datos o está vacio");
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            return AjaxResponse.errorMessage("No se envió ningún archivo");
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return AjaxResponse.errorMessage("Error al procesar el archivo");
        }

    }

    @RequestMapping(value = "/" + URL + "/excel/template", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "associated:load:save", type = RequiresPermissionOr403.Type.WEB)
    @ResponseBody
    public void downloadTemplate(HttpServletResponse response) throws Exception {

        byte[] template = fileService.getAssociatedFile(EXCEL_TEMPLATE);
        if (template != null) {
            MediaType contentType = MediaType.valueOf("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment; filename=" + EXCEL_TEMPLATE);
            response.setContentType(contentType.getType());
            response.getOutputStream().write(template);
        }

    }

}
