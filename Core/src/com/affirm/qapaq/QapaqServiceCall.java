package com.affirm.qapaq;

import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.EntityWebServiceLog;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.PersonOcupationalInformation;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EntityWebServiceUtil;
import com.affirm.common.service.impl.EntityWebServiceUtilImpl;
import com.affirm.common.util.JsonUtil;
import com.affirm.qapaq.model.QapaqOffer;
import com.affirm.qapaq.model.WSExternoSolven;
import com.affirm.qapaq.model.WSExternoSolvenSoap;
import com.affirm.system.configuration.Configuration;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.BindingProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QapaqServiceCall {

    private static final Logger logger = Logger.getLogger(QapaqServiceCall.class);

    private static final List<String> controlF1Flow = Arrays.asList("N - Control 11");
    private static final List<String> controlF2Flow = Arrays.asList("N - Control 40");
    private static final List<String> acceptedClientTypes = Arrays.asList("NoCliente/S1", "NoCliente/S2", "NoCliente/S3");

    private final EntityWebServiceUtil entityWebServiceUtil;
    private final CatalogService catalogService;
    private final PersonDAO personDAO;
    private final UserDAO userDAO;

    @Autowired
    public QapaqServiceCall(EntityWebServiceUtil entityWebServiceUtil, CatalogService catalogService, PersonDAO personDAO, UserDAO userDAO) {
        this.entityWebServiceUtil = entityWebServiceUtil;
        this.catalogService = catalogService;
        this.personDAO = personDAO;
        this.userDAO = userDAO;
    }

    public Boolean capturarValor(String documentNumber, Integer loanApplicationId) throws Exception {
        WSExternoSolven solvenServ = new WSExternoSolven();
        WSExternoSolvenSoap solven = solvenServ.getWSExternoSolvenSoap();
        EntityWebService entityWebService = catalogService.getEntityWebService(EntityWebService.QAPAQ_CAPTURAR_VALOR);
        Pair<String, String> credentials = getAuthCredentials(entityWebService);

        EntityWebServiceLog<String> response = entityWebServiceUtil.callSoapWs(
                entityWebService,
                (BindingProvider) solven,
                loanApplicationId,
                new EntityWebServiceUtilImpl.ISOAPProcess() {
                    @Override
                    public String process() throws Exception {
                        return solven.bCapturarValor("1", documentNumber, credentials.getLeft(), credentials.getRight());
                    }
                });

        if (response.getSoapResponse() == null) {
            entityWebServiceUtil.updateLogStatusToFailed(response.getId(), true);
        }
        return response.getSoapResponse() == null ? null : "0".equals(response.getSoapResponse());
    }

    public void callGenerateLoanOffers(LoanApplication loanApplication) throws Exception {
        Person person = personDAO.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
        List<PersonOcupationalInformation> personOcupationalInformationList = personDAO.getPersonOcupationalInformation(Configuration.getDefaultLocale(), loanApplication.getPersonId());
        PersonOcupationalInformation personOcupationalInformation = personOcupationalInformationList != null ? personOcupationalInformationList.get(0) : null;

        QapaqOffer qapaqOffer = generateOffers(
                loanApplication.getId(),
                person.getDocumentNumber(),
                personOcupationalInformation != null ?
                        personOcupationalInformation.getActivityType().getId() == ActivityType.DEPENDENT ? QapaqOffer.SouceIncome.SALARIO :
                                personOcupationalInformation.getActivityType().getId() == ActivityType.INDEPENDENT ? QapaqOffer.SouceIncome.MICROEMPRESAS :
                                        personOcupationalInformation.getActivityType().getId() == ActivityType.RENTIER ? QapaqOffer.SouceIncome.RENTAS : null :
                        null,
                personOcupationalInformation != null ? personOcupationalInformation.getFixedGrossIncome() : 0.0);

        if (qapaqOffer != null) {
            String controlFlow = qapaqOffer.getSlaControl().toLowerCase().substring(0, (!qapaqOffer.getSlaControl().contains("/") ? qapaqOffer.getSlaControl().length() : qapaqOffer.getSlaControl().indexOf("/")));
            String flow = controlF1Flow.stream().map(String::toLowerCase).collect(Collectors.toList()).contains(controlFlow) ? "{16102}" :
                    controlF2Flow.stream().map(String::toLowerCase).collect(Collectors.toList()).contains(controlFlow) ? "{16101}" :
                            null;

            if (flow == null) {
                personDAO.deletePreApprovedBase(Entity.QAPAQ, Product.TRADITIONAL, IdentityDocumentType.DNI, person.getDocumentNumber());
            } else {
                boolean isAcceptedClientType = acceptedClientTypes.stream().map(String::toLowerCase).collect(Collectors.toList()).contains(qapaqOffer.getClientType().toLowerCase());

                if (isAcceptedClientType)
                    personDAO.registerPreApprovedBaseByEntityProductParameter(Entity.QAPAQ, Product.TRADITIONAL, IdentityDocumentType.DNI, person.getDocumentNumber(),
                            (double) qapaqOffer.getCampaignAmount(), qapaqOffer.getInstallment(), qapaqOffer.getRate(), null, null, null, null, null, flow);
                else
                    personDAO.deletePreApprovedBase(Entity.QAPAQ, Product.TRADITIONAL, IdentityDocumentType.DNI, person.getDocumentNumber());
            }
        } else {
            personDAO.deletePreApprovedBase(Entity.QAPAQ, Product.TRADITIONAL, IdentityDocumentType.DNI, person.getDocumentNumber());
        }
    }

    public QapaqOffer callGetOffer(String documentNumber) {
        QapaqOffer qapaqOffer = null;
        try {
            qapaqOffer = generateOffers(documentNumber);
        } catch (Exception ex) {
            logger.trace(ex);
        }
        return qapaqOffer;
    }

    private QapaqOffer generateOffers(String documentNumber) throws Exception {
        String jsonRequest = new JSONObject()
                .put("NumeroIdentificacion", documentNumber)
                .put("FuenteIngresos", QapaqOffer.SouceIncome.MICROEMPRESAS.getKey())
                .put("MontoIngresos", 0).toString();

        EntityWebServiceLog<JSONObject> webServiceResponse = callEndpoint(System.getenv("QAPAQ_GET_OFFER_URL"), jsonRequest, generateToken());
        QapaqOffer qapaqOffer = new QapaqOffer();

        if (webServiceResponse.getRestResponse().has("Oferta")) {
            qapaqOffer.fillFromJson(webServiceResponse.getRestResponse().getJSONObject("Oferta"));
        }
        String codigoError = JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(), "Codigo", null);

        if ("200".equalsIgnoreCase(codigoError)) {

            if (qapaqOffer.getCampaignAmount() == 0) {
                System.out.println("[Qapaq Generar Ofertas] - Monto Campaña es 0");
                throw new Exception("Monto Campaña es 0");
            }

            return qapaqOffer;
        } else if("005".equalsIgnoreCase(codigoError)) {
            return null;
        } else {
            String descripcionError = JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(), "Descripcion", null);
            System.out.println("[Qapaq Generar Ofertas] - [" + codigoError + "] : " + descripcionError);
            throw new Exception(descripcionError);
        }
    }

    private QapaqOffer generateOffers(Integer loanApplicationId, String documentNumber, QapaqOffer.SouceIncome sourceIncome, double amountIncome) throws Exception {
        String jsonRequest = new JSONObject()
                .put("NumeroIdentificacion", documentNumber)
                .put("FuenteIngresos", sourceIncome == null ? QapaqOffer.SouceIncome.MICROEMPRESAS.getKey() : sourceIncome.getKey())
                .put("MontoIngresos", amountIncome).toString();

        EntityWebServiceLog<JSONObject> webServiceResponse = callEndpoint(EntityWebService.QAPAQ_CONSULTA_DE_OFERTAS, jsonRequest, generateToken(loanApplicationId), loanApplicationId);
        QapaqOffer qapaqOffer = new QapaqOffer();

        if (webServiceResponse.getRestResponse().has("Oferta")) {
            qapaqOffer.fillFromJson(webServiceResponse.getRestResponse().getJSONObject("Oferta"));
        }
        String codigoError = JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(), "Codigo", null);

        if ("200".equalsIgnoreCase(codigoError)) {

            if (qapaqOffer.getCampaignAmount() == 0) {
                entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
                System.out.println("[Qapaq Generar Ofertas] - Monto Campaña es 0");
                throw new Exception("Monto Campaña es 0");
            }

            return qapaqOffer;
        } else if("005".equalsIgnoreCase(codigoError)) {
            return null;
        } else {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            String descripcionError = JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(), "Descripcion", null);
            System.out.println("[Qapaq Generar Ofertas] - [" + codigoError + "] : " + descripcionError);
            throw new Exception(descripcionError);
        }
    }

    private String generateToken() throws Exception {
        String jsonCredentials = new JSONObject().put("Canal", "SOLVEN").toString();
        EntityWebServiceLog<JSONObject> webServiceResponse = callEndpoint(System.getenv("QAPAQ_GET_TOKEN_URL"), jsonCredentials, null);

        String codigoError = JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(), "Codigo", null);

        if (codigoError == null) {
            return JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(), "Token", null);
        } else {
            String descripcionError = JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(), "Descripcion", null);
            System.out.println("[Qapaq Generar Token] - [" + codigoError + "] : " + descripcionError);
            throw new Exception(descripcionError);
        }
    }

    private String generateToken(Integer loanApplicationId) throws Exception {
        String jsonCredentials = new JSONObject().put("Canal", "SOLVEN").toString();
        EntityWebServiceLog<JSONObject> webServiceResponse = callEndpoint(EntityWebService.QAPAQ_GENERADOR_TOKEN, jsonCredentials, null, loanApplicationId);

        String codigoError = JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(), "Codigo", null);

        if (codigoError == null) {
            return JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(), "Token", null);
        } else {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            String descripcionError = JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(), "Descripcion", null);
            System.out.println("[Qapaq Generar Token] - [" + codigoError + "] : " + descripcionError);
            throw new Exception(descripcionError);
        }
    }

    private Pair<String, String> getAuthCredentials(EntityWebService entityWebService) {
        JSONObject json;
        if (Configuration.hostEnvIsProduction()) {
            json = new JSONObject(entityWebService.getProductionSecurityKey());
        } else {
            json = new JSONObject(entityWebService.getSandboxSecurityKey());
        }
        return Pair.of(JsonUtil.getStringFromJson(json, "user", null), JsonUtil.getStringFromJson(json, "password", null));
    }

    private EntityWebServiceLog callEndpoint(Integer entityWebServiceId, String body, String authorization, Integer loanApplicationId) throws Exception {
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of("cache-control", "no-cache"));
        if (authorization != null) {
            headers.add(Pair.of("authorization", "Bearer " + authorization));
        }

        EntityWebServiceLog<JSONObject> webServiceResponse = entityWebServiceUtil.callRestWs(catalogService.getEntityWebService(entityWebServiceId), body, headers, loanApplicationId, true);
        webServiceResponse.setRestResponse(new JSONObject(webServiceResponse.getResponse()));
        return webServiceResponse;
    }

    private EntityWebServiceLog callEndpoint(String url, String body, String authorization) throws Exception {
        List<Pair<String, String>> headers = new ArrayList<>();
        headers.add(Pair.of("cache-control", "no-cache"));
        if (authorization != null) {
            headers.add(Pair.of("authorization", "Bearer " + authorization));
        }

        EntityWebServiceLog<JSONObject> webServiceResponse = entityWebServiceUtil.callRestWs(url, body, headers, true);
        webServiceResponse.setRestResponse(new JSONObject(webServiceResponse.getResponse()));
        return webServiceResponse;
    }


//    CODIGOS DE ERROR - GENERAR OFERTA
//    "99"
//    "Error al Generar la Oferta
//    001
//    No puede usar el servicio, consulta fuera de horario mínimo permitido.
//    002
//    No puede usar el servicio, consulta fuera de horario máximo permitido.
//    003
//    No puede usar el servicio, ha superado el número máximo diario de consultas para este canal.
//    004
//    No puede usar el servicio, canal no autorizado.
//    "005",
//    "Cliente No Aplica"

}
