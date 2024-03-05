package com.affirm.compartamos.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dev5 on 14/12/17.
 */
public class GenerarCreditoResponse {

    @SerializedName("poCredito")
    private Credito credito;
    @SerializedName("paCronograma")
    private List<Cronograma> cronograma;
    @SerializedName("poCliente")
    private Cliente cliente;


    public Credito getCredito() {
        return credito;
    }

    public void setCredito(Credito credito) {
        this.credito = credito;
    }

    public List<Cronograma> getCronograma() {
        return cronograma;
    }

    public void setCronograma(List<Cronograma> cronograma) {
        this.cronograma = cronograma;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
