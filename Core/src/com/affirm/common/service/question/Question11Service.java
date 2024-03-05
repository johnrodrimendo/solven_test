package com.affirm.common.service.question;

import com.affirm.bancoazteca.model.AztecaAgency;
import com.affirm.bancoazteca.model.BancoAztecaCampaniaApi;
import com.affirm.bancoazteca.model.RolConsejero;
import com.affirm.common.dao.*;
import com.affirm.common.model.Address;
import com.affirm.common.model.catalog.*;
import com.affirm.common.model.form.Question11Form;
import com.affirm.common.model.transactional.*;
import com.affirm.common.service.*;
import com.affirm.common.util.AjaxResponse;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.ProcessQuestionResponse;
import com.affirm.security.dao.SecurityDAO;
import com.affirm.security.model.EntityWsResult;
import com.google.gson.Gson;
import org.apache.commons.lang.time.DateUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.*;

@Service("question11Service")
public class Question11Service extends AbstractQuestionService<Question11Form> {

    @Autowired
    private LoanApplicationDAO loanApplicationDao;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private PersonDAO personDao;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private LoanNotifierService loanNotifierService;
    @Autowired
    private PixelConversionService pixelConversionService;
    @Autowired
    private ConversionDAO conversionDao;
    @Autowired
    private PreliminaryEvaluationDAO preliminaryEvaluationDAO;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private EvaluationDAO evaluationDAO;
    @Autowired
    private SecurityDAO securityDAO;
    @Autowired
    private RccDAO rccDao;

