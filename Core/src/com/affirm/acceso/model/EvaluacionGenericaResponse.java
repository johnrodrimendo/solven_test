package com.affirm.acceso.model;

public class EvaluacionGenericaResponse {
    private String codRes;
    private String estado;
    private Integer codigoEvaluacion;

    public String getCodRes() {
        return codRes;
    }

    public void setCodRes(String codRes) {
        this.codRes = codRes;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getCodigoEvaluacion() {
        return codigoEvaluacion;
    }

    public void setCodigoEvaluacion(Integer codigoEvaluacion) {
        this.codigoEvaluacion = codigoEvaluacion;
    }
}
