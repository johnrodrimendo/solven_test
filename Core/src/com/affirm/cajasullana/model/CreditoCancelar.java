package com.affirm.cajasullana.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dev5 on 22/02/18.
 */
public class CreditoCancelar {

    @SerializedName("SALDO_CAPITAL")
    private String capital;
    @SerializedName("NOMBRE")
    private String nombre;
    @SerializedName("NUMERO_PRESTAMO")
    private String entityCode;
    @SerializedName("PORCENTAJE_AMORTIZACION")
    private String porcentajeAmortizacion;
    @SerializedName("MONTO_INICIAL")
    private String montoInicial;
    @SerializedName("DEUDA_TOTAL")
    private String deudaTotal;

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(String entityCode) {
        this.entityCode = entityCode;
    }

    public String getPorcentajeAmortizacion() {
        return porcentajeAmortizacion;
    }

    public void setPorcentajeAmortizacion(String porcentajeAmortizacion) {
        this.porcentajeAmortizacion = porcentajeAmortizacion;
    }

    public String getMontoInicial() {
        return montoInicial;
    }

    public void setMontoInicial(String montoInicial) {
        this.montoInicial = montoInicial;
    }

    public String getDeudaTotal() {
        return deudaTotal;
    }

    public void setDeudaTotal(String deudaTotal) {
        this.deudaTotal = deudaTotal;
    }
}
