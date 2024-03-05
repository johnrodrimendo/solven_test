package com.affirm.warmi.service.impl;

import com.affirm.common.dao.BotDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.Linea;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.OnpeDetail;
import com.affirm.common.model.transactional.Person;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.ErrorService;
import com.affirm.common.service.PersonService;
import com.affirm.common.service.impl.ErrorServiceImpl;
import com.affirm.common.util.JsonUtil;
import com.affirm.system.configuration.Configuration;
import com.affirm.warmi.model.*;
import com.affirm.warmi.model.Employer;
import com.affirm.warmi.service.WarmiService;
import com.google.gson.Gson;
import okhttp3.*;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service("warmiService")
public class WarmiServiceImpl implements WarmiService {

    private static final Logger logger = Logger.getLogger(WarmiServiceImpl.class);

    private static final String URL = System.getenv("WARMI_URL");
    private static final String PROCESS_URL = URL + "/process/";
    private static final String APIKEY = System.getenv("WARMI_APIKEY");
    private static final String ACCOUNT = System.getenv("WARMI_ACCOUNT");
    private static final String WEBHOOK_FOR_WARMI = Configuration.getClientDomain() + "/webhook/warmi";

    private static final String WARMI_DATE_FORMAT = "dd-MM-yyyy";

    @Autowired
    private LoanApplicationDAO loanApplicationDAO;
    @Autowired
    private PersonDAO personDAO;
    @Autowired
    private BotDAO botDAO;
    @Autowired
    private PersonService personService;
    @Autowired
    private ErrorService errorService;

