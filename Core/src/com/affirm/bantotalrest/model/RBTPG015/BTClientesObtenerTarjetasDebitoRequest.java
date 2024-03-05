package com.affirm.bantotalrest.model.RBTPG015;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class BTClientesObtenerTarjetasDebitoRequest extends BtRequestData {

    private Long clienteUId;

    public Long getClienteUId() {
        return clienteUId;
    }

    public void setClienteUId(Long clienteUId) {
        this.clienteUId = clienteUId;
    }
}
