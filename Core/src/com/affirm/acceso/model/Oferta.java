package com.affirm.acceso.model;

import com.affirm.common.model.catalog.Currency;
import com.affirm.common.model.transactional.LoanOffer;
import com.affirm.common.service.CatalogService;

/**
 * Created by dev5 on 31/07/17.
 */
public class Oferta {

    private Integer numExpediente;
    private Integer numCuotas;
    private Double monto;
    private Double cuota;
    private Integer score;
    private String descripion;
    private Double tipoCambio;
    private String moneda;
    private Double cuotaInicial;
    private Double tea;
    private Double tcea;
    private Double tasaMoratoria;
    private Double minCuotaInicial;

    public Oferta(){
//        this.setNumExpediente(1);
//        this.setNumCuotas(48);
//        this.setMonto(30000.00);
//        this.setCuota(500.00);
//        this.setScore(1);
//        this.setDescripion("Descripcion");
//        this.setTipoCambio(3.3);
//        this.setMoneda("S/");
//        this.setCuotaInicial(10000.00);
//        this.setTea(10.00);
//        this.setTcea(12.00);
    }

    public LoanOffer toLoanOffer(CatalogService catalogService) {
        LoanOffer loanOffer = new LoanOffer();
        loanOffer.setInstallments(numCuotas);
        loanOffer.setAmmount(monto);
        loanOffer.setInstallmentAmmount(cuota);
        loanOffer.setEntityScore(score);
        loanOffer.setDownPayment(cuotaInicial);
        loanOffer.setDownPaymentCurrency(catalogService.getCurrency(Currency.USD));// This is USD by default by our request!
        loanOffer.setEffectiveAnualRate(tea != null ? tea * 100 : null);
        loanOffer.setEffectiveAnnualCostRate(tcea != null ? tcea * 100 : null);
        loanOffer.setMoratoriumRate(tasaMoratoria);
        loanOffer.setMinAmmount(minCuotaInicial);
        return loanOffer;
    }

    public Integer getNumExpediente() {
        return numExpediente;
    }

    public void setNumExpediente(Integer numExpediente) {
        this.numExpediente = numExpediente;
    }

    public Integer getNumCuotas() {
        return numCuotas;
    }

    public void setNumCuotas(Integer numCuotas) {
        this.numCuotas = numCuotas;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public Double getCuota() {
        return cuota;
    }

    public void setCuota(Double cuota) {
        this.cuota = cuota;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getDescripion() {
        return descripion;
    }

    public void setDescripion(String descripion) {
        this.descripion = descripion;
    }

    public Double getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(Double tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public Double getTea() {
        return tea;
    }

    public void setTea(Double tea) {
        this.tea = tea;
    }

    public Double getTcea() {
        return tcea;
    }

    public void setTcea(Double tcea) {
        this.tcea = tcea;
    }

    public Double getCuotaInicial() {
        return cuotaInicial;
    }

    public void setCuotaInicial(Double cuotaInicial) {
        this.cuotaInicial = cuotaInicial;
    }

    public Double getTasaMoratoria() {
        return tasaMoratoria;
    }

    public void setTasaMoratoria(Double tasaMoratoria) {
        this.tasaMoratoria = tasaMoratoria;
    }

    public Double getMinCuotaInicial() {
        return minCuotaInicial;
    }

    public void setMinCuotaInicial(Double minCuotaInicial) {
        this.minCuotaInicial = minCuotaInicial;
    }
}
