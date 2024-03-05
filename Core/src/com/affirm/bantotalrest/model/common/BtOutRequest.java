package com.affirm.bantotalrest.model.common;

public class BtOutRequest {
    private String Canal;
    private String Servicio;
    private String Fecha;
    private String Hora;
    private String Requerimiento;
    private Integer Numero;
    private String Estado;

    public String getCanal() {
        return Canal;
    }

    public void setCanal(String canal) {
        Canal = canal;
    }

    public String getServicio() {
        return Servicio;
    }

    public void setServicio(String servicio) {
        Servicio = servicio;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public String getHora() {
        return Hora;
    }

    public void setHora(String hora) {
        Hora = hora;
    }

    public String getRequerimiento() {
        return Requerimiento;
    }

    public void setRequerimiento(String requerimiento) {
        Requerimiento = requerimiento;
    }

    public Integer getNumero() {
        return Numero;
    }

    public void setNumero(Integer numero) {
        Numero = numero;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }
}