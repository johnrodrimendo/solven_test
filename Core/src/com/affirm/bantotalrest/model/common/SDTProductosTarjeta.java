package com.affirm.bantotalrest.model.common;

public class SDTProductosTarjeta {

    private Long tarjetaUId;
    private String numeroTarjeta;
    private String tipoTarjeta;
    private String sucursal;

    public Long getTarjetaUId() {
        return tarjetaUId;
    }

    public void setTarjetaUId(Long tarjetaUId) {
        this.tarjetaUId = tarjetaUId;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }
}
