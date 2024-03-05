package com.affirm.bantotalrest.model.customs;

import com.affirm.bantotalrest.model.common.BTResponseData;

public class InhabilitarDireccionPersonaBcoAztecaResponse extends BTResponseData {

    public static final String DIRECCION_NO_EXISTE = "30005";

    private String Corr;
    private String Estado;

    public String getCorr() {
        return Corr;
    }

    public void setCorr(String corr) {
        Corr = corr;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }
}
