package com.affirm.common.service;

import com.affirm.acceso.model.EvaluacionGenericaResponse;
import com.affirm.bancodelsol.service.impl.BancoDelSolServiceImpl;
import com.affirm.bantotalrest.model.RBTPCO12.BTPersonasObtenerFATCAResponse;
import com.affirm.bantotalrest.model.RBTPG019.BTPersonasValidarEnListasNegrasResponse;
import com.affirm.bantotalrest.model.RBTPG085.BTPersonasObtenerResponse;
import com.affirm.bantotalrest.model.RBTPG292.BTPersonasObtenerDatosPEPResponse;
import com.affirm.common.dao.*;
import com.affirm.common.model.*;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.util.*;
import com.affirm.fdlm.datacredito.model.DatosClienteResponse;
import com.affirm.fdlm.topaz.model.ExecuteResponse;
import com.affirm.nosis.Dato;
import com.affirm.nosis.NosisResult;
import com.affirm.nosis.restApi.NosisRestResult;
import com.affirm.prisma.model.SentinelPrismaResult;
import com.affirm.security.model.EntityWsResult;
import com.affirm.sentinel.infpri.SDTInfCopacEnt;
import com.affirm.sentinel.infpri.WSSentinelInfPriExecuteResponse;
import com.affirm.sentinel.rest.ResultadoEvaluacionTitularResponse;
import com.affirm.system.configuration.Configuration;
import com.affirm.veraz.model.VerazResponse;
import com.affirm.veraz.model.VerazRestResponse;
import com.google.gson.Gson;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTimeComparator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PolicyService {

    private static final Logger logger = Logger.getLogger(PolicyService.class);
    private static final String BDS_AUTONOMO = "AUTONOMO";
    private static final String BDS_MONOTRIBUTISTA = "MONOTRIBUTISTA";
    private static final String BDS_JUBILADO = "JUBILADO";
    private static final String BDS_RELACION_DEPENDENCIA = "DEPENDENCIA";

    private final EvaluationCacheService evaluationCacheService;
    private final EvaluationDAO evaluationDAO;
    private final LoanApplicationDAO loanApplicationDAO;
    private final UserDAO userDAO;
    private final PersonDAO personDAO;
    private final CatalogService catalogService;
    private final CatalogDAO catalogDAO;
    private final RccDAO rccDAO;
    private final UtilService utilService;

    public PolicyService(EvaluationCacheService evaluationCacheService, EvaluationDAO evaluationDAO, LoanApplicationDAO loanApplicationDAO, UserDAO userDAO, PersonDAO personDAO, CatalogService catalogService, CatalogDAO catalogDAO, RccDAO rccDAO, UtilService utilService) {
        this.evaluationCacheService = evaluationCacheService;
        this.evaluationDAO = evaluationDAO;
        this.loanApplicationDAO = loanApplicationDAO;
        this.userDAO = userDAO;
        this.personDAO = personDAO;
        this.catalogService = catalogService;
        this.catalogDAO = catalogDAO;
        this.rccDAO = rccDAO;
        this.utilService = utilService;
    }

    public boolean runPolicies(Integer loanApplicationId, Integer policyId, String param1, String param2, String param3, Integer employerId, Integer entityProductParameterId, Integer evaluationId, Map<String, Object> cachedSources) throws Exception {
        logger.debug(String.format("LoanApplicationId: %s. PolicyId: %s. Param1: %s. Param2: %s. Param3: %s. EmployerId: %s. EntityProductParameterId: %s", loanApplicationId, policyId, param1, param2, param3, employerId, entityProductParameterId));// TODO DEBUG

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            LoanApplication loanApplication = evaluationCacheService.getLoanApplication(loanApplicationId, cachedSources);
            LoanApplicationEvaluation loanApplicationEvaluation = evaluationCacheService.getEvaluationsByLoanApplication(loanApplicationId, cachedSources)
                    .stream()
                    .filter(e -> e.getId().equals(evaluationId))
                    .findFirst()
                    .orElse(null);

            switch (policyId) {
                case Policy.UNEMPLOYED: {
                    List<PersonOcupationalInformation> personOcupations = evaluationCacheService.getOcupationalInformations(loanApplication.getPersonId(), cachedSources);
                    Integer activiyType1 = personOcupations
                            .stream()
                            .filter(o -> o.getNumber() == o.PRINCIPAL)
                            .findFirst()
                            .map(o -> o.getActivityType().getId())
                            .orElse(null);
                    Integer activiyType2 = personOcupations
                            .stream()
                            .filter(o -> o.getNumber() == o.SECUNDARY)
                            .findFirst()
                            .map(o -> o.getActivityType().getId())
                            .orElse(null);
                    if ((activiyType1 != null && activiyType1 == ActivityType.UNEMPLOYED) || (activiyType2 != null && activiyType2 == ActivityType.UNEMPLOYED)) {
                        double sumFixedGrossIncome = personOcupations
                                .stream()
                                .filter(poi -> poi.getActivityType().getId() != ActivityType.UNEMPLOYED && poi.getFixedGrossIncome() != null)
                                .mapToDouble(PersonOcupationalInformation::getFixedGrossIncome).sum();
                        if (sumFixedGrossIncome == 0)
                            return false;
                    }
                    break;
                }
                case Policy.MINIMUM_INCOMES: {
                    List<PersonOcupationalInformation> personOcupations = evaluationCacheService.getOcupationalInformations(loanApplication.getPersonId(), cachedSources);
                    Integer activiyType1 = personOcupations
                            .stream()
                            .filter(o -> o.getNumber() == o.PRINCIPAL)
                            .findFirst()
                            .map(o -> o.getActivityType().getId())
                            .orElse(null);
                    Integer activiyType2 = personOcupations.stream()
                            .filter(o -> o.getNumber() == o.SECUNDARY)
                            .findFirst()
                            .map(o -> o.getActivityType().getId())
                            .orElse(null);

                    double comparatorNumber;

                    if (param1 == null) {
                        int minIncome = catalogService.getActivityTypes(Configuration.getDefaultLocale(), null)
                                .stream()
                                .filter(a -> (a.getId().equals(activiyType1) || a.getId().equals(activiyType2)) && a.getMinIncome() != null)
                                .mapToInt(a -> a.getMinIncome())
                                .min().orElse(0);

                        comparatorNumber = minIncome * 1.0;
                    } else {
                        comparatorNumber = Double.parseDouble(param1);
                    }

                    Double admissionTotalIncome = evaluationCacheService.getAdmissionTotalIncome(loanApplicationId, loanApplicationEvaluation.getEntityId(), loanApplicationEvaluation.getProductId(), cachedSources);
                    if (admissionTotalIncome < comparatorNumber) {
                        return false;
                    }
                    break;
                }
                case Policy.OVERINDEBTEDNESS: {

                    Integer clusterId = evaluationCacheService.getClusterId(loanApplicationEvaluation.getEntityId(), loanApplicationId, loanApplicationEvaluation.getEntityProductParameterId(), cachedSources);
                    List<RateCommissionCluster> rateCommissionClusters = evaluationCacheService.getRateCommissionClusters(cachedSources);
                    List<RateCommission> rateCommissionClustersFiltered = rateCommissionClusters.stream()
                            .filter(c -> c.getRateCommissions() != null)
                            .flatMap(c -> c.getRateCommissions().stream())
                            .filter(c -> c.getProductId().equals(loanApplicationEvaluation.getProductId()) &&
                                    c.getEntityId().equals(loanApplicationEvaluation.getEntityId()) &&
                                    (c.getClusterId() != 0 ? c.getClusterId().equals(clusterId) : true)
                            )
                            .collect(Collectors.toList());
                    Double maxAmount = rateCommissionClustersFiltered
                            .stream()
                            .filter(r -> r.getMaxAmountCommission() != null)
                            .mapToDouble(RateCommission::getMaxAmountCommission)
                            .max()
                            .orElse(0.0);
                    Integer maxInstallments = rateCommissionClustersFiltered
                            .stream()
                            .filter(r -> r.getInstallments() != null)
                            .mapToInt(RateCommission::getInstallments)
                            .max()
                            .orElse(0);
                    Double minEffectiveAnnualRate = rateCommissionClustersFiltered
                            .stream()
                            .filter(r -> r.getEffectiveAnualRate() != null)
                            .mapToDouble(RateCommission::getEffectiveAnualRate)
                            .min()
                            .orElse(0.0);
                    Double tem = Math.pow((1 + (minEffectiveAnnualRate / 100.0)), (30.0 / 365.0)) - 1;
                    Double installmentAmount = (maxAmount * Math.pow((1 + tem), maxInstallments)) / (Math.pow((1 + tem), maxInstallments) - 1) * tem;
                    Double maxInstallmentForEvaluation = evaluationCacheService.getMaxInstallment(loanApplicationId, loanApplicationEvaluation.getEntityId(), loanApplicationEvaluation.getProductId(), loanApplicationEvaluation.getEntityProductParameterId(), cachedSources);

                    if (maxInstallmentForEvaluation < installmentAmount)
                        return false;

                    break;
                }
                case Policy.SUNAT_DEBT: {
                    SunatResult sunatResult = evaluationCacheService.getSunatResultFromCache(loanApplication.getPersonId(), cachedSources);
                    if (sunatResult != null && sunatResult.getTaxpayerCondition() != null) {
                        if (sunatResult.getTaxpayerCondition().toUpperCase().startsWith("NO HABIDO") || sunatResult.getStatus().toUpperCase().contains("COACTIV") || sunatResult.getTaxpayerCondition().toUpperCase().contains("COACTIV"))
                            return false;
                    }
                    break;
                }
                case Policy.REDAM_DEBT: {
                    RedamResult redamResult = evaluationCacheService.getRedamResultFromCache(loanApplication.getPersonId(), cachedSources);
                    double amountDue = redamResult != null && redamResult.getAmountDue() != null ? redamResult.getAmountDue() : 0;
                    double admissionTotalIncome = evaluationCacheService.getAdmissionTotalIncome(loanApplicationId, loanApplicationEvaluation.getEntityId(), loanApplicationEvaluation.getProductId(), cachedSources);
                    double param1Parsed = param1 == null ? 0.0 : Double.parseDouble(param1);

                    if (amountDue >= (admissionTotalIncome / param1Parsed))
                        return false;

                    break;
                }
                case Policy.PARTNER_U1MN: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    if (person.getPartner() != null) {
                        int param1Parsed = param1 != null ? Integer.parseInt(param1) : 2;
                        int param2Parsed = param2 != null ? Integer.parseInt(param2) : 1;
                        boolean partnerRccEvaluation = evaluationDAO.evaluatePartnerRcc(person.getPartner().getId(), param1Parsed, param2Parsed);
                        if (partnerRccEvaluation)
                            return false;
                    }
                    break;
                }
                case Policy.PARTNER_UXMDP: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    if (person.getPartner() != null && person.getPartner().getDocumentNumber() != null) {
                        List<RccIde> rccIdeList = evaluationCacheService.getRccIdeGrouped(person.getPartner().getDocumentNumber(), cachedSources)
                                .stream()
                                .map(RccIdeGrouped::getRccIde)
                                .limit(Integer.parseInt(param1))
                                .collect(Collectors.toList());
                        Double sumCal3 = rccIdeList
                                .stream()
                                .mapToDouble(r -> r.getPorCal3() != null ? r.getPorCal3() : 0)
                                .sum();
                        Double sumCal4 = rccIdeList
                                .stream()
                                .mapToDouble(r -> r.getPorCal4() != null ? r.getPorCal4() : 0)
                                .sum();

                        if (sumCal3 + sumCal4 > 0) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.PARTNER_X_MONTHS_RCC: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    if (person.getPartner() != null && person.getPartner().getDocumentNumber() != null) {
                        List<RccIde> rccIdeList = evaluationCacheService.getRccIdeGrouped(person.getPartner().getDocumentNumber(), cachedSources)
                                .stream()
                                .map(RccIdeGrouped::getRccIde)
                                .collect(Collectors.toList());

                        if (rccIdeList.size() <= Integer.parseInt(param1)) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.PARTNER_CASTIGOS: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    if (person.getPartner() != null) {
                        List<Date> rccDateList = evaluationCacheService.getRccDatesSorted(cachedSources)
                                .stream()
                                .limit(param1 == null || "" .equals(param1) ? 1 : Integer.parseInt(param1))
                                .map(RccDate::getFecRep)
                                .collect(Collectors.toList());

                        double sumSaldoCastigo = evaluationCacheService.getRccSynthesized(person.getPartner().getDocumentNumber(), cachedSources)
                                .stream()
                                .filter(r -> rccDateList.stream().anyMatch(rdl -> DateTimeComparator.getDateOnlyInstance().compare(rdl, r.getFecRep()) == 0))
                                .filter(r -> r.getSaldoCastigo() != null)
                                .mapToDouble(RccSynthesized::getSaldoCastigo)
                                .sum();

                        if (sumSaldoCastigo > (param2 == null || "" .equals(param2) ? 0 : Double.parseDouble(param2))) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.PARTNER_JUDICIAL: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    if (person.getPartner() != null) {
                        List<Date> rccDateList = evaluationCacheService.getRccDatesSorted(cachedSources)
                                .stream()
                                .limit(param1 == null || "" .equals(param1) ? 1 : Integer.parseInt(param1))
                                .map(RccDate::getFecRep)
                                .collect(Collectors.toList());

                        double sumSaldoJudicial = evaluationCacheService.getRccSynthesized(person.getPartner().getDocumentNumber(), cachedSources)
                                .stream()
                                .filter(r -> rccDateList.stream().anyMatch(rdl -> DateTimeComparator.getDateOnlyInstance().compare(rdl, r.getFecRep()) == 0))
                                .filter(r -> r.getSaldoJudicial() != null)
                                .mapToDouble(RccSynthesized::getSaldoJudicial)
                                .sum();

                        if (sumSaldoJudicial > (param2 == null || "" .equals(param2) ? 0 : Double.parseDouble(param2))) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.INFORMAL: {
                    EssaludResult essaludResult = evaluationCacheService.getEssaludResultFromCache(loanApplication.getPersonId(), cachedSources);
                    SunatResult sunatResult = evaluationCacheService.getSunatResultFromCache(loanApplication.getPersonId(), cachedSources);
                    if (sunatResult != null && essaludResult != null) {
                        Date essaludValidUntil = essaludResult.getUntil();
                        if (essaludValidUntil != null && essaludValidUntil.after(new Date()))
                            return false;
                    }
                    break;
                }
                case Policy.HOUSEKEEPER: {
                    List<PersonOcupationalInformation> personOcupations = evaluationCacheService.getOcupationalInformations(loanApplication.getPersonId(), cachedSources);
                    Integer activiyType1 = personOcupations
                            .stream()
                            .filter(o -> o.getNumber() == o.PRINCIPAL)
                            .findFirst()
                            .map(o -> o.getActivityType().getId())
                            .orElse(null);
                    if (activiyType1 != null && activiyType1.equals(ActivityType.HOUSEKEEPER)) {
                        EssaludResult essaludResult = evaluationCacheService.getEssaludResultFromCache(loanApplication.getPersonId(), cachedSources);
                        Date validUntil = essaludResult != null ? essaludResult.getUntil() : null;
                        if (validUntil == null || !validUntil.after(new Date())) {
                            return false;
                        }
                    }
                    break;
                }
                case Policy.EMPLOYMENT_TIME_1: {
                    PersonOcupationalInformation personOcupationalInformation1 = evaluationCacheService.getOcupationalInformations(loanApplication.getPersonId(), cachedSources)
                            .stream()
                            .filter(p -> p.getNumber() == PersonOcupationalInformation.PRINCIPAL)
                            .findFirst()
                            .orElse(null);
                    Date startDate = personOcupationalInformation1 != null ? personOcupationalInformation1.getStartDate() : null;
                    Integer param1Parsed = param1 != null ? Integer.parseInt(param1) : null;
                    if (startDate != null && param1Parsed != null) {
                        LocalDate startDateLocal = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate nowDateLocal = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        long monthsBetween = ChronoUnit.MONTHS.between(startDateLocal, nowDateLocal);
                        if (monthsBetween < param1Parsed) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.EXPIRED_CONTRACT: {
                    Employee employee = evaluationCacheService.getEmployeeFromCache(loanApplication.getPersonId(), employerId, cachedSources);
                    if (employee != null) {
                        boolean contractEndDateLessThanCurrent = employee.getContractEndDate() != null && !DateUtils.truncate(employee.getContractEndDate(), Calendar.DATE).after(DateUtils.truncate(new Date(), Calendar.DATE));
                        boolean contractTypeIsD = employee.getContractType() != null && employee.getContractType() == 'D';

                        if (contractEndDateLessThanCurrent && contractTypeIsD) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.SALARY_GANISHMENT: {
                    Employee employee = evaluationCacheService.getEmployeeFromCache(loanApplication.getPersonId(), employerId, cachedSources);
                    boolean isSalaryGarnishment = employee != null && employee.getSalaryGarnishment();

                    if (isSalaryGarnishment) {
                        return false;
                    }

                    break;
                }
                case Policy.UNPAID_LEAVE: {
                    Employee employee = evaluationCacheService.getEmployeeFromCache(loanApplication.getPersonId(), employerId, cachedSources);
                    boolean isUnpaidLeave = employee != null && employee.getUnpaidLeave();

                    if (isUnpaidLeave) {
                        return false;
                    }

                    break;
                }
                case Policy.NEW_LOAN_NOT_ALLOWED: {
                    LoanApplicationReclosure loanApplicationReclosure = evaluationCacheService.getLoanApplicationReclosure(loanApplicationId, cachedSources);
                    boolean isNotReclosurable = loanApplicationReclosure != null && !loanApplicationReclosure.getReclosureable();

                    if (isNotReclosurable) {
                        return false;
                    }

                    break;
                }
                case Policy.AGREEMENT_NOT_AVAILABLE: {
                    PersonOcupationalInformation personOcupationalInformation1 = evaluationCacheService.getOcupationalInformations(loanApplication.getPersonId(), cachedSources)
                            .stream()
                            .filter(p -> p.getNumber() == PersonOcupationalInformation.PRINCIPAL)
                            .findFirst()
                            .orElse(null);

                    Employee employee = evaluationCacheService.getEmployeeFromCache(loanApplication.getPersonId(), employerId, cachedSources);
                    Integer employeeId = employee != null && employee.getEmployer().getRuc().equals(personOcupationalInformation1.getRuc()) ? employee.getId() : null;

                    if (employeeId == null)
                        return false;
                    else
                        personDAO.updateEmployeePerson(employeeId, loanApplication.getPersonId());

                    break;
                }
                case Policy.DISPOSABLE_INCOME: {

                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);

                    Double sbsMonthlyInstallment = evaluationCacheService.getMonthlyInstallmentTotal(person.getDocumentNumber(), cachedSources);

                    Double sumFixedGrossIncome = evaluationCacheService.getOcupationalInformations(loanApplication.getPersonId(), cachedSources)
                            .stream()
                            .filter(p -> p.getFixedGrossIncome() != null)
                            .mapToDouble(PersonOcupationalInformation::getFixedGrossIncome)
                            .sum();
                    Double sumVariableGrossIncome = evaluationCacheService.getOcupationalInformations(loanApplication.getPersonId(), cachedSources)
                            .stream()
                            .filter(p -> p.getVariableGrossIncome() != null)
                            .mapToDouble(PersonOcupationalInformation::getVariableGrossIncome)
                            .sum();

                    double disposableIncome = sumFixedGrossIncome + sumVariableGrossIncome - sbsMonthlyInstallment;

                    if (disposableIncome < Double.parseDouble(param1)) {
                        return false;
                    }

                    break;
                }
                case Policy.INCOME_TO_MONTHLY_EARNINGS_RATIO: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    Double sbsMonthlyInstallment;
                    if (loanApplicationEvaluation.getEntityId() == Entity.ACCESO) {
                        sbsMonthlyInstallment = evaluationDAO.getMonthlyInstallmentAcceso(person.getDocumentNumber());
                    } else {
                        sbsMonthlyInstallment = evaluationCacheService.getMonthlyInstallmentTotal(person.getDocumentNumber(), cachedSources);
                    }

                    Integer clusterId = evaluationCacheService.getClusterId(loanApplicationEvaluation.getEntityId(), loanApplicationId, loanApplicationEvaluation.getEntityProductParameterId(), cachedSources);
                    List<RateCommissionCluster> rateCommissionClusters = evaluationCacheService.getRateCommissionClusters(cachedSources);
                    List<RateCommission> rateCommissionClustersFiltered = rateCommissionClusters.stream()
                            .filter(c -> c.getRateCommissions() != null)
                            .flatMap(c -> c.getRateCommissions().stream())
                            .filter(c -> c.getProductId().equals(loanApplicationEvaluation.getProductId()) &&
                                    c.getEntityId().equals(loanApplicationEvaluation.getEntityId()) &&
                                    (c.getClusterId() != 0 ? c.getClusterId().equals(clusterId) : true)
                            )
                            .collect(Collectors.toList());

                    List<Product> products = catalogService.getProductsEntity().stream().filter(p -> p.getId().equals(loanApplicationEvaluation.getProductId())).collect(Collectors.toList());
                    int minAmount = products.stream()
                            .map(p -> p.getProductParams(loanApplication.getCountryId(), loanApplicationEvaluation.getEntityId()))
                            .mapToInt(p -> p.getMinAmount() != null ? p.getMinAmount() : 0)
                            .min().orElse(0);
                    int installments = rateCommissionClustersFiltered
                            .stream()
                            .filter(r -> r.getInstallments() != null)
                            .mapToInt(r -> r.getInstallments())
                            .max().orElse(0);
                    Double minTem = rateCommissionClustersFiltered
                            .stream()
                            .filter(r -> r.getEffectiveAnualRate() != null)
                            .mapToDouble(r -> r.getEffectiveAnualRate())
                            .min().orElse(0.0);
                    Double tem = Math.pow((1 + (minTem / 100)), (30.0 / 365.0)) - 1;
                    Double installmentAmount = (minAmount * Math.pow((1 + tem), installments)) / (Math.pow((1 + tem), installments) - 1) * tem;
                    Double admissionTotalIncome = evaluationCacheService.getAdmissionTotalIncome(loanApplicationId, loanApplicationEvaluation.getEntityId(), loanApplicationEvaluation.getProductId(), cachedSources);

                    // Adjust the RCI param for acceso according to the Scores WS response
                    String finalParam1 = param1;
                    if(EntityProductParams.ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD.contains(entityProductParameterId)){
                        EntityWsResult accesoScoreLDConsumo = evaluationCacheService.getAccesoScoreLDConsumoWSResult(loanApplicationId, cachedSources);
                        if (accesoScoreLDConsumo != null && accesoScoreLDConsumo.getResult() != null) {
                            JSONObject result = accesoScoreLDConsumo.getResult();
                            if (result.has("data")) {
                                JSONObject innerData = result.getJSONObject("data");
                                if (innerData.has("nivelRiesgo")) {
                                    Integer nivelRiesgo = JsonUtil.getIntFromJson(innerData, "nivelRiesgo", null);
                                    if(nivelRiesgo == 1) finalParam1 = "0.6";
                                    if(nivelRiesgo == 2) finalParam1 = "0.5";
                                    if(nivelRiesgo == 3) finalParam1 = "0.4";
                                } else {
                                    return false;
                                }
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }

                    boolean firstValidation = ((admissionTotalIncome - sbsMonthlyInstallment) / admissionTotalIncome) < Double.parseDouble(finalParam1);
                    if (firstValidation) {
                        return false;
                    }

                    Double evaluationMaxInstallments = evaluationCacheService.getMaxInstallment(loanApplicationId, loanApplicationEvaluation.getEntityId(), loanApplicationEvaluation.getProductId(), loanApplicationEvaluation.getEntityProductParameterId(), cachedSources);
                    if (evaluationMaxInstallments < installmentAmount) {
                        return false;
                    }

                    break;
                }
                case Policy.AVOID_DISTRICTS_HOME: {
                    Address address = evaluationCacheService.getAddressByPersonId(loanApplication.getPersonId(), cachedSources);
                    if (address != null && address.getUbigeoId() != null) {
                        Ubigeo ubigeo = catalogService.getUbigeo(address.getUbigeoId());
                        String ubigeoIneiId = ubigeo.getIneiUbigeoId();
                        List<String> param1Parsed = param1 == null ? new ArrayList<>() : Arrays.asList(param1.replaceAll("\\{", "").replaceAll("\\}", "").split(","));

                        if ("in" .equalsIgnoreCase(param3) && param1Parsed.contains(ubigeoIneiId.substring(0, Integer.parseInt(param2)))) {
                            return false;
                        } else if ("out" .equalsIgnoreCase(param3) && !param1Parsed.contains(ubigeoIneiId.substring(0, Integer.parseInt(param2)))) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.AVOID_DISTRICTS_NON_PERU_UBIGEOS: {
                    Address address = evaluationCacheService.getAddressByPersonId(loanApplication.getPersonId(), cachedSources);
                    Ubigeo ubigeo = address != null ? catalogService.getUbigeo(address.getUbigeoId()) : null;
                    int param2Parsed = Integer.parseInt(param2);

                    if (ubigeo != null) {
                        int idToFind = 0;
                        if (param2Parsed == 1) {
                            idToFind = ubigeo.getDepartment().getDepartmentId();
                        } else if (param2Parsed == 2) {
                            idToFind = ubigeo.getProvince().getProvinceId();
                        } else if (param2Parsed == 3) {
                            idToFind = ubigeo.getDistrict().getDistrictId().intValue();
                        }

                        final int id = idToFind;
                        if (Arrays
                                .stream(param1.replaceAll("\\{", "").replaceAll("\\}", "").split(","))
                                .map(Integer::parseInt)
                                .anyMatch(s -> s == id)) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.AVOID_DISTRICTS_OCUPATION: {
                    PersonOcupationalInformation poi = evaluationCacheService.getOcupationalInformations(loanApplication.getPersonId(), cachedSources)
                            .stream()
                            .filter(p -> p.getNumber() == PersonOcupationalInformation.PRINCIPAL && p.getAddressUbigeo() != null)
                            .findFirst()
                            .orElse(null);
                    if (poi != null) {
                        boolean inOutCase = false;
                        List<String> param1Parsed = param1 == null ? new ArrayList<>() : Arrays.asList(param1.replaceAll("\\{", "").replaceAll("\\}", "").split(","));
                        String inei = poi.getAddressUbigeo().getIneiUbigeoId();

                        if (param3.equalsIgnoreCase("in")) {
                            inOutCase = param1Parsed.contains(inei.substring(0, Integer.parseInt(param2)));
                        } else if (param3.equalsIgnoreCase("out")) {
                            inOutCase = !param1Parsed.contains(inei.substring(0, Integer.parseInt(param2)));
                        }

                        if (inOutCase) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.SAME_PROVINCE_JOB_AND_HOME: {
                    Address address = evaluationCacheService.getAddressByPersonId(loanApplication.getPersonId(), cachedSources);
                    Ubigeo ubigeoHome = address != null ? catalogService.getUbigeo(address.getUbigeoId()) : null;
                    String homeIneiId = ubigeoHome != null ? ubigeoHome.getDepartment().getIneiId() : null;

                    String jobIneiId = evaluationCacheService.getOcupationalInformations(loanApplication.getPersonId(), cachedSources)
                            .stream()
                            .filter(p -> p.getNumber() == PersonOcupationalInformation.PRINCIPAL)
                            .map(p -> p.getAddressUbigeo().getDepartment().getIneiId())
                            .findFirst()
                            .orElse(null);

                    if (homeIneiId != null && jobIneiId != null && !homeIneiId.equalsIgnoreCase(jobIneiId)) {
                        return false;
                    }

                    break;
                }
                case Policy.NOT_IN_LIMA: {
                    Address address = evaluationCacheService.getAddressByPersonId(loanApplication.getPersonId(), cachedSources);
                    if (address != null && address.getUbigeoId() != null) {
                        Ubigeo ubigeo = catalogService.getUbigeo(address.getUbigeoId());

                        if (!Arrays.asList("15", "07").contains(ubigeo.getDepartment().getIneiId())) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.HOUSING_TYPE: {
                    Address address = evaluationCacheService.getAddressByPersonId(loanApplication.getPersonId(), cachedSources);
                    List<Integer> param1Parsed = param1 == null ? new ArrayList<>() : Arrays.stream(param1.replaceAll("\\{", "").replaceAll("\\}", "").split(",")).map(Integer::parseInt).collect(Collectors.toList());
                    if (param1Parsed.contains(address.getHousingTypeId())) {
                        return false;
                    }

                    break;
                }
                // TODO 86?????
                case Policy.RENT_TYPE: {

                    PersonOcupationalInformation personOcupationalInformation = evaluationCacheService.getOcupationalInformations(loanApplication.getPersonId(), cachedSources)
                            .stream()
                            .filter(p -> p.getNumber() == PersonOcupationalInformation.PRINCIPAL)
                            .filter(p -> p.getActivityType().getId() == ActivityType.DEPENDENT || p.getActivityType().getId() == ActivityType.INDEPENDENT)
                            .findFirst().orElse(null);

                    boolean existsOcupationalInformationWithFilters = false;
                    if (personOcupationalInformation.getActivityType().getId() == ActivityType.DEPENDENT) {
                        EssaludResult essaludResult = evaluationCacheService.getEssaludResultFromCache(loanApplication.getPersonId(), cachedSources);
                        if (essaludResult == null || essaludResult.getUntil() == null || essaludResult.getUntil().after(DateUtils.truncate(new Date(), Calendar.DATE))) {
                            existsOcupationalInformationWithFilters = true;
                        }
                    } else if (personOcupationalInformation.getActivityType().getId() == ActivityType.INDEPENDENT) {
                        if (personOcupationalInformation.getSubActivityType() != null && personOcupationalInformation.getSubActivityType().getId() == SubActivityType.PROFESSIONAL_SERVICE) {

                            SunatResult sunatResult = evaluationCacheService.getSunatResultFromCache(loanApplication.getPersonId(), cachedSources);
                            String sunatRuc = sunatResult == null ? null : sunatResult.getRuc();
                            if (sunatRuc == null || personOcupationalInformation.getRuc().equals(sunatRuc)) {
                                existsOcupationalInformationWithFilters = true;
                            }
                        }
                    }

                    if (!existsOcupationalInformationWithFilters) {
                        return false;
                    }

                    break;
                }
                case Policy.PAREJA_SITUACION_5:
                case Policy.PAREJA_SITUACION_4:
                case Policy.PAREJA_SITUACION_3:
                case Policy.PAREJA_SITUACION_2: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    BcraResult bcraResult = evaluationCacheService.getBcraResultFromCache(person.getPartner().getId(), cachedSources);
                    if (bcraResult != null && bcraResult.getHistorialDeudas() != null) {
                        List<BcraResult.DeudaBanco.RegistroDeuda> registroDeudaList = new ArrayList<>();
                        registroDeudaList = bcraResult.getHistorialDeudas().stream().flatMap(h -> h.getHistorial().stream()).collect(Collectors.toList());

                        registroDeudaList.forEach(rd -> {
                            rd.setPeriodo("01/" + rd.getPeriodo());
                            rd.setPeriodo(rd.getPeriodo().replaceAll("/", "-"));
                        });

                        registroDeudaList = registroDeudaList
                                .stream()
                                .limit(Integer.parseInt(param3))
                                .skip(Integer.parseInt(param2))
                                .collect(Collectors.toList());
//                    TODO NOT SURE IF IS DESC ORDER
                        registroDeudaList.sort(new Comparator<BcraResult.DeudaBanco.RegistroDeuda>() {
                            @Override
                            public int compare(BcraResult.DeudaBanco.RegistroDeuda o1, BcraResult.DeudaBanco.RegistroDeuda o2) {
                                int comparator = 0;
                                try {
                                    comparator = sdf.parse(o2.getPeriodo()).compareTo(sdf.parse(o1.getPeriodo()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return comparator;
                            }
                        });

                        int maxSituacion = registroDeudaList
                                .stream()
                                .mapToInt(BcraResult.DeudaBanco.RegistroDeuda::getSituacionAsInt)
                                .max()
                                .orElse(0);
//                    double monto = registroDeudaList.stream().mapToDouble(BcraResult.DeudaBanco.RegistroDeuda::getMontoDouble).sum();

                        if (maxSituacion >= Integer.parseInt(param1)) {
                            return false;
                        }
                    }


                    if (bcraResult != null && bcraResult.getHistorialDeudas() != null) {
                    }

//                    Map<String, List<BcraResult.DeudaBanco.RegistroDeuda>> grouped = registroDeudaList.stream().collect(Collectors.groupingBy(r -> r.getPeriodo()));
//                    grouped.entrySet().stream()


                    break;
                }
                case Policy.NSE_BUREAU: {
                    ApplicationBureau applicationBureau = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)
                            .stream()
                            .filter(b -> b.getBureauId() == Bureau.NOSIS)
                            .findFirst()
                            .orElse(null);
                    NosisResult nosisResult = applicationBureau != null ? applicationBureau.getNosisResult() : null;
                    List<String> param1Parsed = param1 == null ? new ArrayList<>() : Arrays.asList(param1.replaceAll("\\{", "").replaceAll("\\}", "").split(","));
                    boolean firstValidation = nosisResult.getParteXML()
                            .getDatos()
                            .stream()
                            .filter(d -> d.getCalculoCDA() != null)
                            .flatMap(d -> d.getCalculoCDA().getItems().stream())
                            .filter(i -> i.getClave() != null && i.getClave().equalsIgnoreCase("NSE"))
                            .map(Dato.CalculoCDA.Item::getValor)
                            .anyMatch(param1Parsed::contains);

                    if (!firstValidation || applicationBureau == null) {
                        return false;
                    }

                    break;
                }
                case Policy.ALERTAS_JUDICIALES_QUIEBRA_JUICIOS: {

                    for (ApplicationBureau bureau :
                            evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)) {

                        if (bureau.getBureauId() == Bureau.NOSIS_BDS && bureau.getEquifaxResult() != null) {
                            JSONObject equifaxResult = new JSONObject(bureau.getEquifaxResult());
                            if (equifaxResult.has("Contenido")
                                    && equifaxResult.getJSONObject("Contenido").has("Datos")
                                    && equifaxResult.getJSONObject("Contenido").getJSONObject("Datos").has("Variables")) {

                                JSONArray resultado_nosis = equifaxResult.getJSONObject("Contenido").getJSONObject("Datos").getJSONArray("Variables");
                                if (resultado_nosis != null) {
                                    int sum = StreamSupport
                                            .stream(resultado_nosis.spliterator(), false)
                                            .map(j -> (JSONObject) j)
                                            .filter(j ->
                                                    JsonUtil.getStringFromJson(j, "Nombre", null) != null
                                                            && Arrays.asList("CQ_12m_Cant", "CQ_60m_Cant", "PQ_12m_Cant", "PQ_60m_Cant", "JU_12m_Cant", "JU_60m_Cant").contains(JsonUtil.getStringFromJson(j, "Nombre", null)))
                                            .mapToInt(j -> JsonUtil.getIntFromJson(j, "Valor", 0))
                                            .sum();

                                    if (sum > 0)
                                        return false;
                                }
                            }
                        } else if (bureau.getBureauId() == Bureau.NOSIS && bureau.getEquifaxResult() != null) {
                            if (bureau.getNosisResult() != null) {
                                if (bureau.getNosisResult().getParteXML() != null) {
                                    if (bureau.getNosisResult().getParteXML().getDatos() != null) {
                                        boolean anyJuicio = bureau.getNosisResult().getParteXML().getDatos().stream().anyMatch(d -> d.getJuicio() != null);
                                        if (anyJuicio)
                                            return false;
                                    }
                                }
                            }


                        }
                    }

                    break;
                }
                case Policy.ENDEUDAMIENTO: {
                    ProductMaxMinParameter productMaxMinParameter = catalogDAO.getProductsEntity()
                            .stream()
                            .flatMap(p -> p.getProductMaxMinParameters().stream())
                            .filter(pmm -> pmm.getEntityId() != null && pmm.getEntityId().equals(loanApplicationEvaluation.getEntityId()))
                            .findFirst()
                            .orElse(null);
                    double minEffectiveAnnualRate = catalogDAO.getRateCommissionClusters()
                            .stream()
                            .flatMap(r -> r.getRateCommissions().stream())
                            .filter(r -> r.getEntityId().equals(loanApplicationEvaluation.getEntityId()) && r.getProductId().equals(loanApplicationEvaluation.getProductId()))
                            .mapToDouble(RateCommission::getEffectiveAnualRate)
                            .min()
                            .orElse(0);

                    double igv = (catalogService.getEntity(loanApplicationEvaluation.getEntityId()).getIgv()) / 100.0;
                    double min_amount = productMaxMinParameter != null ? productMaxMinParameter.getMinAmount() : 0;
                    int installments = productMaxMinParameter != null ? productMaxMinParameter.getMaxInstallments() : 0;
                    double nominal_annual_rate = (Math.pow((1 + (minEffectiveAnnualRate / 100.0)), (double) (1.0 / 12.0)) - 1) * 12.0;
                    //select (POWER((1 + min(effective_annual_rate / 100.0)), (1 / 12 :: NUMERIC)) - 1) * 12

                    Double maxInstallmentLoan = evaluationCacheService.getMaxInstallment(loanApplicationId, loanApplicationEvaluation.getEntityId(), loanApplicationEvaluation.getProductId(), loanApplicationEvaluation.getEntityProductParameterId(), cachedSources);
                    if(maxInstallmentLoan == null) return false;

                    boolean firstValidation = maxInstallmentLoan <
                            (min_amount * nominal_annual_rate * (1 + igv) / 12.0 *
                                    Math.pow((1 + nominal_annual_rate * (1 + igv) / 12.0), installments) /
                                    (Math.pow((1 + nominal_annual_rate * (1 + igv) / 12.0), installments) - 1));
                    if (firstValidation) {
                        return false;
                    }

                    boolean deuda_mayor = false;
                    if (loanApplicationEvaluation.getEntityId() != Entity.BANCO_DEL_SOL) {
                        double calc_deu = evaluationDAO.getTotalDebt(loanApplicationId) / evaluationCacheService.getAdmissionTotalIncome(loanApplicationId, loanApplicationEvaluation.getEntityId(), loanApplicationEvaluation.getProductId(), cachedSources);
                        deuda_mayor = calc_deu >= 5;
                    }

                    if (deuda_mayor) {
                        return false;
                    }

                    break;
                }
                case Policy.SCORE_BUREAU_ARG: {
                    Entity entity = catalogService.getEntity(loanApplicationEvaluation.getEntityId());
                    long filterEntityAnd4ExistsInBureauList = entity.getBureaus()
                            .stream()
                            .filter(b -> b.getId() == Bureau.NOSIS_BDS)
                            .count();

                    if (filterEntityAnd4ExistsInBureauList > 0) {
                        NosisRestResult nosisRestResult = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)
                                .stream()
                                .filter(a -> a.getBureauId() == Bureau.NOSIS_BDS && a.getNosisRestResult() != null)
                                .map(ApplicationBureau::getNosisRestResult).findFirst().orElse(null);
                        if (nosisRestResult != null) {
                            Integer nosisResultScore = nosisRestResult.getScore();
                            if (nosisResultScore == null)
                                nosisResultScore = 0;

                            if (nosisResultScore >= (Integer.parseInt(param1))) {
                                return true;
                            }

                            if (param2 != null && nosisResultScore >= Integer.parseInt(param2)) {
                                Integer sco3MTendencia = nosisRestResult.getSco3MTendencia();
                                Boolean cibancarizado = nosisRestResult.getCiBancarizado();
                                if (sco3MTendencia != null && sco3MTendencia == 1 && cibancarizado != null && cibancarizado)
                                    return true;
                            }
                        }
                        return false;
                    } else {
                        Integer score = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)
                                .stream()
                                .filter(a -> a.getScore() != null && a.getBureauId() == Bureau.NOSIS)
                                .map(ApplicationBureau::getScore)
                                .findFirst()
                                .orElse(null);

                        if ((score == null ? 0 : score) < Integer.parseInt(param1)) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.PROBLEMAS_EN_AFIP: {
                    AfipResult afipResult = evaluationCacheService.getAfipResultFromCache(loanApplication.getPersonId(), cachedSources);

                    if (afipResult == null || afipResult.getFullName() == null) {
                        return false;
                    }

                    break;
                }
                case Policy.CONTINUIDAD_LABORAL_BUREAU: {

                    List<ApplicationBureau> bureaus = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources);

                    if (bureaus.stream().anyMatch(b -> b.getBureauId() == Bureau.PYP)) {
                        boolean nosisBdsNoneYes = false;
                        boolean pypTipoActividadValidate = false;

                        for (ApplicationBureau bureau :
                                evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)) {

                            if (bureau.getBureauId() == Bureau.NOSIS_BDS && bureau.getEquifaxResult() != null) {
                                JSONObject equifaxResult = new JSONObject(bureau.getEquifaxResult());
                                if (equifaxResult.has("Contenido")
                                        && equifaxResult.getJSONObject("Contenido").has("Datos")
                                        && equifaxResult.getJSONObject("Contenido").getJSONObject("Datos").has("Variables")) {

                                    JSONArray resultado_nosis = equifaxResult.getJSONObject("Contenido").getJSONObject("Datos").getJSONArray("Variables");
                                    if (resultado_nosis != null) {
                                        nosisBdsNoneYes = StreamSupport
                                                .stream(resultado_nosis.spliterator(), false)
                                                .map(j -> (JSONObject) j)
                                                .filter(j ->
                                                        JsonUtil.getStringFromJson(j, "Nombre", null) != null
                                                                && Arrays.asList("VI_Jubilado_Es", "VI_Inscrip_Monotributo_Es", "VI_Inscrip_Autonomo_Es").contains(JsonUtil.getStringFromJson(j, "Nombre", null)))
                                                .map(j -> JsonUtil.getStringFromJson(j, "Valor", null))
                                                .noneMatch(v -> v != null && v.equalsIgnoreCase("si"));
                                    }
                                }
                            } else if (bureau.getBureauId() == Bureau.PYP && bureau.getEquifaxResult() != null) {
                                JSONObject equifaxResult = new JSONObject(bureau.getEquifaxResult());
                                if (equifaxResult.has("RESULTADO")
                                        && equifaxResult.getJSONObject("RESULTADO").has("Tipo_Actividad")
                                        && (equifaxResult.getJSONObject("RESULTADO").get("Tipo_Actividad") instanceof JSONArray ? equifaxResult.getJSONObject("RESULTADO").getJSONArray("Tipo_Actividad").getJSONObject(0) : equifaxResult.getJSONObject("RESULTADO").getJSONObject("Tipo_Actividad")).has("row")) {

                                    JSONObject pypResultado = new JSONObject(bureau.getEquifaxResult()).getJSONObject("RESULTADO");
                                    JSONArray pypRowJsonArray = (pypResultado.get("Tipo_Actividad") instanceof JSONArray ? pypResultado.getJSONArray("Tipo_Actividad").getJSONObject(0) : pypResultado.getJSONObject("Tipo_Actividad")).get("row") instanceof JSONArray ? (pypResultado.get("Tipo_Actividad") instanceof JSONArray ? pypResultado.getJSONArray("Tipo_Actividad").getJSONObject(0) : pypResultado.getJSONObject("Tipo_Actividad")).getJSONArray("row") : null;
                                    JSONObject pypRowJsonObject = (pypResultado.get("Tipo_Actividad") instanceof JSONArray ? pypResultado.getJSONArray("Tipo_Actividad").getJSONObject(0) : pypResultado.getJSONObject("Tipo_Actividad")).get("row") instanceof JSONObject ? (pypResultado.get("Tipo_Actividad") instanceof JSONArray ? pypResultado.getJSONArray("Tipo_Actividad").getJSONObject(0) : pypResultado.getJSONObject("Tipo_Actividad")).getJSONObject("row") : null;

                                    if (pypRowJsonArray != null) {
                                        pypTipoActividadValidate = StreamSupport
                                                .stream(pypRowJsonArray.spliterator(), false)
                                                .map(j -> (JSONObject) j)
                                                .filter(j -> JsonUtil.getStringFromJson(j, "tipo_actividad", null) != null)
                                                .anyMatch(j -> !j.getString("tipo_actividad").toLowerCase().contains("MONOTRIB" .toLowerCase()) &&
                                                        !j.getString("tipo_actividad").toLowerCase().contains("JUBILADO" .toLowerCase()) &&
                                                        !j.getString("tipo_actividad").toLowerCase().contains("AUTONOMO" .toLowerCase()));
                                    } else if (pypRowJsonObject != null) {
                                        pypTipoActividadValidate = JsonUtil.getStringFromJson(pypRowJsonObject, "tipo_actividad", null) != null &&
                                                !pypRowJsonObject.getString("tipo_actividad").toLowerCase().contains("MONOTRIB" .toLowerCase()) &&
                                                !pypRowJsonObject.getString("tipo_actividad").toLowerCase().contains("JUBILADO" .toLowerCase()) &&
                                                !pypRowJsonObject.getString("tipo_actividad").toLowerCase().contains("AUTONOMO" .toLowerCase());
                                    } else {
                                        throw new Exception("PYP json result RESULTADO:Tipo_actividad is " + pypResultado.get("Tipo_Actividad").getClass().getSimpleName());
                                    }
                                }
                            }
                        }

                        if (nosisBdsNoneYes && pypTipoActividadValidate) {
                            if (evaluationDAO.isEmploymentContinuityBds(loanApplicationId, Bureau.PYP)) {
                                return false;
                            }
                        }
                    } else {
                        Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                        ApplicationBureau nosisBureau = bureaus.stream().filter(b -> b.getBureauId() == Bureau.NOSIS).findFirst().orElse(null);
                        if (nosisBureau.getNosisResult().getParteXML() != null) {
                            if (nosisBureau.getNosisResult().getParteXML().getDatos() != null) {

                                boolean jubilado = nosisBureau.getNosisResult().getParteXML().getDatos()
                                        .stream()
                                        .anyMatch(d -> d.getJubilado() != null && d.getJubilado().equalsIgnoreCase("no") && d.getClave() != null && person.getDocumentNumber().equals(d.getClave().getDoc()));

                                Dato nosisDato = nosisBureau.getNosisResult().getParteXML().getDatos()
                                        .stream()
                                        .filter(d -> d.getApellido() != null)
                                        .findFirst()
                                        .orElse(null);
                                boolean authMonot = nosisDato != null && "no" .equalsIgnoreCase(nosisDato.getAutMonotrib());

                                if (jubilado && authMonot) {
                                    if (evaluationDAO.isEmploymentContinuityBds(loanApplicationId)) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }

                    break;
                }
                case Policy.SIN_ACTIVIDAD_BUREAU: {
                    List<ApplicationBureau> bureaus = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources);
                    if (bureaus.stream().anyMatch(b -> b.getBureauId() == Bureau.PYP)) {

                        String tipo_actividad = "";
                        String situacion_laboral_actual = "";
                        List<String> filteredValor = new ArrayList<>();

                        for (ApplicationBureau bureau : bureaus) {

                            if (bureau.getBureauId() == Bureau.NOSIS_BDS && bureau.getEquifaxResult() != null) {
                                JSONObject resultado_nosis_datos = new JSONObject(bureau.getEquifaxResult()).getJSONObject("Contenido").optJSONObject("Datos");
                                JSONArray resultado_nosis = resultado_nosis_datos != null ? resultado_nosis_datos.getJSONArray("Variables") : new JSONArray();
                                filteredValor = StreamSupport
                                        .stream(resultado_nosis.spliterator(), false)
                                        .map(j -> (JSONObject) j)
                                        .filter(j -> Arrays.asList("VI_Jubilado_Es", "VI_Empleado_Es", "VI_Inscrip_Monotributo_Es", "VI_Inscrip_Autonomo_Es").contains(j.getString("Nombre")))
                                        .map(j -> j.getString("Valor").toLowerCase())
                                        .collect(Collectors.toList());

                                //si alguno es Sí, pasa de frente, sino, pasa a evaluarse

                            } else if (bureau.getBureauId() == Bureau.PYP && bureau.getEquifaxResult() != null) {
                                JSONObject resultado = new JSONObject(bureau.getEquifaxResult()).getJSONObject("RESULTADO");
                                if (resultado != null) {
                                    JSONObject tipoActividadJSON = resultado.get("Tipo_Actividad") instanceof JSONArray ? resultado.getJSONArray("Tipo_Actividad").getJSONObject(0) : resultado.getJSONObject("Tipo_Actividad");
                                    if (tipoActividadJSON != null) {
                                        JSONArray rowJsonArray = JsonUtil.getJsonArrayFromJson(tipoActividadJSON, "row", null);// PARECE SER QUE EN ESTE CASO SI ES UN ARRAY
                                        if (rowJsonArray != null) {
                                            tipo_actividad = StreamSupport.stream(rowJsonArray.spliterator(), false).map(j -> (JSONObject) j).map(j -> j.getString("tipo_actividad")).findFirst().orElse("");
                                        }
                                    }

                                    JSONObject relacionDependenciaJson = JsonUtil.getJsonObjectFromJson(resultado, "RELACION_DEPENDENCIA", null);
                                    if (relacionDependenciaJson != null) {
                                        JSONArray rowJsonArray = relacionDependenciaJson.getJSONArray("row");
                                        if (rowJsonArray != null) {
                                            situacion_laboral_actual = StreamSupport.stream(rowJsonArray.spliterator(), false).map(j -> (JSONObject) j).map(j -> j.getString("situacion_laboral_actual")).findFirst().orElse("");
                                        }
                                    }
                                }
                            }
                        }


                        if (tipo_actividad == null || (!tipo_actividad.toLowerCase().contains("monotrib") && !tipo_actividad.toLowerCase().contains("jubilado") && !tipo_actividad.toLowerCase().contains("autonomo"))) {

                            if (situacion_laboral_actual == null || !situacion_laboral_actual.toLowerCase().contains("SITUACION: ACTIVO" .toLowerCase())) {

                                if (!filteredValor.contains("si")) {
                                    return false;
                                }
                            }
                        }
                    } else {
                        Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                        ApplicationBureau nosisBureau = bureaus.stream().filter(b -> b.getBureauId() == Bureau.NOSIS).findFirst().orElse(null);
                        if (nosisBureau.getNosisResult().getParteXML() != null) {
                            if (nosisBureau.getNosisResult().getParteXML().getDatos() != null) {

                                boolean jubilado = nosisBureau.getNosisResult().getParteXML().getDatos()
                                        .stream()
                                        .anyMatch(d -> d.getJubilado() != null && d.getJubilado().equalsIgnoreCase("no") && d.getClave() != null && person.getDocumentNumber().equals(d.getClave().getDoc()));

                                boolean sinActividad = nosisBureau.getNosisResult().getParteXML().getDatos()
                                        .stream()
                                        .anyMatch(d -> d.getSinActividad() != null && d.getSinActividad().equalsIgnoreCase("si") && d.getClave() != null && person.getDocumentNumber().equals(d.getClave().getDoc()));

                                if (jubilado && sinActividad)
                                    return false;
                            }
                        }
                    }

                    break;
                }
                case Policy.TIEMPO_EN_EL_EMPLEO_BUREAU: {

                    List<ApplicationBureau> bureaus = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources);

                    if (bureaus.stream().anyMatch(b -> b.getBureauId() == Bureau.PYP)) {
                        boolean nosisBdsNoneYes = false;
                        boolean pypTipoActividadValidate = false;

                        for (ApplicationBureau bureau :
                                evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)) {

                            if (bureau.getBureauId() == Bureau.NOSIS_BDS && bureau.getEquifaxResult() != null) {
                                JSONObject equifaxResult = new JSONObject(bureau.getEquifaxResult());
                                if (equifaxResult.has("Contenido")
                                        && equifaxResult.getJSONObject("Contenido").has("Datos")
                                        && equifaxResult.getJSONObject("Contenido").getJSONObject("Datos").has("Variables")) {

                                    JSONArray resultado_nosis = equifaxResult.getJSONObject("Contenido").getJSONObject("Datos").getJSONArray("Variables");
                                    if (resultado_nosis != null) {
                                        nosisBdsNoneYes = StreamSupport
                                                .stream(resultado_nosis.spliterator(), false)
                                                .map(j -> (JSONObject) j)
                                                .filter(j ->
                                                        JsonUtil.getStringFromJson(j, "Nombre", null) != null
                                                                && Arrays.asList("VI_Jubilado_Es", "VI_Inscrip_Monotributo_Es", "VI_Inscrip_Autonomo_Es").contains(JsonUtil.getStringFromJson(j, "Nombre", null)))
                                                .map(j -> JsonUtil.getStringFromJson(j, "Valor", null))
                                                .noneMatch(v -> v != null && v.equalsIgnoreCase("si"));
                                    }
                                }
                            } else if (bureau.getBureauId() == Bureau.PYP && bureau.getEquifaxResult() != null) {
                                JSONObject equifaxResult = new JSONObject(bureau.getEquifaxResult());
                                if (equifaxResult.has("RESULTADO")
                                        && equifaxResult.getJSONObject("RESULTADO").has("Tipo_Actividad")
                                        && (equifaxResult.getJSONObject("RESULTADO").get("Tipo_Actividad") instanceof JSONArray ? equifaxResult.getJSONObject("RESULTADO").getJSONArray("Tipo_Actividad").getJSONObject(0) : equifaxResult.getJSONObject("RESULTADO").getJSONObject("Tipo_Actividad")).has("row")) {

                                    JSONObject pypResultado = new JSONObject(bureau.getEquifaxResult()).getJSONObject("RESULTADO");
                                    JSONArray pypRowJsonArray = (pypResultado.get("Tipo_Actividad") instanceof JSONArray ? pypResultado.getJSONArray("Tipo_Actividad").getJSONObject(0) : pypResultado.getJSONObject("Tipo_Actividad")).get("row") instanceof JSONArray ? (pypResultado.get("Tipo_Actividad") instanceof JSONArray ? pypResultado.getJSONArray("Tipo_Actividad").getJSONObject(0) : pypResultado.getJSONObject("Tipo_Actividad")).getJSONArray("row") : null;
                                    JSONObject pypRowJsonObject = (pypResultado.get("Tipo_Actividad") instanceof JSONArray ? pypResultado.getJSONArray("Tipo_Actividad").getJSONObject(0) : pypResultado.getJSONObject("Tipo_Actividad")).get("row") instanceof JSONObject ? (pypResultado.get("Tipo_Actividad") instanceof JSONArray ? pypResultado.getJSONArray("Tipo_Actividad").getJSONObject(0) : pypResultado.getJSONObject("Tipo_Actividad")).getJSONObject("row") : null;

                                    if (pypRowJsonArray != null) {
                                        pypTipoActividadValidate = StreamSupport
                                                .stream(pypRowJsonArray.spliterator(), false)
                                                .map(j -> (JSONObject) j)
                                                .filter(j -> JsonUtil.getStringFromJson(j, "tipo_actividad", null) != null)
                                                .anyMatch(j -> !j.getString("tipo_actividad").toLowerCase().contains("MONOTRIB" .toLowerCase()) &&
                                                        !j.getString("tipo_actividad").toLowerCase().contains("JUBILADO" .toLowerCase()) &&
                                                        !j.getString("tipo_actividad").toLowerCase().contains("AUTONOMO" .toLowerCase()));
                                    } else if (pypRowJsonObject != null) {
                                        pypTipoActividadValidate = JsonUtil.getStringFromJson(pypRowJsonObject, "tipo_actividad", null) != null &&
                                                !pypRowJsonObject.getString("tipo_actividad").toLowerCase().contains("MONOTRIB" .toLowerCase()) &&
                                                !pypRowJsonObject.getString("tipo_actividad").toLowerCase().contains("JUBILADO" .toLowerCase()) &&
                                                !pypRowJsonObject.getString("tipo_actividad").toLowerCase().contains("AUTONOMO" .toLowerCase());
                                    } else {
                                        throw new Exception("PYP json result RESULTADO:Tipo_actividad is " + pypResultado.get("Tipo_Actividad").getClass().getSimpleName());
                                    }
                                }
                            }
                        }

                        if (nosisBdsNoneYes && pypTipoActividadValidate) {
                            if (evaluationDAO.isEmploymentTimeBds(loanApplicationId, Bureau.PYP)) {
                                return false;
                            }
                        }
                    } else {
                        Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                        ApplicationBureau nosisBureau = bureaus.stream().filter(b -> b.getBureauId() == Bureau.NOSIS).findFirst().orElse(null);
                        if (nosisBureau.getNosisResult().getParteXML() != null) {
                            if (nosisBureau.getNosisResult().getParteXML().getDatos() != null) {

                                boolean jubilado = nosisBureau.getNosisResult().getParteXML().getDatos()
                                        .stream()
                                        .anyMatch(d -> d.getJubilado() != null && d.getJubilado().equalsIgnoreCase("no") && d.getClave() != null && person.getDocumentNumber().equals(d.getClave().getDoc()));

                                Dato nosisDato = nosisBureau.getNosisResult().getParteXML().getDatos()
                                        .stream()
                                        .filter(d -> d.getApellido() != null)
                                        .findFirst()
                                        .orElse(null);
                                boolean authMonot = nosisDato != null && "no" .equalsIgnoreCase(nosisDato.getAutMonotrib());

                                if (jubilado && authMonot) {
                                    if (evaluationDAO.isEmploymentTimeBds(loanApplicationId)) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }

                    break;
                }
                case Policy.ASIGNACION_UNIVERSAL_POR_HIJO: {
                    ApplicationBureau applicationBureau = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)
                            .stream()
                            .filter(a -> a.getBureauId() == Bureau.PYP)
                            .findFirst().orElse(null);
                    int cantAuhCobra = 0;
                    JSONObject bureauResult = applicationBureau.getEquifaxResult() != null ? new JSONObject(applicationBureau.getEquifaxResult()).getJSONObject("RESULTADO") : null;
                    if (bureauResult != null && JsonUtil.getJsonObjectFromJson(bureauResult, "Cobra_AUH", null) != null) {
                        JSONObject jsonCobraAuh = JsonUtil.getJsonObjectFromJson(bureauResult, "Cobra_AUH", null);
                        if (jsonCobraAuh != null && JsonUtil.getJsonObjectFromJson(jsonCobraAuh, "row", null) != null) {
                            JSONObject jsonCobraAuhRow = JsonUtil.getJsonObjectFromJson(jsonCobraAuh, "row", null);
                            if (jsonCobraAuhRow != null && JsonUtil.getIntFromJson(jsonCobraAuhRow, "cant_auh_cobra", null) != null) {
                                cantAuhCobra = JsonUtil.getIntFromJson(jsonCobraAuhRow, "cant_auh_cobra", null);
                            }
                        }
                    }

                    if (cantAuhCobra > 0) {
                        return false;
                    }

                    break;
                }
                case Policy.FALLECIDOS: {
                    JSONArray variables = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)
                            .stream()
                            .filter(a -> a.getEquifaxResult() != null && a.getBureauId().equals(Bureau.NOSIS_BDS))
                            .map(ApplicationBureau::getEquifaxResult)
                            .map(JSONObject::new)
                            .map(j -> j.getJSONObject("Contenido").optJSONObject("Datos"))
                            .filter(Objects::nonNull)
                            .map(j -> j.getJSONArray("Variables"))
                            .findFirst()
                            .orElse(null);

                    String fallecidoEsValor = variables == null ? null : StreamSupport.stream(variables.spliterator(), false)
//                            .map(o -> new JSONObject(o.toString()))
                            .map(j -> ((JSONObject) j))
                            .filter(j -> j.getString("Nombre").equalsIgnoreCase("VI_Fallecido_Es"))
                            .map(j -> j.getString("Valor"))
                            .findFirst()
                            .orElse(null);

                    String fallecido = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)
                            .stream()
                            .filter(a -> a.getEquifaxResult() != null && a.getBureauId() == Bureau.PYP)
                            .map(ApplicationBureau::getEquifaxResult)
                            .map(JSONObject::new)
                            .map(j -> j.getJSONObject("RESULTADO").getJSONObject("Existencia_Fisica_Resu").getJSONObject("row").getString("fallecido"))
                            .findFirst()
                            .orElse(null);

                    if ("Si" .equalsIgnoreCase(fallecidoEsValor) || (fallecido != null && !"No" .equalsIgnoreCase(fallecido))) {
                        return false;
                    }

                    break;
                }
                case Policy.SIN_ACTIVIDAD_JUBILADO: {
                    List<Integer> param1Parsed = param1 == null ? Collections.emptyList() : getListParams(param1).stream().map(Integer::parseInt).collect(Collectors.toList());
                    List<ApplicationBureau> bureaus = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources);

                    if (bureaus.stream().anyMatch(b -> b.getBureauId() == Bureau.PYP)) {
                        Set<String> actividades = new HashSet<>();
                        Integer empleadorActividad01Cod = -1;
                        String clientType = loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_CLIENT_TYPE.getKey());

                        for (ApplicationBureau bureau :
                                evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)) {

                            if (bureau.getBureauId() == Bureau.NOSIS_BDS && bureau.getEquifaxResult() != null) {
                                JSONObject equifaxResult = new JSONObject(bureau.getEquifaxResult());
                                if (equifaxResult.has("Contenido")
                                        && equifaxResult.getJSONObject("Contenido").has("Datos")
                                        && equifaxResult.getJSONObject("Contenido").getJSONObject("Datos").has("Variables")) {

                                    JSONArray resultado_nosis = equifaxResult.getJSONObject("Contenido").getJSONObject("Datos").getJSONArray("Variables");
                                    if (resultado_nosis != null) {
                                        List<String> actividadesNosis = StreamSupport
                                                .stream(resultado_nosis.spliterator(), false)
                                                .map(j -> (JSONObject) j)
                                                .filter(j -> JsonUtil.getStringFromJson(j, "Nombre", null) != null && JsonUtil.getStringFromJson(j, "Valor", null) != null)
                                                .filter(j -> Arrays.asList("VI_Jubilado_Es", "VI_Empleado_Es", "VI_Inscrip_Monotributo_Es", "VI_Inscrip_Autonomo_Es").contains(JsonUtil.getStringFromJson(j, "Nombre", null)))
                                                .map(j -> "VI_Jubilado_Es" .equals(JsonUtil.getStringFromJson(j, "Nombre", null)) && "si" .equalsIgnoreCase(j.optString("Valor", null)) ? BDS_JUBILADO :
                                                        "VI_Empleado_Es" .equals(JsonUtil.getStringFromJson(j, "Nombre", null)) && "si" .equalsIgnoreCase(j.optString("Valor", null)) ? BDS_RELACION_DEPENDENCIA :
                                                                "VI_Inscrip_Monotributo_Es" .equals(JsonUtil.getStringFromJson(j, "Nombre", null)) && "si" .equalsIgnoreCase(j.optString("Valor", null)) ? BDS_MONOTRIBUTISTA :
                                                                        "VI_Inscrip_Autonomo_Es" .equals(JsonUtil.getStringFromJson(j, "Nombre", null)) && "si" .equalsIgnoreCase(j.optString("Valor", null)) ? BDS_AUTONOMO : null)
                                                .collect(Collectors.toList());

                                        empleadorActividad01Cod = StreamSupport
                                                .stream(resultado_nosis.spliterator(), false)
                                                .map(j -> (JSONObject) j)
                                                .filter(j -> JsonUtil.getStringFromJson(j, "Nombre", null) != null && JsonUtil.getStringFromJson(j, "Valor", null) != null)
                                                .filter(j -> Arrays.asList("VI_Act01_Cod").contains(JsonUtil.getStringFromJson(j, "Nombre", null)))
                                                .map(j -> j.optString("Valor", null))
                                                .map(s -> "" .equals(s) ? "-1" : s)
                                                .mapToInt(Integer::parseInt)
                                                .findFirst()
                                                .orElse(-1);

                                        actividades.addAll(actividadesNosis);
                                    }
                                }
                            } else if (bureau.getBureauId() == Bureau.PYP && bureau.getEquifaxResult() != null) {
                                JSONObject equifaxResult = new JSONObject(bureau.getEquifaxResult());
                                if (equifaxResult.has("RESULTADO")
                                        && equifaxResult.getJSONObject("RESULTADO").has("Tipo_Actividad")
                                        && (equifaxResult.getJSONObject("RESULTADO").get("Tipo_Actividad") instanceof JSONArray ? equifaxResult.getJSONObject("RESULTADO").getJSONArray("Tipo_Actividad").getJSONObject(0) : equifaxResult.getJSONObject("RESULTADO").getJSONObject("Tipo_Actividad")).has("row")) {

                                    JSONObject pypResultado = new JSONObject(bureau.getEquifaxResult()).getJSONObject("RESULTADO");
                                    JSONArray pypRowJsonArray = (pypResultado.get("Tipo_Actividad") instanceof JSONArray ? pypResultado.getJSONArray("Tipo_Actividad").getJSONObject(0) : pypResultado.getJSONObject("Tipo_Actividad")).get("row") instanceof JSONArray ? (pypResultado.get("Tipo_Actividad") instanceof JSONArray ? pypResultado.getJSONArray("Tipo_Actividad").getJSONObject(0) : pypResultado.getJSONObject("Tipo_Actividad")).getJSONArray("row") : null;
                                    JSONObject pypRowJsonObject = (pypResultado.get("Tipo_Actividad") instanceof JSONArray ? pypResultado.getJSONArray("Tipo_Actividad").getJSONObject(0) : pypResultado.getJSONObject("Tipo_Actividad")).get("row") instanceof JSONObject ? (pypResultado.get("Tipo_Actividad") instanceof JSONArray ? pypResultado.getJSONArray("Tipo_Actividad").getJSONObject(0) : pypResultado.getJSONObject("Tipo_Actividad")).getJSONObject("row") : null;

                                    if (pypRowJsonArray != null) {
                                        String tipoActividad = StreamSupport.stream(pypRowJsonArray.spliterator(), false).map(j -> (JSONObject) j).map(j -> j.getString("tipo_actividad")).map(String::toLowerCase).findFirst().orElse("");

                                        if (tipoActividad.indexOf("dependen") > -1) {
                                            actividades.add(BDS_RELACION_DEPENDENCIA);
                                        }
                                        if (tipoActividad.indexOf("monotrib") > -1) {
                                            actividades.add(BDS_MONOTRIBUTISTA);
                                        }
                                        if (tipoActividad.indexOf("jubilado") > -1) {
                                            actividades.add(BDS_JUBILADO);
                                        }
                                        if (tipoActividad.indexOf("autonomo") > -1) {
                                            actividades.add(BDS_AUTONOMO);
                                        }
                                    } else if (pypRowJsonObject != null) {
                                        String tipoActividad = pypRowJsonObject.getString("tipo_actividad").toLowerCase();

                                        if (tipoActividad.indexOf("dependen") > -1) {
                                            actividades.add(BDS_RELACION_DEPENDENCIA);
                                        }
                                        if (tipoActividad.indexOf("monotrib") > -1) {
                                            actividades.add(BDS_MONOTRIBUTISTA);
                                        }
                                        if (tipoActividad.indexOf("jubilado") > -1) {
                                            actividades.add(BDS_JUBILADO);
                                        }
                                        if (tipoActividad.indexOf("autonomo") > -1) {
                                            actividades.add(BDS_AUTONOMO);
                                        }
                                    } else {
                                        throw new Exception("PYP json result RESULTADO:Tipo_actividad is " + pypResultado.get("Tipo_Actividad").getClass().getSimpleName());
                                    }
                                }
                            }
                        }

                        actividades.remove(null);
//                        actividad NO autonomo y/o monotributista
                        if (!(
                                actividades.size() == 1 && (actividades.contains(BDS_AUTONOMO) || actividades.contains(BDS_MONOTRIBUTISTA))
                                        || actividades.size() == 2 && (actividades.contains(BDS_AUTONOMO) && actividades.contains(BDS_MONOTRIBUTISTA))
                        )) {
                            return true;
                        } else {
                            return !BancoDelSolServiceImpl.CLIENT_TYPES.get(1).equalsIgnoreCase(clientType) && !param1Parsed.contains(empleadorActividad01Cod);
                        }
                    } else {
                        Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                        ApplicationBureau nosisBureau = bureaus.stream().filter(b -> b.getBureauId() == Bureau.NOSIS).findFirst().orElse(null);

                        if (nosisBureau != null && nosisBureau.getNosisResult().getParteXML() != null) {
                            if (nosisBureau.getNosisResult().getParteXML().getDatos() != null) {

                                boolean jubilado = nosisBureau.getNosisResult().getParteXML().getDatos()
                                        .stream()
                                        .anyMatch(d -> d.getJubilado() != null && d.getJubilado().equalsIgnoreCase("si") && d.getClave() != null && person.getDocumentNumber().equals(d.getClave().getDoc()));

                                if (jubilado) {
                                    return true;
                                }

                                Dato nosisDato = nosisBureau.getNosisResult().getParteXML().getDatos()
                                        .stream()
                                        .filter(d -> d.getApellido() != null)
                                        .findFirst()
                                        .orElse(null);
                                boolean authMonot = nosisDato != null && "si" .equalsIgnoreCase(nosisDato.getAutMonotrib());

                                if (authMonot) {
                                    return false;
                                }
                            }
                        }
                    }

                    break;
                }
                case Policy.ACTIVIDAD_DEPENDENCIA__ACTIVIDAD_EMPLEADOR_EXCLUIDO: {
                    List<Integer> param1Parsed = param1 == null ? Collections.emptyList() : getListParams(param1).stream().map(Integer::parseInt).collect(Collectors.toList());
                    List<ApplicationBureau> bureaus = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources);

                    if (bureaus.stream().anyMatch(b -> b.getBureauId() == Bureau.PYP)) {
                        Set<String> actividades = new HashSet<>();
                        Integer empleadorActividad01Cod = -1;

                        for (ApplicationBureau bureau : bureaus) {

                            if (bureau.getBureauId() == Bureau.NOSIS_BDS && bureau.getEquifaxResult() != null) {
                                JSONArray resultado_nosis = new JSONObject(bureau.getEquifaxResult()).getJSONObject("Contenido").getJSONObject("Datos").getJSONArray("Variables");
                                List<String> actividadesNosis = StreamSupport
                                        .stream(resultado_nosis.spliterator(), false)
                                        .map(j -> (JSONObject) j)
                                        .filter(j -> JsonUtil.getStringFromJson(j, "Nombre", null) != null && JsonUtil.getStringFromJson(j, "Valor", null) != null)
                                        .filter(j -> Arrays.asList("VI_Jubilado_Es", "VI_Empleado_Es", "VI_Inscrip_Monotributo_Es", "VI_Inscrip_Autonomo_Es").contains(JsonUtil.getStringFromJson(j, "Nombre", null)))
                                        .map(j -> "VI_Jubilado_Es" .equals(JsonUtil.getStringFromJson(j, "Nombre", null)) && "si" .equalsIgnoreCase(j.optString("Valor", null)) ? BDS_JUBILADO :
                                                "VI_Empleado_Es" .equals(JsonUtil.getStringFromJson(j, "Nombre", null)) && "si" .equalsIgnoreCase(j.optString("Valor", null)) ? BDS_RELACION_DEPENDENCIA :
                                                        "VI_Inscrip_Monotributo_Es" .equals(JsonUtil.getStringFromJson(j, "Nombre", null)) && "si" .equalsIgnoreCase(j.optString("Valor", null)) ? BDS_MONOTRIBUTISTA :
                                                                "VI_Inscrip_Autonomo_Es" .equals(JsonUtil.getStringFromJson(j, "Nombre", null)) && "si" .equalsIgnoreCase(j.optString("Valor", null)) ? BDS_AUTONOMO : null)
                                        .collect(Collectors.toList());
                                ;

                                empleadorActividad01Cod = StreamSupport
                                        .stream(resultado_nosis.spliterator(), false)
                                        .map(j -> (JSONObject) j)
                                        .filter(j -> JsonUtil.getStringFromJson(j, "Nombre", null) != null && JsonUtil.getStringFromJson(j, "Valor", null) != null)
                                        .filter(j -> Arrays.asList("VI_Empleador_Act01_Cod").contains(JsonUtil.getStringFromJson(j, "Nombre", null)))
                                        .map(j -> j.optString("Valor", null))
                                        .map(s -> "" .equals(s) ? "-1" : s)
                                        .mapToInt(Integer::parseInt)
                                        .findFirst()
                                        .orElse(-1)
                                ;

                                actividades.addAll(actividadesNosis);

                            } else if (bureau.getBureauId() == Bureau.PYP && bureau.getEquifaxResult() != null) {
                                JSONObject resultado = new JSONObject(bureau.getEquifaxResult()).getJSONObject("RESULTADO");
                                if (resultado != null) {
                                    JSONObject tipoActividadJSON = resultado.get("Tipo_Actividad") instanceof JSONArray ? resultado.getJSONArray("Tipo_Actividad").getJSONObject(0) : resultado.getJSONObject("Tipo_Actividad");
                                    if (tipoActividadJSON != null) {
                                        JSONArray rowJsonArray = JsonUtil.getJsonArrayFromJson(tipoActividadJSON, "row", null);// PARECE SER QUE EN ESTE CASO SI ES UN ARRAY
                                        if (rowJsonArray != null) {
                                            String tipoActividad = StreamSupport.stream(rowJsonArray.spliterator(), false).map(j -> (JSONObject) j).map(j -> j.getString("tipo_actividad")).map(String::toLowerCase).findFirst().orElse("");

                                            if (tipoActividad.indexOf("dependen") > -1) {
                                                actividades.add(BDS_RELACION_DEPENDENCIA);
                                            }
                                            if (tipoActividad.indexOf("monotrib") > -1) {
                                                actividades.add(BDS_MONOTRIBUTISTA);
                                            }
                                            if (tipoActividad.indexOf("jubilado") > -1) {
                                                actividades.add(BDS_JUBILADO);
                                            }
                                            if (tipoActividad.indexOf("autonomo") > -1) {
                                                actividades.add(BDS_AUTONOMO);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        actividades.remove(null);
                        if (actividades.contains(BDS_RELACION_DEPENDENCIA)) {
                            if (actividades.size() == 2 && actividades.contains(BDS_JUBILADO)) {
                                return true;
                            }

                            if (param1Parsed.contains(empleadorActividad01Cod)) {
                                if (actividades.contains(BDS_AUTONOMO) || actividades.contains(BDS_MONOTRIBUTISTA)) {
                                    if (actividades.contains(BDS_JUBILADO)) {
                                        return false;
                                    }

                                    return false;
                                }

                                return false;
                            }

                            if (!param1Parsed.contains(empleadorActividad01Cod)) {
                                if (actividades.contains(BDS_AUTONOMO) || actividades.contains(BDS_MONOTRIBUTISTA)) {
                                    if (actividades.contains(BDS_JUBILADO)) {
                                        return true;
                                    }

                                    return true;
                                }
                            }

                            return true;
                        } else {
                            return true;
                        }
                    } else {
                        Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                        ApplicationBureau nosisBureau = bureaus.stream().filter(b -> b.getBureauId() == Bureau.NOSIS).findFirst().orElse(null);

                        if (nosisBureau != null && nosisBureau.getNosisResult().getParteXML() != null) {
                            if (nosisBureau.getNosisResult().getParteXML().getDatos() != null) {

                                boolean relacionDependencia = nosisBureau.getNosisResult().getParteXML().getDatos()
                                        .stream()
                                        .anyMatch(d -> d.getRelDependencia() != null && d.getRelDependencia().equalsIgnoreCase("si") && d.getClave() != null && person.getDocumentNumber().equals(d.getClave().getDoc()));

                                boolean jubilado = nosisBureau.getNosisResult().getParteXML().getDatos()
                                        .stream()
                                        .anyMatch(d -> d.getJubilado() != null && d.getJubilado().equalsIgnoreCase("si") && d.getClave() != null && person.getDocumentNumber().equals(d.getClave().getDoc()));

                                boolean autMonot = nosisBureau.getNosisResult().getParteXML().getDatos()
                                        .stream()
                                        .anyMatch(d -> d.getAutMonotrib() != null && d.getAutMonotrib().equalsIgnoreCase("si") && d.getClave() != null && person.getDocumentNumber().equals(d.getClave().getDoc()));

                                if (relacionDependencia) {
                                    if (jubilado) {
                                        return true;
                                    }

                                    if (autMonot) {
                                        return true;
                                    }
                                } else {
                                    return false;
                                }
                            }
                        }
                    }

                    break;
                }
                case Policy.AGREEMENT_NOT_ALLOWED: {
                    PersonAssociated personAssociated = personDAO.getAssociated(loanApplication.getPersonId(), loanApplicationEvaluation.getEntityId());
                    boolean isAssociate_2 = personAssociated != null && personAssociated.getAssociatedId() != null && Integer.parseInt(personAssociated.getAssociatedId()) == -2;

                    if (isAssociate_2) {
                        return false;
                    }

                    break;
                }
                case Policy.EXPENSIVE_ABACOS_DEBT: {
                    LoanApplicationReclosure loanApplicationReclosure = evaluationCacheService.getLoanApplicationReclosure(loanApplicationId, cachedSources);
                    if (loanApplicationReclosure != null && loanApplicationReclosure.getPendingCreditAmount() != null) {
                        Employee employee = evaluationCacheService.getEmployeeFromCache(loanApplication.getPersonId(), employerId, cachedSources);
                        double maxAmount = employee == null ? 0.0 : 2 * ((employee.getFixedGrossIncome() == null ? 0.0 : employee.getFixedGrossIncome()) + ((employee.getVariableGrossIncome() == null ? 0.0 : employee.getVariableGrossIncome()) * 0.5));

                        if (maxAmount <= loanApplicationReclosure.getPendingCreditAmount() + 25) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.EFRU24MDP: {
                    if (evaluationCacheService.getEquifaxDeudasHistoricasPeriodoSorted(loanApplicationId, cachedSources)
                            .stream()
                            .limit(24)
                            .anyMatch(e -> "DUD" .equalsIgnoreCase(e.getCalificacion()) || "PER" .equalsIgnoreCase(e.getCalificacion())))
                        return false;
                    break;
                }
                case Policy.EFRU712DEF: {
                    if (evaluationCacheService.getEquifaxDeudasHistoricasPeriodoSorted(loanApplicationId, cachedSources)
                            .stream()
                            .skip(Integer.parseInt(param1))
                            .limit(Integer.parseInt(param2))
                            .anyMatch(e -> Arrays.asList("DEF", "DUD", "PER").contains(e.getCalificacion().toUpperCase())))
                        return false;
                    break;
                }
                case Policy.EFRU3456MCPP: {
                    if (evaluationCacheService.getEquifaxDeudasHistoricasPeriodoSorted(loanApplicationId, cachedSources)
                            .stream()
                            .skip(Integer.parseInt(param1))
                            .limit(Integer.parseInt(param2))
                            .anyMatch(e -> Arrays.asList("CPP", "DEF", "DUD", "PER").contains(e.getCalificacion().toUpperCase())))
                        return false;
                    break;
                }
                case Policy.EFRU2MN: {
                    List<EquifaxDeudasHistoricas> takeEfxDeudasHistoricasLimitX = evaluationCacheService.getEquifaxDeudasHistoricasPeriodoSorted(loanApplicationId, cachedSources)
                            .stream()
                            .limit(Integer.parseInt(param1))
                            .collect(Collectors.toList());
                    boolean existsFilterCalificacionNotNOR = takeEfxDeudasHistoricasLimitX
                            .stream()
                            .anyMatch(e -> !"NOR" .equalsIgnoreCase(e.getCalificacion()));

                    if (existsFilterCalificacionNotNOR) {
                        return false;
                    }

                    break;
                }
                case Policy.EFMU2MN: {
                    List<EquifaxDeudasHistoricas> equifaxDeudasHistoricasList;
                    if (param1 == null) {
                        equifaxDeudasHistoricasList = evaluationCacheService.getEquifaxDeudasHistoricasPeriodoSorted(loanApplicationId, cachedSources)
                                .stream()
                                .collect(Collectors.toList());
                    } else {
                        equifaxDeudasHistoricasList = evaluationCacheService.getEquifaxDeudasHistoricasPeriodoSorted(loanApplicationId, cachedSources)
                                .stream()
                                .limit(Integer.parseInt(param1))
                                .collect(Collectors.toList());
                    }

                    List<String> param2Parsed = param2 == null ? Arrays.asList("NOR") : Arrays.asList(param2.replaceAll("\\{", "").replaceAll("\\}", "").split(","));

                    boolean calificacionNotInArray = equifaxDeudasHistoricasList
                            .stream()
                            .anyMatch(e -> !param2Parsed.contains(e.getCalificacion()));
                    if (calificacionNotInArray) {
                        return false;
                    }

                    break;
                }
                case Policy.SICOM: {
                    List<EquifaxSicomCabecera> equifaxSicomCabeceras = evaluationDAO.getEquifaxSicomCabecera(loanApplicationId);
                    double montoSoles = equifaxSicomCabeceras.stream().mapToDouble(e -> e.getMontoSoles() != null ? e.getMontoSoles() : 0).sum();
                    double montoDolares = equifaxSicomCabeceras.stream().mapToDouble(e -> e.getMontoDolares() != null ? e.getMontoDolares() : 0).sum();
                    double sumMontoSolesPlusSumMontoDolares = montoSoles + montoDolares;

                    if (param1 != null && sumMontoSolesPlusSumMontoDolares > Double.parseDouble(param1)) {
                        return false;
                    }
                    break;
                }
                case Policy.IC: {
                    EquifaxIndicadoresConsultaU2M equifaxIndicadoresConsultaU2M = evaluationDAO.getConsultasFromEquifaxIndicadoresConsultaU2MByLoanApplicationId(loanApplicationId);
                    int consultas = (equifaxIndicadoresConsultaU2M != null && equifaxIndicadoresConsultaU2M.getConsultas() != null ? equifaxIndicadoresConsultaU2M.getConsultas() : 0);

                    if (consultas > 2) {
                        return false;
                    }

                    break;
                }
                case Policy.SCORE_BUREAU: {
                    List<Cluster> clusterList = catalogService.getClusters(Configuration.getDefaultLocale());
                    Integer clusterId = null;

                    if (loanApplicationEvaluation.getEntityId() == Entity.CAJASULLANA) {
                        JSONObject entityWsResultJson = evaluationCacheService.getCajaSullanaExperianWSResult(loanApplicationId, cachedSources).getResult();
                        Integer score = JsonUtil.getDoubleFromJson(entityWsResultJson, "score", 0.0).intValue();

                        clusterId = clusterList
                                .stream()
                                .filter(c -> c.getEntityId().equals(Entity.CAJASULLANA) && c.getMinScore() <= score)
                                .mapToInt(Cluster::getId)
                                .min()
                                .orElse(0);
//                        COULD BE NULL

                        if (!JsonUtil.getStringFromJson(entityWsResultJson, "propuestaFinal", "").equalsIgnoreCase("automatico")) {
                            clusterId = 1409;
                        }
                    }

                    if (loanApplicationEvaluation.getEntityId() == Entity.COMPARTAMOS) {
                        JSONObject entityWsResultJson = evaluationCacheService.getCompartamosVariablesEvalWSResult(loanApplicationId, cachedSources).getResult();

                        Integer pnPunExp = entityWsResultJson.getJSONObject("poCliente").getInt("pnPunExp");

                        clusterId = clusterList
                                .stream()
                                .filter(c -> c.getEntityId() != null && c.getEntityId().equals(Entity.COMPARTAMOS) && c.getMinScore() <= pnPunExp)
                                .mapToInt(Cluster::getId)
                                .min()
                                .orElse(0);
//                        COULD BE NULL
                    }

                    if (loanApplication.getEntityId() == Entity.FUNDACION_DE_LA_MUJER) {
                        EntityWsResult entityWsResult = evaluationCacheService.getFdlmDataCreditoDatosClienteWSResult(loanApplicationId, cachedSources);

                        if (entityWsResult == null) {
                            return false;
                        }

                        JSONObject entityWsResultJson = entityWsResult.getResult();
                        DatosClienteResponse response = new Gson().fromJson(entityWsResultJson.toString(), DatosClienteResponse.class);
                        JSONObject result = new JSONObject(response.getResult().getDatosResultado());
                        int param1Parsed = Integer.parseInt(param1);

                        if (!result.has("Informe") || !result.getJSONObject("Informe").has("Score")) {
                            return false;
                        }

                        JSONArray scores = result.getJSONObject("Informe").optJSONArray("Score") != null ? result.getJSONObject("Informe").getJSONArray("Score") : new JSONArray().put(result.getJSONObject("Informe").getJSONObject("Score"));

                        return StreamSupport
                                .stream(scores.spliterator(), false)
                                .map(j -> (JSONObject) j)
                                .filter(j -> j.getInt("tipo") == 67)
                                .mapToInt(j -> j.getInt("puntaje"))
                                .findFirst()
                                .getAsInt() > param1Parsed
                                ;
                    }

                    if (loanApplicationEvaluation.getEntityId() == Entity.CAJASULLANA && (clusterId == null || clusterId == 0 ? 1409 : clusterId) == 1409) {
                        return false;
                    } else if (loanApplicationEvaluation.getEntityId() == Entity.COMPARTAMOS && Arrays.asList(1304, 1306, 1307).contains(clusterId == null || clusterId == 0 ? 1304 : clusterId)) {
                        return false;
                    } else if (clusterId != null && clusterId >= 14) {
                        return false;
                    }

                    break;
                }
                case Policy.ACTIVE_ENTITIES: {
                    List<EquifaxDeudasHistoricas> equifaxDeudasHistoricas = evaluationCacheService.getEquifaxDeudasHistoricasPeriodoSorted(loanApplicationId, cachedSources);
                    EquifaxDeudasHistoricas equifaxDeudasHistorica = equifaxDeudasHistoricas.isEmpty() ? null : equifaxDeudasHistoricas.get(0);
                    int nroEntidades = equifaxDeudasHistorica != null && equifaxDeudasHistorica.getNroEntidades() != null ? equifaxDeudasHistorica.getNroEntidades() : 0;

                    if (nroEntidades > Integer.parseInt(param1)) {
                        return false;
                    }

                    break;
                }
                case Policy.EFX_SCORE: {
                    ApplicationBureau applicationBureau = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)
                            .stream()
                            .findFirst()
                            .orElse(null);
                    Integer score = applicationBureau != null ? applicationBureau.getScore() : null;

                    if (score == null || score < Integer.parseInt(param1)) {
                        return false;
                    }

                    break;
                }
                case Policy.EMPLOYMENT_TIME_2: {
                    JSONObject entityWsResultJson = evaluationCacheService.getCompartamosVariablesEvalWSResult(loanApplicationId, cachedSources).getResult();
                    Integer pnPunExp = entityWsResultJson != null && JsonUtil.getJsonObjectFromJson(entityWsResultJson, "poCliente", null) != null ?
                            JsonUtil.getIntFromJson(entityWsResultJson.getJSONObject("poCliente"), "pnPunExp", null) : null;
                    String compartamosCluster = pnPunExp == null ? null :
                            pnPunExp >= 790 ? "A" :
                                    (pnPunExp >= 730 && pnPunExp <= 789) ? "B" :
                                            (pnPunExp >= 681 && pnPunExp <= 729) ? "C" :
                                                    (pnPunExp >= 13 && pnPunExp <= 15) ? "NB" :
                                                            "0";

                    PersonOcupationalInformation personOcupationalInformation = evaluationCacheService.getOcupationalInformations(loanApplication.getPersonId(), cachedSources)
                            .stream()
                            .filter(p -> p.getNumber() == PersonOcupationalInformation.PRINCIPAL && (p.getActivityType().getId() == ActivityType.DEPENDENT || p.getActivityType().getId() == ActivityType.INDEPENDENT))
                            .min(Comparator.comparingInt(i -> i.getActivityType().getId()))
                            .orElse(null);

                    if (compartamosCluster != null) {
                        if (personOcupationalInformation == null || (Integer.parseInt(personOcupationalInformation.getEmploymentTime()) < ("NB" .equalsIgnoreCase(compartamosCluster) ? 12 : 6))) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.MINIMUM_INCOME_BUREAU: {
                    if (Entity.COMPARTAMOS == loanApplicationEvaluation.getEntityId()) {
                        JSONObject entityWsResultJson = evaluationCacheService.getCompartamosVariablesEvalWSResult(loanApplicationId, cachedSources).getResult();
                        double incomeBureau = entityWsResultJson != null && JsonUtil.getJsonObjectFromJson(entityWsResultJson, "poCliente", null) == null ?
                                JsonUtil.getDoubleFromJson(entityWsResultJson.getJSONObject("poCliente"), "pnIngNet", 0.0) : 0.0;

                        StaticDBInfo essalud = evaluationCacheService.getStaticDBInfoFromCache(loanApplication.getPersonId(), cachedSources);
                        double incomeEssalud = essalud == null || essalud.getIncome() == null ? 0 : essalud.getIncome();

                        if (Math.max(incomeBureau, incomeEssalud) < Double.parseDouble(param1)) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.SOBREENDEUDAMIENTO_BUREAU: {
                    if (Entity.COMPARTAMOS == loanApplicationEvaluation.getEntityId()) {
                        if (evaluationDAO.isOverindebtedCompartamos(loanApplicationId)) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.PUNTAJE_CONYUGUE: {
                    Integer clusterId = null;
                    if (Entity.COMPARTAMOS == loanApplicationEvaluation.getEntityId()) {
                        EntityWsResult entityWsResult = evaluationCacheService.getCompartamosVariablesEvalWSResult(loanApplicationId, cachedSources);
                        Integer conyuguePunExp = entityWsResult != null && entityWsResult.getResult() != null && JsonUtil.getJsonObjectFromJson(entityWsResult.getResult(), "poConyuge", null) != null ?
                                JsonUtil.getIntFromJson(entityWsResult.getResult().getJSONObject("poConyuge"), "pnPunExp", null) : null;

                        if (conyuguePunExp != null) {
                            clusterId = catalogDAO.getClusters()
                                    .stream()
                                    .filter(c -> c.getMinScore() != null && c.getMinScore() <= conyuguePunExp && c.getEntityId() != null && c.getEntityId() == Entity.COMPARTAMOS)
                                    .map(Cluster::getId)
                                    .findFirst()
                                    .orElse(null);
                        }
                    }

                    List<String> param2Parsed = param2 == null ? new ArrayList<>() : Arrays.asList(param2.replaceAll("\\{", "").replaceAll("\\}", "").split(","));
                    if ((loanApplicationEvaluation.getEntityId() == Entity.COMPARTAMOS && "in" .equalsIgnoreCase(param1)) && param2Parsed.contains(clusterId == null ? "1305" : clusterId.toString())) {
                        return false;
                    } else if (clusterId >= 14) {
                        return false;
                    }

                    break;
                }
                case Policy.TAXPAYER_TYPE: {
                    SunatResult sunatResult = evaluationCacheService.getSunatResultFromCache(loanApplication.getPersonId(), cachedSources);

                    if (sunatResult != null && sunatResult.getTaxpayerType().equalsIgnoreCase(param1)) {
                        return false;
                    }

                    break;
                }
                case Policy.EFL_RED: {
                    ApplicationEFLAssessment applicationEFLAssessment = evaluationCacheService.getEFLAssessmentByLoanApplicationId(loanApplicationId, cachedSources);
                    String scoreConfidence = applicationEFLAssessment != null && applicationEFLAssessment.getScoreConfidence() != null ? applicationEFLAssessment.getScoreConfidence() : "";

                    if (!scoreConfidence.equalsIgnoreCase("green")) {
                        return false;
                    }

                    break;
                }
                case Policy.EFL_SCORE: {
                    ApplicationEFLAssessment applicationEFLAssessment = evaluationCacheService.getEFLAssessmentByLoanApplicationId(loanApplicationId, cachedSources);

                    if (applicationEFLAssessment == null || applicationEFLAssessment.getScore() < Integer.parseInt(param1)) {
                        return false;
                    }

                    break;
                }
                case Policy.SAT_DEBT: {
                    if (loanApplication.getGuaranteedVehiclePlate() != null) {
                        List<SatPlateResult> satPlateResultList = evaluationCacheService.getSatPlateResultFromCache(loanApplication.getPersonId(), cachedSources);
                        SatPlateResult satPlateResult = satPlateResultList
                                .stream()
                                .filter(s -> s.getRegPlate().equalsIgnoreCase(loanApplication.getGuaranteedVehiclePlate()))
                                .findFirst()
                                .orElse(null);
                        if (satPlateResult != null && satPlateResult.getTotal() != null && satPlateResult.getTotal() >= Double.parseDouble(param1))
                            return false;
                    }
                    break;
                }
                case Policy.DEUDA_ONPE: {
                    OnpeResult onpeResult = evaluationCacheService.getOnpeResultFromCache(loanApplication.getPersonId(), cachedSources);
                    double sumAmount = (onpeResult == null && onpeResult.getDetails() != null ? 0 : onpeResult.getDetails()
                            .stream()
                            .filter(o -> o.getAmount() != null)
                            .mapToDouble(OnpeDetail::getAmount).sum());

                    if (sumAmount > 0) {
                        return false;
                    }

                    break;
                }
                case Policy.TIPO_DE_ACTIVIDAD: {
                    PersonOcupationalInformation personOcupationalInformation1 = evaluationCacheService.getOcupationalInformations(loanApplication.getPersonId(), cachedSources)
                            .stream()
                            .filter(p -> p.getNumber() == PersonOcupationalInformation.PRINCIPAL)
                            .findFirst()
                            .orElse(null);

                    if (Arrays
                            .stream(param1.replaceAll("\\{", "").replaceAll("\\}", "").split(","))
                            .mapToInt(Integer::parseInt)
                            .noneMatch(a -> personOcupationalInformation1.getActivityType().getId() == a)) {
                        return false;
                    }

                    break;
                }
                case Policy.ALLOWED_PROCESS: {
                    List<ApprovedDataLoanApplication> approvedDataLoanApplications = evaluationCacheService.getApprovedDataLoanApplication(loanApplicationId, cachedSources);

                    boolean allowedProcess = approvedDataLoanApplications
                            .stream()
                            .anyMatch(a -> a.getEntityId().equals(loanApplicationEvaluation.getEntityId()) && a.getAllowedProcess() != null && a.getAllowedProcess());
                    if (!allowedProcess) {
                        return false;
                    }

                    break;
                }
                case Policy.MONTO_MAXIMO_MENOR_AL_MINIMO_RIPLEY_PRESTAMO_YA: {
                    Integer clusterId = evaluationCacheService.getClusterId(loanApplicationEvaluation.getEntityId(), loanApplication.getId(), entityProductParameterId, cachedSources);
                    Integer priceId = catalogService.getEntityProductParamById(entityProductParameterId).getPriceId();
                    Double minAmount = catalogDAO.getRateCommissionClusters()
                            .stream()
                            .flatMap(r -> r.getRateCommissions().stream())
                            .filter(c -> (c.getClusterId() == 0 || c.getClusterId().equals(clusterId)) && c.getPriceId().equals(priceId))
                            .mapToDouble(RateCommission::getMinAmount)
                            .min()
                            .orElse(0);
                    ApprovedDataLoanApplication approvedDataLoanApplication = evaluationCacheService.getApprovedDataLoanApplication(loanApplicationId, cachedSources)
                            .stream().filter(a -> a.getEntityId().equals(Entity.RIPLEY)).findFirst().orElse(null);
                    Double maxAmount = approvedDataLoanApplication != null ? approvedDataLoanApplication.getMaxAmount() : null;

                    if (maxAmount != null && maxAmount <= minAmount) {
                        return false;
                    }

                    break;
                }
                case Policy.MIGRATIONS: {
                    MigracionesResult migracionesResult = personDAO.getMigraciones(loanApplication.getPersonId());

                    if (migracionesResult != null) {
                        if (migracionesResult.getTae().toLowerCase().contains("no vigente") ||
                                migracionesResult.getResidence().toLowerCase().contains("no vigente"))
                            return false;
                    }

                    break;
                }
                case Policy.RUC_PRINCIPAL_OCUPATION: {
                    List<PersonOcupationalInformation> personOcupationalInformations = personDAO.getPersonOcupationalInformation(Configuration.getDefaultLocale(), loanApplication.getPersonId());
                    if (personOcupationalInformations == null)
                        return false;

                    String ruc = personOcupationalInformations.get(0).getRuc();

                    if (ruc != null) {
                        Integer maxScoreMonthly = rccDAO.getMaxScoreMonthly(ruc, 1, 12);

                        if (maxScoreMonthly != null && maxScoreMonthly >= 4)
                            return false;
                    }

                    break;
                }
                case Policy.EFRU24MC: {
                    List<ApplicationBureau> applicationBureaus = loanApplicationDAO.getBureauResults(loanApplicationId);

                    applicationBureaus.sort(Comparator.comparing(ApplicationBureau::getRegisterDate).reversed());

                    ApplicationBureau applicationBureau = applicationBureaus.get(0);

                    if (applicationBureau.getEquifaxResult() == null)
                        return false;

                    List<EquifaxDeudasHistoricas> equifaxDeudasHistoricas = evaluationDAO.getEquifaxDeudasHistoricasByLoanApplicationId(loanApplicationId);

                    Double maxDeudaCastigada = equifaxDeudasHistoricas.stream().mapToDouble(EquifaxDeudasHistoricas::getDeudaCastigada).limit(24).max().orElse(0);

                    if (maxDeudaCastigada > 0)
                        return false;

                    break;
                }
                case Policy.RUC_SECONDARY_OCUPATION: {
                    List<PersonOcupationalInformation> ocupationalInformations = evaluationCacheService.getOcupationalInformations(loanApplication.getPersonId(), cachedSources);
                    if (ocupationalInformations != null) {
                        PersonOcupationalInformation secundaryOcupation = ocupationalInformations.stream().filter(o -> o.getNumber() == PersonOcupationalInformation.SECUNDARY).findFirst().orElse(null);
                        if (secundaryOcupation != null && secundaryOcupation.getRuc() != null) {
                            Integer maxScoreMonthly = rccDAO.getMaxScoreMonthly(secundaryOcupation.getRuc(), 1, 12);
                            if (maxScoreMonthly != null && maxScoreMonthly >= 4)
                                return false;
                        }
                    }

                    break;
                }
                case Policy.EFRU24MJ: {
                    List<EquifaxDeudasHistoricas> equifaxDeudasHistoricas = evaluationCacheService.getEquifaxDeudasHistoricasPeriodoSorted(loanApplicationId, cachedSources);
                    int maxDeudaJudicial = equifaxDeudasHistoricas
                            .stream()
                            .limit(24)
                            .mapToInt(e -> e.getDeudaJudicial() != null ? e.getDeudaJudicial() : 0)
                            .max().orElse(0);
                    if (maxDeudaJudicial > 0)
                        return false;

                    break;
                }
                case Policy.STUDENT: {
                    PersonOcupationalInformation personOcupationalInformation1 = evaluationCacheService.getOcupationalInformations(loanApplication.getPersonId(), cachedSources)
                            .stream()
                            .filter(p -> p.getNumber() == PersonOcupationalInformation.PRINCIPAL)
                            .findFirst()
                            .orElse(null);

                    if (personOcupationalInformation1 != null && personOcupationalInformation1.getActivityType() != null) {
                        if (personOcupationalInformation1.getActivityType().getId() == ActivityType.STUDENT) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.GUARANTOR: {
                    List<EquifaxAvales> equifaxAvales = evaluationDAO.getEquifaxAvales(loanApplicationId);
                    boolean exists = equifaxAvales.stream().anyMatch(e -> e.getCalificacion() != null && Arrays.asList("DUD", "PER", "DEF").contains(e.getCalificacion()));
                    if (exists) {
                        return false;
                    }

                    break;
                }
                case Policy.EFMU3456MCPP: {
                    List<EquifaxDeudasHistoricas> equifaxDeudasHistoricasList = evaluationCacheService.getEquifaxDeudasHistoricasPeriodoSorted(loanApplicationId, cachedSources)
                            .stream()
                            .skip(2)
                            .limit(4)
                            .collect(Collectors.toList());

                    EquifaxDeudasHistoricas equifaxDeudasHistoricasExists = equifaxDeudasHistoricasList
                            .stream()
                            .filter(e -> e.getCalificacion() != null)
                            .filter(e -> Arrays.asList("CPP", "DEF", "DUD", "PER").contains(e.getCalificacion().toUpperCase()))
                            .limit(1)
                            .findAny()
                            .orElse(null);

                    if (equifaxDeudasHistoricasExists != null) {
                        return false;
                    }

                    break;
                }
                case Policy.EFMU712DEF: {
                    List<EquifaxDeudasHistoricas> equifaxDeudasHistoricasList = evaluationCacheService.getEquifaxDeudasHistoricasPeriodoSorted(loanApplicationId, cachedSources)
                            .stream()
                            .skip(6)
                            .limit(6)
                            .collect(Collectors.toList());

                    EquifaxDeudasHistoricas equifaxDeudasHistoricasExists = equifaxDeudasHistoricasList
                            .stream()
                            .filter(e -> e.getCalificacion() != null)
                            .filter(e -> Arrays.asList("DEF", "DUD", "PER").contains(e.getCalificacion().toUpperCase()))
                            .limit(1)
                            .findAny()
                            .orElse(null);

                    if (equifaxDeudasHistoricasExists != null) {
                        return false;
                    }

                    break;
                }
                case Policy.EFMU24MDP: {
                    List<EquifaxDeudasHistoricas> equifaxDeudasHistoricasList = evaluationCacheService.getEquifaxDeudasHistoricasPeriodoSorted(loanApplicationId, cachedSources)
                            .stream()
                            .limit(12)
                            .collect(Collectors.toList());

                    EquifaxDeudasHistoricas equifaxDeudasHistoricasExists = equifaxDeudasHistoricasList
                            .stream()
                            .filter(e -> e.getCalificacion() != null)
                            .filter(e -> Arrays.asList("DUD", "PER").contains(e.getCalificacion().toUpperCase()))
                            .limit(1)
                            .findAny()
                            .orElse(null);

                    if (equifaxDeudasHistoricasExists != null) {
                        return false;
                    }

                    break;
                }
                case Policy.REGISTRA_LIQUIDACION_PLAN_SOCIAL_O_PROGRAMA_DE_EMPLEO: {
                    AnsesResult ansesResult = personDAO.getAnsesResult(loanApplication.getPersonId());
                    List<AnsesDetail> jsResult = ansesResult.getDetails();

                    String text = jsResult
                            .stream()
                            .filter(j -> j.getKey() != null)
                            .filter(j -> j.getKey().equalsIgnoreCase("PlanSoc"))
                            .map(AnsesDetail::getText)
                            .findFirst()
                            .orElse(null);

                    if (text != null && text.toLowerCase().startsWith("no")) {
                        return false;
                    }

                    break;
                }
                case Policy.MIN_ESSALUD_EMPLOYEES_QUANTITY: {
                    List<String> excludedRucs = Arrays.asList(param2.replaceAll("\\{", "").replaceAll("\\}", "").split(","));
                    String ruc;

                    if (Entity.ACCESO == loanApplicationEvaluation.getEntityId()) {
                        StaticDBInfo staticDBInfo = personDAO.getInfoFromStaticDB(loanApplication.getPersonId());

                        if (staticDBInfo != null && staticDBInfo.getRuc() != null) {
                            ruc = staticDBInfo.getRuc();

                            return isEmployeesQuantityValid(ruc, param1);
                        } else {

                            ruc = evaluationCacheService.getRucPersonOccupationalInformation(loanApplication.getPersonId(), cachedSources);

                            if (ruc == null || !excludedRucs.contains(ruc))
                                return false;
                        }
                    } else {
                        ruc = evaluationCacheService.getRucPersonOccupationalInformation(loanApplication.getPersonId(), cachedSources);

                        if (ruc == null)
                            return false;

                        if (!excludedRucs.contains(ruc))
                            return isEmployeesQuantityValid(ruc, param1);
                    }

                    break;
                }
                case Policy.ACCEPTED_LOCALITIES: {
                    List<AcceptedDepartment> acceptedDepartments = stringToArray(param1, AcceptedDepartment[].class);

                    if (!acceptedDepartments.isEmpty()) {

                        String departmentId;
                        String provinceId;
                        String districtId;

                        if (Address.TYPE_HOME.equals(param2)) {
                            PersonContactInformation contactInformation =
                                    personDAO.getPersonContactInformation(Configuration.getDefaultLocale(), loanApplication.getPersonId());

                            departmentId = contactInformation.getDepartment().getDepartmentId().toString();
                            provinceId = contactInformation.getProvince().getId() != null ? contactInformation.getProvince().getId() : contactInformation.getProvince().getProvinceId().toString();
                            districtId = contactInformation.getDistrict().getId() != null ? contactInformation.getDistrict().getId() : contactInformation.getDistrict().getDistrictId().toString();

                        } else if (Address.TYPE_WORK_PLACE.equals(param2)) {
                            PersonOcupationalInformation occupationalInformation = evaluationCacheService.getPersonOcupationalInformationByPerson(loanApplication.getPersonId(), cachedSources);

                            if (occupationalInformation == null) {
                                return true;
                            }

                            departmentId = occupationalInformation.getDistrict().getProvince().getDepartment().getDepartmentId().toString();
                            provinceId = occupationalInformation.getDistrict().getProvince().getId() != null ? occupationalInformation.getDistrict().getProvince().getId() : occupationalInformation.getDistrict().getProvince().getProvinceId().toString();
                            districtId = occupationalInformation.getDistrict().getId() != null ? occupationalInformation.getDistrict().getId() : occupationalInformation.getDistrict().getDistrictId().toString();

                        } else {
                            return true;
                        }

                        AcceptedDepartment acceptedDepartment = acceptedDepartments.stream().filter(e -> e.getDepartmentId().equals(departmentId)).findFirst().orElse(null);
                        if (acceptedDepartment == null) {
                            return false;
                        }

                        if (acceptedDepartment.getProvinces() == null) {
                            return true;
                        }

                        AcceptedProvince acceptedProvince = acceptedDepartment.getProvinces().stream().filter(e -> e.getProvinceId().equals(provinceId)).findFirst().orElse(null);
                        if (acceptedProvince == null) {
                            return false;
                        }

                        if (acceptedProvince.getDistricts() == null) {
                            return true;
                        }

                        AcceptedDistrict acceptedDistrict = acceptedProvince.getDistricts().stream().filter(e -> e.getDistrictId().equals(districtId)).findFirst().orElse(null);
                        if (acceptedDistrict == null) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.SCORE_EMAILAGE: {
                    User user = userDAO.getUser(loanApplication.getUserId());
                    JSONObject emailageQuery = new JSONObject(user.getEmailResult());
                    int param1Parsed = param1 == null ? 0 : Math.abs(Integer.parseInt(param1));

                    if (emailageQuery.has("error") || emailageQuery.getInt("count") <= 0 || !emailageQuery.has("results")) {
                        return false;
                    }

                    JSONArray emailageResults = emailageQuery.getJSONArray("results");

                    if (emailageResults.toList().isEmpty()) {
                        return false;
                    }

                    JSONObject emailageResult = emailageResults.getJSONObject(0);

                    if (!emailageResult.has("EAScore") || !emailageResult.has("fraudRisk")) {
                        return false;
                    }

                    int score = Integer.parseInt(emailageResult.getString("EAScore"));
                    logger.debug(score);

                    return score < param1Parsed;
                }
                case Policy.SCORE_TOPAZ_NUMERACION: {// TODO
                    JSONObject entityWsResultJson = evaluationCacheService.getFdlmTopazWSResult(loanApplicationId, cachedSources).getResult();
                    String responseJson = JsonUtil.getStringFromJson(entityWsResultJson, "responseJson", null);
                    ExecuteResponse executeResponse = new Gson().fromJson(responseJson, ExecuteResponse.class);
                    List<Integer> param1Parsed = param1 == null ? Collections.emptyList() : getListParams(param1).stream().map(Integer::parseInt).collect(Collectors.toList());

                    return executeResponse.getTopazMiddleWareResponse().getErrorCode() != 0;
                }
                case Policy.NIVELES_RIESGO_LISTAS_CONTROL: {

                    List<String> acceptedRiskLevels = getListParams(param1);

                    EntityWsResult consultaPersonaWsResult = evaluationCacheService.getFdlmListasControlConsultarPersonaWSResult(loanApplicationId, cachedSources);
                    if (consultaPersonaWsResult != null && consultaPersonaWsResult.getResult() != null) {
                        if (!consultaPersonaWsResult.getResult().isNull("DetalleRespuesta")) {
                            JSONArray jsonArray = JsonUtil.getJsonArrayFromJson(consultaPersonaWsResult.getResult(), "DetalleRespuesta", null);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (!jsonObject.isNull("Riesgo")) {

                                Integer risk = JsonUtil.getIntFromJson(jsonObject, "Riesgo", null);
                                if (acceptedRiskLevels.contains(risk.toString())) {
                                    return true;
                                }
                            }
                        }
                    }

                    return false;
                }
                case Policy.SCORE_DATACREDITO_DOCUMENTO_VIGENTE: {
                    EntityWsResult entityWsResult = evaluationCacheService.getFdlmDataCreditoDatosClienteWSResult(loanApplicationId, cachedSources);

                    if (entityWsResult == null) {
                        return false;
                    }

                    JSONObject entityWsResultJson = entityWsResult.getResult();
                    DatosClienteResponse response = new Gson().fromJson(entityWsResultJson.toString(), DatosClienteResponse.class);
                    JSONObject result = new JSONObject(response.getResult().getDatosResultado());
                    JSONObject param1Parsed = new JSONObject(param1);

                    JSONObject informe = result.getJSONObject("Informe");

                    if (!informe.has("NaturalNacional")) {
                        return false;
                    }

                    String estado = informe.getJSONObject("NaturalNacional").getJSONObject("Identificacion").getString("estado");
                    List<String> estadoDocumentoVigente = param1Parsed.getJSONArray("estado_documento").toList().stream().map(String::valueOf).collect(Collectors.toList());

                    if (!estadoDocumentoVigente.contains(estado)) {
                        return false;
                    }

                    break;
                }
                case Policy.SCORE_DATACREDITO_RANGO_EDAD: {
                    EntityWsResult entityWsResult = evaluationCacheService.getFdlmDataCreditoDatosClienteWSResult(loanApplicationId, cachedSources);

                    if (entityWsResult == null) {
                        return false;
                    }

                    JSONObject entityWsResultJson = entityWsResult.getResult();
                    DatosClienteResponse response = new Gson().fromJson(entityWsResultJson.toString(), DatosClienteResponse.class);
                    JSONObject result = new JSONObject(response.getResult().getDatosResultado());
                    JSONObject param1Parsed = new JSONObject(param1);

                    JSONObject informe = result.getJSONObject("Informe");

                    if (!informe.has("NaturalNacional")) {
                        return false;
                    }

                    Integer edadMinima = JsonUtil.getIntFromJson(informe.getJSONObject("NaturalNacional").getJSONObject("Edad"), "min", null);
                    Integer edadMaxima = JsonUtil.getIntFromJson(informe.getJSONObject("NaturalNacional").getJSONObject("Edad"), "max", null);
                    List<Integer> rangoEdades = param1Parsed.getJSONArray("rango_edad").toList().stream().map(o -> Integer.parseInt(o.toString())).collect(Collectors.toList());

                    if (edadMinima != null && (edadMinima < rangoEdades.get(0) || edadMinima > rangoEdades.get(1))) {
                        return false;
                    }

                    if (edadMaxima != null && (edadMaxima < rangoEdades.get(0) || edadMaxima > rangoEdades.get(1))) {
                        return false;
                    }

                    break;
                }
                case Policy.SCORE_DATACREDITO_NO_ESTADOS_EN_ESTADO_CUENTA_CUENTA_CORRIENTE_CUENTA_AHORRO: {
                    EntityWsResult entityWsResult = evaluationCacheService.getFdlmDataCreditoDatosClienteWSResult(loanApplicationId, cachedSources);

                    if (entityWsResult == null) {
                        return false;
                    }

                    JSONObject entityWsResultJson = entityWsResult.getResult();
                    DatosClienteResponse response = new Gson().fromJson(entityWsResultJson.toString(), DatosClienteResponse.class);
                    JSONObject result = new JSONObject(response.getResult().getDatosResultado());
                    JSONObject param1Parsed = new JSONObject(param1);

                    JSONObject informe = result.getJSONObject("Informe");

                    List<String> siEstadoCuenta = param1Parsed.getJSONArray("no_estado_cuenta").toList().stream().map(String::valueOf).collect(Collectors.toList());

                    if (informe.has("CuentaCorriente")) {
                        JSONArray cuentaCorriente = informe.optJSONArray("CuentaCorriente") != null ? informe.getJSONArray("CuentaCorriente") : new JSONArray().put(informe.getJSONObject("CuentaCorriente"));

                        boolean buenEstadoCuentaCorriente = StreamSupport
                                .stream(cuentaCorriente.spliterator(), false)
                                .map(j -> (JSONObject) j)
                                .filter(j -> j.has("Estado"))
                                .map(j -> j.getJSONObject("Estado").getString("codigo"))
                                .noneMatch(siEstadoCuenta::contains);

                        if (!buenEstadoCuentaCorriente) {
                            return false;
                        }
                    }

                    if (informe.has("CuentaAhorro")) {
                        JSONArray cuentaAhorro = informe.optJSONArray("CuentaAhorro") != null ? informe.getJSONArray("CuentaAhorro") : new JSONArray().put(informe.getJSONObject("CuentaAhorro"));

                        boolean buenaEstadoCuentaAhorro = StreamSupport
                                .stream(cuentaAhorro.spliterator(), false)
                                .map(j -> (JSONObject) j)
                                .filter(j -> j.has("Estado"))
                                .map(j -> j.getJSONObject("Estado").getString("codigo"))
                                .noneMatch(siEstadoCuenta::contains);

                        if (!buenaEstadoCuentaAhorro) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.SCORE_DATACREDITO_SITUACION_CERO: {
                    EntityWsResult entityWsResult = evaluationCacheService.getFdlmDataCreditoDatosClienteWSResult(loanApplicationId, cachedSources);

                    if (entityWsResult == null) {
                        return false;
                    }

                    JSONObject entityWsResultJson = entityWsResult.getResult();
                    DatosClienteResponse response = new Gson().fromJson(entityWsResultJson.toString(), DatosClienteResponse.class);
                    JSONObject result = new JSONObject(response.getResult().getDatosResultado());
                    JSONObject param1Parsed = new JSONObject(param1);

                    JSONObject informe = result.getJSONObject("Informe");

                    List<Integer> situaciones = param1Parsed.getJSONArray("situacion").toList().stream().map(o -> Integer.parseInt(o.toString())).collect(Collectors.toList());

                    if (informe.has("CuentaCartera")) {
                        JSONArray cuentaCartera = informe.optJSONArray("CuentaCartera") != null ? informe.getJSONArray("CuentaCartera") : new JSONArray().put(informe.getJSONObject("CuentaCartera"));

                        boolean situacionCeroCuentaCartera = StreamSupport
                                .stream(cuentaCartera.spliterator(), false)
                                .map(j -> (JSONObject) j)
                                .filter(j -> j.has("situacionTitular") && j.has("formaPago"))
                                .filter(j -> j.getInt("formaPago") != 1)
                                .map(j -> j.getInt("situacionTitular"))
                                .allMatch(situaciones::contains);

                        if (!situacionCeroCuentaCartera) {
                            return false;
                        }
                    }

                    if (informe.has("TarjetaCredito")) {
                        JSONArray tarjetaCredito = informe.optJSONArray("TarjetaCredito") != null ? informe.getJSONArray("TarjetaCredito") : new JSONArray().put(informe.getJSONObject("TarjetaCredito"));

                        boolean situacionCeroTarjetaCredito = StreamSupport
                                .stream(tarjetaCredito.spliterator(), false)
                                .map(j -> (JSONObject) j)
                                .filter(j -> j.has("situacionTitular") && j.has("formaPago"))
                                .filter(j -> j.getInt("formaPago") != 1)
                                .map(j -> j.getInt("situacionTitular"))
                                .allMatch(situaciones::contains);

                        if (!situacionCeroTarjetaCredito) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.SCORE_DATACREDITO_CALIFICACION_EN_CUENTA_AHORRO_CUENTA_CORRIENTE: {
                    EntityWsResult entityWsResult = evaluationCacheService.getFdlmDataCreditoDatosClienteWSResult(loanApplicationId, cachedSources);

                    if (entityWsResult == null) {
                        return false;
                    }

                    JSONObject entityWsResultJson = entityWsResult.getResult();
                    DatosClienteResponse response = new Gson().fromJson(entityWsResultJson.toString(), DatosClienteResponse.class);
                    JSONObject result = new JSONObject(response.getResult().getDatosResultado());
                    JSONObject param1Parsed = new JSONObject(param1);

                    JSONObject informe = result.getJSONObject("Informe");

                    List<Integer> calificacionSuperintendencia = param1Parsed.getJSONArray("superintendencia").toList().stream().map(o -> Integer.parseInt(o.toString())).collect(Collectors.toList());

                    if (informe.has("CuentaAhorro")) {
                        JSONArray cuentaAhorro = informe.optJSONArray("CuentaAhorro") != null ? informe.getJSONArray("CuentaAhorro") : new JSONArray().put(informe.getJSONObject("CuentaAhorro"));

                        boolean buenaCalificacionCuentaAhorro = StreamSupport
                                .stream(cuentaAhorro.spliterator(), false)
                                .map(j -> (JSONObject) j)
                                .filter(j -> j.has("calificacion"))
                                .map(j -> j.getInt("calificacion"))
                                .noneMatch(c -> c != calificacionSuperintendencia.get(0).intValue() && c != calificacionSuperintendencia.get(1).intValue());

                        if (!buenaCalificacionCuentaAhorro) {
                            return false;
                        }
                    }

                    if (informe.has("CuentaCorriente")) {
                        JSONArray cuentaCorriente = informe.optJSONArray("CuentaCorriente") != null ? informe.getJSONArray("CuentaCorriente") : new JSONArray().put(informe.getJSONObject("CuentaCorriente"));

                        boolean buenaCalificacionCuentaCorriente = StreamSupport
                                .stream(cuentaCorriente.spliterator(), false)
                                .map(j -> (JSONObject) j)
                                .filter(j -> j.has("calificacion"))
                                .map(j -> j.getInt("calificacion"))
                                .noneMatch(c -> c != calificacionSuperintendencia.get(0).intValue() && c != calificacionSuperintendencia.get(1).intValue());

                        if (!buenaCalificacionCuentaCorriente) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.SCORE_DATACREDITO_ESTADO_CUENTA_EN_CUENTA_CARTERA_TARJETA_CREDITO: {
                    EntityWsResult entityWsResult = evaluationCacheService.getFdlmDataCreditoDatosClienteWSResult(loanApplicationId, cachedSources);

                    if (entityWsResult == null) {
                        return false;
                    }

                    JSONObject entityWsResultJson = entityWsResult.getResult();
                    DatosClienteResponse response = new Gson().fromJson(entityWsResultJson.toString(), DatosClienteResponse.class);
                    JSONObject result = new JSONObject(response.getResult().getDatosResultado());
                    JSONObject param1Parsed = new JSONObject(param1);

                    JSONObject informe = result.getJSONObject("Informe");

                    List<String> noEstadoCuenta = param1Parsed.getJSONArray("si_estado_cuenta").toList().stream().map(String::valueOf).collect(Collectors.toList());

                    if (informe.has("CuentaCartera")) {
                        JSONArray cuentaCartera = informe.optJSONArray("CuentaCartera") != null ? informe.getJSONArray("CuentaCartera") : new JSONArray().put(informe.getJSONObject("CuentaCartera"));

                        boolean buenEstadoCuentaCuentaCartera = StreamSupport
                                .stream(cuentaCartera.spliterator(), false)
                                .map(j -> (JSONObject) j)
                                .filter(j -> j.has("Estados") && j.getJSONObject("Estados").has("EstadoCuenta"))
                                .map(j -> j.getJSONObject("Estados").getJSONObject("EstadoCuenta").get("codigo") instanceof String ? j.getJSONObject("Estados").getJSONObject("EstadoCuenta").getString("codigo") : String.valueOf(j.getJSONObject("Estados").getJSONObject("EstadoCuenta").getInt("codigo")))
                                .allMatch(noEstadoCuenta::contains);

                        if (!buenEstadoCuentaCuentaCartera) {
                            return false;
                        }
                    }

                    if (informe.has("TarjetaCredito")) {
                        JSONArray tarjetaCredito = informe.optJSONArray("TarjetaCredito") != null ? informe.getJSONArray("TarjetaCredito") : new JSONArray().put(informe.getJSONObject("TarjetaCredito"));

                        boolean buenEstadoCuentaTarjetaCredito = StreamSupport
                                .stream(tarjetaCredito.spliterator(), false)
                                .map(j -> (JSONObject) j)
                                .filter(j -> j.has("Estados") && j.getJSONObject("Estados").has("EstadoCuenta"))
                                .map(j -> j.getJSONObject("Estados").getJSONObject("EstadoCuenta").get("codigo") instanceof String ? j.getJSONObject("Estados").getJSONObject("EstadoCuenta").getString("codigo") : String.valueOf(j.getJSONObject("Estados").getJSONObject("EstadoCuenta").getInt("codigo")))
                                .allMatch(noEstadoCuenta::contains);

                        if (!buenEstadoCuentaTarjetaCredito) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.SCORE_DATACREDITO_INGRESO_MENSUAL_MINIMO: {
                    EntityWsResult entityWsResult = evaluationCacheService.getFdlmDataCreditoDatosClienteWSResult(loanApplicationId, cachedSources);

                    if (entityWsResult == null) {
                        return false;
                    }

                    JSONObject entityWsResultJson = entityWsResult.getResult();
                    DatosClienteResponse response = new Gson().fromJson(entityWsResultJson.toString(), DatosClienteResponse.class);
                    JSONObject result = new JSONObject(response.getResult().getDatosResultado());
                    List<PersonOcupationalInformation> personOcupationalInformationList = evaluationCacheService.getOcupationalInformations(loanApplication.getPersonId(), cachedSources);

                    JSONObject informe = result.getJSONObject("Informe");

                    if (!informe.has("productosValores")) {
                        return false;
                    }

                    if (!informe.getJSONObject("productosValores").has("valor1smlv") ||
                            !informe.getJSONObject("productosValores").has("valor1") ||
                            informe.getJSONObject("productosValores").getDouble("valor1smlv") < 1.0 ||
                            informe.getJSONObject("productosValores").getInt("valor1") == 0) {
                        return false;
                    }

                    if (personOcupationalInformationList == null) {
                        return false;
                    }

                    int valor1 = informe.getJSONObject("productosValores").getInt("valor1");
                    double valor1smlv = informe.getJSONObject("productosValores").getDouble("valor1smlv");
                    double q3 = valor1 * 1000;
                    double smlv = q3 / valor1smlv;

                    double fixedGrossIncome = personOcupationalInformationList.stream()
                            .filter(p -> p.getNumber() == PersonOcupationalInformation.PRINCIPAL)
                            .filter(p -> p.getFixedGrossIncome() != null)
                            .map(PersonOcupationalInformation::getFixedGrossIncome)
                            .findFirst()
                            .orElse(0.0);

                    if (fixedGrossIncome < smlv && q3 > smlv) {
                        return false;
                    }

                    if (fixedGrossIncome > smlv && q3 < smlv) {
                        return false;
                    }

                    if (fixedGrossIncome < smlv && q3 < smlv) {
                        return false;
                    }

                    break;
                }
                case Policy.SCORE_DATACREDITO_NO_ADJETIVOS_TARJETA_CREDITO_CUENTA_CARTERA: {
                    EntityWsResult entityWsResult = evaluationCacheService.getFdlmDataCreditoDatosClienteWSResult(loanApplicationId, cachedSources);

                    if (entityWsResult == null) {
                        return false;
                    }

                    JSONObject entityWsResultJson = entityWsResult.getResult();
                    DatosClienteResponse response = new Gson().fromJson(entityWsResultJson.toString(), DatosClienteResponse.class);
                    JSONObject result = new JSONObject(response.getResult().getDatosResultado());

                    JSONObject informe = result.getJSONObject("Informe");

                    if (informe.has("TarjetaCredito")) {
                        JSONArray tarjetaCredito = informe.optJSONArray("TarjetaCredito") != null ? informe.getJSONArray("TarjetaCredito") : new JSONArray().put(informe.getJSONObject("TarjetaCredito"));

                        String adjetivoTarjetaCredito = StreamSupport
                                .stream(tarjetaCredito.spliterator(), false)
                                .map(j -> (JSONObject) j)
                                .filter(j -> j.has("Adjetivo") && j.getJSONObject("Adjetivo").has("codigo"))
                                .map(j -> j.getJSONObject("Adjetivo").getString("codigo"))
                                .findFirst()
                                .orElse("0");

                        if (!adjetivoTarjetaCredito.equals("0")) {
                            return false;
                        }
                    }

                    if (informe.has("CuentaCartera")) {
                        JSONArray cuentaCartera = informe.optJSONArray("CuentaCartera") != null ? informe.getJSONArray("CuentaCartera") : new JSONArray().put(informe.getJSONObject("CuentaCartera"));

                        String adjetivoCuentaCartera = StreamSupport
                                .stream(cuentaCartera.spliterator(), false)
                                .map(j -> (JSONObject) j)
                                .filter(j -> j.has("Adjetivo") && j.getJSONObject("Adjetivo").has("codigo"))
                                .map(j -> j.getJSONObject("Adjetivo").getString("codigo"))
                                .findFirst()
                                .orElse("0");

                        if (!adjetivoCuentaCartera.equals("0")) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.SCORE_DATACREDITO_NO_ALERTA: {
                    EntityWsResult entityWsResult = evaluationCacheService.getFdlmDataCreditoDatosClienteWSResult(loanApplicationId, cachedSources);

                    if (entityWsResult == null) {
                        return false;
                    }

                    JSONObject entityWsResultJson = entityWsResult.getResult();
                    DatosClienteResponse response = new Gson().fromJson(entityWsResultJson.toString(), DatosClienteResponse.class);
                    JSONObject result = new JSONObject(response.getResult().getDatosResultado());
                    JSONObject param1Parsed = new JSONObject(param1);

                    JSONObject informe = result.getJSONObject("Informe");

                    if (!informe.has("NaturalNacional")) {
                        return false;
                    }

//                    TODO NO EN XML JSON
                    JSONObject alerta = param1Parsed.getJSONObject("alerta");

                    if (
                            informe.getJSONObject("NaturalNacional").has("Alerta") &&
                                    (
                                            !informe.getJSONObject("NaturalNacional").getJSONObject("Alerta").getString("codigo").equalsIgnoreCase(alerta.getString("codigo")) ||
                                                    !informe.getJSONObject("NaturalNacional").getJSONObject("Alerta").getJSONObject("Fuente").getString("codigo").equalsIgnoreCase(alerta.getString("fuente"))
                                    )
                    ) {
                        return false;
                    }

                    break;
                }
                case Policy.SCORE_DATACREDITO_CON_ADJETIVO_DISTINTO_CUENTA_AHORRO_CUENTA_CORRIENTE: {
                    EntityWsResult entityWsResult = evaluationCacheService.getFdlmDataCreditoDatosClienteWSResult(loanApplicationId, cachedSources);

                    if (entityWsResult == null) {
                        return false;
                    }

                    JSONObject entityWsResultJson = entityWsResult.getResult();
                    DatosClienteResponse response = new Gson().fromJson(entityWsResultJson.toString(), DatosClienteResponse.class);
                    JSONObject result = new JSONObject(response.getResult().getDatosResultado());

                    JSONObject informe = result.getJSONObject("Informe");

                    if (informe.has("CuentaAhorro")) {
                        JSONArray cuentaAhorro = informe.optJSONArray("CuentaAhorro") != null ? informe.getJSONArray("CuentaAhorro") : new JSONArray().put(informe.getJSONObject("CuentaAhorro"));

                        String adjetivoCuentaAhorro = StreamSupport
                                .stream(cuentaAhorro.spliterator(), false)
                                .map(j -> (JSONObject) j)
                                .filter(j -> j.has("Adjetivo") && j.getJSONObject("Adjetivo").has("codigo"))
                                .map(j -> j.getJSONObject("Adjetivo").getString("codigo"))
                                .findFirst()
                                .orElse("0");

                        if (adjetivoCuentaAhorro.equals("5")) {
                            return false;
                        }
                    }

                    if (informe.has("CuentaCorriente")) {
                        JSONArray cuentaCorriente = informe.optJSONArray("CuentaCorriente") != null ? informe.getJSONArray("CuentaCorriente") : new JSONArray().put(informe.getJSONObject("CuentaCorriente"));

                        String adjetivoCuentaCorriente = StreamSupport
                                .stream(cuentaCorriente.spliterator(), false)
                                .map(j -> (JSONObject) j)
                                .filter(j -> j.has("Adjetivo") && j.getJSONObject("Adjetivo").has("codigo"))
                                .map(j -> j.getJSONObject("Adjetivo").getString("codigo"))
                                .findFirst()
                                .orElse("0");

                        if (adjetivoCuentaCorriente.equals("5")) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.SCORE_DATACREDITO_ESTADO_PAGO: {
                    EntityWsResult entityWsResult = evaluationCacheService.getFdlmDataCreditoDatosClienteWSResult(loanApplicationId, cachedSources);

                    if (entityWsResult == null) {
                        return false;
                    }

                    JSONObject entityWsResultJson = entityWsResult.getResult();
                    DatosClienteResponse response = new Gson().fromJson(entityWsResultJson.toString(), DatosClienteResponse.class);
                    JSONObject result = new JSONObject(response.getResult().getDatosResultado());
                    JSONObject param1Parsed = new JSONObject(param1);

                    JSONObject informe = result.getJSONObject("Informe");

                    List<Integer> estadosPago = param1Parsed.getJSONArray("estado_pago").toList().stream().map(s -> Integer.parseInt(s.toString())).collect(Collectors.toList());

                    if (informe.has("TarjetaCredito")) {
                        JSONArray tarjetaCredito = informe.optJSONArray("TarjetaCredito") != null ? informe.getJSONArray("TarjetaCredito") : new JSONArray().put(informe.getJSONObject("TarjetaCredito"));

                        boolean tarjetaCreditoEstadoPagoEntreCodigo01y10 = StreamSupport
                                .stream(tarjetaCredito.spliterator(), false)
                                .map(j -> (JSONObject) j)
                                .filter(j -> j.has("Estados") && j.getJSONObject("Estados").has("EstadoCuenta"))
                                .map(j -> j.getJSONObject("Estados").getJSONObject("EstadoCuenta"))
                                .allMatch(j -> {
                                    int codigo = (j.get("codigo") instanceof Integer ? j.getInt("codigo") : Integer.parseInt(j.getString("codigo")));

                                    return codigo >= estadosPago.get(0) && codigo <= estadosPago.get(1);
                                });

                        if (!tarjetaCreditoEstadoPagoEntreCodigo01y10) {
                            return false;
                        }
                    }

                    if (informe.has("CuentaCartera")) {
                        JSONArray cuentaCartera = informe.optJSONArray("CuentaCartera") != null ? informe.getJSONArray("CuentaCartera") : new JSONArray().put(informe.getJSONObject("CuentaCartera"));

                        boolean cuentaCarteraEstadoPagoEntreCodigo01y10 = StreamSupport
                                .stream(cuentaCartera.spliterator(), false)
                                .map(j -> (JSONObject) j)
                                .filter(j -> j.has("Estados") && j.getJSONObject("Estados").has("EstadoCuenta"))
                                .map(j -> j.getJSONObject("Estados").getJSONObject("EstadoCuenta"))
                                .allMatch(j -> {
                                    int codigo = (j.get("codigo") instanceof Integer ? j.getInt("codigo") : Integer.parseInt(j.getString("codigo")));

                                    return codigo >= estadosPago.get(0) && codigo <= estadosPago.get(1);
                                });

                        if (!cuentaCarteraEstadoPagoEntreCodigo01y10) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.SCORE_DATACREDITO_NO_FORMA_PAGO: {
                    EntityWsResult entityWsResult = evaluationCacheService.getFdlmDataCreditoDatosClienteWSResult(loanApplicationId, cachedSources);

                    if (entityWsResult == null) {
                        return false;
                    }

                    JSONObject entityWsResultJson = entityWsResult.getResult();
                    DatosClienteResponse response = new Gson().fromJson(entityWsResultJson.toString(), DatosClienteResponse.class);
                    JSONObject result = new JSONObject(response.getResult().getDatosResultado());
                    JSONObject param1Parsed = new JSONObject(param1);

                    JSONObject informe = result.getJSONObject("Informe");

//                    NO TIENE FORMA TOTAL DE PAGO 2 +
                    int formaPago = param1Parsed.getInt("forma_pago");

                    if (informe.has("CuentaCartera")) {
                        JSONArray cuentaCartera = informe.optJSONArray("CuentaCartera") != null ? informe.getJSONArray("CuentaCartera") : new JSONArray().put(informe.getJSONObject("CuentaCartera"));

                        boolean formaPagoMayorA2 = StreamSupport
                                .stream(cuentaCartera.spliterator(), false)
                                .map(j -> (JSONObject) j)
                                .filter(j -> j.has("formaPago"))
                                .map(j -> j.getInt("formaPago"))
                                .anyMatch(f -> f > formaPago);

                        if (formaPagoMayorA2) {
                            return false;
                        }
                    }

                    break;
                }
                case Policy.UXM_DATACREDITO: {
                    EntityWsResult entityWsResult = evaluationCacheService.getFdlmDataCreditoDatosClienteWSResult(loanApplicationId, cachedSources);

                    if (entityWsResult == null) {
                        return false;
                    }

                    JSONObject entityWsResultJson = entityWsResult.getResult();
                    DatosClienteResponse response = new Gson().fromJson(entityWsResultJson.toString(), DatosClienteResponse.class);
                    JSONObject result = new JSONObject(response.getResult().getDatosResultado());

                    JSONObject informe = result.getJSONObject("Informe");

                    if (informe.has("InfoAgregadaMicrocredito")) {
                        JSONArray infoAgregadaMicrocredito_analisisVectores_sector = informe.getJSONObject("InfoAgregadaMicrocredito").optJSONObject("AnalisisVectores") != null ?
                                informe.getJSONObject("InfoAgregadaMicrocredito").getJSONObject("AnalisisVectores").optJSONArray("Sector") != null ?
                                        informe.getJSONObject("InfoAgregadaMicrocredito").getJSONObject("AnalisisVectores").getJSONArray("Sector") :
                                        new JSONArray().put(informe.getJSONObject("InfoAgregadaMicrocredito").getJSONObject("AnalisisVectores").getJSONObject("Sector")) :
                                null;

                        if (infoAgregadaMicrocredito_analisisVectores_sector != null) {
//                            U6M - Sector Financiero
                            JSONArray experienciaCrediticiaU6MSectorFinancieroJSONArray = StreamSupport
                                    .stream(infoAgregadaMicrocredito_analisisVectores_sector.spliterator(), false)
                                    .map(j -> (JSONObject) j)
                                    .filter(j -> j.has("nombreSector"))
                                    .filter(j -> j.getString("nombreSector").equalsIgnoreCase("Sector Financiero"))
                                    .map(j -> j.getJSONObject("MorasMaximas").getJSONArray("CaracterFecha"))
                                    .findFirst().orElse(new JSONArray());

                            List<Object> experienciaCrediticiaU6MSectorFinancieroList = experienciaCrediticiaU6MSectorFinancieroJSONArray.toList();
                            Collections.reverse(experienciaCrediticiaU6MSectorFinancieroList);

                            List<JSONObject> experienciaCrediticiaU6MCalificacionSectorFinanciero = StreamSupport
                                    .stream(new JSONArray(experienciaCrediticiaU6MSectorFinancieroList).spliterator(), false)
                                    .map(j -> (JSONObject) j)
                                    .limit(6)
                                    .collect(Collectors.toList());

//                            U6M - Sector Real
                            JSONArray experienciaCrediticiaU6MSectorRealJSONArray = StreamSupport
                                    .stream(infoAgregadaMicrocredito_analisisVectores_sector.spliterator(), false)
                                    .map(j -> (JSONObject) j)
                                    .filter(j -> j.has("nombreSector"))
                                    .filter(j -> j.getString("nombreSector").equalsIgnoreCase("Sector Real"))
                                    .map(j -> j.getJSONObject("MorasMaximas").getJSONArray("CaracterFecha"))
                                    .findFirst().orElse(new JSONArray());

                            List<Object> experienciaCrediticiaU6MSectorRealList = experienciaCrediticiaU6MSectorRealJSONArray.toList();
                            Collections.reverse(experienciaCrediticiaU6MSectorRealList);

                            List<JSONObject> experienciaCrediticiaU6MCalificacionSectorReal = StreamSupport
                                    .stream(new JSONArray(experienciaCrediticiaU6MSectorRealList).spliterator(), false)
                                    .map(j -> (JSONObject) j)
                                    .limit(6)
                                    .collect(Collectors.toList());

                            boolean mergedU6M = mergeSectorFinancieroSectorReal(experienciaCrediticiaU6MCalificacionSectorFinanciero, experienciaCrediticiaU6MCalificacionSectorReal).stream()
                                    .allMatch(j -> Collections.singletonList("N").contains(j.optString("saldoDeudaTotalMora", "-")));

                            if (!mergedU6M) {
                                return false;
                            }

//                            U12M - Sector Financiero
                            JSONArray experienciaCrediticiaU12MSectorFinancieroJSONArray = StreamSupport
                                    .stream(infoAgregadaMicrocredito_analisisVectores_sector.spliterator(), false)
                                    .map(j -> (JSONObject) j)
                                    .filter(j -> j.has("nombreSector"))
                                    .filter(j -> j.getString("nombreSector").equalsIgnoreCase("Sector Financiero"))
                                    .map(j -> j.getJSONObject("MorasMaximas").getJSONArray("CaracterFecha"))
                                    .findFirst().orElse(new JSONArray());

                            List<Object> experienciaCrediticiaU12MSectorFinancieroList = experienciaCrediticiaU12MSectorFinancieroJSONArray.toList();
                            Collections.reverse(experienciaCrediticiaU12MSectorFinancieroList);

                            List<JSONObject> experienciaCrediticiaU12MCalificacionSectorFinanciero = StreamSupport
                                    .stream(new JSONArray(experienciaCrediticiaU12MSectorFinancieroList).spliterator(), false)
                                    .map(j -> (JSONObject) j)
                                    .limit(12)
                                    .collect(Collectors.toList());

//                            U12M - Sector Real
                            JSONArray experienciaCrediticiaU12MSectorRealJSONArray = StreamSupport
                                    .stream(infoAgregadaMicrocredito_analisisVectores_sector.spliterator(), false)
                                    .map(j -> (JSONObject) j)
                                    .filter(j -> j.has("nombreSector"))
                                    .filter(j -> j.getString("nombreSector").equalsIgnoreCase("Sector Real"))
                                    .map(j -> j.getJSONObject("MorasMaximas").getJSONArray("CaracterFecha"))
                                    .findFirst().orElse(new JSONArray());

                            List<Object> experienciaCrediticiaU12MSectorRealList = experienciaCrediticiaU12MSectorRealJSONArray.toList();
                            Collections.reverse(experienciaCrediticiaU12MSectorRealList);

                            List<JSONObject> experienciaCrediticiaU12MCalificacionSectorReal = StreamSupport
                                    .stream(new JSONArray(experienciaCrediticiaU12MSectorRealList).spliterator(), false)
                                    .map(j -> (JSONObject) j)
                                    .limit(12)
                                    .collect(Collectors.toList());

                            boolean mergedU12M = mergeSectorFinancieroSectorReal(experienciaCrediticiaU12MCalificacionSectorFinanciero, experienciaCrediticiaU12MCalificacionSectorReal).stream()
                                    .allMatch(j -> Arrays.asList("N", "-").contains(j.optString("saldoDeudaTotalMora", "-")));

                            if (!mergedU12M) {
                                return false;
                            }

//                            U24M - Sector Financiero
                            JSONArray experienciaCrediticiaU24MSectorFinancieroJSONArray = StreamSupport
                                    .stream(infoAgregadaMicrocredito_analisisVectores_sector.spliterator(), false)
                                    .map(j -> (JSONObject) j)
                                    .filter(j -> j.has("nombreSector"))
                                    .filter(j -> j.getString("nombreSector").equalsIgnoreCase("Sector Financiero"))
                                    .map(j -> j.getJSONObject("MorasMaximas").getJSONArray("CaracterFecha"))
                                    .findFirst().orElse(new JSONArray());

                            List<Object> experienciaCrediticiaU24MSectorFinancieroList = experienciaCrediticiaU24MSectorFinancieroJSONArray.toList();
                            Collections.reverse(experienciaCrediticiaU24MSectorFinancieroList);

                            List<JSONObject> experienciaCrediticiaU24MCalificacionSectorFinanciero = StreamSupport
                                    .stream(new JSONArray(experienciaCrediticiaU24MSectorFinancieroList).spliterator(), false)
                                    .map(j -> (JSONObject) j)
                                    .limit(24)
                                    .collect(Collectors.toList());

//                            U24M - Sector Real
                            JSONArray experienciaCrediticiaU24MSectorRealJSONArray = StreamSupport
                                    .stream(infoAgregadaMicrocredito_analisisVectores_sector.spliterator(), false)
                                    .map(j -> (JSONObject) j)
                                    .filter(j -> j.has("nombreSector"))
                                    .filter(j -> j.getString("nombreSector").equalsIgnoreCase("Sector Real"))
                                    .map(j -> j.getJSONObject("MorasMaximas").getJSONArray("CaracterFecha"))
                                    .findFirst().orElse(new JSONArray());

                            List<Object> experienciaCrediticiaU24MSectorRealList = experienciaCrediticiaU24MSectorRealJSONArray.toList();
                            Collections.reverse(experienciaCrediticiaU24MSectorRealList);

                            List<JSONObject> experienciaCrediticiaU24MCalificacionSectorReal = StreamSupport
                                    .stream(new JSONArray(experienciaCrediticiaU24MSectorRealList).spliterator(), false)
                                    .map(j -> (JSONObject) j)
                                    .limit(24)
                                    .collect(Collectors.toList());

                            boolean mergedU24M = mergeSectorFinancieroSectorReal(experienciaCrediticiaU24MCalificacionSectorFinanciero, experienciaCrediticiaU24MCalificacionSectorReal).stream()
                                    .allMatch(j -> Arrays.asList("2", "1", "N", "-").contains(j.optString("saldoDeudaTotalMora", "-")));

                            if (!mergedU24M) {
                                return false;
                            }
                        }
                    }

                    JSONArray tarjetaCredito = informe.optJSONArray("TarjetaCredito") != null ? informe.getJSONArray("TarjetaCredito") :
                            (informe.optJSONObject("TarjetaCredito") != null ? new JSONArray().put(informe.getJSONObject("TarjetaCredito")) : new JSONArray());
                    JSONArray cuentaCartera = informe.optJSONArray("CuentaCartera") != null ? informe.getJSONArray("CuentaCartera") :
                            (informe.optJSONObject("CuentaCartera") != null ? new JSONArray().put(informe.getJSONObject("CuentaCartera")) : new JSONArray());

//                    Sin calificacion C / D - Tarjeta Credito
                    boolean sinCalificacionCyDU48MTarjetaCredito = StreamSupport
                            .stream(tarjetaCredito.spliterator(), false)
                            .map(j -> (JSONObject) j)
                            .filter(j -> j.has("comportamiento"))
                            .map(j -> j.getString("comportamiento").toUpperCase())
                            .anyMatch(c -> Arrays.asList("C", "D").contains(c));

                    if (sinCalificacionCyDU48MTarjetaCredito) {
                        return false;
                    }

//                    Sin calificacion C / D - Cuenta Cartera
                    boolean sinCalificacionCyDU48MCuentaCartera = StreamSupport
                            .stream(cuentaCartera.spliterator(), false)
                            .map(j -> (JSONObject) j)
                            .filter(j -> j.has("comportamiento"))
                            .map(j -> j.getString("comportamiento").toUpperCase())
                            .anyMatch(c -> Arrays.asList("C", "D").contains(c));

                    if (sinCalificacionCyDU48MCuentaCartera) {
                        return false;
                    }

                    break;
                }
                case Policy.TOPAZ: {

                    List<String> acceptedCodes = getListParams(param1);

                    EntityWsResult consultaCreditoWsResult = evaluationCacheService.getFdlmCreditoConsumoConsultarCreditoWSResult(loanApplicationId, cachedSources);

                    if (consultaCreditoWsResult != null && consultaCreditoWsResult.getResult() != null) {
                        JSONObject result = consultaCreditoWsResult.getResult();

                        if (result.has("CODIGO")) {
                            Integer code = JsonUtil.getIntFromJson(result, "CODIGO", null);

                            if (acceptedCodes.contains(code.toString())) {
                                return true;
                            }
                        }
                    }

                    return false;
                }
                case Policy.FDLM_ENDEUDAMIENTO: {
                    EntityWsResult entityWsResult = evaluationCacheService.getFdlmDataCreditoDatosClienteWSResult(loanApplicationId, cachedSources);

                    if (entityWsResult == null) {
                        return false;
                    }

                    JSONObject entityWsResultJson = entityWsResult.getResult();
                    DatosClienteResponse response = new Gson().fromJson(entityWsResultJson.toString(), DatosClienteResponse.class);
                    JSONObject result = new JSONObject(response.getResult().getDatosResultado());

                    JSONObject informe = result.getJSONObject("Informe");

                    if (!informe.has("CuentaCartera") || !informe.has("TarjetaCredito")) {
                        return false;
                    }

                    JSONArray cuentaCartera = informe.optJSONArray("CuentaCartera") != null ? informe.getJSONArray("CuentaCartera") : new JSONArray().put(informe.getJSONObject("CuentaCartera"));
                    JSONArray tarjetaCredito = informe.optJSONArray("TarjetaCredito") != null ? informe.getJSONArray("TarjetaCredito") : new JSONArray().put(informe.getJSONObject("TarjetaCredito"));

                    // TODO Recorrer json y sacar el doublede las cuotas totales segun datacredit

                    List<JSONObject> cuentaCarteras = StreamSupport
                            .stream(cuentaCartera.spliterator(), false)
                            .map(j -> (JSONObject) j)
                            .filter(j -> j.has("Caracteristicas") && j.getJSONObject("Caracteristicas").has("tipoCuenta")
                                    && JsonUtil.getJsonObjectFromJson(j, "Valores", null) != null && JsonUtil.getJsonObjectFromJson(j.getJSONObject("Valores"), "Valor", null) != null)// Quitar los que no tienen Caracteristicas{tipoCuenta, calidadDeudor} y Valores{valor}
                            .filter(j -> !Arrays.asList("CAV", "SBG").contains(j.getJSONObject("Caracteristicas").getString("tipoCuenta").toUpperCase()))// Solo aceptar si tipoCuenta no es CAV ni SBG
                            .filter(j -> j.getJSONObject("Caracteristicas").getString("calidadDeudor").equals("00"))// Solo aceptar si calidadDeudor es "00"
                            .filter(j -> j.getJSONObject("Valores").getJSONObject("Valor").optInt("periodicidad", 0) == 1)// Solo aceptar si periocidad es 1
                            .filter(j -> j.getJSONObject("Valores").getJSONObject("Valor").optInt("cuota", 0) == 0 ?
                                    (j.getJSONObject("Valores").getJSONObject("Valor").optInt("valorInicial", 0) > 0) : true)// Si la cuota es 0, debe tener valorInicial lleno
                            .filter(j -> JsonUtil.getIntFromJson(j.getJSONObject("Valores").getJSONObject("Valor"), "saldoActual", 0) > 0)// Solo aceptar si el saldoActual > 0. Si no es asi, se entiende que ya esta vencido.
                            .collect(Collectors.toList());

                    List<JSONObject> cuentaTarjetas = StreamSupport
                            .stream(tarjetaCredito.spliterator(), false)
                            .map(j -> (JSONObject) j)
                            .filter(j -> JsonUtil.getJsonObjectFromJson(j, "Valores", null) != null && JsonUtil.getJsonObjectFromJson(j.getJSONObject("Valores"), "Valor", null) != null)// Quitar los que no tienen Caracteristicas{tipoCuenta, calidadDeudor} y Valores{valor}
                            .filter(j -> JsonUtil.getDoubleFromJson(j.getJSONObject("Valores").getJSONObject("Valor"), "saldoActual", null) != null)// Sebe tener el campo valores.valor.saldoActual lleno
                            .filter(j -> j.getJSONObject("Valores").getJSONObject("Valor").optInt("cuota", 0) == 0 ?
                                    (j.getJSONObject("Valores").getJSONObject("Valor").optInt("valorInicial", 0) > 0) : true)// Si la cuota es 0, debe tener valorInicial lleno
                            .filter(j -> JsonUtil.getIntFromJson(j.getJSONObject("Valores").getJSONObject("Valor"), "saldoActual", 0) > 0)// Solo aceptar si el saldoActual > 0. Si no es asi, se entiende que ya esta vencido.
                            .collect(Collectors.toList());

                    List<JSONObject> cuentas = new ArrayList<>();
                    cuentas.addAll(cuentaCarteras);
                    cuentas.addAll(cuentaTarjetas);

                    Long totalDatacreditInstallment = cuentas.stream().mapToLong(j -> {
                        int cuota = j.getJSONObject("Valores").getJSONObject("Valor").optInt("cuota", 0);
                        if (cuota > 0) {
                            System.out.println(j.opt("numero") + " -> cuota: " + cuota);
                            return cuota;
                        } else {
                            int valorInicial = j.getJSONObject("Valores").getJSONObject("Valor").getInt("valorInicial");
                            System.out.println(j.opt("numero") + " -> valorInicial: " + valorInicial + " - multiplicacion: " + (Math.round(valorInicial * 0.0539250156112726)));
                            return Math.round(valorInicial * 0.0539250156112726);
                        }
                    }).sum();

                    ProductMaxMinParameter productMaxMinParameter = catalogDAO.getProductsEntity()
                            .stream()
                            .flatMap(p -> p.getProductMaxMinParameters().stream())
                            .filter(pmm -> pmm.getEntityId() != null && pmm.getEntityId().equals(loanApplicationEvaluation.getEntityId()))
                            .findFirst()
                            .orElse(null);
                    double minEffectiveAnnualRate = catalogDAO.getRateCommissionClusters()
                            .stream()
                            .flatMap(r -> r.getRateCommissions().stream())
                            .filter(r -> r.getEntityId().equals(loanApplicationEvaluation.getEntityId()) && r.getProductId().equals(loanApplicationEvaluation.getProductId()))
                            .mapToDouble(RateCommission::getEffectiveAnualRate)
                            .min()
                            .orElse(0);

                    double igv = (catalogService.getEntity(loanApplicationEvaluation.getEntityId()).getIgv()) / 100.0;
                    double min_amount = productMaxMinParameter != null ? productMaxMinParameter.getMinAmount() : 0;
                    int installments = productMaxMinParameter != null ? productMaxMinParameter.getMaxInstallments() : 0;
                    double nominal_annual_rate = (Math.pow((1 + (minEffectiveAnnualRate / 100.0)), (double) (1.0 / 12.0)) - 1) * 12.0;

                    Double calculatedCreditInstallmentAmount =
                            (min_amount * nominal_annual_rate * (1 + igv) / 12.0 *
                                    Math.pow((1 + nominal_annual_rate * (1 + igv) / 12.0), installments) /
                                    (Math.pow((1 + nominal_annual_rate * (1 + igv) / 12.0), installments) - 1));

                    Double maxClientInstallmentAmount = evaluationCacheService.getMaxInstallment(loanApplicationId, loanApplicationEvaluation.getEntityId(), loanApplicationEvaluation.getProductId(), loanApplicationEvaluation.getEntityProductParameterId(), cachedSources);

                    if (totalDatacreditInstallment + calculatedCreditInstallmentAmount >= maxClientInstallmentAmount)
                        return false;

                    break;
                }
                case Policy.ACCESO_SCORE_LD_CONSUMO: {
                    EntityWsResult accesoScoreLDConsumo = evaluationCacheService.getAccesoScoreLDConsumoWSResult(loanApplicationId, cachedSources);

                    if (accesoScoreLDConsumo != null && accesoScoreLDConsumo.getResult() != null) {
                        JSONObject result = accesoScoreLDConsumo.getResult();

                        if (result.has("data")) {
                            JSONObject innerData = result.getJSONObject("data");

                            if (innerData.has("evaluacion")) {
                                Integer evaluacion = JsonUtil.getIntFromJson(innerData, "evaluacion", null);

                                return evaluacion != null && evaluacion == 1;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
                case Policy.EMPLOYMENT_TIME_BY_ACTIVITY_TYPE: {
                    String activityType = param1;
                    Integer minMonths = param2 != null ? Integer.parseInt(param2) : null;
                    PersonOcupationalInformation personOcupationalInformation1 = evaluationCacheService.getOcupationalInformations(loanApplication.getPersonId(), cachedSources)
                            .stream()
                            .filter(p -> p.getNumber() == PersonOcupationalInformation.PRINCIPAL)
                            .findFirst()
                            .orElse(null);

                    Date startDate = null;
                    if (Objects.equals(param3, "true")) {
                        ApplicationBureau applicationBureau = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)
                                .stream()
                                .filter(a -> a.getBureauId() == Bureau.SENTINEL)
                                .findFirst()
                                .orElse(null);

                        if (applicationBureau != null && applicationBureau.getEquifaxResult() != null) {
                            JSONObject resultado = new JSONObject(applicationBureau.getEquifaxResult()).getJSONObject("Resultado");
                            if (resultado != null && !resultado.optString("FechIniActv").isEmpty()) {
                                startDate = new SimpleDateFormat("dd/MM/yyyy").parse(resultado.getString("FechIniActv"));
                            }
                        }
                    }

                    if (startDate == null) {
                        startDate = personOcupationalInformation1 != null ? personOcupationalInformation1.getStartDate() : null;
                    }

                    if ((activityType.equalsIgnoreCase("D") && personOcupationalInformation1.getActivityType().getId() == ActivityType.DEPENDENT)
                            || activityType.equalsIgnoreCase("I") && personOcupationalInformation1.getActivityType().getId() == ActivityType.INDEPENDENT) {
                        if (startDate != null && minMonths != null) {
                            LocalDate startDateLocal = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            LocalDate nowDateLocal = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            long monthsBetween = ChronoUnit.MONTHS.between(startDateLocal, nowDateLocal);
                            if (monthsBetween < minMonths) {
                                return false;
                            }
                        }
                    }
                    break;
                }
                case Policy.FINANSOL_RADAR_REPOS: {
                    double maxProtestosDebt = param1 != null ? Double.parseDouble(param1) : 0;

                    ApplicationBureau applicationBureau = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)
                            .stream()
                            .filter(a -> a.getBureauId() == Bureau.SENTINEL)
                            .findFirst()
                            .orElse(null);

                    if (applicationBureau != null && applicationBureau.getEquifaxResult() != null) {
                        JSONObject resultado = new JSONObject(applicationBureau.getEquifaxResult()).getJSONObject("Resultado");
                        if (resultado != null && !resultado.optString("DeudaProtestos").isEmpty()) {
                            double protestosDebt = Double.parseDouble(resultado.getString("DeudaProtestos"));

                            return protestosDebt <= maxProtestosDebt;
                        }
                    }

                    return true;
                }
                case Policy.NOSIS_REFERENCIA_COMERCIALES: {
                    List<ApplicationBureau> bureaus = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources);
                    if (bureaus.stream().anyMatch(b -> b.getBureauId() == Bureau.NOSIS_BDS)) {

                        ApplicationBureau bureau = bureaus.stream().filter(b -> b.getBureauId() == Bureau.NOSIS_BDS).findFirst().orElse(null);
                        JSONObject resultado_nosis_datos = new JSONObject(bureau.getEquifaxResult()).getJSONObject("Contenido").optJSONObject("Datos");
                        JSONArray resultado_nosis = resultado_nosis_datos != null ? resultado_nosis_datos.getJSONArray("Variables") : new JSONArray();
                        int vigCant = Integer.parseInt(StreamSupport
                                .stream(resultado_nosis.spliterator(), false)
                                .map(j -> (JSONObject) j)
                                .filter(j -> j.getString("Nombre").equalsIgnoreCase("RC_Vig_Cant"))
                                .findFirst()
                                .map(j -> j.getString("Valor").toLowerCase())
                                .orElse("0"));
                        if (vigCant >= 1)
                            return false;
                    }
                    break;
                }
                case Policy.NOSIS_NO_BANCARIZADOS: {
                    List<ApplicationBureau> bureaus = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources);
                    Integer breakPointBanked = param1 != null && !param1.isEmpty() ? Integer.parseInt(param1) : null;
                    Integer breakPointNoBanked = param2 != null && !param2.isEmpty() ? Integer.parseInt(param2) : null;

                    if (bureaus.stream().anyMatch(b -> b.getBureauId() == Bureau.NOSIS_BDS)) {

                        ApplicationBureau bureau = bureaus.stream().filter(b -> b.getBureauId() == Bureau.NOSIS_BDS).findFirst().orElse(null);
                        NosisRestResult nosisResult = new NosisRestResult();
                        nosisResult.fillFromJson(new JSONObject(bureau.getEquifaxResult()));
                        JSONObject resultado_nosis_datos = new JSONObject(bureau.getEquifaxResult()).getJSONObject("Contenido").optJSONObject("Datos");
                        JSONArray resultado_nosis = resultado_nosis_datos != null ? resultado_nosis_datos.getJSONArray("Variables") : new JSONArray();
                        String bancarizado = StreamSupport
                                .stream(resultado_nosis.spliterator(), false)
                                .map(j -> (JSONObject) j)
                                .filter(j -> j.getString("Nombre").equalsIgnoreCase("CI_Bancarizado"))
                                .findFirst()
                                .map(j -> j.getString("Valor").toLowerCase())
                                .orElse(null);
                        if (bancarizado != null && breakPointNoBanked != null && bancarizado.equalsIgnoreCase("no") && nosisResult.getScore() < breakPointNoBanked)
                            return false;
                        if (bancarizado != null && breakPointBanked != null && bancarizado.equalsIgnoreCase("si") && nosisResult.getScore() < breakPointBanked)
                            return false;
                    }
                    break;
                }
                case Policy.BANBIF_RESULTADO_CUESTIONARIO: {
                    EntityWsResult entityWsResult = evaluationCacheService.getBanBifResultadoCuestionarioWSResult(loanApplicationId, cachedSources);

                    if (entityWsResult != null && entityWsResult.getResult() != null) {
                        ResultadoEvaluacionTitularResponse response = new Gson().fromJson(entityWsResult.getResult().toString(), ResultadoEvaluacionTitularResponse.class);

                        if (response.getViso_resultado() != null &&
                                response.getViso_resultado().getEstado() != null &&
                                "APROBADO" .equalsIgnoreCase(response.getViso_resultado().getEstado())) {
                            return true;
                        }
                    }

                    return false;
                }
                case Policy.BANBIF_OTRA_NACIONALIDAD: {
                    if (loanApplication.getEntityCustomData().has(LoanApplication.EntityCustomDataKeys.BANBIF_ADDITIONAL_NATIONALITY_EXIST.getKey())) {
                        if (loanApplication.getEntityCustomData().getBoolean(LoanApplication.EntityCustomDataKeys.BANBIF_ADDITIONAL_NATIONALITY_EXIST.getKey()))
                            return false;
                    }
                    return true;
                }
                case Policy.SENTINEL_PRISMA: {
                    ApplicationBureau applicationBureau = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)
                            .stream()
                            .filter(a -> a.getBureauId() == Bureau.SENTINEL_INP_PRI)
                            .findFirst()
                            .orElse(null);

                    if (applicationBureau != null && applicationBureau.getEquifaxResult() != null) {
                        return validateSentinelPrismaResult(new Gson().fromJson(applicationBureau.getEquifaxResult(), WSSentinelInfPriExecuteResponse.class), Double.valueOf(param1), Double.valueOf(param2), Integer.parseInt(param3));
                    }
                    break;
                }
                case Policy.SENTINEL_PRISMA_EVALUATION: {
                    ApplicationBureau applicationBureau = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)
                            .stream()
                            .filter(a -> a.getBureauId() == Bureau.SENTINEL_INP_PRI)
                            .findFirst()
                            .orElse(null);

                    if (applicationBureau != null && applicationBureau.getEquifaxResult() != null) {
                        WSSentinelInfPriExecuteResponse sentinelPrismaResult = new Gson().fromJson(applicationBureau.getEquifaxResult(), WSSentinelInfPriExecuteResponse.class);
                        String lastCalification = param1;
                        String worstCalification12Months = param2;
                        if (sentinelPrismaResult != null && sentinelPrismaResult.getTiEntidades() != null && sentinelPrismaResult.getTiEntidades().getSDTInfCopacEnt() != null && !sentinelPrismaResult.getTiEntidades().getSDTInfCopacEnt().isEmpty()) {
                            int cpp = 0;
                            int normal = 0;
                            int deficiente = 0;
                            int dudoso = 0;
                            int perdida = 0;
                            Date lastDate = null;
                            List<SDTInfCopacEnt> lastEntities = new ArrayList<>();
                            SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
                            for (SDTInfCopacEnt entity : sentinelPrismaResult.getTiEntidades().getSDTInfCopacEnt()) {
                                if (entity.getEntiPerCal12M().equalsIgnoreCase("PER")) perdida++;
                                else if (entity.getEntiPerCal12M().equalsIgnoreCase("DUD")) dudoso++;
                                else if (entity.getEntiPerCal12M().equalsIgnoreCase("DEF")) deficiente++;
                                else if (entity.getEntiPerCal12M().equalsIgnoreCase("CPP")) cpp++;
                                else if (entity.getEntiPerCal12M().equalsIgnoreCase("NOR")) normal++;
                                if (lastDate == null) {
                                    lastDate = formatDate.parse(entity.getEntiFechaInf());
                                    lastEntities.add(entity);
                                } else if (formatDate.parse(entity.getEntiFechaInf()).compareTo(lastDate) >= 0) {
                                    lastEntities.add(entity);
                                    lastDate = formatDate.parse(entity.getEntiFechaInf());
                                }
                            }
                            if (param1 != null && !lastEntities.isEmpty() && !lastEntities.stream().filter(e -> !e.getEntiUltCal().equalsIgnoreCase("SCAL")).allMatch(e -> e.getEntiUltCal().equalsIgnoreCase(getSentinelQualificationValue(param1)))) {
                                return false;
                            }
                            if (param2 != null && !validateMaxQualification(normal, cpp, deficiente, dudoso, perdida, param2))
                                return false;
                        }
                    }
                    break;
                }
                case Policy.PEP: {
                    boolean shouldBePep = Boolean.parseBoolean(param1);
                    Person person = personDAO.getPerson(loanApplication.getPersonId(), false, Configuration.getDefaultLocale());
                    if ((shouldBePep && (person.getPep() == null || person.getPep()))
                            || (!shouldBePep && (person.getPep() == null || !person.getPep()))) {
                        return true;
                    }
                    return false;
                }
                case Policy.FATCA: {
                    boolean shouldBeFATCA = Boolean.parseBoolean(param1);
                    List<PersonDisqualifier> personDisqualifierList = personDAO.getPersonDisqualifierByPersonId(loanApplication.getPersonId());
                    boolean fatcaData = personDisqualifierList != null ? personDisqualifierList.stream().filter(p -> PersonDisqualifier.FACTA.equals(p.getType())).findFirst().orElse(null).isDisqualified() : false;
                    if(personDisqualifierList == null || personDisqualifierList.isEmpty()) return true;
                    if(fatcaData == shouldBeFATCA) return true;
                    return false;
                }
                case Policy.VERAZ_BUREAU: {
                    ApplicationBureau applicationBureau = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)
                            .stream()
                            .filter(a -> a.getBureauId() == Bureau.VERAZ_REST_BDS)
                            .findFirst()
                            .orElse(null);

                    if (applicationBureau != null && applicationBureau.getEquifaxResult() != null) {
                        VerazRestResponse verazResponse = new Gson().fromJson(applicationBureau.getEquifaxResult(), VerazRestResponse.class);
                        JSONArray params = new JSONArray(param1);
                        if(verazResponse != null && verazResponse.getApplicants() != null && !verazResponse.getApplicants().isEmpty() && verazResponse.getApplicants().get(0).getSMARTS_RESPONSE() != null && verazResponse.getApplicants().get(0).getSMARTS_RESPONSE().getVariablesDeSalida() != null){
                            JSONObject variablesDeSalida = new JSONObject(new Gson().toJson(verazResponse.getApplicants().get(0).getSMARTS_RESPONSE().getVariablesDeSalida()));
                            for (int i = 0; i < params.length(); i++) {
                                JSONArray valueToValidate = (JSONArray) params.get(i);
                                Boolean rejectedIsNullOrZero = false;
                                if(valueToValidate.get(0).toString().equalsIgnoreCase("score_veraz")) rejectedIsNullOrZero = true;
                                String variableData =  variablesDeSalida.optString(valueToValidate.get(0).toString());
                                if(rejectedIsNullOrZero && (variableData == null || (!variableData.isEmpty() && (variableData.equalsIgnoreCase("-") || Double.valueOf(variableData) <= 0.0)))) return false;
                                if(variableData == null) continue;
                                switch (valueToValidate.get(1).toString()){
                                    case "in":
                                        String valueToValidateIn = variableData != null && !variableData.isEmpty() ? variableData : null;
                                        if(valueToValidateIn.equalsIgnoreCase("-")) valueToValidateIn = null;
                                        List<String> param1Parsed = Arrays.asList(valueToValidate.get(2).toString().replaceAll("\'", "").replaceAll("\\[", "").replaceAll("\\]", "").split(","));
                                        if(valueToValidateIn != null && param1Parsed != null && param1Parsed.contains(valueToValidateIn)) return false;
                                        break;
                                    case "gte":
                                        Double valueToValidateGTE = variableData != null && !variableData.equalsIgnoreCase("-") ? Double.valueOf(variableData) : null;
                                        if(valueToValidateGTE != null && valueToValidateGTE >= Double.valueOf(valueToValidate.get(2).toString())) return false;
                                        break;
                                    case "lt":
                                        Double valueToValidateLT = variableData != null && !variableData.equalsIgnoreCase("-") ? Double.valueOf(variableData) : null;
                                        if(valueToValidateLT != null && valueToValidateLT < Double.valueOf(valueToValidate.get(2).toString())) return false;
                                        break;
                                }
                            }
                        }
                        else return false;
                    }
                    else return false;
                    break;
                }
                case Policy.BANTOTAL_PEP_FATCA_LISTANEGRA: {
                    EntityWsResult entityWsResult = evaluationCacheService.getBantotalListasNegras(loanApplicationId, cachedSources);
                    if(entityWsResult !=null){
                        BTPersonasValidarEnListasNegrasResponse response = new Gson().fromJson(entityWsResult.getResult().toString(), BTPersonasValidarEnListasNegrasResponse.class);
                        if( response != null && response.getExisteEnLista() != null && response.getExisteEnLista().equals("S")){
                            return false;
                        }
                    }

                    EntityWsResult entityWsResultPEP = evaluationCacheService.getBantotalPEP(loanApplicationId, cachedSources);
                    if(entityWsResultPEP !=null){
                        BTPersonasObtenerDatosPEPResponse responsePep = new Gson().fromJson(entityWsResultPEP.getResult().toString(), BTPersonasObtenerDatosPEPResponse.class);
                        if(responsePep !=null && responsePep.getEsPEP() != null && responsePep.getEsPEP().equals("S")){
                            return false;
                        }
                    }
                    break;
                }
                case Policy.VALIDATED_CELLPHONE_EMAIL:{
                    List<String> param1Parsed = param1 == null ? new ArrayList<>() : Arrays.asList(param1.replaceAll("\\{", "").replaceAll("\\}", "").split(","));
                    if (param1Parsed.size() > 0) {
                        for (String i : param1Parsed) {
                            switch (i){
                                case "email":
                                    Boolean isVerifiedEmail = evaluationCacheService.isVerifiedEmail(loanApplication.getAuxData().getRegisteredEmailId(), cachedSources);
                                    if(isVerifiedEmail == null || !isVerifiedEmail){
                                        return false;
                                    }
                                    break;
                                case "phone":
                                    Boolean isVerifiedPhoneNumber = evaluationCacheService.isVerifiedPhoneNumber(loanApplication.getAuxData().getRegisteredPhoneNumberId(), cachedSources);
                                    if(isVerifiedPhoneNumber == null || !isVerifiedPhoneNumber){
                                        return false;
                                    }
                                    break;
                            }
                        }
                    }
                    break;
                }
                case Policy.FINANSOL_CALIFICACION_Y_ENTIDADES_RADAR: {
                    Integer maxNumberOfEntities = param1 != null ? Integer.parseInt(param1) : null;
                    String validScore  = param2;

                    ApplicationBureau applicationBureau = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)
                            .stream()
                            .filter(a -> a.getBureauId() == Bureau.SENTINEL)
                            .findFirst()
                            .orElse(null);

                    if (applicationBureau != null && applicationBureau.getEquifaxResult() != null) {
                        JSONObject resultado = new JSONObject(applicationBureau.getEquifaxResult()).getJSONObject("Resultado");
                        if (resultado != null) {
                            String entitiesNumber = resultado.optString("NroBancos");
                            String scoreRadar = resultado.optString("Calificativo");

                            if(maxNumberOfEntities != null && entitiesNumber != null && !entitiesNumber.trim().isEmpty()){
                                if(Integer.valueOf(entitiesNumber) > maxNumberOfEntities) return false;
                            }

                            if(validScore != null && scoreRadar != null && !scoreRadar.trim().isEmpty()){
                                scoreRadar = scoreRadar.trim().replaceAll("\\s+","");
                                List<String> items = Arrays.asList(scoreRadar.split("\\s*%\\s*"));
                                Double nor = Double.valueOf(items.get(0).replaceAll("NOR", ""));
                                Double cpp = Double.valueOf(items.get(1).replaceAll("CPP", ""));
                                Double def = Double.valueOf(items.get(2).replaceAll("DEF", ""));
                                Double dud = Double.valueOf(items.get(3).replaceAll("DUD", ""));
                                Double per = Double.valueOf(items.get(4).replaceAll("PER", ""));
                                switch (validScore.toUpperCase()){
                                    case "NOR":
                                        return nor > 0 && !(cpp > 0 || def > 0 || dud > 0 || per > 0);
                                    case "CPP":
                                        return (nor > 0 || cpp > 0) && !(def > 0 || dud > 0 || per > 0);
                                    case "DEF":
                                        return (nor > 0 || cpp > 0 || def > 0) && !(dud > 0 || per > 0);
                                    case "DUD":
                                        return (nor > 0 || cpp > 0 || def > 0 || dud > 0) && !(per > 0);
                                    case "PER":
                                        return nor > 0 || cpp > 0 || def > 0 || dud > 0 || per > 0;
                                    default:
                                        return false;
                                }
                            }
                        }
                    }
                    break;
                }
                case Policy.BANTOTAL_VALIDACION_EMAIL_CELULAR: {
                    User user = evaluationCacheService.getUser(loanApplication.getUserId(), cachedSources);
                    EntityWsResult entityWsResult = evaluationCacheService.getBTPersona(loanApplication.getId(), cachedSources);
                    if(entityWsResult !=null){
                        BTPersonasObtenerResponse responseBTPersonasObtener = new Gson().fromJson(entityWsResult.getResult().toString(), BTPersonasObtenerResponse.class);
                        if(user != null && responseBTPersonasObtener != null ){
                            if(user.getEmail() != null && responseBTPersonasObtener.getSdtPersona().getCorreoElectronico() != null && !responseBTPersonasObtener.getSdtPersona().getCorreoElectronico().trim().isEmpty() && responseBTPersonasObtener.getSdtPersona().getCorreoElectronico().trim().matches(StringFieldValidator.PATTER_REGEX_EMAIL) && !user.getEmail().equalsIgnoreCase(responseBTPersonasObtener.getSdtPersona().getCorreoElectronico().trim())) return  false;
                            if(user.getPhoneNumber() != null && responseBTPersonasObtener.getSdtPersona().getTelefonoCelular() != null && !responseBTPersonasObtener.getSdtPersona().getTelefonoCelular().isEmpty() && !user.getPhoneNumber().equalsIgnoreCase(responseBTPersonasObtener.getSdtPersona().getTelefonoCelular())) return  false;
                        }
                    }
                    break;
                }
                case Policy.PROVISIONAL_DEBTORS: {
                    int debtorsMaxCant = Integer.parseInt(param1);
                    ApplicationBureau applicationBureauNosis = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)
                            .stream()
                            .filter(b -> b.getBureauId() == Bureau.NOSIS_BDS)
                            .findFirst()
                            .orElse(null);
                    NosisRestResult nosisResult = new NosisRestResult();
                    nosisResult.fillFromJson(new JSONObject(applicationBureauNosis.getEquifaxResult()));
                    JSONObject resultado_nosis_datos = new JSONObject(applicationBureauNosis.getEquifaxResult()).getJSONObject("Contenido").optJSONObject("Datos");
                    JSONArray resultado_nosis = resultado_nosis_datos != null ? resultado_nosis_datos.getJSONArray("Variables") : new JSONArray();
                    String nonPayments = StreamSupport
                            .stream(resultado_nosis.spliterator(), false)
                            .map(j -> (JSONObject) j)
                            .filter(j -> j.getString("Nombre").equalsIgnoreCase("AP_12m_Empleador_Impagos_Cant"))
                            .findFirst()
                            .map(j -> j.getString("Valor").toLowerCase())
                            .orElse(null);
                    if(nonPayments != null && !nonPayments.isEmpty() && Integer.valueOf(nonPayments) >= debtorsMaxCant) return false;
                    break;
                }
                case Policy.ACCESO_EVALUACION_GENERICA: {
                    EntityWsResult entityWsResult = evaluationCacheService.getAccesoEvaluacionGenerica(loanApplication.getId(), cachedSources);
                    if (entityWsResult != null) {
                        EvaluacionGenericaResponse response = new Gson().fromJson(entityWsResult.getResult().toString(), EvaluacionGenericaResponse.class);
                        if(response != null && response.getEstado() != null && response.getEstado().equalsIgnoreCase("Aprobado")){
                            return true;
                        }
                    }
                    return false;
                }
                case Policy.PARTNER_AGE: {
                    int minYears = Integer.parseInt(param1);
                    int maxYears = Integer.parseInt(param2);
                    Person person = personDAO.getPerson(loanApplication.getPersonId(), true, Configuration.getDefaultLocale());
                    if(person.getPartner() != null && person.getPartner().getBirthday() != null){
                        int yearsDifference = utilService.yearsBetween(person.getPartner().getBirthday(), new Date());
                        if (yearsDifference < minYears || yearsDifference > maxYears)
                            return false;
                    }
                    break;
                }
                case Policy.IFE_BENEFICIARIES: {
                    boolean mustHaveIFE = Boolean.parseBoolean(param1);
                    ApplicationBureau applicationBureauNosis = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)
                            .stream()
                            .filter(b -> b.getBureauId() == Bureau.NOSIS_BDS)
                            .findFirst()
                            .orElse(null);
                    NosisRestResult nosisResult = new NosisRestResult();
                    nosisResult.fillFromJson(new JSONObject(applicationBureauNosis.getEquifaxResult()));
                    JSONObject resultado_nosis_datos = new JSONObject(applicationBureauNosis.getEquifaxResult()).getJSONObject("Contenido").optJSONObject("Datos");
                    JSONArray resultado_nosis = resultado_nosis_datos != null ? resultado_nosis_datos.getJSONArray("Variables") : new JSONArray();
                    String resultIFE = StreamSupport
                            .stream(resultado_nosis.spliterator(), false)
                            .map(j -> (JSONObject) j)
                            .filter(j -> j.getString("Nombre").equalsIgnoreCase("AS_Beneficios_Detalle_Txt"))
                            .findFirst()
                            .map(j -> j.getString("Valor"))
                            .orElse(null);
                    boolean containIFEValue = resultIFE != null && !resultIFE.isEmpty() && (resultIFE.toLowerCase().contains("Ingreso Familiar de Emergencia".toLowerCase()) || resultIFE.toLowerCase().contains("REFUERZO DE INGRESOS".toLowerCase()));
                    if(mustHaveIFE != containIFEValue) return false;
                    break;
                }
                case Policy.IP_GEOLOCATION: {
                    if(param2 == null || param1 == null || loanApplication.getIpCountryCode() == null) return false;
                    List<String> countries = this.getListParams(param2);
                    if (param1.equalsIgnoreCase("in")) {
                        if(!countries.stream().anyMatch(e -> e.toLowerCase().equalsIgnoreCase(loanApplication.getIpCountryCode()))) return false;
                    } else if (param1.equalsIgnoreCase("out")) {
                        if(countries.stream().anyMatch(e -> e.toLowerCase().equalsIgnoreCase(loanApplication.getIpCountryCode()))) return false;
                    }
                    break;
                }
                case Policy.MIN_MAX_AGE: {
                    Integer minYears = param1 != null ? Integer.parseInt(param1) : null;
                    Integer maxYears = param2 != null ? Integer.parseInt(param2) : null;
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    if(person.getBirthday() == null)
                        return false;
                    int yearsDifference = utilService.yearsBetween(person.getBirthday(), new Date());
                    if (minYears != null && yearsDifference < minYears)
                        return false;
                    if (maxYears != null && yearsDifference > maxYears)
                        return false;
                    break;
                }
                default: {
                    logger.warn("Error in Policy " + policyId + ", not mapped");
                }
            }

        } catch (Exception ex) {
            throw new Exception("Error in Policy " + policyId + " with loan " + loanApplicationId + " and eval " + evaluationId + " [" + param1 + "," + param2 + "," + param3 + "]", ex);
        }

        return true;
    }

    private boolean isEmployeesQuantityValid(String ruc, String param1) {
        Integer employeesQuantity = personDAO.getEmployeesQuantity(ruc);
        Integer minEmployeesQuantity = Integer.parseInt(param1);

        if (employeesQuantity == null || employeesQuantity < minEmployeesQuantity)
            return false;

        return true;
    }

    private boolean validateSentinelPrismaResult(WSSentinelInfPriExecuteResponse result, Double minDeuda, Double minImpago, Integer maxEntity) {
        if (result != null) {
            List<SDTInfCopacEnt> entities = result.getTiEntidades() != null && result.getTiEntidades().getSDTInfCopacEnt() != null ? result.getTiEntidades().getSDTInfCopacEnt().stream().filter(e -> e.getEntidad() != null && !e.getEntidad().toUpperCase().contains("CAC MICROFINANZAS PRISMA")).collect(Collectors.toList()) : new ArrayList<>();
            if (maxEntity != null && entities.size() > maxEntity) return false;
            if (minImpago != null) {
                String[] montoDocumentoSplit = result.getTiMontodocumento().split(";");
                if (Arrays.stream(montoDocumentoSplit).anyMatch(e -> e.contains("DI"))) {
                    Double montoImpago = Double.valueOf(
                            Arrays.stream(montoDocumentoSplit)
                                    .filter(e -> e.contains("DI"))
                                    .findFirst().orElse("0.00")
                                    .replace("DI", "").trim()
                    );
                    if (montoImpago >= minImpago) return false;
                }
            }
            if (minDeuda != null && !entities.isEmpty()) {
                Double deudaValue = entities.stream().mapToDouble(SDTInfCopacEnt::getEntiSaldo).sum();
                if (deudaValue >= minDeuda) return false;
            }
            return true;
        }
        return false;
    }

    private boolean validateMaxQualification(
            int normal,
            int cpp,
            int deficiente,
            int dudoso,
            int perdida,
            String maxQualification
    ) {
        switch (maxQualification.toUpperCase()) {
            case "PERDIDA":
                return true;
            case "DUDOSO":
                if (perdida > 0) return false;
                break;
            case "DEFICIENTE":
                if (dudoso > 0 || perdida > 0) return false;
                break;
            case "CPP":
                if (deficiente > 0 || dudoso > 0 || perdida > 0) return false;
                break;
            case "NORMAL":
                if (cpp > 0 || deficiente > 0 || dudoso > 0 || perdida > 0) return false;
                break;
        }
        return true;
    }

    private String getSentinelQualificationValue(String qualification) {
        switch (qualification.toUpperCase()) {
            case "PERDIDA":
                return "PER";
            case "DUDOSO":
                return "DUD";
            case "DEFICIENTE":
                return "DEF";
            case "CPP":
                return "CPP";
            case "NORMAL":
                return "NOR";
        }
        return qualification;
    }

    private List<String> getListParams(String param) {
        return Arrays.asList(param.replaceAll("\\{", "").replaceAll("\\}", "").split(","));
    }

    private static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
        T[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr);
    }

    private List<JSONObject> mergeSectorFinancieroSectorReal(List<JSONObject> sectorFinanciero, List<JSONObject> sectorReal) {
        if (sectorFinanciero.size() != sectorReal.size()) {
            return Collections.emptyList();
        }

        List<String> goodBehaviour = Arrays.asList("", "N", "-");
        List<JSONObject> merged = new ArrayList<>();

        for (int i = 0; i < sectorFinanciero.size(); i++) {
            String saldoSF = sectorFinanciero.get(i).optString("saldoDeudaTotalMora", "-").trim().toUpperCase();
            String saldoSR = sectorReal.get(i).optString("saldoDeudaTotalMora", "-").trim().toUpperCase();

            if (!goodBehaviour.contains(saldoSF)) {
                merged.add(sectorFinanciero.get(i));

                continue;
            }

            if (!goodBehaviour.contains(saldoSR)) {
                merged.add(sectorReal.get(i));

                continue;
            }

            if ("N" .equals(saldoSF) && "-" .equals(saldoSR)) {
                merged.add(sectorFinanciero.get(i));
            } else if ("-" .equals(saldoSF) && "N" .equals(saldoSR)) {
                merged.add(sectorReal.get(i));
            } else {
                merged.add(sectorFinanciero.get(i));// or merged.add(sectorReal.get(i));
            }

        }

        return merged;
    }
}
