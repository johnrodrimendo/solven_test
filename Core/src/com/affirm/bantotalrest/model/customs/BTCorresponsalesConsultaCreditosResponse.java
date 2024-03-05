package com.affirm.bantotalrest.model.customs;

import com.affirm.bantotalrest.model.common.BTResponseData;
import com.affirm.bantotalrest.model.common.DEPE604AS_SItem;
import com.affirm.bantotalrest.model.common.DEPE6S1;
import com.google.gson.Gson;

import java.util.List;

public class BTCorresponsalesConsultaCreditosResponse extends BTResponseData {

    private String Canal;
    private Integer TipoDocumento;
    private String Documento;
    private String Credito;
    private String Fecha;
    private String Hora;
    private String IDUCorre;
    private String IdTerminal;
    private Integer CantidadCuotas;
    private String Nombre;
    private Long Cuenta;
    private Integer Moneda;
    private ListaCreditos ListaCreditos;

    public static class ListaCreditos{
        private List<DEPE604AS_SItem> DEPE604AS_SItem;
        private List<DEPE604AS_SItem> SItem;

        public List<com.affirm.bantotalrest.model.common.DEPE604AS_SItem> getDEPE604AS_SItem() {
            if(DEPE604AS_SItem == null)
                return SItem;
            return DEPE604AS_SItem;
        }

        public void setDEPE604AS_SItem(List<com.affirm.bantotalrest.model.common.DEPE604AS_SItem> DEPE604AS_SItem) {
            this.DEPE604AS_SItem = DEPE604AS_SItem;
        }

        public List<com.affirm.bantotalrest.model.common.DEPE604AS_SItem> getSItem() {
            return SItem;
        }

        public void setSItem(List<com.affirm.bantotalrest.model.common.DEPE604AS_SItem> SItem) {
            this.SItem = SItem;
        }
    }

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

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public Long getCuenta() {
        return Cuenta;
    }

    public void setCuenta(Long cuenta) {
        Cuenta = cuenta;
    }

    public Integer getMoneda() {
        return Moneda;
    }

    public void setMoneda(Integer moneda) {
        Moneda = moneda;
    }

    public BTCorresponsalesConsultaCreditosResponse.ListaCreditos getListaCreditos() {
        return ListaCreditos;
    }

    public void setListaCreditos(BTCorresponsalesConsultaCreditosResponse.ListaCreditos listaCreditos) {
        ListaCreditos = listaCreditos;
    }
}
