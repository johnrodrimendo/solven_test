package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by john on 08/11/16.
 */
public class ProcessQuestion implements Serializable {

    public enum Question {
        IDENTITY_DOCUMENT(Constants.IDENTITY_DOCUMENT),
        BIRTHDATE(Constants.BIRTHDATE),
        LOAN_REASON(Constants.LOAN_REASON),
        CAR_USE(Constants.CAR_USE),
        CAR_AMOUNT(Constants.CAR_AMOUNT),
        LOAN_AMOUNT(Constants.LOAN_AMOUNT),
        PROVABLE_EARNINGS(Constants.PROVABLE_EARNINGS),
        CREATE_ACCOUNT(Constants.CREATE_ACCOUNT),
        RUNNING_EVALUATION(Constants.RUNNING_EVALUATION),
        APPROVE_EVALUATION(Constants.APPROVE_EVALUATION),
        DISAPPROVE_EVALUATION(Constants.DISAPPROVE_EVALUATION),
        DISAPPROVE_ITS_OK(Constants.DISAPPROVE_ITS_OK),
        DISAPPROVE_ADVICES(Constants.DISAPPROVE_ADVICES),

        ARE_YOU_READY(Constants.ARE_YOU_READY),
        MARITAL_STATUS(Constants.MARITAL_STATUS),
        HOME_ADDRESS(Constants.HOME_ADDRESS),
        HOME_TYPE(Constants.HOME_TYPE),
        STUDIES_LEVEL(Constants.STUDIES_LEVEL),
        PROFESSION(Constants.PROFESSION),
        PRINCIPAL_INCOME_TYPE(Constants.PRINCIPAL_INCOME_TYPE),
        RUC_WORK_PLACE_DEPENDENT(Constants.RUC_WORK_PLACE_DEPENDENT),
        PHONE_NUMBER_WORK_PLACE_DEPENDENT(Constants.PHONE_NUMBER_WORK_PLACE_DEPENDENT),
        OCUPATION_WORK_PLACE_DEPENDENT(Constants.OCUPATION_WORK_PLACE_DEPENDENT),
        FIXED_MONTHLY_GROSS_INCOME_DEPENDENT(Constants.FIXED_MONTHLY_GROSS_INCOME_DEPENDENT),
        VARIABLE_U3M_INCOME_DEPENDENT(Constants.VARIABLE_U3M_INCOME_DEPENDENT),

