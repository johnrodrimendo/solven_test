package com.affirm.sentinel.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CrearCuestionarioTitularRequest extends CredencialesRequest {

    private String TipoDoc;
    private String NroDoc;
    private Integer CodCue;
    private String Celular;

    public String getTipoDoc() {
        return TipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        TipoDoc = tipoDoc;
    }

    public String getNroDoc() {
        return NroDoc;
    }

    public void setNroDoc(String nroDoc) {
        NroDoc = nroDoc;
    }

    public Integer getCodCue() {
        return CodCue;
    }

    public void setCodCue(Integer codCue) {
        CodCue = codCue;
    }

    public String getCelular() {
        return Celular;
    }

    public void setCelular(String celular) {
        Celular = celular;
    }
}
