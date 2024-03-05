package com.affirm.common.model.catalog;

import com.affirm.bancodelsol.model.CommissionClusterForm;
import com.affirm.common.EntityProductParamIdentityValidationConfig;
import com.affirm.common.model.EntityProductParamExtraConfiguration;
import com.affirm.common.model.transactional.EntityProductParamApprovalValidationConfig;
import com.affirm.common.model.transactional.LoanOffer;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by stbn on 16/03/17.
 */
public class EntityProductParams {

    public static final int CONTRACT_TYPE_DIGITAL = 1;
    public static final int CONTRACT_TYPE_MANUAL = 2;
    public static final int CONTRACT_TYPE_MIXTO = 3;
    public static final int DISBURSEMENT_TYPE_DEPOSIT = 1;
    public static final int DISBURSEMENT_TYPE_RETIREMNT = 2;
    public static final int FLOW_TYPE_NORMAL = 1;
    public static final int FLOW_TYPE_PRE_APPROVED_BASE = 2;
    public static final int FLOW_TYPE_MIXTO = 3;

    public static final List<Integer> ENT_PROD_PARAM_RIPLEY_PRESTAMOYA = Arrays.asList(8103, 8102);
    public static final int ENT_PROD_PARAM_RIPLEY_PRESTAMOYA_MENOR = 8102;
    public static final int ENT_PROD_PARAM_RIPLEY_PRESTAMOYA_MAYOR = 8103;
    public static final int ENT_PROD_PARAM_RIPLEY = 8101;
    public static final int ENT_PROD_PARAM_AELU_CONVENIO = 17101;
    public static final List<Integer> ENT_PROD_PARAM_ACCESO_GARANTIZADO = Arrays.asList(9901);
    public static final List<Integer> ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD = Arrays.asList(9101, 9102, 9103);
    public static final int ENT_PROD_PARAM_ABACO_CONVENIO = 7701;
    public static final int ENT_PROD_PARAM_QAPAQ_FLUJO_1 = 16102;
    public static final int ENT_PROD_PARAM_TARJETA_PERUANA_PREPAGO = 181001;
    public static final int ENT_PROD_PARAM_BANCO_DEL_SOL = 5403101;
    public static final int ENT_PROD_PARAM_BANCO_DEL_SOL_AGENCIAS = 5403103;
    public static final int ENT_PROD_PARAM_AUTOPLAN_LEAD = 19201;
    public static final int ENT_PROD_PARAM_EFL_DEBT_CONSOLIDATION_OPEN = 10801;
    public static final int ENT_PROD_PARAM_WENANCE_LEAD = 5401101;
    public static final int ENT_PROD_PARAM_BANBIF_LIBRE_DISPONIBILIDAD = 20101;
    public static final int ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO = 201201;
    public static final int ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_CERO_MEMBRESIA = 201202;
    public static final int ENT_PROD_PARAM_BANBIF_LEAD_TARJETA_CREDITO = 201301;
    public static final int ENT_PROD_PARAM_FDLM = 5702101;
    public static final int ENT_PROD_PARAM_CREDIGOB_PROOVEDOR_ESTADO = 21101;
    public static final int ENT_PROD_PARAM_INVERSIONES_LA_CRUZ = 221101;
    public static final int ENT_PROD_PARAM_FINANSOL_CONSUMO = 23101;
    public static final int ENT_PROD_PARAM_FINANSOL_CONSUMO_BASE = 23102;
    public static final int ENT_PROD_PARAM_PRESTAMYPE_PRESTAMO_GAR_HIPOT = 241001;
    public static final int ENT_PROD_PARAM_BANCO_DEL_SOL_CAMPANA = 5403104;
    public static final int ENT_PROD_PARAM_BANCO_DEL_SOL_SINIESTROS = 5403105;
    public static final int ENT_PROD_PARAM_AZTECA = 26101;
    public static final int ENT_PROD_PARAM_AZTECA_ONLINE = 26102;
    public static final int ENT_PROD_PARAM_AZTECA_GATEWAY = 26701;
    public static final int ENT_PROD_PARAM_AZTECA_CUENTA_AHORRO = 26801;
    public static final int ENT_PROD_PARAM_AZTECA_IDENTIDAD = 26901;
    public static final int ENT_PROD_PARAM_AZTECA_ROL_CONSEJERO = 261001;
    public static final int ENT_PROD_PARAM_PRISMA_SOCIIOS_ACTUALES = 25101;
    public static final int ENT_PROD_PARAM_PRISMA_DEPENDIENTES = 25102;
    public static final int ENT_PROD_PARAM_PRISMA_MICROEMPRENDEDORES = 25103;
    public static final int ENT_PROD_PARAM_AZTECA_GATEWAY_VIGENTE = 26702;
    public static final int ENT_PROD_PARAM_BANBIF_TARJETA_CREDITO_MAS_EFECTIVO = 201203;

