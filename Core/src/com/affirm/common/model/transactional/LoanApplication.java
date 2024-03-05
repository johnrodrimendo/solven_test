package com.affirm.common.model.transactional;

import com.affirm.common.model.BantotalApiData;
import com.affirm.common.model.catalog.Currency;
import com.affirm.common.model.catalog.*;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.JsonUtil;
import com.affirm.common.util.Marshall;
import com.affirm.equifax.ws.ReporteCrediticio;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.*;

/**
 * Created by jrodriguez on 09/06/16.
 */

public class LoanApplication implements Serializable {

    public static Character ORIGIN_WEB = 'W';
    public static Character ORIGIN_MESSENGER = 'M';
    public static Character ORIGIN_AFFILIATOR = 'A';
    public static Character ORIGIN_EXTRANET_ENTITY = 'E';
    public static Character ORIGIN_ALTERNATIVE_LANDING = 'L';
    public static Character ORIGIN_LANDING_LEADS = 'D';
    public static Character ORIGIN_API_REST = 'R';
    public static Character ORIGIN_REFERENCED = 'F';

    public static Character QUESTION_FLOW_ONE_BY_ONE = 'O';
    public static Character QUESTION_FLOW_GROUPED = 'G';
    public static final String LEAD_SALESDOUBLER = "salesdoubler";
    public static final String LEAD_LEADGID = "leadgid";
    public static final String LEAD_TORO = "Toro";

    public static String BANBIF_LOAN_APPLICATION_TYPE_ONLINE = "O";
    public static String BANBIF_LOAN_APPLICATION_TYPE_TELEPHONE = "T";

    public static String AB_TESTING_A = "A";
    public static String AB_TESTING_B = "B";

    public static Character GENERATE_CREDIT_PROCESS_RUNNING = 'R';
    public static Character GENERATE_CREDIT_PROCESS_FAILED = 'F';
    public static Character GENERATE_CREDIT_PROCESS_SUCCESS = 'S';

    public static final int FAMILY_PROTECTION_INSURANCE_TYPE = 1;
    public static final int WITHOUT_DEVOLUTION_INSURANCE_TYPE = 2;
    public static final int WITH_DEVOLUTION_INSURANCE_TYPE = 3;


    public enum EntityCustomDataKeys {
        APPLICATION_PAYMENT_TYPE("applicationPaymentType"),
        BANK_ACCOUNT_STATEMENT("bankAccountStatement"),
        TARJETA_PERUANA_DELIVERY_PLACE("tarjetaPeruanaDeliveryPlace"),
        TARJETA_PERUANA_CARD_COLOR("tarjetaPeruanaCardColor"),
        BANCO_DEL_SOL_LOAN_DESTINATION("bancoDelSolLoanDestination"),
        BANCO_DEL_SOL_LOAN_REASON("bancoDelSolLoanReason"),
        BANCO_DEL_SOL_LOAN_CLIENT_TYPE("bancoDelSolLoanClientType"),
        BANCO_DEL_SOL_ACTIVITY_TYPE("bancoDelSolLoanActivityType"),
        BANCO_DEL_SOL_PROVINCIA_RETIRO("bancoDelSolProvinciaRetiro"),
        BANCO_DEL_SOL_INTERNAL_CREDIT_STATUS("bancoDelSolInternalCreditStatus"),
        CREDIGOB_MONTO_OS("credigobMontoOS"),
        CREDIGOB_NUMERO_OS("credigobNumeroOS"),
        CREDIGOB_FEC_ACTIVACION("credigobFecActivacionOS"),
        CREDIGOB_TIEMPO_PAGO("credigobTiempoPago"),
        CREDIGOB_FEC_VENCIMIENTO("credigobFecVencimientoOS"),
        CREDIGOB_PRCT_PARTICIPACION("credigobPorcentajeParticipacion"),
        CREDIGOB_RANGO_INGRESOS("credigobRangoIngresos"),
        CREDIGOB_RANGO_INGRESOS_EMPRESA("credigobRangoIngresosEmpresa"),
        FDM_PROCEDENCIA_SOLICITUD("fdmProcedenciaSolicitud"),
        FDLM_ACEPTA_ASEGURABILIDAD("fdlmAceptaAsegurabilidad"),
        BANCO_DEL_SOL_ENTITY_USER_CHANNEL_ID("bancoDelSolEntityUserChannelId"),
        BANCO_DEL_SOL_BASE_DATA("bancoDelSolBaseData"),
        BANCO_DEL_SOL_BASE_PROCESS_DATE("bancoDelSolBaseProcessDate"),
        BANBIF_DIRECCION_ENTREGA("banbifDireccionEntrega"),
        BANBIF_TIPO_SOLICITUD("banbifTipoSolicitud"),
        BANBIF_SENTINEL_COD_EVA("banbifSentinelCodEva"),
        BANCO_DEL_SOL_BCRA_DATA("bancoDelSolBcraData"),
        PRESTAMYPE_DATA("prestamypeData"),
        PRESTAMYPE_LEAD_EMAIL_SENT("prestamypeLeadEmailSent"),
        BANCO_DEL_SOL_CAMPAIGN_ID("bancoDelSolCampaignId"),
        BANCO_DEL_SOL_CAMPAIGN_DATA("bancoDelSolCampaignData"),
        BANCO_DEL_SOL_PRODUCT_ID("bancoDelSolProductId"),
        BANBIF_LANDING_AB_TESTING("banbifLandingABTesting"),
        BANBIF_TIPO_TARJETA_DE_BASE("banbifTipoTarjeta"),
        BANBIF_BASE_PREAPROBADA("banbifBasePreaprobada"),
        BANBIF_CONVERSION_VALUE("banbifConversionValue"),
        BANBIF_ADDITIONAL_NATIONALITY_EXIST("banbifAdditionalNationality"),
        BANBIF_SENTINEL_COD_EVALUATIONS("banbifSentinelCodEvaluations"),
        BANBIF_TC_PAYMENT_DAY("banbifTcPaymentDay"),
        BANBIF_TC_MASEFECTIVO_LANDING("banbifTcMasEfectivoLanding"),
        BANCO_AZTECA_AGENCY_SELECTED("bancoAztecaAgencySelected"),
        BANCO_AZTECA_BASE_PREAPROBADA("bancoAztecaBasePreaprobada"),
        PRISMA_BASE_PREAPROBADA("prismaBasePreaprobada"),
        AGENCY_NAME("agencyName"),
        BANCO_AZTECA_LANDING_AB_TESTING("bancoAztecaLandingABTesting"),
        BANCO_AZTECA_BASE_GATEWAY("bancoAztecaBaseGateway"),
        GATEWAY_IS_PAID_STATUS("gatewayIsPaid"),
        BANTOTAL_API_DATA("bantotalApiData"),
        AZTECA_DISBUSERMENT_OPTION("aztecaDisbursementOption"),
        CUSTOM_ENTITY_ACTIVITY_ID("customEntityActivityId"),
        AZTECA_CREDIT_PROCESS_OPTION("aztecaCreditProcessOption"),
        AZTECA_CAMPAIGN_BASE("aztecaCampaignBase"),
        TCONEKTA_INFORMATION_BOT_INITIALIZED("tConektaInformationBotInitialized"),
        TCONEKTA_INFORMATION_SENT("tConektaInformationSent"),
        CUSTOM_ENTITY_PROFESSION_ID("customEntityProfessionId"),
        BANTOTAL_GATEWAY_VIGENTE_DATA("bantotalGatewayVigenteData"),
        GENERATE_CREDIT_PROCESS_ACTIVE("generateCreditProcessActive"),
        AZTECA_INSURANCE_TYPE("aztecaInsuranceType"),
        AZTECA_INSURANCE_PLAN("aztecaInsurancePlan"),
        AZTECA_INSURANCE_COST("aztecaInsuranceCost"),
        BANCO_AZTECA_BASES_GATEWAY("bancoAztecaBasesGateway"),
        BANBIF_CALL_KONECTA_LEAD("banbifCallKonectaLead"),
        GEOLOCATION_IP_ORGANIZATION("geolocationIpOrganization"),

