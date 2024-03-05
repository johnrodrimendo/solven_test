package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

/**
 * Created by dev5 on 21/03/18.
 */
public class HardFilter {

    public static final int MIN_AGE = 1;
    public static final int MAX_AGE = 2;
    public static final int U24MC = 3;
    public static final int U24MJ = 4;
    public static final int U24MDP = 5;
    public static final int U712DEF = 6;
    public static final int U9MR = 7;
    public static final int U3456MCPP = 8;
    public static final int U3MN = 9;
    public static final int EXP_PERU = 10;
    public static final int NEGATIVE_BASE = 11;
    public static final int PAST_DUE = 12;
    public static final int CLOSED_PLATFORM = 55;
    public static final int ACTIVE_ENTITIES = 59;
    public static final int ACTIVE_PROCCES_IN_ANOTHER_ENTITY = 60;
    public static final int ACTIVE_DEBT_CONSOLIDATION = 62;
    public static final int U6SV = 65;
    public static final int NOCONSOLIDABLEDEBTS = 68;
    public static final int DEBT_PERCENTAGE_ENTITIES = 70;
    public static final int CREDITOS_MEDIANA_EMPRESA = 78;
    public static final int ENTIDADES_ACREEDORAS = 79;
    public static final int ENTIDADES_REPORTANTES = 80;
    public static final int FILTROS_DUROS_COMPARTAMOS = 81;
    public static final int CLOSED_PLATFORM_BRANDED = 84;
    public static final int CLIENT_TYPE = 86;
    public static final int INTERNAL_EXPOSITION = 87;
    public static final int DEBTMENT = 89;
    public static final int VEHICULO_NO_AUTORIZADO_PARA_GARANTIA = 98;
    public static final int DEUDA_EN_ENTIDAD_RCC = 101;
    public static final int CREDITO_EN_GARANTIA_VIGENTE = 102;
    public static final int EMPLOYER_ID = 103;
    public static final int CALIFICACION_EN_ULTIMOS_MESES = 105;
    public static final int LEAD_ACTIVITY_TYPE = 106;
    public static final int MESES_EN_RCC = 107;
    public static final int HASTA_X_CALIF_PORCENTAJE = 108;
    public static final int MIN_X_DAYS_FOR_NEW_CREDIT = 113;
    public static final int MIN_PERCENTAGE_SIMILARITY_FACES = 129;
    public static final int REQUIRED_TEXT_IN_IMAGE = 130;
    public static final int ACCEPTED_DOCUMENT_TYPES = 131;
    public static final int MAX_ENTIDADES_SINTETIZADO = 137;
    public static final int SITUACION_5 = 54001;
    public static final int SITUACION_4 = 54002;
    public static final int SITUACION_3 = 54003;
    public static final int EXP_ARGENTINA = 54004;
    public static final int HASTA_X_ENTIDADES_FINANCIADORES = 54005;
    public static final int DESEMPLEADO = 54006;
    public static final int SITUACION_2 = 54007;
    public static final int SITUACION_BCRA = 54023;
    public static final int SITUACION_6 = 54024;
    public static final int TIPO_PERSONA_CUIT = 54025;
    public static final int SIN_ASIGNACION_UNIVERSAL_POR_HIJO = 54028;
    public static final int SIN_CHEQUS_RECHAZADOS_NO_LEVANTADOS = 54029;
    public static final int BASE_NEGATIVA_BDS = 54030;
    public static final int NORMAL_DESDE_DICIEMBRE_3_ANOS = 115;
    public static final int CANTIDAD_REPORTES = 116;
    public static final int MESES_CON_MIN_DEUDA = 132;
    public static final int MIN_LINEA_CREDITO_RCC = 133;
    public static final int MIN_DEUDA_DIRECTA_ULT_X_MESES = 134;
    public static final int MAX_SALDO_DEUDA_RCC = 135;
    public static final int BASE_FINANSOL = 136;
    public static final int DEVICE_DUPE_IOVATION = 57001;
    public static final int NO_ENTITY_CREDITS = 138;
    public static final int BCRA_BDS = 54031;
    public static final int CALIFICACION_RCC_CONYUGUE = 139;
    public static final int TIPO_DOCUMENTO_ACEPTABLES = 140;
    public static final int BDS_CAMPAIGN = 54033;
    public static final int MAX_SITUACION_ULT_X_MESES_BCRA = 54032;
    public static final int BASE_BANBIF = 141;
    public static final int BASE_PRISMA = 142;
    public static final int MAX_CALIF_EN_X_MESES_X_REPETICIONES = 143;
    public static final int ESSALUD_ACTIVE = 144;
    public static final int BASE_AZTECA = 145;
    public static final int BDS_EXISTS_IN_CNE = 54034;
    public static final int BASE_COBRANZA_AZTECA = 146;
    public static final int BAN_TOTAL_LISTA_NEGRA = 147;
    public static final int BAN_TOTAL_CLIENT = 148;
    public static final int DIGITAL_KEY_SOLVEN = 149;
    public static final int VALIDATION_PROCESS_APPROVED = 150;
    public static final int BANTOTAL_VALIDACION_EMAIL_CELULAR = 151;
    public static final int ROL_CONSEJERO_BAZ = 152;
    public static final int AZTECA_CAMPANIA_PRODUCT = 153;
    public static final int BANTOTAL_VALIDACION_PRESTAMOS = 154;
    public static final int BANTOTAL_DEUDA_VIGENTE = 155;
    public static final int ACCESO_EVALUACION_GENERICA = 156;
    public static final int MOTIVO_DEL_PRESTAMO = 54035;
    public static final int BDS_CANAL_DE_ADQUISICION = 54036;
    public static final int ALFIN_CLIENTE_PRODUCTO_ACTIVO = 157;

