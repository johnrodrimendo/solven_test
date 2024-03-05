package com.affirm.common.service;

import com.affirm.acceso.model.EvaluacionGenericaResponse;
import com.affirm.bancoazteca.model.BancoAztecaCampaniaApi;
import com.affirm.bancoazteca.model.BancoAztecaGatewayApi;
import com.affirm.bancoazteca.model.RolConsejero;
import com.affirm.bancodelsol.model.CampaniaBds;
import com.affirm.bantotalrest.model.RBTPG006.BTClientesObtenerCuentasAhorroResponse;
import com.affirm.bantotalrest.model.RBTPG011.ObtenerPrestamosClienteResponse;
import com.affirm.bantotalrest.model.RBTPG013.BTClientesObtenerPlazosFijosResponse;
import com.affirm.bantotalrest.model.RBTPG085.BTPersonasObtenerResponse;
import com.affirm.cajasullana.model.AdmisibilidadResponse;
import com.affirm.common.dao.CreditDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.RccDAO;
import com.affirm.common.model.BantotalApiData;
import com.affirm.bantotalrest.model.RBTPG019.BTPersonasValidarEnListasNegrasResponse;
import com.affirm.common.dao.*;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.StringFieldValidator;
import com.affirm.compartamos.model.Cliente;
import com.affirm.compartamos.model.Credito;
import com.affirm.compartamos.model.Solicitud;
import com.affirm.compartamos.model.VariablePreEvaluacionResponse;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.security.model.EntityWsResult;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTimeComparator;
import org.jooq.lambda.tuple.Tuple2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service("hardFilterService")
public class HardFilterService {

    private static final Logger logger = Logger.getLogger(HardFilter.class);

    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private UtilService utilService;
    @Autowired
    private RccDAO rccDao;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private SecurityDAO securityDao;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private EvaluationCacheService evaluationCacheService;
    @Autowired
    private UserDAO userDAO;

    public boolean runHardFilter(int hardFilterId, String param1, String param2, String param3, LoanApplicationPreliminaryEvaluation preliminaryEvaluation, LoanApplication loanApplication, Map<String, Object> cachedSources) throws Exception {
        try {
            switch (hardFilterId) {
                case HardFilter.MIN_AGE: {
                    Integer minYears = Integer.parseInt(param1);
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    if (person == null || person.getBirthday() == null)
                        return false;
                    int yearsDifference = utilService.yearsBetween(person.getBirthday(), new Date());
                    if (yearsDifference < minYears)
                        return false;
                    break;
                }
                case HardFilter.MAX_AGE: {
                    Integer maxYears = Integer.parseInt(param1);
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    if (person == null || person.getBirthday() == null)
                        return false;
                    int yearsDifference = utilService.yearsBetween(person.getBirthday(), new Date());
                    if (yearsDifference > maxYears)
                        return false;
                    break;
                }
                case HardFilter.U24MC: {
                    Integer limitDate = Integer.parseInt(param1 != null ? param1 : "-1");
                    Integer maxCastigo = param2 != null ? Integer.parseInt(param2) : 0;
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    List<Integer> rccDateCodMesToProcess;
                    if (limitDate < 0) {
                        rccDateCodMesToProcess = evaluationCacheService.getRccDatesSorted(cachedSources)
                                .stream()
                                .map(r -> r.getCodMes())
                                .collect(Collectors.toList());
                    } else {
                        rccDateCodMesToProcess = evaluationCacheService.getRccDatesSorted(cachedSources)
                                .stream()
                                .limit(limitDate)
                                .map(r -> r.getCodMes())
                                .collect(Collectors.toList());
                    }
                    List<RccSynthesized> synthesizedsToProcess = evaluationCacheService.getRccSynthesized(person.getDocumentNumber(), cachedSources)
                            .stream()
                            .filter(s -> rccDateCodMesToProcess.contains(s.getCodMes()))
                            .collect(Collectors.toList());
                    double sumSaldoCastigo = synthesizedsToProcess.stream().mapToDouble(s -> s.getSaldoCastigo()).sum();
                    if (sumSaldoCastigo > maxCastigo) {
                        return false;
                    }
                    break;
                }
                case HardFilter.U24MJ: {
                    Integer limitDate = Integer.parseInt(param1 != null ? param1 : "-1");
                    Integer maxSaldoJudicial = param2 != null ? Integer.parseInt(param2) : 0;
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    List<Integer> rccDateCodMesToProcess;
                    if (limitDate < 0) {
                        rccDateCodMesToProcess = evaluationCacheService.getRccDatesSorted(cachedSources)
                                .stream()
                                .map(r -> r.getCodMes())
                                .collect(Collectors.toList());
                    } else {
                        rccDateCodMesToProcess = evaluationCacheService.getRccDatesSorted(cachedSources)
                                .stream()
                                .limit(limitDate)
                                .map(r -> r.getCodMes())
                                .collect(Collectors.toList());
                    }
                    List<RccSynthesized> synthesizedsToProcess = evaluationCacheService.getRccSynthesized(person.getDocumentNumber(), cachedSources)
                            .stream()
                            .filter(s -> rccDateCodMesToProcess.contains(s.getCodMes()))
                            .collect(Collectors.toList());
                    double sumSaldoJudicial = synthesizedsToProcess.stream().mapToDouble(s -> s.getSaldoJudicial()).sum();
                    if (sumSaldoJudicial > maxSaldoJudicial) {
                        return false;
                    }
                    break;
                }
                case HardFilter.U24MDP: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    List<RccMonthlyScore> rccMothlyScoresToProcess = evaluationCacheService.getRccMonthlyScore(person.getDocumentNumber(), cachedSources)
                            .stream()
                            .filter(r -> r.getMonthId() >= 1 && r.getMonthId() <= 24 && r.getScore() != null)
                            .collect(Collectors.toList());
                    if (!rccMothlyScoresToProcess.isEmpty()) {
                        int maxScore = rccMothlyScoresToProcess
                                .stream()
                                .mapToInt(r -> r.getScore())
                                .max().orElse(0);
                        int minScore = rccMothlyScoresToProcess
                                .stream()
                                .mapToInt(r -> r.getScore())
                                .min().orElse(0);
                        if (Arrays.asList(6, 8, 9).contains(maxScore) || minScore == -1) {
                            return false;
                        }
                    }
                    break;
                }
                case HardFilter.U712DEF: {
                    Integer minMonth = Integer.parseInt(param1);
                    Integer maxMonth = Integer.parseInt(param2);
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    List<RccMonthlyScore> rccMothlyScoresToProcess = evaluationCacheService.getRccMonthlyScore(person.getDocumentNumber(), cachedSources)
                            .stream()
                            .filter(r -> r.getMonthId() >= minMonth && r.getMonthId() <= maxMonth && r.getScore() != null && Arrays.asList(5, -1).contains(r.getScore()))
                            .collect(Collectors.toList());
                    if (rccMothlyScoresToProcess.size() > 0) {
                        return false;
                    }
                    break;
                }
                case HardFilter.U9MR: {
                    Integer maxMonth = param1 != null ? Integer.parseInt(param1) : 12;
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    List<RccMonthlyScore> rccMothlyScoresToProcess = evaluationCacheService.getRccMonthlyScore(person.getDocumentNumber(), cachedSources)
                            .stream()
                            .filter(r -> r.getMonthId() >= 1 && r.getMonthId() <= maxMonth && r.getScore() != null && Arrays.asList(7, -1).contains(r.getScore()))
                            .collect(Collectors.toList());
                    if (rccMothlyScoresToProcess.size() > 0) {
                        return false;
                    }
                    break;
                }
                case HardFilter.U3456MCPP: {
                    Integer minMonth = Integer.parseInt(param1);
                    Integer maxMonth = Integer.parseInt(param2);
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    List<RccMonthlyScore> rccMothlyScoresToProcess = evaluationCacheService.getRccMonthlyScore(person.getDocumentNumber(), cachedSources)
                            .stream()
                            .filter(r -> r.getMonthId() >= minMonth && r.getMonthId() <= maxMonth && r.getScore() != null)
                            .collect(Collectors.toList());
                    if (!rccMothlyScoresToProcess.isEmpty()) {
                        int maxScore = rccMothlyScoresToProcess
                                .stream()
                                .mapToInt(r -> r.getScore())
                                .max().orElse(0);
                        int minScore = rccMothlyScoresToProcess
                                .stream()
                                .mapToInt(r -> r.getScore())
                                .min().orElse(0);
                        if (maxScore >= 3 || minScore == -1) {
                            return false;
                        }
                    }
                    break;
                }
                case HardFilter.U3MN: {
                    Integer maxMonth = Integer.parseInt(param1);
                    Integer maxScoreParam = param2 != null ? Integer.parseInt(param2) : 3;
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    List<RccMonthlyScore> rccMothlyScoresToProcess = evaluationCacheService.getRccMonthlyScore(person.getDocumentNumber(), cachedSources)
                            .stream()
                            .filter(r -> r.getMonthId() >= 1 && r.getMonthId() <= maxMonth && r.getScore() != null)
                            .collect(Collectors.toList());
                    if (!rccMothlyScoresToProcess.isEmpty()) {
                        int maxScore = rccMothlyScoresToProcess
                                .stream()
                                .mapToInt(r -> r.getScore())
                                .max().orElse(0);
                        int minScore = rccMothlyScoresToProcess
                                .stream()
                                .mapToInt(r -> r.getScore())
                                .min().orElse(0);
                        if (maxScore >= maxScoreParam || minScore == -1) {
                            return false;
                        }
                    }
                    break;
                }
                case HardFilter.EXP_PERU: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    RccScore rccScore = evaluationCacheService.getRccScore(person.getDocumentNumber(), cachedSources);
                    if (rccScore == null || rccScore.getExperience() == null || rccScore.getExperience() == 0)
                        return false;
                    break;
                }
                case HardFilter.NEGATIVE_BASE: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);

                    if (preliminaryEvaluation.getEntityId() == Entity.AELU) {
                        return evaluationCacheService.getPersonRawAssociateds(person.getDocumentType().getId(), person.getDocumentNumber(), cachedSources)
                                .stream()
                                .noneMatch(p -> PersonRawAssociated.ListType.BLACKLIST.getCode().equalsIgnoreCase(p.getListType()));
                    } else {
                        return !evaluationCacheService.getPersonNegativeBase(loanApplication.getPersonId(), cachedSources);
                    }
                }
                case HardFilter.PAST_DUE: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    List<Credit> personCredits = evaluationCacheService.getCreditsByPerson(person.getId(), cachedSources);
                    Date currentDateTruncated = DateUtils.truncate(new Date(), java.util.Calendar.DAY_OF_MONTH);
                    boolean anyPastDueDate = personCredits.stream()
                            .filter(c -> c.getManagementSchedule() != null)
                            .map(c -> c.getManagementSchedule())
                            .flatMap(Collection::stream)
                            .anyMatch(m -> m.getInArrears() != null && m.getInArrears()
                                    && m.getInstallmentStatusId() != null && m.getInstallmentStatusId() == ManagementSchedule.STATUS_MORA
                                    && (DateUtils.truncate(m.getDueDate(), java.util.Calendar.DAY_OF_MONTH).before(currentDateTruncated) || DateUtils.truncate(m.getDueDate(), java.util.Calendar.DAY_OF_MONTH).equals(currentDateTruncated)));
                    if (anyPastDueDate)
                        return false;
                    break;
                }
