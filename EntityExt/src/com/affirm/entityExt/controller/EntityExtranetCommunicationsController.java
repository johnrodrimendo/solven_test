package com.affirm.entityExt.controller;

import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.common.dao.BasesDAO;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.EntityExtranetDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.WebApplication;
import com.affirm.common.model.transactional.GatewayBaseEvent;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.ReportsService;
import com.affirm.common.service.WebscrapperService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.entityExt.models.PaginatorTableFilterForm;
import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller("entityExtranetCommunicationsController")
public class EntityExtranetCommunicationsController {

    public final static String URL = "communications";

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CreditDAO creditDAO;
    @Autowired
    private EntityExtranetService entityExtranetService;
    @Autowired
    private ReportsService reportsService;
    @Autowired
    private EntityExtranetDAO entityExtranetDAO;
    @Autowired
    private BasesDAO basesDAO;
    @Autowired
    private WebscrapperService webscrapperService;

    @RequestMapping(value = URL, method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:communications:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object communicationsView(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {

        int entityId = entityExtranetService.getLoggedUserEntity().getPrincipalEntity().getId();

        List<GatewayBaseEvent> data = entityExtranetDAO.getCollectionBaseEvent(entityId, null, null, 0,  PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR, locale);
        Pair<Integer, Double> countAdSum = entityExtranetDAO.getCollectionBaseEventCount(entityId, null, null,  locale);

        Integer limitPaginator = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        model.addAttribute("limitPaginator", limitPaginator);
        model.addAttribute("totalCount", countAdSum.getLeft());
        model.addAttribute("data", data);
        model.addAttribute("page", "communications");
        model.addAttribute("title", "Comunicaciones");

        return new ModelAndView("/entityExtranet/extranetCommunication");
    }

    @RequestMapping(value = URL+"/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:communications:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    @ResponseBody
    public Object getCommunications(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        Integer offset = 0;
        Integer limit = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
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
        }

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        int entityId = loggedUserEntity.getPrincipalEntity().getId();

        List<GatewayBaseEvent> data = entityExtranetDAO.getCollectionBaseEvent(entityId,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                offset,  limit, locale);

        Pair<Integer, Double> countAdSum = entityExtranetDAO.getCollectionBaseEventCount(entityId,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                locale);

        if(offset < 0) offset = 0;
        model.addAttribute("offset", offset);
        model.addAttribute("data", data);
        model.addAttribute("totalCount", countAdSum.getLeft());
        model.addAttribute("page", "communications");
        model.addAttribute("title", "Comunicaciones");

        return new ModelAndView("/entityExtranet/extranetCommunication :: list");
    }

    @RequestMapping(value = URL+"/count", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:communications:view", saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    public Object getCommunicationsCount(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        int entityId = loggedUserEntity.getPrincipalEntity().getId();

        String creationFrom = null;
        String creationTo = null;
        String searchValue = null;
        if(filter != null){
            creationFrom = filter.getCreationFrom();
            creationTo = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }

        Pair<Integer, Double> countAdSum = entityExtranetDAO.getCollectionBaseEventCount(entityId,
                creationFrom != null && !creationFrom.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationFrom) : null,
                creationTo != null && !creationTo.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(creationTo) : null,
                locale);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("count",countAdSum.getLeft());

        return AjaxResponse.ok(new Gson().toJson(data));
    }

    @RequestMapping(value = URL+"/preSendSms", method = RequestMethod.POST)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @RequiresPermissionOr403(permissions = "menu:communications:view", saveLog = true, webApp = WebApplication.ENTITY_EXTRANET)
    public Object validateSendSmsToActualBase(
            ModelMap model, Locale locale) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        int entityId = loggedUserEntity.getPrincipalEntity().getId();

        Integer count = basesDAO.getAztecaCobranzaPhonesCount();

        JSONObject json = new JSONObject();
        json.put("countPhones", count);
        json.put("countUsers", count);
        return AjaxResponse.ok(json.toString());
    }

}
