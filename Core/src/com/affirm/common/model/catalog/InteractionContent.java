package com.affirm.common.model.catalog;

import com.affirm.common.service.CatalogService;
import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jrodriguez on 31/08/16.
 */
public class InteractionContent implements Serializable {

    public static final int REGISTRO_AUTHTOKEN_SMS = 10;
    public static final int REGISTRO_AUTHTOKEN_CALL = 20;
    public static final int REGISTRO_CONTRACTTOKEN_SMS = 30;// TODO
    public static final int REGISTRO_CONTRACTTOKEN_CALL = 40;// TODO
    public static final int REGISTRO_LOANAPPLICATION_APPROVAL_MAIL = 100;
    public static final int REGISTRO_LOANAPPLICATION_SIGNATURE_MAIL = 102;
    public static final int REGISTRO_LOANAPPLICATION_SIGNATURE_MAIL_CONSOLIDATION = 106;
    public static final int REGISTRO_LOANAPPLICATION_SIGNATURE_MAIL_AGREEMENT = 103;
    public static final int SALARY_ADVANCE_REGISTRO_LOANAPPLICATION_SIGNATURE_MAIL = 3012;
    public static final int CONFIRMACION_SALARYADVANCE__LOANAPPLICATION_MAIL = 3011;
    public static final int CONFIRMACION_AGREEMENT__LOANAPPLICATION_MAIL = 8;
    public static final int VALIDATION_SALARYADVANCE__LOANAPPLICATION_MAIL = 3010;
    public static final int PAYMENT_RECIVED_MAIL = 5;
    public static final int PAYMENT_RECIVED_SMS = 172;
    public static final int TOTAL_PAYMENT_RECIVED_MAIL = 181;
    public static final int TOTAL_PAYMENT_RECIVED_SMS = 182;
    public static final int DISBURSEMENT_MAIL = 104;
    public static final int DISBURSEMENT_CONSOLIDATION_OPEN_MAIL = 18;
    public static final int DISBURSEMENT_DEALER_ENTITY = 19;
    public static final int DISBURSEMENT_SMS = 9;
    public static final int DISBURSEMENT_MAIL_SALARY_ADVANCE = 7;
    public static final int DISBURSEMENT_SMS_SALARY_ADVANCE = 195;
    public static final int FORCED_APPROVAl_MAIL = 110;
    public static final int REJECTION_MAIL = 111;
    public static final int EMPLOYEE_SALARY_ADVANCE_WELCOME_MAIL = 3;
    public static final int EMPLOYEE_AGREEMENT_WELCOME_MAIL = 4;
    public static final int SALARY_ADVANCE_TOTAL_PAYMENT_RECIVED_MAIL = 198;
    public static final int EMPLOYER_TOTAL_PAYMENT_RECIVED_MAIL = 200;
    public static final int CONSOLIDATION_MAIL = 11;
    public static final int AGREEMENT_PAGARE_MAIL = 17;
    public static final int EXTRANET_ENTITY_ORIGINATED_RETIREMENT_DISBURSEMENT_MAIL = 12;
    public static final int DISBURSEMENT_MAIL_NO_DOCUMENT = 112;
    public static final int REGISTRO_LOANAPPLICATION_EXT_APPROVAL_MAIL_RETIREMENT = 14;
    public static final int REGISTRO_LOANAPPLICATION_EXT_APPROVAL_MAIL_DEPOSIT = 16;
    public static final int REGISTRO_LOANAPPLICATION_SIGNATURE_MAIL_VEHICLE = 114;
    public static final int REGISTRO_LOANAPPLICATION_SIGNATURE_SMS_VEHICLE = 115;
    public static final int SCHEDULE_PHYSICAL_SIGNATURE_MAIL = 116;
    public static final int SCHEDULE_PHYSICAL_SIGNATURE_SMS = 117;
    public static final int VEHICLE_DISBURSED_MAIL = 120;
    public static final int VEHICLE_DISBURSED_SMS = 121;
    public static final int REGISTRO_LOANAPPLICATION_SIGNATURE_MAIL_VEHICLE_V2 = 122;
    public static final int VEHICLE_PARTIAL_DOWNPAYMENT = 123;
    public static final int VEHICLE_TOTAL_DOWNPAYMENT = 124;
    public static final int RESET_PASSWORD = 13;
    public static final int MISSING_DOCUMENTATION = 203;
    public static final int COMPARTAMOS_PENDING_EVALUATION = 666;
    public static final int COMPARTAMOS_READY_EVALUATION = 777;
    public static final int AFFILIATOR_APPROVED = 201;
    public static final int AFFILIATOR_FAILED = 202;
    public static final int FRAUD_ALERTS_TO_REVIEW = 204;
    public static final int DISBURMENT_EMAIL_MULTIFINANZAS=5401;
    public static final int RETURN_ASSISTED_PROCESS = 645; //TODO: Change, its dummy
    public static final int SCHEDULE_GUARANTEED = 207;
    public static final int CONFIRM_SCHEDULE_GUARANTEED = 208;
    public static final int TWO_FACTOR_AUTHENTICATION_INSTRUCTIONS = 209;
    public static final int RESET_PASSWORD_WITH_TFA_INSTRUCTIONS = 210;
    public static final int ACCESO_GUARANTEED_EMAIL= 126;
    public static final int PIN_PROBLEM_EMAIL= 211;
    public static final int AELU_PRELIMINARY_DOCUMENTATION = 222;
    public static final int AELU_PENDIENTE_PAGARE = 224;
    public static final int TARJETAs_PERUANAS_PREPAGO_APROBACION_BO = 229;
    public static final int RECHAZO_EN_OFERTA_1 = 231; // amountInsufficient
    public static final int RECHAZO_EN_OFERTA_3 = 230; //teaTooHigh
    public static final int RECHAZO_EN_OFERTA_4 = 234; //noAgency
    public static final int RECHAZO_EN_OFERTA_6 = 233; // undecided
    public static final int RECHAZO_EN_OFERTA_7 = 232; //other
    public static final int RECHAZO_EN_OFERTA_2 = 235; //other
    public static final int MATCH_TRANSACTIONAL_1 = 236;
    public static final int MATCH_TRANSACTIONAL_2 = 237;
    public static final int MATCH_TRANSACTIONAL_3 = 238;
    public static final int MATCH_TRANSACTIONAL_4 = 239;
    public static final int MATCH_TRANSACTIONAL_5 = 240;
    public static final int ACCESO_LIBRE_DISPONIBILIDAD_DISBURSEMENT = 241;
    public static final int CREATE_USER_EXTRANET_ENTITY = 245;
    public static final int ACCESO_FIRST_INSTALLMENT_TO_EXPIRE = 8012;
    public static final int ACCESO_N_INSTALLMENT_TO_EXPIRE = 8013;
    public static final int ACCESO_ALERT_EXPIRED_INSTALLMENT = 8014;
    //    public static final int CHAT_CUOTAS_POR_VENCER = 8015;
//    public static final int CHAT_CUOTAS_CON_MORA = 8016;
//    public static final int CHAT_FOLLOWUP_PROCESO = 8017;
    public static final int CHAT_FOLLOWUP_BEFORE_OFFER = 8018;
    public static final int CHAT_FOLLOWUP_WITH_OFFER = 8019;
    public static final int ACCESO_CHAT_FIRST_INSTALLMENT_TO_EXPIRE = 8020;
    public static final int ACCESO_CHAT_N_INSTALLMENT_TO_EXPIRE = 8021;
    public static final int ACCESO_CHAT_ALERT_EXPIRED_INSTALLMENT = 8022;
    public static final int ACCESO_CHAT_FIRST_INSTALLMENT_TO_EXPIRE_TODAY = 8023;
    public static final int ACCESO_CHAT_N_INSTALLMENT_TO_EXPIRE_TODAY = 8024;
    public static final int CREDIGOB_APROVED_LOAN_APPLICATION = 246;
    public static final int RESET_PASSWORD_BANCO_DEL_SOL = 23;
    public static final int RESET_PASSWORD_BRANDING = 24;
    public static final int CREATE_USER_EXTRANET_ENTITY_BRANDING = 247;
    public static final int SEND_NEW_LOAN_LINK_ENTITY_EXTRANET = 646;
    public static final int PERU_PROCESS_FINISHED_EMAIL = 8026;
    public static final int PERU_PROCESS_SEND_EMAIL_VERIFICATION = 8027;
    public static final int ARG_PROCESS_FINISHED_EMAIL = 8028;
    public static final int ARG_PROCESS_SEND_EMAIL_VERIFICATION = 8029;
    public static final int FDLM_CREDIT_DISBURSEMENT = 8032;
    public static final int BANBIF_TC_EN_CAMINO = 9102;
    public static final int BANBIF_TC_EN_CAMINO_MAS_EFECTIVO = 9103;
    public static final int FINANSOL_APROBADO = 248;
    public static final int PRISMA_APROBADO = 249;
    public static final int AZTECA_APROBADO = 251;
    public static final int AZTECA_APROBADO_CALL_CENTER = 252;
    public static final int AZTECA_APROBADO_ONLINE = 253;
    public static final int AZTECA_COBRANZA_ACUERDO_EXTRAJUDICIAL = 254;
    public static final int AZTECA_COBRANZA_CARTA_NO_ADEUDO= 255;
    public static final int EMAIL_VERIFICATION_PIN= 258;
    public static final int REGISTRO_LOANAPPLICATION_APPROVAL_MAIL_BANK_ACCOUNT = 259;
    public static final int AZTECA_CUENTA_SOLICITUD_APROBADA = 260;
    public static final int REGISTRO_LOANAPPLICATION_APPROVAL_MAIL_AZTECA_ONLINE = 261;
    public static final int ALFIN_BANTOTAL_PAGO_DE_CUOTA = 262;
    public static final int REJECTED_MAIL_AZTECA = 263;
    public static final int NUEVA_OFERTA_CONSUMO_AZTECA = 264;
    public static final int ALFIN_BANTOTAL_COBRANZA_RECOVERY_PAGO_DE_CUOTA = 265;

