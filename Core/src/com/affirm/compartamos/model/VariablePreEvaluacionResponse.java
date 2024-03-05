package com.affirm.compartamos.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dev5 on 30/11/17.
 */
public class VariablePreEvaluacionResponse {

    /*TraerVariablesPreEvaluacion*/
    /**********RESPONSE***********/
    @SerializedName("poCliente")
    private Cliente poCliente;
    @SerializedName("poCredito")
    private Credito poCredito;
    @SerializedName("poSolicitud")
    private Solicitud poSolicitud;
    /****************************/

    public Credito getPoCredito() {
        return poCredito;
    }

    public void setPoCredito(Credito poCredito) {
        this.poCredito = poCredito;
    }

    public Cliente getPoCliente() {
        return poCliente;
    }

    public void setPoCliente(Cliente poCliente) {
        this.poCliente = poCliente;
    }

    public Solicitud getPoSolicitud() {
        return poSolicitud;
    }

    public void setPoSolicitud(Solicitud poSolicitud) {
        this.poSolicitud = poSolicitud;
    }

}
