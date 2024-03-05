package com.affirm.sentinel.rest;

import com.affirm.acceso.model.Login;
import com.affirm.banbif.model.KonectaLeadRequest;
import com.affirm.banbif.model.KonectaLeadResponse;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EntityWebServiceUtil;
import com.affirm.common.service.ErrorService;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class BanbifServiceCall {

    @Autowired
    private EntityWebServiceUtil entityWebServiceUtil;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private ErrorService errorService;

    public CrearCuestionarioTitularResponse callCreateQuestions(LoanApplication loanApplication) throws Exception {
        Person person = personDao.getPerson(loanApplication.getPersonId(), false, Configuration.getDefaultLocale());
        User user = userDao.getUser(loanApplication.getUserId());
        CrearCuestionarioTitularRequest request= new CrearCuestionarioTitularRequest();
        if(Configuration.hostEnvIsProduction()){
            request.setGx_UsuEnc("xxxxxxxxxxxxxxxxxxxxx==");
            request.setGx_PasEnc("xxxxxxxxxxxxxxxxxxxxx");
            request.setGx_Key("xxxxxxxxxxxxxxxxxxxxx");
        }
        else{
            request.setGx_UsuEnc("xxxxxxxxxxxxxxxxxxxxx==");
            request.setGx_PasEnc("xxxxxxxxxxxxxxxxxxxxx==");
            request.setGx_Key("xxxxxxxxxxxxxxxxxxxxx");
        }
        request.setTipoDoc(person.getDocumentType().getId() == IdentityDocumentType.DNI ? "D" : "4");
        request.setNroDoc(person.getDocumentNumber());
        request.setCodCue(7);
        request.setCelular(user.getPhoneNumber());

        EntityWebServiceLog<JSONObject> webServiceResponse = callRest(catalogService.getEntityWebService(EntityWebService.BANBIF_CREAR_CUESTIONARIO), new Gson().toJson(request), loanApplication.getId(), null);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();
        boolean sendError = true;
        if(jsonResponse != null){
            CrearCuestionarioTitularResponse response = new Gson().fromJson(jsonResponse.toString(), CrearCuestionarioTitularResponse.class);
            if(response.getCodigoWS().equals("0") && response.getResulVISO() == 0){
                return response;
            }
            if(response.getResulVISO() == 18 || response.getResulVISO() == 14){
                sendError = false;
            }
        }
        entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
        if(sendError) errorService.sendErrorCriticSlack(String.format("LOAN: %s \n ERROR BANBIF SERVICE CALL--> %s",loanApplication.getId(), (jsonResponse != null ? jsonResponse.toString() : "Sin respuesta")));
        throw new Exception();
    }

    public ResultadoEvaluacionTitularResponse callGetQuestionResult(LoanApplication loanApplication, Person person, ResultadoEvaluacionTitularRequest request) throws Exception {

        if(Configuration.hostEnvIsProduction()){
            request.setGx_UsuEnc("xxxxxxxxxxxxxxxxxxxxx==");
            request.setGx_PasEnc("xxxxxxxxxxxxxxxxxxxxx");
            request.setGx_Key("xxxxxxxxxxxxxxxxxxxxx");
        }
        else{
            request.setGx_UsuEnc("xxxxxxxxxxxxxxxxxxxxx==");
            request.setGx_PasEnc("xxxxxxxxxxxxxxxxxxxxx==");
            request.setGx_Key("xxxxxxxxxxxxxxxxxxxxx");
        }
        request.setTipoDoc(person.getDocumentType().getId() == IdentityDocumentType.DNI ? "D" : "4");
        request.setNroDoc(person.getDocumentNumber());
        request.setCodCue(7);
        request.setCodEva(request.getCodEva());

        EntityWebServiceLog<JSONObject> webServiceResponse = callRest(catalogService.getEntityWebService(EntityWebService.BANBIF_RESULTADO_CUESTIONARIO), new Gson().toJson(request), loanApplication.getId(), null);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();
        if(jsonResponse != null){
            ResultadoEvaluacionTitularResponse response = new Gson().fromJson(jsonResponse.toString(), ResultadoEvaluacionTitularResponse.class);
            if(response.getCodigoWS().equals("0") && Arrays.asList("0", "1", "7", "10", "11", "13", "22").contains(response.getViso_resultado().getCodError())){
                return response;
            }
        }
        entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
        return null;
    }

    public EntityWebServiceLog<JSONObject> callRest(EntityWebService entityWebService, String request, Integer loanApplicationId, Map<String, String> urlParams) throws Exception {
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of("content-type", "application/json"));

        EntityWebServiceLog<JSONObject> response = entityWebServiceUtil.callRestWs(entityWebService, request, headers, loanApplicationId, false, urlParams);
        response.setRestResponse(new JSONObject(response.getResponse()));
        return response;
    }

    public KonectaLeadResponse callKonectaLead(LoanApplication loanApplication, Person person, User user) throws Exception {

        BanbifPreApprovedBase preApprovedBase = new Gson().fromJson(loanApplication.getEntityCustomData().getJSONObject(LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey()).toString(),BanbifPreApprovedBase.class);

        KonectaLeadRequest request = new KonectaLeadRequest();
        request.setDni(person.getDocumentNumber());
        request.setApellidos(person.getFullSurnames());
        request.setNombres(person.getName());
        request.setTelefono(user.getPhoneNumber());
        request.setLinea(preApprovedBase.getLinea() + "");
        request.setTipotarjeta(preApprovedBase.getPlastico());

        EntityWebServiceLog<JSONObject> webServiceResponse = callRest(catalogService.getEntityWebService(EntityWebService.BANBIF_KONECTA_LEAD), new Gson().toJson(request), loanApplication.getId(), null);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();
        if(jsonResponse != null){
            KonectaLeadResponse response = new Gson().fromJson(jsonResponse.toString(), KonectaLeadResponse.class);
            if(response.getMsg() != null && response.getMsg().equalsIgnoreCase("OK")){
                return response;
            }
        }
        entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
        return null;
    }

}

