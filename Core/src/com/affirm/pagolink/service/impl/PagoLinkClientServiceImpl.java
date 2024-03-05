package com.affirm.pagolink.service.impl;

import com.affirm.common.dao.*;
import com.affirm.common.model.ExternalWSRecord;
import com.affirm.common.model.catalog.EntityProductParams;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.ErrorService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.JsonUtil;
import com.affirm.pagolink.model.*;
import com.affirm.pagolink.service.PagoLinkClientService;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import okhttp3.*;
import org.apache.http.HttpHeaders;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service("pagoLinkClientService")
public class PagoLinkClientServiceImpl implements PagoLinkClientService {

    private static Logger logger = Logger.getLogger(PagoLinkClientServiceImpl.class);

    public final OkHttpClient client = new OkHttpClient.Builder().build();


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

    private PagoLinkConfiguration getConfiguration(Integer entityProductParamId){
        return this.getConfiguration(entityProductParamId, null);
    }

    private PagoLinkConfiguration getConfiguration(Integer entityProductParamId, Integer campaignId){

        String urlGenerateAuthToken = null;
        String urlAuthorizationEcommerce = null;
        String urlSessionToken = null;
        String urlCreateLink = null;
        String urlOrderDetail = null;
        String username = null;
        String password = null;
        String merchantId = null;
        PagoLinkConfiguration pagoLinkConfiguration = new PagoLinkConfiguration();
        if(Configuration.hostEnvIsProduction()){
            urlGenerateAuthToken = "https://apiprod.vnforapps.com/api.security/v1/security";
            urlCreateLink = "https://apitestenv.vnforapps.com/api.ordermgmt/api/v1/order/create/{merchantId}";
            urlOrderDetail = "https://apitestenv.vnforapps.com/api.ordermgmt/api/v1/order/query/{merchantId}/{orderId}";
            urlSessionToken = "https://apiprod.vnforapps.com/api.ecommerce/v2/ecommerce/token/session/{merchantId}";
            urlAuthorizationEcommerce = "https://apiprod.vnforapps.com/api.authorization/v3/authorization/ecommerce/{merchantId}";
        }
        else{
            urlGenerateAuthToken = "https://apitestenv.vnforapps.com/api.security/v1/security";
            urlCreateLink = "https://apitestenv.vnforapps.com/api.ordermgmt/api/v1/order/create/{merchantId}";
            urlOrderDetail = "https://apitestenv.vnforapps.com/api.ordermgmt/api/v1/order/query/{merchantId}/{orderId}";
            urlSessionToken = "https://apitestenv.vnforapps.com/api.ecommerce/v2/ecommerce/token/session/{merchantId}";
            urlAuthorizationEcommerce = "https://apitestenv.vnforapps.com/api.authorization/v3/authorization/ecommerce/{merchantId}";
        }
        switch (entityProductParamId){
            case EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY:
                if(Configuration.hostEnvIsProduction()){
                    username = "xxxxxxxxxxxxxxxxxxxxx";
                    password = "xxxxxxxxxxxxxxxxxxxxx";
                    merchantId = "xxxxxxxxxxxxxxxxxxxxx";
                    if(campaignId != null && Arrays.asList(AztecaGetawayBase.TIPO_CAMPANIA_PKM_1_CUOTA, AztecaGetawayBase.TIPO_CAMPANIA_PKM_NORMAL).contains(campaignId)){
                        merchantId = "xxxxxxxxxxxxxxxxxxxxx";
                    }
                }
                else{
                    username = "xxxxxxxxxxxxxxxxxxxxx";
                    password = "xxxxxxxxxxxxxxxxxxxxx";
                    merchantId = "xxxxxxxxxxxxxxxxxxxxx";
                    if(campaignId != null && Arrays.asList(AztecaGetawayBase.TIPO_CAMPANIA_PKM_1_CUOTA, AztecaGetawayBase.TIPO_CAMPANIA_PKM_NORMAL).contains(campaignId)){
                        merchantId = "xxxxxxxxxxxxxxxxxxxxx";
                    }
                }
                break;
            case EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY_VIGENTE:
                if(Configuration.hostEnvIsProduction()){
                    username = "xxxxxxxxxxxxxxxxxxxxx";
                    password = "xxxxxxxxxxxxxxxxxxxxx";
                    merchantId = "xxxxxxxxxxxxxxxxxxxxx";
                }
                else{
                    username = "xxxxxxxxxxxxxxxxxxxxx";
                    password = "xxxxxxxxxxxxxxxxxxxxx";
                    merchantId = "xxxxxxxxxxxxxxxxxxxxx";
                }
                break;
        }


        if(merchantId != null){
            urlCreateLink = urlCreateLink.replace("{merchantId}",merchantId);
            urlOrderDetail = urlOrderDetail.replace("{merchantId}",merchantId);
            urlSessionToken = urlSessionToken.replace("{merchantId}",merchantId);
            urlAuthorizationEcommerce = urlAuthorizationEcommerce.replace("{merchantId}",merchantId);
        }

        pagoLinkConfiguration.setUrlGenerateAuthToken(urlGenerateAuthToken);
        pagoLinkConfiguration.setUrlAuthorizationEcommerce(urlAuthorizationEcommerce);
        pagoLinkConfiguration.setUrlSessionToken(urlSessionToken);
        pagoLinkConfiguration.setUrlCreateLink(urlCreateLink);
        pagoLinkConfiguration.setUrlOrderDetail(urlOrderDetail);
        pagoLinkConfiguration.setUsername(username);
        pagoLinkConfiguration.setPassword(password);
        pagoLinkConfiguration.setMerchantId(merchantId);

        return pagoLinkConfiguration;

    }


