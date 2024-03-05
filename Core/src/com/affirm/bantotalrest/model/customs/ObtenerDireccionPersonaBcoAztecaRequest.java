package com.affirm.bantotalrest.model.customs;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class ObtenerDireccionPersonaBcoAztecaRequest extends BtRequestData {

    public static final int DOMICILIO_VIVIENDA = 1;
    public static final int DOMICILIO_LABORAL = 3;

    private Integer Pais;
    private Integer Petdoc;
    private String Pendoc;
    private Integer Docod;

    public Integer getPais() {
        return Pais;
    }

    public void setPais(Integer pais) {
        Pais = pais;
    }

    public Integer getPetdoc() {
        return Petdoc;
    }

    public void setPetdoc(Integer petdoc) {
        Petdoc = petdoc;
    }

    public String getPendoc() {
        return Pendoc;
    }

    public void setPendoc(String pendoc) {
        Pendoc = pendoc;
    }

    public Integer getDocod() {
        return Docod;
    }

    public void setDocod(Integer docod) {
        Docod = docod;
    }
}
