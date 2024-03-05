package com.affirm.common.service.impl;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.model.catalog.Bureau;
import com.affirm.common.model.transactional.ApplicationBureauLog;
import com.affirm.common.service.ApplicationBureauUtil;
import com.affirm.common.util.SOAPLogRetainerHandler;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("applicationBureauUtil")
public class ApplicationBureauUtilImpl implements ApplicationBureauUtil {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;

    @Override
    public <T> T callSoapWs(Integer bureauId, BindingProvider wsClientProvider, Integer loanApplicationId, EntityWebServiceUtilImpl.ISOAPProcess process, Class<T> classType) throws Exception {
        SOAPLogRetainerHandler logRetainer = new SOAPLogRetainerHandler();
        java.util.List<Handler> handlers = wsClientProvider.getBinding().getHandlerChain();
        handlers.add(logRetainer);
        wsClientProvider.getBinding().setHandlerChain(handlers);

        ApplicationBureauLog<T> applicationBureauLog = new ApplicationBureauLog<T>();
        applicationBureauLog.setBureauId(bureauId);
        applicationBureauLog.setLoanApplicationId(loanApplicationId);
        applicationBureauLog.setStatus(ApplicationBureauLog.STATUS_RUNNING);
        applicationBureauLog.setStartDate(new Date());
        applicationBureauLog.setId(loanApplicationDao.registerApplicationBureauLog(
                bureauId,
                loanApplicationId,
                applicationBureauLog.getStartDate(), null,
                applicationBureauLog.getStatus(),
                applicationBureauLog.getRequest(), null));

        T processResponse = null;
        Exception processException = null;
        try {
            processResponse = process.process();
        } catch (Exception ex) {
            processException = ex;
        }
        applicationBureauLog.setSoapResponse(processResponse);
        applicationBureauLog.setRequest(logRetainer.getRequestLog());
        applicationBureauLog.setResponse(logRetainer.getResponseLog());
        applicationBureauLog.setFinishDate(new Date());
        if (processException != null)
            applicationBureauLog.setStatus(ApplicationBureauLog.STATUS_FAILED);
        else
            applicationBureauLog.setStatus(ApplicationBureauLog.STATUS_SUCCESS);

        loanApplicationDao.updateApplicationBureauLogFinishDate(applicationBureauLog.getId(), applicationBureauLog.getFinishDate());
        loanApplicationDao.updateApplicationBureauLogStatus(applicationBureauLog.getId(), applicationBureauLog.getStatus());
        loanApplicationDao.updateApplicationBureauLogRequest(applicationBureauLog.getId(), applicationBureauLog.getRequest());
        loanApplicationDao.updateApplicationBureauLogResponse(applicationBureauLog.getId(), applicationBureauLog.getResponse());

        if (processException != null)
            throw processException;

        return processResponse;
    }

    @Override
    public <T> T callRestWs(Integer bureauId, UriComponentsBuilder builder, Integer loanApplicationId, Class<T> classType) throws Exception {

        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());
        RestTemplate restTemplate = new RestTemplate();
        T result = null;

        ApplicationBureauLog applicationBureauLog = new ApplicationBureauLog();
        applicationBureauLog.setBureauId(bureauId);
        applicationBureauLog.setLoanApplicationId(loanApplicationId);
        applicationBureauLog.setRequest(builder.build().encode().toUri().toString());
        applicationBureauLog.setStatus(ApplicationBureauLog.STATUS_RUNNING);
        applicationBureauLog.setStartDate(new Date());
        applicationBureauLog.setId(loanApplicationDao.registerApplicationBureauLog(
                bureauId,
                loanApplicationId,
                applicationBureauLog.getStartDate(), null,
                applicationBureauLog.getStatus(),
                applicationBureauLog.getRequest(), null));

        Exception webserviceException = null;
        HttpEntity<String> response = null;
        try {
            response = restTemplate.exchange(
                    builder.build().encode().toUri(),
                    HttpMethod.GET,
                    entity,
                    String.class);
        } catch (Exception ex) {
            try {
                ex.printStackTrace();
                response = new HttpEntity<>(((HttpClientErrorException) ex).getResponseBodyAsString());
            } catch (Exception e) {
                webserviceException = e;
            }
        }

