package com.affirm.bantotalrest.model.RBTPG013;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class BTClientesObtenerPlazosFijosRequest extends BtRequestData {

    private Long clienteUId;

    public Long getClienteUId() {
        return clienteUId;
    }

    public void setClienteUId(Long clienteUId) {
        this.clienteUId = clienteUId;
    }
}
