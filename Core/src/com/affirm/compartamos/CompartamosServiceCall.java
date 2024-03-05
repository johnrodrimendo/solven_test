package com.affirm.compartamos;

import com.affirm.common.dao.*;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.transactional.EntityWebServiceLog;
import com.affirm.common.model.transactional.WsClient;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EntityWebServiceUtil;
import com.affirm.common.util.JsonUtil;
import com.affirm.compartamos.model.*;
import com.affirm.compartamos.util.CompartamosUtilCall;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.time.Instant;

/**
 * Created by dev5 on 29/11/17.
 */
@Service
public class CompartamosServiceCall {

    @Autowired
    TranslatorDAO translatorDAO;
    @Autowired
    RestApiDAO restApiDAO;
    @Autowired
    WebServiceDAO webServiceDao;
    @Autowired
    EntityWebServiceUtil entityWebServiceUtil;
    @Autowired
    CompartamosUtilCall compartamosUtilCall;
    @Autowired
    CatalogService catalogService;
    @Autowired
    CreditDAO creditDAO;
    @Autowired
    LoanApplicationDAO loanApplicationDAO;

    private static final int wsClientId = 5;

    public VariablePreEvaluacionResponse callTraerVariablesPreevaluacion(VariablePreEvaluacionRequest variablePreEvaluacionRequest, Integer loanApplicationId) throws Exception{
        Gson gson = new Gson();
        String jsonTraerVariablesPreEvaluacion = gson.toJson(variablePreEvaluacionRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = compartamosUtilCall.call(catalogService.getEntityWebService(EntityWebService.COMPARTAMOS_TRAER_VARIABLES_PREEVALUACION), jsonTraerVariablesPreEvaluacion, authHeader("/CreditosSolven/rest/GestionCreditos/TraerVariablesPreEvaluacion"), loanApplicationId);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        int codigoError = JsonUtil.getIntFromJson(jsonResponse, "pcCodErr", null);
        if(codigoError == 0){
            VariablePreEvaluacionResponse variablePreEvaluacionResponse = gson.fromJson(jsonResponse.toString(), VariablePreEvaluacionResponse.class);
            if(loanApplicationId != null)
                webServiceDao.registerEntityWebServiceResult(loanApplicationId, EntityWebService.COMPARTAMOS_TRAER_VARIABLES_PREEVALUACION, new Gson().toJson(variablePreEvaluacionResponse));
            return variablePreEvaluacionResponse;
        }else{
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            String mensajeError = JsonUtil.getStringFromJson(jsonResponse, "pcMsjErr", null);
            System.out.println("[Compartamos Traer Variables PreEvaluacion] - [" + codigoError + "] : " + mensajeError);
            throw new Exception();
        }
    }

    public Boolean callConsultarVariablesEvaluacion(Cliente cliente, Integer loanApplicationId) throws Exception {
        Gson gson = new Gson();
        String jsonConsultarVariablesEvaluacion = gson.toJson(cliente);
        EntityWebServiceLog<JSONObject> webServiceResponse = compartamosUtilCall.call(catalogService.getEntityWebService(EntityWebService.COMPARTAMOS_CONSULTAR_VARIABLES_EVALUACION), jsonConsultarVariablesEvaluacion, authHeader("/CreditosSolven/rest/GestionCreditos/ConsultarVariablesEvaluacion"), loanApplicationId);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        int codigoError = JsonUtil.getIntFromJson(jsonResponse, "pcCodErr", null);
        if(codigoError == 0){
            return true;
        }else{
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            String mensajeError = JsonUtil.getStringFromJson(jsonResponse, "pcMsjErr", null);
            System.out.println("[Compartamos Consultar Variables Evaluacion] - [" + codigoError + "] : " + mensajeError);
            throw new Exception();
        }
    }

    public Cliente callTraerVariablesEvaluacion(DocumentoIdentidad documentoIdentidad, Integer loanApplicationId) throws Exception {
        Gson gson = new Gson();
        String jsonTraerVariablesEvaluacion = gson.toJson(documentoIdentidad);
        EntityWebServiceLog<JSONObject> webServiceResponse = compartamosUtilCall.call(catalogService.getEntityWebService(EntityWebService.COMPARTAMOS_TRAER_VARIABLES_EVALUACION), jsonTraerVariablesEvaluacion, authHeader("/CreditosSolven/rest/GestionCreditos/TraerVariablesEvaluacion"), loanApplicationId);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        int codigoError = JsonUtil.getIntFromJson(jsonResponse, "pcCodErr", null);
        if(codigoError == 0){
            Cliente cliente = gson.fromJson(JsonUtil.getJsonObjectFromJson(jsonResponse, "poCliente", null).toString(), Cliente.class);
            Cliente conyugue = gson.fromJson(JsonUtil.getJsonObjectFromJson(jsonResponse, "poConyuge", null).toString(), Cliente.class);
            cliente.setConyugue(conyugue);
            if(loanApplicationId != null)
                webServiceDao.registerEntityWebServiceResult(loanApplicationId, EntityWebService.COMPARTAMOS_TRAER_VARIABLES_EVALUACION, jsonResponse.toString());
            return cliente;
        }else{
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            String mensajeError = JsonUtil.getStringFromJson(jsonResponse, "pcMsjErr", null);
            System.out.println("[Compartamos Traer Variables Evaluacion] - [" + codigoError + "] : " + mensajeError);
            throw new Exception();
        }
    }

    public GenerarCreditoResponse callGeneracionCredito(GenerarCreditoRequest generarCreditoRequest, Integer loanApplicationId, Integer creditId) throws Exception{
        Gson gson = new Gson();
        String jsonGenerarCredito = gson.toJson(generarCreditoRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = compartamosUtilCall.call(catalogService.getEntityWebService(EntityWebService.COMPARTAMOS_GENERAR_CREDITO), jsonGenerarCredito, authHeader("/CreditosSolven/rest/GestionCreditos/GeneracionCreditoSolven"), loanApplicationId);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        int codigoError = JsonUtil.getIntFromJson(jsonResponse, "pcCodErr", null);
        if(codigoError == 0){
            GenerarCreditoResponse generarCreditoResponse = gson.fromJson(jsonResponse.toString(), GenerarCreditoResponse.class);
            creditDAO.updateCrediCodeByCreditId(creditId, generarCreditoResponse.getCredito().getCuenta());
            return generarCreditoResponse;
        }else{
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            String mensajeError = JsonUtil.getStringFromJson(jsonResponse, "pcMsjErr", null);
            System.out.println("[Compartamos Generar Credito Solven] - [" + codigoError + "] : " + mensajeError);
            throw new Exception();
        }
    }


    public String authHeader(String path) throws Exception {
        long requestUnixTime = Instant.now().getEpochSecond();
        WsClient wsClient = restApiDAO.getWsClientByWsClient(wsClientId);
        String signature = wsClient.getApiKeySharedKey() + "=" + encodeSignature(wsClient.getApiKeySecret(), wsClient.getApiKeySharedKey() + requestUnixTime + path) + "=" + requestUnixTime;
        return signature;
    }

    private String encodeSignature(String secret, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return URLEncoder.encode(org.apache.commons.codec.binary.Base64.encodeBase64String(sha256_HMAC.doFinal(data.getBytes("UTF-8"))), "UTF-8");
    }

}
