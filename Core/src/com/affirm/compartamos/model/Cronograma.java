package com.affirm.compartamos.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dev5 on 14/12/17.
 */
public class Cronograma {

    /*GeneracionCreditoSolven*/
    /********RESPONSE*********/
    @SerializedName("pdFecVen")
    private String fechaPago;
    @SerializedName("pcNroCuo")
    private Integer numeroCuota;
    @SerializedName("pnMCuota")
    private Double cuota;
    @SerializedName("pnCapita")
    private Double capital;
    @SerializedName("pnIntere")
    private Double intereses;
    @SerializedName("pnTasSeg")
    private Double desgravamen;
    @SerializedName("pnMonItf")
    private Double itf;
    @SerializedName("pnSgToRi")
    private Double seguroTodoRiesgo;
    @SerializedName("pnSalCap")
    private Double saldoCapital;
    @SerializedName("pnPagTot")
    private Double pagoTotal;
    /*************************/

    public Cronograma(){}

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public Integer getNumeroCuota() {
        return numeroCuota;
    }

    public void setNumeroCuota(Integer numeroCuota) {
        this.numeroCuota = numeroCuota;
    }

    public Double getCuota() {
        return cuota;
    }

    public void setCuota(Double cuota) {
        this.cuota = cuota;
    }

    public Double getCapital() {
        return capital;
    }

    public void setCapital(Double capital) {
        this.capital = capital;
    }

    public Double getIntereses() {
        return intereses;
    }

    public void setIntereses(Double intereses) {
        this.intereses = intereses;
    }

    public Double getDesgravamen() {
        return desgravamen;
    }

    public void setDesgravamen(Double desgravamen) {
        this.desgravamen = desgravamen;
    }

    public Double getItf() {
        return itf;
    }

    public void setItf(Double itf) {
        this.itf = itf;
    }

    public Double getSeguroTodoRiesgo() {
        return seguroTodoRiesgo;
    }

    public void setSeguroTodoRiesgo(Double seguroTodoRiesgo) {
        this.seguroTodoRiesgo = seguroTodoRiesgo;
    }

    public Double getSaldoCapital() {
        return saldoCapital;
    }

    public void setSaldoCapital(Double saldoCapital) {
        this.saldoCapital = saldoCapital;
    }

    public Double getPagoTotal() {
        return pagoTotal;
    }

    public void setPagoTotal(Double pagoTotal) {
        this.pagoTotal = pagoTotal;
    }
}
