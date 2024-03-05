package com.affirm.bantotalrest.model.RBTPG007;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class BTCuentasDeAhorroObtenerDatosRequest extends BtRequestData {

    private Long operacionUId;

    public Long getOperacionUId() {
        return operacionUId;
    }

    public void setOperacionUId(Long operacionUId) {
        this.operacionUId = operacionUId;
    }
}