    @Override
    public String generateToken(Integer loanApplicationId) throws Exception {

        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId,Configuration.getDefaultLocale());
        AztecaGetawayBase aztecaGetawayBase = loanApplication.getAztecaGatewayBaseData();
        PagoLinkConfiguration pagoLinkConfiguration = this.getConfiguration(loanApplication.getSelectedEntityProductParameterId(), aztecaGetawayBase != null ? aztecaGetawayBase.getCampaniaId() : null);

        OkHttpClient.Builder clientBuilder = client.newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS);

        OkHttpClient clientCall = clientBuilder.build();

//        OkHttpClient clientCall = createAuthenticatedClient(username,password);

        okhttp3.MediaType mediaType = okhttp3.MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE);

        String auth = pagoLinkConfiguration.getUsername() + ":" + pagoLinkConfiguration.getPassword();

        Request.Builder serviceRequestBuilder = new Request.Builder()
                .addHeader(HttpHeaders.AUTHORIZATION, Base64.getEncoder().encodeToString(auth.getBytes()))
                .url(pagoLinkConfiguration.getUrlGenerateAuthToken())
                .get();
        Request serviceRequest = serviceRequestBuilder.build();

        try {
            Response response = clientCall.newCall(serviceRequest).execute();
            ExternalWSRecord externalWSRecord = new ExternalWSRecord(loanApplicationId ,new Date(),null,pagoLinkConfiguration.getUrlGenerateAuthToken(),null,null,null);
            String result = response != null && response.body() != null ? response.body().string() : null;
            externalWSRecord.setResponse(result);
            externalWSRecord.setResponseHttpCode(response != null && response.networkResponse() != null ? response.networkResponse().code() : null);
            externalWSRecordDAO.insertExternalWSRecord(externalWSRecord);
            if(response.code() == 201) return result;
            errorService.sendErrorCriticSlack(String.format("ERROR PAGO LINK : \n Mensaje: NO SE PUDO GENERAR TOKEN \n LOAN : %s, Body: %s", loanApplicationId != null ? loanApplicationId : "", result));
        } catch (Exception ex) {
            ex.printStackTrace();
            errorService.sendErrorCriticSlack(String.format("ERROR PAGO LINK : \n Mensaje: NO SE PUDO GENERAR TOKEN \n LOAN : %s, Body: %s", loanApplicationId != null ? loanApplicationId : "", ex.getMessage()));
            return null;
        }
        return null;
    }

    private static OkHttpClient createAuthenticatedClient(final String username,
                                                          final String password) {
        OkHttpClient httpClient = new OkHttpClient.Builder().authenticator(new Authenticator() {
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(username, password);
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        }).build();
        return httpClient;
    }

    @Override
    public PagoLinkResponse createPaymentLink(int loanApplicationId, Locale locale) throws Exception {

        String token = generateToken(loanApplicationId);
        if(token != null){
            PagoLinkCreate pagoLinkCreate = new PagoLinkCreate();
            LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId,locale);
            AztecaGetawayBase aztecaGetawayBase = loanApplication.getAztecaGatewayBaseData();
            PagoLinkConfiguration pagoLinkConfiguration = this.getConfiguration(loanApplication.getSelectedEntityProductParameterId(), aztecaGetawayBase != null ? aztecaGetawayBase.getCampaniaId() : null);
            Person person = personDAO.getPerson(loanApplication.getPersonId(),false,locale);
            User user = userDAO.getUser(loanApplication.getUserId());

            JSONObject aztecaCobranzaBaseJson = JsonUtil.getJsonObjectFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_BASE_GATEWAY.getKey(), null);
            if(aztecaCobranzaBaseJson != null){
                aztecaGetawayBase = new Gson().fromJson(aztecaCobranzaBaseJson.toString(), AztecaGetawayBase.class);
                pagoLinkCreate.setAmount(aztecaGetawayBase.getMontoCampania() != null ? aztecaGetawayBase.getMontoCampania() : null);
            }

            if(pagoLinkCreate.getAmount() == null || pagoLinkCreate.getAmount() <= 0) throw new Exception("Invalid amount. Cant generate PagoLink Link.");


            pagoLinkCreate.setDescription("Alfin - Cobranzas");
            pagoLinkCreate.setExternalId(loanApplication.getId().toString());
            pagoLinkCreate.setCustomer(new PagoLinkCustomer());
            pagoLinkCreate.getCustomer().setFirstName(person.getFirstName());
            pagoLinkCreate.getCustomer().setLastName(person.getFirstSurname());
            pagoLinkCreate.getCustomer().setDocumentNumber(person.getDocumentNumber());
            pagoLinkCreate.getCustomer().setDocumentType(person.getDocumentType().getId() == IdentityDocumentType.DNI ? "NATIONAL_ID" : (person.getDocumentType().getId() == IdentityDocumentType.CE ? "RESIDENT_ID" : null ) );
            pagoLinkCreate.getCustomer().setEmail(user.getEmail());
            pagoLinkCreate.getCustomer().setPhoneNumber("+"+person.getCountry().getId()+user.getPhoneNumber());
            pagoLinkCreate.setOrderType(PagoLinkCreate.SINGLEPAY_ORDERTYPE);
            pagoLinkCreate.setDescription("Alfin - Cobranza");


            OkHttpClient.Builder clientBuilder = client.newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS);

            OkHttpClient clientCall = clientBuilder.build();

            okhttp3.MediaType mediaType = okhttp3.MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE);
            okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, new Gson().toJson(pagoLinkCreate));

            Request.Builder serviceRequestBuilder = new Request.Builder()
                    .url(pagoLinkConfiguration.getUrlCreateLink())
                    .post(body);
            serviceRequestBuilder.addHeader("Authorization", token);
            Request serviceRequest = serviceRequestBuilder.build();

            try {
                Response response = clientCall.newCall(serviceRequest).execute();
                ExternalWSRecord externalWSRecord = new ExternalWSRecord(loanApplicationId,new Date(),null,pagoLinkConfiguration.getUrlCreateLink(),new Gson().toJson(pagoLinkCreate),null,null);
                String result = response != null && response.body() != null ? response.body().string() : null;
                externalWSRecord.setResponse(result);
                externalWSRecord.setResponseHttpCode(response != null && response.networkResponse() != null ? response.networkResponse().code() : null);
                externalWSRecordDAO.insertExternalWSRecord(externalWSRecord);
                //SAVE RESPONSE
                if(result != null && response.code() == 201){
                    PagoLinkResponse data = new Gson().fromJson(result,PagoLinkResponse.class);
                    return data;
                }
            } catch (Exception ex) {
                throw ex;
            }

        }
        return null;
    }

    @Override
    @Deprecated
    public PagoLinkOrderDetail getOrderDetail(String orderId, Locale locale) throws Exception {
/*        String token = generateToken();
        if(token != null){

            OkHttpClient.Builder clientBuilder = client.newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS);

            OkHttpClient clientCall = clientBuilder.build();

            okhttp3.MediaType mediaType = okhttp3.MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE);

            Request.Builder serviceRequestBuilder = new Request.Builder()
                    .url(urlOrderDetail.replace("{orderId}",orderId));

            serviceRequestBuilder.addHeader("Authorization", token);
            Request serviceRequest = serviceRequestBuilder.build();

            try {
                Response response = clientCall.newCall(serviceRequest).execute();
                ExternalWSRecord externalWSRecord = new ExternalWSRecord(null,new Date(),null,urlOrderDetail.replace("{orderId}",orderId),null,null,null);
                String result = response != null && response.body() != null ? response.body().string() : null;
                externalWSRecord.setResponse(result);
                externalWSRecord.setResponseHttpCode(response != null && response.networkResponse() != null ? response.networkResponse().code() : null);
                externalWSRecordDAO.insertExternalWSRecord(externalWSRecord);
                //SAVE RESPONSE
                if(result != null && (response.code() == 201 || response.code() == 200)){
                    PagoLinkOrderDetail data = new Gson().fromJson(result,PagoLinkOrderDetail.class);
                    return data;
                }
            } catch (Exception ex) {
                throw ex;
            }

        }*/
        return null;
    }


    @Override
    public PagoLinkEcommerceAuthorizationResponse getAuthorizationResponse(int loanApplicationId, String transactionToken, Locale locale) throws Exception {
        String token = generateToken(loanApplicationId);
        if(token != null){
            LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId,locale);
            AztecaGetawayBase aztecaGetawayBase = loanApplication.getAztecaGatewayBaseData();
            PagoLinkConfiguration pagoLinkConfiguration = this.getConfiguration(loanApplication.getSelectedEntityProductParameterId(), aztecaGetawayBase != null ? aztecaGetawayBase.getCampaniaId() : null);
            Person person = personDAO.getPerson(loanApplication.getPersonId(),false,locale);
            Double amount = null;

            String purchaseNumber = null;
            LoanOffer selectedOffer = loanApplicationDAO.getLoanOffers(loanApplication.getId()).stream().filter(o -> o.getSelected() != null && o.getSelected()).findFirst().orElse(null);
            purchaseNumber = person.getDocumentNumber();
            if(selectedOffer.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY){
                JSONObject data = JsonUtil.getJsonObjectFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_BASE_GATEWAY.getKey(), null);
                if(data != null){
                    aztecaGetawayBase = new Gson().fromJson(data.toString(), AztecaGetawayBase.class);
                    amount = selectedOffer.getAmmount();
                }
            }else if(selectedOffer.getEntityProductParameterId() == EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY_VIGENTE){
                amount = selectedOffer.getAmmount();
            }

            if(amount == null || amount <= 0) throw new Exception("Invalid amount. Cant generate PagoLink Link.");
            if(purchaseNumber == null) {
                errorService.sendErrorCriticSlack(String.format("ERROR PAGO LINK : \n Mensaje: NO SE PUDO GENERAR AUTORIZACION \n LOAN : %s, Body: %s", loanApplicationId, "Invalid PurchaseNumber"));
                throw new Exception("Invalid PurchaseNumber PagoLink");
            }

            BigDecimal bd = new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP);
            PagoLinkEcommerceAuthorizationRequest pagoLinkEcommerceAuthorizationRequest = new PagoLinkEcommerceAuthorizationRequest();
            pagoLinkEcommerceAuthorizationRequest.getCardHolder().setDocumentNumber(person.getDocumentNumber());
            pagoLinkEcommerceAuthorizationRequest.getCardHolder().setDocumentType(person.getDocumentType().getId() == IdentityDocumentType.DNI ? "0" : (person.getDocumentType().getId() == IdentityDocumentType.CE ? "1" : "2"));
            pagoLinkEcommerceAuthorizationRequest.getOrder().setAmount(bd.doubleValue());
            pagoLinkEcommerceAuthorizationRequest.getOrder().setCurrency("PEN");
            pagoLinkEcommerceAuthorizationRequest.getOrder().setTokenId(transactionToken);
            pagoLinkEcommerceAuthorizationRequest.getOrder().setPurchaseNumber(purchaseNumber);

            OkHttpClient.Builder clientBuilder = client.newBuilder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS);

            OkHttpClient clientCall = clientBuilder.build();

            okhttp3.MediaType mediaType = okhttp3.MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE);
            okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, new Gson().toJson(pagoLinkEcommerceAuthorizationRequest));

            Request.Builder serviceRequestBuilder = new Request.Builder()
                    .url(pagoLinkConfiguration.getUrlAuthorizationEcommerce())
                    .post(body);

            serviceRequestBuilder.addHeader("Authorization", token);
            Request serviceRequest = serviceRequestBuilder.build();

            try {
                Response response = clientCall.newCall(serviceRequest).execute();
                ExternalWSRecord externalWSRecord = new ExternalWSRecord(loanApplicationId,new Date(),null,pagoLinkConfiguration.getUrlAuthorizationEcommerce(),new Gson().toJson(pagoLinkEcommerceAuthorizationRequest),null,null);
                String result = response != null && response.body() != null ? response.body().string() : null;
                externalWSRecord.setResponse(result);
                externalWSRecord.setResponseHttpCode(response != null && response.networkResponse() != null ? response.networkResponse().code() : null);
                externalWSRecordDAO.insertExternalWSRecord(externalWSRecord);
                //SAVE RESPONSE
                if(result != null){
                    PagoLinkEcommerceAuthorizationResponse data = new Gson().fromJson(result,PagoLinkEcommerceAuthorizationResponse.class);
                    return data;
                }
            } catch (Exception ex) {
                errorService.sendErrorCriticSlack(String.format("ERROR PAGO LINK : \n Mensaje: NO SE PUDO GENERAR AUTORIZACION \n LOAN : %s, Body: %s", loanApplicationId, ex.getMessage()));
                throw ex;
            }

        }
        return null;
    }

    @Override
    public String getMerchantId(int entityProductParameterId, Integer campaignId){
        PagoLinkConfiguration pagoLinkConfiguration = this.getConfiguration(entityProductParameterId, campaignId);
        return  pagoLinkConfiguration.getMerchantId();
    }

}

