package com.affirm.bantotalrest.model.RBTPG034;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class PagarCuotaRequest extends BtRequestData {

    private Long operacionUId;
    private Long clienteUId;
    private Double importe;
    private Long operacionUId_cobro;
    private String referencia;

    public Long getOperacionUId() {
        return operacionUId;
    }

    public void setOperacionUId(Long operacionUId) {
        this.operacionUId = operacionUId;
    }

    public Long getClienteUId() {
        return clienteUId;
    }

    public void setClienteUId(Long clienteUId) {
        this.clienteUId = clienteUId;
    }

    public Double getImporte() {
        return importe;
    }

    public void setImporte(Double importe) {
        this.importe = importe;
    }

    public Long getOperacionUId_cobro() {
        return operacionUId_cobro;
    }

    public void setOperacionUId_cobro(Long operacionUId_cobro) {
        this.operacionUId_cobro = operacionUId_cobro;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
