package com.affirm.fdlm.creditoconsumo.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PreCancelar {

    @JsonProperty("CUENTA")
    private Long cuenta;

    @JsonProperty("SALDO_CAPITAL")
    private Double saldoCapital;

    @JsonProperty("INTERESES")
    private Double intereses;

    @JsonProperty("SEGURO")
    private Double seguro;

    @JsonProperty("TOTAL")
    private Double total;

    public Long getCuenta() {
        return cuenta;
    }

    public void setCuenta(Long cuenta) {
        this.cuenta = cuenta;
    }

    public Double getSaldoCapital() {
        return saldoCapital;
    }

    public void setSaldoCapital(Double saldoCapital) {
        this.saldoCapital = saldoCapital;
    }

    public Double getIntereses() {
        return intereses;
    }

    public void setIntereses(Double intereses) {
        this.intereses = intereses;
    }

    public Double getSeguro() {
        return seguro;
    }

    public void setSeguro(Double seguro) {
        this.seguro = seguro;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
