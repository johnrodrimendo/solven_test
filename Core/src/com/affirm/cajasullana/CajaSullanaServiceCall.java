package com.affirm.cajasullana;

import com.affirm.cajasullana.model.*;
import com.affirm.cajasullana.util.CajaSullanaUtilCall;
import com.affirm.common.dao.*;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.transactional.EntityWebServiceLog;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EntityWebServiceUtil;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dev5 on 29/11/17.
 */
@Service
public class CajaSullanaServiceCall {

    @Autowired
    TranslatorDAO translatorDAO;
    @Autowired
    RestApiDAO restApiDAO;
    @Autowired
    WebServiceDAO webServiceDao;
    @Autowired
    EntityWebServiceUtil entityWebServiceUtil;
    @Autowired
    CajaSullanaUtilCall cajaSullanaUtilCall;
    @Autowired
    CatalogService catalogService;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    LoanApplicationDAO loanApplicationDAO;
    @Autowired
    CreditDAO creditDAO;

    public AdmisibilidadResponse callConsultarAdmisibilidad(AdmisibilidadRequest admisibilidadRequest, Integer loanApplicationId) throws Exception{
        Gson gson = new Gson();
        String jsonConsultarAdmisibilidad = gson.toJson(admisibilidadRequest);
        EntityWebService entityWebService = catalogService.getEntityWebService(EntityWebService.CAJASULLANA_ADMISIBILIDAD);
        EntityWebServiceLog<JSONObject> webServiceResponse = cajaSullanaUtilCall.call(entityWebService, jsonConsultarAdmisibilidad, authHeader(entityWebService), loanApplicationId);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        String codigoError = JsonUtil.getStringFromJson(jsonResponse, "status", null);
        if(codigoError.equals("00") || codigoError.equals("07") || codigoError.equals("01") || codigoError.equals("08") ||
                codigoError.equals("94") || codigoError.equals("96")){
            AdmisibilidadResponse admisibilidadResponse = gson.fromJson(jsonResponse.toString(), AdmisibilidadResponse.class);
            if(loanApplicationId != null){
                webServiceDao.registerEntityWebServiceResult(loanApplicationId, EntityWebService.CAJASULLANA_ADMISIBILIDAD, new Gson().toJson(admisibilidadResponse));
                if(codigoError.equals("00") && admisibilidadResponse.getCodigoCliente() != null){
                    LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale());
                    personDAO.registerAssociated(loanApplication.getPersonId(), Entity.CAJASULLANA, admisibilidadResponse.getCodigoCliente(), null);
                }
            }
            String mensajeError = JsonUtil.getStringFromJson(jsonResponse, "message", null);
            System.out.println("[Caja Sullana Consultar Admisibilidad] - [" + codigoError + "] : " + mensajeError);
            return admisibilidadResponse;
        }else{
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            String mensajeError = JsonUtil.getStringFromJson(jsonResponse, "message", null);
            System.out.println("[Caja Sullana Consultar Admisibilidad] - [" + codigoError + "] : " + mensajeError);
            return null;
        }
    }

    public ValidarExperianResponse callValidarExperian(ValidarExperianRequest validarExperianRequest, Integer loanApplicationId) throws Exception{
        Gson gson = new Gson();
        String jsonValidarExperian = gson.toJson(validarExperianRequest);
        EntityWebService entityWebService = catalogService.getEntityWebService(EntityWebService.CAJASULLANA_EXPERIAN);
        EntityWebServiceLog<JSONObject> webServiceResponse = cajaSullanaUtilCall.call(entityWebService, jsonValidarExperian, authHeader(entityWebService), loanApplicationId);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        String codigoError = JsonUtil.getStringFromJson(jsonResponse, "status", null);
        if(codigoError.equals("00")){
            ValidarExperianResponse validarExperianResponse = gson.fromJson(jsonResponse.toString(), ValidarExperianResponse.class);
            if(loanApplicationId != null){
                webServiceDao.registerEntityWebServiceResult(loanApplicationId, EntityWebService.CAJASULLANA_EXPERIAN, new Gson().toJson(validarExperianResponse));
            }
            String mensajeError = JsonUtil.getStringFromJson(jsonResponse, "message", null);
            System.out.println("[Caja Sullana Validar Experian] - [" + codigoError + "] : " + mensajeError);
            return validarExperianResponse;
        }else{
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            String mensajeError = JsonUtil.getStringFromJson(jsonResponse, "message", null);
            System.out.println("[Caja Sullana Validar Experian] - [" + codigoError + "] : " + mensajeError);
            return null;
        }
    }

    public CreditosCancelarResponse callCancelarCreditos(CreditosCancelarRequest creditosCancelarRequest, Integer loanApplicationId) throws Exception{
        Gson gson = new Gson();
        String jsonCreditosCancelar = gson.toJson(creditosCancelarRequest);
        EntityWebService entityWebService = catalogService.getEntityWebService(EntityWebService.CAJASULLANA_CANCELAR_CREDITO);
        EntityWebServiceLog<JSONObject> webServiceResponse = cajaSullanaUtilCall.call(entityWebService, jsonCreditosCancelar, authHeader(entityWebService), loanApplicationId);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        String codigoError = JsonUtil.getStringFromJson(jsonResponse, "status", null);
        if(codigoError.equals("00")){
            CreditosCancelarResponse creditosCancelarResponse = gson.fromJson(jsonResponse.toString(), CreditosCancelarResponse.class);
            if(loanApplicationId != null){
                webServiceDao.registerEntityWebServiceResult(loanApplicationId, EntityWebService.CAJASULLANA_CANCELAR_CREDITO, new Gson().toJson(creditosCancelarResponse));
            }
            String mensajeError = JsonUtil.getStringFromJson(jsonResponse, "message", null);
            System.out.println("[Caja Sullana Creditos a Cancelar] - [" + codigoError + "] : " + mensajeError);
            return creditosCancelarResponse;
        }else{
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            String mensajeError = JsonUtil.getStringFromJson(jsonResponse, "message", null);
            System.out.println("[Caja Sullana Creditos a Cancelar] - [" + codigoError + "] : " + mensajeError);
            return null;
        }
    }


    public GenerarSolicitudResponse callGenerarSolcitud(GenerarSolicitudRequest generarSolicitudRequest, Integer loanApplicationId) throws Exception{
        Gson gson = new Gson();
        String jsonGenerarSolicitud = gson.toJson(generarSolicitudRequest);
        EntityWebService entityWebService = catalogService.getEntityWebService(EntityWebService.CAJASULLANA_GENERAR_SOLICITUD);
        EntityWebServiceLog<JSONObject> webServiceResponse = cajaSullanaUtilCall.call(entityWebService, jsonGenerarSolicitud, authHeader(entityWebService), loanApplicationId);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        String codigoError = JsonUtil.getStringFromJson(jsonResponse, "status", null);
        if(codigoError.equals("00")){
            GenerarSolicitudResponse generarSolicitudResponse = gson.fromJson(jsonResponse.toString(), GenerarSolicitudResponse.class);
            if(loanApplicationId != null){
                creditDAO.updateCrediCodeByCreditId(loanApplicationDAO.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale()).getCreditId(), generarSolicitudResponse.getNumeroTransaccion());
                webServiceDao.registerEntityWebServiceResult(loanApplicationId, EntityWebService.CAJASULLANA_GENERAR_SOLICITUD, new Gson().toJson(generarSolicitudResponse));
            }
            String mensajeError = JsonUtil.getStringFromJson(jsonResponse, "message", null);
            System.out.println("[Caja Sullana Generar Solicitud] - [" + codigoError + "] : " + mensajeError);
            return generarSolicitudResponse;
        }else{
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            String mensajeError = JsonUtil.getStringFromJson(jsonResponse, "message", null);
            System.out.println("[Caja Sullana Generar Solicitud] - [" + codigoError + "] : " + mensajeError);
            return null;
        }
    }

    private String authHeader(EntityWebService entityWebService) throws Exception {
        if(Configuration.hostEnvIsProduction()) return entityWebService.getProductionSecurityKey();
        else return entityWebService.getSandboxSecurityKey();
    }

}