        AZTECA_DUE_DATE_MODIFIED("bancoAztecaDueDateModified"),

        AZTECA_DUE_DATE_MODIFIED_AFTER_CONTRACT("bancoAztecaDueDateModifiedAfterContract"),
        ;

        private String key;

        EntityCustomDataKeys(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }

    private Integer id;
    private Integer userId;
    private Integer personId;
    private String code;
    private LoanApplicationStatus status;
    private Integer amount;
    private Integer installments;
    private Integer loanDays;
    private LoanApplicationReason reason;
    private Product product;
    private String ipAddress;
    private String ipCountryCode;
    private String ipCountryName;
    private String ipRegionCode;
    private Double ipLatitude;
    private Double ipLongitude;
    private Double navLatitude;
    private Double navLongitude;
    private Boolean consentNavGeolocation;
    private String navFormattedAddress;
    private Date registerDate;
    private Date expirationDate;
    private Date firstDueDate;
    private Boolean credit;
    private Integer creditId;
    private Integer updatedSysUserId;
    private Date updatedDate;
    private Double sbsMonthlyInstallment;
    private Double sbsMonthlyInstallmentMortgage;
    private Double admissionTotalIncome;
    private Double requestedInstallment;
    private Double rci;
    private Integer score;
    private Cluster cluster;
    private Integer humanFormId;
    private Boolean signLinkClicked;
    private Date noAuthLinkExpiration;
    private Boolean messengerLink;
    private List<LoanOffer> offers;
    private ReporteCrediticio equifaxResult;
    private Boolean filesUploaded;
    private Employer employer;
    private Character origin;
    private String rejectionReasonLocal;
    private String rejectionHardFilterKey;
    private String rejectionHardFilter;
    private String rejectionPolicyKey;
    private String rejectionPolicy;
    private String clusterSubcluster;
    private Date newLADate;
    private Date peapprovedDate;
    private Date eapprovedDate;
    private Date waitingapprovalDate;
    private Date approvedDate;
    private Date rejectedDate;
    private Date rejectedautomaticallyDate;
    private Date approvedsignedDate;
    private ApplicationRejectionReason rejectionReason;
    private LoanApplicationRegisterType registerType;
    private Integer currentQuestionId;
    private Double downPayment;
    private Currency downPaymentCurrency;
    private List<ProcessQuestionSequence> questionSequence = new ArrayList<>();
    private Integer productCategoryId;
    private Agent agent;
    private Integer selfEvaluationId;
    private Integer selectedEntityId;
    private Integer selectedProductId;
    private String eflSessionUid;
    private ProductCategory productCategory;
    private Integer entityUserId;
    private UserEntity entityUser;
    private String resultEFL;
    private List<ConsolidableDebt> consolidableDebts;
    private Boolean generatedOffers;
    private Vehicle vehicle;
    private String entityApplicationCode;
    private Integer offerQueryBotId;
    private String tokyCall;
    private Boolean approvedAudit;
    private Double disposableIncome;
    private Integer entityId;
    private Integer countryId;
    private List<Integer> preEvaluationQueryBots;
    private Integer currencyId;
    private Currency currency;
    private Integer fraudFlagId;
    private CountryParam country;
    private Double exchangeRate;
    private Integer requiredReferrals;
    private Integer creditAnalystSysUserId;
    private Double percentage;
    private Boolean assistedProcess;
    private Date assistedProcessScheduledCallingDate;
    private Integer offerRejectionId;
    private String gaClientId;
    private String source;
    private String medium;
    private String campaign;
    private String term;
    private String content;
    private String gclid;
    private String userAgent;
    private JSONObject entityCustomData;
    private ProcessQuestion currentProcessQuestion;
    private Date expired;
    private Date leadReferred;
    private Date rejectedAutomaticallyEvaluation;
    private Double guaranteedVehicleloanToValue;
    private Double loanToValue;
    private Double guaranteedVehiclePrice;
    private Integer guaranteedVehicleBrandId;
    private MaintainedCarBrand guaranteedVehicleBrand;
    private String guaranteedVehicleModel;
    private String guaranteedVehicleMileage;
    private Integer guaranteedVehicleYear;
    private Integer guaranteedVehicleCurrencyId;
    private Date guaranteedVehicleAppointmentDate;
    private Integer guaranteedVehicleAppointmentScheduleId;
    private Boolean guaranteedAcceptedVehicle;
    private Boolean smsSent;
    private String guaranteedVehiclePlate;
    private Integer selectedEntityProductParameterId;
    private String guaranteedVehicleAppointmentPlace;
    private Character questionFlow;
    private JSONObject jsLeadParam;
    private Boolean fraudAlertsProcessed;
    private Date entityApplicationExpirationDate;
    private JSONObject jsNotificationTokens;
    private Boolean observed;
    private String observedComment;
    private String rejectionComment;
    private List<Integer> fraudAlertQueryIds;
    private List<Integer> approvalQueryBotIds;
    private Integer entityProductParameterId;
    private String warmiProcessId;
    private List<LoanApplicationFunnelStep> funnelSteps;
    private LoanApplicationAuxData auxData;
    private Integer assignedEntityUserId;
    private List<LoanApplicationApprovalValidation> approvalValidations;

    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setUserId(JsonUtil.getIntFromJson(json, "user_id", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setCode(JsonUtil.getStringFromJson(json, "loan_application_code", null));
        setAmount(JsonUtil.getIntFromJson(json, "amount", null));
        setInstallments(JsonUtil.getIntFromJson(json, "installments", null));
        setLoanDays(JsonUtil.getIntFromJson(json, "loan_days", null));
        if (JsonUtil.getIntFromJson(json, "reason_id", null) != null) {
            setReason(catalog.getLoanApplicationReason(locale, (JsonUtil.getIntFromJson(json, "reason_id", null))));
        }
        if (JsonUtil.getIntFromJson(json, "product_id", null) != null) {
            setProduct(catalog.getProduct((JsonUtil.getIntFromJson(json, "product_id", null))));
        }
        setIpAddress(JsonUtil.getStringFromJson(json, "ip_address", null));
        setIpCountryCode(JsonUtil.getStringFromJson(json, "ip_country_code", null));
        setIpCountryName(JsonUtil.getStringFromJson(json, "ip_country_name", null));
        setIpRegionCode(JsonUtil.getStringFromJson(json, "ip_region_code", null));
        setIpLatitude(JsonUtil.getDoubleFromJson(json, "ip_latitude", null));
        setIpLongitude(JsonUtil.getDoubleFromJson(json, "ip_longitude", null));
        setNavLatitude(JsonUtil.getDoubleFromJson(json, "nav_latitude", null));
        setNavLongitude(JsonUtil.getDoubleFromJson(json, "nav_longitude", null));
        setConsentNavGeolocation(JsonUtil.getBooleanFromJson(json, "consent_nav_geolocation", null));
        setNavFormattedAddress(JsonUtil.getStringFromJson(json, "nav_formatted_address", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setExpirationDate(JsonUtil.getPostgresDateFromJson(json, "expiration_date", null));
        setFirstDueDate(JsonUtil.getPostgresDateFromJson(json, "first_due_date", null));
        setCredit(JsonUtil.getBooleanFromJson(json, "is_credit", null));
        setCreditId(JsonUtil.getIntFromJson(json, "credit_id", null));
        setUpdatedSysUserId(JsonUtil.getIntFromJson(json, "updated_sysuser_id", null));
        setUpdatedDate(JsonUtil.getPostgresDateFromJson(json, "updated_time", null));
        if (JsonUtil.getIntFromJson(json, "application_status_id", null) != null) {
            setStatus(catalog.getLoanApplicationStatus(locale, (JsonUtil.getIntFromJson(json, "application_status_id", null))));
        }
        setSbsMonthlyInstallment(JsonUtil.getDoubleFromJson(json, "sbs_monthly_installment", null));
        setSbsMonthlyInstallmentMortgage(JsonUtil.getDoubleFromJson(json, "sbs_monthly_installment_mortgage", null));
        setAdmissionTotalIncome(JsonUtil.getDoubleFromJson(json, "admission_total_income", null));
        setRequestedInstallment(JsonUtil.getDoubleFromJson(json, "requested_installment", null));
        setRci(JsonUtil.getDoubleFromJson(json, "rci", null));
        setScore(JsonUtil.getIntFromJson(json, "score", null));
        setHumanFormId(JsonUtil.getIntFromJson(json, "human_form_id", null));
        setFilesUploaded(JsonUtil.getBooleanFromJson(json, "files_uploaded", null));
        if (JsonUtil.getIntFromJson(json, "cluster_id", null) != null) {
            setCluster(catalog.getCluster(JsonUtil.getIntFromJson(json, "cluster_id", null), locale));
        }
        setSignLinkClicked(JsonUtil.getBooleanFromJson(json, "il_click_sign_link", false));
        setNoAuthLinkExpiration(JsonUtil.getPostgresDateFromJson(json, "auth_link_expiration", null));
        setMessengerLink(JsonUtil.getBooleanFromJson(json, "messenger_link", null));
        if (JsonUtil.getIntFromJson(json, "employer_id", null) != null) {
            setEmployer(catalog.getEmployer(JsonUtil.getIntFromJson(json, "employer_id", null)));
        }
        if (JsonUtil.getJsonObjectFromJson(json, "equifax_result", null) != null) {
            Marshall marshall = new Marshall();
            ReporteCrediticio reporteCrediticio = marshall.unmarshall(JsonUtil.getJsonObjectFromJson(json, "equifax_result", null).toString(), ReporteCrediticio.class);
            setEquifaxResult(reporteCrediticio);
        }
        setOrigin(JsonUtil.getCharacterFromJson(json, "application_origin", null));
        Integer rejectionId = JsonUtil.getIntFromJson(json, "rejection_reason_id", null);
        if (rejectionId != null) {
            setRejectionReason(catalog.getApplicationRejectionReason(rejectionId));
            if(getRejectionReason() != null)
                setRejectionReasonLocal(getRejectionReason().getReason());
        }
        setRejectionHardFilterKey(JsonUtil.getStringFromJson(json, "hard_filter_message", null));
//        if (getRejectionHardFilterKey() != null && getProduct() != null) {
//            ProductAgeRange ageRange = catalog.getProductAgeRange(getProduct().getId());
//            setRejectionHardFilter(catalog.getMessageSource().getMessage(getRejectionHardFilterKey(), new Object[]{
//                    Configuration.APP_NAME,
//                    ageRange != null ? ageRange.getMinAge() : null,
//                    ageRange != null ? ageRange.getMaxAge() : null,
//                    Configuration.SOLES_CURRENCY}, locale));
//        }
        setRejectionPolicyKey(JsonUtil.getStringFromJson(json, "policy_message", null));
//        if (getRejectionPolicyKey() != null && getProduct() != null) {
//            ProductAgeRange ageRange = catalog.getProductAgeRange(getProduct().getId());
//            setRejectionPolicy(catalog.getMessageSource().getMessage(getRejectionPolicyKey(), new Object[]{Configuration.APP_NAME, ageRange.getMinAge(), ageRange.getMaxAge(), Configuration.SOLES_CURRENCY}, locale));
//        }
        setClusterSubcluster(JsonUtil.getStringFromJson(json, "cluster", "?") + " - " + JsonUtil.getStringFromJson(json, "sub_cluster", "?"));
        setNewLADate(JsonUtil.getPostgresDateFromJson(json, "new", null));
        setPeapprovedDate(JsonUtil.getPostgresDateFromJson(json, "peapproved", null));
        setEapprovedDate(JsonUtil.getPostgresDateFromJson(json, "eapproved", null));
        setWaitingapprovalDate(JsonUtil.getPostgresDateFromJson(json, "waitingapproval", null));
        setApprovedDate(JsonUtil.getPostgresDateFromJson(json, "approved", null));
        setRejectedDate(JsonUtil.getPostgresDateFromJson(json, "rejected", null));
        setRejectedautomaticallyDate(JsonUtil.getPostgresDateFromJson(json, "rejectedautomatically", null));
        setApprovedsignedDate(JsonUtil.getPostgresDateFromJson(json, "approvedsigned", null));
        Integer registerType = JsonUtil.getIntFromJson(json, "register_type_id", null);
        if (registerType != null) {
            setRegisterType(catalog.getLoanApplicationRegisterTypeById(registerType));
        }
        setCurrentQuestionId(JsonUtil.getIntFromJson(json, "current_process_question_id", null));
        if (getCurrentQuestionId() != null) {
            setCurrentProcessQuestion(catalog.getProcessQuestion(getCurrentQuestionId()));
        }
        if (JsonUtil.getJsonArrayFromJson(json, "proccess_question_id_sequence", null) != null) {
            setQuestionSequence(
                    new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "proccess_question_id_sequence", null).toString(), new TypeToken<ArrayList<ProcessQuestionSequence>>() {
                    }.getType()));
        }
        setDownPayment(JsonUtil.getDoubleFromJson(json, "down_payment", null));
        setProductCategoryId(JsonUtil.getIntFromJson(json, "category_id", null));
        if (JsonUtil.getIntFromJson(json, "form_assistant_id", null) != null)
            setAgent(catalog.getAgent(JsonUtil.getIntFromJson(json, "form_assistant_id", null)));
        setSelfEvaluationId(JsonUtil.getIntFromJson(json, "self_evaluation_id", null));
        if (JsonUtil.getIntFromJson(json, "selected_entity_id", null) != null)
            setSelectedEntityId(JsonUtil.getIntFromJson(json, "selected_entity_id", null));

        if (JsonUtil.getIntFromJson(json, "selected_product_id", null) != null)
            setSelectedProductId(JsonUtil.getIntFromJson(json, "selected_product_id", null));
        setEflSessionUid(JsonUtil.getStringFromJson(json, "efl_session_uid", null));
        if (JsonUtil.getIntFromJson(json, "category_id", null) != null) {
            setProductCategory(catalog.getCatalogById(ProductCategory.class, JsonUtil.getIntFromJson(json, "category_id", null), locale));
        }
        setEntityUserId(JsonUtil.getIntFromJson(json, "entity_user_id", null));

        setResultEFL(JsonUtil.getStringFromJson(json, "efl_score_confidence", null));
        setGeneratedOffers(JsonUtil.getBooleanFromJson(json, "pending_analyst_offer", null));
        if (JsonUtil.getIntFromJson(json, "vehicle_id", null) != null) {
            setVehicle(catalog.getVehicle(JsonUtil.getIntFromJson(json, "vehicle_id", null), locale));
        }
        setEntityApplicationCode(JsonUtil.getStringFromJson(json, "entity_application_code", null));
        setOfferQueryBotId(JsonUtil.getIntFromJson(json, "offers_bot_id", null));
        setApprovedAudit(JsonUtil.getBooleanFromJson(json, "approved_audit", null));
        setDisposableIncome(JsonUtil.getDoubleFromJson(json, "disposable_income", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setCountryId(JsonUtil.getIntFromJson(json, "country_id", null));
        if (JsonUtil.getJsonArrayFromJson(json, "js_pre_eval_bot_id", null) != null) {
            setPreEvaluationQueryBots(new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "js_pre_eval_bot_id", null).toString(), new TypeToken<ArrayList<Integer>>() {
            }.getType()));
        }
        setCurrencyId(JsonUtil.getIntFromJson(json, "currency_id", null));
        if (getCurrencyId() != null) {
            setCurrency(catalog.getCurrency(getCurrencyId()));
        }
        setFraudFlagId(JsonUtil.getIntFromJson(json, "fraud_flag_id", null));

        setCountry(catalog.getCountryParam(getCountryId()));
        if (JsonUtil.getIntFromJson(json, "down_payment_currency_id", null) != null) {
            setDownPaymentCurrency(catalog.getCurrency(JsonUtil.getIntFromJson(json, "down_payment_currency_id", null)));
        }
        setExchangeRate(JsonUtil.getDoubleFromJson(json, "exchange_rate", null));
        setRequiredReferrals(JsonUtil.getIntFromJson(json, "required_referrals", null));
        setCreditAnalystSysUserId(JsonUtil.getIntFromJson(json, "credit_analyst_sysuser_id", null));
        setGaClientId(JsonUtil.getStringFromJson(json, "ga_client_id", null));
        setSource(JsonUtil.getStringFromJson(json, "source", null));
        setMedium(JsonUtil.getStringFromJson(json, "medium", null));
        setCampaign(JsonUtil.getStringFromJson(json, "campaign", null));
        setUserAgent(JsonUtil.getStringFromJson(json, "user_agent", null));
        setAssistedProcessScheduledCallingDate(JsonUtil.getPostgresDateFromJson(json, "assisted_process_scheduled_calling_date", null));
        setAssistedProcess(JsonUtil.getBooleanFromJson(json, "assisted_process", null));
        setOfferRejectionId(JsonUtil.getIntFromJson(json, "offer_rejection_id", null));

        JSONArray jsonArray = JsonUtil.getJsonArrayFromJson(json, "percentage_progress", null);

        double percentage = 0.0;

        if (jsonArray != null) {
            /*for (int i = 0 ; i < jsonArray.length() ; ++i) {
                percentage = jsonArray.getDouble(i);
            }*/
            if (jsonArray.length() > 0)
                percentage = jsonArray.getDouble(jsonArray.length() - 1);
        }

        setPercentage(percentage);

        setEntityCustomData(JsonUtil.getJsonObjectFromJson(json, "entity_custom_data", new JSONObject()));
        setExpired(JsonUtil.getPostgresDateFromJson(json, "expired", null));
        setLeadReferred(JsonUtil.getPostgresDateFromJson(json, "leadreferred", null));
        setRejectedAutomaticallyEvaluation(JsonUtil.getPostgresDateFromJson(json, "rejectedautomaticallyevaluation", null));
        setGuaranteedVehiclePrice(JsonUtil.getDoubleFromJson(json, "guaranteed_vehicle_price", null));
        if (getGuaranteedVehiclePrice() != null)
            setLoanToValue((getAmount() * 100) / getGuaranteedVehiclePrice());
        gclid = JsonUtil.getStringFromJson(json, "gclid", null);

        setGuaranteedVehicleBrandId(JsonUtil.getIntFromJson(json, "guaranteed_vehicle_maintained_car_brand_id", null));
        if (getGuaranteedVehicleBrandId() != null) {
            setGuaranteedVehicleBrand(catalog.getMaintainedCarBrand(getGuaranteedVehicleBrandId()));
        }
        setGuaranteedVehicleModel(JsonUtil.getStringFromJson(json, "guaranteed_vehicle_maintained_model", null));
        setGuaranteedVehicleMileage(JsonUtil.getStringFromJson(json, "guaranteed_vehicle_mileage", null));
        setGuaranteedVehicleYear(JsonUtil.getIntFromJson(json, "guaranteed_vehicle_year", null));
        setGuaranteedVehicleCurrencyId(JsonUtil.getIntFromJson(json, "guaranteed_vehicle_currency_id", null));
        setGuaranteedVehicleAppointmentDate(JsonUtil.getPostgresDateFromJson(json, "guaranteed_vehicle_appointment_date", null));
        setGuaranteedVehicleAppointmentScheduleId(JsonUtil.getIntFromJson(json, "guaranteed_vehicle_appointment_schedule_id", null));
        setGuaranteedAcceptedVehicle(JsonUtil.getBooleanFromJson(json, "guaranteed_vehicle_accepted_vehicle", null));
        setSmsSent(JsonUtil.getBooleanFromJson(json, "sms_sent", null));
        setGuaranteedVehiclePlate(JsonUtil.getStringFromJson(json, "guaranteed_vehicle_plate", null));
        setSelectedEntityProductParameterId(JsonUtil.getIntFromJson(json, "selected_entity_product_parameter_id", null));
        setGuaranteedVehicleAppointmentPlace(JsonUtil.getStringFromJson(json, "guaranteed_appointment_place", null));
        setQuestionFlow(JsonUtil.getCharacterFromJson(json, "question_flow", null));
        setJsLeadParam(JsonUtil.getJsonObjectFromJson(json, "js_lead_params", null));
        setFraudAlertsProcessed(JsonUtil.getBooleanFromJson(json, "fraud_alert_processed", null));
        setEntityApplicationExpirationDate(JsonUtil.getPostgresDateFromJson(json, "entity_application_expiration_date", null));
        setJsNotificationTokens(JsonUtil.getJsonObjectFromJson(json, "notification_tokens", null));
        isObserved(JsonUtil.getBooleanFromJson(json, "is_observed", null));
        setRejectionComment(JsonUtil.getStringFromJson(json, "rejection_comment", null));

        setFraudAlertQueryIds(new ArrayList<>());
        JSONArray fraudAlertQueryIdsJson = JsonUtil.getJsonArrayFromJson(json, "js_fraud_alert_query_id", null);
        if(fraudAlertQueryIdsJson != null){
            for(int i=0; i<fraudAlertQueryIdsJson.length(); i++){
                getFraudAlertQueryIds().add(fraudAlertQueryIdsJson.getInt(i));
            }
        }

        JSONArray approvalQueryBotIdsJson = JsonUtil.getJsonArrayFromJson(json, "js_approve_query_id", null);
        if (approvalQueryBotIdsJson != null) {
            setApprovalQueryBotIds(new ArrayList<>());

            for (int i = 0; i < approvalQueryBotIdsJson.length(); i++) {
                getApprovalQueryBotIds().add(approvalQueryBotIdsJson.getInt(i));
            }
        }

        setEntityProductParameterId(JsonUtil.getIntFromJson(json, "entity_product_parameter_id", null));
        setWarmiProcessId(JsonUtil.getStringFromJson(json, "warmi_process_id", null));
        setFunnelSteps(new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "js_funnel_step", new JSONArray()).toString(), new TypeToken<ArrayList<LoanApplicationFunnelStep>>() {
        }.getType()));
        setAuxData(new Gson().fromJson(JsonUtil.getJsonObjectFromJson(json, "js_aux_data", new JSONObject()).toString(), LoanApplicationAuxData.class));
        setAssignedEntityUserId(JsonUtil.getIntFromJson(json, "assigned_entity_user_id", null));
        setApprovalValidations(new Gson().fromJson(JsonUtil.getJsonArrayFromJson(json, "js_approval_validations", new JSONArray()).toString(), new TypeToken<ArrayList<LoanApplicationApprovalValidation>>() {
        }.getType()));
    }

