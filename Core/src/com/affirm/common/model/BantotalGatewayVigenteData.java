package com.affirm.common.model;

import java.util.Date;
import java.util.List;

public class BantotalGatewayVigenteData {

    private Long prestamoOperacionUId;
    private List<Integer> nroCuotas;
    private Integer nroCuotasImpagas;
    private Integer nroCuotasPagas;
    private Integer nroCuotasSeleccionadas;
    private Double monto;

    public Long getPrestamoOperacionUId() {
        return prestamoOperacionUId;
    }

    public void setPrestamoOperacionUId(Long prestamoOperacionUId) {
        this.prestamoOperacionUId = prestamoOperacionUId;
    }

    public List<Integer> getNroCuotas() {
        return nroCuotas;
    }

    public void setNroCuotas(List<Integer> nroCuotas) {
        this.nroCuotas = nroCuotas;
    }

    public Integer getNroCuotasImpagas() {
        return nroCuotasImpagas;
    }

    public void setNroCuotasImpagas(Integer nroCuotasImpagas) {
        this.nroCuotasImpagas = nroCuotasImpagas;
    }

    public Integer getNroCuotasPagas() {
        return nroCuotasPagas;
    }

    public void setNroCuotasPagas(Integer nroCuotasPagas) {
        this.nroCuotasPagas = nroCuotasPagas;
    }

    public Integer getNroCuotasSeleccionadas() {
        return nroCuotasSeleccionadas;
    }

    public void setNroCuotasSeleccionadas(Integer nroCuotasSeleccionadas) {
        this.nroCuotasSeleccionadas = nroCuotasSeleccionadas;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }
}
