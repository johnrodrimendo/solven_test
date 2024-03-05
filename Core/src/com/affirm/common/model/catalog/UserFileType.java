/**
 *
 */
package com.affirm.common.model.catalog;

import java.io.Serializable;

/**
 * @author jrodriguez
 */
public class UserFileType implements Serializable {

    public static final int DNI_MERGED = 1;
    public static final int COMPROBANTE_DIRECCION = 2;
    public static final int BOLETA_PAGO = 3;
    public static final int RECIBO_HONORARIOS = 5;
    public static final int FACTURA_INTERES = 6;
    public static final int DECLARACION_ANUAL = 7;
    public static final int PDT = 8;
    public static final int RUS = 9;
    public static final int PAGO_IMPUESTOS = 10;
    public static final int TARJETA_PROPIEDAD = 11;
    public static final int CERTIFICADO_PATENTE = 12;
    public static final int CONTRATO = 13;
    public static final int BOLETA_PAGO_PENSION = 15;
    public static final int RESUMEN_CUENTA_AFP = 16;
    public static final int RESUMEN_CUENTA_RENTA_VITALICIA = 17;
    public static final int ELIMINADOS = 18;
    public static final int OTROS = 19;
    public static final int ICAR = 20;
    public static final int CONTRATO_FIRMA = 21;
    public static final int CONTRATO_FIRMA_ADELANTO_SUELDO = 22;
    public static final int SELFIE = 23;
    public static final int OCULTO = 24;
    public static final int DNI_FRONTAL = 25;
    public static final int DNI_ANVERSO = 26;
    public static final int WELCOME_CALL = 28;
    public static final int CONTRACT_CALL = 29;
    public static final int DOCUMENTO_DOMICILIO = 30;
    public static final int BOLETA_PAGO_2 = 32;
    public static final int BOLETA_PAGO_3 = 33;
    public static final int HOJA_RESUMEN = 36;
    public static final int BUREAU_RESULT = 37;
    public static final int CONTRATO_SOLICITUD = 38;
    public static final int RIPLEY_FINAL_SCHEDULE = 39;

    public static final int SOLICITUD_CREDITO_RIPLEY = 40;
    public static final int FINAL_DOCUMENTATION = 42;
    public static final int PRELIMINARY_DOCUMENTATION = 46;
    public static final int PERSON_DISQUALIFIER = 47;

    public static final int CEDULA_CIUDADANIA_FRONTAL = 56;
    public static final int CEDULA_CIUDADANIA_ANVERSO = 57;


    public static final int TRACKING_PHONE_CALL = 62;
    public static final int ACUERDO_EXTRAJUDICIAL = 63;
    public static final int CARTA_NO_ADEUDO = 64;
    public static final int BANTOTAL_TICKET = 65;

    public static final int SELFIE_RECORDING = 66;



    private Integer id;
    private String type;
    private Integer order;
    private String thumbnail;
    private String tooltip;

    public UserFileType(Integer id, String type, Integer order, String thumbnail, String tooltip) {
        this.id = id;
        this.type = type;
        this.order = order;
        this.thumbnail = thumbnail;
        this.tooltip = tooltip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }
}
