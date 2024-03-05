package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * Created by dev5 on 21/03/18.
 */
public class Policy {

    public static final int UNEMPLOYED = 12;
    public static final int MIGRATIONS = 13;
    public static final int MINIMUM_INCOMES = 14;
    public static final int OVERINDEBTEDNESS = 15;
    public static final int SUNAT_DEBT = 16;
    public static final int RUC_PRINCIPAL_OCUPATION = 17;
    public static final int RUC_SECONDARY_OCUPATION = 18;
    public static final int SAT_DEBT = 19;
    public static final int REDAM_DEBT = 20;
    public static final int PARTNER_U1MN = 21;
    public static final int INFORMAL = 22;
    public static final int HOUSEKEEPER = 23;
    public static final int FACEBOOK_FEW_FRIENDS = 24;
    public static final int NEGATIVA_BASE_SOLVEN = 25;
    public static final int PHONE_BASE = 26;
    public static final int EFRU24MC = 27;
    public static final int EFRU24MJ = 28;
    public static final int EFRU24MDP = 29;
    public static final int EFRU712DEF = 30;
    public static final int EFRU9MR = 31;
    public static final int EFRU3456MCPP = 32;
    public static final int EFRU2MN = 33;
    public static final int EFMU24MC = 34;
    public static final int EFMU24MJ = 35;
    public static final int EFMU24MDP = 36;
    public static final int EFMU712DEF = 37;
    public static final int EFMU9MR = 38;
    public static final int EFMU3456MCPP = 39;
    public static final int EFMU2MN = 40;
    public static final int SICOM = 41;
    public static final int IC = 42;
    public static final int RL = 43;
    public static final int BC = 44;
    public static final int GUARANTOR = 45;
    public static final int SCORE_BUREAU = 47;
    public static final int STUDENT = 48;
    public static final int EMPLOYMENT_TIME_1 = 49;// twice
    public static final int EXPIRED_CONTRACT = 50;
    public static final int SALARY_GANISHMENT = 51;
    public static final int UNPAID_LEAVE = 52;
    public static final int NEW_LOAN_NOT_ALLOWED = 53;
    public static final int AGREEMENT_NOT_AVAILABLE = 54;
    public static final int AGREEMENT_NOT_ALLOWED = 56;
    public static final int EXPENSIVE_ABACOS_DEBT = 57;
    public static final int EFL_RED = 58;
    public static final int ACTIVE_ENTITIES = 61;
    public static final int DISPOSABLE_INCOME = 63;
    public static final int INCOME_TO_MONTHLY_EARNINGS_RATIO = 64;
    public static final int AVOID_DISTRICTS_HOME = 66;
    public static final int EFL_SCORE = 67;
    public static final int CLIENT_SKIP_TEST = 69;
    public static final int EFX_SCORE = 71;
    public static final int NOT_IN_LIMA = 72;
    public static final int EMPLOYMENT_TIME_2 = 82;// twice
    public static final int HOUSING_TYPE = 83;
    public static final int RENT_TYPE = 88;
    public static final int MINIMUM_INCOME_BUREAU = 90;
    public static final int SOBREENDEUDAMIENTO_BUREAU = 91;
    public static final int PUNTAJE_CONYUGUE = 92;
    public static final int TAXPAYER_TYPE = 93;
    public static final int AVOID_DISTRICTS_OCUPATION = 94;
    public static final int SAME_PROVINCE_JOB_AND_HOME = 95;
    public static final int AVOID_DISTRICTS_NON_PERU_UBIGEOS = 96;
    public static final int DEUDA_ONPE = 97;
    public static final int MONTO_MAXIMO_MENOR_AL_MINIMO_RIPLEY_PRESTAMO_YA = 99;
    public static final int TIPO_DE_ACTIVIDAD = 100;
    public static final int ALLOWED_PROCESS = 104;
    public static final int PARTNER_JUDICIAL = 109;
    public static final int PARTNER_CASTIGOS = 110;
    public static final int PARTNER_UXMDP = 111;
    public static final int PARTNER_X_MONTHS_RCC = 112;
    public static final int MIN_ESSALUD_EMPLOYEES_QUANTITY = 114;
    public static final int ACCEPTED_LOCALITIES = 115;
    public static final int SCORE_EMAILAGE = 117;
    public static final int ACCESO_SCORE_LD_CONSUMO = 118;
    public static final int EMPLOYMENT_TIME_BY_ACTIVITY_TYPE = 119;
    public static final int FINANSOL_RADAR_REPOS = 120;
    public static final int BANBIF_RESULTADO_CUESTIONARIO = 121;
    public static final int BANBIF_OTRA_NACIONALIDAD = 122;
    public static final int INGRESO_MINIMO_PARAMETRIZABLE = 54009;
    public static final int PROBLEMAS_EN_AFIP = 54010;
    public static final int PAREJA_SITUACION_5 = 54011;
    public static final int PAREJA_SITUACION_4 = 54012;
    public static final int PAREJA_SITUACION_3 = 54013;
    public static final int PAREJA_SITUACION_2 = 54014;
    public static final int NOSIS_SITUACION_5 = 54015;
    public static final int NOSIS_SITUACION_4 = 54016;
    public static final int NOSIS_SITUACION_3 = 54017;
    public static final int NOSIS_SITUACION_2 = 54018;
    public static final int ENDEUDAMIENTO = 54019;
    public static final int IND_DE_CONSULTA_FINANCIERAS_GT_2UM = 54020;
    public static final int PROCESOS_JUDICIALES = 54021;
    public static final int HASTA_X_ENTIDADES_FINANCIADORES = 54022;
    public static final int NSE_BUREAU = 54026;
    public static final int SCORE_BUREAU_ARG = 54027;
    public static final int ALERTAS_JUDICIALES_QUIEBRA_JUICIOS = 54031;
    public static final int REGISTRA_LIQUIDACION_PLAN_SOCIAL_O_PROGRAMA_DE_EMPLEO = 54032;
    public static final int CONTINUIDAD_LABORAL_BUREAU = 54033;
    public static final int SIN_ACTIVIDAD_BUREAU = 54034;
    public static final int TIEMPO_EN_EL_EMPLEO_BUREAU = 54035;
    public static final int ASIGNACION_UNIVERSAL_POR_HIJO = 54036;
    public static final int FALLECIDOS = 54037;
    public static final int SIN_ACTIVIDAD_JUBILADO = 54038;
    public static final int ACTIVIDAD_DEPENDENCIA__ACTIVIDAD_EMPLEADOR_EXCLUIDO = 54039;
    public static final int SCORE_TOPAZ_NUMERACION = 57001;
    public static final int NIVELES_RIESGO_LISTAS_CONTROL = 57003;
//    public static final int SCORE_DATACREDITO = 57004;// TODO BORRAR
    public static final int UXM_DATACREDITO = 57005;
    public static final int TOPAZ = 57006;
    public static final int FDLM_ENDEUDAMIENTO = 57007;
    public static final int SCORE_DATACREDITO_DOCUMENTO_VIGENTE = 57008;
    public static final int SCORE_DATACREDITO_RANGO_EDAD = 57009;
    public static final int SCORE_DATACREDITO_NO_ESTADOS_EN_ESTADO_CUENTA_CUENTA_CORRIENTE_CUENTA_AHORRO = 57010;
    public static final int SCORE_DATACREDITO_SITUACION_CERO = 57011;
    public static final int SCORE_DATACREDITO_CALIFICACION_EN_CUENTA_AHORRO_CUENTA_CORRIENTE = 57012;
    public static final int SCORE_DATACREDITO_ESTADO_CUENTA_EN_CUENTA_CARTERA_TARJETA_CREDITO = 57013;
    public static final int SCORE_DATACREDITO_INGRESO_MENSUAL_MINIMO = 57014;
    public static final int SCORE_DATACREDITO_NO_ADJETIVOS_TARJETA_CREDITO_CUENTA_CARTERA = 57015;
    public static final int SCORE_DATACREDITO_NO_ALERTA = 57016;
    public static final int SCORE_DATACREDITO_CON_ADJETIVO_DISTINTO_CUENTA_AHORRO_CUENTA_CORRIENTE = 57017;
    public static final int SCORE_DATACREDITO_ESTADO_PAGO = 57018;
    public static final int SCORE_DATACREDITO_NO_FORMA_PAGO = 57019;
    public static final int NOSIS_REFERENCIA_COMERCIALES = 54040;
    public static final int NOSIS_NO_BANCARIZADOS = 54041;
    public static final int SENTINEL_PRISMA = 123;
    public static final int SENTINEL_PRISMA_EVALUATION = 124;
    public static final int PEP = 125;
    public static final int FATCA = 126;
    public static final int FINANSOL_CALIFICACION_Y_ENTIDADES_RADAR = 130;
    public static final int VERAZ_BUREAU = 54042;
    public static final int PROVISIONAL_DEBTORS = 54043;
    public static final int BANTOTAL_PEP_FATCA_LISTANEGRA = 127;
    public static final int VALIDATED_CELLPHONE_EMAIL = 128;
    public static final int BANTOTAL_VALIDACION_EMAIL_CELULAR = 129;
    public static final int ACCESO_EVALUACION_GENERICA = 131;
    public static final int PARTNER_AGE = 132;

    public static final int IP_GEOLOCATION = 133;
    public static final int IFE_BENEFICIARIES = 54044;
    public static final int MIN_MAX_AGE = 134;

    private int policyId;
    private String policy;
    private String message;
    private int helpMessage;
    private Integer step;

    public void fillFromDb(JSONObject json, MessageSource messageSource, Locale locale){
        if(JsonUtil.getIntFromJson(json, "policy_id", null) != null)
            setPolicyId(JsonUtil.getIntFromJson(json, "policy_id", null));
        if(JsonUtil.getStringFromJson(json, "message", null) != null)
            setMessage(messageSource.getMessage(JsonUtil.getStringFromJson(json, "message", null), null, locale));
        if(JsonUtil.getIntFromJson(json, "help_message", null) != null)
            setHelpMessage(JsonUtil.getIntFromJson(json, "help_message", null));
        setPolicy(JsonUtil.getStringFromJson(json, "policy", null));
        setStep(JsonUtil.getIntFromJson(json, "step", null));
    }

    public int getPolicyId() {
        return policyId;
    }

    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getHelpMessage() {
        return helpMessage;
    }

    public void setHelpMessage(int helpMessage) {
        this.helpMessage = helpMessage;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }
}
