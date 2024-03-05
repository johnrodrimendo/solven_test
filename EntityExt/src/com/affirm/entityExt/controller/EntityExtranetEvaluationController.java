package com.affirm.entityExt.controller;

import com.affirm.client.service.EntityExtranetService;
import com.affirm.common.dao.WebServiceDAO;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class EntityExtranetEvaluationController {

    public static final String URL = "evaluation";

    private EntityExtranetService entityExtranetService;
    private WebServiceDAO webServiceDAO;

    @Autowired
    public EntityExtranetEvaluationController(
            EntityExtranetService entityExtranetService,
            WebServiceDAO webServiceDAO
            ) {
        this.entityExtranetService = entityExtranetService;
        this.webServiceDAO = webServiceDAO;
    }


//    @RequestMapping(value = "/"+URL, method = RequestMethod.GET)
////    @RequiresPermissionOr403(permissions = "menu:system:monitorApps:view", type = RequiresPermissionOr403.Type.WEB)
//    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.HTML)
//    public Object showPending(
//            ModelMap model, Locale locale, HttpServletRequest request) throws Exception {
//        model.addAttribute("title", "Evaluación");
//        model.addAttribute("page", "evaluation");
//        return new ModelAndView("/entityExtranet/extranetEvaluation");
//    }


}
