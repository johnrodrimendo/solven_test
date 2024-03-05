package com.affirm.common.model.transactional;

import com.affirm.common.model.catalog.*;
import com.affirm.common.model.catalog.Currency;
import com.affirm.common.service.CatalogService;
import com.affirm.common.service.UtilService;
import com.affirm.common.util.JsonUtil;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by jrodriguez on 14/06/16.
 */
public class Credit implements Serializable {

    public enum EntityCustomDataKeys {
        BANCO_DEL_SOL_CONTRACT_DOWNLOAD_DATE("bancoDelSolContractDownloadDate"),
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
    private String code;
    private Integer personId;
    private Integer userId;
    private Integer loanApplicationId;
    private LoanApplicationReason loanApplicationReason;
    private Integer loanOfferId;
    private Date registerDate;
    private CreditStatus status;
    private Double amount;
    private Double loanCapital;
    private Integer paidInstallments;
    private Integer installments;
    private Double installmentAmount;
    private Double effectiveAnnualRate;
    private Double effectiveMonthlyRate;
    private Double effectiveDailyRate;
    private Double loanCommission;
    private Double installmentAmountAvg;
    private Double effectiveAnnualCostRate;
    private Bank bank;
    private String bankAccount;
    private Character bankAccountType;
    private Ubigeo bankAccountUbigeo;
    private Date disbursementDate;
    private Date disbursementConfirmationDate;
    private Character disbursementType;
    private Integer disbursementSignatureSysuser;
    private String disbursementCheckNumber;
    private IdentityDocumentType personDocumentType;
    private String personDocumentNumber;
    private String personName;
    private String personFirstSurname;
    private String personLastSurname;
    private String personFullSurnames;
    private CreditRejectionReason rejectionReason;
    private Double commission;
    private Double commissionIgv;
    private Double totalCommission;
    private List<OriginalSchedule> originalSchedule;
    private List<ManagementSchedule> managementSchedule;
    private Date signatureDate;
    private String cciCode;
    private Product product;
    private Cluster cluster;
    private Entity entity;
    private Integer daysInArrears;
    private Character commissionType;
    private String rejectionLocal;
    private String clusterSubcluster;
    private Double sbsMonthlyInstallment;
    private Double sbsMonthlyInstallmentMotgage;
    private Double admissionTotalIncome;
    private Double rci;
    private Double internalRci;
    private Date inactiveWOScheduleDate;
    private Date inactiveWScheduleDate;
    private Date activeDate;
    private Date cancelledDate;
    private Date rejectedDate;
    private Date originatedDate;
    private Date originatedDisbursementDate;
    private List<Integer> contractUserFileId;
    //    private Double totalPendingInstallmentAmount; // Should not be used
    private Double pendingInstallmentAmount;
    private Double moratoriumRate;
    private Double commission2;
    private Integer activeCredits;
    private Boolean isExpiring;
    private Employer employer;
    private String entityCreditCode;
    private String backofficeAnalyst;
    private Double pendingCreditAmount;
    private List<ConsolidableDebt> consolidatedDebts;
    private Double consolidationRemnants;
    private Boolean generatedOnEntity;
    private Boolean disbursedOnEntity;
    private String generationEntityUser;
    private String disbursementEntityUser;
    private Boolean neverManageCollection;
    private Vehicle vehicle;
    private Double downPayment;
    private Currency downPaymentCurrency;
    private Double paidDownPayment;
    private Boolean isDownPaymentPaid;
    private List<DownPayment> downPaymentDetail;
    private Boolean signedOnEntity;
    private Double cost;
    private String welcomeCall;
    private String welcomeSpeech;
    private Double entityCommision;
    private Double entityTax;
    private Boolean waitingGenerationEntity;
    private LoanApplicationStatus loanApplicationStatus;
    private Date dueDate;
    private Date lastDueDate;
    private Double totalCreditAmount;
    private Double hiddenCommission;
    private Currency currency;
    private CountryParam country;
    private CreditSubStatus subStatus;
    private Double exchangeRate;
    private String depositorCode;
    private List<ReturningReason> returningReasons;
    private Double nominalAnualRate;
    private Integer entityProductParameterId;
    private EntityProductParams entityProductParams;
    private Double stampTax;
    private ObservationReason observationReason;
    private Double stampTaxRate;
    private String rejectionComment;
    private Date disbursedOnEntityDate;
    private Date cancellationOnEntityDate;
    private JSONObject entityCustomData;
    private List<Policy> policiesRejected;
    private List<HardFilter> hardFiltersRejected;
    private Double insurance;
    private List<EntityProductParams> entityProductPreliminaryEvaluations;



