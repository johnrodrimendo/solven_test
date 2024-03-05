package com.affirm.common.model;

import com.affirm.common.model.catalog.*;
import com.affirm.common.model.transactional.HardFilter;
import com.affirm.common.model.transactional.LoanApplication;
import com.affirm.common.model.transactional.Policy;
import com.affirm.common.model.transactional.Referral;
import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.json.Json;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by dev5 on 06/10/17.
 */
public class ReportLoans {

    private Integer loanApplicationId;
    private String country;
    private String loanApplicationCode;
    private Date registerDate;
    private IdentityDocumentType identityDocumentType;
    private String documentNumber;
    private String surname;
    private String personName;
    private Product product;
    private Profession profession;
    private LoanApplicationStatus loanApplicationStatus;
    private CreditStatus creditStatus;
    private DisbursementType disbursementType;
    private String finalResult;
    private String rejectionInstance;
    private String rejectionType;
    private String hardFilterId;
    private String hardFilterMessage;
    private String defaultEvaluationHardFilterId;
    private List<HardFilter> hardFilter;
    private List<HardFilter> defaultHardFilter;
    private List<Policy> defaultEvaluationPolicies;
    private List<Policy> policies;
    private String policyId;
    private String policyMessage;
    private String pep;
    private LoanApplicationReason reason;
    private Double amount;
    private Integer installments;
    private MaritalStatus maritalStatus;
    private Integer dependents;
    private Ubigeo ubigeo;
    private HousingType housingType;
    private StudyLevel studyLevel;
    private ActivityType activityType;
    private Integer employmentTime;
    private Double income;
    private Double fixedGrossIncome;
    private Double variableGrossIncome;
    private String ruc;
    private String companyName;
    private Ocupation ocupation;
    private Date birthday;
    private String gender;
    private Nationality nationality;
    private List<Referral> referrals;
    private Bank bank;
    private String bankAccount;
    private Double sbsMonthlyInstallment;
    private Double loanApplicationRCI;
    private Double creditRCI;
    private Integer score;
    private Integer evaluations;
    private Integer approvedEntities;
    private Entity selectedEntity;
    private String offerAmount;
    private String offerInstallments;
    private String offerTEAS;
    private String offerTCEAS;
    private Double maxAmountSameInstallments;
    private Double maxAmountOffer;
    private String eflProccess;
    private String eflScore;
    private String socialNetworks;
    private Double mortgageAmount;
    private String iovationType;
    private String iovationOs;
    private String iovationBrowser;
    private Integer currentQuestion;
    private ProcessQuestion processQuestion;
    private String operadoresTelefonicos;
    private String approvedEntitiesName;
    private String preapprovedEntitiesName;
    private String rejectedEntitiesName;
    private String prerejectedEntitiesName;
    private Boolean passedPersonalInformation;
    private Boolean passedIncomeInformation;
    private Boolean passedOfferScreen;
    private Boolean passedVerificationScreen;
    private Boolean passedContract;
    private Boolean assistedProcess;

    private String origin;
    private String source;
    private String medium;
    private String campaign;
    private String term;
    private String content;
    private String preEvaluationStatus;
    private String evaluationStatus;
    private String applicationSource;
    private Entity entityBranding;
    private String waitingPreEvaluation;
    private String waitingEvaluation;
    private String hasFraudAlerts;
    private Date lastStatusUpdate;
    private String withGclid;
    private Date disbursementDate;
    private Double loanCapital;
    private Integer preEvaluationExecuted;
    private Integer preEvaluationApproved;
    private Integer applicationCompleted;
    private Integer hasOffers;
    private Integer hasSelectedOffer;
    private Integer verificationCompleted;
    private Integer applicationApproved;
    private Integer applicationApprovedAndSigned;
    private Integer isDisbursed;
    private String referredLeads;
    private List<String> HardFilterMessages;
    private Double monthlyInstallment;
    private Double totalDebt;
    private String socioeconomicLevel;
    private String worstCalificationCurrent;
    private String worstCalificationU6M;
    private String worstCalification7to12;

    private String worstCalification13to24;
    private String hardFilterValues;
    private String policyValues;
    private Character questionFlow;
    private String offerRejectionReasonKey;

    private List<Integer> logApplicationStatusIds;
    private List<Integer> logCreditStatusIds;
    private List<Integer> processQuestionSequenceIds;
    private String phoneNumber;
    private String email;
    private JSONObject entityCustomData;
    private Integer step1;
    private Integer step2;
    private Integer step3;
    private Integer step4;
    private Integer step5;
    private Integer step6;
    private Integer step7;
    private Integer step8;
    private Integer step9;
    private Integer step10;
    private Integer step11;
    private Integer step12;
    private Integer step13;
    private Integer step14;
    private Integer step15;
    private Integer step16;
    private Integer step17;
    private Integer step18;
    private Integer step19;
    private Integer step20;
    private Integer step21;
    private Integer step22;
    private List<Policy> policiesRejected;
    private List<HardFilter> hardFiltersRejected;
    private ApplicationRejectionReason applicationRejectionReason;
    private Boolean isCredit;
    private CreditRejectionReason rejectionReason;
    private EntityProductParams entityProductParam;
    private Character selectedOfferBankAccountDataType;

