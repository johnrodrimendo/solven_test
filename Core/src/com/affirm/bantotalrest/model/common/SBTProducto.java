package com.affirm.bantotalrest.model.common;

public class SBTProducto {
    private String papel;
    private String moneda;
    private Long productoUId;
    private String nombre;

    public String getPapel() {
        return papel;
    }

    public void setPapel(String papel) {
        this.papel = papel;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public Long getProductoUId() {
        return productoUId;
    }

    public void setProductoUId(Long productoUId) {
        this.productoUId = productoUId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
