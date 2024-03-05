package com.affirm.common.service;

import com.affirm.bancodelsol.service.impl.BancoDelSolServiceImpl;
import com.affirm.common.dao.CatalogDAO;
import com.affirm.common.dao.EvaluationDAO;
import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.*;
import com.affirm.common.util.JsonUtil;
import com.affirm.nosis.Dato;
import com.affirm.nosis.restApi.NosisRestResult;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service("evaluationBureauService")
public class EvaluationBureauService {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private EvaluationDAO evaluationDao;
    @Autowired
    private EvaluationCacheService evaluationCacheService;
    @Autowired
    private PolicyService policyService;
    @Autowired
    private PersonDAO personDAO;
    @Autowired
    private CatalogDAO catalogDAO;

    public void runEvaluationPreBureau(LoanApplication loanApplication, int productId, int entityId, Map<String, Object> cachedSources) throws Exception {

        RccDate maxRccDate = Collections.max(
                evaluationCacheService.getRccDatesSorted(cachedSources),
                Comparator.comparing(c -> c.getCodMes()));
        List<LoanApplicationEvaluation> evaluations = loanApplicationDao.getEvaluations(loanApplication.getId(), Configuration.getDefaultLocale());

        // Check if the evaluation has alredy run and if it should not be deleted
        LoanApplicationEvaluation evaluationProcessed = evaluations.stream()
                .filter(e -> e.getEntityId().equals(entityId) && e.getProductId().equals(productId))
                .findFirst().orElse(null);
        if (evaluationProcessed != null) {
            // If it's the same rcc mes, or the evaluation is not expired, then keep it
            if (evaluationProcessed.getRccCodMes().equals(maxRccDate.getCodMes()) ||
                    evaluationProcessed.getEvaluationExpirationDate().before(new Date())) {
                return;
            }

            // TODO Delete the evaluation?
        }

        // Check if there is not any bureau result,  so insert a dummy one
        List<ApplicationBureau> applicationBureaus = loanApplicationDao.getBureauResults(loanApplication.getId());
        if(applicationBureaus.isEmpty()){
            loanApplicationDao.registerBureauResult(loanApplication.getId(), null, null, null, null, null);
        }

        // Call query to insert the evaluations and policies
        evaluationDao.startEvaluation(loanApplication.getId(), entityId, productId);

        // Get the policies to run
        List<LoanApplicationEvaluation> evaluationsToProcess = evaluationDao.getEvaluationsWithPolicies(loanApplication.getId())
                .stream()
                .filter(e -> e.getProductId().equals(productId) && e.getEntityId().equals(entityId))
                .collect(Collectors.toList());
        for(LoanApplicationEvaluation evaluation : evaluationsToProcess){
            for(EvaluationPolicy policy : evaluation.getEvaluationPoliciesOrdered(2)){
                // Call evaluate policy for the policy
                boolean policyResult = policyService.runPolicies(
                        loanApplication.getId(), policy.getPolicy().getPolicyId(), policy.getParam1(),
                        policy.getParam2(), policy.getParam3(),
                        evaluation.getEmployerId(),  evaluation.getEntityProductParameterId(), evaluation.getId(), cachedSources);
                if(!policyResult){
                    Calendar exppirationDate = Calendar.getInstance();
                    exppirationDate.add(Calendar.DATE, 30);
                    loanApplicationDao.updateEvaluationStep(evaluation.getId(), policy.getPolicy().getPolicyId(), exppirationDate.getTime());
                    if(loanApplication.getEntityId() == null){
                        evaluation.setRunDefaultEvaluation(true);
                        evaluationDao.updateRunDefaultEvaluation(evaluation.getId(), evaluation.getRunDefaultEvaluation());
                        runDefaultEvaluation(loanApplication, evaluation, evaluation.getEntityProductParameterId(),  cachedSources);
                    }
                    break;
                }
            }

            evaluationDao.updateStep(evaluation.getId(), 2);
        }

        // Update the evaluation status if al the pre evaluation finished and all the evaluation are rejected
        List<LoanApplicationPreliminaryEvaluation> preEvaluations = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), Configuration.getDefaultLocale());
        if(preEvaluations.stream().allMatch(p -> p.getApproved() != null)){
            evaluations = evaluationDao.getEvaluationsWithPolicies(loanApplication.getId());
            if(evaluations.stream().allMatch(e -> e.getApproved() != null && !e.getApproved())){
                EvaluationPolicy evaluationPolicyRejection = evaluations.stream()
                        .filter(e -> e.getPolicy() != null)
                        .map(e -> e.getEvaluationPolicies().stream().filter(ep -> ep.getPolicy().getPolicyId() == e.getPolicy().getPolicyId()).findFirst().orElse(null))
                        .filter(p -> p != null)
                        .sorted(Comparator.comparingInt(p -> p.getEvaluationOrder()))
                        .findFirst().orElse(null);

                loanApplicationDao.updateLoanApplicationStatus(loanApplication.getId(), LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION, null);
                loanApplicationDao.updatePolicyMessage(loanApplication.getId(), evaluationPolicyRejection.getPolicy().getMessage());
            }
        }

    }

    public void runEvaluationBureau(LoanApplication loanApplication, LoanApplicationEvaluation evaluation, Map<String, Object> cachedSources) throws Exception {

        Boolean result = evaluation.getApproved();

        if(evaluation.getApproved() != null && evaluation.getApproved()){
            if(evaluation.getStatus() != null && evaluation.getStatus().equals('F')){
                evaluation.setApproved(false);
                evaluationDao.updateIsApproved(evaluation.getId(), evaluation.getApproved());
                evaluation.setPolicyMessage("bd.preliminaryEvaluation.closedPlatformBranding");
                evaluationDao.updatePolicyMessage(evaluation.getId(), evaluation.getPolicyMessage());
                evaluation.setRunDefaultEvaluation(true);
                evaluationDao.updateRunDefaultEvaluation(evaluation.getId(), evaluation.getRunDefaultEvaluation());

                runDefaultEvaluation(loanApplication, evaluation, null, cachedSources);
                result = false;
            }else{

                // if it alredy has a rejected policy, just run the update queries
                if(evaluation.getPolicy() != null){
                    Calendar exppirationDate = Calendar.getInstance();
                    exppirationDate.add(Calendar.DATE, 30);
                    loanApplicationDao.updateEvaluationStep(evaluation.getId(), evaluation.getPolicy().getPolicyId(), exppirationDate.getTime());
                    evaluation.setRunDefaultEvaluation(true);
                    evaluationDao.updateRunDefaultEvaluation(evaluation.getId(), evaluation.getRunDefaultEvaluation());
                    runDefaultEvaluation(loanApplication, evaluation, evaluation.getEntityProductParameterId(), cachedSources);

                    result = false;
                }else{
                    // Get the policies to run
                    LoanApplicationEvaluation evaluationsWithPolicies = evaluationDao.getEvaluationsWithPolicies(loanApplication.getId())
                            .stream()
                            .filter(e -> e.getId().equals(evaluation.getId()))
                            .findFirst().orElse(null);

                    for(EvaluationPolicy policy : evaluationsWithPolicies.getEvaluationPoliciesOrdered(3)){
                        // Call evaluate policy for the policy
                        boolean policyResult = policyService.runPolicies(
                                loanApplication.getId(), policy.getPolicy().getPolicyId(), policy.getParam1(),
                                policy.getParam2(), policy.getParam3(),
                                evaluation.getEmployerId(),  evaluation.getEntityProductParameterId(), evaluation.getId(), cachedSources);

                        if(!policyResult){
                            Calendar exppirationDate = Calendar.getInstance();
                            exppirationDate.add(Calendar.DATE, 30);
                            loanApplicationDao.updateEvaluationStep(evaluation.getId(), policy.getPolicy().getPolicyId(), exppirationDate.getTime());
                            if(loanApplication.getEntityId() == null){
                                evaluation.setRunDefaultEvaluation(true);
                                evaluationDao.updateRunDefaultEvaluation(evaluation.getId(), evaluation.getRunDefaultEvaluation());
                                runDefaultEvaluation(loanApplication, evaluation, evaluation.getEntityProductParameterId(), cachedSources);
                            }

                            result = false;
                            break;
                        }
                    }
                }
            }
        }

        evaluationDao.updateStep(evaluation.getId(), 3);

        if(result != null && result){
            evaluation.setApproved(true);
            evaluationDao.updateIsApproved(evaluation.getId(), evaluation.getApproved());
        }

        evaluationDao.updateApplicationStatusEvaluation(loanApplication.getId());
    }

    private void runDefaultEvaluation(LoanApplication loanApplication, LoanApplicationEvaluation evaluation,  Integer entityProductParamId, Map<String, Object> cachedSources) throws Exception {

        // If the default evaluation has alredy ran, update this one with the same values
        List<LoanApplicationEvaluation> evaluations = loanApplicationDao.getEvaluations(loanApplication.getId(), Configuration.getDefaultLocale());
        if(evaluations.stream().anyMatch(e -> e.getDefaultEvaluationPolicyId() != null)){
            LoanApplicationEvaluation evaWithDefaultEva = evaluations.stream().filter(p -> p.getDefaultEvaluationPolicyId() != null).findFirst().orElse(null);
            loanApplicationDao.updateEvaluationStep(
                    evaluation.getId(),
                    evaWithDefaultEva.getDefaultEvaluationPolicyId(),
                    evaWithDefaultEva.getEvaluationExpirationDate());
            return;
        }

        int maxStep = evaluations.stream().filter(e -> e.getStep() != null).mapToInt(e -> e.getStep()).max().orElse(0);

        // Get the policies for the default evaluation
        List<DefaultPolicy> policiesToRun = evaluationDao.getDefaultPolicyParameters().stream()
                .filter(p -> (p.getCountryId() == null || p.getCountryId() == loanApplication.getCountryId().intValue()) && p.getPolicy().getStep() <= maxStep)
                .sorted(Comparator.comparingInt(p -> p.getOrderId()))
                .collect(Collectors.toList());


        // Run the policies for the default evaluation.
        // If any fails, update the evaluation with expiration date +30 days and return.
        for (DefaultPolicy policy : policiesToRun) {
            boolean policyResult = policyService.runPolicies(
                    loanApplication.getId(), policy.getPolicyId(), policy.getParameter1(),
                    policy.getParameter2(), policy.getParameter3(),
                    evaluation.getEmployerId(),  entityProductParamId, evaluation.getId(), cachedSources);
            if (!policyResult) {

                Calendar expirationDate = Calendar.getInstance();
                expirationDate.add(Calendar.DATE, 30);
                loanApplicationDao.updateEvaluationStep(
                        evaluation.getId(),
                        policy.getPolicyId(),
                        expirationDate.getTime());
                return;
            }
        }

        // Update the default evaluation policy id
        LoanApplicationEvaluation updatedEvaluation = evaluations.stream().filter(e -> e.getId().equals(evaluation.getId())).findFirst().orElse(null);
        if(updatedEvaluation.getDefaultEvaluationPolicyId() == null){
            evaluation.setDefaultEvaluationPolicyId(updatedEvaluation.getPolicy() != null ? updatedEvaluation.getPolicy().getPolicyId() : null);
            evaluationDao.updateDefaultEvaluationPolicyId(evaluation.getId(), evaluation.getDefaultEvaluationPolicyId());
        }
    }

    public Pair<Boolean, Integer> evaluatePypResponse(int loanApplicationId, Map<String, Object> cachedSources) throws Exception{
        JSONObject pypResponse = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)
                .stream()
                .filter(a -> a.getEquifaxResult() != null && a.getBureauId() == Bureau.PYP)
                .map(ApplicationBureau::getEquifaxResult)
                .map(JSONObject::new).findFirst().orElse(null);
        if(pypResponse == null)
            return Pair.of(false, null);
        JSONObject resultado = pypResponse.getJSONObject("RESULTADO");
        if(resultado == null)
            return Pair.of(false, null);
        // Evaluate fallecido
        String fallecido = resultado.getJSONObject("Existencia_Fisica_Resu").getJSONObject("row").getString("fallecido");
        if(fallecido != null && !"No".equalsIgnoreCase(fallecido))
            return Pair.of(false, Policy.FALLECIDOS);
        // Evaluate Alertas judiciales

        // Evaluate Asignacion Universal por Hijos
        ApplicationBureau applicationBureau = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)
                .stream()
                .filter(a -> a.getBureauId() == Bureau.PYP)
                .findFirst().orElse(null);
        int cantAuhCobra = 0;
        if (JsonUtil.getJsonObjectFromJson(resultado, "Cobra_AUH", null) != null) {
            JSONObject jsonCobraAuh = JsonUtil.getJsonObjectFromJson(resultado, "Cobra_AUH", null);
            if (jsonCobraAuh != null && JsonUtil.getJsonObjectFromJson(jsonCobraAuh, "row", null) != null) {
                JSONObject jsonCobraAuhRow = JsonUtil.getJsonObjectFromJson(jsonCobraAuh, "row", null);
                if (jsonCobraAuhRow != null && JsonUtil.getIntFromJson(jsonCobraAuhRow, "cant_auh_cobra", null) != null) {
                    cantAuhCobra = JsonUtil.getIntFromJson(jsonCobraAuhRow, "cant_auh_cobra", null);
                }
            }
        }
        if (cantAuhCobra > 0)
            return Pair.of(false, Policy.ASIGNACION_UNIVERSAL_POR_HIJO);
        // Evaluate Sin actividad
        String tipo_actividad = "";
        String situacion_laboral_actual = "";
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
        if (tipo_actividad == null || (!tipo_actividad.toLowerCase().contains("monotrib") && !tipo_actividad.toLowerCase().contains("jubilado") && !tipo_actividad.toLowerCase().contains("autonomo"))) {
            if (situacion_laboral_actual == null || !situacion_laboral_actual.toLowerCase().contains("SITUACION: ACTIVO".toLowerCase())) {
                return Pair.of(false, Policy.SIN_ACTIVIDAD_BUREAU);
            }
        }
        // If nothing of this fails, return true
        return Pair.of(true, null);
    }

    public Triple<Boolean, Integer, Boolean> evaluateNosisResponse(int loanApplicationId, Map<String, Object> cachedSources) throws Exception{



        JSONObject nosisResponse = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)
                .stream()
                .filter(a -> a.getEquifaxResult() != null && a.getBureauId() == Bureau.NOSIS_BDS)
                .map(ApplicationBureau::getEquifaxResult)
                .map(JSONObject::new).findFirst().orElse(null);

        ApplicationBureau applicationBureau = evaluationCacheService.getBureauResultsSorted(loanApplicationId, cachedSources)
                .stream()
                .filter(a -> a.getEquifaxResult() != null && a.getBureauId() == Bureau.NOSIS_BDS).findFirst().orElse(null);

        if(nosisResponse == null)
            return Triple.of(false, null, false);
        JSONObject resultado = nosisResponse.optJSONObject("Contenido").optJSONObject("Datos");
        if(resultado == null)
            return Triple.of(false, null, false);
        JSONArray variables = resultado.getJSONArray("Variables");
        if(variables == null)
            return Triple.of(false, null, false);

        List<EntityProductParamPolicyConfiguration> policies = catalogDAO.getEntityProductParamPolicy(Entity.BANCO_DEL_SOL,EntityProductParams.ENT_PROD_PARAM_BANCO_DEL_SOL);


        NosisRestResult nosisResult = new NosisRestResult();
        nosisResult.fillFromJson(nosisResponse);

        // Validate fallecido (54037)
        String fallecidoEsValor = StreamSupport.stream(variables.spliterator(), false)
                .map(j -> ((JSONObject) j))
                .filter(j -> j.getString("Nombre").equalsIgnoreCase("VI_Fallecido_Es"))
                .map(j -> j.getString("Valor"))
                .findFirst()
                .orElse(null);
        if ("Si" .equalsIgnoreCase(fallecidoEsValor)) {
            return Triple.of(false, Policy.FALLECIDOS, false);
        }

        // Validate cheques rechazados

        // Validate alertas judiciales (54031) John
        if (variables != null) {
            int sum = StreamSupport
                    .stream(variables.spliterator(), false)
                    .map(j -> (JSONObject) j)
                    .filter(j ->
                            JsonUtil.getStringFromJson(j, "Nombre", null) != null
                                    && Arrays.asList("CQ_12m_Cant", "CQ_60m_Cant", "PQ_12m_Cant", "PQ_60m_Cant", "JU_12m_Cant", "JU_60m_Cant").contains(JsonUtil.getStringFromJson(j, "Nombre", null)))
                    .mapToInt(j -> JsonUtil.getIntFromJson(j, "Valor", 0))
                    .sum();

            if (sum > 0)
                return Triple.of(false, Policy.ALERTAS_JUDICIALES_QUIEBRA_JUICIOS, false);
        }

        // Validate Actividad como empleado doméstico

        // Validate Ingreso Mínimo (14)  John
        String incomesPolicyParam = policies.stream().filter(e -> e.getPolicy().getPolicyId() == Policy.MINIMUM_INCOMES).map(EntityProductParamPolicyConfiguration::getParameter)
                .map(String::new).findFirst().orElse(null);

        if(incomesPolicyParam != null ){
            double minIncome = Double.parseDouble(incomesPolicyParam);
            Double admissionTotalIncome = evaluationCacheService.getAdmissionTotalIncome(loanApplicationId, Entity.BANCO_DEL_SOL, Product.TRADITIONAL, cachedSources);
            if (admissionTotalIncome < minIncome) {
                return Triple.of(false, Policy.MINIMUM_INCOMES, false);
            }
        }


        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(loanApplicationId,Configuration.getDefaultLocale());

        Person person = personDAO.getPerson(loanApplication.getPersonId(),false, Configuration.getDefaultLocale());


        // Validate Actividad no admitida (54038) Anelsy

