/**
 *
 */
package com.affirm.common.service.impl;


import com.affirm.common.dao.WebServiceDAO;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.transactional.EntityWebServiceLog;
import com.affirm.common.service.AwsSesEmailService;
import com.affirm.common.service.EntityWebServiceUtil;
import com.affirm.common.util.SOAPLogRetainerHandler;
import com.affirm.sentinel.rest.FixContentTypeInterceptor;
import com.affirm.system.configuration.Configuration;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author jrodriguez
 */
@Service("entityWebServiceUtil")
public class EntityWebServiceUtilImpl implements EntityWebServiceUtil {

    private static Logger logger = Logger.getLogger(EntityWebServiceUtilImpl.class);

    @Autowired
    private WebServiceDAO webServiceDao;
    @Autowired
    private AwsSesEmailService awsSesEmailService;

    public final OkHttpClient client = new OkHttpClient.Builder().build();

    @Override
    public EntityWebServiceLog callRestWs(EntityWebService entityWebService, String request, List<Pair<String, String>> headers, Integer loanApplicationId, boolean shouldCallInsecureEndpoint) throws Exception {
        return callRestWs(entityWebService, request, headers, loanApplicationId, shouldCallInsecureEndpoint, null);
    }

    @Override
    public EntityWebServiceLog callRestWs(EntityWebService entityWebService, String request, List<Pair<String, String>> headers, Integer loanApplicationId, boolean shouldCallInsecureEndpoint, Map<String, String> urlParams) throws Exception {
        return callRestWs(entityWebService, request, headers, loanApplicationId, shouldCallInsecureEndpoint, urlParams, null, null);
    }
    @Override
    public EntityWebServiceLog callRestWs(EntityWebService entityWebService, String request, List<Pair<String, String>> headers, Integer loanApplicationId, boolean shouldCallInsecureEndpoint, Map<String, String> urlParams, okhttp3.RequestBody requestBody, okhttp3.MediaType requestMediaType) throws Exception {

        String url = Configuration.hostEnvIsProduction() ? entityWebService.getProductionUrl() : entityWebService.getSandboxUrl();
        if(urlParams != null){
            for (Map.Entry<String, String> entry : urlParams.entrySet()) {
                url = url.replaceAll("%"+entry.getKey()+"%", entry.getValue());
            }
        }

        OkHttpClient.Builder clientBuilder = client.newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS);

        if (shouldCallInsecureEndpoint) {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            final HostnameVerifier hostnameVerifier = (hostname, session) -> true;

            clientBuilder
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier(hostnameVerifier);
        }

        // Add interceptor to remove the charset of the applicationtype
        if (entityWebService.getId() == EntityWebService.BANBIF_KONECTA_LEAD) {
            clientBuilder.addNetworkInterceptor(new FixContentTypeInterceptor());
        }

        OkHttpClient clientCall = clientBuilder
                .build();

