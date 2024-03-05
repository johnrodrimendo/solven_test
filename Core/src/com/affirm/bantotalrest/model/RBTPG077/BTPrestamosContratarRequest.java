package com.affirm.bantotalrest.model.RBTPG077;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class BTPrestamosContratarRequest extends BtRequestData {

    private Long operacionUId;
    private Long clienteUId;
    private Long operacionUId_desembolso;
    private Long operacionUId_cobro;

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

    public Long getOperacionUId_desembolso() {
        return operacionUId_desembolso;
    }

    public void setOperacionUId_desembolso(Long operacionUId_desembolso) {
        this.operacionUId_desembolso = operacionUId_desembolso;
    }

    public Long getOperacionUId_cobro() {
        return operacionUId_cobro;
    }

    public void setOperacionUId_cobro(Long operacionUId_cobro) {
        this.operacionUId_cobro = operacionUId_cobro;
    }
}
