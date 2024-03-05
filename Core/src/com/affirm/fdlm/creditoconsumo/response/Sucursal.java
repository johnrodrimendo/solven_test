package com.affirm.fdlm.creditoconsumo.response;

import com.google.gson.annotations.SerializedName;

public class Sucursal {

    @SerializedName("CODIGO SUCURSAL")
    private Integer codigo;

    @SerializedName("NOMBRE SUCURSAL")
    private String nombre;

    @SerializedName("DIRECCION")
    private String direccion;

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
