package com.affirm.acceso;

import com.affirm.acceso.model.*;
import com.affirm.acceso.util.AccesoUtilCall;
import com.affirm.bantotalrest.model.common.BTResponseData;
import com.affirm.common.dao.*;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.catalog.RateCommission;
import com.affirm.common.model.catalog.UserFileType;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.SqlErrorMessageException;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.security.model.EntityWsResult;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by miberico on 26/07/17.
 */
@Service
public class AccesoServiceCall {

    @Autowired
    BotDAO botDAO;
    @Autowired
    TranslatorDAO translatorDAO;
    @Autowired
    UtilService utilService;
    @Autowired
    LoanApplicationDAO loanApplicationDao;
    @Autowired
    CreditDAO creditDao;
    @Autowired
    AccesoUtilCall accesoUtilCall;
    @Autowired
    CatalogService catalogService;
    @Autowired
    EntityWebServiceUtil entityWebServiceUtil;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    FileService fileService;
    @Autowired
    CreditService creditService;
    @Autowired
    SecurityDAO securityDao;
    @Autowired
    WebServiceDAO webServiceDao;
    @Autowired
    private ErrorService errorService;
    @Autowired
    private ErrorEntityDao errorEntityDao;

    public Integer callLogin(Boolean forced) throws Exception {
        if (!forced) {
            String sessionId = botDAO.getACSession(Entity.ACCESO);
            if (sessionId != null && !sessionId.isEmpty()) return Integer.valueOf(sessionId);
        }
        Gson gson = new Gson();
        Login loginData = new Login(System.getenv("ACCESO_LOGIN_USER"), System.getenv("ACCESO_LOGIN_PASSWORD"), "", "", "", 1, 3);
        String jsonLoginData = gson.toJson(loginData);
        EntityWebServiceLog<JSONObject> webServiceResponse = accesoUtilCall.call(catalogService.getEntityWebService(EntityWebService.ACCESO_LOGIN), jsonLoginData, null);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        JSONArray arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
        Integer codigoError = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_mensaj", null);
        String mensajeError = JsonUtil.getStringFromJson(arrResultado.getJSONObject(0), "no_estado", null);
        if (codigoError == 7) {
            Integer id_sesion = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "id_sesion", null);
            botDAO.registerACSession(Entity.ACCESO, String.valueOf(id_sesion));
            return id_sesion;
        } else {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            System.out.println("[Acceso Crediticio Login] - [" + codigoError + "] : " + mensajeError);
        }
        return null;
    }

    public Date callConsultarExpediente(String nroDocumento, Integer loanApplicationId, int time) throws Exception {

        if (time > 1) throw new Exception("Error de Session Acceso Crediticio + [DNI - " + nroDocumento + "]");
        Gson gson = new Gson();
        String jsonConsultarExpediente = gson.toJson(new Expediente(callLogin(false), nroDocumento));
        EntityWebServiceLog<JSONObject> webServiceResponse = accesoUtilCall.call(catalogService.getEntityWebService(EntityWebService.ACCESO_CONSULTAR_EXPEDIENTE), jsonConsultarExpediente, loanApplicationId);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();
        String estado = JsonUtil.getStringFromJson(jsonResponse, "estado", null);

        JSONArray arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
        Integer codigoError = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_mensaj", null);
        String mensajeError = JsonUtil.getStringFromJson(arrResultado.getJSONObject(0), "no_estado", null);
        if (estado.equals("ok") && codigoError == 108) {
            Integer resultTI = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "ti_result", null);
            if (resultTI == 2) {
                System.out.println("[DNI - " + nroDocumento + "] - Seguir con el proceso ");
                return null;
            } else if (resultTI == 1) {
                String fecha = JsonUtil.getStringFromJson(arrResultado.getJSONObject(0), "no_result", null);
                System.out.println("[DNI - " + nroDocumento + "] - Fecha : " + fecha);
                return new SimpleDateFormat("yyyy-MM-dd").parse(fecha);
            }
        } else if (codigoError == 8) {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), false);
            System.out.println("[Acceso Crediticio Login] - [" + codigoError + "] : " + mensajeError);
            System.out.println("Error de Session Acceso Crediticio + [DNI - " + nroDocumento + "]");
            callLogin(true);
            return callConsultarExpediente(nroDocumento, loanApplicationId, time++);
        } else if (estado.equals("error")) {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), false);
            System.out.println("[Acceso Crediticio Login] - [" + codigoError + "] : " + jsonResponse.toString());
            System.out.println("Error de Session Acceso Crediticio + [DNI - " + nroDocumento + "]");
        } else {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            System.out.println("[Acceso Crediticio Error] - [" + codigoError + "] : " + jsonResponse.toString());
            System.out.println("Error de Session Acceso Crediticio + [DNI - " + nroDocumento + "]");
            throw new SqlErrorMessageException(null, accesoUtilCall.sendErrorMessage(codigoError));
        }

        entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
        throw new Exception();
    }

    @Async
    public void callEvaluacionGenericaAsync(EvaluacionGenerica evaluacionGenerica, Integer loanApplicationId, int time) throws Exception {
        callEvaluacionGenerica(evaluacionGenerica,loanApplicationId,time);
    }

    public void callEvaluacionGenerica(EvaluacionGenerica evaluacionGenerica, Integer loanApplicationId, int time) throws Exception {
        if (time > 1)
            throw new Exception("Error de Session Acceso Evaluación Generica + [DNI - " + evaluacionGenerica.getProspecto().getDocumento() + "]");
        Gson gson = new Gson();
        evaluacionGenerica.setPlantilla("48");
        String jsonEvaluacionGenerica = gson.toJson(evaluacionGenerica);
        EntityWebServiceLog<JSONObject> webServiceResponse= null;
        Integer count = 0;
        while (count < 2) {
            count++;
            webServiceResponse = accesoUtilCall.callV2(catalogService.getEntityWebService(EntityWebService.ACCESO_EVALUACION_GENERICA), jsonEvaluacionGenerica, loanApplicationId, null);
            JSONObject jsonResponse = webServiceResponse.getRestResponse();
            EvaluacionGenericaResponse evaluacionGenericaResponse = new Gson().fromJson(jsonResponse.toString(), EvaluacionGenericaResponse.class);
            webServiceDao.registerEntityWebServiceResult(loanApplicationId, EntityWebService.ACCESO_EVALUACION_GENERICA, jsonResponse.toString());
            if (!evaluacionGenericaResponse.getEstado().equalsIgnoreCase("Pendiente")) {
                count = 2;
            }
            else if(count == 2){
                errorService.sendErrorCriticSlack("Error Acceso Evaluacion Generica + [LOAN - " + loanApplicationId + "]");
                throw new Exception("Error Acceso Evaluacion Generica + [LOAN - " + loanApplicationId + "]");
            }
            if(count < 2) Thread.sleep(5000);
        }
    }

    public Integer callCrearExpediente(Expediente expediente, Integer loanApplicationId, int time) throws Exception {

        if (time > 1)
            throw new Exception("Error de Session Acceso Crediticio + [DNI - " + expediente.getNroDocumento() + "]");
        Gson gson = new Gson();
        expediente.setSesionId(callLogin(false));
        String jsonCrearExpediente = gson.toJson(expediente);
        EntityWebServiceLog<JSONObject> webServiceResponse = accesoUtilCall.call(catalogService.getEntityWebService(EntityWebService.ACCESO_CREAR_EXPEDIENTE), jsonCrearExpediente, loanApplicationId);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        JSONArray arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
        Integer codigoError = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_mensaj", null);
        String mensajeError = JsonUtil.getStringFromJson(arrResultado.getJSONObject(0), "no_estado", null);
        if (codigoError == 73) {
            Integer calificacion = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "ti_califi", null);
            Integer nro_expediente = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_expedi", null);
            if (calificacion == 1 && nro_expediente != null) {
                System.out.println("[DNI - " + expediente.getNroDocumento() + "] - Expediente : " + nro_expediente);
                return nro_expediente;
            } else if (calificacion == 2) {
                System.out.println("[DNI - " + expediente.getNroDocumento() + "] - Expediente : OBSERVADO");
                return null;
            } else if (calificacion == 3) {
                System.out.println("[DNI - " + expediente.getNroDocumento() + "] - Expediente : RECHAZADO");
                return null;
            }
        } else if (codigoError == 75 || codigoError == 40) {
            // 40: Ingreso insuficiente
            return null;
        } else if (codigoError == 8) {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), false);
            System.out.println("[Acceso Crediticio Login] - [" + codigoError + "] : " + mensajeError);
            System.out.println("Error de Session Acceso Crediticio + [DNI - " + expediente.getNroDocumento() + "]");
            callLogin(true);
            return callCrearExpediente(expediente, loanApplicationId, time++);

        } else {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            throw new SqlErrorMessageException(null, accesoUtilCall.sendErrorMessage(codigoError));
        }

        entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
        throw new Exception();
    }

    public Boolean callCrearDireccion(Direccion direccion, Integer loanApplicationId, int time) throws Exception {

        if (time > 1)
            throw new Exception("Error de Session Acceso Crediticio + [DNI - " + direccion.getNroExpediente() + "]");
        Gson gson = new Gson();
        direccion.setSesionId(callLogin(false));
        String jsonCrearDireccion = gson.toJson(direccion);
        EntityWebServiceLog<JSONObject> webServiceResponse = accesoUtilCall.call(catalogService.getEntityWebService(EntityWebService.ACCESO_CREAR_DIRECCION), jsonCrearDireccion, loanApplicationId);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        JSONArray arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
        Integer codigoError = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_mensaj", null);
        String mensajeError = JsonUtil.getStringFromJson(arrResultado.getJSONObject(0), "no_estado", null);
        if (codigoError == 116 || codigoError == 117) {
            System.out.println("[Expediente - " + direccion.getNroExpediente() + "] - Direccion creada");
            return true;
        } else if (codigoError == 8) {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), false);
            System.out.println("[Acceso Crediticio Login] - [" + codigoError + "] : " + mensajeError);
            System.out.println("Error de Session Acceso Crediticio + [Expediente - " + direccion.getNroExpediente() + "]");
            callLogin(true);
            return callCrearDireccion(direccion, loanApplicationId, time++);
        } else {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            throw new SqlErrorMessageException(null, accesoUtilCall.sendErrorMessage(codigoError));
        }
    }

    public List<Oferta> callCotizar(Cotizador cotizador, Integer loanApplicationId, boolean forceToMinDownPayment, int time) throws Exception {

        if (time > 1)
            throw new Exception("Error de Session Acceso Crediticio + [DNI - " + cotizador.getNroExpediente() + "]");
        Gson gson = new Gson();
        cotizador.setSesionId(callLogin(false));
        String jsonCotizar = gson.toJson(cotizador);
        EntityWebServiceLog<JSONObject> webServiceResponse = accesoUtilCall.call(catalogService.getEntityWebService(EntityWebService.ACCESO_COTIZADOR), jsonCotizar, loanApplicationId);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        JSONArray arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
        Integer codigoError = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_mensaj", null);
        String mensajeError = JsonUtil.getStringFromJson(arrResultado.getJSONObject(0), "no_estado", null);
        if (codigoError == 91) {
            List<Oferta> ofertas = new ArrayList<>();
            for (int i = 0; i < arrResultado.length(); i++) {
                Oferta oferta = new Oferta();
                oferta.setNumExpediente(JsonUtil.getIntFromJson(arrResultado.getJSONObject(i), "co_expedi", null));
                oferta.setNumCuotas(JsonUtil.getIntFromJson(arrResultado.getJSONObject(i), "ca_cuotas", null));
                oferta.setMonto(JsonUtil.getDoubleFromJson(arrResultado.getJSONObject(i), "im_financ", null));
                oferta.setCuota(JsonUtil.getDoubleFromJson(arrResultado.getJSONObject(i), "im_cuotas", null));
                oferta.setScore(JsonUtil.getIntFromJson(arrResultado.getJSONObject(i), "ti_califi", null));
                oferta.setDescripion(JsonUtil.getStringFromJson(arrResultado.getJSONObject(i), "no_mensaj", null));
                oferta.setTipoCambio(JsonUtil.getDoubleFromJson(arrResultado.getJSONObject(i), "ti_cambio", null));
                oferta.setMoneda(translatorDAO.translate(Entity.ACCESO, 33, null, JsonUtil.getStringFromJson(arrResultado.getJSONObject(i), "ti_moneda", null)));
                if (JsonUtil.getDoubleFromJson(arrResultado.getJSONObject(i), "im_cuoini", null) != null)
                    oferta.setCuotaInicial(utilService.roundUpto50(JsonUtil.getDoubleFromJson(arrResultado.getJSONObject(i), "im_cuoini", null)) * 1.0);
                oferta.setTea(JsonUtil.getDoubleFromJson(arrResultado.getJSONObject(i), "va_tasanu", null));
                oferta.setTcea(JsonUtil.getDoubleFromJson(arrResultado.getJSONObject(i), "va_tascea", null));
                oferta.setTasaMoratoria(0.0);
                oferta.setMinCuotaInicial(cotizador.isDownPaymentForced() ? cotizador.getCuotaInicial() : null);
                ofertas.add(oferta);
            }
            System.out.println("[Expediente - " + cotizador.getNroExpediente() + "] - Ofertas generadas");
            return ofertas;
        } else if (codigoError == 90) {
            if (forceToMinDownPayment && JsonUtil.getDoubleFromJson(arrResultado.getJSONObject(0), "im_cuoini", null) != null) {
                cotizador.setCuotaInicial(JsonUtil.getDoubleFromJson(arrResultado.getJSONObject(0), "im_cuoini", null));
                cotizador.setDownPaymentForced(true);
                return callCotizar(cotizador, loanApplicationId, false, 0);
            }
        } else if (codigoError == 8) {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), false);
            System.out.println("[Acceso Crediticio Login] - [" + codigoError + "] : " + mensajeError);
            System.out.println("Error de Session Acceso Crediticio + [Expediente - " + cotizador.getNroExpediente() + "]");
            callLogin(true);
            return callCotizar(cotizador, loanApplicationId, forceToMinDownPayment, time++);
        } else {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            throw new SqlErrorMessageException(null, accesoUtilCall.sendErrorMessage(codigoError));
        }

        entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
        throw new Exception();
    }

    public List<Cronograma> callCronograma(Integer nroExpediente, Integer loanApplicationId, int time) throws Exception {

        if (time > 1) throw new Exception("Error de Session Acceso Crediticio + [DNI - " + nroExpediente + "]");
        Gson gson = new Gson();
        String jsonCronograma = gson.toJson(new Expediente(callLogin(false), nroExpediente));
        EntityWebServiceLog<JSONObject> webServiceResponse = accesoUtilCall.call(catalogService.getEntityWebService(EntityWebService.ACCESO_CRONOGRAMA), jsonCronograma, loanApplicationId);
        String mensajeError = null;
        try{
            JSONObject jsonResponse = webServiceResponse.getRestResponse();

            JSONArray arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
            Integer codigoError = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_mensaj", null);
            mensajeError = JsonUtil.getStringFromJson(arrResultado.getJSONObject(0), "no_estado", null);
            List<Cronograma> cronograma = new ArrayList<>();
            if (codigoError == 109) {
                for (int i = 0; i < arrResultado.length(); i++) {
                    Cronograma cuotaCronograma = new Cronograma();
                    JSONObject cuota = arrResultado.getJSONObject(i);
                    cuotaCronograma.setNumCuotas(JsonUtil.getIntFromJson(cuota, "nu_cuotas", null));
                    cuotaCronograma.setFechaVencimiento(JsonUtil.getPostgresDateFromJson(cuota, "fe_vencim", null));
                    cuotaCronograma.setDiasCuota(JsonUtil.getIntFromJson(cuota, "ca_diacuo", null));
                    cuotaCronograma.setSaldoInicial(JsonUtil.getDoubleFromJson(cuota, "im_salini", null));
                    cuotaCronograma.setCapital(JsonUtil.getDoubleFromJson(cuota, "im_capita", null));
                    cuotaCronograma.setInteres(JsonUtil.getDoubleFromJson(cuota, "im_intere", null));
                    cuotaCronograma.setInteresCapitalizado(JsonUtil.getDoubleFromJson(cuota, "im_intcap", null));
                    cuotaCronograma.setCuota(JsonUtil.getDoubleFromJson(cuota, "im_cuotas", null));
                    cuotaCronograma.setSaldoFinal(JsonUtil.getDoubleFromJson(cuota, "im_salfin", null));
                    cuotaCronograma.setSeguroVehicular(JsonUtil.getDoubleFromJson(cuota, "im_seguro", null));
                    cuotaCronograma.setDesgravamen(JsonUtil.getDoubleFromJson(cuota, "im_desgra", null));
                    cuotaCronograma.setCuotaTotal(JsonUtil.getDoubleFromJson(cuota, "im_cuotot", null));
                    cronograma.add(cuotaCronograma);
                }
                System.out.println("[Expediente - " + nroExpediente + "] - Cronograma generado");
                return cronograma;
            } else if (codigoError == 8) {
                entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), false);
                System.out.println("[Acceso Crediticio Login] - [" + codigoError + "] : " + mensajeError);
                System.out.println("Error de Session Acceso Crediticio + [Expediente - " + nroExpediente + "]");
                callLogin(true);
                return callCronograma(nroExpediente, loanApplicationId, time++);
            } else {
                entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
                throw new SqlErrorMessageException(null, accesoUtilCall.sendErrorMessage(codigoError));
            }
        }
        catch (Exception e){
            sendErrorAndRegister(webServiceResponse,mensajeError, EntityWebService.ACCESO_CRONOGRAMA);
            throw  e;
        }
    }

    public Boolean callInformacionAdicional(InformacionAdicional informacionAdicional, Integer loanApplicationId, int time) throws Exception {

        if (time > 1)
            throw new Exception("Error de Session Acceso Crediticio + [DNI - " + informacionAdicional.getNroExpediente() + "]");
        Gson gson = new Gson();
        informacionAdicional.setSesionId(callLogin(false));
        String jsonInformacionAdicional = gson.toJson(informacionAdicional);
        EntityWebServiceLog<JSONObject> webServiceResponse = accesoUtilCall.call(catalogService.getEntityWebService(EntityWebService.ACCESO_INFORMACION_ADICIONAL), jsonInformacionAdicional, loanApplicationId);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        JSONArray arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
        Integer codigoError = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_mensaj", null);
        String mensajeError = JsonUtil.getStringFromJson(arrResultado.getJSONObject(0), "no_estado", null);
        if (codigoError == 125) {
            System.out.println("[Expediente - " + informacionAdicional.getNroExpediente() + "] - Informacion Adicional generada");
            return true;
        } else if (codigoError == 8) {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), false);
            System.out.println("[Acceso Crediticio Login] - [" + codigoError + "] : " + mensajeError);
            System.out.println("Error de Session Acceso Crediticio + [Expediente - " + informacionAdicional.getNroExpediente() + "]");
            callLogin(true);
            return callInformacionAdicional(informacionAdicional, loanApplicationId, time++);
        } else {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            throw new SqlErrorMessageException(null, accesoUtilCall.sendErrorMessage(codigoError));
        }
    }

    public List<PendienteFirma> callPendienteFirma(int time) throws Exception {

        if (time > 1)
            throw new Exception("Error de Session Acceso Crediticio + [Pendientes de Firma]");
        JSONObject requestJson = new JSONObject();
        requestJson.put("p_id_sesion", callLogin(false));
        EntityWebServiceLog<JSONObject> webServiceResponse = accesoUtilCall.call(catalogService.getEntityWebService(EntityWebService.ACCESO_PENDIENTE_FIRMA), requestJson.toString(), null);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        JSONArray arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
        Integer codigoError = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_mensaj", null);
        String mensajeError = JsonUtil.getStringFromJson(arrResultado.getJSONObject(0), "no_estado", null);
        if (codigoError == 135) {
            List<PendienteFirma> pendientesFirma = new ArrayList<>();
            for (int i = 0; i < arrResultado.length(); i++) {
                PendienteFirma pendienteFirma = new PendienteFirma();
                JSONObject resultadoFirma = arrResultado.getJSONObject(i);
                pendienteFirma.setNroExpediente(JsonUtil.getIntFromJson(resultadoFirma, "co_expedi", null));
                pendientesFirma.add(pendienteFirma);
            }
            System.out.println("[Reporte de Resultado de Firmas generado]");
            return pendientesFirma;
        } else if (codigoError == 8) {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), false);
            System.out.println("[Acceso Crediticio Login] - [" + codigoError + "] : " + mensajeError);
            System.out.println("Error de Session Acceso Crediticio + [Lista de Estado de Firmas]");
            callLogin(true);
            return callPendienteFirma(time++);
        } else if (codigoError == 118) {
            System.out.println("[Acceso Crediticio Login] - [" + codigoError + "] : " + mensajeError);
            System.out.println("No hay expedientes + [Lista de Estado de Firmas]");
            return null;
        } else {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            throw new SqlErrorMessageException(null, accesoUtilCall.sendErrorMessage(codigoError));
        }
    }

    public Boolean callAgendarFirmas(Firma firma, Integer loanApplicationId, int time) throws Exception {

        if (time > 1)
            throw new Exception("Error de Session Acceso Crediticio + [DNI - " + firma.getNroExpediente() + "]");
        Gson gson = new Gson();
        firma.setSesionId(callLogin(false));
        String jsonInformacionAdicional = gson.toJson(firma);
        EntityWebServiceLog<JSONObject> webServiceResponse = accesoUtilCall.call(catalogService.getEntityWebService(EntityWebService.ACCESO_AGENDAR_FIRMA), jsonInformacionAdicional, loanApplicationId);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        JSONArray arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
        Integer codigoError = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_mensaj", null);
        String mensajeError = JsonUtil.getStringFromJson(arrResultado.getJSONObject(0), "no_estado", null);
        if (codigoError == 106) {
            System.out.println("[Expediente - " + firma.getNroExpediente() + "] - Agendamiento de firmas generado");
            return true;
        } else if (codigoError == 8) {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), false);
            System.out.println("[Acceso Crediticio Login] - [" + codigoError + "] : " + mensajeError);
            System.out.println("Error de Session Acceso Crediticio + [Expediente - " + firma.getNroExpediente() + "]");
            callLogin(true);
            return callAgendarFirmas(firma, loanApplicationId, time++);
        } else {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            throw new SqlErrorMessageException(null, accesoUtilCall.sendErrorMessage(codigoError));
        }
    }

    public List<EstadoFirma> callEstadoFirmas(int time) throws Exception {

        if (time > 1)
            throw new Exception("Error de Session Acceso Crediticio + [Estado de Firmas]");
        JSONObject requestJson = new JSONObject();
        requestJson.put("p_id_sesion", callLogin(false));
        EntityWebServiceLog<JSONObject> webServiceResponse = accesoUtilCall.call(catalogService.getEntityWebService(EntityWebService.ACCESO_ESTADO_FIRMA), requestJson.toString(), null);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        JSONArray arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
        Integer codigoError = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_mensaj", null);
        String mensajeError = JsonUtil.getStringFromJson(arrResultado.getJSONObject(0), "no_estado", null);
        if (codigoError == 119) {
            List<EstadoFirma> estadoFirmas = new ArrayList<>();
            for (int i = 0; i < arrResultado.length(); i++) {
                EstadoFirma estadoFirma = new EstadoFirma();
                JSONObject resultadoFirma = arrResultado.getJSONObject(i);
                estadoFirma.setNroExpediente(JsonUtil.getIntFromJson(resultadoFirma, "co_expedi", null));
                estadoFirma.setResultadoVerificacion(JsonUtil.getIntFromJson(resultadoFirma, "co_verifi", null));
                estadoFirma.setComentarioVerificacion(JsonUtil.getStringFromJson(resultadoFirma, "de_verifi", null));
                estadoFirma.setFechaVerificacion(JsonUtil.getPostgresDateFromJson(resultadoFirma, "fe_verifi", null));
                estadoFirmas.add(estadoFirma);
            }
            System.out.println("[Reporte de Resultado de Firmas generado]");
            return estadoFirmas;
        } else if (codigoError == 8) {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), false);
            System.out.println("[Acceso Crediticio Login] - [" + codigoError + "] : " + mensajeError);
            System.out.println("Error de Session Acceso Crediticio + [Lista de Estado de Firmas]");
            callLogin(true);
            return callEstadoFirmas(time++);
        } else if (codigoError == 118) {
            System.out.println("[Acceso Crediticio Login] - [" + codigoError + "] : " + mensajeError);
            System.out.println("Error de Session Acceso Crediticio + [Lista de Estado de Firmas]");
            return new ArrayList<>();
        } else {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            throw new SqlErrorMessageException(null, accesoUtilCall.sendErrorMessage(codigoError));
        }

    }

    public List<Expediente> callExpedientesDespachados(int time) throws Exception {

        if (time > 1)
            throw new Exception("Error de Session Acceso Crediticio + [Expedientes despachados]");
        JSONObject requestJson = new JSONObject();
        requestJson.put("p_id_sesion", callLogin(false));
        EntityWebServiceLog<JSONObject> webServiceResponse = accesoUtilCall.call(catalogService.getEntityWebService(EntityWebService.ACCESO_EXPEDIENTES_DESPACHADOS), requestJson.toString(), null);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        JSONArray arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
        Integer codigoError = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_mensaj", null);
        String mensajeError = JsonUtil.getStringFromJson(arrResultado.getJSONObject(0), "no_estado", null);
        if (codigoError == 126) {
            List<Expediente> expedientes = new ArrayList<>();
            for (int i = 0; i < arrResultado.length(); i++) {
                Expediente expedientesDespachados = new Expediente();
                JSONObject despacho = arrResultado.getJSONObject(i);
                expedientesDespachados.setNroExpediente(JsonUtil.getIntFromJson(despacho, "co_expedi", null));
                expedientesDespachados.setFechaDespacho(JsonUtil.getPostgresDateFromJson(despacho, "fe_estdes", null));
                expedientes.add(expedientesDespachados);
            }
            System.out.println("[Reporte de Expedientes Despachados generado]");
            return expedientes;
        } else if (codigoError == 8) {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), false);
            System.out.println("[Acceso Crediticio Login] - [" + codigoError + "] : " + mensajeError);
            System.out.println("Error de Session Acceso Crediticio + [Lista de Expedientes Despachados]");
            callLogin(true);
            return callExpedientesDespachados(time++);
        } else if (codigoError == 118) {
            System.out.println("[Acceso Crediticio Login] - [" + codigoError + "] : " + mensajeError);
            System.out.println("Error de Session Acceso Crediticio + [Lista de Expedientes Despachados]");
            return new ArrayList<>();
        } else {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            throw new SqlErrorMessageException(null, accesoUtilCall.sendErrorMessage(codigoError));
        }
    }

    public List<CatalogoVehicular> callCatalogoVehicular(Integer time) throws Exception {
        if (time > 1)
            throw new Exception("Error de Session Acceso Crediticio + [Catalogo Vehicular]");
        JSONObject requestJson = new JSONObject();
        requestJson.put("p_id_sesion", callLogin(false));
        EntityWebServiceLog<JSONObject> webServiceResponse = accesoUtilCall.call(catalogService.getEntityWebService(EntityWebService.ACCESO_CATALOGO_VEHICULAR), requestJson.toString(), null);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        JSONArray arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
        List<CatalogoVehicular> catalogoVehiculos = new ArrayList<>();
        Integer codigoError;
        for (int i = 0; i < arrResultado.length(); i++) {
            JSONObject catalogo = arrResultado.getJSONObject(i);
            codigoError = JsonUtil.getIntFromJson(catalogo, "co_mensaj", null);
            if (codigoError == 10) {
                Double onlinePrice = JsonUtil.getDoubleFromJson(catalogo, "im_preven", null);
                Double listPrice = JsonUtil.getDoubleFromJson(catalogo, "im_preveh", null);
                if (onlinePrice == null || listPrice == null || (onlinePrice > listPrice)) {
                    continue;
                }
                CatalogoVehicular catalogoVehicular = new CatalogoVehicular();
                catalogoVehicular.setPoliticaVehicular(JsonUtil.getIntFromJson(catalogo, "co_polveh", null));
                catalogoVehicular.setCodOperador(JsonUtil.getIntFromJson(catalogo, "co_operad", null));
                catalogoVehicular.setDescOperador(JsonUtil.getStringFromJson(catalogo, "no_operad", null));
                catalogoVehicular.setCodProducto(JsonUtil.getIntFromJson(catalogo, "co_produc", null));
                catalogoVehicular.setNombreProducto(JsonUtil.getStringFromJson(catalogo, "no_produc", null));
                catalogoVehicular.setCodSubProducto(JsonUtil.getIntFromJson(catalogo, "ti_modelo", null));
                catalogoVehicular.setNombreSubProducto(JsonUtil.getStringFromJson(catalogo, "no_subpro", null));
                catalogoVehicular.setCentroOperaciones(JsonUtil.getIntFromJson(catalogo, "co_cenope", null));
                catalogoVehicular.setNombreCentroOperaciones(JsonUtil.getStringFromJson(catalogo, "no_cenope", null));
                catalogoVehicular.setCodConcesionario(JsonUtil.getIntFromJson(catalogo, "co_conces", null));
                catalogoVehicular.setNombreConcesionario(JsonUtil.getStringFromJson(catalogo, "no_conces", null));
                catalogoVehicular.setTipoEdad(JsonUtil.getIntFromJson(catalogo, "ti_poleda", null));
                catalogoVehicular.setDescTipoEdad(JsonUtil.getStringFromJson(catalogo, "no_poleda", null));
                catalogoVehicular.setCodMarca(JsonUtil.getIntFromJson(catalogo, "co_marcas", null));
                catalogoVehicular.setDescMarca(JsonUtil.getStringFromJson(catalogo, "no_marcas", null));
                catalogoVehicular.setCodModelo(JsonUtil.getIntFromJson(catalogo, "co_modelo", null));
                catalogoVehicular.setDescModelo(JsonUtil.getStringFromJson(catalogo, "no_modelo", null));
                catalogoVehicular.setPrecioVehiculo(onlinePrice);
                catalogoVehicular.setPrecioLista(listPrice);
                catalogoVehicular.setAnhoFabricacion(JsonUtil.getIntFromJson(catalogo, "de_anofab", null));
                catalogoVehicular.setVersionVehicular(JsonUtil.getIntFromJson(catalogo, "co_verveh", null));
                catalogoVehicular.setDescVersionVehicular(JsonUtil.getStringFromJson(catalogo, "no_verveh", null));
                catalogoVehicular.setCodColor(JsonUtil.getIntFromJson(catalogo, "ti_colveh", null));
                catalogoVehicular.setDescColor(JsonUtil.getStringFromJson(catalogo, "no_colveh", null));
                catalogoVehicular.setCodTipoTransmision(JsonUtil.getIntFromJson(catalogo, "ti_traveh", null));
                catalogoVehicular.setDescTipoTransmision(JsonUtil.getStringFromJson(catalogo, "no_traveh", null));
                catalogoVehicular.setCodTipoCombustible(JsonUtil.getStringFromJson(catalogo, "co_tipcom", null));
                catalogoVehicular.setDescTipoCombustible(JsonUtil.getStringFromJson(catalogo, "no_tipcom", null));
                catalogoVehicular.setCodTipoCuotas(JsonUtil.getStringFromJson(catalogo, "ti_cuotas", null));
                catalogoVehicular.setDescTipoCuota(JsonUtil.getStringFromJson(catalogo, "no_cuotas", null));
                catalogoVehicular.setAnoFabricacion(JsonUtil.getIntFromJson(catalogo, "nu_anofab", null));
                catalogoVehiculos.add(catalogoVehicular);
            } else if (codigoError == 8) {
                entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), false);
                System.out.println("[Acceso Crediticio Login] - [" + codigoError + "] : ");
                System.out.println("Error de Session Acceso Crediticio + [Lista de Expedientes Despachados]");
                callLogin(true);
                return callCatalogoVehicular(time++);
            } else {
                entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
                throw new SqlErrorMessageException(null, accesoUtilCall.sendErrorMessage(codigoError));
            }
        }
        return catalogoVehiculos;
    }

    public Boolean callRegistrarDocumentos(Expediente expediente, Integer loanApplicationId, Integer time) throws Exception {

        if (time > 1)
            throw new Exception("Error de Session Acceso Crediticio + [Catalogo Vehicular]");
        expediente.setSesionId(callLogin(false));
        Gson gson = new Gson();
        String jsonCatalogoData = gson.toJson(expediente);
        EntityWebServiceLog<JSONObject> webServiceResponse = accesoUtilCall.call(catalogService.getEntityWebService(EntityWebService.ACCESO_REGISTRAR_DOCUMENTOS), jsonCatalogoData.toString(), loanApplicationId);
        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        JSONArray arrResultado = JsonUtil.getJsonArrayFromJson(jsonResponse, "resultado", null);
        Integer codigoError = JsonUtil.getIntFromJson(arrResultado.getJSONObject(0), "co_mensaj", null);
        String mensajeError = JsonUtil.getStringFromJson(arrResultado.getJSONObject(0), "no_estado", null);
        if (codigoError == 129) {
            return true;
        } else if (codigoError == 8) {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), false);
            System.out.println("[Acceso Crediticio Login] - [" + codigoError + "] : " + mensajeError);
            System.out.println("Error de Session Acceso Crediticio + [Registrar Documentos]");
            callLogin(true);
            return callRegistrarDocumentos(expediente, loanApplicationId, time++);
        } else {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            throw new SqlErrorMessageException(null, accesoUtilCall.sendErrorMessage(codigoError));
        }
    }

    public String callGenerarExpediente(LoanApplication loanApplication, Credit credit) throws Exception {

        // Fill the expediente to send
        Person person = personDAO.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), true);
        User user = userDAO.getUser(loanApplication.getUserId());
        PersonOcupationalInformation ocupationalInformation = personDAO.getPersonOcupationalInformation(Configuration.getDefaultLocale(), loanApplication.getPersonId())
                .stream().filter(p -> p.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst().orElse(null);
        Integer expedienteId = loanApplication.getEntityApplicationCode() != null ? Integer.parseInt(loanApplication.getEntityApplicationCode()) : null;
        PersonContactInformation contactInformation = personDAO.getPersonContactInformation(Configuration.getDefaultLocale(), loanApplication.getPersonId());
        PersonBankAccountInformation bankAccountInformation = personDAO.getPersonBankAccountInformation(Configuration.getDefaultLocale(), loanApplication.getPersonId());
        LoanOffer loanOffer = loanApplicationDao.getLoanOffers(loanApplication.getId()).stream().filter(o -> o.getSelected()).findFirst().orElse(null);
        List<DisggregatedAddress> dissgregatedAddresses = personDAO.getDisggregatedAddress(person.getId());
        DisggregatedAddress homeAddress = dissgregatedAddresses.stream().filter(a -> a.getType() == 'H').findFirst().orElse(null);
        DisggregatedAddress jobAddress = dissgregatedAddresses.stream().filter(a -> a.getType() == 'J').findFirst().orElse(null);

        AccesoExpedienteLibreDisponibilidad expediente = new AccesoExpedienteLibreDisponibilidad(
                expedienteId,
                translatorDAO,
                person,
                user,
                ocupationalInformation,
                contactInformation,
                loanOffer,
                homeAddress,
                jobAddress,
                bankAccountInformation
        );
        expediente.setEstadoInformacion(loanApplication.getEntityApplicationCode() != null ? 1 : 2);
        if(credit != null) expediente.setCreditoEntidad(credit.getId().toString());
        EntityWsResult entityWsResult = securityDao.getEntityResultWS(loanApplication.getId(), EntityWebService.ACCESO_EVALUACION_GENERICA);
        if (entityWsResult != null) {
            EvaluacionGenericaResponse response = new Gson().fromJson(entityWsResult.getResult().toString(), EvaluacionGenericaResponse.class);
            if (response != null && response.getCodigoEvaluacion() != null) expediente.setCodigoEvaluacion(response.getCodigoEvaluacion().toString());
        }

        // Send the original product tea
        RateCommission accesoRateCommission = catalogService.getRateCommissionClusters().stream()
                .flatMap(c -> c.getRateCommissions() != null ? c.getRateCommissions().stream() : Stream.empty())
                .filter(c -> c.getPriceId() != null && c.getPriceId().equals(9101))
                .findFirst().orElse(null);
        expediente.setSimulacionTea(accesoRateCommission.getEffectiveAnualRate());
        // Send the original installment
        expediente.setSimulacionCuotas(expediente.getSimulacionCuotas());
        String jsonExpediente = new Gson().toJson(expediente);

        // Call the WS
        Map<String, String> urlParams = new HashMap<>();
        String tipoParamWsAcceso = loanApplication.getEntityApplicationCode() != null ? "2" : "1";// Inicialmente sera 1 (crear) la rspta esperada es (78)79. Para convertir a credito sera 2 (actualizar) la rspta esperada es (104)109
        urlParams.put("TIPO_PARAM", tipoParamWsAcceso);
        EntityWebServiceLog<JSONObject> webServiceResponse = accesoUtilCall.callV2(catalogService.getEntityWebService(EntityWebService.ACCESO_GENERAR_EXPEDIENTE_LD), jsonExpediente, loanApplication.getId(), urlParams);
        String errorDetail = null;
        try{
            JSONObject jsonResponse = webServiceResponse.getRestResponse();

            JSONObject resultJson = JsonUtil.getJsonObjectFromJson(jsonResponse, "Expediente", null);
            if(resultJson == null){
                throw new SqlErrorMessageException(null, "Error de comunicación con Acceso Crediticio");
            }
            int codMensaje = resultJson.getInt("codigoMensaj");
            String codigoEstado = JsonUtil.getStringFromJson(resultJson, "codigoEstado", null);
            String returnValue = null;
            if (Integer.parseInt(tipoParamWsAcceso) == 1 && Arrays.asList(78, 79).contains(codMensaje)) {// CREACION DE EXPEDIENTE
                returnValue = String.valueOf(resultJson.getInt("codigoExpedi"));
            } else if (Integer.parseInt(tipoParamWsAcceso) == 2 && Arrays.asList(104, 109).contains(codMensaje)) {// ACTUALIZACION (A CREDITO)
                returnValue = resultJson.getString("codigoCredito");
            } else if (Arrays.asList(112).contains(codMensaje) && loanApplication.getEntityApplicationCode() != null) {// Expediente aprobado, ya no se pueden realizar cambios
                List<EntityWebServiceLog> wsLogs = webServiceDao.getEntityWebServiceLog(loanApplication.getId(), EntityWebService.ACCESO_GENERAR_EXPEDIENTE_LD);
                String codCredito = wsLogs.stream()
                        .sorted(Comparator.comparing(EntityWebServiceLog::getStartDate, Comparator.nullsLast(Comparator.reverseOrder())))
                        .filter(l -> l.getStatus() == 'S' && l.getResponse() != null)
                        .map(l -> {
                            JSONObject jExp = JsonUtil.getJsonObjectFromJson(new JSONObject(l.getResponse()), "Expediente", null);
                            return JsonUtil.getStringFromJson(jExp, "codigoCredito", null);
                        })
                        .filter(l -> l != null)
                        .findFirst().orElse(null);

                if (codCredito != null) {
                    returnValue = codCredito;
                } else {
                    entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), false);
                    errorDetail = "La  solicitud ya fue aprobada en Acceso, pero no esta registrada la ultima llamada correcta";
                    throw new SqlErrorMessageException(null, "Error de comunicación con Acceso Crediticio" + "\n" + "[La  solicitud ya fue aprobada en Acceso, pero no esta registrada la ultima llamada correcta]");
                }
            } else {
                entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), false);
                errorDetail = accesoUtilCall.sendErrorMessage(codMensaje);
                throw new SqlErrorMessageException(null, String.format("Error de comunicación con Acceso Crediticio" + "\n" + "[%s]", accesoUtilCall.sendErrorMessage(codMensaje, codigoEstado)));
            }

            // Validate that the acceso schedule is the same as ours
            JSONArray cronogramaJson = JsonUtil.getJsonArrayFromJson(jsonResponse, "Cronograma", null);
            if (cronogramaJson != null && cronogramaJson.length() > 0) {
                for (int i = credit.getOriginalSchedule().size() - 3; i < credit.getOriginalSchedule().size(); i++) {
                    OriginalSchedule creditSchedule = credit.getOriginalSchedule().get(i);
                    JSONObject accesoSchedule = StreamSupport
                            .stream(cronogramaJson.spliterator(), false)
                            .map(j -> (JSONObject) j)
                            .filter(j -> JsonUtil.getIntFromJson(j, "numeroCuotaAmo", 0).equals(creditSchedule.getInstallmentId()))
                            .findFirst().orElse(null);

                    if (creditSchedule.getInstallmentAmount() != accesoSchedule.getDouble("importeCuotot")) {

//                    double difference = Math.abs(creditSchedule.getInstallmentAmount() - accesoSchedule.getDouble("importeCuotot"));
//                    // Call the function to insert the acceso schedule
//                    if(difference >= 1){
//                        throw new SqlErrorMessageException(null, "La diferencia entre las cuotas del cronograma con Acceso es mayor al limite establecido, por lo que no se procedio con la creacion del credito. Revisar con sistemas.");
//                    }else{
                        creditDao.registerSchedule(credit.getId(), cronogramaJson.toString());

                        credit = creditDao.getCreditByID(credit.getId(), Configuration.getDefaultLocale(), true, Credit.class);
                        creditService.generateCreditTcea(credit);
//                    }

                        break;
                    }
                }
            }
            return returnValue;
        }
        catch (Exception e){
            sendErrorAndRegister(webServiceResponse,errorDetail, EntityWebService.ACCESO_GENERAR_EXPEDIENTE_LD);
            throw  e;
        }
    }

    public void callGenerarDocumentacion(LoanApplication loanApplication) throws Exception {

        // Fill the array to send
        List<UserFile> userFiles = loanApplicationDao.getLoanApplicationUserFiles(loanApplication.getId());
        JSONArray files = new JSONArray();
        // Selfie
        UserFile fileSelfie = userFiles.stream().filter(u -> u.getFileType().getId() == UserFileType.SELFIE).findFirst().orElse(null);
        JSONObject selfieJson = new JSONObject();
        selfieJson.put("tipoDocumento", "351");
        selfieJson.put("urlDocumento", fileService.generatePublicUserFileUrl(fileSelfie.getId(), true));
        files.put(selfieJson);
        // DNI
        UserFile fileDni = userFiles.stream().filter(u -> u.getFileType().getId() == UserFileType.DNI_MERGED).findFirst().orElse(null);
        JSONObject dniJson = new JSONObject();
        dniJson.put("tipoDocumento", "104");
        dniJson.put("urlDocumento", fileService.generatePublicUserFileUrl(fileDni.getId(), true));
        files.put(dniJson);
        // Boleta
        UserFile fileBoleta = userFiles.stream().filter(u -> u.getFileType().getId() == UserFileType.BOLETA_PAGO).findFirst().orElse(null);
        JSONObject boletaJson = new JSONObject();
        boletaJson.put("tipoDocumento", "182");
        boletaJson.put("urlDocumento", fileService.generatePublicUserFileUrl(fileBoleta.getId(), true));
        files.put(boletaJson);
        // RECIBO DE SERVICIOS
        UserFile fileServicios = userFiles.stream().filter(u -> u.getFileType().getId() == UserFileType.COMPROBANTE_DIRECCION).findFirst().orElse(null);
        if(fileServicios != null){
            JSONObject comprobanteServiciosJson = new JSONObject();
            comprobanteServiciosJson.put("tipoDocumento", "028");
            comprobanteServiciosJson.put("urlDocumento", fileService.generatePublicUserFileUrl(fileServicios.getId(), true));
            files.put(comprobanteServiciosJson);
        }

        UserFile contratoFirma = userFiles.stream()
                .filter(u -> u.getFileType().getId() == UserFileType.CONTRATO_FIRMA)
                .sorted(Comparator.comparing(UserFile::getUploadTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .findFirst().orElse(null);
        // Pagaré
        JSONObject pagareJson = new JSONObject();
        pagareJson.put("tipoDocumento", "030");
        pagareJson.put("urlDocumento", fileService.generateUserPdfSplittedUrl(contratoFirma, 17, 19));
        files.put(pagareJson);
        // Hoja resumen
        JSONObject hojaResumenJson = new JSONObject();
        hojaResumenJson.put("tipoDocumento", "132");
        hojaResumenJson.put("urlDocumento", fileService.generateUserPdfSplittedUrl(contratoFirma, 20, 22));
        files.put(hojaResumenJson);
        // Cronograma
        JSONObject cronogramaJson = new JSONObject();
        cronogramaJson.put("tipoDocumento", "069");
        cronogramaJson.put("urlDocumento", fileService.generateUserPdfSplittedUrl(contratoFirma, 23, 27));
        files.put(cronogramaJson);
        // Solicitud de credito
        JSONObject solicitudJson = new JSONObject();
        solicitudJson.put("tipoDocumento", "233");
        solicitudJson.put("urlDocumento", fileService.generateUserPdfSplittedUrl(contratoFirma, 1, 4));
        files.put(solicitudJson);
        // Contrato de credito
        JSONObject contratoJson = new JSONObject();
        contratoJson.put("tipoDocumento", "038");
        contratoJson.put("urlDocumento", fileService.generateUserPdfSplittedUrl(contratoFirma, 5, 16));
        files.put(contratoJson);
        // Carta de aprobación
        JSONObject cartaDeAprobacion = new JSONObject();
        cartaDeAprobacion.put("tipoDocumento", "025");
        cartaDeAprobacion.put("urlDocumento", fileService.generateUserPdfSplittedUrl(contratoFirma, 31, 31));
        files.put(cartaDeAprobacion);

        String jsonDocumentos = files.toString();

        // Call the WS
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("EXPEDIENTE", loanApplication.getEntityApplicationCode());
        EntityWebServiceLog<JSONObject> webServiceResponse = accesoUtilCall.callV2(catalogService.getEntityWebService(EntityWebService.ACCESO_GENERAR_DOCUMENTACION_LD), jsonDocumentos, loanApplication.getId(), urlParams);
        try{
            JSONObject jsonResponse = webServiceResponse.getRestResponse();
            int codMensaje = jsonResponse.getInt("codigoMensaje");
            if (codMensaje == 90 || codMensaje == 101) {
                return;
            } else {
                entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), false);
                throw new SqlErrorMessageException(null, "Error de comunicación con Acceso Crediticio");
            }
        }
        catch (Exception e){
            sendErrorAndRegister(webServiceResponse,null, EntityWebService.ACCESO_GENERAR_DOCUMENTACION_LD);
            throw  e;
        }
    }

    public void callScoreLdConsumo(LoanApplication loanApplication) throws Exception {
        Person person = personDAO.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
        Map<String, String> urlParams = new HashMap<>();

        PersonOcupationalInformation ocupationalInformation = personDAO.getPersonOcupationalInformation(Configuration.getDefaultLocale(), loanApplication.getPersonId())
                .stream().filter(p -> p.getNumber() == PersonOcupationalInformation.PRINCIPAL).findFirst().orElse(null);

        JSONObject requestBody = new JSONObject();
        requestBody.put("tipoDocumento", person.getDocumentType().getId());
        requestBody.put("documentoIdentidad", person.getDocumentNumber());
        requestBody.put("educacion", translatorDAO.translate(Entity.ACCESO, 24, person.getStudyLevel().getId().toString(), null));
        requestBody.put("estadoCivil", translatorDAO.translate(Entity.ACCESO, 23, person.getMaritalStatus().getId().toString(), null));
        requestBody.put("fechaNacimiento", new SimpleDateFormat("yyyy-MM-dd").format(person.getBirthday()));
        requestBody.put("tipoEvaluacion", "1");
        requestBody.put("ingresoMensual", ocupationalInformation != null && ocupationalInformation.getFixedGrossIncome() != null ? ocupationalInformation.getFixedGrossIncome() : null);

        EntityWebServiceLog<JSONObject> webServiceResponse = accesoUtilCall.callV2(catalogService.getEntityWebService(EntityWebService.ACCESO_SCORE_LD_CONSUMO), requestBody.toString(), loanApplication.getId(), urlParams);
        try{
            JSONObject jsonResponse = webServiceResponse.getRestResponse();
            webServiceDao.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.ACCESO_SCORE_LD_CONSUMO, jsonResponse.toString());
        }
        catch (Exception e){
            sendErrorAndRegister(webServiceResponse,null, EntityWebService.ACCESO_SCORE_LD_CONSUMO);
            throw  e;
        }
    }

    private void sendErrorAndRegister(EntityWebServiceLog webServiceLog, String errorDetail, int entityWsId){
        EntityWebService entityWebService = catalogService.getEntityWebService(webServiceLog != null ? webServiceLog.getEntityWebServiceId() : entityWsId);
        errorService.sendErrorCriticSlack(String.format("ERROR ACCESO: \n Mensaje: ERROR %s \n LOAN : %s,  Response: %s",  entityWebService.getWbeserviceName(), webServiceLog != null ? webServiceLog.getLoanApplicationId()  : "-", webServiceLog != null ? webServiceLog.getResponse() : "-"));
        if(webServiceLog != null && webServiceLog.getLoanApplicationId() != null) errorEntityDao.addEntityError(webServiceLog.getLoanApplicationId(),entityWebService.getEntityId(), entityWebService.getId(), errorDetail != null ? errorDetail : "Error conexión con servicio", webServiceLog.getId());
    }

}
