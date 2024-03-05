package com.affirm.bantotalrest.model.customs;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class BTCorresponsalesConsultaCreditosRequest extends BtRequestData {

    private String Canal;
    private Integer TipoDocumento;
    private String Documento;
    private String Credito;
    private String Fecha;
    private String Hora;
    private String IDUCorre;
    private String IdTerminal;
    private Integer CantidadCuotas;
    private Integer Moneda;

    public String getCanal() {
        return Canal;
    }

    public void setCanal(String canal) {
        Canal = canal;
    }

    public Integer getTipoDocumento() {
        return TipoDocumento;
    }

    public void setTipoDocumento(Integer tipoDocumento) {
        TipoDocumento = tipoDocumento;
    }

    public String getDocumento() {
        return Documento;
    }

    public void setDocumento(String documento) {
        Documento = documento;
    }

    public String getCredito() {
        return Credito;
    }

    public void setCredito(String credito) {
        Credito = credito;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public String getHora() {
        return Hora;
    }

    public void setHora(String hora) {
        Hora = hora;
    }

    public String getIDUCorre() {
        return IDUCorre;
    }

    public void setIDUCorre(String IDUCorre) {
        this.IDUCorre = IDUCorre;
    }

    public String getIdTerminal() {
        return IdTerminal;
    }

    public void setIdTerminal(String idTerminal) {
        IdTerminal = idTerminal;
    }

    public Integer getCantidadCuotas() {
        return CantidadCuotas;
    }

    public void setCantidadCuotas(Integer cantidadCuotas) {
        CantidadCuotas = cantidadCuotas;
    }

    public Integer getMoneda() {
        return Moneda;
    }

    public void setMoneda(Integer moneda) {
        Moneda = moneda;
    }
}
