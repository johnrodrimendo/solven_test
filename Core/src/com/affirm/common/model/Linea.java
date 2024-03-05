package com.affirm.common.model;

public class Linea {

    private String numero;
    private String producto;

    public Linea(String numero, String producto) {
        this.numero = numero;
        this.producto = producto;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }
}