    private Integer id;
    private Entity entity;
    private Product product;
    private String entityProduct;
    private Integer flowType;
    private Integer signatureType;
    private Contract contract;
    private List<Contract> contracts;
    private Boolean sendContract;
    private Boolean selfDisbursement;
    private String postSignatureMessage; // TODO Put in properties for I18N
    private String legalFooter;
    private ProcessQuestionsConfiguration evaluation;
    private Double cost;
    private Integer disbursementType;
    private Boolean extranetCreditGeneration;

    private List<Integer> postSignatureInteractionIds;
    private String offerLegalFooter;
    private String postSignatureLegalFooter;
    private String commercialSpeech;
    private String wellcomeSpeech;
    private JSONObject creditGenerationEntityAlertInteraction;
    private List<Integer> requiredDocuments;
    private Boolean extranetTwoStepGeneration;
    private List<Integer> gracePeriod;
    private List<Integer> fixedDueDate;
    private List<Integer> loanApplicationAuditTypes;
    private Boolean solvenGeneratesSchedule;
    private List<String> entityProductParameter;
    private Boolean showWaiting;
    private Boolean showGeneration;
    private Boolean showDisbursement;
    private List<Integer> scheduleAvoidWeekDays;
    private Boolean scheduleAvoidHolidays;
    private Boolean mustRunBureau;
    private List<Integer> disbursementBanks;
    private Boolean requiresDisaggregatedAddress;
    private Integer maxRetries;
    private Boolean runFixedDueDate;
    private List<Integer> disbursementInteractionIds;
    private String template;
    private List<Integer> fixedInstallments;
    private String finalQuestionMessage;
    private Boolean automaticApproval;
    private Boolean simulatedOffer;
    private Boolean signaturePin;
    private Integer priceId;
    private Boolean showResumeContract;
    private String offerScheduleLegalFooter;
    private Boolean requireEmailValidationForApproval;
    private EntityProductParamIdentityValidationConfig entityProductParamIdentityValidationConfig;
    private List<EntityProductParamApprovalValidationConfig> approvalValidationConfigs;
    private Boolean fixedOffer;
    private DisbursementType disbursementTypeObject;
    private Integer fixedInstallmentDays;
    private EntityProductParamExtraConfiguration entityProductParamExtraConfiguration;