    public static final int ALFIN_CAMBIO_DE_FECHA_PRIMERA_CUOTA = 266;
    private Integer id;
    private InteractionType type;
    private String content;
    private String subject;
    private String body;
    private String fromMail;
    private String template;
    private String category;
    private String hourOfDay;
    private JSONObject originalJson;
    private Integer countryId;

    public InteractionContent getCopy(CatalogService catalog){
        InteractionContent interactionContent = new InteractionContent();
        interactionContent.fillFromDb(originalJson, catalog);
        return interactionContent;
    }

    public void fillFromDb(JSONObject json, CatalogService catalog) {
        originalJson = json;
        setId(JsonUtil.getIntFromJson(json, "interaction_content_id", null));
        if (JsonUtil.getIntFromJson(json, "interaction_type_id", null) != null) {
            setType(catalog.getInteractionType(JsonUtil.getIntFromJson(json, "interaction_type_id", null)));
        }
        setContent(JsonUtil.getStringFromJson(json, "interaction_content", null));
        setSubject(JsonUtil.getStringFromJson(json, "subject", null));
        setBody(JsonUtil.getStringFromJson(json, "body", null));
        setFromMail(JsonUtil.getStringFromJson(json, "from_mail", null));
        setTemplate(JsonUtil.getStringFromJson(json, "template", null));
        setCategory(JsonUtil.getStringFromJson(json, "category", null));
        setHourOfDay(JsonUtil.getStringFromJson(json, "hour_of_day", null));
        setCountryId(JsonUtil.getIntFromJson(json, "country_id", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public InteractionType getType() {
        return type;
    }

    public void setType(InteractionType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFromMail() {
        return fromMail;
    }

    public void setFromMail(String fromMail) {
        this.fromMail = fromMail;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(String hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }
}