        INDEPENDENT_INCOME_TYPE(Constants.INDEPENDENT_INCOME_TYPE),
        RUC_PROFESSIONAL_SERVICES(Constants.RUC_PROFESSIONAL_SERVICES),
        SERVICE_TYPE_PROFESSIONAL_SERVICES(Constants.SERVICE_TYPE_PROFESSIONAL_SERVICES),
        RUC_IMPORTANT_CUSTOMER_PROFESSIONAL_SERVICES(Constants.RUC_IMPORTANT_CUSTOMER_PROFESSIONAL_SERVICES),
        RUC_SECOND_IMPORTANT_CUSTOMER_PROFESSIONAL_SERVICES(Constants.RUC_SECOND_IMPORTANT_CUSTOMER_PROFESSIONAL_SERVICES),
        FIXED_U6M_INCOME_PROFESSIONAL_SERVICES(Constants.FIXED_U6M_INCOME_PROFESSIONAL_SERVICES),
        RUC_OWN_BUSINESS(Constants.RUC_OWN_BUSINESS),
        SHAREHOLDING_OWN_BUSINESS(Constants.SHAREHOLDING_OWN_BUSINESS),
        ADDRESS_OWN_BUSINESS(Constants.ADDRESS_OWN_BUSINESS),
        SALES_U12M_OWN_BUSINESS(Constants.SALES_U12M_OWN_BUSINESS),
        NET_RESULT_LAST_EXERCISE_OWN_BUSINESS(Constants.NET_RESULT_LAST_EXERCISE_OWN_BUSINESS),
        SALES_PERCENTAGE_FIXED_COSTS_OWN_BUSINESS(Constants.SALES_PERCENTAGE_FIXED_COSTS_OWN_BUSINESS),
        SALES_PERCENTAGE_VARIABLE_COSTS_OWN_BUSINESS(Constants.SALES_PERCENTAGE_VARIABLE_COSTS_OWN_BUSINESS),
        SALES_PERCENTAGE_IMPORTANT_CUSTOMER_OWN_BUSINESS(Constants.SALES_PERCENTAGE_IMPORTANT_CUSTOMER_OWN_BUSINESS),
        AVERAGE_DAILY_INCOME_OWN_BUSINESS(Constants.AVERAGE_DAILY_INCOME_OWN_BUSINESS),
        COMPENSATION_U12M_OWN_BUSINESS(Constants.COMPENSATION_U12M_OWN_BUSINESS),
        INCOME_RENT(Constants.INCOME_RENT),
        OTHER_INCOME_CAN_DEMONSTRATE(Constants.OTHER_INCOME_CAN_DEMONSTRATE),
        OTHER_INCOME_TYPE(Constants.OTHER_INCOME_TYPE),
        OFFER(Constants.OFFER),
        VERIFICATION_EMAIL(Constants.VERIFICATION_EMAIL),
        VERIFICATION_SOCIAL_NETWORKS(Constants.VERIFICATION_SOCIAL_NETWORKS),
        VERIFICATION_PHONE_NUMBER(Constants.VERIFICATION_PHONE_NUMBER),
        VERIFICATION_PHONE_NUMBER_PIN(Constants.VERIFICATION_PHONE_NUMBER_PIN),
        VERIFICATION_REFERRALS(Constants.VERIFICATION_REFERRALS),
        VERIFICATION_PARTNER(Constants.VERIFICATION_PARTNER),
        VERIFICATION_PAST_ADDRESS(Constants.VERIFICATION_PAST_ADDRESS),
        VERIFICATION_BANK_PRODUCTS(Constants.VERIFICATION_BANK_PRODUCTS),
        VERIFICATION_SELFIE(Constants.VERIFICATION_SELFIE),
        VERIFICATION_DOCUMENTATION(Constants.VERIFICATION_DOCUMENTATION),
        WAITING_APPROVAL(Constants.WAITING_APPROVAL),
        EFL_PSYCHOMETRIC(Constants.EFL_PSYCHOMETRIC),
        CONSOLIDATION_OFFER(Constants.CONSOLIDATION_OFFER),
        DEPENDENTS(Constants.DEPENDENTS),
        VEHICLE_OFFER(Constants.VEHICLE_OFFER),
        ADRESS_WORK_PLACE_DEPENDENT(Constants.ADRESS_WORK_PLACE_DEPENDENT),
        BANK_ACCOUNT_INFORMATION(Constants.BANK_ACCOUNT_INFORMATION),
        PARENTS_NAME(Constants.PARENTS_NAME),
        FDM_AREA_OCCUPATION(Constants.FDM_AREA_OCCUPATION),
        FDM_CONTRACT_TYPE(Constants.FDM_CONTRACT_TYPE),
        EMAIL_AND_CELLPHONE(Constants.EMAIL_AND_CELLPHONE),
        AZTECA_SELECT_DISBURSEMENT(Constants.AZTECA_SELECT_DISBURSEMENT),
        AZTECA_FINAL_MESSAGE(Constants.AZTECA_FINAL_MESSAGE),
        EMAIL_PIN_VERIFICATION(Constants.EMAIL_PIN_VERIFICATION),
        BANBIF_OFERTA_MAS_EFECTIVO(Constants.BANBIF_OFERTA_MAS_EFECTIVO),
        BANBIF_SELECT_DISBURSEMENT_MAS_EFECTIVO(Constants.BANBIF_SELECT_DISBURSEMENT_MAS_EFECTIVO),
        ;

        public int id;

        Question(int id) {
            this.id = id;
        }

        public static Question byId(Integer id) {
            if (id == null)
                return null;

            for (Question q : values()) {
                if (q.id == id) return q;
            }
            return null;
        }

        public int getId() {
            return id;
        }

