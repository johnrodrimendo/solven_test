package com.affirm.efl.util;

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
 * Created by dev5 on 07/07/17.
 */
@Component
public class EFLServiceCall {

    @Autowired
    private EntityWebServiceUtil entityWebServiceUtil;

    public JSONObject call(EntityWebService entityWebService, String request, Integer loanApplicationId) throws Exception{

        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of("content-type", "application/json"));
        headers.add(Pair.of("cache-control", "no-cache"));

        EntityWebServiceLog response = entityWebServiceUtil.callRestWs(entityWebService, request, headers, loanApplicationId, false);

        return new JSONObject(response.getResponse());
    }

}
