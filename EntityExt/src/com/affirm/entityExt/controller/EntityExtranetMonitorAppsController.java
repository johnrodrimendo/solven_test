package com.affirm.entityExt.controller;

import com.affirm.client.model.LoggedUserEntity;
import com.affirm.client.service.EntityExtranetService;
import com.affirm.client.service.MonitorServerService;
import com.affirm.common.dao.WebServiceDAO;
import com.affirm.common.model.EntityErrorExtranetPainter;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.catalog.ExtranetMenu;
import com.affirm.common.model.transactional.BanbifTcLeadLoan;
import com.affirm.common.model.transactional.EntityWebServiceLog;
import com.affirm.common.service.ReportsService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.model.EntityError;
import com.affirm.common.service.ErrorEntityService;
import com.affirm.entityExt.models.PaginatorTableFilterForm;
import com.affirm.heroku.model.ServerStatus;
import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class EntityExtranetMonitorAppsController {

    private MonitorServerService monitorServerService;
    private EntityExtranetService entityExtranetService;
    private WebServiceDAO webServiceDAO;
    private ErrorEntityService errorEntityService;
    @Autowired
    private ReportsService reportsService;

    @Autowired
    public EntityExtranetMonitorAppsController(
            MonitorServerService monitorServerService,
            EntityExtranetService entityExtranetService,
            WebServiceDAO webServiceDAO,
            ErrorEntityService errorEntityService
            ) {
        this.monitorServerService = monitorServerService;
        this.entityExtranetService = entityExtranetService;
        this.webServiceDAO = webServiceDAO;
        this.errorEntityService= errorEntityService;
    }


    @RequestMapping(value = "/monitorApps", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:system:monitorApps:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object showPending(
            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
        Integer daysToShow = 30;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -daysToShow);
        List<ServerStatus.Grouped> groupedData = monitorServerService.getStatusChartData(formatter.format(calendar.getTime()));
        Map<String, Double> accumulatedUptime = monitorServerService.getAccumulatedUptimeData(groupedData, daysToShow);
        List<String> appName = new ArrayList<>();
        for (ServerStatus.Grouped groupedDatum : groupedData) {
            if(!groupedDatum.getStatuses().isEmpty()){
                for (ServerStatus.Status status : groupedDatum.getStatuses()) {
                    if(!appName.contains(status.getApp())) appName.add(status.getApp());
                }
            }
        }
        for (int i = 1; i <= daysToShow; i++) {
            calendar.add(Calendar.DATE, 1);
            if(!groupedData.stream().anyMatch(e -> e.getDate().equals(formatter.format(calendar.getTime())))){
                ServerStatus.Grouped data = new ServerStatus.Grouped();
                data.setDate(formatter.format(calendar.getTime()));
                data.setStatuses(new ArrayList<>());
                for (String s : appName) {
                    ServerStatus.Status status = new ServerStatus.Status();
                    status.setApp(s);
                    data.getStatuses().add(status);
                }
                groupedData.add(data);
            }
        }
        Comparator<ServerStatus.Grouped> compareByDate = (ServerStatus.Grouped o1, ServerStatus.Grouped o2) -> {
            try {
                return formatter.parse(o1.getDate()).compareTo(formatter.parse(o2.getDate()) );
            } catch (ParseException e) {
                return 0;
            }
        };
        Collections.sort(groupedData, compareByDate);
        model.addAttribute("dataLog", groupedData);
        model.addAttribute("daysToShow", daysToShow);
        model.addAttribute("dataUptime", accumulatedUptime);
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        switch (loggedUserEntity.getPrincipalEntity().getId()){
            case Entity.BANBIF:
                model.addAttribute("showWebServiceHistoric", true);
                break;
        }

        //LISTADO DE ERRORES
        Pair<Integer, Double> countAdSum = errorEntityService.getEntityErrorsCount(loggedUserEntity.getPrincipalEntity() != null ? loggedUserEntity.getPrincipalEntity().getId() : null, null, null, null);
        model.addAttribute("totalCount", countAdSum.getLeft());
        model.addAttribute("limitPaginator", PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR);
        List<EntityErrorExtranetPainter> entityErrorList = errorEntityService.getEntityErrors(loggedUserEntity.getPrincipalEntity() != null ? loggedUserEntity.getPrincipalEntity().getId() : null, null, null, null, 0, PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR);
        model.addAttribute("errores", entityErrorList);

        return new ModelAndView("/entityExtranet/monitorApps");
    }

    @RequestMapping(value = "/monitorApps/webServiceResults", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:system:monitorApps:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object showWebServiceHistoric(ModelMap model) throws Exception {
        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();
        model.addAttribute("showWebServiceHistoric", true);
        switch (loggedUserEntity.getPrincipalEntity().getId()){
            case Entity.BANBIF:
                model.addAttribute("webServiceHistoric", webServiceDAO.getEntityWebServiceLogByWsServiceId(EntityWebService.BANBIF_RESULTADO_CUESTIONARIO,0,10));
                break;
        }
        return new ModelAndView("/entityExtranet/monitorApps :: webServiceResults");
    }

    @RequestMapping(value = "/entityError/list", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:system:monitorApps:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object getEntityErrors(ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        Integer offset = 0;
        Integer limit = PaginatorTableFilterForm.DEFAULT_LIMIT_PAGINATOR;
        String startDate = null;
        String finishDate = null;
        String searchValue = null;
        if(filter != null) {
            limit = filter.getLimit();
            startDate = filter.getCreationFrom();
            finishDate = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }
        if(filter != null && filter.getPage() != null && limit != null) {
            offset = filter.getPage() * limit;
        }

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        List<EntityErrorExtranetPainter> entityErrorList = errorEntityService.getEntityErrors(loggedUserEntity.getPrincipalEntity() != null ? loggedUserEntity.getPrincipalEntity().getId() : null,
                startDate != null && !startDate.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(startDate) : null,
                finishDate != null && !finishDate.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(finishDate) : null,
                searchValue,
                offset, limit);

        model.addAttribute("errores", entityErrorList);
        return new ModelAndView("/entityExtranet/monitorApps :: notificationErrorBody");
    }

    @RequestMapping(value = "/entityError/count", method = RequestMethod.POST)
    @RequiresPermissionOr403(permissions = "menu:system:monitorApps:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public Object countNotes(
            ModelMap model, Locale locale, PaginatorTableFilterForm filter) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        String startDate = null;
        String finishDate = null;
        String searchValue = null;
        if(filter != null) {
            startDate = filter.getCreationFrom();
            finishDate = filter.getCreationTo();
            searchValue = filter.getSearch() != null && !filter.getSearch().isEmpty() ? filter.getSearch() : null;
        }
        Pair<Integer, Double> countAdSum = errorEntityService.getEntityErrorsCount(loggedUserEntity.getPrincipalEntity() != null ? loggedUserEntity.getPrincipalEntity().getId() : null,
                startDate != null && !startDate.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(startDate) : null,
                finishDate != null && !finishDate.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(finishDate) : null,
                searchValue
                );
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("count", countAdSum.getLeft());

        return AjaxResponse.ok(new Gson().toJson(data));
    }


    @RequestMapping(value = "/entityError/{entityErrorId}", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "menu:system:monitorApps:view", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public void generateFileErrorDetail(ModelMap model, @PathVariable Integer entityErrorId, Locale locale, HttpServletResponse response, PaginatorTableFilterForm filter) throws Exception {

        LoggedUserEntity loggedUserEntity = entityExtranetService.getLoggedUserEntity();

        EntityErrorExtranetPainter entityError = errorEntityService.getEntityErrorsById(entityErrorId, loggedUserEntity.getPrincipalEntity() != null ? loggedUserEntity.getPrincipalEntity().getId() : null);
        EntityWebServiceLog entityWebServiceLog = null;
        if(entityError != null && entityError.getLgApplicationEntityWSId() != null){
            entityWebServiceLog = webServiceDAO.getEntityWebServiceLogById(entityError.getLgApplicationEntityWSId());
        }

        byte[] file = reportsService.createEntityErrorNotificationDetail(entityError, entityWebServiceLog);
        if (file != null) {
            MediaType contentType = MediaType.valueOf("text/csv");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyy");
            response.setHeader("Content-disposition", "attachment; filename=" + String.format("%s_%s_%s", "ERROR_", entityError.getEntityErrorId(), simpleDateFormat.format(entityError.getRegisterDate()))  + ".txt");
            response.setContentType(contentType.getType());
            response.getOutputStream().write(file);
        }
    }

}
