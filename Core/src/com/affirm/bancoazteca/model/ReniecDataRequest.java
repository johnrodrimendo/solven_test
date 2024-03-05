package com.affirm.bancoazteca.model;

public class ReniecDataRequest {

    private String NroDocumento;
    private String UsuarioConsultante;

    public String getNroDocumento() {
        return NroDocumento;
    }

    public void setNroDocumento(String nroDocumento) {
        NroDocumento = nroDocumento;
    }

    public String getUsuarioConsultante() {
        return UsuarioConsultante;
    }

    public void setUsuarioConsultante(String usuarioConsultante) {
        UsuarioConsultante = usuarioConsultante;
    }
}
