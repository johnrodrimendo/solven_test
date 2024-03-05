package com.affirm.bpeoplerest.util;

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

@Component
public class BPeopleUtilCall {

    @Autowired
    private EntityWebServiceUtil entityWebServiceUtil;

    public EntityWebServiceLog<JSONObject> call(EntityWebService entityWebService, String request, Integer loanApplicationId) throws Exception {

        String securityKey = Configuration.hostEnvIsProduction() ? entityWebService.getProductionSecurityKey() : entityWebService.getSandboxSecurityKey();
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of("content-type", "application/json"));

        if (securityKey != null && securityKey.length() > 0) {
            headers.add(Pair.of("apikey", securityKey));
        }

        EntityWebServiceLog<JSONObject> response = entityWebServiceUtil.callRestWs(entityWebService, request, headers, loanApplicationId, false);
        response.setRestResponse(new JSONObject(response.getResponse()));
        return response;
    }

}
