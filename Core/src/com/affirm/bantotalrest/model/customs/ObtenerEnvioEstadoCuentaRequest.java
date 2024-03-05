package com.affirm.bantotalrest.model.customs;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class ObtenerEnvioEstadoCuentaRequest extends BtRequestData {

    private Long OperacionUId;

    public Long getOperacionUId() {
        return OperacionUId;
    }

    public void setOperacionUId(Long operacionUId) {
        OperacionUId = operacionUId;
    }
}
