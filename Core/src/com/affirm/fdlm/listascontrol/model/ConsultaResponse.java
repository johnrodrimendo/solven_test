package com.affirm.fdlm.listascontrol.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConsultaResponse {

    public static final String NIVEL_RIESGO_SIN_COINCIDENCIAS = "0";
    public static final String NIVEL_RIESGO_BAJO = "1";
    public static final String NIVEL_RIESGO_MEDIO = "2";
    public static final String NIVEL_RIESGO_ALTO = "3";

    @SerializedName("Respuesta")
    private Respuesta respuesta;

    @SerializedName("IdResultado")
    private Integer idResultado;

    @SerializedName("TipoDocumentoRespuesta")
    private String tipoDocumentoRespuesta;

    @SerializedName("NumeroDocumentoRespuesta")
    private Long numeroDocumentoRespuesta;

    @SerializedName("RolPersonaRespuesta")
    private String rolPersonaRespuesta;

    @SerializedName("BloqueoContagio")
    private Boolean bloqueoContagio;

    @SerializedName("DetalleRespuesta")
    private List<Detalle> detalleRespuesta;

    public Respuesta getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(Respuesta respuesta) {
        this.respuesta = respuesta;
    }

    public Integer getIdResultado() {
        return idResultado;
    }

    public void setIdResultado(Integer idResultado) {
        this.idResultado = idResultado;
    }

    public String getTipoDocumentoRespuesta() {
        return tipoDocumentoRespuesta;
    }

    public void setTipoDocumentoRespuesta(String tipoDocumentoRespuesta) {
        this.tipoDocumentoRespuesta = tipoDocumentoRespuesta;
    }

    public Long getNumeroDocumentoRespuesta() {
        return numeroDocumentoRespuesta;
    }

    public void setNumeroDocumentoRespuesta(Long numeroDocumentoRespuesta) {
        this.numeroDocumentoRespuesta = numeroDocumentoRespuesta;
    }

    public String getRolPersonaRespuesta() {
        return rolPersonaRespuesta;
    }

    public void setRolPersonaRespuesta(String rolPersonaRespuesta) {
        this.rolPersonaRespuesta = rolPersonaRespuesta;
    }

    public Boolean getBloqueoContagio() {
        return bloqueoContagio;
    }

    public void setBloqueoContagio(Boolean bloqueoContagio) {
        this.bloqueoContagio = bloqueoContagio;
    }

    public List<Detalle> getDetalleRespuesta() {
        return detalleRespuesta;
    }

    public void setDetalleRespuesta(List<Detalle> detalleRespuesta) {
        this.detalleRespuesta = detalleRespuesta;
    }
}