        switch (bureauId) {
            case Bureau.NOSIS:
                try {
                    DocumentBuilder builderXml = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    InputSource src = new InputSource();
                    src.setCharacterStream(new StringReader(response.getBody()));

                    Document document = builderXml.parse(src);
                    String responseUrl = document.getElementsByTagName("URL").item(0).getTextContent();

                    builder = UriComponentsBuilder.fromHttpUrl(responseUrl);
                    restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));

                    final String[] responseRest = new String[1];
                    List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
                    interceptors.add(new ClientHttpRequestInterceptor() {
                        @Override
                        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                            ClientHttpResponse response = execution.execute(request, body);
                            traceResponse(response);
                            return response;
                        }

                        private void traceResponse(ClientHttpResponse response) throws IOException {
                            HttpHeaders responseHeaders = response.getHeaders();
                            responseRest[0] = StreamUtils.copyToString(response.getBody(), determineCharset(responseHeaders));
                        }

                        protected Charset determineCharset(HttpHeaders headers) {
                            MediaType contentType = headers.getContentType();
                            if (contentType != null) {
                                try {
                                    Charset charSet = contentType.getCharSet();
                                    if (charSet != null) {
                                        return charSet;
                                    }
                                } catch (UnsupportedCharsetException e) {
                                    // ignore
                                }
                            }
                            return StandardCharsets.UTF_8;
                        }
                    });
                    restTemplate.setInterceptors(interceptors);

                    result = restTemplate.getForObject(builder.build().encode().toUri(), classType);

                    applicationBureauLog.setFinishDate(new Date());
                    applicationBureauLog.setResponse(response != null ? responseRest[0] : null);

                } catch (ParserConfigurationException | SAXException | IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                if(response != null)
                    result = (T)response.getBody().toString();

                applicationBureauLog.setFinishDate(new Date());
                applicationBureauLog.setResponse(response != null ? response.getBody().toString() : null);
                break;
        }
        if (webserviceException != null || response == null)
            applicationBureauLog.setStatus(ApplicationBureauLog.STATUS_FAILED);
        else
            applicationBureauLog.setStatus(ApplicationBureauLog.STATUS_SUCCESS);

        loanApplicationDao.updateApplicationBureauLogFinishDate(applicationBureauLog.getId(), applicationBureauLog.getFinishDate());
        loanApplicationDao.updateApplicationBureauLogStatus(applicationBureauLog.getId(), applicationBureauLog.getStatus());
        loanApplicationDao.updateApplicationBureauLogResponse(applicationBureauLog.getId(), applicationBureauLog.getResponse());

        if (webserviceException != null)
            throw webserviceException;

        return result;
    }

    @Override
    public <T> T callRestFormDataWs(Integer bureauId, UriComponentsBuilder builder, MultiValueMap<String, String> requestBody, HttpHeaders headers, Integer loanApplicationId, Class<T> classType) throws Exception {

        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());
        RestTemplate restTemplate = new RestTemplate();
        T result = null;
        if(headers == null) headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ApplicationBureauLog applicationBureauLog = new ApplicationBureauLog();
        applicationBureauLog.setBureauId(bureauId);
        applicationBureauLog.setLoanApplicationId(loanApplicationId);
        applicationBureauLog.setRequest(requestBody.toString());
        applicationBureauLog.setStatus(ApplicationBureauLog.STATUS_RUNNING);
        applicationBureauLog.setStartDate(new Date());
        applicationBureauLog.setId(loanApplicationDao.registerApplicationBureauLog(
                bureauId,
                loanApplicationId,
                applicationBureauLog.getStartDate(), null,
                applicationBureauLog.getStatus(),
                applicationBureauLog.getRequest(), null));

        Exception webserviceException = null;
        HttpEntity<String> response = null;
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(requestBody, headers);
        try {
            response =  restTemplate.postForEntity(builder.build().encode().toUri(), requestEntity,  String.class);
        } catch (Exception ex) {
            try {
                ex.printStackTrace();
                response = new HttpEntity<>(((HttpClientErrorException) ex).getResponseBodyAsString());
            } catch (Exception e) {
                webserviceException = e;
            }
        }

        switch (bureauId) {
            default:
                if(response != null)
                    result = (T)response.getBody().toString();
                applicationBureauLog.setFinishDate(new Date());
                applicationBureauLog.setResponse(response != null ? response.getBody().toString() : null);
                break;
        }

        if (webserviceException != null || response == null)
            applicationBureauLog.setStatus(ApplicationBureauLog.STATUS_FAILED);
        else
            applicationBureauLog.setStatus(ApplicationBureauLog.STATUS_SUCCESS);

        loanApplicationDao.updateApplicationBureauLogFinishDate(applicationBureauLog.getId(), applicationBureauLog.getFinishDate());
        loanApplicationDao.updateApplicationBureauLogStatus(applicationBureauLog.getId(), applicationBureauLog.getStatus());
        loanApplicationDao.updateApplicationBureauLogResponse(applicationBureauLog.getId(), applicationBureauLog.getResponse());

        if (webserviceException != null)
            throw webserviceException;

        return result;
    }

    @Override
    public <T> T callRestPostMethodWs(Integer bureauId, UriComponentsBuilder builder, JSONObject requestBody, HttpHeaders headers, Integer loanApplicationId, Class<T> classType) throws Exception {

        HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());
        RestTemplate restTemplate = new RestTemplate();
        T result = null;
        if(headers == null) headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ApplicationBureauLog applicationBureauLog = new ApplicationBureauLog();
        applicationBureauLog.setBureauId(bureauId);
        applicationBureauLog.setLoanApplicationId(loanApplicationId);
        applicationBureauLog.setRequest(requestBody.toString());
        applicationBureauLog.setStatus(ApplicationBureauLog.STATUS_RUNNING);
        applicationBureauLog.setStartDate(new Date());
        applicationBureauLog.setId(loanApplicationDao.registerApplicationBureauLog(
                bureauId,
                loanApplicationId,
                applicationBureauLog.getStartDate(), null,
                applicationBureauLog.getStatus(),
                applicationBureauLog.getRequest(), null));

        Exception webserviceException = null;
        HttpEntity<String> response = null;
        HttpEntity<String> requestEntity = new HttpEntity<String>(requestBody.toString(), headers);
        HttpEntity<Object> responseTest = null;
        try {
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            response = restTemplate.exchange(
                    builder.build().encode().toUri(),
                    HttpMethod.POST,
                    requestEntity,
                    String.class);
        } catch (Exception ex) {
            try {
                ex.printStackTrace();
                response = new HttpEntity<>(((HttpClientErrorException) ex).getResponseBodyAsString());
            } catch (Exception e) {
                webserviceException = e;
            }
        }

        switch (bureauId) {
            default:
                if(response != null)
                    result = (T)response.getBody().toString();
                applicationBureauLog.setFinishDate(new Date());
                applicationBureauLog.setResponse(response != null ? response.getBody().toString() : null);
                break;
        }

        if (webserviceException != null || response == null)
            applicationBureauLog.setStatus(ApplicationBureauLog.STATUS_FAILED);
        else
            applicationBureauLog.setStatus(ApplicationBureauLog.STATUS_SUCCESS);

        loanApplicationDao.updateApplicationBureauLogFinishDate(applicationBureauLog.getId(), applicationBureauLog.getFinishDate());
        loanApplicationDao.updateApplicationBureauLogStatus(applicationBureauLog.getId(), applicationBureauLog.getStatus());
        loanApplicationDao.updateApplicationBureauLogResponse(applicationBureauLog.getId(), applicationBureauLog.getResponse());

        if (webserviceException != null)
            throw webserviceException;

        return result;
    }


}
