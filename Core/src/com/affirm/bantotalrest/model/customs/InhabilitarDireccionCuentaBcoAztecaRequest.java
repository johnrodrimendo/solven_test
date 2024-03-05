package com.affirm.bantotalrest.model.customs;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class InhabilitarDireccionCuentaBcoAztecaRequest extends BtRequestData {

    private Integer Pgcod;
    private String CuentaBT;
    private Integer Docod;

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

    public Integer getDocod() {
        return Docod;
    }

    public void setDocod(Integer docod) {
        Docod = docod;
    }
}
