package com.affirm.wenance;

import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.UserDAO;
import com.affirm.common.dao.WebServiceDAO;
import com.affirm.common.model.catalog.ActivityType;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.EntityWebServiceUtil;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.affirm.wenance.model.LeadCreateResponse;
import com.affirm.wenance.model.LeadScoreResponse;
import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WenanceServiceCall {

    private final CatalogService catalogService;

    private final PersonDAO personDAO;
    private final UserDAO userDAO;
    private final WebServiceDAO webServiceDAO;

    private final EntityWebServiceUtil entityWebServiceUtil;

    public WenanceServiceCall(CatalogService catalogService, PersonDAO personDAO, UserDAO userDAO, EntityWebServiceUtil entityWebServiceUtil, WebServiceDAO webServiceDAO) {
        this.catalogService = catalogService;
        this.personDAO = personDAO;
        this.userDAO = userDAO;
        this.entityWebServiceUtil = entityWebServiceUtil;
        this.webServiceDAO = webServiceDAO;
    }

    public LeadScoreResponse getLeadScore(LoanApplication loanApplication) throws Exception {
        Person person = personDAO.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
        PersonBankAccountInformation personBankAccountInformation = personDAO.getPersonBankAccountInformation(Configuration.getDefaultLocale(), loanApplication.getPersonId());

        String jsonBody = new JSONObject()
                .put("identification",
                        new JSONObject()
                                .put("type", "IC")// IC - IDENTIFICATION CARD | PC - PASSPORT CARD
                                .put("value", person.getDNIFromCUIT().replaceFirst("^0+(?!$)", ""))// REMOVE LEADING ZEROS
                )
                .put("bank_code", personBankAccountInformation != null ? personBankAccountInformation.getBank().getBankCode() : null)
                .put("gender", person.getGender())
                .put("birth_date", person.getBirthday() != null ? new SimpleDateFormat("yyyy-MM-dd").format(person.getBirthday()) : null)
                .put("source", "Solven")
                .toString();
        EntityWebServiceLog<JSONObject> webServiceResponse = callEndpoint(EntityWebService.WENANCE_SCORE, jsonBody, null, loanApplication.getId());

        Integer codigoError = JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(), "code", JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(), "status", null));// code or status

        if (codigoError == null) {
            LeadScoreResponse leadScoreResponse = new LeadScoreResponse();
            leadScoreResponse.fillFromJson(webServiceResponse.getRestResponse());
            return leadScoreResponse;
        } else {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            String error = JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(), "error", null);
            String descripcionError = JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(), "message", null);
            List<String> explanationErrorList = webServiceResponse.getRestResponse().has("explanation") ? webServiceResponse.getRestResponse().getJSONArray("explanation").toList().stream().map(Object::toString).collect(Collectors.toList()) : null;
            List<String> detailMessageErrorList = webServiceResponse.getRestResponse().has("detail_message") ? webServiceResponse.getRestResponse().getJSONArray("detail_message").toList().stream().map(Object::toString).collect(Collectors.toList()) : null;

            String completeApiError = String.format("[Wenance Score] - [%s] %s: %s. %s", codigoError, error != null ? error : "Error", descripcionError, (explanationErrorList != null ? explanationErrorList.toString() : detailMessageErrorList != null ? detailMessageErrorList.toString() : ""));
            System.out.println(completeApiError);
            throw new Exception(completeApiError);
        }
    }

    public LeadCreateResponse createLead(LoanApplication loanApplication, ZoneId zoneId) throws Exception {
        Person person = personDAO.getPerson(catalogService, Configuration.getDefaultLocale(), loanApplication.getPersonId(), false);
        List<PersonOcupationalInformation> personOcupationalInformationList = personDAO.getPersonOcupationalInformation(Configuration.getDefaultLocale(), loanApplication.getPersonId());
        PersonOcupationalInformation personOcupationalInformation = personOcupationalInformationList.stream().findFirst().orElse(null);
        PersonBankAccountInformation personBankAccountInformation = personDAO.getPersonBankAccountInformation(Configuration.getDefaultLocale(), loanApplication.getPersonId());
        User user = userDAO.getUser(loanApplication.getUserId());
        List<EntityWebServiceLog> webServiceLogList = webServiceDAO.getEntityWebServiceLog(loanApplication.getId(), EntityWebService.WENANCE_SCORE);
        EntityWebServiceLog<JSONObject> webServiceLog = webServiceLogList != null ? webServiceLogList.stream().findFirst().orElse(null) : null;
        LeadScoreResponse leadScoreResponse = null;
        if (webServiceLog != null) {
            leadScoreResponse = new LeadScoreResponse();
            leadScoreResponse.fillFromJson(new JSONObject(webServiceLog.getResponse()));
        }

        JSONObject utmTagsJson = new JSONObject()
//                .put("utm_source", "").put("utm_campaign", "")// TODO not required ATM
                ;

        String jsonBody = new JSONObject()
                .put("field_data",
                        new JSONObject()
                                .put("email",
                                        new JSONObject()
                                                .put("type", "String")
                                                .put("value", user.getEmail())
                                )
                                .put("amount",
                                        new JSONObject()
                                                .put("type", "String")
                                                .put("value", loanApplication.getAmount().toString())
                                )
                                .put("identity_card",
                                        new JSONObject()
                                                .put("type", "String")
                                                .put("value", person.getDNIFromCUIT())
                                )
                                .put("birth_date",
                                        new JSONObject()
                                                .put("type", "String")
                                                .put("value", person.getBirthday() != null ? new SimpleDateFormat("yyyy-MM-dd").format(person.getBirthday()) : null)
                                )
                                .put("employment_status",
                                        new JSONObject()
                                                .put("type", "String")
                                                .put("value",
                                                        personOcupationalInformation != null ? personOcupationalInformation.getActivityType().getId() == ActivityType.DEPENDENT ? "relacion-dependencia" :
                                                                personOcupationalInformation.getActivityType().getId() == ActivityType.PENSIONER ? "jubilado" : "otro" : "otro")
                                )
                                .put("phone_number",
                                        new JSONObject()
                                                .put("type", "String")
                                                .put("value", user.getPhoneNumberWithoutCode())
                                )
                                .put("phone_area",
                                        new JSONObject()
                                                .put("type", "String")
                                                .put("value", user.getPhoneCode())
                                )
                                .put("gender",
                                        new JSONObject()
                                                .put("type", "String")
                                                .put("value", person.getGender())
                                )
                                .put("bank_code",
                                        new JSONObject()
                                                .put("type", "String")
                                                .put("value", personBankAccountInformation != null ? personBankAccountInformation.getBank().getBankCode() : null)
                                )
                )
                .put("score_id", leadScoreResponse != null ? leadScoreResponse.getId() : null)
                .put("source", "Solven")
                .put("utm_tags", utmTagsJson)
                .put("zone_id", zoneId.getDisplayName(TextStyle.NARROW, Configuration.getDefaultLocale()))// https://stackoverflow.com/a/44970021
                .put("operation_id", new Date().getTime() + "-" + loanApplication.getId().toString())// UNIQUE ID FOR THIS OPERATION. FORMAT MILIS-LOANID
                .put("created_date", leadScoreResponse != null && leadScoreResponse.getCreatedDate() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(leadScoreResponse.getCreatedDate()) : null)
                .toString();

        EntityWebServiceLog<JSONObject> webServiceResponse = callEndpoint(EntityWebService.WENANCE_CREAR_LEAD, jsonBody, Collections.singletonList(Pair.of("returnType", "FormLead")), loanApplication.getId());

        Integer codigoError = JsonUtil.getIntFromJson(webServiceResponse.getRestResponse(), "code", null);

        if (codigoError == null) {
            LeadCreateResponse response = new LeadCreateResponse();
            response.fillFromJson(webServiceResponse.getRestResponse());
            return response;
        } else {
            entityWebServiceUtil.updateLogStatusToFailed(webServiceResponse.getId(), true);
            String descripcionError = JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(), "message", null);
            String detailedMessage = JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(), "detail_message", JsonUtil.getStringFromJson(webServiceResponse.getRestResponse(), "expected_fields", null));
            System.out.println(String.format("[Wenance Score] - [%s] : %s %s", codigoError, descripcionError, detailedMessage));
            throw new Exception(descripcionError);
        }
    }

    private EntityWebServiceLog callEndpoint(Integer entityWebServiceId, String body, List<Pair<String, String>> additionalHeaders, Integer loanApplicationId) throws Exception {
        EntityWebService webService = catalogService.getEntityWebService(entityWebServiceId);
        List<Pair<String, String>> headers = new ArrayList<>();

        headers.add(Pair.of("Content-Type", "application/json;charset=UTF-8"));
        headers.add(Pair.of("cache-control", "no-cache"));

        if (Configuration.hostEnvIsProduction()) {
            headers.add(Pair.of("authorization", webService.getProductionSecurityKey()));
        } else {
            headers.add(Pair.of("authorization", webService.getSandboxSecurityKey()));
        }

        if (additionalHeaders != null) {
            for (Pair<String, String> additionalHeader : additionalHeaders) {
                headers.add(Pair.of(additionalHeader.getLeft(), additionalHeader.getRight()));
            }
        }
        System.out.println(body);// TODO DELETE
        EntityWebServiceLog<JSONObject> webServiceResponse = entityWebServiceUtil.callRestWs(webService, body, headers, loanApplicationId, false);
        webServiceResponse.setRestResponse(new JSONObject(webServiceResponse.getResponse()));
        System.out.println(webServiceResponse.getResponse());// TODO DELETE
        return webServiceResponse;
    }

}
