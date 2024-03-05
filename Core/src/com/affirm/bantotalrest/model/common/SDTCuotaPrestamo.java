package com.affirm.bantotalrest.model.common;

import org.apache.commons.lang.time.DateUtils;

import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class SDTCuotaPrestamo {

    private Integer nroCuota;
    private Integer diasMora;
    private String fechaPago;
    private String tipoCuota;
    private String concepto;
    private String fechaVencimiento;
    private String estado;
    private String estadoDsc;
    private String fechaUltimoPago;
    private Double capital;
    private Double intereses;
    private Double comisiones;
    private Double seguros;
    private Double subsidios;
    private Double impuestos;
    private Double interesMora;
    private Double otrosConceptos;
    private Double total;
    private Double importePago;
    private Double redondeo;

    public Date getFechaPagoAsDate() throws Exception{
        if(fechaPago != null)
            return new SimpleDateFormat("yyyy-MM-dd").parse(fechaPago);
        return null;
    }

    public int getDaysWithDebt() throws Exception{
        Date paymentDate = new SimpleDateFormat("yyyy-MM-dd").parse(fechaPago);
        Date currentDate = DateUtils.truncate(new Date(), Calendar.DATE);
        Calendar paymentCal = Calendar.getInstance();
        paymentCal.setTime(paymentDate);
        Calendar currendCal = Calendar.getInstance();
        long daysBetween = ChronoUnit.DAYS.between(paymentCal.toInstant(), currendCal.toInstant());
        if(daysBetween > 0){
            return Math.toIntExact(daysBetween);
        }
        return 0;
    }

    public boolean canBePayed() throws Exception{
        Date paymentDate = new SimpleDateFormat("yyyy-MM-dd").parse(fechaPago);
        Calendar m_calendar=Calendar.getInstance();
        m_calendar.setTime(paymentDate);
        int nMonth1=12*m_calendar.get(Calendar.YEAR)+m_calendar.get(Calendar.MONTH);
        m_calendar.setTime(new Date());
        int nMonth2=12*m_calendar.get(Calendar.YEAR)+m_calendar.get(Calendar.MONTH);
        int monthsBeetwen = nMonth1-nMonth2;
        return monthsBeetwen <= 2;
    }

    public Integer getNroCuota() {
        return nroCuota;
    }

    public void setNroCuota(Integer nroCuota) {
        this.nroCuota = nroCuota;
    }

    public Integer getDiasMora() {
        return diasMora;
    }

    public void setDiasMora(Integer diasMora) {
        this.diasMora = diasMora;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getTipoCuota() {
        return tipoCuota;
    }

    public void setTipoCuota(String tipoCuota) {
        this.tipoCuota = tipoCuota;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstadoDsc() {
        return estadoDsc;
    }

    public void setEstadoDsc(String estadoDsc) {
        this.estadoDsc = estadoDsc;
    }

    public String getFechaUltimoPago() {
        return fechaUltimoPago;
    }

    public void setFechaUltimoPago(String fechaUltimoPago) {
        this.fechaUltimoPago = fechaUltimoPago;
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

    public Double getComisiones() {
        return comisiones;
    }

    public void setComisiones(Double comisiones) {
        this.comisiones = comisiones;
    }

    public Double getSeguros() {
        return seguros;
    }

    public void setSeguros(Double seguros) {
        this.seguros = seguros;
    }

    public Double getSubsidios() {
        return subsidios;
    }

    public void setSubsidios(Double subsidios) {
        this.subsidios = subsidios;
    }

    public Double getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(Double impuestos) {
        this.impuestos = impuestos;
    }

    public Double getInteresMora() {
        return interesMora;
    }

    public void setInteresMora(Double interesMora) {
        this.interesMora = interesMora;
    }

    public Double getOtrosConceptos() {
        return otrosConceptos;
    }

    public void setOtrosConceptos(Double otrosConceptos) {
        this.otrosConceptos = otrosConceptos;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getImportePago() {
        return importePago;
    }

    public void setImportePago(Double importePago) {
        this.importePago = importePago;
    }

    public Double getRedondeo() {
        return redondeo;
    }

    public void setRedondeo(Double redondeo) {
        this.redondeo = redondeo;
    }
}