    public void fillFromDb(JSONObject json, CatalogService catalogService, Locale locale) throws Exception {
        setRegisterDate(JsonUtil.getPostgresDateFromJson(json, "register_date", null));
        setCountry(JsonUtil.getStringFromJson(json, "country", null));
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setLoanApplicationCode(JsonUtil.getStringFromJson(json, "loan_application_code", null));
        if(JsonUtil.getIntFromJson(json, "document_id", null) != null)
            setIdentityDocumentType(catalogService.getIdentityDocumentType(JsonUtil.getIntFromJson(json, "document_id", null)));
        setDocumentNumber(JsonUtil.getStringFromJson(json, "document_number", null));
        setSurname(JsonUtil.getStringFromJson(json, "surname", null));
        setPersonName(JsonUtil.getStringFromJson(json, "person_name", null));
        if(JsonUtil.getIntFromJson(json, "product_id", null) != null)
            setProduct(catalogService.getProduct(JsonUtil.getIntFromJson(json, "product_id", null)));
        setLoanApplicationStatus(catalogService.getLoanApplicationStatus(locale, JsonUtil.getIntFromJson(json, "application_status_id", null)));
        if(JsonUtil.getIntFromJson(json, "credit_status_id", null) != null)
            setCreditStatus(catalogService.getCreditStatus(locale, JsonUtil.getIntFromJson(json, "credit_status_id", null)));
        if(JsonUtil.getIntFromJson(json, "disbursement_type_id", null) != null)
            setDisbursementType(catalogService.getDisbursementTypeById(JsonUtil.getIntFromJson(json, "disbursement_type_id", null)));
        if(JsonUtil.getIntFromJson(json, "profession_id", null) != null)
            setProfession(catalogService.getProfession(locale, JsonUtil.getIntFromJson(json, "profession_id", null)));
        setFinalResult(JsonUtil.getStringFromJson(json, "final_result", null));
        setRejectionInstance(JsonUtil.getStringFromJson(json, "rejection_instance", null));
        setRejectionType(JsonUtil.getStringFromJson(json, "rejection_type", null));
        if(JsonUtil.getJsonArrayFromJson(json, "hard_filter_id", null) != null){
            setHardFilterId(JsonUtil.getJsonArrayFromJson(json, "hard_filter_id", null).toString());
            List<HardFilter> filterTemp = new ArrayList<>();
            for(int i = 0; i < JsonUtil.getJsonArrayFromJson(json, "hard_filter_id", null).length(); i++){
                filterTemp.add(catalogService.getHardFilterById(JsonUtil.getJsonArrayFromJson(json, "hard_filter_id", null).getInt(i)));
            }
            setHardFilter(filterTemp);
        }

        if(JsonUtil.getJsonArrayFromJson(json, "hard_filter_message", null) != null){
            List<String> filterMessageTemp = new ArrayList<>();
            for(int i = 0; i < JsonUtil.getJsonArrayFromJson(json, "hard_filter_message", null).length(); i++){
                filterMessageTemp.add(JsonUtil.getJsonArrayFromJson(json, "hard_filter_message", null).getString(i));
            }
            setHardFilterMessages(filterMessageTemp);
        }

        if(JsonUtil.getJsonArrayFromJson(json, "default_evaluation_policy_id", null) != null){
            List<Policy> policyTemp = new ArrayList<>();
            for(int i = 0; i < JsonUtil.getJsonArrayFromJson(json, "default_evaluation_policy_id", null).length(); i++){
                policyTemp.add(catalogService.getPolicyById(JsonUtil.getJsonArrayFromJson(json, "default_evaluation_policy_id", null).getInt(i)));
            }
            setDefaultEvaluationPolicies(policyTemp);
        }

        if(JsonUtil.getJsonArrayFromJson(json, "policy_id", null) != null){
            List<Policy> policyTemp = new ArrayList<>();
            for(int i = 0; i < JsonUtil.getJsonArrayFromJson(json, "policy_id", null).length(); i++){
                policyTemp.add(catalogService.getPolicyById(JsonUtil.getJsonArrayFromJson(json, "policy_id", null).getInt(i)));
            }
            setPolicies(policyTemp);
        }

        if(JsonUtil.getJsonArrayFromJson(json, "policy_id", null) != null)
            setPolicyId(JsonUtil.getJsonArrayFromJson(json, "policy_id", null).toString());
        if(JsonUtil.getJsonArrayFromJson(json, "policy_message", null) !=null)
            setPolicyMessage(JsonUtil.getJsonArrayFromJson(json, "policy_message", null).toString());
        if(JsonUtil.getBooleanFromJson(json, "pep", null) != null)
            setPep(JsonUtil.getBooleanFromJson(json, "pep", null) ? "Sí" : "No");
        if(JsonUtil.getIntFromJson(json, "reason_id", null)!=null)
            setReason(catalogService.getLoanApplicationReason(locale, JsonUtil.getIntFromJson(json, "reason_id", null)));
        setAmount(JsonUtil.getDoubleFromJson(json, "amount", null));
        setInstallments(JsonUtil.getIntFromJson(json, "installments", null));
        if(JsonUtil.getIntFromJson(json, "marital_status_id", null) != null)
            setMaritalStatus(catalogService.getMaritalStatus(locale, JsonUtil.getIntFromJson(json, "marital_status_id", null)));
        setDependents(JsonUtil.getIntFromJson(json, "dependents", null));
        if(JsonUtil.getStringFromJson(json, "ubigeo_id", null) != null)
            setUbigeo(catalogService.getUbigeo(JsonUtil.getStringFromJson(json, "ubigeo_id", null)));
        if(JsonUtil.getIntFromJson(json, "housing_type_id", null) != null)
            setHousingType(catalogService.getHousingType(locale, JsonUtil.getIntFromJson(json, "housing_type_id", null)));
        if(JsonUtil.getIntFromJson(json, "study_level_id", null) != null)
            setStudyLevel(catalogService.getStudyLevel(locale, JsonUtil.getIntFromJson(json, "study_level_id", null)));
        if(JsonUtil.getIntFromJson(json, "activity_type_id", null) != null)
            setActivityType(catalogService.getActivityType(locale, JsonUtil.getIntFromJson(json, "activity_type_id", null)));
        setEmploymentTime(JsonUtil.getIntFromJson(json, "employment_time", null));
        setIncome(JsonUtil.getDoubleFromJson(json, "incomes", null));
        setFixedGrossIncome(JsonUtil.getDoubleFromJson(json, "fixed_gross_income", null));
        setVariableGrossIncome(JsonUtil.getDoubleFromJson(json, "variable_gross_income", null));
        setRuc(JsonUtil.getStringFromJson(json, "ruc", null));
        setCompanyName(JsonUtil.getStringFromJson(json, "company_name", null));
        if(JsonUtil.getIntFromJson(json, "ocupation_id", null) != null)
            setOcupation(catalogService.getOcupation(locale, JsonUtil.getIntFromJson(json, "ocupation_id", null)));
        setBirthday(JsonUtil.getPostgresDateFromJson(json, "birthday", null));
        setGender(JsonUtil.getStringFromJson(json, "gender", null));
        if(JsonUtil.getIntFromJson(json, "nationality_id", null) != null)
            setNationality(catalogService.getNationality(locale, JsonUtil.getIntFromJson(json, "nationality_id", null)));
        JSONArray referrals = JsonUtil.getJsonArrayFromJson(json, "referrals", null);
        List<Referral> referralList = new ArrayList<>();
        if(referrals != null && referrals.length() > 0){
            for(int i = 0; i < referrals.length(); i++){
                Referral referral = new Referral();
                referral.fillFromDb(referrals.getJSONObject(i), catalogService, locale);
                referralList.add(referral);
            }
            setReferrals(referralList);
        }
        if(JsonUtil.getIntFromJson(json, "bank_id", null) != null)
            setBank(catalogService.getBank(JsonUtil.getIntFromJson(json, "bank_id", null)));
        setBankAccount(JsonUtil.getStringFromJson(json, "bank_account", null));
        setSbsMonthlyInstallment(JsonUtil.getDoubleFromJson(json, "sbs_monthly_installment", null));
        setLoanApplicationRCI(JsonUtil.getDoubleFromJson(json, "loan_application_rci", null));
        setCreditRCI(JsonUtil.getDoubleFromJson(json, "credit_rci", null));
        setScore(JsonUtil.getIntFromJson(json, "efx_score", null));
        setEvaluations(JsonUtil.getIntFromJson(json, "evaluations", null));
        setApprovedEntities(JsonUtil.getIntFromJson(json, "approved_entities", null));
        if(JsonUtil.getIntFromJson(json, "selected_entity_id", null) != null )
            setSelectedEntity(catalogService.getEntity(JsonUtil.getIntFromJson(json, "selected_entity_id", null)));
        if(JsonUtil.getJsonArrayFromJson(json, "offer_amounts", null) != null)
            setOfferAmount(JsonUtil.getJsonArrayFromJson(json, "offer_amounts", null).toString());
        if(JsonUtil.getJsonArrayFromJson(json, "offer_installments", null) != null)
            setOfferInstallments(JsonUtil.getJsonArrayFromJson(json, "offer_installments", null).toString());
        if(JsonUtil.getJsonArrayFromJson(json, "offer_teas", null) != null)
            setOfferTEAS(JsonUtil.getJsonArrayFromJson(json, "offer_teas", null).toString());
        if(JsonUtil.getJsonArrayFromJson(json, "offer_tceas", null) != null)
            setOfferTCEAS(JsonUtil.getJsonArrayFromJson(json, "offer_tceas", null).toString());
        setMaxAmountSameInstallments(JsonUtil.getDoubleFromJson(json, "max_amount_same_installments", null));
        setMaxAmountOffer(JsonUtil.getDoubleFromJson(json, "max_amount_offer", null));
        if(JsonUtil.getBooleanFromJson(json, "efl_proccess", null) != null && JsonUtil.getBooleanFromJson(json, "efl_proccess", null))
            setEflProccess("Sí");
        else
            setEflProccess("No");
        setEflScore(JsonUtil.getStringFromJson(json, "efl_score", null));
        setSocialNetworks(JsonUtil.getStringFromJson(json, "js_social_network", null));
        setMortgageAmount(JsonUtil.getDoubleFromJson(json, "sbs_monthly_installment_mortgage", null));
        setIovationType(JsonUtil.getStringFromJson(json, "iovation_type", null));
        setIovationOs(JsonUtil.getStringFromJson(json, "iovation_os", null));
        setIovationBrowser(JsonUtil.getStringFromJson(json, "iovation_browser", null));
        setCurrentQuestion(JsonUtil.getIntFromJson(json, "current_process_question_id", null));
        setProcessQuestion(catalogService.getProcessQuestion(getCurrentQuestion()));
        if(JsonUtil.getJsonArrayFromJson(json, "ar_operator", null) != null)
            setOperadoresTelefonicos(JsonUtil.getJsonArrayFromJson(json, "ar_operator", null).toString());
        if(JsonUtil.getJsonArrayFromJson(json, "ev_approved_entities_id", null) != null){
            JSONArray entities = JsonUtil.getJsonArrayFromJson(json, "ev_approved_entities_id", null);
            List<String> entitiesList = new ArrayList<>();
            if(entities != null && entities.length() > 0){
                for(int i = 0; i < entities.length(); i++){
                    entitiesList.add(catalogService.getEntity(Integer.valueOf(entities.get(i).toString())).getShortName());
                }
                setApprovedEntitiesName(String.join(", ", entitiesList));
            }
        }
        if(JsonUtil.getJsonArrayFromJson(json, "pe_approved_entities_id", null) != null){
            JSONArray entities = JsonUtil.getJsonArrayFromJson(json, "pe_approved_entities_id", null);
            List<String> entitiesList = new ArrayList<>();
            if(entities != null && entities.length() > 0){
                for(int i = 0; i < entities.length(); i++){
                    entitiesList.add(catalogService.getEntity(Integer.valueOf(entities.get(i).toString())).getShortName());
                }
                setPreapprovedEntitiesName(String.join(", ", entitiesList));
            }
        }

        if(JsonUtil.getJsonArrayFromJson(json, "pe_rejected_entities_id", null) != null){
            JSONArray entities = JsonUtil.getJsonArrayFromJson(json, "pe_rejected_entities_id", null);
            List<String> entitiesList = new ArrayList<>();
            if(entities != null && entities.length() > 0){
                for(int i = 0; i < entities.length(); i++){
                    entitiesList.add(catalogService.getEntity(Integer.valueOf(entities.get(i).toString())).getShortName());
                }
                setPrerejectedEntitiesName(String.join(", ", entitiesList));
            }
        }

        if(JsonUtil.getJsonArrayFromJson(json, "ev_rejected_entities_id", null) != null){
            JSONArray entities = JsonUtil.getJsonArrayFromJson(json, "ev_rejected_entities_id", null);
            List<String> entitiesList = new ArrayList<>();
            if(entities != null && entities.length() > 0){
                for(int i = 0; i < entities.length(); i++){
                    entitiesList.add(catalogService.getEntity(Integer.valueOf(entities.get(i).toString())).getShortName());
                }
                setRejectedEntitiesName(String.join(", ", entitiesList));
            }
        }
        setPassedPersonalInformation(JsonUtil.getBooleanFromJson(json, "passed_personal_information", null));
        setPassedIncomeInformation(JsonUtil.getBooleanFromJson(json, "passed_income_information", null));
        setPassedOfferScreen(JsonUtil.getBooleanFromJson(json, "passed_offer_screen", null));
        setPassedVerificationScreen(JsonUtil.getBooleanFromJson(json, "passed_verification_screen", null));
        setPassedContract(JsonUtil.getBooleanFromJson(json, "passed_contract", null));
        setAssistedProcess(JsonUtil.getBooleanFromJson(json, "assisted_process", false));
        setSource(JsonUtil.getStringFromJson(json, "source", null));
        setMedium(JsonUtil.getStringFromJson(json, "medium", null));
        setCampaign(JsonUtil.getStringFromJson(json, "campaign", null));
        setTerm(JsonUtil.getStringFromJson(json, "term", null));
        setContent(JsonUtil.getStringFromJson(json, "content", null));
        if(JsonUtil.getStringFromJson(json, "default_evaluation_hard_filter_id", null) != null){
            setDefaultEvaluationHardFilterId(JsonUtil.getStringFromJson(json, "default_evaluation_hard_filter_id", null));
            List<HardFilter> filterTemp = new ArrayList<>();
            for(int i = 0; i < JsonUtil.getJsonArrayFromJson(json, "default_evaluation_hard_filter_id", null).length(); i++){
                filterTemp.add(catalogService.getHardFilterById(JsonUtil.getJsonArrayFromJson(json, "default_evaluation_hard_filter_id", null).getInt(i)));
            }
            setDefaultHardFilter(filterTemp);
        }

        if (JsonUtil.getStringFromJson(json, "application_origin", null) != null) {
            switch (JsonUtil.getStringFromJson(json, "application_origin", null)){
                case "M" : setOrigin("Messenger"); break;
                case "E" : setOrigin("Extranet"); break;
                case "A" : setOrigin("AAAAAAA"); break;
                case "W" : setOrigin("Web"); break;
            }
        }
        setPreEvaluationStatus(JsonUtil.getStringFromJson(json, "pre_evaluation_status", null));
        setEvaluationStatus(JsonUtil.getStringFromJson(json, "evaluation_status", null));
        setApplicationSource(JsonUtil.getStringFromJson(json, "application_source", null));
        if(JsonUtil.getIntFromJson(json, "branding_entity_id", null) != null)
            setEntityBranding(catalogService.getEntity(JsonUtil.getIntFromJson(json, "branding_entity_id", null)));
        if(JsonUtil.getBooleanFromJson(json, "waiting_pre_evaluation", null) != null)
            setWaitingPreEvaluation(JsonUtil.getBooleanFromJson(json, "waiting_pre_evaluation", null) ? "Sí" : "No");
        if(JsonUtil.getBooleanFromJson(json, "waiting_evaluation", null) != null)
            setWaitingEvaluation(JsonUtil.getBooleanFromJson(json, "waiting_evaluation", null) ? "Sí" : "No");
        setHasFraudAlerts(JsonUtil.getBooleanFromJson(json, "has_fraud_alerts", false) ? "Sí" : "No");
        setLastStatusUpdate(JsonUtil.getPostgresDateFromJson(json, "last_status_update", null));
        if(JsonUtil.getBooleanFromJson(json, "with_gclid", null) != null)
            setWithGclid(JsonUtil.getBooleanFromJson(json, "with_gclid", null) ? "Sí" : "No");
        setDisbursementDate(JsonUtil.getPostgresDateFromJson(json, "disbursement_date", null));
        setLoanCapital(JsonUtil.getDoubleFromJson(json, "loan_capital", null));
        if (JsonUtil.getBooleanFromJson(json, "pre_evaluation_executed", null) != null)
            setPreEvaluationExecuted(JsonUtil.getBooleanFromJson(json, "pre_evaluation_executed", null) ? 1 : 0);
        if (JsonUtil.getBooleanFromJson(json, "pre_evaluation_approved", null) != null)
            setPreEvaluationApproved(JsonUtil.getBooleanFromJson(json, "pre_evaluation_approved", null) ? 1 : 0);
        if (JsonUtil.getBooleanFromJson(json, "application_completed", null) != null)
            setApplicationCompleted(JsonUtil.getBooleanFromJson(json, "application_completed", null) ? 1 : 0);
        if (JsonUtil.getBooleanFromJson(json, "has_offers", null) != null)
            setHasOffers(JsonUtil.getBooleanFromJson(json, "has_offers", null) ? 1 : 0);
        if (JsonUtil.getBooleanFromJson(json, "has_selected_offer", null) != null)
            setHasSelectedOffer(JsonUtil.getBooleanFromJson(json, "has_selected_offer", null) ? 1 : 0);
        if (JsonUtil.getBooleanFromJson(json, "verification_completed", null) != null)
            setVerificationCompleted(JsonUtil.getBooleanFromJson(json, "verification_completed", null) ? 1 : 0);
        if (JsonUtil.getBooleanFromJson(json, "application_approved", null) != null)
            setApplicationApproved(JsonUtil.getBooleanFromJson(json, "application_approved", null) ? 1 : 0);
        if (JsonUtil.getBooleanFromJson(json, "application_approved_and_signed", null) != null)
            setApplicationApprovedAndSigned(JsonUtil.getBooleanFromJson(json, "application_approved_and_signed", null) ? 1 : 0);
        if (JsonUtil.getBooleanFromJson(json, "is_disbursed", null) != null)
            setIsDisbursed(JsonUtil.getBooleanFromJson(json, "is_disbursed", null) ? 1 : 0);
        if(JsonUtil.getJsonArrayFromJson(json, "referred_leads", null) != null){
            JSONArray referredLeads = JsonUtil.getJsonArrayFromJson(json, "referred_leads", null);
            List<String> entitiesList = new ArrayList<>();
            if(referredLeads != null && referredLeads.length() > 0){
                for(int i = 0; i < referredLeads.length(); i++){
                    entitiesList.add(referredLeads.get(i).toString());
                }
                setReferredLeads(String.join(", ", entitiesList));
            }
        }
        setMonthlyInstallment(JsonUtil.getDoubleFromJson(json, "monthly_installment", null));
        setTotalDebt(JsonUtil.getDoubleFromJson(json, "total_debt", null));
        setSocioeconomicLevel(JsonUtil.getStringFromJson(json, "socioeconomic_level", null));
        setWorstCalificationCurrent(JsonUtil.getStringFromJson(json, "month_1", null));
        setWorstCalificationU6M(JsonUtil.getStringFromJson(json, "month_1_6", null));
        setWorstCalification7to12(JsonUtil.getStringFromJson(json, "month_7_12", null));
        setWorstCalification13to24(JsonUtil.getStringFromJson(json, "month_13_24", null));

        if(JsonUtil.getJsonArrayFromJson(json, "hard_filter", null) != null){
            JSONArray hardFilterValuesJson = JsonUtil.getJsonArrayFromJson(json, "hard_filter", null);
            List<String> hardFiltersArrayList = new ArrayList<>();
            if(hardFilterValuesJson != null && hardFilterValuesJson.length() > 0){
                for(int i = 0; i < hardFilterValuesJson.length(); i++){
                    hardFiltersArrayList.add(hardFilterValuesJson.get(i).toString());
                }
                setHardFilterValues(String.join(", ", hardFiltersArrayList));
            }
        }

        if(JsonUtil.getJsonArrayFromJson(json, "policy", null) != null){
            JSONArray policyValuesJson = JsonUtil.getJsonArrayFromJson(json, "policy", null);
            List<String> policyValuesArrayList = new ArrayList<>();
            if(policyValuesJson != null && policyValuesJson.length() > 0){
                for(int i = 0; i < policyValuesJson.length(); i++){
                    policyValuesArrayList.add(policyValuesJson.get(i).toString());
                }
                setPolicyValues(String.join(", ", policyValuesArrayList));
            }
        }
        setQuestionFlow(JsonUtil.getCharacterFromJson(json, "question_flow", null));
        setOfferRejectionReasonKey(JsonUtil.getStringFromJson(json, "offer_rejection_reason", null));
        setPhoneNumber(JsonUtil.getStringFromJson(json, "phone_number", null));
        setEmail(JsonUtil.getStringFromJson(json, "email", null));

        if (JsonUtil.getJsonArrayFromJson(json, "ar_application_status_id", null) != null) {
            JSONArray applicationStatusIdValuesJson = JsonUtil.getJsonArrayFromJson(json, "ar_application_status_id", null);
            logApplicationStatusIds = new ArrayList<>();
            if (applicationStatusIdValuesJson != null && applicationStatusIdValuesJson.length() > 0) {
                for (int i = 0; i < applicationStatusIdValuesJson.length(); i++) {
                    logApplicationStatusIds.add(applicationStatusIdValuesJson.getInt(i));
                }
            }
        }

        if (JsonUtil.getJsonArrayFromJson(json, "ar_credit_status_id", null) != null) {
            JSONArray creditStatusIdValuesJson = JsonUtil.getJsonArrayFromJson(json, "ar_credit_status_id", null);
            logCreditStatusIds = new ArrayList<>();
            if (creditStatusIdValuesJson != null && creditStatusIdValuesJson.length() > 0) {
                for (int i = 0; i < creditStatusIdValuesJson.length(); i++) {
                    logCreditStatusIds.add(creditStatusIdValuesJson.getInt(i));
                }
            }
        }

        if (JsonUtil.getJsonArrayFromJson(json, "ar_process_question_id", null) != null) {
            JSONArray prcessQuestionSequenceIdValuesJson = JsonUtil.getJsonArrayFromJson(json, "ar_process_question_id", null);
            processQuestionSequenceIds = new ArrayList<>();
            if (prcessQuestionSequenceIdValuesJson != null && prcessQuestionSequenceIdValuesJson.length() > 0) {
                for (int i = 0; i < prcessQuestionSequenceIdValuesJson.length(); i++) {
                    processQuestionSequenceIds.add(prcessQuestionSequenceIdValuesJson.getInt(i));
                }
            }
        }

        setEntityCustomData(JsonUtil.getJsonObjectFromJson(json, "entity_custom_data", new JSONObject()));
        setStep1(JsonUtil.getIntFromJson(json, "step_1", null));
        setStep2(JsonUtil.getIntFromJson(json, "step_2", null));
        setStep3(JsonUtil.getIntFromJson(json, "step_3", null));
        setStep4(JsonUtil.getIntFromJson(json, "step_4", null));
        setStep5(JsonUtil.getIntFromJson(json, "step_5", null));
        setStep6(JsonUtil.getIntFromJson(json, "step_6", null));
        setStep7(JsonUtil.getIntFromJson(json, "step_7", null));
        setStep8(JsonUtil.getIntFromJson(json, "step_8", null));
        setStep9(JsonUtil.getIntFromJson(json, "step_9", null));
        setStep10(JsonUtil.getIntFromJson(json, "step_10", null));
        setStep11(JsonUtil.getIntFromJson(json, "step_11", null));
        setStep12(JsonUtil.getIntFromJson(json, "step_12", null));
        setStep13(JsonUtil.getIntFromJson(json, "step_13", null));
        setStep14(JsonUtil.getIntFromJson(json, "step_14", null));
        setStep15(JsonUtil.getIntFromJson(json, "step_15", null));
        setStep16(JsonUtil.getIntFromJson(json, "step_16", null));
        setStep17(JsonUtil.getIntFromJson(json, "step_17", null));
        setStep18(JsonUtil.getIntFromJson(json, "step_18", null));
        setStep19(JsonUtil.getIntFromJson(json, "step_19", null));
        setStep20(JsonUtil.getIntFromJson(json, "step_20", null));
        setStep21(JsonUtil.getIntFromJson(json, "step_21", null));
        setStep22(JsonUtil.getIntFromJson(json, "step_22", null));

        if (JsonUtil.getJsonArrayFromJson(json, "evaluation_detail", null) != null) {
            List<Policy> policies = new ArrayList<>();
            JSONArray policiesJson = JsonUtil.getJsonArrayFromJson(json, "evaluation_detail", null);
            for (int i = 0; i < policiesJson.length(); i++) {
                if (JsonUtil.getIntFromJson((JSONObject) policiesJson.get(i), "policy_id", null) != null)
                    policies.add(catalogService.getPolicyById(
                            JsonUtil.getIntFromJson((JSONObject) policiesJson.get(i), "policy_id", null))
                    );
            }
            setPoliciesRejected(policies);
        }

        if (JsonUtil.getJsonArrayFromJson(json, "preliminary_evaluation_detail", null) != null) {
            List<HardFilter> hardFilters = new ArrayList<>();
            JSONArray hardFiltersJson = JsonUtil.getJsonArrayFromJson(json, "preliminary_evaluation_detail", null);
            for (int i = 0; i < hardFiltersJson.length(); i++) {
                if (JsonUtil.getIntFromJson((JSONObject) hardFiltersJson.get(i), "hard_filter_id", null) != null)
                    hardFilters.add(catalogService.getHardFilterById(
                            JsonUtil.getIntFromJson((JSONObject) hardFiltersJson.get(i), "hard_filter_id", null))
                    );
            }
            setHardFiltersRejected(hardFilters);
        }
        Integer loanRejectionId = JsonUtil.getIntFromJson(json, "rejection_loan_reason_id", null);
        if(loanRejectionId != null) setApplicationRejectionReason(catalogService.getApplicationRejectionReason(loanRejectionId));
        setIsCredit(JsonUtil.getBooleanFromJson(json, "loan_is_credit", null));
        if (JsonUtil.getIntFromJson(json, "credit_rejection_reason_id", null) != null) {
            setRejectionReason(catalogService.getCreditRejectionReason(JsonUtil.getIntFromJson(json, "credit_rejection_reason_id", null)));
        }
        if(JsonUtil.getIntFromJson(json, "selected_entity_product_parameter_id", null) != null) setEntityProductParam(catalogService.getEntityProductParamById(JsonUtil.getIntFromJson(json, "selected_entity_product_parameter_id", null)));
        setSelectedOfferBankAccountDataType(JsonUtil.getCharacterFromJson(json, "selected_offer_bank_account_data_type", null));
    }


    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public String getLoanApplicationCode() {
        return loanApplicationCode;
    }

