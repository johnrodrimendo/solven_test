package com.affirm.acceso.model;

import java.util.Date;

/**
 * Created by dev5 on 03/08/17.
 */
public class EstadoFirma {

    private Integer nroExpediente;
    private Date fechaVerificacion;
    private Integer resultadoVerificacion;
    private String comentarioVerificacion;


    public Integer getNroExpediente() {
        return nroExpediente;
    }

    public void setNroExpediente(Integer nroExpediente) {
        this.nroExpediente = nroExpediente;
    }

    public Date getFechaVerificacion() {
        return fechaVerificacion;
    }

    public void setFechaVerificacion(Date fechaVerificacion) {
        this.fechaVerificacion = fechaVerificacion;
    }

    public Integer getResultadoVerificacion() {
        return resultadoVerificacion;
    }

    public void setResultadoVerificacion(Integer resultadoVerificacion) {
        this.resultadoVerificacion = resultadoVerificacion;
    }

    public String getComentarioVerificacion() {
        return comentarioVerificacion;
    }

    public void setComentarioVerificacion(String comentarioVerificacion) {
        this.comentarioVerificacion = comentarioVerificacion;
    }
}
