package com.affirm.livenessapi.util;

import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.transactional.EntityWebServiceLog;
import com.affirm.common.service.EntityWebServiceUtil;
import com.affirm.common.service.ErrorService;
import com.affirm.common.service.impl.ErrorServiceImpl;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class LivenessApiUtilCall {

    @Autowired
    private EntityWebServiceUtil entityWebServiceUtil;
    @Autowired
    private ErrorService errorService;

    public EntityWebServiceLog<JSONObject> call(EntityWebService entityWebService, String request, Integer loanApplicationId, okhttp3.RequestBody requestBody, okhttp3.MediaType requestMediaType) throws Exception {

        String securityKey = Configuration.hostEnvIsProduction() ? entityWebService.getProductionSecurityKey() : entityWebService.getSandboxSecurityKey();
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of("Authorization", Configuration.hostEnvIsProduction() ? entityWebService.getProductionSecurityKey() : entityWebService.getSandboxSecurityKey()));
        headers.add(Pair.of("content-type", "multipart/form-data;"));

        EntityWebServiceLog<JSONObject> response = entityWebServiceUtil.callRestWs(entityWebService, request, headers, loanApplicationId, false, null, requestBody, requestMediaType);
        try{
            response.setRestResponse(new JSONObject(response.getResponse()));
        }
        catch (Exception e){
            errorService.onError(e);
        }
        return response;
    }

    public EntityWebServiceLog<JSONObject> callV2(EntityWebService entityWebService, String request, Integer loanApplicationId, Map<String, String> urlParams, okhttp3.RequestBody requestBody, okhttp3.MediaType requestMediaType) throws Exception {

        String securityKey = Configuration.hostEnvIsProduction() ? entityWebService.getProductionSecurityKey() : entityWebService.getSandboxSecurityKey();
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of("Authorization", Configuration.hostEnvIsProduction() ? entityWebService.getProductionSecurityKey() : entityWebService.getSandboxSecurityKey()));
        headers.add(Pair.of("content-type", "multipart/form-data;"));

        EntityWebServiceLog<JSONObject> response = entityWebServiceUtil.callRestWs(entityWebService, request, headers, loanApplicationId, false, urlParams, requestBody, requestMediaType);
        try{
            response.setRestResponse(new JSONObject(response.getResponse()));
        }
        catch (Exception e){
            errorService.onError(e);
        }
        return response;
    }



}
