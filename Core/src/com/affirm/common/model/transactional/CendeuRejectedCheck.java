package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class CendeuRejectedCheck implements Serializable {

    private Integer id;
    private String cuit;
    private String numero;
    private Date fechaRechazo;
    private Double monto;
    private Character causal;
    private Date fechaLevantamiento;
    private Character ley25_326Art16Inc6;
    private Character ley25_326Art38Inc3;
    private String cuitRelacionado;
    private String pagoMulta;

    public void fillFromDb(JSONObject json) {
        setId(JsonUtil.getIntFromJson(json, "id_cheque", null));
        setCuit(JsonUtil.getStringFromJson(json, "cuit", null));
        setNumero(JsonUtil.getStringFromJson(json, "numero", null));
        setFechaRechazo(JsonUtil.getPostgresDateFromJson(json, "fecha_rechazo", null));
        setMonto(JsonUtil.getDoubleFromJson(json, "monto", null));
        setCausal(JsonUtil.getCharacterFromJson(json, "causal", null));
        setFechaLevantamiento(JsonUtil.getPostgresDateFromJson(json, "fecha_levantamiento", null));
        setLey25_326Art16Inc6(JsonUtil.getCharacterFromJson(json, "ley_25_326_art_16_inc_6", null));
        setLey25_326Art38Inc3(JsonUtil.getCharacterFromJson(json, "ley_25_326_art_38_inc_3", null));
        setCuitRelacionado(JsonUtil.getStringFromJson(json, "cuit_relacionado", null));
        setPagoMulta(JsonUtil.getStringFromJson(json, "pago_multa", null));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getFechaRechazo() {
        return fechaRechazo;
    }

    public void setFechaRechazo(Date fechaRechazo) {
        this.fechaRechazo = fechaRechazo;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public Character getCausal() {
        return causal;
    }

    public void setCausal(Character causal) {
        this.causal = causal;
    }

    public Date getFechaLevantamiento() {
        return fechaLevantamiento;
    }

    public void setFechaLevantamiento(Date fechaLevantamiento) {
        this.fechaLevantamiento = fechaLevantamiento;
    }

    public Character getLey25_326Art16Inc6() {
        return ley25_326Art16Inc6;
    }

    public void setLey25_326Art16Inc6(Character ley25_326Art16Inc6) {
        this.ley25_326Art16Inc6 = ley25_326Art16Inc6;
    }

    public Character getLey25_326Art38Inc3() {
        return ley25_326Art38Inc3;
    }

    public void setLey25_326Art38Inc3(Character ley25_326Art38Inc3) {
        this.ley25_326Art38Inc3 = ley25_326Art38Inc3;
    }

    public String getCuitRelacionado() {
        return cuitRelacionado;
    }

    public void setCuitRelacionado(String cuitRelacionado) {
        this.cuitRelacionado = cuitRelacionado;
    }

    public String getPagoMulta() {
        return pagoMulta;
    }

    public void setPagoMulta(String pagoMulta) {
        this.pagoMulta = pagoMulta;
    }
}
