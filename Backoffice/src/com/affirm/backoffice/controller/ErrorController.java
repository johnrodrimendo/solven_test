package com.affirm.backoffice.controller;

import com.affirm.backoffice.dao.ErrorDAO;
import com.affirm.backoffice.model.*;
import com.affirm.common.model.annotation.RequiresPermissionOr403;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.util.AjaxResponse;
import com.google.gson.Gson;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
@Scope("request")
public class ErrorController {
    private static final Logger logger = Logger.getLogger(ErrorController.class);

    @Autowired
    private ErrorDAO errorDAO;

    @RequestMapping(value = "/errors/lastExceptions", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "errors:lastExceptions", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getExceptions(HttpServletRequest request, ModelMap model, Locale locale) throws Exception {
        List<ExceptionApp> exceptions = errorDAO.getExceptions(10, 0);
        model.addAttribute("exceptions", exceptions);
        return new ModelAndView("lastExceptions");
    }

    @RequestMapping(value = "/errors/topExceptions", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "errors:topExceptions", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getTopExceptions(HttpServletRequest request, ModelMap model, Locale locale) throws Exception {
        List<RecurrentException> exceptions = errorDAO.getRecurrentExceptions(10, 0, new Date(), new Date());
        model.addAttribute("topExceptions", exceptions);
        return new ModelAndView("topExceptions");
    }

    @RequestMapping(value = "/errors/entityWsStatus", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "errors:entityWsStatus", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getReportEntityWsStatus(HttpServletRequest request, ModelMap model, Locale locale) throws Exception {
        List<ReportEntityWsStatus> entityWsStatuses = errorDAO.getReportEntityWsStatus(10, 0, new Date(), new Date());
        model.addAttribute("entityWsStatuses", entityWsStatuses);
        return new ModelAndView("reportEntityWsStatus");
    }

    @RequestMapping(value = "/errors/processByHour", method = RequestMethod.GET)
    @RequiresPermissionOr403(permissions = "errors:processByHour", type = RequiresPermissionOr403.Type.WEB)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
    public Object getReportProcessByHour(HttpServletRequest request, ModelMap model, Locale locale) throws Exception {
        DateTimeZone timeZone = DateTimeZone.forID("America/Lima");
        DateTime now = DateTime.now(timeZone);
        DateTime yesterdayStart = now.minusDays(1).withTimeAtStartOfDay();

        List<ReportProcessByHour> processesByHours = errorDAO.getReportProcessByHour(yesterdayStart.toDate(), now.toDate());
        model.addAttribute("processesByHours", new Gson().toJson(processesByHours));
        return new ModelAndView("reportProcessesByHour");
    }

    @RequestMapping(value = "/errors/entityError/entityId", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    public Object getEntityErrorsByEntityId(HttpServletRequest request, ModelMap model, Locale locale) throws Exception {
        List<EntityError> exceptions = errorDAO.getEntityErrorsByEntityId(26);
//        model.addAttribute("exceptions", exceptions);
        Gson gson = new Gson();
        return AjaxResponse.ok(gson.toJson(exceptions));
    }


}
