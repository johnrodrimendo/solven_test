package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.apache.commons.lang.time.DateUtils;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class BanbifPreApprovedBase implements Serializable {

    public final static String BANBIF_CLASSIC_CARD = "Visa Clasica";
    public final static String BANBIF_GOLD_CARD = "Visa Oro";
    public final static String BANBIF_PLATINUM_CARD = "Visa Platinum";
    public final static String BANBIF_INFINITE_CARD = "Visa Infinite";
    public final static String BANBIF_MASTERCARD_CARD = "MasterCard Platinum";
    public final static String BANBIF_SIGNATURE_CARD = "Visa Signature";
    public final static String BANBIF_ZERO_MEMBERSHIP_CARD = "Cero Membresia";
    public final static String BANBIF_MAS_EFECTIVO_CARD = "TC MAS EFECTIVO";

    public final static String BANBIF_CLASSIC_CARD_TCEA = "85.56%";
    public final static String BANBIF_GOLD_CARD_TCEA = "100.72%";
    public final static String BANBIF_MASTERCARD_CARD_TCEA = "120.65%";
    public final static String BANBIF_PLATINUM_CARD_TCEA = "120.65%";
    public final static String BANBIF_SIGNATURE_CARD_TCEA = "130.52%";
    public final static String BANBIF_INFINITE_CARD_TCEA = "145.21%";
    public final static String BANBIF_ZERO_MEMBERSHIP_CARD_TCEA = "70.20%";

    public final static String BANBIF_MAS_EFECTIVO_TIPO_BASE = "BD Aprobada Mas Efectivo";

    public final static Integer HAS_OFFER = 1;
    public final static Integer HAS_NO_OFFER = 0;

    public final static String BANBIF_MAS_EFECTIVO_IMG_URL = "https://solven-public.s3.amazonaws.com/img/banbif/landing_18/Em_TC+%2B+efectivo_recorte.png";

    private String tipoDoc;
    private String numeroDoc;
    private String plastico;
    private Double linea;
    private String tipoBase;
    private String nombres;
    private String apellidos;
    private String canal;
    private Integer promocionUso;
    private Integer promocionAceptacion;
    private Integer pld;
    private String compraDeDeuda;
    private Integer plaza1;
    private Double monto1;
    private Double tcea1;
    private Double cuota1;
    private Integer plaza2;
    private Double monto2;
    private Double tcea2;
    private Double cuota2;
    private Integer plaza3;
    private Double monto3;
    private Double tcea3;
    private Double cuota3;
    private Date validUntil;

    public void fillFromDb(JSONObject json) {
        setTipoDoc(JsonUtil.getStringFromJson(json, "tipo_doc", null));
        setNumeroDoc(JsonUtil.getStringFromJson(json, "numero_doc", null));
        setPlastico(JsonUtil.getStringFromJson(json, "plastico", null));
        setLinea(JsonUtil.getDoubleFromJson(json, "linea", null));
        setTipoBase(JsonUtil.getStringFromJson(json, "tipo_base", null));
        setNombres(JsonUtil.getStringFromJson(json, "nombres", null));
        setApellidos(JsonUtil.getStringFromJson(json, "apellidos", null));
        setCanal(JsonUtil.getStringFromJson(json, "canal", null));
        setPromocionUso(JsonUtil.getIntFromJson(json, "promocion_uso", null));
        setPromocionAceptacion(JsonUtil.getIntFromJson(json, "promocion_aceptacion", null));
        setPld(JsonUtil.getIntFromJson(json, "flag_pld", null));
        setCompraDeDeuda(JsonUtil.getStringFromJson(json, "flag_cd", null));
        setPlaza1(JsonUtil.getIntFromJson(json, "plaza_1", null));
        setMonto1(JsonUtil.getDoubleFromJson(json, "monto_1", null));
        setTcea1(JsonUtil.getDoubleFromJson(json, "tcea_1", null));
        setCuota1(JsonUtil.getDoubleFromJson(json, "cuota_1", null));
        setPlaza2(JsonUtil.getIntFromJson(json, "plaza_2", null));
        setMonto2(JsonUtil.getDoubleFromJson(json, "monto_2", null));
        setTcea2(JsonUtil.getDoubleFromJson(json, "tcea_2", null));
        setCuota2(JsonUtil.getDoubleFromJson(json, "cuota_2", null));
        setPlaza3(JsonUtil.getIntFromJson(json, "plaza_3", null));
        setMonto3(JsonUtil.getDoubleFromJson(json, "monto_3", null));
        setTcea3(JsonUtil.getDoubleFromJson(json, "tcea_3", null));
        setCuota3(JsonUtil.getDoubleFromJson(json, "cuota_3", null));
        setValidUntil(JsonUtil.getPostgresDateFromJson(json, "valid_until", null));
    }

    public boolean isValid() {
        if (validUntil == null) return true;
        return !DateUtils.truncate(new Date(), java.util.Calendar.DAY_OF_MONTH).after(validUntil);
    }

    public String getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public String getNumeroDoc() {
        return numeroDoc;
    }

    public void setNumeroDoc(String numeroDoc) {
        this.numeroDoc = numeroDoc;
    }

    public String getPlastico() {
        return plastico;
    }

    public void setPlastico(String plastico) {
        this.plastico = plastico;
    }

    public Double getLinea() {
        return linea;
    }

    public void setLinea(Double linea) {
        this.linea = linea;
    }

    public String getTipoBase() {
        return tipoBase;
    }

    public void setTipoBase(String tipoBase) {
        this.tipoBase = tipoBase;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public Integer getPromocionUso() {
        return promocionUso;
    }

    public void setPromocionUso(Integer promocionUso) {
        this.promocionUso = promocionUso;
    }

    public Integer getPromocionAceptacion() {
        return promocionAceptacion;
    }

    public void setPromocionAceptacion(Integer promocionAceptacion) {
        this.promocionAceptacion = promocionAceptacion;
    }

    public Integer getPld() {
        return pld;
    }

    public void setPld(Integer pld) {
        this.pld = pld;
    }

    public String getCompraDeDeuda() {
        return compraDeDeuda;
    }

    public void setCompraDeDeuda(String compraDeDeuda) {
        this.compraDeDeuda = compraDeDeuda;
    }

    public Integer getBonoBienvenida() {
        Integer total = 0;
        if (this.getPromocionAceptacion() != null) total += this.getPromocionAceptacion();
        if (this.getPromocionUso() != null) total += this.getPromocionUso();
        return total;
    }

    public Integer getPlaza1() {
        return plaza1;
    }

    public void setPlaza1(Integer plaza1) {
        this.plaza1 = plaza1;
    }

    public Double getMonto1() {
        return monto1;
    }

    public void setMonto1(Double monto1) {
        this.monto1 = monto1;
    }

    public Double getTcea1() {
        return tcea1;
    }

    public void setTcea1(Double tcea1) {
        this.tcea1 = tcea1;
    }

    public Double getCuota1() {
        return cuota1;
    }

    public void setCuota1(Double cuota1) {
        this.cuota1 = cuota1;
    }

    public Integer getPlaza2() {
        return plaza2;
    }

    public void setPlaza2(Integer plaza2) {
        this.plaza2 = plaza2;
    }

    public Double getMonto2() {
        return monto2;
    }

    public void setMonto2(Double monto2) {
        this.monto2 = monto2;
    }

    public Double getTcea2() {
        return tcea2;
    }

    public void setTcea2(Double tcea2) {
        this.tcea2 = tcea2;
    }

    public Double getCuota2() {
        return cuota2;
    }

    public void setCuota2(Double cuota2) {
        this.cuota2 = cuota2;
    }

    public Integer getPlaza3() {
        return plaza3;
    }

    public void setPlaza3(Integer plaza3) {
        this.plaza3 = plaza3;
    }

    public Double getMonto3() {
        return monto3;
    }

    public void setMonto3(Double monto3) {
        this.monto3 = monto3;
    }

    public Double getTcea3() {
        return tcea3;
    }

    public void setTcea3(Double tcea3) {
        this.tcea3 = tcea3;
    }

    public Double getCuota3() {
        return cuota3;
    }

    public void setCuota3(Double cuota3) {
        this.cuota3 = cuota3;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
    }
}
