package com.affirm.common.service;

import com.affirm.banbif.service.BanBifService;
import com.affirm.bancoazteca.model.BancoAztecaCampaniaApi;
import com.affirm.bancoazteca.model.BancoAztecaGatewayApi;
import com.affirm.bancoazteca.service.BancoAztecaServiceCall;
import com.affirm.bantotalrest.service.BTApiRestService;
import com.affirm.common.dao.*;
import com.affirm.common.model.*;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.catalog.RateCommissionCluster;
import com.affirm.common.model.transactional.*;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.security.model.EntityWsResult;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.jooq.lambda.tuple.Tuple2;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("evaluationCacheService")
public class EvaluationCacheService {

    @Autowired
    private PersonDAO personDao;
    @Autowired
    private UserDAO userDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private RccDAO rccDao;
    @Autowired
    private CreditDAO creditDao;
    @Autowired
    private BTApiRestService btApiRestService;
    @Autowired
    private SecurityDAO securityDao;
    @Autowired
    private EvaluationDAO evaluationDao;
    @Autowired
    private CatalogDAO catalogDao;
    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private PersonService personService;
    @Autowired
    private RekognitionService rekognitionService;
    @Autowired
    private WebServiceDAO webServiceDAO;
    @Autowired
    private BancoAztecaServiceCall bancoAztecaServiceCall;
    @Autowired
    private BanBifService banBifService;

    public Map<String, Object> initializeCachedSources(Person person) {
        Map<String, Object> cachedSources = new HashMap<>();
        if (person != null) cachedSources.put("person", person);
        return cachedSources;
    }

