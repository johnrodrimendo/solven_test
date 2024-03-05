package com.affirm.bantotalrest.model.RBTPG030;

import com.affirm.bantotalrest.model.common.BtRequestData;

public class BTCuentasDeAhorroContratarProductoRequest extends BtRequestData {

    private Long clienteUId;
    private Long productoUId;
    private String nombreSubCuenta;

    public Long getClienteUId() {
        return clienteUId;
    }

    public void setClienteUId(Long clienteUId) {
        this.clienteUId = clienteUId;
    }

    public Long getProductoUId() {
        return productoUId;
    }

    public void setProductoUId(Long productoUId) {
        this.productoUId = productoUId;
    }

    public String getNombreSubCuenta() {
        return nombreSubCuenta;
    }

    public void setNombreSubCuenta(String nombreSubCuenta) {
        this.nombreSubCuenta = nombreSubCuenta;
    }
}
