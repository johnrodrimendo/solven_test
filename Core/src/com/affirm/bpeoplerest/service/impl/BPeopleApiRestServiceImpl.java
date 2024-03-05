package com.affirm.bpeoplerest.service.impl;

import com.affirm.bantotalrest.model.common.BTResponseData;
import com.affirm.bpeoplerest.model.BPeopleCrearUsuarioRequest;
import com.affirm.bpeoplerest.service.BPeopleApiRestService;
import com.affirm.bpeoplerest.util.BPeopleUtilCall;
import com.affirm.common.dao.*;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.transactional.EntityWebServiceLog;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.User;
import com.affirm.common.service.AwsSesEmailService;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.ErrorService;

import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Arrays;

@Service("bPeopleApiRestService")
public class BPeopleApiRestServiceImpl implements BPeopleApiRestService {

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private BPeopleUtilCall bPeopleUtilCall;
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
    public void bPeopleCrearUsuario(LoanApplication loanApplication, Person person, User user) throws Exception {

        Gson gson = new Gson();
        BPeopleCrearUsuarioRequest bPeopleCrearUsuarioRequest = new BPeopleCrearUsuarioRequest();
        bPeopleCrearUsuarioRequest.setDocumentType(person.getDocumentType().getId().toString());
        bPeopleCrearUsuarioRequest.setDocument(person.getDocumentNumber());
        bPeopleCrearUsuarioRequest.setCellphone("+" + loanApplication.getCountryId() + user.getPhoneNumber());
        bPeopleCrearUsuarioRequest.setEmail(user.getEmail());
        String jsonRequestData = gson.toJson(bPeopleCrearUsuarioRequest);

        EntityWebServiceLog<JSONObject> webServiceResponse = bPeopleUtilCall.call(catalogService.getEntityWebService(EntityWebService.BPEOPLE_CREACION_USUARIO), jsonRequestData, loanApplication.getId());
        String errorDetail = null;
        boolean sendError = false;

        if (webServiceResponse.getRestResponse() != null) {
            JSONObject msg = JsonUtil.getJsonObjectFromJson(webServiceResponse.getRestResponse(), "msg", null);
            if (msg != null) {
                String severity = JsonUtil.getStringFromJson(msg, "severity", null);
                Boolean success = JsonUtil.getBooleanFromJson(webServiceResponse.getRestResponse(), "success", null);
                if (severity != null && severity.equals("S") && success != null && success) {
                    return;
                }
                if (severity != null && severity.equals("E") && success != null && !success) {
                    sendError = true;
                    errorDetail = JsonUtil.getStringFromJson(msg, "message", null);
                    if(errorDetail != null && errorDetail.contains("Usuario ya existe")) sendError = false;
                }
            }
        }
        if(!sendError) errorService.sendErrorCriticSlack(String.format("ERROR BPEOPLE : \n Mensaje: NO SE PUDO GENERAR EL USUARIO\n LOAN : %s, Response: %s", loanApplication.getId(), webServiceResponse != null ? webServiceResponse.getResponse() : null));
        if(sendError) this.sendErrorAndRegister(webServiceResponse, errorDetail, EntityWebService.BPEOPLE_CREACION_USUARIO);
    }

    private void sendErrorAndRegister(EntityWebServiceLog webServiceLog, String errorDetail,  int entityWsId) throws IOException, MessagingException {
        EntityWebService entityWebService = catalogService.getEntityWebService(webServiceLog != null ? webServiceLog.getEntityWebServiceId() : entityWsId);
        errorService.sendErrorCriticSlack(String.format("ERROR ALFIN: \n Mensaje: NO SE PUDO GENERAR EL USUARIO %s \n LOAN : %s,  Response: %s",  entityWebService.getWbeserviceName(), webServiceLog != null ? webServiceLog.getLoanApplicationId() : "-", webServiceLog != null ? webServiceLog.getResponse() : "-"));
        if(webServiceLog != null && webServiceLog.getLoanApplicationId() != null) errorEntityDao.addEntityError(webServiceLog.getLoanApplicationId(),entityWebService.getEntityId(), entityWebService.getId(), errorDetail, webServiceLog.getId());
        sendErrorBantotal(entityWebService.getWbeserviceName(), webServiceLog != null ? webServiceLog.getRequest() : "-", webServiceLog != null ? webServiceLog.getResponse() : "-", errorDetail, "-");
    }

    private void sendErrorBantotal(String serviceName, String request, String response, String errorDetail, String errorCode) throws MessagingException, IOException {
        if(errorCode == null) errorCode = "-";
        if(errorDetail == null) errorDetail = "-";
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
}