    public void fillFromDb(JSONObject json, CatalogService catalog) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "entity_product_parameter_id", null));
        if (JsonUtil.getIntFromJson(json, "entity_id", null) != null)
            setEntity(catalog.getEntity(JsonUtil.getIntFromJson(json, "entity_id", null)));
        if (JsonUtil.getIntFromJson(json, "product_id", null) != null)
            setProduct(catalog.getProduct(JsonUtil.getIntFromJson(json, "product_id", null)));
        setFlowType(JsonUtil.getIntFromJson(json, "flow_type_id", null));
        setSignatureType(JsonUtil.getIntFromJson(json, "signature_type_id", null));
        if (JsonUtil.getIntFromJson(json, "contract_id", null) != null)
            setContract(catalog.getContractById(JsonUtil.getIntFromJson(json, "contract_id", null)));
        if (JsonUtil.getJsonArrayFromJson(json, "ar_contract_id", null) != null) {
            JSONArray contractArr = JsonUtil.getJsonArrayFromJson(json, "ar_contract_id", null);
            List<Contract> contractsArr = new ArrayList<>();
            for (int i = 0; i < contractArr.length(); i++) {
                contractsArr.add(catalog.getContractById(contractArr.getInt(i)));
            }
            setContracts(contractsArr);
        }

        setSendContract(JsonUtil.getBooleanFromJson(json, "send_contract", null));
        setSelfDisbursement(JsonUtil.getBooleanFromJson(json, "self_disbursement", null));
        setPostSignatureMessage(JsonUtil.getStringFromJson(json, "post_signature_message", null));
        setLegalFooter(JsonUtil.getStringFromJson(json, "legal_footer", null));
        if (JsonUtil.getJsonObjectFromJson(json, "js_evaluation_process", null) != null) {
            setEvaluation(new ProcessQuestionsConfiguration());
            getEvaluation().fillFromDb(JsonUtil.getJsonObjectFromJson(json, "js_evaluation_process", null));
        }
        setCost(JsonUtil.getDoubleFromJson(json, "cost", null));
        setDisbursementType(JsonUtil.getIntFromJson(json, "disbursement_type_id", null));
        if(JsonUtil.getIntFromJson(json, "disbursement_type_id", null) != null) setDisbursementTypeObject(catalog.getDisbursementTypeById(JsonUtil.getIntFromJson(json, "disbursement_type_id", null)));
        setExtranetCreditGeneration(JsonUtil.getBooleanFromJson(json, "extranet_credit_generation", null));


        if (JsonUtil.getJsonArrayFromJson(json, "ar_post_signature_interaction_id", null) != null) {
            JSONArray interactionsArray = JsonUtil.getJsonArrayFromJson(json, "ar_post_signature_interaction_id", null);
            postSignatureInteractionIds = new ArrayList<>();
            for (int i = 0; i < interactionsArray.length(); i++) {
                postSignatureInteractionIds.add(interactionsArray.getInt(i));
            }
        }
        setOfferLegalFooter(JsonUtil.getStringFromJson(json, "offer_legal_footer", null));
        setPostSignatureLegalFooter(JsonUtil.getStringFromJson(json, "post_signature_legal_footer", null));
        setCommercialSpeech(JsonUtil.getStringFromJson(json, "commercial_speech", null));
        setWellcomeSpeech(JsonUtil.getStringFromJson(json, "welcome_speech", null));
        setCreditGenerationEntityAlertInteraction(JsonUtil.getJsonObjectFromJson(json, "credit_generation_entity_alert_interaction", null));

        if (JsonUtil.getJsonArrayFromJson(json, "js_required_filetype", null) != null) {
            JSONArray requireds = JsonUtil.getJsonArrayFromJson(json, "js_required_filetype", null);
            List<Integer> requiedFiles = new ArrayList<>();
            for (int j = 0; j < requireds.length(); j++) {
                requiedFiles.add(requireds.getInt(j));
            }
            setRequiredDocuments(requiedFiles);
        }
        setExtranetTwoStepGeneration(JsonUtil.getBooleanFromJson(json, "extranet_two_step_generation", null));
        if (JsonUtil.getJsonArrayFromJson(json, "grace_period", null) != null) {
            JSONArray gracePeriodArray = JsonUtil.getJsonArrayFromJson(json, "grace_period", null);
            gracePeriod = new ArrayList<>();
            for (int i = 0; i < gracePeriodArray.length(); i++) {
                gracePeriod.add(gracePeriodArray.getInt(i));
            }
        }
        if (JsonUtil.getJsonArrayFromJson(json, "fixed_due_days", null) != null) {
            JSONArray fixedDueDatesarray = JsonUtil.getJsonArrayFromJson(json, "fixed_due_days", null);
            fixedDueDate = new ArrayList<>();
            for (int i = 0; i < fixedDueDatesarray.length(); i++) {
                fixedDueDate.add(fixedDueDatesarray.getInt(i));
            }
        }
        if (JsonUtil.getJsonArrayFromJson(json, "loan_application_audit_type", null) != null) {
            JSONArray jsonArray = JsonUtil.getJsonArrayFromJson(json, "loan_application_audit_type", null);
            loanApplicationAuditTypes = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                loanApplicationAuditTypes.add(jsonArray.getInt(i));
            }
        }

        setSolvenGeneratesSchedule(JsonUtil.getBooleanFromJson(json, "solven_generates_schedule", null));

        if (JsonUtil.getJsonArrayFromJson(json, "ar_parameters", null) != null) {
            JSONArray jsonArray = JsonUtil.getJsonArrayFromJson(json, "ar_parameters", null);
            List<String> parameterArr = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                parameterArr.add(jsonArray.getString(i));
            }
            setEntityProductParameter(parameterArr);
        }

        if (JsonUtil.getJsonObjectFromJson(json, "extranet_actions", null) != null) {
            JSONObject actions = JsonUtil.getJsonObjectFromJson(json, "extranet_actions", null);
            setShowWaiting((Boolean) actions.get("waiting"));
            setShowGeneration((Boolean) actions.get("generation"));
            setShowDisbursement((Boolean) actions.get("disbursement"));
        }

        if (JsonUtil.getJsonArrayFromJson(json, "schedule_avoid_days", null) != null) {
            JSONArray scheduleAvoidWeekDaysArray = JsonUtil.getJsonArrayFromJson(json, "schedule_avoid_days", null);
            scheduleAvoidWeekDays = new ArrayList<>();
            for (int i = 0; i < scheduleAvoidWeekDaysArray.length(); i++) {
                scheduleAvoidWeekDays.add(scheduleAvoidWeekDaysArray.getInt(i));
            }
        }
        setScheduleAvoidHolidays(JsonUtil.getBooleanFromJson(json, "schedule_avoid_holidays", null));
        setMustRunBureau(JsonUtil.getBooleanFromJson(json, "must_run_bureau", false));
        if (JsonUtil.getJsonArrayFromJson(json, "ar_disbursement_bank_id", null) != null) {
            JSONArray jsonArray = JsonUtil.getJsonArrayFromJson(json, "ar_disbursement_bank_id", null);
            setDisbursementBanks(new ArrayList<>());
            for (int i = 0; i < jsonArray.length(); i++) {
                getDisbursementBanks().add(jsonArray.getInt(i));
            }
        }
        setEntityProduct(JsonUtil.getStringFromJson(json, "entity_product", null));
        setRequiresDisaggregatedAddress(JsonUtil.getBooleanFromJson(json, "requires_disaggregated_address", false));
        setMaxRetries(JsonUtil.getIntFromJson(json, "max_retries", null));
        setRunFixedDueDate(JsonUtil.getBooleanFromJson(json, "run_fixed_due_date", false));
        if (JsonUtil.getJsonArrayFromJson(json, "ar_disbursement_interaction_id", null) != null) {
            JSONArray interactionsArray = JsonUtil.getJsonArrayFromJson(json, "ar_disbursement_interaction_id", null);
            disbursementInteractionIds = new ArrayList<>();
            for (int i = 0; i < interactionsArray.length(); i++) {
                disbursementInteractionIds.add(interactionsArray.getInt(i));
            }
        }
        setTemplate(JsonUtil.getStringFromJson(json, "template", null));
        if (JsonUtil.getJsonArrayFromJson(json, "fixed_installments", null) != null) {
            JSONArray installmentsArray = JsonUtil.getJsonArrayFromJson(json, "fixed_installments", null);
            setFixedInstallments(new ArrayList<>());
            for (int i = 0; i < installmentsArray.length(); i++) {
                getFixedInstallments().add(installmentsArray.getInt(i));
            }
        }
        setFinalQuestionMessage(JsonUtil.getStringFromJson(json, "final_question_message", null));
        setAutomaticApproval(JsonUtil.getBooleanFromJson(json, "automatic_approval", null));
        setSimulatedOffer(JsonUtil.getBooleanFromJson(json, "simulated_offer", null));
        setSignaturePin(JsonUtil.getBooleanFromJson(json, "signature_pin", null));
        setPriceId(JsonUtil.getIntFromJson(json, "price_id", null));
        setShowResumeContract(JsonUtil.getBooleanFromJson(json, "show_resume_contract", true));
        setOfferScheduleLegalFooter(JsonUtil.getStringFromJson(json, "offer_schedule_legal_footer", null));
        setRequireEmailValidationForApproval(JsonUtil.getBooleanFromJson(json, "require_email_validation_for_approval", false));
        if(JsonUtil.getJsonObjectFromJson(json, "js_identity_validation_config", null) != null) setEntityProductParamIdentityValidationConfig(new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "js_identity_validation_config", null) .toString(), EntityProductParamIdentityValidationConfig.class));
        if (JsonUtil.getJsonArrayFromJson(json, "js_approval_validation_id", null) != null) {
            approvalValidationConfigs = new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "js_approval_validation_id", null).toString(), new TypeToken<List<EntityProductParamApprovalValidationConfig>>() {
            }.getType());
        }
        setFixedOffer(JsonUtil.getBooleanFromJson(json, "fixed_offer", false));
        setFixedInstallmentDays(JsonUtil.getIntFromJson(json, "fixed_installment_days", null));
        if (JsonUtil.getJsonObjectFromJson(json, "js_extra_configuration", null) != null) {
            entityProductParamExtraConfiguration = new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "js_extra_configuration", null).toString(),EntityProductParamExtraConfiguration.class);
        }
    }

    public Boolean getRequiresContractCall() {
        return Arrays.asList(ENT_PROD_PARAM_RIPLEY, ENT_PROD_PARAM_RIPLEY_PRESTAMOYA_MENOR).contains(id);
    }

    public Boolean getRequiresInterbankCode() {
        List<Integer> entityparams = new ArrayList<>();
        entityparams.add(ENT_PROD_PARAM_RIPLEY_PRESTAMOYA_MENOR);
        entityparams.add(ENT_PROD_PARAM_RIPLEY);
        entityparams.addAll(ENT_PROD_PARAM_ACCESO_LIBRE_DISPONIBLIDAD);
        return entityparams.contains(id);
    }

    public String getSignatureTypeName() {
        if (signatureType != null)
            switch (signatureType) {
                case CONTRACT_TYPE_DIGITAL:
                    return "Digital";
                case CONTRACT_TYPE_MANUAL:
                    return "Manual";
                case CONTRACT_TYPE_MIXTO:
                    return "Mixto";
            }
        return null;
    }

    public String getPostSignatureMessageReplaced(String bankAccount, String cci) {
        return getPostSignatureMessageReplaced(bankAccount, cci, "");
    }

    public String getPostSignatureMessageReplaced(String bankAccount, String cci, String personName) {
        if (bankAccount != null) {
            return postSignatureMessage
                    .replaceAll("%BANK_ACCOUNT_NUMBER%", " bancaria " + bankAccount)
                    .replaceAll("%ENTITY_SHORT_NAME%", entity.getShortName())
                    .replaceAll("%CLIENT_NAME%", personName);
        } else if (cci != null) {
            return postSignatureMessage
                    .replaceAll("%BANK_ACCOUNT_NUMBER%", "con cci " + cci)
                    .replaceAll("%ENTITY_SHORT_NAME%", entity.getShortName())
                    .replaceAll("%CLIENT_NAME%", personName);
        }
        return postSignatureMessage
                .replaceAll("%BANK_ACCOUNT_NUMBER%", "")
                .replaceAll("%ENTITY_SHORT_NAME%", entity.getShortName())
                .replaceAll("%CLIENT_NAME%", personName);
    }

    public String getPostSignatureLegalFooterReplaced() {
        if (postSignatureLegalFooter == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        do
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        while (calendar.get(Calendar.DAY_OF_MONTH) != 3);
        String replaced = postSignatureLegalFooter.replaceAll("%ripleyNEXT3DOM%", new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));

        return replaced;
    }

    public String getOfferLegalFooterReplaced(LoanOffer loanOffer, UtilService utilService) {
        if (offerLegalFooter == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        do
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        while (calendar.get(Calendar.DAY_OF_MONTH) != 3);
        String replaced = offerLegalFooter.replaceAll("%ripleyNEXT3DOM%", new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));

        if (loanOffer != null) {
            replaced = offerLegalFooter.replaceAll("%TCEA%", loanOffer.getEffectiveAnnualCostRate().toString())
                    .replaceAll("%TEA%", loanOffer.getEffectiveAnualRate().toString())
                    .replaceAll("%AMOUNT%", utilService.doubleMoneyFormat(loanOffer.getAmmount(), loanOffer.getCurrency().getSymbol()))
                    .replaceAll("%INSTALLMENTS%", loanOffer.getInstallments().toString())
                    .replaceAll("%AVG_INSTALLMENT%", utilService.doubleMoneyFormat(loanOffer.getInstallmentAmountAvg(), loanOffer.getCurrency().getSymbol()))
                    .replaceAll("%EXCHANGE_RATE%", utilService.doubleMoneyFormat(loanOffer.getExchangeRate(), loanOffer.getCurrency().getSymbol()))
                    .replaceAll("%FIRST_DUE_DATE%", utilService.dateFormat(loanOffer.getFirstDueDate()))
                    .replaceAll("%CURRENT_DATE%", utilService.dateFormat(new Date()))
                    .replaceAll("%TOTAL_INTEREST%", utilService.doubleMoneyFormat(loanOffer.getTotalScheduleField("totalInterest"), loanOffer.getCurrency().getSymbol()));
        }

        return replaced;
    }

    public Boolean getShowPostSignatureDownloadContractButton() {
        return getSignatureType() == CONTRACT_TYPE_MANUAL && !Arrays.asList(Entity.AELU, Entity.ACCESO, Entity.QAPAQ, Entity.FUNDACION_DE_LA_MUJER).contains(getEntity().getId());
    }

    public List<Integer> getAllApprovalValidationIds(){
        if(approvalValidationConfigs != null){
            return approvalValidationConfigs.stream().map(a -> a.getValidationIds())
                            .flatMap(Collection::stream).distinct().collect(Collectors.toList());
        }
        return null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getFlowType() {
        return flowType;
    }

    public void setFlowType(Integer flowType) {
        this.flowType = flowType;
    }

    public Integer getSignatureType() {
        return signatureType;
    }

    public void setSignatureType(Integer signatureType) {
        this.signatureType = signatureType;
    }


    /*public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }*/

    public Boolean getSendContract() {
        return sendContract;
    }

    public void setSendContract(Boolean sendContract) {
        this.sendContract = sendContract;
    }

    public Boolean getSelfDisbursement() {
        return selfDisbursement;
    }

    public void setSelfDisbursement(Boolean selfDisbursement) {
        this.selfDisbursement = selfDisbursement;
    }

    public String getPostSignatureMessage() {
        return postSignatureMessage;
    }

    public void setPostSignatureMessage(String postSignatureMessage) {
        this.postSignatureMessage = postSignatureMessage;
    }

    public String getLegalFooter() {
        return legalFooter;
    }

    public void setLegalFooter(String legalFooter) {
        this.legalFooter = legalFooter;
    }

    public ProcessQuestionsConfiguration getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(ProcessQuestionsConfiguration evaluation) {
        this.evaluation = evaluation;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Integer getDisbursementType() {
        return disbursementType;
    }

    public void setDisbursementType(Integer disbursementType) {
        this.disbursementType = disbursementType;
    }

    public Boolean getExtranetCreditGeneration() {
        return extranetCreditGeneration;
    }

    public void setExtranetCreditGeneration(Boolean extranetCreditGeneration) {
        this.extranetCreditGeneration = extranetCreditGeneration;
    }

    public List<Integer> getPostSignatureInteractionIds() {
        return postSignatureInteractionIds;
    }

    public void setPostSignatureInteractionIds(List<Integer> postSignatureInteractionIds) {
        this.postSignatureInteractionIds = postSignatureInteractionIds;
    }

    public String getOfferLegalFooter() {
        return offerLegalFooter;
    }

    public void setOfferLegalFooter(String offerLegalFooter) {
        this.offerLegalFooter = offerLegalFooter;
    }

    public String getPostSignatureLegalFooter() {
        return postSignatureLegalFooter;
    }

    public void setPostSignatureLegalFooter(String postSignatureLegalFooter) {
        this.postSignatureLegalFooter = postSignatureLegalFooter;
    }

    public String getCommercialSpeech() {
        return commercialSpeech;
    }

    public void setCommercialSpeech(String commercialSpeech) {
        this.commercialSpeech = commercialSpeech;
    }

    public JSONObject getCreditGenerationEntityAlertInteraction() {
        return creditGenerationEntityAlertInteraction;
    }

    public void setCreditGenerationEntityAlertInteraction(JSONObject creditGenerationEntityAlertInteraction) {
        this.creditGenerationEntityAlertInteraction = creditGenerationEntityAlertInteraction;
    }

    public List<Integer> getRequiredDocuments() {
        return requiredDocuments;
    }

    public void setRequiredDocuments(List<Integer> requiredDocuments) {
        this.requiredDocuments = requiredDocuments;
    }

    public Boolean getExtranetTwoStepGeneration() {
        return extranetTwoStepGeneration;
    }

    public void setExtranetTwoStepGeneration(Boolean extranetTwoStepGeneration) {
        this.extranetTwoStepGeneration = extranetTwoStepGeneration;
    }

    public List<Integer> getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(List<Integer> gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public List<Integer> getFixedDueDate() {
        return fixedDueDate;
    }

    public void setFixedDueDate(List<Integer> fixedDueDate) {
        this.fixedDueDate = fixedDueDate;
    }

    public String getWellcomeSpeech() {
        return wellcomeSpeech;
    }

    public void setWellcomeSpeech(String wellcomeSpeech) {
        this.wellcomeSpeech = wellcomeSpeech;
    }

    public List<Integer> getLoanApplicationAuditTypes() {
        return loanApplicationAuditTypes;
    }

    public void setLoanApplicationAuditTypes(List<Integer> loanApplicationAuditTypes) {
        this.loanApplicationAuditTypes = loanApplicationAuditTypes;
    }

    public Boolean getSolvenGeneratesSchedule() {
        return solvenGeneratesSchedule;
    }

    public void setSolvenGeneratesSchedule(Boolean solvenGeneratesSchedule) {
        this.solvenGeneratesSchedule = solvenGeneratesSchedule;
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(List<Contract> contracts) {
        this.contracts = contracts;
    }

    public List<String> getEntityProductParameter() {
        return entityProductParameter;
    }

    public void setEntityProductParameter(List<String> entityProductParameter) {
        this.entityProductParameter = entityProductParameter;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Boolean getShowWaiting() {
        return showWaiting;
    }

    public void setShowWaiting(Boolean showWaiting) {
        this.showWaiting = showWaiting;
    }

    public Boolean getShowGeneration() {
        return showGeneration;
    }

    public void setShowGeneration(Boolean showGeneration) {
        this.showGeneration = showGeneration;
    }

    public Boolean getShowDisbursement() {
        return showDisbursement;
    }

    public void setShowDisbursement(Boolean showDisbursement) {
        this.showDisbursement = showDisbursement;
    }

    public List<Integer> getScheduleAvoidWeekDays() {
        return scheduleAvoidWeekDays;
    }

    public void setScheduleAvoidWeekDays(List<Integer> scheduleAvoidWeekDays) {
        this.scheduleAvoidWeekDays = scheduleAvoidWeekDays;
    }

    public Boolean getScheduleAvoidHolidays() {
        return scheduleAvoidHolidays;
    }

    public void setScheduleAvoidHolidays(Boolean scheduleAvoidHolidays) {
        this.scheduleAvoidHolidays = scheduleAvoidHolidays;
    }

    public Boolean getMustRunBureau() {
        return mustRunBureau;
    }

    public void setMustRunBureau(Boolean mustRunBureau) {
        this.mustRunBureau = mustRunBureau;
    }

    public boolean mustRunBureau() {
        return getMustRunBureau() != null && getMustRunBureau();
    }

    public String getEntityProduct() {
        return entityProduct;
    }

    public void setEntityProduct(String entityProduct) {
        this.entityProduct = entityProduct;
    }

    public List<Integer> getDisbursementBanks() {
        return disbursementBanks;
    }

    public void setDisbursementBanks(List<Integer> disbursementBanks) {
        this.disbursementBanks = disbursementBanks;
    }

    public Boolean getRequiresDisaggregatedAddress() {
        return requiresDisaggregatedAddress;
    }

    public void setRequiresDisaggregatedAddress(Boolean requiresDisaggregatedAddress) {
        this.requiresDisaggregatedAddress = requiresDisaggregatedAddress;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Boolean getRunFixedDueDate() {
        return runFixedDueDate;
    }

    public void setRunFixedDueDate(Boolean runFixedDueDate) {
        this.runFixedDueDate = runFixedDueDate;
    }

    public List<Integer> getDisbursementInteractionIds() {
        return disbursementInteractionIds;
    }

    public void setDisbursementInteractionIds(List<Integer> disbursementInteractionIds) {
        this.disbursementInteractionIds = disbursementInteractionIds;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public List<Integer> getFixedInstallments() {
        return fixedInstallments;
    }

    public void setFixedInstallments(List<Integer> fixedInstallments) {
        this.fixedInstallments = fixedInstallments;
    }

    public String getFinalQuestionMessage() {
        return finalQuestionMessage;
    }

    public void setFinalQuestionMessage(String finalQuestionMessage) {
        this.finalQuestionMessage = finalQuestionMessage;
    }

    public Boolean getAutomaticApproval() {
        return automaticApproval;
    }

    public void setAutomaticApproval(Boolean automaticApproval) {
        this.automaticApproval = automaticApproval;
    }

    public Boolean getSimulatedOffer() {
        return simulatedOffer;
    }

    public void setSimulatedOffer(Boolean simulatedOffer) {
        this.simulatedOffer = simulatedOffer;
    }

    public Boolean getSignaturePin() {
        return signaturePin;
    }

    public void setSignaturePin(Boolean signaturePin) {
        this.signaturePin = signaturePin;
    }

    public Integer getPriceId() {
        return priceId;
    }

    public void setPriceId(Integer priceId) {
        this.priceId = priceId;
    }

    public Boolean getShowResumeContract() {
        return showResumeContract;
    }

    public void setShowResumeContract(Boolean showResumeContract) {
        this.showResumeContract = showResumeContract;
    }

    public String getOfferScheduleLegalFooter() {
        return offerScheduleLegalFooter;
    }

    public void setOfferScheduleLegalFooter(String offerScheduleLegalFooter) {
        this.offerScheduleLegalFooter = offerScheduleLegalFooter;
    }

    public Boolean getRequireEmailValidationForApproval() {
        return requireEmailValidationForApproval;
    }

    public void setRequireEmailValidationForApproval(Boolean requireEmailValidationForApproval) {
        this.requireEmailValidationForApproval = requireEmailValidationForApproval;
    }

    public EntityProductParamIdentityValidationConfig getEntityProductParamIdentityValidationConfig() {
        return entityProductParamIdentityValidationConfig;
    }

    public void setEntityProductParamIdentityValidationConfig(EntityProductParamIdentityValidationConfig entityProductParamIdentityValidationConfig) {
        this.entityProductParamIdentityValidationConfig = entityProductParamIdentityValidationConfig;
    }

    public Boolean getFixedOffer() {
        return fixedOffer;
    }

    public void setFixedOffer(Boolean fixedOffer) {
        this.fixedOffer = fixedOffer;
    }

    public List<EntityProductParamApprovalValidationConfig> getApprovalValidationConfigs() {
        return approvalValidationConfigs;
    }

    public void setApprovalValidationConfigs(List<EntityProductParamApprovalValidationConfig> approvalValidationConfigs) {
        this.approvalValidationConfigs = approvalValidationConfigs;
    }

    public DisbursementType getDisbursementTypeObject() {
        return disbursementTypeObject;
    }

    public void setDisbursementTypeObject(DisbursementType disbursementTypeObject) {
        this.disbursementTypeObject = disbursementTypeObject;
    }

    public Integer getFixedInstallmentDays() {
        return fixedInstallmentDays;
    }

    public void setFixedInstallmentDays(Integer fixedInstallmentDays) {
        this.fixedInstallmentDays = fixedInstallmentDays;
    }

    public EntityProductParamExtraConfiguration getEntityProductParamExtraConfiguration() {
        return entityProductParamExtraConfiguration;
    }

    public void setEntityProductParamExtraConfiguration(EntityProductParamExtraConfiguration entityProductParamExtraConfiguration) {
        this.entityProductParamExtraConfiguration = entityProductParamExtraConfiguration;
    }
}
