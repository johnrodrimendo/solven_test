package com.affirm.fdlm.listascontrol.model;

import com.google.gson.annotations.SerializedName;

public class Detalle {

    @SerializedName("Riesgo")
    private Integer riesgo;

    @SerializedName("ListaRiesgo")
    private String listaRiesgo;

    public Integer getRiesgo() {
        return riesgo;
    }

    public void setRiesgo(Integer riesgo) {
        this.riesgo = riesgo;
    }

    public String getListaRiesgo() {
        return listaRiesgo;
    }

    public void setListaRiesgo(String listaRiesgo) {
        this.listaRiesgo = listaRiesgo;
    }
}
