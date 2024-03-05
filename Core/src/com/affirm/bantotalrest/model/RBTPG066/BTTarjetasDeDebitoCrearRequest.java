package com.affirm.bantotalrest.model.RBTPG066;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class BTTarjetasDeDebitoCrearRequest extends BtRequestData {

    private Long clienteUId;
    private String tipoTarjeta;
    private String nombreTarjeta;

    public Long getClienteUId() {
        return clienteUId;
    }

    public void setClienteUId(Long clienteUId) {
        this.clienteUId = clienteUId;
    }

    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public String getNombreTarjeta() {
        return nombreTarjeta;
    }

    public void setNombreTarjeta(String nombreTarjeta) {
        this.nombreTarjeta = nombreTarjeta;
    }
}
