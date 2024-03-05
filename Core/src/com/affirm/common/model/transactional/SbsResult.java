package com.affirm.common.model.transactional;

import java.io.Serializable;

/**
 * Created by john on 29/09/16.
 */
public class SbsResult implements Serializable {

    private String producto;
    private String entidad;
    private double tasa;

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public double getTasa() {
        return tasa;
    }

    public void setTasa(double tasa) {
        this.tasa = tasa;
    }

    @Override
    public String toString() {
        return "SbsResult{" + "producto=" + producto + ", entidad=" + entidad + ", tasa=" + tasa + '}';
    }
}
