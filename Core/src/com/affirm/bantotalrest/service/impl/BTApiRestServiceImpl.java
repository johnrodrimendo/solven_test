package com.affirm.bantotalrest.service.impl;

import com.affirm.acceso.model.Direccion;
import com.affirm.bantotalrest.exception.BT40147Exception;
import com.affirm.bantotalrest.exception.BTInvalidSessionException;
import com.affirm.bantotalrest.model.BantotalToken;
import com.affirm.bantotalrest.model.RBTPCO10.BTPersonasAgregarFATCARequest;
import com.affirm.bantotalrest.model.RBTPCO10.BTPersonasAgregarFATCAResponse;
import com.affirm.bantotalrest.model.RBTPCO12.BTPersonasObtenerFATCARequest;
import com.affirm.bantotalrest.model.RBTPCO12.BTPersonasObtenerFATCAResponse;
import com.affirm.bantotalrest.model.RBTPG006.BTClientesObtenerCuentasAhorroRequest;
import com.affirm.bantotalrest.model.RBTPG006.BTClientesObtenerCuentasAhorroResponse;
import com.affirm.bantotalrest.model.RBTPG007.BTCuentasDeAhorroObtenerDatosRequest;
import com.affirm.bantotalrest.model.RBTPG007.BTCuentasDeAhorroObtenerDatosResponse;
import com.affirm.bantotalrest.model.RBTPG012.BTPrestamoObtenerDetalleRequest;
import com.affirm.bantotalrest.model.RBTPG012.BTPrestamoObtenerDetalleResponse;
import com.affirm.bantotalrest.model.RBTPG011.ObtenerPrestamosClienteRequest;
import com.affirm.bantotalrest.model.RBTPG011.ObtenerPrestamosClienteResponse;
import com.affirm.bantotalrest.model.RBTPG013.BTClientesObtenerPlazosFijosRequest;
import com.affirm.bantotalrest.model.RBTPG013.BTClientesObtenerPlazosFijosResponse;
import com.affirm.bantotalrest.model.RBTPG015.BTClientesObtenerTarjetasDebitoRequest;
import com.affirm.bantotalrest.model.RBTPG015.BTClientesObtenerTarjetasDebitoResponse;
import com.affirm.bantotalrest.model.RBTPG019.BTPersonasValidarEnListasNegrasRequest;
import com.affirm.bantotalrest.model.RBTPG019.BTPersonasValidarEnListasNegrasResponse;
import com.affirm.bantotalrest.model.RBTPG027.BTClientesCrearRequest;
import com.affirm.bantotalrest.model.RBTPG027.BTClientesCrearResponse;
import com.affirm.bantotalrest.model.RBTPG030.BTCuentasDeAhorroContratarProductoRequest;
import com.affirm.bantotalrest.model.RBTPG030.BTCuentasDeAhorroContratarProductoResponse;
import com.affirm.bantotalrest.model.RBTPG034.PagarCuotaRequest;
import com.affirm.bantotalrest.model.RBTPG034.PagarCuotaResponse;
import com.affirm.bantotalrest.model.RBTPG036.BTPersonasObtenerProfesionesResponse;
import com.affirm.bantotalrest.model.RBTPG042.BTConfiguracionBantotalObtenerActividadesRequest;
import com.affirm.bantotalrest.model.RBTPG042.BTConfiguracionBantotalObtenerActividadesResponse;
import com.affirm.bantotalrest.model.RBTPG054.BTCuentasDeAhorroObtenerProductosRequest;
import com.affirm.bantotalrest.model.RBTPG054.BTCuentasDeAhorroObtenerProductosResponse;
import com.affirm.bantotalrest.model.RBTPG066.BTTarjetasDeDebitoCrearRequest;
import com.affirm.bantotalrest.model.RBTPG066.BTTarjetasDeDebitoCrearResponse;
import com.affirm.bantotalrest.model.RBTPG072.BTPrestamoObtenerCronogramaRequest;
import com.affirm.bantotalrest.model.RBTPG072.BTPrestamosObtenerCronogramaResponse;
import com.affirm.bantotalrest.model.RBTPG075.BTPrestamosSimularRequest;
import com.affirm.bantotalrest.model.RBTPG075.BTPrestamosSimularResponse;
import com.affirm.bantotalrest.model.RBTPG077.BTPrestamosContratarRequest;
import com.affirm.bantotalrest.model.RBTPG077.BTPrestamosContratarResponse;
import com.affirm.bantotalrest.model.RBTPG079.BTConfiguracionBantotalObtenerPizarrasRequest;
import com.affirm.bantotalrest.model.RBTPG079.BTConfiguracionBantotalObtenerPizarrasResponse;
import com.affirm.bantotalrest.model.RBTPG085.BTPersonasObtenerRequest;
import com.affirm.bantotalrest.model.RBTPG085.BTPersonasObtenerResponse;
import com.affirm.bantotalrest.model.RBTPG100.BTCuentasDeAhorroContratarConFacultadesRequest;
import com.affirm.bantotalrest.model.RBTPG100.BTCuentasDeAhorroContratarConFacultadesResponse;
import com.affirm.bantotalrest.model.RBTPG146.BTPersonasAgregarDatosPEPRequest;
import com.affirm.bantotalrest.model.RBTPG146.BTPersonasAgregarDatosPEPResponse;
import com.affirm.bantotalrest.model.RBTPG182.ObtenerCuentasClienteRequest;
import com.affirm.bantotalrest.model.RBTPG182.ObtenerCuentasClienteResponse;
import com.affirm.bantotalrest.model.RBTPG211.BTClientesObtenerCuentaClienteRequest;
import com.affirm.bantotalrest.model.RBTPG211.BTClientesObtenerCuentaClienteResponse;
import com.affirm.bantotalrest.model.RBTPG218.BTClientesCrearConPersonaExistenteRequest;
import com.affirm.bantotalrest.model.RBTPG218.BTClientesCrearConPersonaExistenteResponse;
import com.affirm.bantotalrest.model.RBTPG220.ObtenerIdentificadorUnicoRequest;
import com.affirm.bantotalrest.model.RBTPG220.ObtenerIdentificadorUnicoResponse;
import com.affirm.bantotalrest.model.RBTPG265.BTPrestamosSimularAmortizableSinClienteRequest;
import com.affirm.bantotalrest.model.RBTPG292.BTPersonasObtenerDatosPEPRequest;
import com.affirm.bantotalrest.model.RBTPG292.BTPersonasObtenerDatosPEPResponse;
import com.affirm.bantotalrest.model.RBTPG328.BTPersonasActualizarProfesionRequest;
import com.affirm.bantotalrest.model.RBTPG328.BTPersonasActualizarProfesionResponse;
import com.affirm.bantotalrest.model.authentication.AuthenticationRequest;
import com.affirm.bantotalrest.model.authentication.AuthenticationResponse;
import com.affirm.bantotalrest.model.common.*;
import com.affirm.bantotalrest.model.customs.*;
import com.affirm.bantotalrest.model.customs.BPBAZServicesGenerarCCIRequest;
import com.affirm.bantotalrest.model.customs.BPBAZServicesGenerarCCIResponse;
import com.affirm.bantotalrest.model.customs.BPBAZServicesObtieneCCIRequest;
import com.affirm.bantotalrest.model.customs.BPBAZServicesObtieneCCIResponse;
import com.affirm.bantotalrest.service.BTApiRestService;
import com.affirm.bantotalrest.util.BantotalUtilCall;
import com.affirm.common.dao.*;
import com.affirm.common.dao.impl.TranslatorDAOImpl;
import com.affirm.common.model.EntityError;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.catalog.Ubigeo;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.AwsSesEmailService;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.ErrorService;
import com.affirm.common.util.JsonUtil;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.security.model.EntityWsResult;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.shiro.session.InvalidSessionException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service("bTApiRestService")
public class BTApiRestServiceImpl implements BTApiRestService {

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private BantotalUtilCall bantotalUtilCall;
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
    private ExternalDAO externalDAO;
    @Autowired
    private SecurityDAO securityDAO;
    @Autowired
    private ErrorEntityDao errorEntityDao;


    public static final String BT_DATE_FORMAT  = "yyyy-MM-dd";
    public static final String BT_HOUR_FORMAT  = "HH:mm:ss";
    public static final int DEFAULT_ACTIVITY_ID = 999999;

