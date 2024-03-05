package com.affirm.acceso.model;

import com.affirm.common.model.transactional.OriginalSchedule;

import java.util.Date;

/**
 * Created by dev5 on 31/07/17.
 */
public class Cronograma {

    private Integer numCuotas;
    private Date fechaVencimiento;
    private Integer diasCuota;
    private Double saldoInicial;
    private Double capital;
    private Double interes;
    private Double interesCapitalizado;
    private Double cuota;
    private Double saldoFinal;
    private Double seguroVehicular;
    private Double desgravamen;
    private Double cuotaTotal;

    public Cronograma(){
        this.setNumCuotas(1);
        this.setFechaVencimiento(new Date());
        this.setDiasCuota(5);
        this.setSaldoInicial(10000.00);
        this.setCapital(30000.00);
        this.setInteres(10.00);
        this.setInteresCapitalizado(1000.00);
        this.setCuota(500.00);
        this.setSaldoFinal(30000.00);
        this.setSeguroVehicular(100.00);
        this.setDesgravamen(100.00);
        this.setCuotaTotal(2000.00);
    }

    public OriginalSchedule toOriginalSchedule(){
        OriginalSchedule schedule = new OriginalSchedule();
        schedule.setInstallmentId(numCuotas);
        schedule.setDueDate(fechaVencimiento);
        schedule.setInstallmentCapital(capital);
        schedule.setInstallment(cuota);
        schedule.setInterest(interes);
        schedule.setInsurance(desgravamen);
        schedule.setInstallmentAmount(cuotaTotal);
        schedule.setCarInsurance(seguroVehicular);
        return schedule;
    }

    public Integer getNumCuotas() {
        return numCuotas;
    }

    public void setNumCuotas(Integer numCuotas) {
        this.numCuotas = numCuotas;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Integer getDiasCuota() {
        return diasCuota;
    }

    public void setDiasCuota(Integer diasCuota) {
        this.diasCuota = diasCuota;
    }

    public Double getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(Double saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public Double getCapital() {
        return capital;
    }

    public void setCapital(Double capital) {
        this.capital = capital;
    }

    public Double getInteres() {
        return interes;
    }

    public void setInteres(Double interes) {
        this.interes = interes;
    }

    public Double getInteresCapitalizado() {
        return interesCapitalizado;
    }

    public void setInteresCapitalizado(Double interesCapitalizado) {
        this.interesCapitalizado = interesCapitalizado;
    }

    public Double getCuota() {
        return cuota;
    }

    public void setCuota(Double cuota) {
        this.cuota = cuota;
    }

    public Double getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(Double saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    public Double getSeguroVehicular() {
        return seguroVehicular;
    }

    public void setSeguroVehicular(Double seguroVehicular) {
        this.seguroVehicular = seguroVehicular;
    }

    public Double getDesgravamen() {
        return desgravamen;
    }

    public void setDesgravamen(Double desgravamen) {
        this.desgravamen = desgravamen;
    }

    public Double getCuotaTotal() {
        return cuotaTotal;
    }

    public void setCuotaTotal(Double cuotaTotal) {
        this.cuotaTotal = cuotaTotal;
    }
}
