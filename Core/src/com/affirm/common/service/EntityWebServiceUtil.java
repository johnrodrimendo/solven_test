package com.affirm.common.service;

import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.transactional.EntityWebServiceLog;
import com.affirm.common.service.impl.EntityWebServiceUtilImpl;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.ws.BindingProvider;
import java.util.List;
import java.util.Map;

/**
 * Created by john on 21/10/16.
 */
public interface EntityWebServiceUtil {

    EntityWebServiceLog callRestWs(EntityWebService entityWebService, String request, List<Pair<String, String>> headers, Integer loanApplicationId, boolean shouldCallInsecureEndpoint) throws Exception;

    EntityWebServiceLog callRestWs(EntityWebService entityWebService, String request, List<Pair<String, String>> headers, Integer loanApplicationId, boolean shouldCallInsecureEndpoint, Map<String, String> urlParams) throws Exception;
    EntityWebServiceLog callRestWs(EntityWebService entityWebService, String request, List<Pair<String, String>> headers, Integer loanApplicationId, boolean shouldCallInsecureEndpoint, Map<String, String> urlParams, okhttp3.RequestBody requestBody, okhttp3.MediaType requestMediaType) throws Exception;

    <T> EntityWebServiceLog<T> callSoapWs(EntityWebService entityWebService, BindingProvider wsClientProvider, Integer loanApplicationId, EntityWebServiceUtilImpl.ISOAPProcess process) throws Exception;

    void updateLogStatusToFailed(int entityWebServiceLogId, boolean sendAlert);

    EntityWebServiceLog callRestWs(String url, String request, List<Pair<String, String>> headers, boolean shouldCallInsecureEndpoint) throws Exception;

    EntityWebServiceLog callRestWsGetMethod(EntityWebService entityWebService, String request, List<Pair<String, String>> headers, Integer loanApplicationId, boolean shouldCallInsecureEndpoint) throws Exception;

    EntityWebServiceLog callRestWsGetMethod(EntityWebService entityWebService, String request, List<Pair<String, String>> headers, Integer loanApplicationId, boolean shouldCallInsecureEndpoint, Map<String, String> urlParams) throws Exception;
}
