package com.affirm.common.service.impl;

import com.affirm.common.dao.*;
import com.affirm.common.model.ExternalWSRecord;
import com.affirm.common.model.PagoEfectivoConfiguration;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.pagoefectivo.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.ErrorService;
import com.affirm.common.service.PagoEfectivoClientService;
import com.affirm.common.service.UtilService;
import com.affirm.system.configuration.Configuration;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.apache.poi.util.LocaleUtil.setUserTimeZone;

@Service("pagoEfectivoClientService")
public class PagoEfectivoClientServiceImpl implements PagoEfectivoClientService {

    private static Logger logger = Logger.getLogger(PagoEfectivoClientServiceImpl.class);

    @Autowired
    private LoanApplicationDAO loanApplicationDAO;
    @Autowired
    private PersonDAO personDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private ExternalWSRecordDAO externalWSRecordDAO;
    @Autowired
    private ErrorService errorService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private CreditDAO creditDAO;



    public final OkHttpClient client = new OkHttpClient.Builder().build();

    public final Integer expirationHours = 24;


    @Override
    public PagoEfectivoCreateAuthorizationResponse generateAuthToken(int entityProductParamId) throws IOException {
        PagoEfectivoConfiguration pagoEfectivoConfiguration = this.getConfiguration(entityProductParamId);
        PagoEfectivoCreateAuthorizationRequest pagoEfectivoCreateAuthorizationRequest = new PagoEfectivoCreateAuthorizationRequest();
        pagoEfectivoCreateAuthorizationRequest.setIdService(pagoEfectivoConfiguration.getIdService());
        pagoEfectivoCreateAuthorizationRequest.setAccessKey(pagoEfectivoConfiguration.getAccessKey());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        df.setTimeZone(TimeZone.getTimeZone("America/Lima"));
        pagoEfectivoCreateAuthorizationRequest.setDateRequest(df.format(new Date()));
        pagoEfectivoCreateAuthorizationRequest.setHashString(
                Hashing.sha256().hashString(String.format("%s.%s.%s.%s",pagoEfectivoConfiguration.getIdService(),pagoEfectivoConfiguration.getAccessKey(),pagoEfectivoConfiguration.getSecretKey(),pagoEfectivoCreateAuthorizationRequest.getDateRequest()), StandardCharsets.UTF_8).toString()
        );

        OkHttpClient.Builder clientBuilder = client.newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS);

        OkHttpClient clientCall = clientBuilder.build();

