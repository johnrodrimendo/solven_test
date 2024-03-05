package com.affirm.fdlm.listascontrol.model;

import com.google.gson.annotations.SerializedName;

public class Respuesta {

    @SerializedName("IdRespuesta")
    private Integer idRespuesta;

    @SerializedName("MensajeRespuesta")
    private String mensajeRespuesta;

    public Integer getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(Integer idRespuesta) {
        this.idRespuesta = idRespuesta;
    }

    public String getMensajeRespuesta() {
        return mensajeRespuesta;
    }

    public void setMensajeRespuesta(String mensajeRespuesta) {
        this.mensajeRespuesta = mensajeRespuesta;
    }
}