    @Override
    public Map<String, Object> getViewAttributes(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean fillSavedData, Map<String, Object> params) throws Exception {

        Map<String, Object> attributes = new HashMap<>();
        switch (flowType) {
            case LOANAPPLICATION:
                LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
                Person person = personDao.getPerson(catalogService, locale, loanApplication.getPersonId(), false);
                EntityBranding entityBranding = loanApplication.getEntityId() != null ? catalogService.getEntityBranding(loanApplication.getEntityId()) : null;

                boolean hasMessageToShow = false;
                LoanApplicationPreliminaryEvaluation loanPreliminaryEvaluation = loanApplicationService.getLastPreliminaryEvaluation(loanApplication.getId(), locale, entityBranding != null && entityBranding.getBranded() ? entityBranding : null, true);
                LoanApplicationPreliminaryEvaluation fullLoanPreliminaryEvaluation = null;
                if (loanPreliminaryEvaluation != null) {
                    int lastPreliminaryId = loanPreliminaryEvaluation.getId();
                    fullLoanPreliminaryEvaluation = loanApplicationDao.getPreliminaryEvaluations(loanApplication.getId(), locale).stream()
                            .filter(p -> p.getId().equals(lastPreliminaryId))
                            .findFirst().orElse(null);
                }
                Double defaultLatitude = -12.046374;
                Double defaultLongitude = -77.042793;
                attributes.put("navLatitude", defaultLatitude);
                attributes.put("navLongitude", defaultLongitude);

                if(loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA && loanApplication.getProductCategoryId() == ProductCategory.CONSEJ0){
                    attributes.put("customMessage", "Estamos buscando cómo llegar a ti con alternativas digitales que te permitan seguir progresando. ¡Pronto tendremos noticias!");
                }
                if (loanPreliminaryEvaluation == null || !loanPreliminaryEvaluation.getApproved()) {
                    if (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.BANCO_DEL_SOL) {
                        if (loanApplication.getStatus().getId() == LoanApplicationStatus.REJECTED_AUTOMATIC
                                && loanPreliminaryEvaluation != null
                                && loanPreliminaryEvaluation.getStatus() != null
                                && loanPreliminaryEvaluation.getStatus() == 'F') {
                            attributes.put("showBDSFailedEvaluation", true);
                        }
                        if (loanApplication.getStatus().getId() == LoanApplicationStatus.REJECTED_AUTOMATIC
                                && fullLoanPreliminaryEvaluation != null
                                && fullLoanPreliminaryEvaluation.getHardFilter() != null
                                && Arrays.asList(HardFilter.MAX_AGE, HardFilter.MIN_AGE).contains(fullLoanPreliminaryEvaluation.getHardFilter().getId())) {
                            attributes.put("customMessage", "La edad de " + person.getFirstName() + ", no cumple con los requisitos establecidos por el Banco para acceder a un nuevo préstamo.");
                        }
                        if (loanApplication.getStatus().getId() == LoanApplicationStatus.REJECTED_AUTOMATIC
                                && fullLoanPreliminaryEvaluation != null
                                && fullLoanPreliminaryEvaluation.getHardFilter() != null
                                && fullLoanPreliminaryEvaluation.getHardFilter().getId() == HardFilter.BASE_NEGATIVA_BDS) {
                            attributes.put("customMessage", "Revisamos los antecedentes de " + person.getFirstName() + " y verificamos que registra información desfavorable en nuestras bases");
                        }
                        if (loanApplication.getStatus().getId() == LoanApplicationStatus.REJECTED_AUTOMATIC
                                && fullLoanPreliminaryEvaluation != null
                                && fullLoanPreliminaryEvaluation.getHardFilter() != null
                                && fullLoanPreliminaryEvaluation.getHardFilter().getId() == HardFilter.BCRA_BDS) {
                            attributes.put("customMessage", "Revisamos los antecedentes de " + person.getFirstName() + " y verificamos que registra información desfavorable en la Central de Deudores del BCRA");
                        }
                        if (loanApplication.getStatus().getId() == LoanApplicationStatus.REJECTED_AUTOMATIC
                                && fullLoanPreliminaryEvaluation != null
                                && fullLoanPreliminaryEvaluation.getHardFilter() != null
                                && fullLoanPreliminaryEvaluation.getHardFilter().getId() == HardFilter.MAX_SITUACION_ULT_X_MESES_BCRA) {
                            attributes.put("customMessage", "Revisamos los antecedentes de " + person.getFirstName() + " y verificamos que registra información desfavorable en la Central de Deudores del BCRA");
                        }
                        if (loanApplication.getStatus().getId() == LoanApplicationStatus.REJECTED_AUTOMATIC
                                && fullLoanPreliminaryEvaluation != null
                                && fullLoanPreliminaryEvaluation.getHardFilter() != null
                                && fullLoanPreliminaryEvaluation.getHardFilter().getId() == HardFilter.BDS_EXISTS_IN_CNE) {
                            attributes.put("customMessage", "No podemos continuar con la evaluación ya que el B.C.R.A nos indica que el solicitante deberá cumplimentar el CNE (Censo Nacional Económico) para poder proseguir con el proceso (Comunicación “B” 12100). Para ello deberá acceder a https://cne.indec.gob.ar, y completar el Censo.  Una vez efectuado el mismo se estiman 24 /48 hs hasta tanto el BCRA actualice la información.");
                        }
                    }
                    if(loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA && loanApplication.getProductCategoryId() == ProductCategory.VALIDACION_IDENTIDAD){
                        attributes.put("customMessage", "Por favor acércate a una de nuestras agencias para finalizar el proceso.");
                        attributes.put("showAgencies", true);
                        attributes.put("agencies", null);
                        setGeolocation(loanApplication,defaultLatitude,defaultLongitude,attributes, null);
                    }
                    if(loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA
                            && fullLoanPreliminaryEvaluation != null
                            && fullLoanPreliminaryEvaluation.getHardFilter() != null
                            && fullLoanPreliminaryEvaluation.getHardFilter().getId() == HardFilter.AZTECA_CAMPANIA_PRODUCT){
                        // Default message for this hardfilter
                        attributes.put("customMessage", "Por favor acércate a una de nuestras agencias para finalizar el proceso.");
                        attributes.put("showAgencies", true);
                        attributes.put("agencies", null);
                        setGeolocation(loanApplication,defaultLatitude,defaultLongitude,attributes, null);
                        // custom message for prestahorro or for no product
                        JSONObject dataCampaign = JsonUtil.getJsonObjectFromJson(loanApplication.getEntityCustomData(), LoanApplication.EntityCustomDataKeys.AZTECA_CAMPAIGN_BASE.getKey(), new JSONObject());
                        BancoAztecaCampaniaApi campaniaApi = new Gson().fromJson(dataCampaign.toString(), BancoAztecaCampaniaApi.class);
                        if(campaniaApi != null && campaniaApi.getCampo3() != null && campaniaApi.getCampo3().equalsIgnoreCase("PrestAhorro")){
                            attributes.put("customMessage", "Felicitaciones, tienes un crédito Prestahorro disponible.\n Por favor acércate a una de nuestras agencias para finalizar el proceso.");
                        }
                        if(campaniaApi == null || campaniaApi.getCampo3() == null || campaniaApi.getCampo3().isEmpty() || campaniaApi.getCampo3().equalsIgnoreCase("0")){
                            attributes.put("customMessage", "Lo sentimos, por el momento no podemos continuar con tu solicitud por este medio. <br>Vuelve a intentarlo el próximo mes.");
                            attributes.put("showAgencies", false);
                        }
                    }

                    if (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA){
                        if (loanApplication.getStatus().getId() == LoanApplicationStatus.REJECTED_AUTOMATIC
                                && fullLoanPreliminaryEvaluation != null
                                && fullLoanPreliminaryEvaluation.getHardFilter() != null
                                && fullLoanPreliminaryEvaluation.getHardFilter().getId() == HardFilter.ROL_CONSEJERO_BAZ) {
                            EntityWsResult diagnosis = securityDAO.getEntityResultWS(id, EntityWebService.BANCO_AZTECA_OBTAIN_ADVISER_ROLE);
                            if(diagnosis != null && diagnosis.getResult() != null) {
                                RolConsejero rolConsejero = new Gson().fromJson(diagnosis.getResult().toString(), RolConsejero.class);
                                if(rolConsejero != null && !rolConsejero.getRecomendacion().isEmpty()){
                                    attributes.put("customMessage", rolConsejero.getRecomendacion());
                                }
                            }
                        }
                    }

                    if(loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.BANBIF && loanApplication.getProductCategoryId() == ProductCategory.TARJETA_CREDITO){
                        if(loanApplication.getStatus().getId() == LoanApplicationStatus.REJECTED_AUTOMATIC
                                && fullLoanPreliminaryEvaluation != null
                                && fullLoanPreliminaryEvaluation.getHardFilter() != null
                                && fullLoanPreliminaryEvaluation.getHardFilter().getId() == HardFilter.BASE_BANBIF){
                            Date baseValidUntil = rccDao.getBanbifBaseValidUntil();
                            Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
                            if(currentDate.after(baseValidUntil)){
                                attributes.put("customMessage", "Estamos en proceso de carga, por favor intenta en las próximas 24 horas.");
                            }
                        }
                    }

                    attributes.put("preEvaluation", loanPreliminaryEvaluation);
                    validateExpirationForFdlm(loanApplication, loanPreliminaryEvaluation, locale);
                    if (loanPreliminaryEvaluation != null)
                        hasMessageToShow = true;
                } else {
                    LoanApplicationEvaluation loanEvaluation = loanApplicationService.getLastEvaluation(loanApplication.getId(), loanApplication.getPersonId(), locale, true);
                    LoanApplicationEvaluation fullLoanEvaluation = null;
                    if (loanEvaluation != null) {
                        int lastEvaluationId = loanEvaluation.getId();
                        fullLoanEvaluation = loanApplicationDao.getEvaluations(loanApplication.getId(), locale).stream()
                                .filter(p -> p.getId().equals(lastEvaluationId))
                                .findFirst().orElse(null);
                    }
                    if (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.BANCO_DEL_SOL) {
                        if (loanApplication.getStatus().getId() == LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION
                                && loanEvaluation != null
                                && loanEvaluation.getStatus() != null
                                && loanEvaluation.getStatus() == 'F') {
                            attributes.put("showBDSFailedEvaluation", true);
                        }
                        if (loanApplication.getStatus().getId() == LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION
                                && fullLoanEvaluation != null
                                && fullLoanEvaluation.getPolicy() != null
                                && fullLoanEvaluation.getPolicy().getPolicyId() == Policy.PROVISIONAL_DEBTORS
                        ) {
                            attributes.put("customMessage", String.format("El cliente %s registra deuda previsional impaga en el último año. Para acceder al préstamo deberá cancelar la deuda mediante alguna de las opciones disponibles por AFIP.",person.getFirstName()));
                        }
                    }else if (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.AZTECA) {

                        if(loanApplication.getProductCategoryId() == ProductCategory.VALIDACION_IDENTIDAD){
                            attributes.put("customMessage", "Por favor acércate a una de nuestras agencias para finalizar el proceso.");
                            attributes.put("showAgencies", true);
                            attributes.put("agencies", null);
                            setGeolocation(loanApplication,defaultLatitude,defaultLongitude,attributes,null);
                        }
                        if(loanApplication.getProductCategoryId() == ProductCategory.GATEWAY){
                            AztecaGetawayBase aztecaGetawayBase = loanApplication.getAztecaGatewayBaseData();
                            if(aztecaGetawayBase != null && loanApplication.getSelectedEntityProductParameterId() != null && Arrays.asList(EntityProductParams.ENT_PROD_PARAM_AZTECA_GATEWAY).contains(loanApplication.getSelectedEntityProductParameterId())){
                                attributes.put("customMessage", "Por favor acércate a una de nuestras agencias para finalizar el proceso.");
                                attributes.put("showAgencies", true);
                                attributes.put("agencies", null);
                                searchAndSetGeolocationByCobranzaBase(loanApplication,defaultLatitude,defaultLongitude,attributes,null);
                            }
                        }
                        if (loanApplication.getStatus().getId() == LoanApplicationStatus.REJECTED_AUTOMATICALLY_EVALUATION
                                && fullLoanEvaluation != null
                                && fullLoanEvaluation.getPolicy() != null
                                && (fullLoanEvaluation.getPolicy().getPolicyId() == Policy.PEP || fullLoanEvaluation.getPolicy().getPolicyId() == Policy.FATCA || fullLoanEvaluation.getPolicy().getPolicyId() == Policy.AVOID_DISTRICTS_HOME || fullLoanEvaluation.getPolicy().getPolicyId() == Policy.BANTOTAL_VALIDACION_EMAIL_CELULAR)) {

                            if(fullLoanEvaluation.getPolicy().getPolicyId() == Policy.AVOID_DISTRICTS_HOME || fullLoanEvaluation.getPolicy().getPolicyId() == Policy.BANTOTAL_VALIDACION_EMAIL_CELULAR) attributes.put("customMessage", "Lo sentimos por el momento no podemos continuar con tu solicitud por este medio. <br/> Por favor acércate a tu agencia más cercana y continúa con el proceso.");
                            else attributes.put("customMessage", String.format("%s, tenemos una oferta para tí, acércate a nuestra agencia mas cercana para continuar el proceso.",person.getFirstName() != null ? person.getFirstName() : "Hola"));

                            attributes.put("showAgencies", true);
                            attributes.put("agencies", null);
                            setGeolocation(loanApplication,defaultLatitude,defaultLongitude,attributes,null);

                        }
                    }
                    attributes.put("evaluation", loanEvaluation);
                    if (loanEvaluation != null)
                        hasMessageToShow = true;
                }
                attributes.put("marketplaceRejection", false);

                if (!hasMessageToShow) {
                    if (entityBranding != null && entityBranding.getEntity().getId().equals(Entity.FUNDACION_DE_LA_MUJER)) {
                        LoanApplicationPreliminaryEvaluation dummyPreEva = new LoanApplicationPreliminaryEvaluation();
                        dummyPreEva.setHardFilterMessage(messageSource.getMessage("bd.fdlm.policy.rejected", null, new Locale("es", "CO")));

                        attributes.put("preEvaluation", dummyPreEva);
                    }
                }

                if (!pixelConversionService.hasSendConversion(loanApplication, PixelConversionService.GOOGLE_SOURCE, PixelConversionService.SOME_REJECTION)) {
                    conversionDao.registerPixelConversion(loanApplication.getId(), PixelConversionService.GOOGLE_SOURCE, PixelConversionService.SOME_REJECTION);
                    loanNotifierService.notifyRejection(loanApplication, null);
                }

                attributes.put("showHasRescueOffers", loanApplicationService.getRescueSreenParams(loanApplication.getId(), loanApplication.getPersonId()).getShowScreen());
                if (loanApplication.getEntityUserId() == null && !isBrandingFDLM(loanApplication)) {
                    attributes.put("helpMessage", loanApplicationService.loanApplicationtHelpMessage(loanApplication));
                }

                attributes.put("personFirstName", person.getFirstName());
                attributes.put("loanApplication", loanApplication);
                break;
        }
        return attributes;
    }