        okhttp3.MediaType mediaType = okhttp3.MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, new Gson().toJson(pagoEfectivoCreateAuthorizationRequest));

        Request.Builder serviceRequestBuilder = new Request.Builder()
                .url(pagoEfectivoConfiguration.getUrlGenerateAuthToken())
                .post(body);
        Request serviceRequest = serviceRequestBuilder.build();

        try {
            Response response = clientCall.newCall(serviceRequest).execute();
            ExternalWSRecord externalWSRecord = new ExternalWSRecord(null,new Date(),null,pagoEfectivoConfiguration.getUrlGenerateAuthToken(),new Gson().toJson(pagoEfectivoCreateAuthorizationRequest),null,null);
            String result = response != null && response.body() != null ? response.body().string() : null;
            externalWSRecord.setResponse(result);
            externalWSRecord.setResponseHttpCode(response != null && response.networkResponse() != null ? response.networkResponse().code() : null);
            externalWSRecordDAO.insertExternalWSRecord(externalWSRecord);
            //SAVE RESPONSE
            //
            if(result != null){
                PagoEfectivoCreateAuthorizationResponse data = new Gson().fromJson(result,PagoEfectivoCreateAuthorizationResponse.class);
                return  data;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

    @Override
    public PagoEfectivoCreateCIPResponse createCIPResponse(int loanApplicationId, Locale locale) throws Exception {

        LoanOffer selectedOffer = loanApplicationDAO.getLoanOffers(loanApplicationId).stream().filter(o -> o.getSelected() != null && o.getSelected()).findFirst().orElse(null);
        PagoEfectivoConfiguration pagoEfectivoConfiguration = this.getConfiguration(selectedOffer.getEntityProductParameterId());
        PagoEfectivoCreateAuthorizationResponse token = generateAuthToken(selectedOffer.getEntityProductParameterId());
        if(token != null && token.getData() != null && token.getData().getToken() != null){
            PagoEfectivoCreateCIPRequest createCIPRequest = new PagoEfectivoCreateCIPRequest();
            LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId,locale);
            Person person = personDAO.getPerson(loanApplication.getPersonId(),false,locale);
            User user = userDAO.getUser(loanApplication.getUserId());
            String email = user.getEmail();
            String transactionCode  = null;
            if(selectedOffer.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY){
                transactionCode = "WEB_CLIENTE_RECOVERY_1222";
                AztecaGetawayBase aztecaGetawayBase = loanApplication.getAztecaGatewayBaseData();
                if(aztecaGetawayBase != null){
                    createCIPRequest.setAmount(selectedOffer.getAmmount().toString());
                }

                setUserTimeZone(TimeZone.getTimeZone("America/Lima"));
                Calendar calendar = Calendar.getInstance();
                if(aztecaGetawayBase.getCampaniaId() == null || !Arrays.asList(AztecaGetawayBase.TIPO_CAMPANIA_PKM_1_CUOTA, AztecaGetawayBase.TIPO_CAMPANIA_PKM_NORMAL).contains(aztecaGetawayBase.getCampaniaId())){
                    calendar.setTime(loanApplication.getExpirationDate());
                    calendar.add(Calendar.MINUTE, -1);
                    calendar.set(Calendar.SECOND, 59);
                }
                else {
                    transactionCode = "WEB_CLIENTE_RECOVERY_PKM_1225";
                    calendar.setTime(loanApplication.getRegisterDate());
                    calendar.set(Calendar.HOUR_OF_DAY, 21);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                sdf.setTimeZone(TimeZone.getTimeZone("America/Lima"));
                createCIPRequest.setDateExpiry(sdf.format(calendar.getTime()));
            }
            else if(selectedOffer.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY_VIGENTE){
                transactionCode = "WEB_CLIENTE_CARTERA_AUTOM_1223";
                createCIPRequest.setAmount(selectedOffer.getAmmount().toString());

                setUserTimeZone(TimeZone.getTimeZone("America/Lima"));
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 21);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                sdf.setTimeZone(TimeZone.getTimeZone("America/Lima"));
                createCIPRequest.setDateExpiry(sdf.format(calendar.getTime()));
            }

            if(createCIPRequest.getAmount() == null || createCIPRequest.getAmount().isEmpty()) throw new Exception("Invalid amount. Cant generate CIP.");

            createCIPRequest.setCurrency(pagoEfectivoConfiguration.getCurrency());
            createCIPRequest.setTransactionCode(transactionCode);
            createCIPRequest.setPaymentConcept("Alfin Cobranzas");
            createCIPRequest.setUserName(person.getName());
            createCIPRequest.setUserLastName(person.getFirstSurname());
            createCIPRequest.setUserDocumentType(person.getDocumentType().getName());
            createCIPRequest.setUserDocumentNumber(person.getDocumentNumber());
            createCIPRequest.setUserPhone(user.getPhoneNumber());
            createCIPRequest.setUserCodeCountry("+"+person.getCountry().getId());
            createCIPRequest.setUserEmail(Configuration.hostEnvIsProduction() ? email : Configuration.EMAIL_DEV_TO);
            createCIPRequest.setServiceId(pagoEfectivoConfiguration.getIdService());

            OkHttpClient.Builder clientBuilder = client.newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS);

            OkHttpClient clientCall = clientBuilder.build();

            okhttp3.MediaType mediaType = okhttp3.MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE);
            okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, new Gson().toJson(createCIPRequest));

            Request.Builder serviceRequestBuilder = new Request.Builder()
                    .url(pagoEfectivoConfiguration.getUrlGenerateCIP())
                    .post(body);
            serviceRequestBuilder.addHeader("Authorization", "Bearer "+token.getData().getToken());
            Request serviceRequest = serviceRequestBuilder.build();

            try {
                Response response = clientCall.newCall(serviceRequest).execute();
                ExternalWSRecord externalWSRecord = new ExternalWSRecord(loanApplicationId,new Date(),null,pagoEfectivoConfiguration.getUrlGenerateCIP(),new Gson().toJson(createCIPRequest),null,null);
                String result = response != null && response.body() != null ? response.body().string() : null;
                externalWSRecord.setResponse(result);
                externalWSRecord.setResponseHttpCode(response != null && response.networkResponse() != null ? response.networkResponse().code() : null);
                externalWSRecordDAO.insertExternalWSRecord(externalWSRecord);
                //SAVE RESPONSE
                //
                if(result != null){
                    PagoEfectivoCreateCIPResponse data = new Gson().fromJson(result,PagoEfectivoCreateCIPResponse.class);
                    return data;
                }
            } catch (Exception ex) {
                throw ex;
            }

        }
        return null;
    }

    private PagoEfectivoConfiguration getConfiguration(Integer entityProductParam){
        PagoEfectivoConfiguration pagoEfectivoConfiguration = new PagoEfectivoConfiguration();
        String idService = null;
        String accessKey = null;
        String secretKey = null;
        String urlGenerateAuthToken = null;
        String urlGenerateCIP = null;
        String urlStatusCIP = null;
        String currency = null;
        if(Configuration.hostEnvIsProduction()){
            urlGenerateAuthToken = "https://services.pagoefectivo.pe/v1/authorizations";
            urlGenerateCIP = "https://services.pagoefectivo.pe/v1/cips";
            urlStatusCIP = "https://services.pagoefectivo.pe/v1/cips/search";
            currency = "PEN";
        }
        else{
            urlGenerateAuthToken = "https://pre1a.services.pagoefectivo.pe/v1/authorizations";
            urlGenerateCIP = "https://pre1a.services.pagoefectivo.pe/v1/cips";
            urlStatusCIP = "https://pre1a.services.pagoefectivo.pe/v1/cips/search";
            currency = "PEN";
        }
        switch (entityProductParam){
            case EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY:
                if(Configuration.hostEnvIsProduction()){
                    idService = "19844";
                    accessKey = "NzA5MjRjZWRmOGRlMzJi";
                    secretKey = "IJNkdGd+7R7uaROhTJSeLfJfpsM4mP3AkeDXXS+L";
                }
                else{
                    idService = "1339";
                    accessKey = "YTQ3Mjk1YjllYjlkYTA0";
                    secretKey = "Qb4/7g0rFbp1k7sd6l6doQ2shCXUNqkgBUqDXTz4";
                }
                break;
            case EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY_VIGENTE:
                if(Configuration.hostEnvIsProduction()){
                    idService = "19845";
                    accessKey = "NWIwZDg4MjAzNzNmNWU3";
                    secretKey = "yov/ODEsM24aVBUakZBYlDTYq0w1OspMiZuOsFvF";
                }
                else{
                    idService = "1339";
                    accessKey = "YTQ3Mjk1YjllYjlkYTA0";
                    secretKey = "Qb4/7g0rFbp1k7sd6l6doQ2shCXUNqkgBUqDXTz4";
                }
                break;
        }
        pagoEfectivoConfiguration.setIdService(idService);
        pagoEfectivoConfiguration.setAccessKey(accessKey);
        pagoEfectivoConfiguration.setSecretKey(secretKey);
        pagoEfectivoConfiguration.setUrlGenerateAuthToken(urlGenerateAuthToken);
        pagoEfectivoConfiguration.setUrlGenerateCIP(urlGenerateCIP);
        pagoEfectivoConfiguration.setUrlStatusCIP(urlStatusCIP);
        pagoEfectivoConfiguration.setCurrency(currency);
        return pagoEfectivoConfiguration;
    }

    @Override
    public boolean validateWebhookData(String body, String signature) throws Exception {
        String PESignatureGenerado;
        PagoEfectivoWebhookBody bodyData = new Gson().fromJson(body,PagoEfectivoWebhookBody.class);
        Integer entityProductParamId = this.getEntityProductParameterFromCIP(bodyData.getData().getCip());
        PagoEfectivoConfiguration pagoEfectivoConfiguration = this.getConfiguration(entityProductParamId);
        try {

            SecretKeySpec secretKeySpec;

            secretKeySpec = new SecretKeySpec(pagoEfectivoConfiguration.getSecretKey().getBytes(StandardCharsets.UTF_8), "HmacSHA256");

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);

            byte[] hmacSha256;
            hmacSha256 = mac.doFinal(body.getBytes(StandardCharsets.UTF_8));

            PESignatureGenerado = String.format("%064x", new BigInteger(1, hmacSha256));

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {

            errorService.sendErrorCriticSlack(String.format("ERROR PAGO EFECTIVO: \n Mensaje: Hubo un error en el PE Signature \n Body: %s",body));
            throw new Exception("Hubo un error en el PE Signature");
        };

        if(signature.equals(PESignatureGenerado)) {
            return true;
        }else {
            errorService.sendErrorCriticSlack(String.format("ERROR PAGO EFECTIVO: \n Mensaje: El PE-Signature es incorrecto \n Body: %s",body));
            throw new Exception("El PE-Signature es incorrecto");
        }
    }

    @Override
    public void CIPPayed(String body) throws Exception{
        if(body != null){
            PagoEfectivoWebhookBody bodyData = new Gson().fromJson(body,PagoEfectivoWebhookBody.class);
            if(bodyData.getEventType().equalsIgnoreCase("cip.paid")){
                if(bodyData.getData().getTransactionCode() != null && bodyData.getData().getCip() != null){

                }
                else{
                    errorService.sendErrorCriticSlack(String.format("ERROR PAGO EFECTIVO WEBHOOK: \n Mensaje: Loan o CIP no indicado \n Body: %s",body));
                }
            }
            else{
                errorService.sendErrorCriticSlack(String.format("ERROR PAGO EFECTIVO WEBHOOK: \n Mensaje: Evento desconocido \n Body: %s",body));
            }
        }
        else {
            errorService.sendErrorCriticSlack("ERROR PAGO EFECTIVO: \n Mensaje: Sin data \n");
        }
    }

    @Override
    public void verifyStatusCIP(String CIP, Integer loanCollectionPaymentId) throws Exception{
        Integer entityProductParamId = this.getEntityProductParameterFromCIP(CIP);
        PagoEfectivoConfiguration pagoEfectivoConfiguration = this.getConfiguration(entityProductParamId);
        PagoEfectivoCreateAuthorizationResponse token = generateAuthToken(entityProductParamId);
        if(token != null && token.getData() != null && token.getData().getToken() != null){

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cip", CIP);


            OkHttpClient.Builder clientBuilder = client.newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS);

            OkHttpClient clientCall = clientBuilder.build();

            okhttp3.MediaType mediaType = okhttp3.MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE);
            okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, new Gson().toJson(jsonObject));

            Request.Builder serviceRequestBuilder = new Request.Builder()
                    .url(pagoEfectivoConfiguration.getUrlStatusCIP())
                    .post(body);
            serviceRequestBuilder.addHeader("Authorization", "Bearer "+token.getData().getToken());
            Request serviceRequest = serviceRequestBuilder.build();

            try {
                Response response = clientCall.newCall(serviceRequest).execute();
                String result = response != null && response.body() != null ? response.body().string() : null;
                //SAVE RESPONSE
                //
                if(result != null){
                    System.out.println(response);
                }
            } catch (Exception ex) {
                throw ex;
            }

        }
        return;
    }

    private Integer getEntityProductParameterFromCIP(String CIP) throws Exception {

        return null;
    }

}
