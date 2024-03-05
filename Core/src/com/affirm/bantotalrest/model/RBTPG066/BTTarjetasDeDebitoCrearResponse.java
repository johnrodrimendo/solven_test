package com.affirm.bantotalrest.model.RBTPG066;

import com.affirm.bantotalrest.model.common.BTResponseData;

public class BTTarjetasDeDebitoCrearResponse extends BTResponseData {

    private Long tarjetaUId;
    private String numeroTarjeta;
    private String fechaExpiracion;
    private String estadoTarjeta;
    private String estadoPlastico;

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

    public String getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(String fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public String getEstadoTarjeta() {
        return estadoTarjeta;
    }

    public void setEstadoTarjeta(String estadoTarjeta) {
        this.estadoTarjeta = estadoTarjeta;
    }

    public String getEstadoPlastico() {
        return estadoPlastico;
    }

    public void setEstadoPlastico(String estadoPlastico) {
        this.estadoPlastico = estadoPlastico;
    }
}
