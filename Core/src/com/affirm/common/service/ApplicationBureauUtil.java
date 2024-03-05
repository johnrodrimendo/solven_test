package com.affirm.common.service;

import com.affirm.common.service.impl.EntityWebServiceUtilImpl;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.xml.ws.BindingProvider;
import java.util.Map;


public interface ApplicationBureauUtil {
    public <T> T callSoapWs(Integer bureauId, BindingProvider wsClientProvider, Integer loanApplicationId, EntityWebServiceUtilImpl.ISOAPProcess process, Class<T> classType) throws Exception;
    public <T> T callRestWs(Integer bureauId, UriComponentsBuilder builder, Integer loanApplicationId, Class<T> classType) throws Exception;
    public <T> T callRestFormDataWs(Integer bureauId, UriComponentsBuilder builder, MultiValueMap<String, String> requestBody, HttpHeaders headers, Integer loanApplicationId, Class<T> classType) throws Exception;
    public <T> T callRestPostMethodWs(Integer bureauId, UriComponentsBuilder builder, JSONObject requestBody, HttpHeaders headers, Integer loanApplicationId, Class<T> classType) throws Exception;
}
