package com.affirm.bantotalrest.model.common;

import java.util.List;

public class DEPE604AS_SItem {

    private Integer NroCredito;
    private Long OperacionUID;
    private Integer MonedaCred;
    private Double MontoCancTotal;
    private ListaCuotas ListaCuotas;

    public static class ListaCuotas{
        private List<DEPE6S1> DEPE6S1;

        public List<com.affirm.bantotalrest.model.common.DEPE6S1> getDEPE6S1() {
            return DEPE6S1;
        }

        public void setDEPE6S1(List<com.affirm.bantotalrest.model.common.DEPE6S1> DEPE6S1) {
            this.DEPE6S1 = DEPE6S1;
        }
    }

    public Integer getNroCredito() {
        return NroCredito;
    }

    public void setNroCredito(Integer nroCredito) {
        NroCredito = nroCredito;
    }

    public Long getOperacionUID() {
        return OperacionUID;
    }

    public void setOperacionUID(Long operacionUID) {
        OperacionUID = operacionUID;
    }

    public Integer getMonedaCred() {
        return MonedaCred;
    }

    public void setMonedaCred(Integer monedaCred) {
        MonedaCred = monedaCred;
    }

    public Double getMontoCancTotal() {
        return MontoCancTotal;
    }

    public void setMontoCancTotal(Double montoCancTotal) {
        MontoCancTotal = montoCancTotal;
    }

    public DEPE604AS_SItem.ListaCuotas getListaCuotas() {
        return ListaCuotas;
    }

    public void setListaCuotas(DEPE604AS_SItem.ListaCuotas listaCuotas) {
        ListaCuotas = listaCuotas;
    }
}