    public Person getPersonFromCache(int personId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("person"))
            cachedSources.put("person", personDao.getPerson(catalogService, Configuration.getDefaultLocale(), personId, true));
        return (Person) cachedSources.get("person");
    }

    public SunatResult getSunatResultFromCache(int personId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("sunatResult"))
            cachedSources.put("sunatResult", personDao.getSunatResult(personId));
        return (SunatResult) cachedSources.get("sunatResult");
    }

    public RedamResult getRedamResultFromCache(int personId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("redamResult"))
            cachedSources.put("redamResult", personDao.getRedamResult(personId));
        return (RedamResult) cachedSources.get("redamResult");
    }

    public EssaludResult getEssaludResultFromCache(int personId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("essaludResult"))
            cachedSources.put("essaludResult", personDao.getEssaludResult(personId));
        return (EssaludResult) cachedSources.get("essaludResult");
    }

    public AfipResult getAfipResultFromCache(int personId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("afipResult"))
            cachedSources.put("afipResult", personDao.getAfipResult(personId));
        return (AfipResult) cachedSources.get("afipResult");
    }

    public List<SatPlateResult> getSatPlateResultFromCache(int personId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("satPlateResult"))
            cachedSources.put("satPlateResult", personDao.getSatPlateResult(personId));
        return (List<SatPlateResult>) cachedSources.get("satPlateResult");
    }

    public OnpeResult getOnpeResultFromCache(int personId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("onpeResult"))
            cachedSources.put("onpeResult", personDao.getOnpeResult(personId));
        return (OnpeResult) cachedSources.get("onpeResult");
    }

    public BcraResult getBcraResultFromCache(int personId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("bcraResult"))
            cachedSources.put("bcraResult", personDao.getBcraResult(personId));
        return (BcraResult) cachedSources.get("bcraResult");
    }

    public Employee getEmployeeFromCache(int personId, Integer employeerId, Map<String, Object> cachedSources) throws Exception {
        String key = "employee-" + personId + "-" + employeerId;
        if (!cachedSources.containsKey(key))
            cachedSources.put(key, personDao.getEmployeeByPerson(personId, employeerId, Configuration.getDefaultLocale()));
        return (Employee) cachedSources.get(key);
    }

    public StaticDBInfo getStaticDBInfoFromCache(int personId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("staticDb"))
            cachedSources.put("staticDb", personDao.getInfoFromStaticDB(personId));
        return (StaticDBInfo) cachedSources.get("staticDb");
    }

    public List<RccDate> getRccDatesSorted(Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("rccDatesSorted"))
            cachedSources.put("rccDatesSorted", rccDao.getRccDates().stream().sorted(Comparator.comparingInt(RccDate::getCodMes).reversed()).collect(Collectors.toList()));
        return (List<RccDate>) cachedSources.get("rccDatesSorted");
    }

    public List<RccSynthesized> getRccSynthesized(String docNumber, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("rccSynthesized"))
            cachedSources.put("rccSynthesized", rccDao.getRccSynthesized(docNumber));
        return (List<RccSynthesized>) cachedSources.get("rccSynthesized");
    }

    public List<RccSynthesizedExtraFields> getRccSynthesizedExtraFields(String docNumber, Map<String, Object> cachedSources) throws Exception {
        String key = "rccSynthesizedExtraFields-" + docNumber;
        if (!cachedSources.containsKey(key))
            cachedSources.put(key, rccDao.getRccSynthesizedExtraFields(docNumber));
        return (List<RccSynthesizedExtraFields>) cachedSources.get(key);
    }

    public RccScore getRccScore(String docNumber, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("rccyScore"))
            cachedSources.put("rccScore", rccDao.getRccScore(docNumber));
        return (RccScore) cachedSources.get("rccScore");
    }

    public List<RccMonthlyScore> getRccMonthlyScore(String docNumber, Map<String, Object> cachedSources) throws Exception {
        RccScore score = getRccScore(docNumber, cachedSources);
        if (score == null)
            return new ArrayList<>();
        return score.getRccMonthlyScores();
    }

    public List<EntityConsolidableDebt> getRccConsolidabelDebts(int docType, String docNumber, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("rccConsolidableDebts"))
            cachedSources.put("rccConsolidableDebts", personDao.getConsolidableDebts(docType, docNumber, true));
        return (List<EntityConsolidableDebt>) cachedSources.get("rccConsolidableDebts");
    }

    public EntityWsResult getCompartamosVariablesEvalWSResult(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("compartamosVarEvalWSResult"))
            cachedSources.put("compartamosVarEvalWSResult", securityDao.getEntityResultWS(loanApplicationId, EntityWebService.COMPARTAMOS_TRAER_VARIABLES_EVALUACION));
        return (EntityWsResult) cachedSources.get("compartamosVarEvalWSResult");
    }

    public EntityWsResult getCajaSullanaAdmisibilidadWSResult(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("CajaSullanaAdmisibilidadWSResult"))
            cachedSources.put("CajaSullanaAdmisibilidadWSResult", securityDao.getEntityResultWS(loanApplicationId, EntityWebService.CAJASULLANA_ADMISIBILIDAD));
        return (EntityWsResult) cachedSources.get("CajaSullanaAdmisibilidadWSResult");
    }

    public EntityWsResult getCajaSullanaExperianWSResult(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("CajaSullanaExperianWSResult"))
            cachedSources.put("CajaSullanaExperianWSResult", securityDao.getEntityResultWS(loanApplicationId, EntityWebService.CAJASULLANA_EXPERIAN));
        return (EntityWsResult) cachedSources.get("CajaSullanaExperianWSResult");
    }

    public List<RccIdeGrouped> getRccIdeGrouped(String docNumber, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("rccIdeGrouped"))
            cachedSources.put("rccIdeGrouped", rccDao.getRccIdeGrouped(docNumber));
        return (List<RccIdeGrouped>) cachedSources.get("rccIdeGrouped");
    }

    public BcraResult getBcraFromCache(Integer personId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("bcra"))
            cachedSources.put("bcra", personDao.getBcraResult(personId));
        return (BcraResult) cachedSources.get("bcra");
    }

    public List<CendeuResult> getCendeuResults(String docNumber, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("cendeuResults"))
            cachedSources.put("cendeuResults", personDao.getCendeuResult(docNumber));
        return (List<CendeuResult>) cachedSources.get("cendeuResults");
    }

    public List<CendeuUlt24Result> getCendeu24Results(String docNumber, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("getCendeu24Results"))
            cachedSources.put("getCendeu24Results", personDao.getCendeu24Result(docNumber));
        return (List<CendeuUlt24Result>) cachedSources.get("getCendeu24Results");
    }

    public List<CendeuDate> getCendeuDatesSorted(Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("cendeuDates"))
            cachedSources.put("cendeuDates", rccDao.getCendeuDates().stream().sorted(Comparator.comparingInt(CendeuDate::getCodMes).reversed()).collect(Collectors.toList()));
        return (List<CendeuDate>) cachedSources.get("cendeuDates");
    }

    public List<Credit> getCreditsByPerson(int personId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("creditsByPerson"))
            cachedSources.put("creditsByPerson", creditDao.getCreditsByPerson(personId, Configuration.getDefaultLocale(), Credit.class));
        return (List<Credit>) cachedSources.get("creditsByPerson");
    }

    public List<PersonRawAssociated> getPersonRawAssociateds(Integer docType, String docnumber, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("personRawAssociateds"))
            cachedSources.put("personRawAssociateds", personDao.getPersonRawAssociatedByDocument(docType, docnumber));
        return (List<PersonRawAssociated>) cachedSources.get("personRawAssociateds");
    }

    public Boolean getPersonNegativeBase(Integer personId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("personNegativeBase"))
            cachedSources.put("personNegativeBase", personDao.getPersonNegativeBase(personId));
        return (Boolean) cachedSources.get("personNegativeBase");
    }

    public List<BDSBase> getBancoDelSolBase(String docnumber, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("bancoDelSolBase"))
            cachedSources.put("bancoDelSolBase", rccDao.getBancoDelSolBase(docnumber));
        return (List<BDSBase>) cachedSources.get("bancoDelSolBase");
    }

    public List<PersonOcupationalInformation> getOcupationalInformations(int personId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("personOcupationalInformations")) {
            List<PersonOcupationalInformation> ocupations = personDao.getPersonOcupationalInformation(Configuration.getDefaultLocale(), personId);
            cachedSources.put("personOcupationalInformations", ocupations != null ? ocupations : new ArrayList<>());
        }
        return (List<PersonOcupationalInformation>) cachedSources.get("personOcupationalInformations");
    }

    public Double getAdmissionTotalIncome(int loanApplicationId, int entityId, int productId, Map<String, Object> cachedSources) throws Exception {
        String key = "admissionTotalIncome-" + loanApplicationId + "-" + entityId + "-" + productId;
        if (!cachedSources.containsKey(key))
            cachedSources.put(key, evaluationDao.getAdmissionTotalIncome(loanApplicationId, entityId, productId));
        return (Double) cachedSources.get(key);
    }

    public List<EquifaxDeudasHistoricas> getEquifaxDeudasHistoricasPeriodoSorted(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("equifaxDeudasHistoricas"))
            cachedSources.put("equifaxDeudasHistoricas", evaluationDao.getEquifaxDeudasHistoricasByLoanApplicationId(loanApplicationId)
                    .stream().sorted(Comparator.comparing(EquifaxDeudasHistoricas::getPeriodo).reversed()).collect(Collectors.toList()));
        return (List<EquifaxDeudasHistoricas>) cachedSources.get("equifaxDeudasHistoricas");
    }

    public List<RateCommissionCluster> getRateCommissionClusters(Map<String, Object> cachedSources) {
        if (!cachedSources.containsKey("rateCommissionClusters"))
            cachedSources.put("rateCommissionClusters", catalogDao.getRateCommissionClusters());
        return (List<RateCommissionCluster>) cachedSources.get("rateCommissionClusters");
    }

    public Integer getClusterId(Integer entityId, Integer loanApplicationId, Integer entityProductParamId, Map<String, Object> cachedSources) throws Exception {
        String key = "clusterId-" + entityId + "-" + loanApplicationId + "-" + entityProductParamId;
        if (!cachedSources.containsKey(key))
            cachedSources.put(key, evaluationDao.getClusterId(entityId, loanApplicationId, entityProductParamId));
        return (Integer) cachedSources.get(key);
    }

    public Double getMaxInstallment(int loanApplicationId, int entityId, int productId, int entityProductParamId, Map<String, Object> cachedSources) throws Exception {
        String key = "maxInstallment-" + entityId + "-" + productId + "-" + entityProductParamId;
        if (!cachedSources.containsKey(key))
            cachedSources.put(key, evaluationDao.getMaxInstallment(loanApplicationId, entityId, productId, entityProductParamId));
        return (Double) cachedSources.get(key);
    }

    public LoanApplicationReclosure getLoanApplicationReclosure(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("loanApplicationReclosure"))
            cachedSources.put("loanApplicationReclosure", evaluationDao.getLoanApplicationReclosure(loanApplicationId));
        return (LoanApplicationReclosure) cachedSources.get("loanApplicationReclosure");
    }

    public ApplicationEFLAssessment getEFLAssessmentByLoanApplicationId(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("EFLAssessmentByLoanApplicationId"))
            cachedSources.put("EFLAssessmentByLoanApplicationId", evaluationDao.getEFLAssessmentByLoanApplicationId(loanApplicationId));
        return (ApplicationEFLAssessment) cachedSources.get("EFLAssessmentByLoanApplicationId");
    }

    public Double getMonthlyInstallmentTotal(String docNumber, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("monthlyInstallmentTotal"))
            cachedSources.put("monthlyInstallmentTotal", evaluationDao.getMonthlyInstallmentTotal(docNumber));
        return (Double) cachedSources.get("monthlyInstallmentTotal");
    }

    public Address getAddressByPersonId(Integer personId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("addressByPersonId"))
            cachedSources.put("addressByPersonId", evaluationDao.getAddressByPersonId(personId));
        return (Address) cachedSources.get("addressByPersonId");
    }

    public List<ApplicationBureau> getBureauResultsSorted(Integer loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("bureauResultsSorted"))
            cachedSources.put("bureauResultsSorted", loanApplicationDao.getBureauResults(loanApplicationId)
                    .stream().sorted(Comparator.comparing(ApplicationBureau::getRegisterDate).reversed()).collect(Collectors.toList()));
        return (List<ApplicationBureau>) cachedSources.get("bureauResultsSorted");
    }

    public List<ApprovedDataLoanApplication> getApprovedDataLoanApplication(Integer loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("approvedDataLoanApplication"))
            cachedSources.put("approvedDataLoanApplication", evaluationDao.getApprovedDataLoanApplication(loanApplicationId));
        return (List<ApprovedDataLoanApplication>) cachedSources.get("approvedDataLoanApplication");
    }

    public LoanApplication getLoanApplication(Integer loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("loanApplication"))
            cachedSources.put("loanApplication", loanApplicationDao.getLoanApplication(loanApplicationId, Configuration.getDefaultLocale()));
        return (LoanApplication) cachedSources.get("loanApplication");
    }

    public List<LoanApplicationEvaluation> getEvaluationsByLoanApplication(Integer loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("evaluationsByLoanApplication"))
            cachedSources.put("evaluationsByLoanApplication", loanApplicationDao.getEvaluations(loanApplicationId, Configuration.getDefaultLocale()));
        return (List<LoanApplicationEvaluation>) cachedSources.get("evaluationsByLoanApplication");
    }

    public String getRucPersonOccupationalInformation(Integer personId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("rucPersonOccupationalInformation"))
            cachedSources.put("rucPersonOccupationalInformation", personService.getRucPersonOccupationalInformation(personId, Configuration.getDefaultLocale()));
        return (String) cachedSources.get("rucPersonOccupationalInformation");
    }

    public EntityWsResult getFdlmTopazWSResult(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("fdlmTopazWSResult"))
            cachedSources.put("fdlmTopazWSResult", securityDao.getEntityResultWS(loanApplicationId, EntityWebService.FDLM_TOPAZ_EXECUTE));
        return (EntityWsResult) cachedSources.get("fdlmTopazWSResult");
    }

    public EntityWsResult getFdlmDataCreditoDatosClienteWSResult(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("fdlmDataCreditoDatosClienteWSResult"))
            cachedSources.put("fdlmDataCreditoDatosClienteWSResult", securityDao.getEntityResultWS(loanApplicationId, EntityWebService.FDLM_DATACREDITO_DATOS_CLIENTE));
        return (EntityWsResult) cachedSources.get("fdlmDataCreditoDatosClienteWSResult");
    }

    public Double getPercentageSimilarityFacesFromCache(LoanApplication loanApplication, Tuple2<Integer, Integer> filesTypes, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("percentageSimilarityFaces"))
            cachedSources.put("percentageSimilarityFaces", rekognitionService.getPercentageSimilarityFaces(loanApplication, filesTypes));
        return (Double) cachedSources.get("percentageSimilarityFaces");
    }

    public List<String> getTextsInImageFromCache(LoanApplication loanApplication, Integer fileTypeId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("textsInImage"))
            cachedSources.put("textsInImage", rekognitionService.getTextsInImage(loanApplication, fileTypeId));
        return (List<String>) cachedSources.get("textsInImage");
    }

    public EntityWsResult getFdlmListasControlConsultarPersonaWSResult(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("fdlmListasControlConsultarPersonaWSResult"))
            cachedSources.put("fdlmListasControlConsultarPersonaWSResult", securityDao.getEntityResultWS(loanApplicationId, EntityWebService.FDLM_LISTAS_CONTROL_CONSULTAR_PERSONA));
        return (EntityWsResult) cachedSources.get("fdlmListasControlConsultarPersonaWSResult");
    }

    public EntityWsResult getFdlmCreditoConsumoConsultarCreditoWSResult(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("fdlmCreditoConsumoConsultarCreditoWSResult"))
            cachedSources.put("fdlmCreditoConsumoConsultarCreditoWSResult", securityDao.getEntityResultWS(loanApplicationId, EntityWebService.FDLM_CREDITO_CONSUMO_CONSULTAR_CREDITO));
        return (EntityWsResult) cachedSources.get("fdlmCreditoConsumoConsultarCreditoWSResult");
    }

    public EntityWsResult getAccesoScoreLDConsumoWSResult(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("accesoScoreLDConsumoWSResult"))
            cachedSources.put("accesoScoreLDConsumoWSResult", securityDao.getEntityResultWS(loanApplicationId, EntityWebService.ACCESO_SCORE_LD_CONSUMO));
        return (EntityWsResult) cachedSources.get("accesoScoreLDConsumoWSResult");
    }

    public JSONArray getIovationResultByPerson(int personId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("getIovationResultByPerson"))
            cachedSources.put("getIovationResultByPerson", loanApplicationDao.getIovationByPerson(personId));
        return (JSONArray) cachedSources.get("getIovationResultByPerson");
    }

    public PersonOcupationalInformation getPersonOcupationalInformationByPerson(int personId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("personOcupationalInformationByPerson"))
            cachedSources.put("personOcupationalInformationByPerson", personService.getPrincipalOcupationalInormation(personId, Configuration.getDefaultLocale()));
        return (PersonOcupationalInformation) cachedSources.get("personOcupationalInformationByPerson");
    }

    public Boolean existsInFinansolPreApprovedBase(String documentNumber, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("existsFinansolPreApprovedBase"))
            cachedSources.put("existsFinansolPreApprovedBase", rccDao.existsInFinansolPreApprovedBase(documentNumber));
        return (Boolean) cachedSources.get("existsFinansolPreApprovedBase");
    }

    public BanbifPreApprovedBase getBanbifPreApprovedBase(Person person, LoanApplication loanApplication, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("getBanbifPreApprovedBase"))
            cachedSources.put("getBanbifPreApprovedBase", banBifService.getBanbifPreApprovedBase(person.getDocumentType().getName(), person.getDocumentNumber(), loanApplication));
        return (BanbifPreApprovedBase) cachedSources.get("getBanbifPreApprovedBase");
    }

    public EntityWsResult getBanBifResultadoCuestionarioWSResult(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("banBifResultadoCuestionarioWSResult"))
            cachedSources.put("banBifResultadoCuestionarioWSResult", securityDao.getEntityResultWS(loanApplicationId, EntityWebService.BANBIF_RESULTADO_CUESTIONARIO));
        return (EntityWsResult) cachedSources.get("banBifResultadoCuestionarioWSResult");
    }

    public Boolean existsInPrismaPreApprovedBase(String documentNumber, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("existsPrismaPreApprovedBase"))
            cachedSources.put("existsPrismaPreApprovedBase", rccDao.existsInPrismaPreApprovedBase(documentNumber));
        return (Boolean) cachedSources.get("existsPrismaPreApprovedBase");
    }

    public Boolean existsInAztecaPreApprovedBase(String documentNumber, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("existsAztecaPreApprovedBase"))
            cachedSources.put("existsAztecaPreApprovedBase", rccDao.existsInAztecaPreApprovedBase(documentNumber));
        return (Boolean) cachedSources.get("existsAztecaPreApprovedBase");
    }

    public AztecaPreApprovedBase getAztecaPreApprovedBase(Person person, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("getAztecaPreApprovedBase"))
            cachedSources.put("getAztecaPreApprovedBase", rccDao.getAztecaPreApprovedBase(person.getDocumentNumber()));
        return (AztecaPreApprovedBase) cachedSources.get("getAztecaPreApprovedBase");
    }

    public AztecaPreApprovedBase getAztecaPreApprovedBaseFromCampaignApi(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("getAztecaPreApprovedBase")){
            EntityWsResult personCampaignResult = securityDao.getEntityResultWS(loanApplicationId, EntityWebService.BANCO_AZTECA_OBTAIN_PERSON_CAMPAIGNS);
            AztecaPreApprovedBase aztecaPreApprovedBase = null;
            if(personCampaignResult != null && personCampaignResult.getResult() != null){
                BancoAztecaCampaniaApi bancoAztecaCampaniaApiData = new Gson().fromJson(personCampaignResult.getResult().toString(),BancoAztecaCampaniaApi.class);
                aztecaPreApprovedBase = bancoAztecaCampaniaApiData.getAztecaPreApprovedBase();
            }
            cachedSources.put("getAztecaPreApprovedBase", aztecaPreApprovedBase);
        }
        return (AztecaPreApprovedBase) cachedSources.get("getAztecaPreApprovedBase");
    }

    public BancoAztecaCampaniaApi getAztecaCampaign(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("getAztecaCampaign")){
            EntityWsResult personCampaignResult = securityDao.getEntityResultWS(loanApplicationId, EntityWebService.BANCO_AZTECA_OBTAIN_PERSON_CAMPAIGNS);
            if(personCampaignResult != null && personCampaignResult.getResult() != null){
                BancoAztecaCampaniaApi bancoAztecaCampaniaApiData = new Gson().fromJson(personCampaignResult.getResult().toString(),BancoAztecaCampaniaApi.class);
                cachedSources.put("getAztecaCampaign", bancoAztecaCampaniaApiData);
            }
        }
        return (BancoAztecaCampaniaApi) cachedSources.get("getAztecaCampaign");
    }

    public Boolean existsInBDSCNEBase(String documentNumber, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("existsInBDSCNEBase"))
            cachedSources.put("existsInBDSCNEBase", rccDao.existsInBDSCNEBase(documentNumber));
        return (Boolean) cachedSources.get("existsInBDSCNEBase");
    }

    public List<CendeuRejectedCheck> getCendeuRejectedChecksByCuit(String docNumber, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("getCendeuRejectedChecks"))
            cachedSources.put("getCendeuRejectedChecks", rccDao.getCendeuRejectedChecksByCuit(docNumber));
        return (List<CendeuRejectedCheck>) cachedSources.get("getCendeuRejectedChecks");
    }

    public AztecaGetawayBase getAztecaCobranzaBase(Person person, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("getAztecaCobranzaBase"))
            cachedSources.put("getAztecaCobranzaBase", rccDao.getAztecaCobranzaBase(person.getDocumentType().getName(), person.getDocumentNumber()));
        return (AztecaGetawayBase) cachedSources.get("getAztecaCobranzaBase");
    }

    public EntityWsResult getBantotalListasNegras(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("bantotalListasNegras")) cachedSources.put("bantotalListasNegras", securityDao.getEntityResultWS(loanApplicationId, EntityWebService.BANTOTAL_APIREST_VALIDAR_EN_LISTAS_NEGRAS));
        return (EntityWsResult) cachedSources.get("bantotalListasNegras");
    }

    public EntityWsResult getAccesoEvaluacionGenerica(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("accesoEvaluacionGenerica")) cachedSources.put("accesoEvaluacionGenerica", securityDao.getEntityResultWS(loanApplicationId, EntityWebService.ACCESO_EVALUACION_GENERICA));
        return (EntityWsResult) cachedSources.get("accesoEvaluacionGenerica");
    }

    public EntityWsResult getBantotalPEP(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("bantotalPEP")) cachedSources.put("bantotalPEP", securityDao.getEntityResultWS(loanApplicationId, EntityWebService.BANTOTAL_APIREST_OBTENER_PEP));
        return (EntityWsResult) cachedSources.get("bantotalPEP");
    }

    public EntityWsResult getBantotalFATCA(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("bantotalFATCA")) cachedSources.put("bantotalFATCA", securityDao.getEntityResultWS(loanApplicationId, EntityWebService.BANTOTAL_APIREST_OBTENER_FATCA));
        return (EntityWsResult) cachedSources.get("bantotalFATCA");
    }

    public User getUser(int userId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("user")) cachedSources.put("user", userDao.getUser(userId));
        return (User) cachedSources.get("user");
    }

    public EntityWsResult getBTPersona(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("btPersona"))
            cachedSources.put("btPersona", securityDao.getEntityResultWS(loanApplicationId, EntityWebService.BANTOTAL_APIREST_OBTENER_PERSONA));
        return (EntityWsResult) cachedSources.get("btPersona");
    }

    public Boolean isVerifiedEmail(int email_id, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("isVerifiedEmail" + email_id))
            cachedSources.put("isVerifiedEmail" + email_id, userDao.isVerifiedEmail(email_id));
        return (Boolean) cachedSources.get("isVerifiedEmail" + email_id);
    }

    public Boolean isVerifiedPhoneNumber(int phone_number_id, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("isVerifiedPhoneNumber" + phone_number_id))
            cachedSources.put("isVerifiedPhoneNumber" + phone_number_id, userDao.isVerifiedPhoneNumber(phone_number_id));
        return (Boolean) cachedSources.get("isVerifiedPhoneNumber" + phone_number_id);
    }

    public EntityWsResult getAdviserRoleAzteca(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("adviserRoleAzteca")) cachedSources.put("adviserRoleAzteca",securityDao.getEntityResultWS(loanApplicationId, EntityWebService.BANCO_AZTECA_OBTAIN_ADVISER_ROLE));
        return (EntityWsResult) cachedSources.get("adviserRoleAzteca");
    }

    public EntityWsResult getBTClientePlazoFijo(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("btClientePlazoFijo"))
            cachedSources.put("btClientePlazoFijo", securityDao.getEntityResultWS(loanApplicationId, EntityWebService.BANTOTAL_APIREST_OBTENER_PLAZOS_FIJOS));
        return (EntityWsResult) cachedSources.get("btClientePlazoFijo");
    }

    public EntityWsResult getBTClienteCuenta(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("btClienteCuenta"))
            cachedSources.put("btClienteCuenta", securityDao.getEntityResultWS(loanApplicationId, EntityWebService.BANTOTAL_APIREST_OBTENER_CUENTA_CLIENTE));
        return (EntityWsResult) cachedSources.get("btClienteCuenta");
    }

    public EntityWsResult getBTClienteCuentasAhorro(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("btClienteCuentasAhorro"))
            cachedSources.put("btClienteCuentasAhorro", securityDao.getEntityResultWS(loanApplicationId, EntityWebService.BANTOTAL_APIREST_OBTENER_CUENTAS_AHORRO));
        return (EntityWsResult) cachedSources.get("btClienteCuentasAhorro");
    }

    public EntityWsResult getBTClientePrestamos(int loanApplicationId, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("btClientePrestamos"))
            cachedSources.put("btClientePrestamos", securityDao.getEntityResultWS(loanApplicationId, EntityWebService.BANTOTAL_APIREST_OBTENER_PRESTAMOS_CLIENTE));
        return (EntityWsResult) cachedSources.get("btClientePrestamos");
    }

    public List<BancoAztecaGatewayApi> getAztecaCobranzaBases(LoanApplication loanApplication, Person person, Map<String, Object> cachedSources) throws Exception {
        if (!cachedSources.containsKey("getAztecaCobranzaBases"))
            cachedSources.put("getAztecaCobranzaBases", bancoAztecaServiceCall.getPersonRecoveryCampaigns(loanApplication, person, null));
        return (List<BancoAztecaGatewayApi>) cachedSources.get("getAztecaCobranzaBases");
    }
}
