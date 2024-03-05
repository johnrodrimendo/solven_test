package com.affirm.bancoazteca.service.impl;

import com.affirm.bancoazteca.model.ReniecDataRequest;
import com.affirm.bancoazteca.model.ReniecDataResponse;
import com.affirm.bancoazteca.model.ReniecLoginRequest;
import com.affirm.bancoazteca.model.ReniecLoginResponse;
import com.affirm.bancoazteca.service.BancoAztecaReniecServiceCall;
import com.affirm.bancoazteca.util.BancoAztecaUtilCall;
import com.affirm.bantotalrest.exception.BTInvalidSessionException;
import com.affirm.bantotalrest.model.customs.BTCorresponsalesObtenerDetallePagoDeCuotaResponse;
import com.affirm.common.dao.*;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.transactional.EntityWebServiceLog;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.service.AwsSesEmailService;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.ErrorService;
import com.affirm.common.service.FileService;
import com.affirm.common.service.impl.FileServiceImpl;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.castor.core.util.Base64Decoder;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;

@Service("bancoAztecaReniecServiceCall")
public class BancoAztecaReniecServiceCallImpl implements BancoAztecaReniecServiceCall {

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
    @Autowired
    private FileService fileService;


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

    private void sendErrorAndRegister(EntityWebServiceLog<JSONObject> webServiceResponse, int entityWsId) throws IOException, MessagingException {
        EntityWebService entityWebService = catalogService.getEntityWebService(webServiceResponse != null ? webServiceResponse.getEntityWebServiceId() : entityWsId);
        if(webServiceResponse != null && webServiceResponse.getLoanApplicationId() != null) errorEntityDao.addEntityError(webServiceResponse.getLoanApplicationId(),entityWebService.getEntityId(), entityWebService.getId(), webServiceResponse != null ? (
                webServiceResponse.getRestResponse() != null && JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(),"Message", null) != null ? JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(),"Message", null) : "-"
        ): "", webServiceResponse.getId());
        if(!Arrays.asList(EntityWebService.BANCO_AZTECA_RENIEC_SERVICE_LOGIN).contains(entityWebService.getId())) sendErrorBPeople(entityWebService.getWbeserviceName(), webServiceResponse != null ? webServiceResponse.getRequest() : "-", webServiceResponse != null ? webServiceResponse.getResponse() : "-",
                webServiceResponse != null && webServiceResponse.getRestResponse() != null && JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(),"Message", null) != null ? JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(),"Message", null) : "-",
                webServiceResponse != null && webServiceResponse.getRestResponse() != null && JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(),"Code", null) != null ? JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(),"Code", null).toString() : "-"
        );
        else sendErrorBPeople(entityWebService.getWbeserviceName(), "-", webServiceResponse.getResponse(),
                webServiceResponse != null && webServiceResponse.getRestResponse() != null && JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(),"Message", null) != null ? JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(),"Message", null) : "-",
                webServiceResponse != null && webServiceResponse.getRestResponse() != null && JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(),"Code", null) != null ? JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(),"Code", null).toString() : "-"
        );
    }

    @Override
    public ReniecLoginResponse login(LoanApplication loanApplication) throws Exception {
        ReniecLoginRequest request = new ReniecLoginRequest();
        request.setUserLogin(System.getenv("BANCO_AZTECA_RENIEC_LOGIN"));
        request.setPassword(System.getenv("BANCO_AZTECA_RENIEC_PASSWORD"));
        request.setApplicationId(Integer.valueOf(System.getenv("BANCO_AZTECA_RENIEC_APP_ID")));
        String jsonLoginData = new Gson().toJson(request);
        String errorMessage = null;
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of("ApiKey", System.getenv("BANCO_AZTECA_RENIEC_API_KEY")));

        EntityWebServiceLog<JSONObject> webServiceResponse = bancoAztecaUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANCO_AZTECA_RENIEC_SERVICE_LOGIN), jsonLoginData, loanApplication.getId(),headers);
        if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("ALFIN RENIEC WS - SERVICE FAILED");
        ReniecLoginResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),ReniecLoginResponse.class);
        if(response != null && response.getCode().intValue() == 0 && response.getData() != null && response.getData().getSuccess()){
            return response;
        }
        errorMessage = String.format("ALFIN RENIEC : \n Mensaje: NO SE PUDO GENERAR TOKEN\n LOAN : %s, Response: SIN RESPUESTA", loanApplication.getId());
        errorService.sendErrorCriticSlack(errorMessage);
        sendErrorAndRegister(webServiceResponse, EntityWebService.BANCO_AZTECA_RENIEC_SERVICE_LOGIN);
        throw new Exception(errorMessage);
    }

    @Override
    public ReniecDataResponse getPersonData(LoanApplication loanApplication, Person person, String token) throws Exception {
        if(person == null){
            person = personDAO.getPerson(loanApplication.getPersonId(),false, Configuration.getDefaultLocale());
        }
        if(token == null){
            ReniecLoginResponse loginResponse = login(loanApplication);
            token = loginResponse.getData().getToken();
        }

        ReniecDataRequest request = new ReniecDataRequest();
        request.setNroDocumento(person.getDocumentNumber());
        request.setUsuarioConsultante(person.getFullName());
        String jsonRequestData = new Gson().toJson(request);
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of("Authorization", "Bearer "+token));

        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bancoAztecaUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANCO_AZTECA_RENIEC_SERVICE_OBTENER_DATA), jsonRequestData, loanApplication.getId(), headers);
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("ALFIN RENIEC WS - SERVICE FAILED");

            ReniecDataResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),ReniecDataResponse.class);
            if(response != null && response.getCode().intValue() == 0 && response.getData() != null && response.getData().getCodigoError().equalsIgnoreCase("0000")){
                //Change Foto field
                JSONObject jsonObject = webServiceResponse.getRestResponse();
                String fotoData = jsonObject.getJSONObject("Data").getString("Foto");
                if(fotoData != null && !fotoData.trim().isEmpty()){
                    Base64Decoder decoder = new org.castor.core.util.Base64Decoder();
                    byte[] imageByte = Base64Decoder.decode(fotoData.trim());
                    String urlFile = fileService.writeWebServiceFile(imageByte, loanApplication.getId(), String.format("%s_%s.png",
                            webServiceResponse.getId(),
                            new Date().getTime()));
                    jsonObject.getJSONObject("Data").put("Foto", urlFile);
                    response.getData().setFoto(urlFile);
                    webServiceDAO.updateEntityWebServiceLogResponse(webServiceResponse.getId(), jsonObject.toString());
                }


                webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANCO_AZTECA_RENIEC_SERVICE_OBTENER_DATA, new Gson().toJson(response));
                return response;
            }
            sendErrorAndRegister(webServiceResponse, EntityWebService.BANCO_AZTECA_RENIEC_SERVICE_OBTENER_DATA);
            return null;

        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, EntityWebService.BANCO_AZTECA_RENIEC_SERVICE_OBTENER_DATA);
            throw ex;
        }
    }


}