    private Integer id;
    private String hardFilterMessage;
    private String message;
    private Integer helpMessage;
    private String defaultParameter1;
    private String defaultParameter2;
    private String defaultParameter3;
    private Integer defaultOrder;
    private Integer countryId;

    public void fillFromDb(JSONObject json) throws Exception {
        if(JsonUtil.getIntFromJson(json, "hard_filter_id", null) != null)
            setId(JsonUtil.getIntFromJson(json, "hard_filter_id", null));
        if(JsonUtil.getStringFromJson(json, "message", null) != null)
            setMessage(JsonUtil.getStringFromJson(json, "message", null));
        if(JsonUtil.getIntFromJson(json, "help_message", null) != null)
            setHelpMessage(JsonUtil.getIntFromJson(json, "help_message", null));
        setHardFilterMessage(JsonUtil.getStringFromJson(json, "hard_filter", null));
        setDefaultParameter1(JsonUtil.getStringFromJson(json, "default_parameter_1", null));
        setDefaultParameter2(JsonUtil.getStringFromJson(json, "default_parameter_2", null));
        setDefaultParameter3(JsonUtil.getStringFromJson(json, "default_parameter_3", null));
        setDefaultOrder(JsonUtil.getIntFromJson(json, "default_order", null));
        setCountryId(JsonUtil.getIntFromJson(json, "country_id", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setHelpMessage(Integer helpMessage) {
        this.helpMessage = helpMessage;
    }

    public String getDefaultParameter1() {
        return defaultParameter1;
    }

    public void setDefaultParameter1(String defaultParameter1) {
        this.defaultParameter1 = defaultParameter1;
    }

    public String getDefaultParameter2() {
        return defaultParameter2;
    }

    public void setDefaultParameter2(String defaultParameter2) {
        this.defaultParameter2 = defaultParameter2;
    }

    public String getDefaultParameter3() {
        return defaultParameter3;
    }

    public void setDefaultParameter3(String defaultParameter3) {
        this.defaultParameter3 = defaultParameter3;
    }

    public Integer getDefaultOrder() {
        return defaultOrder;
    }

    public void setDefaultOrder(Integer defaultOrder) {
        this.defaultOrder = defaultOrder;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHardFilterMessage() {
        return hardFilterMessage;
    }

    public void setHardFilterMessage(String hardFilterMessage) {
        this.hardFilterMessage = hardFilterMessage;
    }

    public Integer getHelpMessage() {
        return helpMessage;
    }
}