//        SIN_ACTIVIDAD_JUBILADO

        String BDS_AUTONOMO = "AUTONOMO";
        String BDS_MONOTRIBUTISTA = "MONOTRIBUTISTA";
        String BDS_JUBILADO = "JUBILADO";
        String BDS_RELACION_DEPENDENCIA = "DEPENDENCIA";
        Set<String> actividades = new HashSet<>();
        Integer empleadorActividad01Cod = -1;
        String param1 = policies.stream().filter(e -> e.getPolicy().getPolicyId() == Policy.SIN_ACTIVIDAD_JUBILADO).map(EntityProductParamPolicyConfiguration::getParameter)
                .map(String::new).findFirst().orElse(null);

        if (variables != null) {
            List<Integer> param1Parsed = param1 == null ? Collections.emptyList() : getListParams(param1).stream().map(Integer::parseInt).collect(Collectors.toList());


            String clientType = loanApplication.getEntityCustomData(LoanApplication.EntityCustomDataKeys.BANCO_DEL_SOL_LOAN_CLIENT_TYPE.getKey());

            List<String> actividadesNosis = StreamSupport
                    .stream(variables.spliterator(), false)
                    .map(j -> (JSONObject) j)
                    .filter(j -> JsonUtil.getStringFromJson(j, "Nombre", null) != null && JsonUtil.getStringFromJson(j, "Valor", null) != null)
                    .filter(j -> Arrays.asList("VI_Jubilado_Es", "VI_Empleado_Es", "VI_Inscrip_Monotributo_Es", "VI_Inscrip_Autonomo_Es").contains(JsonUtil.getStringFromJson(j, "Nombre", null)))
                    .map(j -> "VI_Jubilado_Es" .equals(JsonUtil.getStringFromJson(j, "Nombre", null)) && "si" .equalsIgnoreCase(j.optString("Valor", null)) ? BDS_JUBILADO :
                            "VI_Empleado_Es" .equals(JsonUtil.getStringFromJson(j, "Nombre", null)) && "si" .equalsIgnoreCase(j.optString("Valor", null)) ? BDS_RELACION_DEPENDENCIA :
                                    "VI_Inscrip_Monotributo_Es" .equals(JsonUtil.getStringFromJson(j, "Nombre", null)) && "si" .equalsIgnoreCase(j.optString("Valor", null)) ? BDS_MONOTRIBUTISTA :
                                            "VI_Inscrip_Autonomo_Es" .equals(JsonUtil.getStringFromJson(j, "Nombre", null)) && "si" .equalsIgnoreCase(j.optString("Valor", null)) ? BDS_AUTONOMO : null)
                    .collect(Collectors.toList());

            empleadorActividad01Cod = StreamSupport
                    .stream(variables.spliterator(), false)
                    .map(j -> (JSONObject) j)
                    .filter(j -> JsonUtil.getStringFromJson(j, "Nombre", null) != null && JsonUtil.getStringFromJson(j, "Valor", null) != null)
                    .filter(j -> Arrays.asList("VI_Act01_Cod").contains(JsonUtil.getStringFromJson(j, "Nombre", null)))
                    .map(j -> j.optString("Valor", null))
                    .map(s -> "" .equals(s) ? "-1" : s)
                    .mapToInt(Integer::parseInt)
                    .findFirst()
                    .orElse(-1);

            actividades.addAll(actividadesNosis);

            actividades.remove(null);
            if (actividades.size() == 1 && (actividades.contains(BDS_AUTONOMO) || actividades.contains(BDS_MONOTRIBUTISTA)) || actividades.size() == 2 && (actividades.contains(BDS_AUTONOMO) && actividades.contains(BDS_MONOTRIBUTISTA))) {
                if(!(!BancoDelSolServiceImpl.CLIENT_TYPES.get(1).equalsIgnoreCase(clientType) && !param1Parsed.contains(empleadorActividad01Cod)))  return Triple.of(false,Policy.SIN_ACTIVIDAD_JUBILADO,false);
            }
        }


        // Validate Actividad empleador excluido (54039) Anelsy

