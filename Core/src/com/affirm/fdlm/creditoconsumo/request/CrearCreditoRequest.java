package com.affirm.fdlm.creditoconsumo.request;

import com.google.gson.annotations.SerializedName;

public class CrearCreditoRequest {

    public static final String TIPO_VIVIENDA_PROPIA = "P";
    public static final String TIPO_VIVIENDA_ALQUILADA = "A";
    public static final String TIPO_VIVIENDA_FAMILIAR = "F";
    public static final String TIPO_VIVIENDA_OTROS = "O";

    @SerializedName("tipo_credito")
    private String tipoCredito;

    @SerializedName("nro_solicitud")
    private Long numeroSolicitud;

    @SerializedName("producto")
    private Integer producto;

    @SerializedName("nro_documento")
    private Long numeroDocumento;

    @SerializedName("tipo_documento")
    private String tipoDocumento;

    @SerializedName("primer_apellido")
    private String primerApellido;

    @SerializedName("segundo_apellido")
    private String segundoApellido;

    @SerializedName("primer_nombre")
    private String primerNombre;

    @SerializedName("segundo_nombre")
    private String segundoNombre;

    @SerializedName("genero")
    private Character genero;

    @SerializedName("direccion_dom")
    private String direccionDomicilio;

    @SerializedName("pais")
    private String pais;

    @SerializedName("departamento")
    private Long departamento;

    @SerializedName("municipio")
    private Long municipio;

    @SerializedName("barrio")
    private Long barrio;

    @SerializedName("tipo_vivienda")
    private String tipoVivienda;

    @SerializedName("procedencia")
    private Integer procedencia;

    @SerializedName("monto_solicitado")
    private Integer montoSolicitado;

    @SerializedName("valor_cuota")
    private Double valorCuota;

    @SerializedName("nro_cuota")
    private Integer numeroCuotas;

    @SerializedName("tipo_cuenta")
    private String tipoCuenta;

    @SerializedName("nombre_banco")
    private String nombreBanco;

    @SerializedName("nro_cuenta")
    private Long numeroCuenta;

    @SerializedName("cod_banco")
    private Integer codigoBanco;

    @SerializedName("fecha_desfase")
    private String fechaDesfase;

    @SerializedName("fecha_nacimiento")
    private String fechaNacimiento;

    public String getTipoCredito() {
        return tipoCredito;
    }

    public void setTipoCredito(String tipoCredito) {
        this.tipoCredito = tipoCredito;
    }

    public Long getNumeroSolicitud() {
        return numeroSolicitud;
    }

    public void setNumeroSolicitud(Long numeroSolicitud) {
        this.numeroSolicitud = numeroSolicitud;
    }

    public Integer getProducto() {
        return producto;
    }

    public void setProducto(Integer producto) {
        this.producto = producto;
    }

    public Long getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(Long numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getPrimerNombre() {
        return primerNombre;
    }

    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }

    public String getSegundoNombre() {
        return segundoNombre;
    }

    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }

    public Character getGenero() {
        return genero;
    }

    public void setGenero(Character genero) {
        this.genero = genero;
    }

    public String getDireccionDomicilio() {
        return direccionDomicilio;
    }

    public void setDireccionDomicilio(String direccionDomicilio) {
        this.direccionDomicilio = direccionDomicilio;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public Long getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Long departamento) {
        this.departamento = departamento;
    }

    public Long getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Long municipio) {
        this.municipio = municipio;
    }

    public Long getBarrio() {
        return barrio;
    }

    public void setBarrio(Long barrio) {
        this.barrio = barrio;
    }

    public String getTipoVivienda() {
        return tipoVivienda;
    }

    public void setTipoVivienda(String tipoVivienda) {
        this.tipoVivienda = tipoVivienda;
    }

    public Integer getProcedencia() {
        return procedencia;
    }

    public void setProcedencia(Integer procedencia) {
        this.procedencia = procedencia;
    }

    public Integer getMontoSolicitado() {
        return montoSolicitado;
    }

    public void setMontoSolicitado(Integer montoSolicitado) {
        this.montoSolicitado = montoSolicitado;
    }

    public Double getValorCuota() {
        return valorCuota;
    }

    public void setValorCuota(Double valorCuota) {
        this.valorCuota = valorCuota;
    }

    public Integer getNumeroCuotas() {
        return numeroCuotas;
    }

    public void setNumeroCuotas(Integer numeroCuotas) {
        this.numeroCuotas = numeroCuotas;
    }

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public Long getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(Long numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public Integer getCodigoBanco() {
        return codigoBanco;
    }

    public void setCodigoBanco(Integer codigoBanco) {
        this.codigoBanco = codigoBanco;
    }

    public String getFechaDesfase() {
        return fechaDesfase;
    }

    public void setFechaDesfase(String fechaDesfase) {
        this.fechaDesfase = fechaDesfase;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}
