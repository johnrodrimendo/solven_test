package com.affirm.bantotalrest.model.RBTPG211;

import com.affirm.bantotalrest.model.common.BTResponseData;

import java.util.List;

public class BTClientesObtenerCuentaClienteResponse extends BTResponseData {

    private Long empresaId;
    private Long cuentaBT;

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Long getCuentaBT() {
        return cuentaBT;
    }

    public void setCuentaBT(Long cuentaBT) {
        this.cuentaBT = cuentaBT;
    }
}