        public static class Constants {
            public static final int IDENTITY_DOCUMENT = 1;
            public static final int BIRTHDATE = 2;
            public static final int LOAN_REASON = 3;
            public static final int CAR_USE = 4;
            public static final int CAR_AMOUNT = 5;
            public static final int LOAN_AMOUNT = 6;
            public static final int PROVABLE_EARNINGS = 7;
            public static final int CREATE_ACCOUNT = 8;
            public static final int RUNNING_EVALUATION = 9;
            public static final int APPROVE_EVALUATION = 10;
            public static final int DISAPPROVE_EVALUATION = 11;
            public static final int DISAPPROVE_ITS_OK = 12;
            public static final int DISAPPROVE_ADVICES = 13;
            public static final int ARE_YOU_READY = 20;
            public static final int MARITAL_STATUS = 21;
            public static final int HOME_ADDRESS = 22;
            public static final int HOME_TYPE = 23;
            public static final int STUDIES_LEVEL = 24;
            public static final int PROFESSION = 25;
            public static final int PRINCIPAL_INCOME_TYPE = 26;
            public static final int RUC_WORK_PLACE_DEPENDENT = 27;
            public static final int PHONE_NUMBER_WORK_PLACE_DEPENDENT = 28;
            public static final int OCUPATION_WORK_PLACE_DEPENDENT = 29;
            public static final int FIXED_MONTHLY_GROSS_INCOME_DEPENDENT = 30;
            public static final int VARIABLE_U3M_INCOME_DEPENDENT = 31;
            public static final int INDEPENDENT_INCOME_TYPE = 32;
            public static final int RUC_PROFESSIONAL_SERVICES = 33;
            public static final int SERVICE_TYPE_PROFESSIONAL_SERVICES = 34;
            public static final int RUC_IMPORTANT_CUSTOMER_PROFESSIONAL_SERVICES = 35;
            public static final int RUC_SECOND_IMPORTANT_CUSTOMER_PROFESSIONAL_SERVICES = 36;
            public static final int FIXED_U6M_INCOME_PROFESSIONAL_SERVICES = 37;
            public static final int RUC_OWN_BUSINESS = 38;
            public static final int SHAREHOLDING_OWN_BUSINESS = 39;
            public static final int ADDRESS_OWN_BUSINESS = 61;
            public static final int SALES_U12M_OWN_BUSINESS = 40;
            public static final int NET_RESULT_LAST_EXERCISE_OWN_BUSINESS = 41;
            public static final int SALES_PERCENTAGE_FIXED_COSTS_OWN_BUSINESS = 42;
            public static final int SALES_PERCENTAGE_VARIABLE_COSTS_OWN_BUSINESS = 43;
            public static final int SALES_PERCENTAGE_IMPORTANT_CUSTOMER_OWN_BUSINESS = 44;
            public static final int AVERAGE_DAILY_INCOME_OWN_BUSINESS = 45;
            public static final int COMPENSATION_U12M_OWN_BUSINESS = 46;
            public static final int INCOME_RENT = 47;
            public static final int OTHER_INCOME_CAN_DEMONSTRATE = 49;
            public static final int OTHER_INCOME_TYPE = 48;
            public static final int OFFER = 50;
            public static final int VERIFICATION_EMAIL = 51;
            public static final int VERIFICATION_SOCIAL_NETWORKS = 52;
            public static final int VERIFICATION_PHONE_NUMBER = 53;
            public static final int VERIFICATION_PHONE_NUMBER_PIN = 54;
            public static final int VERIFICATION_REFERRALS = 55;
            public static final int VERIFICATION_PARTNER = 56;
            public static final int VERIFICATION_PAST_ADDRESS = 57;
            public static final int VERIFICATION_BANK_PRODUCTS = 58;
            public static final int VERIFICATION_SELFIE = 59;
            public static final int VERIFICATION_DOCUMENTATION = 60;
            public static final int WAITING_APPROVAL = 62;
            public static final int COMPARISON_REASON = 70;
            public static final int COMPARISON_URGENCY_REASON = 71;
            public static final int COMPARISON_LOAN_REASON = 72;
            public static final int COMPARISON_CREDIT_CARD_REASON = 73;
            public static final int COMPARISON_CREDIT_HISTORY_REASON = 74;
            public static final int COMPARISON_INCOME = 75;
            public static final int COMPARISON_AMOUNT = 76;
            public static final int COMPARISON_SELFEVALUATION = 77;
            public static final int EFL_PSYCHOMETRIC = 78;
            public static final int CONSOLIDATION_OFFER = 79;
            public static final int DEPENDENTS = 80;
            public static final int VEHICLE_OFFER = 81;
            public static final int ADRESS_WORK_PLACE_DEPENDENT = 82;
            public static final int BANK_ACCOUNT_INFORMATION = 83;
            public static final int DEPENDENT_HAS_OTHER_INCOMES = 85;
            public static final int RUN_IOVATION = 86;
            public static final int RUN_EMAILAGE = 87;
            public static final int OCUPATION_START_DATE_DEPENDENT = 88;
            public static final int OCUPATION_START_DATE_PROFESSIONAL_SERVICES = 89;
            public static final int OCUPATION_START_DATE_OWN_BUSINESS = 90;
            public static final int OCUPATION_START_DATE_RENT = 91;
            public static final int RUC_RENT = 92;
            public static final int EMAIL = 93;
            public static final int CONSOLIDATION_DEBTS_AMOUNT = 94;
            public static final int RUNNING_PREEVALUATION = 95;
            public static final int PENSIONER_AMOUNT = 98;
            public static final int OCUPATION_START_DATE_MONOTRIBUTISTA = 99;
            public static final int ADRESS_WORK_PLACE_MONOTRIBUTISTA = 100;
            public static final int PHONE_NUMBER_WORK_PLACE_MONOTRIBUTISTA = 101;
            public static final int OCUPATION_WORK_PLACE_MONOTRIBUTISTA = 102;
            public static final int MONTHLY_NET_INCOME_MONOTRIBUTISTA = 103;
            public static final int OCUPATION_START_DATE_SHAREHOLDER = 104;
            public static final int EMAIL_BEFORE_PREEVALUATION = 105;
            public static final int IOVATION_AFTER_PREEVALUATION = 106;
            public static final int PRODUCT_CATEGORY_REASON = 107;
            public static final int PARTNER_IDENTIFICATION = 108;
            public static final int OTHER_INCOME_CANT_DEMOSTRATE = 109;
            public static final int MISSING_DOCUMENTATION = 110;
            public static final int IOVATION_AFTER_MISSING_DOCUMENTATION = 111;
            public static final int PEP = 113;
            public static final int ASSISTED_PROCESS = 115;
            public static final int ASSISTED_PROCESS_WAITING = 116;
            public static final int OFFER_REJECTION_REASON = 117;
            public static final int RESIDENCE_TIME = 118;
            public static final int NO_RENIEC_NAME_BIRTHDAY = 119;
            public static final int SALARY_FREQUENCY = 120;
            public static final int MAINTAINED_CAR = 121;
            public static final int GUARANTEED_PRODUCT = 122;
            public static final int APPROVE_WENANCE = 123;
            public static final int GUARANTEED_BEFORE_PRE_EVALUATION = 124;
            public static final int RESCUE_OFFERS = 125;
            public static final int RESCUE_CONSOLIDATION_DEBTS = 126;
            public static final int BANK_ACCOUNT_STATEMENT = 127;
            public static final int WRONG_PHONE_NUMBER = 128;
            public static final int NO_RENIEC_BEFORE_PRE_EVALUATION = 129;
            public static final int ALL_PERSONAL_INFORMATION = 130;
            public static final int CUSTOM_PROFESSION = 131;
            public static final int ALL_INCOME = 132;
            public static final int PREPAY_CARD_DELIVERY_PLACE = 133;
            public static final int LEAD_RESULT = 134;
            public static final int CONTRACT_SIGNATURE = 135;
            public static final int ONLY_AMOUNT = 136;
            public static final int BANCO_DEL_SOL_PERSON_FORM = 137;
            public static final int BANCO_DEL_SOL_DATA_VALIDATION = 138;
            public static final int RESULT_LEAD = 139;
            public static final int PROFESSION_OCUPATIONS = 140;
            public static final int WENANCE_BANKCODE = 141;
            public static final int HOME_ADDRESS_AND_TYPE = 142;
            public static final int PROFESSION_AND_OCUPATIONS = 143;
            public static final int BANBIF_ACCOUNT_OPENING = 144;
            public static final int CREDIGOB_SERVICE_ORDER = 145;
            public static final int PARENTS_NAME = 146;
            public static final int FDM_AREA_OCCUPATION = 148;
            public static final int FDM_CONTRACT_TYPE = 149;
            public static final int FDLM_VERIFICATION_DOCUMENTATION = 150;
            public static final int WORK_PLACE_NAME_AND_PHONE = 151;
            public static final int PROFESSION_OCCUPATION_AND_POSITION = 152;
            public static final int FDLM_DECLARACION_ASEGURABILIDAD = 153;
            public static final int HOME_ADDRESS_DISGREGATED = 154;
            public static final int WORKPLACE_ADDRESS_DISGREGATED = 155;
            public static final int BANBIF_OFFERS = 158;
            public static final int BANBIF_WAITING_APPROVAL = 159;
            public static final int BANBIF_DISAPPROVED_EVALUATION = 160;
            public static final int BANBIF_CHALLENGE_QUESTION = 161;
            public static final int BANBIF_GOOD_SCORE = 162;
            public static final int PRESTAMYPE_FORMULARIO = 156;
            public static final int PRESTAMYPE_APROBADO = 157;
            public static final int EMAIL_AND_CELLPHONE = 164;
            public static final int AZTECA_SELECT_PRODUCT = 165;
            public static final int MERGE_OCUPATION_WORK_PLACE_DEPENDENT_PEP_PROFESSION_AND_OCUPATIONS = 166;
            public static final int BANK_ACCOUNT_OFFERS = 173;
            public static final int AZTECA_SELECT_DISBURSEMENT = 174;
            public static final int AZTECA_FINAL_MESSAGE = 175;
            public static final int EMAIL_PIN_VERIFICATION = 176;
            public static final int IDENTITY_VALIDATION_OFFERS = 177;
            public static final int AZTECA_ADVISER_ROLE_DIAGNOSIS = 178;

