package com.affirm.client.controller;

import com.affirm.client.service.MonitorServerService;
import com.affirm.common.model.annotation.ErrorControllerAnnotation;
import com.affirm.common.util.AjaxResponse;
import com.google.gson.Gson;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Scope("request")
@RequestMapping("monitor-servers")
public class MonitorServersController {

    private final MonitorServerService monitorServerService;

    private static final Gson gson = new Gson();

    public MonitorServersController(MonitorServerService monitorServerService) {
        this.monitorServerService = monitorServerService;
    }

    @RequestMapping(value = "/statuses", method = RequestMethod.GET)
    @ErrorControllerAnnotation(responseType = ErrorControllerAnnotation.ResponseType.JSON)
    @ResponseBody
    public ResponseEntity<String> getStatus(@RequestParam String dateFrom) throws Exception {
        return AjaxResponse.ok(gson.toJson(monitorServerService.getStatusChartData(dateFrom)));
    }

}
