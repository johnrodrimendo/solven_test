package com.affirm.bancoazteca.service.impl;

import com.affirm.bancoazteca.model.BancoAztecaCampaniaApi;
import com.affirm.bancoazteca.model.BancoAztecaGatewayApi;
import com.affirm.bancoazteca.model.RolConsejero;
import com.affirm.bancoazteca.service.BancoAztecaServiceCall;
import com.affirm.bancoazteca.util.BancoAztecaUtilCall;
import com.affirm.common.dao.*;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.transactional.EntityWebServiceLog;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.service.AwsSesEmailService;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.ErrorService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;

@Service("bancoAztecaServiceCall")
public class BancoAztecaServiceCallImpl implements BancoAztecaServiceCall {

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private BancoAztecaUtilCall bancoAztecaUtilCall;
    @Autowired
    private ErrorService errorService;
    @Autowired
    private LoanApplicationDAO loanApplicationDAO;
    @Autowired
    private PersonDAO personDAO;
    @Autowired
    private TranslatorDAO translatorDAO;
    @Autowired
    private WebServiceDAO webServiceDAO;
    @Autowired
    private AwsSesEmailService awsSesEmailService;
    @Autowired
    private ErrorEntityDao errorEntityDao;

    @Override
    public List<BancoAztecaCampaniaApi>  getPersonCampaigns(LoanApplication loanApplication, Person person, String token) throws Exception {
        if(person == null){
            person = personDAO.getPerson(loanApplication.getPersonId(),false,Configuration.getDefaultLocale());
        }
        if(token == null){
            JSONObject loginResponse = login(loanApplication);
            token = loginResponse.getJSONObject("Data").getString("Token");
        }
        JSONObject request = new JSONObject();
        request.put("TipoDocumento",person.getDocumentType().getId().toString());
        request.put("NroDocumento",person.getDocumentNumber());
        String jsonRequestData = request.toString();
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of("Authorization", "Bearer "+token));

