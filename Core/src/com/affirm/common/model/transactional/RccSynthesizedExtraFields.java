package com.affirm.common.model.transactional;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

public class RccSynthesizedExtraFields extends RccSynthesized {

    private Double lineaTc;
    private Double saldoReestructurado;
    private Double saldoRefinanciado;
    private Double saldoVencido;
    private Double granSaldoTc;
    private Double pequenaSaldoTc;

    public void fillFromDb(JSONObject json) throws Exception {
        super.fillFromDb(json);

        setSaldoReestructurado(JsonUtil.getDoubleFromJson(json, "saldo_reestructurado", null));
        setSaldoRefinanciado(JsonUtil.getDoubleFromJson(json, "saldo_refinanciado", null));
        setSaldoVencido(JsonUtil.getDoubleFromJson(json, "saldo_vencido", null));
        setLineaTc(JsonUtil.getDoubleFromJson(json, "linea_tc", null));
        setGranSaldoTc(JsonUtil.getDoubleFromJson(json, "gran_saldo_tc", null));
        setPequenaSaldoTc(JsonUtil.getDoubleFromJson(json, "pequena_saldo_tc", null));
    }

    public Double getSaldoReestructurado() {
        return saldoReestructurado;
    }

    public void setSaldoReestructurado(Double saldoReestructurado) {
        this.saldoReestructurado = saldoReestructurado;
    }

    public Double getSaldoRefinanciado() {
        return saldoRefinanciado;
    }

    public void setSaldoRefinanciado(Double saldoRefinanciado) {
        this.saldoRefinanciado = saldoRefinanciado;
    }

    public Double getSaldoVencido() {
        return saldoVencido;
    }

    public void setSaldoVencido(Double saldoVencido) {
        this.saldoVencido = saldoVencido;
    }

    public Double getLineaTc() {
        return lineaTc;
    }

    public void setLineaTc(Double lineaTc) {
        this.lineaTc = lineaTc;
    }

    public Double getGranSaldoTc() {
        return granSaldoTc;
    }

    public void setGranSaldoTc(Double granSaldoTc) {
        this.granSaldoTc = granSaldoTc;
    }

    public Double getPequenaSaldoTc() {
        return pequenaSaldoTc;
    }

    public void setPequenaSaldoTc(Double pequenaSaldoTc) {
        this.pequenaSaldoTc = pequenaSaldoTc;
    }
}