//        ACTIVIDAD_DEPENDENCIA__ACTIVIDAD_EMPLEADOR_EXCLUIDO


        param1 = policies.stream().filter(e -> e.getPolicy().getPolicyId() == Policy.ACTIVIDAD_DEPENDENCIA__ACTIVIDAD_EMPLEADOR_EXCLUIDO).map(EntityProductParamPolicyConfiguration::getParameter)
                .map(String::new).findFirst().orElse(null);

        if(variables != null){
            actividades = new HashSet<>();
            empleadorActividad01Cod = -1;

            List<Integer> param1Parsed = param1 == null ? Collections.emptyList() : getListParams(param1).stream().map(Integer::parseInt).collect(Collectors.toList());

            List<String> actividadesNosis = StreamSupport
                    .stream(variables.spliterator(), false)
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
                    .stream(variables.spliterator(), false)
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

            actividades.remove(null);
            
            if (actividades.contains(BDS_RELACION_DEPENDENCIA)) {
                if (param1Parsed.contains(empleadorActividad01Cod)) {
                    if (actividades.contains(BDS_AUTONOMO) || actividades.contains(BDS_MONOTRIBUTISTA)) {
                        if (actividades.contains(BDS_JUBILADO)) {
                            return Triple.of(false, Policy.ACTIVIDAD_DEPENDENCIA__ACTIVIDAD_EMPLEADOR_EXCLUIDO, false);
                        }

                        return Triple.of(false, Policy.ACTIVIDAD_DEPENDENCIA__ACTIVIDAD_EMPLEADOR_EXCLUIDO, false);
                    }

                    return Triple.of(false, Policy.ACTIVIDAD_DEPENDENCIA__ACTIVIDAD_EMPLEADOR_EXCLUIDO, false);
                }
            }
        }



        // Validate Referencias Comerciales (54040) Anelsy

        int vigCant = Integer.parseInt(StreamSupport
                .stream(variables.spliterator(), false)
                .map(j -> (JSONObject) j)
                .filter(j -> j.getString("Nombre").equalsIgnoreCase("RC_Vig_Cant"))
                .findFirst()
                .map(j -> j.getString("Valor").toLowerCase())
                .orElse("0"));
        if (vigCant >= 1)
            return Triple.of(false, Policy.NOSIS_REFERENCIA_COMERCIALES, false);


        // Validate Score nosis (54041) Anelsy


        String breakPointBankedParam = policies.stream().filter(e -> e.getPolicy().getPolicyId() == Policy.NOSIS_NO_BANCARIZADOS).map(EntityProductParamPolicyConfiguration::getParameter)
                .map(String::new).findFirst().orElse(null);

        String breakPointNoBankedParam = policies.stream().filter(e -> e.getPolicy().getPolicyId() == Policy.NOSIS_NO_BANCARIZADOS).map(EntityProductParamPolicyConfiguration::getParameter2)
                .map(String::new).findFirst().orElse(null);


        String bancarizado = StreamSupport
                .stream(variables.spliterator(), false)
                .map(j -> (JSONObject) j)
                .filter(j -> j.getString("Nombre").equalsIgnoreCase("CI_Bancarizado"))
                .findFirst()
                .map(j -> j.getString("Valor").toLowerCase())
                .orElse(null);

        if(breakPointBankedParam != null && !breakPointBankedParam.isEmpty()){
            Integer breakPointBanked = Integer.valueOf(breakPointBankedParam);

            if (bancarizado != null && bancarizado.equalsIgnoreCase("si") && nosisResult.getScore() < breakPointBanked)
                return Triple.of(false, Policy.NOSIS_NO_BANCARIZADOS, false);

        }

        if(breakPointNoBankedParam != null && !breakPointNoBankedParam.isEmpty()){
            Integer breakPointNoBanked = Integer.valueOf(breakPointNoBankedParam);

            if (bancarizado != null && bancarizado.equalsIgnoreCase("no") && nosisResult.getScore() < breakPointNoBanked)
                return Triple.of(false, Policy.NOSIS_NO_BANCARIZADOS, false);

            if (bancarizado != null && bancarizado.equalsIgnoreCase("no") && nosisResult.getScore() >= breakPointNoBanked)
                return Triple.of(true, Policy.NOSIS_NO_BANCARIZADOS, false);

        }




        return Triple.of(true, null, true);
    }

    private List<String> getListParams(String param) {
        return Arrays.asList(param.replaceAll("\\{", "").replaceAll("\\}", "").split(","));
    }
}