    @Override
    public JSONObject runProcess(LoanApplication loanApplication, Person person) {
        RunProcessRequest request = new RunProcessRequest();
        request.setAccount(ACCOUNT);
        request.setCountryId(person.getCountry().getId());
        request.setDocumentNumber(person.getDocumentNumber());
        request.setUrl(WEBHOOK_FOR_WARMI);

        switch (person.getDocumentType().getId()) {
            case IdentityDocumentType.DNI:
                request.setDocumentType("DNI");
                break;
            case IdentityDocumentType.CE:
                request.setDocumentType("CE");
                request.setDateOfBirth(convertDateWarmiFormat(person.getBirthday()));
                break;
            default:
                logger.warn(String.format("Document type %s is not configured", person.getDocumentType().getId()));
                return null;
        }

        if (loanApplication.getWarmiProcessId() == null) {
            try {
                String jsonContentType = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
                RequestBody body = RequestBody.create(MediaType.parse(jsonContentType), new Gson().toJson(request));
                Request.Builder serviceRequestBuilder = new Request.Builder()
                        .addHeader("Content-Type", jsonContentType)
                        .addHeader("warmi-authorization", APIKEY)
                        .url(PROCESS_URL)
                        .post(body);
                Request serviceRequest = serviceRequestBuilder.build();

                OkHttpClient client = new OkHttpClient.Builder().build();
                Response response = client.newCall(serviceRequest).execute();

                if (response != null) {
                    logger.debug("Warmi status code " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        String result = response.body().string();
                        logger.debug(result);

                        loanApplicationDAO.updateWarmiProcessId(
                                loanApplication.getId(),
                                JsonUtil.getStringFromJson(new JSONObject(result), "processId", null));

                    } else {
                        String result = response.body() != null ? response.body().string() : response.message();
                        logger.error(result);
                        if(Configuration.hostEnvIsProduction()) errorService.sendErrorCriticSlack(String.format("Error en comunicacion con Warmi. Loan: %s, Result: %s", loanApplication.getId(), result));
                        else errorService.sendGeneralErrorSlack(String.format("Error en comunicacion con Warmi. Loan: %s, Result: %s", loanApplication.getId(), result));
                    }
                    response.body().close();
                } else {
                    String errorMessage = "No fue posible llamar a " + PROCESS_URL;
                    logger.error(errorMessage);
                    if(Configuration.hostEnvIsProduction()) errorService.sendErrorCriticSlack(String.format("Error en comunicacion con Warmi. Loan: %s. %s", loanApplication.getId(), errorMessage));
                    else errorService.sendGeneralErrorSlack(String.format("Error en comunicacion con Warmi. Loan: %s. %s", loanApplication.getId(), errorMessage));
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return null;
    }

    private String convertDateWarmiFormat(Date date) {
        if(date == null)
            return null;
        DateFormat df = new SimpleDateFormat(WARMI_DATE_FORMAT);
        return df.format(date);
    }

    @Override
    public void saveResult(ProcessDetail result) throws Exception {
        Locale locale = Configuration.getDefaultLocale();
        int documentId = translateDocumentIdFromWarmi(result.getPerson().getDocumentType());
        String documentNumber = result.getPerson().getDocumentNumber();
        int personId = personDAO.getPersonIdByDocument(documentId, documentNumber);

        List<LoanApplication> loanApplications = loanApplicationDAO.getActiveLoanApplicationsByPerson(locale, personId, LoanApplication.class);
        LoanApplication loanApplication = loanApplications
                .stream()
                .filter(e -> Objects.equals(result.getProcessId(), e.getWarmiProcessId()))
                .findFirst()
                .orElse(null);

        if (loanApplication != null && loanApplication.getCountry().getId() == CountryParam.COUNTRY_PERU) {
            PersonOcupationalInformation occupationalInformation = personService.getPrincipalOcupationalInormation(personId, locale);

            if(occupationalInformation != null){
                if (ActivityType.DEPENDENT == occupationalInformation.getActivityType().getId()) {
                    saveSunatRucResult(loanApplication, result.getEmployer());
                } else if (ActivityType.INDEPENDENT == occupationalInformation.getActivityType().getId()) {
                    saveSunatDniResult(loanApplication, documentNumber, result.getCommercial());
                }
            }

            saveClaroResult(loanApplication, documentId, documentNumber, result.getTelephoneServices());
            saveMovistarResult(loanApplication, documentId, documentNumber, result.getTelephoneServices());
            saveEntelResult(loanApplication, documentId, documentNumber, result.getTelephoneServices());
            saveBitelResult(loanApplication, documentId, documentNumber, result.getTelephoneServices());
            saveEssaludResult(loanApplication, documentId, documentNumber, result.getHealthSystem(), result.getPerson());
            saveSatResult(loanApplication, documentId, documentNumber, result.getTaxes());
            saveRedamResult(loanApplication, documentId, documentNumber, result.getPenaltyFee());

            if (documentId == IdentityDocumentType.DNI) {
                saveOnpeResult(loanApplication, documentNumber, result.getPenaltyFee().getElectoralFines());
                saveReniecResult(loanApplication, documentNumber, result.getPerson());
            } else if (documentId == IdentityDocumentType.CE) {
                Person person = personDAO.getPerson(personId, false, locale);
                saveMigracionesResult(loanApplication, documentNumber, person.getBirthday(), result.getPerson());
            }
        }
    }

    private int translateDocumentIdFromWarmi(String documentTypeWarmi) {
        switch (documentTypeWarmi) {
            case "DNI":
                return IdentityDocumentType.DNI;
            case "CE":
                return IdentityDocumentType.CE;
            default:
                return 0;
        }
    }

    private void saveRedamResult(LoanApplication loanApplication, int docType, String documentNumber, PenaltyFee penaltyFeeResult) {
        RedamResult result = new RedamResult();

        if (penaltyFeeResult != null && penaltyFeeResult.getChildSupportJudgement() != null) {
            result.setDocumentNumber(documentNumber);
        }

        try {
            QueryBot queryBot = botDAO.registerQuery(Bot.REDAM_BOT, QueryBot.STATUS_QUEUE, loanApplication.getUserId());
            botDAO.registerQueryResultRedam(queryBot.getId(), docType, documentNumber);
            botDAO.updateQueryResultRedam(queryBot.getId(), result);
            registerSucces(queryBot);
        } catch (Exception e) {
            logger.error("saveRedamResult exception", e);
        }
    }

    private void saveSatResult(LoanApplication loanApplication, int docType, String documentNumber, List<Tax> taxesResult) {
        SatResult result = new SatResult();

        if (taxesResult != null) {
            result.setSatIdReportJSONArray(new JSONArray(taxesResult));
        }

        try {
            QueryBot queryBot = botDAO.registerQuery(Bot.SAT, QueryBot.STATUS_QUEUE, loanApplication.getUserId());
            botDAO.registerQueryResultSat(queryBot.getId(), docType, documentNumber);
            botDAO.updateQueryResultSat(queryBot.getId(), result);
            registerSucces(queryBot);
        } catch (Exception e) {
            logger.error("saveSatResult exception", e);
        }
    }

    /*private void saveRedamResult(LoanApplication loanApplication, int docType, String documentNumber, PenaltyFee penaltyFeeResult) {
        RedamResult result = new RedamResult();

        if (penaltyFeeResult != null) {
            // todo
        }

        try {
            QueryBot queryBot = botDAO.registerQuery(Bot.REDAM_BOT, QueryBot.STATUS_QUEUE, loanApplication.getUserId());
            botDAO.registerQueryResultRedam(queryBot.getId(), docType, documentNumber);
            botDAO.updateQueryResultRedam(queryBot.getId(), result);
            registerSucces(queryBot);
        } catch (Exception e) {
            logger.error("saveRedamResult exception", e);
        }
    }*/

    private void saveReniecResult(LoanApplication loanApplication, String documentNumber, com.affirm.warmi.model.Person personResult) {
        ReniecResult result = new ReniecResult();

        if (personResult != null) {
            result.setFull_name(personResult.getSurnames() + " " + personResult.getNames());
            result.setDocument_number(personResult.getDocumentNumber());
        }

        try {
            QueryBot queryBot = botDAO.registerQuery(Bot.RENIEC_BOT, QueryBot.STATUS_QUEUE, loanApplication.getUserId());
            botDAO.registerQueryResultReniec(queryBot.getId(), documentNumber);
            botDAO.updateQueryResultReniec(queryBot.getId(), result);
            registerSucces(queryBot);
        } catch (Exception e) {
            logger.error("saveReniecResult exception", e);
        }
    }

    private void saveEssaludResult(LoanApplication loanApplication, int docType, String documentNumber, HealthSystem healthSystemResult, com.affirm.warmi.model.Person personResult) throws Exception {
        EssaludResult result = new EssaludResult();

        if (healthSystemResult != null && personResult != null) {
            result.setDocumentNumber(documentNumber);
            result.setFullName(personResult.getSurnames() + " " + personResult.getNames());
            result.setInssuredType(healthSystemResult.getInsuredType());
            result.setInssuranceType(healthSystemResult.getSystemType());
            if (healthSystemResult.getValidThru() != null) {
                result.setUntil(convertToDate(healthSystemResult.getValidThru()));
            }
        }

        try {
            QueryBot queryBot = botDAO.registerQuery(Bot.ESSALUD_BOT, QueryBot.STATUS_QUEUE, loanApplication.getUserId());
            botDAO.registerQueryResultEssalud(queryBot.getId(), docType, documentNumber);
            botDAO.updateQueryResultEssalud(queryBot.getId(), result);
            registerSucces(queryBot);
        } catch (Exception e) {
            logger.error("saveEssaludResult exception", e);
        }
    }

    private void saveOnpeResult(LoanApplication loanApplication, String documentNumber, List<ElectoralFine> electoralFines) {
        OnpeResult result = new OnpeResult();

        if (electoralFines != null && !electoralFines.isEmpty()) {
            List<OnpeDetail> onpeDetails = new ArrayList<>();
            electoralFines.forEach(e -> {
                OnpeDetail detail = new OnpeDetail();
                detail.setCode(e.getCode());
                detail.setAmount(e.getAmount());
                detail.setCollectionStage(e.getCollectionStage());
                detail.setElectoralProcess(e.getDescription());
                detail.setOmissionType(e.getComissionType());

                onpeDetails.add(detail);
            });

            result.setDetails(onpeDetails);
        }

        try {
            QueryBot queryBot = botDAO.registerQuery(Bot.ONPE, QueryBot.STATUS_QUEUE, loanApplication.getUserId());
            botDAO.registerQueryResultONPE(queryBot.getId(), documentNumber);
            botDAO.updateQueryResultONPE(queryBot.getId(), result);
            registerSucces(queryBot);
        } catch (Exception e) {
            logger.error("saveOnpeResult exception", e);
        }
    }

    private void saveMigracionesResult(LoanApplication loanApplication, String documentNumber, Date birthday, com.affirm.warmi.model.Person person) {
        MigracionesResult result = new MigracionesResult();

        result.setFullName(String.format("%s %s", person.getNames(), person.getSurnames()));
        result.setNationality(person.getForeignNationality());
        result.setDocumentExpeditionDate(convertToDate(person.getForeignDocumentExpeditionDate()));
        result.setDocumentDueDate(convertToDate(person.getForeignDocumentDueDate()));

        try {
            QueryBot queryBot = botDAO.registerQuery(Bot.MIGRACIONES, QueryBot.STATUS_QUEUE, loanApplication.getUserId());
            botDAO.registerQueryResultMigrations(queryBot.getId(), documentNumber, birthday);
            botDAO.updateQueryResultMigrations(queryBot.getId(), result);
            registerSucces(queryBot);
        } catch (Exception e) {
            logger.error("saveMigracionesResult exception", e);
        }
    }

    private void saveSunatDniResult(LoanApplication loanApplication, String documentNumber, Commercial commercialResult) throws Exception {
        SunatResult result = new SunatResult();

        if (commercialResult != null) {
            result.setRuc(commercialResult.getTaxPayerId());
            result.setTradeName(commercialResult.getTaxPayerName());
            result.setStatus(commercialResult.getTaxPayerStatus());
            result.setTaxpayerType(commercialResult.getTaxPayerType());
            result.setTaxpayerCondition(commercialResult.getTaxPayerCondition());
            result.setFiscalAddress(commercialResult.getAddress());
            if (commercialResult.getActivity() != null) {
                result.setEconomicActivities(convertEconomicActivities(commercialResult.getActivity()));
            }
            if (commercialResult.getTaxPayerRegisterDate() != null) {
                result.setRegisterDate(convertToDate(commercialResult.getTaxPayerRegisterDate()));
            }
            result.setPleJoinedSince(commercialResult.getActivityStartDate());
        }

        try {
            QueryBot queryBot = botDAO.registerQuery(Bot.SUNAT_BOT, QueryBot.STATUS_QUEUE, loanApplication.getUserId());
            botDAO.registerQueryResultSunat(queryBot.getId(), null, SunatResult.DNI_TYPE, documentNumber);
            botDAO.updateQueryResultSunat(queryBot.getId(), result);
            registerSucces(queryBot);
        } catch (Exception e) {
            logger.error("saveSunatResult exception", e);
        }
    }

    private void saveSunatRucResult(LoanApplication loanApplication, Employer employerResult) throws Exception {
        SunatResult result = new SunatResult();

        if (employerResult != null) {
            result.setRuc(employerResult.getTaxPayerId());
            result.setTradeName(employerResult.getTaxPayerName());
            result.setStatus(employerResult.getTaxPayerStatus());
            result.setTaxpayerType(employerResult.getTaxPayerType());
            result.setTaxpayerCondition(employerResult.getTaxPayerCondition());
            result.setFiscalAddress(employerResult.getAddress());
            if (employerResult.getActivity() != null) {
                result.setEconomicActivities(convertEconomicActivities(employerResult.getActivity()));
            }
            if (employerResult.getTaxPayerRegisterDate() != null) {
                result.setRegisterDate(convertToDate(employerResult.getTaxPayerRegisterDate()));
            }
            result.setPleJoinedSince(employerResult.getActivityStartDate());
        }

        try {
            QueryBot queryBot = botDAO.registerQuery(Bot.SUNAT_BOT, QueryBot.STATUS_QUEUE, loanApplication.getUserId());
            botDAO.registerQueryResultSunat(queryBot.getId(), null, SunatResult.RUC_TYPE, result.getRuc());
            botDAO.updateQueryResultSunat(queryBot.getId(), result);
            registerSucces(queryBot);
        } catch (Exception e) {
            logger.error("saveSunatRucResult exception", e);
        }
    }

    private static String convertEconomicActivities(String economicActivities) {
        JSONArray jsonArray = new JSONArray();
        String[] array = economicActivities.split("\\n");
        if (array.length > 0) {
            for (int i = 0; i < array.length; i++) {
                if (i == 0)
                    jsonArray.put("Principal - CIIU " + array[i]);
                else
                    jsonArray.put("Secundaria " + i +" - CIIU " + array[i]);
            }
        }
        return jsonArray.toString();
    }

    private void saveClaroResult(LoanApplication loanApplication, int documentId, String documentNumber, List<TelephoneService> telephoneServices) {
        LineaResult result = new LineaResult();

        if (telephoneServices != null) {
            List<TelephoneService> claroServices = telephoneServices
                    .stream()
                    .filter(e -> Objects.equals(e.getCompany(), "Claro"))
                    .collect(Collectors.toList());

            if (!claroServices.isEmpty()) {
                List<Linea> lineas = new ArrayList<>();
                claroServices.forEach(e -> lineas.add(new Linea(e.getPhoneNumber(), e.getServiceType())));

                result.setCantidad(lineas.size());
                result.setOperador(PhoneContractOperator.CLARO);
                result.setLineas(new Gson().toJson(lineas));
            }
        }

        try {
            QueryBot queryBot = botDAO.registerQuery(Bot.CLARO, QueryBot.STATUS_QUEUE, loanApplication.getUserId());
            botDAO.registerQueryResultPhoneContracts(queryBot.getId(), documentId, documentNumber, PhoneContractOperator.CLARO);
            botDAO.updateQueryResultPhoneContracts(queryBot.getId(), result);
            registerSucces(queryBot);
        } catch (Exception e) {
            logger.error("saveClaroResult exception", e);
        }
    }

    private void saveMovistarResult(LoanApplication loanApplication, int documentId, String documentNumber, List<TelephoneService> telephoneServices) {
        LineaResult result = new LineaResult();

        if (telephoneServices != null) {
            List<TelephoneService> movistarServices = telephoneServices
                    .stream()
                    .filter(e -> Objects.equals(e.getCompany(), "Movistar"))
                    .collect(Collectors.toList());

            if (!movistarServices.isEmpty()) {
                List<Linea> lineas = new ArrayList<>();
                movistarServices.forEach(e -> lineas.add(new Linea(e.getPhoneNumber(), e.getServiceType())));

                result.setCantidad(lineas.size());
                result.setOperador(PhoneContractOperator.MOVISTAR);
                result.setLineas(new Gson().toJson(lineas));
            }
        }

        try {
            QueryBot queryBot = botDAO.registerQuery(Bot.MOVISTAR, QueryBot.STATUS_QUEUE, loanApplication.getUserId());
            botDAO.registerQueryResultPhoneContracts(queryBot.getId(), documentId, documentNumber, PhoneContractOperator.MOVISTAR);
            botDAO.updateQueryResultPhoneContracts(queryBot.getId(), result);
            registerSucces(queryBot);
        } catch (Exception e) {
            logger.error("saveMovistarResult exception", e);
        }
    }

    private void saveEntelResult(LoanApplication loanApplication, int documentId, String documentNumber, List<TelephoneService> telephoneServices) {
        LineaResult result = new LineaResult();

        if (telephoneServices != null) {
            List<TelephoneService> entelServices = telephoneServices
                    .stream()
                    .filter(e -> Objects.equals(e.getCompany(), "Entel"))
                    .collect(Collectors.toList());

            if (!entelServices.isEmpty()) {
                List<Linea> lineas = new ArrayList<>();
                entelServices.forEach(e -> lineas.add(new Linea(e.getPhoneNumber(), e.getServiceType())));

                result.setCantidad(lineas.size());
                result.setOperador(PhoneContractOperator.ENTEL);
                result.setLineas(new Gson().toJson(lineas));
            }
        }

        try {
            QueryBot queryBot = botDAO.registerQuery(Bot.ENTEL, QueryBot.STATUS_QUEUE, loanApplication.getUserId());
            botDAO.registerQueryResultPhoneContracts(queryBot.getId(), documentId, documentNumber, PhoneContractOperator.ENTEL);
            botDAO.updateQueryResultPhoneContracts(queryBot.getId(), result);
            registerSucces(queryBot);
        } catch (Exception e) {
            logger.error("saveEntelResult exception", e);
        }
    }

    private void saveBitelResult(LoanApplication loanApplication, int documentId, String documentNumber, List<TelephoneService> telephoneServices)  {
        LineaResult result = new LineaResult();

        if (telephoneServices != null) {
            List<TelephoneService> bitelServices = telephoneServices
                    .stream()
                    .filter(e -> Objects.equals(e.getCompany(), "Bitel"))
                    .collect(Collectors.toList());

            if (!bitelServices.isEmpty()) {
                List<Linea> lineas = new ArrayList<>();
                bitelServices.forEach(e -> lineas.add(new Linea(e.getPhoneNumber(), e.getServiceType())));

                result.setCantidad(lineas.size());
                result.setOperador(PhoneContractOperator.BITEL);
                result.setLineas(new Gson().toJson(lineas));
            }
        }

        try {
            QueryBot queryBot = botDAO.registerQuery(Bot.BITEL, QueryBot.STATUS_QUEUE, loanApplication.getUserId());
            botDAO.registerQueryResultPhoneContracts(queryBot.getId(), documentId, documentNumber, PhoneContractOperator.BITEL);
            botDAO.updateQueryResultPhoneContracts(queryBot.getId(), result);
            registerSucces(queryBot);
        } catch (Exception e) {
            logger.error("saveBitelResult exception", e);
        }
    }

    private void registerSucces(QueryBot queryBot) throws Exception {
        queryBot.setStatusId(QueryBot.STATUS_SUCCESS);
        queryBot.setFinishTime(new Date());
        botDAO.updateQuery(queryBot);
    }

    private static Date convertToDate(String date) {
        try {
            return new SimpleDateFormat("dd/MM/yy").parse(date);
        } catch (Exception e) {
            logger.error(String.format("Error on parse date %s. Exception: %s", date, e.getMessage()));
            return null;
        }

    }
}