        okhttp3.MediaType mediaType = requestMediaType != null ? requestMediaType : okhttp3.MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE);
        okhttp3.RequestBody body = requestBody != null ? requestBody : okhttp3.RequestBody.create(mediaType, request);

        Request.Builder serviceRequestBuilder = new Request.Builder()
                .url(url)
                .post(body);
        if (headers != null)
            for (Pair<String, String> header : headers) {
                serviceRequestBuilder.addHeader(header.getLeft(), header.getRight());
            }
        Request serviceRequest = serviceRequestBuilder.build();

        EntityWebServiceLog webServiceLog = new EntityWebServiceLog();
        webServiceLog.setEntityWebServiceId(entityWebService.getId());
        webServiceLog.setLoanApplicationId(loanApplicationId);
        webServiceLog.setRequest(request);
        webServiceLog.setStatus(EntityWebServiceLog.STATUS_RUNNING);
        webServiceLog.setStartDate(new Date());
        webServiceLog.setId(webServiceDao.registerEntityWebServiceLog(
                entityWebService.getId(),
                loanApplicationId,
                webServiceLog.getStartDate(), null,
                webServiceLog.getStatus(),
                webServiceLog.getRequest(), null));
        logger.debug("[Service Request [" + url + "] :" + request);

        String responseBody = null;
        boolean isSuccessful = false;
        Exception webserviceException = null;
        try {
//            if(Configuration.hostEnvIsLocal() || Configuration.hostEnvIsDev()){
//                switch (entityWebService.getId()){
//                    case EntityWebService.COMPARTAMOS_TRAER_VARIABLES_PREEVALUACION:
//                        responseBody = "{\"pcCodErr\":\"0\",\"poCredito\":{\"pcTipCre\":\"N\"},\"poCliente\":{\"plCliBdn\":false,\"pnPorPar\":0,\"plVincul\":false,\"plTraCmp\":false,\"pnPatrim\":0},\"poSolicitud\":{}}";
//                        break;
//                    case EntityWebService.COMPARTAMOS_CONSULTAR_VARIABLES_EVALUACION:
//                        responseBody = "{\"pcCodErr\":\"0\"}";
//                        break;
//                    case EntityWebService.COMPARTAMOS_TRAER_VARIABLES_EVALUACION:
//                        responseBody = "{\"poConyuge\":{},\"pcCodErr\":\"0\",\"poCliente\":{\"pnPatrim\":0,\"pnIngNet\":15000.00,\"pnPunExp\":1000}}";
//                        break;
//                    case EntityWebService.COMPARTAMOS_GENERAR_CREDITO:
//                        responseBody = "{\"paCronograma\":[{\"pnIntere\":0,\"pnSgToRi\":0,\"pnCapita\":2000,\"pcNroCuo\":\"000\",\"pnMCuota\":2000,\"pnMonItf\":0.1,\"pnTasSeg\":0,\"pnPagTot\":0,\"pdFecVen\":\"2018-08-22 00:00:00\",\"pnSalCap\":2000},{\"pnIntere\":145.46,\"pnSgToRi\":0,\"pnCapita\":258.04,\"pcNroCuo\":\"001\",\"pnMCuota\":403.5,\"pnMonItf\":0,\"pnTasSeg\":0,\"pnPagTot\":0,\"pdFecVen\":\"2018-10-04 00:00:00\",\"pnSalCap\":1741.96},{\"pnIntere\":93.43,\"pnSgToRi\":0,\"pnCapita\":310.07,\"pcNroCuo\":\"002\",\"pnMCuota\":403.5,\"pnMonItf\":0,\"pnTasSeg\":0,\"pnPagTot\":0,\"pdFecVen\":\"2018-11-05 00:00:00\",\"pnSalCap\":1431.89},{\"pnIntere\":69.43,\"pnSgToRi\":0,\"pnCapita\":334.07,\"pcNroCuo\":\"003\",\"pnMCuota\":403.5,\"pnMonItf\":0,\"pnTasSeg\":0,\"pnPagTot\":0,\"pdFecVen\":\"2018-12-04 00:00:00\",\"pnSalCap\":1097.82},{\"pnIntere\":57,\"pnSgToRi\":0,\"pnCapita\":346.5,\"pcNroCuo\":\"004\",\"pnMCuota\":403.5,\"pnMonItf\":0,\"pnTasSeg\":0,\"pnPagTot\":0,\"pdFecVen\":\"2019-01-04 00:00:00\",\"pnSalCap\":751.32},{\"pnIntere\":39.01,\"pnSgToRi\":0,\"pnCapita\":364.49,\"pcNroCuo\":\"005\",\"pnMCuota\":403.5,\"pnMonItf\":0,\"pnTasSeg\":0,\"pnPagTot\":0,\"pdFecVen\":\"2019-02-04 00:00:00\",\"pnSalCap\":386.83},{\"pnIntere\":18.1,\"pnSgToRi\":0,\"pnCapita\":386.83,\"pcNroCuo\":\"006\",\"pnMCuota\":404.93,\"pnMonItf\":0,\"pnTasSeg\":0,\"pnPagTot\":0,\"pdFecVen\":\"2019-03-04 00:00:00\",\"pnSalCap\":0}],\"pcCodErr\":\"0\",\"poCredito\":{\"pcCodCta\":\"999710000277\",\"pcNomPro\":\"CREDITO 100% EN LINEA\",\"pnMonTca\":80,\"pcNomCan\":\"INTERBANK\",\"pnTasEfe\":80},\"poCliente\":{\"pcCodCli\":\"9990013196\",\"pnPatrim\":0}}";
//                        break;
//                }
//                if(responseBody != null)
//                    isSuccessful = true;
//            }

            if (responseBody == null) {
                Response response = clientCall.newCall(serviceRequest).execute();
                responseBody = response != null && response.body() != null ? response.body().string() : null;
                isSuccessful = response != null && response.networkResponse() != null && response.networkResponse().isSuccessful();
            }
        } catch (Exception ex) {
            webserviceException = ex;
        }

        webServiceLog.setFinishDate(new Date());
        webServiceLog.setResponse(responseBody);
        if (webserviceException != null || !isSuccessful)
            webServiceLog.setStatus(EntityWebServiceLog.STATUS_FAILED);
        else
            webServiceLog.setStatus(EntityWebServiceLog.STATUS_SUCCESS);
        logger.debug("[Service Response [" + url + "][" + (webServiceLog.getStartDate().getTime() - webServiceLog.getFinishDate().getTime()) + "]] : " + webServiceLog.getResponse());

        webServiceDao.updateEntityWebServiceLogFinishDate(webServiceLog.getId(), webServiceLog.getFinishDate());
        webServiceDao.updateEntityWebServiceLogStatus(webServiceLog.getId(), webServiceLog.getStatus());
        webServiceDao.updateEntityWebServiceLogResponse(webServiceLog.getId(), webServiceLog.getResponse());

        if (webserviceException != null)
            throw webserviceException;

        return webServiceLog;
    }

    @Override
    public EntityWebServiceLog callRestWsGetMethod(EntityWebService entityWebService, String request, List<Pair<String, String>> headers, Integer loanApplicationId, boolean shouldCallInsecureEndpoint) throws Exception {
        return callRestWsGetMethod(entityWebService, request, headers, loanApplicationId, shouldCallInsecureEndpoint, null);
    }

    @Override
    public EntityWebServiceLog callRestWsGetMethod(EntityWebService entityWebService, String request, List<Pair<String, String>> headers, Integer loanApplicationId, boolean shouldCallInsecureEndpoint, Map<String, String> urlParams) throws Exception {

        String url = Configuration.hostEnvIsProduction() ? entityWebService.getProductionUrl() : entityWebService.getSandboxUrl();
        if(urlParams != null){
            for (Map.Entry<String, String> entry : urlParams.entrySet()) {
                url = url.replaceAll("%"+entry.getKey()+"%", entry.getValue());
            }
        }

        OkHttpClient.Builder clientBuilder = client.newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS);

        if (shouldCallInsecureEndpoint) {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            final HostnameVerifier hostnameVerifier = (hostname, session) -> true;

            clientBuilder
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier(hostnameVerifier);
        }

        OkHttpClient clientCall = clientBuilder.build();

        okhttp3.MediaType mediaType = okhttp3.MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE);

        Request.Builder serviceRequestBuilder = new Request.Builder()
                .url(url)
                .get();
        if (headers != null)
            for (Pair<String, String> header : headers) {
                serviceRequestBuilder.addHeader(header.getLeft(), header.getRight());
            }
        Request serviceRequest = serviceRequestBuilder.build();

        EntityWebServiceLog webServiceLog = new EntityWebServiceLog();
        webServiceLog.setEntityWebServiceId(entityWebService.getId());
        webServiceLog.setLoanApplicationId(loanApplicationId);
        webServiceLog.setRequest(request);
        webServiceLog.setStatus(EntityWebServiceLog.STATUS_RUNNING);
        webServiceLog.setStartDate(new Date());
        webServiceLog.setId(webServiceDao.registerEntityWebServiceLog(
                entityWebService.getId(),
                loanApplicationId,
                webServiceLog.getStartDate(), null,
                webServiceLog.getStatus(),
                webServiceLog.getRequest(), null));
        logger.debug("[Service Request [" + url + "] :" + request);

        String responseBody = null;
        boolean isSuccessful = false;
        Exception webserviceException = null;
        try {
//            if(Configuration.hostEnvIsLocal() || Configuration.hostEnvIsDev()){
//                switch (entityWebService.getId()){
//                    case EntityWebService.COMPARTAMOS_TRAER_VARIABLES_PREEVALUACION:
//                        responseBody = "{\"pcCodErr\":\"0\",\"poCredito\":{\"pcTipCre\":\"N\"},\"poCliente\":{\"plCliBdn\":false,\"pnPorPar\":0,\"plVincul\":false,\"plTraCmp\":false,\"pnPatrim\":0},\"poSolicitud\":{}}";
//                        break;
//                    case EntityWebService.COMPARTAMOS_CONSULTAR_VARIABLES_EVALUACION:
//                        responseBody = "{\"pcCodErr\":\"0\"}";
//                        break;
//                    case EntityWebService.COMPARTAMOS_TRAER_VARIABLES_EVALUACION:
//                        responseBody = "{\"poConyuge\":{},\"pcCodErr\":\"0\",\"poCliente\":{\"pnPatrim\":0,\"pnIngNet\":15000.00,\"pnPunExp\":1000}}";
//                        break;
//                    case EntityWebService.COMPARTAMOS_GENERAR_CREDITO:
//                        responseBody = "{\"paCronograma\":[{\"pnIntere\":0,\"pnSgToRi\":0,\"pnCapita\":2000,\"pcNroCuo\":\"000\",\"pnMCuota\":2000,\"pnMonItf\":0.1,\"pnTasSeg\":0,\"pnPagTot\":0,\"pdFecVen\":\"2018-08-22 00:00:00\",\"pnSalCap\":2000},{\"pnIntere\":145.46,\"pnSgToRi\":0,\"pnCapita\":258.04,\"pcNroCuo\":\"001\",\"pnMCuota\":403.5,\"pnMonItf\":0,\"pnTasSeg\":0,\"pnPagTot\":0,\"pdFecVen\":\"2018-10-04 00:00:00\",\"pnSalCap\":1741.96},{\"pnIntere\":93.43,\"pnSgToRi\":0,\"pnCapita\":310.07,\"pcNroCuo\":\"002\",\"pnMCuota\":403.5,\"pnMonItf\":0,\"pnTasSeg\":0,\"pnPagTot\":0,\"pdFecVen\":\"2018-11-05 00:00:00\",\"pnSalCap\":1431.89},{\"pnIntere\":69.43,\"pnSgToRi\":0,\"pnCapita\":334.07,\"pcNroCuo\":\"003\",\"pnMCuota\":403.5,\"pnMonItf\":0,\"pnTasSeg\":0,\"pnPagTot\":0,\"pdFecVen\":\"2018-12-04 00:00:00\",\"pnSalCap\":1097.82},{\"pnIntere\":57,\"pnSgToRi\":0,\"pnCapita\":346.5,\"pcNroCuo\":\"004\",\"pnMCuota\":403.5,\"pnMonItf\":0,\"pnTasSeg\":0,\"pnPagTot\":0,\"pdFecVen\":\"2019-01-04 00:00:00\",\"pnSalCap\":751.32},{\"pnIntere\":39.01,\"pnSgToRi\":0,\"pnCapita\":364.49,\"pcNroCuo\":\"005\",\"pnMCuota\":403.5,\"pnMonItf\":0,\"pnTasSeg\":0,\"pnPagTot\":0,\"pdFecVen\":\"2019-02-04 00:00:00\",\"pnSalCap\":386.83},{\"pnIntere\":18.1,\"pnSgToRi\":0,\"pnCapita\":386.83,\"pcNroCuo\":\"006\",\"pnMCuota\":404.93,\"pnMonItf\":0,\"pnTasSeg\":0,\"pnPagTot\":0,\"pdFecVen\":\"2019-03-04 00:00:00\",\"pnSalCap\":0}],\"pcCodErr\":\"0\",\"poCredito\":{\"pcCodCta\":\"999710000277\",\"pcNomPro\":\"CREDITO 100% EN LINEA\",\"pnMonTca\":80,\"pcNomCan\":\"INTERBANK\",\"pnTasEfe\":80},\"poCliente\":{\"pcCodCli\":\"9990013196\",\"pnPatrim\":0}}";
//                        break;
//                }
//                if(responseBody != null)
//                    isSuccessful = true;
//            }

            if (responseBody == null) {
                Response response = clientCall.newCall(serviceRequest).execute();
                responseBody = response != null && response.body() != null ? response.body().string() : null;
                isSuccessful = response != null && response.networkResponse() != null && response.networkResponse().isSuccessful();
            }
        } catch (Exception ex) {
            webserviceException = ex;
        }

        webServiceLog.setFinishDate(new Date());
        webServiceLog.setResponse(responseBody);
        if (webserviceException != null || !isSuccessful)
            webServiceLog.setStatus(EntityWebServiceLog.STATUS_FAILED);
        else
            webServiceLog.setStatus(EntityWebServiceLog.STATUS_SUCCESS);
        logger.debug("[Service Response [" + url + "][" + (webServiceLog.getStartDate().getTime() - webServiceLog.getFinishDate().getTime()) + "]] : " + webServiceLog.getResponse());

        webServiceDao.updateEntityWebServiceLogFinishDate(webServiceLog.getId(), webServiceLog.getFinishDate());
        webServiceDao.updateEntityWebServiceLogStatus(webServiceLog.getId(), webServiceLog.getStatus());
        webServiceDao.updateEntityWebServiceLogResponse(webServiceLog.getId(), webServiceLog.getResponse());

        if (webserviceException != null)
            throw webserviceException;

        return webServiceLog;
    }

    @Override
    public <T> EntityWebServiceLog<T> callSoapWs(EntityWebService entityWebService, BindingProvider wsClientProvider, Integer loanApplicationId, ISOAPProcess process) throws Exception {

        SOAPLogRetainerHandler logRetainer = new SOAPLogRetainerHandler();
        java.util.List<Handler> handlers = wsClientProvider.getBinding().getHandlerChain();
        handlers.add(logRetainer);
        wsClientProvider.getBinding().setHandlerChain(handlers);

        EntityWebServiceLog<T> webServiceLog = new EntityWebServiceLog<>();
        webServiceLog.setEntityWebServiceId(entityWebService.getId());
        webServiceLog.setLoanApplicationId(loanApplicationId);
        webServiceLog.setStatus(EntityWebServiceLog.STATUS_RUNNING);
        webServiceLog.setStartDate(new Date());
        webServiceLog.setId(webServiceDao.registerEntityWebServiceLog(
                entityWebService.getId(),
                loanApplicationId,
                webServiceLog.getStartDate(), null,
                webServiceLog.getStatus(),
                webServiceLog.getRequest(), null));

        T processResponse = null;
        Exception processException = null;
        try {
            processResponse = process.process();
        } catch (Exception ex) {
            processException = ex;
        }
        webServiceLog.setSoapResponse(processResponse);
        webServiceLog.setRequest(logRetainer.getRequestLog());
        webServiceLog.setResponse(logRetainer.getResponseLog());
        webServiceLog.setFinishDate(new Date());
        if (processException != null)
            webServiceLog.setStatus(EntityWebServiceLog.STATUS_FAILED);
        else
            webServiceLog.setStatus(EntityWebServiceLog.STATUS_SUCCESS);

        webServiceDao.updateEntityWebServiceLogFinishDate(webServiceLog.getId(), webServiceLog.getFinishDate());
        webServiceDao.updateEntityWebServiceLogStatus(webServiceLog.getId(), webServiceLog.getStatus());
        webServiceDao.updateEntityWebServiceLogRequest(webServiceLog.getId(), webServiceLog.getRequest());
        webServiceDao.updateEntityWebServiceLogResponse(webServiceLog.getId(), webServiceLog.getResponse());

        if (processException != null)
            throw processException;
        return webServiceLog;
    }

    @Override
    public void updateLogStatusToFailed(int entityWebServiceLogId, boolean sendAlert) {
        try {
            webServiceDao.updateEntityWebServiceLogStatus(entityWebServiceLogId, EntityWebServiceLog.STATUS_FAILED);

            if (Configuration.hostEnvIsProduction() && sendAlert) {
                awsSesEmailService.sendEmail(
                        "error@solven.pe"//from
                        , Configuration.EMAIL_ERROR_ENTITY_WEBSERVICE_TO()//to
                        , null//cc
                        , "Error de webservice entidad - " + entityWebServiceLogId//title
                        , "Revisa el id: " + entityWebServiceLogId + " en la tabla security.lg_application_entity_ws!"//body
                        , null
                        , null);//others
            }
        } catch (Exception ex) {
            ErrorServiceImpl.onErrorStatic(ex);
        }
    }

    public interface ISOAPProcess {
        <T> T process() throws Exception;
    }

    @Override
    public EntityWebServiceLog callRestWs(String url, String request, List<Pair<String, String>> headers, boolean shouldCallInsecureEndpoint) throws Exception {

        OkHttpClient.Builder clientBuilder = client.newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS);

        if (shouldCallInsecureEndpoint) {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            final HostnameVerifier hostnameVerifier = (hostname, session) -> true;

            clientBuilder
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier(hostnameVerifier);
        }

        OkHttpClient clientCall = clientBuilder.build();

        okhttp3.MediaType mediaType = okhttp3.MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, request);

        Request.Builder serviceRequestBuilder = new Request.Builder()
                .url(url)
                .post(body);
        if (headers != null)
            for (Pair<String, String> header : headers) {
                serviceRequestBuilder.addHeader(header.getLeft(), header.getRight());
            }
        Request serviceRequest = serviceRequestBuilder.build();

        EntityWebServiceLog webServiceLog = new EntityWebServiceLog();
        webServiceLog.setRequest(request);
        webServiceLog.setStatus(EntityWebServiceLog.STATUS_RUNNING);
        webServiceLog.setStartDate(new Date());
        logger.debug("[Service Request [" + url + "] :" + request);

        String responseBody = null;
        boolean isSuccessful = false;
        Exception webserviceException = null;

        try {
            if (responseBody == null) {
                Response response = clientCall.newCall(serviceRequest).execute();
                responseBody = response != null && response.body() != null ? response.body().string() : null;
                isSuccessful = response != null && response.networkResponse() != null && response.networkResponse().isSuccessful();
            }
        } catch (Exception ex) {
            webserviceException = ex;
        }

        webServiceLog.setFinishDate(new Date());
        webServiceLog.setResponse(responseBody);
        if (webserviceException != null || !isSuccessful)
            webServiceLog.setStatus(EntityWebServiceLog.STATUS_FAILED);
        else
            webServiceLog.setStatus(EntityWebServiceLog.STATUS_SUCCESS);
        logger.debug("[Service Response [" + url + "][" + (webServiceLog.getStartDate().getTime() - webServiceLog.getFinishDate().getTime()) + "]] : " + webServiceLog.getResponse());

        if (webserviceException != null)
            throw webserviceException;

        return webServiceLog;
    }

}
