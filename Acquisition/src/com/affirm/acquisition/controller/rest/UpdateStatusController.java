package com.affirm.acquisition.controller.rest;

/**
 * Created by dev5 on 22/11/17.
 */

import com.affirm.client.model.annotation.ErrorRestControllerAnnotation;
import com.affirm.client.util.ApiWsResponse;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.RestApiDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.creditService.model.StatusResponse;
import com.affirm.system.configuration.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

@RestController
@RequestMapping("/api/solven")
public class UpdateStatusController {

    @Autowired
    CreditDAO creditDAO;
    @Autowired
    AgreementService agreementService;
    @Autowired
    SpringTemplateEngine templateEngine;
    @Autowired
    LoanNotifierService loanNotifierService;
    @Autowired
    RestApiDAO restApiDao;
    @Autowired
    CatalogService catalogService;
    @Autowired
    LoanApplicationDAO loanApplicationDAO;
    @Autowired
    CreditService creditService;
    @Autowired
    InteractionService interactionService;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    FileService fileService;

    @RequestMapping(value = "/confirmarOperacion", method = RequestMethod.POST)
    @ErrorRestControllerAnnotation
    public Object rebateTransaction(
            ModelMap model, Locale locale, HttpServletRequest request, HttpServletResponse response,
            @RequestParam("creditId") String creditId,
            @RequestParam("operationId") String operationId,
            @RequestParam(value = "returningReasons", required = false) String returningReasons) throws Exception {

        try {
            if (operationId.equals("1")) {
//                String authHeader = request.getHeader("Authorization");
//                WsClient wsClient = restApiDao.getWsClientByApiKey(authHeader.split("=")[0]);
                WsClient wsClient = (WsClient) request.getAttribute("wsClient");
                Integer solvenCreditId = creditDAO.getCreditByCreditCode(creditId.trim(), restApiDao.getEntityByClientId(wsClient.getId()));

                if (solvenCreditId == null)
                    return ApiWsResponse.error("-2", "El crédito no existe");

                Credit credit = creditDAO.getCreditByID(solvenCreditId, Configuration.getDefaultLocale(), false, Credit.class);
                creditDAO.updateCreditStatus(credit.getId(), CreditStatus.ORIGINATED, credit.getEntity().getId());
                creditDAO.updateCreditSubStatus(credit.getId(), CreditSubStatus.PENDING_ENTIY_DISBURSEMENT);
                creditDAO.updateGeneratedInEntity(credit.getId(), null);
                return ApiWsResponse.ok(new StatusResponse(1, null));
            } else if (operationId.equals("2")) {
//                String authHeader = request.getHeader("Authorization");
//                WsClient wsClient = restApiDao.getWsClientByApiKey(authHeader.split("=")[0]);
                WsClient wsClient = (WsClient) request.getAttribute("wsClient");
                Credit credit = null;
                Integer solvenCreditId = creditDAO.getCreditByCreditCode(creditId.trim(), restApiDao.getEntityByClientId(wsClient.getId()));
                if (solvenCreditId == null){
                    try {
                        solvenCreditId = Integer.parseInt(creditId);
                    } catch (Exception ex) {
                    }

                    if(solvenCreditId != null){
                        Credit creditTemp = creditDAO.getCreditByID(solvenCreditId, Configuration.getDefaultLocale(), false, Credit.class);
                        if(creditTemp != null && creditTemp.getEntity().getId().equals(restApiDao.getEntityByClientId(wsClient.getId()))){
                            credit = creditTemp;
                        }
                    }
                }else{
                    credit = creditDAO.getCreditByID(solvenCreditId, Configuration.getDefaultLocale(), false, Credit.class);
                }

                if(credit == null){
                    return ApiWsResponse.error("-2", "El crédito no existe");
                }

                creditDAO.updateCreditStatus(credit.getId(), CreditStatus.ORIGINATED_DISBURSED, null);
                creditDAO.updateDisbursmentInInEntity(credit.getId(), null);
                PersonContactInformation personContact = personDAO.getPersonContactInformation(locale, credit.getPersonId());

                EntityProductParams entityProductParams = catalogService.getEntityProductParam(credit.getEntity().getId(), credit.getProduct().getId());

                if(!Arrays.asList(EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL,
                        EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL_AGENCIAS,
                        EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL_CAMPANA,
                        EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL_SINIESTROS).contains(entityProductParams.getId())){
                    if (entityProductParams.getDisbursementInteractionIds() != null && !entityProductParams.getDisbursementInteractionIds().isEmpty()) {

                        for (Integer interactionId : entityProductParams.getDisbursementInteractionIds()) {
                            LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(credit.getLoanApplicationId(), locale);

                            PersonInteraction interaction = new PersonInteraction();
                            interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                            interaction.setInteractionContent(catalogService.getInteractionContent(interactionId, loanApplication.getCountryId()));
                            interaction.setDestination(personContact.getEmail());
                            interaction.setCreditId(credit.getId());
                            interaction.setPersonId(credit.getPersonId());

                            JSONObject jsonVars = new JSONObject();
                            Person person = personDAO.getPerson(catalogService, locale, credit.getPersonId(), false);
                            jsonVars.put("CLIENT_NAME", person.getName().split(" ")[0]);
                            jsonVars.put("ENTITY", credit.getEntity().getFullName());
                            jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);
                            jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);

                            byte[] contract = creditService.createContract(credit.getId(), request, response, locale, templateEngine, "Contrato-".concat(credit.getEntity().getFullName()).concat(".pdf"), false);
                            PersonInteractionAttachment atachment = new PersonInteractionAttachment();
                            atachment.setBytes(contract);
                            atachment.setFilename("Contrato-".concat(credit.getEntity().getFullName()).concat(".pdf"));
                            interaction.setAttachments(Arrays.asList(atachment));

                            interactionService.sendPersonInteraction(interaction, jsonVars, null);
                        }

                    } else if (entityProductParams.getDisbursementType().equals(EntityProductParams.DISBURSEMENT_TYPE_DEPOSIT)
                            && entityProductParams.getSignatureType().equals(EntityProductParams.CONTRACT_TYPE_DIGITAL)) {

                        LoanApplication loanApplication = loanApplicationDAO.getLoanApplication(credit.getLoanApplicationId(), locale);

                        PersonInteraction interaction = new PersonInteraction();
                        interaction.setInteractionType(catalogService.getInteractionType(InteractionType.MAIL));
                        interaction.setInteractionContent(catalogService.getInteractionContent(InteractionContent.DISBURSEMENT_MAIL_NO_DOCUMENT, loanApplication.getCountryId()));
                        interaction.setDestination(personContact.getEmail());
                        interaction.setCreditId(credit.getId());
                        interaction.setPersonId(credit.getPersonId());

                        JSONObject jsonVars = new JSONObject();
                        Person person = personDAO.getPerson(catalogService, locale, credit.getPersonId(), false);
                        jsonVars.put("CLIENT_NAME", person.getName().split(" ")[0]);
                        jsonVars.put("ENTITY", credit.getEntity().getFullName());
                        jsonVars.put("AGENT_IMAGE_URL", loanApplication.getAgent() != null ? loanApplication.getAgent().getAvatarUrl() : Configuration.AGENT_IMAGE_URL_COLLECTION);
                        jsonVars.put("AGENT_FULLNAME", loanApplication.getAgent() != null ? loanApplication.getAgent().getName() : Configuration.AGENT_FULLNAME_COLLECTION);

                        byte[] contract = creditService.createContract(credit.getId(), request, response, locale, templateEngine, "Contrato-".concat(credit.getEntity().getFullName()).concat(".pdf"), false);
                        PersonInteractionAttachment atachment = new PersonInteractionAttachment();
                        atachment.setBytes(contract);
                        atachment.setFilename("Contrato-".concat(credit.getEntity().getFullName()).concat(".pdf"));
                        interaction.setAttachments(Arrays.asList(atachment));

                        interactionService.sendPersonInteraction(interaction, jsonVars, null);
                    }
                }

                creditDAO.updateDisbursementDate(credit.getId(), new Date());
                loanNotifierService.notifyDisbursement(credit.getLoanApplicationId(), Configuration.getDefaultLocale());
                Credit credito = creditDAO.getCreditByID(solvenCreditId, Configuration.getDefaultLocale(), false, Credit.class);
                String loanInfoURL = null;
                if(credito.getContractUserFileId() != null && !credito.getContractUserFileId().isEmpty()){
                    loanInfoURL = fileService.generatePublicUserFileUrl(credito.getContractUserFileId().get(0), false);
                }
                return ApiWsResponse.ok(new StatusResponse(1, loanInfoURL));
            } else if (operationId.equals("3")) {
                if (returningReasons == null || returningReasons.isEmpty())
                    return ApiWsResponse.error("-1", "El request no es válido");
//                String authHeader = request.getHeader("Authorization");
//                WsClient wsClient = restApiDao.getWsClientByApiKey(authHeader.split("=")[0]);
                WsClient wsClient = (WsClient) request.getAttribute("wsClient");
                Credit credit = null;
                Integer solvenCreditId = creditDAO.getCreditByCreditCode(creditId.trim(), restApiDao.getEntityByClientId(wsClient.getId()));
                if (solvenCreditId == null){
                    try {
                        solvenCreditId = Integer.parseInt(creditId);
                    } catch (Exception ex) {
                    }

                    if(solvenCreditId != null){
                        Credit creditTemp = creditDAO.getCreditByID(solvenCreditId, Configuration.getDefaultLocale(), false, Credit.class);
                        if(creditTemp != null && creditTemp.getEntity().getId().equals(restApiDao.getEntityByClientId(wsClient.getId()))){
                            credit = creditTemp;
                        }
                    }
                }else{
                    credit = creditDAO.getCreditByID(solvenCreditId, Configuration.getDefaultLocale(), false, Credit.class);
                }

                JSONArray jsonArr = null;
                if (returningReasons.trim().startsWith("[")) {
                    jsonArr = new JSONArray(returningReasons);
                } else {
                    jsonArr = new JSONArray();
                    jsonArr.put(Integer.parseInt(returningReasons));
                }
                JSONArray flowBackAccount = new JSONArray();
                for (Object obj : jsonArr) {
                    Integer returninReasonId = (Integer) obj;
                    if (catalogService.getReturningReasonById(returninReasonId) == null)
                        return ApiWsResponse.error("-3", "El identificador del motivo de rechazo [" + returninReasonId.toString() + "] no es válido");

                    ReturningReason returningReason = catalogService.getReturningReasonById(returninReasonId);
                    switch (returningReason.getCorrectionFlow().getId()) {
                        case CorrectionFlow.BACK_ACCOUNT:
                            flowBackAccount.put(returninReasonId);
                            break;
                    }
                }

                if (flowBackAccount.length() > 0) {
                    creditDAO.updateReturningReasons(credit.getId(), flowBackAccount);
                }
                return ApiWsResponse.ok(new StatusResponse(1, null));

            } else {
                return ApiWsResponse.error("-1", "El request no es válido");
            }
        } catch (Exception e) {
            String estado = null;
            switch (operationId) {
                case "1":
                    estado = catalogService.getCreditStatus(locale, CreditStatus.ORIGINATED).getStatus();
                    break;
                case "2":
                    estado = catalogService.getCreditStatus(locale, CreditStatus.ORIGINATED_DISBURSED).getStatus();
                    break;
                default:
                    estado = "Estado desconocido";
                    break;
            }
            String errorMessage;
            String errorCode;
            if (!operationId.equals("3")) {
                errorMessage = "Error al actualizar el crédito de la entidad [" + creditId + "] al estado [" + estado + "]";
                errorCode = "-4";
            } else {
                errorMessage = "Error al procesar rechazo de entidad en el crédito [" + creditId + "] con los siguientes motivos [" + returningReasons + "]";
                errorCode = "-5";
            }

            System.out.print("[Confirmar Operacion] : ERROR - " + errorMessage);
            e.printStackTrace();
            return ApiWsResponse.error(errorCode, errorMessage);
        }

    }
}