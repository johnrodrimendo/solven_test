package com.affirm.ripley.model;

import java.util.Date;

/**
 * Created by dev5 on 26/05/17.
 */
public class RipleyPreAprrovedBase {

    private String codigoBase;
    private int tipoDocumento;
    private String numeroDocumento;
    private String nombres;
    private int edad;
    private String condicionLaboral;
    private String empleador;
    private int plazo;
    private double monto;
    private double tasa;
    private String productosOfrecidos;
    private String modalidadActivacion;
    private Date vigenciaDesde;
    private Date vigenciaHasta;
    private String distrito;
    private String direccion;
    private String telefonoParticular1;
    private String telefonoParticular2;
    private String telefonoEmpleador1;
    private String telefonoEmpleador2;
    private String observacion;
    private String plaza;
    private String cuenta;
    private String tipoTarjeta;
    private String productoFisa;
    private String grupoSEF;

    public RipleyPreAprrovedBase(){
        this.setCodigoBase("RIE_SEF_CLIENTES_201704_NO_PD");
        this.setTipoDocumento(1);
        this.setNumeroDocumento("42608040");
        this.setNombres("Martin Iberico Hidalgo");
        this.setEdad(32);
        this.setCondicionLaboral("DEP");
        this.setEmpleador(null);
        this.setPlazo(36);
        this.setMonto(20000.00);
        this.setTasa(1.5);
        this.setProductosOfrecidos("Super Efectivo");
        this.setModalidadActivacion("DNI");
        this.setVigenciaDesde(new Date());
        this.setVigenciaHasta(new Date());
        this.setDistrito("Miraflores");
        this.setDireccion("Av. Mariscal La Mar 398");
        this.setTelefonoParticular1(null);
        this.setTelefonoParticular2(null);
        this.setTelefonoEmpleador1(null);
        this.setTelefonoEmpleador2(null);
    }

    public String getCodigoBase() {
        return codigoBase;
    }

    public void setCodigoBase(String codigoBase) {
        this.codigoBase = codigoBase;
    }

    public int getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(int tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getCondicionLaboral() {
        return condicionLaboral;
    }

    public void setCondicionLaboral(String condicionLaboral) {
        this.condicionLaboral = condicionLaboral;
    }

    public String getEmpleador() {
        return empleador;
    }

    public void setEmpleador(String empleador) {
        this.empleador = empleador;
    }

    public int getPlazo() {
        return plazo;
    }

    public void setPlazo(int plazo) {
        this.plazo = plazo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getTasa() {
        return tasa;
    }

    public void setTasa(double tasa) {
        this.tasa = tasa;
    }

    public String getProductosOfrecidos() {
        return productosOfrecidos;
    }

    public void setProductosOfrecidos(String productosOfrecidos) {
        this.productosOfrecidos = productosOfrecidos;
    }

    public String getModalidadActivacion() {
        return modalidadActivacion;
    }

    public void setModalidadActivacion(String modalidadActivacion) {
        this.modalidadActivacion = modalidadActivacion;
    }

    public Date getVigenciaDesde() {
        return vigenciaDesde;
    }

    public void setVigenciaDesde(Date vigenciaDesde) {
        this.vigenciaDesde = vigenciaDesde;
    }

    public Date getVigenciaHasta() {
        return vigenciaHasta;
    }

    public void setVigenciaHasta(Date vigenciaHasta) {
        this.vigenciaHasta = vigenciaHasta;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefonoParticular1() {
        return telefonoParticular1;
    }

    public void setTelefonoParticular1(String telefonoParticular1) {
        this.telefonoParticular1 = telefonoParticular1;
    }

    public String getTelefonoParticular2() {
        return telefonoParticular2;
    }

    public void setTelefonoParticular2(String telefonoParticular2) {
        this.telefonoParticular2 = telefonoParticular2;
    }

    public String getTelefonoEmpleador1() {
        return telefonoEmpleador1;
    }

    public void setTelefonoEmpleador1(String telefonoEmpleador1) {
        this.telefonoEmpleador1 = telefonoEmpleador1;
    }

    public String getTelefonoEmpleador2() {
        return telefonoEmpleador2;
    }

    public void setTelefonoEmpleador2(String telefonoEmpleador2) {
        this.telefonoEmpleador2 = telefonoEmpleador2;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getPlaza() {
        return plaza;
    }

    public void setPlaza(String plaza) {
        this.plaza = plaza;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public String getProductoFisa() {
        return productoFisa;
    }

    public void setProductoFisa(String productoFisa) {
        this.productoFisa = productoFisa;
    }

    public String getGrupoSEF() {
        return grupoSEF;
    }

    public void setGrupoSEF(String grupoSEF) {
        this.grupoSEF = grupoSEF;
    }
}
