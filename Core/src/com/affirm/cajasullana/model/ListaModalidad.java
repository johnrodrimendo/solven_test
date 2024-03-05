package com.affirm.cajasullana.model;

import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;

/**
 * Created by dev5 on 16/04/18.
 */
public class ListaModalidad {

    @SerializedName("CLASE")
    private String clase;
    @SerializedName("CODIGO_CLASE")
    private int codigoClase;

    public ListaModalidad(){}

    public ListaModalidad(JSONObject obj){
        setClase(obj.getString("CLASE"));
        setCodigoClase(obj.getInt("CODIGO_CLASE"));
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public int getCodigoClase() {
        return codigoClase;
    }

    public void setCodigoClase(int codigoClase) {
        this.codigoClase = codigoClase;
    }
}