//            case HardFilter.CLOSED_PLATFORM:{
//                break;
//            }
                case HardFilter.ACTIVE_ENTITIES: {
                    Integer maxEntities = Integer.parseInt(param1);
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    List<RccSynthesized> synthesizedsToProcess = evaluationCacheService.getRccSynthesized(person.getDocumentNumber(), cachedSources)
                            .stream()
                            .filter(r -> r.getCodMes() != null && r.getCodMes().intValue() == preliminaryEvaluation.getRccCodMes())
                            .collect(Collectors.toList());
                    List<String> distinctEntities = synthesizedsToProcess
                            .stream()
                            .filter(s -> s.getCodEmp() != null)
                            .map(s -> s.getCodEmp())
                            .distinct().collect(Collectors.toList());
                    if (distinctEntities.size() > maxEntities)
                        return false;
                    break;
                }
                case HardFilter.ACTIVE_PROCCES_IN_ANOTHER_ENTITY: {
                    Date entityApplicationExpirationDate = loanApplication.getEntityApplicationExpirationDate();
                    if (entityApplicationExpirationDate != null) {
                        entityApplicationExpirationDate = DateUtils.truncate(loanApplication.getEntityApplicationExpirationDate(), java.util.Calendar.DAY_OF_MONTH);
                    } else {
                        Calendar c = Calendar.getInstance();
                        c.add(Calendar.DATE, -1);
                        entityApplicationExpirationDate = DateUtils.truncate(c.getTime(), java.util.Calendar.DAY_OF_MONTH);
                    }
                    Date currentDate = DateUtils.truncate(new Date(), java.util.Calendar.DAY_OF_MONTH);
                    if (entityApplicationExpirationDate.after(currentDate))
                        return false;
                    break;
                }
                case HardFilter.ACTIVE_DEBT_CONSOLIDATION: {
                    List<Integer> activeConsolidationCredits = creditDao.getCreditsByPersonFiltered(
                            loanApplication.getPersonId(),
                            Arrays.asList(Product.DEBT_CONSOLIDATION, Product.DEBT_CONSOLIDATION_OPEN),
                            Arrays.asList(CreditStatus.ACTIVE));
                    if (activeConsolidationCredits.size() > 0)
                        return false;
                    break;
                }
//            case HardFilter.U6SV:{
//                break;
//            }
                case HardFilter.NOCONSOLIDABLEDEBTS: {
                    Double minBalance = Double.parseDouble(param1);
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    List<EntityConsolidableDebt> rccConsolidableDebts = evaluationCacheService.getRccConsolidabelDebts(person.getDocumentType().getId(), person.getDocumentNumber(), cachedSources);
                    double sumBalance = rccConsolidableDebts.stream()
                            .filter(r -> r.getAccounts() != null)
                            .map(r -> r.getAccounts())
                            .flatMap(Collection::stream)
                            .filter(a -> a != null && a.getAccount() != null && a.getAccount().getId() != null)
                            .filter(a -> Arrays.asList(42, 45, 13, 14, 15, 16, 20, 21, 22, 23).contains(a.getAccount().getId()))
                            .mapToDouble(a -> a.getBalance() != null ? a.getBalance() : 0).sum();
                    if (sumBalance < minBalance)
                        return false;
                    break;
                }
                case HardFilter.DEBT_PERCENTAGE_ENTITIES: {
                    RccDate rccDate = evaluationCacheService.getRccDatesSorted(cachedSources)
                            .stream()
                            .filter(d -> preliminaryEvaluation.getRccCodMes().equals(d.getCodMes())).findFirst().orElse(null);

                    if (rccDate != null) {
                        Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);

                        List<RccSal> rccSals = evaluationCacheService.getRccIdeGrouped(person.getDocumentNumber(), cachedSources)
                                .stream()
                                .flatMap(g -> g.getRccSals().stream())
                                .filter(g -> DateTimeComparator.getDateOnlyInstance().compare(g.getFecRep(), rccDate.getFecRep()) == 0)
                                .filter(g ->
                                        (g.getCodCue().matches("(^14)\\d\\d(0202)\\d+") && !g.getCodCue().matches("(^14)\\d(9)\\d+") && !g.getCodCue().matches("(^14)\\d(8)\\d+"))
                                                || (g.getCodCue().matches("(^14)\\d\\d(020601)\\d+") && !g.getCodCue().matches("(^14)\\d(9)\\d+") && !g.getCodCue().matches("(^14)\\d(8)\\d+"))
                                                || (g.getCodCue().matches("(^14)\\d\\d(0204)\\d+") && !g.getCodCue().matches("(^14)\\d(9)\\d+") && !g.getCodCue().matches("(^14)\\d(8)\\d+"))
                                                || (g.getCodCue().matches("(^14)\\d\\d(020602)\\d+") && !g.getCodCue().matches("(^14)\\d(9)\\d+") && !g.getCodCue().matches("(^14)\\d(8)\\d+"))
                                                || (g.getCodCue().matches("(^14)\\d\\d(1302)\\d+") && !g.getCodCue().matches("(^14)\\d(9)\\d+") && !g.getCodCue().matches("(^14)\\d(8)\\d+"))
                                                || (g.getCodCue().matches("(^14)\\d\\d(130601)\\d+") && !g.getCodCue().matches("(^14)\\d(9)\\d+") && !g.getCodCue().matches("(^14)\\d(8)\\d+"))
                                                || (g.getCodCue().matches("(^14)\\d\\d(1304)\\d+") && !g.getCodCue().matches("(^14)\\d(9)\\d+") && !g.getCodCue().matches("(^14)\\d(8)\\d+"))
                                                || (g.getCodCue().matches("(^14)\\d\\d(0302)\\d+") && !g.getCodCue().matches("(^14)\\d(9)\\d+") && !g.getCodCue().matches("(^14)\\d(8)\\d+"))
                                                || (g.getCodCue().matches("(^14)\\d\\d(030602)\\d+") && !g.getCodCue().matches("(^14)\\d(9)\\d+") && !g.getCodCue().matches("(^14)\\d(8)\\d+"))
                                                || (g.getCodCue().matches("(^14)\\d\\d(030603)\\d+") && !g.getCodCue().matches("(^14)\\d(9)\\d+") && !g.getCodCue().matches("(^14)\\d(8)\\d+"))
                                                || (g.getCodCue().matches("(^14)\\d\\d(030604)\\d+") && !g.getCodCue().matches("(^14)\\d(9)\\d+") && !g.getCodCue().matches("(^14)\\d(8)\\d+"))
                                                || (g.getCodCue().matches("(^14)\\d\\d(030605)\\d+") && !g.getCodCue().matches("(^14)\\d(9)\\d+") && !g.getCodCue().matches("(^14)\\d(8)\\d+"))
                                                || (g.getCodCue().matches("(^14)\\d\\d(030609)\\d+") && !g.getCodCue().matches("(^14)\\d(9)\\d+") && !g.getCodCue().matches("(^14)\\d(8)\\d+"))
                                                || (g.getCodCue().matches("(^14)\\d\\d(039902)\\d+") && !g.getCodCue().matches("(^14)\\d(9)\\d+") && !g.getCodCue().matches("(^14)\\d(8)\\d+"))
                                ).collect(Collectors.toList());

                        double totalSaldos = rccSals.stream().mapToDouble(RccSal::getSaldo).sum();
                        double customSaldos = rccSals.stream().mapToDouble(s -> param1 != null && Arrays.asList(param1.replaceAll("\\{", "").replaceAll("\\}", "").split(",")).contains(s.getCodEmp()) ? s.getSaldo() : 0).sum();

                        if (param2 != null && (customSaldos * 100.0 / totalSaldos) < Integer.parseInt(param2))
                            return false;
                    }
                    break;
                }
                case HardFilter.CREDITOS_MEDIANA_EMPRESA: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    List<RccSynthesized> synthesizedsToProcess = evaluationCacheService.getRccSynthesized(person.getDocumentNumber(), cachedSources)
                            .stream()
                            .filter(r -> r.getCodMes() != null && r.getCodMes().intValue() == preliminaryEvaluation.getRccCodMes())
                            .collect(Collectors.toList());
                    double sumMedianas = synthesizedsToProcess.stream().
                            mapToDouble(s ->
                                    (s.getMedianaArrendamiento() != null ? s.getMedianaArrendamiento() : 0) +
                                            (s.getMedianaLeaseBack() != null ? s.getMedianaLeaseBack() : 0) +
                                            (s.getMedianaLineaCredito() != null ? s.getMedianaLineaCredito() : 0) +
                                            (s.getMedianaPrestamosNoRev() != null ? s.getMedianaPrestamosNoRev() : 0) +
                                            (s.getMedianaPrestamosRev() != null ? s.getMedianaPrestamosRev() : 0) +
                                            (s.getMedianaSaldoTc() != null ? s.getMedianaSaldoTc() : 0) +
                                            (s.getMedianaSobregiros() != null ? s.getMedianaSobregiros() : 0))
                            .sum();
                    if (sumMedianas > 0)
                        return false;
                    break;
                }
                case HardFilter.ENTIDADES_ACREEDORAS: {
                    Integer maxCreditors = param1 != null ? Integer.parseInt(param1) : null;
                    if (maxCreditors != null) {
                        Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                        List<RccSynthesized> synthesizedsToProcess = evaluationCacheService.getRccSynthesized(person.getDocumentNumber(), cachedSources)
                                .stream()
                                .filter(r -> r.getCodMes() != null && r.getCodMes().intValue() == preliminaryEvaluation.getRccCodMes())
                                .filter(r -> r.getSaldoVigente() != null && r.getSaldoVigente() > 0)
                                .collect(Collectors.toList());
//                        if (param2 != null) {
//                            List<String> list = Arrays.asList(param2.replaceAll("[\\{\\}]", "").split(","));
//                            if(list != null && !list.isEmpty()){
//                                //Remove codEmp in params
//                                synthesizedsToProcess = synthesizedsToProcess.stream().filter(e -> e.getCodEmp() != null && list.indexOf(e.getCodEmp()) == -1).collect(Collectors.toList());
//                            }
//                        }
                        if (synthesizedsToProcess.size() > maxCreditors)
                            return false;
                    }
                    break;
                }
                case HardFilter.ENTIDADES_REPORTANTES: {
                    Integer maxCreditors = param1 != null ? Integer.parseInt(param1) : null;
                    if (maxCreditors != null) {
                        Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                        List<RccSynthesized> synthesizedsToProcess = evaluationCacheService.getRccSynthesized(person.getDocumentNumber(), cachedSources)
                                .stream()
                                .filter(r -> r.getCodMes() != null && r.getCodMes().intValue() == preliminaryEvaluation.getRccCodMes())
                                .collect(Collectors.toList());
//                        if (param2 != null) {
//                            List<String> list = Arrays.asList(param2.replaceAll("[\\{\\}]", "").split(","));
//                            if(list != null && !list.isEmpty()){
//                                //Remove codEmp in params
//                                synthesizedsToProcess = synthesizedsToProcess.stream().filter(e -> e.getCodEmp() != null && list.indexOf(e.getCodEmp()) == -1).collect(Collectors.toList());
//                            }
//                        }
                        if (synthesizedsToProcess.size() > maxCreditors)
                            return false;
                    }
                    break;
                }
                case HardFilter.FILTROS_DUROS_COMPARTAMOS: {
                    EntityWsResult compartamosResult = securityDao.getEntityResultWS(loanApplication.getId(), EntityWebService.COMPARTAMOS_TRAER_VARIABLES_PREEVALUACION);
                    if (compartamosResult == null || compartamosResult.getResult() == null)
                        return false;
                    VariablePreEvaluacionResponse variablePreEvaluacionResponse = new Gson().fromJson(compartamosResult.getResult().toString(), VariablePreEvaluacionResponse.class);
                    Credito crd = variablePreEvaluacionResponse.getPoCredito();
                    Cliente cli = variablePreEvaluacionResponse.getPoCliente();
                    Solicitud sol = variablePreEvaluacionResponse.getPoSolicitud();
                    if ((crd != null && crd.getCreditosVigentes() != null && crd.getCreditosVigentes() >= 3)
                            || (crd != null && crd.getMaximoAtraso() != null && crd.getMaximoAtraso() >= 8)
                            || (cli != null && cli.getBaseNegativa() != null && cli.getBaseNegativa())
                            || (cli != null && cli.getIndicadorTrabajador() != null && cli.getIndicadorTrabajador())
                            || (crd != null && crd.getCreditosSolven() != null && crd.getCreditosSolven() >= 1)
                            || (cli != null && cli.getPorcentajeParticipacion() != null && cli.getPorcentajeParticipacion() > 50)
                            || (crd != null && crd.getCreditosVigentesConyugue() != null && crd.getCreditosVigentesConyugue() >= 1)
                            || (crd != null && crd.getCreditosFiadorVencidos() != null && crd.getCreditosFiadorVencidos() >= 1)
                            || (crd != null && crd.getCreditosFiadorJudicial() != null && crd.getCreditosFiadorJudicial() >= 1)
                            || (crd != null && crd.getCreditosFiadorCastigado() != null && crd.getCreditosFiadorCastigado() >= 1)
                            || (sol != null && sol.getCreditoTramiteTitular() != null && sol.getCreditoTramiteTitular() >= 1)
                            || (cli != null && cli.getIndicadorVinculados() != null && cli.getIndicadorVinculados()))
                        return false;
                    break;
                }