    public void fillFromDb(JSONObject json, CatalogService catalog, Locale locale) throws Exception {
        setId(JsonUtil.getIntFromJson(json, "credit_id", null));
        setUserId(JsonUtil.getIntFromJson(json, "user_id", null));
        setCode(JsonUtil.getStringFromJson(json, "credit_code", null));
        setPersonId(JsonUtil.getIntFromJson(json, "person_id", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        if (JsonUtil.getIntFromJson(json, "reason_id", null) != null) {
            setLoanApplicationReason(catalog.getLoanApplicationReason(locale, JsonUtil.getIntFromJson(json, "reason_id", null)));
        }
        setLoanOfferId(JsonUtil.getIntFromJson(json, "loan_offer_id", null));
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        if (JsonUtil.getIntFromJson(json, "credit_status_id", null) != null) {
            setStatus(catalog.getCreditStatus(locale, (JsonUtil.getIntFromJson(json, "credit_status_id", null))));
        }
        setLoanCapital(JsonUtil.getDoubleFromJson(json, "loan_capital", null));
        setAmount(JsonUtil.getDoubleFromJson(json, "amount", null));
        setPaidInstallments(JsonUtil.getIntFromJson(json, "paid_installments", null));
        setInstallments(JsonUtil.getIntFromJson(json, "installments", null));
        setInstallmentAmount(JsonUtil.getDoubleFromJson(json, "installment_amount", null));
        setEffectiveAnnualRate(JsonUtil.getDoubleFromJson(json, "effective_annual_rate", null));
        setEffectiveMonthlyRate(JsonUtil.getDoubleFromJson(json, "effective_monthly_rate", null));
        setEffectiveDailyRate(JsonUtil.getDoubleFromJson(json, "effective_daily_rate", null));
        setLoanCommission(JsonUtil.getDoubleFromJson(json, "loan_commission", null));
        setInstallmentAmountAvg(JsonUtil.getDoubleFromJson(json, "installment_amount_avg", null));
        setEffectiveAnnualCostRate(JsonUtil.getDoubleFromJson(json, "effective_annual_cost_rate", null));
        if (JsonUtil.getIntFromJson(json, "bank_id", null) != null) {
            setBank(catalog.getBank(JsonUtil.getIntFromJson(json, "bank_id", null)));
        }
        setBankAccount(JsonUtil.getStringFromJson(json, "bank_account", null));
        setBankAccountType(JsonUtil.getCharacterFromJson(json, "bank_account_type", null));
        if (JsonUtil.getStringFromJson(json, "account_ubigeo", null) != null) {
            setBankAccountUbigeo(catalog.getUbigeo(JsonUtil.getStringFromJson(json, "account_ubigeo", null)));
        }
        setDisbursementDate(JsonUtil.getPostgresDateFromJson(json, "disbursement_date", null));
        setDisbursementConfirmationDate(JsonUtil.getPostgresDateFromJson(json, "disbursement_confirmation_date", null));
        setDisbursementType(JsonUtil.getCharacterFromJson(json, "disbursement_type", null));
        setDisbursementSignatureSysuser(JsonUtil.getIntFromJson(json, "disbursement_signature_sysuser_id", null));
        setDisbursementCheckNumber(JsonUtil.getStringFromJson(json, "check_number", null));
        if (JsonUtil.getIntFromJson(json, "document_id", null) != null) {
            setPersonDocumentType(catalog.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_id", null)));
        }
        setPersonDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setPersonName(JsonUtil.getStringFromJson(json, "person_name", null));
        setPersonFirstSurname(JsonUtil.getStringFromJson(json, "first_surname", null));
        setPersonLastSurname(JsonUtil.getStringFromJson(json, "last_surname", null));
        setSignatureDate(JsonUtil.getPostgresDateFromJson(json, "signature_date", null));
        setCciCode(JsonUtil.getStringFromJson(json, "cci_code", null));
        if (JsonUtil.getIntFromJson(json, "rejection_reason_id", null) != null) {
            setRejectionReason(catalog.getCreditRejectionReason(JsonUtil.getIntFromJson(json, "rejection_reason_id", null)));
        }
        setCommission(JsonUtil.getDoubleFromJson(json, "commission", null));
        setCommissionIgv(JsonUtil.getDoubleFromJson(json, "commission_igv", null));
        setTotalCommission(JsonUtil.getDoubleFromJson(json, "total_commission", null));
        Integer product_id = JsonUtil.getIntFromJson(json, "product_id", null);
        if (product_id != null) {
            setProduct(catalog.getProduct(product_id));
        }
        if (JsonUtil.getIntFromJson(json, "cluster_id", null) != null) {
            setCluster(catalog.getCluster(JsonUtil.getIntFromJson(json, "cluster_id", null), locale));
        }
        Integer entity_id = JsonUtil.getIntFromJson(json, "entity_id", null);
        if (entity_id != null) {
            setEntity(catalog.getEntity(entity_id));
        }
        setDaysInArrears(JsonUtil.getIntFromJson(json, "days_in_arrears", null));
        setCommissionType(JsonUtil.getCharacterFromJson(json, "commission_type", null));
        Integer rejectionId = JsonUtil.getIntFromJson(json, "rejection_reason_id", null);
        setRejectionLocal(rejectionId != null && catalog.getCreditRejectionReason(rejectionId) != null ? "Motivo de rechazo: " + catalog.getCreditRejectionReason(rejectionId).getReason() : "");
        setClusterSubcluster(JsonUtil.getStringFromJson(json, "cluster", "?") + " - " + JsonUtil.getStringFromJson(json, "sub_cluster", "?"));
        setSbsMonthlyInstallment(JsonUtil.getDoubleFromJson(json, "sbs_monthly_installment", null));
        setSbsMonthlyInstallmentMotgage(JsonUtil.getDoubleFromJson(json, "sbs_monthly_installment_mortgage", null));
        setAdmissionTotalIncome(JsonUtil.getDoubleFromJson(json, "admission_total_income", null));
        setRci(JsonUtil.getDoubleFromJson(json, "rci", null));
        setInternalRci(JsonUtil.getDoubleFromJson(json, "internal_rci", null));
        setInactiveWOScheduleDate(JsonUtil.getPostgresDateFromJson(json, "inactivewoschedule_date", null));
        setInactiveWScheduleDate(JsonUtil.getPostgresDateFromJson(json, "inactivewschedule_date", null));
        setActiveDate(JsonUtil.getPostgresDateFromJson(json, "active_date", null));
        setCancelledDate(JsonUtil.getPostgresDateFromJson(json, "cancelled_date", null));
        setRejectedDate(JsonUtil.getPostgresDateFromJson(json, "rejected_date", null));
        setOriginatedDate(JsonUtil.getPostgresDateFromJson(json, "originated", null));
        setOriginatedDisbursementDate(JsonUtil.getPostgresDateFromJson(json, "originated_disbursed", null));
        setPendingInstallmentAmount(JsonUtil.getDoubleFromJson(json, "pending_installment_amount", 0.0));
        setMoratoriumRate(JsonUtil.getDoubleFromJson(json, "moratorium_rate", null));
        setCommission2(JsonUtil.getDoubleFromJson(json, "commission_2", null));
        setExpiring(JsonUtil.getBooleanFromJson(json, "is_expiring", null));
        if (JsonUtil.getJsonArrayFromJson(json, "contract_user_file_id", null) != null)
            setContractUserFileId(JsonUtil.getListFromJsonArray(json.getJSONArray("contract_user_file_id"), (arr, i) -> arr.getInt(i)));
        setActiveCredits(JsonUtil.getIntFromJson(json, "active_credits", null));
        if (JsonUtil.getIntFromJson(json, "employer_id", null) != null)
            setEmployer(catalog.getEmployer(JsonUtil.getIntFromJson(json, "employer_id", null)));
        setEntityCreditCode(JsonUtil.getStringFromJson(json, "entity_credit_code", null));
        setBackofficeAnalyst(JsonUtil.getStringFromJson(json, "username", null));
        setPendingCreditAmount(JsonUtil.getDoubleFromJson(json, "pending_credit_amount", null));
        setConsolidationRemnants(JsonUtil.getDoubleFromJson(json, "consolidation_remnants", null));
        if (JsonUtil.getBooleanFromJson(json, "generated_on_entity", null) != null) {
            setGeneratedOnEntity(JsonUtil.getBooleanFromJson(json, "generated_on_entity", null));
            setGenerationEntityUser(JsonUtil.getStringFromJson(json, "generation_entity_user", null));
        }
        if (JsonUtil.getBooleanFromJson(json, "disbursed_on_entity", null) != null) {
            setDisbursedOnEntity(JsonUtil.getBooleanFromJson(json, "disbursed_on_entity", null));
            setDisbursementEntityUser(JsonUtil.getStringFromJson(json, "disbursement_entity_user", null));
        }
        setNeverManageCollection(JsonUtil.getBooleanFromJson(json, "never_manage_collection", null));
        if (JsonUtil.getIntFromJson(json, "vehicle_id", null) != null) {
            setVehicle(catalog.getVehicle(JsonUtil.getIntFromJson(json, "vehicle_id", null), locale));
        }
        setDownPayment(JsonUtil.getDoubleFromJson(json, "down_payment", null));
        if (JsonUtil.getIntFromJson(json, "down_payment_currency_id", null) != null) {
            setDownPaymentCurrency(catalog.getCurrency(JsonUtil.getIntFromJson(json, "down_payment_currency_id", null)));
        }
        setPaidDownPayment(JsonUtil.getDoubleFromJson(json, "down_payment_paid_amount", null));
        setIsDownPaymentPaid(JsonUtil.getBooleanFromJson(json, "down_payment_paid", null));
        setSignedOnEntity(JsonUtil.getBooleanFromJson(json, "signed_on_entity", null));

        setEntityCommision(JsonUtil.getDoubleFromJson(json, "entity_commission", null));
        setEntityTax(JsonUtil.getDoubleFromJson(json, "entity_commission_tax", null));
        setCost(JsonUtil.getDoubleFromJson(json, "cost", null));
        setWaitingGenerationEntity(JsonUtil.getBooleanFromJson(json, "waiting_for_generation_on_entity", null));
        if (JsonUtil.getIntFromJson(json, "application_status_id", null) != null) {
            setLoanApplicationStatus(catalog.getLoanApplicationStatus(locale, JsonUtil.getIntFromJson(json, "application_status_id", null)));
        }
        setWelcomeCall(JsonUtil.getBooleanFromJson(json, "welcome_call", false) ? "SUBIDO" : "");
        setWelcomeSpeech(JsonUtil.getStringFromJson(json, "welcome_speech", null));
        setDueDate(JsonUtil.getPostgresDateFromJson(json, "due_date", null));
        setLastDueDate(JsonUtil.getPostgresDateFromJson(json, "last_due_date", null));
        setTotalCreditAmount(JsonUtil.getDoubleFromJson(json, "total_credit_amount", null));
        setHiddenCommission(JsonUtil.getDoubleFromJson(json, "hidden_commission", null));
        if (JsonUtil.getIntFromJson(json, "currency_id", null) != null) {
            setCurrency(catalog.getCurrency(JsonUtil.getIntFromJson(json, "currency_id", null)));
        }
        if (JsonUtil.getIntFromJson(json, "country_id", null) != null) {
            setCountry(catalog.getCountryParam(JsonUtil.getIntFromJson(json, "country_id", null)));
        }
        if (JsonUtil.getIntFromJson(json, "credit_sub_status_id", null) != null) {
            setSubStatus(catalog.getCreditSubStatus(locale, JsonUtil.getIntFromJson(json, "credit_sub_status_id", null)));
        }
        setExchangeRate(JsonUtil.getDoubleFromJson(json, "exchange_rate", null));
        setDepositorCode(JsonUtil.getStringFromJson(json, "depositor_code", null));

        setEntityProductParameterId(JsonUtil.getIntFromJson(json, "entity_product_parameter_id", null));
        if (getEntityProductParameterId() != null) {
            setEntityProductParams(catalog.getEntityProductParamById(getEntityProductParameterId()));
        }
        if (JsonUtil.getJsonArrayFromJson(json, "js_returning_reason_id", null) != null) {
            List<ReturningReason> returningReasons = new ArrayList<>();
            JSONArray returningReasonJson = JsonUtil.getJsonArrayFromJson(json, "js_returning_reason_id", null);
            for (int i = 0; i < returningReasonJson.length(); i++) {
                returningReasons.add(catalog.getReturningReasonById(returningReasonJson.getInt(i)));
            }
            setReturningReasons(returningReasons);
        }
        setNominalAnualRate(JsonUtil.getDoubleFromJson(json, "nominal_annual_rate", null));
        setStampTax(JsonUtil.getDoubleFromJson(json, "stamp_tax", null));
        setStampTaxRate(JsonUtil.getDoubleFromJson(json, "stamp_tax_rate", null));
        if (JsonUtil.getIntFromJson(json, "observation_reason_id", null) != null) {
            setObservationReason(catalog.getCreditObservationReason(JsonUtil.getIntFromJson(json, "observation_reason_id", null)));
        }
        setRejectionComment(JsonUtil.getStringFromJson(json, "rejection_comment", null));
        setDisbursedOnEntityDate(JsonUtil.getPostgresDateFromJson(json, "disbursed_on_entity_date", null));
        setCancellationOnEntityDate(JsonUtil.getPostgresDateFromJson(json, "cancellation_on_entity_date", null));
        setEntityCustomData(JsonUtil.getJsonObjectFromJson(json, "credit_entity_custom_data", new JSONObject()));
        if (personFirstSurname != null) {
            setPersonFullSurnames(personFirstSurname + (personLastSurname == null ? "" : " " + personLastSurname));
        }
        if (JsonUtil.getJsonArrayFromJson(json, "evaluation_detail", null) != null) {
            List<Policy> policies = new ArrayList<>();
            JSONArray policiesJson = JsonUtil.getJsonArrayFromJson(json, "evaluation_detail", null);
            for (int i = 0; i < policiesJson.length(); i++) {
                if (JsonUtil.getIntFromJson((JSONObject) policiesJson.get(i), "policy_id", null) != null)
                    policies.add(catalog.getPolicyById(
                            JsonUtil.getIntFromJson((JSONObject) policiesJson.get(i), "policy_id", null))
                    );
            }
            setPoliciesRejected(policies);
        }
        if (JsonUtil.getJsonArrayFromJson(json, "preliminary_evaluation_detail", null) != null) {
            List<HardFilter> hardFilters = new ArrayList<>();
            JSONArray hardFiltersJson = JsonUtil.getJsonArrayFromJson(json, "preliminary_evaluation_detail", null);
            entityProductPreliminaryEvaluations = new ArrayList<>();
            for (int i = 0; i < hardFiltersJson.length(); i++) {
                if (JsonUtil.getIntFromJson((JSONObject) hardFiltersJson.get(i), "hard_filter_id", null) != null)
                    hardFilters.add(catalog.getHardFilterById(
                            JsonUtil.getIntFromJson((JSONObject) hardFiltersJson.get(i), "hard_filter_id", null))
                    );
                if (JsonUtil.getIntFromJson((JSONObject) hardFiltersJson.get(i), "entity_product_parameter_id", null) != null) entityProductPreliminaryEvaluations.add(catalog.getEntityProductParamById(
                        JsonUtil.getIntFromJson((JSONObject) hardFiltersJson.get(i), "entity_product_parameter_id", null)));
            }
            setEntityProductPreliminaryEvaluations(entityProductPreliminaryEvaluations);
            setHardFiltersRejected(hardFilters);
        }
        setInsurance(JsonUtil.getDoubleFromJson(json, "insurance", null));
    }

    public String getFullName() {
        String fullname = "";
        if (personName != null) {
            fullname = fullname + personName + " ";
        }
        if (personFirstSurname != null) {
            fullname = fullname + personFirstSurname + " ";
        }
        if (personLastSurname != null) {
            fullname = fullname + personLastSurname + " ";
        }
        return fullname;
    }

    public Double getTotalPendingAmmount() {
        if (managementSchedule != null) {
            MutableDouble total = new MutableDouble();
            managementSchedule.stream().forEach(c -> total.add(c.getPendingInstallmentCapital()));
            return total.doubleValue();
        }
        return null;
    }

    public Integer getTotalPendingAmmountRound() {
        Double ammount = getTotalPendingAmmount();
        if (ammount == null) {
            return null;
        }
        return Math.toIntExact(Math.round(ammount));
    }

    public Integer getPendingInstallments() {
        if (managementSchedule != null) {
            return Math.toIntExact(managementSchedule.stream().filter(c -> c.getInstallmentStatusId() != ManagementSchedule.STATUS_PAYED).count());
        }
        return null;
    }

    public String getDisbursementTypeDescription() {
        if (disbursementType == null) {
            return null;
        }
        if (disbursementType == 'C') {
            return "cheque";
        } else if (disbursementType == 'T') {
            return "transferencia";
        } else {
            return disbursementType + "";
        }
    }

    public Double getStampTaxRate() {
        return stampTaxRate;
    }

    public void setStampTaxRate(Double stampTaxRate) {
        this.stampTaxRate = stampTaxRate;
    }

    public Double getTotalScheduleField(String field, char scheduleType) {

        if (scheduleType == 'O' && originalSchedule == null)
            return null;
        if (scheduleType == 'M' && managementSchedule == null)
            return null;

        if (scheduleType == 'O') {
            switch (field) {
                case "installmentAmount":
                    return originalSchedule.stream().mapToDouble(c -> c.getInstallmentAmount()).sum();
                case "insurance":
                    return originalSchedule.stream().mapToDouble(c -> c.getInsurance()).sum();
                case "totalCollectionCommission":
                    return originalSchedule.stream().mapToDouble(c -> c.getTotalCollectionCommission()).sum();
                case "collectionCommission":
                    return originalSchedule.stream().mapToDouble(OriginalSchedule::getCollectionCommission).sum();
                case "collectionCommissionTax":
                    return originalSchedule.stream().mapToDouble(OriginalSchedule::getCollectionCommissionTax).sum();
                case "totalInterest":
                    return originalSchedule.stream().mapToDouble(c -> c.getTotalInterest()).sum();
                case "installmentCapital":
                    return originalSchedule.stream().mapToDouble(c -> c.getInstallmentCapital()).sum();
                case "interest":
                    return originalSchedule.stream().mapToDouble(c -> c.getInterest()).sum();
                case "interestTax":
                    return originalSchedule.stream().mapToDouble(c -> c.getInterestTax()).sum();
                case "installment":
                    return originalSchedule.stream().filter(e -> e.getInstallment() != null).mapToDouble(c -> c.getInstallment()).sum();
            }
        } else if (scheduleType == 'M') {
            switch (field) {
                case "installmentAmount":
                    return managementSchedule.stream().mapToDouble(c -> c.getInstallmentAmount()).sum();
                case "carInsurance":
                    return managementSchedule.stream().mapToDouble(c -> c.getCarInsurance() != null ? c.getCarInsurance() : 0).sum();
                case "insurance":
                    return managementSchedule.stream().mapToDouble(c -> c.getInsurance()).sum();
                case "totalCollectionCommission":
                    return managementSchedule.stream().mapToDouble(c -> c.getTotalCollectionCommission()).sum();
                case "collectionCommission":
                    return managementSchedule.stream().mapToDouble(ManagementSchedule::getCollectionCommission).sum();
                case "collectionCommissionTax":
                    return managementSchedule.stream().mapToDouble(ManagementSchedule::getCollectionCommissionTax).sum();
                case "totalInterest":
                    return managementSchedule.stream().mapToDouble(c -> c.getTotalInterest()).sum();
                case "installmentCapital":
                    return managementSchedule.stream().mapToDouble(c -> c.getInstallmentCapital()).sum();
                case "totalPendingInterest":
                    return managementSchedule.stream().mapToDouble(c -> c.getTotalPendingInterest()).sum();
                case "totalMoratoriumInterest":
                    return managementSchedule.stream().mapToDouble(c -> c.getTotalMoratoriumInterest()).sum();
                case "totalMoratoriumCharge":
                    return managementSchedule.stream().mapToDouble(c -> c.getTotalMoratoriumCharge()).sum();
            }
        }
        return null;
    }

    public Date getFinishDate() {
        Calendar registerCal = Calendar.getInstance();
        registerCal.setTime(registerDate);
        registerCal.add(Calendar.MONTH, installments);
        return registerCal.getTime();
    }

    public boolean hasWelcomeCall() {
        return getWelcomeCall() != null && !getWelcomeCall().isEmpty();
    }

    public double getCreditAmount() {
        return amount + (stampTax != null ? stampTax : 0);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public Integer getLoanOfferId() {
        return loanOfferId;
    }

    public void setLoanOfferId(Integer loanOfferId) {
        this.loanOfferId = loanOfferId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public CreditStatus getStatus() {
        return status;
    }

    public void setStatus(CreditStatus status) {
        this.status = status;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getInstallments() {
        return installments;
    }

    public void setInstallments(Integer installments) {
        this.installments = installments;
    }

    public Double getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(Double installmentAmount) {
        this.installmentAmount = installmentAmount;
    }

    public Double getEffectiveAnnualRate() {
        return effectiveAnnualRate;
    }

    public void setEffectiveAnnualRate(Double effectiveAnnualRate) {
        this.effectiveAnnualRate = effectiveAnnualRate;
    }

    public Double getEffectiveMonthlyRate() {
        return effectiveMonthlyRate;
    }

    public void setEffectiveMonthlyRate(Double effectiveMonthlyRate) {
        this.effectiveMonthlyRate = effectiveMonthlyRate;
    }

    public Double getEffectiveDailyRate() {
        return effectiveDailyRate;
    }

    public void setEffectiveDailyRate(Double effectiveDailyRate) {
        this.effectiveDailyRate = effectiveDailyRate;
    }

    public Double getLoanCommission() {
        return loanCommission;
    }

    public void setLoanCommission(Double loanCommission) {
        this.loanCommission = loanCommission;
    }

    public Double getInstallmentAmountAvg() {
        return installmentAmountAvg;
    }

    public void setInstallmentAmountAvg(Double installmentAmountAvg) {
        this.installmentAmountAvg = installmentAmountAvg;
    }

    public Double getEffectiveAnnualCostRate() {
        return effectiveAnnualCostRate;
    }

    public void setEffectiveAnnualCostRate(Double effectiveAnnualCostRate) {
        this.effectiveAnnualCostRate = effectiveAnnualCostRate;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Character getBankAccountType() {
        return bankAccountType;
    }

    public void setBankAccountType(Character bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    public Ubigeo getBankAccountUbigeo() {
        return bankAccountUbigeo;
    }

    public void setBankAccountUbigeo(Ubigeo bankAccountUbigeo) {
        this.bankAccountUbigeo = bankAccountUbigeo;
    }

    public Date getDisbursementDate() {
        return disbursementDate;
    }

    public void setDisbursementDate(Date disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public Date getDisbursementConfirmationDate() {
        return disbursementConfirmationDate;
    }

    public void setDisbursementConfirmationDate(Date disbursementConfirmationDate) {
        this.disbursementConfirmationDate = disbursementConfirmationDate;
    }

    public Character getDisbursementType() {
        return disbursementType;
    }

    public void setDisbursementType(Character disbursementType) {
        this.disbursementType = disbursementType;
    }

    public Integer getDisbursementSignatureSysuser() {
        return disbursementSignatureSysuser;
    }

    public void setDisbursementSignatureSysuser(Integer disbursementSignatureSysuser) {
        this.disbursementSignatureSysuser = disbursementSignatureSysuser;
    }

    public IdentityDocumentType getPersonDocumentType() {
        return personDocumentType;
    }

    public void setPersonDocumentType(IdentityDocumentType personDocumentType) {
        this.personDocumentType = personDocumentType;
    }

    public String getPersonDocumentNumber() {
        return personDocumentNumber;
    }

    public void setPersonDocumentNumber(String personDocumentNumber) {
        this.personDocumentNumber = personDocumentNumber;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonFirstSurname() {
        return personFirstSurname;
    }

    public void setPersonFirstSurname(String personFirstSurname) {
        this.personFirstSurname = personFirstSurname;
    }

    public String getPersonLastSurname() {
        return personLastSurname;
    }

    public void setPersonLastSurname(String personLastSurname) {
        this.personLastSurname = personLastSurname;
    }

    public List<OriginalSchedule> getOriginalSchedule() {
        return originalSchedule;
    }

    public void setOriginalSchedule(List<OriginalSchedule> originalSchedule) {
        this.originalSchedule = originalSchedule;
    }

    public Date getSignatureDate() {
        return signatureDate;
    }

    public void setSignatureDate(Date signatureDate) {
        this.signatureDate = signatureDate;
    }

    public String getCciCode() {
        return cciCode;
    }

    public void setCciCode(String cciCode) {
        this.cciCode = cciCode;
    }

    public String getDisbursementCheckNumber() {
        return disbursementCheckNumber;
    }

    public void setDisbursementCheckNumber(String disbursementCheckNumber) {
        this.disbursementCheckNumber = disbursementCheckNumber;
    }

    public CreditRejectionReason getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(CreditRejectionReason rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public Double getCommissionIgv() {
        return commissionIgv;
    }

    public void setCommissionIgv(Double commissionIgv) {
        this.commissionIgv = commissionIgv;
    }

    public Double getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(Double totalCommission) {
        this.totalCommission = totalCommission;
    }

    public List<ManagementSchedule> getManagementSchedule() {
        return managementSchedule;
    }

    public void setManagementSchedule(List<ManagementSchedule> managementSchedule) {
        this.managementSchedule = managementSchedule;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Integer getDaysInArrears() {
        return daysInArrears;
    }

    public void setDaysInArrears(Integer daysInArrears) {
        this.daysInArrears = daysInArrears;
    }

    public LoanApplicationReason getLoanApplicationReason() {
        return loanApplicationReason;
    }

    public void setLoanApplicationReason(LoanApplicationReason loanApplicationReason) {
        this.loanApplicationReason = loanApplicationReason;
    }

    public Character getCommissionType() {
        return commissionType;
    }

    public void setCommissionType(Character commissionType) {
        this.commissionType = commissionType;
    }

    public void setRejectionLocal(String rejectionLocal) {
        this.rejectionLocal = rejectionLocal;
    }

    public String getRejectionLocal() {
        return rejectionLocal;
    }

    public String getClusterSubcluster() {
        return clusterSubcluster;
    }

    public void setClusterSubcluster(String clusterSubcluster) {
        this.clusterSubcluster = clusterSubcluster;
    }

    public String getCreditDescription(UtilService utilService) {
        String creditDescription = utilService.doubleMoneyFormat(getAmount(), currency);
        if (getInstallments() != null) {
            creditDescription = creditDescription + "\t@ " + getInstallments() + "m\t@ " + utilService.percentFormat(getEffectiveAnnualRate(), currency) + "\t@ " + utilService.percentFormat(getEffectiveAnnualCostRate(), currency);
        }
        return creditDescription;
    }

    public String getCreditDescription(UtilService utilService, String symbol, String separator) {
        String creditDescription = utilService.doubleMoneyFormat(getAmount(), currency);
        if (getInstallments() != null) {
            creditDescription = creditDescription + "\t@ " + getInstallments() + "m\t@ " + utilService.percentFormat(getEffectiveAnnualRate(), currency) + "\t@ " + utilService.percentFormat(getEffectiveAnnualCostRate(), currency);
        }
        return creditDescription;
    }

    public Double getSbsMonthlyInstallment() {
        return sbsMonthlyInstallment;
    }

    public void setSbsMonthlyInstallment(Double sbsMonthlyInstallment) {
        this.sbsMonthlyInstallment = sbsMonthlyInstallment;
    }

    public Double getSbsMonthlyInstallmentMotgage() {
        return sbsMonthlyInstallmentMotgage;
    }

    public void setSbsMonthlyInstallmentMotgage(Double sbsMonthlyInstallmentMotgage) {
        this.sbsMonthlyInstallmentMotgage = sbsMonthlyInstallmentMotgage;
    }

    public Double getAdmissionTotalIncome() {
        return admissionTotalIncome;
    }

    public void setAdmissionTotalIncome(Double admissionTotalIncome) {
        this.admissionTotalIncome = admissionTotalIncome;
    }

    public Double getRci() {
        return rci;
    }

    public void setRci(Double rci) {
        this.rci = rci;
    }

    public Date getInactiveWOScheduleDate() {
        return inactiveWOScheduleDate;
    }

    public void setInactiveWOScheduleDate(Date inactiveWOScheduleDate) {
        this.inactiveWOScheduleDate = inactiveWOScheduleDate;
    }

    public Date getInactiveWScheduleDate() {
        return inactiveWScheduleDate;
    }

    public void setInactiveWScheduleDate(Date inactiveWScheduleDate) {
        this.inactiveWScheduleDate = inactiveWScheduleDate;
    }

    public Date getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(Date activeDate) {
        this.activeDate = activeDate;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public Date getRejectedDate() {
        return rejectedDate;
    }

    public void setRejectedDate(Date rejectedDate) {
        this.rejectedDate = rejectedDate;
    }

    public List<Integer> getContractUserFileId() {
        return contractUserFileId;
    }

    public void setContractUserFileId(List<Integer> contractUserFileId) {
        this.contractUserFileId = contractUserFileId;
    }

    public Double getPendingInstallmentAmount() {
        return pendingInstallmentAmount;
    }

    public void setPendingInstallmentAmount(Double pendingInstallmentAmount) {
        this.pendingInstallmentAmount = pendingInstallmentAmount;
    }

    public Double getMoratoriumRate() {
        return moratoriumRate;
    }

    public void setMoratoriumRate(Double moratoriumRate) {
        this.moratoriumRate = moratoriumRate;
    }

    public Double getCommission2() {
        return commission2;
    }

    public void setCommission2(Double commission2) {
        this.commission2 = commission2;
    }

    public Integer getActiveCredits() {
        return activeCredits;
    }

    public Boolean getExpiring() {
        return isExpiring;
    }

    public void setExpiring(Boolean expiring) {
        isExpiring = expiring;
    }

    public void setActiveCredits(Integer activeCredits) {
        this.activeCredits = activeCredits;
    }

    public Employer getEmployer() {
        return employer;
    }

    public void setEmployer(Employer employer) {
        this.employer = employer;
    }

    public String getEntityCreditCode() {
        return entityCreditCode;
    }

    public void setEntityCreditCode(String entityCreditCode) {
        this.entityCreditCode = entityCreditCode;
    }


    public Double getInternalRci() {
        return internalRci;
    }

    public void setInternalRci(Double internalRci) {
        this.internalRci = internalRci;
    }

    public String getBackofficeAnalyst() {
        return backofficeAnalyst;
    }

    public void setBackofficeAnalyst(String backofficeAnalyst) {
        this.backofficeAnalyst = backofficeAnalyst;
    }

    public Date getOriginatedDate() {
        return originatedDate;
    }

    public void setOriginatedDate(Date originatedDate) {
        this.originatedDate = originatedDate;
    }

    public Date getOriginatedDisbursementDate() {
        return originatedDisbursementDate;
    }

    public void setOriginatedDisbursementDate(Date originatedDisbursementDate) {
        this.originatedDisbursementDate = originatedDisbursementDate;
    }

    public Double getPendingCreditAmount() {
        return pendingCreditAmount;
    }

    public void setPendingCreditAmount(Double pendingCreditAmount) {
        this.pendingCreditAmount = pendingCreditAmount;
    }

    public List<ConsolidableDebt> getConsolidatedDebts() {
        return consolidatedDebts;
    }

    public void setConsolidatedDebts(List<ConsolidableDebt> consolidatedDebts) {
        this.consolidatedDebts = consolidatedDebts;
    }

    public Double getConsolidationRemnants() {
        return consolidationRemnants;
    }

    public void setConsolidationRemnants(Double consolidationRemnants) {
        this.consolidationRemnants = consolidationRemnants;
    }

    public Double getLoanCapital() {
        return loanCapital;
    }

    public void setLoanCapital(Double loanCapital) {
        this.loanCapital = loanCapital;
    }

    public Boolean getGeneratedOnEntity() {
        return generatedOnEntity;
    }

    public void setGeneratedOnEntity(Boolean generatedOnEntity) {
        this.generatedOnEntity = generatedOnEntity;
    }

    public Boolean getDisbursedOnEntity() {
        return disbursedOnEntity;
    }

    public void setDisbursedOnEntity(Boolean disbursedOnEntity) {
        this.disbursedOnEntity = disbursedOnEntity;
    }

    public String getGenerationEntityUser() {
        return generationEntityUser;
    }

    public void setGenerationEntityUser(String generationEntityUser) {
        this.generationEntityUser = generationEntityUser;
    }

    public String getDisbursementEntityUser() {
        return disbursementEntityUser;
    }

    public void setDisbursementEntityUser(String disbursementEntityUser) {
        this.disbursementEntityUser = disbursementEntityUser;
    }

    public Boolean getNeverManageCollection() {
        return neverManageCollection;
    }

    public void setNeverManageCollection(Boolean neverManageCollection) {
        this.neverManageCollection = neverManageCollection;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Double getDownPayment() {
        return downPayment;
    }

    public void setDownPayment(Double downPayment) {
        this.downPayment = downPayment;
    }

    public Double getPaidDownPayment() {
        return paidDownPayment;
    }

    public void setPaidDownPayment(Double paidDownPayment) {
        this.paidDownPayment = paidDownPayment;
    }

    public Boolean getIsDownPaymentPaid() {
        return isDownPaymentPaid;
    }

    public void setIsDownPaymentPaid(Boolean downPaymentPaid) {
        isDownPaymentPaid = downPaymentPaid;
    }

    public List<DownPayment> getDownPaymentDetail() {
        return downPaymentDetail;
    }

    public void setDownPaymentDetail(List<DownPayment> downPaymentDetail) {
        this.downPaymentDetail = downPaymentDetail;
    }

    public Boolean getSignedOnEntity() {
        return signedOnEntity;
    }

    public void setSignedOnEntity(Boolean signedOnEntity) {
        this.signedOnEntity = signedOnEntity;
    }

    public Double getEntityCommision() {
        return entityCommision;
    }

    public void setEntityCommision(Double entityCommision) {
        this.entityCommision = entityCommision;
    }

    public Double getEntityTax() {
        return entityTax;
    }

    public void setEntityTax(Double entityTax) {
        this.entityTax = entityTax;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Boolean getWaitingGenerationEntity() {
        return waitingGenerationEntity;
    }

    public void setWaitingGenerationEntity(Boolean waitingGenerationEntity) {
        this.waitingGenerationEntity = waitingGenerationEntity;
    }

    public LoanApplicationStatus getLoanApplicationStatus() {
        return loanApplicationStatus;
    }

    public void setLoanApplicationStatus(LoanApplicationStatus loanApplicationStatus) {
        this.loanApplicationStatus = loanApplicationStatus;
    }

    public String getWelcomeCall() {
        return welcomeCall;
    }

    public void setWelcomeCall(String welcomeCall) {
        this.welcomeCall = welcomeCall;
    }

    public String getWelcomeSpeech() {
        return welcomeSpeech;
    }

    public void setWelcomeSpeech(String welcomeSpeech) {
        this.welcomeSpeech = welcomeSpeech;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Double getTotalCreditAmount() {
        return totalCreditAmount;
    }

    public void setTotalCreditAmount(Double totalCreditAmount) {
        this.totalCreditAmount = totalCreditAmount;
    }

    public Date getLastDueDate() {
        return lastDueDate;
    }

    public void setLastDueDate(Date lastDueDate) {
        this.lastDueDate = lastDueDate;
    }

    public Double getHiddenCommission() {
        return hiddenCommission;
    }

    public void setHiddenCommission(Double hiddenCommission) {
        this.hiddenCommission = hiddenCommission;
    }

    public Integer getPaidInstallments() {
        return paidInstallments;
    }

    public void setPaidInstallments(Integer paidInstallments) {
        this.paidInstallments = paidInstallments;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public CountryParam getCountry() {
        return country;
    }

    public void setCountry(CountryParam country) {
        this.country = country;
    }

    public CreditSubStatus getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(CreditSubStatus subStatus) {
        this.subStatus = subStatus;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Currency getDownPaymentCurrency() {
        return downPaymentCurrency;
    }

    public void setDownPaymentCurrency(Currency downPaymentCurrency) {
        this.downPaymentCurrency = downPaymentCurrency;
    }

    public String getDepositorCode() {
        return depositorCode;
    }

    public void setDepositorCode(String depositorCode) {
        this.depositorCode = depositorCode;
    }

    public Integer getEntityProductParameterId() {
        return entityProductParameterId;
    }

    public void setEntityProductParameterId(Integer entityProductParameterId) {
        this.entityProductParameterId = entityProductParameterId;
    }

    public List<ReturningReason> getReturningReasons() {
        return returningReasons;
    }

    public void setReturningReasons(List<ReturningReason> returningReasons) {
        this.returningReasons = returningReasons;
    }

    public Double getNominalAnualRate() {
        return nominalAnualRate;
    }

    public void setNominalAnualRate(Double nominalAnualRate) {
        this.nominalAnualRate = nominalAnualRate;
    }

    public Double getStampTax() {
        return stampTax;
    }

    public void setStampTax(Double stampTax) {
        this.stampTax = stampTax;
    }

    public EntityProductParams getEntityProductParams() {
        return entityProductParams;
    }

    public void setEntityProductParams(EntityProductParams entityProductParams) {
        this.entityProductParams = entityProductParams;
    }

    public ObservationReason getObservationReason() {
        return observationReason;
    }

    public void setObservationReason(ObservationReason observationReason) {
        this.observationReason = observationReason;
    }

    public String getRejectionComment() {
        return rejectionComment;
    }

    public void setRejectionComment(String rejectionComment) {
        this.rejectionComment = rejectionComment;
    }


    public Date getDisbursedOnEntityDate() {
        return disbursedOnEntityDate;
    }

    public void setDisbursedOnEntityDate(Date disbursedOnEntityDate) {
        this.disbursedOnEntityDate = disbursedOnEntityDate;
    }

    public Date getCancellationOnEntityDate() {
        return cancellationOnEntityDate;
    }

    public void setCancellationOnEntityDate(Date cancellationOnEntityDate) {
        this.cancellationOnEntityDate = cancellationOnEntityDate;
    }

    public JSONObject getEntityCustomData() {
        return entityCustomData;
    }

    public void setEntityCustomData(JSONObject entityCustomData) {
        this.entityCustomData = entityCustomData;
    }

    public String getPersonFullSurnames() {
        return personFullSurnames;
    }

    public void setPersonFullSurnames(String personFullSurnames) {
        this.personFullSurnames = personFullSurnames;
    }

    public List<Policy> getPoliciesRejected() {
        return policiesRejected;
    }

    public void setPoliciesRejected(List<Policy> policiesRejected) {
        this.policiesRejected = policiesRejected;
    }

    public List<HardFilter> getHardFiltersRejected() {
        return hardFiltersRejected;
    }

    public void setHardFiltersRejected(List<HardFilter> hardFiltersRejected) {
        this.hardFiltersRejected = hardFiltersRejected;
    }

    public Double getInsurance() {
        return insurance;
    }

    public void setInsurance(Double insurance) {
        this.insurance = insurance;
    }

    public EntityProductParams getFirstPreliminaryEvaluation(){
        if(entityProductPreliminaryEvaluations == null || entityProductPreliminaryEvaluations.isEmpty()) return null;
        return entityProductPreliminaryEvaluations.get(0);
    }

    public List<EntityProductParams> getEntityProductPreliminaryEvaluations() {
        return entityProductPreliminaryEvaluations;
    }

    public void setEntityProductPreliminaryEvaluations(List<EntityProductParams> entityProductPreliminaryEvaluations) {
        this.entityProductPreliminaryEvaluations = entityProductPreliminaryEvaluations;
    }

    public Integer getAztecaGraceDays(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(registerDate);
        cal.add(Calendar.MONTH, 1);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateString1 = sdf.format(cal.getTime());
        String dateString2 = sdf.format(getOriginalSchedule().get(0).getDueDate());

        LocalDate date1 = LocalDate.parse(dateString1, dtf);
        LocalDate date2 = LocalDate.parse(dateString2, dtf);
        long daysBetween = ChronoUnit.DAYS.between(date1, date2);
        return (int)daysBetween;
    }
}