            public static final int BANBIF_OFERTA_MAS_EFECTIVO = 181;
            public static final int BANBIF_SELECT_DISBURSEMENT_MAS_EFECTIVO = 182;
        }
    }

    private Integer id;
    private String question;
    private ProcessQuestionCategory category;
    private transient JSONObject results;
    private String stringResults;
    private Boolean skippable;
    private Boolean skip;
    private transient JSONObject configuration;
    private String stringConfiguration;

    public ProcessQuestion() {
    }

    public ProcessQuestion(Integer id, JSONObject results, Boolean skippable) {
        this.id = id;
        this.results = results;
        this.skippable = skippable;
    }

    public void fillFromDb(JSONObject json, CatalogService catalogService) {
        setId(JsonUtil.getIntFromJson(json, "process_question_id", null));
        setQuestion(JsonUtil.getStringFromJson(json, "process_question", null));
        if (JsonUtil.getIntFromJson(json, "process_questions_category_id", null) != null)
            setCategory(catalogService.getProcessQuestionCategory(JsonUtil.getIntFromJson(json, "process_questions_category_id", null)));
    }

    public void fillFromProcessQuestionConfiguration(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "id", null));
        setResults(JsonUtil.getJsonObjectFromJson(json, "results", null));
        setSkippable(JsonUtil.getBooleanFromJson(json, "skippable", null));
        setSkip(JsonUtil.getBooleanFromJson(json, "skip", null));
        setConfiguration(JsonUtil.getJsonObjectFromJson(json, "configuration", null));
        setStringResults(JsonUtil.getStringFromJson(json, "results", null));
        setStringConfiguration(JsonUtil.getStringFromJson(json, "configuration", null));
    }

    public Integer getResultQuestionId(String result) {
        return JsonUtil.getIntFromJson(results, result, null);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public JSONObject getResults() {
        if (results != null) {
            return results;
        } else {
            if (this.getStringResults() != null) {
                JSONObject jsonObj = new JSONObject(this.getStringResults());
                return jsonObj;
            } else {
                return null;
            }
        }
    }

    public void setResults(JSONObject results) {
        this.results = results;
    }

    public Boolean getSkippable() {
        return skippable;
    }

    public void setSkippable(Boolean skippable) {
        this.skippable = skippable;
    }

    public ProcessQuestionCategory getCategory() {
        return category;
    }

    public void setCategory(ProcessQuestionCategory category) {
        this.category = category;
    }

    public Boolean getSkip() {
        return skip;
    }

    public void setSkip(Boolean skip) {
        this.skip = skip;
    }

    public JSONObject getConfiguration() {
        if (results != null) {
            return configuration;
        } else {
            if (this.getStringConfiguration() != null) {
                JSONObject jsonObj = new JSONObject(this.getStringConfiguration());
                return jsonObj;
            } else {
                return null;
            }
        }
    }

    public void setConfiguration(JSONObject configuration) {
        this.configuration = configuration;
    }

    public String getStringResults() {
        return stringResults;
    }

    public void setStringResults(String stringResults) {
        this.stringResults = stringResults;
    }

    public String getStringConfiguration() {
        return stringConfiguration;
    }

    public void setStringConfiguration(String stringConfiguration) {
        this.stringConfiguration = stringConfiguration;
    }
}
