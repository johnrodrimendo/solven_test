package com.affirm.common.service.question;

import com.affirm.common.dao.LoanApplicationDAO;
import com.affirm.common.dao.PersonDAO;
import com.affirm.common.dao.WebServiceDAO;
import com.affirm.common.model.catalog.EntityWebService;
import com.affirm.common.model.form.Question161Form;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.GoToNextQuestionException;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.ResponseEntityException;
import com.affirm.sentinel.rest.BanbifServiceCall;
import com.affirm.sentinel.rest.CrearCuestionarioTitularResponse;
import com.affirm.sentinel.rest.ResultadoEvaluacionTitularRequest;
import com.affirm.sentinel.rest.ResultadoEvaluacionTitularResponse;
import com.affirm.system.configuration.Configuration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.json.JsonException;
import java.util.*;
import java.util.stream.IntStream;

@Service("question161Service")
public class Question161Service extends AbstractQuestionService<Question161Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private WebServiceDAO webServiceDao;
    @Autowired
    private BanbifServiceCall banbifServiceCall;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                List<EntityWebServiceLog> wsResponses = webServiceDao.getEntityWebServiceLog(loanApplication.getId(), EntityWebService.BANBIF_CREAR_CUESTIONARIO);

                CrearCuestionarioTitularResponse cuestionarioResponse = null;
                boolean showErrorMessage = false;
                if (wsResponses != null) {
                    EntityWebServiceLog<CrearCuestionarioTitularResponse> lastResponse = wsResponses.stream()
                            .filter(r -> r.getStatus() == EntityWebServiceLog.STATUS_SUCCESS)
                            .sorted(Comparator.nullsLast(
                                    (e1, e2) -> e2.getFinishDate().compareTo(e1.getFinishDate())))
                            .findFirst()
                            .orElse(null);
                    if (lastResponse != null)
                        cuestionarioResponse = lastResponse.getParsedResponse(CrearCuestionarioTitularResponse.class);
                }
                JSONArray banbifSentinelEvas = JsonUtil.getJsonArrayFromJson(loanApplication.getEntityCustomData(),LoanApplication.EntityCustomDataKeys.BANBIF_SENTINEL_COD_EVALUATIONS.getKey(), new JSONArray());
                if(cuestionarioResponse != null){
                    for(int i =0; i < banbifSentinelEvas.length(); i++){
                        if(JsonUtil.getBooleanFromJson(banbifSentinelEvas.getJSONObject(i), "current", false) && JsonUtil.getStringFromJson(banbifSentinelEvas.getJSONObject(i),"result","").equalsIgnoreCase("DESAPROBADO")){
                            cuestionarioResponse = null;
                            break;
                        }
                    }
                }
                if (cuestionarioResponse == null || !cuestionarioResponse.isValid()) {
                    try {
                        cuestionarioResponse = banbifServiceCall.callCreateQuestions(loanApplication);
                    } catch (Exception ex) {
                        if(!Configuration.hostEnvIsProduction()){
                            cuestionarioResponse = new Gson().fromJson("{\"viso_cuestionario\":{\"CodEvaluacion\":\"7003\",\"FechaHoraInicio\":\"2022-11-09T11:18:03\",\"TiempoEvaluacion\":1240,\"Preguntas\":[{\"CodPregunta\":17,\"DesPregunta\":\"EN LOS ÚLTIMOS 12 MESES, ¿CON CUÁL DE ESTAS ENTIDADES TIENE O HA TENIDO UN BIEN EN GARANTÍA?\",\"Alternativas\":[{\"CodAlternativa\":1,\"DesAlternativa\":\"BANCO FALABELLA\"},{\"CodAlternativa\":2,\"DesAlternativa\":\"FINANCIERA COMPARTAMOS\"},{\"CodAlternativa\":3,\"DesAlternativa\":\"CAJA ICA\"},{\"CodAlternativa\":4,\"DesAlternativa\":\"MITSUI AUTO FINANCE\"},{\"CodAlternativa\":5,\"DesAlternativa\":\"NINGUNA DE LAS ANTERIORES\"}]},{\"CodPregunta\":43,\"DesPregunta\":\"¿HA SIDO CANCELADA ALGUNA CUENTA CORRIENTE EN EL ÚLTIMO AÑO? (CANCELADA POR LA ENTIDAD POR FALTA DE FONDOS)\",\"Alternativas\":[{\"CodAlternativa\":1,\"DesAlternativa\":\"SI\"},{\"CodAlternativa\":2,\"DesAlternativa\":\"NO\"}]},{\"CodPregunta\":40,\"DesPregunta\":\"¿ES O HA SIDO AVAL DE ALGUNA DE ESTAS PERSONAS?\",\"Alternativas\":[{\"CodAlternativa\":1,\"DesAlternativa\":\"MARTICORENA MALLMA DIDEAR ANGEL\"},{\"CodAlternativa\":2,\"DesAlternativa\":\"SANTOS TACZA SORAYA\"},{\"CodAlternativa\":3,\"DesAlternativa\":\"PEREZ MINCHERRENO FELIPE\"},{\"CodAlternativa\":4,\"DesAlternativa\":\"NINGUNA DE LAS ANTERIORES\"}]},{\"CodPregunta\":4,\"DesPregunta\":\"¿USTED TIENE O HA TENIDO RELACIÓN LABORAL CON LA EMPRESA <B>INVESTMENT AND CONSULTING S.A.C.<\\/B> ?\",\"Alternativas\":[{\"CodAlternativa\":1,\"DesAlternativa\":\"SI\"},{\"CodAlternativa\":2,\"DesAlternativa\":\"NO\"}]}]},\"ResulVISO\":0,\"CodigoWS\":\"0\"}", CrearCuestionarioTitularResponse.class);
                        }else{
                            attributes.put("errorMessage", "Has superado el número de intentos por hoy. Ingresa nuevamente mañana.");
                            break;
                        }
                        // If its an errpr of retries, go t next question.
//                        throw new GoToNextQuestionException("DEFAULT", ProcessQuestionSequence.TYPE_SKIPPED);
                    }
                    for(int i =0; i < banbifSentinelEvas.length(); i++){
                        JSONObject banbifSentinelEva = banbifSentinelEvas.getJSONObject(i);
                        String result = JsonUtil.getStringFromJson(banbifSentinelEva, "result", null);
                        if(result != null && result.equalsIgnoreCase("DESAPROBADO")){
                            showErrorMessage = true;
                        }
                        banbifSentinelEva.put("current",false);
                        banbifSentinelEvas.put(i,banbifSentinelEva);
                    }
                    JSONObject banbifSentinelEva = new JSONObject();
                    banbifSentinelEva.put("codEvaluacion", cuestionarioResponse.getViso_cuestionario().getCodEvaluacion());
                    banbifSentinelEva.put("current", true);
                    banbifSentinelEvas.put(banbifSentinelEva);
                    loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANBIF_SENTINEL_COD_EVALUATIONS.getKey(), banbifSentinelEvas);
                    loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                }
                JSONObject data = JsonUtil.getJsonObjectFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey(), null);
                if(data != null){
                    BanbifPreApprovedBase approvedBase = new Gson().fromJson(data.toString(),BanbifPreApprovedBase.class);
                    if(approvedBase != null){
                        if(approvedBase.getPlastico() != null && Arrays.asList(BanbifPreApprovedBase.BANBIF_GOLD_CARD,BanbifPreApprovedBase.BANBIF_CLASSIC_CARD,BanbifPreApprovedBase.BANBIF_INFINITE_CARD,BanbifPreApprovedBase.BANBIF_PLATINUM_CARD,BanbifPreApprovedBase.BANBIF_SIGNATURE_CARD, BanbifPreApprovedBase.BANBIF_MASTERCARD_CARD, BanbifPreApprovedBase.BANBIF_MAS_EFECTIVO_CARD).contains(approvedBase.getPlastico())){
                            attributes.put("cardType", approvedBase.getPlastico());
                            attributes.put("cardTypeName", approvedBase.getPlastico().equalsIgnoreCase(BanbifPreApprovedBase.BANBIF_MAS_EFECTIVO_CARD) ? "Tarjeta de Crédito +Efectivo" : approvedBase.getPlastico());
                        }
                        else {
                            attributes.put("cardType", BanbifPreApprovedBase.BANBIF_CLASSIC_CARD);
                            attributes.put("cardTypeName", approvedBase.getPlastico());
                        }
                    }
                }
                CrearCuestionarioTitularResponse.Pregunta additionalNacionalityQuestion = new CrearCuestionarioTitularResponse.Pregunta();
                additionalNacionalityQuestion.setCodPregunta(-1);
                additionalNacionalityQuestion.setDesPregunta("¿Tiene una nacionalidad adicional a la peruana?".toUpperCase());
                List<CrearCuestionarioTitularResponse.Alternativa> alternativas = new ArrayList<>();
                CrearCuestionarioTitularResponse.Alternativa yesOption = new CrearCuestionarioTitularResponse.Alternativa();
                yesOption.setCodAlternativa(1);
                yesOption.setDesAlternativa("SI");
                alternativas.add(yesOption);
                CrearCuestionarioTitularResponse.Alternativa noOption = new CrearCuestionarioTitularResponse.Alternativa();
                noOption.setCodAlternativa(0);
                noOption.setDesAlternativa("NO");
                alternativas.add(noOption);
                additionalNacionalityQuestion.setAlternativas(alternativas);
                cuestionarioResponse.getViso_cuestionario().getPreguntas().add(additionalNacionalityQuestion);

                Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);

                String personTittle;
                if (person.getGender() == null) {
                    personTittle = "Estimado(a)";
                } else if (person.getGender() == 'M') {
                    personTittle = "Estimado";
                } else {
                    personTittle = "Estimada";
                }

                attributes.put("personFirstName", personDao.getPerson(loanApplication.getPersonId(), false, locale).getName());
                attributes.put("preguntas", cuestionarioResponse.getViso_cuestionario().getPreguntas());
                attributes.put("preguntasJson", new Gson().toJson(cuestionarioResponse.getViso_cuestionario().getPreguntas()));
                attributes.put("showErrorMessage",showErrorMessage);
                attributes.put("personTittle", personTittle);

                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question161Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