        Map<String, String> urlParams = new HashMap<>();
        for (String keyStr : request.keySet()) {
            urlParams.put(keyStr,request.getString(keyStr));
        }
        boolean sendError = false;
        EntityWebServiceLog<JSONObject> webServiceResponse = bancoAztecaUtilCall.callGetMethod(catalogService.getEntityWebService(EntityWebService.BANCO_AZTECA_OBTAIN_PERSON_CAMPAIGNS), jsonRequestData, loanApplication.getId(),headers,urlParams);
        String errorMessage = String.format("BANCO AZTECA CAMPAÑA : \n Mensaje: ERROR AL OBTENER CAMPAÑAS\n LOAN : %s", loanApplication.getId());
        if(webServiceResponse.getRestResponse() != null && JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(),"Code", null) != null && JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(),"Code", null).equals(0)){
            if(JsonUtil.getJsonObjectFromJson(webServiceResponse.getRestResponse(),"Data", null) != null && JsonUtil.getJsonArrayFromJson(JsonUtil.getJsonObjectFromJson(webServiceResponse.getRestResponse(),"Data", null),"campanias",null) != null){
                JSONArray campaignsJson = JsonUtil.getJsonArrayFromJson(JsonUtil.getJsonObjectFromJson(webServiceResponse.getRestResponse(),"Data", null),"campanias",null);
                List<BancoAztecaCampaniaApi> campaigns = new ArrayList<>();
                for (int i = 0; i < campaignsJson.length(); i++) {
                    campaigns.add(new Gson().fromJson(campaignsJson.getJSONObject(i).toString(),BancoAztecaCampaniaApi.class));
                }
                webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANCO_AZTECA_OBTAIN_PERSON_CAMPAIGNS, campaigns.isEmpty() ? null : new Gson().toJson(campaigns.get(0)));
                return campaigns;
            }
            else {
                sendError = true;
            }
        }
        else sendError = true;
        if(sendError){
            errorService.sendErrorCriticSlack(errorMessage);
            sendErrorAndRegister(webServiceResponse, EntityWebService.BANCO_AZTECA_OBTAIN_PERSON_CAMPAIGNS);
        }
        return null;
    }

    @Override
    public JSONObject login(LoanApplication loanApplication) throws Exception {
        JSONObject request = new JSONObject();
        request.put("UserLogin",System.getenv("BANCO_AZTECA_USER_CAMPAIGN"));
        request.put("Password",System.getenv("BANCO_AZTECA_PASSWORD_CAMPAIGN"));
        request.put("ApplicationId",Integer.valueOf(System.getenv("BANCO_AZTECA_APP_ID_CAMPAIGN")));
        String jsonLoginData = request.toString();
        String errorMessage = null;
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of("ApiKey", System.getenv("BANCO_AZTECA_API_KEY")));

        EntityWebServiceLog<JSONObject> webServiceResponse = bancoAztecaUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANCO_AZTECA_CAMPAIGN_LOGIN), jsonLoginData, loanApplication.getId(),headers);
        if(webServiceResponse.getRestResponse() != null && JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(),"Code", null) != null && JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(),"Code", null).equals(0)){
            JSONObject data = JsonUtil.getJsonObjectFromJson(webServiceResponse.getRestResponse(), "Data", null);
            if(data != null && JsonUtil.getStringFromJson(data, "Token", null) != null){
                return webServiceResponse.getRestResponse();
            }
            else {
                errorMessage = String.format("BANCO AZTECA CAMPAÑA : \n Mensaje: NO SE PUDO GENERAR TOKEN\n LOAN : %s, Response: %s", loanApplication.getId(),webServiceResponse.getRestResponse() != null ? webServiceResponse.getRestResponse().toString() : null );
                errorService.sendErrorCriticSlack(errorMessage);
                sendErrorBPeople(catalogService.getEntityWebService(EntityWebService.BANCO_AZTECA_CAMPAIGN_LOGIN).getWbeserviceName(),
                        "",
                        webServiceResponse.getRestResponse() != null ? webServiceResponse.getResponse() : "-",
                        webServiceResponse.getRestResponse() != null && JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(),"Message", null) != null ? JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(),"Message", null) : "-",
                        webServiceResponse.getRestResponse() != null && JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(),"Code", null) != null ? JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(),"Code", null).toString() : "-"
                );
                throw new Exception(errorMessage);
            }
        }
        errorMessage = String.format("BANCO AZTECA CAMPAÑA : \n Mensaje: NO SE PUDO GENERAR TOKEN\n LOAN : %s, Response: SIN RESPUESTA", loanApplication.getId());
        errorService.sendErrorCriticSlack(errorMessage);
        sendErrorAndRegister(webServiceResponse, EntityWebService.BANCO_AZTECA_CAMPAIGN_LOGIN);
        throw new Exception(errorMessage);
    }

    @Override
    public RolConsejero getAdviserRole(LoanApplication loanApplication, Person person, String token) throws Exception {
        if(person == null){
            person = personDAO.getPerson(loanApplication.getPersonId(),false,Configuration.getDefaultLocale());
        }
        if(token == null){
            JSONObject loginResponse = login(loanApplication);
            token = loginResponse.getJSONObject("Data").getString("Token");
        }
        JSONObject request = new JSONObject();
        request.put("TipoDocumento",person.getDocumentType().getId().toString());
        request.put("NroDocumento",person.getDocumentNumber());
        String jsonRequestData = request.toString();
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of("Authorization", "Bearer "+token));

        Map<String, String> urlParams = new HashMap<>();
        for (String keyStr : request.keySet()) {
            urlParams.put(keyStr,request.getString(keyStr));
        }

        boolean sendError = false;

        EntityWebServiceLog<JSONObject> webServiceResponse = bancoAztecaUtilCall.callGetMethod(catalogService.getEntityWebService(EntityWebService.BANCO_AZTECA_OBTAIN_ADVISER_ROLE), jsonRequestData, loanApplication.getId(),headers,urlParams);
        String errorMessage = String.format("BANCO AZTECA: \n Mensaje: ERROR AL OBTENER ROL CONSEJERO\n LOAN : %s", loanApplication.getId());
        if(webServiceResponse.getRestResponse() != null && JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(),"Code", null) != null && JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(),"Code", null).equals(0)){
            if(JsonUtil.getJsonObjectFromJson(webServiceResponse.getRestResponse(),"Data", null) != null){
                RolConsejero rolConsejero = new Gson().fromJson(JsonUtil.getJsonObjectFromJson(webServiceResponse.getRestResponse(),"Data", null).toString(), RolConsejero.class);
                webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANCO_AZTECA_OBTAIN_ADVISER_ROLE, rolConsejero != null ? new Gson().toJson(rolConsejero) : null);
                return rolConsejero;
            }
            else {
                sendError = true;
            }
        }
        else {
            sendError = true;
        }

        if(sendError){
            errorService.sendErrorCriticSlack(errorMessage);
            sendErrorAndRegister(webServiceResponse, EntityWebService.BANCO_AZTECA_OBTAIN_ADVISER_ROLE);
        }

        return null;
    }

    private void sendErrorAndRegister(EntityWebServiceLog<JSONObject> webServiceResponse, int entityWsId) throws IOException, MessagingException {
        EntityWebService entityWebService = catalogService.getEntityWebService(webServiceResponse != null ? webServiceResponse.getEntityWebServiceId() : entityWsId);
        if(webServiceResponse != null && webServiceResponse.getLoanApplicationId() != null) errorEntityDao.addEntityError(webServiceResponse.getLoanApplicationId(),entityWebService.getEntityId(), entityWebService.getId(), webServiceResponse != null ? (
                        webServiceResponse.getRestResponse() != null && JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(),"Message", null) != null ? JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(),"Message", null) : "-"
                ): "", webServiceResponse.getId());
        if(!Arrays.asList(EntityWebService.BANCO_AZTECA_CAMPAIGN_LOGIN).contains(entityWebService.getId())) sendErrorBPeople(entityWebService.getWbeserviceName(), webServiceResponse != null ? webServiceResponse.getRequest() : "-", webServiceResponse != null ? webServiceResponse.getResponse() : "-",
                webServiceResponse.getRestResponse() != null && JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(),"Message", null) != null ? JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(),"Message", null) : "-",
                webServiceResponse.getRestResponse() != null && JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(),"Code", null) != null ? JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(),"Code", null).toString() : "-"
        );
        else sendErrorBPeople(entityWebService.getWbeserviceName(), "-", webServiceResponse.getResponse(),
                webServiceResponse.getRestResponse() != null && JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(),"Message", null) != null ? JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(),"Message", null) : "-",
                webServiceResponse.getRestResponse() != null && JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(),"Code", null) != null ? JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(),"Code", null).toString() : "-"
        );
    }

    private void sendErrorBPeople(String serviceName, String request, String response, String errorDetail, String errorCode) throws MessagingException, IOException {
        String bodyMessage = String.format("No se pudo finalizar la conexión con el servicio: <br/> Detalles: <br/><br/>Error Code:  %s  <br/><br/> Error Message: %s <br/><br/> Request: %s <br/><br/> Response: %s <br/><br/>",
                errorCode,
                errorDetail,
                request,
                response);
        String subject = String.format("Error en conexión Solven al servicio %s  Alfin",serviceName);
        awsSesEmailService.sendRawEmail(
                null,
                "notificaciones@solven.pe",
                null,
                "mesadeayuda@abancodigital.atlassian.net",
                Entity.AZTECA_NOTIFICATION_LIST,
                subject,
                bodyMessage,
                bodyMessage,
                null,
                null, null, null, null);
    }

    @Override
    public List<BancoAztecaGatewayApi>  getPersonRecoveryCampaigns(LoanApplication loanApplication, Person person, String token) throws Exception {
        if(person == null){
            person = personDAO.getPerson(loanApplication.getPersonId(),false,Configuration.getDefaultLocale());
        }
        if(token == null){
            JSONObject loginResponse = login(loanApplication);
            token = loginResponse.getJSONObject("Data").getString("Token");
        }
        JSONObject request = new JSONObject();
        request.put("TipoDocumento",person.getDocumentType().getId().toString());
        request.put("NroDocumento",person.getDocumentNumber());
        String jsonRequestData = request.toString();
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of("Authorization", "Bearer "+token));

        Map<String, String> urlParams = new HashMap<>();
        for (String keyStr : request.keySet()) {
            urlParams.put(keyStr,request.getString(keyStr));
        }
        boolean sendError = false;
        EntityWebServiceLog<JSONObject> webServiceResponse = bancoAztecaUtilCall.callGetMethod(catalogService.getEntityWebService(EntityWebService.BANCO_AZTECA_CAMPANIA_COBRANZA), jsonRequestData, loanApplication.getId(),headers,urlParams);
        String errorMessage = String.format("BANCO AZTECA CAMPAÑA COBRANZA: \n Mensaje: ERROR AL OBTENER CAMPAÑAS DE COBRANZA\n LOAN : %s", loanApplication.getId());
        if(webServiceResponse.getRestResponse() != null && JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(),"Code", null) != null && Arrays.asList(0,100).contains(JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(),"Code", null))){
            if(JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(),"Code", null).equals(100)) return null;
            if(JsonUtil.getJsonObjectFromJson(webServiceResponse.getRestResponse(),"Data", null) != null && JsonUtil.getJsonArrayFromJson(JsonUtil.getJsonObjectFromJson(webServiceResponse.getRestResponse(),"Data", null),"campanias",null) != null){
                JSONArray campaignsJson = JsonUtil.getJsonArrayFromJson(JsonUtil.getJsonObjectFromJson(webServiceResponse.getRestResponse(),"Data", null),"campanias",null);
                List<BancoAztecaGatewayApi> campaigns = new ArrayList<>();
                for (int i = 0; i < campaignsJson.length(); i++) {
                    campaigns.add(new Gson().fromJson(campaignsJson.getJSONObject(i).toString(), BancoAztecaGatewayApi.class));
                }
                webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANCO_AZTECA_CAMPANIA_COBRANZA, campaigns.isEmpty() ? null : new Gson().toJson(campaigns.get(0)));
                return campaigns;
            }
            else {
                sendError = true;
            }
        }
        else sendError = true;
        if(sendError){
            errorService.sendErrorCriticSlack(errorMessage);
            sendErrorAndRegister(webServiceResponse, EntityWebService.BANCO_AZTECA_CAMPANIA_COBRANZA);
        }
        return null;
    }

}
