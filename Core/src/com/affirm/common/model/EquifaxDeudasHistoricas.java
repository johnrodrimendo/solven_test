package com.affirm.common.model;

import com.affirm.common.util.JsonUtil;
import org.json.JSONObject;

// TODO MOVE TO FOLDER EQUIFAX/MODEL ???
public class EquifaxDeudasHistoricas {

    private Integer loanApplicationId;
    private String periodo;
    private String calificacion;
    private Integer porcentaje;
    private Integer nroEntidades;
    private Integer deudaVigente;
    private Integer deudaAtrasada;
    private Integer deudaJudicial;
    private Integer deudaCastigada;
    private Integer deudaTotal;
    private Integer diasAtraso;

    public void fillFromDb(JSONObject json) {
        setLoanApplicationId(JsonUtil.getIntFromJson(json, "loan_application_id", null));
        setPeriodo(JsonUtil.getStringFromJson(json, "periodo", null));
        setCalificacion(JsonUtil.getStringFromJson(json, "calificacion", null));
        setPorcentaje(JsonUtil.getIntFromJson(json, "porcentaje", null));
        setNroEntidades(JsonUtil.getIntFromJson(json, "nroentidades", null));
        setDeudaVigente(JsonUtil.getIntFromJson(json, "deudavigente", null));
        setDeudaAtrasada(JsonUtil.getIntFromJson(json, "deudaatrasada", null));
        setDeudaJudicial(JsonUtil.getIntFromJson(json, "deudajudicial", null));
        setDeudaCastigada(JsonUtil.getIntFromJson(json, "deudacastigada", null));
        setDeudaTotal(JsonUtil.getIntFromJson(json, "deudatotal", null));
        setDiasAtraso(JsonUtil.getIntFromJson(json, "diasatrasado", null));
    }

    public Integer getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Integer loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }

    public Integer getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(Integer porcentaje) {
        this.porcentaje = porcentaje;
    }

    public Integer getNroEntidades() {
        return nroEntidades;
    }

    public void setNroEntidades(Integer nroEntidades) {
        this.nroEntidades = nroEntidades;
    }

    public Integer getDeudaVigente() {
        return deudaVigente;
    }

    public void setDeudaVigente(Integer deudaVigente) {
        this.deudaVigente = deudaVigente;
    }

    public Integer getDeudaAtrasada() {
        return deudaAtrasada;
    }

    public void setDeudaAtrasada(Integer deudaAtrasada) {
        this.deudaAtrasada = deudaAtrasada;
    }

    public Integer getDeudaJudicial() {
        return deudaJudicial;
    }

    public void setDeudaJudicial(Integer deudaJudicial) {
        this.deudaJudicial = deudaJudicial;
    }

    public Integer getDeudaCastigada() {
        return deudaCastigada;
    }

    public void setDeudaCastigada(Integer deudaCastigada) {
        this.deudaCastigada = deudaCastigada;
    }

    public Integer getDeudaTotal() {
        return deudaTotal;
    }

    public void setDeudaTotal(Integer deudaTotal) {
        this.deudaTotal = deudaTotal;
    }

    public Integer getDiasAtraso() {
        return diasAtraso;
    }

    public void setDiasAtraso(Integer diasAtraso) {
        this.diasAtraso = diasAtraso;
    }
}
