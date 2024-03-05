package com.affirm.fdlm;

import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.TranslatorDAO;
import com.affirm.common.dao.WebServiceDAO;
import com.affirm.common.model.catalog.Entity;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.catalog.HousingType;
import com.affirm.common.model.catalog.IdentityDocumentType;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EntityWebServiceUtil;
import com.affirm.common.service.PersonService;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.SqlErrorMessageException;
import com.affirm.fdlm.creditoconsumo.request.ConsultaCreditoRequest;
import com.affirm.fdlm.creditoconsumo.request.CrearCreditoRequest;
import com.affirm.fdlm.creditoconsumo.request.ObtenerSucursalRequest;
import com.affirm.fdlm.creditoconsumo.response.Sucursal;
import com.affirm.fdlm.datacredito.model.*;
import com.affirm.fdlm.listascontrol.model.ConsultaPersonaRequest;
import com.affirm.fdlm.listascontrol.model.ConsultaResponse;
import com.affirm.fdlm.listascontrol.model.ConsultaSolicitudRequest;
import com.affirm.fdlm.topaz.model.ExecuteRequest;
import com.affirm.fdlm.topaz.model.ExecuteResponse;
import com.affirm.fdlm.util.FdlmUtilCall;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.security.model.EntityWsResult;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FdlmServiceCall {
    private static final Logger logger = Logger.getLogger(FdlmServiceCall.class);

    private final FdlmUtilCall fdlmUtilCall;
    private final CatalogService catalogService;
    private final PersonDAO personDAO;
    private final TranslatorDAO translatorDAO;
    private final WebServiceDAO webServiceDao;
    private final SecurityDAO securityDAO;
    private final EntityWebServiceUtil entityWebServiceUtil;
    private final PersonService personService;

    @Autowired
    public FdlmServiceCall(FdlmUtilCall fdlmServiceCall, CatalogService catalogService, PersonDAO personDAO,
                           WebServiceDAO webServiceDao, TranslatorDAO translatorDAO, SecurityDAO securityDAO,
                           EntityWebServiceUtil entityWebServiceUtil, PersonService personService) {
        this.fdlmUtilCall = fdlmServiceCall;
        this.catalogService = catalogService;
        this.personDAO = personDAO;
        this.translatorDAO = translatorDAO;
        this.securityDAO = securityDAO;
        this.webServiceDao = webServiceDao;
        this.entityWebServiceUtil = entityWebServiceUtil;
        this.personService = personService;
    }

    public DatosClienteResponse callGetDatosCliente(LoanApplication loanApplication) throws Exception {
        Locale locale = Configuration.getDefaultLocale();
        Person person = personDAO.getPerson(loanApplication.getPersonId(), false, locale);
        PersonOcupationalInformation ocupationalInformation = personService.getPrincipalOcupationalInormation(loanApplication.getPersonId(), locale);

        JSONObject translateLocality = translatorDAO.translateLocality(Entity.FUNDACION_DE_LA_MUJER, ocupationalInformation.getDistrict().getDistrictId());
        Sucursal sucursal = callObtenerSucursal(loanApplication.getId(), translateLocality);

        ParametrosWSF request = new ParametrosWSF();

        if (IdentityDocumentType.COL_CEDULA_CIUDADANIA == person.getDocumentType().getId()) {
            request.setTipoId(ConsultaPersonaRequest.TIPO_DOCUMENTO_CEDULA_CIUDADANIA);
        }

        request.setNumeroId(person.getDocumentNumber());
        request.setNombre(person.getFirstSurname());
        request.setTipoConsulta("1"); // consulta general
        request.setCentralRiesgo("2"); // datacredito
        request.setIdSolicitud(null);
        request.setUsuario("LJC3");
        request.setTipoResultado("1");

        if (sucursal != null && sucursal.getCodigo() != null) {
            request.setSucursal(sucursal.getCodigo().toString());
        }

        Gson gson = new Gson();
        String jsonRequest = gson.toJson(request);

        EntityWebServiceLog<JSONObject> webServiceResponse = fdlmUtilCall.call(
                catalogService.getEntityWebService(EntityWebService.FDLM_DATACREDITO_DATOS_CLIENTE), jsonRequest, loanApplication.getId(), null);

        JSONObject jsonResponse = webServiceResponse.getRestResponse();

        webServiceDao.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.FDLM_DATACREDITO_DATOS_CLIENTE, jsonResponse.toString());

        DatosClienteResponse response = gson.fromJson(jsonResponse.toString(), DatosClienteResponse.class);

        logger.debug(response);

        return response;
    }

    public ExisteInformacionLocalResponse callGetExiteInformacionLocal(ParametrosWSF request, Integer loanApplicationId) throws Exception {
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(request);

        EntityWebServiceLog<JSONObject> webServiceResponse = fdlmUtilCall.call(
                catalogService.getEntityWebService(EntityWebService.FDLM_DATACREDITO_EXISTE_INFORMACION_LOCAL), jsonRequest, loanApplicationId, null);

        JSONObject jsonResponse = webServiceResponse.getRestResponse();
        webServiceDao.registerEntityWebServiceResult(loanApplicationId, EntityWebService.FDLM_DATACREDITO_EXISTE_INFORMACION_LOCAL, jsonResponse.toString());
        ExisteInformacionLocalResponse response = gson.fromJson(jsonResponse.toString(), ExisteInformacionLocalResponse.class);

        logger.debug(response);

        return response;
    }

    public ProximaConsultaResponse callGetProximaConsulta(ParametrosWSF request, Integer loanApplicationId) throws Exception {
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(request);

        EntityWebServiceLog<JSONObject> webServiceResponse = fdlmUtilCall.call(
                catalogService.getEntityWebService(EntityWebService.FDLM_DATACREDITO_PROXIMA_CONSULTA), jsonRequest, loanApplicationId, null);

        JSONObject jsonResponse = webServiceResponse.getRestResponse();
        webServiceDao.registerEntityWebServiceResult(loanApplicationId, EntityWebService.FDLM_DATACREDITO_PROXIMA_CONSULTA, jsonResponse.toString());
        ProximaConsultaResponse response = gson.fromJson(jsonResponse.toString(), ProximaConsultaResponse.class);

        logger.debug(response);

        return response;
    }

    public ReporteClienteTPZResponse callGetReporteClienteTPZ(ParametrosWSF request, Integer loanApplicationId) throws Exception {
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(request);

        EntityWebServiceLog<JSONObject> webServiceResponse = fdlmUtilCall.call(
                catalogService.getEntityWebService(EntityWebService.FDLM_DATACREDITO_REPORTE_CLIENTE_TPZ), jsonRequest, loanApplicationId, null);

        JSONObject jsonResponse = webServiceResponse.getRestResponse();
        webServiceDao.registerEntityWebServiceResult(loanApplicationId, EntityWebService.FDLM_DATACREDITO_REPORTE_CLIENTE_TPZ, jsonResponse.toString());
        ReporteClienteTPZResponse response = gson.fromJson(jsonResponse.toString(), ReporteClienteTPZResponse.class);

        logger.debug(response);

        return response;
    }

    public UltimaConsultaResponse callGetUltimaConsulta(ParametrosWSF request, Integer loanApplicationId) throws Exception {
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(request);

        EntityWebServiceLog<JSONObject> webServiceResponse = fdlmUtilCall.call(
                catalogService.getEntityWebService(EntityWebService.FDLM_DATACREDITO_ULTIMA_CONSULTA), jsonRequest, loanApplicationId, null);

        JSONObject jsonResponse = webServiceResponse.getRestResponse();
        webServiceDao.registerEntityWebServiceResult(loanApplicationId, EntityWebService.FDLM_DATACREDITO_ULTIMA_CONSULTA, jsonResponse.toString());
        UltimaConsultaResponse response = gson.fromJson(jsonResponse.toString(), UltimaConsultaResponse.class);

        logger.debug(response);

        return response;
    }

    public ConsultaResponse callConsultarPersona(LoanApplication loanApplication) throws Exception {
        Person person = personDAO.getPerson(loanApplication.getPersonId(), false, Configuration.getDefaultLocale());

        ConsultaPersonaRequest request = new ConsultaPersonaRequest();
        request.setNumeroSolicitud(null);
        request.setNumeroDocumento(person.getDocumentNumber());
        request.setRolSolicitud(ConsultaPersonaRequest.ROL_SOLICITUD_TITULAR);

        if (IdentityDocumentType.COL_CEDULA_CIUDADANIA == person.getDocumentType().getId())
            request.setTipoDocumento(ConsultaPersonaRequest.TIPO_DOCUMENTO_CEDULA_CIUDADANIA);

        Gson gson = new Gson();
        String jsonRequest = gson.toJson(request);

        EntityWebServiceLog<JSONObject> webServiceResponse = fdlmUtilCall.call(
                catalogService.getEntityWebService(EntityWebService.FDLM_LISTAS_CONTROL_CONSULTAR_PERSONA), jsonRequest, loanApplication.getId(), null);

        JSONObject jsonResponse = webServiceResponse.getRestResponse();
        webServiceDao.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.FDLM_LISTAS_CONTROL_CONSULTAR_PERSONA, jsonResponse.toString());

        ConsultaResponse consultaResponse = gson.fromJson(jsonResponse.toString(), ConsultaResponse.class);
        logger.debug(consultaResponse);

        return consultaResponse;
    }

    public List<ConsultaResponse> callConsultarSolicitud(ConsultaSolicitudRequest request, Integer loanApplicationId) throws Exception {
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(request);

        EntityWebServiceLog<JSONArray> webServiceResponse = fdlmUtilCall.call(
                catalogService.getEntityWebService(EntityWebService.FDLM_LISTAS_CONTROL_CONSULTAR_SOLICITUD), jsonRequest, loanApplicationId);

        JSONArray jsonArrayResponse = webServiceResponse.getRestResponse();
        webServiceDao.registerEntityWebServiceResult(loanApplicationId, EntityWebService.FDLM_LISTAS_CONTROL_CONSULTAR_SOLICITUD, jsonArrayResponse.toString());

        List<ConsultaResponse> consultaResponses = new ArrayList<>();

        if (jsonArrayResponse != null) {
            Iterator<Object> iterator = jsonArrayResponse.iterator();

            while(iterator.hasNext()) {
                Object item = iterator.next();

                consultaResponses.add(gson.fromJson(item.toString(), ConsultaResponse.class));
            }
        }

        logger.debug(consultaResponses);

        return consultaResponses;
    }

    public ExecuteResponse callExecute(ExecuteRequest request, Integer loanApplicationId) throws Exception {
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(request);

        EntityWebServiceLog<JSONObject> webServiceResponse = fdlmUtilCall.call(
                catalogService.getEntityWebService(EntityWebService.FDLM_TOPAZ_EXECUTE), jsonRequest, loanApplicationId, null);

        JSONObject jsonResponse = webServiceResponse.getRestResponse();
        webServiceDao.registerEntityWebServiceResult(loanApplicationId, EntityWebService.FDLM_TOPAZ_EXECUTE, jsonResponse.toString());
        String responseJson = JsonUtil.getStringFromJson(jsonResponse, "responseJson", null);
        ExecuteResponse executeResponse = gson.fromJson(responseJson, ExecuteResponse.class);

        logger.debug(executeResponse);

        return executeResponse;
    }

    public void callConsultarCredito(LoanApplication loanApplication) throws Exception {
        Person person = personDAO.getPerson(loanApplication.getPersonId(), false, Configuration.getDefaultLocale());

        ConsultaCreditoRequest request = new ConsultaCreditoRequest();
        request.setProducto(obtenerProducto());
        request.setNumeroDocumento(Long.parseLong(person.getDocumentNumber()));

        if (IdentityDocumentType.COL_CEDULA_CIUDADANIA == person.getDocumentType().getId())
            request.setTipoDocumento(ConsultaPersonaRequest.TIPO_DOCUMENTO_CEDULA_CIUDADANIA);

        Gson gson = new Gson();
        String jsonRequest = gson.toJson(request);

        EntityWebServiceLog<JSONObject> webServiceResponse = fdlmUtilCall.call(
                catalogService.getEntityWebService(EntityWebService.FDLM_CREDITO_CONSUMO_CONSULTAR_CREDITO), jsonRequest, loanApplication.getId(), null);

        JSONObject jsonResponse = webServiceResponse.getRestResponse();
        webServiceDao.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.FDLM_CREDITO_CONSUMO_CONSULTAR_CREDITO, jsonResponse.toString());

        logger.debug(jsonResponse);
    }

    public Sucursal callObtenerSucursal(Integer loanApplicationId, JSONObject localityTranslationJsonObject) throws Exception {

        ObtenerSucursalRequest request = new ObtenerSucursalRequest(localityTranslationJsonObject);

        Gson gson = new Gson();
        String jsonRequest = gson.toJson(request);

        EntityWebServiceLog<JSONObject> webServiceResponse = fdlmUtilCall.call(
                catalogService.getEntityWebService(EntityWebService.FDLM_TOPAZ_OBTENER_SUCURSAL), jsonRequest, loanApplicationId, null);

        JSONObject jsonObjectResponse = webServiceResponse.getRestResponse();
        logger.debug(jsonObjectResponse);

        webServiceDao.registerEntityWebServiceResult(loanApplicationId, EntityWebService.FDLM_TOPAZ_OBTENER_SUCURSAL, jsonObjectResponse.toString());

        JSONObject sucursalJsonObject = JsonUtil.getJsonObjectFromJson(jsonObjectResponse, "DATOS", new JSONObject());
        Sucursal sucursal = gson.fromJson(sucursalJsonObject.toString(), Sucursal.class);

        return sucursal;
    }

    public void callCrearCredito(LoanApplication loanApplication, LoanOffer offerSelected) throws Exception {
        Locale locale = Configuration.getDefaultLocale();
        Person person = personDAO.getPerson(loanApplication.getPersonId(), false, locale);
        PersonContactInformation contactInformation = personDAO.getPersonContactInformation(locale, loanApplication.getPersonId());

        PersonOcupationalInformation ocupationalInformation = personService.getPrincipalOcupationalInormation(loanApplication.getPersonId(), locale);
        JSONObject translateLocality = translatorDAO.translateLocality(Entity.FUNDACION_DE_LA_MUJER, ocupationalInformation.getDistrict().getDistrictId());

        EntityWsResult consultaCreditoWsResult = securityDAO.getEntityResultWS(loanApplication.getId(), EntityWebService.FDLM_CREDITO_CONSUMO_CONSULTAR_CREDITO);

        CrearCreditoRequest request = new CrearCreditoRequest();
        request.setTipoCredito(consultaCreditoWsResult.getResult().getString("DATOS"));
        request.setNumeroSolicitud(generarNumeroSolicitud(loanApplication, translateLocality));
        request.setProducto(obtenerProducto());
        request.setNumeroDocumento(Long.parseLong(person.getDocumentNumber()));

        if (IdentityDocumentType.COL_CEDULA_CIUDADANIA == person.getDocumentType().getId())
            request.setTipoDocumento(ConsultaPersonaRequest.TIPO_DOCUMENTO_CEDULA_CIUDADANIA);

        request.setPrimerApellido(person.getFirstSurname());
        request.setSegundoApellido(person.getLastSurname() != null ? person.getLastSurname() : "");
        request.setPrimerNombre(person.getFirstName());
        request.setSegundoNombre("");
        request.setGenero(obtenerGenero(loanApplication.getId()));
        request.setDireccionDomicilio(contactInformation.getAddressStreetName());
        request.setPais("CO");
        request.setDepartamento(JsonUtil.getLongFromJson(translateLocality, "entity_department_id", null));
        request.setMunicipio(JsonUtil.getLongFromJson(translateLocality, "entity_province_id", null));
        request.setBarrio(JsonUtil.getLongFromJson(translateLocality, "entity_locality_id", null));

        switch (contactInformation.getHousingType().getId()) {
            case HousingType.OWN:
                request.setTipoVivienda(CrearCreditoRequest.TIPO_VIVIENDA_PROPIA);
                break;
            case HousingType.RENTED:
                request.setTipoVivienda(CrearCreditoRequest.TIPO_VIVIENDA_ALQUILADA);
                break;
            case HousingType.FAMILY:
                request.setTipoVivienda(CrearCreditoRequest.TIPO_VIVIENDA_FAMILIAR);
                break;
            default:
                request.setTipoVivienda(CrearCreditoRequest.TIPO_VIVIENDA_OTROS);
                break;
        }

        request.setProcedencia(JsonUtil.getIntFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.FDM_PROCEDENCIA_SOLICITUD.getKey(), null));
        request.setMontoSolicitado(offerSelected.getAmmount().intValue());
        request.setValorCuota(offerSelected.getInstallmentAmmount());
        request.setNumeroCuotas(offerSelected.getInstallments());
        request.setTipoCuenta(null);
        request.setNombreBanco(null);
        request.setNumeroCuenta(null);
        request.setCodigoBanco(null);
        request.setFechaDesfase(new SimpleDateFormat("yyyy-MM-dd").format(loanApplication.getFirstDueDate()));
        request.setFechaNacimiento(new SimpleDateFormat("yyyy-MM-dd").format(person.getBirthday()));

        Gson gson = new Gson();
        String jsonRequest = gson.toJson(request);

        EntityWebServiceLog<JSONObject> webServiceResponse = fdlmUtilCall.call(
                catalogService.getEntityWebService(EntityWebService.FDLM_TOPAZ_CREAR_CREDITO), jsonRequest, loanApplication.getId(), null);

        JSONObject jsonResponse = webServiceResponse.getRestResponse();
        logger.debug(jsonResponse);

        webServiceDao.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.FDLM_TOPAZ_CREAR_CREDITO, jsonResponse.toString());

        Integer codigo = JsonUtil.getIntFromJson(jsonResponse, "CODIGO", null);

        if (codigo != 1) {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), false);
            throw new SqlErrorMessageException(null, String.format("Error al crear credito FDLM" + "\n" + "[%s]", ""));
        }
    }

    private Long generarNumeroSolicitud(LoanApplication loanApplication, JSONObject localityTranslationJsonObject) throws Exception {
        Sucursal sucursal = callObtenerSucursal(loanApplication.getId(), localityTranslationJsonObject);

        if (sucursal != null && sucursal.getCodigo() != null) {
            ExecuteRequest executeRequest = new ExecuteRequest();
            executeRequest.setServiceName("NumeradoresServicio");
            executeRequest.setNumero("9423");
            executeRequest.setSucursal(sucursal.getCodigo().toString());

            ExecuteResponse executeResponse = callExecute(executeRequest, loanApplication.getId());
            Long valorNumerador = executeResponse.getTopazMiddleWareResponse().getNumerators().getNumerator().getValue();

            DateFormat df = new SimpleDateFormat("yy");
            String last2digitsYear = df.format(Calendar.getInstance().getTime());

            return Long.parseLong(sucursal.getCodigo() + last2digitsYear + valorNumerador);
        }

        return null;
    }

    private Character obtenerGenero(Integer loanApplicationId) throws Exception {
        EntityWsResult datosClienteDatacreditoWsResult = securityDAO.getEntityResultWS(loanApplicationId, EntityWebService.FDLM_DATACREDITO_DATOS_CLIENTE);
        JSONObject entityWsResultJson = datosClienteDatacreditoWsResult.getResult();
        DatosClienteResponse response = new Gson().fromJson(entityWsResultJson.toString(), DatosClienteResponse.class);
        JSONObject result = new JSONObject(response.getResult().getDatosResultado());

        JSONObject informe = result.getJSONObject("Informe");
        Integer genero = informe.getJSONObject("NaturalNacional").getInt("genero");

        return genero == 4 ? 'M' : 'F';
    }

    private int obtenerProducto() {
        return Configuration.hostEnvIsProduction() ? 0 : 422; // TODO: Producto value for production
    }

    public Integer obtenerTopazConsultarCredito(Integer loanApplicationId) throws Exception {
        EntityWsResult consultarCredito = securityDAO.getEntityResultWS(loanApplicationId, EntityWebService.FDLM_CREDITO_CONSUMO_CONSULTAR_CREDITO);
        JSONObject precargados = consultarCredito.getResult().optJSONObject("PRECARGADOS");

        if (precargados != null) {
            return precargados.getInt("SALDOTOTAL");
        }

        return null;
    }
}
