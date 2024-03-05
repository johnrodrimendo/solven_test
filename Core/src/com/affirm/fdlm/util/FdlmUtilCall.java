package com.affirm.fdlm.util;

import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.transactional.EntityWebServiceLog;
import com.affirm.common.service.EntityWebServiceUtil;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class FdlmUtilCall {

    @Autowired
    private EntityWebServiceUtil entityWebServiceUtil;

    public EntityWebServiceLog<JSONObject> call(EntityWebService entityWebService, String request, Integer loanApplicationId, Map<String, String> urlParams) throws Exception {

        List<Pair<String, String>> headers = generateHeaders(entityWebService);

        EntityWebServiceLog<JSONObject> response = entityWebServiceUtil.callRestWs(entityWebService, request, headers, loanApplicationId, true, urlParams);
        response.setRestResponse(new JSONObject(response.getResponse()));
        return response;
    }

    public EntityWebServiceLog<JSONArray> call(EntityWebService entityWebService, String request, Integer loanApplicationId) throws Exception {

        List<Pair<String, String>> headers = generateHeaders(entityWebService);

        EntityWebServiceLog<JSONArray> response = entityWebServiceUtil.callRestWs(entityWebService, request, headers, loanApplicationId, true, null);
        response.setRestResponse(new JSONArray(response.getResponse()));
        return response;
    }

    private List<Pair<String, String>> generateHeaders(EntityWebService entityWebService) {
        String securityKey = Configuration.hostEnvIsProduction() ? entityWebService.getProductionSecurityKey() : entityWebService.getSandboxSecurityKey();
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of(HttpHeaders.CONTENT_TYPE, "application/json"));
        headers.add(Pair.of(HttpHeaders.AUTHORIZATION, "Basic " + securityKey));

        return headers;
    }

}
