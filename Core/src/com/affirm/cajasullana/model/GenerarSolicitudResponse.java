package com.affirm.cajasullana.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dev5 on 23/02/18.
 */
public class GenerarSolicitudResponse {

    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private String status;
    @SerializedName("numTrx")
    private String numeroTransaccion;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNumeroTransaccion() {
        return numeroTransaccion;
    }

    public void setNumeroTransaccion(String numeroTransaccion) {
        this.numeroTransaccion = numeroTransaccion;
    }
}
