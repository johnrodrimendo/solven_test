package com.affirm.client.model.interceptor;

import com.affirm.common.dao.RestApiDAO;
import com.affirm.common.model.transactional.WsClient;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.time.Instant;

/**
 * Created by john on 18/11/16.
 */
@Component
public class RestApiInterceptor extends HandlerInterceptorAdapter {

    private static Logger logger = Logger.getLogger(RestApiInterceptor.class);

    @Autowired
    private RestApiDAO restApiDao;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // This code let the local request to pass without cheking the header, because it was boring setting the header with every request
        if (false && Configuration.hostEnvIsLocal()) {
            WsClient wsClient = restApiDao.getWsClientByApiKey("r9ycU6m4utH9YCvfh92CKdZL6fa64lKgvunXJxl");
            request.setAttribute("wsClient", wsClient);
            return true;
        }



//        if (Configuration.hostEnvIsNotLocal() && !request.getScheme().toLowerCase().equals("https")) {
//            logger.debug("Is not https -> "+request.getRequestURL());
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return false;
//        }

        // Get the signature
        String authHeader = request.getHeader("Authorization");
        logger.debug("Auth header: " + authHeader);
        logger.debug("Content type: " + request.getHeader("content-type"));
        if (authHeader == null) {
            logger.debug("Auth header is null");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        WsClient wsClient;
        if(!authHeader.contains("=")){

            wsClient = restApiDao.getWsClientByApiKey(authHeader);
            if(wsClient == null){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

        }else{

            if (authHeader.split("=").length != 3) {
                logger.debug("Auth header size is not 3 ");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            String apiKey = authHeader.split("=")[0];
            String requestSignature = authHeader.split("=")[1];
            long requestUnixTime = Long.parseLong(authHeader.split("=")[2]);
            long actualUnixTime = Instant.now().getEpochSecond();

            // Validate that is only 1 min of diference between the request and now
            if (!Configuration.hostEnvIsLocal() && Math.abs(actualUnixTime - requestUnixTime) > 120) {
                logger.debug("Auth header time is not valid");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            // Validate the signature
            wsClient = restApiDao.getWsClientByApiKey(apiKey);
            String signature = encodeSignature(wsClient.getApiKeySecret(), wsClient.getApiKeySharedKey() + requestUnixTime + request.getRequestURI());
            if (!signature.equals(requestSignature)) {
                logger.debug("Signatures doesnt match");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }

        // Validate that the apikey is valid
        if (wsClient == null || !wsClient.getActive() || !wsClient.getApiKeyActive()) {
            logger.debug("The ws client is not ative");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // Add wsClient to request
        request.setAttribute("wsClient", wsClient);

        return true;
    }

    public static String encodeSignature(String secret, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        return URLEncoder.encode(Base64.encodeBase64String(sha256_HMAC.doFinal(data.getBytes("UTF-8"))), "UTF-8");
    }

    public static void main(String[] args) throws Exception {
        String secretKey = "M0o2WyLAW5KVJKHg5yQsyrFhuFRKa7kaOoDhVrf8Rh5VT7K4PuptcJ5jnzcJiUMfxnF51";
        String sharedKey = "LkqAiYlntqGi3MoxINm4rVue3x2IaQYOJjWcNSi";
//        String secretKey = "SBjHictS9PvdApRuYZhaoo0mi0T8nVjzM7yufpZ18Ds2y2zFWzzWA1Gp8MYlrltGqmvreg";
//        String sharedKey = "OaNxUrQHGlbOBnxgzGyFx10Vff7sAHE7CkY1eERp";
        long actualUnixTime = Instant.now().getEpochSecond();
//        long actualUnixTime = 1492014312;

        /*Para conectarse con ABACO en PRD*/
        String secretKeySolven = "uH58kMbE8BmtnrazBlgyINGXwJxsXrg1r9Pev8HcBut3rBlRNNjBBMuuUkFQ4olY949JqS";
        String sharedKeySolven = "goINBzBSY0LoPOfn8kyzKrCoKKWMiXVkYhjap10p";

        /*Para conectarse con ABACO en DEV*/
        System.out.println("*************************ABACO STG V1***************************************************************************************");
        secretKey = "SBjHictS9PvdApRuYZhaoo0mi0T8nVjzM7yufpZ18Ds2y2zFWzzWA1Gp8MYlrltGqmvreg";
        sharedKey = "OaNxUrQHGlbOBnxgzGyFx10Vff7sAHE7CkY1eERp";
        System.out.println(sharedKey + "=" + encodeSignature(secretKey, sharedKey + actualUnixTime + "/servicio/WSCredito") + "=" + actualUnixTime);
        System.out.println("*************************************************************************************************************************");

        System.out.println("*************************ABACO STG V2***************************************************************************************");
        secretKey = "SBjHictS9PvdApRuYZhaoo0mi0T8nVjzM7yufpZ18Ds2y2zFWzzWA1Gp8MYlrltGqmvreg";
        sharedKey = "OaNxUrQHGlbOBnxgzGyFx10Vff7sAHE7CkY1eERp";
        System.out.println(sharedKey + "=" + encodeSignature(secretKey, sharedKey + actualUnixTime + "/servicio/v2.0/WSCredito") + "=" + actualUnixTime);
        System.out.println("*************************************************************************************************************************");

        System.out.println("*************************ABACO WS***************************************************************************************");
        secretKey = "6W8wSkHDVz3pk6lMkYoisUtaHRCmaSLVJlP58v8nqOqzcdZ7IKJrfMS0S4rGI3a";
        sharedKey = "iJNhld4VXgUNDF0sN6enOD5bvcSumvLAhvlnTkwL8ZM";
        System.out.println(sharedKey + "=" + encodeSignature(secretKey, sharedKey + actualUnixTime + "/servicio/WSCredito") + "=" + actualUnixTime);
        System.out.println("*************************************************************************************************************************");


        /*Para conectarse con Compartamos en DEV*/
//        System.out.println("*************************COMPARTAMOS STG*********************************************************************************");
//        secretKey = "Mo6D(92tN,LgSYM<x)0,]KkH.1onU+Mir4v^/@?)3njdB|&q!gCr3r}2~7tC1d3";
//        sharedKey = "o0BOTPI5onjEhKimJ6BXwXAO9Kk4SWC9";
//        System.out.println(sharedKey + "=" + encodeSignature(secretKey, sharedKey + actualUnixTime + "/CreditosSolven/rest/GestionCreditos/TraerVariablesPreEvaluacion") + "=" + actualUnixTime);
//        System.out.println(sharedKey + "=" + encodeSignature(secretKey, sharedKey + actualUnixTime + "/CreditosSolven/rest/GestionCreditos/ConsultarVariablesEvaluacion") + "=" + actualUnixTime);
//        System.out.println(sharedKey + "=" + encodeSignature(secretKey, sharedKey + actualUnixTime + "/CreditosSolven/rest/GestionCreditos/TraerVariablesEvaluacion") + "=" + actualUnixTime);
//        System.out.println(sharedKey + "=" + encodeSignature(secretKey, sharedKey + actualUnixTime + "/CreditosSolven/rest/GestionCreditos/GeneracionCreditoSolven") + "=" + actualUnixTime);
//        System.out.println("*************************************************************************************************************************");


        /*Para conectarse con CAJA SULLANA en LOCAL*/
//        System.out.println("*************************CAJA SULLANA LOCAL*********************************************************************************");
//        secretKey = "0YZVWVVDQWTT6BJM83caGcoSHkPNtfeesjtHvItRwDwZw81tWc3kwgGHqNUURNkzgZ9LfS";
//        sharedKey = "GLxhA76jt7gz94np86xbqparBe8SHnX9TY9QDmtX";
//        System.out.println(sharedKey + "=" + encodeSignature(secretKey, sharedKey + actualUnixTime + "/api/solven/confirmarOperacion") + "=" + actualUnixTime);
//        System.out.println("*************************************************************************************************************************");

        /*Para conectarse con COMPARTAMOS en LOCAL*/
//        System.out.println("*************************COMPARTAMOS STG*********************************************************************************");
//        secretKey = "tJAeZ5EtldpgQkWRJdfYUzWY1YlO1UwV9gUC3K85ascL1BaZdjZSjPnwsLpIwDuqhKDHwt";
//        sharedKey = "H6CRn1YoTvxhy3l6ue06XG57tCkLY7YDMWSo6WBfHQ";
//        System.out.println(sharedKey + "=" + encodeSignature(secretKey, sharedKey + actualUnixTime + "/api/solven/confirmarOperacion") + "=" + actualUnixTime);
//        System.out.println("*************************************************************************************************************************");

        /*Para conectarse con COMPARTAMOS en LOCAL*/
//        System.out.println("*************************SOLVEN LOCAL*********************************************************************************");
//        secretKey = "tJAeZ5EtldpgQkWRJdfYUzWY1YlO1UwV9gUC3K85ascL1BaZdjZSjPnwsLpIwDuqhKDHwt";
//        sharedKey = "H6CRn1YoTvxhy3l6ue06XG57tCkLY7YDMWSo6WBfHQ";
//        System.out.println(sharedKey + "=" + encodeSignature(secretKey, sharedKey + actualUnixTime + "/api/solven/confirmarOperacion") + "=" + actualUnixTime);
//        System.out.println("*************************************************************************************************************************");


//        System.out.println("KEYS REST");
//        System.out.println(sharedKey + "=" + encodeSignature(secretKey, sharedKey + actualUnixTime + "/api/depositor/amount") + "=" + actualUnixTime);
//        System.out.println(sharedKey + "=" + encodeSignature(secretKey, sharedKey + actualUnixTime + "/api/depositor/payment") + "=" + actualUnixTime);
//        System.out.println(sharedKey + "=" + encodeSignature(secretKey, sharedKey + actualUnixTime + "/api/depositor/transaction/rebate") + "=" + actualUnixTime);
//        System.out.println("KEYS SOAP");


        /*System.out.println();
        System.out.println();
        //System.out.println(sharedKeySolven + "=" + encodeSignature(secretKeySolven, sharedKeySolven + actualUnixTime + "/api/solven/confirmarOperacion") + "=" + actualUnixTime);
        System.out.println(sharedKeySolven + "=" + encodeSignature(secretKeySolven, sharedKeySolven + actualUnixTime + "/CreditosSolven/rest/GestionCreditos/TraerVariablesPreEvaluacion") + "=" + actualUnixTime);
        System.out.println(sharedKeySolven + "=" + encodeSignature(secretKeySolven, sharedKeySolven + actualUnixTime + "/CreditosSolven/rest/GestionCreditos/ConsultarVariablesEvaluacion") + "=" + actualUnixTime);
        System.out.println(sharedKeySolven + "=" + encodeSignature(secretKeySolven, sharedKeySolven + actualUnixTime + "/CreditosSolven/rest/GestionCreditos/TraerVariablesEvaluacion") + "=" + actualUnixTime);
        System.out.println(sharedKeySolven + "=" + encodeSignature(secretKeySolven, sharedKeySolven + actualUnixTime + "/CreditosSolven/rest/GestionCreditos/GeneracionCreditoSolven") + "=" + actualUnixTime);

        System.out.println();*/
//        System.out.println(encodeSignature("WWWWWWWWWWWWWWWSECRET--KEYHHHHHHHHHHHHHH", "WWWWWWWWWWWWWWWWWAPI--KEYHHHHHHHHHHHHHHH" + actualUnixTime+"/api/depositor/amount"));
    }
}
