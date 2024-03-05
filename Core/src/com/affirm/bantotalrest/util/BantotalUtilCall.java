package com.affirm.bantotalrest.util;

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

/**
 * Created by dev5 on 26/07/17.
 */
@Component
public class BantotalUtilCall {

    @Autowired
    private EntityWebServiceUtil entityWebServiceUtil;

    public EntityWebServiceLog<JSONObject> call(EntityWebService entityWebService, String request, Integer loanApplicationId) throws Exception {

        String securityKey = Configuration.hostEnvIsProduction() ? entityWebService.getProductionSecurityKey() : entityWebService.getSandboxSecurityKey();
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of("content-type", "application/json"));

        EntityWebServiceLog<JSONObject> response = entityWebServiceUtil.callRestWs(entityWebService, request, headers, loanApplicationId, false);
        try{
            response.setRestResponse(new JSONObject(response.getResponse()));
        }
        catch (Exception e){
            System.out.println(String.format("ERROR RESPONSE BANTOTAL - %s", response != null && response.getLoanApplicationId() != null ? response.getLoanApplicationId().toString() : ""));
            e.printStackTrace();
        }
        return response;
    }

    public EntityWebServiceLog<JSONObject> callV2(EntityWebService entityWebService, String request, Integer loanApplicationId, Map<String, String> urlParams) throws Exception {

        String securityKey = Configuration.hostEnvIsProduction() ? entityWebService.getProductionSecurityKey() : entityWebService.getSandboxSecurityKey();
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of("content-type", "application/json"));

        EntityWebServiceLog<JSONObject> response = entityWebServiceUtil.callRestWs(entityWebService, request, headers, loanApplicationId, false, urlParams);
        try{
            response.setRestResponse(new JSONObject(response.getResponse()));
        }
        catch (Exception e){
            System.out.println(String.format("ERROR RESPONSE BANTOTAL - %s", response != null && response.getLoanApplicationId() != null ? response.getLoanApplicationId().toString() : ""));
            e.printStackTrace();
        }
        return response;
    }



}
