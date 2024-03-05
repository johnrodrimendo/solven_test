package com.affirm.bantotalrest.model.customs;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class ActualizarDireccionPersonaBcoAztecaRequest extends ActualizarDireccionCommonBcoAztecaRequest {

    private String Pais;
    private String Petdoc;
    private String Pendoc;

    public String getPais() {
        return Pais;
    }

    public void setPais(String pais) {
        Pais = pais;
    }

    public String getPetdoc() {
        return Petdoc;
    }

    public void setPetdoc(String petdoc) {
        Petdoc = petdoc;
    }

    public String getPendoc() {
        return Pendoc;
    }

    public void setPendoc(String pendoc) {
        Pendoc = pendoc;
    }
}
