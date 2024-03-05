package com.affirm.bantotalrest.model.customs;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class ActualizarDireccionCuentaBcoAztecaRequest extends ActualizarDireccionCommonBcoAztecaRequest {

    private Integer Pgcod;
    private String CuentaBT;

    public Integer getPgcod() {
        return Pgcod;
    }

    public void setPgcod(Integer pgcod) {
        Pgcod = pgcod;
    }

    public String getCuentaBT() {
        return CuentaBT;
    }

    public void setCuentaBT(String cuentaBT) {
        CuentaBT = cuentaBT;
    }
}
