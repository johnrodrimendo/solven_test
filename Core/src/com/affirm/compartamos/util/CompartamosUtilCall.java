package com.affirm.compartamos.util;

import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.transactional.EntityWebServiceLog;
import com.affirm.common.service.EntityWebServiceUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dev5 on 29/11/17.
 */
@Component
public class CompartamosUtilCall {

    @Autowired
    private EntityWebServiceUtil entityWebServiceUtil;

    public EntityWebServiceLog<JSONObject> call(EntityWebService entityWebService, String request, String authorization, Integer loanApplicationId) throws Exception {

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of("cache-control", "no-cache"));
        headers.add(Pair.of("authorization", "Bearer " + authorization));

        EntityWebServiceLog<JSONObject> response = entityWebServiceUtil.callRestWs(entityWebService, request, headers, loanApplicationId, false);
        response.setRestResponse(new JSONObject(response.getResponse()));
        return response;
    }
}