    //OBTENER DETALLE PAGO
    @Override
    public BTCorresponsalesObtenerDetallePagoDeCuotaResponse obtenerDetallePagoDeCuota(LoanApplication loanApplication, Long transactionId, String token) throws Exception {
        Integer count = 0;
        try{
            return obtenerDetallePagoDeCuota(loanApplication, transactionId, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return obtenerDetallePagoDeCuota(loanApplication, transactionId, token, count+1);
        }
    }

    private BTCorresponsalesObtenerDetallePagoDeCuotaResponse obtenerDetallePagoDeCuota(LoanApplication loanApplication,  Long transactionId, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        BTCorresponsalesObtenerDetallePagoDeCuotaRequest request = getCommonRequestByEntity(entityId, BTCorresponsalesObtenerDetallePagoDeCuotaRequest.class);
        request.getBtinreq().setToken(token);
        request.setIdTrans(transactionId);
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_DETALLE_PAGO_CUOTA), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTCorresponsalesObtenerDetallePagoDeCuotaResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTCorresponsalesObtenerDetallePagoDeCuotaResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_DETALLE_PAGO_CUOTA);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_DETALLE_PAGO_CUOTA, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_DETALLE_PAGO_CUOTA);
            throw ex;
        }
    }

    @Override
    public AuthenticationResponse authenticateRequest(Integer entityId) throws Exception {
        Gson gson = new Gson();
        AuthenticationRequest authenticationRequest = getCommonRequestByEntity(entityId,AuthenticationRequest.class);
        authenticationRequest.setUserid(System.getenv("BANTOTAL_AZTECA_USERID"));
        authenticationRequest.setUserpassword(new String(Base64.getDecoder().decode(System.getenv("BANTOTAL_AZTECA_USERPASSWORD"))));
        String jsonLoginData = gson.toJson(authenticationRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try{
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_AUTHENTICATION), jsonLoginData, null);
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                AuthenticationResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),AuthenticationResponse.class);
                if(response == null || response.getSessionToken()  == null || (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_AUTHENTICATION);
                    throw new Exception(String.format("ERROR BANTOTAL : \n Mensaje: NO SE PUDO GENERAR TOKEN\n, Response: %s",  new Gson().toJson(response.getErroresnegocio())));
                }
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_AUTHENTICATION);
            throw ex;
        }
    }

    public String getToken(Integer entityId) throws Exception {
        BantotalToken bantotalToken = externalDAO.getBantotalToken();
        if(bantotalToken == null){
            return generateAndSaveToken(entityId);
        }
        return bantotalToken != null ? bantotalToken.getToken() : null;
    }

    private String generateAndSaveToken(Integer entityId) throws Exception {
        AuthenticationResponse authenticationResponse = authenticateRequest(entityId);
        externalDAO.insertBantotalToken(authenticationResponse.getSessionToken());
        return authenticationResponse.getSessionToken();
    }

    @Override
    public ObtenerDireccionPersonaBcoAztecaResponse btObtenerDireccionPersonaBcoAztecaResponse(LoanApplication loanApplication,Person person, Integer doCod, String token) throws Exception {
        Integer count = 0;
        try{
            return btObtenerDireccionPersonaBcoAztecaResponse(loanApplication, person, doCod, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btObtenerDireccionPersonaBcoAztecaResponse(loanApplication, person, doCod, token, count+1);
        }
    }

    private ObtenerDireccionPersonaBcoAztecaResponse btObtenerDireccionPersonaBcoAztecaResponse(LoanApplication loanApplication, Person person, Integer doCod, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        ObtenerDireccionPersonaBcoAztecaRequest request = getCommonRequestByEntity(entityId,ObtenerDireccionPersonaBcoAztecaRequest.class);
        request.getBtinreq().setToken(token);
        String documentTypeId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_DOCUMENT_TYPE_ID, person.getDocumentType().getId().toString(),null);
        String documentCountryId =translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_COUNTRY_ID, person.getCountry().getId().toString(),null);
        request.setPais(Integer.valueOf(documentCountryId));
        request.setPetdoc(Integer.valueOf(documentTypeId));
        request.setPendoc(person.getDocumentNumber());
        request.setDocod(doCod);
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_DIRECCION_PERSONA), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                ObtenerDireccionPersonaBcoAztecaResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),ObtenerDireccionPersonaBcoAztecaResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty() && !existsError(response.getErroresnegocio().getBTErrorNegocio(), Arrays.asList(ObtenerDireccionCuentaBcoAztecaResponse.DIRECCION_NO_EXISTE)))){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_DIRECCION_PERSONA);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_DIRECCION_PERSONA, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_DIRECCION_PERSONA);
            throw ex;
        }
    }

    @Override
    public ObtenerDireccionCuentaBcoAztecaResponse btObtenerDireccionCuentaBcoAztecaResponse(LoanApplication loanApplication, String cuentaBT, Integer doCod, String token) throws Exception {
        Integer count = 0;
        try{
            return btObtenerDireccionCuentaBcoAztecaResponse(loanApplication, cuentaBT, doCod, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btObtenerDireccionCuentaBcoAztecaResponse(loanApplication, cuentaBT, doCod, token, count+1);
        }
    }

    private ObtenerDireccionCuentaBcoAztecaResponse btObtenerDireccionCuentaBcoAztecaResponse(LoanApplication loanApplication, String cuentaBT, Integer doCod, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        ObtenerDireccionCuentaBcoAztecaRequest request = getCommonRequestByEntity(entityId,ObtenerDireccionCuentaBcoAztecaRequest.class);
        request.getBtinreq().setToken(token);
        if(cuentaBT != null){
            request.setCuentaBT(cuentaBT);
        }
        else{
            EntityWsResult savingAccountDetail = securityDAO.getEntityResultWS(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_DIRECCION_CUENTA);
            if(savingAccountDetail != null && savingAccountDetail.getResult() != null){
                BTClientesObtenerCuentaClienteResponse btCuentasDeAhorroObtenerDatosResponse = new Gson().fromJson(savingAccountDetail.getResult().toString(), BTClientesObtenerCuentaClienteResponse.class);
                request.setCuentaBT(btCuentasDeAhorroObtenerDatosResponse.getCuentaBT().toString());
            }
        }
        request.setDocod(doCod);
        request.setPgcod(1);
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_DIRECCION_CUENTA), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                ObtenerDireccionCuentaBcoAztecaResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(), ObtenerDireccionCuentaBcoAztecaResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty() && !existsError(response.getErroresnegocio().getBTErrorNegocio(), Arrays.asList(ObtenerDireccionCuentaBcoAztecaResponse.DIRECCION_NO_EXISTE)))){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_DIRECCION_CUENTA);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_DIRECCION_CUENTA, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_DIRECCION_CUENTA);
            throw ex;
        }
    }

    //INHABILITAR

    @Override
    public InhabilitarDireccionPersonaBcoAztecaResponse btInhabilitarDireccionPersonaBcoAztecaResponse(LoanApplication loanApplication,Person person, Integer doCod, String token) throws Exception {
        Integer count = 0;
        try{
            return btInhabilitarDireccionPersonaBcoAztecaResponse(loanApplication, person, doCod, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btInhabilitarDireccionPersonaBcoAztecaResponse(loanApplication, person, doCod, token, count+1);
        }
    }

    private InhabilitarDireccionPersonaBcoAztecaResponse btInhabilitarDireccionPersonaBcoAztecaResponse(LoanApplication loanApplication, Person person, Integer doCod, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        ObtenerDireccionPersonaBcoAztecaRequest request = getCommonRequestByEntity(entityId,ObtenerDireccionPersonaBcoAztecaRequest.class);
        request.getBtinreq().setToken(token);
        String documentTypeId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_DOCUMENT_TYPE_ID, person.getDocumentType().getId().toString(),null);
        String documentCountryId =translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_COUNTRY_ID, person.getCountry().getId().toString(),null);
        request.setPais(Integer.valueOf(documentCountryId));
        request.setPetdoc(Integer.valueOf(documentTypeId));
        request.setPendoc(person.getDocumentNumber());
        request.setDocod(doCod);
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_INHABILITAR_DIRECCION_PERSONA), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                InhabilitarDireccionPersonaBcoAztecaResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),InhabilitarDireccionPersonaBcoAztecaResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty() && !existsError(response.getErroresnegocio().getBTErrorNegocio(), Arrays.asList(InhabilitarDireccionPersonaBcoAztecaResponse.DIRECCION_NO_EXISTE)))){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_INHABILITAR_DIRECCION_PERSONA);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_INHABILITAR_DIRECCION_PERSONA, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_INHABILITAR_DIRECCION_PERSONA);
            throw ex;
        }
    }

    @Override
    public InhabilitarDireccionCuentaBcoAztecaResponse btInhabilitarDireccionCuentaBcoAztecaResponse(LoanApplication loanApplication, String cuentaBT, Integer doCod, String token) throws Exception {
        Integer count = 0;
        try{
            return btInhabilitarDireccionCuentaBcoAztecaResponse(loanApplication, cuentaBT, doCod, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btInhabilitarDireccionCuentaBcoAztecaResponse(loanApplication, cuentaBT, doCod, token, count+1);
        }
    }

    private InhabilitarDireccionCuentaBcoAztecaResponse btInhabilitarDireccionCuentaBcoAztecaResponse(LoanApplication loanApplication, String cuentaBT, Integer doCod, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        InhabilitarDireccionCuentaBcoAztecaRequest request = getCommonRequestByEntity(entityId,InhabilitarDireccionCuentaBcoAztecaRequest.class);
        request.getBtinreq().setToken(token);
        if(cuentaBT != null){
            request.setCuentaBT(cuentaBT);
        }
        else{
            EntityWsResult savingAccountDetail = securityDAO.getEntityResultWS(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_INHABILITAR_DIRECCION_CUENTA);
            if(savingAccountDetail != null && savingAccountDetail.getResult() != null){
                BTClientesObtenerCuentaClienteResponse btCuentasDeAhorroObtenerDatosResponse = new Gson().fromJson(savingAccountDetail.getResult().toString(), BTClientesObtenerCuentaClienteResponse.class);
                request.setCuentaBT(btCuentasDeAhorroObtenerDatosResponse.getCuentaBT().toString());
            }
        }
        request.setDocod(doCod);
        request.setPgcod(1);
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_INHABILITAR_DIRECCION_CUENTA), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                InhabilitarDireccionCuentaBcoAztecaResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(), InhabilitarDireccionCuentaBcoAztecaResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty() && !existsError(response.getErroresnegocio().getBTErrorNegocio(), Arrays.asList(InhabilitarDireccionCuentaBcoAztecaResponse.DIRECCION_NO_EXISTE)))){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_INHABILITAR_DIRECCION_CUENTA);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_INHABILITAR_DIRECCION_CUENTA, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_INHABILITAR_DIRECCION_CUENTA);
            throw ex;
        }
    }


    @Override
    public ActualizarDireccionPersonaBcoAztecaResponse btActualizarDireccionPersona(LoanApplication loanApplication,Person person, ActualizarDireccionPersonaBcoAztecaRequest requestBase, Integer doCod, String token) throws Exception {
        Integer count = 0;
        try{
            return btActualizarDireccionPersona(loanApplication, person, requestBase, doCod, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btActualizarDireccionPersona(loanApplication, person, requestBase, doCod, token, count+1);
        }
    }

    private ActualizarDireccionPersonaBcoAztecaResponse btActualizarDireccionPersona(LoanApplication loanApplication, Person person, ActualizarDireccionPersonaBcoAztecaRequest requestBase, Integer doCod, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        ActualizarDireccionPersonaBcoAztecaRequest request = getCommonRequestByEntity(entityId,ActualizarDireccionPersonaBcoAztecaRequest.class);
        request.getBtinreq().setToken(token);
        String documentTypeId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_DOCUMENT_TYPE_ID, person.getDocumentType().getId().toString(),null);
        String documentCountryId =translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_COUNTRY_ID, person.getCountry().getId().toString(),null);
        request.setPais(documentCountryId);
        request.setPetdoc(documentTypeId);
        request.setPendoc(person.getDocumentNumber());
        request.setDocod(doCod);
        request.setUbigeo(requestBase.getUbigeo());
        request.setPaisDom(requestBase.getPaisDom());
        request.setNivel1(requestBase.getNivel1());
        request.setNivelDesc1(requestBase.getNivelDesc1());
        request.setNivel2(requestBase.getNivel2());
        request.setNivelDesc2(requestBase.getNivelDesc2());
        request.setNivel3(requestBase.getNivel3());
        request.setNivelDesc3(requestBase.getNivelDesc3());
        request.setNivel4(requestBase.getNivel4());
        request.setNivelDesc4(requestBase.getNivelDesc4());
        //request.setNivel5(requestBase.getNivel5());
        //request.setNivelDesc5(requestBase.getNivelDesc5());
        //request.setNivel6(requestBase.getNivel6());
        //request.setNivelDesc6(requestBase.getNivelDesc6());
        request.setReferencia(requestBase.getReferencia());
        request.setTipoVivC(request.getTipoVivC());
        request.setResDesde(requestBase.getResDesde());

        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_ACTUALIZAR_DIRECCION_PERSONA), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                ActualizarDireccionPersonaBcoAztecaResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),ActualizarDireccionPersonaBcoAztecaResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_ACTUALIZAR_DIRECCION_PERSONA);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_ACTUALIZAR_DIRECCION_PERSONA, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_ACTUALIZAR_DIRECCION_PERSONA);
            throw ex;
        }
    }

    @Override
    public ActualizarDireccionCuentaBcoAztecaResponse btActualizarDireccionCuenta(LoanApplication loanApplication,Person person,String cuentaBT, ActualizarDireccionCuentaBcoAztecaRequest requestBase, Integer doCod, String token) throws Exception {
        Integer count = 0;
        try{
            return btActualizarDireccionCuenta(loanApplication, person, cuentaBT, requestBase, doCod, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btActualizarDireccionCuenta(loanApplication, person, cuentaBT, requestBase, doCod, token, count+1);
        }
    }

    private ActualizarDireccionCuentaBcoAztecaResponse btActualizarDireccionCuenta(LoanApplication loanApplication, Person person,String cuentaBT, ActualizarDireccionCuentaBcoAztecaRequest requestBase, Integer doCod, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        ActualizarDireccionCuentaBcoAztecaRequest request = getCommonRequestByEntity(entityId,ActualizarDireccionCuentaBcoAztecaRequest.class);
        request.getBtinreq().setToken(token);
        String documentTypeId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_DOCUMENT_TYPE_ID, person.getDocumentType().getId().toString(),null);
        String documentCountryId =translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_COUNTRY_ID, person.getCountry().getId().toString(),null);
        request.setPgcod(1);
        if(cuentaBT != null){
            request.setCuentaBT(cuentaBT);
        }
        else{
            EntityWsResult savingAccountDetail = securityDAO.getEntityResultWS(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_ACTUALIZAR_DIRECCION_CUENTA);
            if(savingAccountDetail != null && savingAccountDetail.getResult() != null){
                BTClientesObtenerCuentaClienteResponse btCuentasDeAhorroObtenerDatosResponse = new Gson().fromJson(savingAccountDetail.getResult().toString(), BTClientesObtenerCuentaClienteResponse.class);
                request.setCuentaBT(btCuentasDeAhorroObtenerDatosResponse.getCuentaBT().toString());
            }
        }
        request.setDocod(doCod);
        request.setUbigeo(requestBase.getUbigeo());
        request.setPaisDom(requestBase.getPaisDom());
        request.setNivel1(requestBase.getNivel1());
        request.setNivelDesc1(requestBase.getNivelDesc1());
        request.setNivel2(requestBase.getNivel2());
        request.setNivelDesc2(requestBase.getNivelDesc2());
        request.setNivel3(requestBase.getNivel3());
        request.setNivelDesc3(requestBase.getNivelDesc3());
        request.setNivel4(requestBase.getNivel4());
        request.setNivelDesc4(requestBase.getNivelDesc4());
        //request.setNivel5(requestBase.getNivel5());
        //request.setNivelDesc5(requestBase.getNivelDesc5());
        //request.setNivel6(requestBase.getNivel6());
        //request.setNivelDesc6(requestBase.getNivelDesc6());
        request.setReferencia(requestBase.getReferencia());
        request.setTipoVivC(request.getTipoVivC());
        request.setResDesde(requestBase.getResDesde());

        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_ACTUALIZAR_DIRECCION_CUENTA), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                ActualizarDireccionCuentaBcoAztecaResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),ActualizarDireccionCuentaBcoAztecaResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_ACTUALIZAR_DIRECCION_CUENTA);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_ACTUALIZAR_DIRECCION_CUENTA, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_ACTUALIZAR_DIRECCION_CUENTA);
            throw ex;
        }
    }


    public <T extends ActualizarDireccionCommonBcoAztecaRequest> T crearActualizarDireccionCommonBcoAztecaRequest(LoanApplication loanApplication, Person person, User user, Integer doCod, Direccion direccion,  Class<T> returnType) throws Exception {
        T requestObject = returnType.getConstructor().newInstance();

        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        String documentTypeId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_DOCUMENT_TYPE_ID, person.getDocumentType().getId().toString(),null);
        String documentCountryId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_COUNTRY_ID, person.getCountry().getId().toString(),null);

        requestObject.setDocod(doCod);
        requestObject.setPaisDom(documentCountryId);

        if(direccion == null) direccion = personDAO.getDisggregatedAddress(person.getId(), doCod == ObtenerDireccionPersonaBcoAztecaRequest.DOMICILIO_VIVIENDA ? "H" : (doCod == ObtenerDireccionPersonaBcoAztecaRequest.DOMICILIO_LABORAL ? "J" : null));
        if (direccion != null) {
            requestObject.setReferencia(direccion.getReferencia());
            String zoneId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_ZONE_ID, direccion.getTipoZona().toString(),null);
            String streetTypeId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_STREET_TYPE_ID, direccion.getTipoVia().toString(),null);
            requestObject.setUbigeo(direccion.getUbigeoInei());
            requestObject.setNivel1(zoneId);
            requestObject.setNivelDesc1(direccion.getNombreZona());
            requestObject.setNivel2(streetTypeId);
            requestObject.setNivelDesc2(direccion.getNombreVia());
            requestObject.setNivel3("31");//NUMERO
            requestObject.setNivelDesc3(direccion.getNumeroVia() != null ? direccion.getNumeroVia() : null);
            requestObject.setNivel4("32");//NUMERO INTERIOR
            requestObject.setNivelDesc4(direccion.getNumeroInterior() != null ? direccion.getNumeroInterior() : "0");
            Ubigeo ubigeo = catalogService.getUbigeo(direccion.getUbigeo());
            if(ubigeo != null){
                String departmentId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_DEPARTMENT_ID, ubigeo.getDepartmentUbigeoId(),null);;
                String localityId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_PROVINCE_ID, ubigeo.getProvinceUbigeoId(),null);
            }
        }

        return requestObject;
    }

    @Override
    public boolean existsError(List<BtError> btErrors, List<String> codes){
        if(btErrors == null || codes == null) return false;
        return btErrors.stream().anyMatch(e -> e.getCodigo() != null && codes.contains(e.getCodigo()));
    }


    //OBTENER ID PERSONA

    @Override
    public ObtenerIdentificadorUnicoResponse obtenerIdentificadorUnico(LoanApplication loanApplication, Person person, String token) throws Exception {
        Integer count = 0;
        try{
            return obtenerIdentificadorUnico(loanApplication,person,token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return obtenerIdentificadorUnico(loanApplication,person,token, count+1);
        }
    }

    private ObtenerIdentificadorUnicoResponse obtenerIdentificadorUnico(LoanApplication loanApplication, Person person, String token, Integer retryCount) throws Exception {
        if(person == null){
            person = personDAO.getPerson(loanApplication.getPersonId(),false,Configuration.getDefaultLocale());
        }
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        ObtenerIdentificadorUnicoRequest obtenerIdentificadorUnicoRequest = getCommonRequestByEntity(entityId,ObtenerIdentificadorUnicoRequest.class);
        String documentTypeId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_DOCUMENT_TYPE_ID, person.getDocumentType().getId().toString(),null);
        String documentCountryId =translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_COUNTRY_ID, person.getCountry().getId().toString(),null);
        obtenerIdentificadorUnicoRequest.getBtinreq().setToken(token);
        obtenerIdentificadorUnicoRequest.setNumeroDocumento(person.getDocumentNumber());
        obtenerIdentificadorUnicoRequest.setTipoDocumentoId(documentTypeId != null ? Integer.valueOf(documentTypeId) : null);
        obtenerIdentificadorUnicoRequest.setPaisDocumentoId(documentCountryId != null ? Integer.valueOf(documentCountryId) : null);
        String jsonRequestData = gson.toJson(obtenerIdentificadorUnicoRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_IDENTIFICADOR_UNICO), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                ObtenerIdentificadorUnicoResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),ObtenerIdentificadorUnicoResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response != null && response.getPersonaUId() != null && (response.getErroresnegocio() == null || response.getErroresnegocio().getBTErrorNegocio() == null || response.getErroresnegocio().getBTErrorNegocio().isEmpty())) webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_IDENTIFICADOR_UNICO, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_IDENTIFICADOR_UNICO);
            throw ex;
        }
    }

    //OBTENER PERSONA

    @Override
    public BTPersonasObtenerResponse btPersonasObtener(LoanApplication loanApplication, Long personaUId, String token) throws Exception {
        Integer count = 0;
        try{
            return btPersonasObtener(loanApplication,personaUId,token,count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btPersonasObtener(loanApplication,personaUId,token,count+1);
        }
    }

    private BTPersonasObtenerResponse btPersonasObtener(LoanApplication loanApplication, Long personaUId, String token, Integer retryCount) throws Exception {
        if(personaUId == null) throw new Exception("Error en BtPersonasObtener, parámetro personaUId inválidp");
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        BTPersonasObtenerRequest btPersonasObtenerRequest = getCommonRequestByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),BTPersonasObtenerRequest.class);
        btPersonasObtenerRequest.getBtinreq().setToken(token);
        btPersonasObtenerRequest.setPersonaUId(personaUId);

        String jsonRequestData = gson.toJson(btPersonasObtenerRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_PERSONA), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTPersonasObtenerResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTPersonasObtenerResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio() != null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_PERSONA);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_PERSONA, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_PERSONA);
            throw ex;
        }
    }

    //OBTENER CUENTAS CLIENTE

    @Override
    public ObtenerCuentasClienteResponse obtenerCuentasCliente(LoanApplication loanApplication, Long personaUId, String token) throws Exception {
        Integer count = 0;
        try{
            return obtenerCuentasCliente(loanApplication,personaUId,token,count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return obtenerCuentasCliente(loanApplication,personaUId,token,count+1);
        }
    }

    private ObtenerCuentasClienteResponse obtenerCuentasCliente(LoanApplication loanApplication, Long personaUId, String token, Integer retryCount) throws Exception {
        if(personaUId == null) throw new Exception("Error en obtenerCuentasCliente, parámetro personaUId inválidp");
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        ObtenerCuentasClienteRequest obtenerCuentasClienteRequest = getCommonRequestByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),ObtenerCuentasClienteRequest.class);
        obtenerCuentasClienteRequest.getBtinreq().setToken(token);
        obtenerCuentasClienteRequest.setPersonaUId(personaUId);

        String jsonRequestData = gson.toJson(obtenerCuentasClienteRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_CUENTAS_CLIENTE), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                ObtenerCuentasClienteResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),ObtenerCuentasClienteResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_CUENTAS_CLIENTE);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_CUENTAS_CLIENTE, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_CUENTAS_CLIENTE);
            throw ex;
        }
    }

    //OBTENER CCI
    @Override
    public BPBAZServicesGenerarCCIResponse bpBAZServicesGenerarCCI(LoanApplication loanApplication, Long operacionUID, String token) throws Exception {
        Integer count = 0;
        try{
            return bpBAZServicesGenerarCCI(loanApplication, operacionUID, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return bpBAZServicesGenerarCCI(loanApplication, operacionUID, token, count+1);
        }
    }

    private BPBAZServicesGenerarCCIResponse bpBAZServicesGenerarCCI(LoanApplication loanApplication,  Long operacionUID, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        BPBAZServicesGenerarCCIRequest request = getCommonRequestByEntity(entityId,BPBAZServicesGenerarCCIRequest.class);
        request.getBtinreq().setToken(token);
        request.setOperacionUID(operacionUID);
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_GENERAR_Y_OBTENER_CCI), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BPBAZServicesGenerarCCIResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BPBAZServicesGenerarCCIResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_GENERAR_Y_OBTENER_CCI);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_GENERAR_Y_OBTENER_CCI, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_GENERAR_Y_OBTENER_CCI);
            throw ex;
        }
    }

    //OBTENER CUENTA DE AHORRO
    @Override
    public BTClientesObtenerCuentasAhorroResponse BtClientesObtenerCuentasAhorro(LoanApplication loanApplication, Long clienteUId, String token) throws Exception {
        Integer count = 0;
        try{
            return BtClientesObtenerCuentasAhorro(loanApplication, clienteUId, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return BtClientesObtenerCuentasAhorro(loanApplication, clienteUId, token, count+1);
        }
    }

    private BTClientesObtenerCuentasAhorroResponse BtClientesObtenerCuentasAhorro(LoanApplication loanApplication, Long clienteUId, String token, Integer retryCount) throws Exception {
        if(clienteUId == null) throw new Exception("Error en btClientesObtenerCuentasAhorro, parámetro clienteUId inválidp");
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        BTClientesObtenerCuentasAhorroRequest btClientesObtenerCuentasAhorroRequest = getCommonRequestByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),BTClientesObtenerCuentasAhorroRequest.class);
        btClientesObtenerCuentasAhorroRequest.getBtinreq().setToken(token);
        btClientesObtenerCuentasAhorroRequest.setClienteUId(clienteUId);

        String jsonRequestData = gson.toJson(btClientesObtenerCuentasAhorroRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_CUENTAS_AHORRO), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTClientesObtenerCuentasAhorroResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTClientesObtenerCuentasAhorroResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_CUENTAS_AHORRO);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_CUENTAS_AHORRO, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_CUENTAS_AHORRO);
            throw ex;
        }
    }

    //OBTENER PRODUCTOS DE CUENTA DE AHORRO

    @Override
    public BTCuentasDeAhorroObtenerProductosResponse btCuentasDeAhorroObtenerProductos(LoanApplication loanApplication, String token) throws Exception {
        Integer count = 0;
        try{
            return btCuentasDeAhorroObtenerProductos(loanApplication,token,count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btCuentasDeAhorroObtenerProductos(loanApplication,token,count+1);
        }
    }

    private BTCuentasDeAhorroObtenerProductosResponse btCuentasDeAhorroObtenerProductos(LoanApplication loanApplication, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        BTCuentasDeAhorroObtenerProductosRequest btCuentasDeAhorroObtenerProductosRequest = getCommonRequestByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),BTCuentasDeAhorroObtenerProductosRequest.class);
        btCuentasDeAhorroObtenerProductosRequest.getBtinreq().setToken(token);

        String jsonRequestData = gson.toJson(btCuentasDeAhorroObtenerProductosRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_PRODUCTOS_CUENTAS_AHORRO), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTCuentasDeAhorroObtenerProductosResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTCuentasDeAhorroObtenerProductosResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_PRODUCTOS_CUENTAS_AHORRO);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_PRODUCTOS_CUENTAS_AHORRO, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_PRODUCTOS_CUENTAS_AHORRO);
            throw ex;
        }
    }

    //CONTRATAR DE CUENTA DE AHORRO
    @Override
    public BTCuentasDeAhorroContratarProductoResponse btCuentasDeAhorroContratarProducto(LoanApplication loanApplication, String token, Long clienteUId, Long productoUId, String nombreSubCuenta) throws Exception {
        Integer count = 0;
        try{
            return btCuentasDeAhorroContratarProducto(loanApplication,token,clienteUId,productoUId,nombreSubCuenta,count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btCuentasDeAhorroContratarProducto(loanApplication,token,clienteUId,productoUId,nombreSubCuenta,count+1);
        }
    }

    private BTCuentasDeAhorroContratarProductoResponse btCuentasDeAhorroContratarProducto(LoanApplication loanApplication, String token,Long clienteUId,Long productoUId,String nombreSubCuenta, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        BTCuentasDeAhorroContratarProductoRequest btCuentasDeAhorroContratarProductoRequest = getCommonRequestByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),BTCuentasDeAhorroContratarProductoRequest.class);
        btCuentasDeAhorroContratarProductoRequest.getBtinreq().setToken(token);
        btCuentasDeAhorroContratarProductoRequest.setProductoUId(productoUId);
        btCuentasDeAhorroContratarProductoRequest.setNombreSubCuenta(nombreSubCuenta);
        btCuentasDeAhorroContratarProductoRequest.setClienteUId(clienteUId);

        String jsonRequestData = gson.toJson(btCuentasDeAhorroContratarProductoRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_CONTRATAR_PRODUCTO_CUENTAS_AHORRO), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTCuentasDeAhorroContratarProductoResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTCuentasDeAhorroContratarProductoResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_CONTRATAR_PRODUCTO_CUENTAS_AHORRO);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_CONTRATAR_PRODUCTO_CUENTAS_AHORRO, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_CONTRATAR_PRODUCTO_CUENTAS_AHORRO);
            throw ex;
        }
    }

    //OBTENER CREDITOS

    @Override
    public BTCorresponsalesConsultaCreditosResponse BTCorresponsalesConsultaCreditos(LoanApplication loanApplication, Person person, String token) throws Exception {
        Integer count = 0;
        try{
            return BTCorresponsalesConsultaCreditos(loanApplication,person, token,count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return BTCorresponsalesConsultaCreditos(loanApplication,person,  token,count+1);
        }
    }

    private BTCorresponsalesConsultaCreditosResponse BTCorresponsalesConsultaCreditos(LoanApplication loanApplication, Person person,  String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        BTCorresponsalesConsultaCreditosRequest btCorresponsalesConsultaCreditosRequest = getCommonRequestByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),BTCorresponsalesConsultaCreditosRequest.class);
        btCorresponsalesConsultaCreditosRequest.getBtinreq().setToken(token);
        btCorresponsalesConsultaCreditosRequest.setMoneda(1); //SOLES
        btCorresponsalesConsultaCreditosRequest.setCantidadCuotas(999); //TODAS LAS CUOTAS
        btCorresponsalesConsultaCreditosRequest.setDocumento(person.getDocumentNumber());
        String documentTypeId = translatorDAO.translate(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(), TranslatorDAOImpl.BANTOTAL_DOCUMENT_TYPE_ID, person.getDocumentType().getId().toString(),null);
        btCorresponsalesConsultaCreditosRequest.setTipoDocumento(Integer.valueOf(documentTypeId));
        String jsonRequestData = gson.toJson(btCorresponsalesConsultaCreditosRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_CONSULTA_CREDITOS_CUOTAS_POR_DNI), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTCorresponsalesConsultaCreditosResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTCorresponsalesConsultaCreditosResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_CONSULTA_CREDITOS_CUOTAS_POR_DNI);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_CONSULTA_CREDITOS_CUOTAS_POR_DNI, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_CONSULTA_CREDITOS_CUOTAS_POR_DNI);
            throw ex;
        }
    }
    
    //SIMULAR CON CLIENTE
    @Override
    public BTPrestamosSimularResponse btPrestamosSimularResponse(LoanApplication loanApplication, String token, BTPrestamosSimularRequest.SBTPrestamoAlta sbtPrestamoAlta) throws BT40147Exception, Exception {
        Integer count = 0;
        try{
            return btPrestamosSimularResponse(loanApplication,token,sbtPrestamoAlta,count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btPrestamosSimularResponse(loanApplication,token,sbtPrestamoAlta,count+1);
        }
    }

    private BTPrestamosSimularResponse btPrestamosSimularResponse(LoanApplication loanApplication, String token, BTPrestamosSimularRequest.SBTPrestamoAlta sbtPrestamoAlta, Integer retryCount) throws BT40147Exception, Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        BTPrestamosSimularRequest btPrestamosSimularRequest = getCommonRequestByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),BTPrestamosSimularRequest.class);
        btPrestamosSimularRequest.getBtinreq().setToken(token);
        btPrestamosSimularRequest.setSdtPrestamo(sbtPrestamoAlta);

        String jsonRequestData = gson.toJson(btPrestamosSimularRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_PRESTAMO_SIMULAR_CON_CLIENTE), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTPrestamosSimularResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTPrestamosSimularResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_PRESTAMO_SIMULAR_CON_CLIENTE);
                    if(response != null && response.getErroresnegocio() != null && response.getErroresnegocio().getBTErrorNegocio() != null && response.getErroresnegocio().getBTErrorNegocio().stream().anyMatch( e -> Arrays.asList(BT40147Exception.FECHA_INVALIDA_CODIGO).contains(e.getCodigo()))){
                        throw new BT40147Exception("Fecha invalida");
                    }
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_PRESTAMO_SIMULAR_CON_CLIENTE, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_PRESTAMO_SIMULAR_CON_CLIENTE);
            throw ex;
        }
    }

    //SIMULAR SIN CLIENTE
    @Override
    public BTPrestamosSimularResponse btPrestamosSimularSinCliente(LoanApplication loanApplication, String token, BTPrestamosSimularAmortizableSinClienteRequest.SBTDatosAmortizable sdtDatosAmortizable) throws BT40147Exception, Exception {
        Integer count = 0;
        try{
            return btPrestamosSimularSinCliente(loanApplication,token,sdtDatosAmortizable,count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btPrestamosSimularSinCliente(loanApplication,token,sdtDatosAmortizable,count+1);
        }
    }

    private BTPrestamosSimularResponse btPrestamosSimularSinCliente(LoanApplication loanApplication, String token, BTPrestamosSimularAmortizableSinClienteRequest.SBTDatosAmortizable sdtDatosAmortizable, Integer retryCount) throws BT40147Exception, Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        BTPrestamosSimularAmortizableSinClienteRequest btPrestamosSimularRequest = getCommonRequestByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),BTPrestamosSimularAmortizableSinClienteRequest.class);
        btPrestamosSimularRequest.getBtinreq().setToken(token);
        btPrestamosSimularRequest.setSdtDatosAmortizable(sdtDatosAmortizable);

        String jsonRequestData = gson.toJson(btPrestamosSimularRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_PRESTAMO_SIMULAR_SIN_CLIENTE), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTPrestamosSimularResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTPrestamosSimularResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_PRESTAMO_SIMULAR_SIN_CLIENTE);
                    if(response != null && response.getErroresnegocio() != null && response.getErroresnegocio().getBTErrorNegocio() != null && response.getErroresnegocio().getBTErrorNegocio().stream().anyMatch( e -> Arrays.asList(BT40147Exception.FECHA_INVALIDA_CODIGO).contains(e.getCodigo()))){
                        throw new BT40147Exception("Fecha invalida");
                    }
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_PRESTAMO_SIMULAR_SIN_CLIENTE, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (BT40147Exception ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_PRESTAMO_SIMULAR_SIN_CLIENTE);
            throw ex;
        }
    }

    //CREAR CLIENTE CON PERSONA EXISTENTE
    @Override
    public BTClientesCrearConPersonaExistenteResponse btClientesCrearConPersonaExistente(LoanApplication loanApplication, String token, Long personaUId, Integer sectorId, Integer clasificacionInternaId, Integer ejecutivoId) throws Exception {
        Integer count = 0;
        try{
            return btClientesCrearConPersonaExistente(loanApplication,token,personaUId,sectorId,clasificacionInternaId,ejecutivoId,count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btClientesCrearConPersonaExistente(loanApplication,token,personaUId,sectorId,clasificacionInternaId,ejecutivoId,count+1);
        }
    }

    private BTClientesCrearConPersonaExistenteResponse btClientesCrearConPersonaExistente(LoanApplication loanApplication, String token,Long personaUId, Integer sectorId, Integer clasificacionInternaId,Integer ejecutivoId, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        BTClientesCrearConPersonaExistenteRequest btClientesCrearConPersonaExistenteRequest = getCommonRequestByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),BTClientesCrearConPersonaExistenteRequest.class);
        btClientesCrearConPersonaExistenteRequest.getBtinreq().setToken(token);
        btClientesCrearConPersonaExistenteRequest.setPersonaUId(personaUId);
        btClientesCrearConPersonaExistenteRequest.setSectorId(sectorId);
        btClientesCrearConPersonaExistenteRequest.setClasificacionInternaId(clasificacionInternaId);
        btClientesCrearConPersonaExistenteRequest.setEjecutivoId(ejecutivoId);

        String jsonRequestData = gson.toJson(btClientesCrearConPersonaExistenteRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_CREAR_CLIENTE_CON_PERSONA), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTClientesCrearConPersonaExistenteResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTClientesCrearConPersonaExistenteResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_CREAR_CLIENTE_CON_PERSONA);
                    throw new Exception(String.format("ERROR BANTOTAL AZTECA : \n Mensaje: ERROR btClientesCrearConPersonaExistente \n LOAN : %s,  Response: %s", loanApplication.getId() != null ? loanApplication.getId() : "-", new Gson().toJson(response.getErroresnegocio())));
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_CREAR_CLIENTE_CON_PERSONA, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_CREAR_CLIENTE_CON_PERSONA);
            throw ex;
        }
    }

    //CREAR CLIENTE Y PERSONA
    @Override
    public BTClientesCrearResponse btClientesCrear(LoanApplication loanApplication, String token, SBTPersona1 sBTPersona1) throws Exception {
        Integer count = 0;
        try{
            return btClientesCrear(loanApplication,token,sBTPersona1,count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btClientesCrear(loanApplication,token,sBTPersona1,count+1);
        }
    }

    private BTClientesCrearResponse btClientesCrear(LoanApplication loanApplication, String token, SBTPersona1 sBTPersona1, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        BTClientesCrearRequest btClientesCrearRequest = getCommonRequestByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),BTClientesCrearRequest.class);
        btClientesCrearRequest.getBtinreq().setToken(token);
        btClientesCrearRequest.setSdtPersona(sBTPersona1);

        String jsonRequestData = gson.toJson(btClientesCrearRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_CREAR_CLIENTE_Y_PERSONA), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTClientesCrearResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTClientesCrearResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio() != null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_CREAR_CLIENTE_Y_PERSONA);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_CREAR_CLIENTE_Y_PERSONA, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_CREAR_CLIENTE_Y_PERSONA);
            throw ex;
        }
    }

    //CONTRATAR PRESTAMO
    @Override
    public BTPrestamosContratarResponse btPrestamosContratar(LoanApplication loanApplication, String token, Long operacionUId, Long clienteUId, Long operacionUId_desembolso, Long operacionUId_cobro) throws Exception {
        Integer count = 0;
        try{
            return btPrestamosContratar(loanApplication,token,operacionUId,clienteUId,operacionUId_desembolso,operacionUId_cobro,count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btPrestamosContratar(loanApplication,token,operacionUId,clienteUId,operacionUId_desembolso,operacionUId_cobro,count+1);
        }
    }

    private BTPrestamosContratarResponse btPrestamosContratar(LoanApplication loanApplication, String token, Long operacionUId, Long clienteUId, Long operacionUId_desembolso, Long operacionUId_cobro, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        BTPrestamosContratarRequest btPrestamosContratarRequest = getCommonRequestByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),BTPrestamosContratarRequest.class);
        btPrestamosContratarRequest.getBtinreq().setToken(token);
        btPrestamosContratarRequest.setOperacionUId(operacionUId);
        btPrestamosContratarRequest.setClienteUId(clienteUId);
        btPrestamosContratarRequest.setOperacionUId_cobro(operacionUId_cobro);
        btPrestamosContratarRequest.setOperacionUId_desembolso(operacionUId_desembolso);

        String jsonRequestData = gson.toJson(btPrestamosContratarRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_CONTRATAR_PRESTAMO), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTPrestamosContratarResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTPrestamosContratarResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_CONTRATAR_PRESTAMO);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_CONTRATAR_PRESTAMO, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_CONTRATAR_PRESTAMO);
            throw ex;
        }
    }

    //OBTENER PIZARRAS
    @Override
    public BTConfiguracionBantotalObtenerPizarrasResponse btConfiguracionBantotalObtenerPizarras(LoanApplication loanApplication, String token) throws Exception {
        Integer count = 0;
        try{
            return btConfiguracionBantotalObtenerPizarras(loanApplication,token,count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btConfiguracionBantotalObtenerPizarras(loanApplication,token,count+1);
        }
    }

    private BTConfiguracionBantotalObtenerPizarrasResponse btConfiguracionBantotalObtenerPizarras(LoanApplication loanApplication, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        BTConfiguracionBantotalObtenerPizarrasRequest btPrestamosContratarRequest = getCommonRequestByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),BTConfiguracionBantotalObtenerPizarrasRequest.class);
        btPrestamosContratarRequest.getBtinreq().setToken(token);

        String jsonRequestData = gson.toJson(btPrestamosContratarRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_PIZARRAS), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTConfiguracionBantotalObtenerPizarrasResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTConfiguracionBantotalObtenerPizarrasResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_PIZARRAS);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_PIZARRAS, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_PIZARRAS);
            throw ex;
        }
    }

    //OBTENER ACTIVIDADES
    @Override
    public BTConfiguracionBantotalObtenerActividadesResponse btConfiguracionBantotalObtenerActividades(LoanApplication loanApplication, String token) throws Exception {
        Integer count = 0;
        try{
            return btConfiguracionBantotalObtenerActividades(loanApplication,token,count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btConfiguracionBantotalObtenerActividades(loanApplication,token,count+1);
        }
    }

    private BTConfiguracionBantotalObtenerActividadesResponse btConfiguracionBantotalObtenerActividades(LoanApplication loanApplication, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        BTConfiguracionBantotalObtenerActividadesRequest btConfiguracionBantotalObtenerActividadesRequest = getCommonRequestByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),BTConfiguracionBantotalObtenerActividadesRequest.class);
        btConfiguracionBantotalObtenerActividadesRequest.getBtinreq().setToken(token);

        String jsonRequestData = gson.toJson(btConfiguracionBantotalObtenerActividadesRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_ACTIVIDADES), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTConfiguracionBantotalObtenerActividadesResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTConfiguracionBantotalObtenerActividadesResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_ACTIVIDADES);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_ACTIVIDADES, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_ACTIVIDADES);
            throw ex;
        }
    }

    private <T extends BtRequestData> T getCommonRequestByEntity(int entityId,  Class<T> returnType) throws Exception {
        T requestObject = returnType.getConstructor().newInstance();
        if(requestObject.getBtinreq() == null) requestObject.setBtinreq(new BtInRequest());
        switch (entityId){
            case Entity.AZTECA:
                requestObject.getBtinreq().setCanal(System.getenv("BANTOTAL_AZTECA_CANAL"));
                requestObject.getBtinreq().setDevice(System.getenv("BANTOTAL_AZTECA_DEVICE"));
                requestObject.getBtinreq().setUsuario(System.getenv("BANTOTAL_AZTECA_USUARIO"));
        }
        return requestObject;
    }

    @Override
    public String convertDateToStringFormat(Date date){
        if(date != null){
            return new SimpleDateFormat(BT_DATE_FORMAT).format(date);
        }
        return null;
    }

    @Override
    public Date convertStringToDate(String date) throws ParseException {
        if(date != null){
            return new SimpleDateFormat(BT_DATE_FORMAT).parse(date);
        }
        return null;
    }

    @Override
    public Date convertStringWithHourToDate(String date) throws ParseException {
        if(date != null){
            return new SimpleDateFormat(BT_DATE_FORMAT+" "+BT_HOUR_FORMAT).parse(date);
        }
        return null;
    }

    @Override
    public String convertDateToHourStringFormat(Date date){
        if(date != null){
            return new SimpleDateFormat(BT_HOUR_FORMAT).format(date);
        }
        return null;
    }

    @Override
    public SBTPersona1 createSBTPersona1Object(LoanApplication loanApplication, Person person, User user) throws Exception {

        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        String documentTypeId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_DOCUMENT_TYPE_ID, person.getDocumentType().getId().toString(),null);
        String documentCountryId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_COUNTRY_ID, person.getCountry().getId().toString(),null);
        String maritalStatusId = person.getMaritalStatus() != null ? translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_MARITAL_STATUS_ID, person.getMaritalStatus().getId().toString(),null) : null;
        String occupationId = person.getProfessionOccupationId() != null ? translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_OCCUPATION_ID, person.getProfessionOccupationId().toString(),null) : null;

        SBTPersona1 sbtPersona1 = new SBTPersona1();
        sbtPersona1.setPaisDocumentoId(documentCountryId != null ? Integer.valueOf(documentCountryId) : null);
        sbtPersona1.setTipoDocumentoId(documentTypeId != null ? Integer.valueOf(documentTypeId) : null);
        sbtPersona1.setNroDocumento(person.getDocumentNumber());
        sbtPersona1.setPrimerNombre(person.getFirstName());
        sbtPersona1.setSegundoNombre(person.getOtherNames());
        sbtPersona1.setPrimerApellido(person.getFirstSurname());
        sbtPersona1.setSegundoApellido(person.getLastSurname());
        sbtPersona1.setPaisDomicilioId(documentCountryId != null ? Integer.valueOf(documentCountryId) : null);
        sbtPersona1.setClasificacionInternaId(1);
        sbtPersona1.setSectorId(1);
        sbtPersona1.setEstadoCivilId(maritalStatusId);
        sbtPersona1.setSexo(person.getGender().toString());
        sbtPersona1.setFechaNacimiento(person.getBirthday() != null ? convertDateToStringFormat(person.getBirthday()) : null);
        sbtPersona1.setTelefonoCelular(user.getPhoneNumber());
        sbtPersona1.setCorreoElectronico(user.getEmail());
        Integer activityId = DEFAULT_ACTIVITY_ID;
        Integer activitySelected = JsonUtil.getIntFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.CUSTOM_ENTITY_ACTIVITY_ID.getKey(), null);
        if(activitySelected != null){
            CustomEntityActivity activity = catalogService.getCustomActivity(activitySelected);
            if(activity != null && activity.getIdentifier() != null) activityId = activity.getIdentifier();
        }
        sbtPersona1.setActividadLaboralId(activityId);
        sbtPersona1.setOcupacionId(occupationId != null ? Integer.valueOf(occupationId) : null);
        List<PersonOcupationalInformation> ocupations = personDAO.getPersonOcupationalInformation(Configuration.getDefaultLocale(), loanApplication.getPersonId());
        if (ocupations != null) {
            PersonOcupationalInformation principalOcupation = ocupations.stream().filter(o -> o.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst().orElse(null);
            if(principalOcupation != null && principalOcupation.getFixedGrossIncome() != null) sbtPersona1.setIngresos(principalOcupation.getFixedGrossIncome());
        }
        Direccion disaggregatedHomeAddress = personDAO.getDisggregatedAddress(person.getId(), "H");
        if (disaggregatedHomeAddress != null) {
            Ubigeo ubigeo = catalogService.getUbigeo(disaggregatedHomeAddress.getUbigeo());
           if(ubigeo != null){
               String departmentId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_DEPARTMENT_ID, ubigeo.getDepartmentUbigeoId(),null);;
               String localityId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_PROVINCE_ID, ubigeo.getProvinceUbigeoId(),null);;
               sbtPersona1.setDepartamentoId(departmentId != null ? Integer.valueOf(departmentId) : null);
               sbtPersona1.setLocalidadId(localityId != null ? Integer.valueOf(localityId) : null);
           }
           sbtPersona1.setCalle(disaggregatedHomeAddress.getNombreVia());
           if(disaggregatedHomeAddress.getNumeroInterior() != null) sbtPersona1.setNumeroPuerta(disaggregatedHomeAddress.getNumeroInterior());
        }
        return sbtPersona1;
    }

    //VALIDAR LISTAS NEGRAS
    @Override
    public BTPersonasValidarEnListasNegrasResponse validarEnListasNegras(LoanApplication loanApplication, Person person, String token) throws Exception {
        Integer count = 0;
        try{
            return validarEnListasNegras(loanApplication, person,token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return validarEnListasNegras(loanApplication, person,token, count+1);
        }
    }

    private BTPersonasValidarEnListasNegrasResponse validarEnListasNegras(LoanApplication loanApplication, Person person, String token, Integer retryCount) throws Exception {
        if(person == null){
            person = personDAO.getPerson(loanApplication.getPersonId(),false,Configuration.getDefaultLocale());
        }
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        BTPersonasValidarEnListasNegrasRequest request = getCommonRequestByEntity(entityId,BTPersonasValidarEnListasNegrasRequest.class);
        String documentTypeId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_DOCUMENT_TYPE_ID, person.getDocumentType().getId().toString(),null);
        String documentCountryId =translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_COUNTRY_ID, person.getCountry().getId().toString(),null);
        request.getBtinreq().setToken(token);
        request.setNumeroDocumento(person.getDocumentNumber());
        request.setTipoDocumentoId(documentTypeId);
        request.setPaisDocumentoId(documentCountryId);
        request.setPrimerNombre(person.getFirstName());
        request.setPrimerApellido(person.getFirstSurname());
        request.setSegundoNombre(person.getOtherNames());
        request.setSegundoApellido(person.getLastSurname());
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_VALIDAR_EN_LISTAS_NEGRAS), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTPersonasValidarEnListasNegrasResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTPersonasValidarEnListasNegrasResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_VALIDAR_EN_LISTAS_NEGRAS);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_VALIDAR_EN_LISTAS_NEGRAS, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_VALIDAR_EN_LISTAS_NEGRAS);
            throw ex;
        }
    }

    //AGREGAR FATCA
    @Override
    public BTPersonasAgregarFATCAResponse btPersonasAgregarFATCA(LoanApplication loanApplication, Person person, Long personaUId, SBTPCOInformacionFATCA sbtpcoInformacionFATCA, String token) throws Exception {
        Integer count = 0;
        try{
            return btPersonasAgregarFATCA(loanApplication, person, personaUId, sbtpcoInformacionFATCA, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btPersonasAgregarFATCA(loanApplication, person, personaUId, sbtpcoInformacionFATCA, token, count+1);
        }
    }

    private BTPersonasAgregarFATCAResponse btPersonasAgregarFATCA(LoanApplication loanApplication, Person person, Long personaUId, SBTPCOInformacionFATCA sbtpcoInformacionFATCA, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        BTPersonasAgregarFATCARequest request = getCommonRequestByEntity(entityId,BTPersonasAgregarFATCARequest.class);
        request.getBtinreq().setToken(token);
        request.setPersonaUId(personaUId);
        request.setSdtInformacionFATCA(sbtpcoInformacionFATCA);
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_AGREGAR_FATCA), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTPersonasAgregarFATCAResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTPersonasAgregarFATCAResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_AGREGAR_FATCA);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_AGREGAR_FATCA, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_AGREGAR_FATCA);
            throw ex;
        }
    }

    //AGREGAR PEP
    @Override
    public BTPersonasAgregarDatosPEPResponse btPersonasAgregarDatosPEP(LoanApplication loanApplication, Long personaUId, String isPEP, SdtDatosPEP sdtDatosPEP, String token) throws Exception {
        Integer count = 0;
        try{
            return btPersonasAgregarDatosPEP(loanApplication, personaUId, isPEP, sdtDatosPEP, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btPersonasAgregarDatosPEP(loanApplication, personaUId, isPEP, sdtDatosPEP, token, count+1);
        }
    }

    private BTPersonasAgregarDatosPEPResponse btPersonasAgregarDatosPEP(LoanApplication loanApplication, Long personaUId, String isPEP, SdtDatosPEP sdtDatosPEP, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        BTPersonasAgregarDatosPEPRequest request = getCommonRequestByEntity(entityId,BTPersonasAgregarDatosPEPRequest.class);
        request.getBtinreq().setToken(token);
        request.setPersonaUId(personaUId);
        request.setEsPEP(isPEP);
        request.setSdtDatosPEP(sdtDatosPEP);
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_AGREGAR_PEP), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTPersonasAgregarDatosPEPResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTPersonasAgregarDatosPEPResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_AGREGAR_PEP);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_AGREGAR_PEP, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_AGREGAR_PEP);
            throw ex;
        }
    }

    //OBTENER DATOS FATCA
    @Override
    public BTPersonasObtenerFATCAResponse btPersonasObtenerFATCA(LoanApplication loanApplication, Long personaUId, String token) throws Exception {
        Integer count = 0;
        try{
            return btPersonasObtenerFATCA(loanApplication, personaUId, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btPersonasObtenerFATCA(loanApplication, personaUId, token, count+1);
        }
    }

    private BTPersonasObtenerFATCAResponse btPersonasObtenerFATCA(LoanApplication loanApplication, Long personaUId, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        BTPersonasObtenerFATCARequest request = getCommonRequestByEntity(entityId,BTPersonasObtenerFATCARequest.class);
        request.getBtinreq().setToken(token);
        request.setPersonaUId(personaUId);
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_FATCA), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTPersonasObtenerFATCAResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTPersonasObtenerFATCAResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_FATCA);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_FATCA, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_FATCA);
            throw ex;
        }
    }

    //OBTENER DATOS PEP
    @Override
    public BTPersonasObtenerDatosPEPResponse btPersonasObtenerDatosPEP(LoanApplication loanApplication, Long personaUId, String token) throws Exception {
        Integer count = 0;
        try{
            return btPersonasObtenerDatosPEP(loanApplication, personaUId, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btPersonasObtenerDatosPEP(loanApplication, personaUId, token, count+1);
        }
    }

    private BTPersonasObtenerDatosPEPResponse btPersonasObtenerDatosPEP(LoanApplication loanApplication, Long personaUId, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        BTPersonasObtenerDatosPEPRequest request = getCommonRequestByEntity(entityId,BTPersonasObtenerDatosPEPRequest.class);
        request.getBtinreq().setToken(token);
        request.setPersonaUId(personaUId);
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_PEP), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTPersonasObtenerDatosPEPResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTPersonasObtenerDatosPEPResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_PEP);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_PEP, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_PEP);
            throw ex;
        }
    }

    //OBTENER TARJETAS DE DEBITO
    @Override
    public BTClientesObtenerTarjetasDebitoResponse btClientesObtenerTarjetasDebito(LoanApplication loanApplication, Long clienteUId, String token) throws Exception {
        Integer count = 0;
        try{
            return btClientesObtenerTarjetasDebito(loanApplication, clienteUId, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btClientesObtenerTarjetasDebito(loanApplication, clienteUId, token, count+1);
        }
    }

    private BTClientesObtenerTarjetasDebitoResponse btClientesObtenerTarjetasDebito(LoanApplication loanApplication, Long clienteUId, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        BTClientesObtenerTarjetasDebitoRequest request = getCommonRequestByEntity(entityId,BTClientesObtenerTarjetasDebitoRequest.class);
        request.getBtinreq().setToken(token);
        request.setClienteUId(clienteUId);
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_TARJETAS_DE_DEBITO), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTClientesObtenerTarjetasDebitoResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTClientesObtenerTarjetasDebitoResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_TARJETAS_DE_DEBITO);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_TARJETAS_DE_DEBITO, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_TARJETAS_DE_DEBITO);
            throw ex;
        }
    }

    //CREAR TARJETA DE DEBITO
    @Override
    public BTTarjetasDeDebitoCrearResponse btTarjetasDeDebitoCrear(LoanApplication loanApplication, Long clienteUId, String tipoTarjeta, String nombreTarjeta, String token) throws Exception {
        Integer count = 0;
        try{
            return btTarjetasDeDebitoCrear(loanApplication, clienteUId, tipoTarjeta, nombreTarjeta, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btTarjetasDeDebitoCrear(loanApplication, clienteUId, tipoTarjeta, nombreTarjeta, token, count+1);
        }
    }

    private BTTarjetasDeDebitoCrearResponse btTarjetasDeDebitoCrear(LoanApplication loanApplication, Long clienteUId, String tipoTarjeta, String nombreTarjeta, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        BTTarjetasDeDebitoCrearRequest request = getCommonRequestByEntity(entityId,BTTarjetasDeDebitoCrearRequest.class);
        request.getBtinreq().setToken(token);
        request.setClienteUId(clienteUId);
        request.setTipoTarjeta(tipoTarjeta);
        request.setNombreTarjeta(nombreTarjeta);
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_CREAR_TARJETA_DE_DEBITO), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTTarjetasDeDebitoCrearResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTTarjetasDeDebitoCrearResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_CREAR_TARJETA_DE_DEBITO);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_CREAR_TARJETA_DE_DEBITO, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_CREAR_TARJETA_DE_DEBITO);
            throw ex;
        }
    }

    //CONTRATAR DE CUENTA DE AHORRO CON FACULTADES
    @Override
    public BTCuentasDeAhorroContratarConFacultadesResponse btCuentasDeAhorroContratarConFacultades(LoanApplication loanApplication, String token, Long clienteUId, Long productoUId, String nombreSubCuenta, String tipoIntegracion) throws Exception {
        Integer count = 0;
        try{
            return btCuentasDeAhorroContratarConFacultades(loanApplication, token, clienteUId,productoUId,nombreSubCuenta,tipoIntegracion, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btCuentasDeAhorroContratarConFacultades(loanApplication, token, clienteUId,productoUId,nombreSubCuenta,tipoIntegracion, count+1);
        }
    }

    private BTCuentasDeAhorroContratarConFacultadesResponse btCuentasDeAhorroContratarConFacultades(LoanApplication loanApplication, String token, Long clienteUId, Long productoUId, String nombreSubCuenta, String tipoIntegracion, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        BTCuentasDeAhorroContratarConFacultadesRequest request = getCommonRequestByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),BTCuentasDeAhorroContratarConFacultadesRequest.class);
        request.getBtinreq().setToken(token);
        request.setProductoUId(productoUId);
        request.setNombreSubCuenta(nombreSubCuenta);
        request.setClienteUId(clienteUId);
        request.setTipoIntegracion(tipoIntegracion);

        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_CONTRATAR_PRODUCTO_CUENTAS_AHORRO_CON_FACULTADES), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTCuentasDeAhorroContratarConFacultadesResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTCuentasDeAhorroContratarConFacultadesResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_CONTRATAR_PRODUCTO_CUENTAS_AHORRO_CON_FACULTADES);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_CONTRATAR_PRODUCTO_CUENTAS_AHORRO_CON_FACULTADES, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_CONTRATAR_PRODUCTO_CUENTAS_AHORRO_CON_FACULTADES);
            throw ex;
        }
    }

    //OBTENER DETALLE DE RPESTAMO
    @Override
    public BTPrestamoObtenerDetalleResponse btPrestamosObtenerDetalle(LoanApplication loanApplication, Long operacionUId, String token) throws Exception {
        Integer count = 0;
        try{
            return btPrestamosObtenerDetalle(loanApplication,  operacionUId, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btPrestamosObtenerDetalle(loanApplication,  operacionUId, token, count+1);
        }
    }

    private BTPrestamoObtenerDetalleResponse btPrestamosObtenerDetalle(LoanApplication loanApplication, Long operacionUId, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        BTPrestamoObtenerDetalleRequest request = getCommonRequestByEntity(entityId,BTPrestamoObtenerDetalleRequest.class);
        request.getBtinreq().setToken(token);
        request.setOperacionUId(operacionUId);

        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_DETALLE_PRESTAMO), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTPrestamoObtenerDetalleResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTPrestamoObtenerDetalleResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_DETALLE_PRESTAMO);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_DETALLE_PRESTAMO, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_DETALLE_PRESTAMO);
            throw ex;
        }
    }

    //OBTENER DETALLE CUENTA DE AHORRO
    @Override
    public BTCuentasDeAhorroObtenerDatosResponse btCuentasDeAhorroObtenerDatos(LoanApplication loanApplication, Long operacionUId, String token) throws Exception {
        Integer count = 0;
        try{
            return btCuentasDeAhorroObtenerDatos(loanApplication,  operacionUId, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btCuentasDeAhorroObtenerDatos(loanApplication,  operacionUId, token, count+1);
        }
    }

    private BTCuentasDeAhorroObtenerDatosResponse btCuentasDeAhorroObtenerDatos(LoanApplication loanApplication, Long operacionUId, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        BTCuentasDeAhorroObtenerDatosRequest request = getCommonRequestByEntity(entityId,BTCuentasDeAhorroObtenerDatosRequest.class);
        request.getBtinreq().setToken(token);
        request.setOperacionUId(operacionUId);

        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_DETALLE_CUENTA), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTCuentasDeAhorroObtenerDatosResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTCuentasDeAhorroObtenerDatosResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_DETALLE_CUENTA);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_DETALLE_CUENTA, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_DETALLE_CUENTA);
            throw ex;
        }
    }

    // Obtener Prestamos
    @Override
    public ObtenerPrestamosClienteResponse obtenerPrestamos(LoanApplication loanApplication, Long clienteUID, String token) throws Exception {
        Integer count = 0;
        try{
            return obtenerPrestamos(loanApplication,  clienteUID, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return obtenerPrestamos(loanApplication,  clienteUID, token, count+1);
        }
    }

    private ObtenerPrestamosClienteResponse obtenerPrestamos(LoanApplication loanApplication, Long clienteUID, String token, Integer retryCount) throws Exception {
        if(clienteUID == null) throw new Exception("Error en obtenerPrestamos, parámetro clienteUID inválidp");
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        ObtenerPrestamosClienteRequest obtenerprestamosClienteRequest = getCommonRequestByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),ObtenerPrestamosClienteRequest.class);
        obtenerprestamosClienteRequest.getBtinreq().setToken(token);
        obtenerprestamosClienteRequest.setClienteUId(clienteUID);

        String jsonRequestData = gson.toJson(obtenerprestamosClienteRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_PRESTAMOS_CLIENTE), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                ObtenerPrestamosClienteResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),ObtenerPrestamosClienteResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_PRESTAMOS_CLIENTE);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_PRESTAMOS_CLIENTE, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_PRESTAMOS_CLIENTE);
            throw ex;
        }
    }

    //OBTENER CRONOGRAMA
    @Override
    public BTPrestamosObtenerCronogramaResponse btPrestamosObtenerCronograma(LoanApplication loanApplication, Long operacionUId, String token) throws Exception {
        Integer count = 0;
        try{
            return btPrestamosObtenerCronograma(loanApplication,  operacionUId, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btPrestamosObtenerCronograma(loanApplication,  operacionUId, token, count+1);
        }
    }

    private BTPrestamosObtenerCronogramaResponse btPrestamosObtenerCronograma(LoanApplication loanApplication, Long operacionUId, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        BTPrestamoObtenerCronogramaRequest request = getCommonRequestByEntity(entityId,BTPrestamoObtenerCronogramaRequest.class);
        request.getBtinreq().setToken(token);
        request.setOperacionUId(operacionUId);
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_CRONOGRAMA), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTPrestamosObtenerCronogramaResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTPrestamosObtenerCronogramaResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_CRONOGRAMA);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_CRONOGRAMA, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_CRONOGRAMA);
            throw ex;
        }
    }

    //OBTENER PROFESIONES
    @Override
    public BTPersonasObtenerProfesionesResponse btPersonaObtenerProfesiones(LoanApplication loanApplication, String token) throws Exception {
        Integer count = 0;
        try{
            return btPersonaObtenerProfesiones(loanApplication,  token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btPersonaObtenerProfesiones(loanApplication,  token, count+1);
        }
    }

    private BTPersonasObtenerProfesionesResponse btPersonaObtenerProfesiones(LoanApplication loanApplication, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        BTPrestamoObtenerCronogramaRequest request = getCommonRequestByEntity(entityId,BTPrestamoObtenerCronogramaRequest.class);
        request.getBtinreq().setToken(token);
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_PROFESIONES), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTPersonasObtenerProfesionesResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTPersonasObtenerProfesionesResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_PROFESIONES);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_PROFESIONES, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_PROFESIONES);
            throw ex;
        }
    }

    //ACTUALIZAR PROFESION
    @Override
    public BTPersonasActualizarProfesionResponse btPersonasActualizarProfesion(LoanApplication loanApplication, Long personaUId, Long profesionUId, Date fechaInicio, String token) throws Exception {
        Integer count = 0;
        try{
            return btPersonasActualizarProfesion(loanApplication,personaUId,profesionUId,fechaInicio,  token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btPersonasActualizarProfesion(loanApplication,personaUId,profesionUId,fechaInicio,  token, count+1);
        }
    }

    private BTPersonasActualizarProfesionResponse btPersonasActualizarProfesion(LoanApplication loanApplication, Long personaUId, Long profesionUId, Date fechaInicio, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        BTPersonasActualizarProfesionRequest request = getCommonRequestByEntity(entityId,BTPersonasActualizarProfesionRequest.class);
        request.getBtinreq().setToken(token);
        request.setProfesionId(profesionUId);
        request.setPersonaUId(personaUId);
        if(fechaInicio != null) request.setFechaInicioProfesion(convertDateToStringFormat(fechaInicio));
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_ACTUALIZAR_PROFESION), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTPersonasActualizarProfesionResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTPersonasActualizarProfesionResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_ACTUALIZAR_PROFESION);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_ACTUALIZAR_PROFESION, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_ACTUALIZAR_PROFESION);
            throw ex;
        }
    }


    //OBTENER CCI
    @Override
    public BPBAZServicesObtieneCCIResponse bpBAZServicesObtieneCCI(LoanApplication loanApplication, Long productoUId, String token) throws Exception {
        Integer count = 0;
        try{
            return bpBAZServicesObtieneCCI(loanApplication, productoUId, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return bpBAZServicesObtieneCCI(loanApplication, productoUId, token, count+1);
        }
    }

    private BPBAZServicesObtieneCCIResponse bpBAZServicesObtieneCCI(LoanApplication loanApplication,  Long productoUId, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        BPBAZServicesObtieneCCIRequest request = getCommonRequestByEntity(entityId,BPBAZServicesObtieneCCIRequest.class);
        request.getBtinreq().setToken(token);
        request.setProducto(productoUId);
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_CCI), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BPBAZServicesObtieneCCIResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BPBAZServicesObtieneCCIResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_CCI);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_CCI, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_CCI);
            throw ex;
        }
    }

    //OBTENER CCI
    @Override
    public AgregarDomicilioBcoAztecaResponse btAgregarDomicilioBcoAzteca(LoanApplication loanApplication, AgregarDomicilioBcoAztecaRequest request, String token) throws Exception {
        Integer count = 0;
        try{
            return btAgregarDomicilioBcoAzteca(loanApplication, request, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btAgregarDomicilioBcoAzteca(loanApplication, request, token, count+1);
        }
    }

    private AgregarDomicilioBcoAztecaResponse btAgregarDomicilioBcoAzteca(LoanApplication loanApplication, AgregarDomicilioBcoAztecaRequest requestData, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        AgregarDomicilioBcoAztecaRequest request = getCommonRequestByEntity(entityId,AgregarDomicilioBcoAztecaRequest.class);
        request.getBtinreq().setToken(token);
        request.setDomicilio(requestData.getDomicilio());
        request.setPgcod(requestData.getPgcod());
        request.setCuentaBT(requestData.getCuentaBT());
        request.setPais(requestData.getPais());
        request.setPetdoc(requestData.getPetdoc());
        request.setPendoc(requestData.getPendoc());
        request.setUbigeo(requestData.getUbigeo());
        request.setNivel1(requestData.getNivel1());
        request.setDescNivel1(requestData.getDescNivel1());
        request.setNivel2(requestData.getNivel2());
        request.setDescNivel2(requestData.getDescNivel2());
        request.setNivel3(requestData.getNivel3());
        request.setDescNivel3(requestData.getDescNivel3());
        request.setNivel4(requestData.getNivel4());
        request.setDescNivel4(requestData.getDescNivel4());
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_AGREGAR_DOMICILIO), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                AgregarDomicilioBcoAztecaResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),AgregarDomicilioBcoAztecaResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_AGREGAR_DOMICILIO);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_AGREGAR_DOMICILIO, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_AGREGAR_DOMICILIO);
            throw ex;
        }
    }


    @Override
    public BTClientesObtenerCuentaClienteResponse btClientesObtenerCuentaCliente(LoanApplication loanApplication, Long clienteUid, String token) throws Exception {
        Integer count = 0;
        try{
            return btClientesObtenerCuentaCliente(loanApplication, clienteUid, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btClientesObtenerCuentaCliente(loanApplication, clienteUid, token, count+1);
        }
    }

    private BTClientesObtenerCuentaClienteResponse btClientesObtenerCuentaCliente(LoanApplication loanApplication, Long clienteUid, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        BTClientesObtenerCuentaClienteRequest request = getCommonRequestByEntity(entityId,BTClientesObtenerCuentaClienteRequest.class);
        request.getBtinreq().setToken(token);
        request.setClienteUId(clienteUid);
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_CUENTA_CLIENTE), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTClientesObtenerCuentaClienteResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTClientesObtenerCuentaClienteResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_CUENTA_CLIENTE);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_CUENTA_CLIENTE, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_CUENTA_CLIENTE);
            throw ex;
        }
    }


    //PAGAR CUOTA

    @Override
    public BTCorresponsalesPagoDeCuotaResponse pagarCuota(LoanApplication loanApplication, Long Credito, Date fechaDePago, Double ImpPago, Double ImpCom, Double ImpRed, String RefUnica, Integer Canal, Integer tipoMov, String token) throws Exception {
        Integer count = 0;
        try{
            return pagarCuota(loanApplication,Credito, fechaDePago, ImpPago, ImpCom, ImpRed, RefUnica, Canal, tipoMov, token,count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return pagarCuota(loanApplication,Credito, fechaDePago, ImpPago, ImpCom, ImpRed, RefUnica, Canal, tipoMov, token,count+1);
        }
    }

    private BTCorresponsalesPagoDeCuotaResponse pagarCuota(LoanApplication loanApplication, Long credito, Date fechaDePago, Double impPago, Double impCom, Double impRed, String refUnica, Integer canal, Integer tipoMov, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        BTCorresponsalesPagoDeCuotaRequest pagarCuotaRequest = getCommonRequestByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),BTCorresponsalesPagoDeCuotaRequest.class);
        pagarCuotaRequest.getBtinreq().setToken(token);
        pagarCuotaRequest.setTipoMov(tipoMov);
        pagarCuotaRequest.setMdaPago(1);
        pagarCuotaRequest.setCredito(credito);
        pagarCuotaRequest.setFecPago(convertDateToStringFormat(fechaDePago));
        pagarCuotaRequest.setHoraPago(convertDateToHourStringFormat(fechaDePago));
        pagarCuotaRequest.setImpPago(impPago);
        pagarCuotaRequest.setImpRed(impRed);
        pagarCuotaRequest.setImpCom(impCom);
        pagarCuotaRequest.setRefUnica(refUnica);
        String jsonRequestData = gson.toJson(pagarCuotaRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_PAGAR_CUOTA), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                BTCorresponsalesPagoDeCuotaResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTCorresponsalesPagoDeCuotaResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null || (response != null && (response.getResult() == null || !response.getResult().equalsIgnoreCase("OK"))) ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_PAGAR_CUOTA);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_PAGAR_CUOTA, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_PAGAR_CUOTA);
            throw ex;
        }
    }

    private void sendErrorAndRegister(EntityWebServiceLog webServiceLog, BTResponseData btResponseData, int entityWsId) throws IOException, MessagingException {
        EntityWebService entityWebService = catalogService.getEntityWebService(webServiceLog != null ? webServiceLog.getEntityWebServiceId() : entityWsId);
        errorService.sendErrorCriticSlack(String.format("ERROR ALFIN: \n Mensaje: ERROR %s \n LOAN : %s,  Response: %s",  entityWebService.getWbeserviceName(), webServiceLog != null ? webServiceLog.getLoanApplicationId() : "-", webServiceLog != null ? webServiceLog.getResponse() : "-"));
        if(webServiceLog != null && webServiceLog.getLoanApplicationId() != null) errorEntityDao.addEntityError(webServiceLog.getLoanApplicationId(),entityWebService.getEntityId(), entityWebService.getId(), btResponseData != null ? btResponseData.getErrorDetail() : "", webServiceLog.getId());
        if(!Arrays.asList(EntityWebService.BANTOTAL_APIREST_AUTHENTICATION).contains(entityWebService.getId())) sendErrorBantotal(entityWebService.getWbeserviceName(), webServiceLog != null ? webServiceLog.getRequest() : "-", webServiceLog != null ? webServiceLog.getResponse() : "-", btResponseData != null ? btResponseData.getErrorDetail() : "", btResponseData != null ? btResponseData.getErrorCode() : "");
        else sendErrorBantotal(entityWebService.getWbeserviceName(), "-", webServiceLog.getResponse(), btResponseData != null ? btResponseData.getErrorDetail() : "", btResponseData != null ? btResponseData.getErrorCode() : "");
    }

    private void sendErrorBantotal(String serviceName, String request, String response, String errorDetail, String errorCode) throws MessagingException, IOException {
        if(errorDetail == null) errorDetail = "-";
        if(errorCode == null) errorCode = "-";
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
    public AgregarDomicilioBcoAztecaRequest createAgregarDomicilioBcoAztecaObject(LoanApplication loanApplication, Person person, User user) throws Exception {

        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        String documentTypeId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_DOCUMENT_TYPE_ID, person.getDocumentType().getId().toString(),null);
        String documentCountryId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_COUNTRY_ID, person.getCountry().getId().toString(),null);

        AgregarDomicilioBcoAztecaRequest request = new AgregarDomicilioBcoAztecaRequest();
        EntityWsResult savingAccountDetail = securityDAO.getEntityResultWS(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_CUENTA_CLIENTE);
        if(savingAccountDetail != null && savingAccountDetail.getResult() != null){
            BTClientesObtenerCuentaClienteResponse btCuentasDeAhorroObtenerDatosResponse = new Gson().fromJson(savingAccountDetail.getResult().toString(), BTClientesObtenerCuentaClienteResponse.class);
            request.setCuentaBT(btCuentasDeAhorroObtenerDatosResponse.getCuentaBT().toString());
        }

        request.setPgcod(1); //ALWAYS SEND 1;
        request.setPais(documentCountryId != null ? Integer.valueOf(documentCountryId) : null);
        request.setPetdoc(documentTypeId != null ? Integer.valueOf(documentTypeId) : null);
        request.setPendoc(person.getDocumentNumber());

        AgregarDomicilioBcoAztecaRequest.DomicilioBantotal domicilioBantotal = new AgregarDomicilioBcoAztecaRequest.DomicilioBantotal();
        domicilioBantotal.setPaisDomicilioId(documentCountryId != null ? Integer.valueOf(documentCountryId) : null);
        domicilioBantotal.setApartamento(null); //TODO
        domicilioBantotal.setCodigoPostal(null); //TODO
        domicilioBantotal.setTipoDeDomicilioId("01"); //SIEMPRE ES VIVIENDA


        Direccion disaggregatedHomeAddress = personDAO.getDisggregatedAddress(person.getId(), "H");
        if (disaggregatedHomeAddress != null) {
            String zoneId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_ZONE_ID, disaggregatedHomeAddress.getTipoZona().toString(),null);
            String streetTypeId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_STREET_TYPE_ID, disaggregatedHomeAddress.getTipoVia().toString(),null);
            request.setUbigeo(disaggregatedHomeAddress.getUbigeoInei());
            request.setNivel1(zoneId);
            request.setDescNivel1(disaggregatedHomeAddress.getNombreZona());
            request.setNivel2("1");//NO APLICA
            request.setDescNivel2(null);
            request.setNivel3(streetTypeId);
            request.setDescNivel3(disaggregatedHomeAddress.getNombreVia());
            request.setNivel4("4");//NUMERO INTERIOR
            request.setDescNivel4(disaggregatedHomeAddress.getNumeroInterior() != null ? disaggregatedHomeAddress.getNumeroInterior() : "0");
            domicilioBantotal.setCalle(disaggregatedHomeAddress.getNumeroVia());
            Ubigeo ubigeo = catalogService.getUbigeo(disaggregatedHomeAddress.getUbigeo());
            if(ubigeo != null){
                String departmentId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_DEPARTMENT_ID, ubigeo.getDepartmentUbigeoId(),null);;
                String localityId = translatorDAO.translate(entityId, TranslatorDAOImpl.BANTOTAL_PROVINCE_ID, ubigeo.getProvinceUbigeoId(),null);;
                domicilioBantotal.setDepartamentoId(departmentId != null ? Integer.valueOf(departmentId) : null);
                domicilioBantotal.setLocalidadId(localityId != null ? Integer.valueOf(localityId) : null);
                domicilioBantotal.setNumeroPuerta(disaggregatedHomeAddress.getNumeroInterior() != null ? disaggregatedHomeAddress.getNumeroInterior() : "0");
            }
        }

        request.setDomicilio(domicilioBantotal);
        return request;
    }



     @Override
    public ObtenerEnvioEstadoCuentaResponse aBCuentasVistaObtenerEnvioEstadoCuenta(LoanApplication loanApplication, Long operacionUid, String token) throws Exception {
        Integer count = 0;
        try{
            return aBCuentasVistaObtenerEnvioEstadoCuenta(loanApplication,operacionUid,  token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return aBCuentasVistaObtenerEnvioEstadoCuenta(loanApplication,operacionUid, token, count+1);
        }
    }

    private ObtenerEnvioEstadoCuentaResponse aBCuentasVistaObtenerEnvioEstadoCuenta(LoanApplication loanApplication, Long operacionUid, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        ObtenerEnvioEstadoCuentaRequest request = getCommonRequestByEntity(entityId,ObtenerEnvioEstadoCuentaRequest.class);
        request.getBtinreq().setToken(token);
        request.setOperacionUId(operacionUid);
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_ENVIO_ESTADO_DE_CUENTA), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                ObtenerEnvioEstadoCuentaResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),ObtenerEnvioEstadoCuentaResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !this.excludeErrorCodes(response.getErroresnegocio().getBTErrorNegocio(), Arrays.asList("30001")).isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_ENVIO_ESTADO_DE_CUENTA);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_ENVIO_ESTADO_DE_CUENTA, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_ENVIO_ESTADO_DE_CUENTA);
            throw ex;
        }
    }

    @Override
    public ActualizarEnvioEstadoCuentaResponse aBCuentasVistaActualizarEnvioEstadoCuenta(LoanApplication loanApplication, Long operacionUid, String envioEC, String modoEnvioEC, String token) throws Exception {
        Integer count = 0;
        try{
            return aBCuentasVistaObtenerEnvioEstadoCuenta(loanApplication,operacionUid, envioEC, modoEnvioEC, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return aBCuentasVistaObtenerEnvioEstadoCuenta(loanApplication,operacionUid, envioEC, modoEnvioEC, token, count+1);
        }
    }

    private ActualizarEnvioEstadoCuentaResponse aBCuentasVistaObtenerEnvioEstadoCuenta(LoanApplication loanApplication, Long operacionUid, String envioEC, String modoEnvioEC, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        ActualizarEnvioEstadoCuentaRequest request = getCommonRequestByEntity(entityId,ActualizarEnvioEstadoCuentaRequest.class);
        request.getBtinreq().setToken(token);
        request.setOperacionUId(operacionUid);
        request.setEnvioEC(envioEC);
        request.setModoEnvioEC(modoEnvioEC);

        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_ACTUALIZAR_ENVIO_ESTADO_DE_CUENTA), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                ActualizarEnvioEstadoCuentaResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),ActualizarEnvioEstadoCuentaResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_ACTUALIZAR_ENVIO_ESTADO_DE_CUENTA);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_ACTUALIZAR_ENVIO_ESTADO_DE_CUENTA, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_ACTUALIZAR_ENVIO_ESTADO_DE_CUENTA);
            throw ex;
        }
    }

    @Override
    public AgregarEnvioEstadoCuentaResponse aBCuentasVistaAgregarEnvioEstadoCuenta(LoanApplication loanApplication, Long operacionUid, String envioEC, String modoEnvioEC, String token) throws Exception {
        Integer count = 0;
        try{
            return aBCuentasVistaAgregarEnvioEstadoCuenta(loanApplication,operacionUid, envioEC, modoEnvioEC, token, count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return aBCuentasVistaAgregarEnvioEstadoCuenta(loanApplication,operacionUid, envioEC, modoEnvioEC, token, count+1);
        }
    }

    private AgregarEnvioEstadoCuentaResponse aBCuentasVistaAgregarEnvioEstadoCuenta(LoanApplication loanApplication, Long operacionUid, String envioEC, String modoEnvioEC, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        Integer entityId = loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId();
        ActualizarEnvioEstadoCuentaRequest request = getCommonRequestByEntity(entityId,ActualizarEnvioEstadoCuentaRequest.class);
        request.getBtinreq().setToken(token);
        request.setOperacionUId(operacionUid);
        request.setEnvioEC(envioEC);
        request.setModoEnvioEC(modoEnvioEC);
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_AGREGAR_ENVIO_ESTADO_DE_CUENTA), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            
                AgregarEnvioEstadoCuentaResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),AgregarEnvioEstadoCuentaResponse.class);
                if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
                if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                    sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_AGREGAR_ENVIO_ESTADO_DE_CUENTA);
                }
                else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_AGREGAR_ENVIO_ESTADO_DE_CUENTA, new Gson().toJson(response));
                return response;
            
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_AGREGAR_ENVIO_ESTADO_DE_CUENTA);
            throw ex;
        }
    }

    @Override
    public List<BtError> excludeErrorCodes(List<BtError> data, List<String> errorCodes){
        if(errorCodes == null) return data;
        return data.stream().filter(e -> e.getCodigo() == null || (e.getCodigo() != null && !errorCodes.contains(e.getCodigo()))).collect(Collectors.toList());
    }


    //OBTENER PLAZO FIJO

    @Override
    public BTClientesObtenerPlazosFijosResponse btClientesObtenerPlazosFijos(LoanApplication loanApplication, Long clienteUId, String token) throws Exception {
        Integer count = 0;
        try{
            return btClientesObtenerPlazosFijos(loanApplication, clienteUId, token,count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btClientesObtenerPlazosFijos(loanApplication, clienteUId, token,count+1);
        }
    }

    private BTClientesObtenerPlazosFijosResponse btClientesObtenerPlazosFijos(LoanApplication loanApplication, Long clienteUId, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        BTClientesObtenerPlazosFijosRequest btClientesObtenerPlazosFijosRequest = getCommonRequestByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),BTClientesObtenerPlazosFijosRequest.class);
        btClientesObtenerPlazosFijosRequest.getBtinreq().setToken(token);
        btClientesObtenerPlazosFijosRequest.setClienteUId(clienteUId);
        String jsonRequestData = gson.toJson(btClientesObtenerPlazosFijosRequest);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_PLAZOS_FIJOS), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            BTClientesObtenerPlazosFijosResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),BTClientesObtenerPlazosFijosResponse.class);
            if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
            if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_PLAZOS_FIJOS);
            }
            else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_PLAZOS_FIJOS, new Gson().toJson(response));
            return response;

        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_PLAZOS_FIJOS);
            throw ex;
        }
    }


    //OBTENER TIPO DE MOVIMIENTO
    @Override
    public ObtenerTipoDeMovimientoResponse btCorresponsalesObtenerTipoMovimiento(LoanApplication loanApplication, Long operacionUId, Long campaniaId, String token) throws Exception {
        Integer count = 0;
        try{
            return btCorresponsalesObtenerTipoMovimiento(loanApplication, operacionUId, campaniaId, token,count);
        }
        catch (BTInvalidSessionException exception){
            token = generateAndSaveToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
            return btCorresponsalesObtenerTipoMovimiento(loanApplication, operacionUId, campaniaId, token,count+1);
        }
    }

    private ObtenerTipoDeMovimientoResponse btCorresponsalesObtenerTipoMovimiento(LoanApplication loanApplication, Long operacionUId, Long campaniaId, String token, Integer retryCount) throws Exception {
        if(token == null){
            token = getToken(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId());
        }
        Gson gson = new Gson();
        ObtenerTipoDeMovimientoRequest request = getCommonRequestByEntity(loanApplication.getEntityId() != null ? loanApplication.getEntityId() : loanApplication.getSelectedEntityId(),ObtenerTipoDeMovimientoRequest.class);
        request.getBtinreq().setToken(token);
        request.setCampana(campaniaId);
        request.setOperacionUId(operacionUId);
        String jsonRequestData = gson.toJson(request);
        EntityWebServiceLog<JSONObject> webServiceResponse = null;
        try {
            webServiceResponse = bantotalUtilCall.call(catalogService.getEntityWebService(EntityWebService.BANTOTAL_APIREST_OBTENER_TIPO_MOVIMIENTO), jsonRequestData, loanApplication.getId());
            if(Arrays.asList(EntityWebServiceLog.STATUS_FAILED).contains(webServiceResponse.getStatus())) throw new Exception("BANTOTAL WS - SERVICE FAILED");
            ObtenerTipoDeMovimientoResponse response = new Gson().fromJson(webServiceResponse.getRestResponse().toString(),ObtenerTipoDeMovimientoResponse.class);
            if(response != null && response.isInvalidSession() && retryCount < 1) throw new BTInvalidSessionException("Invalid session");
            if(response == null ||  (response.getErroresnegocio()!= null && response.getErroresnegocio().getBTErrorNegocio() != null && !response.getErroresnegocio().getBTErrorNegocio().isEmpty())){
                sendErrorAndRegister(webServiceResponse, response, EntityWebService.BANTOTAL_APIREST_OBTENER_TIPO_MOVIMIENTO);
            }
            else webServiceDAO.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANTOTAL_APIREST_OBTENER_TIPO_MOVIMIENTO, new Gson().toJson(response));
            return response;
        }
        catch (BTInvalidSessionException ex){
            throw ex;
        }
        catch (Exception ex){
            sendErrorAndRegister(webServiceResponse, null, EntityWebService.BANTOTAL_APIREST_OBTENER_TIPO_MOVIMIENTO);
            throw ex;
        }
    }


}
