package com.affirm.compartamos.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dev5 on 30/11/17.
 */
public class RiesgoUnico {

    @SerializedName("plRiUnDi")
    private Boolean indicadorRiesgo;
    @SerializedName("pcTipRie")
    private String tipoRiesgo;


    public Boolean getIndicadorRiesgo() {
        return indicadorRiesgo;
    }

    public void setIndicadorRiesgo(Boolean indicadorRiesgo) {
        this.indicadorRiesgo = indicadorRiesgo;
    }

    public String getTipoRiesgo() {
        return tipoRiesgo;
    }

    public void setTipoRiesgo(String tipoRiesgo) {
        this.tipoRiesgo = tipoRiesgo;
    }
}