    public ProductMaxMinParameter getProductMaxMinParameter() {
        return product != null ? product.getProductParams(getCountryId()) : null;
    }

    public LoanOffer getSelectedLoanOffer() {
        if (offers == null)
            return null;
        Optional<LoanOffer> selectedOffer = offers.stream().filter(o -> o.getSelected()).findFirst();
        return selectedOffer.isPresent() ? selectedOffer.get() : null;
    }

    public String getOriginName() {
        if (origin != null) {
            return origin == 'W' ? "Web" : (origin == 'M' ? "Messenger" : "");
        }
        return null;
    }

    public String getEntityCustomData(String key) {
        if (entityCustomData == null)
            return null;

        return JsonUtil.getStringFromJson(entityCustomData, key, null);
    }

    public String getApplicantType() {
        return (entityUserId == null) ? "U" : "F";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Integer getLoanDays() {
        return loanDays;
    }

    public void setLoanDays(Integer loanDays) {
        this.loanDays = loanDays;
    }

    public LoanApplicationReason getReason() {
        return reason;
    }

    public void setReason(LoanApplicationReason reason) {
        this.reason = reason;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpCountryCode() {
        return ipCountryCode;
    }

    public void setIpCountryCode(String ipCountryCode) {
        this.ipCountryCode = ipCountryCode;
    }

    public String getIpCountryName() {
        return ipCountryName;
    }

    public void setIpCountryName(String ipCountryName) {
        this.ipCountryName = ipCountryName;
    }

    public String getIpRegionCode() {
        return ipRegionCode;
    }

    public void setIpRegionCode(String ipRegionCode) {
        this.ipRegionCode = ipRegionCode;
    }

    public Double getIpLatitude() {
        return ipLatitude;
    }

    public void setIpLatitude(Double ipLatitude) {
        this.ipLatitude = ipLatitude;
    }

    public Double getIpLongitude() {
        return ipLongitude;
    }

    public void setIpLongitude(Double ipLongitude) {
        this.ipLongitude = ipLongitude;
    }

    public Double getNavLatitude() {
        return navLatitude;
    }

    public void setNavLatitude(Double navLatitude) {
        this.navLatitude = navLatitude;
    }

    public Double getNavLongitude() {
        return navLongitude;
    }

    public void setNavLongitude(Double navLongitude) {
        this.navLongitude = navLongitude;
    }

    public String getNavFormattedAddress() {
        return navFormattedAddress;
    }

    public void setNavFormattedAddress(String navFormattedAddress) {
        this.navFormattedAddress = navFormattedAddress;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public List<LoanOffer> getOffers() {
        return offers;
    }

    public void setOffers(List<LoanOffer> offers) {
        this.offers = offers;
    }

    public Boolean getCredit() {
        return credit;
    }

    public void setCredit(Boolean credit) {
        this.credit = credit;
    }

    public Integer getUpdatedSysUserId() {
        return updatedSysUserId;
    }

    public void setUpdatedSysUserId(Integer updatedSysUserId) {
        this.updatedSysUserId = updatedSysUserId;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public LoanApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(LoanApplicationStatus status) {
        this.status = status;
    }

    public Integer getCreditId() {
        return creditId;
    }

    public void setCreditId(Integer creditId) {
        this.creditId = creditId;
    }

    public Double getSbsMonthlyInstallment() {
        return sbsMonthlyInstallment;
    }

    public void setSbsMonthlyInstallment(Double sbsMonthlyInstallment) {
        this.sbsMonthlyInstallment = sbsMonthlyInstallment;
    }

    public Double getSbsMonthlyInstallmentMortgage() {
        return sbsMonthlyInstallmentMortgage;
    }

    public void setSbsMonthlyInstallmentMortgage(Double sbsMonthlyInstallmentMortgage) {
        this.sbsMonthlyInstallmentMortgage = sbsMonthlyInstallmentMortgage;
    }

    public Double getAdmissionTotalIncome() {
        return admissionTotalIncome;
    }

    public void setAdmissionTotalIncome(Double admissionTotalIncome) {
        this.admissionTotalIncome = admissionTotalIncome;
    }

    public Double getRequestedInstallment() {
        return requestedInstallment;
    }

    public void setRequestedInstallment(Double requestedInstallment) {
        this.requestedInstallment = requestedInstallment;
    }

    public Double getRci() {
        return rci;
    }

    public void setRci(Double rci) {
        this.rci = rci;
    }

    public Date getFirstDueDate() {
        return firstDueDate;
    }

    public void setFirstDueDate(Date firstDueDate) {
        this.firstDueDate = firstDueDate;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public Integer getHumanFormId() {
        return humanFormId;
    }

    public void setHumanFormId(Integer humanFormId) {
        this.humanFormId = humanFormId;
    }

    public ReporteCrediticio getEquifaxResult() {
        return equifaxResult;
    }

    public void setEquifaxResult(ReporteCrediticio equifaxResult) {
        this.equifaxResult = equifaxResult;
    }

    public Boolean getSignLinkClicked() {
        return signLinkClicked;
    }

    public void setSignLinkClicked(Boolean signLinkClicked) {
        this.signLinkClicked = signLinkClicked;
    }

    public Date getNoAuthLinkExpiration() {
        return noAuthLinkExpiration;
    }

    public void setNoAuthLinkExpiration(Date noAuthLinkExpiration) {
        this.noAuthLinkExpiration = noAuthLinkExpiration;
    }

    public Boolean getFilesUploaded() {
        return filesUploaded;
    }

    public void setFilesUploaded(Boolean filesUploaded) {
        this.filesUploaded = filesUploaded;
    }

    public Boolean getMessengerLink() {
        return messengerLink;
    }

    public void setMessengerLink(Boolean messengerLink) {
        this.messengerLink = messengerLink;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    public Character getOrigin() {
        return origin;
    }

    public void setOrigin(Character origin) {
        this.origin = origin;
    }

    public Boolean getConsentNavGeolocation() {
        return consentNavGeolocation;
    }

    public void setConsentNavGeolocation(Boolean consentNavGeolocation) {
        this.consentNavGeolocation = consentNavGeolocation;
    }

    public String getRejectionHardFilterKey() {
        return rejectionHardFilterKey;
    }

    public void setRejectionHardFilterKey(String rejectionHardFilterKey) {
        this.rejectionHardFilterKey = rejectionHardFilterKey;
    }

    public String getRejectionPolicyKey() {
        return rejectionPolicyKey;
    }

    public void setRejectionPolicyKey(String rejectionPolicyKey) {
        this.rejectionPolicyKey = rejectionPolicyKey;
    }

    public String getRejectionReasonLocal() {
        return rejectionReasonLocal;
    }

    public void setRejectionReasonLocal(String rejectionReasonLocal) {
        this.rejectionReasonLocal = rejectionReasonLocal;
    }

    public String getClusterSubcluster() {
        return clusterSubcluster;
    }

    public void setClusterSubcluster(String clusterSubcluster) {
        this.clusterSubcluster = clusterSubcluster;
    }

    public Date getNewLADate() {
        return newLADate;
    }

    public void setNewLADate(Date newLADate) {
        this.newLADate = newLADate;
    }

    public Date getPeapprovedDate() {
        return peapprovedDate;
    }

    public void setPeapprovedDate(Date peapprovedDate) {
        this.peapprovedDate = peapprovedDate;
    }

    public Date getEapprovedDate() {
        return eapprovedDate;
    }

    public void setEapprovedDate(Date eapprovedDate) {
        this.eapprovedDate = eapprovedDate;
    }

    public Date getWaitingapprovalDate() {
        return waitingapprovalDate;
    }

    public void setWaitingapprovalDate(Date waitingapprovalDate) {
        this.waitingapprovalDate = waitingapprovalDate;
    }

    public Date getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public Date getRejectedDate() {
        return rejectedDate;
    }

    public void setRejectedDate(Date rejectedDate) {
        this.rejectedDate = rejectedDate;
    }

    public Date getRejectedautomaticallyDate() {
        return rejectedautomaticallyDate;
    }

    public void setRejectedautomaticallyDate(Date rejectedautomaticallyDate) {
        this.rejectedautomaticallyDate = rejectedautomaticallyDate;
    }

    public Date getApprovedsignedDate() {
        return approvedsignedDate;
    }

    public void setApprovedsignedDate(Date approvedsignedDate) {
        this.approvedsignedDate = approvedsignedDate;
    }

    public ApplicationRejectionReason getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(ApplicationRejectionReason rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LoanApplicationRegisterType getRegisterType() {
        return registerType;
    }

    public void setRegisterType(LoanApplicationRegisterType registerType) {
        this.registerType = registerType;
    }

    public String getRejectionHardFilter() {
        return rejectionHardFilter;
    }

    public void setRejectionHardFilter(String rejectionHardFilter) {
        this.rejectionHardFilter = rejectionHardFilter;
    }

    public String getRejectionPolicy() {
        return rejectionPolicy;
    }

    public void setRejectionPolicy(String rejectionPolicy) {
        this.rejectionPolicy = rejectionPolicy;
    }

    public Integer getCurrentQuestionId() {
        return currentQuestionId;
    }

    public void setCurrentQuestionId(Integer currentQuestionId) {
        this.currentQuestionId = currentQuestionId;
    }

    public List<ProcessQuestionSequence> getQuestionSequence() {
        return questionSequence;
    }

    public void setQuestionSequence(List<ProcessQuestionSequence> questionSequence) {
        this.questionSequence = questionSequence;
    }

    public Double getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(Double downPayment) {
        this.downPayment = downPayment;
    }

    public Integer getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(Integer productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Integer getSelfEvaluationId() {
        return selfEvaluationId;
    }

    public void setSelfEvaluationId(Integer selfEvaluationId) {
        this.selfEvaluationId = selfEvaluationId;
    }

    public Integer getSelectedEntityId() {
        return selectedEntityId;
    }

    public void setSelectedEntityId(Integer selectedEntityId) {
        this.selectedEntityId = selectedEntityId;
    }

    public String getEflSessionUid() {
        return eflSessionUid;
    }

    public void setEflSessionUid(String eflSessionUid) {
        this.eflSessionUid = eflSessionUid;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public Integer getEntityUserId() {
        return entityUserId;
    }

    public void setEntityUserId(Integer entityUserId) {
        this.entityUserId = entityUserId;
    }

    public String getResultEFL() {
        return resultEFL;
    }

    public void setResultEFL(String resultEFL) {
        this.resultEFL = resultEFL;
    }

    public List<ConsolidableDebt> getConsolidableDebts() {
        return consolidableDebts;
    }

    public void setConsolidableDebts(List<ConsolidableDebt> consolidableDebts) {
        this.consolidableDebts = consolidableDebts;
    }

    public Boolean getGeneratedOffers() {
        return generatedOffers;
    }

    public void setGeneratedOffers(Boolean generatedOffers) {
        this.generatedOffers = generatedOffers;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public String getEntityApplicationCode() {
        return entityApplicationCode;
    }

    public void setEntityApplicationCode(String entityApplicationCode) {
        this.entityApplicationCode = entityApplicationCode;
    }

    public UserEntity getEntityUser() {
        return entityUser;
    }

    public void setEntityUser(UserEntity entityUser) {
        this.entityUser = entityUser;
    }

    public Integer getOfferQueryBotId() {
        return offerQueryBotId;
    }

    public void setOfferQueryBotId(Integer offerQueryBotId) {
        this.offerQueryBotId = offerQueryBotId;
    }

    public String getTokyCall() {
        return tokyCall;
    }

    public void setTokyCall(String tokyCall) {
        this.tokyCall = tokyCall;
    }

    public Boolean getApprovedAudit() {
        return approvedAudit;
    }

    public void setApprovedAudit(Boolean approvedAudit) {
        this.approvedAudit = approvedAudit;
    }

    public Double getDisposableIncome() {
        return disposableIncome;
    }

    public void setDisposableIncome(Double disposableIncome) {
        this.disposableIncome = disposableIncome;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getFraudFlagId() {
        return fraudFlagId;
    }

    public void setFraudFlagId(Integer fraudFlagId) {
        this.fraudFlagId = fraudFlagId;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public List<Integer> getPreEvaluationQueryBots() {
        return preEvaluationQueryBots;
    }

    public void setPreEvaluationQueryBots(List<Integer> preEvaluationQueryBots) {
        this.preEvaluationQueryBots = preEvaluationQueryBots;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setCountry(CountryParam country) {
        this.country = country;
    }

    public Currency getDownPaymentCurrency() {
        return downPaymentCurrency;
    }

    public void setDownPaymentCurrency(Currency downPaymentCurrency) {
        this.downPaymentCurrency = downPaymentCurrency;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public CountryParam getCountry() {
        return country;
    }

    public void setRequiredReferrals(Integer requiredReferrals) {
        this.requiredReferrals = requiredReferrals;
    }

    public Integer getRequiredReferrals() {
        return requiredReferrals;
    }

    public Integer getCreditAnalystSysUserId() {
        return creditAnalystSysUserId;
    }

    public void setCreditAnalystSysUserId(Integer creditAnalystSysUserId) {
        this.creditAnalystSysUserId = creditAnalystSysUserId;
    }

    public Double getNetAmount() {
        return (amount == null ? 0.0 : amount) - (downPayment == null ? 0.0 : downPayment);
    }

    public String getGaClientId() {
        return gaClientId;
    }

    public void setGaClientId(String gaClientId) {
        this.gaClientId = gaClientId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getLoanApplicationAmmount(UtilService utilService) {
        String loanApplicationAmount = utilService.integerMoneyFormat(amount);
        if (installments != null) {
            loanApplicationAmount = loanApplicationAmount + " @ " + installments + "m";
        } else if (loanDays != null) {
            loanApplicationAmount = loanApplicationAmount + " @ " + loanDays + "d";
        }
        return loanApplicationAmount;
    }

    public String getLoanApplicationAmmount(UtilService utilService, String symbol, String separator) {
        String loanApplicationAmount = utilService.integerMoneyFormat(amount, symbol, separator);
        if (installments != null) {
            loanApplicationAmount = loanApplicationAmount + " @ " + installments + "m";
        } else if (installments != null) {
            loanApplicationAmount = loanApplicationAmount + " @ " + installments + "d";
        }
        return loanApplicationAmount;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Integer getSelectedProductId() {
        return selectedProductId;
    }

    public void setSelectedProductId(Integer selectedProductId) {
        this.selectedProductId = selectedProductId;
    }

    public JSONObject getEntityCustomData() {
        return entityCustomData;
    }

    public void setEntityCustomData(JSONObject entityCustomData) {
        this.entityCustomData = entityCustomData;
    }

    public Date getAssistedProcessScheduledCallingDate() {
        return assistedProcessScheduledCallingDate;
    }

    public void setAssistedProcessScheduledCallingDate(Date assistedProcessScheduledCallingDate) {
        this.assistedProcessScheduledCallingDate = assistedProcessScheduledCallingDate;
    }

    public Boolean getAssistedProcess() {
        return assistedProcess;
    }

    public void setAssistedProcess(Boolean assistedProcess) {
        this.assistedProcess = assistedProcess;
    }

    public ProcessQuestion getCurrentProcessQuestion() {
        return currentProcessQuestion;
    }

    public void setCurrentProcessQuestion(ProcessQuestion currentProcessQuestion) {
        this.currentProcessQuestion = currentProcessQuestion;
    }

    public Integer getOfferRejectionId() {
        return offerRejectionId;
    }

    public void setOfferRejectionId(Integer offerRejectionId) {
        this.offerRejectionId = offerRejectionId;
    }

    public String getGclid() {
        return gclid;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public Date getLeadReferred() {
        return leadReferred;
    }

    public void setLeadReferred(Date leadReferred) {
        this.leadReferred = leadReferred;
    }

    public Date getRejectedAutomaticallyEvaluation() {
        return rejectedAutomaticallyEvaluation;
    }

    public void setRejectedAutomaticallyEvaluation(Date rejectedAutomaticallyEvaluation) {
        this.rejectedAutomaticallyEvaluation = rejectedAutomaticallyEvaluation;
    }

    public Double getGuaranteedVehicleloanToValue() {
        return guaranteedVehicleloanToValue;
    }

    public void setGuaranteedVehicleloanToValue(Double guaranteedVehicleloanToValue) {
        this.guaranteedVehicleloanToValue = guaranteedVehicleloanToValue;
    }

    public Double getGuaranteedVehiclePrice() {
        return guaranteedVehiclePrice;
    }

    public void setGuaranteedVehiclePrice(Double guaranteedVehiclePrice) {
        this.guaranteedVehiclePrice = guaranteedVehiclePrice;
    }

    public Integer getGuaranteedVehicleBrandId() {
        return guaranteedVehicleBrandId;
    }

    public void setGuaranteedVehicleBrandId(Integer guaranteedVehicleBrandId) {
        this.guaranteedVehicleBrandId = guaranteedVehicleBrandId;
    }

    public String getGuaranteedVehicleMileage() {
        return guaranteedVehicleMileage;
    }

    public void setGuaranteedVehicleMileage(String guaranteedVehicleMileage) {
        this.guaranteedVehicleMileage = guaranteedVehicleMileage;
    }

    public Integer getGuaranteedVehicleYear() {
        return guaranteedVehicleYear;
    }

    public void setGuaranteedVehicleYear(Integer guaranteedVehicleYear) {
        this.guaranteedVehicleYear = guaranteedVehicleYear;
    }

    public Integer getGuaranteedVehicleCurrencyId() {
        return guaranteedVehicleCurrencyId;
    }

    public void setGuaranteedVehicleCurrencyId(Integer guaranteedVehicleCurrencyId) {
        this.guaranteedVehicleCurrencyId = guaranteedVehicleCurrencyId;
    }

    public Date getGuaranteedVehicleAppointmentDate() {
        return guaranteedVehicleAppointmentDate;
    }

    public void setGuaranteedVehicleAppointmentDate(Date guaranteedVehicleAppointmentDate) {
        this.guaranteedVehicleAppointmentDate = guaranteedVehicleAppointmentDate;
    }

    public Integer getGuaranteedVehicleAppointmentScheduleId() {
        return guaranteedVehicleAppointmentScheduleId;
    }

    public void setGuaranteedVehicleAppointmentScheduleId(Integer guaranteedVehicleAppointmentScheduleId) {
        this.guaranteedVehicleAppointmentScheduleId = guaranteedVehicleAppointmentScheduleId;
    }

    public Boolean getGuaranteedAcceptedVehicle() {
        return guaranteedAcceptedVehicle;
    }

    public void setGuaranteedAcceptedVehicle(Boolean guaranteedAcceptedVehicle) {
        this.guaranteedAcceptedVehicle = guaranteedAcceptedVehicle;
    }

    public Double getLoanToValue() {
        return loanToValue;
    }

    public void setLoanToValue(Double loanToValue) {
        this.loanToValue = loanToValue;
    }

    public String getGuaranteedVehicleModel() {
        return guaranteedVehicleModel;
    }

    public void setGuaranteedVehicleModel(String guaranteedVehicleModel) {
        this.guaranteedVehicleModel = guaranteedVehicleModel;
    }

    public MaintainedCarBrand getGuaranteedVehicleBrand() {
        return guaranteedVehicleBrand;
    }

    public void setGuaranteedVehicleBrand(MaintainedCarBrand guaranteedVehicleBrand) {
        this.guaranteedVehicleBrand = guaranteedVehicleBrand;
    }

    public Boolean getSmsSent() {
        return smsSent;
    }

    public void setSmsSent(Boolean smsSent) {
        this.smsSent = smsSent;
    }

    public String getGuaranteedVehiclePlate() {
        return guaranteedVehiclePlate;
    }

    public void setGuaranteedVehiclePlate(String guaranteedVehiclePlate) {
        this.guaranteedVehiclePlate = guaranteedVehiclePlate;
    }

    public Integer getSelectedEntityProductParameterId() {
        return selectedEntityProductParameterId;
    }

    public void setSelectedEntityProductParameterId(Integer selectedEntityProductParameterId) {
        this.selectedEntityProductParameterId = selectedEntityProductParameterId;
    }

    public String getGuaranteedVehicleAppointmentPlace() {
        return guaranteedVehicleAppointmentPlace;
    }

    public void setGuaranteedVehicleAppointmentPlace(String guaranteedVehicleAppointmentPlace) {
        this.guaranteedVehicleAppointmentPlace = guaranteedVehicleAppointmentPlace;
    }

    public Character getQuestionFlow() {
        return questionFlow;
    }

    public void setQuestionFlow(Character questionFlow) {
        this.questionFlow = questionFlow;
    }

    public JSONObject getJsLeadParam() {
        return jsLeadParam;
    }

    public void setJsLeadParam(JSONObject jsLeadParam) {
        this.jsLeadParam = jsLeadParam;
    }

    public void setGclid(String gclid) {
        this.gclid = gclid;
    }

    public Boolean getFraudAlertsProcessed() {
        return fraudAlertsProcessed;
    }

    public void setFraudAlertsProcessed(Boolean fraudAlertsProcessed) {
        this.fraudAlertsProcessed = fraudAlertsProcessed;
    }

    public Date getEntityApplicationExpirationDate() {
        return entityApplicationExpirationDate;
    }

    public void setEntityApplicationExpirationDate(Date entityApplicationExpirationDate) {
        this.entityApplicationExpirationDate = entityApplicationExpirationDate;
    }

    public JSONObject getJsNotificationTokens() {
        return jsNotificationTokens;
    }

    public void setJsNotificationTokens(JSONObject jsNotificationTokens) {
        this.jsNotificationTokens = jsNotificationTokens;
    }

    public Boolean isObserved() {
        return observed;
    }

    public void isObserved(Boolean observed) {
        this.observed = observed;
    }

    public String getObservedComment() {
        return observedComment;
    }

    public void setObservedComment(String observedComment) {
        this.observedComment = observedComment;
    }

    public String getRejectionComment() {
        return rejectionComment;
    }

    public void setRejectionComment(String rejectionComment) {
        this.rejectionComment = rejectionComment;
    }

    public List<Integer> getFraudAlertQueryIds() {
        return fraudAlertQueryIds;
    }

    public void setFraudAlertQueryIds(List<Integer> fraudAlertQueryIds) {
        this.fraudAlertQueryIds = fraudAlertQueryIds;
    }

    public List<Integer> getApprovalQueryBotIds() {
        return approvalQueryBotIds;
    }

    public void setApprovalQueryBotIds(List<Integer> approvalQueryBotIds) {
        this.approvalQueryBotIds = approvalQueryBotIds;
    }

    public Integer getEntityProductParameterId() {
        return entityProductParameterId;
    }

    public void setEntityProductParameterId(Integer entityProductParameterId) {
        this.entityProductParameterId = entityProductParameterId;
    }

    public String getWarmiProcessId() {
        return warmiProcessId;
    }

    public void setWarmiProcessId(String warmiProcessId) {
        this.warmiProcessId = warmiProcessId;
    }

    public Integer getBanbifBaseDataAsInteger(String dataKey){
        if(entityCustomData != null && entityCustomData.has(LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey())){
            JSONObject banbifBase = entityCustomData.getJSONObject(LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey());
            return JsonUtil.getIntFromJson(banbifBase, dataKey, null);
        }
        return null;
    }

    public List<LoanApplicationFunnelStep> getFunnelSteps() {
        return funnelSteps;
    }

    public void setFunnelSteps(List<LoanApplicationFunnelStep> funnelSteps) {
        this.funnelSteps = funnelSteps;
    }

    public LoanApplicationAuxData getAuxData() {
        return auxData;
    }

    public void setAuxData(LoanApplicationAuxData auxData) {
        this.auxData = auxData;
    }

    public Integer getAssignedEntityUserId() {
        return assignedEntityUserId;
    }

    public void setAssignedEntityUserId(Integer assignedEntityUserId) {
        this.assignedEntityUserId = assignedEntityUserId;
    }

    public List<LoanApplicationApprovalValidation> getApprovalValidations() {
        return approvalValidations;
    }

    public void setApprovalValidations(List<LoanApplicationApprovalValidation> approvalValidations) {
        this.approvalValidations = approvalValidations;
    }

    public LoanApplicationApprovalValidation getApprovalValidationById(Integer approvalValidationId) {
        if (approvalValidations == null || approvalValidations.isEmpty() || approvalValidationId == null) return null;
        return approvalValidations.stream().filter(e -> e.getApprovalValidationId().equals(approvalValidationId)).findFirst().orElse(null);
    }

    public String getCodeWithoutString(){
        return code != null ? code.replaceAll("[a-zA-Z]","") : null;
    }

    public BantotalApiData getBanTotalApiData(){
        JSONObject bantotalApiDataJson = JsonUtil.getJsonObjectFromJson(entityCustomData, LoanApplication.EntityCustomDataKeys.BANTOTAL_API_DATA.getKey(), null);
        BantotalApiData bantotalApiData = null;
        Long personaUId = null;
        Long clienteUId = null;
        if(bantotalApiDataJson != null){
            bantotalApiData = new Gson().fromJson(bantotalApiDataJson.toString(),BantotalApiData.class);
        }
        return bantotalApiData;
    }

    public List<AztecaGetawayBase> getAztecaGatewayBasesData(){
        List<AztecaGetawayBase> aztecaGetawayBases = new ArrayList<>();
        JSONArray aztecaGatewayBasesArray = JsonUtil.getJsonArrayFromJson(entityCustomData, LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_BASES_GATEWAY.getKey(), null);
        if(aztecaGatewayBasesArray != null){
            for (int i = 0; i < aztecaGatewayBasesArray.length(); i++) {
                aztecaGetawayBases.add(new Gson().fromJson(aztecaGatewayBasesArray.getJSONObject(i).toString(), AztecaGetawayBase.class));
            }
        }
        return aztecaGetawayBases;
    }

    public AztecaGetawayBase getAztecaGatewayBaseData(){
        JSONObject data = JsonUtil.getJsonObjectFromJson(entityCustomData, LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_BASE_GATEWAY.getKey(), null);
        AztecaGetawayBase aztecaGetawayBase = null;
        if(data != null) aztecaGetawayBase = new  Gson().fromJson(data.toString(), AztecaGetawayBase.class);
        return aztecaGetawayBase;
    }



}