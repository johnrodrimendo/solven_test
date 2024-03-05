package com.affirm.cajasullana.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dev5 on 21/02/18.
 */
public class AdmisibilidadResponse {

    @SerializedName("nombreConyuge")
    private String nombreConyugue;
    @SerializedName("tipDocumentoConyuge")
    private String tipoDocumentoConyugue;
    @SerializedName("status")
    private String status;
    @SerializedName("estadoCivil")
    private String estadoCivil;
    @SerializedName("apellidoPaternoTitular")
    private String apellidoPaternoTitular;
    @SerializedName("sexo")
    private String genero;
    @SerializedName("apellidoMaternoConyuge")
    private String apellidoMatrnoConyugue;
    @SerializedName("tipoPersoneria")
    private String tipoPersona;
    @SerializedName("apellidoPaternoConyuge")
    private String apellidoPaternoConyugue;
    @SerializedName("numDocumentoConyuge")
    private String numeroDocumentoConyugue;
    @SerializedName("nombreTitular")
    private String nombreTitular;
    @SerializedName("message")
    private String errorMessage;
    @SerializedName("apellidoCasadaConyuge")
    private String apellidoCasadaConyugue;
    @SerializedName("codigoCliente")
    private String codigoCliente;
    @SerializedName("apellidoCasadaTitular")
    private String apellidoCasadaTitular;
    @SerializedName("nombreConyugeCompleto")
    private String nombreConyugueCompleto;
    @SerializedName("apellidoMaternoTitular")
    private String apellidoMaternoTitular;
    @SerializedName("nombreTitularCompleto")
    private String nombreTitularCompleto;
    @SerializedName("codigoClienteC")
    private String codigoClienteC;
    @SerializedName("listaModalidad")
    private List<ListaModalidad> listaModalidadList;



    public String getNombreConyugue() {
        return nombreConyugue;
    }

    public void setNombreConyugue(String nombreConyugue) {
        this.nombreConyugue = nombreConyugue;
    }

    public String getTipoDocumentoConyugue() {
        return tipoDocumentoConyugue;
    }

    public void setTipoDocumentoConyugue(String tipoDocumentoConyugue) {
        this.tipoDocumentoConyugue = tipoDocumentoConyugue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getApellidoPaternoTitular() {
        return apellidoPaternoTitular;
    }

    public void setApellidoPaternoTitular(String apellidoPaternoTitular) {
        this.apellidoPaternoTitular = apellidoPaternoTitular;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getApellidoMatrnoConyugue() {
        return apellidoMatrnoConyugue;
    }

    public void setApellidoMatrnoConyugue(String apellidoMatrnoConyugue) {
        this.apellidoMatrnoConyugue = apellidoMatrnoConyugue;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public String getApellidoPaternoConyugue() {
        return apellidoPaternoConyugue;
    }

    public void setApellidoPaternoConyugue(String apellidoPaternoConyugue) {
        this.apellidoPaternoConyugue = apellidoPaternoConyugue;
    }

    public String getNumeroDocumentoConyugue() {
        return numeroDocumentoConyugue;
    }

    public void setNumeroDocumentoConyugue(String numeroDocumentoConyugue) {
        this.numeroDocumentoConyugue = numeroDocumentoConyugue;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getApellidoCasadaConyugue() {
        return apellidoCasadaConyugue;
    }

    public void setApellidoCasadaConyugue(String apellidoCasadaConyugue) {
        this.apellidoCasadaConyugue = apellidoCasadaConyugue;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getApellidoCasadaTitular() {
        return apellidoCasadaTitular;
    }

    public void setApellidoCasadaTitular(String apellidoCasadaTitular) {
        this.apellidoCasadaTitular = apellidoCasadaTitular;
    }

    public String getNombreConyugueCompleto() {
        return nombreConyugueCompleto;
    }

    public void setNombreConyugueCompleto(String nombreConyugueCompleto) {
        this.nombreConyugueCompleto = nombreConyugueCompleto;
    }

    public String getApellidoMaternoTitular() {
        return apellidoMaternoTitular;
    }

    public void setApellidoMaternoTitular(String apellidoMaternoTitular) {
        this.apellidoMaternoTitular = apellidoMaternoTitular;
    }

    public String getNombreTitularCompleto() {
        return nombreTitularCompleto;
    }

    public void setNombreTitularCompleto(String nombreTitularCompleto) {
        this.nombreTitularCompleto = nombreTitularCompleto;
    }

    public String getCodigoClienteC() {
        return codigoClienteC;
    }

    public void setCodigoClienteC(String codigoClienteC) {
        this.codigoClienteC = codigoClienteC;
    }

    public List<ListaModalidad> getListaModalidadList() {
        return listaModalidadList;
    }

    public void setListaModalidadList(List<ListaModalidad> listaModalidadList) {
        this.listaModalidadList = listaModalidadList;
    }
}