    public void setLoanApplicationCode(String loanApplicationCode) {
        this.loanApplicationCode = loanApplicationCode;
    }

    public IdentityDocumentType getIdentityDocumentType() {
        return identityDocumentType;
    }

    public void setIdentityDocumentType(IdentityDocumentType identityDocumentType) {
        this.identityDocumentType = identityDocumentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public DisbursementType getDisbursementType() {
        return disbursementType;
    }

    public void setDisbursementType(DisbursementType disbursementType) {
        this.disbursementType = disbursementType;
    }

    public String getFinalResult() {
        return finalResult;
    }

    public void setFinalResult(String finalResult) {
        this.finalResult = finalResult;
    }

    public String getRejectionInstance() {
        return rejectionInstance;
    }

    public void setRejectionInstance(String rejectionInstance) {
        this.rejectionInstance = rejectionInstance;
    }

    public String getRejectionType() {
        return rejectionType;
    }

    public void setRejectionType(String rejectionType) {
        this.rejectionType = rejectionType;
    }

    public String getHardFilterId() {
        return hardFilterId;
    }

    public void setHardFilterId(String hardFilterId) {
        this.hardFilterId = hardFilterId;
    }

    public String getHardFilterMessage() {
        return hardFilterMessage;
    }

    public void setHardFilterMessage(String hardFilterMessage) {
        this.hardFilterMessage = hardFilterMessage;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getPolicyMessage() {
        return policyMessage;
    }

    public void setPolicyMessage(String policyMessage) {
        this.policyMessage = policyMessage;
    }

    public String getPep() {
        return pep;
    }

    public void setPep(String pep) {
        this.pep = pep;
    }

    public LoanApplicationReason getReason() {
        return reason;
    }

    public void setReason(LoanApplicationReason reason) {
        this.reason = reason;
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

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Integer getDependents() {
        return dependents;
    }

    public void setDependents(Integer dependents) {
        this.dependents = dependents;
    }

    public Ubigeo getUbigeo() {
        return ubigeo;
    }

    public void setUbigeo(Ubigeo ubigeo) {
        this.ubigeo = ubigeo;
    }

    public HousingType getHousingType() {
        return housingType;
    }

    public void setHousingType(HousingType housingType) {
        this.housingType = housingType;
    }

    public StudyLevel getStudyLevel() {
        return studyLevel;
    }

    public void setStudyLevel(StudyLevel studyLevel) {
        this.studyLevel = studyLevel;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public Integer getEmploymentTime() {
        return employmentTime;
    }

    public void setEmploymentTime(Integer employmentTime) {
        this.employmentTime = employmentTime;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public Double getFixedGrossIncome() {
        return fixedGrossIncome;
    }

    public void setFixedGrossIncome(Double fixedGrossIncome) {
        this.fixedGrossIncome = fixedGrossIncome;
    }

    public Double getVariableGrossIncome() {
        return variableGrossIncome;
    }

    public void setVariableGrossIncome(Double variableGrossIncome) {
        this.variableGrossIncome = variableGrossIncome;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Ocupation getOcupation() {
        return ocupation;
    }

    public void setOcupation(Ocupation ocupation) {
        this.ocupation = ocupation;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Nationality getNationality() {
        return nationality;
    }

    public void setNationality(Nationality nationality) {
        this.nationality = nationality;
    }

    public List<Referral> getReferrals() {
        return referrals;
    }

    public void setReferrals(List<Referral> referrals) {
        this.referrals = referrals;
    }

    public LoanApplicationStatus getLoanApplicationStatus() {
        return loanApplicationStatus;
    }

    public void setLoanApplicationStatus(LoanApplicationStatus loanApplicationStatus) {
        this.loanApplicationStatus = loanApplicationStatus;
    }

    public CreditStatus getCreditStatus() {
        return creditStatus;
    }

    public void setCreditStatus(CreditStatus creditStatus) {
        this.creditStatus = creditStatus;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public Double getSbsMonthlyInstallment() {
        return sbsMonthlyInstallment;
    }

    public void setSbsMonthlyInstallment(Double sbsMonthlyInstallment) {
        this.sbsMonthlyInstallment = sbsMonthlyInstallment;
    }

    public Double getLoanApplicationRCI() {
        return loanApplicationRCI;
    }

    public void setLoanApplicationRCI(Double loanApplicationRCI) {
        this.loanApplicationRCI = loanApplicationRCI;
    }

    public Double getCreditRCI() {
        return creditRCI;
    }

    public void setCreditRCI(Double creditRCI) {
        this.creditRCI = creditRCI;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(Integer evaluations) {
        this.evaluations = evaluations;
    }

    public Integer getApprovedEntities() {
        return approvedEntities;
    }

    public void setApprovedEntities(Integer approvedEntities) {
        this.approvedEntities = approvedEntities;
    }

    public Entity getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(Entity selectedEntity) {
        this.selectedEntity = selectedEntity;
    }

    public String getOfferAmount() {
        return offerAmount;
    }

    public void setOfferAmount(String offerAmount) {
        this.offerAmount = offerAmount;
    }

    public String getOfferInstallments() {
        return offerInstallments;
    }

    public void setOfferInstallments(String offerInstallments) {
        this.offerInstallments = offerInstallments;
    }

    public String getOfferTEAS() {
        return offerTEAS;
    }

    public void setOfferTEAS(String offerTEAS) {
        this.offerTEAS = offerTEAS;
    }

    public String getOfferTCEAS() {
        return offerTCEAS;
    }

    public void setOfferTCEAS(String offerTCEAS) {
        this.offerTCEAS = offerTCEAS;
    }

    public Double getMaxAmountSameInstallments() {
        return maxAmountSameInstallments;
    }

    public void setMaxAmountSameInstallments(Double maxAmountSameInstallments) {
        this.maxAmountSameInstallments = maxAmountSameInstallments;
    }

    public Double getMaxAmountOffer() {
        return maxAmountOffer;
    }

    public void setMaxAmountOffer(Double maxAmountOffer) {
        this.maxAmountOffer = maxAmountOffer;
    }

    public String getEflProccess() {
        return eflProccess;
    }

    public void setEflProccess(String eflProccess) {
        this.eflProccess = eflProccess;
    }

    public String getEflScore() {
        return eflScore;
    }

    public void setEflScore(String eflScore) {
        this.eflScore = eflScore;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public String getSocialNetworks() {
        return socialNetworks;
    }

    public void setSocialNetworks(String socialNetworks) {
        this.socialNetworks = socialNetworks;
    }

    public Double getMortgageAmount() {
        return mortgageAmount;
    }

    public void setMortgageAmount(Double mortgageAmount) {
        this.mortgageAmount = mortgageAmount;
    }

    public String getIovationType() {
        return iovationType;
    }

    public void setIovationType(String iovationType) {
        this.iovationType = iovationType;
    }

    public String getIovationOs() {
        return iovationOs;
    }

    public void setIovationOs(String iovationOs) {
        this.iovationOs = iovationOs;
    }

    public String getIovationBrowser() {
        return iovationBrowser;
    }

    public void setIovationBrowser(String iovationBrowser) {
        this.iovationBrowser = iovationBrowser;
    }

    public Integer getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Integer currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public ProcessQuestion getProcessQuestion() {
        return processQuestion;
    }

    public void setProcessQuestion(ProcessQuestion processQuestion) {
        this.processQuestion = processQuestion;
    }

    public String getOperadoresTelefonicos() {
        return operadoresTelefonicos;
    }

    public void setOperadoresTelefonicos(String operadoresTelefonicos) {
        this.operadoresTelefonicos = operadoresTelefonicos;
    }

    public String getApprovedEntitiesName() {
        return approvedEntitiesName;
    }

    public void setApprovedEntitiesName(String approvedEntitiesName) {
        this.approvedEntitiesName = approvedEntitiesName;
    }

    public String getPreapprovedEntitiesName() {
        return preapprovedEntitiesName;
    }

    public void setPreapprovedEntitiesName(String preapprovedEntitiesName) {
        this.preapprovedEntitiesName = preapprovedEntitiesName;
    }

    public String getRejectedEntitiesName() {
        return rejectedEntitiesName;
    }

    public void setRejectedEntitiesName(String rejectedEntitiesName) {
        this.rejectedEntitiesName = rejectedEntitiesName;
    }

    public String getPrerejectedEntitiesName() {
        return prerejectedEntitiesName;
    }

    public void setPrerejectedEntitiesName(String prerejectedEntitiesName) {
        this.prerejectedEntitiesName = prerejectedEntitiesName;
    }

    public Boolean getPassedPersonalInformation() {
        return passedPersonalInformation;
    }

    public void setPassedPersonalInformation(Boolean passedPersonalInformation) {
        this.passedPersonalInformation = passedPersonalInformation;
    }

    public Boolean getPassedIncomeInformation() {
        return passedIncomeInformation;
    }

    public void setPassedIncomeInformation(Boolean passedIncomeInformation) {
        this.passedIncomeInformation = passedIncomeInformation;
    }

    public Boolean getPassedOfferScreen() {
        return passedOfferScreen;
    }

    public void setPassedOfferScreen(Boolean passedOfferScreen) {
        this.passedOfferScreen = passedOfferScreen;
    }

    public Boolean getPassedVerificationScreen() {
        return passedVerificationScreen;
    }

    public void setPassedVerificationScreen(Boolean passedVerificationScreen) {
        this.passedVerificationScreen = passedVerificationScreen;
    }

    public Boolean getPassedContract() {
        return passedContract;
    }

    public void setPassedContract(Boolean passedContract) {
        this.passedContract = passedContract;
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

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getAssistedProcess() { return assistedProcess; }

    public void setAssistedProcess(Boolean assistedProcess) { this.assistedProcess = assistedProcess; }

    public String getDefaultEvaluationHardFilterId() {
        return defaultEvaluationHardFilterId;
    }

    public void setDefaultEvaluationHardFilterId(String defaultEvaluationHardFilterId) {
        this.defaultEvaluationHardFilterId = defaultEvaluationHardFilterId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public List<HardFilter> getHardFilter() {
        return hardFilter;
    }

    public void setHardFilter(List<HardFilter> hardFilter) {
        this.hardFilter = hardFilter;
    }

    public String getDefaultHardFilterString() {
        Gson gson = new Gson();
        List<Integer> defaultHardFiltersArr = new ArrayList<>();
        if(getDefaultHardFilter() != null){
            for(HardFilter hardFilter : getDefaultHardFilter()){
                defaultHardFiltersArr.add(hardFilter.getId());
            }
        }else{
            return "";
        }

        return gson.toJson(defaultHardFiltersArr);
    }

    public String getDefaultPolicyString() {
        Gson gson = new Gson();
        List<Integer> defaultPoliciesArr = new ArrayList<>();
        if(getDefaultEvaluationPolicies() != null){
            for(Policy policy : getDefaultEvaluationPolicies()){
                defaultPoliciesArr.add(policy.getPolicyId());
            }
        }else{
            return "";
        }

        return gson.toJson(defaultPoliciesArr);
    }

    public String getDefaultPolicyMessageString() {
        Gson gson = new Gson();
        List<String> defaultPoliciesArr = new ArrayList<>();
        if(getDefaultEvaluationPolicies() != null){
            for(Policy policy : getDefaultEvaluationPolicies()){
                defaultPoliciesArr.add(policy.getMessage());
            }
        }else{
            return "";
        }

        return gson.toJson(defaultPoliciesArr);
    }

    public String getPolicyString() {
        Gson gson = new Gson();
        List<Integer> policiesArr = new ArrayList<>();
        if(getPolicies() != null){
            for(Policy policy : getPolicies()){
                policiesArr.add(policy.getPolicyId());
            }
        }else{
            return "";
        }

        return gson.toJson(policiesArr);
    }

    public String getPolicyMessageString() {
        Gson gson = new Gson();
        List<String> policiesArr = new ArrayList<>();
        if(getPolicies() != null){
            for(Policy policy : getPolicies()){
                policiesArr.add(policy.getMessage());
            }
        }else{
            return "";
        }

        return gson.toJson(policiesArr);
    }

    public void setDefaultHardFilter(List<HardFilter> defaultHardFilter) {
        this.defaultHardFilter = defaultHardFilter;
    }

    public List<HardFilter> getDefaultHardFilter() {
        return defaultHardFilter;
    }

    public String getPreEvaluationStatus() {
        return preEvaluationStatus;
    }

    public void setPreEvaluationStatus(String preEvaluationStatus) {
        this.preEvaluationStatus = preEvaluationStatus;
    }

    public String getEvaluationStatus() {
        return evaluationStatus;
    }

    public void setEvaluationStatus(String evaluationStatus) {
        this.evaluationStatus = evaluationStatus;
    }

    public Entity getEntityBranding() {
        return entityBranding;
    }

    public void setEntityBranding(Entity entityBranding) {
        this.entityBranding = entityBranding;
    }

    public String getApplicationSource() {
        return applicationSource;
    }

    public void setApplicationSource(String applicationSource) {
        this.applicationSource = applicationSource;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWaitingPreEvaluation() {
        return waitingPreEvaluation;
    }

    public void setWaitingPreEvaluation(String waitingPreEvaluation) {
        this.waitingPreEvaluation = waitingPreEvaluation;
    }

    public String getWaitingEvaluation() {
        return waitingEvaluation;
    }

    public void setWaitingEvaluation(String waitingEvaluation) {
        this.waitingEvaluation = waitingEvaluation;
    }

    public String getHasFraudAlerts() {
        return hasFraudAlerts;
    }

    public void setHasFraudAlerts(String hasFraudAlerts) {
        this.hasFraudAlerts = hasFraudAlerts;
    }

    public Date getLastStatusUpdate() {
        return lastStatusUpdate;
    }

    public void setLastStatusUpdate(Date lastStatusUpdate) {
        this.lastStatusUpdate = lastStatusUpdate;
    }

    public String getWithGclid() {
        return withGclid;
    }

    public void setWithGclid(String withGclid) {
        this.withGclid = withGclid;
    }

    public List<Policy> getDefaultEvaluationPolicies() {
        return defaultEvaluationPolicies;
    }

    public void setDefaultEvaluationPolicies(List<Policy> defaultEvaluationPolicies) {
        this.defaultEvaluationPolicies = defaultEvaluationPolicies;
    }

    public List<Policy> getPolicies() {
        return policies;
    }

    public void setPolicies(List<Policy> policies) {
        this.policies = policies;
    }

    public Date getDisbursementDate() {
        return disbursementDate;
    }

    public void setDisbursementDate(Date disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public Double getLoanCapital() {
        return loanCapital;
    }

    public void setLoanCapital(Double loanCapital) {
        this.loanCapital = loanCapital;
    }

    public Integer getPreEvaluationExecuted() {
        return preEvaluationExecuted;
    }

    public void setPreEvaluationExecuted(Integer preEvaluationExecuted) {
        this.preEvaluationExecuted = preEvaluationExecuted;
    }

    public Integer getPreEvaluationApproved() {
        return preEvaluationApproved;
    }

    public void setPreEvaluationApproved(Integer preEvaluationApproved) {
        this.preEvaluationApproved = preEvaluationApproved;
    }

    public Integer getApplicationCompleted() {
        return applicationCompleted;
    }

    public void setApplicationCompleted(Integer applicationCompleted) {
        this.applicationCompleted = applicationCompleted;
    }

    public Integer getHasOffers() {
        return hasOffers;
    }

    public void setHasOffers(Integer hasOffers) {
        this.hasOffers = hasOffers;
    }

    public Integer getHasSelectedOffer() {
        return hasSelectedOffer;
    }

    public void setHasSelectedOffer(Integer hasSelectedOffer) {
        this.hasSelectedOffer = hasSelectedOffer;
    }

    public Integer getVerificationCompleted() {
        return verificationCompleted;
    }

    public void setVerificationCompleted(Integer verificationCompleted) {
        this.verificationCompleted = verificationCompleted;
    }

    public Integer getApplicationApproved() {
        return applicationApproved;
    }

    public void setApplicationApproved(Integer applicationApproved) {
        this.applicationApproved = applicationApproved;
    }

    public Integer getApplicationApprovedAndSigned() {
        return applicationApprovedAndSigned;
    }

    public void setApplicationApprovedAndSigned(Integer applicationApprovedAndSigned) {
        this.applicationApprovedAndSigned = applicationApprovedAndSigned;
    }

    public Integer getIsDisbursed() {
        return isDisbursed;
    }

    public void setIsDisbursed(Integer isDisbursed) {
        this.isDisbursed = isDisbursed;
    }

    public String getReferredLeads() {  return referredLeads; }

    public void setReferredLeads(String referredLeads) { this.referredLeads = referredLeads; }

    public List<String> getHardFilterMessages() { return HardFilterMessages; }

    public void setHardFilterMessages(List<String> hardFilterMessages) { HardFilterMessages = hardFilterMessages; }

    public Double getMonthlyInstallment() {
        return monthlyInstallment;
    }

    public void setMonthlyInstallment(Double monthlyInstallment) {
        this.monthlyInstallment = monthlyInstallment;
    }

    public Double getTotalDebt() {
        return totalDebt;
    }

    public void setTotalDebt(Double totalDebt) {
        this.totalDebt = totalDebt;
    }

    public String getSocioeconomicLevel() {
        return socioeconomicLevel;
    }

    public void setSocioeconomicLevel(String socioeconomicLevel) {
        this.socioeconomicLevel = socioeconomicLevel;
    }

    public String getWorstCalificationCurrent() {
        return worstCalificationCurrent;
    }

    public void setWorstCalificationCurrent(String worstCalificationCurrent) {
        this.worstCalificationCurrent = worstCalificationCurrent;
    }

    public String getWorstCalificationU6M() {
        return worstCalificationU6M;
    }

    public void setWorstCalificationU6M(String worstCalificationU6M) {
        this.worstCalificationU6M = worstCalificationU6M;
    }

    public String getWorstCalification7to12() {
        return worstCalification7to12;
    }

    public void setWorstCalification7to12(String worstCalification7to12) {
        this.worstCalification7to12 = worstCalification7to12;
    }

    public String getWorstCalification13to24() {
        return worstCalification13to24;
    }

    public void setWorstCalification13to24(String worstCalification13to24) {
        this.worstCalification13to24 = worstCalification13to24;
    }
    public String getHardFilterValues() {
        return hardFilterValues;
    }

    public void setHardFilterValues(String hardFilterValues) {
        this.hardFilterValues = hardFilterValues;
    }

    public String getPolicyValues() {
        return policyValues;
    }

    public void setPolicyValues(String policyValues) {
        this.policyValues = policyValues;
    }

    public Character getQuestionFlow() {
        return questionFlow;
    }

    public void setQuestionFlow(Character questionFlow) {
        this.questionFlow = questionFlow;
    }

    public String getOfferRejectionReasonKey() {
        return offerRejectionReasonKey;
    }

    public void setOfferRejectionReasonKey(String offerRejectionReasonKey) {
        this.offerRejectionReasonKey = offerRejectionReasonKey;
    }

    public List<Integer> getLogApplicationStatusIds() {
        return logApplicationStatusIds;
    }

    public void setLogApplicationStatusIds(List<Integer> logApplicationStatusIds) {
        this.logApplicationStatusIds = logApplicationStatusIds;
    }

    public List<Integer> getLogCreditStatusIds() {
        return logCreditStatusIds;
    }

    public void setLogCreditStatusIds(List<Integer> logCreditStatusIds) {
        this.logCreditStatusIds = logCreditStatusIds;
    }

    public List<Integer> getProcessQuestionSequenceIds() {
        return processQuestionSequenceIds;
    }

    public void setProcessQuestionSequenceIds(List<Integer> processQuestionSequenceIds) {
        this.processQuestionSequenceIds = processQuestionSequenceIds;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public JSONObject getEntityCustomData() {
        return entityCustomData;
    }

    public String getEntityCustomData(String key) {
        if (entityCustomData == null)
            return null;

        return JsonUtil.getStringFromJson(entityCustomData, key, null);
    }

    public void setEntityCustomData(JSONObject entityCustomData) {
        this.entityCustomData = entityCustomData;
    }

    public Integer getBanbifBaseDataAsInteger(String dataKey){
        if(entityCustomData != null && entityCustomData.has(LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey())){
            JSONObject banbifBase = entityCustomData.getJSONObject(LoanApplication.EntityCustomDataKeys.BANBIF_BASE_PREAPROBADA.getKey());
            return JsonUtil.getIntFromJson(banbifBase, dataKey, null);
        }
        return null;
    }

    public Double getAztecaCobranzaRecoveryDataAsDouble(String dataKey){
        if(entityCustomData != null && entityCustomData.has(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_BASE_GATEWAY.getKey())){
            JSONObject base = entityCustomData.getJSONObject(LoanApplication.EntityCustomDataKeys.BANCO_AZTECA_BASE_GATEWAY.getKey());
            return JsonUtil.getDoubleFromJson(base, dataKey, null);
        }
        return null;
    }

    public Double getAztecaCobranzaVigenteDataAsDouble(String dataKey){
        if(entityCustomData != null && entityCustomData.has(LoanApplication.EntityCustomDataKeys.BANTOTAL_GATEWAY_VIGENTE_DATA.getKey())){
            JSONObject base = entityCustomData.getJSONObject(LoanApplication.EntityCustomDataKeys.BANTOTAL_GATEWAY_VIGENTE_DATA.getKey());
            return JsonUtil.getDoubleFromJson(base, dataKey, null);
        }
        return null;
    }

    public String getBanbifRequestType() {
        if (entityCustomData != null && entityCustomData.has(LoanApplication.EntityCustomDataKeys.BANBIF_TIPO_SOLICITUD.getKey())) {
            if (entityCustomData.optString(LoanApplication.EntityCustomDataKeys.BANBIF_TIPO_SOLICITUD.getKey()).equals(LoanApplication.BANBIF_LOAN_APPLICATION_TYPE_ONLINE))
                return "Online";
            else if (entityCustomData.optString(LoanApplication.EntityCustomDataKeys.BANBIF_TIPO_SOLICITUD.getKey()).equals(LoanApplication.BANBIF_LOAN_APPLICATION_TYPE_TELEPHONE))
                return "Banca telefónica";
        }
        return null;
    }

    public Integer getStep1() {
        return step1;
    }

    public void setStep1(Integer step1) {
        this.step1 = step1;
    }

    public Integer getStep2() {
        return step2;
    }

    public void setStep2(Integer step2) {
        this.step2 = step2;
    }

    public Integer getStep3() {
        return step3;
    }

    public void setStep3(Integer step3) {
        this.step3 = step3;
    }

    public Integer getStep4() {
        return step4;
    }

    public void setStep4(Integer step4) {
        this.step4 = step4;
    }

    public Integer getStep5() {
        return step5;
    }

    public void setStep5(Integer step5) {
        this.step5 = step5;
    }

    public Integer getStep6() {
        return step6;
    }

    public void setStep6(Integer step6) {
        this.step6 = step6;
    }

    public Integer getStep7() {
        return step7;
    }

    public void setStep7(Integer step7) {
        this.step7 = step7;
    }

    public Integer getStep8() {
        return step8;
    }

    public void setStep8(Integer step8) {
        this.step8 = step8;
    }

    public Integer getStep9() {
        return step9;
    }

    public void setStep9(Integer step9) {
        this.step9 = step9;
    }

    public Integer getStep10() {
        return step10;
    }

    public void setStep10(Integer step10) {
        this.step10 = step10;
    }

    public Integer getStep11() {
        return step11;
    }

    public void setStep11(Integer step11) {
        this.step11 = step11;
    }

    public Integer getStep12() {
        return step12;
    }

    public void setStep12(Integer step12) {
        this.step12 = step12;
    }

    public Integer getStep13() {
        return step13;
    }

    public void setStep13(Integer step13) {
        this.step13 = step13;
    }

    public Integer getStep14() {
        return step14;
    }

    public void setStep14(Integer step14) {
        this.step14 = step14;
    }

    public Integer getStep15() {
        return step15;
    }

    public void setStep15(Integer step15) {
        this.step15 = step15;
    }

    public Integer getStep16() {
        return step16;
    }

    public void setStep16(Integer step16) {
        this.step16 = step16;
    }

    public Integer getStep17() {
        return step17;
    }

    public void setStep17(Integer step17) {
        this.step17 = step17;
    }

    public Integer getStep18() {
        return step18;
    }

    public void setStep18(Integer step18) {
        this.step18 = step18;
    }

    public String getHardFilterOrPolicyMessageToShow() {
        if (getPoliciesRejected() != null && !getPoliciesRejected().isEmpty()) {
            return getPoliciesRejected().get(0).getPolicy();
        } else if (getHardFiltersRejected() != null && !getHardFiltersRejected().isEmpty()) {
            return getHardFiltersRejected().get(0).getHardFilterMessage();
        }
        return null;
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

    public ApplicationRejectionReason getApplicationRejectionReason() {
        return applicationRejectionReason;
    }

    public void setApplicationRejectionReason(ApplicationRejectionReason applicationRejectionReason) {
        this.applicationRejectionReason = applicationRejectionReason;
    }

    public Boolean getIsCredit() {
        return isCredit;
    }

    public void setIsCredit(Boolean credit) {
        isCredit = credit;
    }

    public CreditRejectionReason getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(CreditRejectionReason rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Integer getStep19() {
        return step19;
    }

    public void setStep19(Integer step19) {
        this.step19 = step19;
    }

    public Integer getStep20() {
        return step20;
    }

    public void setStep20(Integer step20) {
        this.step20 = step20;
    }

    public EntityProductParams getEntityProductParam() {
        return entityProductParam;
    }

    public void setEntityProductParam(EntityProductParams entityProductParam) {
        this.entityProductParam = entityProductParam;
    }

    public Character getSelectedOfferBankAccountDataType() {
        return selectedOfferBankAccountDataType;
    }

    public void setSelectedOfferBankAccountDataType(Character selectedOfferBankAccountDataType) {
        this.selectedOfferBankAccountDataType = selectedOfferBankAccountDataType;
    }

    public Integer getStep21() {
        return step21;
    }

    public void setStep21(Integer step21) {
        this.step21 = step21;
    }

    public Integer getStep22() {
        return step22;
    }

    public void setStep22(Integer step22) {
        this.step22 = step22;
    }

    public Boolean getCredit() {
        return isCredit;
    }

    public void setCredit(Boolean credit) {
        isCredit = credit;
    }
}