//                return "DEFAULT";
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                JSONArray banbifSentinelEvas  = JsonUtil.getJsonArrayFromJson(loanApplication.getEntityCustomData(),LoanApplication.EntityCustomDataKeys.BANBIF_SENTINEL_COD_EVALUATIONS.getKey(), new JSONArray());
                Integer maxRetry = 2;
                Integer countRejected = null;
                for(int i =0; i < banbifSentinelEvas.length(); i++){
                    JSONObject banbifSentinelEva = banbifSentinelEvas.getJSONObject(i);
                    String result = JsonUtil.getStringFromJson(banbifSentinelEva, "result", null);
                    if(result != null){
                        switch (result.toUpperCase()){
                            case "APROBADO":
                                return "DEFAULT";
                            case "DESAPROBADO":
                                if(countRejected == null) countRejected = 0;
                                countRejected++;
                                if(countRejected >= maxRetry) return "DEFAULT";
                                break;
                        }

                    }
                }
                if(countRejected != null && countRejected < maxRetry) return "RETRY";
                return "DEFAULT";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question161Form form, Locale locale) throws Exception {
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question161Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, Configuration.getDefaultLocale());
                Person person = personDao.getPerson(loanApplication.getPersonId(), false, locale);

                ResultadoEvaluacionTitularRequest request = new ResultadoEvaluacionTitularRequest();
                request.setPreRpta(new Gson().fromJson(form.getResponses(), new TypeToken<ArrayList<ResultadoEvaluacionTitularRequest.PreguntaRespuesta>>() {
                }.getType()));
                int positionAdditional = -1;
                for (int i = 0; i < request.getPreRpta().size(); i++) {
                    ResultadoEvaluacionTitularRequest.PreguntaRespuesta responseAdditionalNationality = request.getPreRpta().get(i);
                    if(responseAdditionalNationality != null && responseAdditionalNationality.getPreguntaId().equals("-1")){
                        positionAdditional = i;
                        loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANBIF_ADDITIONAL_NATIONALITY_EXIST.getKey(), responseAdditionalNationality.getRptaId().equals("0") ? false : true);
                        loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                    }
                }
                if(positionAdditional > -1) request.getPreRpta().remove(positionAdditional);
                JSONArray banbifSentinelEvasData = JsonUtil.getJsonArrayFromJson(loanApplication.getEntityCustomData(),LoanApplication.EntityCustomDataKeys.BANBIF_SENTINEL_COD_EVALUATIONS.getKey(), new JSONArray());
                for(int i =0; i < banbifSentinelEvasData.length(); i++){
                    JSONObject banbifSentinelEvalutationData = banbifSentinelEvasData.getJSONObject(i);
                    Boolean currentEva = JsonUtil.getBooleanFromJson(banbifSentinelEvalutationData, "current", false);
                    if(currentEva){
                        request.setCodEva(JsonUtil.getIntFromJson(banbifSentinelEvalutationData,"codEvaluacion", null));
                    }
                }
                ResultadoEvaluacionTitularResponse result = banbifServiceCall.callGetQuestionResult(loanApplication, person, request);
                if (result == null) {
                    throw new ResponseEntityException(AjaxResponse.errorMessage("Por favor, recargue la página e inténtelo nuevamente."));
                }

                webServiceDao.registerEntityWebServiceResult(loanApplication.getId(), EntityWebService.BANBIF_RESULTADO_CUESTIONARIO, new Gson().toJson(result));

                if(result != null){
                    if (Objects.equals(result.getViso_resultado().getCodError(), "7")) {
                        throw new ResponseEntityException(AjaxResponse.errorMessage("Se excedió el tiempo límite para responder esta pregunta. Por favor, recargue la página e inténtelo nuevamente."));
                    }
    
                    if (result.getViso_resultado() != null &&
                            result.getViso_resultado().getEstado() != null) {
                        JSONArray banbifSentinelEvas = JsonUtil.getJsonArrayFromJson(loanApplication.getEntityCustomData(),LoanApplication.EntityCustomDataKeys.BANBIF_SENTINEL_COD_EVALUATIONS.getKey(), new JSONArray());
                        for(int i =0; i < banbifSentinelEvas.length(); i++){
                            JSONObject banbifSentinelEva = banbifSentinelEvas.getJSONObject(i);
                            if(banbifSentinelEva.getInt("codEvaluacion") == request.getCodEva()){
                                banbifSentinelEva.put("result", result.getViso_resultado().getEstado());
                            }
                        }
                        loanApplication.getEntityCustomData().put(LoanApplication.EntityCustomDataKeys.BANBIF_SENTINEL_COD_EVALUATIONS.getKey(), banbifSentinelEvas);
                        loanApplicationDao.updateEntityCustomData(loanApplication.getId(), loanApplication.getEntityCustomData());
                    }
                }
                break;
        }
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
        return null;
    }

}

