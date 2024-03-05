package com.affirm.bantotalrest.model.common;

public class BtError {
    private String Codigo;
    private String Descripcion;
    private String Severidad;

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String codigo) {
        Codigo = codigo;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getSeveridad() {
        return Severidad;
    }

    public void setSeveridad(String severidad) {
        Severidad = severidad;
    }
}
