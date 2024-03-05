package com.affirm.bantotalrest.model.customs;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class ObtenerTipoDeMovimientoRequest extends BtRequestData {

    private Long operacionUId;
    private Long campana;

    public Long getOperacionUId() {
        return operacionUId;
    }

    public void setOperacionUId(Long operacionUId) {
        this.operacionUId = operacionUId;
    }

    public Long getCampana() {
        return campana;
    }

    public void setCampana(Long campana) {
        this.campana = campana;
    }
}
