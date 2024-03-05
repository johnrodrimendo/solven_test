package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

import java.util.Date;

public class RccSynthesized {

    private Integer codMes;
    private Date fecRep;
    private String codEmp;
    private Double saldoCastigo;
    private Double saldoJudicial;
    private Double medianaArrendamiento;
    private Double medianaLeaseBack;
    private Double medianaLineaCredito;
    private Double medianaPrestamosNoRev;
    private Double medianaPrestamosRev;
    private Double medianaSaldoTc;
    private Double medianaSobregiros;
    private Double saldoVigente;
    private Integer calificacion;

    public void fillFromDb(JSONObject json) throws Exception {
        setCodMes(JsonUtil.getIntFromJson(json, "cod_mes", null));
        setFecRep(JsonUtil.getPostgresDateFromJson(json, "fec_rep", null));
        setCodEmp(JsonUtil.getStringFromJson(json, "cod_emp", null));
        setSaldoCastigo(JsonUtil.getDoubleFromJson(json, "saldo_castigo", null));
        setSaldoJudicial(JsonUtil.getDoubleFromJson(json, "saldo_judicial", null));
        setMedianaArrendamiento(JsonUtil.getDoubleFromJson(json, "mediana_arrendamiento", null));
        setMedianaLeaseBack(JsonUtil.getDoubleFromJson(json, "mediana_lease_back", null));
        setMedianaLineaCredito(JsonUtil.getDoubleFromJson(json, "mediana_linea_credito", null));
        setMedianaPrestamosNoRev(JsonUtil.getDoubleFromJson(json, "mediana_prestamos_no_rev", null));
        setMedianaPrestamosRev(JsonUtil.getDoubleFromJson(json, "mediana_prestamos_rev", null));
        setMedianaSaldoTc(JsonUtil.getDoubleFromJson(json, "mediana_saldo_tc", null));
        setMedianaSobregiros(JsonUtil.getDoubleFromJson(json, "mediana_sobregiros", null));
        setSaldoVigente(JsonUtil.getDoubleFromJson(json, "saldo_vigente", null));
        setCalificacion(JsonUtil.getIntFromJson(json, "calificacion", null));
    }

    public Integer getCodMes() {
        return codMes;
    }

    public void setCodMes(Integer codMes) {
        this.codMes = codMes;
    }

    public Date getFecRep() {
        return fecRep;
    }

    public void setFecRep(Date fecRep) {
        this.fecRep = fecRep;
    }

    public String getCodEmp() {
        return codEmp;
    }

    public void setCodEmp(String codEmp) {
        this.codEmp = codEmp;
    }

    public Double getSaldoCastigo() {
        return saldoCastigo;
    }

    public void setSaldoCastigo(Double saldoCastigo) {
        this.saldoCastigo = saldoCastigo;
    }

    public Double getSaldoJudicial() {
        return saldoJudicial;
    }

    public void setSaldoJudicial(Double saldoJudicial) {
        this.saldoJudicial = saldoJudicial;
    }

    public Double getMedianaArrendamiento() {
        return medianaArrendamiento;
    }

    public void setMedianaArrendamiento(Double medianaArrendamiento) {
        this.medianaArrendamiento = medianaArrendamiento;
    }

    public Double getMedianaLeaseBack() {
        return medianaLeaseBack;
    }

    public void setMedianaLeaseBack(Double medianaLeaseBack) {
        this.medianaLeaseBack = medianaLeaseBack;
    }

    public Double getMedianaLineaCredito() {
        return medianaLineaCredito;
    }

    public void setMedianaLineaCredito(Double medianaLineaCredito) {
        this.medianaLineaCredito = medianaLineaCredito;
    }

    public Double getMedianaPrestamosNoRev() {
        return medianaPrestamosNoRev;
    }

    public void setMedianaPrestamosNoRev(Double medianaPrestamosNoRev) {
        this.medianaPrestamosNoRev = medianaPrestamosNoRev;
    }

    public Double getMedianaPrestamosRev() {
        return medianaPrestamosRev;
    }

    public void setMedianaPrestamosRev(Double medianaPrestamosRev) {
        this.medianaPrestamosRev = medianaPrestamosRev;
    }

    public Double getMedianaSaldoTc() {
        return medianaSaldoTc;
    }

    public void setMedianaSaldoTc(Double medianaSaldoTc) {
        this.medianaSaldoTc = medianaSaldoTc;
    }

    public Double getMedianaSobregiros() {
        return medianaSobregiros;
    }

    public void setMedianaSobregiros(Double medianaSobregiros) {
        this.medianaSobregiros = medianaSobregiros;
    }

    public Double getSaldoVigente() {
        return saldoVigente;
    }

    public void setSaldoVigente(Double saldoVigente) {
        this.saldoVigente = saldoVigente;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }
}
