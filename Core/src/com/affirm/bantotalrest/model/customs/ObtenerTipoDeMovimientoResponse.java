package com.affirm.bantotalrest.model.customs;

import com.affirm.bantotalrest.model.common.BTResponseData;

public class ObtenerTipoDeMovimientoResponse extends BTResponseData {

    private Integer tipoMov;

    public Integer getTipoMov() {
        return tipoMov;
    }

    public void setTipoMov(Integer tipoMov) {
        tipoMov = tipoMov;
    }
}
