/**
 *
 */
package com.affirm.common.model.catalog;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class EntityWebService implements Serializable {

    public static final int COMPARTAMOS_TRAER_VARIABLES_PREEVALUACION = 1;
    public static final int COMPARTAMOS_CONSULTAR_VARIABLES_EVALUACION = 2;
    public static final int COMPARTAMOS_TRAER_VARIABLES_EVALUACION = 3;
    public static final int COMPARTAMOS_GENERAR_CREDITO = 4;
    public static final int COMPARTAMOS_CONFIRMAR_OPERACION = 5;
    public static final int ACCESO_LOGIN = 6;
    public static final int ACCESO_CONSULTAR_EXPEDIENTE = 7;
    public static final int ACCESO_CREAR_EXPEDIENTE = 8;
    public static final int ACCESO_CREAR_DIRECCION = 9;
    public static final int ACCESO_COTIZADOR = 10;
    public static final int ACCESO_CRONOGRAMA = 11;
    public static final int ACCESO_INFORMACION_ADICIONAL = 12;
    public static final int ACCESO_PENDIENTE_FIRMA = 13;
    public static final int ACCESO_AGENDAR_FIRMA = 14;
    public static final int ACCESO_ESTADO_FIRMA = 15;
    public static final int ACCESO_EXPEDIENTES_DESPACHADOS = 16;
    public static final int ACCESO_CATALOGO_VEHICULAR = 17;
    public static final int ACCESO_REGISTRAR_DOCUMENTOS = 18;
    public static final int ACCESO_SCORE_LD_CONSUMO = 26;
    public static final int EFL_START_PROCESS = 19;
    public static final int EFL_SCORE = 20;
    public static final int ABACO_CREAR_CREDITO = 21;
    public static final int ABACO_CREAR_SOCIO = 22;
    public static final int ABACO_ES_SOCIO = 23;
    public static final int ABACO_ACTUALIZAR_SOCIO = 24;
    public static final int EFECTIVA_CONSULTAR_BASE = 25;
    public static final int CAJASULLANA_ADMISIBILIDAD = 30;
    public static final int CAJASULLANA_EXPERIAN = 31;
    public static final int CAJASULLANA_CANCELAR_CREDITO = 32;
    public static final int CAJASULLANA_GENERAR_SOLICITUD = 33;
    public static final int QAPAQ_CAPTURAR_VALOR = 34;
    public static final int QAPAQ_GENERADOR_TOKEN = 35;
    public static final int QAPAQ_CONSULTA_DE_OFERTAS = 36;
    public static final int WENANCE_SCORE = 37;
    public static final int WENANCE_CREAR_LEAD = 38;
    public static final int ACCESO_GENERAR_EXPEDIENTE_LD = 39;
    public static final int ACCESO_GENERAR_DOCUMENTACION_LD = 40;
    public static final int FDLM_DATACREDITO_DATOS_CLIENTE = 48;
    public static final int FDLM_DATACREDITO_EXISTE_INFORMACION_LOCAL = 49;
    public static final int FDLM_DATACREDITO_PROXIMA_CONSULTA = 50;
    public static final int FDLM_DATACREDITO_REPORTE_CLIENTE_TPZ = 51;
    public static final int FDLM_DATACREDITO_ULTIMA_CONSULTA = 52;
    public static final int FDLM_LISTAS_CONTROL_CONSULTAR_PERSONA = 53;
    public static final int FDLM_LISTAS_CONTROL_CONSULTAR_SOLICITUD = 54;
    public static final int FDLM_TOPAZ_EXECUTE = 55;
    public static final int FDLM_CREDITO_CONSUMO_CONSULTAR_CREDITO = 56;
    public static final int FDLM_TOPAZ_OBTENER_SUCURSAL = 57;
    public static final int FDLM_TOPAZ_CREAR_CREDITO = 58;
    public static final int BANBIF_CREAR_CUESTIONARIO = 60;
    public static final int BANBIF_RESULTADO_CUESTIONARIO = 61;
    //BANTOTAL
    public static final int BANTOTAL_APIREST_AUTHENTICATION = 62;
    public static final int BANTOTAL_APIREST_OBTENER_IDENTIFICADOR_UNICO = 63;
    public static final int BANTOTAL_APIREST_OBTENER_PERSONA = 64;
    public static final int BANTOTAL_APIREST_OBTENER_CUENTAS_CLIENTE = 65;
    public static final int BANTOTAL_APIREST_OBTENER_CUENTAS_AHORRO = 66;
    public static final int BANTOTAL_APIREST_OBTENER_PRODUCTOS_CUENTAS_AHORRO = 67;
    public static final int BANTOTAL_APIREST_CONTRATAR_PRODUCTO_CUENTAS_AHORRO = 68;
    public static final int BANTOTAL_APIREST_PRESTAMO_SIMULAR_CON_CLIENTE = 69;
    public static final int BANTOTAL_APIREST_PRESTAMO_SIMULAR_SIN_CLIENTE = 70;
    public static final int BANTOTAL_APIREST_CREAR_CLIENTE_CON_PERSONA = 71;
    public static final int BANTOTAL_APIREST_CREAR_CLIENTE_Y_PERSONA = 72;
    public static final int BANTOTAL_APIREST_CONTRATAR_PRESTAMO = 73;
    public static final int BANTOTAL_APIREST_OBTENER_PIZARRAS= 74;
    public static final int BANTOTAL_APIREST_OBTENER_ACTIVIDADES = 75;
    public static final int BANTOTAL_APIREST_VALIDAR_EN_LISTAS_NEGRAS = 76;
    public static final int BANTOTAL_APIREST_AGREGAR_FATCA = 77;
    public static final int BANTOTAL_APIREST_AGREGAR_PEP = 78;
    public static final int BANTOTAL_APIREST_OBTENER_FATCA = 79;
    public static final int BANTOTAL_APIREST_OBTENER_PEP = 80;
    public static final int BPEOPLE_CREACION_USUARIO = 81;
    public static final int BANCO_AZTECA_CAMPAIGN_LOGIN = 82;
    public static final int BANCO_AZTECA_OBTAIN_PERSON_CAMPAIGNS = 83;
    public static final int BANCO_AZTECA_OBTAIN_ADVISER_ROLE = 84;
    public static final int BANTOTAL_APIREST_OBTENER_TARJETAS_DE_DEBITO = 85;
    public static final int BANTOTAL_APIREST_CREAR_TARJETA_DE_DEBITO = 86;
    public static final int BANTOTAL_APIREST_CONTRATAR_PRODUCTO_CUENTAS_AHORRO_CON_FACULTADES = 87;
    public static final int BANTOTAL_APIREST_OBTENER_DETALLE_PRESTAMO = 88;
    public static final int BANTOTAL_APIREST_OBTENER_DETALLE_CUENTA = 89;
    public static final int BANTOTAL_APIREST_OBTENER_PRESTAMOS_CLIENTE = 90;
    public static final int BANTOTAL_APIREST_OBTENER_CRONOGRAMA = 91;
    public static final int BANTOTAL_APIREST_OBTENER_PROFESIONES = 92;
    public static final int BANTOTAL_APIREST_ACTUALIZAR_PROFESION = 93;
    public static final int BANTOTAL_APIREST_OBTENER_CCI = 94;
    public static final int BANTOTAL_APIREST_AGREGAR_DOMICILIO = 95;
    public static final int BANTOTAL_APIREST_OBTENER_CUENTA_CLIENTE= 96;
    public static final int BANTOTAL_APIREST_PAGAR_CUOTA = 97;
    public static final int BANTOTAL_APIREST_OBTENER_ENVIO_ESTADO_DE_CUENTA = 98;
    public static final int BANTOTAL_APIREST_ACTUALIZAR_ENVIO_ESTADO_DE_CUENTA = 99;
    public static final int BANTOTAL_APIREST_AGREGAR_ENVIO_ESTADO_DE_CUENTA = 100;
    public static final int BANTOTAL_APIREST_OBTENER_DIRECCION_PERSONA = 101;
    public static final int BANTOTAL_APIREST_OBTENER_DIRECCION_CUENTA = 102;
    public static final int BANTOTAL_APIREST_ACTUALIZAR_DIRECCION_PERSONA = 103;
    public static final int BANTOTAL_APIREST_ACTUALIZAR_DIRECCION_CUENTA = 104;
    public static final int BANTOTAL_APIREST_CONSULTA_CREDITOS_CUOTAS_POR_DNI = 105;
    public static final int BANTOTAL_APIREST_GENERAR_Y_OBTENER_CCI = 106;
    public static final int BANTOTAL_APIREST_INHABILITAR_DIRECCION_PERSONA = 107;
    public static final int BANTOTAL_APIREST_INHABILITAR_DIRECCION_CUENTA = 108;
    public static final int ACCESO_EVALUACION_GENERICA = 109;
    public static final int BANTOTAL_APIREST_OBTENER_DETALLE_PAGO_CUOTA = 110;
    public static final int BANTOTAL_APIREST_OBTENER_PLAZOS_FIJOS = 111;
    public static final int BANCO_AZTECA_RENIEC_SERVICE_LOGIN = 116;
    public static final int BANCO_AZTECA_RENIEC_SERVICE_OBTENER_DATA = 117;
    public static final int LIVENESS_API = 118;
    public static final int BANTOTAL_APIREST_OBTENER_TIPO_MOVIMIENTO = 119;
    public static final int BANCO_AZTECA_CAMPANIA_COBRANZA = 121;
    public static final int BANBIF_KONECTA_LEAD = 125;


    private Integer id;
    private Integer entityId;
    private String wbeserviceName;
    private String productionUrl;
    private String sandboxUrl;
    private String productionSecurityKey;
    private String sandboxSecurityKey;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "entity_ws_id", null));
        setEntityId(JsonUtil.getIntFromJson(json, "entity_id", null));
        setWbeserviceName(JsonUtil.getStringFromJson(json, "entity_ws", null));
        setProductionUrl(JsonUtil.getStringFromJson(json, "production_url", null));
        setSandboxUrl(JsonUtil.getStringFromJson(json, "sandbox_url", null));
        setProductionSecurityKey(JsonUtil.getStringFromJson(json, "production_security_key", null));
        setSandboxSecurityKey(JsonUtil.getStringFromJson(json, "sandbox_security_key", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getWbeserviceName() {
        return wbeserviceName;
    }

    public void setWbeserviceName(String wbeserviceName) {
        this.wbeserviceName = wbeserviceName;
    }

    public String getProductionUrl() {
        return productionUrl;
    }

    public void setProductionUrl(String productionUrl) {
        this.productionUrl = productionUrl;
    }

    public String getSandboxUrl() {
        return sandboxUrl;
    }

    public void setSandboxUrl(String sandboxUrl) {
        this.sandboxUrl = sandboxUrl;
    }

    public String getProductionSecurityKey() {
        return productionSecurityKey;
    }

    public void setProductionSecurityKey(String productionSecurityKey) {
        this.productionSecurityKey = productionSecurityKey;
    }

    public String getSandboxSecurityKey() {
        return sandboxSecurityKey;
    }

    public void setSandboxSecurityKey(String sandboxSecurityKey) {
        this.sandboxSecurityKey = sandboxSecurityKey;
    }
}