    @Override
    public String getQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Question11Form form) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                return form.getAdvices() ? "CONSEJOS" : "NO_CONSEJOS";
        }
        return null;
    }

    @Override
    protected void validateForm(QuestionFlowService.Type flowType, Integer id, Question11Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
    }

    @Override
    public void saveData(QuestionFlowService.Type flowType, Integer id, Question11Form form, Locale locale) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
    }

    @Override
    public Object customMethod(String path, QuestionFlowService.Type flowType, Integer id, Locale locale, Map<String, Object> params) throws Exception {
        LoanApplication loanApplication = loanApplicationDao.getLoanApplication(id, locale);
        switch (flowType) {
            case LOANAPPLICATION:
                switch (path) {
                    case "affiliation":
                        if (loanApplication.getJsLeadParam() != null) {
                            loanApplication.getJsLeadParam().put("company_affiliation", (boolean) params.get("affiliation"));
                        } else {
                            loanApplication.setJsLeadParam(new JSONObject().put("company_affiliation", (boolean) params.get("affiliation")));
                        }

                        loanApplicationDao.updateLeadParams(loanApplication.getId(), loanApplication.getJsLeadParam());

                        return AjaxResponse.ok(null);
                    case "backToRescue":
                        return ProcessQuestionResponse.goToQuestion(evaluationService.forwardByResult(
                                loanApplication,
                                "RESCUE_OFFERS",
                                null));
                }
                break;
        }
        throw new Exception("No method configured");
    }

    @Override
    public String getSkippedQuestionResultToGo(QuestionFlowService.Type flowType, Integer id, Locale locale, boolean saveData) throws Exception {
        switch (flowType) {
            case LOANAPPLICATION:
                break;
        }
        return null;
    }

    private void validateExpirationForFdlm(LoanApplication loanApplication, LoanApplicationPreliminaryEvaluation loanPreliminaryEvaluation,
                                           Locale locale) throws Exception {

        if (loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.FUNDACION_DE_LA_MUJER) {
            HardFilter hardFilter = preliminaryEvaluationDAO.getPreliminaryEvaluationsWithHardFilters(loanApplication.getId(), locale)
                    .stream()
                    .filter(e -> e.getHardFilterMessageKey().equals(loanPreliminaryEvaluation.getHardFilterMessageKey()))
                    .findFirst()
                    .map(LoanApplicationPreliminaryEvaluation::getHardFilter)
                    .orElse(null);

            if (hardFilter != null && Arrays.asList(HardFilter.MIN_PERCENTAGE_SIMILARITY_FACES, HardFilter.REQUIRED_TEXT_IN_IMAGE).contains(hardFilter.getId())) {
                loanApplicationDao.expireLoanApplication(loanApplication.getId());
            }
        }
    }

    private boolean isBrandingFDLM(LoanApplication loanApplication) {
        return loanApplication.getEntityId() != null && loanApplication.getEntityId() == Entity.FUNDACION_DE_LA_MUJER;
    }

    public void setGeolocation(LoanApplication loanApplication, Double defaultLatitude, Double defaultLongitude, Map<String, Object> attributes, ModelMap model) throws Exception {
        if(defaultLatitude == null) defaultLatitude = -12.046374;
        if(defaultLongitude == null) defaultLongitude = -77.042793;
        Double latitude = defaultLatitude;
        Double longitude = defaultLongitude;
        AztecaAgency aztecaAgency = null;
        if(loanApplication != null){
            if(loanApplication.getNavLatitude() != null && loanApplication.getNavLongitude() != null){
                latitude = loanApplication.getNavLatitude();
                longitude = loanApplication.getNavLongitude();
            }
            Address address = evaluationDAO.getAddressByPersonId(loanApplication.getPersonId());
            if (address != null && address.getUbigeoId() != null) {
                if(loanApplication.getNavLatitude() == null && loanApplication.getNavLongitude() == null && address.getAddressLatitude() != null && address.getAddressLongitude() != null){
                    latitude = address.getAddressLatitude();
                    longitude = address.getAddressLongitude();
                }
                Ubigeo ubigeo = catalogService.getUbigeo(address.getUbigeoId());
                aztecaAgency = null;
            }
        }
        if(attributes != null){
            attributes.put("navLatitude", latitude);
            attributes.put("navLongitude", longitude);
            attributes.put("closestAgency", aztecaAgency);
        }
        else if(model != null){
            model.addAttribute("navLatitude", latitude);
            model.addAttribute("navLongitude", longitude);
            model.addAttribute("closestAgency", aztecaAgency);
        }
    }


    public void searchAndSetGeolocationByCobranzaBase(LoanApplication loanApplication, Double defaultLatitude, Double defaultLongitude, Map<String, Object> attributes, ModelMap model) throws Exception {
        if(defaultLatitude == null) defaultLatitude = -12.046374;
        if(defaultLongitude == null) defaultLongitude = -77.042793;
        Double latitude = defaultLatitude;
        Double longitude = defaultLongitude;
        AztecaAgency aztecaAgency = null;
        if(loanApplication != null){
            AztecaGetawayBase aztecaGetawayBase = loanApplication.getAztecaGatewayBaseData();
            if(aztecaGetawayBase != null){
                List<AztecaAgency> agencyList = null;
                if(aztecaGetawayBase.getDistrito() != null) aztecaAgency = agencyList.stream().filter(e -> e.getDistrict() != null && e.getDistrict().equalsIgnoreCase(aztecaGetawayBase.getDistrito())).findFirst().orElse(null);
                if(aztecaAgency == null && aztecaGetawayBase.getDepartamento() != null) aztecaAgency = agencyList.stream().filter(e -> e.getDepartment() != null && e.getDepartment().equalsIgnoreCase(aztecaGetawayBase.getDepartamento())).findFirst().orElse(null);
            }
        }
        if(attributes != null){
            attributes.put("navLatitude", latitude);
            attributes.put("navLongitude", longitude);
            attributes.put("closestAgency", aztecaAgency);
        }
        else if(model != null){
            model.addAttribute("navLatitude", latitude);
            model.addAttribute("navLongitude", longitude);
            model.addAttribute("closestAgency", aztecaAgency);
        }
    }


}

