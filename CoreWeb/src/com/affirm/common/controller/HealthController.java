/**
 *
 */
package com.affirm.common.controller;

import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.model.catalog.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.AjaxResponse;
import com.google.gson.Gson;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author jrodriguez
 */

@Controller
@Scope("request")
@RequestMapping("/health")
public class HealthController {

    private static final Logger logger = Logger.getLogger(HealthController.class);


    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public  ResponseEntity<String> getDepartments(
            Locale locale) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", "ok");
        return AjaxResponse.ok(jsonObject.toString());
    }


}
