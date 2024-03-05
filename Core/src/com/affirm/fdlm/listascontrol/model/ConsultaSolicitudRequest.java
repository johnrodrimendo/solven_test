package com.affirm.fdlm.listascontrol.model;

import com.google.gson.annotations.SerializedName;

public class ConsultaSolicitudRequest {

    public static final String ROL_SOLICITUD_TITULAR = "T";
    public static final String ROL_SOLICITUD_GARANTE_O_CODEUDOR = "G";
    public static final String ROL_SOLICITUD_CONYUGE = "O";

    public static final String TIPO_DOCUMENTO_CEDULA_CIUDADANIA = "C";
    public static final String TIPO_DOCUMENTO_NIT = "N";


    @SerializedName("NumeroSolicitud")
    private String numeroSolicitud;

    @SerializedName("UsuarioConsulta")
    private String usuarioConsulta;

    @SerializedName("AplicacionConsulta")
    private String aplicacionConsulta;

    public String getNumeroSolicitud() {
        return numeroSolicitud;
    }

    public void setNumeroSolicitud(String numeroSolicitud) {
        this.numeroSolicitud = numeroSolicitud;
    }

    public String getUsuarioConsulta() {
        return usuarioConsulta;
    }

    public void setUsuarioConsulta(String usuarioConsulta) {
        this.usuarioConsulta = usuarioConsulta;
    }

    public String getAplicacionConsulta() {
        return aplicacionConsulta;
    }

    public void setAplicacionConsulta(String aplicacionConsulta) {
        this.aplicacionConsulta = aplicacionConsulta;
    }
}
