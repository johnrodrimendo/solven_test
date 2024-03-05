package com.affirm.bancoazteca.util;

import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.transactional.EntityWebServiceLog;
import com.affirm.common.service.EntityWebServiceUtil;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BancoAztecaUtilCall {

    @Autowired
    private EntityWebServiceUtil entityWebServiceUtil;

    public EntityWebServiceLog<JSONObject> call(EntityWebService entityWebService, String request, Integer loanApplicationId) throws Exception {
        return call(entityWebService,request,loanApplicationId,null);
    }

    public EntityWebServiceLog<JSONObject> call(EntityWebService entityWebService, String request, Integer loanApplicationId,List<Pair<String, String>> headers) throws Exception {

        String securityKey = Configuration.hostEnvIsProduction() ? entityWebService.getProductionSecurityKey() : entityWebService.getSandboxSecurityKey();
        if(headers == null) headers = new ArrayList<>();
        headers.add(Pair.of("content-type", "application/json"));

        EntityWebServiceLog<JSONObject> response = entityWebServiceUtil.callRestWs(entityWebService, request, headers, loanApplicationId, !Configuration.hostEnvIsProduction() ? true : false);
        response.setRestResponse(new JSONObject(response.getResponse()));
        return response;
    }

    public EntityWebServiceLog<JSONObject> callGetMethod(EntityWebService entityWebService, String request, Integer loanApplicationId,List<Pair<String, String>> headers,Map<String, String> urlParams) throws Exception {

        String securityKey = Configuration.hostEnvIsProduction() ? entityWebService.getProductionSecurityKey() : entityWebService.getSandboxSecurityKey();
        if(headers == null) headers = new ArrayList<>();
        headers.add(Pair.of("content-type", "application/json"));

        EntityWebServiceLog<JSONObject> response = entityWebServiceUtil.callRestWsGetMethod(entityWebService, request, headers, loanApplicationId, !Configuration.hostEnvIsProduction() ? true : false, urlParams);
        response.setRestResponse(new JSONObject(response.getResponse()));
        return response;
    }

    public EntityWebServiceLog<JSONObject> callV2(EntityWebService entityWebService, String request, Integer loanApplicationId, Map<String, String> urlParams) throws Exception {

        String securityKey = Configuration.hostEnvIsProduction() ? entityWebService.getProductionSecurityKey() : entityWebService.getSandboxSecurityKey();
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of("content-type", "application/json"));

        EntityWebServiceLog<JSONObject> response = entityWebServiceUtil.callRestWs(entityWebService, request, headers, loanApplicationId, false, urlParams);
        response.setRestResponse(new JSONObject(response.getResponse()));
        return response;
    }



}