//            case HardFilter.CLOSED_PLATFORM_BRANDED:{
//                break;
//            }
                case HardFilter.CLIENT_TYPE: { // TODO Verificar bien con Esteban
                    if (preliminaryEvaluation.getEntityId() == Entity.CAJASULLANA) {
                        Integer param2Parsed = param2 != null ? Integer.parseInt(param2) : null; // TODO Consultar si esta bien?
                        EntityProductParams entityProductParams = catalogService.getEntityProductParamById(preliminaryEvaluation.getEntityProductParameterId());
                        EntityWsResult cajaSullanaResult = evaluationCacheService.getCajaSullanaAdmisibilidadWSResult(loanApplication.getId(), cachedSources);
                        if (cajaSullanaResult == null || cajaSullanaResult.getResult() == null)
                            return false;
                        AdmisibilidadResponse admisibilidadResponse = new Gson().fromJson(cajaSullanaResult.getResult().toString(), AdmisibilidadResponse.class);
                        String status = admisibilidadResponse.getStatus();
                        if (status == null)
                            return false;
                        boolean sullanaNewClient = false;
                        if (admisibilidadResponse.getListaModalidadList() != null)
                            sullanaNewClient = admisibilidadResponse.getListaModalidadList()
                                    .stream()
                                    .anyMatch(m -> m.getCodigoClase() == 0);
                        if (sullanaNewClient && status.equals("00"))
                            status = "07";

                        if (param1.equals("N") && (!status.equals("07") || (param2Parsed != null && entityProductParams.getDisbursementType().intValue() != param2Parsed)))
                            return false;
                        else if (param1.equals("R") && (!status.equals("00") || (param2Parsed != null && entityProductParams.getDisbursementType().intValue() != param2Parsed)))
                            return false;
                        else
                            return false;

                    } else if (preliminaryEvaluation.getEntityId() == Entity.COMPARTAMOS) {
                        List<String> creditTypes = new ArrayList<>();
                        if (param1 != null)
                            creditTypes = new Gson().fromJson(param1.replaceAll("\\{", "[").replaceAll("\\}", "]"), new TypeToken<ArrayList<String>>() {
                            }.getType());
                        EntityWsResult compartamosResult = securityDao.getEntityResultWS(loanApplication.getId(), EntityWebService.COMPARTAMOS_TRAER_VARIABLES_PREEVALUACION);
                        if (compartamosResult == null || compartamosResult.getResult() == null)
                            return false;
                        VariablePreEvaluacionResponse variablePreEvaluacionResponse = new Gson().fromJson(compartamosResult.getResult().toString(), VariablePreEvaluacionResponse.class);
                        if (variablePreEvaluacionResponse.getPoCredito() != null && variablePreEvaluacionResponse.getPoCredito().getTipoCredito() != null)
                            if (creditTypes.stream().anyMatch(s -> s.equals(variablePreEvaluacionResponse.getPoCredito().getTipoCredito())))
                                return false;
                    }
                    break;
                }
                case HardFilter.INTERNAL_EXPOSITION: {

                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    RccDate preEvaluationRccDate = evaluationCacheService.getRccDatesSorted(cachedSources).stream().filter(d -> d.getCodMes() == preliminaryEvaluation.getRccCodMes().intValue()).findFirst().orElse(null);
                    List<RccSal> rccSalToProcess = evaluationCacheService.getRccIdeGrouped(person.getDocumentNumber(), cachedSources)
                            .stream()
                            .filter(r -> DateTimeComparator.getDateOnlyInstance().compare(r.getRccIde().getFecRep(), preEvaluationRccDate.getFecRep()) == 0)
                            .map(r -> r.getRccSals())
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());
                    double sumSaldo = 0.0;
                    if (preliminaryEvaluation.getEntity().getRccEntityCode() != null) {
                        sumSaldo = rccSalToProcess.stream()
                                .filter(r -> r.getCodEmp() != null)
                                .mapToDouble(r -> r.getCodEmp().equals(preliminaryEvaluation.getEntity().getRccEntityCode()) ? r.getSaldo() : 0)
                                .sum();
                    }

                    boolean paramValidation;
                    if (param1.equals("<")) {
                        paramValidation = param2 != null && sumSaldo < Double.parseDouble(param2);
                    } else if (param1.equals(">=")) {
                        paramValidation = param2 != null && sumSaldo >= Double.parseDouble(param2);
                    } else {
                        break; // Default pre evaluation doesnt has a param 1
                    }
                    if (paramValidation) {
                        long distinctEmp = rccSalToProcess.stream()
                                .map(r -> r.getCodEmp())
                                .distinct()
                                .count();
                        if (param3 != null && distinctEmp <= Integer.parseInt(param3))
                            return false;
                    } else {
                        return false;
                    }
                    break;
                }
                case HardFilter.DEBTMENT: {
                    RccDate rccDate = evaluationCacheService.getRccDatesSorted(cachedSources)
                            .stream()
                            .filter(d -> preliminaryEvaluation.getRccCodMes().equals(d.getCodMes())).findFirst().orElse(null);

                    if (rccDate != null) {
                        Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                        double saldo = evaluationCacheService.getRccIdeGrouped(person.getDocumentNumber(), cachedSources)
                                .stream()
                                .flatMap(g -> g.getRccSals().stream())
                                .filter(g -> DateTimeComparator.getDateOnlyInstance().compare(g.getFecRep(), rccDate.getFecRep()) == 0)
                                .filter(g -> g.getCodCue().matches("(^14)\\d\\d(04)\\d+") && !g.getCodCue().matches("(^14)\\d(9)\\d+") && !g.getCodCue().matches("(^14)\\d(8)\\d+"))
                                .mapToDouble(RccSal::getSaldo)
                                .sum();

                        if (param1 != null && saldo > Integer.parseInt(param1))
                            return false;
                    }

                    break;
                }
                case HardFilter.VEHICULO_NO_AUTORIZADO_PARA_GARANTIA: {
                    if (loanApplication.getGuaranteedAcceptedVehicle() == null || !loanApplication.getGuaranteedAcceptedVehicle())
                        return false;
                    break;
                }
                case HardFilter.DEUDA_EN_ENTIDAD_RCC: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    RccDate preEvaluationRccDate = evaluationCacheService.getRccDatesSorted(cachedSources).stream().filter(d -> d.getCodMes() == preliminaryEvaluation.getRccCodMes().intValue()).findFirst().orElse(null);
                    List<String> rccSynthesizedCodEmpList = evaluationCacheService.getRccSynthesized(person.getDocumentNumber(), cachedSources)
                            .stream()
                            .filter(r -> DateTimeComparator.getDateOnlyInstance().compare(r.getFecRep(), preEvaluationRccDate.getFecRep()) == 0)
                            .map(RccSynthesized::getCodEmp)
                            .distinct()
                            .collect(Collectors.toList());

                    return !rccSynthesizedCodEmpList.contains(param1);
                }
                case HardFilter.CREDITO_EN_GARANTIA_VIGENTE: {
                    RccDate rccDate = evaluationCacheService.getRccDatesSorted(cachedSources)
                            .stream()
                            .filter(d -> preliminaryEvaluation.getRccCodMes().equals(d.getCodMes())).findFirst().orElse(null);

                    if (rccDate != null) {
                        Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                        double saldo = evaluationCacheService.getRccIdeGrouped(person.getDocumentNumber(), cachedSources)
                                .stream()
                                .flatMap(g -> g.getRccSals().stream())
                                .filter(g -> DateTimeComparator.getDateOnlyInstance().compare(g.getFecRep(), rccDate.getFecRep()) == 0)
                                .filter(g -> g.getCodCue().matches("(^84)\\d(40209)\\d+") || g.getCodCue().matches("(^84)\\d(409)\\d+") || g.getCodCue().matches("(^84)\\d(504)\\d+"))
                                .mapToDouble(RccSal::getSaldo)
                                .sum();

                        if (saldo > 0)
                            return false;
                    }
                    break;
                }
                case HardFilter.EMPLOYER_ID: {
                    if (param1 == null)
                        return false;

                    String[] employeesArray = param1.replaceAll("\\{", "").replaceAll("\\}", "").split(",");
                    ArrayList<Integer> intEmployeesArray = new ArrayList<>();
                    for (String s : employeesArray) {
                        intEmployeesArray.add(Integer.parseInt(s.trim()));
                    }

                    if (!intEmployeesArray.contains(preliminaryEvaluation.getEmployerId()))
                        return false;
                    break;
                }
                case HardFilter.CALIFICACION_EN_ULTIMOS_MESES: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    List<RccSynthesized> rccSynthesizedList = evaluationCacheService.getRccSynthesized(person.getDocumentNumber(), cachedSources);

                    int maxCalificacion;
                    if (param2 == null) {
                        maxCalificacion = rccSynthesizedList
                                .stream()
                                .filter(s -> s.getCalificacion() != null)
                                .mapToInt(RccSynthesized::getCalificacion)
                                .max()
                                .orElse(-1);
                    } else {
                        maxCalificacion = rccSynthesizedList
                                .stream()
                                .limit(Integer.parseInt(param2))
                                .filter(s -> s.getCalificacion() != null)
                                .mapToInt(RccSynthesized::getCalificacion)
                                .max()
                                .orElse(-1);
                    }

                    if (param1 != null && Integer.parseInt(param1) == maxCalificacion)
                        return false;
                    break;
                }
                case HardFilter.LEAD_ACTIVITY_TYPE: {
                    if (param1 == null)
                        return false;
                    LeadLoanApplication leadLoanApplication = loanApplicationDao.getLeadLoanApplicationByLoanApplicationId(loanApplication.getId());

                    String[] activityTypeArray = param1.replaceAll("\\{", "").replaceAll("\\}", "").split(",");
                    ArrayList<Integer> integerActivityTypeArray = new ArrayList<>();
                    for (String s : activityTypeArray) {
                        integerActivityTypeArray.add(Integer.parseInt(s.trim()));
                    }

                    if (leadLoanApplication == null || !integerActivityTypeArray.contains(leadLoanApplication.getActivityTypeId()))
                        return false;
                    break;
                }
                case HardFilter.SITUACION_5:
                case HardFilter.SITUACION_4:
                case HardFilter.SITUACION_3:
                case HardFilter.SITUACION_2:
                case HardFilter.SITUACION_6: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    List<CendeuResult> cendeuResultList = personDao.getCendeuResult(person.getDocumentNumber());
                    List<Integer> cendeuDates;

                    if (param1 == null) {
                        cendeuDates = evaluationCacheService.getCendeuDatesSorted(cachedSources)
                                .stream()
                                .map(CendeuDate::getCodMes)
                                .collect(Collectors.toList());
                    } else {
                        cendeuDates = evaluationCacheService.getCendeuDatesSorted(cachedSources)
                                .stream()
                                .map(CendeuDate::getCodMes)
                                .limit(Integer.parseInt(param1))
                                .collect(Collectors.toList());
                    }

                    BcraResult bcraResult = evaluationCacheService.getBcraFromCache(loanApplication.getPersonId(), cachedSources);

                    List<CendeuResult> filteredCendeuResultList = cendeuResultList
                            .stream()
                            .filter(c -> cendeuDates.contains(c.getCodMes()))
                            .collect(Collectors.toList());

                    if (param2 == null)
                        return true;

                    if (filteredCendeuResultList
                            .stream()
                            .anyMatch(fil -> {
                                double monto = filteredCendeuResultList.stream().mapToDouble(f -> (f.getPrestamos() == null ? 0 : f.getPrestamos()) * 1000.00).sum();
                                double montoSituacion = filteredCendeuResultList.stream()
                                        .mapToDouble(f -> param3 != null && f.getSituacion() == Integer.parseInt(param3) ? f.getPrestamos() * 1000.00 : 0)
                                        .sum();

                                return montoSituacion >= (monto / Double.parseDouble(param2));
                            })) {
                        return false;
                    } else if (bcraResult != null && bcraResult.getHistorialDeudas() != null && bcraResult.getHistorialDeudas().isEmpty()) {
                        return false;
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                        if (bcraResult != null && bcraResult.getHistorialDeudas() != null && !bcraResult.getHistorialDeudas().isEmpty()) {

                            bcraResult.getHistorialDeudas()
                                    .stream()
                                    .flatMap(h -> h.getHistorial().stream())
                                    .forEach(d -> d.setPeriodo("01/" + d.getPeriodo()));

                            List<BcraResult.DeudaBanco.RegistroDeuda> registroDeudaStream;

                            if (param1 == null) {
                                registroDeudaStream = bcraResult.getHistorialDeudas()
                                        .stream()
                                        .flatMap(h -> h.getHistorial().stream())
                                        .sorted((o1, o2) -> {
                                            try {
                                                return DateTimeComparator.getDateOnlyInstance().compare(sdf.parse(o2.getPeriodo()), sdf.parse(o1.getPeriodo()));// DESC
                                            } catch (ParseException e) {
                                                return 0;
                                            }
                                        })
                                        .collect(Collectors.toList());
                            } else {
                                registroDeudaStream = bcraResult.getHistorialDeudas()
                                        .stream()
                                        .flatMap(h -> h.getHistorial().stream())
                                        .sorted((o1, o2) -> {
                                            try {
                                                return DateTimeComparator.getDateOnlyInstance().compare(sdf.parse(o2.getPeriodo()), sdf.parse(o1.getPeriodo()));// DESC
                                            } catch (ParseException e) {
                                                return 0;
                                            }
                                        })
                                        .limit(Integer.parseInt(param1))
                                        .collect(Collectors.toList());
                            }

                            double monto = registroDeudaStream.stream().mapToDouble(r -> r.getMonto() == null ? 0 : Double.parseDouble(r.getMonto().replaceAll(",", ".").replaceAll("-", "0.0"))).sum();
                            double montoSituacion = registroDeudaStream.stream().mapToDouble(r -> param3 == null || param3.equals(r.getSituacion()) ? Double.parseDouble(r.getMonto() != null ? r.getMonto().replaceAll(",", ".").replaceAll("-", "0.0") : "0.0") : 0).sum();

                            return !registroDeudaStream.stream()
                                    .anyMatch(r -> montoSituacion >= (monto / (param2 == null || Integer.parseInt(param2) == 0 ? 1 : Double.parseDouble(param2))) && monto > 0 && montoSituacion > 0);
                        }

                    }

                    break;
                }
                case HardFilter.EXP_ARGENTINA: {
                    BcraResult bcraResult = evaluationCacheService.getBcraFromCache(loanApplication.getPersonId(), cachedSources);

                    if (bcraResult != null && bcraResult.getHistorialDeudas() != null && !bcraResult.getHistorialDeudas().isEmpty()) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.MONTH, -6);
                        cal.set(Calendar.DATE, 1);

                        Stream<BcraResult.DeudaBanco.RegistroDeuda> registroDeudaStream = bcraResult.getHistorialDeudas()
                                .stream()
                                .flatMap(h -> h.getHistorial().stream());

                        bcraResult.getHistorialDeudas()
                                .stream()
                                .flatMap(h -> h.getHistorial().stream())
                                .forEach(d -> d.setPeriodo("01/" + d.getPeriodo()));

                        bcraResult.getHistorialDeudas()
                                .stream()
                                .flatMap(h -> h.getHistorial().stream())
                                .forEach(d -> {
                                    if ("N/A".equals(d.getSituacion().toUpperCase())) {
                                        d.setSituacion(null);
                                    }
                                });

                        bcraResult.getHistorialDeudas()
                                .stream()
                                .flatMap(h -> h.getHistorial().stream())
                                .forEach(d -> {
                                    if ("N/A".equals(d.getMonto().toUpperCase())) {
                                        d.setMonto(null);
                                    }
                                });

                        return bcraResult.getHistorialDeudas()
                                .stream()
                                .flatMap(h -> h.getHistorial().stream())
                                .anyMatch(d -> {
                                    try {
                                        return DateTimeComparator.getDateOnlyInstance().compare(sdf.parse(d.getPeriodo()), cal.getTime()) < 0;
                                    } catch (ParseException e) {
                                        return true;
                                    }
                                });
                    }
                    break;
                }
                case HardFilter.HASTA_X_ENTIDADES_FINANCIADORES: {
                    BcraResult bcraResult = evaluationCacheService.getBcraFromCache(loanApplication.getPersonId(), cachedSources);

                    if (bcraResult != null && bcraResult.getHistorialDeudas() != null && !bcraResult.getHistorialDeudas().isEmpty()) {
                        return bcraResult.getHistorialDeudas()
                                .stream()
                                .map(BcraResult.DeudaBanco::getNombre)
                                .distinct()
                                .count() < Integer.parseInt(param1);
                    }
                    break;
                }
                case HardFilter.DESEMPLEADO: {
                    AnsesResult ansesResult = personDao.getAnsesResult(loanApplication.getPersonId());

                    if (ansesResult != null && ansesResult.getDetails() != null && !ansesResult.getDetails().isEmpty()) {
                        return ansesResult.getDetails()
                                .stream()
                                .noneMatch(a -> a.getText().toLowerCase().contains("no registra declaraciones juradas como trabajador en actividad"));
                    }
                    break;
                }
                case HardFilter.SITUACION_BCRA: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    int maxCodMes = evaluationCacheService.getCendeuDatesSorted(cachedSources)
                            .stream()
                            .mapToInt(c -> c.getCodMes()).max().orElse(0);
                    int maxCendeuSituacion = evaluationCacheService.getCendeuResults(person.getDocumentNumber(), cachedSources)
                            .stream()
                            .filter(c -> c.getCodMes() == maxCodMes)
                            .mapToInt(c -> c.getSituacion())
                            .max().orElse(0);

                    BcraResult bcraResult = evaluationCacheService.getBcraFromCache(loanApplication.getPersonId(), cachedSources);

                    if (maxCendeuSituacion > 1) {
                        return false;
                    } else if (bcraResult != null && (bcraResult.getHistorialDeudas() == null || bcraResult.getHistorialDeudas().isEmpty())) {
                        return false;
                    } else {
                        if (bcraResult != null && bcraResult.getHistorialDeudas() != null && !bcraResult.getHistorialDeudas().isEmpty()) {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                            bcraResult.getHistorialDeudas()
                                    .stream()
                                    .flatMap(h -> h.getHistorial().stream())
                                    .forEach(d -> d.setPeriodo("01/" + d.getPeriodo()));

                            bcraResult.getHistorialDeudas()
                                    .stream()
                                    .flatMap(h -> h.getHistorial().stream())
                                    .filter(d -> d != null && d.getSituacion() != null)
                                    .forEach(d -> d.setSituacion(d.getSituacion().replaceAll("-", "")));

                            Stream<BcraResult.DeudaBanco.RegistroDeuda> registroDeudaStream = bcraResult.getHistorialDeudas()
                                    .stream()
                                    .flatMap(h -> h.getHistorial().stream())
                                    .sorted((o1, o2) -> {
                                        try {
                                            return DateTimeComparator.getDateOnlyInstance().compare(sdf.parse(o2.getPeriodo()), sdf.parse(o1.getPeriodo()));// DESC
                                        } catch (ParseException e) {
                                            return 0;
                                        }
                                    })
                                    .limit(1);

                            return !(registroDeudaStream
                                    .mapToInt(r -> {
                                        if (r.getSituacion() == null || "N/A".equals(r.getSituacion().toUpperCase()) || "".equals(r.getSituacion().toUpperCase())) {
                                            r.setSituacion("1");
                                        }
                                        return Integer.parseInt(r.getSituacion());
                                    })
                                    .max().getAsInt() > 1);
                        }
                    }

                    break;
                }
                case HardFilter.TIPO_PERSONA_CUIT: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    if (param1 != null) {
                        return Arrays.asList(param1.replaceAll("[\\{\\}]", "").split(",")).contains(person.getDocumentNumber().substring(0, 2));
                    }
                    return false;
                }
                case HardFilter.SIN_ASIGNACION_UNIVERSAL_POR_HIJO: {
                    AnsesResult ansesResult = personDao.getAnsesResult(loanApplication.getPersonId());

                    if (ansesResult != null && ansesResult.getDetails() != null && !ansesResult.getDetails().isEmpty()) {
                        if (ansesResult.getDetails().stream().anyMatch(a -> "AUH".equals(a.getKey()))) {
                            return ansesResult.getDetails()
                                    .stream()
                                    .filter(a -> "AUH".equals(a.getKey()))
                                    .anyMatch(a -> a.getText().toLowerCase().startsWith("no"));
                        }
                    }
                    break;
                }
                case HardFilter.SIN_CHEQUS_RECHAZADOS_NO_LEVANTADOS: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    Boolean rejectedChecks = rccDao.getCendeuRejectedChecks(person.getDocumentNumber());
                    if (rejectedChecks != null && rejectedChecks) {
                        return false;
                    }
                    break;
                }
                case HardFilter.MESES_EN_RCC: {
                    Integer lastXMonths = param1 != null ? Integer.parseInt(param1) : null;
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    List<Date> rccDates;
                    if (lastXMonths == null) {
                        rccDates = evaluationCacheService.getRccDatesSorted(cachedSources)
                                .stream()
                                .map(r -> r.getFecRep())
                                .collect(Collectors.toList());
                        lastXMonths = rccDates.size();
                    } else {
                        rccDates = evaluationCacheService.getRccDatesSorted(cachedSources)
                                .stream()
                                .limit(lastXMonths)
                                .map(r -> r.getFecRep())
                                .collect(Collectors.toList());
                    }
                    List<RccIde> rccIdes = evaluationCacheService.getRccIdeGrouped(person.getDocumentNumber(), cachedSources)
                            .stream()
                            .map(g -> g.getRccIde())
                            .filter(g -> rccDates.stream().anyMatch(r -> DateTimeComparator.getDateOnlyInstance().compare(g.getFecRep(), r) == 0))
                            .collect(Collectors.toList());
                    if (rccIdes.size() != lastXMonths) {
                        return false;
                    }
                    break;
                }
                case HardFilter.HASTA_X_CALIF_PORCENTAJE: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    // Get the months from 1 to 6
                    List<Date> rccDates = evaluationCacheService.getRccDatesSorted(cachedSources)
                            .stream()
                            .limit(6)
                            .map(r -> r.getFecRep())
                            .collect(Collectors.toList());
                    rccDates.remove(0);
                    // Filter the rccGroupeds by the months
                    List<RccIdeGrouped> rccIdeGroupeds = evaluationCacheService.getRccIdeGrouped(person.getDocumentNumber(), cachedSources)
                            .stream()
                            .filter(g -> g.getRccSals() != null && rccDates.stream().anyMatch(r -> DateTimeComparator.getDateOnlyInstance().compare(g.getRccIde().getFecRep(), r) == 0))
                            .collect(Collectors.toList());

                    // If it have more than 1 CPP in the array, is failed
                    long cppsInTheLastMonths = rccIdeGroupeds.stream()
                            .filter(r -> r.getRccSals().stream().anyMatch(s -> s.getClaDeu() != null && s.getClaDeu().equals("1")))
                            .count();
                    if (cppsInTheLastMonths > 1)
                        return false;

                    // The Saldo in the CPP should be <= than 40% of the total debt of the period (excluding hipotecario y/o vehicular) and <= S/ 8000
                    for (RccIdeGrouped rccIdeGrouped : rccIdeGroupeds) {
                        List<RccSal> rccSals = rccIdeGrouped.getRccSals();
                        // If it has CPP
                        if (rccSals != null && rccSals.stream().anyMatch(s -> s.getClaDeu() != null && s.getClaDeu().equals("1"))) {
                            double cppSaldoOfPeriod = rccSals.stream()
                                    .filter(s -> s.getClaDeu() != null && s.getClaDeu().equals("1"))
                                    .mapToDouble(s -> s.getSaldo())
                                    .sum();
                            // If the CPP saldo is > 8000, is failed
                            if (cppSaldoOfPeriod > 8000) {
                                return false;
                            }
                            double totalDebtOfPeriodWithoutHipotecaVehicular = rccSals.stream()
                                    .filter(s -> !Arrays.asList(  //filter out las cuentas vehicular
                                            "1401030602",
                                            "1404030602",
                                            "1405030602",
                                            "1406030602").contains(s.getCodCue()))
                                    .filter(s -> !(s.getCodCue().matches("(^14)\\d\\d(04)\\d+")  //filter out las cuentas hipotecarias
                                            && !s.getCodCue().matches("(^14)\\d(9)\\d+")
                                            && !s.getCodCue().matches("(^14)\\d(8)\\d+")))
                                    .mapToDouble(s -> s.getSaldo())
                                    .sum();
                            double cppPercentageOfTheDebt = (cppSaldoOfPeriod * 100) / totalDebtOfPeriodWithoutHipotecaVehicular;
                            if (cppPercentageOfTheDebt > 40)
                                return false;
                        }
                    }
                    break;
                }
                case HardFilter.MIN_X_DAYS_FOR_NEW_CREDIT: {
                    List<Credit> credits = evaluationCacheService.getCreditsByPerson(loanApplication.getPersonId(), cachedSources);

                    if (!credits.isEmpty()) {
                        Date lastCreditDisbursementDate = credits.stream()
                                .filter(c -> c.getDisbursedOnEntityDate() != null && c.getCancellationOnEntityDate() == null && preliminaryEvaluation.getEntityId().equals(c.getEntity().getId()))
                                .map(Credit::getDisbursedOnEntityDate)
                                .max(Date::compareTo)
                                .orElse(null);
                        if (lastCreditDisbursementDate != null) {
                            long minDays = Long.parseLong(param1);
                            long days = utilService.daysBetween(lastCreditDisbursementDate, new Date());

                            if (days <= minDays)
                                return false;
                        }
                    }

                    break;
                }
                case HardFilter.BASE_NEGATIVA_BDS: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    List<BDSBase> bdsBases = evaluationCacheService.getBancoDelSolBase(person.getDocumentNumber(), cachedSources);
                    for (BDSBase base : bdsBases) {
                        if ((base.getInhabcc() != null && base.getInhabcc() == 1)
                                || (base.getEntliq() != null && base.getEntliq() == 1)
                                || (base.getMora() != null && base.getMora() == 1))
                            return false;
                    }
                    break;
                }
                case HardFilter.NORMAL_DESDE_DICIEMBRE_3_ANOS: {

                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    int fromMonth = param2 != null ? Integer.parseInt(param2) : 0;
                    int toMonth = param3 != null ? Integer.parseInt(param3) : 0;

                    List<Date> rccDates = evaluationCacheService.getRccDatesSorted(cachedSources)
                            .stream()
                            .map(r -> r.getFecRep())
                            .skip(fromMonth - 1)
                            .limit(toMonth - (fromMonth - 1))
                            .collect(Collectors.toList());

                    List<RccIdeGrouped> rccIdeFilters = evaluationCacheService.getRccIdeGrouped(person.getDocumentNumber(), cachedSources)
                            .stream()
                            .filter(g -> rccDates.stream().anyMatch(r -> DateTimeComparator.getDateOnlyInstance().compare(g.getRccIde().getFecRep(), r) == 0))
                            .collect(Collectors.toList());

                    double sumOfCalifications = 0;
                    if (param1.equalsIgnoreCase("NOR")) {
                        sumOfCalifications = rccIdeFilters.stream().mapToDouble(g -> (g.getRccIde().getPorCal1() != null ? g.getRccIde().getPorCal1() : 0.0) +
                                        (g.getRccIde().getPorCal2() != null ? g.getRccIde().getPorCal2() : 0.0) +
                                        (g.getRccIde().getPorCal3() != null ? g.getRccIde().getPorCal3() : 0.0) +
                                        (g.getRccIde().getPorCal4() != null ? g.getRccIde().getPorCal4() : 0.0))
                                .sum();
                    } else if (param1.equalsIgnoreCase("CPP")) {
                        sumOfCalifications = rccIdeFilters.stream().mapToDouble(g -> (g.getRccIde().getPorCal2() != null ? g.getRccIde().getPorCal2() : 0.0) +
                                        (g.getRccIde().getPorCal3() != null ? g.getRccIde().getPorCal3() : 0.0) +
                                        (g.getRccIde().getPorCal4() != null ? g.getRccIde().getPorCal4() : 0.0))
                                .sum();
                    }

                    if (sumOfCalifications > 0)
                        return false;

                    break;
                }
                case HardFilter.CANTIDAD_REPORTES: {

                    int minReports = param1 != null ? Integer.parseInt(param1) : 0;
                    int fromMonth = param2 != null ? Integer.parseInt(param2) : 0;
                    List<String> tipoDocs = param3 == null ? new ArrayList<>() : Arrays.asList(param3.replaceAll("\\{", "").replaceAll("\\}", "").split(","));
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);

                    if (!tipoDocs.isEmpty()) {
                        if (tipoDocs.stream().noneMatch(d -> d.equalsIgnoreCase(person.getDocumentType().getName())))
                            return true;
                    }

                    List<Date> rccDates = evaluationCacheService.getRccDatesSorted(cachedSources)
                            .stream()
                            .map(r -> r.getFecRep())
                            .limit(fromMonth)
                            .collect(Collectors.toList());

                    List<RccIdeGrouped> rccIdeFilters = evaluationCacheService.getRccIdeGrouped(person.getDocumentNumber(), cachedSources)
                            .stream()
                            .filter(g -> rccDates.stream().anyMatch(r -> DateTimeComparator.getDateOnlyInstance().compare(g.getRccIde().getFecRep(), r) == 0))
                            .collect(Collectors.toList());

                    if (rccIdeFilters.size() < minReports)
                        return false;

                    break;
                }
                case HardFilter.MIN_PERCENTAGE_SIMILARITY_FACES: {
                    double minSimilarityPercentage = Double.parseDouble(param1);

                    List<String> fileTypes = getListParams(param2);
                    Tuple2<Integer, Integer> tupleFileTypes = new Tuple2<>(Integer.parseInt(fileTypes.get(0)), Integer.parseInt(fileTypes.get(1)));

                    double similarityResult = evaluationCacheService.getPercentageSimilarityFacesFromCache(loanApplication, tupleFileTypes, cachedSources);

                    if (similarityResult < minSimilarityPercentage)
                        return false;

                    break;
                }
                case HardFilter.REQUIRED_TEXT_IN_IMAGE: {
                    List<String> requieredTexts = getListParams(param1);

                    if (!requieredTexts.isEmpty()) {
                        Integer fileTypeId = Integer.parseInt(param2);

                        List<String> detectedTexts = evaluationCacheService.getTextsInImageFromCache(loanApplication, fileTypeId, cachedSources);

                        boolean requiredTextPresent = requieredTexts
                                .stream()
                                .allMatch(e -> detectedTexts
                                        .stream()
                                        .anyMatch(x -> x.contains(e))
                                );

                        if (!requiredTextPresent)
                            return false;
                    }

                    break;
                }
                case HardFilter.ACCEPTED_DOCUMENT_TYPES: {
                    List<String> acceptedDocumentTypes = getListParams(param1);

                    if (!acceptedDocumentTypes.isEmpty()) {

                        Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);

                        if (!acceptedDocumentTypes.contains(person.getDocumentType().getId().toString()))
                            return false;
                    }

                    break;
                }
                case HardFilter.DEVICE_DUPE_IOVATION: {
                    JSONArray iovationResults = evaluationCacheService.getIovationResultByPerson(loanApplication.getPersonId(), cachedSources);

                    if (iovationResults != null && iovationResults.length() > 1) {
                        int totalMobileDevices = param1 != null ? Integer.parseInt(param1) : 0;
                        int totalDesktopDevices = param2 != null ? Integer.parseInt(param2) : 0;

                        List<JSONObject> jsonList = StreamSupport
                                .stream(iovationResults.spliterator(), false)
                                .map(j -> (JSONObject) j)
                                .flatMap(j -> StreamSupport.stream(j.getJSONObject("js_iovation").getJSONArray("details").spliterator(), false))
                                .map(j -> (JSONObject) j)
                                .filter(j -> j.getString("name").equalsIgnoreCase("device.alias") || j.getString("name").equalsIgnoreCase("device.type"))
                                .collect(Collectors.toList());

                        HashMap<String, List<String>> aliasType = new HashMap<>();

                        for (int i = 0; i < jsonList.size(); i++) {
                            String value = jsonList.get(i).getString("value");
                            i++;
                            String key = jsonList.get(i).getString("value").toLowerCase();

                            List<String> values = aliasType.getOrDefault(key, new ArrayList<>());
                            values.add(value);

                            aliasType.put(key, values);
                        }

                        boolean moreThanOneMobile = true;
                        boolean moreThanTwoDesktop = true;
                        List<String> desktops = new ArrayList<>();

                        List<String> mobiles = new ArrayList<>(aliasType.getOrDefault("android", new ArrayList<>()));
                        mobiles.addAll(aliasType.getOrDefault("iphone", new ArrayList<>()));
                        mobiles = mobiles.stream().distinct().collect(Collectors.toList());

                        if (mobiles.isEmpty() || mobiles.size() == totalMobileDevices) {
                            moreThanOneMobile = false;
                        }

                        aliasType.remove("android");
                        aliasType.remove("iphone");

                        for (String key : aliasType.keySet()) {
                            desktops.addAll(aliasType.get(key));
                        }

                        desktops = desktops.stream().distinct().collect(Collectors.toList());

                        if (desktops.isEmpty() || desktops.size() < totalDesktopDevices) {
                            moreThanTwoDesktop = false;
                        }

                        return moreThanOneMobile || moreThanTwoDesktop;
                    }
                }
                case HardFilter.MESES_CON_MIN_DEUDA: {
                    double minSaldo = Double.parseDouble(param1);
                    int monthTo = Integer.parseInt(param2);
                    long minMonths = Long.parseLong(param3);
                    int monthFrom = 1;

                    List<RccSynthesizedExtraFields> synthesizeds = getSynthesizedsInRange(loanApplication.getPersonId(), monthFrom, monthTo, cachedSources);

                    Map<Integer, List<RccSynthesizedExtraFields>> synthesizedsPerPerPeriod = synthesizeds
                            .stream()
                            .collect(Collectors.groupingBy(RccSynthesized::getCodMes));

                    int monthsWithMinSaldo = 0;
                    for (Map.Entry<Integer, List<RccSynthesizedExtraFields>> entry : synthesizedsPerPerPeriod.entrySet()) {
                        if (minSaldo < entry.getValue().stream().mapToDouble(this::sumAllSaldos).sum()) {
                            monthsWithMinSaldo++;
                        }
                    }

                    if (monthsWithMinSaldo < minMonths) {
                        return false;
                    }
                    break;
                }
                case HardFilter.MIN_LINEA_CREDITO_RCC: {
                    double minAmount = Double.parseDouble(param1);
                    int monthFrom = Integer.parseInt(param2);
                    int monthTo = Integer.parseInt(param3);

                    List<RccSynthesizedExtraFields> synthesizeds = getSynthesizedsInRange(loanApplication.getPersonId(), monthFrom, monthTo, cachedSources);

                    Map<Integer, List<RccSynthesizedExtraFields>> synthesizedsPerPerPeriod = synthesizeds
                            .stream()
                            .collect(Collectors.groupingBy(RccSynthesized::getCodMes));

                    for (Map.Entry<Integer, List<RccSynthesizedExtraFields>> entry : synthesizedsPerPerPeriod.entrySet()) {
                        if (minAmount >= entry.getValue().stream().mapToDouble(RccSynthesizedExtraFields::getLineaTc).sum()) {
                            return false;
                        }
                    }
                    break;
                }
                case HardFilter.MIN_DEUDA_DIRECTA_ULT_X_MESES: {
                    double minAmount = Double.parseDouble(param1);
                    int monthFrom = Integer.parseInt(param2);
                    int monthTo = Integer.parseInt(param3);

                    List<RccSynthesizedExtraFields> synthesizeds = getSynthesizedsInRange(loanApplication.getPersonId(), monthFrom, monthTo, cachedSources);

                    Map<Integer, List<RccSynthesizedExtraFields>> synthesizedsPerPerPeriod = synthesizeds
                            .stream()
                            .collect(Collectors.groupingBy(RccSynthesized::getCodMes));

                    boolean atLeastOneValid = false;
                    for (Map.Entry<Integer, List<RccSynthesizedExtraFields>> entry : synthesizedsPerPerPeriod.entrySet()) {
                        if (minAmount < entry.getValue().stream().mapToDouble(this::sumAllSaldos).sum()) {
                            atLeastOneValid = true;
                            break;
                        }
                    }

                    return atLeastOneValid;
                }
                case HardFilter.MAX_SALDO_DEUDA_RCC: {
                    List<String> fields = getListParams(param1);
                    double maxAmount = Double.parseDouble(param2);
                    int monthTo = Integer.parseInt(param3);
                    int monthFrom = 1;

                    List<RccSynthesizedExtraFields> synthesizeds = getSynthesizedsInRange(loanApplication.getPersonId(), monthFrom, monthTo, cachedSources);

                    Map<Integer, List<RccSynthesizedExtraFields>> synthesizedsPerPerPeriod = synthesizeds
                            .stream()
                            .collect(Collectors.groupingBy(RccSynthesized::getCodMes));

                    for (Map.Entry<Integer, List<RccSynthesizedExtraFields>> entry : synthesizedsPerPerPeriod.entrySet()) {
                        double sumFields = 0;
                        for (RccSynthesizedExtraFields synthesized : entry.getValue()) {
                            JSONObject jsonObject = new JSONObject(synthesized);
                            sumFields += fields
                                    .stream()
                                    .mapToDouble(e -> JsonUtil.getDoubleFromJson(jsonObject, e, 0.0))
                                    .sum();
                        }

                        if (sumFields > maxAmount) {
                            return false;
                        }
                    }

                    break;
                }
                case HardFilter.NO_ENTITY_CREDITS: {
                    List<String> entityIds = getListParams(param1);
                    List<Integer> possibleStatuses = Arrays.asList(CreditStatus.ACTIVE, CreditStatus.ORIGINATED, CreditStatus.ORIGINATED_DISBURSED, CreditStatus.INACTIVE_W_SCHEDULE);
                    List<Credit> credits = creditDao.getCreditsByPerson(loanApplication.getPersonId(), Configuration.getDefaultLocale(), Credit.class);
                    if (credits.stream().anyMatch(c -> entityIds.contains(c.getEntity().getId() + "") && possibleStatuses.contains(c.getStatus().getId()) && c.getCancellationOnEntityDate() == null && new Date().before(c.getFinishDate())))
                        return false;
                    break;
                }
                case HardFilter.BASE_FINANSOL: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    int docType = 0;
                    if (param1.equalsIgnoreCase("dni")) {
                        docType = IdentityDocumentType.DNI;
                    } else if (param1.equalsIgnoreCase("ce")) {
                        docType = IdentityDocumentType.CE;
                    }

                    if (docType == person.getDocumentType().getId()) {
                        if (param2.equalsIgnoreCase("in")) {
                            // Approve only if the document is in the base
                            if (!evaluationCacheService.existsInFinansolPreApprovedBase(person.getDocumentNumber(), cachedSources)) {
                                return false;
                            }
                        } else if (param2.equalsIgnoreCase("out")) {
                            // Approve only if the document is not in the base
                            if (evaluationCacheService.existsInFinansolPreApprovedBase(person.getDocumentNumber(), cachedSources)) {
                                return false;
                            }
                        }
                    }
                    break;
                }
                case HardFilter.BCRA_BDS: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    List<CendeuUlt24Result> cendeuResults = evaluationCacheService.getCendeu24Results(person.getDocumentNumber(), cachedSources);
                    if (cendeuResults != null) {
                        int maxSituacion = cendeuResults.stream()
                                .filter(c -> c.getSituacion1() != null)
                                .mapToInt(c -> c.getSituacion1())
                                .max().orElse(0);
                        if (maxSituacion > 1) {
                            double sumaDeudaSituacion2 = cendeuResults.stream()
                                    .filter(c -> c.getSituacion1() != null && c.getSituacion1() >= 2)
                                    .mapToDouble(c -> c.getMonto1())
                                    .sum();
                            double sumaDeudaTotal = cendeuResults.stream()
                                    .filter(c -> c.getSituacion1() != null)
                                    .mapToDouble(c -> c.getMonto1())
                                    .sum();
                            if (sumaDeudaSituacion2 / sumaDeudaTotal > 0.1)
                                return false;
                        }
                        List<CendeuRejectedCheck> rejectedChecks = evaluationCacheService.getCendeuRejectedChecksByCuit(person.getDocumentNumber(), cachedSources);
                        if (!rejectedChecks.isEmpty()) {
                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.MONTH, -12);
                            Date date12monthsbefore = cal.getTime();

                            long chequesImpagosUlt12M = rejectedChecks.stream()
                                    .filter(c -> c.getFechaLevantamiento() == null || c.getFechaLevantamiento().before(date12monthsbefore))
                                    .filter(c -> c.getFechaRechazo().after(date12monthsbefore))
                                    .count();
                            if (chequesImpagosUlt12M > 3)
                                return false;
                        }
                    }
                    break;
                }
                case HardFilter.MAX_ENTIDADES_SINTETIZADO: {
                    Integer maxEntities = Integer.parseInt(param1);
                    boolean includePartner = Boolean.parseBoolean(param2);

                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    RccDate preEvaluationRccDate = evaluationCacheService.getRccDatesSorted(cachedSources).stream().filter(d -> d.getCodMes() == preliminaryEvaluation.getRccCodMes().intValue()).findFirst().orElse(null);
                    List<RccSynthesizedExtraFields> rccSynthesized = evaluationCacheService.getRccSynthesizedExtraFields(person.getDocumentNumber(), cachedSources)
                            .stream()
                            .filter(r -> DateTimeComparator.getDateOnlyInstance().compare(r.getFecRep(), preEvaluationRccDate.getFecRep()) == 0)
                            .collect(Collectors.toList());
                    long entitiesCount = rccSynthesized.stream().filter(s -> s.getLineaTc() != null && s.getLineaTc() > 0).map(r -> r.getCodEmp()).distinct().count();
                    if (includePartner && person.getPartner() != null) {
                        List<RccSynthesizedExtraFields> rccPartnerSynthesized = evaluationCacheService.getRccSynthesizedExtraFields(person.getPartner().getDocumentNumber(), cachedSources)
                                .stream()
                                .filter(r -> DateTimeComparator.getDateOnlyInstance().compare(r.getFecRep(), preEvaluationRccDate.getFecRep()) == 0)
                                .collect(Collectors.toList());
                        entitiesCount += rccPartnerSynthesized.stream().filter(s -> s.getLineaTc() != null && s.getLineaTc() > 0).map(r -> r.getCodEmp()).distinct().count();
                    }
                    if (entitiesCount > maxEntities)
                        return false;
                    break;
                }
                case HardFilter.CALIFICACION_RCC_CONYUGUE: {

                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    if (person.getPartner() != null) {
                        int fromMonth = param2 != null ? Integer.parseInt(param2) : 0;
                        int toMonth = param3 != null ? Integer.parseInt(param3) : 0;

                        List<Date> rccDates = evaluationCacheService.getRccDatesSorted(cachedSources)
                                .stream()
                                .map(r -> r.getFecRep())
                                .skip(fromMonth - 1)
                                .limit(toMonth - (fromMonth - 1))
                                .collect(Collectors.toList());

                        List<RccIdeGrouped> rccIdeFilters = evaluationCacheService.getRccIdeGrouped(person.getPartner().getDocumentNumber(), cachedSources)
                                .stream()
                                .filter(g -> rccDates.stream().anyMatch(r -> DateTimeComparator.getDateOnlyInstance().compare(g.getRccIde().getFecRep(), r) == 0))
                                .collect(Collectors.toList());

                        double sumOfCalifications = 0;
                        if (param1.equalsIgnoreCase("NOR")) {
                            sumOfCalifications = rccIdeFilters.stream().mapToDouble(g -> (g.getRccIde().getPorCal1() != null ? g.getRccIde().getPorCal1() : 0.0) +
                                            (g.getRccIde().getPorCal2() != null ? g.getRccIde().getPorCal2() : 0.0) +
                                            (g.getRccIde().getPorCal3() != null ? g.getRccIde().getPorCal3() : 0.0) +
                                            (g.getRccIde().getPorCal4() != null ? g.getRccIde().getPorCal4() : 0.0))
                                    .sum();
                        } else if (param1.equalsIgnoreCase("CPP")) {
                            sumOfCalifications = rccIdeFilters.stream().mapToDouble(g -> (g.getRccIde().getPorCal2() != null ? g.getRccIde().getPorCal2() : 0.0) +
                                            (g.getRccIde().getPorCal3() != null ? g.getRccIde().getPorCal3() : 0.0) +
                                            (g.getRccIde().getPorCal4() != null ? g.getRccIde().getPorCal4() : 0.0))
                                    .sum();
                        }

                        if (sumOfCalifications > 0)
                            return false;
                    }

                    break;
                }
                case HardFilter.TIPO_DOCUMENTO_ACEPTABLES: {
                    List<String> tipoDocsIds = getListParams(param1);
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    if (tipoDocsIds.stream().noneMatch(t -> t.equalsIgnoreCase(person.getDocumentType().getId().toString()))) {
                        return false;
                    }
                    break;
                }
                case HardFilter.BDS_CAMPAIGN: {
                    boolean approveWhenInBase = Boolean.parseBoolean(param1);
                    CampaniaBds campaign = null;
                    if (loanApplication.getEntityCustomData().has(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_CAMPAIGN_ID.getKey())) {
                        campaign = rccDao.getCampaniaBdsById(loanApplication.getEntityCustomData().getInt(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_CAMPAIGN_ID.getKey()));
                    }
                    if (approveWhenInBase && campaign != null)
                        return true;
                    else if (!approveWhenInBase && campaign == null)
                        return true;

                    return false;
                }
                case HardFilter.MAX_SITUACION_ULT_X_MESES_BCRA: {
                    int maxSituacion = Integer.parseInt(param1);
                    int maxCountSituacion = Integer.parseInt(param2);
                    int ultMeses = Integer.parseInt(param3);
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);

                    List<CendeuUlt24Result> cendeuResults = evaluationCacheService.getCendeu24Results(person.getDocumentNumber(), cachedSources);
                    long countSituacion = 0;
                    for (int i = 1; i <= ultMeses; i++) {
                        int index = i;
                        countSituacion += cendeuResults.stream().filter(c -> c.getSituacionOfMonth(index) > maxSituacion).count();
                    }
                    if (countSituacion > maxCountSituacion)
                        return false;

                    break;
                }
                case HardFilter.BASE_BANBIF: {
                    String shouldHaveLine = param1;
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    BanbifPreApprovedBase base = evaluationCacheService.getBanbifPreApprovedBase(person, loanApplication, cachedSources);
                    if (base == null)
                        return false;
                    if (!base.isValid())
                        return false;
                    if (shouldHaveLine.equalsIgnoreCase("true") && (base.getLinea() == null || base.getLinea() <= 0))
                        return false;
                    if (shouldHaveLine.equalsIgnoreCase("false") && (base.getLinea() != null && base.getLinea() > 0))
                        return false;
                    if(param2 != null && param3 != null){
                        List<String> values = getListParams(param3);
                        if (param2.equalsIgnoreCase("in")) {
                            if (values.stream().noneMatch(v -> base.getTipoBase().equalsIgnoreCase(v)))
                                return false;
                        } else if (param2.equalsIgnoreCase("out")) {
                            if (values.stream().anyMatch(v -> base.getTipoBase().equalsIgnoreCase(v)))
                                return false;
                        }
                    }
                    break;
                }
                case HardFilter.BASE_PRISMA: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    if (param1 == null) return false;
                    if (param1.equalsIgnoreCase("in")) {
                        // Approve only if the document is in the base
                        if (!evaluationCacheService.existsInPrismaPreApprovedBase(person.getDocumentNumber(), cachedSources)) {
                            return false;
                        }
                    } else if (param1.equalsIgnoreCase("out")) {
                        // Approve only if the document is not in the base
                        if (evaluationCacheService.existsInPrismaPreApprovedBase(person.getDocumentNumber(), cachedSources)) {
                            return false;
                        }
                    }
                    break;
                }
                case HardFilter.MAX_CALIF_EN_X_MESES_X_REPETICIONES: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    int fromMonth = param2 != null ? Integer.parseInt(param2.split("-")[0]) : 0;
                    int toMonth = param2 != null ? Integer.parseInt(param2.split("-")[1]) : 0;
                    int maxRepetitions = param3 != null ? Integer.parseInt(param3) : 0;

                    List<Date> rccDates = evaluationCacheService.getRccDatesSorted(cachedSources)
                            .stream()
                            .map(RccDate::getFecRep)
                            .skip(fromMonth - 1)
                            .limit(toMonth - (fromMonth - 1))
                            .collect(Collectors.toList());

                    List<RccIdeGrouped> rccIdeFilters = evaluationCacheService.getRccIdeGrouped(person.getDocumentNumber(), cachedSources)
                            .stream()
                            .filter(g -> rccDates.stream().anyMatch(r -> DateTimeComparator.getDateOnlyInstance().compare(g.getRccIde().getFecRep(), r) == 0))
                            .collect(Collectors.toList());

                    double sumOfCalifications = 0;
                    if (param1.equalsIgnoreCase("NOR")) {
                        sumOfCalifications = rccIdeFilters.stream().mapToDouble(g -> (g.getRccIde().getPorCal1() != null ? g.getRccIde().getPorCal1() : 0.0) +
                                        (g.getRccIde().getPorCal2() != null ? g.getRccIde().getPorCal2() : 0.0) +
                                        (g.getRccIde().getPorCal3() != null ? g.getRccIde().getPorCal3() : 0.0) +
                                        (g.getRccIde().getPorCal4() != null ? g.getRccIde().getPorCal4() : 0.0))
                                .sum();
                    } else if (param1.equalsIgnoreCase("CPP")) {
                        sumOfCalifications = rccIdeFilters.stream().mapToDouble(g -> (g.getRccIde().getPorCal2() != null ? g.getRccIde().getPorCal2() : 0.0) +
                                        (g.getRccIde().getPorCal3() != null ? g.getRccIde().getPorCal3() : 0.0) +
                                        (g.getRccIde().getPorCal4() != null ? g.getRccIde().getPorCal4() : 0.0))
                                .sum();
                    }

                    if (sumOfCalifications > 0)
                        return false;

                    if (maxRepetitions > 0) {
                        long repetitions = 0;
                        if (param1.equalsIgnoreCase("CPP")) {

                            Map<Date, List<RccIdeGrouped>> ideGroupedsByPeriod = rccIdeFilters.stream()
                                    .filter(e -> e.getRccIde() != null && e.getRccIde().getFecRep() != null)
                                    .collect(Collectors.groupingBy(e -> e.getRccIde().getFecRep(), HashMap::new, Collectors.toCollection(ArrayList::new)));

                            for (Map.Entry<Date, List<RccIdeGrouped>> entry : ideGroupedsByPeriod.entrySet()) {
                                if (entry.getValue().stream().anyMatch(e -> e.getRccIde().getPorCal1() != null && e.getRccIde().getPorCal1() > 0)) {
                                    repetitions++;
                                }
                            }
                        }

                        if (repetitions > maxRepetitions)
                            return false;
                    }

                    break;
                }
                case HardFilter.ESSALUD_ACTIVE: {
                    if (param1 == null) return false;
                    StaticDBInfo essalud = evaluationCacheService.getStaticDBInfoFromCache(loanApplication.getPersonId(), cachedSources);
                    if (param1.equalsIgnoreCase("true") && essalud == null) return false;
                    if (param1.equalsIgnoreCase("false") && essalud != null) return false;
                    break;
                }
                case HardFilter.BASE_AZTECA: {
                    if (param1 == null) return false;
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    AztecaPreApprovedBase aztecaBase = evaluationCacheService.getAztecaPreApprovedBaseFromCampaignApi(loanApplication.getId(), cachedSources);
                    //AztecaPreApprovedBase aztecaBase = evaluationCacheService.getAztecaPreApprovedBase(person, cachedSources);
                    if (param1.equalsIgnoreCase("in")) {
                        // Approve only if the document is in the base
                        if (aztecaBase == null) {
                            return false;
                        } else {
                            // If it has 2nd param, valiidate the accepted campaigns and groups
                            if (param2 != null) {
                                if (aztecaBase.getIdCampania() == null)
                                    return false;

                                JSONArray campaigns = new JSONArray(param2);
                                boolean accepted = false;
                                for (int i = 0; i < campaigns.length(); i++) {
                                    int campaignId = campaigns.getJSONObject(i).getInt("campaign");
                                    if (aztecaBase.getIdCampania() == campaignId) {
                                        if (campaigns.getJSONObject(i).has("visitType")) {
                                            if (aztecaBase.getTipovisita() != null) {
                                                boolean isInGroups = false;
                                                for (int j = 0; j < campaigns.getJSONObject(i).getJSONArray("visitType").length(); j++) {
                                                    if (campaigns.getJSONObject(i).getJSONArray("visitType").getString(j).equalsIgnoreCase(aztecaBase.getTipovisita()))
                                                        isInGroups = true;
                                                }
                                                if (isInGroups)
                                                    accepted = true;
                                            }
                                        } else {
                                            accepted = true;
                                        }
                                    }
                                }
                                if (!accepted)
                                    return false;
                            }
                            // If it has 3rd param, valiidate the rejected campaigns
                            if (param3 != null) {
                                List<String> rejectedCampaigns = getListParams(param3);
                                if (aztecaBase.getIdCampania() == null || rejectedCampaigns.stream().anyMatch(s -> s.equalsIgnoreCase(String.valueOf(aztecaBase.getIdCampania()))))
                                    return false;
                            }
                            return true;
                        }
                    } else if (param1.equalsIgnoreCase("out")) {
                        // Approve only if the document is not in the base
                        if (aztecaBase != null) {
                            return false;
                        }
                    }
                    break;
                }
                case HardFilter.BDS_EXISTS_IN_CNE: {
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    if (param1 == null) return false;
                    if (param1.equalsIgnoreCase("in")) {
                        // Approve only if the document is in the base
                        if (!evaluationCacheService.existsInBDSCNEBase(person.getDocumentNumber(), cachedSources)) {
                            return false;
                        }
                    } else if (param1.equalsIgnoreCase("out")) {
                        // Approve only if the document is not in the base
                        if (evaluationCacheService.existsInBDSCNEBase(person.getDocumentNumber(), cachedSources)) {
                            return false;
                        }
                    }
                    break;
                }
                case HardFilter.BASE_COBRANZA_AZTECA: {
                    if (param1 == null) return false;
                    Person person = evaluationCacheService.getPersonFromCache(loanApplication.getPersonId(), cachedSources);
                    List<BancoAztecaGatewayApi> aztecaBases = evaluationCacheService.getAztecaCobranzaBases(loanApplication, person, cachedSources);
                    int minHour = Integer.valueOf(AztecaGetawayBase.MIN_HOUR_FOR_PAYMENTS.split(":")[0]);
                    int maxHour = Integer.valueOf(AztecaGetawayBase.MAX_HOUR_FOR_PAYMENTS.split(":")[0]);
                    int minMinuteForMinHour = Integer.valueOf(AztecaGetawayBase.MIN_HOUR_FOR_PAYMENTS.split(":")[1]);
                    Calendar currentCal = Calendar.getInstance(TimeZone.getTimeZone("America/Lima"));
                    int hourOfDay = currentCal.get(Calendar.HOUR_OF_DAY);
                    int minuteOfDay = currentCal.get(Calendar.MINUTE);
                    if (param1.equalsIgnoreCase("in")) {
                        // Approve only if the document is in the base
                        if (aztecaBases == null || aztecaBases.isEmpty()) {
                            return false;
                        }
                        if(hourOfDay < minHour || (hourOfDay == minHour && minuteOfDay < minMinuteForMinHour)) return false;
                    } else if (param1.equalsIgnoreCase("out")) {
                        // Approve only if the document is not in the base
                        if (aztecaBases != null && !aztecaBases.isEmpty()) {
                            if(hourOfDay < minHour || (hourOfDay == minHour && minuteOfDay < minMinuteForMinHour)) return true;
                            return false;
                        }
                    }
                    if (aztecaBases != null && !aztecaBases.isEmpty()) {
                        /*
                        Se cambio debido a la implementacion del WS de campañas de cobranzas, ya no tenemos un id unico en nuestra base de datos
                        List<LoanApplication> collectionLoansApplication = loanApplicationDao.getLoansApplicationByCollectionIds(Configuration.getDefaultLocale(), ProductCategory.GATEWAY, Entity.AZTECA, aztecaBases.stream().map(BancoAztecaCampaniaCobranzaApi::getId).collect(Collectors.toList()));
                        if (collectionLoansApplication != null && !collectionLoansApplication.isEmpty()) {
                            if (collectionLoansApplication.stream().anyMatch(e -> e.getEntityCustomData() != null && JsonUtil.getBooleanFromJson(e.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.GATEWAY_IS_PAID_STATUS.getKey(), false))) {
                                return false;
                            }
                        }*/
                    }
                    break;
                }
                case HardFilter.BANTOTAL_VALIDACION_EMAIL_CELULAR: {
                    User user = evaluationCacheService.getUser(loanApplication.getUserId(), cachedSources);
                    EntityWsResult entityWsResult = evaluationCacheService.getBTPersona(loanApplication.getId(), cachedSources);
                    if (entityWsResult != null) {
                        BTPersonasObtenerResponse responseBTPersonasObtener = new Gson().fromJson(entityWsResult.getResult().toString(), BTPersonasObtenerResponse.class);
                        if (user != null && responseBTPersonasObtener != null) {
                            if (user.getEmail() != null && responseBTPersonasObtener.getSdtPersona().getCorreoElectronico() != null && !responseBTPersonasObtener.getSdtPersona().getCorreoElectronico().trim().isEmpty() && responseBTPersonasObtener.getSdtPersona().getCorreoElectronico().trim().matches(StringFieldValidator.PATTER_REGEX_EMAIL) && !user.getEmail().equalsIgnoreCase(responseBTPersonasObtener.getSdtPersona().getCorreoElectronico()))
                                return false;
                            if (user.getPhoneNumber() != null && responseBTPersonasObtener.getSdtPersona().getTelefonoCelular() != null && !responseBTPersonasObtener.getSdtPersona().getTelefonoCelular().isEmpty() && !user.getPhoneNumber().equalsIgnoreCase(responseBTPersonasObtener.getSdtPersona().getTelefonoCelular()))
                                return false;
                        }
                    }
                    break;
                }
                case HardFilter.BAN_TOTAL_LISTA_NEGRA: {
                    EntityWsResult entityWsResult = evaluationCacheService.getBantotalListasNegras(loanApplication.getId(), cachedSources);
                    if (entityWsResult != null) {
                        BTPersonasValidarEnListasNegrasResponse response = new Gson().fromJson(entityWsResult.getResult().toString(), BTPersonasValidarEnListasNegrasResponse.class);
                        if (response != null && response.getExisteEnLista() != null && response.getExisteEnLista().equals("S")) {
                            return false;
                        }
                    }
                    break;
                }
                case HardFilter.ACCESO_EVALUACION_GENERICA: {
                    EntityWsResult entityWsResult = evaluationCacheService.getAccesoEvaluacionGenerica(loanApplication.getId(), cachedSources);
                    if (entityWsResult != null) {
                        EvaluacionGenericaResponse response = new Gson().fromJson(entityWsResult.getResult().toString(), EvaluacionGenericaResponse.class);
                        if (response != null && response.getEstado() != null && response.getEstado().equals("Rechazado")) {
                            return false;
                        }
                    }
                    break;
                }
                case HardFilter.BANTOTAL_VALIDACION_PRESTAMOS: {
                    BantotalApiData bantotalApiData = loanApplication.getBanTotalApiData();
                    int daysToCompare = Integer.parseInt(param1);
                    boolean validateSameMonth = param2 != null ? Boolean.parseBoolean(param2) : false;
                    if (bantotalApiData != null && bantotalApiData.getBtPrestamos() != null && !bantotalApiData.getBtPrestamos().isEmpty()) {
                        for (BantotalApiData.BantotalPrestamos btPrestamo : bantotalApiData.getBtPrestamos()) {
                            if (btPrestamo.getFechaValor() != null) {
                                Integer daysBetween = Math.toIntExact(TimeUnit.DAYS.convert(Math.abs(new Date().getTime() - btPrestamo.getFechaValor().getTime()), TimeUnit.MILLISECONDS));
                                if (daysBetween <= daysToCompare) return false;
                                if (validateSameMonth) {
                                    SimpleDateFormat fmt = new SimpleDateFormat("yyyyMM");
                                    if (fmt.format(btPrestamo.getFechaValor()).equalsIgnoreCase(fmt.format(loanApplication.getRegisterDate())))
                                        return false;
                                }
                            }
                        }
                    }
                    break;
                }
                case HardFilter.BAN_TOTAL_CLIENT: {
                    boolean isClient = Boolean.parseBoolean(param1);
                    if (loanApplication.getBanTotalApiData() != null) {
                        if (loanApplication.getBanTotalApiData().getClienteUId() != null && isClient) {
                            return true;
                        } else if (loanApplication.getBanTotalApiData().getClienteUId() == null && !isClient) {
                            return true;
                        }
                    }
                    return false;
                }
                case HardFilter.DIGITAL_KEY_SOLVEN: {
                    boolean verifyEmail = Boolean.parseBoolean(param1);
                    boolean verifyPhoneNumber = Boolean.parseBoolean(param2);
                    if ((verifyEmail || verifyPhoneNumber) && loanApplication.getAuxData() == null) return false;
                    User user = userDAO.getUser(loanApplication.getUserId());
                    if (verifyEmail) {
                        if (loanApplication.getAuxData().getRegisteredEmail() == null) return false;
                        if (user.getEmail() != null && !user.getEmail().equalsIgnoreCase(loanApplication.getAuxData().getRegisteredEmail()))
                            return false;
                        else if (userDAO.existsOtherUsersByEmail(loanApplication.getAuxData().getRegisteredEmail(), user.getId()))
                            return false;
                    }
                    if (verifyPhoneNumber) {
                        if (loanApplication.getAuxData().getRegisteredPhoneNumber() == null) return false;
                        if (user.getPhoneNumber() != null && !user.getPhoneNumber().equalsIgnoreCase(loanApplication.getAuxData().getRegisteredPhoneNumber()))
                            return false;
                        else if (userDAO.existsOtherUsersByPhoneNumber(loanApplication.getAuxData().getRegisteredPhoneNumber(), user.getCountryCode(), user.getId()))
                            return false;
                    }
                    break;
                }
                case HardFilter.VALIDATION_PROCESS_APPROVED: {
                    List<Credit> creditsByPersonList = evaluationCacheService.getCreditsByPerson(loanApplication.getPersonId(), cachedSources);

                    if (creditsByPersonList != null && !creditsByPersonList.isEmpty()) {
                        if (creditsByPersonList.stream().anyMatch(e -> e.getProduct() != null && e.getProduct().getProductCategoryId() == ProductCategory.VALIDACION_IDENTIDAD && e.getStatus() != null && e.getStatus().getId() == CreditStatus.ORIGINATED_DISBURSED)) {
                            return false;
                        }
                    }
                    break;
                }
                case HardFilter.ROL_CONSEJERO_BAZ: {
                    EntityWsResult entityWsResult = evaluationCacheService.getAdviserRoleAzteca(loanApplication.getId(), cachedSources);
                    if (entityWsResult != null) {
                        RolConsejero rolConsejero = new Gson().fromJson(entityWsResult.getResult().toString(), RolConsejero.class);
                        if (rolConsejero == null || rolConsejero.getDiagnostico() == null || rolConsejero.getDiagnostico().isEmpty()) {
                            return false;
                        }
                    }
                    break;
                }
                case HardFilter.AZTECA_CAMPANIA_PRODUCT: {
                    if (param1 == null) return false;
                    BancoAztecaCampaniaApi aztecaCampaign = evaluationCacheService.getAztecaCampaign(loanApplication.getId(), cachedSources);

                    if (param1.equalsIgnoreCase("in")) {
                        if (aztecaCampaign == null)
                            return false;
                    } else if (param1.equalsIgnoreCase("out")) {
                        if (aztecaCampaign != null)
                            return false;
                    }
                    boolean aztecaCampaignApproved = false;
                    // Approve only if the product in the campaign api is in the list
                    if (param2 != null) {
                        if (aztecaCampaign != null && aztecaCampaign.getCampo3() != null) {
                            List<String> products = getListParams(param2);
                            if (products.stream().anyMatch(p -> aztecaCampaign.getCampo3().equalsIgnoreCase(p))) aztecaCampaignApproved = true;
                        }
                        else return false;
                    }

                    if (param3 != null && aztecaCampaignApproved) {
                        JSONObject jsonObjectParam3 = new JSONObject(param3);

                        if(jsonObjectParam3.has("Campo8")){
                            List<String> campo8ValidValues = getListParams(jsonObjectParam3.getString("Campo8"));
                            if(aztecaCampaign != null && aztecaCampaign.getCampo8() != null && campo8ValidValues.stream().anyMatch(p -> aztecaCampaign.getCampo8().equalsIgnoreCase(p))) aztecaCampaignApproved = true;
                            else aztecaCampaignApproved = false;
                        }

                        if(jsonObjectParam3.has("Campo9") && aztecaCampaignApproved){
                            String Campo9Operation = "=";
                            if(jsonObjectParam3.optString("Campo9Operation") != null) Campo9Operation = jsonObjectParam3.optString("Campo9Operation");
                            switch (Campo9Operation){
                                case "=":
                                    if(aztecaCampaign != null && aztecaCampaign.getCampo9() != null && aztecaCampaign.getCampo9().equalsIgnoreCase(jsonObjectParam3.getString("Campo9"))) aztecaCampaignApproved = true;
                                    else aztecaCampaignApproved = false;
                                    break;
                                case "!=":
                                    if(aztecaCampaign != null && aztecaCampaign.getCampo9() != null && !aztecaCampaign.getCampo9().equalsIgnoreCase(jsonObjectParam3.getString("Campo9"))) aztecaCampaignApproved = true;
                                    else aztecaCampaignApproved = false;
                                    break;
                            }
                        }
                        return aztecaCampaignApproved;
                    }
                    return aztecaCampaignApproved;
                }
                case HardFilter.MOTIVO_DEL_PRESTAMO: {
                    List<String> reasons = getListParams(param2);
                    String reason = JsonUtil.getStringFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_REASON.getKey(), null);
                    if (param1.equalsIgnoreCase("in")) {
                        // Approve only if the reason is in list
                        if (!reasons.contains(reason)) {
                            return false;
                        }
                    } else if (param1.equalsIgnoreCase("out")) {
                        // Approve only if the reason isnt in list
                        if (reasons.contains(reason)) {
                            return false;
                        }
                    }
                    break;
                }
                case HardFilter.BDS_CANAL_DE_ADQUISICION: {
                    UserEntity userEntity = userDAO.getEntityUserById(loanApplication.getEntityUserId(), Configuration.getDefaultLocale());
                    EntityAcquisitionChannel entityAcquisitionChannel = null;
                    if (userEntity != null && userEntity.getEntityAcquisitionChannelId() != null) {
                        entityAcquisitionChannel = catalogService.getEntityAcquisitionChannelById(userEntity.getEntityAcquisitionChannelId());
                    }
                    List<Integer> acquisitionChannelsId = getListParamsAsInteger(param2);
                    if (param1.equalsIgnoreCase("in")) {
                        // Approve only if the reason is in list
                        if (entityAcquisitionChannel == null || !acquisitionChannelsId.contains(entityAcquisitionChannel.getEntityAcquisitionChannelId())) {
                            return false;
                        }
                    } else if (param1.equalsIgnoreCase("out")) {
                        // Approve only if the reason isnt in list
                        if (entityAcquisitionChannel != null && acquisitionChannelsId.contains(entityAcquisitionChannel.getEntityAcquisitionChannelId())) {
                            return false;
                        }
                    }
                    break;
                }
                case HardFilter.BANTOTAL_DEUDA_VIGENTE: {
                    if (loanApplication.getBanTotalApiData() != null) {
                        if (loanApplication.getBanTotalApiData().getBtPrestamos() != null && !loanApplication.getBanTotalApiData().getBtPrestamos().isEmpty() && loanApplication.getBanTotalApiData().getBtPrestamos().stream().anyMatch(e -> e.getMontoCancTotal() != null)) {
                            return true;
                        }
                    }
                    return false;
                }
                case HardFilter.ALFIN_CLIENTE_PRODUCTO_ACTIVO: {
                    boolean approvedActiveProduct = false;
                    if (loanApplication.getBanTotalApiData() != null) {

                        EntityWsResult obtenerPrestamosClienteWsResult = evaluationCacheService.getBTClientePrestamos(loanApplication.getId(), cachedSources);
                        if (obtenerPrestamosClienteWsResult != null) {
                            ObtenerPrestamosClienteResponse obtenerPrestamosClienteResponse = new Gson().fromJson(obtenerPrestamosClienteWsResult.getResult().toString(), ObtenerPrestamosClienteResponse.class);
                            if (obtenerPrestamosClienteResponse != null && obtenerPrestamosClienteResponse.getSdtProductosPrestamos() != null && obtenerPrestamosClienteResponse.getSdtProductosPrestamos().getsBTProductoPrestamo() != null && !obtenerPrestamosClienteResponse.getSdtProductosPrestamos().getsBTProductoPrestamo().isEmpty()) approvedActiveProduct = true;
                        }

                        if(!approvedActiveProduct){
                            EntityWsResult btClientesObtenerCuentasAhorroWsResult = evaluationCacheService.getBTClienteCuentasAhorro(loanApplication.getId(), cachedSources);
                            if (btClientesObtenerCuentasAhorroWsResult != null) {
                                BTClientesObtenerCuentasAhorroResponse btClientesObtenerCuentasAhorroResponse = new Gson().fromJson(btClientesObtenerCuentasAhorroWsResult.getResult().toString(), BTClientesObtenerCuentasAhorroResponse.class);
                                if (btClientesObtenerCuentasAhorroResponse != null && btClientesObtenerCuentasAhorroResponse.getSdtProductosPasivos() != null && btClientesObtenerCuentasAhorroResponse.getSdtProductosPasivos().getsBTProductoPasivo() != null && !btClientesObtenerCuentasAhorroResponse.getSdtProductosPasivos().getsBTProductoPasivo().isEmpty()) approvedActiveProduct = true;
                            }
                        }

                        if(!approvedActiveProduct){
                            EntityWsResult btClientesObtenerPlazosFijosWsResult = evaluationCacheService.getBTClientePlazoFijo(loanApplication.getId(), cachedSources);
                            if (btClientesObtenerPlazosFijosWsResult != null) {
                                BTClientesObtenerPlazosFijosResponse btClientesObtenerPlazosFijosResponse = new Gson().fromJson(btClientesObtenerPlazosFijosWsResult.getResult().toString(), BTClientesObtenerPlazosFijosResponse.class);
                                if (btClientesObtenerPlazosFijosResponse != null && btClientesObtenerPlazosFijosResponse.getSdtProductosPasivos() != null && btClientesObtenerPlazosFijosResponse.getSdtProductosPasivos().getsBTProductoPasivo() != null && !btClientesObtenerPlazosFijosResponse.getSdtProductosPasivos().getsBTProductoPasivo().isEmpty()) approvedActiveProduct = true;
                            }
                        }

                    }
                    return approvedActiveProduct;
                }
            }
        } catch (Exception ex) {
            throw new Exception("Error in Hard Filter " + hardFilterId + " with loan " + loanApplication.getId() + " and preeva " + preliminaryEvaluation.getId() + " [" + param1 + "," + param2 + "," + param3 + "]", ex);
        }

        return true;
    }

    private List<String> getListParams(String param) {
        return Arrays.asList(param.replaceAll("\\{", "").replaceAll("\\}", "").split(","));
    }

    private List<Integer> getListParamsAsInteger(String param) {
        return param == null ? new ArrayList<>() : Arrays.stream(param.replaceAll("\\{", "").replaceAll("\\}", "").split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

    private double sumAllSaldos(RccSynthesizedExtraFields synthesized) {
        return synthesized.getSaldoVigente() +
                synthesized.getSaldoJudicial() +
                synthesized.getSaldoCastigo() +
                synthesized.getSaldoVencido() +
                synthesized.getSaldoRefinanciado() +
                synthesized.getSaldoReestructurado();
    }

    private List<RccSynthesizedExtraFields> getSynthesizedsInRange(int personId, int monthFrom, int monthTo, Map<String, Object> cachedSources) throws Exception {
        List<Integer> rccDateCodMesToProcess = evaluationCacheService.getRccDatesSorted(cachedSources)
                .stream()
                .skip(monthFrom - 1) // inclusive
                .limit(monthTo - monthFrom + 1) // inclusive
                .map(RccDate::getCodMes)
                .collect(Collectors.toList());

        Person person = evaluationCacheService.getPersonFromCache(personId, cachedSources);
        return evaluationCacheService.getRccSynthesizedExtraFields(person.getDocumentNumber(), cachedSources)
                .stream()
                .filter(s -> rccDateCodMesToProcess.contains(s.getCodMes()))
                .collect(Collectors.toList());
    }
}
