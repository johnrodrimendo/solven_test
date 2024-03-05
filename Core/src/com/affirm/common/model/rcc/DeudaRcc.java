package com.affirm.common.model.rcc;

import java.util.Date;

public class DeudaRcc {

    private Date rccDate; //fe_repsbs,
    private String codigoEmpresa; //co_entsbs,
    private String codigoCuenta; //co_cuesbs,
    private String tipoMoneda; //ti_moneda,
    private Double saldo; //im_cuenta,
    private Integer tipoCredito; //ti_cresbs,
    private Integer claDeu; //ti_clasbs,
    private Integer conDia; //ca_diamor,
    private String nombreCalificacion; //no_califi
    private String nombreEmpresa; //no_califi
    private String codigoCuentaOriginal; //no_califi

    public Date getRccDate() {
        return rccDate;
    }

    public void setRccDate(Date rccDate) {
        this.rccDate = rccDate;
    }

    public String getCodigoEmpresa() {
        return codigoEmpresa;
    }

    public void setCodigoEmpresa(String codigoEmpresa) {
        this.codigoEmpresa = codigoEmpresa;
    }

    public String getCodigoCuenta() {
        return codigoCuenta;
    }

    public void setCodigoCuenta(String codigoCuenta) {
        this.codigoCuenta = codigoCuenta;
    }

    public String getTipoMoneda() {
        return tipoMoneda;
    }

    public void setTipoMoneda(String tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public Integer getTipoCredito() {
        return tipoCredito;
    }

    public void setTipoCredito(Integer tipoCredito) {
        this.tipoCredito = tipoCredito;
    }

    public Integer getClaDeu() {
        return claDeu;
    }

    public void setClaDeu(Integer claDeu) {
        this.claDeu = claDeu;
    }

    public Integer getConDia() {
        return conDia;
    }

    public void setConDia(Integer conDia) {
        this.conDia = conDia;
    }

    public String getNombreCalificacion() {
        return nombreCalificacion;
    }

    public void setNombreCalificacion(String nombreCalificacion) {
        this.nombreCalificacion = nombreCalificacion;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getCodigoCuentaOriginal() {
        return codigoCuentaOriginal;
    }

    public void setCodigoCuentaOriginal(String codigoCuentaOriginal) {
        this.codigoCuentaOriginal = codigoCuentaOriginal;
    }
}
