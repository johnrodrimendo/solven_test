package com.affirm.fdlm.datacredito.model;

public class CommonResult {

    private Integer codigo;
    private String mensaje;
    private String datosResultado;
    private String reporte;
    private Integer derechosPoliticos;
    private Integer origen;

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getDatosResultado() {
        return datosResultado;
    }

    public void setDatosResultado(String datosResultado) {
        this.datosResultado = datosResultado;
    }

    public String getReporte() {
        return reporte;
    }

    public void setReporte(String reporte) {
        this.reporte = reporte;
    }

    public Integer getDerechosPoliticos() {
        return derechosPoliticos;
    }

    public void setDerechosPoliticos(Integer derechosPoliticos) {
        this.derechosPoliticos = derechosPoliticos;
    }

    public Integer getOrigen() {
        return origen;
    }

    public void setOrigen(Integer origen) {
        this.origen = origen;
    }
}