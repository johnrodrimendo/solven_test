package com.affirm.intico.service.impl;

import com.affirm.common.dao.*;
import com.affirm.common.model.ExternalWSRecord;
import com.affirm.common.model.pagoefectivo.PagoEfectivoCreateAuthorizationResponse;
import com.affirm.common.service.ErrorService;
import com.affirm.common.service.UtilService;
import com.affirm.intico.model.InticoConfirmSmsRequest;
import com.affirm.intico.model.InticoConfirmSmsResponse;
import com.affirm.intico.model.InticoSendSmsRequest;
import com.affirm.intico.model.InticoSendSmsResponse;
import com.affirm.intico.service.InticoClientService;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import okhttp3.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service("inticoClientService")
public class InticoClientServiceImpl implements InticoClientService {

    private static Logger logger = Logger.getLogger(com.affirm.common.service.impl.PagoEfectivoClientServiceImpl.class);

    public final OkHttpClient client = new OkHttpClient.Builder().build();

    private String senderId;
    private String user;
    private String password;
    private String urlSendSms;
    private String urlSmsStatus;

    private Integer proxyPort;
    private String proxyHost;
    private String proxyUsername;
    private String proxyPassword;

    @Autowired
    private ExternalWSRecordDAO externalWSRecordDAO;


    InticoClientServiceImpl(){
        getConfiguration();
    }

    private void getConfiguration(){
        if(Configuration.hostEnvIsProduction()){
            urlSendSms = "https://ws.intico.com.pe:8181/rest/webresources/envioSMS/smsv2";
            urlSmsStatus = "https://ws.intico.com.pe:8181/rest/webresources/envioSMS/confirmav2";
            user = "xxxxxxxxxxxxxxxxxxxx";
            password = "xxxxxxxxxxxxxxxxxxxx";
            senderId = "xxxxxxxxxxxxxxxxxxxx";
        }
        else{
            urlSendSms = "https://ws.intico.com.pe:8181/rest/webresources/envioSMS/smsv2";
            urlSmsStatus = "https://ws.intico.com.pe:8181/rest/webresources/envioSMS/confirmav2";
            user = "xxxxxxxxxxxxxxxxxxxx";
            password = "xxxxxxxxxxxxxxxxxxxx";
            senderId = "xxxxxxxxxxxxxxxxxxxx";
        }
    }

    @Override
    public InticoSendSmsResponse sendInticoSms(String phoneNumber, String message) throws IOException {

        List<String> phoneNumbersWL = Configuration.getPhoneNumbersForTest();

        if(!Configuration.hostEnvIsProduction() && !phoneNumbersWL.contains(phoneNumber)) return null;

        InticoSendSmsRequest inticoSendSmsRequest = new InticoSendSmsRequest();
        inticoSendSmsRequest.setMensaje(message);
        inticoSendSmsRequest.setCelular(phoneNumber);
        inticoSendSmsRequest.setSenderId(senderId);
        inticoSendSmsRequest.setPassword(password);
        inticoSendSmsRequest.setUsuario(user);

        OkHttpClient.Builder clientBuilder = client.newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS);
//                .connectTimeout(100, TimeUnit.SECONDS)
//                .readTimeout(100, TimeUnit.SECONDS)
//                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)))
//                .proxyAuthenticator(generateProxyAuthenticator())
//                .writeTimeout(100, TimeUnit.SECONDS);

        OkHttpClient clientCall = clientBuilder.build();

        okhttp3.MediaType mediaType = okhttp3.MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, new Gson().toJson(inticoSendSmsRequest));

        Request.Builder serviceRequestBuilder = new Request.Builder()
                .url(urlSendSms)
                .post(body);

//        java.net.Proxy proxy = new Proxy(Proxy.Type.HTTP,  new InetSocketAddress(proxyHost, proxyPort));
        Request serviceRequest = serviceRequestBuilder.build();

        try {
            Response response = clientCall.newCall(serviceRequest).execute();
            ExternalWSRecord externalWSRecord = new ExternalWSRecord(null,new Date(),null,urlSendSms,new Gson().toJson(inticoSendSmsRequest),null,null);
            String result = response != null && response.body() != null ? response.body().string() : null;
            externalWSRecord.setResponse(result);
            externalWSRecord.setResponseHttpCode(response != null && response.networkResponse() != null ? response.networkResponse().code() : null);
            externalWSRecordDAO.insertExternalWSRecord(externalWSRecord);
            //SAVE RESPONSE
            //
            if(result != null){
                InticoSendSmsResponse data = new Gson().fromJson(result,InticoSendSmsResponse.class);
                return  data;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

    @Override
    public InticoConfirmSmsResponse verifySmsStatus(String code) throws IOException {

        InticoConfirmSmsRequest inticoConfirmSmsRequest = new InticoConfirmSmsRequest();
        inticoConfirmSmsRequest.setCodigo(code);
        inticoConfirmSmsRequest.setPassword(password);
        inticoConfirmSmsRequest.setUsuario(user);

        OkHttpClient.Builder clientBuilder = client.newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS);
//                .connectTimeout(100, TimeUnit.SECONDS)
//                .readTimeout(100, TimeUnit.SECONDS)
//                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)))
//                .proxyAuthenticator(generateProxyAuthenticator())
//                .writeTimeout(100, TimeUnit.SECONDS);

        OkHttpClient clientCall =  clientBuilder.build();

        okhttp3.MediaType mediaType = okhttp3.MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, new Gson().toJson(inticoConfirmSmsRequest));

        Request.Builder serviceRequestBuilder = new Request.Builder()
                .url(urlSmsStatus)
                .post(body);

        Request serviceRequest = serviceRequestBuilder.build();

        try {
            Response response = clientCall.newCall(serviceRequest).execute();
            ExternalWSRecord externalWSRecord = new ExternalWSRecord(null,new Date(),null,urlSmsStatus,new Gson().toJson(inticoConfirmSmsRequest),null,null);
            String result = response != null && response.body() != null ? response.body().string() : null;
            externalWSRecord.setResponse(result);
            externalWSRecord.setResponseHttpCode(response != null && response.networkResponse() != null ? response.networkResponse().code() : null);
            externalWSRecordDAO.insertExternalWSRecord(externalWSRecord);
            if(result != null){
                InticoConfirmSmsResponse data = new Gson().fromJson(result,InticoConfirmSmsResponse.class);
                return  data;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

    private Proxy generateProxy(){

        InetSocketAddress proxyAddr = new InetSocketAddress(proxyHost, proxyPort);
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, proxyAddr);

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                if (getRequestingHost().equalsIgnoreCase(proxyHost)) {
                    if (proxyPort == getRequestingPort()) {
                        return new PasswordAuthentication(proxyUsername, proxyPassword.toCharArray());
                    }
                }
                return null;
            }
        });

        return proxy;
    }

    private okhttp3.Authenticator generateProxyAuthenticator(){
        return new okhttp3.Authenticator() {
            @Override public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(proxyUsername, proxyPassword);
                return response.request().newBuilder()
                        .header("Proxy-Authorization", credential)
                        .build();
            }
        };
    }
}
